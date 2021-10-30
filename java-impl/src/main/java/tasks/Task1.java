package tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Task1 {

	public static final int AES_KEY_SIZE = 128; // in bits
	public static final int GCM_IV_SIZE = 92; // in bits
	public static final int GCM_TAG_SIZE = 128; // in bits

	public static void main(String[] args) throws Exception {
		SecretKey secret = generateSecretKey();
		byte[] IV = generateRandomIV();
		
		// Test de chiffrement
		// Cree un fichier dans /resources/encrypted
		File chiffre = encryptFileWithAES(new File(Task1.class.getClassLoader().getResource("Task1_fileToEncrypt").toURI()), secret, IV);
		
		// Test de dechiffrement
		// Cree un fichier dans /resources/decrypted
		File dechiffre = decryptFileWithAES(chiffre, secret, IV);
		
		// Verification du contenu
		URI origine = Task1.class.getClassLoader().getResource("Task1_fileToEncrypt").toURI();
		
		// Verification de dechiffrement avec succes
		if (Long.valueOf(-1).equals(Utils.filesCompareByByte(Paths.get(origine), Paths.get(dechiffre.toURI())))) {
			System.out.println("Verification ok, le fichier a bien été chiffré puis déchiffré correctement.");
		}
	}

	public static SecretKey generateSecretKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance("AES"); // Algo utilise : AES
		kg.init(AES_KEY_SIZE); // Definit la taille de la cle
		return kg.generateKey();
	}

	/* must use SecureRandom class*/
	/* Tableau de bytes renvoye par le return */
	public static byte[] generateRandomIV() throws Exception {

		// Definit la taille du vecteur
		byte[] IV = new byte[GCM_IV_SIZE];
		SecureRandom random = new SecureRandom();

		// Ecrit dans le tableau de bytes
		random.nextBytes(IV);
		return IV;
	}

	// Retourne le fichier chiffre
	public static File encryptFileWithAES(File in, SecretKey secret, byte[] IV) throws Exception {

		// Recuperation du cipher : GCM
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

		// Generation de SecretKeySpec
		SecretKeySpec keySpec = new SecretKeySpec(secret.getEncoded(), "AES");

		// Creation d'un objet GCMParameterSpec
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_SIZE, IV);

		// Inititalisation du cipher en mode chiffrement
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec); // En cryptant

		// Traitement du chiffrement
		byte[] cipherText = cipher.doFinal(Files.readAllBytes(Paths.get(in.getAbsolutePath())));

		// Creation du fichier de retour
		File fileOut = new File("src/main/resources/encrypted/encr_" + in.getName());

		// Ecriture du chiffre dans le fichier
		try (FileOutputStream outputStream = new FileOutputStream(fileOut)) { outputStream.write(cipherText); }

		return fileOut;
	}

	public static File decryptFileWithAES(File in, SecretKey secret, byte[] IV) throws Exception {
		
		// Recuperation du cipher : GCM
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

		// Generation de SecretKeySpec
		SecretKeySpec keySpec = new SecretKeySpec(secret.getEncoded(), "AES");

		// Creation d'un objet GCMParameterSpec
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_SIZE, IV);

		// Inititalisation du cipher en mode dechiffrement
		cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

		// Traitement du dechiffrement
		byte[] decryptedText = cipher.doFinal(Files.readAllBytes(Paths.get(in.getAbsolutePath())));

		// Creation du fichier de retour
		File fileOut = new File("src/main/resources/decrypted/decr_" + in.getName());

		// Ecriture du message dans le fichier
		try (FileOutputStream outputStream = new FileOutputStream(fileOut)) { outputStream.write(decryptedText); }
		
		return fileOut;
	}
}
