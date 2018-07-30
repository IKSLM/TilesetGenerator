package si.cat.client.geometries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thothbot.parallax.core.shared.core.Face3;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;

/**
 * <img src="http://thothbot.github.com/parallax/static/docs/cube.gif" />
 * 
 * <p>
 * Cube geometry
 * 
 * @author thothbot
 *
 */
public final class DoubleUVPlaneBoxGeometry extends Geometry {
  private int widthSegments;
  private int heightSegments;
  private int depthSegments;
  private Vector2 uvScale = new Vector2(1, 1);
  private Vector2 uvTranslate = new Vector2(1, 1);
  private Vector2 uvScale2 = new Vector2(1, 1);
  private Vector2 uvTranslate2 = new Vector2(1, 1);

  public DoubleUVPlaneBoxGeometry(double width, double height, double depth, Vector2 uvScale, Vector2 uvTranslate,
      Vector2 uvScale2, Vector2 uvTranslate2, List<Plane> planes) {
    this(width, height, depth, uvScale, uvTranslate, uvScale2, uvTranslate2, 1, 1, 1, planes);
  }

  private DoubleUVPlaneBoxGeometry(double width, double height, double depth, Vector2 uvScale, Vector2 uvTranslate,
      Vector2 uvScale2, Vector2 uvTranslate2, int segmentsWidth, int segmentsHeight, int segmentsDepth,
      List<Plane> planes) {
    super();

    this.uvScale = uvScale;
    this.uvTranslate = uvTranslate;

    this.uvScale2 = uvScale2;
    this.uvTranslate2 = uvTranslate2;

    this.widthSegments = segmentsWidth;
    this.heightSegments = segmentsHeight;
    this.depthSegments = segmentsDepth;

    double width_half = width / 2.0;
    double height_half = height / 2.0;
    double depth_half = depth / 2.0;

    if (planes.contains(Plane.Left)) {
      buildPlane("z", "y", -1, -1, depth, height, width_half, 0); // px
    }

    if (planes.contains(Plane.Right)) {
      buildPlane("z", "y", 1, -1, depth, height, -width_half, 1); // nx
    }

    if (planes.contains(Plane.Top)) {
      buildPlane("x", "z", 1, 1, width, depth, height_half, 2); // py
    }

    if (planes.contains(Plane.Bottom)) {
      buildPlane("x", "z", 1, -1, width, depth, -height_half, 3); // ny
    }

    if (planes.contains(Plane.Front)) {
      buildPlane("x", "y", 1, -1, width, height, depth_half, 4); // pz
    }

    if (planes.contains(Plane.Back)) {
      buildPlane("x", "y", -1, -1, width, height, -depth_half, 5); // nz
    }

    this.mergeVertices();
  }

  private void buildPlane(String u, String v, int udir, int vdir, double width, double height, double depth,
      int materialIndex) {
    int gridX = this.widthSegments;
    int gridY = this.heightSegments;
    double width_half = width / 2.0;
    double height_half = height / 2.0;

    // For second UVs.
    getFaceVertexUvs().add(new ArrayList<List<Vector2>>());

    int offset = this.getVertices().size();

    String w = "";

    if ((u.equals("x") && v.equals("y")) || (u.equals("y") && v.equals("x"))) {
      w = "z";
    } else if ((u.equals("x") && v.equals("z")) || (u.equals("z") && v.equals("x"))) {
      w = "y";
      gridY = this.depthSegments;
    } else if ((u.equals("z") && v.equals("y")) || (u.equals("y") && v.equals("z"))) {
      w = "x";
      gridX = this.depthSegments;

    }

    int gridX1 = gridX + 1;
    int gridY1 = gridY + 1;
    double segment_width = width / (double) gridX;
    double segment_height = height / (double) gridY;
    Vector3 normal = new Vector3();

    int normalValue = (depth > 0) ? 1 : -1;
    if (w.equals("x"))
      normal.setX(normalValue);
    else if (w.equals("y"))
      normal.setY(normalValue);
    else if (w.equals("z")) normal.setZ(normalValue);

    for (int iy = 0; iy < gridY1; iy++) {
      for (int ix = 0; ix < gridX1; ix++) {
        Vector3 vector = new Vector3();

        double u1 = (double) (ix * segment_width - width_half) * udir;
        if (u.equals("x"))
          vector.setX(u1);
        else if (u.equals("y"))
          vector.setY(u1);
        else if (u.equals("z")) vector.setZ(u1);

        double v1 = (double) (iy * segment_height - height_half) * vdir;
        if (v.equals("x"))
          vector.setX(v1);
        else if (v.equals("y"))
          vector.setY(v1);
        else if (v.equals("z")) vector.setZ(v1);

        if (w.equals("x"))
          vector.setX(depth);
        else if (w.equals("y"))
          vector.setY(depth);
        else if (w.equals("z")) vector.setZ(depth);

        getVertices().add(vector);
      }
    }

    for (int iy = 0; iy < gridY; iy++) {
      for (int ix = 0; ix < gridX; ix++) {
        int a = ix + gridX1 * iy;
        int b = ix + gridX1 * (iy + 1);
        int c = (ix + 1) + gridX1 * (iy + 1);
        int d = (ix + 1) + gridX1 * iy;

        Vector2 uva = new Vector2(ix / (double) gridX, 1.0 - iy / (double) gridY);
        Vector2 uvb = new Vector2(ix / (double) gridX, 1.0 - (iy + 1.0) / (double) gridY);
        Vector2 uvc = new Vector2((ix + 1.0) / (double) gridX, 1.0 - (iy + 1.0) / (double) gridY);
        Vector2 uvd = new Vector2((ix + 1.0) / (double) gridX, 1.0 - iy / (double) gridY);

        Face3 face = new Face3(a + offset, b + offset, d + offset);
        face.getNormal().copy(normal);
        face.getVertexNormals().addAll(Arrays.asList(normal.clone(), normal.clone(), normal.clone()));
        face.setMaterialIndex(materialIndex);

        this.getFaces().add(face);
        this.getFaceVertexUvs().get(0).add(Arrays.asList(uv(uva), uv(uvb), uv(uvd)));
        this.getFaceVertexUvs().get(1).add(Arrays.asList(uv2(uva), uv2(uvb), uv2(uvd)));

        face = new Face3(b + offset, c + offset, d + offset);
        face.getNormal().copy(normal);
        face.getVertexNormals().addAll(Arrays.asList(normal.clone(), normal.clone(), normal.clone()));
        face.setMaterialIndex(materialIndex);

        this.getFaces().add(face);
        this.getFaceVertexUvs().get(0).add(Arrays.asList(uv(uvb), uv(uvc), uv(uvd)));
        this.getFaceVertexUvs().get(1).add(Arrays.asList(uv2(uvb), uv2(uvc), uv2(uvd)));

      }
    }
  }

  private Vector2 uv(Vector2 uv) {
    uv = uv.clone();
    uv.multiply(uvScale);
    uv.add(uvTranslate);
    return uv;
  }

  private Vector2 uv2(Vector2 uv) {
    uv = uv.clone();
    uv.multiply(uvScale2);
    uv.add(uvTranslate2);
    return uv;
  }
}
