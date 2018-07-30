package si.cat.client;

import java.util.logging.Level;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import si.cat.client.events.Bus;
import si.cat.client.events.EventLoadResources;
import si.cat.client.presenter.EditorPresenter;
import si.cat.client.view.EditorView;
import thothbot.parallax.core.shared.Log;

public class TilesetGenerator implements EntryPoint {
  @SuppressWarnings("unused")
  private static final Controller controller = new Controller();

  @Override
  public void onModuleLoad() {

    // Increase logging level because Texture (thothbot.parallax.core.client.textures.Texture:580)
    // was printing base64 encoded data and that was slowing down the eclipse and dev mode.
    Log.logger.setLevel(Level.WARNING);

    // When the resources are loaded, we can load the editor.
    // TODO: show loader on entry, then hide on resources loaded
    Bus.post(new EventLoadResources(() -> {
      Scheduler.get().scheduleDeferred(() -> {
        new EditorPresenter(new EditorView()).go(RootLayoutPanel.get());
        // new EditorPresenter(new ColorMulEditorView()).go(RootLayoutPanel.get());
      });
    }));
  }
}
