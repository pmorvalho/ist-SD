package org.komparator.security.handler;

import java.util.Set;

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

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AttackHandler implements SOAPHandler<SOAPMessageContext>{

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
					
					if (!opn.getLocalPart().equals("getProduct")) {
						// Handler only cares about getProduct operation
						return true;
					}
					
					NodeList children;
					try{
						children = ((Node) sb).getFirstChild().getFirstChild().getChildNodes();
					} catch(NullPointerException e){
						return true;
					}

					for (int i = 0; i < children.getLength(); i++) {
						Node attribute = children.item(i);
						if (attribute.getNodeName().equals("id")) {

							System.out.println("Changing price in outbound SOAP message...");					

							String id = attribute.getTextContent();
							
							if(!id.equals("Trojan")){
								return true;
							}
						}
					}

					for (int i = 0; i < children.getLength(); i++) {
						Node attribute = children.item(i);
						if (attribute.getNodeName().equals("price")) {

							System.out.println("Changing price in outbound SOAP message...");					

							attribute.setTextContent("1");
							
							msg.saveChanges();

							return true;
						}
					}

				}
				else{
					
					return true;
					
				}
			} catch (DOMException e) {
				throw new RuntimeException("DOM Exception caught in CryptoHandler: " + e);
			} catch (SOAPException e){
				throw new RuntimeException("SOAP Exception caught in CryptoHandler: " + e);
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
