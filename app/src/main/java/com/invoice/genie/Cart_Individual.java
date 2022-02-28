package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.invoice.genie.databinding.ActivityCartIndividualBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.cart.Items;
import com.invoice.genie.viewmodels.CartViewModel;

import java.util.List;

public class Cart_Individual extends AppCompatActivity implements View.OnClickListener {

    ActivityCartIndividualBinding binding;

    RecursiveMethods helper; private final String TAG = "Cart_Individual class";
    String email; Context context; Activity activity;

    Inventory inventoryClicked; Items itemToCart;
    CartViewModel cartViewModel;

    ImageView productImage; ImageButton productAdd, productDelete;
    TextView productName, productPrice, productDescription, productPackage, productCount;

    int count; double total_price; boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartIndividualBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); email = helper.getCurrentUser(this);
        context = this; activity = this;

        count = 1; total_price = 0; firstTime = true;

        inventoryClicked = (Inventory) getIntent().getSerializableExtra("inventory");
        itemToCart = new Items();

        this.cartViewModel = CartViewModel.getInstance(getApplication());
        this.cartViewModel.itemsLiveData.observe(this, new Observer<List<Items>>() {
            @Override
            public void onChanged(List<Items> items) {
                for(Items item: items){
                    if(firstTime){
                        if(item.getProduct_name().equalsIgnoreCase(inventoryClicked.getName())){
                            count = item.getProduct_quantity();
                            if(count>=1){
                                binding.individualProductAddToCartButton.setEnabled(false);
                                binding.individualProductStepperLinear.setVisibility(View.VISIBLE);
                                binding.individualProductCartCountText.setText(String.valueOf(count));
                                firstTime = false;
                            }
                        }
                    }
                }
            }
        });
        this.cartViewModel.getItems(email);

        initialiseValues(); displayValueInTextView();

        this.binding.individualProductAddToCartButton.setOnClickListener(this);
        this.binding.individualProductAddButton.setOnClickListener(this);
        this.binding.individualProductDeleteButton.setOnClickListener(this);
        this.binding.individualBack.setOnClickListener(this);
    }

    private void initialiseValues(){
        count = 1;
        productImage = this.binding.individualProductImage;
        productName = this.binding.individualProductName;
        productPrice = this.binding.individualProductPrice;
        productDescription = this.binding.individualProductDescription;
        productPackage = this.binding.individualProductPackage;
        productAdd = this.binding.individualProductAddButton;
        productDelete = this.binding.individualProductDeleteButton;
        productCount = this.binding.individualProductCartCountText;
    }

    private void displayValueInTextView() {
        Glide.with(this)
                .load(inventoryClicked.getUrl())
                .into(productImage);
        productName.setText(inventoryClicked.getName());
        productPrice.setText(String.valueOf(inventoryClicked.getPrice()));
        productDescription.setText(inventoryClicked.getDescription());
        productPackage.setText(inventoryClicked.getPack());
        productCount.setText("1");
    }

    private void createItem(int quantity){
        //set total price
        double price = inventoryClicked.getPrice();
        total_price = quantity * price;
        //rounding double value
        total_price = Math.round(total_price*100.0)/100.0;
        //create item
        itemToCart.setProduct_name(inventoryClicked.getName());
        itemToCart.setProduct_price(inventoryClicked.getPrice());
        itemToCart.setUrl(inventoryClicked.getUrl());
        itemToCart.setUnit_type(inventoryClicked.getPack());
        itemToCart.setProduct_quantity(count);
        itemToCart.setProduct_total_price(total_price);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            if (id == R.id.individual_product_add_to_cart_button) {
                binding.individualProductAddToCartButton.setEnabled(false);
                binding.individualProductStepperLinear.setVisibility(View.VISIBLE);
                count = 1;
                binding.individualProductCartCountText.setText(String.valueOf(count));
                createItem(count);
                this.cartViewModel.addItem(itemToCart, email);
            } else if (id == R.id.individual_product_delete_button) {
                count = Integer.parseInt(binding.individualProductCartCountText.getText().toString());
                if (count==1) {
                    createItem(1);
                    this.cartViewModel.deleteItem(itemToCart,email);
                    binding.individualProductStepperLinear.setVisibility(View.INVISIBLE);
                    binding.individualProductAddToCartButton.setEnabled(true);
                } else {
                    count = Integer.parseInt(binding.individualProductCartCountText.getText().toString());
                    count = count - 1;
                    binding.individualProductCartCountText.setText(String.valueOf(count));
                    createItem(count);
                    this.cartViewModel.updateItem(itemToCart,email);
                }
            } else if (id == R.id.individual_product_add_button) {
                count = Integer.parseInt(binding.individualProductCartCountText.getText().toString());
                if (count == 10 || count > 10) {
                    count = 10;
                    binding.individualProductCartCountText.setText(String.valueOf(count));
                } else {
                    helper.logD(TAG, "Count is - "+count);
                    count = count + 1;
                    binding.individualProductCartCountText.setText(String.valueOf(count));
                    helper.logD(TAG, "Count after - "+count);
                }
                createItem(count);
                this.cartViewModel.updateItem(itemToCart,email);
            } else if (id == R.id.individual_back) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}