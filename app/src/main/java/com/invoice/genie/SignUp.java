package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.invoice.genie.databinding.ActivitySignUpBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding binding;

    LoginViewModel loginViewModel; private List<LoginInfo> allUsersInfoList = new ArrayList<>(); private LoginInfo userInfo;
    String userEmail, userPassword; FirebaseAuth mAuth;

    RecursiveMethods helper; private final String TAG = "SignUp Class"; Context context;

    private boolean seller = false; private int clicked = 0; String companyText;

    TextInputLayout email_layout, first_name_layout, last_name_layout, password_layout, confirm_layout, company_layout;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this;

        this.userInfo = new LoginInfo();

        this.mAuth = FirebaseAuth.getInstance();

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        this.loginViewModel.getAllUsersInformation();
        this.allUsersInfoList = this.loginViewModel.viewModelAllUserInfoList;

        email_layout = this.binding.signUpEmailLayout;
        first_name_layout = this.binding.signUpFirstNameLayout;
        last_name_layout = this.binding.signUpLastNameLayout;
        password_layout = this.binding.signUpPasswordLayout;
        confirm_layout = this.binding.signUpConfirmLayout;
        company_layout = this.binding.signUpCompanyLayout;

        RadioGroup radioGroup = this.binding.signUpRadioGroup;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sign_up_seller_radio) {
                    helper.logD(TAG, "Selected Seller Option");
                    clicked = 1;
                    seller = true;
                    binding.signUpCompanyLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.sign_up_buyer_radio) {
                    helper.logD(TAG, "Selected Buyer Option");
                    clicked = 1;
                    seller = false;
                    binding.signUpCompanyLayout.setVisibility(View.GONE);
                    companyText = "NA";
                }
            }
        });

        this.binding.signUpCreateButton.setOnClickListener(this);
        this.binding.signUpGoBackButton.setOnClickListener(this);
    }

    private void clearTextEntries() {
        helper.logD(TAG, "Clearing fields");

        Objects.requireNonNull(email_layout.getEditText()).getText().clear();
        Objects.requireNonNull(first_name_layout.getEditText()).getText().clear();
        Objects.requireNonNull(last_name_layout.getEditText()).getText().clear();
        Objects.requireNonNull(password_layout.getEditText()).getText().clear();
        Objects.requireNonNull(confirm_layout.getEditText()).getText().clear();
        Objects.requireNonNull(company_layout.getEditText()).getText().clear();
    }

    private void saveProfileToDB() {
        showProgressBar();
        helper.logD(TAG, "Saving Profile to Firebase");

        String first_name = Objects.requireNonNull(first_name_layout.getEditText()).getText().toString();
        String last_name = Objects.requireNonNull(last_name_layout.getEditText()).getText().toString();
        userEmail = Objects.requireNonNull(email_layout.getEditText()).getText().toString();
        userPassword= Objects.requireNonNull(password_layout.getEditText()).getText().toString();
        int image = 1;
        companyText = "NA";

        this.userInfo.setEmail(userEmail);
        this.userInfo.setFirst_name(first_name);
        this.userInfo.setLast_name(last_name);
        this.userInfo.setImage(image);

        if (seller) {
            helper.logD(TAG, "Creating seller profile");
            String company = Objects.requireNonNull(company_layout.getEditText()).getText().toString();
            this.userInfo.setSeller(getString(R.string.yes));
            this.userInfo.setCompany(company);
        } else {
            helper.logD(TAG, "Creating buyer profile");
            this.userInfo.setSeller(getString(R.string.no));
            this.userInfo.setCompany(companyText);
        }

        this.mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            loginViewModel.createUserInformation(userInfo);
                            helper.logD(TAG, "Account Created Successfully");
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            Objects.requireNonNull(firebaseUser).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if(progressDialog!=null){
                                        progressDialog.dismiss();
                                    }
                                    userConfirmation();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(progressDialog!=null){
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void showProgressBar(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    private void userConfirmation() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Account Created")
                .setTitle("Success");
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
        if (v != null) {
            int id = v.getId();

            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            this.binding.signUpEmailLayout.setError(null);
            this.binding.signUpFirstNameLayout.setError(null);
            this.binding.signUpLastNameLayout.setError(null);
            this.binding.signUpCompanyLayout.setError(null);
            this.binding.signUpPasswordLayout.setError(null);
            this.binding.signUpConfirmLayout.setError(null);

            this.loginViewModel.getAllUsersInformation();
            this.allUsersInfoList = this.loginViewModel.viewModelAllUserInfoList;

            if (id == R.id.sign_up_create_button) {
                helper.logD(TAG, "Clicked on CREATE Button");

                String email_text = Objects.requireNonNull(this.binding.signUpEmailLayout.getEditText()).getText().toString();
                String first_name_text = Objects.requireNonNull(this.binding.signUpFirstNameLayout.getEditText()).getText().toString();
                String last_name_text = Objects.requireNonNull(this.binding.signUpLastNameLayout.getEditText()).getText().toString();
                String password_text = Objects.requireNonNull(this.binding.signUpPasswordLayout.getEditText()).getText().toString();
                String confirm_password_text = Objects.requireNonNull(this.binding.signUpConfirmLayout.getEditText()).getText().toString();
                String company_text = Objects.requireNonNull(this.binding.signUpCompanyLayout.getEditText()).getText().toString();

                if (email_text.isEmpty()) {
                    helper.logD(TAG, "Email ID field is empty");
                    email_layout.setError(getString(R.string.enter_email_id));
                    return;
                } else {
                    email_layout.setError(null);
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    this.binding.signUpEmailLayout.setError(getString(R.string.enter_valid_email));
                    return;
                } else {
                    email_layout.setError(null);
                }
                this.loginViewModel.getAllUsersInformation();
                this.allUsersInfoList = this.loginViewModel.viewModelAllUserInfoList;
                for (LoginInfo userInfo : this.allUsersInfoList) {
                    String email, enteredEmail;
                    email = userInfo.getEmail();
                    enteredEmail = email_text;
                    if (enteredEmail.equalsIgnoreCase(email)) {
                        this.binding.signUpEmailLayout.setError(getString(R.string.email_already_used));
                        return;
                    } else {
                        this.binding.signUpEmailLayout.setError(null);
                    }
                }

                if (first_name_text.isEmpty()) {
                    helper.logD(TAG, "First Name field is empty");
                    first_name_layout.setError(getString(R.string.enter_first_name));
                    return;
                } else {
                    first_name_layout.setError(null);
                }
                if (first_name_text.length() > 15) {
                    this.binding.signUpFirstNameLayout.setError(getString(R.string.first_15_max));
                    return;
                } else {
                    first_name_layout.setError(null);
                }
                if (last_name_text.isEmpty()) {
                    helper.logD(TAG, "Last Name field is empty");
                    last_name_layout.setError(getString(R.string.enter_last_name));
                    return;
                } else {
                    last_name_layout.setError(null);
                }
                if (last_name_text.length() > 15) {
                    this.binding.signUpLastNameLayout.setError(getString(R.string.last_15_max));
                    return;
                } else {
                    last_name_layout.setError(null);
                }

                if (seller) {
                    if (company_text.isEmpty()) {
                        this.binding.signUpCompanyLayout.setError(getString(R.string.enter_company));
                        return;
                    } else {
                        this.binding.signUpCompanyLayout.setError(null);
                    }

                    if (this.allUsersInfoList != null) {
                        for (LoginInfo userInfo : this.allUsersInfoList) {
                            String company, enteredCompany;
                            company = userInfo.getCompany();
                            enteredCompany = company_text;
                            if (enteredCompany.equalsIgnoreCase(company)) {
                                this.binding.signUpCompanyLayout.setError(getString(R.string.company_used));
                                return;
                            } else {
                                this.binding.signUpCompanyLayout.setError(null);
                            }
                        }
                    }
                }

                if (password_text.isEmpty()) {
                    helper.logD(TAG, "Password field is empty");
                    password_layout.setError(getString(R.string.enter_password));
                    return;
                } else {
                    password_layout.setError(null);
                }

                if (password_text.length() < 8) {
                    this.binding.signUpPasswordLayout.setError(getString(R.string.min_8_password));
                    return;
                } else {
                    password_layout.setError(null);
                }

                if (confirm_password_text.isEmpty()) {
                    helper.logD(TAG, "Confirm Password field is empty");
                    confirm_layout.setError(getString(R.string.enter_password));
                    return;
                } else {
                    confirm_layout.setError(null);
                }

                if (!password_text.equals(confirm_password_text)) {
                    Objects.requireNonNull(password_layout.getEditText()).getText().clear();
                    Objects.requireNonNull(confirm_layout.getEditText()).getText().clear();
                    helper.logD(TAG, "Password fields are not same");
                    confirm_layout.setError("Enter Same Password");
                    return;
                } else {
                    confirm_layout.setError(null);
                }

                if (clicked == 0) {
                    helper.logD(TAG, "No option is selected (Seller/Buyer)");
                    helper.toastMsg(this, getString(R.string.toast_select_sell_buy));
                    return;
                }

                if (seller && company_text.isEmpty()) {
                    helper.logD(TAG, "Company Name field is empty");
                    company_layout.setError(getString(R.string.enter_company));
                    return;
                } else if(seller && company_text.length()>15){
                    company_layout.setError("Company Name cannot be more than 15 characters");
                    company_layout.setError(null);
                }

                this.saveProfileToDB();
                this.clearTextEntries();
            } else if (id == R.id.sign_up_go_back_button) {
                this.clearTextEntries();
                helper.logD(TAG, "Clicked on GO BACK button");
                Intent insertIntent = new Intent(this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(insertIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.clearTextEntries();
        helper.logD(TAG, "Clicked on Android Back button");
        Intent insertIntent = new Intent(this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}