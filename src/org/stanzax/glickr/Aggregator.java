/**
 * 
 */
package org.stanzax.glickr;

import java.util.HashSet;
import java.util.Set;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * @author basicthinker
 *
 */
public class Aggregator {

	private static final String apiKey = "b4b9f25f25e78fdc60c4eb954ee62d49";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Flickr flickr = new Flickr(apiKey);
		PhotosInterface photos = flickr.getPhotosInterface();
		
		String text = "cat";
		int perPage = 10;
		int numPage = 1;
		
		SearchParameters para = new SearchParameters();
		para.setText(text);
		para.setHasGeo(true);
		Set<String> extras = new HashSet<String>(1, 1);
		extras.add(Extras.GEO);
		para.setExtras(extras);
		Long timeBegin = System.currentTimeMillis();
		try {
			PhotoList list = photos.search(para, perPage, numPage);
			for (Object obj : list) {
				Photo photo = (Photo)obj;
				System.out.println(photo.getGeoData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long latency = System.currentTimeMillis() - timeBegin;
		System.out.println(latency);
	}

}
