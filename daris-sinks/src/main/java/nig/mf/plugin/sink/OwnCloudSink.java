package nig.mf.plugin.sink;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import nig.util.PathUtil;
import nig.webdav.client.owncloud.OwncloudClient;
import nig.webdav.client.owncloud.OwncloudClientFactory;
import arc.archive.ArchiveInput;
import arc.archive.ArchiveRegistry;
import arc.mf.plugin.DataSinkImpl;
import arc.mf.plugin.PluginTask;
import arc.mf.plugin.dtype.BooleanType;
import arc.mf.plugin.dtype.DataType;
import arc.mf.plugin.dtype.PasswordType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.UrlType;
import arc.mf.plugin.sink.ParameterDefinition;
import arc.mime.NamedMimeType;
import arc.streams.LongInputStream;
import arc.streams.StreamCopy;
import arc.xml.XmlDoc;

public class OwnCloudSink implements DataSinkImpl {

    public static final String SINK_TYPE = "owncloud";
    public static final String DEFAULT_DIRECTORY = "/";

    public static enum ParamDefn {

        // @formatter:off
        URL("url", UrlType.DEFAULT, "The owncloud webdav server address."),
        DIRECTORY("directory",StringType.DEFAULT, "The default directory. Defaults to /."),
        DECOMPRESS("decompress", BooleanType.DEFAULT, "Indicate whether to decompress the archive. Defaults to true."),
        USER("user", StringType.DEFAULT, "User name to access the owncloud webdav server."),
        PASSWORD("password", PasswordType.DEFAULT, "User's password to access the owncloud webdav server."),
        CHUNKED("chunked", BooleanType.DEFAULT, "Enable chunked upload. Defaults to false.");
        // @formatter:on

        private String _paramName;
        private ParameterDefinition _paramDefn;

        ParamDefn(String name, DataType type, String description) {
            _paramName = name;
            _paramDefn = new ParameterDefinition(type, description);
        }

        @Override
        public final String toString() {
            return paramName();
        }

        public String paramName() {
            return _paramName;
        }

        public ParameterDefinition definition() {
            return _paramDefn;
        }

        private static Map<String, ParameterDefinition> _defns;

        public static Map<String, ParameterDefinition> definitions() {
            if (_defns == null) {
                _defns = new LinkedHashMap<String, ParameterDefinition>();
                ParamDefn[] vs = values();
                for (ParamDefn v : vs) {
                    _defns.put(v.paramName(), v.definition());
                }
            }
            return Collections.unmodifiableMap(_defns);
        }
    }

    private static class Params {
        public final String url;
        public final String directory;
        public final boolean decompress;
        public final String user;
        public final String password;
        public final boolean chunked;

        Params(String url, String directory, boolean decompress, String user, String password, boolean chunked) {
            this.url = url;
            this.directory = directory;
            this.decompress = decompress;
            this.user = user;
            this.password = password;
            this.chunked = chunked;
        }

        public nig.webdav.client.UserCredentials userCredentials() {
            return new nig.webdav.client.UserCredentials(user, password);
        }

        public static Params parse(Map<String, String> params) throws Throwable {
            if (params == null || params.isEmpty()) {
                throw new IllegalArgumentException("Sink parameters cannot be null or empty.");
            }
            String url = params.get(ParamDefn.URL.paramName());
            if (url == null) {
                throw new IllegalArgumentException("Missing webdav sink parameter: " + ParamDefn.URL.paramName());
            }

            String directory = params.get(ParamDefn.DIRECTORY.paramName());
            if (directory == null) {
                directory = DEFAULT_DIRECTORY;
            }

            String d = params.get(ParamDefn.DECOMPRESS.paramName());
            boolean decompress = d == null ? true : Boolean.parseBoolean(d);

            String user = params.get(ParamDefn.USER.paramName());
            if ((user == null)) {
                throw new IllegalArgumentException("Missing webdav sink parameter: " + ParamDefn.USER.paramName());
            }

            String password = params.get(ParamDefn.PASSWORD.paramName());
            if ((password == null)) {
                throw new IllegalArgumentException("Missing webdav sink parameter: " + ParamDefn.PASSWORD.paramName());
            }

            String chkd = params.get(ParamDefn.CHUNKED.paramName());
            boolean chunked = chkd == null ? false : Boolean.parseBoolean(chkd);

            return new Params(url, directory, decompress, user, password, chunked);
        }
    }

    private static class MultiTransferContext {

        private static final AtomicLong count = new AtomicLong();

        public final long id;
        public final OwncloudClient client;
        public final Set<String> existingDirectories;

        private MultiTransferContext(OwncloudClient client) {
            this.id = count.incrementAndGet();
            this.client = client;
            this.existingDirectories = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        }

        public int hashCode() {
            return new Long(id).hashCode();
        }

        public boolean equals(Object o) {
            return o != null && (o instanceof MultiTransferContext) && this.id == ((MultiTransferContext) o).id;
        }

    }

    private Set<MultiTransferContext> _mctxs;

    public OwnCloudSink() {
        _mctxs = Collections.newSetFromMap(new ConcurrentHashMap<MultiTransferContext, Boolean>());
    }

    @Override
    public String[] acceptedTypes() throws Throwable {
        // all mime types are accepted.
        return null;
    }

    @Override
    public Object beginMultiple(Map<String, String> parameters) throws Throwable {
        Params params = Params.parse(parameters);
        OwncloudClient client = OwncloudClientFactory.create(params.url, params.userCredentials());
        MultiTransferContext mctx = new MultiTransferContext(client);
        _mctxs.add(mctx);
        return mctx;
    }

    @Override
    public int compressionLevelRequired() {
        // don't care
        return -1;
    }

    @Override
    public void consume(java.lang.Object multiCtx, java.lang.String path,
            java.util.Map<java.lang.String, java.lang.String> parameters, XmlDoc.Element userMeta, XmlDoc.Element meta,
            LongInputStream in, java.lang.String appMimeType, java.lang.String streamMimeType, long length)
            throws Throwable {
        OwncloudClient client = multiCtx == null ? null : ((MultiTransferContext) multiCtx).client;
        Params params = Params.parse(parameters);
        if (multiCtx == null) {
            client = OwncloudClientFactory.create(params.url, params.userCredentials());
        }

        String assetId = meta != null ? meta.value("@id") : null;
        String ext = meta != null ? meta.value("content/type/@ext") : null;

        try {

            StringBuilder sb = new StringBuilder(params.directory);
            if (!params.directory.endsWith("/")) {
                sb.append("/");
            }
            if (path != null) {
                path = path.replace("\\\\", "/").replace("\\", "/");
                while (path.startsWith("/")) {
                    path = path.substring(1);
                }
                sb.append(path);
            }

            Set<String> existingDirs = multiCtx == null ? Collections
                    .newSetFromMap(new ConcurrentHashMap<String, Boolean>())
                    : ((MultiTransferContext) multiCtx).existingDirectories;
            if (params.decompress && streamMimeType != null && ArchiveRegistry.isAnArchive(streamMimeType)) {
                // decompress archive
                if (assetId != null) {
                    sb.append("/");
                    sb.append("asset_");
                    sb.append(assetId);
                }
                extractAndTransfer(client, ArchiveRegistry.createInput(in, new NamedMimeType(streamMimeType)),
                        params.chunked, sb.toString(), existingDirs);
            } else {
                // single file
                if (assetId != null) {
                    sb.append("/");
                    sb.append("asset_");
                    sb.append(assetId);
                }
                if (ext != null) {
                    sb.append(".");
                    sb.append(ext);
                }
                transfer(client, in, length, params.chunked, sb.toString(), existingDirs);
            }
        } finally {

        }
    }

    private static void extractAndTransfer(OwncloudClient client, ArchiveInput ai, boolean chunked, String dir,
            Set<String> existingDirs) throws Throwable {

        ArchiveInput.Entry entry = null;
        try {
            while ((entry = ai.next()) != null) {
                String remotePath = PathUtil.join(dir, entry.name());
                if (entry.isDirectory()) {
                    client.mkdir(remotePath, true);
                    existingDirs.add(remotePath);
                } else {
                    transfer(client, entry.stream(), entry.size(), chunked, remotePath, existingDirs);
                }
            }
        } finally {
            ai.close();
        }

    }

    private static void transfer(OwncloudClient client, InputStream in, long length, boolean chunked,
            String remotePath, Set<String> existingDirs) throws Throwable {

        String remoteDir = PathUtil.getParentDirectory(remotePath);
        if (existingDirs == null || !existingDirs.contains(remoteDir)) {
            client.mkdir(remoteDir, true);
            if (existingDirs != null) {
                existingDirs.add(remoteDir);
            }
        }
        try {
            if (chunked) {
                // chunked upload requires stream length
                // if length is not available, save the stream to a temporary file to get its length
                if (length < 0 && in instanceof LongInputStream) {
                    length = ((LongInputStream) in).length();
                }
                if (length < 0) {
                    File tf = PluginTask.createTemporaryFile();
                    OutputStream tfos = new BufferedOutputStream(new FileOutputStream(tf));
                    try {
                        StreamCopy.copy(in, tfos);
                    } finally {
                        tfos.close();
                        in.close();
                    }
                    in = PluginTask.deleteOnCloseInputStream(tf);
                    length = tf.length();
                }

                client.chunkedPut(in, length, remotePath);
            } else {
                client.put(in, length, remotePath);
            }
        } finally {
            in.close();
        }
    }

    @Override
    public String description() throws Throwable {
        return "owncloud sink";
    }

    @Override
    public void endMultiple(Object ctx) throws Throwable {
        MultiTransferContext mctx = (MultiTransferContext) ctx;
        _mctxs.remove(mctx);
    }

    @Override
    public Map<String, ParameterDefinition> parameterDefinitions() throws Throwable {
        return ParamDefn.definitions();
    }

    @Override
    public void shutdown() throws Throwable {
        _mctxs.clear();
    }

    @Override
    public String type() throws Throwable {
        return SINK_TYPE;
    }

}
