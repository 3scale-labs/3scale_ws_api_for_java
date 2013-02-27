package threescale.v3.api.example;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ServerError;
import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

/**
 * Simple Example of using the API
 */
public class Example {

    public void performAuthRep() {

        ServiceApi serviceApi = new ServiceApiDriver("my_provider_key");    // Create the API object

        ParameterMap params = new ParameterMap();                           // Create top level ParameterMap
        params.add("app_id", "appid");                                      // Set the Users App Id

        ParameterMap usage = new ParameterMap();                            // Create 1st Level PM for usage
        usage.add("hits", "3");                                             // Add number of hits metric
        params.add("usage", usage);                                         // Add 1st level to top level as "usage"

        try {
            final AuthorizeResponse response = serviceApi.authrep(params);  // Perform the AuthRep and get the response

            if (response.success()) {                                       // Check if the AuthRep succeeded
                // Perform your calls there
            } else {
                // Handle failure here
            }
        } catch (ServerError serverError) {
            // Thrown if there is a communications error with the server.
            serverError.printStackTrace();
        }
    }
}
