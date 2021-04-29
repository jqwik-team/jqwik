package net.jqwik.time.api.testingSupport;

import java.time.*;

public class ForDuration {

	public static int getSecond(Duration d) {
		return (int) (d.getSeconds() % 60);
	}

	public static int getMinute(Duration d) {
		return (int) ((d.getSeconds() % 3600) / 60);
	}

	public static long getHour(Duration d) {
		return d.getSeconds() / 3600;
	}

}
