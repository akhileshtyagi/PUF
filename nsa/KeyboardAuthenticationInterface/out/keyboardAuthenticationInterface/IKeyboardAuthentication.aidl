package keyboardAuthenticationInterface;

interface IKeyboardAuthentication {
    boolean isNewResultAvailable();
    double receiveResult();
    void sendData(in double result);
}