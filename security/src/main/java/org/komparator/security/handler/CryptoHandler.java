package org.komparator.security.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.komparator.security.KomparatorSecurityException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public class CryptoHandler implements SOAPHandler<SOAPMessageContext>{
	private static final String ALIAS = "a68_mediator";

	private static final String PASSWORD = "peBiX6UK";

	private static final String KEY_STORE = "A68_Mediator.jks";

	public static final String CONTEXT_PROPERTY = "my.property";
	
	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("CryptoHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);


			try {
				if (outboundElement.booleanValue()) {
					System.out.println("Looking for credit card number in outbound SOAP message...");

					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();
					SOAPBody sb = se.getBody();

					// add header
					SOAPHeader sh = se.getHeader();
					if (sh == null)
						sh = se.addHeader();
					
					QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
					
					if (!opn.getLocalPart().equals("buyCart")) {
						// Handler only ciphers the credit card
						// which is only included in the buyCart operation
						return true;
					}
					
					NodeList children = ((Node) sb).getFirstChild().getChildNodes();
					
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = children.item(i);
						if (argument.getNodeName().equals("creditCardNr")) {
							
							System.out.println("Ciphering credit card in outbound SOAP message...");
							
							cipherCreditCardNumber(argument);
							
							msg.saveChanges();
							
							return true;
						}
					}

				}
				else {
					System.out.println("Looking for credit card number in inbound SOAP message...");
					
					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();
					SOAPBody sb = se.getBody();

					// add header
					SOAPHeader sh = se.getHeader();
					if (sh == null)
						sh = se.addHeader();
					
					//QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
					QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
					
					if (!opn.getLocalPart().equals("buyCart")) {
						// Handler only ciphers the credit card
						// which is only included in the buyCart operation
						return true;
					}
					
					NodeList children = ((Node) sb).getFirstChild().getChildNodes();
					
					for (int i = 0; i < children.getLength(); i++) {
						Node argument = children.item(i);
						if (argument.getNodeName().equals("creditCardNr")) {
							
							System.out.println("Deciphering credit card in outbound SOAP message...");
							
							decipherCreditCardNumber(argument);
							
							msg.saveChanges();
							
							return true;
						}
					}

				}
				
			} catch(UnrecoverableKeyException e){
				throw new RuntimeException("UnrecoverableKey Exception caught in CryptoHandler: " + e);
			} catch(KeyStoreException e){
				throw new RuntimeException("KeyStore Exception caught in CryptoHandler: " + e);
			} catch(CertificateException e){
				throw new RuntimeException("Certificate Exception caught in CryptoHandler: " + e);
			} catch(DOMException e){
				throw new RuntimeException("DOM Exception caught in CryptoHandler: " + e);
			} catch(SOAPException e){
				throw new RuntimeException("SOAP Exception caught in CryptoHandler: " + e);
			} catch(IOException e){
		 		throw new RuntimeException("IO Exception caught in CryptoHandler: " + e);
			} catch(CAClientException e){
				throw new RuntimeException("CAClient Exception caught in CryptoHandler: " + e);
			} catch(KomparatorSecurityException e){
				throw new RuntimeException("Komparator Exception caught in CryptoHandler: " + e);
			} catch(NoSuchAlgorithmException e){
				throw new RuntimeException("NoSuchAlgorithm Exception caught in CryptoHandler: " + e);
			} catch(NoSuchPaddingException e){
				throw new RuntimeException("NoSuchPadding Exception caught in CryptoHandler: " + e);
			} catch(IllegalBlockSizeException e){
				throw new RuntimeException("IllegalBlockSize Exception caught in CryptoHandler: " + e);
			} catch(BadPaddingException e){
				throw new RuntimeException("BadPadding Exception caught in CryptoHandler: " + e);
			} catch(InvalidKeyException e){
				throw new RuntimeException("InvalidKey Exception caught in CryptoHandler: " + e);
			}
			
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("Ignoring fault message...");
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}
	
	
	// aux methods
	
	private void cipherCreditCardNumber(Node argument) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, CertificateException, KomparatorSecurityException, CAClientException, IOException{
		
		String secretArgument = argument.getTextContent();

		// get public key from CA
		PublicKey publicKey = CryptoUtil.getPublicKeyFromCA("A68_Mediator");


		// cipher message with public key
		byte[] cipheredArgument = CryptoUtil.asymCipher(secretArgument.getBytes(), publicKey);

		String encodedSecretArgument = printBase64Binary(cipheredArgument);
		argument.setTextContent(encodedSecretArgument);
	}
	
	private void decipherCreditCardNumber(Node argument) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		
		String secretArgument = argument.getTextContent();

		// get private key from KeyStore
		PrivateKey privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEY_STORE,
				PASSWORD.toCharArray(), ALIAS, PASSWORD.toCharArray());

		// cipher message with public key
		byte[] decipheredArgument = CryptoUtil.asymDecipher(parseBase64Binary(secretArgument), privateKey);

		argument.setTextContent(new String(decipheredArgument));
	}
	
	
}
