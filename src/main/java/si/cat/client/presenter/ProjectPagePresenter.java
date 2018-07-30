package si.cat.client.presenter;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import si.cat.client.DataServiceAsync;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.EventAddTextureDescriptors;
import si.cat.client.events.EventAddTiles;
import si.cat.client.events.EventApplySettings;
import si.cat.client.events.EventClear;
import si.cat.client.events.EventGetSettings;
import si.cat.client.events.EventLoadProject;
import si.cat.client.events.EventOnRpcError;
import si.cat.client.events.EventProjectLoaded;
import si.cat.client.events.EventShowMessageBox;
import si.cat.client.events.EventShowPrompt;
import si.cat.client.presenter.DialogsPresenter.MessageType;
import si.cat.client.presenter.DialogsPresenter.PromptResponse;
import si.cat.client.presenter.DialogsPresenter.PromptType;
import si.cat.client.presenter.ProjectPagePresenter.ProjectPage;
import si.cat.client.view.View;
import si.cat.shared.model.OverlayTile;
import si.cat.shared.model.Project;
import si.cat.shared.model.Settings;
import si.cat.shared.model.TextureDescriptor;
import si.cat.shared.model.Tile;

public class ProjectPagePresenter extends NoModelPresenter<ProjectPage> implements BusHelper {

  public interface ProjectPage extends View {
    Button save();

    Button saveAs();

    Button open();

    Button clear();
  }

  public ProjectPagePresenter(ProjectPage view) {
    super(view);
  }

  private String lastUid = null;

  @Override
  public void bind() {

    // Handle remote project loading.
    Bus.addHandler(EventLoadProject.TYPE, e -> {
      handleLoad(e.getUid());
    });

    view.save().addClickHandler(e -> handleSave());
    view.saveAs().addClickHandler(e -> handleSaveAs());
    view.open().addClickHandler(e -> handleLoad());
    view.clear().addClickHandler(e -> handleClear());
  }

  private void handleSaveAs() {
    // Get the caches, prepare the project and save. Give the user the UID.
    getTextureDescriptorCache(cache1 -> {
      getTileCache(cache2 -> {
        post(new EventGetSettings(settings -> {
          save(null, settings, cache1, cache2);
        }));
      });
    });
  }

  private void handleSave() {

    // Get the caches, prepare the project and save. Give the user the UID.
    getTextureDescriptorCache(cache1 -> {
      getTileCache(cache2 -> {
        post(new EventGetSettings(settings -> {
          save(lastUid, settings, cache1, cache2);
        }));
      });
    });
  }

  private void save(String uid, Settings settings, TextureDescriptorCache cache1, TileCache cache2) {
    Project project = new Project(settings, new ArrayList<>(cache1.values()), new ArrayList<>(cache2.values()));

    DataServiceAsync.Util.getInstance().save(project, uid, new AsyncCallback<String>() {

      @Override
      public void onSuccess(String result) {
        if (lastUid == null || !lastUid.equals(result)) {
          post(new EventShowMessageBox("Project info",
              "Project UID (save it so you can access the project later!): " + result, MessageType.INFO, () -> {}));
        }
        lastUid = result;
      }

      @Override
      public void onFailure(Throwable caught) {
        post(new EventOnRpcError(caught));
      }
    });
  }

  private void handleLoad() {

    // Ask the user for the UID.
    post(new EventShowPrompt("Project load", "Enter the project UID", null, PromptType.OK_CANCEL, (response, value) -> {
      if (response == PromptResponse.OK) {
        handleLoad(value);
      }
    }));
  }

  protected void handleLoad(String uid) {

    if (uid == null || uid.trim().isEmpty()) {
      return;
    }

    DataServiceAsync.Util.getInstance().load(uid, new AsyncCallback<Project>() {

      @Override
      public void onSuccess(Project result) {
        handleLoad(result);
        lastUid = uid;
      }

      @Override
      public void onFailure(Throwable caught) {
        post(new EventOnRpcError(caught));
      }
    });
  }

  protected void handleLoad(Project project) {
    if (project == null) {
      return;
    }

    post(new EventClear(() -> {

      // Load the caches, add the tiles.
      getTextureDescriptorCache(cache1 -> {
        getTileCache(cache2 -> {

          ArrayList<TextureDescriptor> textureDescriptors = project.getTextureDescriptors();
          ArrayList<Tile> tiles = project.getTiles();

          // Load textures.
          if (!textureDescriptors.isEmpty()) {
            textureDescriptors.forEach(td -> cache1.put(td.getAlias(), td));
            post(new EventAddTextureDescriptors(textureDescriptors, () -> {
              GWT.log("Texture descriptors loaded. Count: " + textureDescriptors.size());
            }));
          }

          // Load tiles.
          if (!tiles.isEmpty()) {

            // Insert all the tiles into the tile cache.
            tiles.forEach(t -> {
              cache2.put(t.getAlias(), t);
            });

            // Update overlay tiles with actual overlay tiles (only aliases are serialized).
            tiles.stream().filter(t -> t instanceof OverlayTile).map(t -> (OverlayTile) t).forEach(t -> {
              t.getOverlayTiles().addAll(t.getOverlays().stream().map(a -> cache2.get(a)).collect(Collectors.toList()));
            });

            post(new EventAddTiles(tiles, () -> {
              GWT.log("Tiles loaded. Count: " + tiles.size());
            }));
          }

          // And settings.
          if (project.getSettings() != null) {
            post(new EventApplySettings(project.getSettings()));
          }

          post(new EventProjectLoaded(project));
        });
      });

    }));
  }

  private void handleClear() {
    post(new EventClear(() -> {}));
  }

  @Override
  public void go(HasWidgets container) {
    container.add(view.asWidget());
  }

}
