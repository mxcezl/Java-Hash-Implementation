package tasks;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Task3 {
	// Ajout du provider Bouncy Castle
	static {Security.addProvider(new BouncyCastleProvider());}

	public static void main(String[] args) throws Exception {

		// Generations des clés publiques et privées pour alice et bob
		KeyPair aliceKeys = aliceGenerateDHKeypair();
		KeyPair bobKeys = bobGenerateDHKeypair();

		// KeyAgreement permet l'echange entre alice et bob possible, les clés seront échangées via ces objets
		KeyAgreement aliceKeyAgreement = KeyAgreement.getInstance("DH", "BC");
		aliceKeyAgreement.init(aliceKeys.getPrivate());
		KeyAgreement bobKeyAgreement = KeyAgreement.getInstance("DH", "BC");
		bobKeyAgreement.init(bobKeys.getPrivate());

		// Alice et bob generent la clé privée partagée en s'échangeant leur clés publiques
		String aliceSecret = Utils.bytesToHex(aliceGenerateSharedSecret(aliceKeyAgreement, bobKeys.getPublic()));
		String bobSecret = Utils.bytesToHex(bobGenerateSharedSecret(bobKeyAgreement, aliceKeys.getPublic()));

		// Affichage des clés privées générés par alice et bob
		System.out.println("[Diffie-Hellman](" + aliceSecret.getBytes().length + ") Alice : " + aliceSecret);
		System.out.println("[Diffie-Hellman](" + bobSecret.getBytes().length + ") Bob   : " + bobSecret);

		// On constate qu'ils sont les mêmes
		Utils.compareHashs(aliceSecret, bobSecret);
	}

	// Genere les cles de taille 2048 pour alice
	// Note : il pourait n'y avoir qu'une fonction pour la generation de cle mais je souhaitais separer alice de bob.
	public static KeyPair aliceGenerateDHKeypair() throws Exception{
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DH", "BC");
		keyGenerator.initialize(2048);
		return keyGenerator.generateKeyPair();
	}

	// Genere les cles de taille 2048 pour bob
	// Note : il pourait n'y avoir qu'une fonction pour la generation de cle mais je souhaitais separer alice de bob.
	public static KeyPair bobGenerateDHKeypair() throws Exception{
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DH", "BC");
		keyGenerator.initialize(2048);
		return  keyGenerator.generateKeyPair();
	}

	// Bob envoie sa clé publique à Alice ce qui lui permet de calculer la clé privée partagée
	public static byte[] aliceGenerateSharedSecret(KeyAgreement aliceKeyAgreement, PublicKey bobPublicKey) throws Exception {
		aliceKeyAgreement.doPhase(bobPublicKey, true);
		return aliceKeyAgreement.generateSecret();
	}

	// Alice envoie sa clé publique à Bob ce qui lui permet de calculer la clé privée partagée
	public static byte[] bobGenerateSharedSecret(KeyAgreement bobKeyAgreement, PublicKey alicePublicKey) throws Exception {
		bobKeyAgreement.doPhase(alicePublicKey, true);
		return bobKeyAgreement.generateSecret();
	}
}
