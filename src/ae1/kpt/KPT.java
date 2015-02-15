package ae1.kpt; // KNOWN PLAIN TEXT

import ae1.Main;
import ae1.sac.Sac;

public class KPT {
	
	public static void bruteForce(String plainFirstBlock, String cipher) {
		if (plainFirstBlock.length() == 2) {
			plainFirstBlock = Sac.out2(plainFirstBlock.charAt(0), plainFirstBlock.charAt(1));
		}
		int key = Main.KEY_MIN;
		int max = Main.KEY_MAX;
		String cipherBlocks[] = cipher.split("\\n");
		// The plainFirstBlock corresponds to the first block of cipher text
		String cipherBlockStart = cipherBlocks[0];
		while (key <= max) {
			String plainTextStart = Sac.decryptAllBlocks(cipherBlockStart, key).split("\\n")[0];
			if (plainTextStart.equals(plainFirstBlock)) {
				System.out.println("The key is: 0x" + Integer.toHexString(key) + " DECRYPTING...");
				System.out.println("***\n" + Sac.blockToText(Sac.decryptAllBlocks(cipher, key)) + "***");
				break;
			}
			key++;
		}
	}
}
