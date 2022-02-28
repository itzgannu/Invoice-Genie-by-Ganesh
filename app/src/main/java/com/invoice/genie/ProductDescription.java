package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.invoice.genie.databinding.ActivityProductDescriptionBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.aisle.InventoryWithoutURL;

import com.invoice.genie.viewmodels.InventoryViewModel;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ProductDescription extends AppCompatActivity implements View.OnClickListener {

    ActivityProductDescriptionBinding binding;
    InventoryViewModel inventoryViewModel;

    RecursiveMethods helper;
    String email;
    boolean update = true;
    int RESULT_LOAD_IMAGE = 200;
    boolean imageSet = false;
    Uri url;
    byte[] uploadByte;

    Inventory inventoryClicked;

    Context context; Activity activity;

    ImageView productImage;
    TextInputLayout price, description, quantity, unit_type;
    TextInputEditText product_price, product_description, product_quantity, product_unit_type;
    Editable prod_price, prod_description, prod_quantity, prod_unit_type;
    TextView product_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityProductDescriptionBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        context = this; activity = this;
        email = helper.getCurrentUser(this);

        inventoryClicked = (Inventory) getIntent().getSerializableExtra("inventory");

        productImage = this.binding.descImage;

        product_name = this.binding.descTitle;
        description = this.binding.descDescLayout;
        price = this.binding.descPriceLayout;
        quantity = this.binding.descQuantityLayout;
        unit_type = this.binding.descUnitTypeLayout;

        product_description = this.binding.descDesc;
        product_price = this.binding.descPrice;
        product_quantity = this.binding.descQuantity;
        product_unit_type = this.binding.descUnitType;

        prod_description = Objects.requireNonNull(product_description.getText());
        prod_price = Objects.requireNonNull(product_price.getText());
        prod_quantity = Objects.requireNonNull(product_quantity.getText());
        prod_unit_type = Objects.requireNonNull(product_unit_type.getText());

        displayDataInFields();

        this.binding.descUpdate.setOnClickListener(this);
        this.binding.descBack.setOnClickListener(this);
        this.binding.descDelete.setOnClickListener(this);
        this.binding.descImagePicker.setOnClickListener(this);
        this.inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());
    }

    private void displayDataInFields() {
        binding.descTitle.setText(inventoryClicked.getName());
        product_price.setText(String.valueOf(inventoryClicked.getPrice()));
        product_description.setText(inventoryClicked.getDescription());
        product_quantity.setText(String.valueOf(inventoryClicked.getQuantity()));
        product_unit_type.setText(inventoryClicked.getPack());
        Glide.with(this)
                .load(inventoryClicked.getUrl())
                .into(productImage);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.desc_update) {
            String saveText = getString(R.string.save);
            String updateText = getString(R.string.update);
            if (update) {
                update = false;
                enableFields();
                this.binding.descUpdate.setText(saveText);
            } else {
                if(updateProduct()){
                    update = true;
                    disableFields();
                    this.binding.descUpdate.setText(updateText);
                }
            }
        } else if (id == R.id.desc_delete) {
            deleteProduct();
        } else if (id == R.id.desc_back) {
            Intent backIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(backIntent);
            finish();
        } else if (id == R.id.desc_imagePicker) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
        }
    }

    /**
     * onActivityResult
     * get Uri from selected Image & set the same Image to Imageview
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                Uri selectedImageUri = data.getData();
                url = selectedImageUri;
                if (null != selectedImageUri) {
                    productImage.setImageURI(selectedImageUri);
                    imageSet = true;
                }
            }
        }
    }

    private void getByteArrayData(){
        productImage.setDrawingCacheEnabled(true);
        productImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        uploadByte = byteArrayOutputStream.toByteArray();
    }

    private void enableFields() {
        this.binding.descPriceLayout.setEnabled(true);
        this.binding.descQuantityLayout.setEnabled(true);
        this.binding.descDescLayout.setEnabled(true);
        this.binding.descUnitTypeLayout.setEnabled(true);
        this.binding.descImagePicker.setEnabled(true);
    }

    private void disableFields(){
        this.binding.descPriceLayout.setEnabled(false);
        this.binding.descQuantityLayout.setEnabled(false);
        this.binding.descDescLayout.setEnabled(false);
        this.binding.descUnitTypeLayout.setEnabled(false);
        this.binding.descImagePicker.setEnabled(false);
    }
    private boolean fieldValidations() {
        String empty = getString(R.string.empty_field);
        if (Objects.requireNonNull(this.binding.descPrice.getText()).toString().isEmpty()) {
            price.setError(empty);
            return false;
        } else{
            price.setError(null);
        }
        if (Objects.requireNonNull(this.binding.descQuantity.getText()).toString().isEmpty()) {
            quantity.setError(empty);
            return false;
        } else{
            quantity.setError(null);
        }
        if (Objects.requireNonNull(this.binding.descDesc.getText()).toString().isEmpty()) {
            description.setError(empty);
            return false;
        }else{
            description.setError(null);
        }
        if(this.binding.descDesc.getText().toString().length()>100){
            description.setError("Description should be maximum of 100 characters");
            return false;
        } else{
            description.setError(null);
        }

        if (Objects.requireNonNull(this.binding.descUnitType.getText()).toString().isEmpty()) {
            unit_type.setError(empty);
            return false;
        } else{
            unit_type.setError(null);
        }
        if(this.binding.descUnitType.getText().toString().length()>15){
            unit_type.setError(getString(R.string.unit_type_max_15));
            return false;
        }else{
            unit_type.setError(null);
        }
        return true;
    }

    private boolean updateProduct(){
        if(fieldValidations()){
            getByteArrayData();

            String name = this.binding.descTitle.getText().toString();
            String price = Objects.requireNonNull(this.binding.descPrice.getText()).toString();
            String quantity = Objects.requireNonNull(this.binding.descQuantity.getText()).toString();
            String description = Objects.requireNonNull(this.binding.descDesc.getText()).toString();
            String unitType = Objects.requireNonNull(this.binding.descUnitType.getText()).toString();

            InventoryWithoutURL product = new InventoryWithoutURL();
            product.setName(name);
            product.setDescription(description);

            if(price.length()>0){
                product.setPrice(Double.parseDouble(price));
            }
            if(quantity.length()>0){
                product.setQuantity(Integer.parseInt(quantity));
            }

            product.setPack(unitType);
            this.inventoryViewModel.updateProduct(product,email, uploadByte);
            userConfirmation();
            return true;
        }else{
            return false;
        }
    }

    private void userConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Product Details Updated")
                .setTitle("Success");
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void deleteProduct(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to DELETE?")
                .setTitle("Confirmation");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = product_name.getText().toString();
                inventoryViewModel.deleteProduct(name, email);
                Intent goBack = new Intent(context, Home.class);
                startActivity(goBack);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }
}