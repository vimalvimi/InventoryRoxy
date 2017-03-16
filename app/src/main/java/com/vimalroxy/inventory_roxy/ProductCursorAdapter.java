package com.vimalroxy.inventory_roxy;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vimalroxy.inventory_roxy.data.ProductContract;

import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_IMAGE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_SOLD;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.vimalroxy.inventory_roxy.data.ProductContract.ProductEntry._ID;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/*FLAGS*/);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        //Get Fields
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvSeller = (TextView) view.findViewById(R.id.seller);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        ImageView tvImage = (ImageView) view.findViewById(R.id.list_image);
        TextView tvInventory = (TextView) view.findViewById(R.id.inventory_update);
        TextView tvSold = (TextView) view.findViewById(R.id.sold_update);
        TextView tvButton = (TextView) view.findViewById(R.id.sell_button);

        //Find columns
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int sellerColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);
        int inventoryColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int soldColumnIndex = cursor.getColumnIndex(COLUMN_SOLD);

        //Read attributes from cursor and write to string
        final int currentId = cursor.getInt(cursor.getColumnIndex(_ID));
        String sName = cursor.getString(nameColumnIndex);
        String sSeller = cursor.getString(sellerColumnIndex);
        String sPrice = "$ " + cursor.getString(priceColumnIndex);
        String thumbUri = cursor.getString(imageColumnIndex);
        final int sInventory = cursor.getInt(inventoryColumnIndex);
        final int sSold = cursor.getInt(soldColumnIndex);
        final Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, currentId);

        String itemInventory = String.valueOf(sInventory);
        String itemSold = String.valueOf(sSold);

        //Populate fields with extracted properties
        tvName.setText(sName);
        tvSeller.setText(sSeller);
        tvPrice.setText(sPrice);
        tvInventory.setText(R.string.inventory_tv);
        tvInventory.append(itemInventory);
        tvSold.setText(R.string.sold_tv);
        tvSold.append(itemSold);

        Picasso.with(context)
                .load(thumbUri)
                .placeholder(R.drawable.insert_photo_icon)
                .error(R.drawable.insert_photo_icon)
                .into(tvImage);

        //SELL BUTTON
        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (sInventory > 0) {
                    int inventory = sInventory;
                    int sold = sSold;
                    values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, --inventory);
                    values.put(ProductContract.ProductEntry.COLUMN_SOLD, ++sold);
                    contentResolver.update(currentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {
                    Toast.makeText(context, R.string.out_of_stock_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });

        // CHECK FOR EMPTY FEILDS
        int sNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
        String sellerInfo = cursor.getString(sNameColumnIndex);
        if (TextUtils.isEmpty(sellerInfo)) {
            sellerInfo = context.getString(R.string.unknown_info);
        }
        tvSeller.setText(sellerInfo);
    }

}
