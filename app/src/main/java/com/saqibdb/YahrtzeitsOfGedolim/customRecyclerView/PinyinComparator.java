package com.saqibdb.YahrtzeitsOfGedolim.customRecyclerView;


import com.saqibdb.YahrtzeitsOfGedolim.model.EventDetails;

import java.util.Comparator;

/**
 * Created by dongjunkun on 2015/7/4.
 * <p>
 * 根据拼音来排列HeadListView中的数据
 */
public class PinyinComparator implements Comparator<EventDetails> {
    @Override
    public int compare(EventDetails c1, EventDetails c2) {

            return c1.getSubjectTitle().compareTo(c2.getSubjectTitle());

/*
        if (c1.getSortLetter().equals("@") || c2.getSortLetter().equals("#")) {
            return -1;
        } else if (c1.getSortLetter().equals("#") || c2.getSortLetter().equals("@")) {
            return 1;
        } else {
            return c1.getSortLetter().compareTo(c2.getSortLetter());
        }
*/
    }
}
