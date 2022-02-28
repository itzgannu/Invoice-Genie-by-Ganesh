package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.invoice.genie.adapter.RecyclerCartView;
import com.invoice.genie.databinding.ActivityCartProductsBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Items;
import com.invoice.genie.viewmodels.CartViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart_Products extends AppCompatActivity implements View.OnClickListener {

    ActivityCartProductsBinding binding;

    RecursiveMethods helper;
    String current_user;
    Context context;
    Activity activity;
    private final String TAG = "Cart_Products Class";

    CartViewModel cartViewModel;
    List<Items> itemsList = new ArrayList<>();
    double total = 0;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 500;

    //Recycler View Variables
    RecyclerView recyclerView;
    RecyclerCartView recyclerCartView;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartProductsBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        context = this;
        activity = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.cartViewModel = CartViewModel.getInstance(getApplication());

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                cartViewModel.getItems(current_user);
                executeInLoop();
            }
        }, delay);

        this.binding.cartProductsGoToCheckout.setOnClickListener(this);
        this.binding.cartProductsBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityCartProductsBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        context = this;
        activity = this;
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.binding.cartProductsGoToCheckout.setOnClickListener(this);

        this.cartViewModel = CartViewModel.getInstance(getApplication());

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                cartViewModel.getItems(current_user);
                executeInLoop();
            }
        }, delay);

        this.binding.cartProductsGoToCheckout.setOnClickListener(this);
        this.binding.cartProductsBack.setOnClickListener(this);
    }

    private void executeInLoop() {
        this.cartViewModel.itemsLiveData.observe(this, new Observer<List<Items>>() {
            @Override
            public void onChanged(List<Items> items) {
                if (items != null) {
                    total = 0;
                    for (int i = 0; i < items.size(); i++) {
                        total = total + items.get(i).getProduct_total_price();
                    }
                    binding.cartProductsLoader.setVisibility(View.GONE);
                    String text;
                    total = Math.round(total*100.0)/100.0;
                    if (total == 0) {
                        text = "No items are added in cart";
                        binding.cartProductsGoToCheckout.setEnabled(false);
                        binding.cartProductsTotalPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    } else {
                        text = "CAD " + total;
                        binding.cartProductsGoToCheckout.setEnabled(true);
                        binding.cartProductsTotalPrice.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    }
                    binding.cartProductsTotalPrice.setText(text);
                    itemsList = items;
                }
                initializeRecyclerProductView(items);
            }
        });
        this.cartViewModel.getItems(current_user);
        handler.removeCallbacks(runnable);//stop loop
    }

    public void initializeRecyclerProductView(List<Items> items) {
        this.itemsList = items;
        this.recyclerView = this.binding.cartProductsRecyclerView;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerCartView = new RecyclerCartView(this, items);
        recyclerView.setAdapter(recyclerCartView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            itemsList.remove(position);
            recyclerCartView.notifyItemRemoved(position);
            changeText(itemsList);
        }
    };

    private void changeText(List<Items> newList) {
        total = 0;
        for (int i = 0; i < newList.size(); i++) {
            total = total + newList.get(i).getProduct_total_price();
        }
        String text;
        total = Math.round(total*100.0)/100.0;
        if (total == 0) {
            text = "No items are added in cart";
            binding.cartProductsGoToCheckout.setEnabled(false);
            binding.cartProductsTotalPrice.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            text = "CAD " + total;
            binding.cartProductsGoToCheckout.setEnabled(true);
            binding.cartProductsTotalPrice.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
        binding.cartProductsTotalPrice.setText(text);
    }


    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.cart_products_go_to_checkout) {
                helper.logD(TAG, "Go To Checkout button is clicked");
                Intent homeIntent = new Intent(this, Cart_Customer.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                homeIntent.putExtra("items_list", (Serializable) this.itemsList);
                homeIntent.putExtra("order_total", total);
                startActivity(homeIntent);
                finish();
            } else if (id == R.id.cart_products_back) {
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
}