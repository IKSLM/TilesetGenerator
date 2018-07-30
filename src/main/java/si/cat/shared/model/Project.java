package si.cat.shared.model;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project implements Serializable {
  private Settings settings;
  private ArrayList<TextureDescriptor> textureDescriptors;
  private ArrayList<Tile> tiles;
}
