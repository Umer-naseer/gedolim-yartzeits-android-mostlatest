package com.saqibdb.YahrtzeitsOfGedolim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.fragment.EventDetailsFragment;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;
import java.util.ArrayList;


public class EventsSlidePagerActivity extends FragmentActivity {
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    ArrayList<EventDetails> eventsArray = new ArrayList<EventDetails>();
    int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_slide);
        getListOfEvents();

        // Instantiate a ViewPager and a PagerAdapter.

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
    }

    private void getListOfEvents(){
        startPosition = this.getIntent().getIntExtra(Constants.EVENT_DETAILS_POSITION, 0);
        final ArrayList<Parcelable> events =
                this.getIntent().getParcelableArrayListExtra(Constants.EVENT_DETAILS_ARRAY);
        if (events != null) {
            for (Parcelable parcelable : events) {
                eventsArray.add((EventDetails) parcelable);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() <= 1) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            Intent intent = new Intent(this, CalendarViewActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            EventDetailsFragment fragment = new EventDetailsFragment();
            Bundle bundle = new Bundle();
            if (position == 0) {
                bundle.putSerializable(Constants.EVENT_DETAILS, eventsArray.get(startPosition));
            } else {
                bundle.putSerializable(Constants.EVENT_DETAILS, eventsArray.get(position));
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return eventsArray.size();
        }
    }

}
