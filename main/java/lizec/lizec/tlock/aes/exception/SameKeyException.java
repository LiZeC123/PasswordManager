package lizec.lizec.tlock.aes.exception;

public class SameKeyException extends Exception {
    public SameKeyException(){
        super();
    }

    public SameKeyException(String s){
        super(s);
    }
}
