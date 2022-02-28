package com.invoice.genie;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.WriterException;
import com.invoice.genie.databinding.ActivityCartOrderSuccessBinding;
import com.invoice.genie.helpers.JavaMailAPI;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.model.cart.Items;

import java.util.List;

public class Cart_OrderSuccess extends AppCompatActivity implements View.OnClickListener {

    ActivityCartOrderSuccessBinding binding;

    RecursiveMethods helper; private final String TAG = "Cart_OrderSuccess Class";

    String order_id; Invoice placeHolderInvoice;
    String userEmail, subject, bodyMessage;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); Intent i = getIntent();

        order_id = i.getStringExtra("order_id");
        placeHolderInvoice = (Invoice) i.getSerializableExtra("complete_order");

        this.binding.successOrderId.setText(order_id);

        generateBarCode();
        sendEmail();

        this.binding.successClose.setOnClickListener(this);
        this.binding.successOrderShare.setOnClickListener(this);
    }

    private void generateBarCode(){
        helper.logD(TAG, "generating bar code for the order id text");
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;
        qrgEncoder = new QRGEncoder(order_id, null, QRGContents.Type.TEXT, dimen);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            this.binding.successOrderIdBarCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            helper.logE(TAG, e.toString());
        }
    }

    private void sendEmail(){
        userEmail = placeHolderInvoice.getCustomer_email();
        List<Items> itemsList = placeHolderInvoice.getItems();
        StringBuilder itemText = new StringBuilder();
        for(int j=0; j<itemsList.size(); j++){
            itemText.append("\n")
                    .append("Product Name - ").append(itemsList.get(j).getProduct_name()).append("\n")
                    .append("Product Quantity - ").append(itemsList.get(j).getProduct_quantity()).append("\n")
                    .append("Product Price - ").append(itemsList.get(j).getProduct_price()).append(" - ").append(itemsList.get(j).getUnit_type()).append("\n\n");
        }
        subject = "Your Order is placed Successfully & Order ID is "+order_id;
        bodyMessage = "Customer Name - "+placeHolderInvoice.getCustomer_name()+"\n"+
                "Order Date - "+placeHolderInvoice.getOrder_date()+"\n"+
                "Order Time - "+placeHolderInvoice.getOrder_time()+"\n"+
                "Order Amount - CAD "+placeHolderInvoice.getOrder_total()+"\n"+"\n"+
                "Items - "+itemText;

        JavaMailAPI javaMailAPI = new JavaMailAPI(placeHolderInvoice, order_id, false, "");
        javaMailAPI.execute() ;
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();
            if(id==R.id.success_close){
                Intent insertIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(insertIntent);
                finish();
            }
            else if(id==R.id.success_order_share){
                Intent insertIntent = new Intent(this, AddInvoice.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                insertIntent.putExtra("share_invoice", placeHolderInvoice);
                insertIntent.putExtra("order_id", order_id);
                startActivity(insertIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent insertIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}