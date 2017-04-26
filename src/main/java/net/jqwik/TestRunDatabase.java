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
			List<TestRun> data = new ArrayList<>();
			while (true) {
				try {
					TestRun testRun = (TestRun) ois.readObject();
					data.add(testRun);
				} catch (EOFException eof) {
					break;
				}
			}
			return new TestRunData(data);
		} catch (Exception e) {
			LOG.severe(e.toString());
			return new TestRunData();
		}
	}

	private ObjectOutputStream createObjectOutputStream() {
		try {
			return new ObjectOutputStream(Files.newOutputStream(databasePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			stopRecording = true;
			LOG.severe(e.toString());
			return null;
		}
	}

	private ObjectInputStream createObjectInputStream() {
		try {
			return new ObjectInputStream(Files.newInputStream(databasePath, StandardOpenOption.CREATE));
		} catch (IOException e) {
			stopRecording = true;
			LOG.severe(e.toString());
			return null;
		}
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
				LOG.severe(e.toString());
			}
		}

		@Override
		public void close() {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				LOG.severe(e.toString());
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
