/**
 * 
 */
package org.stanzax.glickr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;

/**
 * @author basicthinker
 *
 */
public class Aggregator {
	
	public final int perPage = 10;
	public final int numPage = 1;

	public Aggregator(String apiKey) {
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
		endCount.set(0);
		Long timeBegin = System.currentTimeMillis();
		for (final Group group : groups) {
			new java.lang.Thread(new Runnable() {
					public void run() {
						SearchParameters para = new SearchParameters();
						para.setText(text);
						para.setHasGeo(true);
						para.setGroupId(group.getID());
						para.setExtras(extras);
						
						try {
							long timeSearch = System.currentTimeMillis();
							photos.search(para, perPage, numPage);
							long latency = System.currentTimeMillis() - timeSearch;
							System.out.println(group.getName() + "\t" + latency);
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
		Long latency = System.currentTimeMillis() - timeBegin;
		System.out.println(latency);
	}
	
	private ArrayList<Group> groups = new ArrayList<Group>(50);
	private PhotosInterface photos;
	private Set<String> extras;
	private AtomicInteger endCount = new AtomicInteger();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Aggregator aggr = new Aggregator("b4b9f25f25e78fdc60c4eb954ee62d49");
		aggr.Search("cat");

	}

}
