package com.saqibdb.YahrtzeitsOfGedolim.customDatePicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saqibdb.YahrtzeitsOfGedolim.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DateTimePickerDialog extends DialogFragment {
    public static final String MONTH_KEY = "monthValue";
    public static final String DAY_KEY = "dayValue";
    public static final String YEAR_KEY = "yearValue";
    int monthVal = -1, dayVal = -1, yearVal = -1;
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> monthList = new ArrayList<>();
    ArrayList<String> dayList = new ArrayList<>();
    ArrayList<String> dayArbList = new ArrayList<>();
    String[] months;
    private DateTimeListener listener;
    private LoopView monthLoopView;
    private LoopView yearLoopView;
    private LoopView dayLoopView;
    private TextView textView_confirm, textView_cancel;
    private ImageView closeBtn;
    private int yearPos = 0;
    private int monthPos = 0;
    private int dayPos = 0;
    private int minYear;
    private int maxYear;
    private int viewTextSize;

    public static DateTimePickerDialog newInstance(int monthIndex, int daysIndex, String year, int yearIndex) {
        DateTimePickerDialog f = new DateTimePickerDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(MONTH_KEY, monthIndex);
        args.putInt(DAY_KEY, daysIndex);
        args.putInt(YEAR_KEY, yearIndex);
        f.setArguments(args);

        return f;
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    public static boolean isLeapYear2(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String format2LenStr(int num) {
        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getArguments();
        if (extras != null) {
            monthVal = extras.getInt(MONTH_KEY, -1);
            dayVal = extras.getInt(DAY_KEY, -1);
            yearVal = extras.getInt(YEAR_KEY, -1);
        }

        minYear = 5700; // Calendar.getInstance().get(Calendar.YEAR);
        viewTextSize = 18;
        maxYear = 5999; // Calendar.getInstance().get(Calendar.YEAR) + 20;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        months = getActivity().getResources().getStringArray(R.array.array_months);

        View dialog = inflater.inflate(R.layout.month_year_picker, null);
        monthLoopView = (LoopView) dialog.findViewById(R.id.picker_month);
        yearLoopView = (LoopView) dialog.findViewById(R.id.picker_year);
        dayLoopView = (LoopView) dialog.findViewById(R.id.picker_day);
        textView_confirm = (TextView) dialog.findViewById(R.id.textView_confirm);
        textView_cancel = (TextView) dialog.findViewById(R.id.textView_cancel);
        closeBtn = (ImageView) dialog.findViewById(R.id.closeBtn);

        yearLoopView.setNotLoop();
        monthLoopView.setNotLoop();
        dayLoopView.setNotLoop();

        //set loopview text btnTextsize
        yearLoopView.setTextSize(viewTextSize);
        monthLoopView.setTextSize(viewTextSize);
        dayLoopView.setTextSize(viewTextSize);

        //set checked listen
        yearLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                yearPos = item;
                initDayPickerView();
            }
        });
        monthLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                monthPos = item;
                initDayPickerView();
            }
        });
        dayLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                dayPos = item;
            }
        });

        initPickerViews(); // init year and month loop view
        initDayPickerView(); //init day loop view

        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog.this.getDialog().cancel();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog.this.getDialog().cancel();
            }
        });

        textView_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = yearPos;
                int month = monthPos + 1;
                int day = dayPos + 1;
                StringBuffer sb = new StringBuffer();

                sb.append(format2LenStr(day));
                sb.append("-");
                sb.append(format2LenStr(month));
                sb.append("-");
                sb.append(String.valueOf(year));

                String years = "";
                years = yearList.get(year);

                listener.setDate(day, month, years, yearPos);
                DateTimePickerDialog.this.getDialog().cancel();
            }
        });

        monthLoopView.setInitPosition(monthVal);
        dayLoopView.setInitPosition(dayVal);
        yearLoopView.setInitPosition(yearVal);

        yearPos = yearVal;
        monthPos = monthVal;
        dayPos = dayVal;

        builder.setView(dialog);
        return builder.create();
    }

    public void setListener(DateTimeListener dateTimeListener) {
        this.listener = dateTimeListener;
    }

    private void initPickerViews() {

        int yearCount = maxYear - minYear;

        for (int i = 0; i < yearCount; i++) {
            yearList.add(format2LenStr(minYear + i));
        }

        yearLoopView.setArrayList((ArrayList) yearList);

        for (int i = 0; i < months.length; i++) {
            String monthName = months[i];
            monthList.add(monthName);
        }

        yearLoopView.setArrayList((ArrayList) yearList);
        yearLoopView.setInitPosition(yearPos);

        monthLoopView.setArrayList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);
    }

    /**
     * Init day item
     */
    private void initDayPickerView() {

        int dayMaxInMonth;
        Calendar calendar = Calendar.getInstance();
        dayList = new ArrayList<String>();
        dayArbList = new ArrayList<String>();

        calendar.set(Calendar.YEAR, minYear + yearPos);
        calendar.set(Calendar.MONTH, monthPos);

        //get max day in month
        dayMaxInMonth = 30;//calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < dayMaxInMonth; i++) {
            dayList.add(format2LenStr(i + 1));
        }
        dayLoopView.setArrayList((ArrayList) dayList);

        dayLoopView.setInitPosition(dayPos);
    }
}
