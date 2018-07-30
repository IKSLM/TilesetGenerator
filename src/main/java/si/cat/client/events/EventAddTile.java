package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventAddTile.Handler;
import si.cat.shared.model.Tile;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventAddTile extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventAddTile event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Tile tile;
  private final Callback0 callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
