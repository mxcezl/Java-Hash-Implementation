package tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Task4 {

	// Add Bouncy Castle provider
	static {Security.addProvider(new BouncyCastleProvider());}

	// Constantes contenant le nom des fichiers utilis�s dans cette classe
	private static String AES_ENCRYPTED_FILENAME = "Task4_fileEncryptedWithAES";
	private static String IV_FILENAME = "Task4_ivBytes";
	private static String AES_KEY_FILENAME = "Task4_aesKeyEncrypted";
	private static String SECRET_KEY_RSA_FILENAME = "Task4_skRSA";

	public static void main(String[] args) throws Exception {
		
		// Recup�ration du vecteur IV
		byte[] IV = Utils.readAllBytesFromFile(IV_FILENAME);
		
		// D�chiffre la cl� AES avec la cl� secrete RSA
		byte[] decryptedAESKey = decryptAESKey(AES_KEY_FILENAME, SECRET_KEY_RSA_FILENAME);
		
		// Affichage de la cl� r�cup�r�e
		System.out.println("[AES](" + decryptedAESKey.length + ") Private key gathered = " + new String(decryptedAESKey, StandardCharsets.UTF_8));
		
		// D�chiffrage du fichier avec AES/GCM/NoPadding et la cl� d�chiffr�e
		File decrypted = decryptFile(decryptedAESKey, IV, new File(Utils.class.getClassLoader().getResource(AES_ENCRYPTED_FILENAME).toURI()));
		
		// Affichage de r�ussite (execption lev�e avant cette ligne s'il y a une erreur).
		System.out.println("[AES](" + decryptedAESKey.length + ") File decrypted successfully under src/main/resources/decrypted/" + decrypted.getName());
	}

	// Permet de dechiffrer la cl� AES contenue dans le fichier {encryptedAESKeyFilename} avec la cl� priv�e contenue dans le fichier {rsaSecretKeyFilename}
	public static byte[] decryptAESKey(String encryptedAESKeyFilename, String rsaSecretKeyFilename) throws Exception{
		
		// R�cup�ration du cipher
		Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWITHSHA-256ANDMGF1PADDING");
		
		// Utilisation du cipher en mode d�chiffrement avec la cl� du fichier {rsaSecretKeyFilename}
		cipher.init(Cipher.DECRYPT_MODE, Utils.getPrivateKeyFromBytes(Utils.readAllBytesFromFile(rsaSecretKeyFilename)));
		
		// On renvoie le message (cl� AES)
		return cipher.doFinal(Utils.readAllBytesFromFile(encryptedAESKeyFilename));
	}

	// Dechiffre un fichier pass� en param�tre avec le vecteur IV et la cl� AES pass�s en param�tres
	public static File decryptFile(byte[] aesKeyBytes, byte[] IV, File in) throws Exception{
		
		// Recuperation du cipher : AES/GCM
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

		// Generation de SecretKeySpec
		SecretKeySpec keySpec = new SecretKeySpec(aesKeyBytes, "AES");

		// Creation d'un objet GCMParameterSpec avec le vecteur IV
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(Task1.GCM_TAG_SIZE, IV);

		// Inititalisation du cipher en mode dechiffrement
		cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

		// Traitement du dechiffrement
		byte[] cipherText = cipher.doFinal(Files.readAllBytes(Paths.get(in.getAbsolutePath())));

		// Creation de l'objet de sortie
		File out = new File("src/main/resources/decrypted/4decr_" + in.getName());

		// Ecriture du chiffre dans le fichier
		try (FileOutputStream outputStream = new FileOutputStream(out)) { outputStream.write(cipherText); }

		return out;
	}
}


