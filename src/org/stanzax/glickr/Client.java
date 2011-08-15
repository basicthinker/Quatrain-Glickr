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

import org.stanzax.quatrain.client.MrClient;
import org.stanzax.quatrain.client.ReplySet;
import org.stanzax.quatrain.hadoop.HadoopWrapper;
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
	
	public ArrayList<Double> evaSearch(String method) {
		ArrayList<Double> latency = new ArrayList<Double>();
		int count;
		double beginTime, totalTime;
		for (String keyword : keywords) {
			beginTime = System.currentTimeMillis();
			ReplySet positions = remote.invoke(String.class, method, keyword);
			count = 0;
			totalTime = 0;
			String position;
			while ((position = (String)positions.nextElement()) != null) {
				totalTime += System.currentTimeMillis() - beginTime;
				++count;
				System.out.print(position);
			}
			System.out.println();
			latency.add(totalTime / count);
			positions.close();
		}
		return latency;
	}
	
	private MrClient remote;
	private ArrayList<String> keywords;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client("localhost", 3122, new HadoopWrapper(), 10000,
					"KeyWords.txt");
			ArrayList<Double> normal = client.evaSearch("Search");
			ArrayList<Double> mr = client.evaSearch("MrSearch");
			
			FileWriter out = new FileWriter("Hadoop-Glickr-" + System.currentTimeMillis());
			BufferedWriter br = new BufferedWriter(out);
			br.write("#WordNum\tNormalLatency\tMrLatency");
			br.write('\n');
			int count = normal.size();
			if (mr.size() != count) 
				throw new IllegalArgumentException("Keywords diverse of normal search and multi-return search.");
			for (int i = 0; i < count; ++i) {
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
