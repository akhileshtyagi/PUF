package edu.isu.reu.intent_collection_service;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;

/**
 * Created by element on 6/27/16.
 */
public class IntentCollectionBinder implements IBinder {
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

    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return "IBinder";
    }

    @Override
    public boolean pingBinder() {
        //TODO
        return false;
    }

    @Override
    public boolean isBinderAlive() {
        //TODO
        return false;
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        //TODO
        return null;
    }

    @Override
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        //TODO
    }

    @Override
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        //TODO
    }

    /**
     * The transaction will preform the
     * action specified by code to
     * incoming data parcel
     *
     * @param code action to perform
     * @param data incoming data
     * @param reply outgoing data
     * @param flags 0 (normal) or FLAG_ONEWAY
     * @return true for successfully completed transaction
     * @throws RemoteException
     */
    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        // parcel describes an intent, add this intent to the list in service
        if(code == Action.ADD.get_int_value()){
            // extract the intent from the parcel data
            //TODO
            Intent parcel_intent = new Intent();

            // use the service to handle the intent
            this.intent_collection_service.handle_intent(parcel_intent);
        }


        return false;
    }

    @Override
    public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {
        //TODO
    }

    @Override
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        //TODO
        return false;
    }

    public IntentCollectionService get_service(){
        return this.intent_collection_service;
    }
}
