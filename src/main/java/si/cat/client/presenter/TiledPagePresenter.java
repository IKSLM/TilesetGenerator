package si.cat.client.presenter;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventGetTilesetInfo;
import si.cat.client.events.EventRefreshTiledInfo;
import si.cat.client.events.EventSelectTiles;
import si.cat.client.presenter.TiledPagePresenter.TiledPage;
import si.cat.client.view.View;
import si.cat.shared.Size;

public class TiledPagePresenter extends NoModelPresenter<TiledPage> implements BusHelper {

  public interface TiledPage extends View {
    TextBox mapTileWidth();

    TextBox mapTileHeight();

    TextBox tilesetTileWidth();

    TextBox tilesetTileHeight();

    TextBox selectedIndices();
  }

  public TiledPagePresenter(TiledPage view) {
    super(view);
  }

  @Override
  public void bind() {

    // Handle selected tiles updating.
    getTileCache(cache -> {

      Bus.addHandler(EventSelectTiles.TYPE, e -> {
        StringBuffer indices = new StringBuffer();

        // Get indices from the cache.
        e.getTiles().forEach(t -> {
          if (indices.length() > 0) {
            indices.append(",");
          }
          indices.append(cache.values().indexOf(t) + 1);
        });

        view.selectedIndices().setText(indices.toString());
      });
    });

    // Handle refreshing of the tiled info.
    Bus.addHandler(EventRefreshTiledInfo.TYPE, e -> {

      post(new EventGetTilesetInfo(tilesetInfo -> {

        // No tile has been added.
        if (tilesetInfo == null) {
          return;
        }

        Size mapTileSize = tilesetInfo.getMapTileSize();
        Size tilesetTileSize = tilesetInfo.getTilesetTileSize();

        view.mapTileWidth().setText(String.valueOf(Math.round(mapTileSize.width)));
        view.mapTileHeight().setText(String.valueOf(Math.round(mapTileSize.height)));

        view.tilesetTileWidth().setText(String.valueOf(Math.round(tilesetTileSize.width)));
        view.tilesetTileHeight().setText(String.valueOf(Math.round(tilesetTileSize.height)));
      }));
    });

    // TODO: all types of download
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());
  }
}
