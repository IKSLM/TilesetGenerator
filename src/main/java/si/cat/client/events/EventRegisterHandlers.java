package si.cat.client.events;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import si.cat.client.events.EventRegisterHandlers.Handler;
import thothbot.parallax.core.client.events.ViewportResizeHandler;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EventRegisterHandlers extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void onRegisterHandlers(EventRegisterHandlers event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private MouseWheelHandler mouseWheelHandler = null;

  private MouseUpHandler mouseUpHandler = null;

  private MouseDownHandler mouseDownHandler = null;

  private MouseMoveHandler mouseMoveHandler = null;

  private ContextMenuHandler contextMenuHandler = null;

  private MouseOverHandler mouseOverHandler = null;

  private ClickHandler clickHandler = null;

  private DoubleClickHandler doubleClickHandler = null;

  private ViewportResizeHandler viewportResizeHandler = null;

  // Keyboard handlers
  private KeyPressHandler keyPressHandler = null;

  private KeyDownHandler keyDownHandler = null;

  private KeyUpHandler keyUpHandler = null;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onRegisterHandlers(this);
  }
}
