package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventOnTextureChanged.Handler;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventOnTextureChanged extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventOnTextureChanged event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  public static enum Change {
    ADDED, REMOVED, UPDATED
  }

  private final Change change;
  private final String alias;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
