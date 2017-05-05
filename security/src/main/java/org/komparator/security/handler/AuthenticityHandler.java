package org.komparator.security.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;
import org.komparator.security.KomparatorSecurityException;
import org.komparator.security.KomparatorSecurityManager;
import org.w3c.dom.DOMException;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class AuthenticityHandler implements SOAPHandler<SOAPMessageContext>{

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
		System.out.println("\nAuthenticityHandler: Handling message.\n");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);


			try {
				if (outboundElement.booleanValue()) {
					System.out.println("Signing outbound SOAP message...");

					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();

					// add header
					SOAPHeader sh = se.getHeader();
					if (sh == null){
						throw new RuntimeException();
					}
					
					String wsName = KomparatorSecurityManager.getWsName();
					
					// add header element wsName (name, namespace prefix, namespace)
					Name name = se.createName("wsName", "l", "http://lmao");
					SOAPHeaderElement element = sh.addHeaderElement(name);
									
					// add header element wsName
					element.addTextNode(wsName);
					msg.saveChanges();
					
					byte[] byteMsg = soapMessageToBytes(msg);

					byte[] signature = CryptoUtil.makeSignature(byteMsg, wsName.toLowerCase(), wsName+".jks");
									
					// add header element signature (name, namespace prefix, namespace)
					name = se.createName("signature", "l", "http://lmao");
					element = sh.addHeaderElement(name);

					// add header element value
					element.addTextNode(printBase64Binary(signature));
					

				}
				else {
					System.out.println("Verifying signature in inbound SOAP message...");
					
					// get SOAP envelope
					SOAPMessage msg = smc.getMessage();
					SOAPPart sp = msg.getSOAPPart();
					SOAPEnvelope se = sp.getEnvelope();

					// add header
					SOAPHeader sh = se.getHeader();
					if (sh == null){
						throw new RuntimeException();
					}
					
					
					SOAPElement signature = getSoapElement(se, sh, "signature");
					
					sh.removeChild(signature);
					msg.saveChanges();
					
					SOAPElement wsNameElement = getSoapElement(se, sh, "wsName");
					
					String wsName = wsNameElement.getValue();
									
					byte[] byteMsg = soapMessageToBytes(msg);
					
					byte[] signatureBytes = parseBase64Binary(signature.getValue());
									
					if(!CryptoUtil.verifySignature(byteMsg, wsName, signatureBytes)){
						System.err.println("Signature is not correct!");
						throw new RuntimeException();
					}
					

				}
				
			} catch(UnrecoverableKeyException e){
				throw new RuntimeException("UnrecoverableKey Exception caught in AuthenticityHandler: " + e);
			} catch(KeyStoreException e){
				throw new RuntimeException("KeyStore Exception caught in AuthenticityHandler: " + e);
			} catch(CertificateException e){
				throw new RuntimeException("Certificate Exception caught in AuthenticityHandler: " + e);
			} catch(DOMException e){
				throw new RuntimeException("DOM Exception caught in AuthenticityHandler: " + e);
			} catch(SOAPException e){
				throw new RuntimeException("SOAP Exception caught in AuthenticityHandler: " + e);
			} catch(IOException e){
		 		throw new RuntimeException("IO Exception caught in AuthenticityHandler: " + e);
			} catch(CAClientException e){
				throw new RuntimeException("CAClient Exception caught in AuthenticityHandler: " + e);
			} catch(KomparatorSecurityException e){
				throw new RuntimeException("KomparatorSecurity Exception caught in AuthenticityHandler: " + e);
			} catch (RuntimeException e){
				throw e;
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
	
	private byte[] soapMessageToBytes(SOAPMessage msg) throws SOAPException, IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		msg.writeTo(out);
		return out.toByteArray();
	}
	
	private SOAPElement getSoapElement(SOAPEnvelope se, SOAPHeader sh, String element) throws SOAPException, SecurityException{
		Name name = se.createName(element, "l", "http://lmao");
		Iterator it = sh.getChildElements(name);
		// check header element
		if (!it.hasNext()) {
			System.out.println("Header element "+ element +" not found.");
			throw new SecurityException("Header element "+ element + " not found.");
		}
		return (SOAPElement) it.next();
	}
}
