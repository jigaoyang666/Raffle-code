package au.edu.utas.gaoyangj.raffle_mainpage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dahoo on 04-May-20.
 */
public class TicketTable {

    private String TABLE_NAME;
    //public static final String TABLE_NAME = "Raffle";
    public static final String KEY_ID = "id";     // in sqlite, first one is 1.
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_CREATE = "creation";
    public static final String KEY_WINNER = "winner";

    public String getCREATE_STATEMENT() {
        return CREATE_STATEMENT;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        this.CREATE_STATEMENT = "CREATE TABLE "
                + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAME + " VARCHAR(255) not null, "
                + KEY_MOBILE + " VARCHAR(255), "
                + KEY_CREATE + " VARCHAR(255) not null, "
                + KEY_WINNER + " integer"      // only 0 or 1
                +");";
    }

    private String CREATE_STATEMENT;
    /*
    public String CREATE_STATEMENT = "CREATE TABLE "
            + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, "
            + KEY_NAME + " VARCHAR(255) not null, "
            + KEY_MOBILE + " VARCHAR(255), "
            + KEY_CREATE + " VARCHAR(255) not null, "
            + KEY_WINNER + " integer"      // only 0 or 1
            +");";
    */

    public ArrayList<TicketDetails> selectAll(SQLiteDatabase db)
    {
        ArrayList<TicketDetails> results = new ArrayList<>();

        Cursor c = db.query(this.TABLE_NAME, null, null, null,null,null,null);
        //check for error
        if (c != null)
        { //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list
            while (!c.isAfterLast())
            {
                TicketDetails r = createFromCursor(c);
                results.add(r);
                //increment the cursor
                c.moveToNext();
            }
        }
        String str[] = new String[results.size()];
        for(int i=0;i<results.size();++i)
        {
            str[i] = results.get(i).getName();
        }
        Log.d("Tickets", Arrays.toString(str));
        return results;
    }

    public ArrayList<String> selectAllID(SQLiteDatabase db)
    {
        ArrayList<String> results = new ArrayList<>();

        Cursor c = db.query(this.TABLE_NAME, null, null, null,null,null,null);
        //check for error
        if (c != null)
        { //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list
            while (!c.isAfterLast())
            {
                String id = String.valueOf(createIDFromCursor(c));
                results.add(id);
                //increment the cursor
                c.moveToNext();
            }
        }
        return results;
    }

    public int soldNumber(SQLiteDatabase db)
    {
        return selectAllID(db).size();
    }

    public int createIDFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        {
            return -1;
        }
        else {
            return c.getInt(c.getColumnIndex(KEY_ID));
        }
    }

    public TicketDetails getByID(SQLiteDatabase db, int id)
    {
        Cursor c = db.query(this.TABLE_NAME, null, KEY_ID+"="+id, null,null,null,null);
        c.moveToFirst();

        return createFromCursor(c);
    }

    public TicketDetails createFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        { return null; }
        else {
            TicketDetails r = new TicketDetails();

            r.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            r.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            r.setMobile(c.getString(c.getColumnIndex(KEY_MOBILE)));
            r.setCreation(RaffleTable.convertStringDate(c.getString(c.getColumnIndex(KEY_CREATE))));
            r.setWinner(c.getInt(c.getColumnIndex(KEY_WINNER)));
            return r;
        }
    }

    // insert a row
    public void insert(SQLiteDatabase db, TicketDetails r) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, r.getName());
        values.put(KEY_MOBILE, r.getMobile());
        values.put(KEY_CREATE, RaffleTable.getCurrentDateTime());
        // winner not insert, but updated

        long id = db.insert(this.TABLE_NAME, null, values);

        if(id != -1 ) {
            Log.d("Ticket", "Success: Sell a ticket!");
        }
        else{
            Log.d("Ticket", "Error: Fail to sell a ticket");
        }
    }

    // Update a row. only for winner
    public void draw(SQLiteDatabase db, int ticket)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_WINNER, 1);
        // creation time, id, tickettable can not be modified
        db.update(this.TABLE_NAME, values, KEY_ID + "= ?", new String[]{"" + ticket});
    }

    public int TicketsofOneUser(SQLiteDatabase db, String mobile)
    {
        if(!(mobile == null && mobile.isEmpty())){
            Cursor c = db.query(this.TABLE_NAME, null, KEY_MOBILE+"="+mobile, null,null,null,null);
            if(c != null && c.getCount() > 0)
            {
                return c.getCount();
            }
            else{
                return 0;
            }
        }
        else{
            return -1;
        }

    }
}
