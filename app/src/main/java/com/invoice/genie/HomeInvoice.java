package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.invoice.genie.adapter.RecyclerCartView;
import com.invoice.genie.databinding.ActivityHomeInvoiceBinding;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.model.cart.Items;

import java.util.ArrayList;
import java.util.List;

public class HomeInvoice extends AppCompatActivity {

    ActivityHomeInvoiceBinding binding;
    Invoice invoice = new Invoice();
    List<Items> individualItemsList = new ArrayList<>();
    String order_id; Context context;
    //Recycler View Variables
    RecyclerView recyclerView;
    RecyclerCartView recyclerCartView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityHomeInvoiceBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        Intent i = getIntent(); context = this;
        this.order_id = i.getStringExtra("order_id");
        this.invoice = (Invoice) i.getSerializableExtra("invoice");
        assignValues();
        this.binding.homeInvoiceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.binding.homeInvoiceShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insertIntent = new Intent(context, AddInvoice.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                insertIntent.putExtra("share_invoice", invoice);
                insertIntent.putExtra("order_id", order_id);
                startActivity(insertIntent);
            }
        });
    }

    private void assignValues(){
        String customerName = "Customer Name : " + this.invoice.getCustomer_name();
        String customerEmail = "Customer Email : " + this.invoice.getCustomer_email();
        String orderInfo = "Ordered On : "+this.invoice.getOrder_date()+" at "+this.invoice.getOrder_time();
        String orderTotal = "Total : CAD "+ String.valueOf(this.invoice.getOrder_total());
        this.individualItemsList = this.invoice.getItems();
        this.binding.homeInvoiceTitle.setText(order_id);
        this.binding.homeInvoiceCustomerName.setText(customerName);
        this.binding.homeInvoiceCustomerEmail.setText(customerEmail);
        this.binding.homeInvoiceOrderTiming.setText(orderInfo);
        this.binding.homeInvoiceOrderAmount.setText(orderTotal);
        initializeRecyclerProductView(this.individualItemsList);
    }

    private void initializeRecyclerProductView(List<Items> items) {
        this.recyclerView = this.binding.individualItemsRecyclerView;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerCartView = new RecyclerCartView(this, items);
        recyclerView.setAdapter(recyclerCartView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}