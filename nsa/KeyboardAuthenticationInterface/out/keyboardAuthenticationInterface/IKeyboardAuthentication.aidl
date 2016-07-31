package keyboardAuthenticationInterface;

import android.view.MotionEvent;

interface IKeyboardAuthentication {
    boolean isNewResultAvailable();
    double receiveResult();
    void sendData(in MotionEvent data);
}