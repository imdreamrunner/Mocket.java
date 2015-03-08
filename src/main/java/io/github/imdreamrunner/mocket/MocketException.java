package io.github.imdreamrunner.mocket;

public class MocketException extends Exception {
    private Exception originException;

    public MocketException(Exception originException) {
        this.originException = originException;
    }

    public Exception getOriginException() {
        return originException;
    }

    @Override
    public String toString() {
        if (originException != null) {
            return "MocketException: " + originException.toString();
        }
        return "UnknownMocketException";
    }
}
