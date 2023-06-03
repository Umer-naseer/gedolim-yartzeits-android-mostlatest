
package com.saqibdb.YahrtzeitsOfGedolim.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetEvent {

    @SerializedName("Dates")
    @Expose
    private List<EventDetails> dates = null;
    @SerializedName("Total")
    @Expose
    private Integer total;
    @SerializedName("NextPage")
    @Expose
    private Boolean nextPage;

    public List<EventDetails> getEventDetails() {
        return dates;
    }

    public void setEventDetails(List<EventDetails> dates) {
        this.dates = dates;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getNextPage() {
        return nextPage;
    }

    public void setNextPage(Boolean nextPage) {
        this.nextPage = nextPage;
    }

}