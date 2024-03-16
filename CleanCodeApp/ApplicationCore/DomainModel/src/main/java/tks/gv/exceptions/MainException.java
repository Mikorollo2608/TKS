package tks.gv.exceptions;


///TODO hierarchia wyjatkow w serwisach adapterach, repozytoriach :***
public class MainException extends RuntimeException {
    public MainException(String message) {
        super(message);
    }
}
