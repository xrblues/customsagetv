package sagex.studio;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.event.TreeModelListener;

import tv.sage.StudioAPI;
import tv.sage.StudioPlugin;

public class StubStudioAPI implements StudioAPI {

    public void AddTreeModelListener(TreeModelListener treemodellistener) {
        // TODO Auto-generated method stub

    }

    public JFrame GetJFrame() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends StudioPlugin> T GetPlugin(Class<T> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public WidgetRef[] GetSelectedWidgetRefs() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object[] GetSelectedWidgets() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object GetSingleSelectedWidget() {
        return new String("Fake Single Selected Widget");
    }

    public Icon GetWidgetTypeIcon(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    public void InvokeMenuCommand(String s) {
        // TODO Auto-generated method stub

    }

    public WidgetRef MakeRef(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    public WidgetRef MakeRef(Object obj, Object obj1) {
        // TODO Auto-generated method stub
        return null;
    }

    public void RefreshTree() {
        // TODO Auto-generated method stub

    }

    public void RemoveTreeModelListener(TreeModelListener treemodellistener) {
        // TODO Auto-generated method stub

    }

    public void SelectWidget(Object obj, boolean flag) {
        // TODO Auto-generated method stub

    }

    public void SelectWidgetRefs(WidgetRef[] awidgetref, boolean flag) {
        // TODO Auto-generated method stub

    }

    public void SelectWidgets(Object[] aobj, boolean flag) {
        // TODO Auto-generated method stub

    }

}
