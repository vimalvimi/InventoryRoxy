package com.vimalroxy.inventory_roxy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "products.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + ProductContract.ProductEntry.COLUMN_SOLD + " INTEGER NOT NULL DEFAULT 0,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE + " INTEGER,"
                + ProductContract.ProductEntry.COLUMN_IMAGE + " TEXT);";

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
