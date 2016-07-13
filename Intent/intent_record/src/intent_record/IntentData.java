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
public class IntentData implements Parcelable{
    private Intent intent;
    private Intent sender;
    private Intent receiver;

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public IntentData createFromParcel(Parcel in) {
            return new IntentData(in);
        }

        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };

    /**
     * This is the constructor run when intent_record.IntentData is
     * created from a parcel.
     */
    public IntentData(Parcel in){
        // read from parcel
        intent = in.readParcelable(Intent.class.getClassLoader());
        sender = in.readParcelable(Intent.class.getClassLoader());
        receiver = in.readParcelable(Intent.class.getClassLoader());
    }

    public IntentData(Intent intent, Intent sender, Intent receiver){
        this.intent = intent;
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int flags) {
        // write to parcel
        in.writeParcelable(intent, 0);
        in.writeParcelable(sender, 0);
        in.writeParcelable(receiver, 0);
    }
}
