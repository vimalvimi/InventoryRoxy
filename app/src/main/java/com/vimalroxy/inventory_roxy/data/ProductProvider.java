package com.vimalroxy.inventory_roxy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.vimalroxy.inventory_roxy.data.ProductContract.CONTENT_AUTHORITY;
import static com.vimalroxy.inventory_roxy.data.ProductContract.CONTENT_ITEM_TYPE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.CONTENT_LIST_TYPE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.PATH_PRODUCTS;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry._ID;

public class ProductProvider extends ContentProvider {

    private static final String TAG = "ProductProvider";

    private ProductDbHelper mDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            default:
                throw new IllegalStateException("Cannot Query Unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalStateException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = contentValues.getAsInteger(COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product requires a Price");
        }

        Integer quantity = contentValues.getAsInteger(COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product requires a Quantity");
        }

        String sName = contentValues.getAsString(COLUMN_SUPPLIER_NAME);
        if (sName == null) {
            throw new IllegalArgumentException("Product requires a Supplier Name");
        }

        Integer sPhone = contentValues.getAsInteger(COLUMN_SUPPLIER_PHONE);
        if (sPhone == null) {
            throw new IllegalArgumentException("Product requires a Supplier Number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Track the number of rows that were deleted
        int rowsDeleted;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection,
                              String[] selectionArgs) {

        if (values.containsKey(COLUMN_PRICE)) {
            Integer price = values.getAsInteger(COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires a Price");
            }
        }

        if (values.containsKey(COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(COLUMN_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires a Quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}