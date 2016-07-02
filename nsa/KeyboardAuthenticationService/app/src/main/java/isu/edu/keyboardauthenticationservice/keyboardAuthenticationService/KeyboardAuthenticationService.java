package isu.edu.keyboardauthenticationservice.keyboardAuthenticationService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import isu.edu.keyboardauthenticationservice.keyboardAuthenticationInterface.Data;
import isu.edu.keyboardauthenticationservice.keyboardAuthenticationInterface.KeyboardAuthenticationInterface;
import isu.edu.keyboardauthenticationservice.keyboardAuthenticationInterface.Result;

/**
 * Created by tim on 7/1/16.
 *
 * This service acts as an intermediary
 * between the keyboard application and
 * other applications which use the authenticated value.
 *
 * This
 */
public class KeyboardAuthenticationService extends Service implements KeyboardAuthenticationInterface {
    private boolean new_result_available;
    private Result result;

    /**
     * create a constructor
     */
    public KeyboardAuthenticationService(){
        super();

        this.new_result_available = false;

        Log.d("KAS", "service created");
    }

    /**
     * preform startup actions
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Notification notification = create_notification();

        // start the service in the foreground
        // this will prevent it from being killed in most situations
        startForeground(5000, notification);

        Log.d("KAS", "service startup finished");

        return START_STICKY;
    }

    @Override
    public boolean isNewResultAvailable() {
        return this.new_result_available;
    }

    /**
     * This is the result of the keyboard authentication
     *
     * This method should be called if
     * the desire is to acquire information from the service
     */
    @Override
    public Result receiveResult() {
        return this.result;
    }

    /**
     * This method is used to send data to the service
     */
    @Override
    public void sendData(Data data) {
        this.result = new Result();

        this.result.value = data.compare_result;
        this.result.confidence = data.confidence;

        this.new_result_available = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //TODO
        return null;
    }

    /**
     * create a notification to be shown while this service is active
     */
    private Notification create_notification(){
        Notification notification = new Notification.Builder(this)
                .setContentTitle("KAS")
                .setContentText("KeyboardAuthenticationService is running.")
                .build();

        return notification;
    }
}
