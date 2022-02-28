package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.invoice.genie.databinding.ActivityAddProductBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.InventoryWithoutURL;
import com.invoice.genie.viewmodels.InventoryViewModel;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AddProduct extends AppCompatActivity implements View.OnClickListener {

    ActivityAddProductBinding binding;
    InventoryViewModel inventoryViewModel;

    RecursiveMethods helper; private String email;
    private final String TAG = "AddProduct";

    final int RESULT_LOAD_IMAGE = 200; boolean imageSet = false; Uri url; byte[] uploadByte;

    ImageView productImage;
    TextInputLayout name, price, description, quantity, unit_type;
    TextInputEditText prod_name, prod_price, prod_description, prod_quantity, prod_unit_type;
    Editable product_name, product_price, product_description, product_quantity, product_unit_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods();
        inventoryViewModel = InventoryViewModel.getInstance(this.getApplication());

        email = helper.getCurrentUser(this);

        productImage = this.binding.addProductImagePicker;

        name = this.binding.addProductNameLayout;
        description = this.binding.addProductDescriptionLayout;
        price = this.binding.addProductPriceLayout;
        quantity = this.binding.addProductQuantityLayout;
        unit_type = this.binding.addProductUnitTypeLayout;

        prod_name = this.binding.addProductName;
        prod_description = this.binding.addProductDescription;
        prod_price = this.binding.addProductPrice;
        prod_quantity = this.binding.addProductQuantity;
        prod_unit_type = this.binding.addProductUnitType;

        product_name = Objects.requireNonNull(prod_name.getText());
        product_description = Objects.requireNonNull(prod_description.getText());
        product_price = Objects.requireNonNull(prod_price.getText());
        product_quantity = Objects.requireNonNull(prod_quantity.getText());
        product_unit_type = Objects.requireNonNull(prod_unit_type.getText());

        this.binding.addProductSave.setOnClickListener(this);
        this.binding.addProductBack.setOnClickListener(this);
        this.binding.addProductImagePickerButton.setOnClickListener(this);
    }

    private void clearTextFields() {
        helper.logD(TAG, "Clearing Text Fields");
        product_name.clear();
        product_description.clear();
        product_price.clear();
        product_quantity.clear();
        product_unit_type.clear();
        binding.addProductImagePicker.setImageResource(R.drawable.grocery);
        imageSet = false;
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
                    helper.logD(TAG, "Picked image from Gallery & assigning to Imageview");
                    this.binding.addProductImagePicker.setImageURI(selectedImageUri);
                    imageSet = true;
                }
            }
        }
    }

    private void getByteArrayData() {
        helper.logD(TAG, "Getting Image as Byte Array Data");
        productImage.setDrawingCacheEnabled(true);
        productImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        uploadByte = byteArrayOutputStream.toByteArray();
    }

    /**
     * userConfirmation
     * Dialog box to inform the user that he didn't the image
     */
    private void userConfirmation() {
        helper.logD(TAG, "User Confirmation as no Image is selected");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You didn't pick an image from gallery.\nDefault image will be set, it is fine?")
                .setTitle("Confirmation");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                url = Uri.parse("android.resource://com.invoice.genie/drawable/grocery");
                binding.addProductImagePicker.setImageURI(url);
                imageSet = true;
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dismiss
            }
        });
        builder.create();
        builder.show();
    }

    /**
     * fieldValidations
     */
    private boolean fieldValidations() {
        helper.logD(TAG, "Field Validations");
        product_name = Objects.requireNonNull(prod_name.getText());
        product_description = Objects.requireNonNull(prod_description.getText());
        product_price = Objects.requireNonNull(prod_price.getText());
        product_quantity = Objects.requireNonNull(prod_quantity.getText());
        product_unit_type = Objects.requireNonNull(prod_unit_type.getText());
        String empty = getString(R.string.empty_field);
        if (product_name.toString().isEmpty()) {
            name.setError(empty);
            return false;
        }else{
            name.setError(null);
        }
        if(product_name.toString().length()>15){
            name.setError("Product name should be maximum of 15 characters");
            return false;
        }else{
            name.setError(null);
        }
        if (product_description.toString().isEmpty()) {
            description.setError(empty);
            return false;
        } else {
            description.setError(null);
        }
        if(product_description.toString().length()>100){
            description.setError("Product description should be maximum of 100 characters");
            return false;
        }else{
            description.setError(null);
        }
        if (product_price.toString().isEmpty()) {
            price.setError(empty);
            return false;
        } else{
            price.setError(null);
        }
        if (product_quantity.toString().isEmpty()) {
            quantity.setError(empty);
            return false;
        } else{
            quantity.setError(null);
        }
        if (product_unit_type.toString().isEmpty()) {
            unit_type.setError(empty);
            return false;
        } else{
            unit_type.setError(null);
        }if(product_unit_type.toString().length()>15){
            unit_type.setError("Product unit type should be maximum of 15 characters");
            return false;
        }else{
            unit_type.setError(null);
        }if (!imageSet) {
            userConfirmation();
            return false;
        } else {
            return true;
        }
    }

    private void userConfirmation(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_product_back) {
            helper.logD(TAG, "Clicked on Back Icon button in Add InventoryWithoutURL Screen");
            Intent backIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(backIntent);
            finish();
        } else if (id == R.id.add_product_image_picker_button) {
            helper.logD(TAG, "Clicked on PICK button in Add InventoryWithoutURL Screen");
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), RESULT_LOAD_IMAGE);
        } else if (id == R.id.add_product_save) {
            helper.logD(TAG, "Clicked on SAVE Icon Button in Add InventoryWithoutURL Screen");
            if (fieldValidations()) {
                getByteArrayData();
                String desc = product_description.toString();
                String name = product_name.toString();
                double price = Double.parseDouble(product_price.toString());
                int quantity = Integer.parseInt(product_quantity.toString());
                String type = product_unit_type.toString();
                InventoryWithoutURL product = new InventoryWithoutURL(desc, name, price, quantity, type);
                this.inventoryViewModel.addNewProduct(product, email, uploadByte);
                clearTextFields();
                userConfirmation("Product Added", "Success");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(backIntent);
        finish();
    }
}