package ae1.cto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ae1.Main;
import ae1.sac.Sac;

/*
 * * English is highly redundant:
 * ____________________________________________________________________
 * 
 * If all 26 letters had the same probability of occurring the Entropy 
 * of English would be S p(Xi) log2(1/p(Xi)) == log2(26) = 4.7
 * 
 * This is also the ABSOLUTE RATE of the language. In computing however
 * every English character is usually given 8 bits - 1 byte.
 * 
 * The ACTUAL RATE (r) in English is between 1.0 and 1.5. This is the 
 * average number of bits needed for each character in a typical text.
 * (Entropy drops when probabilities raise).
 * 
 * The redundancy of the language is the ABSOLUTE - ACTUAL RATE. 
 * For English it is ~ 3.2 (or 6.5 in computing).
 * 
 * * Unicity distance:
 * ____________________________________________________________________
 * 
 * In English the number of possible messages (strings - N) 
 * is 2^RN: 2^(4.7*N)
 * 
 * In English the number of meaningful strings is 2^rN: 2^(~1*N)
 * 
 * If all messages are equally likely, then the probability of getting 
 * a meaningful message by chance is 2^(r-R)N == 2rN / 2RN == 2^-DN: 
 * 2^((1-4.7)*N) 
 * 
 * 
 */

// How many cipher text blocks are needed to decode the message unambiguously?

/* 
 * * Total number of attempts:
 * ____________________________________________________________________
 * 
 * Unicity Distance is The Entropy of the key space / the redundancy (8 - 1.5)
 * 
 * r = H / N
 * R = log2(N)
 * D = R - r
 * 
 * H(K) / D == 16 / 6.5 == 2.46
 * 
 * 
 * We need 2 letters i.e. 1 cipher text block.
 * 
 */

// The actual number of blocks needed for your particular message
//probability();

public class CTO {
	
	private static final double r = 1.5;
	private static final double R = 8;
	
	public static void bruteForce(String cipher) {
		int maxAttempts = cipher.split("\\n").length;
		int attempts = 1;
		while (attempts <= maxAttempts) {
			Integer[] keys = bruteForceKeys(getStartBlocks(cipher, attempts));
			System.out.println(keys.length + " candidate keys found using block size " + attempts + " " + probabilityOfMeaningfulMsg(attempts,1.5));
			if (keys.length == 1) {
				System.out.println("The key 0x" + Integer.toHexString(keys[0]) 
						+ "(" + keys[0] + ") found after " + attempts + " attempts");
				System.out.println(attempts + " blocks (" + (attempts * 2) 
						+ " characters/bytes needed) - DECRYPTING...");
				String msg = Sac.blockToText(Sac.decryptAllBlocks(cipher, keys[0]));
				System.out.println("***\n" + msg + "***");
				calculateLetterEntropy(msg);
				break;
			}
			attempts++;
		}
	}
	
	/**
	 * Get first no length blocks of cipher
	 * @param cipher
	 * @param no
	 * @return
	 */
	private static String getStartBlocks(String cipher, int no) {
		String cipherBlock[] = cipher.split("\\n");
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<no;i++) {
			if (i != 0) {
				sb.append("\n");
			}
			sb.append(cipherBlock[i]);
		}
		return sb.toString();
	}
	
	
	public static Integer[] bruteForceKeys(String startBlocks) {
		int key = Main.KEY_MIN;
		int max = Main.KEY_MAX;
		List<Integer> keys = new ArrayList<Integer>();
		while (key < max) {	
			String decryptBlockStart[] = Sac.decryptAllBlocks(startBlocks, key).split("\\n");
			int checked = 0;
			while (checked != (decryptBlockStart.length-1)) {
				if (!isEnglish(Sac.blockToText(decryptBlockStart[checked]))) {
					break;
				}
				checked++;
			}
			if (checked == (decryptBlockStart.length-1)) {
				keys.add(key);
			}
			key++;
		}
		return keys.toArray(new Integer[keys.size()]);
	}

	private static boolean isEnglish(String block) {
		//String alphabet = "lm,ivutsrRpy ebano"; -- 7 attempts
		String alphabet = "abcdefghijklmnoqprstuvwxyz ";
		// as a result of decryption some blocks may shrink in size due to NULL character decrypts etc
		if (block.length() > 1) {
			return alphabet.contains(String.format("%c", block.charAt(0)).toLowerCase())
					&& alphabet.contains(String.format("%c", block.charAt(1)).toLowerCase());			
		}
		return false;
	}

	/**
	 * 2H(K) * 2-DN
	 * @return
	 */
	private static double probabilityOfMeaningfulMsg(int n, double r) {
		return Math.pow(2,Main.KEY_LENGTH - ((8 - r) * n));
	}
	/**
	 * ========================================================================
	 */
	private static void probability() {
		for (int i=0;i<10;i++) {
			System.out.println("For n = " + i + " = " + probability(i));
		}
	}
	private static double probability(int n) {
		return Math.pow(2, Main.KEY_LENGTH) * Math.pow(2, ((r - R) * n));
	}
	
	public static double calculateLetterEntropy(String msg) {
		/*1) aggregate letter occurrences in the String */
		Map<Character,Double> occurences = new HashMap<Character,Double>();
		for (Character c : msg.toCharArray()) {
			//System.out.println(c);
			if (!occurences.containsKey(c)) {
				occurences.put(c,(double) 1);
			} else {
				occurences.put(c, occurences.get(c)+1);
			}
		}
		/*2) calculate the sum of occurrences (needed to calculate probabilities) */
		double maxOcc = 0;
		for (Map.Entry<Character, Double> e : occurences.entrySet()) {
			maxOcc += e.getValue();
		}
		
		/*3) calculate the probabilities */ 
		Map<Character,Double> frequencies = new HashMap<Character,Double>();
		for (Map.Entry<Character, Double> e : occurences.entrySet()) {
			frequencies.put(e.getKey(), e.getValue() / maxOcc );
		}
		System.out.println("Frequencies:");
		printMap(frequencies);
		
		/*4) calculate the entropy */
		double entropy = 0.000;
		for (Map.Entry<Character, Double> e : frequencies.entrySet()) {
			entropy = entropy + (e.getValue() * (Math.log(1 / e.getValue()) / Math.log(2)));
		}
		
		System.out.println("\nLetters r = " + (entropy / frequencies.size()) + " (" + entropy + "/" + frequencies.size() + ")");
		return entropy;
	}
	
	
	private static void printMap(Map<Character, Double> map) {
		for (Map.Entry<Character, Double> e : map.entrySet()) {
			System.out.print(e.getKey());
			//System.out.println(e.getValue());
		}
	}
}
