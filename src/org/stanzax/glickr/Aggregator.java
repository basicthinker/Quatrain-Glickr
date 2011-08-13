/**
 * 
 */
package org.stanzax.glickr;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.geo.GeoInterface;

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
		GeoInterface geo = flickr.getGeoInterface();
		
		String text = "cat";
		int perPage = 10;
		int numPage = 1;
		
		SearchParameters para = new SearchParameters();
		para.setText(text);
		para.setHasGeo(true);
		try {
			PhotoList list = photos.search(para, perPage, numPage);
			for (Object obj : list) {
				Photo photo = (Photo)obj;
				GeoData geoData = geo.getLocation(photo.getId());
				System.out.println(geoData.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
