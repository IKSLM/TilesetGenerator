package si.cat.client.ui;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DialogBox.CaptionImpl;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import si.cat.client.cache.TextureCache;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.events.Bus;
import si.cat.client.events.BusHelper;
import si.cat.client.events.Callback;
import si.cat.client.events.EventClear;
import si.cat.client.events.EventOnTextureChanged;
import si.cat.client.events.EventOnTextureChanged.Change;
import si.cat.client.geometries.QuadBufferGeometry;
import si.cat.shared.Rect;
import si.cat.shared.Size;
import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.RenderingPanel;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Box2;
import thothbot.parallax.core.shared.math.Box3;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;

public class UVEditor extends AnimatedScene implements BusHelper {

  private static final double CAMERA_Z = 1000.0;
  protected static final Texture SELECTOR_TEXTURE = new Texture("./static/gfx/selector_full.png", tx -> {});

  private String alias;
  private Callback<Rect> callback;
  private List<Rect> uvs;

  private DialogBox popup;

  private VerticalPanel container;

  private TextBox tileWidth;
  private TextBox tileHeight;
  private TextBox border;

  private Size size;

  private OrthographicCamera camera;

  private boolean pressed = false;
  private boolean dragged = false;

  private Mesh tilesetMesh;
  private Object3D uvsObject;
  private Size tilesetSize;

  private Vector2 pressedMouse;
  private Vector3 pressedMesh;
  private RenderingPanel renderingPanel;

  private TextureCache localTextureCache = new TextureCache();

  public UVEditor() {

    container = new VerticalPanel();

    popup = new DialogBox(true, false, new CaptionImpl());
    popup.setGlassEnabled(false);
    popup.setAnimationEnabled(true);

    HorizontalPanel inputs = new HorizontalPanel();
    inputs.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

    Label l = new Label("Tile width: ");
    tileWidth = new TextBox();
    tileWidth.setText("32");
    tileWidth.setWidth("50px");
    inputs.add(l);
    inputs.add(tileWidth);

    l = new Label("Tile height: ");
    tileHeight = new TextBox();
    tileHeight.setText("32");
    tileHeight.setWidth("50px");
    inputs.add(l);
    inputs.add(tileHeight);

    l = new Label("Margin: ");
    TextBox box = new TextBox();
    box.setText("0");
    box.setWidth("50px");
    inputs.add(l);
    inputs.add(box);

    l = new Label("Spacing: ");
    box = new TextBox();
    box.setText("0");
    box.setWidth("50px");
    inputs.add(l);
    inputs.add(box);

    l = new Label("Border: ");
    border = new TextBox();
    border.setText("0");
    border.setWidth("50px");
    inputs.add(l);
    inputs.add(border);

    // TODO: use margin and spacing

    container.add(inputs);

    renderingPanel = new RenderingPanel();
    renderingPanel.setBackground(0xC0FFC0);
    renderingPanel.setAnimatedScene(this);

    // Make the UV editor about 60% of the client view.
    size = new Size(0.6 * Window.getClientWidth(), 0.6 * Window.getClientHeight());

    SimplePanel panel = new SimplePanel(renderingPanel);
    panel.setSize(size.width() + "px", size.height() + "px");

    container.add(panel);

    popup.add(container);

    bind();
  }

  private void bind() {
    // Handle clear event, we must free the local cache and objects.
    Bus.addHandler(EventClear.TYPE, e -> {
      localTextureCache.clear();
      if (getScene() != null) {
        getScene().getChildren().clear();
      }
    });

    // Handle texture removal and updating. Remove the reference in both cases.
    Bus.addHandler(EventOnTextureChanged.TYPE, e -> {
      if (e.getChange() == Change.REMOVED || e.getChange() == Change.UPDATED) {
        localTextureCache.remove(e.getAlias());
      }
    });
  }

  public void show(String alias, List<Rect> uvs, Callback<Rect> callback) {

    this.alias = alias;
    this.callback = callback;
    this.uvs = uvs;

    popup.show();
    popup.center();
  }

  @Override
  protected void onUpdate(double duration) {

    assertTexture();

    getRenderer().render(getScene(), camera);
  }

  private void assertTexture() {

    if (this.alias == null) {
      return;
    }

    String alias = this.alias;
    this.alias = null;

    getTextureDescriptorCache(cache -> assertTexture(alias, cache));
  }

  private void assertTexture(String alias, TextureDescriptorCache cache) {

    // Load the texture and prepare the mesh.
    localTextureCache.get(cache.get(alias), (texture, size) -> {

      tilesetSize = size;

      QuadBufferGeometry geometry =
          new QuadBufferGeometry(new Box2(new Vector2(0, 0), new Vector2(1, 1)), size.width(), size.height());

      MeshPhongMaterial mat = new MeshPhongMaterial();
      mat.setMap(texture);

      tilesetMesh = new Mesh(geometry, mat);
      getScene().getChildren().clear();
      getScene().add(new AmbientLight(0xFFFFFF));
      getScene().add(tilesetMesh);

      uvsObject = new Object3D();
      tilesetMesh.add(uvsObject);

      // If initial UVs have been given, load UV quads and center the view about them.
      assertInitialUVs(true);
    });
  }

  private void assertInitialUVs(boolean center) {
    if (uvs == null || uvs.isEmpty()) {
      return;
    }

    uvsObject.getChildren().clear();

    Box3 bounds = new Box3();

    uvs.forEach(uv -> {
      // Create a textured selector mesh for each existing UV.

      double w = tilesetSize.width;
      double h = tilesetSize.height;

      double minx = uv.getMin().x * w;
      double miny = uv.getMin().y * h;
      double maxx = uv.getMax().x * w;
      double maxy = uv.getMax().y * h;

      QuadBufferGeometry geometry =
          new QuadBufferGeometry(new Box2(new Vector2(0, 0), new Vector2(1, 1)), maxx - minx, maxy - miny);

      Mesh uvMesh = new Mesh(geometry, getSelectorMaterial(SELECTOR_TEXTURE));
      uvMesh.setPosition(new Vector3((minx + maxx) * 0.5 - w * 0.5, -(miny + maxy) * 0.5 + h * 0.5, 1));
      uvsObject.add(uvMesh);

      bounds.union(new Box3().setFromObject(uvMesh));
    });

    uvs = null;

    // Then center the tileset.
    if (center) {
      tilesetMesh.setPosition(new Vector3(-bounds.center().getX(), -bounds.center().getY(), 0));
    }
  }

  protected static Material getSelectorMaterial(Texture selectorTexture) {
    MeshBasicMaterial mat = new MeshBasicMaterial();
    mat.setMap(selectorTexture);
    mat.setBlending(Material.BLENDING.NORMAL);
    mat.setTransparent(true);
    return mat;
  }

  @Override
  protected void onStart() {

    // Set the default ISO camera.
    camera = new OrthographicCamera(size.width(), size.height(), 1, 2000) {
      @Override
      public void onResize(ViewportResizeEvent event) {
        setSize(size.width(), size.height());
      }
    };

    camera.setPosition(new Vector3(0, 0, CAMERA_Z));

    // Listen for the events, this will allow us to drag and pick the tileset.
    registerEventHandlers();
  }

  private void registerEventHandlers() {
    getCanvas().addDomHandler(e -> handlePressed(e), MouseDownEvent.getType());
    getCanvas().addDomHandler(e -> handleDragged(e), MouseMoveEvent.getType());
    getCanvas().addDomHandler(e -> handleReleased(e), MouseUpEvent.getType());
    getCanvas().addDomHandler(e -> handleClicked(e), ClickEvent.getType());
  }

  private void handlePressed(MouseDownEvent e) {
    pressed = true;
    dragged = false;

    pressedMouse = new Vector2(e.getX(), e.getY());
    pressedMesh = tilesetMesh.getPosition().clone();
  }

  private void handleDragged(MouseMoveEvent e) {
    if (!pressed) {
      return;
    }

    Vector2 diff = pressedMouse.clone().sub(new Vector2(e.getX(), e.getY()));

    if (diff.length() <= 1) {
      return;
    }

    dragged = true;

    tilesetMesh.setPosition(pressedMesh.clone().sub(new Vector3(diff.getX(), -diff.getY(), 0)));
  }

  private void handleReleased(MouseUpEvent e) {
    pressed = false;
  }

  private void handleClicked(ClickEvent e) {
    if (dragged) {
      return;
    }

    // Compute the mouse position on the tileset.
    Vector2 loc = new Vector2(e.getX(), e.getY());
    Vector2 center = new Vector2(size.width * 0.5, size.height * 0.5);
    Vector2 wpos = loc.sub(center).add(tilesetMesh.getPosition().clone().multiply(new Vector2(-1, 1)));

    double minx = -tilesetSize.width * 0.5;
    double miny = -tilesetSize.height * 0.5;

    int x = (int) Math.round(wpos.getX() - minx);
    int y = (int) Math.round(wpos.getY() - miny);

    double iw = tilesetSize.width;
    double ih = tilesetSize.height;

    if (x < 0 || y < 0 || x >= iw || y >= ih) {
      // Out of bounds.
      return;
    }

    // // Align to boundary.
    int tw = Integer.valueOf(tileWidth.getText());
    int th = Integer.valueOf(tileHeight.getText());

    x = (x / tw) * tw;
    y = (y / th) * th;

    int border = Integer.valueOf(this.border.getText());

    // Apply the border.
    x += border;
    y += border;
    tw -= border * 2;
    th -= border * 2;

    Vector2d min = new Vector2d((double) x / iw, (double) y / ih);
    Vector2d max = min.clone().add(new Vector2d((double) tw / iw, (double) th / ih));

    Rect uv = new Rect(min, max);

    callback.onCallback(uv);

    uvs = Collections.singletonList(uv);
    assertInitialUVs(false);
  }
}
