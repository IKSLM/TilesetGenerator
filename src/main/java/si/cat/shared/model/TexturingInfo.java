package si.cat.shared.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import si.cat.shared.Rect;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TexturingInfo implements Serializable {
  private String alias;
  private Rect uv;

  public TexturingInfo clone() {
    return new TexturingInfo(alias, uv == null ? null : uv.clone());
  }

  public void set(String alias, Rect uv) {
    this.alias = alias;
    this.uv = uv;
  }
}
