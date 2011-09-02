/**
 * 
 */
package org.stanzax.glickr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.stanzax.quatrain.hprose.HproseWrapper;
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
	
	public void WarmUp() {
		// Retrieve the keyword list
		ArrayList<String> keywords = new ArrayList<String>(100);
		try {
			BufferedReader in = new BufferedReader(new FileReader("KeyWords.txt"));
			String keyword;
			while ((keyword = in.readLine()) != null) {
				keywords.add(keyword);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Build data structure for log
		int cntKeys = keywords.size();
		final ConcurrentHashMap<Group, ArrayList<Long>> results = 
			new ConcurrentHashMap<Group, ArrayList<Long>>();
		results.put(Group.ARGENTINA, new ArrayList<Long>(cntKeys));
		results.put(Group.AUSTRIA, new ArrayList<Long>(cntKeys));
		results.put(Group.BHUTAN, new ArrayList<Long>(cntKeys));
		results.put(Group.BOLIVIA, new ArrayList<Long>(cntKeys));
		results.put(Group.BRAZIL, new ArrayList<Long>(cntKeys));
		results.put(Group.CANADA, new ArrayList<Long>(cntKeys));
		results.put(Group.CHINA, new ArrayList<Long>(cntKeys));
		results.put(Group.CUBA, new ArrayList<Long>(cntKeys));
		results.put(Group.ENGLAND, new ArrayList<Long>(cntKeys));
		results.put(Group.FRANCE, new ArrayList<Long>(cntKeys));
		results.put(Group.GERMANY, new ArrayList<Long>(cntKeys));
		results.put(Group.GREAT_BRITAIN, new ArrayList<Long>(cntKeys));
		results.put(Group.GREECE, new ArrayList<Long>(cntKeys));
		results.put(Group.GUYANA, new ArrayList<Long>(cntKeys));
		results.put(Group.INDIA, new ArrayList<Long>(cntKeys));
		results.put(Group.IRAN, new ArrayList<Long>(cntKeys));
		results.put(Group.JAMAICA, new ArrayList<Long>(cntKeys));
		results.put(Group.JAPAN, new ArrayList<Long>(cntKeys));
		results.put(Group.KAZAKHSTAN, new ArrayList<Long>(cntKeys));
		results.put(Group.KOREA, new ArrayList<Long>(cntKeys));
		results.put(Group.LAOS, new ArrayList<Long>(cntKeys));
		results.put(Group.MALAYSIA, new ArrayList<Long>(cntKeys));
		results.put(Group.MEXICO, new ArrayList<Long>(cntKeys));
		results.put(Group.NEPAL, new ArrayList<Long>(cntKeys));
		results.put(Group.PARAGUAY, new ArrayList<Long>(cntKeys));
		results.put(Group.SPAIN, new ArrayList<Long>(cntKeys));
		results.put(Group.SWITZERLAND, new ArrayList<Long>(cntKeys));
		results.put(Group.THAILAND, new ArrayList<Long>(cntKeys));
		results.put(Group.URUGUAY, new ArrayList<Long>(cntKeys));
		results.put(Group.USA, new ArrayList<Long>(cntKeys));
		
		System.out.print("Warmup finishes: ");
		for (final String text : keywords) {
			final AtomicInteger count = new AtomicInteger();
			for (final Group group : groups) {
				new java.lang.Thread(new Runnable() {
	
					@Override
					public void run() {
						SearchParameters para = new SearchParameters();
						para.setText(text);
						para.setHasGeo(true);
						para.setGroupId(group.getID());
						para.setExtras(extras);
						
						try {
							long beginTime = System.currentTimeMillis();
							photos.search(para, perPage, numPage);
							results.get(group).add(System.currentTimeMillis() - beginTime);
						} catch (Exception e) {
							results.get(group).add(Long.MAX_VALUE);
							System.out.println("[WarmUp]Error in " + group.getName() +
									" for " + text + " : " + e.getMessage());
						} finally {
							int current = count.incrementAndGet();
							if (current == groups.size()) {
								synchronized(text) {
									text.notifyAll();
								}
							}
						}
					}
					
				}).start();
			}
			synchronized(text) {
				while (count.get() != groups.size()) {
					try {
						text.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.print(" " + text);
		} // for each key word
		System.out.println();
		
		// Output log
		PrintStream printer;
		try {
			printer = new PrintStream(
					new File("glickr-warmup-stat-" + System.currentTimeMillis()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			printer = System.out;
		}
		printer.print("#");
		for (Group group : groups) {
			printer.print("\t" + group.getName());
		}
		printer.println();
		for (int i = 0; i < cntKeys; ++i) {
			printer.print((i + 1) + "\t");
			for (Group group : groups) {
				printer.print("\t" + results.get(group).get(i));
			}
			printer.println();
		}
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
							System.out.println("[Search] Error in " + group.getName() +
									" for " + text + " : " + e.getMessage());
						} finally {
							int current = endCount.incrementAndGet();
							if (current == groups.size()) {
								synchronized(Aggregator.this) {
									Aggregator.this.notifyAll();
								}
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
		String[] arrayPos = new String[positions.size()];
		int i = 0;
		for (String position : positions) {
			arrayPos[i++] = position;
		}
		preturn(arrayPos);
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
						String[] positions = new String[list.size()];
						int i = 0;
						for (Object obj : list) {
							Photo photo = (Photo)obj;
							positions[i++] = photo.getGeoData().toString();
						}
						preturn(positions);
					} catch (Exception e) {
						System.out.println("[MrSearch] Error in " + group.getName() +
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
	 * 	args[0] Server IP
	 * 	args[1]	Port number
	 * 	args[2]	Count of worker threads
	 */
	public static void main(String[] args) {
		try {
			Aggregator aggr = new Aggregator(args[0], 
					Integer.valueOf(args[1]), new HproseWrapper(), 
					Integer.valueOf(args[2]), 
					"b4b9f25f25e78fdc60c4eb954ee62d49");
			aggr.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
