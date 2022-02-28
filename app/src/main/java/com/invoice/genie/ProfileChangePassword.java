package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.invoice.genie.databinding.ActivityProfileChangePasswordBinding;
import com.invoice.genie.helpers.RecursiveMethods;

import java.util.Objects;

public class ProfileChangePassword extends AppCompatActivity implements View.OnClickListener {

    ActivityProfileChangePasswordBinding binding;

    RecursiveMethods helper; String email, userType; Context context; Activity activity;

    TextInputLayout new_password_layout, confirm_password_layout;

    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityProfileChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this; activity = this;
        email = helper.getCurrentUser(this);
        userType = helper.getCurrentUserType(this);
        helper.isCacheExists(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        new_password_layout = this.binding.changePasswordNewPasswordLayout;
        confirm_password_layout = this.binding.changePasswordConfirmNewPasswordLayout;

        this.binding.changePasswordButton.setOnClickListener(this);
        this.binding.changePasswordBack.setOnClickListener(this);
    }

    private void checkIfBothPasswordFieldsAreSame() {
        new_password_layout.setError(null);
        confirm_password_layout.setError(null);

        showProgressBar();

        String new_pass = Objects.requireNonNull(new_password_layout.getEditText()).getText().toString();
        String confirm_pass = Objects.requireNonNull(confirm_password_layout.getEditText()).getText().toString();

        if(new_pass.contentEquals(confirm_pass)) {
            firebaseUser.updatePassword(new_pass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if(progressDialog!=null){
                                    progressDialog.dismiss();
                                }
                                userConfirmation("Password Reset", "Success");
                                Objects.requireNonNull(new_password_layout.getEditText()).getText().clear();
                                Objects.requireNonNull(confirm_password_layout.getEditText()).getText().clear();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(progressDialog!=null){
                                progressDialog.dismiss();
                            }
                            if(e.getLocalizedMessage().contains("Log in again before retrying this request")){
                                FirebaseAuth.getInstance().signOut();
                                helper.signOut(context, activity);
                            }else{
                                userConfirmation("Not able to reset Password", "Un-Success");
                                Objects.requireNonNull(new_password_layout.getEditText()).getText().clear();
                                Objects.requireNonNull(confirm_password_layout.getEditText()).getText().clear();
                            }
                        }
                    });
        }else{
            confirm_password_layout.setError("Enter Same Password in both fields");
        }
    }

    private void showProgressBar(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    private void userConfirmation(String message, String title) {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
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
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();

            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            if (id == R.id.change_password_button) {
                if(Objects.requireNonNull(new_password_layout.getEditText()).getText().toString().isEmpty()){
                    this.binding.changePasswordNewPasswordLayout.setError("Password field cannot be empty");
                    return;
                }else{
                    this.binding.changePasswordNewPasswordLayout.setError(null);
                }
                if(Objects.requireNonNull(new_password_layout.getEditText()).getText().toString().length()<8){
                    this.binding.changePasswordNewPasswordLayout.setError("Password should be minimum of 8 characters");
                return;
                }else{
                    this.binding.changePasswordNewPasswordLayout.setError(null);
                }
                if(Objects.requireNonNull(confirm_password_layout.getEditText()).getText().toString().isEmpty()){
                    this.binding.changePasswordConfirmNewPasswordLayout.setError("Password field cannot be empty");
                    return;
                }else{
                    this.binding.changePasswordConfirmNewPasswordLayout.setError(null);
                }
                checkIfBothPasswordFieldsAreSame();
            }

            else if (id == R.id.change_password_back) {
                if (userType.equalsIgnoreCase("no")) {
                    Intent goToBuyerProfile = new Intent(this, BuyerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToBuyerProfile);
                    finish();
                } else if (userType.equalsIgnoreCase("yes")) {
                    Intent goToSellerProfile = new Intent(this, SellerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToSellerProfile);
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (userType.equalsIgnoreCase("no")) {
            Intent goToBuyerProfile = new Intent(this, BuyerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToBuyerProfile);
            finish();
        } else if (userType.equalsIgnoreCase("yes")) {
            Intent goToSellerProfile = new Intent(this, SellerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerProfile);
            finish();
        } else{
            helper.toastMsg(this, "Something went wrong!");
            helper.signOut(this,this);
        }
    }
}