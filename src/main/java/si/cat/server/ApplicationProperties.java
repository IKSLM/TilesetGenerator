package si.cat.server;

import java.io.IOException;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

@Log4j
public class ApplicationProperties {
  private Properties properties = null;

  public ApplicationProperties() {
    properties = new Properties();
    try {
      properties.load(DataServiceImpl.class.getClassLoader().getResourceAsStream("application.properties"));
    } catch (IOException e) {
      log.error("Can not load the application properties.", e);
    }
  }

  public String project_dir() {
    return properties.getProperty("project.dir");
  }
}
