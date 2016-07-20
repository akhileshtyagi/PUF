package intent_record;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

//import edu.isu.reu.intent_collection_service.R;

/**
 * Created by element on 6/25/16.
 */
public class IntentCollectionService extends Service {
    public final static String TAG = "ICS";

    //private final IBinder binder = new IntentCollectionBinder(this);
//    private final intent_collection.IIntentCollectionService.Stub binder = new IIntentCollectionService.Stub() {
//        public IntentData get_intent_data() throws RemoteException {
//            return intent_data;
//        }
//
//        public void add_intent(Intent intent, int sender, int receiver) throws RemoteException {
//            //TODO add intent to intent_data
//        }
//    };

    /**
     * provide an enum with a list of possible commands
     */
    public enum Command {
        STOP("stop"),
        START("start");

        private String string_value;
        private Uri uri_value;

        Command(String string) {
            this.string_value = string;
            this.uri_value = Uri.parse(string);
        }

        public String get_string() {
            return this.string_value;
        }

        public Uri get_uri() {
            return this.uri_value;
        }
    }

    /**
     * commands given to messenger to interact with service
     */
    static final int MSG_SAY_HELLO = 1;
    static final int MSG_RESPOND_INTENT_LIST = 2;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        /**
         * In this method,
         * we decide what to do with received messages.
         * Some of the received messages will contain
         * IntentData in the form:
         * Intent, sender, receiver
         */
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    // decode message and add to list
                    intent_list.add(IntentRecord.decode_message(msg));

                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_RESPOND_INTENT_LIST:
                    // reply with the intent list to the caller
                    Message message = new Message();
                    message.obj = intent_list;

                    // send the message to the sender
                    try{
                        //TODO this seems not to work
                        //TODO this is becuase replyTo is null.
                        //TODO figure out why replyTo is null
                        // 1 is it being set?
                        // 2 is it null even when set?, why wouldl this be?
                        //message.replyTo.send(message);

                        Messenger reply_messenger = new Messenger(message.replyTo.getBinder());
                        reply_messenger.send(message);
                    }catch(Exception e){ e.printStackTrace(); }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new IncomingHandler());

    /**
     * stores a list of the intents which ave been handled
     */
    private ArrayList<IntentData> intent_list;

    /**
     * create an constructor
     */
    public IntentCollectionService() {
        super();

        this.intent_list = new ArrayList<>();

        Log.d("ICS", "created service");
    }

    /**
     * preform startup actions
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = create_notification();

        // start the service in the foreground
        // this will prevent it from being killed in most situations
        startForeground(5000, notification);

        Log.d("ICS", "started successfully");

        return START_STICKY;
    }

    /**
     * preform closedown actions
     */
    @Override
    public void onDestroy() {
        Log.d("ICS", "service stopped!");
    }


    /**
     * provide binder
     * this will allow bound applications to submit intents
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("bound", "bound");

        return messenger.getBinder();
    }

    /**
     * create a notification to be shown while this service is active
     */
    private Notification create_notification() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Intent Collector")
                .setContentText("Intent collector is running.")
                //.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .build();

        return notification;
    }

    /**
     * handle an intent
     */
    protected void handle_intent(Intent workIntent) {
        //TODO

        // add the incoming intent to the list of handled intents
        //this.intent_list.add(workIntent);

        Log.d("ICS", "intents received number: " + this.intent_list.size());
    }
}
