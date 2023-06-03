package com.saqibdb.YahrtzeitsOfGedolim.customRecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.helper.Utility;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SearchNameEventAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<EventDetails> eventList;

    public SearchNameEventAdapter(List<EventDetails> eventList) {
        this.eventList = eventList;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeadViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (HeadViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_layout, null);
            viewHolder = new HeadViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.mHeadText.setText("" + eventList.get(position).getSubjectTitle().charAt(0));
        return convertView;
    }

    public void updateList(List<EventDetails> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int i) {
        return eventList.get(i).getSubjectTitle().charAt(0);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.tvEventTitle.setText(eventList.get(position).getSubjectTitle());
        viewHolder.tvEngDate.setText(eventList.get(position).getDayHebrew() != null
                ? eventList.get(position).getDayHebrew() + ""
                : eventList.get(position).getDay() + "");
        viewHolder.tvHebrewDate.setText(eventList.get(position).getMonthHebrewStr() != null
                ? eventList.get(position).getMonthHebrewStr() + ""
                : Utility.arrHebrewMonth[eventList.get(position).getMonth() - 1]);
        return convertView;
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = eventList.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    static class ViewHolder {
        TextView tvEventTitle, tvEngDate, tvHebrewDate;

        ViewHolder(View view) {
            tvEventTitle = (TextView) view.findViewById(R.id.tvEventTitle);
            tvEngDate = (TextView) view.findViewById(R.id.tvEngDate);
            tvHebrewDate = (TextView) view.findViewById(R.id.tvHebrewDate);
        }
    }

    static class HeadViewHolder {
        TextView mHeadText;

        HeadViewHolder(View view) {
            mHeadText = (TextView) view.findViewById(R.id.headText);
        }
    }
}
