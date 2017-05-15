package org.komparator.mediator.ws;

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
			medClient.imAlive();
			System.out.println("imAlive sent!");
		}
	}

}
