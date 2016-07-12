package intent_collection;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by element on 7/10/16.
 *
 * Contains all information about all intents.
 *      1. intents themselves
 *      2. sender
 *      3. receiver
 */
public class IntentData implements Parcelable{
    private ArrayList<Intent> intent_list;
    private ArrayList<Intent> sender_list;
    private ArrayList<Intent> receiver_list;

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
     * This is the constructor run when IntentData is
     * created from a parcel.
     */
    public IntentData(Parcel in){
        intent_list = new ArrayList<>();
        sender_list = new ArrayList<>();
        receiver_list = new ArrayList<>();

        // get the object lists from the parcel
        Object[] intent_object_list = in.readArray(Intent.class.getClassLoader());
        Object[] sender_object_list = in.readArray(Intent.class.getClassLoader());
        Object[] receiver_object_list = in.readArray(Intent.class.getClassLoader());

        // cast the objects to their respective classes and store
        for(int i=0; i<intent_object_list.length; i++){
            intent_list.add((Intent)intent_object_list[i]);
            sender_list.add((Intent)sender_object_list[i]);
            receiver_list.add((Intent)receiver_object_list[i]);
        }
    }

    public IntentData(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(intent_list.toArray());
        dest.writeArray(sender_list.toArray());
        dest.writeArray(receiver_list.toArray());
    }
}
