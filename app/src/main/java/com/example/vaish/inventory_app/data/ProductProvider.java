package com.example.vaish.inventory_app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.vaish.inventory_app.ProductCursorAdapter;

/**
 * Created by Vaish on 14-10-2016.
 */
public class ProductProvider extends ContentProvider {
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();



    private static final int PRODUCTS = 100;

    private static final int PRODUCT_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:

                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:

                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
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
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertProduct(Uri uri, ContentValues values) {


        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer quan = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
        if (quan == null || quan < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer batch = values.getAsInteger(ProductContract.ProductEntry.COLUMN_BATCH);
        if (batch == null) {
            throw new IllegalArgumentException("Product requires a valid batch no.");
        }

        String shipment = values.getAsString(ProductContract.ProductEntry.COLUMN_SHIPMENT);
        if (shipment.contains("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        String sold = values.getAsString(ProductContract.ProductEntry.COLUMN_SOLD);
        if (sold.contains("[a-zA-Z]+")){
            throw new IllegalArgumentException("Product requires a valid quantity");
        }



        String email = values.getAsString(ProductContract.ProductEntry.COLUMN_EMAIL);

        if (email == null) {
            throw new IllegalArgumentException("Email id required");
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
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
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }


        Integer quan = null;
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_QUANTITY)) {
            quan = values.getAsInteger(ProductContract.ProductEntry.COLUMN_QUANTITY);
            if (quan == null || quan < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }


        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_BATCH)) {
            Integer batch = values.getAsInteger(ProductContract.ProductEntry.COLUMN_BATCH);
            if (batch == null) {
                throw new IllegalArgumentException("Product requires valid batch no.");
            }
        }


        if (values.containsKey(ProductContract.ProductEntry.COLUMN_SHIPMENT)) {
            String shipment = values.getAsString(ProductContract.ProductEntry.COLUMN_SHIPMENT);
            if (shipment.contains("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_SOLD)) {
            String sold = values.getAsString(ProductContract.ProductEntry.COLUMN_SOLD);
            if (sold.contains("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }


        if (values.containsKey(ProductContract.ProductEntry.COLUMN_EMAIL)) {


            String email = values.getAsString(ProductContract.ProductEntry.COLUMN_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("Email id required");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quan - ProductCursorAdapter.count);

        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
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
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
