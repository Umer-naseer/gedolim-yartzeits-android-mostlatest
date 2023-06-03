package com.saqibdb.YahrtzeitsOfGedolim.customRecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.activity.EventDetailActivity;
import com.saqibdb.YahrtzeitsOfGedolim.helper.DatabaseHandler;
import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SearchByNamesActivity extends AppCompatActivity implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener,
        AdapterView.OnItemClickListener, TextWatcher {

    private ClearEditText mClearEditText;
    private StickyListHeadersListView mStickListHeadersListView;
    private TextView mDialog;
    private SideBar mSideBar;
    private ProgressBar mProgressBar;
    private TextView tvEventNotFound;

    private SearchNameEventAdapter searchNameEventAdapter;
    private CharacterParser characterParser;
    private List<EventDetails> eventLists = new ArrayList<>();
    private PinyinComparator pinyinComparator;

    private boolean showPrivate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvEventNotFound = (TextView) findViewById(R.id.tvEventNotFound);
        tvEventNotFound.setVisibility(View.GONE);
        mClearEditText = (ClearEditText) findViewById(R.id.clearEditText);
        mStickListHeadersListView = (StickyListHeadersListView) findViewById(R.id.stickListHeadersListView);
        mDialog = (TextView) findViewById(R.id.dialog);
        mSideBar = (SideBar) findViewById(R.id.sideBar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.ivClose).setOnClickListener(this);

        showPrivate = this.getIntent().getBooleanExtra(Constants.PRIVATE_LIST_SHOW, false);

        initView();
        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }

    private void initView() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mStickListHeadersListView.setOnItemClickListener(this);
        searchNameEventAdapter = new SearchNameEventAdapter(eventLists);
        mStickListHeadersListView.setAdapter(searchNameEventAdapter);
        mClearEditText.addTextChangedListener(this);
    }

    /**
     * @param s
     */
    private void filterData(String s) {
        if (eventLists != null && eventLists.size() > 0) {
            List<EventDetails> filterDataList = new ArrayList<>();
            if (TextUtils.isEmpty(s)) {
                filterDataList = eventLists;
            } else {
                filterDataList.clear();
                for (EventDetails sortModel : eventLists) {
                    String name = sortModel.getSubjectTitle();
                    if (name.toLowerCase().contains(s.toString().toLowerCase())) {
                        filterDataList.add(sortModel);
                    }
                }
            }

            Collections.sort(filterDataList, pinyinComparator);
            searchNameEventAdapter.updateList(filterDataList);
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = searchNameEventAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mStickListHeadersListView.setSelection(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SearchByNamesActivity.this, EventDetailActivity.class);
        intent.putExtra("eventDetail", (Serializable) searchNameEventAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public class MyAsyncTask extends AsyncTask<Object, Integer, List<String>> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(Object... params) {
            try {
                DatabaseHandler dbHandler = new DatabaseHandler(SearchByNamesActivity.this);
                return dbHandler.getAllEventList();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            mProgressBar.setVisibility(View.GONE);
            if (list != null && list.size() > 0) {
                Gson gson = new Gson();
                for (String s : list) {

                    EventDetails event = gson.fromJson(s, EventDetails.class);

                    String[] strings = event.getSubjectTitle().split(" ");
                    String lastName = strings[strings.length-1];
                    lastName= lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();
                    String nameWithLastName = event.getSubjectTitle().replace(lastName, "");
                    event.setSubjectTitle(lastName +" "+ nameWithLastName);

                    String pinyin = characterParser.getSelling(event.getSubjectTitle());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    event.setSortLetter(sortString);

                    if (showPrivate){
                        if (event.isPrivate()){
                            eventLists.add(event);
                        }
                    } else {
                        eventLists.add(event);
                    }
                }
                Toast.makeText(SearchByNamesActivity.this, "List Size : " + eventLists.size(), Toast.LENGTH_SHORT).show();

/*
                for (EventDetails event : eventLists) {
                    String pinyin = characterParser.getSelling(event.getSubjectTitle());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    event.setSortLetter(sortString);
                }
*/
                Collections.sort(eventLists, pinyinComparator);
                searchNameEventAdapter.updateList(eventLists);
                mStickListHeadersListView.setVisibility(View.VISIBLE);
                mSideBar.setVisibility(View.VISIBLE);
            } else {
                tvEventNotFound.setVisibility(View.GONE);
                mStickListHeadersListView.setVisibility(View.GONE);
                mSideBar.setVisibility(View.GONE);
            }
        }
    }
}
