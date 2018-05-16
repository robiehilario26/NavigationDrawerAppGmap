package com.xperiasola.philubiq64wi.navigationdrawerapp.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xperiasola.philubiq64wi.navigationdrawerapp.dataholder.DataHolder;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.InvitationModel;
import com.xperiasola.philubiq64wi.navigationdrawerapp.models.UserModel;

import java.util.ArrayList;

/**
 * Created by philUbiq64wi on 1/26/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //** Login user database and table column
    public static final String DATABASE_NAME_LOGIN = "log_user.db";
    public static final String TABLE_NAME_LOGIN = "tbl_user_log";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_FULLNAME = "FULLNAME";
    public static final String COLUMN_AGE = "AGE";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_PASSWORD_HINT = "PASSWORD_HINT";

    //** Invitation table column
    public static final String TABLE_NAME_INVITATION = "tbl_user_invitation";
    public static final String COLUMN_INVITATION_ID = "ID";
    public static final String COLUMN_INVITATION_FK_ID = "FK_USER_ID";
    public static final String COLUMN_INVITATION_TICKET_ID = "TICKET_ID";
    public static final String COLUMN_INVITATION_SENDER_ID = "SENDER_ID";
    public static final String COLUMN_INVITATION_REQUEST_STATUS = "REQUEST_STATUS";
    public static final String COLUMN_INVITATION_TIMESTAMP = "TIME_STAMP";

    //** Invitation_ticket table column
    public static final String TABLE_NAME_TICKET = "tbl_invitation_ticket";
    public static final String COLUMN_TICKET_ID = "ID";
    public static final String COLUMN_TICKET_PLACE = "PLACE";
    public static final String COLUMN_TICKET_ADDRESS = "ADDRESS";
    public static final String COLUMN_TICKET_STATUS = "STATUS";
    public static final String COLUMN_TICKET_EVENT_TITLE = "EVENT_TITLE";
    public static final String COLUMN_TICKET_EVENT_DATE = "EVENT_DATE";
    public static final String COLUMN_TICKET_EVENT_START = "EVENT_START";
    public static final String COLUMN_TICKET_EVENT_END = "EVENT_END";
    public static final String COLUMN_TICKET_EVENT_REMARKS = "EVENT_REMARKS";
    public static final String COLUMN_TICKET_EVENT_ISADVANCE = "EVENT_ADVANCE";
    public static final String COLUMN_TICKET_TIMESTAMP = "TIME_STAMP";


    //** Database version is set to 1
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME_LOGIN, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        //** CREATE TABLE Login
        String CREATE_TABLE_LOGIN = "CREATE TABLE " + TABLE_NAME_LOGIN +
                " ( " + COLUMN_INVITATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_AGE + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PASSWORD_HINT + " TEXT )";


        //** CREATE TABLE Invitation
        String CREATE_TABLE_INVITATION = "CREATE TABLE " + TABLE_NAME_INVITATION +
                " ( " + COLUMN_INVITATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INVITATION_FK_ID + " TEXT, " +
                COLUMN_INVITATION_TICKET_ID + " INTEGER, " +
                COLUMN_INVITATION_REQUEST_STATUS + " TEXT, " +
                COLUMN_INVITATION_SENDER_ID + " TEXT, " +
                COLUMN_INVITATION_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";


        //** CREATE TABLE Invitation_ticket
        String CREATE_TABLE_INVITATION_TICKET = "CREATE TABLE " + TABLE_NAME_TICKET +
                " ( " + COLUMN_TICKET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TICKET_PLACE + " TEXT, " +
                COLUMN_TICKET_STATUS + " TEXT, " +
                COLUMN_TICKET_ADDRESS + " TEXT, " +
                COLUMN_TICKET_EVENT_TITLE + " TEXT, " +
                COLUMN_TICKET_EVENT_DATE + " BOOLEAN, " +
                COLUMN_TICKET_EVENT_START + " TEXT, " +
                COLUMN_TICKET_EVENT_END + " TEXT, " +
                COLUMN_TICKET_EVENT_REMARKS + " TEXT, " +
                COLUMN_TICKET_EVENT_ISADVANCE + " TEXT, " +
                COLUMN_TICKET_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";


        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN);
        sqLiteDatabase.execSQL(CREATE_TABLE_INVITATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_INVITATION_TICKET);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOGIN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_INVITATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TICKET);
        onCreate(sqLiteDatabase);
    }


    //** Check email register if existed in table_user_log
    public boolean checkEmail(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor queryResult = sqLiteDatabase.rawQuery("select * from tbl_user_log where EMAIL = '" + user.getUser_email() + "'", null);
        System.out.println("COUNT RESULT " + queryResult.getCount());
        if (queryResult.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    //** Select last data inserted from table invitation_ticket limit by 1
    public Cursor getLastInvitaionTicket() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor queryResult = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME_TICKET + " order by id desc limit 1", null);
        return queryResult;
    }


    //** Insert data in table tbl_user_log
    public boolean insertUserData(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, user.getUser_email());
        contentValues.put(COLUMN_FULLNAME, user.getUser_fullName());
        contentValues.put(COLUMN_AGE, user.getUser_age());
        contentValues.put(COLUMN_PASSWORD, user.getUser_password());
        contentValues.put(COLUMN_PASSWORD_HINT, user.getUser_hint());

        long isInserted = sqLiteDatabase.insert(TABLE_NAME_LOGIN, null, contentValues);
        if (isInserted == -1) {
            sqLiteDatabase.close();
            return false;
        } else {
            sqLiteDatabase.close();
            return true;
        }


    }

    // TODO: For testing
    //** Insert to table tbl_invitation_ticket
    public boolean insertInvitationTicketBeta(String place, String address, String eventTitle,
                                              String eventDate, String eventStart,
                                              String eventEnd, String eventRemarks,
                                              Boolean isAdvance) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TICKET_PLACE, place);
        contentValues.put(COLUMN_TICKET_ADDRESS, address);
        contentValues.put(COLUMN_TICKET_STATUS, "Active");
        contentValues.put(COLUMN_TICKET_EVENT_TITLE, eventTitle);
        contentValues.put(COLUMN_TICKET_EVENT_DATE, eventDate);
        contentValues.put(COLUMN_TICKET_EVENT_START, eventStart);
        contentValues.put(COLUMN_TICKET_EVENT_END, eventEnd);
        contentValues.put(COLUMN_TICKET_EVENT_REMARKS, eventRemarks);
        contentValues.put(COLUMN_TICKET_EVENT_ISADVANCE, isAdvance);


        long isInserted = sqLiteDatabase.insert(TABLE_NAME_TICKET, null, contentValues);
        if (isInserted == -1) {
            sqLiteDatabase.close();
            return false;
        } else {
            sqLiteDatabase.close();
            return true;
        }

    }


    //** Insert to table tbl_invitation_ticket
    public boolean insertInvitationTicket(String place, String address) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TICKET_PLACE, place);
        contentValues.put(COLUMN_TICKET_ADDRESS, address);
        contentValues.put(COLUMN_TICKET_STATUS, "Active");


        long isInserted = sqLiteDatabase.insert(TABLE_NAME_TICKET, null, contentValues);
        if (isInserted == -1) {
            return false;
        } else {
            return true;
        }

    }

    //** Insert all contact invited to table tbl_invitation
    public boolean insertInvitationData(String userId, String latestTicketId) {
        //* Set status default value to Pending for new invitation inserted
        String status = "Pending";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INVITATION_FK_ID, userId);
        contentValues.put(COLUMN_INVITATION_TICKET_ID, latestTicketId);
        contentValues.put(COLUMN_INVITATION_SENDER_ID, DataHolder.getData().toString());
        contentValues.put(COLUMN_INVITATION_REQUEST_STATUS, status);

        long isInserted = sqLiteDatabase.insert(TABLE_NAME_INVITATION, null, contentValues);
        if (isInserted == -1) {
            sqLiteDatabase.close();
            return false;
        } else {
            sqLiteDatabase.close();
            return true;
        }


    }


    //** Select all tbl_user_log data
    public Cursor listLoginUser() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorResult = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME_LOGIN, null);
        sqLiteDatabase.close();
        return cursorResult;

    }

    //** Update tbl_invitation data using UserModel Class
    public boolean updateInvitationData(InvitationModel invite) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INVITATION_REQUEST_STATUS, invite.getStatus());
        sqLiteDatabase.update(TABLE_NAME_INVITATION, contentValues, COLUMN_INVITATION_TICKET_ID + " = ? AND " + COLUMN_INVITATION_FK_ID + " = ?", new String[]{String.valueOf(invite.getTicket_id()), String.valueOf(invite.getFk_user_id())});
        sqLiteDatabase.close();
        return true;
    }

    //** Update tbl_ticket status for cancellation
    public boolean updateTicketStatus(String ticket_id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TICKET_STATUS, "Cancel");
        sqLiteDatabase.update(TABLE_NAME_TICKET, contentValues, COLUMN_TICKET_ID + " = ? ", new String[]{ticket_id});
        sqLiteDatabase.close();
        return true;
    }

    //Update tbl_ticket status for removing person in invitation list
    //Back here
    public boolean removePerson(String ticket_id) {

        try
        {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_INVITATION_REQUEST_STATUS, "Remove");
            sqLiteDatabase.update(TABLE_NAME_INVITATION, contentValues, COLUMN_INVITATION_ID + " = ? ", new String[]{ticket_id});
            sqLiteDatabase.close();
        }

        catch (Exception e){
            System.out.println("error "+ e.toString());
        }

        return true;
    }

    // Update tbl_ticket information
    // Back here
    public boolean updateEventInfo(InvitationModel invite) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TICKET_PLACE, invite.getPlaceName());
        contentValues.put(COLUMN_TICKET_ADDRESS, invite.getPlace());
        contentValues.put(COLUMN_TICKET_EVENT_ISADVANCE, invite.getEventAdvance());
        contentValues.put(COLUMN_TICKET_EVENT_TITLE, invite.getEventTitle());
        contentValues.put(COLUMN_TICKET_EVENT_DATE, invite.getEventDate());
        contentValues.put(COLUMN_TICKET_EVENT_START, invite.getEventStart());
        contentValues.put(COLUMN_TICKET_EVENT_END, invite.getEventEnd());
        contentValues.put(COLUMN_TICKET_EVENT_REMARKS, invite.getEventRemarks());

        sqLiteDatabase.update(TABLE_NAME_TICKET, contentValues, COLUMN_TICKET_ID + " = ? ", new String[]{String.valueOf(invite.getTicket_id())});
        sqLiteDatabase.close();
        return true;
    }


    //** Update tbl_user_log data using UserModel Class
    public boolean updateLoginUser(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, user.getUser_email());
        contentValues.put(COLUMN_FULLNAME, user.getUser_fullName());
        contentValues.put(COLUMN_AGE, user.getUser_age());
        contentValues.put(COLUMN_PASSWORD, user.getUser_password());
        contentValues.put(COLUMN_PASSWORD_HINT, user.getUser_hint());
        sqLiteDatabase.update(TABLE_NAME_LOGIN, contentValues, "" + COLUMN_ID + " = ?", new String[]{String.valueOf(user.getUser_id())});
        sqLiteDatabase.close();
        return true;
    }


    //** Select user email and password
    public Cursor loginUser(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursoLoginResult = sqLiteDatabase.rawQuery("select * from tbl_user_log " +
                "where EMAIL = '" + user.getUser_email() + "' AND PASSWORD = '" + user.getUser_password() + "'", null);
        return cursoLoginResult;


    }

    // Fetch password hint
    public Cursor fetchPasswordHint(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursoLoginResult = sqLiteDatabase.rawQuery("select * from tbl_user_log " +
                "where EMAIL = '" + user.getUser_email() + "' ", null);
        return cursoLoginResult;
    }


    //** Fetch user data
    public Cursor loadPersonalInfo(UserModel user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorInfoResult = sqLiteDatabase.rawQuery("select * from tbl_user_log where ID = '" + user.getUser_id() + "'", null);
        return cursorInfoResult;

    }


    //** Fetch all User that registered except your own userId
    public ArrayList<UserModel> fetchContacts(String id) {
        String query = "SELECT * FROM " + TABLE_NAME_LOGIN + " WHERE NOT id = '" + id + "' ORDER BY ID";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<UserModel> contactList = new ArrayList<>();

        //** while cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                UserModel users = new UserModel(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5));
                contactList.add(users);

                //** Checking result of query
//                  System.out.println("USERS: " + users.getUser_id() + "/" + users.getUser_fullName()); //** TODO: Remove this code when final release
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        cursor.close();
        return contactList;
    }

    // TODO: This is the original code.
    //** Fetch all Data invitations
    public ArrayList<InvitationModel> fetchInvitations(String id) {

        String query = "SELECT tbl_invite.id, tbl_invite.fk_user_id, tbl_invite.ticket_id, tbl_invite.REQUEST_STATUS, tbl_ticket.place," +
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' a where a.ticket_id = tbl_invite.ticket_id) as people, " + //** Sub Query for counting invited People status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' a where a.REQUEST_STATUS = 'Pending' and a.ticket_id = tbl_invite.ticket_id) as pending, " + //** Sub Query for counting Pending status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' a where a.REQUEST_STATUS = 'Accepted' and a.ticket_id = tbl_invite.ticket_id) as accepted, " + //** Sub Query for counting Accepted status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' a where a.REQUEST_STATUS = 'Rejected' and a.ticket_id = tbl_invite.ticket_id) as rejected, tbl_user.fullname, tbl_user.id,tbl_ticket.address " + //** Sub Query for counting Rejected status
                "FROM " + TABLE_NAME_INVITATION + " tbl_invite " +
                "INNER JOIN " + TABLE_NAME_LOGIN + " tbl_user ON tbl_invite.sender_id = tbl_user.id " +
                "INNER JOIN " + TABLE_NAME_TICKET + " tbl_ticket ON tbl_invite.ticket_id = tbl_ticket.id " +
                "WHERE tbl_ticket.status ='Active' AND tbl_invite.fk_user_id = '" + id + "' OR sender_id = '" + DataHolder.getData().toString() + "' AND tbl_ticket.status ='Active' " +
                "GROUP BY tbl_invite.ticket_id ORDER BY tbl_invite.id desc";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<InvitationModel> contactList = new ArrayList<>();

        //** while cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                //** call InvitationModel constructor
                InvitationModel invitation = new InvitationModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11));
                contactList.add(invitation);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }


    // TODO: This is for testing in navigation drawer. Fetching all Invitation data sent by the user
    //** Fetch all Data invitations sent by the user
    public ArrayList<InvitationModel> fetchInvitationsSent(String id) {

        String query = "SELECT tbl_invite.id, tbl_invite.fk_user_id, tbl_invite.ticket_id, " +
                "tbl_invite.REQUEST_STATUS, tbl_ticket.place," +
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "'" +
                " a where a.ticket_id = tbl_invite.ticket_id) as people, " + //** Sub Query for counting invited People status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "'" +
                " a where a.REQUEST_STATUS = 'Pending' and a.ticket_id = tbl_invite.ticket_id) as pending, " + //** Sub Query for counting Pending status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "'" +
                " a where a.REQUEST_STATUS = 'Accepted' and a.ticket_id = tbl_invite.ticket_id) as accepted, " + //** Sub Query for counting Accepted status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "'" +
                " a where a.REQUEST_STATUS = 'Rejected' and a.ticket_id = tbl_invite.ticket_id) as rejected," +//** Sub Query for counting Rejected status
                " tbl_user.fullname, tbl_user.id,tbl_ticket.address,tbl_ticket.event_title, " +
                "tbl_ticket.event_date,tbl_ticket.event_start, tbl_ticket.event_end, tbl_ticket.event_remarks," +
                "tbl_ticket.event_advance, tbl_ticket.place " +
                "FROM " + TABLE_NAME_INVITATION + " tbl_invite " +
                "INNER JOIN " + TABLE_NAME_LOGIN + " tbl_user ON tbl_invite.sender_id = tbl_user.id " +
                "INNER JOIN " + TABLE_NAME_TICKET + " tbl_ticket ON tbl_invite.ticket_id = tbl_ticket.id " +
                "WHERE sender_id = '" + id + "' AND tbl_ticket.status ='Active' " +
                "GROUP BY tbl_invite.ticket_id ORDER BY tbl_invite.id desc";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<InvitationModel> contactList = new ArrayList<>();

        //** while cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                //** call InvitationModel constructor
                InvitationModel invitation = new InvitationModel(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getInt(17) > 0, cursor.getString(18));
                contactList.add(invitation);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }

    // TODO: This is for testing in navigation drawer. Fetching all Invitation data receive by other user
    // Fetch all Data invitations receice by other user
    public ArrayList<InvitationModel> fetchInvitationsRecieve(String id) {

        String query = "SELECT tbl_invite.id, tbl_invite.fk_user_id, tbl_invite.ticket_id, " +
                "tbl_invite.REQUEST_STATUS, tbl_ticket.place," +
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' " +
                "a where a.ticket_id = tbl_invite.ticket_id) as people, " + //** Sub Query for counting invited People status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' " +
                "a where a.REQUEST_STATUS = 'Pending' and a.ticket_id = tbl_invite.ticket_id) as pending, " + //** Sub Query for counting Pending status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' " +
                "a where a.REQUEST_STATUS = 'Accepted' and a.ticket_id = tbl_invite.ticket_id) as accepted, " + //** Sub Query for counting Accepted status
                "(Select COUNT(a.fk_user_id) from '" + TABLE_NAME_INVITATION + "' " +
                "a where a.REQUEST_STATUS = 'Rejected' and a.ticket_id = tbl_invite.ticket_id) as rejected, " + //** Sub Query for counting Rejected status
                "tbl_user.fullname, tbl_user.id,tbl_ticket.address,tbl_ticket.event_title,tbl_ticket.event_date, " +
                "tbl_ticket.event_start, tbl_ticket.event_end, tbl_ticket.event_remarks,tbl_ticket.event_advance " +
                "FROM " + TABLE_NAME_INVITATION + " tbl_invite " +
                "INNER JOIN " + TABLE_NAME_LOGIN + " tbl_user ON tbl_invite.sender_id = tbl_user.id " +
                "INNER JOIN " + TABLE_NAME_TICKET + " tbl_ticket ON tbl_invite.ticket_id = tbl_ticket.id " +
                "WHERE tbl_ticket.status ='Active' AND tbl_invite.fk_user_id = '" + id + "' AND tbl_invite.request_status != 'Remove' " +
                "GROUP BY tbl_invite.ticket_id ORDER BY tbl_invite.id desc";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<InvitationModel> contactList = new ArrayList<>();

        //** while cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                //** call InvitationModel constructor
                InvitationModel invitation = new InvitationModel(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7),
                        cursor.getString(8), cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getInt(17) > 0);

                contactList.add(invitation);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }


    // Fetch Invitation information such as name, request status and invitation Id using InvitationModel.class
    public ArrayList<InvitationModel> fetchInvitationsDetailsObj(String ticket_id) {
        String query = "SELECT tbl_login.fullname, tbl_invite.request_status, tbl_invite.id," +
                "tbl_invite.fk_user_id " +
                "FROM " + TABLE_NAME_INVITATION + " tbl_invite " +
                "INNER JOIN " + TABLE_NAME_LOGIN + " tbl_login ON tbl_invite.fk_user_id = tbl_login.id " +
                "WHERE tbl_invite.ticket_id = '" + ticket_id + "' " +
                "AND tbl_invite.request_status != 'Remove' " +
                "GROUP BY tbl_login.id";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<InvitationModel> contactList = new ArrayList<>();

        //** While cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                InvitationModel invitation = new InvitationModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3));
                contactList.add(invitation);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }

    // TODO: For testing in expandable list view 3/13/2018
    //** Fetch Invitation information such as name, request status and invitation Id using InvitationModel.class
    public ArrayList<String> fetchInvitationsDetailsString(String ticket_id) {
        String query = "SELECT tbl_login.fullname, tbl_invite.request_status, tbl_invite.id " +
                "FROM " + TABLE_NAME_INVITATION + " tbl_invite " +
                "INNER JOIN " + TABLE_NAME_LOGIN + " tbl_login ON tbl_invite.fk_user_id = tbl_login.id " +
                "WHERE tbl_invite.ticket_id = '" + ticket_id + "'" +
                "GROUP BY tbl_login.id";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<String> contactList = new ArrayList<>();

        //** While cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
               /* InvitationModel invitation = new InvitationModel(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2));
                contactList.add(invitation);*/
                String personName = cursor.getString(0);
                contactList.add(personName);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }

    //** TODO: Remove this code when final release
    //** Fetch all user inside invitation
    public ArrayList<InvitationModel> fetchUserInsideInvitations() {
        String query = "SELECT tbl_invite.id, tbl_invite.fk_user_id, tbl_invite.ticket_id, tbl_invite.REQUEST_STATUS, " +
                "tbl_ticket.place FROM " + TABLE_NAME_INVITATION + " tbl_invite INNER JOIN " + TABLE_NAME_TICKET + " " +
                "tbl_ticket ON tbl_invite.ticket_id = tbl_ticket.id GROUP BY tbl_invite.ticket_id";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ArrayList<InvitationModel> contactList = new ArrayList<>();

        // while cursor has next value, add to array list
        if (cursor.moveToFirst()) {
            do {
                InvitationModel invitation = new InvitationModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                contactList.add(invitation);

                //checking result of query
                System.out.println("Place : " + invitation.getPlace());
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return contactList;
    }


}
