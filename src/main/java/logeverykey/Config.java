package logeverykey;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {

	public static final String LOG_PATH_KEY = "log_file";
	public static final String LOG_FILE = "logeverykey.log";
	private Preferences prefs;
	
	public Config() {
		prefs = Preferences.userRoot();
	}

	public Path getLogPath() {
		return Paths.get(prefs.get(LOG_PATH_KEY, getDefaultLogPath()), LOG_FILE);
	}

	private String getDefaultLogPath() {
		return System.getProperty("user.home");
	}

	public void setLogPath(String path) throws BackingStoreException {
		prefs.put(LOG_PATH_KEY, path);
		prefs.flush();
	}

	public void reset() throws BackingStoreException {
		prefs.clear();
		prefs.flush();
	}

}
