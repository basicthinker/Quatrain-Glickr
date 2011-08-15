/**
 * 
 */
package org.stanzax.glickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.stanzax.quatrain.hadoop.HadoopWrapper;
import org.stanzax.quatrain.io.WritableWrapper;
import org.stanzax.quatrain.server.MrServer;

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
public class Aggregator extends MrServer {
	
	public final int perPage = 10;
	public final int numPage = 1;

	public Aggregator(String address, int port, WritableWrapper wrapper,
            int handlerCount, String apiKey) throws IOException {
		super(address, port, wrapper, handlerCount);
		
		Flickr flickr = new Flickr(apiKey);
		this.photos = flickr.getPhotosInterface();
		
		this.extras = new HashSet<String>(1, 1);
		extras.add(Extras.GEO);
		
		groups.add(Group.ARGENTINA);
		groups.add(Group.AUSTRIA);
		groups.add(Group.BHUTAN);
		groups.add(Group.BOLIVIA);
		groups.add(Group.BRAZIL);
		groups.add(Group.CANADA);
		groups.add(Group.CHINA);
		groups.add(Group.CUBA);
		groups.add(Group.ENGLAND);
		groups.add(Group.FRANCE);
		groups.add(Group.GERMANY);
		groups.add(Group.GREAT_BRITAIN);
		groups.add(Group.GREECE);
		groups.add(Group.GUYANA);
		groups.add(Group.INDIA);
		groups.add(Group.IRAN);
		groups.add(Group.JAMAICA);
		groups.add(Group.JAPAN);
		groups.add(Group.KAZAKHSTAN);
		groups.add(Group.KOREA);
		groups.add(Group.LAOS);
		groups.add(Group.MALAYSIA);
		groups.add(Group.MEXICO);
		groups.add(Group.NEPAL);
		groups.add(Group.PARAGUAY);
		groups.add(Group.SPAIN);
		groups.add(Group.SWITZERLAND);
		groups.add(Group.THAILAND);
		groups.add(Group.URUGUAY);
		groups.add(Group.USA);
	}
	
	public void Search(final String text) {
		final AtomicInteger endCount = new AtomicInteger();
		final Vector<String> positions = new Vector<String>(groups.size() * perPage);
		for (final Group group : groups) {
			new java.lang.Thread(new Runnable() {
					public void run() {
						SearchParameters para = new SearchParameters();
						para.setText(text);
						para.setHasGeo(true);
						para.setGroupId(group.getID());
						para.setExtras(extras);
						
						try {
							PhotoList list = photos.search(para, perPage, numPage);
							for (Object obj : list) {
								Photo photo = (Photo)obj;
								positions.add(photo.getGeoData().toString());
							}
						} catch (Exception e) {
							System.out.println("Error in " + group.getName() +
									" for " + text + " : " + e.getMessage());
						} finally {
							endCount.incrementAndGet();
							synchronized(Aggregator.this) {
								Aggregator.this.notifyAll();
							}
						}
					}
					
				}
			).start();
		} // trigger multiple threads to search local
		
		synchronized(this) {
			while (endCount.get() != groups.size()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		preturn(positions.toArray());
	}
	
	public void MrSearch(final String text) {
		for (final Group group : groups) {
			new Thread(new Runnable() {
				public void run() {
					SearchParameters para = new SearchParameters();
					para.setText(text);
					para.setHasGeo(true);
					para.setGroupId(group.getID());
					para.setExtras(extras);
					
					try {
						PhotoList list = photos.search(para, perPage, numPage);
						Vector<String> positions = new Vector<String>(perPage);
						for (Object obj : list) {
							Photo photo = (Photo)obj;
							positions.add(photo.getGeoData().toString());
						}
						preturn(positions.toArray());
					} catch (Exception e) {
						System.out.println("Error in " + group.getName() +
								" for " + text + " : " + e.getMessage());
					}
				}
				
			}).start();
		} // trigger multiple threads to search local
	}
	
	private ArrayList<Group> groups = new ArrayList<Group>(50);
	private PhotosInterface photos;
	private Set<String> extras;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Aggregator aggr = new Aggregator("localhost", 3122, new HadoopWrapper(), 10, 
					"b4b9f25f25e78fdc60c4eb954ee62d49");
			aggr.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
