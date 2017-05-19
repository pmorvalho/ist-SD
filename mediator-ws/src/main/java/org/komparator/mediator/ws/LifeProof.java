package org.komparator.mediator.ws;

import java.util.Date;
import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask{

	MediatorEndpointManager mediator;
	
	MediatorClient medClient;
	
	public LifeProof(MediatorEndpointManager mediator){
		this.mediator = mediator;
		if(mediator.isPrimary()){
			try {
				medClient = new MediatorClient(mediator.makeSecondaryMedUrl(2));
			} catch (MediatorClientException e) {
				System.err.println("Error creating mediator client");
				System.err.println(e);
			}
		}
	}
	
	@Override
	public void run() {
		if(mediator.isPrimary()){
			if(medClient != null){
				medClient.imAlive();
				System.out.println("imAlive sent!");
			}
		}
		else{
			if(mediator.getLatestLifeProof() != null && (new Date()).getTime()-mediator.getLatestLifeProof().getTime() > 7*1000){
				try {
					mediator.changeToPrimary();
				} catch (Exception e) {
					System.out.println("Failed to register Secondary Server in UDDI!");
				}
			}
		}
	}

}
