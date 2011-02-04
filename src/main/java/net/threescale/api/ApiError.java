package net.threescale.api;

/**
 * DTO for error responses
 */
public class ApiError {

    private final String id;
    private final int index;
    private final String message;

    public ApiError(String id, int index, String message) {
        this.id = id;
        this.index = index;
        this.message = message;
    }

    /**
     * Get the id key for this error.
     * @return Error Id code
     */
    public String getId() {
        return id;
    }

    /**
     * Get the index value when used to report errors in a batch call. Returns 0 if error is not in a batch.
     * @return  Index value
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the actual text of the message.
     * @return  Message text
     */
    public String getMessage() {
        return message;
    }
}
