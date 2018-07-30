package si.cat.shared;

import java.io.Serializable;

import javax.vecmath.Vector2d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rect implements Serializable {
  private Vector2d min;
  private Vector2d max;

  public Vector2d size() {
    return max.clone().sub(min);
  }

  public Rect clone() {
    return new Rect(min.clone(), max.clone());
  }
}
