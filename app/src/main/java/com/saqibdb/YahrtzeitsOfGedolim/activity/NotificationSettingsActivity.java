package com.saqibdb.YahrtzeitsOfGedolim.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.NotificationGenerate;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;

public class NotificationSettingsActivity extends AppCompatActivity {
    private TimePicker timePicker;
    SwitchCompat onOffNotification;
    TextView doneBtn ;

    int hour=8, min=0;
    boolean onOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        initViews();
        initActions();
    }

    void initViews(){
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        onOffNotification = findViewById(R.id.onOffNotification);
        doneBtn = findViewById(R.id.doneBtn);

        timePicker.setIs24HourView(DateFormat.is24HourFormat(NotificationSettingsActivity.this));
        // here you can define your hour and minute value.
        timePicker.setCurrentHour(SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_HOURS, 8));
        timePicker.setCurrentMinute(SharedPreferencesHelper.getInstance().getInt(Constants.SAVE_NOTIFICATION_MIN, 0));

        onOffNotification.setChecked(SharedPreferencesHelper.getInstance().getBoolean(Constants.TURN_NOTIFICATION_ON_OFF, true));
        onOff = SharedPreferencesHelper.getInstance().getBoolean(Constants.TURN_NOTIFICATION_ON_OFF, true);

    }
    void initActions(){

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Constants.NOTIFICATION_HOURS = timePicker.getHour();
                Constants.NOTIFICATION_MIN = timePicker.getMinute();
                SharedPreferencesHelper.getInstance().setInt(Constants.SAVE_NOTIFICATION_HOURS, timePicker.getHour());
                SharedPreferencesHelper.getInstance().setInt(Constants.SAVE_NOTIFICATION_MIN, timePicker.getMinute());
//                new NotificationGenerate(NotificationSettingsActivity.this, NotificationGenerate.ALL, onOff).execute();
            }
        });

        onOffNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesHelper.getInstance().setBoolean(Constants.TURN_NOTIFICATION_ON_OFF, b);
                new NotificationGenerate(NotificationSettingsActivity.this, NotificationGenerate.ALL).execute();
            }
        });

        doneBtn.setOnClickListener(v -> {
            new NotificationGenerate(NotificationSettingsActivity.this, NotificationGenerate.ALL).execute();
            onBackPressed();
        });


    }
}