package au.edu.utas.gaoyangj.raffle_mainpage;

/**
 * Created by dahoo on 02-May-20.
 */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RaffleTable {
    public static final String TABLE_NAME = "Raffles";
    public static final String KEY_ID = "id";
    public static final String KEY_TYPE = "type";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_LIMIT = "limits";
    public static final String KEY_CREATE = "creation";        //
    public static final String KEY_DRAW = "drawtime";          //
    public static final String KEY_WINNER = "winner";
    public static final String KEY_TICKET = "ticket";

    public static final String CREATE_STATEMENT = "CREATE TABLE "
            + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, "
            + KEY_TYPE + " VARCHAR(255) not null, "
            + KEY_NAME + " VARCHAR(255) not null, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_PRICE + " integer not null, "
            + KEY_IMAGE + " TEXT, "
            + KEY_TOTAL + " integer default 0, "
            + KEY_LIMIT + " integer default 1, "
            + KEY_CREATE + " VARCHAR(255) not null, "
            + KEY_DRAW + " VARCHAR(255) not null, "
            + KEY_WINNER + " integer, "
            + KEY_TICKET + " VARCHAR(255) not null"
            +");";

    // insert a row
    public static void insert(SQLiteDatabase db, RaffleDetails r) {

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, KEY_ID + " DESC", "1");
        String tickettable = "Raffle_" + r.getType() + "_";
        int raffleid;
        if (c == null || c.getCount() ==0) {
            raffleid = 1;
        }
        else{
            c.moveToFirst();
            raffleid = c.getInt(c.getColumnIndex(KEY_ID)) + 1;
        }
        tickettable = tickettable + String.valueOf(raffleid);

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, r.getType());
        values.put(KEY_NAME, r.getName());
        values.put(KEY_DESCRIPTION, r.getDescription());
        values.put(KEY_PRICE, r.getPrice());
        values.put(KEY_IMAGE, r.getImage());      // need to transfer path, remember store it in internal storage
        values.put(KEY_TOTAL, r.getTotal());
        values.put(KEY_LIMIT, r.getLimit());
        values.put(KEY_DRAW, convertDateTime(r.getDrawtime()));
        values.put(KEY_CREATE, getCurrentDateTime());
        values.put(KEY_TICKET, tickettable);
        long id = db.insert(TABLE_NAME, null, values);
        if (id != -1) {
            Log.d("Raffle", "Success: Insert a new raffle!");
            //create a ticket table
            String CREATE_TABLE = "CREATE TABLE ";
            if(r.getType().equals("Normal")){
                TicketTable tt = new TicketTable();
                tt.setTABLE_NAME(tickettable);
                db.execSQL(tt.getCREATE_STATEMENT());
                Log.d("Ticket", "Create:" + CREATE_TABLE);
            }
            else
            {
                Log.d("Ticket", "Create:" + tickettable);
                TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
                tm.setTable_name(tickettable);
                tm.setTotal(r.getTotal());
                db.execSQL(tm.getCREATE_STATEMENT());
                tm.createRandomTickets(db);
            }

        }
        else {
            Log.d("Raffle", "Error: Fail to insert a new raffle");
        }
    }

    public static String getCurrentDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String datetime = dateFormat.format(date);
        Log.d("Current Time", datetime);
        return datetime;
    }

    public static String convertDateTime(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        if(date == null)
            return "";
        String datetime = dateFormat.format(date);
        return datetime;
    }

    public static Date convertStringDate(String s){
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date;
        try{
            date = formater.parse(s);
        }
        catch (Exception e){
            date = null;
        }
        return date;
    }

    public static ArrayList<RaffleDetails> selectAll(SQLiteDatabase db)
    {
        ArrayList<RaffleDetails> results = new ArrayList<>();

        Cursor c = db.query(TABLE_NAME, null, null, null,null,null,null);
        //check for error
        if (c != null && c.getCount() >0)
        { //make sure the cursor is at the start of the list
            c.moveToFirst();
            //loop through until we are at the end of the list
            while (!c.isAfterLast())
            {
                RaffleDetails r = createFromCursor(c);
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
        Log.d("Raffle", Arrays.toString(str));
        return results;
    }

    public static ArrayList<String> selectAllID(SQLiteDatabase db)
    {
        ArrayList<String> results = new ArrayList<>();

        Cursor c = db.query(TABLE_NAME, null, null, null,null,null,null);
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

    public static int createIDFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        {
            return -1;
        }
        else {
            return c.getInt(c.getColumnIndex(KEY_ID));
        }
    }

    public static RaffleDetails getByID(SQLiteDatabase db, int id)
    {
        Cursor c = db.query(TABLE_NAME, null, KEY_ID+"="+id, null,null,null,null);
        c.moveToFirst();

        return createFromCursor(c);
    }

    public static RaffleDetails createFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        { return null; }
        else {
            RaffleDetails r = new RaffleDetails();

            r.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            r.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
            r.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            r.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
            r.setPrice(c.getInt(c.getColumnIndex(KEY_PRICE)));
            r.setTotal(c.getInt(c.getColumnIndex(KEY_TOTAL)));
            r.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
            r.setLimit(c.getInt(c.getColumnIndex(KEY_LIMIT)));
            r.setDrawtime(convertStringDate(c.getString(c.getColumnIndex(KEY_DRAW))));
            r.setCreation(convertStringDate(c.getString(c.getColumnIndex(KEY_CREATE))));
            r.setWinner(c.getInt(c.getColumnIndex(KEY_WINNER)));
            r.setTickettable(c.getString(c.getColumnIndex(KEY_TICKET)));
            return r;
        }
    }

    // Update a row.
    public static void update(SQLiteDatabase db, RaffleDetails r)
    {
        RaffleDetails rd = getByID(db, r.getId());

        String tablename = rd.getTickettable();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, r.getType());
        values.put(KEY_NAME, r.getName());
        values.put(KEY_DESCRIPTION, r.getDescription());
        values.put(KEY_PRICE, r.getPrice());
        values.put(KEY_IMAGE, r.getImage());      // need to transfer path to binary data
        values.put(KEY_TOTAL, r.getTotal());
        values.put(KEY_LIMIT, r.getLimit());
        values.put(KEY_DRAW, convertDateTime(r.getDrawtime()));
        // creation time, id, tickettable, and winner can not be modified
        db.update(TABLE_NAME, values, KEY_ID + "= ?", new String[]{"" + r.getId()});

    }

    // Delete a row.
    public static boolean delete(SQLiteDatabase db, RaffleDetails r)
    {
        boolean can_delete = false;
        if(r.getType().equals("Normal")) {
            TicketTable tt = new TicketTable();
            tt.setTABLE_NAME(r.getTickettable());
            if (tt.soldNumber(db) > 0){
                can_delete = false;
            }
            else{
                can_delete = true;
                db.execSQL("DROP TABLE IF EXISTS '" + r.getTickettable()+ "'");
            }
        }
        else{
            TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
            tm.setTable_name(r.getTickettable());
            if(tm.soldNumber(db) > 0)
                can_delete = false;
            else{
                can_delete = true;
                db.execSQL("DROP TABLE IF EXISTS '" + r.getTickettable() + "'");
            }
        }
        //check for error
        if (can_delete) {
            int val = db.delete(TABLE_NAME, KEY_ID + "= ?", new String[]{"" + r.getId()});
            if(val >0)
                Log.d("Delete ", String.valueOf(val) + " row, name is "+ r.getName());
            else
                Log.d("Delete ", String.valueOf(val) + " row");
            return true;
        }
        else{
            Log.d("Delete ", "Tickets have been sold, so this raffle can not be deleted!");
            return false;
        }

    }

    // Draw a raffle
    public static int draw(SQLiteDatabase db, RaffleDetails r, int ticketid) {
        if (r.getWinner() != 0) {
            Log.d("Draw", "This raffle has been drawed!");
            return -1;
        }

        Date drawtime = r.getDrawtime();
        Date now = new Date();
        if (now.compareTo(drawtime) < 0) {
            Log.d("Draw", "It is not drawtime yet!");   // too early
            return 0;
        }

        int sold = 0;
        if(r.getType().equals("Normal")) {
            TicketTable tt = new TicketTable();
            tt.setTABLE_NAME(r.getTickettable());
            sold = tt.soldNumber(db);
        }
        else{
            TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
            tm.setTable_name(r.getTickettable());
            sold = tm.soldNumber(db);
        }

        //check for error
        if (sold <= 0) {
            Log.d("Draw", "Tickets have not been sold, so this raffle can not be drawed!");
            return -2;
        }

        if (r.getType().equals("Normal")) {
            Cursor c_t = db.query(r.getTickettable(), null, "id=" + ticketid, null, null, null, null);
            if (c_t.getCount() == 1) {
                //update Raffles
                ContentValues values = new ContentValues();
                values.put(KEY_WINNER, ticketid);
                db.update(TABLE_NAME, values, KEY_ID + "= ?", new String[]{"" + r.getId()});

                //update Tickettable
                ContentValues values_ticket = new ContentValues();
                values_ticket.put("winner", 1);

                db.update(r.getTickettable(), values_ticket, "id= ?", new String[]{"" + ticketid});
                return 1;

            } else {
                Log.d("Draw", "No one get prize!");
                return -3;
            }

        } else {
            TicketTable_MarginRaffle tm = new TicketTable_MarginRaffle();
            tm.setTable_name(r.getTickettable());
            MarginTicketDetails mtd = tm.getByTicketID(db, ticketid);
            if (mtd == null || mtd.getMobile() == null || mtd.getMobile().isEmpty()) {
                Log.d("Draw", "No one get prize!");
                return -3;

            } else {
                //update Raffles
                ContentValues values = new ContentValues();
                values.put(KEY_WINNER, ticketid);
                db.update(TABLE_NAME, values, KEY_ID + "= ?", new String[]{"" + r.getId()});

                //update Tickettable
                ContentValues values_ticket = new ContentValues();
                values_ticket.put("winner", 1);

                db.update(r.getTickettable(), values_ticket, "ticket= ?", new String[]{"" + ticketid});
                Log.d("Draw", "One get prize!");
                return 1;
            }
        }
    }
}
