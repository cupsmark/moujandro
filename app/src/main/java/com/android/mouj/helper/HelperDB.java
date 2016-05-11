package com.android.mouj.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.mouj.models.ModelsTableColumn;
import com.android.mouj.models.UGroup;
import com.android.mouj.models.USch;
import com.android.mouj.models.UState;
import com.android.mouj.models.UUs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekobudiarto on 10/13/15.
 */
public class HelperDB extends SQLiteOpenHelper{

    Context mContext;

    public HelperDB(Context context) {
        super(context, ModelsTableColumn.DB_NAME, null, ModelsTableColumn.DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        create_UGroup(db);
        create_UUs(db);
        create_USch(db);
        create_UState(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        create_UGroup(db);
        create_UUs(db);
        create_USch(db);
        create_UState(db);
    }

    //Segment UGroup

    public void addUGroup(UGroup group){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.USERS_GROUP_ID, group.getGroupID());
        values.put(ModelsTableColumn.USERS_GROUP_NAME, group.getGroupName());
        values.put(ModelsTableColumn.USERS_GROUP_DESC, group.getGroupDesc());
        db.insert(ModelsTableColumn.USERS_GROUP_TBL, null, values);
        db.close();
    }

    public void updateUGroup(UGroup group){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.USERS_GROUP_ID, group.getGroupID());
        values.put(ModelsTableColumn.USERS_GROUP_NAME, group.getGroupName());
        values.put(ModelsTableColumn.USERS_GROUP_DESC, group.getGroupDesc());
        db.update(ModelsTableColumn.USERS_GROUP_TBL, values, ModelsTableColumn.USERS_GROUP_ID + " = ?", new String[]
                {String.valueOf(group.getGroupID())});
        db.close();
    }

    public List<UGroup> getAllUGroup()
    {
        List<UGroup> lists = new ArrayList<UGroup>();
        String sql = "SELECT * FROM " + ModelsTableColumn.USERS_GROUP_TBL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do{
                UGroup group = new UGroup();
                group.setGroupID(Integer.parseInt(cursor.getString(0)));
                group.setGroupName(cursor.getString(1));
                group.setGroupDesc(cursor.getString(2));
                lists.add(group);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }

    public boolean checkUGroup(int id)
    {
        String sql = "SELECT * FROM " + ModelsTableColumn.USERS_GROUP_TBL + " WHERE " + ModelsTableColumn.USERS_GROUP_ID +
                "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //End Segment UGroup

    //Segment UUS
    public void addUUs(UUs us){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.UUS_ID, us.getUUsID());
        values.put(ModelsTableColumn.UUS_NAME, us.getUUsName());
        values.put(ModelsTableColumn.UUS_FULL, us.getUUsFull());
        values.put(ModelsTableColumn.UUS_DESC, us.getUUsDesc());
        values.put(ModelsTableColumn.UUS_GROUP, us.getUUsGroup());
        values.put(ModelsTableColumn.UUS_TOKEN, us.getUUsToken());
        values.put(ModelsTableColumn.UUS_AVA, us.getUUsAva());
        values.put(ModelsTableColumn.UUS_IS_IN, us.getUUsIsIn());
        values.put(ModelsTableColumn.UUS_STEP, us.getUUsStep());
        values.put(ModelsTableColumn.UUS_HASGR, us.getUUsGR());
        db.insert(ModelsTableColumn.UUS_TBL, null, values);
        db.close();
    }

    public void updateUUs(UUs us){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.UUS_ID, us.getUUsID());
        values.put(ModelsTableColumn.UUS_NAME, us.getUUsName());
        values.put(ModelsTableColumn.UUS_FULL, us.getUUsFull());
        values.put(ModelsTableColumn.UUS_DESC, us.getUUsDesc());
        values.put(ModelsTableColumn.UUS_GROUP, us.getUUsGroup());
        values.put(ModelsTableColumn.UUS_TOKEN, us.getUUsToken());
        values.put(ModelsTableColumn.UUS_AVA, us.getUUsAva());
        values.put(ModelsTableColumn.UUS_IS_IN, us.getUUsIsIn());
        values.put(ModelsTableColumn.UUS_STEP, us.getUUsStep());
        values.put(ModelsTableColumn.UUS_HASGR, us.getUUsGR());
        db.update(ModelsTableColumn.UUS_TBL, values, ModelsTableColumn.UUS_ID + " = ?", new String[]
                {String.valueOf(us.getUUsID())});
        db.close();
    }

    public void updateUUsIsIn(UUs us, int uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.UUS_IS_IN, us.getUUsIsIn());
        db.update(ModelsTableColumn.UUS_TBL, values, ModelsTableColumn.UUS_ID + " = ?", new String[]
                {String.valueOf(uid)});
        db.close();
    }

    public void updateUUsStep(UUs us, int uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.UUS_STEP, us.getUUsStep());
        db.update(ModelsTableColumn.UUS_TBL, values, ModelsTableColumn.UUS_ID + " = ?", new String[]
                {String.valueOf(uid)});
        db.close();
    }

    public List<UUs> getAllUUs(int l)
    {
        List<UUs> lists = new ArrayList<UUs>();
        String sql = "SELECT * FROM " + ModelsTableColumn.UUS_TBL +" ORDER BY "+ModelsTableColumn.UUS_ID+" DESC LIMIT "+Integer.toString(l)+" OFFSET 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do{
                UUs us = new UUs();
                us.setUUsID(Integer.parseInt(cursor.getString(0)));
                us.setUUsName(cursor.getString(1));
                us.setUUsFull(cursor.getString(2));
                us.setUUsDesc(cursor.getString(3));
                us.setUUsGroup(Integer.parseInt(cursor.getString(4)));
                us.setUUsToken(cursor.getString(5));
                us.setUUsAva(cursor.getString(6));
                us.setUUsIsIn(cursor.getString(7));
                us.setUUsStep(cursor.getString(8));
                us.setUUsGR(cursor.getString(9));
                lists.add(us);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }

    public boolean checkUUs(int id)
    {
        String sql = "SELECT * FROM " + ModelsTableColumn.UUS_TBL + " WHERE " + ModelsTableColumn.UUS_ID +
                "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //UUS END
    public void addUSch(USch sch){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.SCH_DATE, sch.getSchDate());
        values.put(ModelsTableColumn.SCH_LOC, sch.getSchLocation());
        values.put(ModelsTableColumn.SCH_ID, sch.getSchID());
        values.put(ModelsTableColumn.SCH_TITLE, sch.getSchTitle());
        values.put(ModelsTableColumn.SCH_DESC, sch.getSchDesc());
        values.put(ModelsTableColumn.SCH_REPEAT, sch.getSchRepeat());
        values.put(ModelsTableColumn.SCH_MILIS, sch.getSchMilis());
        db.insert(ModelsTableColumn.SCH_TBL, null, values);
        db.close();
    }

    public void updateUSch(USch sch){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.SCH_DATE, sch.getSchDate());
        values.put(ModelsTableColumn.SCH_LOC, sch.getSchLocation());
        values.put(ModelsTableColumn.SCH_ID, sch.getSchID());
        values.put(ModelsTableColumn.SCH_TITLE, sch.getSchTitle());
        values.put(ModelsTableColumn.SCH_DESC, sch.getSchDesc());
        values.put(ModelsTableColumn.SCH_REPEAT, sch.getSchRepeat());
        values.put(ModelsTableColumn.SCH_MILIS, sch.getSchMilis());
        db.update(ModelsTableColumn.SCH_TBL, values, ModelsTableColumn.SCH_ID + " = ?", new String[]
                {String.valueOf(sch.getSchID())});
        db.close();
    }

    public List<USch> getAllUSch(int m, int y, String d)
    {
        List<USch> lists = new ArrayList<USch>();
        String sql = "SELECT * FROM " + ModelsTableColumn.SCH_TBL+" ";
        if(m != 0 && y != 0)
        {
            sql += "WHERE strftime('%m',"+ModelsTableColumn.SCH_DATE+") = "+Integer.toString(m)+" AND strftime('%y',"+
                    ModelsTableColumn.SCH_DATE+") = "+ Integer.toString(y)+" ";
        }

        if(!d.equals(""))
        {
            sql += " WHERE "+ModelsTableColumn.SCH_DATE+" >= '" + d + " 00:00:00' AND "+ModelsTableColumn.SCH_DATE+" <= '"+
            d+" 23:59:59'";
        }

        //sql = "SELECT * FROM " + ModelsTableColumn.SCH_TBL+" ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do{
                USch sch = new USch();
                sch.setSchID(Integer.parseInt(cursor.getString(0)));
                sch.setSchTitle(cursor.getString(1));
                sch.setSchDesc(cursor.getString(2));
                sch.setSchDate(cursor.getString(3));
                sch.setSchLocation(cursor.getString(4));
                sch.setSchRepeat(cursor.getString(5));
                sch.setSchMilis(cursor.getString(6));
                lists.add(sch);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }

    public boolean checkUSch(int id)
    {
        String sql = "SELECT * FROM " + ModelsTableColumn.SCH_TBL + " WHERE " + ModelsTableColumn.SCH_ID +
                "=" + Integer.toString(id);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    //Segment UState

    public void addUState(UState state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.STATE_ID, state.getStateID());
        values.put(ModelsTableColumn.STATE_NAME, state.getStateName());
        values.put(ModelsTableColumn.STATE_PROV, state.getProvID());
        db.insert(ModelsTableColumn.STATE_TBL, null, values);
        db.close();
    }

    public void updateUState(UState state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ModelsTableColumn.STATE_ID, state.getStateID());
        values.put(ModelsTableColumn.STATE_NAME, state.getStateName());
        values.put(ModelsTableColumn.STATE_PROV, state.getProvID());
        db.update(ModelsTableColumn.STATE_TBL, values, ModelsTableColumn.STATE_ID + " = ?", new String[]
                {String.valueOf(state.getStateID())});
        db.close();
    }

    public List<UState> getAllUState()
    {
        List<UState> lists = new ArrayList<UState>();
        String sql = "SELECT * FROM " + ModelsTableColumn.STATE_TBL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            do{
                UState state = new UState();
                state.setStateID(Integer.parseInt(cursor.getString(0)));
                state.setStateName(cursor.getString(1));
                state.setProvID(cursor.getString(2));
                lists.add(state);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }

    public boolean checkUState(int id)
    {
        String sql = "SELECT * FROM " + ModelsTableColumn.STATE_TBL + " WHERE " + ModelsTableColumn.STATE_ID +
                "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //End Segment UState






    //CREATE
    private void create_UGroup(SQLiteDatabase db)
    {
        String TBL_UGROUP = "CREATE TABLE " + ModelsTableColumn.USERS_GROUP_TBL +" (" +
                ModelsTableColumn.USERS_GROUP_ID + " INTEGER PRIMARY KEY, " + ModelsTableColumn.USERS_GROUP_NAME + " TEXT, " +
                ModelsTableColumn.USERS_GROUP_DESC + " TEXT )";
        db.execSQL("DROP TABLE IF EXISTS " + ModelsTableColumn.USERS_GROUP_TBL);
        db.execSQL(TBL_UGROUP);
    }

    private void create_UUs(SQLiteDatabase db)
    {
        String TBL_UUS = "CREATE TABLE " + ModelsTableColumn.UUS_TBL +" (" +
                ModelsTableColumn.UUS_ID + " INTEGER PRIMARY KEY, " + ModelsTableColumn.UUS_NAME + " TEXT, " +
                ModelsTableColumn.UUS_FULL + " TEXT, "+ModelsTableColumn.UUS_DESC+" TEXT, "+ModelsTableColumn.UUS_GROUP+" INTEGER" +
                ", "+ModelsTableColumn.UUS_TOKEN+" TEXT, "+ModelsTableColumn.UUS_AVA+" TEXT, "+ModelsTableColumn.UUS_IS_IN+" TEXT"+
                ", "+ModelsTableColumn.UUS_STEP+" TEXT,"+ModelsTableColumn.UUS_HASGR+" TEXT )";
        db.execSQL("DROP TABLE IF EXISTS " + ModelsTableColumn.UUS_TBL);
        db.execSQL(TBL_UUS);
    }

    private void create_USch(SQLiteDatabase db)
    {
        String TBL_SCH = "CREATE TABLE " + ModelsTableColumn.SCH_TBL +" (" +
                ModelsTableColumn.SCH_ID + " INTEGER PRIMARY KEY, " + ModelsTableColumn.SCH_TITLE + " TEXT, " +
                ModelsTableColumn.SCH_DESC + " TEXT, "+ModelsTableColumn.SCH_DATE+" DATETIME, "+ModelsTableColumn.SCH_LOC+" TEXT" +
                ", "+ModelsTableColumn.SCH_REPEAT+" TEXT, "+ModelsTableColumn.SCH_MILIS+" TEXT)";
        db.execSQL("DROP TABLE IF EXISTS " + ModelsTableColumn.SCH_TBL);
        db.execSQL(TBL_SCH);
    }

    private void create_UState(SQLiteDatabase db)
    {
        String TBL_USTATE = "CREATE TABLE " + ModelsTableColumn.STATE_TBL +" (" +
                ModelsTableColumn.STATE_ID + " INTEGER PRIMARY KEY, " + ModelsTableColumn.STATE_NAME + " TEXT, " +
                ModelsTableColumn.STATE_PROV + " TEXT )";
        db.execSQL("DROP TABLE IF EXISTS " + ModelsTableColumn.STATE_TBL);
        db.execSQL(TBL_USTATE);
    }
}
