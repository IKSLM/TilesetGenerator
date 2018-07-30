package si.cat.client.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class Bus {
  static class CustomHandlerManager extends HandlerManager {
    public CustomHandlerManager() {
      super(null);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
      GWT.log("Event fired: " + event.getClass());

      super.fireEvent(event);
    }
  }

  private static final CustomHandlerManager eventBus = new CustomHandlerManager();
  private static final Map<Type<?>, Object> handlerRef = new HashMap<GwtEvent.Type<?>, Object>();
  private static final Map<Type<?>, List<GwtEvent<?>>> waitingEvents =
      new HashMap<GwtEvent.Type<?>, List<GwtEvent<?>>>();

  public static void post(GwtEvent<?> event) {
    // Check if we have a handler for the event, if not handlers can be
    // found, we store the event and fire when the first handler is added.
    boolean hasHandler = handlerRef.containsKey(event.getAssociatedType());

    if (hasHandler) {
      eventBus.fireEvent(event);
    } else {
      // If there are no handlers for this event type, we store it for
      // when the handler becomes available.
      synchronized (waitingEvents) {
        List<GwtEvent<?>> events = waitingEvents.get(event.getAssociatedType());

        if (events == null) {
          events = new Vector<GwtEvent<?>>();
          events.add(event);

          waitingEvents.put(event.getAssociatedType(), events);
        } else {
          events.add(event);
        }
      }
    }
  }

  public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler) {
    handlerRef.put(type, handler);

    HandlerRegistration reg = eventBus.addHandler(type, handler);

    // If there are some waiting events, we fire them, then remove. This
    // only happens when a handler is added for the first time and there are
    // some events waiting.
    synchronized (waitingEvents) {
      List<GwtEvent<?>> events = waitingEvents.get(type);

      if (events != null) {
        for (GwtEvent<?> event : events) {
          post(event);
        }

        events.clear();
      }
    }

    return reg;
  }
}
