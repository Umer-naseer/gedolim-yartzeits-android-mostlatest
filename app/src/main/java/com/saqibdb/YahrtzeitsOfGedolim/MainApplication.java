package com.saqibdb.YahrtzeitsOfGedolim;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class MainApplication extends MultiDexApplication {

    public static Context context;
    private static MainApplication mInstance;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MainApplication.context = context;
    }

    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());
        mInstance = this;
    }
}
