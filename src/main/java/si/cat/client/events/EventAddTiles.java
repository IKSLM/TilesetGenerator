package si.cat.client.events;

import java.util.Collection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import si.cat.client.events.EventAddTiles.Handler;
import si.cat.shared.model.Tile;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class EventAddTiles extends GwtEvent<Handler> {
  public interface Handler extends EventHandler {
    void handle(EventAddTiles event);
  }

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Collection<Tile> tiles;
  private final Callback0 callback;

  @Accessors(chain = true)
  private boolean clear = false;

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.handle(this);
  }
}
