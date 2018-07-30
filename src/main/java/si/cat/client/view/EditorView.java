package si.cat.client.view;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Vector3d;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import lombok.Getter;
import lombok.Setter;
import si.cat.client.cache.TextureCache;
import si.cat.client.cache.TextureDescriptorCache;
import si.cat.client.cache.TileCache;
import si.cat.client.geometries.DoubleUVPlaneBoxGeometry;
import si.cat.client.geometries.Plane;
import si.cat.client.geometries.PlaneBoxGeometry;
import si.cat.client.geometries.QuadBufferGeometry;
import si.cat.client.presenter.EditorPresenter.Editor;
import si.cat.client.presenter.handlers.StartHandler;
import si.cat.client.presenter.handlers.UpdateHandler;
import si.cat.client.utils.GeometryHelper;
import si.cat.client.utils.SceneHelper;
import si.cat.shared.Atomic;
import si.cat.shared.Rect;
import si.cat.shared.Size;
import si.cat.shared.model.OverlayTile;
import si.cat.shared.model.Settings;
import si.cat.shared.model.TextureDescriptor;
import si.cat.shared.model.TexturingInfo;
import si.cat.shared.model.Tile;
import si.cat.shared.model.Tile.Side;
import si.cat.shared.model.TilesetInfo;
import thothbot.parallax.core.client.AnimatedScene;
import thothbot.parallax.core.client.events.ViewportResizeEvent;
import thothbot.parallax.core.client.renderers.WebGLRenderer;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.core.AbstractGeometry;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.lights.AmbientLight;
import thothbot.parallax.core.shared.lights.DirectionalLight;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshBasicMaterial;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Box2;
import thothbot.parallax.core.shared.math.Box3;
import thothbot.parallax.core.shared.math.Color;
import thothbot.parallax.core.shared.math.Matrix4;
import thothbot.parallax.core.shared.math.Ray;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;

public class EditorView extends AnimatedScene implements Editor {

  protected static final double ANGLE_Y = 45;
  protected static final double ANGLE_X = 35.264;
  protected static final Texture SELECTOR_TEXTURE_1 = new Texture("./static/gfx/selector.png", tx -> {});
  protected static final Texture SELECTOR_TEXTURE_2 = new Texture("./static/gfx/selector3.png", tx -> {});
  protected static final Texture SELECTOR_TEXTURE_3 = new Texture("./static/gfx/selector5.png", tx -> {});

  interface GuiUiBinder extends UiBinder<Widget, EditorGui> {}

  protected static GuiUiBinder uiBinder = GWT.create(GuiUiBinder.class);

  @Getter
  protected EditorGui gui = new EditorGui();

  @Getter
  protected OrthographicCamera camera = null;

  protected List<StartHandler> startHandlers = null;
  protected List<UpdateHandler> updateHandlers = null;

  protected double duration = 0;

  protected Object3D root = new Object3D();

  // FIXME: if tile with max size is resized, this should also be updated
  @Getter
  protected Size maxTransformedTileSize = new Size(-1, -1);

  @Setter
  protected TextureDescriptorCache textureDescriptorCache;

  @Setter
  protected TextureCache textureCache;

  @Setter
  protected TileCache tileCache;

  protected Settings settings = new Settings();
  protected AmbientLight ambientLight;
  protected DirectionalLight directionalLight;

  // private int frame = 0;

  public EditorView() {

    startHandlers = new Vector<>();
    updateHandlers = new Vector<>();

    gui.initWidget(uiBinder.createAndBindUi(gui), this);
  }

  @Override
  public TilesetInfo getTilesetInfo() {

    Size cs = getCanvasSize();
    int columns = (cs.width() / maxTransformedTileSize.width());
    int tileCount = tileCache.size();
    Size tilesetTileSize = maxTransformedTileSize;

    // Rotate the diagonal by the ISO angle and take the height.
    Vector3 r = new Vector3(tilesetTileSize.width, 0, 0);
    Matrix4 m = new Matrix4();
    m.makeRotationAxis(new Vector3(0, 0, 1), Math.toRadians(ANGLE_X));
    r.apply(m);

    Size mapTileSize = new Size(tilesetTileSize.width, Math.abs(Math.round(r.getY())));

    return new TilesetInfo(columns, tileCount, mapTileSize, tilesetTileSize);
  }

  @Override
  public Widget asWidget() {
    return gui;
  }

  @Override
  protected void onStart() {
    setupView();

    ambientLight = new AmbientLight(0x404040);
    getScene().add(ambientLight);

    directionalLight = new DirectionalLight(0xffffff);
    directionalLight.getPosition().set(0, 500, 1000);
    getScene().add(directionalLight);

    applySettings(settings);

    getScene().add(root);

    getRenderer().setClearColor(0x000000, 0);

    startHandlers.forEach(h -> h.onStart(getRenderer()));
  }

  @Override
  protected void onUpdate(double duration) {
    double delta = (this.duration == 0 ? 0 : duration - this.duration);

    this.duration = duration;

    updateHandlers.forEach(h -> h.onUpdate(this, delta));

    // frame++;

    // if (frame % 10 == 0) {
    getRenderer().render(getScene(), camera);
    // }

    // TODO: when dirty, render to texture, else render the texture
    // TODO: saving project also grabs the screen and stores it
  }

  protected void setupView() {

    Size s = getCanvasSize();

    camera = new OrthographicCamera(s.width(), s.height(), 1, 2000) {
      @Override
      public void onResize(ViewportResizeEvent event) {
        setSize(s.width(), s.height());
      }
    };

    camera.setPosition(new Vector3(s.width * 0.5, -s.height * 0.5, 1000));
  }

  @Override
  public Widget getDomHandler() {
    return getCanvas();
  }

  @Override
  public void addStartHandler(StartHandler handler) {
    startHandlers.add(handler);
  }

  @Override
  public void addUpdateHandler(UpdateHandler handler) {
    updateHandlers.add(handler);
  }

  @Override
  public Ray getRay(Vector2 mouseLocation) {
    return SceneHelper.getRay(mouseLocation, camera, getCanvasSize());
  }

  public Size getCanvasSize() {
    WebGLRenderer r = getRenderer();

    return new Size(r.getAbsoluteWidth(), r.getAbsoluteHeight());
  }

  @Override
  public void addTile(Tile tile, Integer index, boolean update) {

    // Reset the size if we're updating the first tile.
    if (update && tileCache.size() == 1) {
      maxTransformedTileSize.set(-1, -1);
    }

    // Create a mesh with provided size and default material.
    Object3D tileObject = null;

    if (update) {
      tileObject = getTileObject(index);
      tileObject.getChildren().clear();
    } else {

      Object3D container = new Object3D();
      root.add(container);

      tileObject = new Object3D();
      container.add(tileObject);

      // Apply the standard ISO perspective.
    }

    prepareMeshes(tile, tileObject);

    // Reset and if not a billboard, apply the rotation.
    tileObject.setRotationFromMatrix(new Matrix4().identity());
    if (!tile.isBillboard()) {
      tileObject.rotateX(Math.toRadians(ANGLE_X));
      tileObject.rotateY(Math.toRadians(ANGLE_Y));
    }

    // If tile size changed, adjust all the tiles.
    Size transformedTileSize = GeometryHelper.getTransformedSize(tileObject);

    boolean forceLayout = false;

    if (transformedTileSize.width > maxTransformedTileSize.width) {
      maxTransformedTileSize.width = transformedTileSize.width;
      forceLayout = true;
    }
    if (transformedTileSize.height > maxTransformedTileSize.height) {
      maxTransformedTileSize.height = transformedTileSize.height;
      forceLayout = true;
    }

    Size cs = getCanvasSize();

    // Compute where to put the tile.
    if (index == null) {
      index = tileCache.size() - 1;
    }
    int n = (int) Math.round(cs.width / maxTransformedTileSize.width);
    int i = index % n;
    int j = index / n;

    // Meshes are positioned relatively to the right bottom point of the max size!
    if (forceLayout && index > 0) {

      // Max size has been modified, do a full layout.
      for (index = 0; index < root.getChildren().size(); index++) {
        tileObject = getTileObject(index);

        i = index % n;
        j = index / n;

        setTileObjectPosition(tileObject, i, j);
      }

    } else {
      setTileObjectPosition(tileObject, i, j);
    }
  }

  private void setTileObjectPosition(Object3D tileObject, int i, int j) {

    // Place the tile on the sheet according to the max size. Alignment has already been processed
    // when creating a mesh.
    Vector3 pos = new Vector3(getRight(i) - maxTransformedTileSize.width * 0.5,
        getBottom(j) + maxTransformedTileSize.height * 0.5, 0);

    tileObject.setPosition(pos);
  }

  protected void prepareMeshes(Tile tile, Object3D tileObject) {

    if (tile instanceof OverlayTile) {

      // Merge all the overlaid tiles.
      List<Tile> overlays = ((OverlayTile) tile).getOverlayTiles();

      // With depth sorting and transparency disabled and alpha discarding enabled, draw the
      // overlays in top to bottom way.
      for (int i = overlays.size() - 1; i >= 0; i--) {
        // for (int i = 0; i < overlays.size(); i++) {
        prepareMeshes(overlays.get(i), tileObject);
      }
    } else {
      // Each mesh is built from 3 separate geometries so we don't have to deal with
      // multi-texturing.
      for (Side side : Side.values()) {

        // Right is the front face so if the tile is billboard, we need only the front face.
        if (tile.isBillboard() && side != Side.Right) {
          continue;
        }

        Mesh mesh = new Mesh();
        Atomic<Boolean> visible = new Atomic<>();
        mesh.setGeometry(getGeometry(tile, side, visible));
        mesh.setMaterial(getMaterial(tile, side));
        mesh.setVisible(visible.get());

        tileObject.add(mesh);

        double maxSize = maxTransformedTileSize.width / Math.sqrt(2.0);

        Vector3d rs = tile.getRelativeSizes();

        // Then adjust for the aligns.
        switch (tile.getAlignWidth()) {
          case Left:
            mesh.translateX(-(maxSize / 2.0 * (1 - rs.x)));
            break;
          case Right:
            mesh.translateX((maxSize / 2.0 * (1 - rs.x)));
            break;
          default:
            break;
        }

        switch (tile.getAlignHeight()) {
          case Bottom:
            mesh.translateY(-(maxSize / 2.0 * (1 - rs.y)));
            break;
          case Top:
            mesh.translateY((maxSize / 2.0 * (1 - rs.y)));
            break;
          default:
            break;
        }

        switch (tile.getAlignDepth()) {
          case Back:
            mesh.translateZ((maxSize / 2.0 * (1 - rs.z)));
            break;
          case Front:
            mesh.translateZ(-(maxSize / 2.0 * (1 - rs.z)));
            break;
          default:
            break;
        }

        // And the relative offsets.
        Vector3d ro = tile.getRelativeOffsets();
        mesh.translateX(maxSize * ro.x);
        mesh.translateY(maxSize * ro.y);
        mesh.translateZ(maxSize * ro.z);
      }
    }
  }

  protected AbstractGeometry getGeometry(Tile tile, Side side, Atomic<Boolean> visible) {

    Vector2 uvScale = new Vector2(1, 1);
    Vector2 uvTranslate = new Vector2(0, 0);

    Vector2 uvScale2 = new Vector2(1, 1);
    Vector2 uvTranslate2 = new Vector2(0, 0);

    // If there are some custom UVs defined, use that.
    Rect uv = tile.getTextures().get(side).getUv();

    if (uv != null) {
      double sx = uv.size().getX();
      double sy = uv.size().getY();
      uvScale.set(sx, sy);

      uvTranslate.set(uv.getMin().getX(), 1.0 - uv.getMin().getY() - sy);
    }

    Rect uv2 = tile.getMasks().get(side).getUv();

    if (uv2 != null) {
      double sx = uv2.size().getX();
      double sy = uv2.size().getY();
      uvScale2.set(sx, sy);

      uvTranslate2.set(uv2.getMin().getX(), 1.0 - uv2.getMin().getY() - sy);
    }

    boolean doubleUV = (tile.getMasks().get(side).getAlias() != null);

    Vector3d rs = tile.getRelativeSizes();

    if (tile.isUvScaling()) {
      // Automatically adjust the UVs.
      switch (side) {
        case Left:
          uvScale.setX(uvScale.getX() * rs.z);
          uvScale.setY(uvScale.getY() * rs.y);
          break;
        case Right:
          uvScale.setX(uvScale.getX() * rs.x);
          // double dy = uvScale.getY() - uvScale.getY() * rs.y;
          uvScale.setY(uvScale.getY() * rs.y);

          // If enabled, the top of the texture remains mapped, else bottom.
          // uvTranslate.setY(uvTranslate.getY() + dy);
          // uvTranslate2.setY(uvTranslate2.getY() + dy);
          break;
        case Top:
          uvScale.setX(uvScale.getX() * rs.x);
          // dy = uvScale.getY() - uvScale.getY() * rs.y;
          uvScale.setY(uvScale.getY() * rs.z);

          // If enabled, the top of the texture remains mapped, else bottom.
          // uvTranslate.setY(uvTranslate.getY() + dy);
          // uvTranslate2.setY(uvTranslate2.getY() + dy);
          break;
      }
    }

    double tileSize = tile.getSize() / Math.sqrt(2);

    // Billboards are of actual size.
    if (tile.isBillboard()) {
      tileSize = tile.getSize();
    }

    double w = tileSize * rs.x;
    double h = tileSize * rs.y;
    double d = tileSize * rs.z;

    // Limit the minimum size.
    w = Math.max(w, 0.001);
    h = Math.max(h, 0.001);
    d = Math.max(d, 0.001);

    // Visible if height > 0 or top side.
    // visible.set(side == Side.Top || tileSize.height > 0);
    visible.set(true);

    if (doubleUV) {
      return new DoubleUVPlaneBoxGeometry(w, h, d, uvScale, uvTranslate, uvScale2, uvTranslate2,
          Collections.singletonList(getPlane(side)));
    }

    return new PlaneBoxGeometry(w, h, d, uvScale, uvTranslate, Collections.singletonList(getPlane(side)));
  }

  protected Plane getPlane(Side side) {
    switch (side) {
      case Top:
        return Plane.Top;
      case Left:
        return Plane.Right;
      case Right:
        return Plane.Front;
    }

    return null;
  }

  protected Mesh getMesh(Side side, Object3D tileObject) {
    switch (side) {
      case Top:
        return (Mesh) tileObject.getChildren().get(0);
      case Left:
        return (Mesh) tileObject.getChildren().get(1);
      case Right:
        return (Mesh) tileObject.getChildren().get(2);
    }

    return null;
  }

  @Override
  public void refreshTile(Tile tile) {
    int index = tileCache.values().indexOf(tile);

    // Create a new tile and replace the old.
    addTile(tile, index, true);
  }

  protected double getRight(int i) {
    return maxTransformedTileSize.width * i + maxTransformedTileSize.width;
  }

  protected double getBottom(int j) {
    return -maxTransformedTileSize.height * j - maxTransformedTileSize.height;
  }

  @Override
  public void clear() {
    maxTransformedTileSize.set(-1, -1);
    root.getChildren().clear();
  }

  @Override
  public void applySettings(Settings settings) {
    this.settings = settings;

    ambientLight.setColor(new Color(Integer.parseInt(settings.getAmbientLightColor(), 16)));
    directionalLight.setColor(new Color(Integer.parseInt(settings.getDirectionalLightColor(), 16)));

    Vector3d p = settings.getDirectionalLightPosition();

    directionalLight.setPosition(new Vector3(p.x, p.y, p.z));
  }

  @Override
  public Settings getSettings() {
    return settings;
  }

  @Override
  public void clearSelection() {
    // Hide all the selectors.
    for (int index = 0; index < root.getChildren().size(); index++) {
      Object3D selector = getSelector(index);

      if (selector == null) {
        continue;
      }

      selector.setVisible(false);
    }
  }

  @Override
  public void selectTile(Tile tile) {

    if (tile instanceof OverlayTile) {
      // If this is an overlay tile, select all overlays with secondary selector.
      ((OverlayTile) tile).getOverlayTiles().forEach(o -> selectTile(o, SELECTOR_TEXTURE_2));
    }

    // Check if the tile is being used in some overlay tile.
    tileCache.values().stream().filter(t -> t instanceof OverlayTile)
        .filter(t -> ((OverlayTile) t).containsAnyTile(Collections.singletonList(tile.getAlias())))
        .forEach(o -> selectTile(o, SELECTOR_TEXTURE_3));

    selectTile(tile, SELECTOR_TEXTURE_1);
  }

  private void selectTile(Tile tile, Texture selectorTexture) {

    int index = tileCache.values().indexOf(tile);

    Mesh selector = (Mesh) getSelector(index);

    if (selector == null) {
      // Prepare a special selector object.
      selector = new Mesh(getSelectorGeometry(maxTransformedTileSize), getSelectorMaterial(selectorTexture));
      getContainer(index).add(selector);
    }

    selector.setMaterial(getSelectorMaterial(selectorTexture));
    selector.setPosition(getTileObject(index).getPosition().clone().add(new Vector3(0, 0, -100)));
    selector.setVisible(true);
  }

  protected Object3D getTileObject(int index) {
    return root.getChildren().get(index).getChildren().get(0);
  }

  protected Object3D getSelector(int index) {
    List<Object3D> children = root.getChildren().get(index).getChildren();

    return (children.size() == 1 ? null : children.get(1));
  }

  protected Object3D getContainer(int index) {
    return root.getChildren().get(index);
  }

  protected static Material getSelectorMaterial(Texture selectorTexture) {
    MeshBasicMaterial mat = new MeshBasicMaterial();
    mat.setMap(selectorTexture);
    mat.setBlending(Material.BLENDING.NORMAL);
    mat.setTransparent(true);
    return mat;
  }

  protected Material getMaterial(Tile tile, Side side) {

    TexturingInfo info = tile.getTextures().get(side);

    if (info.getAlias() == null) {
      // We have no texture.
      return new MeshPhongMaterial();
    }

    TextureDescriptor td = textureDescriptorCache.get(info.getAlias());

    if (td != null) {

      // Check if we also have a mask.
      TexturingInfo info2 = tile.getMasks().get(side);

      if (info2.getAlias() != null && textureDescriptorCache.get(info2.getAlias()) != null) {
        TextureDescriptor td2 = textureDescriptorCache.get(info2.getAlias());

        Texture diffuse = textureCache.get(td, (tx, size) -> {});
        Texture mask = textureCache.get(td2, (tx, size) -> {});

        MeshPhongMaterial mat = new MeshPhongMaterial();
        mat.setMap(diffuse);
        mat.setLightMap(mask);
        mat.setBlending(Material.BLENDING.NORMAL);
        mat.setTransparent(true);
        mat.setSpecular(new Color(0x000000));
        mat.setShininess(0);
        return mat;

      } else {

        // We only have the base texture.
        MeshPhongMaterial mat = new MeshPhongMaterial();
        mat.setMap(textureCache.get(td, (tx, size) -> {}));
        mat.setBlending(Material.BLENDING.NORMAL);
        mat.setTransparent(true);
        mat.setSpecular(new Color(0x000000));
        mat.setShininess(0);
        return mat;
      }
    }

    // We have no texture.
    return new MeshPhongMaterial();
  }

  protected static AbstractGeometry getSelectorGeometry(Size tileSize) {
    double w = tileSize.width * 1.0;
    double h = tileSize.height * 1.0;

    QuadBufferGeometry geom = new QuadBufferGeometry(new Box2(new Vector2(0, 0), new Vector2(1, 1)), w, h);

    return geom;
  }

  @Override
  public Tile getTile(Vector2 location) {
    Vector3 worldLocation =
        SceneHelper.unproject(new Vector3(location.getX(), location.getY(), 0), camera, getCanvasSize());

    for (int i = 0; i < tileCache.size(); i++) {

      // Check if the clicked world transformed point overlaps any tile. Simple bounds test is
      // enough.
      Box3 box = GeometryHelper.getTransformedBounds(getTileObject(i));

      worldLocation.setZ(box.center().getZ());

      if (box.isContainsPoint(worldLocation)) {
        return tileCache.values().get(i);
      }
    }

    return null;
  }
}
