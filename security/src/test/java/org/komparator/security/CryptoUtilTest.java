package org.komparator.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class CryptoUtilTest {
	
	
    // static members
	/** Plain text to digest. */
	private final String plainText = "Build Success!";
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();

	/** Asymmetric cryptography algorithm. */
	private static final String ASYM_ALGO = "RSA";
	/** Asymmetric cryptography key size. */
	private static final int ASYM_KEY_SIZE = 2048;
	/**
	 * Asymmetric cipher: combination of algorithm, block processing, and
	 * padding.
	 */
	
	private static final String CERT = "example.cer";
	private static final String KEYSTORE = "example.jks";
	private static final String KEYSTORE_PASSWORD = "1nsecure";
	private static final String KEY_ALIAS = "example";
	private static final String KEY_PASSWORD = "ins3cur3";
	
	private static KeyPairGenerator keyGen;
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	
	
    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws NoSuchAlgorithmException {
        keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
        keyGen.initialize(ASYM_KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

//    @AfterClass
//    public static void oneTimeTearDown() {
//        // runs once after all tests in the suite
//    }
//
//    // members
//
//    // initialization and clean-up for each test
//    @Before
//    public void setUp() {
//
//    }
//
//    @After
//    public void tearDown() {
//        // runs after each test
//    }

    // tests, estes throws sao inapropriados TODO
    @Test
    public void sucess() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	byte[] ciphered = CryptoUtil.asymCipher(plainBytes, publicKey);
    	byte[] deciphered = CryptoUtil.asymDecipher(ciphered, privateKey);
     
    	assertEquals(new String(plainBytes),new String(deciphered));
    }
    
    @Test(expected = BadPaddingException.class)
    public void wrongKey() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	byte[] ciphered = CryptoUtil.asymCipher(plainBytes, publicKey);
    	PrivateKey wrongKey = keyGen.generateKeyPair().getPrivate();
    	CryptoUtil.asymDecipher(ciphered,wrongKey);
    }
    
    @Test(expected = BadPaddingException.class)
    public void modifiedData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	byte[] ciphered = CryptoUtil.asymCipher(plainBytes, publicKey);
    	ciphered[1]= (byte) 65;
    	CryptoUtil.asymDecipher(ciphered, privateKey);
    }
    
    @Test
    public void sucessCertKeyStore()
    		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
    		BadPaddingException, CertificateException, UnrecoverableKeyException, KeyStoreException, IOException {
    	
    	PublicKey pubKey = CertUtil.getX509CertificateFromResource(CERT).getPublicKey();
    	PrivateKey prvKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE,KEYSTORE_PASSWORD.toCharArray(), KEY_ALIAS, KEY_PASSWORD.toCharArray());
    	
    	System.out.println("Text: " + plainText);
		System.out.print("BytesHex: ");
		System.out.println(printHexBinary(plainBytes));
		System.out.print("Bytes64: ");
		System.out.println(printBase64Binary(plainBytes));
		
    	System.out.println("\nCiphering...");
    	byte[] ciphered = CryptoUtil.asymCipher(plainBytes, pubKey);
    	
    	System.out.print("Ciphered BytesHex: ");
		System.out.println(printHexBinary(ciphered));
		System.out.print("Ciphered Bytes64: ");
		System.out.println(printBase64Binary(ciphered));
    	
    	System.out.println("\nDeciphering...");
    	byte[] deciphered = CryptoUtil.asymDecipher(ciphered, prvKey);
    	
    	System.out.print("Deciphered BytesHex: ");
		System.out.println(printHexBinary(deciphered));
		System.out.print("Deciphered Bytes64: ");
		System.out.println(printBase64Binary(deciphered));
		System.out.println("Deciphered text: " + new String(deciphered));
    	
    	assertEquals(new String(plainBytes),new String(deciphered));
    }
    
    @Test
    public void successSignature() 
    		throws UnrecoverableKeyException, KeyStoreException, CertificateException, IOException, CAClientException, SecurityException {
    	
    	byte[] signature = CryptoUtil.makeSignature(plainBytes, "a68_mediator", "A68_Mediator.jks");
    	
    	boolean res = CryptoUtil.verifySignature(plainBytes, "A68_Mediator", signature);
    	
    	assertTrue(res);
    }

}
