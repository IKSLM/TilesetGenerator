package si.cat.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.cache.TileCache;
import si.cat.client.events.EventGetTileCache.Handler;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventGetTileCache extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventGetTileCache event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Callback<TileCache> callback;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
