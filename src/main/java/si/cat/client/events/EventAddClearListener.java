package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventAddClearListener.Handler;
import si.cat.client.events.listeners.ClearListener;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventAddClearListener extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventAddClearListener event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final ClearListener listener;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
