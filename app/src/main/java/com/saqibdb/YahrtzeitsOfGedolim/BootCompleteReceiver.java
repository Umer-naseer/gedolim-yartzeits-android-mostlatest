package com.saqibdb.YahrtzeitsOfGedolim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        new NotificationGenerate(context, NotificationGenerate.ALL).execute();
        Log.e("eventTesting","boot complete");
    }
}
