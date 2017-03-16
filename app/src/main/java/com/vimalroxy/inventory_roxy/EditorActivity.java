package com.vimalroxy.inventory_roxy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vimalroxy.inventory_roxy.data.ProductContract;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //LOG tag
    private static final String TAG = "EditorActivity";

    //UI Elements
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQuantity;
    private EditText mSupplierName;
    private EditText mSupplierNumber;
    private ImageView mImageBanner;

    private Button Contact;
    private Button orderButton;

    //Identifiers
    private static final int EXISTING_PET_LOADER = 0;
    private static final int PICK_IMAGE = 100;

    //Validators
    private boolean mItemHasChanged = false;

    private String mCurrentImageBannerURI = "NULL";
    private Uri mCurrentItemUri;

    //Email Content
    private String mOrderEmail;
    private String mOrderContent;
    private int mOrderQuantity;
    private String mOrderSupplierName;

    //Projection Array
    String[] projection = {
            ProductContract.ProductEntry._ID,
            ProductContract.ProductEntry.COLUMN_NAME,
            ProductContract.ProductEntry.COLUMN_PRICE,
            ProductContract.ProductEntry.COLUMN_QUANTITY,
            ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
            ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE,
            ProductContract.ProductEntry.COLUMN_IMAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Remove auto pop-up keyboard
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Get info from intent
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.title_add_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_edit_item));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        //UI CASTING in Edit Activity
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_Quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name);
        mSupplierNumber = (EditText) findViewById(R.id.edit_product_supplier_number);
        mImageBanner = (ImageView) findViewById(R.id.editor_image);

        //onTouchListener
        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierNumber.setOnTouchListener(mTouchListener);
        mImageBanner.setOnTouchListener(mTouchListener);

        //Buttons in Edit Activity
        Contact = (Button) findViewById(R.id.contact_button);
        orderButton = (Button) findViewById(R.id.button_order);

        Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderPhone();
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderEmail();
            }
        });

        //ImageUpload
        mImageBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                mCurrentImageBannerURI = String.valueOf(uri);

                Picasso.with(this)
                        .load(mCurrentImageBannerURI)
                        .placeholder(R.drawable.insert_photo_icon)
                        .error(R.drawable.insert_photo_icon)
                        .into(mImageBanner);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.editor_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor_delete:
                deleteParticular();
                finish();
                return true;
            case R.id.editor_save:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteParticular() {
        int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
        Toast.makeText(this, getString(R.string.delete_successful),
                Toast.LENGTH_SHORT).show();
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,        // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int idColumnsIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);
            int sImageColumnIndex = cursor.getColumnIndex(
                    ProductContract.ProductEntry.COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sPhone = cursor.getString(sPhoneColumnIndex);
            mCurrentImageBannerURI = cursor.getString(sImageColumnIndex);

            mProductName.setText(name);
            mProductPrice.setText(price);
            mProductQuantity.setText(quantity);
            mSupplierName.setText(sName);
            mSupplierNumber.setText(sPhone);

            //Disable Text Fields in Edit Menu
            disableEditText(mProductName);
            disableEditText(mSupplierName);
            disableEditText(mSupplierNumber);

            //New Order Intent
            mOrderEmail = "support@" + sName.toLowerCase().replace(" ", "") + ".com";
            mOrderContent = name;
            mOrderSupplierName = sName;
            mOrderQuantity = Integer.parseInt((quantity) + 2 * 2);

            Picasso.with(this)
                    .load(mCurrentImageBannerURI)
                    .placeholder(R.drawable.insert_photo_icon)
                    .error(R.drawable.insert_photo_icon)
                    .into(mImageBanner);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mSupplierName.setText("");
        mSupplierNumber.setText("");
    }

    private void saveItem() {
        //Adding EditText Values in String Variables.
        String mProductName = this.mProductName.getText().toString().trim();
        String mProductPrice = this.mProductPrice.getText().toString().trim();
        String mProductQuantity = this.mProductQuantity.getText().toString().trim();
        String mSupplierName = this.mSupplierName.getText().toString().trim();
        String mSupplierNumber = this.mSupplierNumber.getText().toString().trim();

        //Check if all fields are filled
        if (TextUtils.isEmpty(mProductName) ||
                TextUtils.isEmpty(mProductPrice) ||
                TextUtils.isEmpty(mProductQuantity) ||
                TextUtils.isEmpty(mSupplierName) ||
                TextUtils.isEmpty(mSupplierNumber)) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_LONG).show();
            return;
        }

        //Converting Required String Values in int.
        int mProductPriceInt = Integer.parseInt(mProductPrice);
        int mProductQuantityInt = Integer.parseInt(mProductQuantity);
        int mSupplierNumberInt = Integer.parseInt(mSupplierNumber);

        //Putting Values In Columns
        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_NAME, mProductName);
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, mProductPriceInt);
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, mProductQuantityInt);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, mSupplierName);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE, mSupplierNumberInt);
        values.put(ProductContract.ProductEntry.COLUMN_IMAGE, mCurrentImageBannerURI);

        // Show a toast message depending on whether or not the insertion was successful
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(
                    ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

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
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
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

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
    }

    //ORDER
    private void orderEmail() {

        String[] address = {mOrderEmail};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("*/*");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Order : " + mOrderContent);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi " + mOrderSupplierName + ", " + "\n" +
                "Please Deliver " + mOrderContent + "\n" +
                "Quantity : " + mOrderQuantity);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void orderPhone() {
        String phone = mSupplierNumber.getText().toString();
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                "tel", phone, null));
        startActivity(phoneIntent);
    }
}
