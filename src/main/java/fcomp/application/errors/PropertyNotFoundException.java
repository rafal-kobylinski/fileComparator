package fcomp.application.errors;

public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(String error)
    {
        super(error, null, true, false);
    }
}
