package net.jqwik.engine.recording;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class TestRunDatabase {

	private static final Logger LOG = Logger.getLogger(TestRunDatabase.class.getName());

	private final Path databasePath;
	private final TestRunData previousRunData;
	private boolean stopRecording = false;

	public TestRunDatabase(Path databasePath) {
		this.databasePath = databasePath;
		this.previousRunData = loadExistingRunData();
	}

	private TestRunData loadExistingRunData() {
		if (!Files.exists(databasePath)) {
			return new TestRunData();
		}

		try (ObjectInputStream ois = createObjectInputStream()) {
			List<TestRun> data = readAllTestRuns(ois);
			return new TestRunData(data);
		} catch (Exception e) {
			logReadException(e);
			deleteDatabase();
			return new TestRunData();
		}
	}

	private void deleteDatabase() {
		try {
			Files.delete(databasePath);
		} catch (IOException ignore) {
		}
	}

	private List<TestRun> readAllTestRuns(ObjectInputStream ois) throws ClassNotFoundException {
		List<TestRun> testRuns = new ArrayList<>();
		while (true) {
			try {
				TestRun testRun = (TestRun) ois.readObject();
				testRuns.add(testRun);
			} catch (EOFException eof) {
				break;
			} catch (IOException eof) {
				logReadException(eof);
				deleteDatabase();
				break;
			}
		}
		return testRuns;
	}

	private void logReadException(Exception eof) {
		LOG.log(Level.WARNING, eof, () -> String.format("Cannot read database [%s]", databasePath.toAbsolutePath()));
	}

	private void logWriteException(Exception e) {
		LOG.log(Level.WARNING, e, () -> String.format("Cannot write database [%s]", databasePath.toAbsolutePath()));
	}

	private ObjectOutputStream createObjectOutputStream() {
		try {
			return new ObjectOutputStream(Files.newOutputStream(databasePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			stopRecording = true;
			logWriteException(e);
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
			if (stopRecording) {
				return;
			}
			try {
				objectOutputStream.writeObject(testRun);
			} catch (IOException e) {
				stopRecording = true;
				logWriteException(e);
			}
		}

		@Override
		public void close() {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				logWriteException(e);
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
