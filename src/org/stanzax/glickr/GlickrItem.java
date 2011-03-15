package org.stanzax.glickr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GlickrItem {

	public final String href; // href to original picture page
	public final String thumb; // href to thumb nail image
	public final String title; // name of the picture
	public final String location; // geographic location

	public GlickrItem(String href, String thumb, String title, String location) {
		this.href = href;
		this.thumb = thumb;
		this.title = title;
		this.location = location;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{GlickrItem: href=" + href + ", thumb=" + thumb + ", title="
				+ title + ", location=" + location + "}");
		return sb.toString();
	}

	private static String fetchWebPage(String pageUrl) {
		StringBuffer sb = new StringBuffer();
		try {
			URL url = new URL(pageUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private static ArrayList<GlickrItem> doSearch(
			String urlTemplate,
			String escapedKeywords, int beginNum, int endNum) {
		ArrayList<GlickrItem> items = new ArrayList<GlickrItem>();
		int imagesPerPage = 60;
		int startPageId = beginNum / imagesPerPage + 1;
		int stopPageId = endNum / imagesPerPage + 1;

		for (int pageId = startPageId; pageId <= stopPageId; pageId++) {
			String pageURL = urlTemplate + "&q="
					+ escapedKeywords + "&page=" + pageId;
			String pageSrc = fetchWebPage(pageURL);
			int idx = 0;
			int idx2 = 0;
			int idx3 = 0;
			int idx4 = 0;
			int counter = 0;
			while (true) {
				idx = pageSrc.indexOf("DetailResultItem", idx);
				if (idx < 0) {
					break;
				}
				idx2 = pageSrc.indexOf("\n</div>", idx);
				String pageSrcPartial = pageSrc.substring(idx, idx2);

				// finding original page href
				idx = pageSrc.indexOf("<a href=\"", idx) + 9;
				idx2 = pageSrc.indexOf('"', idx);
				String originalHref = "http://www.flickr.com"
						+ pageSrc.substring(idx, idx2);

				// finding thumb nail
				idx = pageSrc.indexOf("<img src=\"", idx) + 10;
				idx2 = pageSrc.indexOf('"', idx);
				String thumbnail = pageSrc.substring(idx, idx2);

				// finding title
				idx = pageSrc.indexOf("<h3 class=\"PicTitle\">", idx) + 21;
				idx2 = pageSrc.indexOf("</h3>", idx);
				String title = pageSrc.substring(idx, idx2);

				int imageId = (startPageId - 1) * imagesPerPage + counter;

				// finding location info
				idx3 = pageSrcPartial.indexOf("Taken in");
				String placeInfo = "";
				if (idx3 < 0) {
					// no place info
					placeInfo = "";
				} else {
					idx3 += 8;
					idx4 = pageSrcPartial.indexOf("(<a ", idx3);
					placeInfo = cleanPlaceInfo(pageSrcPartial.substring(idx3,
							idx4));
				}
				GlickrItem item = new GlickrItem(originalHref, thumbnail,
						title, placeInfo);
				if (beginNum <= imageId && imageId <= endNum) {
					items.add(item);
				}

				counter++;
			}
		}

		return items;
	}

	private static String cleanPlaceInfo(String placeInfo) {
		StringBuffer sb = new StringBuffer();
		int level = 0;
		for (char ch : placeInfo.toCharArray()) {
			if (level == 0) {
				if (ch == '<') {
					level++;
				} else {
					sb.append(ch);
				}
			} else {
				if (ch == '>') {
					level--;
				} else if (ch == '<') {
					level++;
				}
			}
		}
		String[] splt = sb.toString().split(" |\t|\r|\n");
		StringBuffer cleanInfo = new StringBuffer();
		for (String sp : splt) {
			if (sp.length() == 0) {
				continue;
			}
			if (cleanInfo.length() > 0) {
				cleanInfo.append(" ");
			}
			cleanInfo.append(sp);
		}
		return cleanInfo.toString();
	}


	public static String escapeKeywords(String keywords) {
		StringBuffer sb = new StringBuffer();
		String[] segs = keywords.split(" ");
		for (String seg : segs) {
			if (sb.length() != 0) {
				// append separator "+"
				sb.append("+");
			}
			for (char ch : seg.toCharArray()) {
				if (ch == '+') {
					sb.append("%2B");
				} else if (ch == '%') {
					sb.append("%25");
				} else if (ch == '@') {
					sb.append("%40");
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Get a bunch of glickr items
	 * 
	 * @param keywords
	 *            Search keywords, split with " "
	 * @param groupName
	 *            Place search group name. If it is null or "", then group search
	 *            will be disabled.
	 * @param beginNum
	 *            Start id, used for paging
	 * @param endNum
	 *            Stop id, used for paging
	 * @return
	 */
	public static GlickrItem[] getGlickrItems(String keywords,
			String groupName, int beginNum, int endNum) {
		String escapedKeywords = escapeKeywords(keywords);
		ArrayList<GlickrItem> items = null;
		if (groupName == null || groupName == "") {
			items = doSearch("http://www.flickr.com/search/?z=m&w=all&m=text", escapedKeywords, beginNum, endNum);
		} else {
			// first of all, determine group id
			String groupId = getGroupId(groupName);
			String urlTemplate = "http://www.flickr.com/search/groups/?m=pool&w=" + escapeKeywords(groupId);
			items = doSearch(urlTemplate, escapedKeywords, beginNum,
					endNum);
		}
		return items.toArray(new GlickrItem[items.size()]);
	}

	private static String getGroupId(String groupName) {
		String webUrl = "http://www.flickr.com/groups/" + groupName;
		String pageSrc = fetchWebPage(webUrl);
		int idx = pageSrc.indexOf("var global_group_nsid");
		idx = pageSrc.indexOf('"', idx) + 1;
		int idx2 = pageSrc.indexOf('"', idx);
		String groupId = pageSrc.substring(idx, idx2);
		return groupId;
	}

}
