package com.xperiasola.philubiq64wi.navigationdrawerapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by philUbiq64wi on 3/16/2018.
 */


// Class use Parcelable
public class EventInfoModel implements Parcelable {
    private String eventTitle;
    private String eventDate;
    private String eventStart;
    private String eventEnd;
    private String eventRemarks;
    private String place;
    private String placeName;
    private Boolean isAdvance;
    private int ticketId;
    private String fk_user_id;


    public EventInfoModel() {
    }


    public EventInfoModel(String fk_user_id) {
        this.fk_user_id = fk_user_id;
    }


    // Constructor for changing event info
    public EventInfoModel(String eventTitle, String eventDate, String eventStart, String eventEnd,
                          String eventRemarks, String place, Boolean isAdvance, String placeName,
                          int ticketId) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventRemarks = eventRemarks;
        this.place = place;
        this.isAdvance = isAdvance;
        this.placeName = placeName;
        this.ticketId = ticketId;
    }


//    // Constructor for changing event info
//    public EventInfoModel(EventInfoModel eventInfoModel) {
//        this.eventTitle = eventInfoModel.eventTitle;
//        this.eventDate = eventInfoModel.eventDate;
//        this.eventStart = eventInfoModel.eventStart;
//        this.eventEnd = eventInfoModel.eventEnd;
//        this.eventRemarks = eventInfoModel.eventRemarks;
//        this.place = eventInfoModel.place;
//        this.isAdvance = eventInfoModel.isAdvance;
//        this.placeName = eventInfoModel.placeName;
//
//    }

//    // Constructor for changing event address
//    public EventInfoModel(String placeName, String place) {
//        this.placeName = placeName;
//        this.place = place;
//    }


    protected EventInfoModel(Parcel in) {
        eventTitle = in.readString();
        eventDate = in.readString();
        eventStart = in.readString();
        eventEnd = in.readString();
        eventRemarks = in.readString();
        place = in.readString();
        byte tmpIsAdvance = in.readByte();
        isAdvance = tmpIsAdvance == 0 ? null : tmpIsAdvance == 1;
        placeName = in.readString();
        ticketId = in.readInt();
        fk_user_id = in.readString();
    }


    public static final Creator<EventInfoModel> CREATOR = new Creator<EventInfoModel>() {
        @Override
        public EventInfoModel createFromParcel(Parcel in) {
            return new EventInfoModel(in);
        }

        @Override
        public EventInfoModel[] newArray(int size) {
            return new EventInfoModel[size];
        }
    };

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getEventRemarks() {
        return eventRemarks;
    }

    public void setEventRemarks(String eventRemarks) {
        this.eventRemarks = eventRemarks;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Boolean getAdvance() {
        return isAdvance;
    }

    public void setAdvance(Boolean advance) {
        isAdvance = advance;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(String fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventTitle);
        dest.writeString(eventDate);
        dest.writeString(eventStart);
        dest.writeString(eventEnd);
        dest.writeString(eventRemarks);
        dest.writeString(place);
        dest.writeByte((byte) (isAdvance == null ? 0 : isAdvance ? 1 : 2));
        dest.writeString(placeName);
        dest.writeInt(ticketId);
        dest.writeString(fk_user_id);

    }
}
