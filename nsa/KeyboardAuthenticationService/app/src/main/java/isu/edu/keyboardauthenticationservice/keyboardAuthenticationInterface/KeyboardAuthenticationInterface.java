package isu.edu.keyboardauthenticationservice.keyboardAuthenticationInterface;

/**
 * this interface defines how others will interact with our service
 * in other words,
 * our service will implement this interface.
 * when applications bind to this service,
 * they will be able to use it's public methods
 * in order to send and receive information from the service.
 */
public interface KeyboardAuthenticationInterface {
    /**
     * implements the pull feature,
     * returns true if compare receiveResult()
     * has not been called after a new result has been generated
     */
    boolean isNewResultAvailable();

    /**
     * make available the compare value and the confidence of that value
     *
     * this is the output from the service
     */
    Result receiveResult();

    /**
     * defines how data is received
     *
     * this is the input to the service
     */
    void sendData(Data data);
}