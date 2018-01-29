package me.duncte123.fandomApi.models;

public class FandomException implements FandomResult {

    private final String type;
    private final String message;
    private final int code;
    private final String details;

    private final String trace_id;

    public FandomException(String type, String message, int code, String details, String trace_id) {
        this.type = type;
        this.message = message;
        this.code = code;
        this.details = details;
        this.trace_id = trace_id;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public String getMessage() {
        return message;
    }

    public String getTrace_id() {
        return trace_id;
    }

    @Override
    public String toString() {
        return getType() + ": " + getMessage();
    }
}
