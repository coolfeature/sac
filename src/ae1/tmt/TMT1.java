package ae1.tmt;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ae1.Main;
import ae1.sac.Hex16;
import ae1.sac.Sac;
import ae1.sac.Table;

public class TMT1 {
	
	// set the default length of the chain
	public static int L = 8;

	public static Table buildChainTable(String firstPlainTextBlock) {	
		L = 4;
		boolean allowDups = true;
		return buildChainTable(L, firstPlainTextBlock, allowDups);
	}
	
	/**
	 * Builds chain table.
	 * @param L (length of chain)
	 * @param plainText (2 characters plain text or 16bit hex representation)
	 * @return table
	 */
	public static Table buildChainTable(int L, String firstPlainTextBlock, boolean allowDups) {
		if (firstPlainTextBlock.length() == 2) {
			firstPlainTextBlock = Sac.out2(firstPlainTextBlock.charAt(0), firstPlainTextBlock.charAt(1));
		}
		Table tab = new Table();
		int min = Main.KEY_MIN;
		int max = Main.KEY_MAX; // 2^16
		int N = max / L;
		
		/* The table will do an upsert for every duplicate therefore its size
		 * will usually be smaller and the smaller L, the biggest N and 
		 * hence the change of generating the same starting key increases. 
		 */
		if (allowDups) {
			Random rand = new Random();
			for (int i=0;i<N;i++) {
				int startingKey = rand.nextInt((max - min) + 1) + min;
				int key = computeChain(startingKey,L,firstPlainTextBlock);
				tab.add(key, startingKey);
			}
		} else {
			Integer[] randomKeys = randomKeys(N,min,max);
			for (int startingKey : randomKeys) {
				int key = computeChain(startingKey,L,firstPlainTextBlock);
				//(XL,X0)
				tab.add(key, startingKey);
			}
		}

		return tab;
	}
	
	public static int computeChain(int startingKey, int L, String plainTextBlock) {
		int key = startingKey;
		for (int c=0;c<L;c++) {
			// All this is possible as the data has the same size as key - 16 bit
			String encrypted = Sac.encryptAllBlocks(plainTextBlock, key);
			key = Hex16.convert(encrypted);
		}
		return key;
	}
	
	/**
	 * Returns an array of integers with populated with distinct keys.
	 * @param N
	 * @param min
	 * @param max
	 * @return
	 */
	private static Integer[] randomKeys(int N,int min, int max) {
		List<Integer> keys = new ArrayList<Integer>();
		int i = 0;
		Random rand = new Random();
		while (i<N) {
			int keyCandidate = rand.nextInt((max - min) + 1) + min;
			if (!keys.contains(keyCandidate)) {
				keys.add(keyCandidate);
				i++;
			}
		}
		return keys.toArray(new Integer[N]);
	}
	
	/**
	 * Writes table to file.
	 * @param table
	 * @param fileName
	 */
	public static void writeTableToFile(Table table, String fileName) {
		try {
			FileWriter fw = null;
			try {
				fw = new FileWriter(fileName);
				String line;
				for (Map.Entry<Integer, Integer> v : table.values()) {
					line = String.format("%d %d", v.getKey(),v.getValue());	
					fw.write(line + System.lineSeparator());
				}
			} finally {
				if (fw != null)
					fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
