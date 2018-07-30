package si.cat.client.utils;

import java.util.List;

import si.cat.shared.Size;
import thothbot.parallax.core.client.gl2.arrays.Float32Array;
import thothbot.parallax.core.shared.core.AbstractGeometry;
import thothbot.parallax.core.shared.core.BufferGeometry;
import thothbot.parallax.core.shared.core.Geometry;
import thothbot.parallax.core.shared.core.GeometryObject;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.core.Object3D.Traverse;
import thothbot.parallax.core.shared.math.Box2;
import thothbot.parallax.core.shared.math.Box3;
import thothbot.parallax.core.shared.math.Vector2;
import thothbot.parallax.core.shared.math.Vector3;

public class GeometryHelper {
  public enum BoxPoint {
    TL, TR, BL, BR
  }

  /**
   * Only does snaps on X, Y. Z is ignored.
   * 
   * @param boxToSnapTo
   * @param boxToSnap
   */
  public static void snap(Object3D objectToSnapTo, BoxPoint pointToSnapTo, Object3D objectToSnap, BoxPoint pointToSnap,
      Vector3 additionalOffset) {
    snap(getBounds(objectToSnapTo), pointToSnapTo, objectToSnap, pointToSnap, additionalOffset);
  }

  public static void snap(Box3 boxToSnapTo, BoxPoint pointToSnapTo, Object3D objectToSnap, BoxPoint pointToSnap,
      Vector3 additionalOffset) {
    Vector2 move = getPoint(boxToSnapTo, pointToSnapTo).sub(getPoint(getBounds(objectToSnap), pointToSnap));

    objectToSnap.getPosition().add(new Vector3(move.getX(), move.getY(), 0)).add(additionalOffset);
  }

  public static Vector2 getPoint(Box3 box, BoxPoint boxPoint) {
    switch (boxPoint) {
      case BL:
        return new Vector2(box.getMin().getX(), box.getMin().getY());
      case BR:
        return new Vector2(box.getMax().getX(), box.getMin().getY());
      case TL:
        return new Vector2(box.getMin().getX(), box.getMax().getY());
      case TR:
        return new Vector2(box.getMax().getX(), box.getMax().getY());
      default:
        return new Vector2(0, 0);
    }
  }

  public static interface AffectsBounds {
    public boolean affects(Object3D obj);
  }

  // Remove the Z component.
  public static Box2 getBounds2d(Object3D obj) {
    Box3 box = getBounds(obj, null);

    return new Box2(new Vector2(box.getMin().getX(), box.getMin().getY()),
        new Vector2(box.getMax().getX(), box.getMax().getY()));
  }

  public static Box3 getBounds(Object3D obj) {
    return getBounds(obj, null);
  }

  public static Box3 getBounds(Object3D obj, final AffectsBounds affectsBounds) {
    final Box3 bounds = new Box3();

    obj.updateMatrixWorld(true);

    // Backtrace all the leaves bounds.
    obj.traverse(new Traverse() {

      @Override
      public void callback(Object3D node) {
        if (affectsBounds != null && !affectsBounds.affects(node)) {
          return;
        }

        if (node instanceof GeometryObject && node.getChildren().size() == 0) {
          Object3D _node = node;

          Box3 traceBox = new Box3();

          boolean set = false;

          while (_node != null) {
            set |= trace(_node, traceBox);

            _node = _node.getParent();
          }

          if (set) {
            bounds.union(traceBox);
          }
        }
      }
    });

    return bounds;
  }

  private static boolean trace(Object3D node, Box3 box) {
    if (node instanceof GeometryObject) {
      AbstractGeometry geometry = ((GeometryObject) node).getGeometry();

      if (geometry == null) {
        return false;
      }

      if (geometry.getBoundingBox() == null) {
        geometry.computeBoundingBox();
      }

      Box3 nodeBox = geometry.getBoundingBox().clone();

      nodeBox.translate(node.getPosition());

      box.union(nodeBox);

      return true;
    } else {
      box.translate(node.getPosition());

      return false;
    }
  }

  public static Size getTransformedSize(Object3D object) {
    Box3 bounds = GeometryHelper.getTransformedBounds(object);

    return new Size(bounds.size().getX(), bounds.size().getY());
  }

  public static Box3 getTransformedBounds(Object3D object) {
    object.updateMatrixWorld(true);

    final Box3 box = new Box3();

    box.makeEmpty();

    object.traverse(new Traverse() {

      @Override
      public void callback(Object3D node) {
        if (!(node instanceof GeometryObject)) {
          return;
        }

        AbstractGeometry geometry = ((GeometryObject) node).getGeometry();

        Vector3 v1 = new Vector3();

        if (geometry != null) {
          if (geometry instanceof Geometry) {
            List<Vector3> vertices = ((Geometry) geometry).getVertices();

            for (int i = 0, il = vertices.size(); i < il; i++) {
              v1.copy(vertices.get(i));
              v1.apply(node.getMatrixWorld());

              box.expandByPoint(v1);
            }

          } else if (geometry instanceof BufferGeometry
              && ((BufferGeometry) geometry).getAttribute("position") != null) {
            Float32Array positions = (Float32Array) ((BufferGeometry) geometry).getAttribute("position").getArray();

            for (int i = 0, il = positions.getLength(); i < il; i += 3) {
              v1.set(positions.get(i), positions.get(i + 1), positions.get(i + 2));
              v1.apply(node.getMatrixWorld());

              box.expandByPoint(v1);
            }
          }
        }
      }
    });

    return box;
  }
}
