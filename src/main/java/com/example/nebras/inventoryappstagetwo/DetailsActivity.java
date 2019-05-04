package com.example.nebras.inventoryappstagetwo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nebras.inventoryappstagetwo.SQLite.InventoryContract.ProductsTable;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri currentUri;
    private TextView productOverviewTextView;
    private TextView priceTextView;
    private TextView quantityTextView;
    private TextView supplierNameTextView;
    private TextView supplierPhoneNumberTextView;
    public static final int PRODUCT_LOADER = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.up_button_menu_item) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        currentUri = getIntent().getData();
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        productOverviewTextView = findViewById(R.id.product_overview_details);
        priceTextView = findViewById(R.id.price_details);
        quantityTextView = findViewById(R.id.quantity_details);
        supplierNameTextView = findViewById(R.id.supplier_name_details);
        supplierPhoneNumberTextView = findViewById(R.id.supplier_phone_num_details);
        Button callSupplierButton = findViewById(R.id.call_supplier_button);
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumberTextView.getText().toString().trim()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
        Button editProductButton = findViewById(R.id.edit_product_button);
        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(currentUri);
                startActivity(intent);

            }
        });
        Button deleteProductButton = findViewById(R.id.delete_product_button);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        Button quantityIncreaseButton = findViewById(R.id.quantity_increase_button_details);
        quantityIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quantityTextView.getText().toString().equals(getApplicationContext().
                        getString(R.string.add_quantity_here))) {
                    int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
                    currentQuantity++;
                    quantityTextView.setText("" + currentQuantity);
                    Uri uri = Uri.withAppendedPath(ProductsTable.CONTENT_URI, currentUri.getLastPathSegment());
                    String selection = ProductsTable._ID + "=?";
                    String[] selectionArgs = new String[]{currentUri.getLastPathSegment()};
                    ContentValues values = new ContentValues();
                    values.put(ProductsTable.QUANTITY, currentQuantity);
                    getContentResolver().update(uri, values, selection, selectionArgs);
                }
            }
        });
        Button quantityDecreaseButton = findViewById(R.id.quantity_decrease_button_details);
        quantityDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quantityTextView.getText().toString().equals(getApplicationContext().
                        getString(R.string.add_quantity_here))) {
                    int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
                    if (currentQuantity > 0) {
                        currentQuantity--;
                        quantityTextView.setText("" + currentQuantity);

                        Uri uri = Uri.withAppendedPath(ProductsTable.CONTENT_URI, currentUri.getLastPathSegment());
                        String selection = ProductsTable._ID + "=?";
                        String[] selectionArgs = new String[]{currentUri.getLastPathSegment()};
                        ContentValues values = new ContentValues();
                        values.put(ProductsTable.QUANTITY, currentQuantity);
                        getContentResolver().update(uri, values, selection, selectionArgs);
                    }
                    if (currentQuantity == 0) {
                        Toast.makeText(DetailsActivity.this, R.string.stock_empty_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteProduct() {
        int id = getContentResolver().delete(currentUri, null, null);
        if (id != 0) {
            Toast.makeText(this, R.string.delete_done_msg, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.delete_error_msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, currentUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        cursor.moveToFirst();
        String productOverview = cursor.getString(cursor.getColumnIndex(ProductsTable.PRODUCT_NAME));
        float productPrice = cursor.getFloat(cursor.getColumnIndex(ProductsTable.PRODUCT_PRICE));
        int productQuantity = cursor.getInt(cursor.getColumnIndex(ProductsTable.QUANTITY));
        String productSupplierName = cursor.getString(cursor.getColumnIndex(ProductsTable.SUPPLIER_NAME));
        String ProductSupplierPhoneNumber = cursor.getString(cursor.getColumnIndex(ProductsTable.SUPPLIER_PHONE_NUMBER));
        if (productPrice == 0.0) {
            priceTextView.setText(getString(R.string.add_price_here));
        }
        if (productQuantity == 0) {
            quantityTextView.setText(getString(R.string.add_quantity_here));
        }
        if (TextUtils.isEmpty(productSupplierName)) {
            productSupplierName = getString(R.string.add_supplier_name_here);
        }
        if (TextUtils.isEmpty(ProductSupplierPhoneNumber)) {
            ProductSupplierPhoneNumber = getString(R.string.add_supplier_phone_number_here);
        }
        productOverviewTextView.setText(productOverview);
        priceTextView.setText("" + productPrice);
        quantityTextView.setText("" + productQuantity);
        supplierNameTextView.setText(productSupplierName);
        supplierPhoneNumberTextView.setText(ProductSupplierPhoneNumber);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productOverviewTextView.setText("");
        priceTextView.setText("");
        quantityTextView.setText("");
        supplierNameTextView.setText("");
        supplierPhoneNumberTextView.setText("");
    }
}
