package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.invoice.genie.adapter.RecyclerProductView;
import com.invoice.genie.databinding.ActivityProductsBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.viewmodels.InventoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class Products extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

    ActivityProductsBinding binding;
    BottomNavigationView bottomNavigationView;

    //Variables
    RecursiveMethods helper; String current_user; Context context;
    private final String TAG = "ProductsActivityClass";

    //Inventory Repository Variables (View Model, Model)
    InventoryViewModel inventoryViewModel;
    List<Inventory> inventories = new ArrayList<>();

    //Recycler View Variables
    RecyclerView recyclerView; RecyclerProductView recyclerProductViewAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_products);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.binding.addProduct.setOnClickListener(this);

        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
        this.inventoryViewModel.inventoryViewModelProductsMutableLiveData.observe(this, new Observer<List<Inventory>>() {
            @Override
            public void onChanged(List<Inventory> inventoryList) {
                if (inventoryList != null) {
                    for (Inventory inventory : inventoryList) {
                        helper.logD(TAG, "List of Inventories can be printed here - " + inventory.toString());
                    }
                }
                binding.productsEmpty.setVisibility(View.GONE);
                initializeRecyclerProductView(inventoryList);
            }
        });
        this.inventories = this.inventoryViewModel.getAllProducts(current_user);

        this.binding.productSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_products);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.binding.addProduct.setOnClickListener(this);

        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
        this.inventoryViewModel.inventoryViewModelProductsMutableLiveData.observe(this, new Observer<List<Inventory>>() {
            @Override
            public void onChanged(List<Inventory> inventoryList) {
                if (inventoryList != null) {
                    for (Inventory inventory : inventoryList) {
                        helper.logD(TAG, "List of Inventories can be printed here - " + inventory.toString());
                    }
                }
                binding.productsEmpty.setVisibility(View.GONE);
                initializeRecyclerProductView(inventoryList);
            }
        });
        this.inventories = this.inventoryViewModel.getAllProducts(current_user);

        this.binding.productSearchText.addTextChangedListener(this);
    }

    public void initializeRecyclerProductView(List<Inventory> inventories) {
        this.recyclerView = this.binding.inventoryRecyclerView;
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerProductViewAdapter = new RecyclerProductView(this, inventories);
        recyclerView.setAdapter(recyclerProductViewAdapter);
    }

    private void filterList(String searchText) {
        List<Inventory> searchList = new ArrayList<>();

        for(Inventory inventory : this.inventories){
            if(inventory.getName().toLowerCase().contains(searchText)){
                searchList.add(inventory);
            }
        }
        if(recyclerProductViewAdapter != null){
            recyclerProductViewAdapter.filteredList(searchList);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.seller_bottom_home) {
            helper.logD(TAG, "Clicked on seller_bottom_home");
            Intent goToSellerHome = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerHome);
            finish();
            return true;
        } else if (itemId == R.id.seller_bottom_products) {
            //do nothing
            helper.logD(TAG, "Clicked on seller_bottom_products");
            return true;
        } else if (itemId == R.id.seller_bottom_more) {
            helper.logD(TAG, "Clicked on seller_bottom_profile");
            Intent goToSellerMore = new Intent(this, SellerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerMore);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getId() == R.id.addProduct) {
                helper.logD(TAG, "Clicked on addProduct");
                Intent AddProductIntent = new Intent(this, AddProduct.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(AddProductIntent);
                finish();
            }
        }
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