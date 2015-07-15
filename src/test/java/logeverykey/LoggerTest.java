package logeverykey;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class LoggerTest {
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
	private File logFile;
	private Logger logger;
	
	@Before
	public void setUp() throws IOException {
		logFile = testFolder.newFile();
		logger = new Logger(logFile);
	}
	
	@Test
	public void createLogger() throws IOException {
		assertEquals(logFile, logger.getFile());
	}
	
	@Test
	public void append() throws IOException {
		String text = "test";
		logger.append(text);
		logger.close();
		assertEquals(text, readLogFile());
	}
	
	@Test
	public void flush() throws IOException {
		logger.append("test1");
		logger.flush();
		logger.append("test2");
		logger.flush();
		assertEquals("test1test2", readLogFile());
	}
	
	@Test
	public void maxDefaultSize() throws IOException {
		for (int i=0; i<100000; i++) {
			logger.append("aaaaaaaaaaaaaaa\n");
		}
		assertTrue(String.format("Log size is %d bytes but log max size id %d\n", logger.getFile().length(), logger.getMaxSize())
				, logger.getFile().length() <= logger.getMaxSize());
	}
	
	@Test
	public void rotate() throws IOException {
		logger = new Logger(logFile, 3);
		logger.append("1234");
		assertEquals("", readLogFile());
	}
	
	@After
	public void tearDown() throws IOException {
		logger.close();
	}
	
	private String readLogFile() throws IOException {
		return new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8);
	}
}
