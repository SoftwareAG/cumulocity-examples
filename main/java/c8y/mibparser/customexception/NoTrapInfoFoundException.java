package c8y.mibparser.customexception;

public class NoTrapInfoFoundException extends IllegalMibUploadException {
    public NoTrapInfoFoundException(String message) {
        super(message);
    }
}
