package org.stanzax.glickr;

import java.util.Date;

public class Application {

	private static void queryWithTiming(String keywords, String groupName) {
		int beginNum = 0;
		int endNum = 19;
		Date startTime = new Date();
		GlickrItem.getGlickrItems(keywords, groupName, beginNum, endNum);
		Date stopTime = new Date();
		long milli = stopTime.getTime() - startTime.getTime();
		System.out.println("finished querying \"" + keywords + "\" in group \""
				+ groupName + "\", time cost is " + milli + " ms");
	}

	public static void main(String[] args) {
		System.out.println("This is Glickr!");
		String[] groups = { "china", "usaunitedstatesofamerica", "japan",
				"australia", "canada", "norway", "england", "france", "egypt",
				"south_africa", "madagascar" };
		String keywords = "cat";
		for (String group : groups) {
			queryWithTiming(keywords, group);
		}
	}

}
