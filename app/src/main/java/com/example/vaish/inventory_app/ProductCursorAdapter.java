package com.example.vaish.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaish.inventory_app.data.ProductContract;


public class ProductCursorAdapter extends CursorAdapter {

    public static int count = 0;
    public static int q;

    int w=0;

    private TextView button;
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        button = (TextView) view.findViewById(R.id.sell_one);

        TextView nameTextView = (TextView) view.findViewById(R.id.listName);
        final TextView qtyTextView = (TextView) view.findViewById(R.id.listQuantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.listPrice);

        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);

        String pName = cursor.getString(nameColumnIndex);
        String pQuantity = cursor.getString(quantityColumnIndex);
        String pPrice = cursor.getString(priceColumnIndex);

        Log.v("",""+pQuantity);




        nameTextView.setText(pName);
        priceTextView.setText(pPrice);
        qtyTextView.setText(pQuantity);

        String quantity = qtyTextView.getText().toString().trim();
        final int[] quan = {Integer.parseInt(quantity)};
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quan[0]--;
                if(quan[0] < 0){
                    Toast.makeText(context, "Quantity cannot be less than 0",
                            Toast.LENGTH_SHORT).show();
                    quan[0]++;
                    return;
                }
                else {
                    count++;
                    qtyTextView.setText(String.valueOf(quan[0]));
                }
                q=quan[0];
            }
        });




    }

}
