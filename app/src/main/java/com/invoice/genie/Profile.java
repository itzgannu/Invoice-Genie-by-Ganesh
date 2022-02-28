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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivityProfileBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    ActivityProfileBinding binding;

    LoginViewModel loginViewModel; private List<LoginInfo> userInfoList = new ArrayList<>(); FirebaseAuth mAuth;

    RecursiveMethods helper; String current_user; private final String TAG = "Profile Seller Class";

    Context context; Activity activity; boolean firstInstance = true;

    TextView profile_email; TextInputLayout first_name_layout, last_name_layout, company_layout; ImageView profile_avatar;

    ImageButton imageButton1, imageButton2; Button save, cancel;
    private AlertDialog dialog; int image = 0; int selectedImage = 0; int dbSetImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        context = this; activity = this;

        helper = new RecursiveMethods(); helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this); firstInstance = true;

        this.mAuth = FirebaseAuth.getInstance();

        profile_email = this.binding.sellerEmailTitle;
        first_name_layout = this.binding.sellerFirstNameLayout;
        last_name_layout = this.binding.sellerLastNameLayout;
        company_layout = this.binding.sellerCompanyNameLayout;
        profile_avatar = this.binding.sellerProfilePic;

        this.binding.sellerUpdateProfileButton.setEnabled(false);
        this.binding.sellerGoBack.setEnabled(false);
        disableFields(); assignValuesFromShared();
        if(Objects.requireNonNull(this.mAuth.getCurrentUser()).isEmailVerified()){
            String verified = "EMAIL VERIFIED";
            binding.sellerEmailNotVerified.setText(verified);
            binding.sellerEmailNotVerified.setTextColor(Color.parseColor("#006400"));
            binding.sellerEmailNotVerified.setEnabled(false);
        }else{
            String not_verified = "EMAIL NOT VERIFIED, CLICK TO RESEND";
            binding.sellerEmailNotVerified.setText(not_verified);
            binding.sellerEmailNotVerified.setEnabled(true);
        }

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

        this.binding.sellerGoBack.setOnClickListener(this);
        this.binding.sellerUpdateProfileButton.setOnClickListener(this);
        this.binding.sellerProfilePic.setOnClickListener(this);
        this.binding.sellerEmailNotVerified.setOnClickListener(new View.OnClickListener() {
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

        this.binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        context = this; activity = this;

        helper = new RecursiveMethods(); helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this); firstInstance = true;

        this.mAuth = FirebaseAuth.getInstance();

        profile_email = this.binding.sellerEmailTitle;
        first_name_layout = this.binding.sellerFirstNameLayout;
        last_name_layout = this.binding.sellerLastNameLayout;
        company_layout = this.binding.sellerCompanyNameLayout;
        profile_avatar = this.binding.sellerProfilePic;

        this.binding.sellerUpdateProfileButton.setEnabled(false);
        this.binding.sellerGoBack.setEnabled(false);
        disableFields(); assignValuesFromShared();
        if(Objects.requireNonNull(this.mAuth.getCurrentUser()).isEmailVerified()){
            String verified = "EMAIL VERIFIED";
            binding.sellerEmailNotVerified.setText(verified);
            binding.sellerEmailNotVerified.setTextColor(Color.parseColor("#006400"));
            binding.sellerEmailNotVerified.setEnabled(false);
        }else{
            String not_verified = "EMAIL NOT VERIFIED, CLICK TO RESEND";
            binding.sellerEmailNotVerified.setText(not_verified);
            binding.sellerEmailNotVerified.setEnabled(true);
        }

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

        this.binding.sellerGoBack.setOnClickListener(this);
        this.binding.sellerUpdateProfileButton.setOnClickListener(this);
        this.binding.sellerProfilePic.setOnClickListener(this);
        this.binding.sellerEmailNotVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().sendEmailVerification();
                helper.toastMsg(context, "Sent Email Verification!");
            }
        });
    }

    private void disableFields() {
        first_name_layout.setEnabled(false);
        last_name_layout.setEnabled(false);
        company_layout.setEnabled(false);
        profile_avatar.setEnabled(false);
    }

    private void assignValuesFromShared(){
        String first_name = helper.getCurrentFirstName(this);
        String last_name = helper.getCurrentLastName(this);
        String company_name = helper.getCompanyName(this);
        String email = helper.getCurrentUser(this);
        int avatar_num = helper.getCurrentUserAvatar(this);

        binding.sellerLoadingProfileScreen.setVisibility(View.GONE);
        binding.sellerUpdateProfileButton.setEnabled(true);
        binding.sellerGoBack.setEnabled(true);

        Objects.requireNonNull(first_name_layout.getEditText()).setText(first_name);
        Objects.requireNonNull(last_name_layout.getEditText()).setText(last_name);
        Objects.requireNonNull(company_layout.getEditText()).setText(company_name);
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

        binding.sellerLoadingProfileScreen.setVisibility(View.GONE);
        binding.sellerUpdateProfileButton.setEnabled(true);
        binding.sellerGoBack.setEnabled(true);

        Objects.requireNonNull(first_name_layout.getEditText()).setText(first_name);
        Objects.requireNonNull(last_name_layout.getEditText()).setText(last_name);
        Objects.requireNonNull(company_layout.getEditText()).setText(company_name);
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
                if (image == 0) {
                    helper.toastMsg(getApplicationContext(), "Click on any Image to select");
                } else {
                    selectedImage = image;
                    dbSetImage = image;
                    helper.toastMsg(getApplicationContext(), "Selected image - " + selectedImage);
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
        if (i == 0 || i == 1) {
            this.binding.sellerProfilePic.setImageResource(R.drawable.male_01);
        } else if (i == 2) {
            this.binding.sellerProfilePic.setImageResource(R.drawable.female_01);
        } else {
            this.binding.sellerProfilePic.setImageResource(R.drawable.male_01);
        }
    }

    private boolean validateFields() {
        this.loginViewModel.getAllUsersInformation();
        this.userInfoList = this.loginViewModel.viewModelAllUserInfoList;
        if (Objects.requireNonNull(this.binding.sellerFirstName.getText()).toString().isEmpty()) {
            this.binding.sellerFirstNameLayout.setError(getString(R.string.first_name_empty));
            return false;
        } else {
            this.binding.sellerFirstNameLayout.setError(null);
        }

        if (this.binding.sellerFirstName.getText().toString().length() > 15) {
            this.binding.sellerFirstNameLayout.setError("First name should be maximum 15 characters");
            return false;
        } else {
            this.binding.sellerFirstNameLayout.setError(null);
        }

        if (Objects.requireNonNull(this.binding.sellerLastName.getText()).toString().isEmpty()) {
            this.binding.sellerLastNameLayout.setError(getString(R.string.last_name_empty));
            return false;
        } else {
            this.binding.sellerLastNameLayout.setError(null);
        }

        if (this.binding.sellerLastName.getText().toString().length() > 15) {
            this.binding.sellerLastNameLayout.setError("Last name should be maximum of 15 characters");
            return false;
        } else {
            this.binding.sellerLastNameLayout.setError(null);
        }

        if (Objects.requireNonNull(this.binding.sellerCompanyName.getText()).toString().isEmpty()) {
            this.binding.sellerCompanyNameLayout.setError("Company name cannot be empty");
            return false;
        } else {
            this.binding.sellerCompanyNameLayout.setError(null);
        }

        if (this.userInfoList != null) {
            for (LoginInfo loginInfo : this.userInfoList) {
                String email = helper.getCurrentUser(this);
                String company = loginInfo.getCompany();
                String enteredCompany = this.binding.sellerCompanyName.getText().toString();
                if (enteredCompany.equalsIgnoreCase(company)) {
                    if (!loginInfo.getEmail().equalsIgnoreCase(email)) {
                        this.binding.sellerCompanyNameLayout.setError(getString(R.string.company_registered));
                        return false;
                    }
                }
            }
        }

        if (this.binding.sellerCompanyName.getText().toString().length() > 15) {
            this.binding.sellerCompanyNameLayout.setError("Company name cannot be more than 15 characters");
            return false;
        } else {
            this.binding.sellerCompanyNameLayout.setError(null);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            this.loginViewModel.getAllUsersInformation();
            this.userInfoList = this.loginViewModel.viewModelAllUserInfoList;
            String save = getString(R.string.save);
            String update = getString(R.string.update_profile);
            String cancel = getString(R.string.cancel);
            String goBack = getString(R.string.back);
            if (id == R.id.seller_go_back) {
                String buttonText = String.valueOf(this.binding.sellerGoBack.getText());
                if (!buttonText.equalsIgnoreCase("Cancel")) {
                    Intent goToSellerMore = new Intent(this, SellerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(goToSellerMore);
                    finish();
                } else{
                    assignValuesFromShared();
                    binding.sellerUpdateProfileButton.setText(update); binding.sellerGoBack.setText(goBack); disableFields();
                }
            }

            else if (id == R.id.seller_update_profile_button) {
                if (binding.sellerUpdateProfileButton.getText().toString().equalsIgnoreCase(save)) {
                    if (validateFields()) {
                        String first = Objects.requireNonNull(this.binding.sellerFirstName.getText()).toString();
                        String last = Objects.requireNonNull(this.binding.sellerLastName.getText()).toString();
                        String company_name = Objects.requireNonNull(this.binding.sellerCompanyName.getText()).toString();
                        String seller = "yes";

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

                        binding.sellerUpdateProfileButton.setText(update); binding.sellerGoBack.setText(goBack); disableFields();
                    }
                } else {
                    first_name_layout.setEnabled(true);
                    last_name_layout.setEnabled(true);
                    company_layout.setEnabled(true);
                    profile_avatar.setEnabled(true);
                    binding.sellerUpdateProfileButton.setText(save);
                    binding.sellerGoBack.setText(cancel);
                }
            }

            else if (id == R.id.seller_profile_pic) {
                avatarPopUp();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //enable touch
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent insertIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}
