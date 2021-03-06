package nig.mf.plugin.pssd.services;

import nig.mf.plugin.pssd.util.AssetRegistry;
import arc.mf.plugin.PluginService;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;


public class SvcRoleMemberRegList extends PluginService {
	private Interface _defn;

	public SvcRoleMemberRegList() {
		_defn = new Interface();

	}

	public String name() {
		return "om.pssd.role-member-registry.list";
	}

	public String description() {
		return "Lists the local roles that are available for use as a role-member when creating local projects.";
	}

	public Interface definition() {
		return _defn;
	}

	public Access access() {
		return ACCESS_MODIFY;
	}

	public void execute(XmlDoc.Element args, Inputs in, Outputs out, XmlWriter w)
			throws Throwable {

		// Find the Registry. Return if none yet.
		String id = AssetRegistry.findRegistry(executor(), SvcRoleMemberRegAdd.REGISTRY_ASSET_NAME, AssetRegistry.AccessType.PUBLIC);
		if (id==null) return;
		
		// List
		String xpath = "asset/meta/" + SvcRoleMemberRegAdd.DOCTYPE + "/role";
		AssetRegistry.list(executor(), id, xpath, w);
	}
		
}
