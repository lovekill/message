package com.hh.sdk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hh.sdk.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by engine on 15/1/29.
 */
public class OrderDao {
    private DBHelper mDBHelper ;
    private SQLiteDatabase mDb;
    private String[] colum ={DBHelper.OrderColum.EXT_INFO, DBHelper.OrderColum.CP_ORDER_ID, DBHelper.OrderColum.AMOUNT, DBHelper.OrderColum.GIVE_OUT, DBHelper.OrderColum.ORDER_NO, DBHelper.OrderColum.STATUS} ;
    private OrderDao(Context context){
        mDBHelper = new DBHelper(context);
        mDb= mDBHelper.getWritableDatabase() ;
    }
    private static OrderDao instance ;
    public static OrderDao getInstance(Context context){
        if(instance==null){
            instance = new OrderDao(context) ;
        }
       return  instance ;
    }
    public void addOrder(Order order){
        if(getOrderByID(order.orderNo)!=null){
            deleteOrderById(order.orderNo);
        }
        try {
            ContentValues contentValues = new ContentValues() ;
            contentValues.put(DBHelper.OrderColum.ORDER_NO,order.orderNo);
            contentValues.put(DBHelper.OrderColum.AMOUNT,order.amount);
            contentValues.put(DBHelper.OrderColum.CP_ORDER_ID,order.cpOrderId);
            contentValues.put(DBHelper.OrderColum.EXT_INFO,order.extInfo);
            contentValues.put(DBHelper.OrderColum.GIVE_OUT,order.giveOut);
            contentValues.put(DBHelper.OrderColum.STATUS,order.status);
            mDb.insert(DBHelper.OrderColum.TABLE_NAME,null,contentValues) ;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Order getOrderByID(String orderId){
            Cursor cursor = mDb.query(DBHelper.OrderColum.TABLE_NAME,null, DBHelper.OrderColum.ORDER_NO + "=?", new String[]{orderId}, null, null, null) ;
            if(cursor.moveToNext()){
                Order order = coventOrderFormCursor(cursor) ;
                cursor.close();
                return  order ;
            }else {
                return  null ;
            }

    }

    public void deleteOrderById(String orderId){
        mDb.delete(DBHelper.OrderColum.TABLE_NAME, DBHelper.OrderColum.ORDER_NO+"=?",new String[]{orderId}) ;
    }
    public List<Order> getCricleOrder(){
      Cursor cursor = mDb.query(DBHelper.OrderColum.TABLE_NAME,null,DBHelper.OrderColum.STATUS+"=2",null,null,null,null) ;
        List<Order> list = new ArrayList<Order>() ;
        while (cursor.moveToNext()){
            Order order = coventOrderFormCursor(cursor) ;
            list.add(order);
        }
        cursor.close();
//        mDb.close();
        return list ;
    }
    private Order coventOrderFormCursor(Cursor cursor){
        Order order = new Order() ;
        order.orderNo= cursor.getString(cursor.getColumnIndex(DBHelper.OrderColum.ORDER_NO)) ;
        order.amount = cursor.getInt(cursor.getColumnIndex(DBHelper.OrderColum.AMOUNT)) ;
        order.cpOrderId = cursor.getString(cursor.getColumnIndex(DBHelper.OrderColum.CP_ORDER_ID)) ;
        order.extInfo = cursor.getString(cursor.getColumnIndex(DBHelper.OrderColum.EXT_INFO)) ;
        order.giveOut = cursor.getInt(cursor.getColumnIndex(DBHelper.OrderColum.GIVE_OUT)) ;
        order.status = cursor.getInt(cursor.getColumnIndex(DBHelper.OrderColum.STATUS)) ;
        return order ;
    }
}
