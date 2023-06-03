package com.saqibdb.YahrtzeitsOfGedolim.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.saqibdb.YahrtzeitsOfGedolim.AppTextView;
import com.saqibdb.YahrtzeitsOfGedolim.BroadcastManager;
import com.saqibdb.YahrtzeitsOfGedolim.BuildConfig;
import com.saqibdb.YahrtzeitsOfGedolim.ConfirmationDeleteEventDialog;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.DeleteEventDialog;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.helper.PicassoImageGetter;
import com.saqibdb.YahrtzeitsOfGedolim.helper.SharedPreferencesHelper;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvEngMonth, tvEngDay, tvHebrewMonth, tvHebrewDay;
    DatabaseHandler dbHandler;
    HebrewDateModel todayHebrewDateModel;
    boolean isVisible = false;
    EventDetails eventDetails;
    Calendar calendar;

    AppTextView textView;

    int position;

    TickSeekBar seekbarFontSize;
    private int fontSize = 17;

    public EventDetailActivity() {
    }

    EventDetailActivity(EventDetails eventDetails, int position) {
        this.eventDetails = eventDetails;
        this.position = position;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        eventDetails = (EventDetails) getIntent().getSerializableExtra("eventDetail");
        findViewById(R.id.ivClose).setOnClickListener(this);
        findViewById(R.id.ivShare).setOnClickListener(this);
        seekbarFontSize = findViewById(R.id.mSeekbarFontSize);
        fontSize = SharedPreferencesHelper.getInstance().getInt(Constants.FONT_SIZE, 17);
        seekbarFontSize.setProgress(fontSize);

        ImageView deleteView = findViewById(R.id.ivDelete);
        ImageView EditView = findViewById(R.id.ivEdit);

        if (eventDetails.isPrivate()) {
            deleteView.setVisibility(View.VISIBLE);
            deleteView.setOnClickListener(this);

            EditView.setVisibility(View.VISIBLE);
            EditView.setOnClickListener(this);

        } else {
            deleteView.setVisibility(View.GONE);
            EditView.setVisibility(View.GONE);
        }


        textView = findViewById(R.id.tvEventDiscription);
        String html = eventDetails.getSubjectDescription();
        html = html.replace("<h2>", "<h5>");
        html = html.replace("</h2>", "</h5>");
        html = html.replace("<li>", " <li> ");
        Log.e("htmls", html);
        PicassoImageGetter imageGetter = new PicassoImageGetter(textView);
        Spannable html2;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html2 = (Spannable) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html2 = (Spannable) Html.fromHtml(html, imageGetter, null);
        }
        textView.setText(html2);
        textView.setTextSize(fontSize);

        /*Spanned fromHtml = HtmlCompat.fromHtml(this, html, 0);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(fromHtml);
        */
        LinearLayout descLayout = findViewById(R.id.descLayout);

        if (eventDetails.getImageList() != null) {
            int count = 0;
            for (String uri : eventDetails.getImageList()) {

                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(32, 8, 32, 16);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                Glide.with(this).load(uri).into(imageView);
                descLayout.addView(imageView);


                TextView extraView = new TextView(this);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                //params.setMargins(32, 16, 32, 16);
                extraView.setLayoutParams(params2);
                extraView.setPadding(16, 4, 16, 4);
                extraView.setTypeface(Typeface.SERIF);
                extraView.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                extraView.setTextSize(fontSize);
                Spanned fromHtml = HtmlCompat.fromHtml("<h5>" + eventDetails.getExtras().get(count) + "</h5>", 0);
                extraView.setMovementMethod(LinkMovementMethod.getInstance());
                extraView.setText(fromHtml);
                descLayout.addView(extraView);

                count++;
            }
        }

        seekbarFontSize.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                fontSize = seekParams.progress;
                SharedPreferencesHelper.getInstance().setInt(Constants.FONT_SIZE, fontSize);
                textView.setTextSize(fontSize);
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar tickSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar tickSeekBar) {

            }
        });

        if (eventDetails.getYear() == null) {
            doConversionTodayDate(eventDetails);
        } else {
            initDisplayData(eventDetails);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fontSize = SharedPreferencesHelper.getInstance().getInt(Constants.FONT_SIZE, 17);
        seekbarFontSize.setProgress(fontSize);
        textView.setTextSize(fontSize);
    }

    public void initDisplayData(EventDetails eventDetails) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(eventDetails.getYear(), eventDetails.getMonth() - 1, eventDetails.getDay());
        tvEngMonth = (TextView) findViewById(R.id.tvEngMonth);
        tvEngDay = (TextView) findViewById(R.id.tvEngDay);
        tvHebrewMonth = (TextView) findViewById(R.id.tvHebrewMonth2);
        tvHebrewDay = (TextView) findViewById(R.id.tvHebrewDay);

        if (eventDetails != null) {
            Date date = new Date(calendar.getTimeInMillis());
            SimpleDateFormat fmt = new SimpleDateFormat("MMM");
            String month = fmt.format(date);
            tvEngMonth.setText(month);
            tvEngDay.setText("" + eventDetails.getDay());
            tvHebrewMonth.setText("" + eventDetails.getMonthHebrewStr());
            tvHebrewDay.setText("" + eventDetails.getDayHebrew());
        }
    }

    private void doConversionTodayDate(final EventDetails eventDetails) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        todayHebrewDateModel = DateUtil.convertGDateToHDate(year, month, day);
        new GetSelectedHebrewDateToEnglish(eventDetails).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEdit:
                Intent intent = new Intent(EventDetailActivity.this, AddEventActivity.class);
                intent.putExtra("eventDetail", (Serializable) eventDetails);
                intent.putExtra(Constants.IS_EDIT, true);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.ivClose:
                onBackPressed();
                break;
            case R.id.ivDelete:
                ConfirmationDeleteEventDialog confirmationDialog = new ConfirmationDeleteEventDialog(EventDetailActivity.this);
                confirmationDialog.dialogCancel.setOnClickListener(v1 -> {
                    confirmationDialog.dismiss();
                });

                confirmationDialog.dialogDelete.setOnClickListener(v2 -> {
                    confirmationDialog.dismiss();
                    if (dbHandler == null)
                        dbHandler = new DatabaseHandler(EventDetailActivity.this);
                    if (dbHandler.deleteEvent(eventDetails.getId().toString())) {
                        DeleteEventDialog deleteEventDialog = new DeleteEventDialog(EventDetailActivity.this);
                        deleteEventDialog.ok.setOnClickListener(v3 -> {
                            startActivity(new Intent(EventDetailActivity.this, CalendarViewActivity.class));
                            EventDetailActivity.this.overridePendingTransition(0, 0);
                        });
                        deleteEventDialog.setOnDismissListener(dialog -> {
                            startActivity(new Intent(EventDetailActivity.this, CalendarViewActivity.class));
                            EventDetailActivity.this.overridePendingTransition(0, 0);
                        });

                        AlarmManager alarmManager = (AlarmManager) EventDetailActivity.this.getSystemService(EventDetailActivity.this.ALARM_SERVICE);
                        Intent notificationIntent = new Intent(EventDetailActivity.this, BroadcastManager.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(EventDetailActivity.this, eventDetails.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);

                        deleteEventDialog.show();
                    }
                });
                confirmationDialog.show();

                break;
            case R.id.ivShare:
                shareByText();
                break;
        }
    }

    private void shareByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, eventDetails.getSubjectTitle());

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, removeTags(eventDetails.getSubjectDescription()) + " Yartzeit : " + eventDetails.getDayHebrewStr() + "\nInfo from the Gedolim Yartzeit App. \n Get it here: \n iOS Download = https://itunes.apple.com/us/app/gedolim-yartzeits/id1439800530?ls=1&mt=8  \n Android Download = https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void shareByText() {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra("address", "");
        sendIntent.putExtra(Intent.EXTRA_TEXT, removeTags(eventDetails.getSubjectDescription()) + " Yartzeit : " + eventDetails.getDayHebrewStr() + "\nInfo from the Gedolim Yartzeit App. \n Get it here: \n iOS Download = https://itunes.apple.com/us/app/gedolim-yartzeits/id1439800530?ls=1&mt=8 \n Android Download = https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "..."));

//        final Intent intent = new Intent(Intent.ACTION_VIEW);
//        new Intent(Intent.ACTION_VIEW);
//        intent.putExtra("address", "");
//        intent.putExtra("sms_body", removeTags(eventDetails.getSubjectDescription()) + " Yartzeit : " + eventDetails.getDayHebrewStr() + "\nInfo from the Gedolim Yartzeit App. \n Get it here: \n iOS Download = https://itunes.apple.com/us/app/gedolim-yartzeits/id1439800530?ls=1&mt=8 \n Android Download = https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
//        intent.setData(Uri.parse("smsto:"));
//        startActivity(intent);
    }

    private String removeTags(String subjectDescription) {
        String desc[] = subjectDescription.split("</h1>");
        String s = "";
        if (desc.length == 2) {
            char[] chars = desc[1].toCharArray();
            boolean isTag = false;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '<') {
                    isTag = true;
                    continue;
                } else if (chars[i] == '>') {
                    isTag = false;
                    continue;
                }
                if (!isTag)
                    s = s + chars[i];
            }
        } else {
            char[] chars = desc[1].toCharArray();
            boolean isTag = false;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '<') {
                    isTag = true;
                    continue;
                } else if (chars[i] == '>') {
                    isTag = false;
                    continue;
                }
                if (!isTag)
                    s = s + chars[i];
            }
        }
        s = s.replace("&nbsp", "");
        return s;
    }

    private class GetSelectedHebrewDateToEnglish extends AsyncTask<Void, HebrewDateModel, HebrewDateModel> {

        private EventDetails event;

        public GetSelectedHebrewDateToEnglish(EventDetails event) {
            this.event = event;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HebrewDateModel doInBackground(Void... params) {
            return DateUtil.convertHDateToGDate(todayHebrewDateModel.getHy(), event.getMonth(), event.getDay());
        }

        @Override
        protected void onPostExecute(HebrewDateModel hebrewDateModel) {
            super.onPostExecute(hebrewDateModel);
            if (hebrewDateModel != null) {
                String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
                event.setDay(hebrewDateModel.getGd());
                event.setMonth(hebrewDateModel.getGm());
                event.setYear(hebrewDateModel.getGy());
                event.setDayHebrewStr("" + arrStr[0].trim());
                event.setDayHebrew("" + hebrewDateModel.getHd());
                event.setMonthHebrew("" + hebrewDateModel.getHm_());
                event.setMonthHebrewStr("" + hebrewDateModel.getHm());
                event.setYearHebrew("" + hebrewDateModel.getHy());
                eventDetails = event;
                initDisplayData(event);

                if (dbHandler == null)
                    dbHandler = new DatabaseHandler(EventDetailActivity.this);
                dbHandler.updateEventCompleted(event);
            }
        }
    }
}
