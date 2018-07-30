package si.cat.shared;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings("serial")
@ToString
@NoArgsConstructor
public class Size implements Serializable {
  public double width = 0;
  public double height = 0;

  public Size(double width, double height) {
    super();
    set(width, height);
  }

  public void set(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public int width() {
    return (int) width;
  }

  public int height() {
    return (int) height;
  }

  public Size clone() {
    return new Size(width, height);
  }
}
