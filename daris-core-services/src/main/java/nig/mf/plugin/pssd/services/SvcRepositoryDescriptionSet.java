package nig.mf.plugin.pssd.services;

import java.util.List;

import nig.mf.plugin.pssd.RepositoryDescription;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.dtype.DateType;
import arc.mf.plugin.dtype.StringType;
import arc.mf.plugin.dtype.UrlType;
import arc.mf.plugin.dtype.XmlDocType;
import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

public class SvcRepositoryDescriptionSet extends PluginService {
	
	private Interface _defn;

	public SvcRepositoryDescriptionSet() {
		
		// matches DocType daris:pssd-repository-description
		
		_defn = new Interface();
		
		// Custodian
		Interface.Element me = new Interface.Element("custodian", XmlDocType.DEFAULT, 
				"The person responsible for the management of the repository.", 1, 1);
		me.add(new Interface.Element("email", StringType.DEFAULT,
				"The custodian's email address", 1, 1));
		me.add(new Interface.Element("prefix", StringType.DEFAULT,
				"Prefix for the custodian's name.", 0, 1));
		me.add(new Interface.Element("first", StringType.DEFAULT,
				"The custodian's first name.", 1, 1));
		me.add(new Interface.Element("middle", StringType.DEFAULT,
				"The custodian's middle name.", 0, Integer.MAX_VALUE));
		me.add(new Interface.Element("last", StringType.DEFAULT,
				"The custodian's last name.", 1, 1));
		me.add(new Interface.Element("NLA-ID", StringType.DEFAULT, "National Library of Australia identifier", 0, 1));
		//
		Interface.Element me2 = new Interface.Element("address", XmlDocType.DEFAULT, 
				"The institutional address of the custodian.", 0, 1);
		me2.add(new Interface.Element("department", StringType.DEFAULT,
				"The custodian's department.", 0, 1));
		me2.add(new Interface.Element("institution", StringType.DEFAULT,
				"The custodian's institution.", 0, 1));
		me2.add(new Interface.Element("physical-address", StringType.DEFAULT,
				"The custodian's address - use as many of these as you need.", 0, Integer.MAX_VALUE));
		me.add(me2);
		//
		_defn.add(me);
		
		// Location				
		me = new Interface.Element("name", StringType.DEFAULT,
						"The name of the repository.", 1, 1);
		me.add(new Interface.Attribute(
				"acronym", StringType.DEFAULT, "Acronym for the repository.", 0));
		_defn.add(me);
		//
		me = new Interface.Element("location", XmlDocType.DEFAULT, 
				"The physical location of the repository.", 1, 1);
		me.add(new Interface.Element("institution", StringType.DEFAULT,
				"The institution hosting the data.", 1, 1));
		me.add(new Interface.Element("department", StringType.DEFAULT,
				"The department within the institution.", 0, 1));
		me.add(new Interface.Element("building", StringType.DEFAULT,
				"The building within the institution.", 0, 1));
		me.add(new Interface.Element("precinct", StringType.DEFAULT,
				"Can be a suburb or generic term describing an area.", 0, 1));
		_defn.add(me);
		
		// rights
		me = new Interface.Element("rights", XmlDocType.DEFAULT, 
				"A description of the rights process to gain access to collections in the repository.", 1, 1);
		me.add(new Interface.Element("description", StringType.DEFAULT,
				"The description.", 1, 1));
		_defn.add(me);

		// data holdings
		me = new Interface.Element("data-holdings", XmlDocType.DEFAULT, 
				"Describes broadly the data holdings in the repositoryo collections in the repository.", 1, 1);
		me.add(new Interface.Element("description", StringType.DEFAULT,
				"The description.", 1, 1));
		me.add(new Interface.Element("start-date", DateType.DEFAULT,
				"The date on which the repository was activated and started managing data.", 0, 1));
		//
		_defn.add(me);
		//
		_defn.add(new Interface.Element("originating-source", UrlType.DEFAULT, "The originating source for any meta-data harvested from this repository", 0, 1));

		
	}

	public String name() {
		return "om.pssd.repository.description.set";
	}

	public String description() {
		return "Set the repository description record.";
	}

	public Interface definition() {
		return _defn;
	}

	public Access access() {
		return ACCESS_MODIFY;
	}

	public void execute(XmlDoc.Element args, Inputs in, Outputs out, XmlWriter w)
			throws Throwable {
		
		List<XmlDoc.Element> c = args.elements();
		if (args == null || c==null) {
			throw new Exception ("No input arguments given; this would overwrite all entries with null");
		}

		// See if the singleton asset already exists; else create.
		String id = RepositoryDescription.findAndCreateRepositoryDescription(executor());
		
		// Add the new record 
		XmlDoc.Element name = args.element("name");
		XmlDoc.Element custodian = args.element("custodian");
		XmlDoc.Element location = args.element("location");
		XmlDoc.Element rights = args.element("rights");
		XmlDoc.Element holdings = args.element("data-holdings");
		XmlDoc.Element origSrc = args.element("originating-source");
		RepositoryDescription.replaceRecord(executor(), id, name, custodian, location, rights, holdings, origSrc);

		// Return
		w.add("id", id);
	}
}
