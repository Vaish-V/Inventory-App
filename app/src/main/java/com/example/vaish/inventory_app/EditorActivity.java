package com.example.vaish.inventory_app;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vaish.inventory_app.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    int qty,pr,b,srvd,isd,i=0;
    int balance=0;

    private String bal,name,emailId;

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentUri;

    private EditText mNameEditText;

    private EditText mQuantityEditText;

    private EditText mPriceEditText;

    private EditText mBatchEditText;

    private EditText mShipment;

    private EditText mEmailIdEditText;

    private EditText mSold;

    private Button updateQ;

    private Button orderMore;

    private ImageView addImage;

    private Bitmap bitmap = null;

    private String[] i1;

    private byte[] img;

    final ContentValues values = new ContentValues();



    private boolean mProductHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        Intent intent = getIntent();
        mCurrentUri = intent.getData();


        if (mCurrentUri == null) {
            setTitle("Add a Product");


            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.nameEnter);
        mQuantityEditText = (EditText) findViewById(R.id.qtyEnter);
        mPriceEditText = (EditText) findViewById(R.id.priceEnter);
        mBatchEditText = (EditText) findViewById(R.id.batchEnter);
        mShipment = (EditText) findViewById(R.id.shipmentEnter);
        mSold = (EditText) findViewById(R.id.soldEnter);
        mEmailIdEditText = (EditText) findViewById(R.id.emailEnter);

        updateQ = (Button)findViewById(R.id.updateButton);
        orderMore = (Button)findViewById(R.id.orderMore);

        addImage = (ImageView)findViewById(R.id.addImage);

        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mBatchEditText.setOnTouchListener(mTouchListener);

        mEmailIdEditText.setOnTouchListener(mTouchListener);



        updateQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(balance<0)
                {
                    Toast.makeText(getBaseContext(), "Quantiy cannot be less than 0",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                mQuantityEditText.setText(String.valueOf(balance));
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, i);
                i = i + 1;
            }
        });







    }

protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);


    if (requestCode == i && resultCode == RESULT_OK && data != null) {
        Uri pickedImage = data.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        img = stream.toByteArray();

        values.put(ProductContract.ProductEntry.COLUMN_PICTURE,img);




        cursor.close();
    }
}


    private void saveProduct() {



        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String batchString = mBatchEditText.getText().toString().trim();
        String shipmentString = mShipment.getText().toString().trim();
        String soldString = mSold.getText().toString().trim();
        String emailString = mEmailIdEditText.getText().toString().trim();



        if (mCurrentUri == null && (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(emailString))) {

            return;
        }



        values.put(ProductContract.ProductEntry.COLUMN_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_EMAIL, emailString);


        int quan = 0;
        if(!TextUtils.isEmpty(quantityString)){
            quan = Integer.parseInt(quantityString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_QUANTITY, quan);

        int price = 0;
        if(!TextUtils.isEmpty(priceString)){
            price = Integer.parseInt(priceString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_PRICE, price);

        int batch = 0;
        if(!TextUtils.isEmpty(batchString)){
            batch = Integer.parseInt(batchString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_BATCH, batch);

        int sr = 0;
        if(!TextUtils.isEmpty(shipmentString)){
            sr = Integer.parseInt(shipmentString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_SHIPMENT, sr);

        int sold = 0;
        if(!TextUtils.isEmpty(soldString)){
            sold = Integer.parseInt(soldString);
        }
        values.put(ProductContract.ProductEntry.COLUMN_SOLD, sold);

        updateQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mQuantityEditText.setText(String.valueOf(balance));

            }
        });






        if (mCurrentUri == null) {

            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Insert failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Insert successful",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Update failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update Successful",
                        Toast.LENGTH_SHORT).show();
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
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                saveProduct();



                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }


        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_BATCH,
                ProductContract.ProductEntry.COLUMN_SHIPMENT,
                ProductContract.ProductEntry.COLUMN_SOLD,
                ProductContract.ProductEntry.COLUMN_EMAIL
                };

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }




        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
            int qtyColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
            int batchColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_BATCH);
            int shipColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SHIPMENT);
            int soldColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SOLD);
            int emailColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_EMAIL);

            name = cursor.getString(nameColumnIndex);
            qty = cursor.getInt(qtyColumnIndex);
            qty=ProductCursorAdapter.q;
            pr = cursor.getInt(priceColumnIndex);
            b = cursor.getInt(batchColumnIndex);
            srvd = cursor.getInt(shipColumnIndex);
            isd = cursor.getInt(soldColumnIndex);
            emailId = cursor.getString(emailColumnIndex);

            Log.v("qty,srvd,isd"," :" +qty);
            Log.v("qty,srvd,isd"," :" +srvd);
            Log.v("qty,srvd,isd"," :" +isd);
            int x = qty+srvd-isd;
            Log.v("qty,srvd,isd"," :" +x);
            balance = qty+srvd-isd;
            Log.v("qty,srvd,isd"," :" +balance);






            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(qty));
            mPriceEditText.setText(Integer.toString(pr));
            mBatchEditText.setText(Integer.toString(b));
            mShipment.setText(Integer.toString(srvd));
            mSold.setText(Integer.toString(isd));
            mEmailIdEditText.setText(emailId);





            orderMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Order for product "+name);
                    intent.putExtra(Intent.EXTRA_TEXT, "Requesting a new order for the product.");
                    intent.setData(Uri.parse("mailto:"+emailId));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });


        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mBatchEditText.setText("");
        mShipment.setText("");
        mSold.setText("");
        mEmailIdEditText.setText("");
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to discard changes or continue editing ?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Delete failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete successful",
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }


}
