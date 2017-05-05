package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test suite
 */
public class EVAL_PingIT extends EVAL_BaseIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingEmptyTest() {
		assertNotNull(mediatorClient.ping("test"));
	}

}
