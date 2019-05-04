package com.example.nebras.inventoryappstagetwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nebras.inventoryappstagetwo.SQLite.InventoryContract.ProductsTable;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(final Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final ContentValues values = new ContentValues();

        final TextView productOverviewTextView = view.findViewById(R.id.product_overview);
        TextView priceTextView = view.findViewById(R.id.price);
        final TextView quantityTextView = view.findViewById(R.id.quantity);
        final Button sellButton = view.findViewById(R.id.sell_button);
        String product = cursor.getString(cursor.getColumnIndex(ProductsTable.PRODUCT_NAME));
        float price = cursor.getFloat(cursor.getColumnIndex(ProductsTable.PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductsTable.QUANTITY));
        productOverviewTextView.setText(product);
        if (price == 0) {
            priceTextView.setText(context.getString(R.string.not_provided));
        } else {
            priceTextView.setText("" + price);
        }
        if (quantity == 0) {
            quantityTextView.setText(R.string.not_provided_or_stock_empty);
        } else {
            quantityTextView.setText("" + quantity);
        }
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentQuantity;
                if (!quantityTextView.getText().toString().equals(context.getString(R.string.not_provided_or_stock_empty))) { // i.e. contains number and can be reduced
                    currentQuantity = Integer.parseInt(String.valueOf(quantityTextView.getText()));
                    if (currentQuantity > 0) {
                        currentQuantity--;
                        values.put(ProductsTable.QUANTITY, currentQuantity);
                        quantityTextView.setText("" + currentQuantity);
                        if (currentQuantity == 0) {
                            Toast.makeText(context, R.string.stock_empty_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    String [] projectionForId = new String [] {ProductsTable.PRODUCT_ID};
                    String selectionForQuery = ProductsTable.PRODUCT_NAME + "=?";
                    String  [] selectionArgsForQuery = new String []{ productOverviewTextView.getText().toString()};
                    Cursor productNameOfClickedButtonCursor = context.getContentResolver().query(ProductsTable.CONTENT_URI,projectionForId
                            ,selectionForQuery,selectionArgsForQuery,null,null);
                    productNameOfClickedButtonCursor.moveToNext();
                    int updatedItemId = productNameOfClickedButtonCursor.getInt(productNameOfClickedButtonCursor.
                            getColumnIndex(ProductsTable.PRODUCT_ID));
                    //update the quantity
                    Uri uri = Uri.withAppendedPath(ProductsTable.CONTENT_URI, String.valueOf(updatedItemId));
                    String selection = ProductsTable._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(updatedItemId)};

                    context.getContentResolver().update(uri, values, selection, selectionArgs);
                }
            }
        });
    }
}
