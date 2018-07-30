package si.cat.client.view;

import si.cat.client.geometries.DoubleUVQuadBufferGeometry;
import thothbot.parallax.core.client.gl2.enums.TextureMagFilter;
import thothbot.parallax.core.client.gl2.enums.TextureMinFilter;
import thothbot.parallax.core.client.textures.Texture;
import thothbot.parallax.core.shared.core.AbstractGeometry;
import thothbot.parallax.core.shared.materials.Material;
import thothbot.parallax.core.shared.materials.MeshPhongMaterial;
import thothbot.parallax.core.shared.math.Box2;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;
import thothbot.parallax.core.shared.objects.Mesh;

// Test blending.
public class ColorMulEditorView extends EditorView {

  @Override
  protected void onStart() {
    super.onStart();

    double s = 100;

    AbstractGeometry geom = new DoubleUVQuadBufferGeometry(new Box2(new Vector2(0, 0), new Vector2(1, 1)), s, s);

    // geom = new DoubleUVPlaneBoxGeometry(s, s, s, new Vector2(1, 1), new Vector2(0, 0), new
    // Vector2(1, 1),
    // new Vector2(0, 0), Plane.values());

    Mesh base = new Mesh(geom, getMaterial(t1(), t2()));

    // base.rotateY(Math.PI / 4.0);

    getScene().add(base);

    ///////////////

    geom = new DoubleUVQuadBufferGeometry(new Box2(new Vector2(0, 0), new Vector2(1, 1)), s, s);

    // geom = new DoubleUVPlaneBoxGeometry(s, s, s, new Vector2(1, 1), new Vector2(0, 0), new
    // Vector2(1, 1),
    // new Vector2(0, 0), Plane.values());

    base = new Mesh(geom, getMaterial(t1(), t3()));
    base.setPosition(new Vector3(s, 0, 0));

    // base.rotateY(Math.PI / 4.0);

    getScene().add(base);

    camera.setPosition(new Vector3(0, 0, 1000));
  }

  private Material getMaterial(Texture t, Texture mul) {
    // return new Mul().getMaterial(t, mul);
    MeshPhongMaterial mp = new MeshPhongMaterial();
    mp.setMap(t);
    mp.setLightMap(mul);

    // mp.setAlphaMap(mul);
    // mp.setTransparent(true);

    return mp;
  }

  private Texture t1() {
    Texture t = new Texture("./static/gfx/base.png", tx -> {});
    t.setMinFilter(TextureMinFilter.NEAREST);
    t.setMagFilter(TextureMagFilter.NEAREST);

    return t;
  }

  private Texture t2() {
    Texture t = new Texture("./static/gfx/overlay4.png", tx -> {});
    t.setMinFilter(TextureMinFilter.LINEAR);
    t.setMagFilter(TextureMagFilter.LINEAR);

    return t;
  }

  private Texture t3() {
    Texture t = new Texture("./static/gfx/overlay2.png", tx -> {});
    t.setMinFilter(TextureMinFilter.LINEAR);
    t.setMagFilter(TextureMagFilter.LINEAR);

    return t;
  }

}
