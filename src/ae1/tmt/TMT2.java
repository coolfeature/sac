package ae1.tmt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import ae1.sac.Hex16;
import ae1.sac.Sac;
import ae1.sac.Table;

public class TMT2 {
	
	public static void decryptChained(int key, String cipher) {
		if (key != -1) {
			System.out.println("The key 0x" + Integer.toHexString(key) 
					+ "(" + key + ") has been found - DECRYPTING...");
			String msg = Sac.blockToText(Sac.decryptAllBlocks(cipher, key));
			System.out.println("***\n" + msg + "***");
		} else {
			System.out.println("Not found");
		}
	}

	public static int findKey(int l,Table table,String firstPlainTextBlock,String cipher) {
		if (firstPlainTextBlock.length() == 2) {
			firstPlainTextBlock = Sac.out2(firstPlainTextBlock.charAt(0), firstPlainTextBlock.charAt(1));
		}
		String firstCipherBlock = cipher.split("\\n")[0];
		int key = table.find(Hex16.convert(firstCipherBlock));
		if (key != -1) {
			return TMT1.computeChain(key,l-1,firstPlainTextBlock);
		} else {
			return -1;
		}
	}
	/**
	 * Reads in the chain table from a file with the specified name.
	 * @param fileName
	 * @return
	 */
	public static Table readFile(String fileName) {
		Table table = new Table();
		try {
			FileReader fr = null;
			Scanner s = null;
			try {
				fr = new FileReader(fileName);
				s = new Scanner(fr);
				String[] line = null;
				while (s.hasNextLine()) {
					line = s.nextLine().split("[\\s]+");
					table.add(Integer.parseInt(line[0].trim())
							,Integer.parseInt(line[1].trim()));
				}		
			} finally {
				if (fr != null) { fr.close(); };
				if (s != null) { s.close(); };
			}
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table;
	}

}
