package com.example.nebras.inventoryappstagetwo.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public final static String DATA_BASE_NAME = "inventory.db";
    public final static int DATA_BASE_VERSION = 1;
    public final static String SQL_CREATE_STATEMENT = "CREATE TABLE " + InventoryContract.ProductsTable.TABLE_NAME + " ( " +
            InventoryContract.ProductsTable.PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryContract.ProductsTable.PRODUCT_NAME + " TEXT NOT NULL UNIQUE, " +
            InventoryContract.ProductsTable.PRODUCT_PRICE + " FLOAT, " +
            InventoryContract.ProductsTable.QUANTITY + " INTEGER, " +
            InventoryContract.ProductsTable.SUPPLIER_NAME + " TEXT, " +
            InventoryContract.ProductsTable.SUPPLIER_PHONE_NUMBER + " TEXT " + ");";
    public final static String SQL_DELETE_STATEMENT = "DROP TABLE IF EXISTS " + InventoryContract.ProductsTable.TABLE_NAME + ";";

    public InventoryDbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_STATEMENT);
        onCreate(db);
    }
}
