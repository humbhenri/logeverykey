package logeverykey;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;

import org.junit.Before;
import org.junit.Test;

public class ConfigTester {
	
	private Config config;

	@Before
	public void setUp() {
		config = new Config();
	}

	@Test
	public void configFilePath() throws BackingStoreException {
		config.reset();
		assertEquals(Paths.get(userHome(), Config.LOG_FILE), config.getLogPath());
	}
	
	@Test
	public void setLogPath() throws BackingStoreException {
		String newPath = "C:\\tmp\\";
		config.setLogPath(newPath);
		assertEquals(Paths.get(newPath, Config.LOG_FILE), config.getLogPath());
	}

	private String userHome() {
		return System.getProperty("user.home");
	}
}
