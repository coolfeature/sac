package ae1.sac;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Sac {

	/* ------------------------ DECRYPT ALL BLOCKS --------------------------*/

	public static String decryptAllBlocks(String s, int key) {
		StringBuffer sb = new StringBuffer();
		String[] allBlocks = s.split("\\n");
		for (String block : allBlocks) {
			int	c = Hex16.convert(block);
			int	p = Coder.decrypt(key, c);
			sb.append(String.format("0x%04x\n", p));
		}
		return sb.toString();
	}
	
	public static String decryptAllBlocks(String s, String strKey) {
		return decryptAllBlocks(s,Hex16.convert(strKey));
	}
	
	/* ------------------------ ENCRYPT ALL BLOCKS --------------------------*/
	
	public static String encryptAllBlocks(String s, int key) {
		StringBuffer sb = new StringBuffer();
		String[] allBlocks = s.split("\\n");
		for (String block : allBlocks) {
			int	p = Hex16.convert(block);
			int	c = Coder.encrypt(key, p);
			sb.append(String.format("0x%04x\n", c));
		}
		return sb.toString();
	}
	
	public static String encryptAllBlocks(String s, String strKey) {
		return encryptAllBlocks(s, Hex16.convert(strKey));
	}
	
	/* -------------------------- TEXT TO BLOCK -----------------------------*/
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static String textToBlock(String filename) {
		StringBuffer sb = null;
		try {
			FileReader fr = null;
			try {
				sb = new StringBuffer();
				fr = new FileReader(filename);
				int	count = 0;
				char ch0 = 0;
				int c = 0;
				while ((c = fr.read()) != -1) {
					char ch1 = (char) c;
					count++;
					if (count % 2 == 0)	// two chars for full block
						sb.append(out2(ch0, ch1));
					ch0 = ch1;	// remember this one
				}
				if (count % 2 == 1)	// odd number, pad with 0
					sb.append(out2(ch0, (char)0));				
			} finally {    //if the files were opened successfully then close them
				if (fr != null) { fr.close(); };
			}
		} catch (FileNotFoundException e) {   //if the files were not found then catch this exception and print an error
			e.printStackTrace();
		} catch (IOException e) {   //if there was an error reading the contents from the file then print an error
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public	static	String	out2(char c0, char c1) {
		return String.format("0x%02x%02x\n", (int) c0, (int) c1);
	}
	
	/* -------------------------- BLOCK TO TEXT -----------------------------*/
	
	/**
	 * 
	 * @param s (Strings of 0x1234 format concatenated with \n)
	 */
	public static String blockToText(String s) {
		StringBuffer sb = new StringBuffer();
		String[] allBlocks = s.split("\\n");
		for (String block : allBlocks) {
			int	i = Hex16.convert(block);
			int	c0 = i / 256;
			int	c1 = i % 256;
			sb.append(c1 != 0 ? new String(new char[]{(char)c0,(char)c1}) : new String(new char[]{(char)c0}));
		}
		return sb.toString();
	}
	
	/* ------------------------------ HEX16 ---------------------------------*/
	
	/**
	 * Converts 0x1234 hex string into int
	 * @param s (String of 0x1234 format)
	 * @return
	 */
	public	static	int	convert(String s) {
		int	i0 = hex2int(s.charAt(2));
		int	i1 = hex2int(s.charAt(3));
		int	i2 = hex2int(s.charAt(4));
		int	i3 = hex2int(s.charAt(5));
		return i3 + 16 * (i2 + 16 * (i1 + 16 * i0));
	}
	
	private	static	int	hex2int(char c) {
		if (c >= '0' && c <= '9')
			return (int)(c - '0');
		else if (c >= 'a' && c <= 'f')
			return (int) (c - 'a') + 10;
		else if (c >= 'A' && c <= 'F')
			return (int) (c - 'A') + 10;
		else
			return 0;
	}

}
