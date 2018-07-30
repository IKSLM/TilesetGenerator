package si.cat.shared.model;

import java.io.Serializable;

import javax.vecmath.Vector3d;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Settings implements Serializable {
  private String ambientLightColor = "404040";
  private String directionalLightColor = "FFFFFF";
  private Vector3d directionalLightPosition = new Vector3d(0, 500, 1000);
}
