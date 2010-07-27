package net.threescale.api;


public class ApiError {
    private final String id;
    private final int index;
    private final String message;

    public ApiError(String id, int index, String message) {
        this.id = id;
        this.index = index;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public String getMessage() {
        return message;
    }
}
