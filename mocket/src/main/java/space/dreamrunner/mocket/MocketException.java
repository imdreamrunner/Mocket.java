package space.dreamrunner.mocket;

public class MocketException extends Exception {
    private Exception originException;
    private String message;

    public MocketException(Exception originException) {
        this.originException = originException;
    }

    public MocketException(String message) {
        this.message = message;
    }

    public Exception getOriginException() {
        return originException;
    }

    @Override
    public String toString() {
        if (originException != null) {
            return "MocketException: " + originException.toString();
        }
        if (message != null) {
            return "MocketException: " + message;
        }
        return "UnknownMocketException";
    }
}
