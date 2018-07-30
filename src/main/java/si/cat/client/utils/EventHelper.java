package si.cat.client.utils;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseEvent;

import thothbot.parallax.core.shared.math.Vector2;

public class EventHelper {
  public static boolean isLeftMouseButton(MouseEvent<?> event) {
    return (event.getNativeButton() == NativeEvent.BUTTON_LEFT);
  }

  public static boolean isRightMouseButton(MouseEvent<?> event) {
    return (event.getNativeButton() == NativeEvent.BUTTON_RIGHT);
  }

  public static boolean isMiddleMouseButton(MouseEvent<?> event) {
    return (event.getNativeButton() == NativeEvent.BUTTON_MIDDLE);
  }

  public static Vector2 getLocation(MouseEvent<?> event) {
    return new Vector2(event.getX(), event.getY());
  }

  public static Vector2 getLocation(DomEvent<?> event) {
    NativeEvent ne = event.getNativeEvent();

    return new Vector2(ne.getClientX(), ne.getClientY());
  }
}
