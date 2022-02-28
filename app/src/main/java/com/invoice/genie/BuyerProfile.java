package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivityBuyerProfileBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuyerProfile extends AppCompatActivity implements View.OnClickListener {

    ActivityBuyerProfileBinding binding;

    LoginViewModel loginViewModel; private List<LoginInfo> userInfoList = new ArrayList<>(); FirebaseAuth mAuth;

    RecursiveMethods helper; String current_user;

    Context context; Activity activity; boolean firstInstance = true;

    TextView profile_email; TextInputLayout first_name_layout, last_name_layout; ImageView profileAvatar;

    ImageButton imageButton1, imageButton2; Button save, cancel;
    private AlertDialog dialog; int image = 0; int selectedImage = 0; int dbSetImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityBuyerProfileBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); helper.isCacheExists(this); firstInstance = true;
        current_user = helper.getCurrentUser(this); context = this; activity = this;

        this.mAuth = FirebaseAuth.getInstance();

        this.profile_email = this.binding.emailTitleProfile;
        this.first_name_layout = this.binding.buyerFirstNameLayout;
        this.last_name_layout = this.binding.buyerLastNameLayout;
        this.profileAvatar = this.binding.buyerProfilePic;
        if(Objects.requireNonNull(this.mAuth.getCurrentUser()).isEmailVerified()){
            String verified = "EMAIL VERIFIED";
            binding.emailNotVerified.setText(verified);
            binding.emailNotVerified.setTextColor(Color.parseColor("#006400"));
            binding.emailNotVerified.setEnabled(false);
        }else{
            String not_verified = "EMAIL NOT VERIFIED, CLICK TO RESEND";
            binding.emailNotVerified.setText(not_verified);
            binding.emailNotVerified.setEnabled(true);
        }

        this.binding.buyerProfileUpdateButton.setEnabled(false);
        this.binding.buyerProfileGoBack.setEnabled(false);
        disableFields(); assignValuesFromShared();

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        if(helper.getCurrentFirstName(this).equalsIgnoreCase("")){
            this.loginViewModel.getAllUsersInformation();
            this.userInfoList = this.loginViewModel.viewModelAllUserInfoList;
            this.loginViewModel.viewModelAllUsersInfoMutableLiveData.observe(this, new Observer<List<LoginInfo>>() {
                @Override
                public void onChanged(List<LoginInfo> userInfoList) {
                    for(LoginInfo singleUser : userInfoList){
                        if (singleUser.getEmail().equalsIgnoreCase(current_user)) {
                            if(firstInstance){
                                assignValuesFromFirestore(singleUser);
                                firstInstance = false;
                            }
                        }
                    }
                }
            });
        }

        this.binding.buyerProfileGoBack.setOnClickListener(this);
        this.binding.buyerProfileUpdateButton.setOnClickListener(this);
        this.binding.buyerProfilePic.setOnClickListener(this);
        this.binding.emailNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().sendEmailVerification();
                helper.toastMsg(context, "Sent Email Verification!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityBuyerProfileBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); helper.isCacheExists(this); firstInstance = true;
        current_user = helper.getCurrentUser(this); context = this; activity = this;

        this.mAuth = FirebaseAuth.getInstance();

        this.profile_email = this.binding.emailTitleProfile;
        this.first_name_layout = this.binding.buyerFirstNameLayout;
        this.last_name_layout = this.binding.buyerLastNameLayout;
        this.profileAvatar = this.binding.buyerProfilePic;
        if(Objects.requireNonNull(this.mAuth.getCurrentUser()).isEmailVerified()){
            String verified = "EMAIL VERIFIED";
            binding.emailNotVerified.setText(verified);
            binding.emailNotVerified.setTextColor(Color.parseColor("#006400"));
            binding.emailNotVerified.setEnabled(false);
        }else{
            String not_verified = "EMAIL NOT VERIFIED, CLICK TO RESEND";
            binding.emailNotVerified.setText(not_verified);
            binding.emailNotVerified.setEnabled(true);
        }

        this.binding.buyerProfileUpdateButton.setEnabled(false);
        this.binding.buyerProfileGoBack.setEnabled(false);
        disableFields(); assignValuesFromShared();

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        if(helper.getCurrentFirstName(this).equalsIgnoreCase("")){
            this.loginViewModel.getAllUsersInformation();
            this.userInfoList = this.loginViewModel.viewModelAllUserInfoList;
            this.loginViewModel.viewModelAllUsersInfoMutableLiveData.observe(this, new Observer<List<LoginInfo>>() {
                @Override
                public void onChanged(List<LoginInfo> userInfoList) {
                    for(LoginInfo singleUser : userInfoList){
                        if (singleUser.getEmail().equalsIgnoreCase(current_user)) {
                            if(firstInstance){
                                assignValuesFromFirestore(singleUser);
                                firstInstance = false;
                            }
                        }
                    }
                }
            });
        }

        this.binding.buyerProfileGoBack.setOnClickListener(this);
        this.binding.buyerProfileUpdateButton.setOnClickListener(this);
        this.binding.buyerProfilePic.setOnClickListener(this);
        this.binding.emailNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().sendEmailVerification();
                helper.toastMsg(context, "Sent Email Verification!");
            }
        });
    }

    private void disableFields() {
        this.first_name_layout.setEnabled(false);
        this.last_name_layout.setEnabled(false);
        this.profileAvatar.setEnabled(false);
    }

    private void assignValuesFromShared(){
        String first_name = helper.getCurrentFirstName(this);
        String last_name = helper.getCurrentLastName(this);
        String email = helper.getCurrentUser(this);
        int avatar_num = helper.getCurrentUserAvatar(this);

        binding.buyerLoadingProfileScreen.setVisibility(View.GONE);
        binding.buyerProfileUpdateButton.setEnabled(true);
        binding.buyerProfileGoBack.setEnabled(true);

        Objects.requireNonNull(first_name_layout.getEditText()).setText(first_name);
        Objects.requireNonNull(last_name_layout.getEditText()).setText(last_name);
        profile_email.setText(email);
        setImage(avatar_num);
    }

    private void assignValuesFromFirestore(LoginInfo loginInfo){
        String first_name = loginInfo.getFirst_name();
        String last_name = loginInfo.getLast_name();
        String company_name = loginInfo.getCompany();
        String email = current_user;
        int avatar_num = loginInfo.getImage();

        helper.setCompanyName(context, company_name);
        helper.setCurrentFirstName(context, first_name);
        helper.setCurrentLastName(context, last_name);
        helper.setCurrentUserAvatar(context,avatar_num);

        binding.buyerLoadingProfileScreen.setVisibility(View.GONE);
        binding.loadedBuyerProfileScreen.setEnabled(true);
        binding.buyerProfileGoBack.setEnabled(true);

        Objects.requireNonNull(first_name_layout.getEditText()).setText(first_name);
        Objects.requireNonNull(last_name_layout.getEditText()).setText(last_name);
        profile_email.setText(email);
        setImage(avatar_num);
    }

    private void avatarPopUp() {
        image = 0; selectedImage = 0; dbSetImage = 0;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.avatar_picker, null);
        imageButton1 = popUpView.findViewById(R.id.male_avatar_01);
        imageButton2 = popUpView.findViewById(R.id.female_avatar_01);

        save = popUpView.findViewById(R.id.avatar_save);
        cancel = popUpView.findViewById(R.id.avatar_close);

        dialogBuilder.setView(popUpView);
        dialog = dialogBuilder.create();
        dialog.show();

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = 1;
                imageButton1.setBackgroundColor(getColor(R.color.black));
                imageButton2.setBackgroundColor(getColor(R.color.white));
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton2.setBackgroundColor(getColor(R.color.black));
                imageButton1.setBackgroundColor(getColor(R.color.white));
                image = 2;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image == 0 ){
                    helper.toastMsg(getApplicationContext(), "Click on any Image to select");
                }else{
                    selectedImage = image;
                    dbSetImage = image;
                    helper.toastMsg(getApplicationContext(), "Selected image - "+selectedImage);
                    dialog.dismiss();
                    setImage(image);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = 0;
                dialog.dismiss();
            }
        });
    }

    private void setImage(int i) {
        if(i == 0 || i == 1){
            this.binding.buyerProfilePic.setImageResource(R.drawable.male_01);
        }else if(i == 2){
            this.binding.buyerProfilePic.setImageResource(R.drawable.female_01);
        }else{
            this.binding.buyerProfilePic.setImageResource(R.drawable.male_01);
        }
    }

    private boolean validateFields() {
        if(Objects.requireNonNull(this.binding.buyerFirstNameField.getText()).toString().isEmpty()){
            this.binding.buyerFirstNameLayout.setError(getString(R.string.first_name_empty));
            return  false;
        }else{
            this.binding.buyerFirstNameLayout.setError(null);
        }
        if(this.binding.buyerFirstNameField.getText().toString().length()>15){
            this.binding.buyerFirstNameLayout.setError("First name should be maximum 15 characters");
            return  false;
        }else{
            this.binding.buyerFirstNameLayout.setError(null);
        }
        if(Objects.requireNonNull(this.binding.buyerLastNameField.getText()).toString().isEmpty()){
            this.binding.buyerLastNameLayout.setError(getString(R.string.last_name_empty));
            return false;
        }else{
            this.binding.buyerLastNameLayout.setError(null);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            String save = getString(R.string.save);
            String update = getString(R.string.update_profile);
            String cancel = getString(R.string.cancel);
            String goBack = getString(R.string.back);

            if (id == R.id.buyer_profile_go_back) {
                String buttonText = String.valueOf(this.binding.buyerProfileGoBack.getText());
                if (!buttonText.equalsIgnoreCase("Cancel")) {
                    Intent goToBuyerMore = new Intent(this, BuyerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToBuyerMore);
                    finish();
                } else{
                    assignValuesFromShared();
                    binding.buyerProfileUpdateButton.setText(update); binding.buyerProfileGoBack.setText(goBack); disableFields();
                }
            }

            else if (id == R.id.buyer_profile_update_button) {
                if (binding.buyerProfileUpdateButton.getText().toString().equalsIgnoreCase(save)) {
                    if(validateFields()){
                        String first = Objects.requireNonNull(first_name_layout.getEditText()).getText().toString();
                        String last = Objects.requireNonNull(last_name_layout.getEditText()).getText().toString();
                        String company_name = "NA";
                        String seller = "no";

                        LoginInfo updatedLoginInfo = new LoginInfo();
                        updatedLoginInfo.setEmail(current_user);
                        updatedLoginInfo.setCompany(company_name);
                        updatedLoginInfo.setImage(dbSetImage);
                        updatedLoginInfo.setFirst_name(first);
                        updatedLoginInfo.setLast_name(last);
                        updatedLoginInfo.setSeller(seller);

                        helper.setCurrentFirstName(context, first);
                        helper.setCurrentLastName(context,last);
                        helper.setCompanyName(context,company_name);
                        helper.setCurrentUserAvatar(context,dbSetImage);

                        this.loginViewModel.updateUserInformation(updatedLoginInfo);

                        binding.buyerProfileUpdateButton.setText(update); binding.buyerProfileGoBack.setText(goBack); disableFields();
                    }
                } else {
                    first_name_layout.setEnabled(true);
                    last_name_layout.setEnabled(true);
                    profileAvatar.setEnabled(true);
                    binding.buyerProfileUpdateButton.setText(save);
                    binding.buyerProfileGoBack.setText(cancel);
                }
            }

            else if (id == R.id.buyer_profile_pic){
                avatarPopUp();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent insertIntent = new Intent(this, BuyerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}
