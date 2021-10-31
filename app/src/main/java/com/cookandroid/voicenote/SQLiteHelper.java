package com.cookandroid.voicenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.text.Editable;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SQLiteHelper {
    private static final String dbName = "myMemo";
    private static final String table1 = "MemoTable";
    private static final int dbVersion = 1;

    private OpenHelper opener;
    private SQLiteDatabase db;
    private Context context;

    public SQLiteHelper(Context context){
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();
    }

    private class OpenHelper extends SQLiteOpenHelper{

        public OpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase){
            String create = "CREATE TABLE " + table1 + "(" +
                    "seq integer PRIMARY KEY AUTOINCREMENT," +
                    "maintext text," +
                    "subtext text," +
                    "isdone integer)";
            sqLiteDatabase.execSQL(create);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table1);
            onCreate(sqLiteDatabase);
        }
    }

    public void insertMemo(Memo memo){
        String sql = "INSERT INTO " + table1 + " VALUES(NULL,'"+memo.maintext+"','"+memo.subtext+"',"+memo.getIsdone()+");";
        db.execSQL(sql);
    }

    public void deleteMemo(int position){
        String sql = "DELETE FROM " + table1 + " WHERE seq = " + position +";";
        db.execSQL(sql);
    }

    public void updateMemo(int position, String memot){
        String guideStr = "수정 시도";
        Toast.makeText(context.getApplicationContext(), guideStr, Toast.LENGTH_SHORT).show();
        String sql = "UPDATE " + table1 +" SET maintext = '"+ memot + "' WHERE seq = " + position +";";
        db.execSQL(sql);
    }

    public ArrayList<Memo> selectAll(){
        String sql = "SELECT * FROM " + table1;

        ArrayList<Memo> list = new ArrayList<>();


        Cursor results = db.rawQuery(sql, null);
        results.moveToFirst();

        while(!results.isAfterLast()){
            Memo memo = new Memo(results.getInt(0), results.getString(1), results.getString(2), results.getInt(3));
            list.add(memo);
            results.moveToNext();
        }

        //        Collections.reverse(list);
        results.close();
        return list;
    }

    //테이블 전체 데이터 삭제
    public void deleteAll(){
        String sql = "DELETE FROM " + table1 +";";
        db.execSQL(sql);
    }

    //특정 날짜 기준 삭제
    public void deleteDate(){
        Calendar cal= Calendar.getInstance();
        cal.add(Calendar.DATE, -1); //하루 전 날짜
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String pDate=sdf.format(cal.getTime());
        db.execSQL("DELETE FROM TB_CHARGE_HISTORY WHERE _start_date<"+""+pDate+"00:00:00");
    }
}
