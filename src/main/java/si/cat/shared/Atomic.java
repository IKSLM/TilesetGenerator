package si.cat.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Atomic<T> implements IsSerializable {
  private T t;

  public T get() {
    return t;
  }

  public void set(T t) {
    this.t = t;
  }
}
