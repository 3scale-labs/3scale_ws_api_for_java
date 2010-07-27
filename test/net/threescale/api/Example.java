package net.threescale.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Example {

    /**
     * This example uses the Sync operations
     */
	public void sync_example() {
		Api api = ApiFactory.createApi("http://server.3scale.net", "My Provider Key");
		
		
		try {
			ApiStartResponse transaction = api.start("Users Contract Key from your http request");

			try {
				// Perform your transaction
				
				Map<String, String> metrics = new HashMap<String, String>();
				
				// Set metrics used by the transaction. You define these in your contract.
				metrics.put("requests", "1");
				metrics.put("bytestransfered", "51983");
				
				try {
					api.confirm(transaction.getTransactionId(), metrics);
				} catch (ApiException ex){
					// Log application error
					ex.printStackTrace();
				}
			}
			catch (Exception ex) {
				// Handle exception from application
				try {
					api.cancel(transaction.getTransactionId());
				} catch (ApiException ex2){
					// Log application error
					ex2.printStackTrace();
				}
			}
			
		} catch (ApiException e) {
			if (e.getResponseCode() == 403) {
				// Analyse error and return error message to user
			}
		}
	}

    public void async_example() {
        Api api = ApiFactory.createApi("http://server.3scale.net", "My Provider Key");

        try {
            ApiAuthorizeResponse authorize_response = api.authorize("Users contract key");

            // Do your usage / plan checking here
            if (authorize_response.getPlan().equals("Pro")) {
                // Perform transaction and record hits.

                ApiBatchMetric[] batchMetrics = new ApiBatchMetric[2];

                Map<String, String> metric0 = new HashMap<String, String>();
                metric0.put("hits", "4");
                metric0.put("KbTransfer", "541");
                batchMetrics[0] = new ApiBatchMetric("User_key", metric0, null );

                Map<String, String> metric1 = new HashMap<String, String>();
                metric1.put("hits", "40");
                metric1.put("KbTransfer", "1345");
                batchMetrics[1] = new ApiBatchMetric("A different User_key", metric0, new Date() );


                try {
                    api.batch(batchMetrics) ;
                } catch (ApiException ex) {
                    // Iterate over ex.getErrors()
                    // and handle / report errors
                }

            } else {
                // Throw some for of exception or inform user of failure.
                // throw new Exception("Not authorised for this plan");
            }
        } catch (ApiException e) {
            //  Handle and report the fact that we cannot connect to the server
            e.printStackTrace();
        }
    }
}
