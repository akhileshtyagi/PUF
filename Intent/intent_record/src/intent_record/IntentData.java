package intent_record;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by element on 7/10/16.
 *
 * Contains all information about all intent.
 *      1. intents themselves
 *      2. sender
 *      3. receiver
 *
 * Represents a single Intent's data
 */
public class IntentData {
    /** the intent which was sent from sender to receiver */
    private Intent intent;

    /** the package name of the sender */
    private String sender;

    /** the package name of the receiver */
    private String receiver;

    public IntentData(Intent intent, String sender, String receiver){
        this.intent = intent;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String get_sender(){
        return this.sender;
    }

    public String get_receiver(){
        return this.receiver;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("intent: ");
        sb.append(intent.toString());
        sb.append(" ");

        sb.append("sender: ");
        sb.append(sender.toString());
        sb.append(" ");

        sb.append("receiver: ");
        sb.append(receiver.toString());
        sb.append(" ");

        return sb.toString();
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel in, int flags) {
//        // write to parcel
//        in.writeParcelable(intent, 0);
//        in.writeParcelable(sender, 0);
//        in.writeParcelable(receiver, 0);
//    }

//    public static final Parcelable.Creator CREATORString
//            = new Parcelable.Creator() {
//        public IntentData createFromParcel(Parcel in) {
//            return new IntentData(in);
//        }
//
//        public IntentData[] newArray(int size) {
//            return new IntentData[size];
//        }
//    };

    /**
     * This is the constructor run when intent_record.IntentData is
     * created from a parcel.
     */
//    public IntentData(Parcel in){
//        // read from parcel
//        intent = in.readParcelable(Intent.class.getClassLoader());
//        sender = in.readParcelable(String.class.getClassLoader());
//        receiver = in.readParcelable(String.class.getClassLoader());
//    }
}
