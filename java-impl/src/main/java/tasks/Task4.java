package tasks;

import java.io.File;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Task4 {

	// Add Bouncy Castle provider
	static {Security.addProvider(new BouncyCastleProvider());}
	
	public static void main(String[] args) throws Exception {	
		
	}
		
	public static byte [] decryptAESKey(File encryptedAESKey, File rsaSecretKey) throws Exception{
		
		return null; // TODO Remove
	}
	
	public static void decryptFile(File out, byte[] aesKeyBytes, File ivFile, File in) throws Exception{
				
	}
}


