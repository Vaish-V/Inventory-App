package com.example.vaish.inventory_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Vaish on 14-10-2016.
 */
public class ProductDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "products.db";


    private static final int DATABASE_VERSION = 1;


    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_TABLE =  "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_BATCH + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_SHIPMENT + " INTEGER DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_SOLD + " INTEGER DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PICTURE + " BLOB)";

        Log.v("Query"," "+SQL_CREATE_TABLE);



        db.execSQL(SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
