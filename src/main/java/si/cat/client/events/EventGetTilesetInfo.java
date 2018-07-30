package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventGetTilesetInfo.Handler;
import si.cat.shared.model.TilesetInfo;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventGetTilesetInfo extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventGetTilesetInfo event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Callback<TilesetInfo> callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
