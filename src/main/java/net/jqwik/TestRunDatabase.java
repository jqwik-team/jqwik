package net.jqwik;

import net.jqwik.recording.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class TestRunDatabase {

	private static final Logger LOG = Logger.getLogger(TestRunDatabase.class.getName());

	private final Path databasePath;
	private final TestRunData previousRunData;
	private boolean stopRecording = false;

	public TestRunDatabase() {
		this(Paths.get(".jqwik-database"));
	}

	public TestRunDatabase(Path databasePath) {
		this.databasePath = databasePath;
		this.previousRunData = loadExistingRunData();
	}

	private TestRunData loadExistingRunData() {
		if (!Files.exists(databasePath))
			return new TestRunData();

		try (ObjectInputStream ois = createObjectInputStream()) {
			List<TestRun> data = readAllTestRuns(ois);
			return new TestRunData(data);
		} catch (Exception e) {
			logException(e);
			try {
				Files.delete(databasePath);
			} catch (IOException ignore) {
			}
			return new TestRunData();
		}
	}

	private List<TestRun> readAllTestRuns(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		List<TestRun> testRuns = new ArrayList<>();
		while (true) {
			try {
				TestRun testRun = (TestRun) ois.readObject();
				testRuns.add(testRun);
			} catch (EOFException eof) {
				break;
			}
		}
		return testRuns;
	}

	private void logException(Exception e) {
		LOG.log(Level.SEVERE, e.getMessage(), e);
	}

	private ObjectOutputStream createObjectOutputStream() {
		try {
			return new ObjectOutputStream(Files.newOutputStream(databasePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			stopRecording = true;
			logException(e);
			return null;
		}
	}

	private ObjectInputStream createObjectInputStream() throws IOException {
		return new ObjectInputStream(Files.newInputStream(databasePath, StandardOpenOption.CREATE));
	}

	private class Recorder implements TestRunRecorder {

		private final ObjectOutputStream objectOutputStream;

		private Recorder(ObjectOutputStream objectOutputStream) {
			this.objectOutputStream = objectOutputStream;
		}

		@Override
		public void record(TestRun testRun) {
			if (stopRecording)
				return;
			try {
				objectOutputStream.writeObject(testRun);
			} catch (IOException e) {
				stopRecording = true;
				logException(e);
			}
		}

		@Override
		public void close() {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				logException(e);
			}
		}

	}

	public TestRunData previousRun() {
		return previousRunData;
	}

	public TestRunRecorder recorder() {
		return new Recorder(createObjectOutputStream());
	}
}
