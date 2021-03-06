package nig.mf.plugin.pssd.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nig.mf.plugin.pssd.project.DataUse;
import nig.mf.plugin.pssd.project.DataUseRole;
import nig.mf.pssd.ProjectRole;

public class ModelUserRoleSet extends UserRoleSet {

	private Map<String, ProjectRole> _projectRoles;
	private Map<String, DataUseRole> _dataUseRoles;

	public ModelUserRoleSet(Collection<String> roles) {

		super(roles);
		_projectRoles = new HashMap<String, ProjectRole>();
		_dataUseRoles = new HashMap<String, DataUseRole>();
		if (roles != null) {
			for (String role : roles) {
				try {
					ProjectRole pr = ProjectRole.parse(role);
					if (pr != null) {
						_projectRoles.put(pr.cid(), pr);
					}
				} catch (Throwable e) {

				}
				try {
					DataUseRole dur = DataUseRole.parse(role);
					if (dur != null) {
						_dataUseRoles.put(dur.cid(), dur);
					}
				} catch (Throwable e) {

				}
			}
		}
	}

	public boolean hasProjectAdminRole(String cid) {

		ProjectRole role = _projectRoles.get(cid);
		if (role != null) {
			if (role.type() == ProjectRole.Type.project_administrator) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSubjectAdminRole(String cid) {

		ProjectRole role = _projectRoles.get(cid);
		if (role != null) {
			if (role.type() == ProjectRole.Type.subject_administrator) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMemberRole(String cid) {

		ProjectRole role = _projectRoles.get(cid);
		if (role != null) {
			if (role.type() == ProjectRole.Type.member) {
				return true;
			}
		}
		return false;
	}

	public boolean hasGuestRole(String cid) {

		ProjectRole role = _projectRoles.get(cid);
		if (role != null) {
			if (role.type() == ProjectRole.Type.guest) {
				return true;
			}
		}
		return false;
	}

	public boolean hasSpecificDataUseRole(String cid) {

		DataUseRole role = _dataUseRoles.get(cid);
		if (role != null) {
			if (role.dataUse() == DataUse.specific) {
				return true;
			}
		}
		return false;
	}

	public boolean hasExtendedDataUseRole(String cid) {

		DataUseRole role = _dataUseRoles.get(cid);
		if (role != null) {
			if (role.dataUse() == DataUse.extended) {
				return true;
			}
		}
		return false;
	}

	public boolean hasUnspecifiedDataUseRole(String cid) {

		DataUseRole role = _dataUseRoles.get(cid);
		if (role != null) {
			if (role.dataUse() == DataUse.unspecified) {
				return true;
			}
		}
		return false;
	}

}
