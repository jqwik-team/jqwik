package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.discovery.*;

class LifecycleContextSupport {

	static String formatUniqueId(UniqueId uniqueId) {
		List<String> segments = new ArrayList<>();
		List<UniqueId.Segment> segmentList = uniqueId.getSegments();
		for (int i = 0, segmentListSize = segmentList.size(); i < segmentListSize; i++) {
			UniqueId.Segment segment = segmentList.get(i);
			String s = formatUniqueIdSegment(segment, i);
			segments.add(s);
		}
		return String.join("/", segments);
	}

	private static String formatUniqueIdSegment(UniqueId.Segment segment, int index) {
		String segmentValue = segment.getValue();

		// As long as there is no inbetween segment (eg for packages), normal class containers are on index 1
		if (segment.getType().equals(JqwikUniqueIDs.CONTAINER_SEGMENT_TYPE) && index > 1) {
			int indexOfDollar = segmentValue.lastIndexOf("$");
			if (indexOfDollar > 0) {
				segmentValue = segmentValue.substring(indexOfDollar + 1);
			}
		}
		return String.format("[%s:%s]", segment.getType(), segmentValue);
	}

}
