package com.xperiasola.philubiq64wi.navigationdrawerapp.models;

import java.sql.Timestamp;

/**
 * Created by philUbiq64wi on 2/1/2018.
 */

public class InvitationModel {


    private String id;
    private String fk_user_id;
    private String ticket_id;
    private String status;
    private Timestamp time_stamp;


    private String place;
    private String people;
    private String pending;
    private String address;


    private String accepted;
    private String rejected;
    private String sender;
    private String senderId;


    private String eventTitle;
    private String eventDate;
    private String eventStart;
    private String eventEnd;
    private String eventRemarks;
    private Boolean isEventAdvance;
    private String placeName;

    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
    }


    public InvitationModel(String people, String status, String id, String fk_user_id) {
        this.people = people;
        this.status = status;
        this.id = id;
        this.fk_user_id = fk_user_id;
    }


    // TODO Remove this when final release
    //** Call by fetchInvitations created inside DatabaseHelper
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place, String people, String pending, String accepted, String rejected, String sender, String senderId) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;

    }

    /*// TODO: Testing. create object. my back up constructor
    // Call by fetchInvitations created inside DatabaseHelper
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place, String people, String pending, String accepted, String rejected, String sender, String senderId, String address) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;
        this.address = address;

    }*/

    // TODO: Testing version 2/28/2018
    //** Call by fetchInvitations created inside DatabaseHelper
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place, String people, String pending, String accepted, String rejected, String sender, String senderId, String address) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;
        this.address = address;

    }

    // TODO: Testing version 3/6/2018
    //** Call by fetchInvitations created inside DatabaseHelper
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place, String people,
                           String pending, String accepted, String rejected, String sender, String senderId, String address, String eventTitle) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;
        this.address = address;
        this.eventTitle = eventTitle;

    }


    // TODO: Testing version 3/19/2018
    // Constructor for for invitation sent
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place,
                           String people, String pending, String accepted, String rejected, String sender,
                           String senderId, String address, String eventTitle, String eventDate,
                           String eventStart, String eventEnd, String eventRemarks,
                           Boolean isEventAdvance, String placeName) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;
        this.address = address;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventRemarks = eventRemarks;
        this.isEventAdvance = isEventAdvance;
        this.placeName = placeName;


    }


    // TODO: Testing version 3/7/2018
    // Constructor for for invitation receive
    public InvitationModel(String id, String fk_user_id, String ticket_id, String status, String place,
                           String people, String pending, String accepted, String rejected, String sender,
                           String senderId, String address, String eventTitle, String eventDate,
                           String eventStart, String eventEnd, String eventRemarks,
                           Boolean isEventAdvance) {
        this.id = id;
        this.fk_user_id = fk_user_id;
        this.ticket_id = ticket_id;
        this.status = status;
        this.place = place;
        this.people = people;
        this.pending = pending;
        this.accepted = accepted;
        this.rejected = rejected;
        this.sender = sender;
        this.senderId = senderId;
        this.address = address;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventRemarks = eventRemarks;
        this.isEventAdvance = isEventAdvance;


    }


    public InvitationModel() {

    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getRejected() {
        return rejected;
    }

    public void setRejected(String rejected) {
        this.rejected = rejected;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(String fk_user_id) {
        this.fk_user_id = fk_user_id;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Timestamp time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Boolean getEventAdvance() {
        return isEventAdvance;
    }

    public void setEventAdvance(Boolean eventAdvance) {
        isEventAdvance = eventAdvance;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    @Override
    public String toString() {
        return fk_user_id;
    }

}
