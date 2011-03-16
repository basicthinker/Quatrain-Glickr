package org.stanzax.glickr;

public class Application {

	private static void queryWithTiming(String keywords, String groupName) {
		int beginNum = 0;
		int endNum = 19;
		long startTime = System.currentTimeMillis();
		GlickrItem.getGlickrItems(keywords, groupName, beginNum, endNum);
		long stopTime = System.currentTimeMillis();
		long duration = stopTime - startTime;
		System.out.println("finished querying \"" + keywords + "\" in group \""
				+ groupName + "\", time cost is " + duration + " ms");
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
