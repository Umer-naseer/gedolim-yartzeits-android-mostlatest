package com.saqibdb.YahrtzeitsOfGedolim.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.AddEventDialog;
import com.saqibdb.YahrtzeitsOfGedolim.BroadcastManager;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.UpdateEventDialog;
import com.saqibdb.YahrtzeitsOfGedolim.WheelView;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.Utility;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.saqibdb.YahrtzeitsOfGedolim.network.ServerManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class AddEventActivity extends AppCompatActivity {
    Button convertBtn;
    EditText detailEt;
    TextView galleryBtn, cameraBtn, addBtn, cancelBtn, editNameTv;
    TextInputLayout nameLo;
    TextInputEditText nameEt;
    ImageView crosBtn, imageView;
    WheelView dayWheel, monthWheel;
    DatabaseHandler databaseHandler;
    int GALLERY_IMAGE_REQUEST = 123;
    int CAPTURE_IMAGE_REQUEST = 321;
    Uri photoURI;
    File photoFile;
    DatePickerDialog datePickerDialog;
    HebrewDateModel todayHebrewDateModel;
    int cYear, cMonth, cDay, cHour, cMint, cSec;
    Calendar calendar;
    ProgressDialog progressDialog;
    int hDay = 4;
    int hMonth = 4;
    String title = "", desc = "";
    Dialog dateDialog;
    ArrayList<String> images;
    ArrayList<EditText> extras = new ArrayList<>();
    boolean isConvert = false;

    EventDetails eventDetails;
    boolean isEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Calendar cal = Calendar.getInstance();
        cDay = cal.get(Calendar.DAY_OF_MONTH);
        cMonth = cal.get(Calendar.MONTH) + 1;
        cYear = cal.get(Calendar.YEAR);
        cHour = cal.get(Calendar.HOUR_OF_DAY);
        cMint = cal.get(Calendar.MINUTE);
        cSec = cal.get(Calendar.SECOND);

        eventDetails = (EventDetails) getIntent().getSerializableExtra("eventDetail");
        isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
        images = new ArrayList();

        inItView();
        if (!isEdit)
            getDate();
        setOnClick();
        inItDialog();
        checkIsEdit();
        databaseHandler = new DatabaseHandler(this);
    }

    private void inItDialog() {
        progressDialog = new ProgressDialog(AddEventActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);

        dateDialog = new Dialog(AddEventActivity.this);
        dateDialog.setContentView(R.layout.calender_dialog);
        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dateDialog.findViewById(R.id.dialogCancel).setOnClickListener(v -> {
            dateDialog.dismiss();
        });
        DatePicker picker = ((DatePicker) dateDialog.findViewById(R.id.datepickerDialog));
        dateDialog.findViewById(R.id.dialogSelect).setOnClickListener(v -> {
            isConvert = true;
            boolean afterSunset = ((Switch) dateDialog.findViewById(R.id.afterSunset)).isChecked();
            if (afterSunset) {
                dateDialog.dismiss();
                progressDialog.show();
                String strUrl = "http://www.hebcal.com/converter/?cfg=json&gy="
                        + picker.getYear() + "&gm=" + (picker.getMonth() + 1) + "&gd=" + picker.getDayOfMonth() + "&g2h=1&gs=on";
                ServerManager.doDateConversionTodayDate(R.string.internet_connection_error_text, AddEventActivity.this, strUrl, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progressDialog.hide();
                        Toast.makeText(AddEventActivity.this, "something went wrong try again", Toast.LENGTH_SHORT).show();
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.hide();
                        String strResponse = response.toString();
                        Gson gson = new Gson();
                        todayHebrewDateModel = gson.fromJson(strResponse, HebrewDateModel.class);
                        if (todayHebrewDateModel != null) {
                            hDay = todayHebrewDateModel.hd;
                            dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
                            HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
                            monthWheel.setSeletion(dateModel.hm_ - 1);
                            hMonth = dateModel.hm_;
                        } else
                            Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                todayHebrewDateModel = DateUtil.convertGDateToHDate(picker.getYear(), picker.getMonth() + 1, picker.getDayOfMonth());
                if (todayHebrewDateModel != null) {
                    hDay = todayHebrewDateModel.hd;
                    dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
                    HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
                    monthWheel.setSeletion(dateModel.hm_ - 1);
                    hMonth = dateModel.hm_;
                } else
                    Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();
                dateDialog.dismiss();
            }
        });
    }

    private void getDate(){
        Calendar today = Calendar.getInstance();

        todayHebrewDateModel = DateUtil.convertGDateToHDate(today.get(Calendar.YEAR), (today.get(Calendar.MONTH) + 1), today.get(Calendar.DATE));
        if (todayHebrewDateModel != null) {
            hDay = todayHebrewDateModel.hd;
            dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
            HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
            monthWheel.setSeletion(dateModel.hm_ - 1);
            hMonth = dateModel.hm_;
        } else
            Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();
//
//        String strUrl = "http://www.hebcal.com/converter/?cfg=json&gy="
//                + today.get(Calendar.YEAR) + "&gm=" + (today.get(Calendar.MONTH) + 1) + "&gd=" + today.get(Calendar.DATE) + "&g2h=1&gs=on";
//        ServerManager.doDateConversionTodayDate(R.string.internet_connection_error_text, AddEventActivity.this, strUrl, new JsonHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                progressDialog.hide();
//                Toast.makeText(AddEventActivity.this, "something went wrong try again", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                progressDialog.hide();
//                String strResponse = response.toString();
//                Gson gson = new Gson();
//                todayHebrewDateModel = gson.fromJson(strResponse, HebrewDateModel.class);
//                if (todayHebrewDateModel != null) {
//                    hDay = todayHebrewDateModel.hd;
//                    dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
//                    HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
//                    monthWheel.setSeletion(dateModel.hm_ - 1);
//                    hMonth = dateModel.hm_;
//                } else
//                    Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    private void getDateOfEditableEvent(EventDetails event){
        todayHebrewDateModel = DateUtil.convertGDateToHDate(event.getYear(), event.getMonth(), event.getDay());
        if (todayHebrewDateModel != null) {
            hDay = todayHebrewDateModel.hd;
            dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
            HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
            monthWheel.setSeletion(dateModel.hm_ - 1);
            hMonth = dateModel.hm_;
        } else
            Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();
//
//        String strUrl = "http://www.hebcal.com/converter/?cfg=json&gy="
//                + today.get(Calendar.YEAR) + "&gm=" + (today.get(Calendar.MONTH) + 1) + "&gd=" + today.get(Calendar.DATE) + "&g2h=1&gs=on";
//        ServerManager.doDateConversionTodayDate(R.string.internet_connection_error_text, AddEventActivity.this, strUrl, new JsonHttpResponseHandler() {
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                progressDialog.hide();
//                Toast.makeText(AddEventActivity.this, "something went wrong try again", Toast.LENGTH_SHORT).show();
//            }
//
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                progressDialog.hide();
//                String strResponse = response.toString();
//                Gson gson = new Gson();
//                todayHebrewDateModel = gson.fromJson(strResponse, HebrewDateModel.class);
//                if (todayHebrewDateModel != null) {
//                    hDay = todayHebrewDateModel.hd;
//                    dayWheel.setSeletion(todayHebrewDateModel.getHd() - 1);
//                    HebrewDateModel dateModel = DateUtil.convertGDateToHDate(todayHebrewDateModel.gy, todayHebrewDateModel.gm, todayHebrewDateModel.gd);
//                    monthWheel.setSeletion(dateModel.hm_ - 1);
//                    hMonth = dateModel.hm_;
//                } else
//                    Toast.makeText(AddEventActivity.this, "Wrong Date", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    @SuppressLint("SetTextI18n")
    private void checkIsEdit(){
        if (isEdit){
            addBtn.setText("Update");
            nameEt.setText(eventDetails.getSubjectTitle());
            String description = eventDetails.getSubjectDescription();
            description = description.replace("<h1><span style=\"color: #ffffff;\">", "");
            description = description.replace("</span></h1> <h2><span style=\"color: #ffffff;\">", "");
            description = description.replace("</span></h2>", "");
            detailEt.setText(description);
            images.addAll(eventDetails.getImageList());
            if (images.size() > 0){
                for (String image : images){
                    setImage(Uri.parse(image));
                }
            }

            getDateOfEditableEvent(eventDetails);

        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        isEdit = false;
//        addBtn.setText("Add");
//        nameEt.setText("");
//        detailEt.setText("");
//        images.clear();
//    }

    private void setOnClick() {

        crosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventActivity.this, CalendarViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
//                hideKeyboard();
//                onBackPressed();
//                overridePendingTransition(0, 0);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventActivity.this, CalendarViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
//                hideKeyboard();
//                onBackPressed();
//                overridePendingTransition(0, 0);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = nameEt.getText().toString();
                desc = detailEt.getText().toString();
                if (title.isEmpty() || desc.isEmpty()) {
                    AddEventDialog dialog = new AddEventDialog(AddEventActivity.this);
                    dialog.title.setText("    Incomplete    ");
                    dialog.message.setText("Please Fill all Fields before adding.");
                    dialog.ok.setText("OK!");
                    dialog.setCancelable(false);
                    dialog.ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else if (isEdit){
                    updateEvent();
                } else {
                    if (todayHebrewDateModel == null) {
                        todayHebrewDateModel = DateUtil.convertHDateToGDate(Utility.H_YEAR, hMonth, hDay);
                        if (todayHebrewDateModel == null)
                            Toast.makeText(AddEventActivity.this, "Invalid Hebrew Date", Toast.LENGTH_SHORT).show();
                        else
                            addEvent();
                    } else {
                        addEvent();
                    }
                }
                hideKeyboard();
//                if (isConvert){
//                    isConvert = false;
////                    hideKeyboard();
//
//                } else {
//                    Toast.makeText(AddEventActivity.this, "Please select date, by clicking on convert", Toast.LENGTH_LONG).show();
//                }
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hideKeyboard();
                if (ContextCompat.checkSelfPermission(AddEventActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA}, 10);
                    }
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            photoURI = FileProvider.getUriForFile(AddEventActivity.this, "com.saqibdb.YahrtzeitsOfGedolim.provider", photoFile);
                        } else {
                            photoURI = Uri.fromFile(photoFile);
                        }
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
                    }
                }
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hideKeyboard();
                if (ContextCompat.checkSelfPermission(AddEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 19);
                    }
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), GALLERY_IMAGE_REQUEST);
                }
            }
        });
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hideKeyboard();
                dateDialog.show();
                //datePickerDialog.show();
            }
        });
        findViewById(R.id.detailLayout).setOnClickListener(v -> {
            detailEt.requestFocus();
            detailEt.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(detailEt, InputMethodManager.SHOW_FORCED);

        });
    }

    private void updateEvent(){
        int sec = Calendar.getInstance().get(Calendar.SECOND);
        EventDetails tempEventDetails;
        tempEventDetails = eventDetails;

        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
        tempEventDetails.setSubjectTitle(title);
        String descInHtml = "<h1><span style=\"color: #ffffff;\">" + title + "</span></h1> <h2><span style=\"color: #ffffff;\">" + desc + "</span></h2>";
        tempEventDetails.setSubjectDescription(descInHtml);
        tempEventDetails.setModifiedDate("" + cYear + "/" + cMonth + "/" + cDay);

        tempEventDetails.setDay(todayHebrewDateModel.getGd());
        tempEventDetails.setMonth(todayHebrewDateModel.getGm());
        tempEventDetails.setYear(todayHebrewDateModel.getGy());
        tempEventDetails.setDayHebrew(todayHebrewDateModel.getHd().toString());
        tempEventDetails.setMonthHebrew("" + todayHebrewDateModel.getHm_());
        tempEventDetails.setYearHebrew(todayHebrewDateModel.getHy().toString());
        tempEventDetails.setDayHebrewStr(todayHebrewDateModel.getHebrew());
        tempEventDetails.setMonthHebrewStr(todayHebrewDateModel.getHm());
        tempEventDetails.setYearHebrew(todayHebrewDateModel.getHy().toString());


        tempEventDetails.setImageList(images);

        ArrayList<String> list = new ArrayList();
        for (EditText extra : extras) {
            list.add(extra.getText().toString());
        }
        tempEventDetails.setExtras(list);
        tempEventDetails.setDateFull(tempEventDetails.getYear().toString());
        if (tempEventDetails.getMonth().toString().length() == 1)
            tempEventDetails.setDateFull(tempEventDetails.getDateFull() + "0" + tempEventDetails.getMonth());
        else
            tempEventDetails.setDateFull(tempEventDetails.getDateFull() + tempEventDetails.getMonth());
        if (tempEventDetails.getDay().toString().length() == 1)
            tempEventDetails.setDateFull(tempEventDetails.getDateFull() + "0" + tempEventDetails.getDay());
        else
            tempEventDetails.setDateFull(tempEventDetails.getDateFull() + tempEventDetails.getDay());

        if (databaseHandler == null)
            databaseHandler = new DatabaseHandler(AddEventActivity.this);
        int status = databaseHandler.updatePrivateEvent(tempEventDetails);

        if (status == 1) {
            UpdateEventDialog updateEventDialog = new UpdateEventDialog(this);
            updateEventDialog.ok.setOnClickListener(v1 -> {
                startActivity(new Intent(AddEventActivity.this, CalendarViewActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
            updateEventDialog.setOnDismissListener(dialog -> {
                startActivity(new Intent(AddEventActivity.this, CalendarViewActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });

            setLocalNotification(tempEventDetails);

            updateEventDialog.show();
        }

    }

    private void addEvent() {
        int sec = Calendar.getInstance().get(Calendar.SECOND);
        EventDetails eventDetails = new EventDetails();
        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
        eventDetails.setSubjectTitle(title);
        String descInHtml = "<h1><span style=\"color: #ffffff;\">" + title + "</span></h1> <h2><span style=\"color: #ffffff;\">" + desc + "</span></h2>";
        eventDetails.setSubjectDescription(descInHtml);
        eventDetails.setDay(todayHebrewDateModel.getGd());
        eventDetails.setMonth(todayHebrewDateModel.getGm());
        eventDetails.setYear(todayHebrewDateModel.getGy());
        eventDetails.setDayHebrew(todayHebrewDateModel.getHd().toString());
        eventDetails.setMonthHebrew("" + todayHebrewDateModel.getHm_());
        eventDetails.setYearHebrew(todayHebrewDateModel.getHy().toString());
        eventDetails.setDayHebrewStr(todayHebrewDateModel.getHebrew());
        eventDetails.setMonthHebrewStr(todayHebrewDateModel.getHm());
        eventDetails.setYearHebrew(todayHebrewDateModel.getHy().toString());
        eventDetails.setCreatedDate("" + cYear + "/" + cMonth + "/" + cDay);
        eventDetails.setModifiedDate("" + cYear + "/" + cMonth + "/" + cDay);
        eventDetails.setPrivate(true);
        eventDetails.setImageList(images);

        ArrayList<String> list = new ArrayList();
        for (EditText extra : extras) {
            list.add(extra.getText().toString());
        }
        eventDetails.setExtras(list);
        eventDetails.setDateFull(eventDetails.getYear().toString());
        if (eventDetails.getMonth().toString().length() == 1)
            eventDetails.setDateFull(eventDetails.getDateFull() + "0" + eventDetails.getMonth());
        else
            eventDetails.setDateFull(eventDetails.getDateFull() + eventDetails.getMonth());
        if (eventDetails.getDay().toString().length() == 1)
            eventDetails.setDateFull(eventDetails.getDateFull() + "0" + eventDetails.getDay());
        else
            eventDetails.setDateFull(eventDetails.getDateFull() + eventDetails.getDay());

        eventDetails.setId(Integer.parseInt(eventDetails.getDateFull().substring(4) + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND)));
        Log.e("tedddd", eventDetails.getId().toString());
        setLocalNotification(eventDetails);
        Long rows = databaseHandler.addEvent(eventDetails, 1);
        Log.e("rows", rows.toString() + todayHebrewDateModel.hm + todayHebrewDateModel.hd);
        AddEventDialog dialog = new AddEventDialog(AddEventActivity.this);
        dialog.title.setText("     Success     ");
        dialog.message.setText("  Congratulations!   The Yartzeit has been added successfully.");
        dialog.ok.setText("Close");
        dialog.setCancelable(false);
        dialog.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(AddEventActivity.this, CalendarViewActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        dialog.show();
    }

    private File createImageFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "yahrtzeits_of_gedlim");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator
                + System.currentTimeMillis() + ".jpg");
    }

    private void inItView() {
        dayWheel = (WheelView) findViewById(R.id.datePicker);
        monthWheel = (WheelView) findViewById(R.id.datePicker2);
        convertBtn = findViewById(R.id.convertBtn);
        detailEt = findViewById(R.id.detailEt);
        galleryBtn = findViewById(R.id.galleryBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        addBtn = findViewById(R.id.addBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        crosBtn = findViewById(R.id.crosBtn);
        imageView = findViewById(R.id.imageView);
        nameEt = findViewById(R.id.nameEt);
        editNameTv = findViewById(R.id.editNameTv);
        nameLo = findViewById(R.id.nameLo);
        nameEt.clearFocus();

        dayWheel.setOffset(3);
        ArrayList<String> dayList = new ArrayList();
        dayList.add("1");
        dayList.add("2");
        dayList.add("3");
        dayList.add("4");
        dayList.add("5");
        dayList.add("6");
        dayList.add("7");
        dayList.add("8");
        dayList.add("9");
        dayList.add("10");
        dayList.add("11");
        dayList.add("12");
        dayList.add("13");
        dayList.add("14");
        dayList.add("15");
        dayList.add("16");
        dayList.add("17");
        dayList.add("18");
        dayList.add("19");
        dayList.add("20");
        dayList.add("21");
        dayList.add("22");
        dayList.add("23");
        dayList.add("24");
        dayList.add("25");
        dayList.add("26");
        dayList.add("27");
        dayList.add("28");
        dayList.add("29");
        dayList.add("30");
        dayWheel.setItems(dayList);
        dayWheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                hDay = Integer.parseInt(item);
                todayHebrewDateModel = null;
            }
        });
        dayWheel.setSeletion(3);

        monthWheel.setOffset(3);
        ArrayList<String> monthList = new ArrayList();
        for (String s : Utility.arrHebrewMonth) {
            monthList.add(s);
        }

        monthWheel.setItems(monthList);
        monthWheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                for (int k = 0; k < Utility.arrHebrewMonth.length; k++) {
                    if (Utility.arrHebrewMonth[k].equals(item)) {
                        hMonth = k + 1;
                        break;
                    }
                }
                todayHebrewDateModel = null;

            }
        });
        monthWheel.setSeletion(3);
        calendar = Calendar.getInstance();
    }

    private void setLocalNotification(EventDetails event) {
        if (event == null || event.getMonth() == null || event.getSubjectTitle() == null || event.getDay() == null)
            return;

        if (event.getYear() > calendar.get(Calendar.YEAR)) ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() > calendar.get(Calendar.MONTH) + 1)
            ;
        else if (event.getYear() == calendar.get(Calendar.YEAR) && event.getMonth() == calendar.get(Calendar.MONTH) + 1
                && event.getDay() > calendar.get(Calendar.DAY_OF_MONTH)) ;
        else return;

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, BroadcastManager.class);
        notificationIntent.putExtra("msg", event.getSubjectTitle());
        notificationIntent.putExtra("date", event.getDateFull());
        notificationIntent.putExtra("id", event.getId());

        PendingIntent broadcast = PendingIntent.getBroadcast(this, event.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        calendar.set(Calendar.YEAR, event.getYear());
        calendar.set(Calendar.MONTH, event.getMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, event.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 3);
        calendar.set(Calendar.SECOND, 1);

        Log.e("eventTesting444: " + event.getDateFull(), event.getSubjectTitle());
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            setImage(photoURI);
            images.add(photoURI.toString());
        } else if (requestCode == GALLERY_IMAGE_REQUEST && data != null) {
            setImage(data.getData());
            File file = copyFileFromUri(data.getData());
            if (file != null) {
                Uri uri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    uri = FileProvider.getUriForFile(AddEventActivity.this, "com.saqibdb.YahrtzeitsOfGedolim.provider", file);
                } else {
                    uri = Uri.fromFile(photoFile);
                }
                images.add(uri.toString());
            }
            ;
        }
    }

    private void setImage(Uri uri) {
        ImageView imageView = new ImageView(this);
        imageView.setPadding(4, 4, 4, 4);
        Glide.with(this).load(uri).into(imageView);
        ((LinearLayout) findViewById(R.id.detailLayout)).addView(imageView);

        EditText editText = new EditText(this);
        editText.setPadding(8, 8, 8, 8);
        editText.setBackgroundResource(R.color.color_grey_dark);
        editText.setTextSize(16);
        editText.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        extras.add(editText);
        ((LinearLayout) findViewById(R.id.detailLayout)).addView(editText);
    }

    public File copyFileFromUri(Uri fileUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File outputFile;
        try {
            ContentResolver content = this.getContentResolver();
            inputStream = content.openInputStream(fileUri);
            outputFile = createImageFile();
            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, buffer.length);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        return outputFile;
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    public void edit(View view) {
        detailEt.requestFocus();
    }


}
