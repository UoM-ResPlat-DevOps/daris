package daris.client.ui.project;

import arc.gui.gwt.dnd.DragWidget;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.widget.label.Label;
import arc.gui.menu.Menu;
import arc.gui.object.SelectedObjectSet;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.window.Window;
import arc.mf.model.authentication.Authority;
import daris.client.model.project.ProjectMember;

public class ProjectMemberGUI implements ObjectGUI {

    public static final ProjectMemberGUI INSTANCE = new ProjectMemberGUI();

    private ProjectMemberGUI() {

    }

    @Override
    public String idToString(Object o) {

        return ((ProjectMember) o).toString();
    }

    @Override
    public String icon(Object o, int size) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object reference(Object o) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean needToResolve(Object o) {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void displayDetails(Object o, ObjectDetailsDisplay dd,
            boolean forEdit) {

        // TODO Auto-generated method stub

    }

    @Override
    public void open(Window w, Object o) {

        // TODO Auto-generated method stub

    }

    @Override
    public DropHandler dropHandler(Object o) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DragWidget dragWidget(Object o) {

        ProjectMember pm = (ProjectMember) o;
        Authority authority = pm.user().domain().authority();
        String authorityName = authority == null ? null : authority.name();
        return new DragWidget("member", new Label((authorityName == null ? ""
                : (authorityName + ":"))
                + pm.user().domain()
                + ":"
                + pm.user().name()));
    }

    @Override
    public Menu actionMenu(Window w, Object o, SelectedObjectSet selected,
            boolean readOnly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Menu memberActionMenu(Window w, Object o,
            SelectedObjectSet selected, boolean readOnly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectUpdateHandle createUpdateMonitor(Object o,
            ObjectUpdateListener ul) {
        // TODO Auto-generated method stub
        return null;
    }

}
