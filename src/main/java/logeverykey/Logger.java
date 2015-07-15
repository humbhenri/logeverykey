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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Logger {

	private static final int DEFAULT_MAX_SIZE = 4096;

	private static final int SECONDS_BETWEEN_FLUSH = 60;

	private static final int NUM_THREADS = 1;

	private long maxSize;

	private File logFile;

	private Writer appender;

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(NUM_THREADS);

	public Logger(File logFile, long maxSize) throws IOException {
		this.logFile = logFile;
		initialize();
		this.maxSize = maxSize;
		executor.scheduleAtFixedRate(createPeriodicFlush(), 0, SECONDS_BETWEEN_FLUSH, TimeUnit.SECONDS);
	}

	public Logger(File logFile) throws IOException {
		this(logFile, DEFAULT_MAX_SIZE);
	}

	private void initialize() throws IOException {
		appender = new BufferedWriter(new FileWriter(logFile, true));
	}

	private Runnable createPeriodicFlush() {
		return new Runnable() {
			public void run() {
				try {
					flush();
					System.out.println("auto flush");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
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
		Files.write(logFile.toPath(), new byte[] {});
		initialize();
	}

	public long getMaxSize() {
		return maxSize;
	}

}
