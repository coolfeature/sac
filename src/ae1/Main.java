package ae1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import ae1.cto.CTO;
import ae1.kpt.KPT;
import ae1.sac.Table;
import ae1.tmt.TMT1;
import ae1.tmt.TMT2;

public class Main {

	// Bit strength
	public static final int KEY_LENGTH = 16;
	// The minimum value of the key as integer
	public static final int KEY_MIN = 0;
	// The maximum value of the key as integer
	public static int KEY_MAX = (int) Math.pow(2, KEY_LENGTH);
	
	public static void main(String[] args) {
		//kpt();
		cto();
		//tmt();
	}
	
	/**
	 * KNOWN PLAIN TEXT ATTACK
	 * --------------------------------------------------------------------
	 * You will be given a chunk of cipher text, together with the 
	 * plaintext corresponding to the first block. 
	 * Write a program to perform a brute force search of the key space to 
	 * find the key. Use this key to decode the rest of the message.
	 * Your report should contain the key you found and the decoded 
	 * message.
	 */
	private static void kpt() {
		System.out.println("--------------------- KPT ----------------------");
		String plainFirstBlock = readBlocksIn("1p.txt").split("\\n")[0];
		String cipher = readBlocksIn("1c.txt");
		KPT.bruteForce(plainFirstBlock, cipher);
	}
	
	/**	
	 * CIPHER TEXT ONLY ATTACK
	 * --------------------------------------------------------------------
	 * You will be given another chunk of cipher text, as above, but no 
	 * plaintext. You will need to perform a brute force attack as before,
	 * knowing that the plaintext is in English. You will also need to 
	 * perform an experiment to find out how many cipher text blocks were
	 * needed to decode the message unambiguously. Your report should 
	 * contain the key you found and the decoded message. You should also 
	 * include a theoretical calculation of the number of blocks of cipher
	 * text needed before unambiguous decoding is possible, together with 
	 * the actual number of blocks needed for your particular message, as
	 * found by your experiment.
	 */
	private static void cto() {
		System.out.println("--------------------- CTO ----------------------");
		String cipher = readBlocksIn("2c.txt");
		CTO.bruteForce(cipher);
	}
	
	/**
	 * TIME MEMORY TRADE-OFF ATTACK
	 * --------------------------------------------------------------------
	 * You will be given the first plaintext block, which you should use to
	 * construct a time memory tradeoff table. This table should be written
	 * to a file, and the simplest way of doing this is to write the 
	 * entries as two integers per line.
	 * 
	 * Some time later you will be given the full cipher text, and your 
	 * second program should read in the table entries from the file, 
	 * construct the table, obtain the first cipher text block, discover 
	 * the key and decode the rest of the cipher text. Since the 
	 * time-memory tradeoff is a probabilistic attack, it is possible that
	 * the key I have used is not in your table. In this case, run the 
	 * table generating program again with different random values and try again. 
	 */
	private static void tmt() {
		System.out.println("--------------------- TMT ----------------------");
		String plainFirstBlock = readBlocksIn("3p.txt").split("\\n")[0];
		//String plainFirstBlock = "ws";
		String cipher = readBlocksIn("3c.txt");
		//String cipher = "0x649f";
		//table filename
		String tableFile = "tmtChainTable.txt";
		// max attempts to get a result for a chain of given length
		int maxAttemptsPerL = 3;
		// chain length
		int startL = 4;
		int endL = 16;
		// allow duplicates in the chain table
		boolean dups = false;
		
		// Try chain length
		for (int l=startL;l<endL;l=l+2) {
			int key = -1;
			// in production the table would be pre-built
			Table table = null;
			for (int a=0;a<maxAttemptsPerL;a++) {
				System.out.print("Searching table of chain length " + l + " attempt " + (a+1) + "\r");
				table = TMT1.buildChainTable(l,plainFirstBlock,dups);
				key = TMT2.findKey(l,table,plainFirstBlock,cipher);
				if (key != -1) {
					TMT1.writeTableToFile(table,tableFile);
					break;
				}
			}
			if (key != -1) {
				table = TMT2.readFile(tableFile);
				TMT2.decryptChained(key, cipher);
				break;
			}		
		}
	}
	
	
	/**
	 * Reads hex blocks in from the specified filename. 
	 * @param filename
	 * @return
	 */
	public static String readBlocksIn(String filename) {
		StringBuffer sb = null;
		try {
			FileReader fr = null;
			Scanner s = null;
			try {
				sb = new StringBuffer();
				fr = new FileReader(filename);
				s = new Scanner(fr);
				while(s.hasNextLine()) {
					sb.append(s.nextLine().trim().replaceAll("\\r", "") + "\n");
				}
			} finally {
				if (s != null) { s.close(); };
				if (fr != null) { fr.close(); };
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
