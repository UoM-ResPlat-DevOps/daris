package daris.client.model.project.messages;

import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.client.model.object.DObject;
import daris.client.model.project.Project;
import daris.client.model.project.ProjectMember;
import daris.client.model.project.ProjectRoleMember;

public class ProjectRoleMemberReplace extends ObjectMessage<Boolean> {

    private String _id;
    private List<ProjectMember> _members;
    private List<ProjectRoleMember> _roleMembers;

    private ProjectRoleMemberReplace(String id, List<ProjectMember> members,
            List<ProjectRoleMember> roleMembers) {

        assert id != null;
        assert !(members == null && roleMembers == null);
        _id = id;
        _members = members;
        _roleMembers = roleMembers;
    }

    public ProjectRoleMemberReplace(Project o) {

        this(o.id(), o.members(), o.roleMembers());
    }

    @Override
    protected void messageServiceArgs(XmlWriter w) {

        w.add("id", _id);
        if (_members != null) {
            for (ProjectMember m : _members) {
                w.push("member");
                if (m.user().domain().authority() != null
                        && m.user().domain().authority().name() != null) {
                    if (m.user().domain().authority().protocol() != null) {
                        w.add("authority", new String[] { "protocol",
                                m.user().domain().authority().protocol() }, m
                                .user().domain().authority().name());
                    } else {
                        w.add("authority", m.user().domain().authority().name());
                    }
                }
                w.add("domain", m.user().domain());
                w.add("user", m.user().name());
                w.add("role", m.role());
                if (m.dataUse() != null) {
                    w.add("data-use", m.dataUse());
                }
                w.pop();
            }
        }
        if (_roleMembers != null) {
            for (ProjectRoleMember rm : _roleMembers) {
                w.push("role-member");
                w.add("member", rm.member());
                w.add("role", rm.role());
                if (rm.dataUse() != null) {
                    w.add("data-use", rm.dataUse());
                }
                w.pop();
            }
        }

    }

    @Override
    protected String messageServiceName() {

        return "om.pssd.project.members.replace";
    }

    @Override
    protected Boolean instantiate(XmlElement xe) throws Throwable {

        return true;
    }

    @Override
    protected String objectTypeName() {

        return DObject.Type.project.toString();
    }

    @Override
    protected String idToString() {

        return _id;
    }

}
