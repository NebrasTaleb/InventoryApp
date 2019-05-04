package com.example.nebras.inventoryappstagetwo.SQLite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.nebras.inventoryappstagetwo.SQLite.InventoryContract.ProductsTable;

public class ProductProvider extends ContentProvider {

    private InventoryDbHelper inventoryDbHelper;
    //Uri matcher codes
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsTable.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match : " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = inventoryDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductsTable.TABLE_NAME, projection, selection, selectionArgs, null
                        , null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductsTable.PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductsTable.TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query in the provider with this uri : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        long id = db.insert(ProductsTable.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        if (id != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = inventoryDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int updatedRows;
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                updatedRows = database.delete(ProductsTable.TABLE_NAME, selection, selectionArgs);
                if (updatedRows != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return updatedRows;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductsTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRows = database.delete(ProductsTable.TABLE_NAME, selection, selectionArgs);
                if (updatedRows != 0) {
                    getContext().getContentResolver().notifyChange(ProductsTable.CONTENT_URI, null);
                }
                return updatedRows;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductsTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0)
            return 0;
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        if (values.containsKey(ProductsTable.PRODUCT_NAME)) {
            String productName = values.getAsString(ProductsTable.PRODUCT_NAME);
            if (productName == null)
                throw new IllegalArgumentException("product requires a name.");
        }

        if (values.containsKey(ProductsTable.PRODUCT_PRICE)) {
            Float productPrice = values.getAsFloat(ProductsTable.PRODUCT_PRICE);
            if (productPrice == null)
                throw new IllegalArgumentException("product requires a price.");
        }

        if (values.containsKey(ProductsTable.QUANTITY)) {
            Integer productQuantity = values.getAsInteger(ProductsTable.QUANTITY);
            if (productQuantity < 0)
                throw new IllegalArgumentException("product requires valid quantity.");
        }


        if (values.containsKey(ProductsTable.SUPPLIER_NAME)) {
            String productSupplierName = values.getAsString(ProductsTable.SUPPLIER_NAME);
            if (productSupplierName == null)
                throw new IllegalArgumentException("product requires supplier name.");
        }


        if (values.containsKey(ProductsTable.SUPPLIER_PHONE_NUMBER)) {
            String productSupplierPhoneNumber = values.getAsString(ProductsTable.QUANTITY);
            if (productSupplierPhoneNumber == null)
                throw new IllegalArgumentException("product requires supplier phone number.");
        }

        int updatedRows = db.update(ProductsTable.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }
}




