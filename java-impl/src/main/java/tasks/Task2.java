package tasks;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Task2 {

	private static String NAME_FILE_LETTER = "Task2_letter.ps";
	private static String NAME_FILE_ORDER = "Task2_order.ps";
	public static void main(String[] args) throws Exception {

		/////////////////////
		/* ----- MD5 ----- */
		/////////////////////
		byte[] letterMd5Bytes = computeMD5Hashes(new File(Task2.class.getClassLoader().getResource(NAME_FILE_LETTER).toURI()));
		byte[] orderMd5Bytes = computeMD5Hashes(new File(Task2.class.getClassLoader().getResource(NAME_FILE_ORDER).toURI()));

		String md5Letter = Utils.bytesToHex(letterMd5Bytes);
		String md5Order = Utils.bytesToHex(orderMd5Bytes);

		// Affichage des deux hashs MD5
		System.out.println("[MD5] " + NAME_FILE_LETTER + " : " + md5Letter);
		System.out.println("[MD5] " + NAME_FILE_ORDER + "  : " + md5Order);

		// Comparaison et affichage du resultat
		Utils.compareHashs(md5Letter, md5Order);

		// Il y a une collision de hashs, les des hashs MD5 sont identiques bien que le contenus des deux fichiers est différent
		
		/////////////////////////				
		/* ----- SHA-256 ----- */
		/////////////////////////
		byte[] letterSha256Bytes = computeSHA256Hashes(new File(Task2.class.getClassLoader().getResource(NAME_FILE_LETTER).toURI()));
		byte[] orderSha256Bytes = computeSHA256Hashes(new File(Task2.class.getClassLoader().getResource(NAME_FILE_ORDER).toURI()));

		String sha256Letter = Utils.bytesToHex(letterSha256Bytes);
		String sha256Order = Utils.bytesToHex(orderSha256Bytes);

		// Affichage des deux hashs SHA-256
		System.out.println("\n[SHA-256] " + NAME_FILE_LETTER + " : " + sha256Letter);
		System.out.println("[SHA-256] " + NAME_FILE_ORDER + "  : " + sha256Order);

		// Comparaison et affichage du resultat
		Utils.compareHashs(sha256Letter, sha256Order);

		// Il n'y a plus de collision avec le SHA-2, les deux hashs sont différents
		
		//////////////////////
		/* ----- HMAC ----- */
		//////////////////////
		final byte[] HMACSecretKey = "maclesecretepourHMAC".getBytes("UTF-8");
		byte[] letterHMACBytes = computeHMAC(new File(Task2.class.getClassLoader().getResource(NAME_FILE_LETTER).toURI()), HMACSecretKey);
		byte[] orderHMACBytes = computeHMAC(new File(Task2.class.getClassLoader().getResource(NAME_FILE_ORDER).toURI()), HMACSecretKey);

		String HMACLetter = Utils.bytesToHex(letterHMACBytes);
		String HMACOrder = Utils.bytesToHex(orderHMACBytes);

		// Affichage des deux hashs HMAC
		System.out.println("\n[HMAC] " + NAME_FILE_LETTER + " : " + HMACLetter);
		System.out.println("[HMAC] " + NAME_FILE_ORDER + "  : " + HMACOrder);

		// Comparaison et affichage du resultat
		Utils.compareHashs(HMACLetter, HMACOrder);
	}

	public static byte[] computeMD5Hashes(File fileToHash) throws Exception {

		// Creation de l'instance MessageDigest pour le MD5
		MessageDigest md = MessageDigest.getInstance("MD5");

		// Ajout du contenu du fichier dans le digest
		md.update(Files.readAllBytes(Paths.get(fileToHash.getAbsolutePath())));

		//Get the hash's bytes 
		return md.digest();
	}

	public static byte[] computeSHA256Hashes(File fileToHash) throws Exception {

		// Recuperation de l'algorithme SHA-256
		MessageDigest md = MessageDigest.getInstance("SHA-256"); 

		// Renvoie le hash du fichier passé en parametres
		return md.digest(Files.readAllBytes(Paths.get(fileToHash.getAbsolutePath()))); 
	}

	/*use HMAC-SHA256*/
	public static byte[] computeHMAC(File fileToHashMac, byte[] secret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secret, "HmacSHA256");
		mac.init(secretKeySpec);
		return mac.doFinal(Files.readAllBytes(Paths.get(fileToHashMac.getAbsolutePath())));
	}

}

