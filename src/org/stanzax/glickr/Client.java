/**
 * 
 */
package org.stanzax.glickr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		ArrayList<Double> tri = new ArrayList<Double>();
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
			tri.add(totalTime);
			System.out.print(totalTime + "\t");
			positions.close();
		}
		System.out.println();
		Collections.sort(tri);
		return tri.get(1);
	}
	
	private MrClient remote;
	private ArrayList<String> keywords;
	
	/**
	 * @param args
	 * 	args[0] Server IP
	 * 	args[1]	Port number
	 * 	args[2]	Timeout
	 *  args[3] To warm-up
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client(args[0], 
					Integer.valueOf(args[1]), new HproseWrapper(), 
					Long.valueOf(args[2]),
					"KeyWords.txt");
			
			if (args[3].equals("1")) client.warmup();
			ArrayList<Double> normal = client.evaSearch("Search");
			ArrayList<Double> mr = client.evaSearch("MrSearch");
			
			FileWriter out = new FileWriter("hprose-glickr-" + (int)System.currentTimeMillis());
			BufferedWriter br = new BufferedWriter(out);
			br.write("#WordNum\tNormalLatency\tMrLatency");
			br.write('\n');
			int count = normal.size();
			if (mr.size() != count) 
				throw new IllegalArgumentException("Keywords diverse of normal search and multi-return search.");
			for (int i = 1; i <= count; ++i) {
				br.write(i + "\t");
				br.write(normal.get(i) + "\t");
				br.write(mr.get(i) + "\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
