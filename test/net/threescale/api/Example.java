package net.threescale.api;

import java.util.HashMap;
import java.util.Map;

public class Example {
	
	public void example() {
		Api api = ApiFactory.createApi("http://beta.3scale.net", "My Provider Key");
		
		
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
}
