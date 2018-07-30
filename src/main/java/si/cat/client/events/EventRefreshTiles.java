package si.cat.client.events;

import java.util.Collection;
import java.util.Collections;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import si.cat.client.events.EventRefreshTiles.Handler;
import si.cat.shared.model.Tile;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class EventRefreshTiles extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventRefreshTiles event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Collection<Tile> tiles;

  public EventRefreshTiles(Tile tile) {
    tiles = Collections.singletonList(tile);
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
