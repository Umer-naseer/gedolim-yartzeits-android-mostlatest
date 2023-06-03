package com.saqibdb.YahrtzeitsOfGedolim.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.activity.CalendarViewActivity;
import com.saqibdb.YahrtzeitsOfGedolim.activity.EventsSlidePagerActivity;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import com.saqibdb.YahrtzeitsOfGedolim.model.HebrewDateModel;

import java.util.ArrayList;

public class CalendarEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<EventDetails> mItems;
    Activity activity;
    HebrewDateModel dateModel;

    public CalendarEventAdapter(Activity activity, HebrewDateModel dateModel, ArrayList<EventDetails> items) {
        this.dateModel = dateModel;
        this.activity = activity;
        this.mItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View viewData = inflater.inflate(R.layout.row_event_desc, viewGroup, false);
        return new SectionDataViewHolder(viewData);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SectionDataViewHolder vh2 = (SectionDataViewHolder) viewHolder;
        EventDetails eventDetails = mItems.get(position);

        vh2.getTxtTitle().setText(eventDetails.getSubjectTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            vh2.getTxtDiscription().setText(Html.fromHtml(eventDetails.getSubjectDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            vh2.getTxtDiscription().setText(Html.fromHtml(eventDetails.getSubjectDescription()));
        }

        if (eventDetails.isPrivate()){
            vh2.getPrivatEventsImage().setVisibility(View.VISIBLE);
        } else {
            vh2.getPrivatEventsImage().setVisibility(View.GONE);
        }

//        vh2.getTxtEngDate().setText("" + engDate);
        vh2.getTxtEngDate().setText(dateModel.getGd() + "");
        vh2.getTxtHebrewDate().setText(TextUtils.isEmpty(eventDetails.getDayHebrewStr()) ? "..." : eventDetails.getDayHebrewStr());
    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    public class SectionDataViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHebrewDate;
        private TextView tvEngDate;
        private TextView txtTitle;
        private TextView txtDiscription;
        private final ImageView privatEvents;

        public SectionDataViewHolder(View v) {
            super(v);
            tvHebrewDate = (TextView) v.findViewById(R.id.tvHebrewDate);
            tvEngDate = (TextView) v.findViewById(R.id.tvEngDate);
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtDiscription = (TextView) v.findViewById(R.id.txtDiscription);
            privatEvents = v.findViewById(R.id.privatEvents);



            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<EventDetails> temp = new ArrayList<EventDetails>(mItems);
                    for(EventDetails item: temp){
                        item.setDay(dateModel.getGd());
                        item.setMonth(dateModel.getGm());
                    }
                    Intent intent = new Intent(activity, EventsSlidePagerActivity.class);
                    intent.putParcelableArrayListExtra(Constants.EVENT_DETAILS_ARRAY, temp);
                    intent.putExtra(Constants.EVENT_DETAILS_POSITION, getAdapterPosition());

                    activity.startActivity(intent);
                    activity.finish();

//                    Intent intent = new Intent(activity, EventDetailActivity.class);
//                    EventDetails eventDetails=mItems.get(getAdapterPosition());
//                    eventDetails.setDay(dateModel.getGd());
//                    eventDetails.setMonth(dateModel.getGm());
//                    intent.putExtra("eventDetail", eventDetails);
//                    activity.startActivity(intent);
                }
            });

            privatEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((CalendarViewActivity) activity).showPrivateEventsOnly();
                }
            });

        }

        public TextView getTxtTitle() {
            return txtTitle;
        }

        public void setTxtTitle(TextView txtTitle) {
            this.txtTitle = txtTitle;
        }

        public TextView getTxtHebrewDate() {
            return tvHebrewDate;
        }

        public void setTxtHebrewDate(TextView tvHebrewDate) {
            this.tvHebrewDate = tvHebrewDate;
        }

        public TextView getTxtEngDate() {
            return tvEngDate;
        }

        public void setTxtEngDate(TextView tvEngDate) {
            this.tvEngDate = tvEngDate;
        }

        public TextView getTxtDiscription() {
            return txtDiscription;
        }

        public void setTxtDiscription(TextView txtDiscription) {
            this.txtDiscription = txtDiscription;
        }
        public ImageView getPrivatEventsImage(){
            return privatEvents;
        }
    }
}
