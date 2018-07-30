package si.cat.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import lombok.Getter;
import si.cat.client.cache.TextureCache;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventAddClearListener;
import si.cat.client.events.EventClear;
import si.cat.client.events.EventGetTextureCache;
import si.cat.client.events.EventGetTextureDescriptorCache;
import si.cat.client.events.EventGetTileCache;
import si.cat.client.events.EventLoadResources;
import si.cat.client.events.EventOnRpcError;
import si.cat.client.events.EventShowTextureImportDialog;
import si.cat.client.events.listeners.ClearListener;
import si.cat.client.presenter.DialogsPresenter;
import si.cat.client.presenter.TextureImportDialogPresenter;
import si.cat.client.presenter.TextureImportDialogPresenter.TextureDialogModel;
import si.cat.client.resources.ResourceCache;
import si.cat.client.view.DialogsView;
import si.cat.client.view.TextureImportDialogView;

public class Controller implements BusHelper {

  private TextureDescriptorCache textureDescriptorCache = new TextureDescriptorCache();

  private TextureCache textureCache = new TextureCache();

  private TileCache tileCache = new TileCache();

  @Getter
  private DialogsPresenter dialogsPresenter = new DialogsPresenter(new DialogsView());

  private List<ClearListener> clearListeners = new ArrayList<>();

  public Controller() {
    bind();
  }

  private void bind() {

    // Handle the clear listers.
    Bus.addHandler(EventAddClearListener.TYPE, e -> {
      clearListeners.add(e.getListener());
    });

    // Handle the RPC errors.
    Bus.addHandler(EventOnRpcError.TYPE, e -> {
      e.getCaught().printStackTrace();
      GWT.log(e.getCaught().getMessage());
    });

    // Handle the project clear.
    Bus.addHandler(EventClear.TYPE, e -> {
      // First clear all the listeners.
      clearListeners.forEach(l -> l.onClear());

      // Then the caches.
      tileCache.clear();
      textureCache.clear();
      textureDescriptorCache.clear();

      // And notify the caller that we're done.
      e.getCallback().onCallback();
    });

    // Bind the cache access.
    Bus.addHandler(EventGetTextureCache.TYPE, e -> {
      e.getCallback().onCallback(textureCache);
    });
    Bus.addHandler(EventGetTextureDescriptorCache.TYPE, e -> {
      e.getCallback().onCallback(textureDescriptorCache);
    });
    Bus.addHandler(EventGetTileCache.TYPE, e -> {
      e.getCallback().onCallback(tileCache);
    });

    // Bind the loading of resources.
    Bus.addHandler(EventLoadResources.TYPE, event -> {
      ResourceCache.build(() -> event.getCallback().onCallback());
    });

    // Bind the texture dialog.
    Bus.addHandler(EventShowTextureImportDialog.TYPE, event -> {
      new TextureImportDialogPresenter(new TextureImportDialogView(),
          new TextureDialogModel(event.getInitialAlias(), event.getCallback())).go(null);
    });
  }
}
