package logeverykey;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static final int DEFAULT_MAX_SIZE = 4096;
	private long maxSize;
	private File logFile;
	private Writer appender;

	public Logger(File logFile, long maxSize) throws IOException {
		this.logFile = logFile;
		initialize();
		this.maxSize = maxSize;
	}
	
	public Logger(File logFile) throws IOException {
		this(logFile, DEFAULT_MAX_SIZE);
	}

	private void initialize() throws IOException {
		appender = new BufferedWriter(new FileWriter(logFile, true));
	}

	public File getFile() {
		return logFile;
	}

	public void append(String text) throws IOException {
		appender.append(text);
		checkRollover();
	}

	private void checkRollover() throws IOException {
		if (overSized()) {
			rollover();
		}
	}
	
	public void close() throws IOException {
		appender.close();
	}

	public void flush() throws IOException {
		appender.flush();
		checkRollover();
	}

	private void rollover() throws IOException {
		archiveLog();
		clearLog();
	}

	private boolean overSized() {
		return logFile.length() > maxSize;
	}

	private void archiveLog() throws IOException {
		copyLogWithSuffix(getDateSuffix());
	}

	private void copyLogWithSuffix(String suffix) throws IOException {
		Files.copy(logFile.toPath(), logFilePathWithSuffix(suffix), StandardCopyOption.REPLACE_EXISTING);
	}

	private Path logFilePathWithSuffix(String suffix) {
		return Paths.get(logFile.toPath().toString() + suffix);
	}

	private String getDateSuffix() {
		return new SimpleDateFormat("_yyMMddHHmmss").format(new Date());
	}

	private void clearLog() throws IOException {
		appender.close();
		Files.write(logFile.toPath(), new byte[]{});
		initialize();
	}

	public long getMaxSize() {
		return maxSize;
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\pb003295\\AppData\\Local\\Temp\\t.txt");
		file.createNewFile();
		Logger logger = new Logger(file, 3);
		logger.append("teste");
		logger.close();
	}

}
