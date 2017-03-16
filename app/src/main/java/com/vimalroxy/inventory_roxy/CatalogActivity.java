package com.vimalroxy.inventory_roxy;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vimalroxy.inventory_roxy.data.ProductContract;
import com.vimalroxy.inventory_roxy.data.ProductsRandom;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mProductCursorAdapter;

    private static final String TAG = "CatalogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find ListView to populate
        ListView lvItems = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);

        //Setup Adapter
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        lvItems.setAdapter(mProductCursorAdapter);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //Setup item click listener
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(

                    AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);

                Uri intentUri = ContentUris.withAppendedId(
                        ProductContract.ProductEntry.CONTENT_URI, id);

                intent.setData(intentUri);
                startActivity(intent);
            }
        });

        //Kickoff Loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void InsertItem() {

        ProductsRandom randomClass = new ProductsRandom();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.
                COLUMN_NAME, randomClass.getRandomName());
        values.put(ProductContract.ProductEntry.
                COLUMN_IMAGE, randomClass.getRandomImageURI());
        values.put(ProductContract.ProductEntry.
                COLUMN_PRICE, randomClass.getRandomPrice());
        values.put(ProductContract.ProductEntry.
                COLUMN_QUANTITY, randomClass.getRandomQuantity());
        values.put(ProductContract.ProductEntry.
                COLUMN_SUPPLIER_NAME, randomClass.getRandomSupplierName());
        values.put(ProductContract.ProductEntry.
                COLUMN_SUPPLIER_PHONE, randomClass.getRandomSupplierNumber());
        values.put(ProductContract.ProductEntry.
                COLUMN_SOLD, 0);

        Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_random_data:
                InsertItem();
                return true;
            case R.id.action_delete_All:
                deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(
                ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_IMAGE,
                ProductContract.ProductEntry.COLUMN_SOLD};

        String sortOrder = getString(R.string.sort_order);

        return new CursorLoader(
                this, ProductContract.ProductEntry.CONTENT_URI, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mProductCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProductCursorAdapter.swapCursor(null);
    }
}