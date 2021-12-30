package com.example.java_final_project.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "USER_RECORD";
    private static final String TABLE_FIRST = "USER_DATA";
    private static final String U_COL_1 = "ID";
    private static final String U_COL_2 = "USERNAME";
    private static final String U_COL_3 = "EMAIL";
    private static final String U_COL_4 = "PW";
    private static final String TABLE_SECOND = "PAPER";
    private static final String P_COL_1 = "ID";
    private static final String P_COL_2 = "USERNAME";
    private static final String P_COL_3 = "PAPER_IMAGE";
    private static final String P_COL_4 = "DATE";
    private static final String P_COL_5 = "PILL";
    private static final String TABLE_THIRD = "MEDI";
    private static final String M_COL_1 = "ID";
    private static final String M_COL_2 = "USERNAME";
    private static final String M_COL_3 = "PILL_NAME";
    private static final String M_COL_4 = "ACHE";
    private static final String M_COL_5 = "FIRST_DATE";
    private static final String M_COL_6 = "LAST_DATE";
    private static final String M_COL_7 = "ALARM_WHEN";
    private static final String TABLE_FOURTH = "ALARM";
    private static final String A_COL_1 = "ID";
    private static final String A_COL_2 = "ALARM_MORNING";
    private static final String A_COL_3 = "ALARM_LUNCH";
    private static final String A_COL_4 = "ALARM_DINNER";
    private static final String A_COL_5 = "PILL_NAME";
    private static final String A_COL_6 = "PILL_ID";
    private static final String A_COL_7 = "MORNING_CODE";
    private static final String A_COL_8 = "LUNCH_CODE";
    private static final String A_COL_9 = "DINNER_CODE";
    private static final String TABLE_FIFTH = "WALKDATA";
    private static final String W_COL_1 = "ID";
    private static final String W_COL_2 = "USERNAME";
    private static final String W_COL_3 = "WALK_NUM";
    private static final String W_COL_4 = "DATE";
    private static final String W_COL_5 = "WALK_TIME";
    private static final String W_COL_6 = "REAL_WALK";
    private static final String W_COL_7 = "RATES";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, (int) 1.1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " +
                TABLE_FIRST + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, EMAIL TEXT, PW TEXT)");
        db.execSQL("CREATE TABLE " +
                TABLE_SECOND + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, PAPER_IMAGE TEXT, DATE TEXT, PILL TEXT)");
        db.execSQL("CREATE TABLE " +
                TABLE_THIRD + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, PILL_NAME TEXT, ACHE TEXT," +
                "FIRST_DATE TEXT, LAST_DATE TEXT, ALARM_WHEN TEXT)");
        db.execSQL("CREATE TABLE " +
                TABLE_FOURTH + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ALARM_MORNING TEXT, ALARM_LUNCH TEXT, ALARM_DINNER TEXT," +
                "PILL_NAME TEXT, PILL_ID TEXT, MORNING_CODE TEXT," +
                "LUNCH_CODE TEXT, DINNER_CODE TEXT)");
        db.execSQL("CREATE TABLE " +
                TABLE_FIFTH + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERNAME TEXT, WALK_NUM TEXT, DATE TEXT, " +
                "WALK_TIME TEXT, REAL_WALK TEXT, RATES TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldv, int newv) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIRST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECOND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THIRD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOURTH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIFTH);
        onCreate(db);
    }

    /**
     * registerUser
     * 회원가입 시 데이터 저장하는 불린값의 DB
     * @param username
     * @param email
     * @param pw
     * @return
     */
    public boolean addUser(String username, String email, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(U_COL_2, username);
        values.put(U_COL_3, email);
        values.put(U_COL_4, pw);
        long result = db.insert(TABLE_FIRST, null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * addPaper
     * 처방전을 기입하는 불린값의 DB
     * @param username
     * @param uri
     * @param date
     * @return
     */
    public boolean addPaper(String username, Uri uri, String date) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(P_COL_2, username);
        contentValues.put(P_COL_3, String.valueOf(uri));
        contentValues.put(P_COL_4, date);
        long result = database.insert
                (TABLE_SECOND, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * addPill
     * 복용할 약의 데이터를 기입하는 불린값의 DB
     * @param username
     * @param name
     * @param ache
     * @param f_d
     * @param l_d
     * @return
     */
    public boolean addPill(String username, String name, String ache,
                           String f_d, String l_d, String alarm) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(M_COL_2, username);
        contentValues.put(M_COL_3, name);
        contentValues.put(M_COL_4, ache);
        contentValues.put(M_COL_5, f_d);
        contentValues.put(M_COL_6, l_d);
        contentValues.put(M_COL_7, alarm);
        long result = database.insert
                (TABLE_THIRD, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean addAlarm(String ALARM_MORNING, String ALARM_LUNCH,
                            String ALARM_DINNER, String PILL_NAME,
                            String PILL_ID, int MORNING_CODE,
                            int LUNCH_CODE, int DINNER_CODE) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(A_COL_2, ALARM_MORNING);
        contentValues.put(A_COL_3, ALARM_LUNCH);
        contentValues.put(A_COL_4, ALARM_DINNER);
        contentValues.put(A_COL_5, PILL_NAME);
        contentValues.put(A_COL_6, PILL_ID);
        contentValues.put(A_COL_7, String.valueOf(MORNING_CODE));
        contentValues.put(A_COL_8, String.valueOf(LUNCH_CODE));
        contentValues.put(A_COL_9, String.valueOf(DINNER_CODE));
        long result = database.insert
                (TABLE_FOURTH, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public boolean addWalkData(String username, String walk_num,
                               String date, String walk_time,
                               String real_walk, String rate) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(W_COL_2, username);
        contentValues.put(W_COL_3, walk_num);
        contentValues.put(W_COL_4, date);
        contentValues.put(W_COL_5, walk_time);
        contentValues.put(W_COL_6, real_walk);
        contentValues.put(W_COL_7, rate);
        long result = database.insert
                (TABLE_FIFTH, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * checkUser
     * 로그인 시 해당 유저의 아이디값과 패스워드 값이 옳은지를 판단해 진입시키는 불린값의 DB
     * @param username
     * @param pw
     * @return
     */
    public boolean checkUser(String username, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FIRST +
                " where " + U_COL_2 + "=?" + " and " +
                U_COL_4 + "=?", new String[] {username, pw});
        int count = cursor.getCount();
        db.close();;
        cursor.close();
        if (count > 0) {
            return true;
        } else {
           return false;
        }
    }

    /**
     * checkUserEmail
     * 회원가입 시 동일 이메일 주소 발견시 다시 시도를 유도하는 불린값의 DB
     * @param username
     * @param email
     * @return
     */
    public boolean checkUserEmail(String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FIRST +
                " where " + U_COL_2 + "=?" + " or " +
                U_COL_3 + "=?", new String[] {username, email});
        int count = cursor.getCount();
        db.close();;
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    public Cursor getUserId(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT " + U_COL_1 + " FROM " +
                TABLE_FIRST + " where "
                        + U_COL_2 + "=?", new String[] {username},
                null);
        return cursorId;
    }
    public Cursor getUserPaperId(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + P_COL_1 + " FROM "
                        + TABLE_SECOND + " where "
                        + P_COL_4 + "=?", new String[] {date},
                null);
        return cursorId;
    }
    public Cursor getUserPillId(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + M_COL_1 + " FROM "
                        + TABLE_THIRD + " where "
                        + M_COL_3 + "=?", new String[] {name},
                null);
        return cursorId;
    }
    public Cursor getUserPaper(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorName = db.rawQuery("SELECT " + P_COL_1 + ", " +
                        P_COL_3 + ", " + P_COL_4 + ", " + P_COL_5 +
                " FROM " + TABLE_SECOND + " where "
                + P_COL_2 + "=?", new String[] {username},
                null);
        return cursorName;
    }
    public Cursor getUserPaperAll(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorName = db.rawQuery("SELECT " + P_COL_2 + ", " +
                        P_COL_3 + ", " + P_COL_4 + ", " + P_COL_5 +
                        " FROM " + TABLE_SECOND + " where "
                        + P_COL_1 + "=?", new String[] {id},
                null);
        return cursorName;
    }
    public Cursor getUserPills(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorName = db.rawQuery("SELECT " + M_COL_1 + ", "
                        + M_COL_3 + ", " + M_COL_4 + ", "
                        + M_COL_5 + ", " + M_COL_6 + ", "
                        + M_COL_7 + " FROM " + TABLE_THIRD + " where "
                        + M_COL_2 + "=?", new String[] {username},
                null);
        return cursorName;
    }
    public Cursor getUserPillByName(String pillname) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorName = db.rawQuery("SELECT " + M_COL_1 + ", "
                        + M_COL_2 + ", " + M_COL_4 + ", "
                        + M_COL_5 + ", " + M_COL_6 + ", "
                        + M_COL_7 + " FROM " + TABLE_THIRD + " where "
                        + M_COL_3 + "=?", new String[] {pillname},
                null);
        return cursorName;
    }
    public Cursor getUserPillName(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + M_COL_3 + " FROM "
                        + TABLE_THIRD + " where "
                        + M_COL_1 + "=?", new String[] {id},
                null);
        return cursorId;
    }
    public Cursor getAlarmList(String pillid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + A_COL_1 + " FROM "
                        + TABLE_FOURTH + " where "
                        + A_COL_6 + "=?", new String[] {pillid},
                null);
        return cursorId;
    }
    public Cursor getAlarmSetWhen(String pill_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + A_COL_2 + ", "
                        + A_COL_3 + ", " + A_COL_4 + ", " + A_COL_7 + ", "
                        + A_COL_8 + ", " + A_COL_9 + ", "  + A_COL_5 + " FROM "
                        + TABLE_FOURTH + " where "
                        + A_COL_5 + "=?", new String[] {pill_name},
                null);
        return cursorId;
    }
    public Cursor getWalkSaveData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorId = db.rawQuery("SELECT "  + W_COL_1 + ", "
                        + W_COL_3 + ", " + W_COL_4 + ", "
                        + W_COL_5 + ", " + W_COL_6 + ", " + W_COL_7 + " FROM "
                        + TABLE_FIFTH + " where "
                        + W_COL_2 + "=?", new String[] {username},
                null);
        return cursorId;
    }

    /**
     * updateData
     * 유저의 기본 정보 DB값을 변경시 필요한 불린값의 DB
     * @param id
     * @param username
     * @param email
     * @param pw
     * @return
     */
    public boolean updateData(String id, String username, String email, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(U_COL_1, id);
        contentValues.put(U_COL_2, username);
        contentValues.put(U_COL_3, email);
        contentValues.put(U_COL_4, pw);
        db.update(TABLE_FIRST, contentValues, "ID = ?", new String[]{id});
        return true;
    }
    public boolean updatePaper(String id, String username,
                               String image, String date,
                               String[] pill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(P_COL_1, id);
        contentValues.put(P_COL_2, username);
        contentValues.put(P_COL_3, image);
        contentValues.put(P_COL_4, date);
        contentValues.put(P_COL_5, Arrays.toString(pill));
        db.update(TABLE_SECOND, contentValues, "ID = ?", new String[]{id});
        return true;
    }
    public boolean updatePill(String id, String username, String pill_name
            , String ache, String f_d, String l_d,
                              String[] alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(M_COL_1, id);
        contentValues.put(M_COL_2, username);
        contentValues.put(M_COL_3, pill_name);
        contentValues.put(M_COL_4, ache);
        contentValues.put(M_COL_5, f_d);
        contentValues.put(M_COL_6, l_d);
        contentValues.put(M_COL_7, Arrays.toString(alarm));
        db.update(TABLE_THIRD, contentValues, "ID = ?", new String[]{id});
        return true;
    }
    public boolean updateAlarmList(String id, String ALARM_MORNING, String ALARM_LUNCH,
                                   String ALARM_DINNER, String PILL_NAME,
                                   String PILL_ID, int MORNING_CODE,
                                   int LUNCH_CODE, int DINNER_CODE) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(A_COL_1, id);
        contentValues.put(A_COL_2, ALARM_MORNING);
        contentValues.put(A_COL_3, ALARM_LUNCH);
        contentValues.put(A_COL_4, ALARM_DINNER);
        contentValues.put(A_COL_5, PILL_NAME);
        contentValues.put(A_COL_6, PILL_ID);
        contentValues.put(A_COL_7, String.valueOf(MORNING_CODE));
        contentValues.put(A_COL_8, String.valueOf(LUNCH_CODE));
        contentValues.put(A_COL_9, String.valueOf(DINNER_CODE));
        db.update(TABLE_FOURTH, contentValues, "ID = ?", new String[]{id});
        return true;
    }
    public boolean updateWalk(String id, String username, String walk_num, String date
            , String walk_hour, String real_walk, String rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(W_COL_1, id);
        contentValues.put(W_COL_2, username);
        contentValues.put(W_COL_3, walk_num);
        contentValues.put(W_COL_4, date);
        contentValues.put(W_COL_5, walk_hour);
        contentValues.put(W_COL_6, real_walk);
        contentValues.put(W_COL_7, rate);
        db.update(TABLE_FIFTH, contentValues, "ID = ?", new String[]{id});
        return true;
    }
    public void deleteUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FIRST + " WHERE " +
                U_COL_1 + "=?", new String[]{id});
    }
    public void deletePaper(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SECOND + " WHERE " +
                P_COL_1 + "=?", new String[]{id});
    }
    public void deletePill(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_THIRD + " WHERE " +
                M_COL_1 + "=?", new String[]{id});
    }
    public void deleteAlarm(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FOURTH + " WHERE " +
                A_COL_1 + "=?", new String[]{id});
    }
}
