package com.saqibdb.YahrtzeitsOfGedolim.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.activity.CalendarViewActivity;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DateUtil;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;
import com.saqibdb.YahrtzeitsOfGedolim.network.AppRestClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 2018-08-15
    public static List<String> dayString;
    /**
     * calendar instance for previous month for getting complete view
     */
    int firstDay;
    // 2018-08-15
    public String curentDateString;
    DateFormat df;
    private CalendarViewActivity mContext;
    private java.util.Calendar month;
    private View previousView;
    int position = 0;


    public CalendarDateAdapter(CalendarViewActivity c, GregorianCalendar monthCalendar) {
        dayString = new ArrayList<>();
        //Locale.setDefault(Locale.US);
        month = monthCalendar;
        GregorianCalendar selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }

    public String refreshDays() {
        AppRestClient.cancelAllRequests();
        dayString.clear();
        Locale.setDefault(Locale.US);
        GregorianCalendar pmonth = (GregorianCalendar) month.clone(); // calendar instance for previous month
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        int maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        int mnthlength = maxWeeknumber * 7;
        int maxP = getMaxP(pmonth); // previous month maximum day 31,30....
        int calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        GregorianCalendar pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        String firstDateOfMonth = "", itemvalue;

        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            dayString.add(itemvalue);
            String[] date = itemvalue.split("-");
            if (date[date.length - 1].equalsIgnoreCase("01") || date[date.length - 1].equalsIgnoreCase("1")) {
                firstDateOfMonth = itemvalue;
            }
        }

        return firstDateOfMonth;
    }

    private int getMaxP(GregorianCalendar pmonth) {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        return maxP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View viewData = inflater.inflate(R.layout.calendar_item, viewGroup, false);
        return new SectionDataViewHolder(viewData);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SectionDataViewHolder vh2 = (SectionDataViewHolder) viewHolder;
        String[] separatedTime = dayString.get(position).split("-");
        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            // setting offdays to white color.
            vh2.getTxtHebrewDate().setTextColor(ContextCompat.getColor(mContext, R.color.yellow_50));
            vh2.getTxtEngDate().setTextColor(ContextCompat.getColor(mContext, R.color.color_white_50));

        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            vh2.getTxtHebrewDate().setTextColor(ContextCompat.getColor(mContext, R.color.yellow_50));
            vh2.getTxtEngDate().setTextColor(ContextCompat.getColor(mContext, R.color.color_white_50));

        } else {
            // setting curent month's days in blue color.
            vh2.getTxtHebrewDate().setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            vh2.getTxtEngDate().setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
        }

        if (dayString.get(position).equals(curentDateString)) {
            setSelected(vh2.getLinParent(), position);

        } else {
            vh2.getLinParent().setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_transprent));
        }

        // set Hebrew and English Date
        /*if (gridvalue.equals("1"))
            this.position=position;*/
        vh2.getTxtEngDate().setText(gridvalue);
        vh2.getTxtHebrewDate().setText("...");
        doDateConversion(vh2.getTxtHebrewDate(),
                Integer.parseInt(separatedTime[0]),
                Integer.parseInt(separatedTime[1]),
                Integer.parseInt(separatedTime[2]),
                position);
    }

    public void setSelected(View view, int layoutPos) {
        if (previousView != null) {
            ((TextView) previousView.findViewById(R.id.tvEngDate)).setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
            ((TextView) previousView.findViewById(R.id.tvHebrewDate)).setTextColor(ContextCompat.getColor(mContext, R.color.color_white));
            LinearLayout linearLayout = previousView.findViewById(R.id.linView);
            linearLayout.setBackgroundResource(R.drawable.selected_event_transparent_back);
        }
        previousView = view;

        ((TextView) view.findViewById(R.id.tvEngDate)).setTextColor(ContextCompat.getColor(mContext, R.color.red));
        ((TextView) view.findViewById(R.id.tvHebrewDate)).setTextColor(ContextCompat.getColor(mContext, R.color.color_black));
        LinearLayout linearLayout = previousView.findViewById(R.id.linView);
        linearLayout.setBackgroundResource(R.drawable.selected_event_white_back);
        ((CalendarViewActivity) mContext).getDisplaySelectedDateEventList(layoutPos, dayString.get(layoutPos));
    }

    @Override
    public int getItemCount() {
        return dayString.size();
    }

    public void doDateConversion(final TextView txtHebrewDate, int year, int month, int day, final int position) {
        HebrewDateModel hebrewDateModel = DateUtil.convertGDateToHDate(year, month, day);
        String[] arrStr = String.valueOf(hebrewDateModel.getHebrew()).split(" ");
        txtHebrewDate.setText(arrStr[0].trim());
    }


    public class SectionDataViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHebrewDate;
        private TextView tvEngDate;
        private LinearLayout linParent;

        public SectionDataViewHolder(View v) {
            super(v);
            tvHebrewDate = (TextView) v.findViewById(R.id.tvHebrewDate);
            tvEngDate = (TextView) v.findViewById(R.id.tvEngDate);
            linParent = (LinearLayout) v.findViewById(R.id.linParent);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curentDateString = dayString.get(getLayoutPosition());
                    setSelected(v, getLayoutPosition());
                }
            });
        }

        public TextView getTxtHebrewDate() {
            return tvHebrewDate;
        }

        public TextView getTxtEngDate() {
            return tvEngDate;
        }

        public LinearLayout getLinParent() {
            return linParent;
        }
    }
}
