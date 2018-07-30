package si.cat.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thothbot.parallax.core.shared.core.GeometryObject;
import thothbot.parallax.core.shared.core.Object3D;
import thothbot.parallax.core.shared.core.Raycaster;
import thothbot.parallax.core.shared.math.Vector3;

public class CustomRaycaster extends Raycaster {
  public CustomRaycaster(Vector3 origin, Vector3 direction) {
    super(origin, direction);
  }

  @SuppressWarnings("unchecked")
  public List<Raycaster.Intersect> intersectObjects(List<? extends Object3D> objects, boolean recursive) {
    List<Raycaster.Intersect> intersects = new ArrayList<Raycaster.Intersect>();

    for (int i = 0, l = objects.size(); i < l; i++) {
      Object3D object = objects.get(i);

      if (object instanceof GeometryObject) {
        intersectObject((GeometryObject) object, this, intersects, recursive);
      } else if (recursive && object.getChildren() != null && object.getChildren().size() > 0) {
        intersects.addAll(intersectObjects(object.getChildren(), recursive));
      }
    }

    Collections.sort(intersects);

    return intersects;

  }

  private void intersectObject(GeometryObject object, Raycaster raycaster, List<Intersect> intersects,
      boolean recursive) {
    object.raycast(raycaster, intersects);

    if (recursive == true) {
      List<Object3D> children = object.getChildren();

      for (int i = 0, l = children.size(); i < l; i++) {
        intersectObject((GeometryObject) children.get(i), raycaster, intersects, true);
      }
    }
  }
}
