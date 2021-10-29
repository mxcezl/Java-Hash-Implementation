package tasks;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderTest {
	// Add BouncyCastle Provider
	static { Security.addProvider(new BouncyCastleProvider()); }
	
	public static void main(String[] args) {
		// Check if BouncyCastle is available
		if(Security.getProvider("BC") == null){
            System.out.println("Bouncy Castle provider is not available");
        }
        else {
            System.out.println("Bouncy Castle provider is available");
        }
		
		// List available providers
		for (Provider provider: Security.getProviders()) {
			  System.out.println(provider.getName());
		}
	}

}
