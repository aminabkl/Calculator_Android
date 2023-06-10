package ma.fstt.calculatrice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    protected static final String DATABASE_NAME = "calculator.db";
    protected static final int DATABASE_VERSION = 1;
    protected static final String TABLE_NAME = "history";
    protected static final String COLUMN_ID = "_id";
    protected static final String COLUMN_OPERATION = "operation";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OPERATION + " TEXT)";
        db.execSQL(createTableQuery);
        Log.d("onCreate", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
        Log.d("onUpgrade", "table updated");
    }
}
