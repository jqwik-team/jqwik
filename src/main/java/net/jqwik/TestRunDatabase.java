package net.jqwik;

import net.jqwik.recording.*;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

public class TestRunDatabase {

	private final Path databasePath;
	private final ObjectOutputStream objectOutputStream;

	private static final Logger LOG = Logger.getLogger(TestRunDatabase.class.getName());
	private boolean stopRecording = false;

	public TestRunDatabase() {
		this(Paths.get(".jqwik-database"));
	}

	public TestRunDatabase(Path databasePath) {
		this.databasePath = databasePath;
		this.objectOutputStream = createDataOutputStream();
	}

	private ObjectOutputStream createDataOutputStream() {
		try {
			return new ObjectOutputStream(Files.newOutputStream(databasePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			stopRecording = true;
			LOG.severe(e.toString());
			return null;
		}
	}

	private class Recorder implements TestRunRecorder {
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
		return new TestRunData();
	}

	public TestRunRecorder recorder() {
		return new Recorder();
	}
}
