package au.edu.utas.gaoyangj.raffle_mainpage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by dahoo on 04-May-20.
 */
public class TicketTable_MarginRaffle{

    private String table_name;

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
        this.CREATE_STATEMENT = "CREATE TABLE "
                + table_name + " (" + KEY_ID + " integer primary key autoincrement, "
                + KEY_TICEKT + " integer, "
                + KEY_NAME + " VARCHAR(255), "
                + KEY_MOBILE + " VARCHAR(255), "
                + KEY_CREATE + " VARCHAR(255), "
                + KEY_WINNER + " integer"      // only 0 or 1
                +");";
    }

    public String getTable_name() {
        return new String(table_name);
    }

    public int getTotal() {
        return total;
    }

    private int total;

    //public static final String TABLE_NAME = "Raffle";
    public static final String KEY_ID = "id";     // in sqlite, first one is 1.
    public static final String KEY_TICEKT = "ticket";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_CREATE = "creation";
    public static final String KEY_WINNER = "winner";


    public String CREATE_STATEMENT;
    public String getCREATE_STATEMENT() {
        return CREATE_STATEMENT;
    }

    public void createRandomTickets(SQLiteDatabase db)
    {
        Integer[] intArray = new Integer[total];
        for (int i=0; i< total; ++i)
        {
            intArray[i] = i+1;
        }
        List<Integer> intList = Arrays.asList(intArray);
        Collections.shuffle(intList);

        Log.d("Random", intList.toString());
        String insertMultiple = "INSERT INTO " + table_name + " (ticket) VALUES ";
        for(int i=0; i<total-1; i++)
        {
            insertMultiple += "( " + intList.get(i) + " ),";
        }
        insertMultiple +="( " + intList.get(total-1) + " );";
        db.execSQL(insertMultiple);

    }

    public ArrayList<MarginTicketDetails> selectAll(SQLiteDatabase db)
    {
        ArrayList<MarginTicketDetails> results = new ArrayList<>();
        Log.d("TicketTableName", this.table_name);

        Cursor c = db.query(this.table_name, null, null, null,null,null,null);
        //check for error
        if (c != null)
        { //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list
            while (!c.isAfterLast())
            {
                MarginTicketDetails r = createFromCursor(c);
                if(!(r.getMobile() == null || r.getMobile().isEmpty()) )
                {
                    results.add(r);
                }
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

        Cursor c = db.query(this.table_name, null, null, null,null,null,null);
        //check for error
        if (c != null)
        { //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list
            while (!c.isAfterLast())
            {
                MarginTicketDetails r = createFromCursor(c);
                if(!(r.getMobile() == null || r.getMobile().isEmpty()) )
                {
                    results.add(String.valueOf(r.getId()));
                }
                //increment the cursor
                c.moveToNext();
            }
        }
        return results;
    }

    public int soldNumber(SQLiteDatabase db){
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

    /*
    public MarginTicketDetails getByID(SQLiteDatabase db, int id)
    {
        Cursor c = db.query(this.table_name, null, KEY_ID+"="+id, null,null,null,null);
        c.moveToFirst();

        return createFromCursor(c);
    }
     */

    public MarginTicketDetails getByTicketID(SQLiteDatabase db, int id)
    {
        Cursor c = db.query(this.table_name, null, KEY_TICEKT+"="+id, null,null,null,null);
        c.moveToFirst();

        return createFromCursor(c);
    }


    public MarginTicketDetails createFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        { return null; }
        else {
            MarginTicketDetails r = new MarginTicketDetails();

            r.setId(c.getInt(c.getColumnIndex(KEY_TICEKT)));
            r.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            r.setMobile(c.getString(c.getColumnIndex(KEY_MOBILE)));
            r.setCreation(RaffleTable.convertStringDate(c.getString(c.getColumnIndex(KEY_CREATE))));
            r.setWinner(c.getInt(c.getColumnIndex(KEY_WINNER)));
            r.setTicket(c.getInt(c.getColumnIndex(KEY_TICEKT)));
            return r;
        }
    }

    // sell a ticket, it is update actually
    public void sell(SQLiteDatabase db, MarginTicketDetails r) {

        Cursor c = db.query(this.table_name, null, null, null,null,null,null);
        if(c != null && c.getCount() != 0)
        {
            Cursor c1 = db.query(this.table_name, null, KEY_MOBILE+" IS NOT NULL", null,null,null,null);
            c1.moveToFirst();
            int count = c1.getCount();
            int id = count+1;
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, r.getName());
            values.put(KEY_MOBILE, r.getMobile());
            values.put(KEY_CREATE, RaffleTable.getCurrentDateTime());
            db.update(this.table_name, values, KEY_ID + "= ?", new String[]{"" + id});
        }
    }

    // Update a row. only for winner
    public void draw(SQLiteDatabase db, int ticket)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_WINNER, 1);
        // creation time, id, tickettable can not be modified
        db.update(this.table_name, values, KEY_TICEKT + "= ?", new String[]{"" + ticket});
    }

    public int TicketsofOneUser(SQLiteDatabase db, String mobile)
    {
        if(!(mobile == null && mobile.isEmpty()))
        {
            Cursor c = db.query(this.table_name, null, KEY_MOBILE+"="+mobile, null,null,null,null);
            if(c != null && c.getCount() > 0)
            {
                return c.getCount();
            }
            else{
                return 0;
            }
        }
        else {
            return -1;
        }
    }
}
