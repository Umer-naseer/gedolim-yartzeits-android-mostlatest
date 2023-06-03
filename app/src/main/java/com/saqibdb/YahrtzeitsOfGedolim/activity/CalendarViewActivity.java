package com.saqibdb.YahrtzeitsOfGedolim.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.AddEventDialog;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.adapter.CalendarDateAdapter;
import com.saqibdb.YahrtzeitsOfGedolim.adapter.CalendarEventAdapter;
import com.saqibdb.YahrtzeitsOfGedolim.customDatePicker.DateTimeListener;
import com.saqibdb.YahrtzeitsOfGedolim.customDatePicker.DateTimePickerDialog;
import com.saqibdb.YahrtzeitsOfGedolim.customRecyclerView.SearchByNamesActivity;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.helper.Utility;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.GetEvent;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.saqibdb.YahrtzeitsOfGedolim.network.AppRestClient;
import com.saqibdb.YahrtzeitsOfGedolim.network.ServerManager;
import com.saqibdb.YahrtzeitsOfGedolim.saveAllEventInLocalDB;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class CalendarViewActivity extends AppCompatActivity {

    //    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    public GregorianCalendar month, itemmonth;
    public CalendarDateAdapter adapter;
    RecyclerView recyclerViewCalendar;
    DatabaseHandler dbHandler;
    TextView tvEngMonth, tvEngYear, tvHebrewMonth, tvHebrewYear, goToToday, addNewEvent, notificationSettings, adarIAdarIISameDayEventsLink;
    RecyclerView recyclerViewEvent;
    ImageView addManuallyBtn;
    ProgressDialog progressDialog;
    HebrewDateModel todayHebrewDateModel;
    CalendarEventAdapter calendarEventAdapter;
    ArrayList<EventDetails> currentDayEventList = new ArrayList<>();
    private int savedPosition = -1;
    private String saveSelectedDate = "";
    boolean isShowLoading;
    LinearLayout shareLayout;
    boolean isVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        Locale.setDefault(Locale.US);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        initView();
        onClick();
        showReviewPopup();

        String[] strMatch = ((String) DateFormat.format("MMM yyyy", month)).split(" ");
        tvEngMonth.setText(strMatch[0]);
        tvEngYear.setText(strMatch[1]);

        if (dbHandler == null) {
            dbHandler = new DatabaseHandler(CalendarViewActivity.this);
        }
        askForPermissions();
        doConversionTodayDate();

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void showReviewPopup() {
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(CalendarViewActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void initView() {
        recyclerViewEvent = (RecyclerView) findViewById(R.id.recyclerViewEvent);
        addManuallyBtn = (ImageView) findViewById(R.id.addManuallyBtn);
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(CalendarViewActivity.this, LinearLayoutManager.VERTICAL, false));
        tvEngMonth = (TextView) findViewById(R.id.tvEngMonth);
        tvEngYear = (TextView) findViewById(R.id.tvEngYear);
        tvHebrewMonth = (TextView) findViewById(R.id.tvHebrewMonthC);
        tvHebrewYear = (TextView) findViewById(R.id.tvHebrewYear);
        goToToday = findViewById(R.id.tvGoToday);
        shareLayout = findViewById(R.id.shareOptionView);
        addNewEvent = findViewById(R.id.addNewEvent);
        notificationSettings = findViewById(R.id.notificationSettings);
        adarIAdarIISameDayEventsLink = findViewById(R.id.adarIAdarIISameDayEventsLink);


        recyclerViewCalendar = (RecyclerView) findViewById(R.id.recyclerViewCalendar);
        recyclerViewCalendar.setLayoutManager(new GridLayoutManager(CalendarViewActivity.this, 7));
        adapter = new CalendarDateAdapter(CalendarViewActivity.this, month);
        recyclerViewCalendar.setAdapter(adapter);
        progressDialog = new ProgressDialog(CalendarViewActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
    }

    public void setRows(int count) {
    }

    private void askForPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
    }

    private void onClick() {
        findViewById(R.id.tvSearchNames).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalendarViewActivity.this, SearchByNamesActivity.class).putExtra(Constants.PRIVATE_LIST_SHOW, false));
            }
        });
        addManuallyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible) {
                    shareLayout.setVisibility(View.GONE);
                    isVisible = false;
                } else {
                    shareLayout.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
            }
        });

        addNewEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLayout.setVisibility(View.GONE);
                isVisible = false;
                AddEventDialog dialog = new AddEventDialog(CalendarViewActivity.this);
                dialog.setCancelable(true);
                dialog.ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(CalendarViewActivity.this, AddEventActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
                dialog.show();
            }
        });

        notificationSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLayout.setVisibility(View.GONE);
                isVisible = false;
                startActivity(new Intent(CalendarViewActivity.this, NotificationSettingsActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        goToToday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                month.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                adapter.curentDateString = df.format(calendar.getTimeInMillis());
                refreshCalendar();
            }
        });

        findViewById(R.id.linHebrewMonthYear).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.previous).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAndClearEventList();
                setPreviousMonth();
                refreshCalendar();
            }
        });

        findViewById(R.id.next).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAndClearEventList();
                setNextMonth();
                refreshCalendar();
            }
        });

        adarIAdarIISameDayEventsLink.setOnClickListener(v -> {
            showAdarIAdarIISameDayEvents();
        });
    }

    @Override
    protected void onResume() {
        String date = SharedPreferencesHelper.getInstance().getString(Constants.DATE_SELECTED, "");
        if (!date.isEmpty()) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar calendar = Calendar.getInstance();
            try {
                Date dateObj;
                dateObj = df.parse(date);
                calendar.setTime(dateObj);
                final int year = calendar.get(Calendar.YEAR);
                final int hMonth = calendar.get(Calendar.MONTH) + 1;
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                HebrewDateModel dateModel = DateUtil.convertGDateToHDate(year, hMonth, day);
                month.set(dateModel.getGy(), dateModel.getGm() - 1, dateModel.getGd());
                dateObj = df.parse(dateModel.getGy() + "-" + dateModel.getGm() + "-" + dateModel.getGd());
                adapter.curentDateString = df.format(dateObj.getTime());
                refreshCalendar();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        super.onResume();
    }

    private void showAdarIAdarIISameDayEvents() {
        String date = SharedPreferencesHelper.getInstance().getString(Constants.DATE_SELECTED, "");
        if (!date.isEmpty()) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar calendar = Calendar.getInstance();
            try {
                final Date[] dateObj = new Date[1];
                dateObj[0] = df.parse(date);
                calendar.setTime(dateObj[0]);
                final int year = calendar.get(Calendar.YEAR);
                final int[] hMonth = {calendar.get(Calendar.MONTH) + 1};
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                HebrewDateModel dateModel = DateUtil.convertGDateToHDate(year, hMonth[0], day);
                String toggleMonth;

                if (dateModel != null) {
                    if (dateModel.getHm().equals("Adar II"))
                        toggleMonth = "Adar1";
                    else
                        toggleMonth = "Adar2";

                    // call api to convert date of same day
                    String strUrl = "http://www.hebcal.com/converter/?cfg=json&hy="
                            + dateModel.getHy() + "&hm=" + toggleMonth + "&hd=" + dateModel.getHd() + "&h2g=1";
                    ServerManager.doDateConversionTodayDate(R.string.internet_connection_error_text, CalendarViewActivity.this, strUrl, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            progressDialog.show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            progressDialog.hide();
                            Toast.makeText(CalendarViewActivity.this, "something went wrong try again", Toast.LENGTH_SHORT).show();
                        }

                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            progressDialog.hide();
                            String strResponse = response.toString();
                            Gson gson = new Gson();
                            HebrewDateModel convertedHebrewDateModel = gson.fromJson(strResponse, HebrewDateModel.class);
                            if (convertedHebrewDateModel != null) {
                                month.set(convertedHebrewDateModel.getGy(), convertedHebrewDateModel.getGm() - 1, convertedHebrewDateModel.getGd());
                                try {
                                    dateObj[0] = df.parse(convertedHebrewDateModel.getGy() + "-" + convertedHebrewDateModel.getGm() + "-" + convertedHebrewDateModel.getGd());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                adapter.curentDateString = df.format(dateObj[0].getTime());
                                refreshCalendar();
                            } else
                                Toast.makeText(CalendarViewActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void showPrivateEventsOnly() {
        startActivity(new Intent(CalendarViewActivity.this, SearchByNamesActivity.class).putExtra(Constants.PRIVATE_LIST_SHOW, true));
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int hMonth = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        HebrewDateModel dateModel = DateUtil.convertGDateToHDate(year, hMonth, day);

        int intMonth = Utility.getMonthInt(dateModel.getHm());
        DateTimePickerDialog pd = DateTimePickerDialog.newInstance(intMonth - 1, dateModel.getHd() - 1,
                "" + dateModel.hy, dateModel.getHy() - 5700);
        pd.setListener(new DateTimeListener() {
            @Override
            public void setDate(int date, int intMonth, String year, int yearNumber) {
                HebrewDateModel hebrewDateModel1;
                if (intMonth == 0)
                    intMonth = 1;
                hebrewDateModel1 = DateUtil.convertHDateToGDate(Integer.parseInt(year), intMonth, date);
                if (hebrewDateModel1 == null) {
                    hebrewDateModel1 = DateUtil.convertHDateToGDate(Integer.parseInt(year), intMonth - 1, date);
                    if (hebrewDateModel1 != null) {
                        if (hebrewDateModel1.getHm().equals("Adar") || hebrewDateModel1.getHm().equals("Adar I") || hebrewDateModel1.getHm().equals("Adar II")) {
                            adarIAdarIISameDayEventsLink.setVisibility(View.VISIBLE);
                        } else {
                            adarIAdarIISameDayEventsLink.setVisibility(View.GONE);
                        }
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date dateObj = new Date();
                        try {
                            dateObj = df.parse(hebrewDateModel1.getGy() + "-" + hebrewDateModel1.getGm() + "-" + hebrewDateModel1.getGd());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        month.set(hebrewDateModel1.getGy(), hebrewDateModel1.getGm() - 1, hebrewDateModel1.getGd());
                        adapter.curentDateString = df.format(dateObj.getTime());
                        //                    Toast.makeText(CalendarViewActivity.this, "Invalid Hebrew Date", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (hebrewDateModel1.getHm().equals("Adar") || hebrewDateModel1.getHm().equals("Adar I") || hebrewDateModel1.getHm().equals("Adar II")) {
                        adarIAdarIISameDayEventsLink.setVisibility(View.VISIBLE);
                    } else {
                        adarIAdarIISameDayEventsLink.setVisibility(View.GONE);
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date dateObj = new Date();
                    try {
                        dateObj = df.parse(hebrewDateModel1.getGy() + "-" + hebrewDateModel1.getGm() + "-" + hebrewDateModel1.getGd());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    month.set(hebrewDateModel1.getGy(), hebrewDateModel1.getGm() - 1, hebrewDateModel1.getGd());
                    adapter.curentDateString = df.format(dateObj.getTime());
                }
                refreshCalendar();


//                else {
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//                    Date dateObj = new Date();
//                    try {
//                        dateObj = df.parse(hebrewDateModel1.getGy() + "-" + hebrewDateModel1.getGm() + "-" + hebrewDateModel1.getGd());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    month.set(hebrewDateModel1.getGy(), hebrewDateModel1.getGm() - 1, hebrewDateModel1.getGd());
//                    adapter.curentDateString = df.format(dateObj.getTime());
//                    refreshCalendar();
//                }
            }
        });
        pd.show(getSupportFragmentManager(), "datePicker");


    }


    public void hideAndClearEventList() {
        recyclerViewEvent.setVisibility(View.GONE);
        findViewById(R.id.tvEventNotFound).setVisibility(View.VISIBLE);
    }

    public void setToolbarHebrewMonthYear(String selectedGridDate) {
        String[] split = selectedGridDate.split("-");
        HebrewDateModel dateModel = DateUtil.convertGDateToHDate(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        if (dateModel != null) {
            tvHebrewMonth.setText("");
            String text = dateModel.getHm();
            tvHebrewMonth.setText(" " + text + " ");
            Log.e("hms", dateModel.getHm());
            tvHebrewYear.setText("" + dateModel.hy);
        }
    }

    // FromCalendarDateAdapter
    public void getDisplaySelectedDateEventList(int position, final String selectedDate) {

//        savedPosition = position;

        SharedPreferencesHelper.getInstance().setString(Constants.DATE_SELECTED, selectedDate);
//        saveSelectedDate = selectedDate;
        String[] separatedTime = selectedDate.split("-");
        String strMonth = separatedTime[1].replaceFirst("^0*", "");// taking last part of day. ie; 2 from 02.
        String strDay = separatedTime[2].replaceFirst("^0*", "");// taking last part of day. ie; 2 from 02.

        HebrewDateModel hebrewDateModel = DateUtil.convertGDateToHDate(
                Integer.parseInt(separatedTime[0]), Integer.parseInt(strMonth), Integer.parseInt(strDay));

        String selectedGridDate = CalendarDateAdapter.dayString.get(position);
        //String[] separatedTime = selectedGridDate.split("-");
        String gridvalueString = separatedTime[2].replaceFirst("^0*", "");     // taking last part of date. ie; 2 from 2012-12-02.
        int gridvalue = Integer.parseInt(gridvalueString);
        // navigate to next or previous month on clicking offdays.
        if ((gridvalue > 10) && (position < 8)) {
            setPreviousMonth();
            refreshCalendar();
        } else if ((gridvalue < 7) && (position > 28)) {
            setNextMonth();
            refreshCalendar();
        }
        setToolbarHebrewMonthYear(selectedGridDate);
        getEventListByDayMonth(hebrewDateModel, selectedDate);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(Constants.POSITION, savedPosition);
        savedInstanceState.putString(Constants.DATE_SELECTED, saveSelectedDate);
    }

    //yeh hai
    public void getEventListByDayMonth(final HebrewDateModel hebrewDateModel, String selectedDate) {

        String[] stringArray = selectedDate.split("-");
        HebrewDateModel dateModel = DateUtil.convertGDateToHDate(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]));
        currentDayEventList = new ArrayList<>();
        currentDayEventList = dbHandler.getEventListByDayMonth(dateModel.getHd().toString(), "" + dateModel.getHm_());
        if (currentDayEventList != null && currentDayEventList.size() > 0) {
            Collections.sort(currentDayEventList, new Comparator<EventDetails>() {
                @Override
                public int compare(EventDetails o1, EventDetails o2) {
                    return o1.getSubjectTitle().compareTo(o2.getSubjectTitle());
                }
            });
            calendarEventAdapter = new CalendarEventAdapter(CalendarViewActivity.this, hebrewDateModel, currentDayEventList);
            recyclerViewEvent.getRecycledViewPool().clear();
            recyclerViewEvent.setAdapter(calendarEventAdapter);
            isShowLoading = false;
            recyclerViewEvent.setVisibility(View.VISIBLE);
            findViewById(R.id.tvEventNotFound).setVisibility(View.GONE);
        } else {
            isShowLoading = true;
            recyclerViewEvent.setVisibility(View.GONE);
            findViewById(R.id.tvEventNotFound).setVisibility(View.VISIBLE);
        }

        int hMonth = hebrewDateModel.getHm_();
        int hDay = hebrewDateModel.getHd();
        ServerManager.getEventListByDate(R.string.internet_connection_error_text, CalendarViewActivity.this,
                hMonth, hDay, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();

                        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing()) {
                            if (isShowLoading) {
                                progressDialog.show();
                            }
                        }//chirag
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                            progressDialog.dismiss();
                        Log.d("jsonresponseOrder : ", throwable.getMessage());
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("jsonresponseOrder : ", response.toString());
                        Utility.SYNCED_LIST.add(selectedDate);
                        try {
                            Gson gson = new Gson();
                            GetEvent getEvent = gson.fromJson(response.toString(), GetEvent.class);

//                            List<EventDetails> currentDayEventList = new ArrayList<>();
                            if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                                currentDayEventList = (ArrayList<EventDetails>) getEvent.getEventDetails();
                            }

                            for (int i = 0; i < currentDayEventList.size(); i++) {
                                String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                                currentDayEventList.get(i).setDay(hebrewDateModel.getGd());
                                currentDayEventList.get(i).setMonth(hebrewDateModel.getGm());
                                currentDayEventList.get(i).setYear(hebrewDateModel.getGy());
                                currentDayEventList.get(i).setDayHebrewStr("" + arrStr[0].trim());
                                currentDayEventList.get(i).setDayHebrew("" + hebrewDateModel.getHd());
                                currentDayEventList.get(i).setMonthHebrew("" + hebrewDateModel.getHm_());
                                currentDayEventList.get(i).setMonthHebrewStr("" + hebrewDateModel.getHm());
                                currentDayEventList.get(i).setYearHebrew("" + hebrewDateModel.getHy());
                            }

                            if (currentDayEventList.size() > 0) {
                                dbHandler.addEvent(currentDayEventList);
                                new Handler().postDelayed(() -> {
                                    new saveAllEventInLocalDB(CalendarViewActivity.this, hebrewDateModel, null, new ArrayList<>()).execute();
                                    getRefreshData(hebrewDateModel,selectedDate);
                                }, 100);
                            } else {
                                getRefreshData(hebrewDateModel,selectedDate);
//                                getEventListByDayMonth(hebrewDateModel, selectedDate);
                            }

                        } catch (Exception e) {
                            Toast.makeText(CalendarViewActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                            progressDialog.dismiss();

                    }
                });


    }

    public void getRefreshData(HebrewDateModel hebrewDateModel,String saveSelectedDate) {
        String[] stringArray = saveSelectedDate.split("-");
        HebrewDateModel dateModel = DateUtil.convertGDateToHDate(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]));

        currentDayEventList = dbHandler.getEventListByDayMonth(dateModel.getHd().toString(), "" + dateModel.getHm_());

        if (currentDayEventList != null && currentDayEventList.size() > 0) {
            Collections.sort(currentDayEventList, new Comparator<EventDetails>() {
                @Override
                public int compare(EventDetails o1, EventDetails o2) {
                    return o1.getSubjectTitle().compareTo(o2.getSubjectTitle());
                }
            });
        }
        calendarEventAdapter = new CalendarEventAdapter(CalendarViewActivity.this, hebrewDateModel, currentDayEventList);
        recyclerViewEvent.getRecycledViewPool().clear();
        recyclerViewEvent.setAdapter(calendarEventAdapter);
    }

    public void getEventListByDayMonthOld(final HebrewDateModel hebrewDateModel, String selectedDate) {

        if (!Utility.SYNCED_LIST.contains(selectedDate)) {
            int hMonth = hebrewDateModel.getHm_();
            int hDay = hebrewDateModel.getHd();
//            if (hMonth == 12 || hMonth == 13){
//                getEvetnsOfAdarIAdarII(hebrewDateModel, selectedDate);
//            } else{
//
//            }
            ServerManager.getEventListByDate(R.string.internet_connection_error_text, CalendarViewActivity.this,
                    hMonth, hDay, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();

                            if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
                                progressDialog.show();//chirag
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();
                            Log.d("jsonresponseOrder : ", throwable.getMessage());
                        }

                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("jsonresponseOrder : ", response.toString());
                            Utility.SYNCED_LIST.add(selectedDate);
                            try {
                                Gson gson = new Gson();
                                GetEvent getEvent = gson.fromJson(response.toString(), GetEvent.class);

                                List<EventDetails> currentDayEventList = new ArrayList<>();
                                if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                                    currentDayEventList = getEvent.getEventDetails();
                                }

                                for (int i = 0; i < currentDayEventList.size(); i++) {
                                    String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                                    currentDayEventList.get(i).setDay(hebrewDateModel.getGd());
                                    currentDayEventList.get(i).setMonth(hebrewDateModel.getGm());
                                    currentDayEventList.get(i).setYear(hebrewDateModel.getGy());
                                    currentDayEventList.get(i).setDayHebrewStr("" + arrStr[0].trim());
                                    currentDayEventList.get(i).setDayHebrew("" + hebrewDateModel.getHd());
                                    currentDayEventList.get(i).setMonthHebrew("" + hebrewDateModel.getHm_());
                                    currentDayEventList.get(i).setMonthHebrewStr("" + hebrewDateModel.getHm());
                                    currentDayEventList.get(i).setYearHebrew("" + hebrewDateModel.getHy());
                                }

                                if (currentDayEventList.size() > 0) {
                                    dbHandler.addEvent(currentDayEventList);
                                    new Handler().postDelayed(() -> {
                                        new saveAllEventInLocalDB(CalendarViewActivity.this, hebrewDateModel, null, new ArrayList<>()).execute();
                                        getEventListByDayMonth(hebrewDateModel, selectedDate);
                                    }, 100);
                                } else {
                                    getEventListByDayMonth(hebrewDateModel, selectedDate);
                                }

                            } catch (Exception e) {
                                Toast.makeText(CalendarViewActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();

                        }
                    });
        } else {
            String[] stringArray = selectedDate.split("-");
            HebrewDateModel dateModel = DateUtil.convertGDateToHDate(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]));

            ArrayList<EventDetails> list = dbHandler.getEventListByDayMonth(dateModel.getHd().toString(), "" + dateModel.getHm_());
            if (list != null && list.size() > 0) {
                Collections.sort(list, new Comparator<EventDetails>() {
                    @Override
                    public int compare(EventDetails o1, EventDetails o2) {
                        return o1.getSubjectTitle().compareTo(o2.getSubjectTitle());
                    }
                });
                calendarEventAdapter = new CalendarEventAdapter(CalendarViewActivity.this, hebrewDateModel, list);
                recyclerViewEvent.getRecycledViewPool().clear();
                recyclerViewEvent.setAdapter(calendarEventAdapter);

                recyclerViewEvent.setVisibility(View.VISIBLE);
                findViewById(R.id.tvEventNotFound).setVisibility(View.GONE);
            } else {
                recyclerViewEvent.setVisibility(View.GONE);
                findViewById(R.id.tvEventNotFound).setVisibility(View.VISIBLE);
            }
        }
    }

    private void getEvetnsOfAdarIAdarII(final HebrewDateModel hebrewDateModel, String selectedDate) {
        ArrayList<EventDetails> currentDayEventList = new ArrayList<>();
        int hMonth = hebrewDateModel.getHm_();
        int hDay = hebrewDateModel.getHd();

        if (hMonth == 13) {
            int temp = hMonth - 1;
            ServerManager.getEventListByDate(R.string.internet_connection_error_text, CalendarViewActivity.this,
                    temp, hDay, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();

                            if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
                                progressDialog.show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();
                            Log.d("jsonresponseOrder : ", throwable.getMessage());
                        }

                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("jsonresponseOrder : ", response.toString());
                            Utility.SYNCED_LIST.add(selectedDate);
                            try {
                                Gson gson = new Gson();
                                GetEvent getEvent = gson.fromJson(response.toString(), GetEvent.class);


                                if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                                    currentDayEventList.addAll(getEvent.getEventDetails());
                                }

                                for (int i = 0; i < currentDayEventList.size(); i++) {
                                    String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                                    currentDayEventList.get(i).setDay(hebrewDateModel.getGd());
                                    currentDayEventList.get(i).setMonth(hebrewDateModel.getGm());
                                    currentDayEventList.get(i).setYear(hebrewDateModel.getGy());
                                    currentDayEventList.get(i).setDayHebrewStr("" + arrStr[0].trim());
                                    currentDayEventList.get(i).setDayHebrew("" + hebrewDateModel.getHd());
                                    currentDayEventList.get(i).setMonthHebrew("" + hebrewDateModel.getHm_());
                                    currentDayEventList.get(i).setMonthHebrewStr("" + hebrewDateModel.getHm());
                                    currentDayEventList.get(i).setYearHebrew("" + hebrewDateModel.getHy());
                                }


                            } catch (Exception e) {
                                Toast.makeText(CalendarViewActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();

                        }
                    });


        } else {
            int temp = hMonth + 1;
            ServerManager.getEventListByDate(R.string.internet_connection_error_text, CalendarViewActivity.this,
                    temp, hDay, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();

                            if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
                                progressDialog.show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();
                            Log.d("jsonresponseOrder : ", throwable.getMessage());
                        }

                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("jsonresponseOrder : ", response.toString());
                            Utility.SYNCED_LIST.add(selectedDate);
                            try {
                                Gson gson = new Gson();
                                GetEvent getEvent = gson.fromJson(response.toString(), GetEvent.class);


                                if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                                    currentDayEventList.addAll(getEvent.getEventDetails());
                                }

                                for (int i = 0; i < currentDayEventList.size(); i++) {
                                    String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                                    currentDayEventList.get(i).setDay(hebrewDateModel.getGd());
                                    currentDayEventList.get(i).setMonth(hebrewDateModel.getGm());
                                    currentDayEventList.get(i).setYear(hebrewDateModel.getGy());
                                    currentDayEventList.get(i).setDayHebrewStr("" + arrStr[0].trim());
                                    currentDayEventList.get(i).setDayHebrew("" + hebrewDateModel.getHd());
                                    currentDayEventList.get(i).setMonthHebrew("" + hebrewDateModel.getHm_());
                                    currentDayEventList.get(i).setMonthHebrewStr("" + hebrewDateModel.getHm());
                                    currentDayEventList.get(i).setYearHebrew("" + hebrewDateModel.getHy());
                                }


                            } catch (Exception e) {
                                Toast.makeText(CalendarViewActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                                progressDialog.dismiss();

                        }
                    });
        }

        ServerManager.getEventListByDate(R.string.internet_connection_error_text, CalendarViewActivity.this,
                hMonth, hDay, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();

                        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
                            progressDialog.show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                            progressDialog.dismiss();
                        Log.d("jsonresponseOrder : ", throwable.getMessage());
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("jsonresponseOrder : ", response.toString());
                        Utility.SYNCED_LIST.add(selectedDate);
                        try {
                            Gson gson = new Gson();
                            GetEvent getEvent = gson.fromJson(response.toString(), GetEvent.class);


                            if (getEvent != null && getEvent.getEventDetails() != null && getEvent.getEventDetails().size() > 0) {
                                currentDayEventList.addAll(getEvent.getEventDetails());
                            }

                            for (int i = 0; i < currentDayEventList.size(); i++) {
                                String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                                currentDayEventList.get(i).setDay(hebrewDateModel.getGd());
                                currentDayEventList.get(i).setMonth(hebrewDateModel.getGm());
                                currentDayEventList.get(i).setYear(hebrewDateModel.getGy());
                                currentDayEventList.get(i).setDayHebrewStr("" + arrStr[0].trim());
                                currentDayEventList.get(i).setDayHebrew("" + hebrewDateModel.getHd());
                                currentDayEventList.get(i).setMonthHebrew("" + hebrewDateModel.getHm_());
                                currentDayEventList.get(i).setMonthHebrewStr("" + hebrewDateModel.getHm());
                                currentDayEventList.get(i).setYearHebrew("" + hebrewDateModel.getHy());
                            }


                        } catch (Exception e) {
                            Toast.makeText(CalendarViewActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
                            progressDialog.dismiss();

                    }
                });

        if (currentDayEventList.size() > 0) {
            dbHandler.addEvent(currentDayEventList);
            new Handler().postDelayed(() -> {
                new saveAllEventInLocalDB(CalendarViewActivity.this, hebrewDateModel, null, new ArrayList<>()).execute();
                getEvetnsOfAdarIAdarII(hebrewDateModel, selectedDate);
            }, 100);
        }
//        else {
//            getEvetnsOfAdarIAdarII(hebrewDateModel, selectedDate);
//        }
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(month.get(GregorianCalendar.YEAR), month.get(GregorianCalendar.MONTH) + 1, 1);
        }
    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(month.get(GregorianCalendar.YEAR), month.get(GregorianCalendar.MONTH) - 1, 1);
        }
    }

    public void refreshCalendar() {
        AppRestClient.cancelAllDateRequests();
        adapter = new CalendarDateAdapter(CalendarViewActivity.this, month);
        recyclerViewCalendar.post(new Runnable() {
            @Override
            public void run() {
                recyclerViewCalendar.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
        });

        if (dbHandler == null)
            dbHandler = new DatabaseHandler(CalendarViewActivity.this);

        String firstDateOfMonth = adapter.refreshDays();
        String[] split = firstDateOfMonth.split("-");
        int gMonth = Integer.parseInt(split[1]);

        HebrewDateModel dateModel = DateUtil.convertGDateToHDate(Integer.parseInt(split[0]), gMonth - 1, Integer.parseInt(split[2]));
        //tvHebrewMonth.setText("" + dateModel.getHm());
        //Log.e("hms",dateModel.getHm());
//        tvHebrewYear.setText("" + dateModel.getHy());

        String[] strMatch = ((String) DateFormat.format("MMM yyyy", month)).split(" ");
        if (strMatch != null && strMatch.length > 1) {
            tvEngMonth.setText(strMatch[0]);
            tvEngYear.setText(strMatch[1]);
        }
    }

    private void doConversionTodayDate() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        todayHebrewDateModel = DateUtil.convertGDateToHDate(year, month, day);
        Utility.H_YEAR = todayHebrewDateModel.hy;
        Utility.G_YEAR = year;
        new saveAllEventInLocalDB(CalendarViewActivity.this, todayHebrewDateModel, null, new ArrayList<>()).execute();//OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sponsorMail(View view) {

        startActivity(new Intent(CalendarViewActivity.this, SponsorActivity.class));


//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                "mailto", "info@yartzeits.com", null));
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//                "Dedicate a Gadol");
//        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
//                " Hi,\n I would like to dedicate a Yartzeit of a Gadol, please get back to me.\n\n Thank you.");
//        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void suggestMail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"info@yartzeits.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestions Yartzeits App");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));

    }

}