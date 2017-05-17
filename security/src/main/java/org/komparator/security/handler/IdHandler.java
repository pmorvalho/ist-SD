package org.komparator.security.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.KomparatorSecurityManager;

public class IdHandler implements SOAPHandler<SOAPMessageContext>{

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
		System.out.println("IdHandler: Handling message.");

		return processMessage(smc);
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		System.out.println("IdHandler: Handling fault message.");
		
		return processMessage(smc);
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
	
	private boolean processMessage(SOAPMessageContext smc) {
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing client and operation ID header in outbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();
				
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				if (!opn.getLocalPart().equals("buyCart") && !opn.getLocalPart().equals("addToCart")) {
					// Handler only ciphers the credit card
					// which is only included in the buyCart operation
					return true;
				}
				
				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("opId", "l", "http://lmao");
				SOAPHeaderElement element = sh.addHeaderElement(name);

				// add header element value
				
				int id = KomparatorSecurityManager.getIdCounter();
				String strId = Integer.toString(id);
				element.addTextNode(strId);
				
				System.out.println("Added opId #" + strId + "to SOAP Message!");
				
				msg.saveChanges();
				
				// add header element (name, namespace prefix, namespace)
				name = se.createName("clientId", "l", "http://lmao");
				element = sh.addHeaderElement(name);

				// add header element value
				
				strId = "777"; //TODO random number???
				element.addTextNode(strId);
				
				System.out.println("Added clientId #" + strId + "to SOAP Message!");

			} else {
				System.out.println("Inbound SOAP message: Incrementing operation ID");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					sh = se.addHeader();
				}

				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				if (!opn.getLocalPart().equals("buyCart") && !opn.getLocalPart().equals("addToCart")) {
					return true;
				}
				
				KomparatorSecurityManager.incIdCounter();
				System.out.println("Incremented Id!");

			}
		} catch (SOAPException e) {
			throw new RuntimeException("SOAP Exception caught in DateHandler: " + e);
		}
		

		return true;
	}
	
	
}
