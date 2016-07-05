package keyboardAuthenticationInterface;

import android.os.Binder;

/**
 * Created by element on 7/4/16.
 */
public class KeyboardAuthenticationBinder extends Binder {
    /**
     * reference to service that implements interface
     */
    private KeyboardAuthenticationInterface service;

    /**
     * provides constructor
     */
    public KeyboardAuthenticationBinder(KeyboardAuthenticationInterface service){
        super();

        this.service = service;
    }

    public KeyboardAuthenticationInterface get_service(){
        return this.service;
    }
}
