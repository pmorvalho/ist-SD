package org.komparator.security;

import static org.junit.Assert.assertEquals;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CryptoUtilTest {
	
	
    // static members
	/** Plain text to digest. */
	private final String plainText = "Trabalha-se melhor sem o Silveira!";
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
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	
	private static CryptoUtil cryptoUtil = new CryptoUtil();
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

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests, estes throws sao inapropriados TODO
    @Test
    public void sucess() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	byte[] ciphered = cryptoUtil.asymCipher(plainBytes, publicKey);
    	byte[] deciphered = cryptoUtil.asymDecipher(ciphered, privateKey);
     
    	assertEquals(new String(plainBytes),new String(deciphered));
    }
    
    @Test(expected = BadPaddingException.class)
    public void wrongKey() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	byte[] ciphered = cryptoUtil.asymCipher(plainBytes, publicKey);
    	PrivateKey wrongKey = keyGen.generateKeyPair().getPrivate();
    	byte[] deciphered = cryptoUtil.asymDecipher(ciphered,wrongKey);
    }
    
    @Test(expected = BadPaddingException.class)
    public void modifiedData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    	byte[] ciphered = cryptoUtil.asymCipher(plainBytes, publicKey);
    	ciphered[1]= (byte) 65;
    	byte[] deciphered = cryptoUtil.asymDecipher(ciphered, privateKey);
    }
    
    

}
