package org.komparator.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class CryptoUtil {
	
	private static final String CA_WS_NAME = "CA";

	private static final String UDDI_URL = "http://a68:peBiX6UK@uddi.sd.rnl.tecnico.ulisboa.pt:9090/";

	private static final String CA_URL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca";

	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	
	private static final String SIGNATURE_ALGO = "SHA256withRSA";
	
	private static final String PASSWORD = "peBiX6UK";
	
	//Este throws e inapropriado, mas eh o que Eles fazem nos testes TODO
	public static byte[] asymCipher(byte[] plainBytes, PublicKey key) throws BadPaddingException {
			//throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(ASYM_CIPHER);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No Such Algorithm");
			return null;
		} catch (NoSuchPaddingException e) {
			System.err.println("No Such Padding");
			return null;
		}
		//NoSuchAlgorithmException, NoSuchPaddingException
		
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (InvalidKeyException e) {
			System.err.println("Invalid Key");
			return null;
		}
		//InvalidKeyException
		
		byte[] cipherBytes = null;
		try {
			cipherBytes = cipher.doFinal(plainBytes);
		} catch (IllegalBlockSizeException e) {
			System.err.println("Illegal Block Size");
//		} catch (BadPaddingException e) {
//			System.err.println("Bad Padding");
		}
		//IllegalBlockSizeException, BadPaddingException
		
		return cipherBytes;
	} 
	
	public static byte[] asymDecipher(byte[] cipheredBytes, PrivateKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
		//NoSuchAlgorithmException, NoSuchPaddingException
		
		cipher.init(Cipher.DECRYPT_MODE, key);
		//InvalidKeyException
		
		byte[] decipheredBytes = cipher.doFinal(cipheredBytes);
		//IllegalBlockSizeException, BadPaddingException
		
		return decipheredBytes;
	}
	
	public static byte[] makeSignature(byte[] plainBytes , String alias, String keyStore )
			throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException {
		
		PrivateKey privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(keyStore,
				PASSWORD.toCharArray(), alias, PASSWORD.toCharArray());
		
		return CertUtil.makeDigitalSignature(SIGNATURE_ALGO, privateKey, plainBytes);
	}
	
	public static boolean verifySignature(byte[] plainBytes, String certName, byte[] digitalSignature) 
			throws CertificateException, IOException, CAClientException, SecurityException {
		
		PublicKey publicKey = getPublicKeyFromCA(certName);
		
		return CertUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes, digitalSignature);
	}
	
	public static PublicKey getPublicKeyFromCA(String certName) 
			throws SecurityException, CAClientException, CertificateException, IOException {
		
		// get Certificate from CA
		CAClient ca = new CAClient(CA_URL);
		Certificate cert = CertUtil.getX509CertificateFromPEMString(ca.getCertificate(certName));
		
		if(!CertUtil.verifySignedCertificate(cert, CertUtil.getX509CertificateFromResource("ca.cer"))){
			System.err.println("Certificate is not authentic!");
			throw new SecurityException("Certificate is not authentic!");
		}
		
		return cert.getPublicKey();
	}

}
