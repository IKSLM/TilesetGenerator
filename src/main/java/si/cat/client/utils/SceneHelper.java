package si.cat.client.utils;

import si.cat.shared.Size;
import thothbot.parallax.core.shared.cameras.Camera;
import thothbot.parallax.core.shared.cameras.OrthographicCamera;
import thothbot.parallax.core.shared.math.Line3;
import thothbot.parallax.core.shared.math.Plane;
import thothbot.parallax.core.shared.math.Ray;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;

public class SceneHelper {
  public static Ray getRay(Vector2 mouseLocation, Camera camera, Size canvasSize) {
    Vector3 origin = null;
    Vector3 direction = null;

    if (camera instanceof OrthographicCamera) {
      // If the camera is orthographic the rays must be
      // perpendicular to the near plane.
      Vector3 worldLocation0 =
          unproject(new Vector3(mouseLocation.getX(), mouseLocation.getY(), 0.0), camera, canvasSize);
      Vector3 worldLocation1 =
          unproject(new Vector3(mouseLocation.getX(), mouseLocation.getY(), 1.0), camera, canvasSize);

      Vector3 dir = worldLocation1.clone().sub(worldLocation0);

      // Move a "little" back.
      worldLocation0.sub(dir);

      origin = worldLocation0;
      direction = dir.normalize();
    } else {
      // Else, the rays goes from the camera to the cursor
      // location.
      Vector3 worldLocation =
          unproject(new Vector3(mouseLocation.getX(), mouseLocation.getY(), 0.5), camera, canvasSize);

      origin = camera.getPosition();
      direction = worldLocation.clone().sub(camera.getPosition()).normalize();
    }

    return new Ray(origin, direction);
  }

  /**
   * Converts windows (pixel) coordinates into world coordinates.
   * 
   * @param win
   * @param camera
   * @param renderer
   * @return
   */
  public static Vector3 unproject(Vector3 win, Camera camera, Size canvasSize) {
    Size s = canvasSize;

    // Before we unproject the window coordinates (pixels) we have to
    // normalize them to [-1..1].
    return new Vector3((win.getX() / s.width) * 2.0 - 1.0, -(win.getY() / s.height) * 2.0 + 1.0, win.getZ())
        .unproject(camera);
  }

  /**
   * Converts the object coordinates into window coordinates.
   * 
   * @param obj
   * @param camera
   * @param renderer
   * @return
   */
  public static Vector3 project(Vector3 obj, Camera camera, Size canvasSize) {
    // Project to coordinates, this will produce the normalized coordinates.
    obj = obj.clone().project(camera);

    // Now convert the normalized coordinates into window ones. Also invert
    // the Y.
    return new Vector3((obj.getX() + 1.0) * canvasSize.width * 0.5,
        canvasSize.height - (obj.getY() + 1.0) * canvasSize.height * 0.5, obj.getZ());
  }

  /**
   * Converts pixels to [-1..1] relative coordinates.
   * 
   * @param win
   * @param s
   * @return
   */
  public static Vector2 normalize(Vector2 win, Size s) {
    return new Vector2((win.getX() / s.width) * 2.0 - 1.0, (win.getY() / s.height) * 2.0 - 1.0);
  }

  public static Vector2 getViewportSize(OrthographicCamera c) {
    return new Vector2(c.getRight() - c.getLeft(), c.getTop() - c.getBottom());
  }

  public static Vector3 mouseToWorldBasePlaneXY(Vector2 mousePosition, Size canvasSize, Camera camera) {
    Plane p = new Plane().setFromNormalAndCoplanarPoint(new Vector3(0, 0, -1), new Vector3(0, 0, 0));

    return mouseToWorldBasePlane(mousePosition, canvasSize, camera, p);
  }

  public static Vector3 mouseToWorldBasePlaneXZ(Vector2 mousePosition, Size canvasSize, Camera camera) {
    Plane p = new Plane().setFromNormalAndCoplanarPoint(new Vector3(0, 1, 0), new Vector3(0, 0, 0));

    return mouseToWorldBasePlane(mousePosition, canvasSize, camera, p);
  }

  public static Vector3 mouseToWorldBasePlane(Vector2 mousePosition, Size canvasSize, Camera camera, Plane p) {
    // Create a line from near plane to far plane.
    double x = mousePosition.getX();
    double y = mousePosition.getY();

    boolean ortho = (camera instanceof OrthographicCamera);

    Vector3 near = unproject(new Vector3(x, y, ortho ? -1000000.0 : 0.0), camera, canvasSize);
    Vector3 far = unproject(new Vector3(x, y, ortho ? 1000000.0 : 1.0), camera, canvasSize);

    Line3 line = new Line3(near, far);

    return p.intersectLine(line);
  }

  public static Vector2 mouseToWorldOrtho(Vector2 mousePosition, Size canvasSize, Size worldSize, boolean isCentered) {
    double dx = mousePosition.getX() / canvasSize.width;
    double dy = mousePosition.getY() / canvasSize.height;

    return new Vector2(dx * worldSize.width - (isCentered ? worldSize.width / 2 : 0),
        dy * worldSize.height - (isCentered ? worldSize.height / 2 : 0));
  }
}
