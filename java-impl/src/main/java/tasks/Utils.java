package tasks;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Random;

public class Utils {
	/*method from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java*/
	private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	public static String bytesToHex(byte[] input) {
	    byte[] hexChars = new byte[input.length * 2];
	    for (int j = 0; j < input.length; j++) {
	        int v = input[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	/*use this function to generate random bytes - do not use for crypto materials*/
	public static void getRandomBytes(byte[] output) {
		new Random().nextBytes(output);
	}
	
	/*method from https://www.baeldung.com/java-compare-files*/
	// Retourne -1 si les fichiers sont identiques, sinon retourne 
	// l'indice du premier byte qui est different entre les deux fichier
	public static long filesCompareByByte(Path path1, Path path2) throws IOException {
	    try (BufferedInputStream fis1 = new BufferedInputStream(new FileInputStream(path1.toFile()));
	         BufferedInputStream fis2 = new BufferedInputStream(new FileInputStream(path2.toFile()))) {
	        
	        int ch = 0;
	        long pos = 1;
	        while ((ch = fis1.read()) != -1) {
	            if (ch != fis2.read()) {
	                return pos;
	            }
	            pos++;
	        }
	        if (fis2.read() == -1) {
	            return -1;
	        }
	        else {
	            return pos;
	        }
	    }
	}
	
	// Compare et affiche le resultat de la comparaison de deux hashs au format String
	public static void compareHashs(String h1, String h2) {
		if (h1.equals(h2)) {
			System.out.println("Les deux hashs sont identiques.");
		} else {
			System.out.println("Les deux hashs sont différents.");
		}
	}
}
