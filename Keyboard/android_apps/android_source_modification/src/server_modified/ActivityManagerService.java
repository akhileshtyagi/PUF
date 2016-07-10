package com.android.server.am;

import android.app.IApplicationThread;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.server.am.ActivityManagerServiceOriginal;

/**
 * The purpose of this class is to capture intents sent between activities
 */
public final class ActivityManagerService extends ActivityManagerServiceOriginal {

    public ActivityManagerService(Context systemContext) {
        super(systemContext);
    }

    @Override startActivity(IApplicationThread caller, String callingPackage,
                            Intent intent, String resolvedType, IBinder resultTo,
                            String resultWho, int requestCode, int startFlags,
                            String profileFile, ParcelFileDescriptor profileFd, Bundle options){
        // understand where the intent came from
        intentProfile(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, profileFile, profileFd, options);

        // preform all same operations as parent otherwise
        return super(caller, callingPackage, intent, resolvedType, resultTo, resultWho, requestCode, startFlags, profileFile, profileFd, options);
    }

    private void intentProfile(IApplicationThread caller, String callingPackage,
                  Intent intent, String resolvedType, IBinder resultTo,
                  String resultWho, int requestCode, int startFlags,
                  String profileFile, ParcelFileDescriptor profileFd, Bundle options, String callerMethod) {
        /** PolyResearch **/
        // Add Log.i's for each parameter
        // callerMethod is method that called intentProfile
        Log.i("intentProfile callingPackage", callingPackage);
        Log.i("intentProfile intent", intent.toString());
        Log.i("intentProfile resolvedType", resolvedType);
        Log.i("intentProfile resultTo", resultTo.toString());
    }
}
