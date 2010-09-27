package net.threescale.api.v2;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 16:54:44
 */
public class ApiException extends Exception {
    private final String errorCode;
    private final String errorMessage;

    public ApiException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
