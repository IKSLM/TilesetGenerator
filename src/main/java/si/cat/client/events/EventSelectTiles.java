package si.cat.client.events;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import si.cat.client.events.EventSelectTiles.Handler;
import si.cat.shared.model.Tile;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class EventSelectTiles extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventSelectTiles event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final List<Tile> tiles;

  @Accessors(chain = true)
  private Object origin;

  public EventSelectTiles(Tile tile) {
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
