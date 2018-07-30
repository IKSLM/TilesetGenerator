package si.cat.shared;

import lombok.Data;

@Data
public class Pair<S, T> {
  private final S left;
  private final T right;

  public static <S, T> Pair<S, T> of(S left, T right) {
    return new Pair<>(left, right);
  }

  public static <S> Pair<S, Void> of(S left) {
    return of(left, null);
  }

  public boolean contains(Object o) {
    return (o.equals(left) || o.equals(right));
  }
}
