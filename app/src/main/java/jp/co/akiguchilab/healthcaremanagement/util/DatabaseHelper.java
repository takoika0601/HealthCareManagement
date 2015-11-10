package jp.co.akiguchilab.healthcaremanagement.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        super(context, "WalkCount", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            SQLiteStatement statement;

            //歩数情報作成
            db.execSQL("CREATE TABLE WORK_COUNTER_INFO (N_CNT INTEGER, REG_DATE TEXT);");
            statement = db.compileStatement("INSERT INTO WORK_COUNTER_INFO VALUES(0, ?);");
            String date = CountUtil.dateformat("yyyyMMddHHmmss", new Date(), 0);
            statement.bindString(1, date);
            statement.executeInsert();

            //歩数の履歴作成
            db.execSQL("CREATE TABLE WORK_COUNTER__HISTORY (HISTORY_DATE TEXT PRIMARY KEY, H_CNT INTEGER)");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    //現在歩数の取得
    public long selectCount() {
        final SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT N_CNT FROM WORK_COUNTER_INFO;", null);
        c.moveToFirst();
        long ret = c.getLong(0);
        c.close();
        //DBUtils.dbTablesOutputLog(this);
        return ret;
    }

    //歩数の更新
    public void updateCount(long counter) {
        final SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String date = CountUtil.dateformat("yyyyMMddHHmmss", new Date(), 0);
            db.execSQL("UPDATE WORK_COUNTER_INFO SET N_CNT = ?, REG_DATE = ?;", new String[]{"" + counter, date});
        } finally {
            db.endTransaction();
        }
    }
}
