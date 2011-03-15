package org.stanzax.glickr;

public class Application {
	
	public static void main(String[] args) {
		System.out.println("This is Glickr!");
		String keywords = "cat man";
		String search = "";
		int beginNum = 0;
		int endNum = 19;
		GlickrItem items[] = GlickrItem.getGlickrItems(keywords, search, beginNum, endNum);
		for (GlickrItem item : items) {
			System.out.println(item);
		}
	}

}
