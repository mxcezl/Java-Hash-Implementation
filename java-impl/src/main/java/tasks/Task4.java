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

	// Constantes contenant le nom des fichiers utilisés dans cette classe
	private static String AES_ENCRYPTED_FILENAME = "Task4_fileEncryptedWithAES";
	private static String IV_FILENAME = "Task4_ivBytes";
	private static String AES_KEY_FILENAME = "Task4_aesKeyEncrypted";
	private static String SECRET_KEY_RSA_FILENAME = "Task4_skRSA";

	public static void main(String[] args) throws Exception {
		
		// Recupération du vecteur IV
		byte[] IV = Utils.readAllBytesFromFile(IV_FILENAME);
		
		// Déchiffre la clé AES avec la clé secrete RSA
		byte[] decryptedAESKey = decryptAESKey(AES_KEY_FILENAME, SECRET_KEY_RSA_FILENAME);
		
		// Affichage de la clé récupérée
		System.out.println("[AES](" + decryptedAESKey.length + ") Private key gathered = " + new String(decryptedAESKey, StandardCharsets.UTF_8));
		
		// Déchiffrage du fichier avec AES/GCM/NoPadding et la clé déchiffrée
		File decrypted = decryptFile(decryptedAESKey, IV, new File(Utils.class.getClassLoader().getResource(AES_ENCRYPTED_FILENAME).toURI()));
		
		// Affichage de réussite (execption levée avant cette ligne s'il y a une erreur).
		System.out.println("[AES](" + decryptedAESKey.length + ") File decrypted successfully under src/main/resources/decrypted/" + decrypted.getName());
	}

	// Permet de dechiffrer la clé AES contenue dans le fichier {encryptedAESKeyFilename} avec la clé privée contenue dans le fichier {rsaSecretKeyFilename}
	public static byte[] decryptAESKey(String encryptedAESKeyFilename, String rsaSecretKeyFilename) throws Exception{
		
		// Récupération du cipher
		Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWITHSHA-256ANDMGF1PADDING");
		
		// Utilisation du cipher en mode déchiffrement avec la clé du fichier {rsaSecretKeyFilename}
		cipher.init(Cipher.DECRYPT_MODE, Utils.getPrivateKeyFromBytes(Utils.readAllBytesFromFile(rsaSecretKeyFilename)));
		
		// On renvoie le message (clé AES)
		return cipher.doFinal(Utils.readAllBytesFromFile(encryptedAESKeyFilename));
	}

	// Dechiffre un fichier passé en paramètre avec le vecteur IV et la clé AES passés en paramètres
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


