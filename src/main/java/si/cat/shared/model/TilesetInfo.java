package si.cat.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import si.cat.shared.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TilesetInfo implements IsSerializable {
  private int columns;
  private int tileCount;
  private Size mapTileSize;
  private Size tilesetTileSize;
}
