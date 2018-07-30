package si.cat.shared;

import java.io.Serializable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class Utils implements Serializable {

  public static final double EPS = 0.00001;

  public static boolean equals(double a, double b, double eps) {
    return Math.abs(a - b) < eps;
  }

  public static boolean equals(double a, double b) {
    return equals(a, b, EPS);
  }
}
