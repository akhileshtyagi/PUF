package edu.isu.reu.intent_collection_service;

import android.os.Binder;
import android.util.Log;

/**
 * Created by element on 6/27/16.
 */
public class IntentCollectionBinder extends Binder {
    public enum Action {
        ADD(1);

        private  int int_value;

        Action(int value){ this.int_value = value; }

        public int get_int_value(){ return this.int_value; }
    }

    /**
     * provides constructor
     */
    public IntentCollectionBinder(IntentCollectionService intent_collection_service){
        super();

        this.intent_collection_service = intent_collection_service;
    }

    /**
     * reference to intent collection service
     */
    private IntentCollectionService intent_collection_service;

    public IntentCollectionService get_service(){
        //TODO test to see fi the service has been assigned
        Log.d("ICB", "isnull: " + this.intent_collection_service);

        return this.intent_collection_service;
    }

//    /**
//     * The transaction will preform the
//     * action specified by code to
//     * incoming data parcel
//     *
//     * @param code action to perform
//     * @param data incoming data
//     * @param reply outgoing data
//     * @param flags 0 (normal) or FLAG_ONEWAY
//     * @return true for successfully completed transaction
//     * @throws RemoteException
//     */
////    @Override
////    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
////        // parcel describes an intent, add this intent to the list in service
////        if(code == Action.ADD.get_int_value()){
////            // extract the intent from the parcel data
////            //TODO
////            Intent parcel_intent = new Intent();
////
////            // use the service to handle the intent
////            this.intent_collection_service.handle_intent(parcel_intent);
////        }
////
////
////        return false;
////    }
}
