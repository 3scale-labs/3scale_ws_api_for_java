package net.threescale.api.v2;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:51:11
 */
public class ApiResponse {
    
    public boolean getAuthorized() {
        return false;
    }

    public ArrayList<ApiUsageReport> getUsageReports() {
        return null;
    }

    public String getReason() {
        return "";
    }

    public String getPlan() {
        return ""; 
    }
}
