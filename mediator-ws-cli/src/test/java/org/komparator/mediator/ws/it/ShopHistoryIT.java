package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Test suite
 */
public class ShopHistoryIT extends BaseIT {

    // tests
    // assertEquals(expected, actual);

    // public String ping(String x)

    @Test
    public void noPurchases() {
        assertTrue(mediatorClient.shopHistory().isEmpty());
    }
    
    @Test
    public void onePurchase() {
    	
        assertTrue(mediatorClient.shopHistory().isEmpty());
    }

}
