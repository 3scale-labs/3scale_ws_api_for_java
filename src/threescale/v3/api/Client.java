package threescale.v3.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: geoffd
 * Date: 15/02/2013
 */
public interface Client {
    public AuthorizeResponse authrep(HashMap<String, String> options);

    public void report(ArrayList<HashMap<String, String>> transactions);

    public Response authorize(HashMap<String, String> options);
}
