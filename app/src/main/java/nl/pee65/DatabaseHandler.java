package nl.pee65;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DBNAME = "wijkenDB";
    private static final int DBVERSION = 1;
    private static final String TABLE = "wijken";
    private static final String NAME = "naam";
    private static final String GEKOZEN = "gekozen";
    private static final String TAG = "dbhandler";

    public DatabaseHandler(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" + NAME + " TEXT,"
                + GEKOZEN + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
        log("table created");
        fillDb(db);

    }

    private void fillDb(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Wijk[] wijken = Wijk.values();
        for (Wijk w : wijken) {
            values.put(NAME, w.name()); // Contact Name
            values.put(GEKOZEN, 0); // Contact Phone
            long insert = db.insert(TABLE, null, values);
            log("table inserrtion result was :" + insert);
        }


        log("table filled");
        String countQuery = "SELECT  * FROM " + TABLE;

        Cursor cursor = db.rawQuery(countQuery, null);

        // log count
        log("#rows is : " + cursor.getCount());
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(db);
    }

    public Wijk getWijk() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE, new String[]{NAME}, GEKOZEN + "=?",
                new String[]{"1"}, null, null, null, null);
        log("table queried cursor is null?  :" + (cursor == null));
        if (cursor != null && cursor.moveToFirst()) {
            String wijknaam = cursor.getString(0);
            log("table queried wijknaam  :" + wijknaam);
            try {
                return Wijk.valueOf(wijknaam);
            } catch (RuntimeException e) {
                return null;
            }

        }
        log("table queried but retruning null");
        return null;
    }

    public void setWijk(Wijk w) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GEKOZEN, "0");

        int res = db.update(TABLE, values, null,
                null);
        values = new ContentValues();
        values.put(GEKOZEN, "1");

        // updating row
        res = db.update(TABLE, values, NAME + " =? ",
                new String[]{w.name()});
        log("setWijk to " + w + ", update result is " + res);

    }

    private void log(String string) {
        Log.d(TAG, string);
    }
}
