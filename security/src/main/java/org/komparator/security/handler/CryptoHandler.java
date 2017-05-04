package org.komparator.security.handler;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

public class CryptoHandler implements SOAPHandler<SOAPMessageContext>{
	private static final String ALIAS = "a68_mediator";

	private static final String PASSWORD = "peBiX6UK";

	private static final String KEY_STORE = "A68_Mediator.jks";

	public static final String CONTEXT_PROPERTY = "my.property";
	
	private static final String CA_URL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca";

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
						
						System.out.println("Ciphering credit card in outbound SOAP message...");
						
						String secretArgument = argument.getTextContent();
						
						// get public key from CA
						CAClient ca = new CAClient(CA_URL);
						Certificate cert = CertUtil.getX509CertificateFromPEMString(ca.getCertificate("A68_Mediator"));
						if(!CertUtil.verifySignedCertificate(cert, CertUtil.getX509CertificateFromResource("ca.cer"))){
							System.err.println("Certificate is not authentic!");
							return true;
						}
						PublicKey publicKey = cert.getPublicKey();
						
						// cipher message with public key
						byte[] cipheredArgument = CryptoUtil.asymCipher(secretArgument.getBytes(), publicKey);

						String encodedSecretArgument = printBase64Binary(cipheredArgument);
						argument.setTextContent(encodedSecretArgument);
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
						
						String secretArgument = argument.getTextContent();
						
						// get private key from KeyStore
						PrivateKey privateKey = CertUtil.getPrivateKeyFromKeyStoreResource(KEY_STORE,
								PASSWORD.toCharArray(), ALIAS, PASSWORD.toCharArray());
						
						// cipher message with public key
						byte[] decipheredArgument = CryptoUtil.asymDecipher(parseBase64Binary(secretArgument), privateKey);

						argument.setTextContent(new String(decipheredArgument));
						msg.saveChanges();
						
						return true;
					}
				}

			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
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
}
