package com.hh.sdk.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by engine on 15/1/29.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "zhidian_cpa.db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_ORDER_TABLE = "CREATE TABLE "+OrderColum.TABLE_NAME + " ("
            + OrderColum.ORDER_NO + " TEXT PRIMARY KEY ,"
            + OrderColum.STATUS + " int,"
            + OrderColum.GIVE_OUT + " int,"
            + OrderColum.AMOUNT + " int,"
            + OrderColum.CP_ORDER_ID + " TEXT,"
            + OrderColum.EXT_INFO+ " TEXT"
            + ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ORDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    class OrderColum {
        public static final  String TABLE_NAME = "order_t" ;
        public static final String ORDER_NO = "orderNo";
        public static final String STATUS = "status";
        public static final String GIVE_OUT = "giveOut";
        public static final String AMOUNT = "amount";
        public static final String CP_ORDER_ID = "cp_order_id";
        public static final String EXT_INFO = "ext_info";
    }
}
