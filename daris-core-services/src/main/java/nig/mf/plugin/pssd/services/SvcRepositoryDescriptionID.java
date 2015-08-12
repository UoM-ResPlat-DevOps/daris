package nig.mf.plugin.pssd.services;

import nig.mf.plugin.pssd.RepositoryDescription;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

public class SvcRepositoryDescriptionID extends PluginService {

	private Interface _defn;

	public SvcRepositoryDescriptionID() {
		
	}

	public String name() {

		return "om.pssd.repository.id";
	}

	public String description() {

		return "Returns the asset ID of the repository description asset.";
	}

	public Interface definition() {

		return _defn;
	}

	public Access access() {

		return ACCESS_MODIFY;
	}

	public void execute(XmlDoc.Element args, Inputs in, Outputs out, XmlWriter w)
			throws Throwable {

		String id = RepositoryDescription.findRepositoryDescription(executor());
		w.add("id", id);

	}
}
