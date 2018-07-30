package si.cat.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class GUIHelper {
  public static void enableAllChildren(Widget widget, boolean enable) {
    if (widget instanceof HasWidgets) {
      Iterator<Widget> iter = ((HasWidgets) widget).iterator();
      while (iter.hasNext()) {
        Widget nextWidget = iter.next();
        enableAllChildren(nextWidget, enable);
        if (nextWidget instanceof FocusWidget) {
          ((FocusWidget) nextWidget).setEnabled(enable);
        }
      }
    }
  }
}
