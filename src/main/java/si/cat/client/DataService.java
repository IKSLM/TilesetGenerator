package si.cat.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import si.cat.shared.model.Project;

@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {

  // Returns a base64 encoded image.
  String loadRemoteTexture(String url);

  // Saves the project (Save As) and returns a project unique identifier (UID). If existing UID is
  // specified, the existing save is overwritten (Save).
  String save(Project project, String uid);

  // Loads the project given the UID.
  Project load(String uid);
}
