package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivitySignInBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    ActivitySignInBinding binding;

    RecursiveMethods helper; private final String TAG = "SignIn Class"; Context context;

    TextInputLayout email_layout, password_layout;

    FirebaseAuth mAuth; LoginViewModel loginViewModel; List<LoginInfo> allUsersInformation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this;

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        this.loginViewModel.getAllUsersInformation();
        this.allUsersInformation = this.loginViewModel.viewModelAllUserInfoList;


        this.binding.signInSignUpButton.setEnabled(true);
        this.binding.signInLoginButton.setEnabled(true);
        String loginText = getString(R.string.login);
        this.binding.signInLoginButton.setText(loginText);

        checkForLoggedInUser();

        email_layout = this.binding.signInEmailLayout;
        password_layout = this.binding.signInPasswordLayout;

        mAuth = FirebaseAuth.getInstance();

        this.binding.signInLoginButton.setOnClickListener(this);
        this.binding.signInSignUpButton.setOnClickListener(this);
        this.binding.signInForgetPasswordButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); context = this;

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        this.loginViewModel.getAllUsersInformation();
        this.allUsersInformation = this.loginViewModel.viewModelAllUserInfoList;


        this.binding.signInSignUpButton.setEnabled(true);
        this.binding.signInLoginButton.setEnabled(true);
        String loginText = getString(R.string.login);
        this.binding.signInLoginButton.setText(loginText);

        checkForLoggedInUser();

        email_layout = this.binding.signInEmailLayout;
        password_layout = this.binding.signInPasswordLayout;

        mAuth = FirebaseAuth.getInstance();

        this.binding.signInLoginButton.setOnClickListener(this);
        this.binding.signInSignUpButton.setOnClickListener(this);
        this.binding.signInForgetPasswordButton.setOnClickListener(this);
    }

    private void checkForLoggedInUser() {
        if (helper.getCurrentUser(this).equalsIgnoreCase("") && helper.getCurrentUserType(this).equalsIgnoreCase("")) {
            //do nothing
            helper.logD(TAG, "No user logged in");
        } else {
            Intent insertIntent = new Intent(this, PlaceHolder.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(insertIntent);
            finish();
        }
    }

    public boolean validateEmail() {
        String enteredEmail = Objects.requireNonNull(this.binding.signInEmailLayout.getEditText()).getText().toString();
        if (enteredEmail.isEmpty()) {
            email_layout.setError(getString(R.string.enter_email_id));
            return false;
        } else {
            email_layout.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String enteredPass = Objects.requireNonNull(this.binding.signInPasswordLayout.getEditText()).getText().toString();
        if (enteredPass.isEmpty()) {
            password_layout.setError(getString(R.string.enter_password));
            return false;
        } else {
            password_layout.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            if (id == R.id.sign_in_sign_up_button) {
                Intent insertIntent = new Intent(this, SignUp.class);
                startActivity(insertIntent);
            }
            else if (id == R.id.sign_in_login_button) {
                if (!validateEmail() | !validatePassword()) {
                    return;
                }

                String userEmail = Objects.requireNonNull(this.binding.signInEmailLayout.getEditText()).getText().toString();
                String userPassword = Objects.requireNonNull(this.binding.signInPasswordLayout.getEditText()).getText().toString();

                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    helper.setCurrentUser(context, userEmail);
                                    Intent insertIntent = new Intent(SignIn.this, PlaceHolder.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    insertIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(insertIntent);
                                    finish();
                                } else{
                                    helper.toastMsg(context, "Login Failed! Try Again!");
                                }
                            }
                        });
                Objects.requireNonNull(this.binding.signInEmailLayout.getEditText()).getText().clear();
                Objects.requireNonNull(this.binding.signInPasswordLayout.getEditText()).getText().clear();
            }
            else if(id == R.id.sign_in_forget_password_button){
                String userEmail = Objects.requireNonNull(this.binding.signInEmailLayout.getEditText()).getText().toString();
                if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    this.binding.signInEmailLayout.setError("Enter Valid Email ID");
                } else {
                    this.binding.signInEmailLayout.setError(null);
                    mAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        binding.signInEmailLayout.getEditText().getText().clear();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Password RESET Email Sent")
                                                .setTitle("Success");
                                        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create();
                                        builder.show();
                                    }
                                }
                            });
                }
            }
        }
    }
}