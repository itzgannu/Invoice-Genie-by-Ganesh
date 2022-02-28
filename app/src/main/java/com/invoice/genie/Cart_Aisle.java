package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.invoice.genie.adapter.RecyclerAisleView;
import com.invoice.genie.databinding.ActivityCartAisleBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.cart.Items;
import com.invoice.genie.viewmodels.CartViewModel;
import com.invoice.genie.viewmodels.InventoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class Cart_Aisle extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    ActivityCartAisleBinding binding;

    //Variables
    RecursiveMethods helper;
    String current_user;
    Context context;
    private final String TAG = "CartAisleActivityClass";

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 300;

    //Inventory Repository Variables (View Model, Model)
    InventoryViewModel inventoryViewModel;
    List<Inventory> inventories = new ArrayList<>();

    //Recycler View Variables
    RecyclerView recyclerView;
    RecyclerAisleView recyclerAisleViewAdapter;
    GridLayoutManager gridLayoutManager;

    CartViewModel cartViewModel; int count = 0; boolean restart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartAisleBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        context = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
        this.cartViewModel = CartViewModel.getInstance(this.getApplication());
        this.cartViewModel.getItems(current_user);
        restart = false;

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                inventoryViewModel.getAllProducts(current_user);
                cartViewModel.getItems(current_user);
                executeInLoop();
            }
        }, delay);

        this.binding.aisleCart.setOnClickListener(this);
        this.binding.aisleClose.setOnClickListener(this);

        this.binding.aisleSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityCartAisleBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        context = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
        this.cartViewModel = CartViewModel.getInstance(this.getApplication());
        this.cartViewModel.getItems(current_user);
        restart = false;

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                inventoryViewModel.getAllProducts(current_user);
                cartViewModel.getItems(current_user);
                executeInLoop();
            }
        }, delay);

        this.binding.aisleCart.setOnClickListener(this);
        this.binding.aisleClose.setOnClickListener(this);

        this.binding.aisleSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.binding = ActivityCartAisleBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        helper = new RecursiveMethods();
        context = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
        this.cartViewModel = CartViewModel.getInstance(this.getApplication());
        restart = true;

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                inventoryViewModel.getAllProducts(current_user);
                executeInLoop();
            }
        }, delay);

        this.binding.aisleCart.setOnClickListener(this);
        this.binding.aisleClose.setOnClickListener(this);

        this.binding.aisleSearchText.addTextChangedListener(this);
    }

    private void executeInLoop(){
        this.inventoryViewModel.inventoryViewModelProductsMutableLiveData.observe(this, new Observer<List<Inventory>>() {
            @Override
            public void onChanged(List<Inventory> inventoryList) {
                binding.aisleLoader.setVisibility(View.GONE);
                if (inventoryList != null) {
                    for (Inventory inventory : inventoryList) {
                        helper.logD(TAG, "List of Inventories can be printed here - " + inventory.toString());
                    }
                }
                initializeRecyclerProductView(inventoryList);
            }
        });
        this.inventories = this.inventoryViewModel.getAllProducts(current_user);
        if(restart){
            //don't get count from db
        } else{
            List<Items> items = this.cartViewModel.getItems(current_user);
            count = this.cartViewModel.itemsList.size();
        }

        handler.removeCallbacks(runnable);//stop loop
    }

    public void initializeRecyclerProductView(List<Inventory> inventories) {
        this.binding.aisleCount.setText(String.valueOf(count));
        this.inventories = inventories;
        this.recyclerView = this.binding.aisleRecyclerView;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerAisleViewAdapter = new RecyclerAisleView(this, this.inventories);
        recyclerView.setAdapter(recyclerAisleViewAdapter);
    }

    private void filterList(String searchText) {
        List<Inventory> searchList = new ArrayList<>();

        for(Inventory inventory : this.inventories){
            if(inventory.getName().toLowerCase().contains(searchText)){
                searchList.add(inventory);
            }
        }
        if(recyclerAisleViewAdapter != null){
            recyclerAisleViewAdapter.filteredList(searchList);
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.aisle_cart) {
                helper.logD(TAG, "Clicked on Cart Button");
                Intent goToCart = new Intent(this, Cart_Products.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToCart);
            } else if (id == R.id.aisle_close) {
                helper.logD(TAG, "Clicked on Close Button");
                Intent homeIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(homeIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s!=null){
            filterList(s.toString());
        }
    }
}