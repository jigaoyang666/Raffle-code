package au.edu.utas.gaoyangj.raffle_mainpage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by dahoo on 22-May-20.
 */
public class users_table {
    public static final String TABLE_NAME = "Users";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";

    public static final String CREATE_STATEMENT = "CREATE TABLE "
            + TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, "
            + KEY_NAME + " VARCHAR(255) not null, "
            + KEY_MOBILE + " VARCHAR(255) not null"
            +");";

    // insert a row
    public static void insert(SQLiteDatabase db, user u) {
        Cursor c = db.query(TABLE_NAME, null, KEY_MOBILE+"="+u.getMobile(), null, null, null, KEY_ID + " DESC", "1");
        if(c != null && c.getCount() > 0)
        {
            return;
        }
        else {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, u.getName());
            values.put(KEY_MOBILE, u.getMobile());
            long id = db.insert(TABLE_NAME, null, values);
        }
    }

    public static user getByMobile(SQLiteDatabase db, String mo) {
        Cursor c = db.query(TABLE_NAME, null, KEY_MOBILE + "=" + mo, null, null, null, null);
        c.moveToFirst();
        return createFromCursor(c);
    }


    public static user createFromCursor(Cursor c) {
        if (c == null || c.isAfterLast() || c.isBeforeFirst())
        { return null; }
        else {
            user u = new user();

            u.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            u.setMobile(c.getString(c.getColumnIndex(KEY_MOBILE)));
            return u;
        }
    }

}
