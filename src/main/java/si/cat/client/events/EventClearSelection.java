package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventClearSelection.Handler;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EventClearSelection extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventClearSelection event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
