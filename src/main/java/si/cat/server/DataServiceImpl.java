package si.cat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import javax.vecmath.Vector3d;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import lombok.extern.log4j.Log4j;
import si.cat.client.DataService;
import si.cat.shared.model.Project;

@SuppressWarnings("serial")
@Log4j
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

  private static final ApplicationProperties APPLICATION_PROPERTIES = new ApplicationProperties();
  private static final File PROJECT_DIR = new File(APPLICATION_PROPERTIES.project_dir());

  @Override
  public String loadRemoteTexture(String url) {

    try (InputStream in = new URL(url).openStream()) {
      String encoded = getPrefix(url) + Base64.encodeBase64String(IOUtils.toByteArray(in));

      return encoded;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private String getPrefix(String url) {
    return "data:image/" + FilenameUtils.getExtension(url).toLowerCase() + ";base64,";
  }

  @Override
  public String save(Project project, String uid) {

    // If UID file already exists, we overwrite.
    boolean ow = true;
    if (uid == null || !getProjectFile(uid).exists()) {
      ow = false;
      UUID uuid = UUID.randomUUID();
      uid = uuid.toString();
    }

    log.debug("Saving project to '" + uid + "'. Overwrite: " + ow);

    try (FileOutputStream out = new FileOutputStream(getProjectFile(uid))) {
      out.write(SerializationUtils.serialize(project));
    } catch (Exception e) {
      log.error("Error saving project!", e);
      return "Error saving project!";
    }

    return uid;
  }

  private File getProjectFile(String uid) {
    return new File(PROJECT_DIR, uid);
  }

  @Override
  public Project load(String uid) {
    try (FileInputStream in = new FileInputStream(new File(PROJECT_DIR, uid))) {
      Project p = SerializationUtils.deserialize(in);

      // Handle manual field setting (deserialization of older serialized objects).
      p.getTiles().forEach(t -> {
        if (t.getRelativeOffsets() == null) {
          t.setRelativeOffsets(new Vector3d(0.0, 0.0, 0.0));
        }
      });

      return p;
    } catch (Exception e) {
      log.error("Error loading project!", e);
      return null;
    }
  }
}
