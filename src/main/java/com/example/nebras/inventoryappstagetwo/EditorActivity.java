package com.example.nebras.inventoryappstagetwo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nebras.inventoryappstagetwo.SQLite.InventoryContract.ProductsTable;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }

    };
    private EditText mProductOverview;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mProductSupplierName;
    private EditText mProductSupplierPhoneNumber;
    private Button mAddNewProductButton;

    private int clickedProductId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.exit_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.exit_button_menu_item:
                // If the product hasn't changed, finish the editor activity
                if (!mProductHasChanged) {
                    finish();
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_layout);
        mProductOverview = findViewById(R.id.product_overview_edit_text);
        mProductOverview.setOnTouchListener(mTouchListener);
        mProductPrice = findViewById(R.id.price_edit_text);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity = findViewById(R.id.quantity_edit_text);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductSupplierName = findViewById(R.id.supplier_name_edit_text);
        mProductSupplierName.setOnTouchListener(mTouchListener);
        mProductSupplierPhoneNumber = findViewById(R.id.supplier_phone_num_edit_text);
        mProductSupplierPhoneNumber.setOnTouchListener(mTouchListener);
        mAddNewProductButton = findViewById(R.id.add_new_product_button);
        mAddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int saveProductResult = saveProduct();
                if (saveProductResult == 0) {
                    finish();
                }
            }
        });

        Uri uri = getIntent().getData();
        if (uri == null) {
            this.setTitle("Add a Product");
            invalidateOptionsMenu();
        } else {
            clickedProductId = Integer.parseInt(uri.getLastPathSegment());
            this.setTitle("Edit Product");
            mAddNewProductButton.setText(R.string.editor_update_mode_button_text);
            getLoaderManager().initLoader(MainActivity.PRODUCT_LOADER, null, this);
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
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
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private int saveProduct() {
        if (TextUtils.isEmpty(mProductOverview.getText().toString().trim())) {
            // it is now allowed to not provide overview for the product
            Toast.makeText(this, R.string.product_name_required_msg, Toast.LENGTH_LONG).show();
            return 1; // 1 means don't save and don't finish the editor activity
        }
        if (TextUtils.isEmpty(mProductPrice.getText().toString().trim())) {
            // it is now allowed to not provide price for the product
            Toast.makeText(this, R.string.product_price_required_msg, Toast.LENGTH_LONG).show();
            return 1; // 1 means don't save and don't finish the editor activity
        }
        if (TextUtils.isEmpty(mProductQuantity.getText().toString().trim())) {
            // it is now allowed to not provide quantity for the product
            Toast.makeText(this, R.string.product_quantity_required_msg, Toast.LENGTH_LONG).show();
            return 1; // 1 means don't save and don't finish the editor activity
        }
        if (TextUtils.isEmpty(mProductSupplierName.getText().toString().trim())) {
            // it is now allowed to not provide supplier name for the product
            Toast.makeText(this, R.string.supplier_name_required_msg, Toast.LENGTH_LONG).show();
            return 1; // 1 means don't save and don't finish the editor activity
        }
        if (TextUtils.isEmpty(mProductSupplierPhoneNumber.getText().toString().trim())) {
            // it is now allowed to not provide supplier phone number for the product
            Toast.makeText(this, R.string.supplier_phone_number_required_msg, Toast.LENGTH_LONG).show();
            return 1; // 1 means don't save and don't finish the editor activity
        }
        ContentValues values = new ContentValues();
        values.put(ProductsTable.PRODUCT_NAME, mProductOverview.getText().toString().trim());
        values.put(ProductsTable.PRODUCT_PRICE, mProductPrice.getText().toString().trim());
        values.put(ProductsTable.QUANTITY, mProductQuantity.getText().toString().trim());
        values.put(ProductsTable.SUPPLIER_NAME, mProductSupplierName.getText().toString().trim());
        values.put(ProductsTable.SUPPLIER_PHONE_NUMBER, mProductSupplierPhoneNumber.getText().toString().trim());

        long id;

        if (getIntent().getData() == null) { // we came here (to the editor) by pressing the add button

            Uri rowId = getContentResolver().insert(ProductsTable.CONTENT_URI, values);
            if (Integer.parseInt(rowId.getLastPathSegment()) != -1) {
                Toast.makeText(this, R.string.saving_done_msg, Toast.LENGTH_SHORT).show();
                getContentResolver().notifyChange(ProductsTable.CONTENT_URI, null);
            } else {
                Toast.makeText(this, R.string.saving_error_msg, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Tip: product overview must be unique", Toast.LENGTH_LONG).show();
                return 1; // 1 means don't save and don't finish the editor activity
            }
        } else { // we came here by pressing on the edit button
            if (!mProductHasChanged) {
                return 0; // the user has not changed anything so finish the editor
            }
            id = getContentResolver().update(getIntent().getData(), values, null, null);
            if (id != 0) {
                Toast.makeText(this, R.string.update_done_msg, Toast.LENGTH_SHORT).show();
                getContentResolver().notifyChange(ProductsTable.CONTENT_URI, null);
            } else {
                Toast.makeText(this, R.string.update_error_msg, Toast.LENGTH_SHORT).show();
            }
        }
        return 0; // 0 means save if no error and finish the editor activity
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uriForOneProduct = Uri.withAppendedPath(ProductsTable.CONTENT_URI, String.valueOf(clickedProductId));
        return new CursorLoader(this, uriForOneProduct, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToNext()) {
            mProductOverview.setText(cursor.getString(cursor.getColumnIndex(ProductsTable.PRODUCT_NAME)));
            mProductPrice.setText("" + cursor.getFloat(cursor.getColumnIndex(ProductsTable.PRODUCT_PRICE)));
            mProductQuantity.setText("" + cursor.getInt(cursor.getColumnIndex(ProductsTable.QUANTITY)));
            mProductSupplierName.setText(cursor.getString(cursor.getColumnIndex(ProductsTable.SUPPLIER_NAME)));
            mProductSupplierPhoneNumber.setText(cursor.getString(cursor.getColumnIndex(ProductsTable.SUPPLIER_PHONE_NUMBER)));

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mProductOverview.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mProductSupplierName.setText("");
        mProductSupplierPhoneNumber.setText("");
    }
}
