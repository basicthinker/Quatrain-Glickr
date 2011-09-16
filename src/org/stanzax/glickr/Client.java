/**
 * 
 */
package org.stanzax.glickr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

import org.stanzax.quatrain.client.MrClient;
import org.stanzax.quatrain.client.ReplySet;
import org.stanzax.quatrain.hprose.HproseWrapper;
import org.stanzax.quatrain.io.WritableWrapper;

/**
 * @author basicthinker
 *
 */
public class Client {

	public Client(String host, int port, WritableWrapper wrapper, long timeout,
			String filePath) throws IOException {
		remote = new MrClient(InetAddress.getByName(host), port, wrapper, timeout);
		
		keywords = new ArrayList<String>(100);
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		try {
			String keyword;
			while ((keyword = in.readLine()) != null) {
				keywords.add(keyword);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void warmup() {
		ReplySet rs = remote.invoke(null, "WarmUp");
		while (rs.isPartial()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		rs.close();
	}
	
	public ArrayList<Double> evaSearch(String method) {
		ArrayList<Double> latency = new ArrayList<Double>();
		for (String keyword : keywords) {
			latency.add(evaInvoke(method, keyword));
		}
		return latency;
	}
	
	private double evaInvoke(String method, String keyword) {
		ArrayList<Double> results = new ArrayList<Double>();
		int count;
		double beginTime, totalTime;
		for (int i = 0; i < 3; ++i) {
			beginTime = System.currentTimeMillis();
			ReplySet positions = remote.invoke(String.class, method, keyword);
			count = 0;
			totalTime = 0;
			while ((String)positions.nextElement() != null) {
				totalTime += System.currentTimeMillis() - beginTime;
				++count;
			}
			totalTime /= count;
			results.add(totalTime);
			System.out.print(totalTime + "\t");
			positions.close();
		}
		System.out.println();
		Collections.sort(results);
		return results.get(1);
	}
	
	private MrClient remote;
	private ArrayList<String> keywords;
	
	/**
	 * @param args
	 * 	args[0] Server IP
	 * 	args[1]	Port number
	 * 	args[2]	Timeout
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client(args[0], 
					Integer.valueOf(args[1]), new HproseWrapper(), 
					Long.valueOf(args[2]),
					"KeyWords.txt");
			
			client.warmup();
			ArrayList<Double> normal = client.evaSearch("Search");
			ArrayList<Double> mr = client.evaSearch("MrSearch");
			
			PrintStream out = new PrintStream(
					new File("hprose-glickr-" + (int)System.currentTimeMillis()));
			out.println("#WordNum\tNormalLatency\tMrLatency");
			int count = normal.size();
			if (mr.size() != count) 
				throw new IllegalArgumentException("Keywords diverse of normal search and multi-return search.");
			for (int i = 0; i < count; ++i) {
				out.print((i + 1) + "\t");
				out.print(normal.get(i) + "\t");
				out.println(mr.get(i));
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
