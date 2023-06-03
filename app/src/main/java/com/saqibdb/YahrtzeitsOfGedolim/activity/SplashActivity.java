package com.saqibdb.YahrtzeitsOfGedolim.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.saveAllEventInLocalDB;

import java.util.ArrayList;
import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 1500;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ProgressBar progressBar = findViewById(R.id.progressBarS);
        boolean isSaved = SharedPreferencesHelper.getInstance().getBoolean("isDataSaved_", false);
        Constants.NOTIFICATION_HOURS = SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 8);
        Constants.NOTIFICATION_MIN = SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_MIN, 0);
        SharedPreferencesHelper.getInstance().setString(Constants.DATE_SELECTED, "");
        if (isSaved) {
            progressBar.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, CalendarViewActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH) + 1;
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            new saveAllEventInLocalDB(SplashActivity.this, DateUtil.convertGDateToHDate(year, month, day), new saveAllEventInLocalDB.OnComplete() {
                @Override
                public void onComplete() {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent i = new Intent(SplashActivity.this, CalendarViewActivity.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        finishAffinity();
                    },1000);
                }
            }, new ArrayList<>()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        //new NotificationGenerate(this, NotificationGenerate.NEW);

    }

}