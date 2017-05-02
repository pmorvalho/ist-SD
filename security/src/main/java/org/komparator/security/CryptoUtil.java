package org.komparator.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptoUtil {
	
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
	
//	A68_Supplier1 - a68_supplier1
//	A68_Mediator - a68_Mediator
	
	public static byte[] makeSignature(byte[] plainBytes , String alias, String keyStore )
			throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException {
		
		PrivateKey privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(keyStore,
				PASSWORD.toCharArray(), alias, PASSWORD.toCharArray());
		byte[] digitalSignature = CertUtil.makeDigitalSignature(SIGNATURE_ALGO, privateKey, plainBytes);
		
		return digitalSignature;
	}
	
	public static boolean verifySignature(byte[] plainBytes, String certName, byte[] digitalSignature) throws CertificateException, IOException {
		PublicKey publicKey = CertUtil.getX509CertificateFromResource("asd").getPublicKey();
		boolean result = CertUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes, digitalSignature);
		return true;
	}

}
