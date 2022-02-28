package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivityPlaceHolderBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.util.List;
import java.util.Objects;

public class PlaceHolder extends AppCompatActivity {

    ActivityPlaceHolderBinding binding;

    RecursiveMethods helper; private final String TAG = "Place Holder";
    Context context; String email; String seller;

    LoginViewModel loginViewModel;

    FirebaseAuth mAuth;

    Handler handler = new Handler(); Runnable runnable; int delay = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);

        this.binding = ActivityPlaceHolderBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        // to hide top status bar (system), use below line
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        helper = new RecursiveMethods();
        context = this;

        mAuth = FirebaseAuth.getInstance();

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());

        //get data from shared Preferences
        this.email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        this.seller = helper.getCurrentUserType(this);

        helper.logD(TAG, this.email);
        helper.logD(TAG, this.seller);

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                check();
            }
        }, delay);

    }

    private void check(){
        if(helper.getCurrentFirstName(context).equalsIgnoreCase("") || helper.getCurrentUserType(context).equalsIgnoreCase("")) {
            this.loginViewModel.getAllUsersInformation();
            this.loginViewModel.viewModelAllUsersInfoMutableLiveData.observe(this, new Observer<List<LoginInfo>>() {
                @Override
                public void onChanged(List<LoginInfo> userInfoList) {
                    for(LoginInfo singleUser : userInfoList){
                        if (singleUser.getEmail().equalsIgnoreCase(email)) {
                            helper.setCompanyName(context, singleUser.getCompany());
                            helper.setCurrentFirstName(context, singleUser.getFirst_name());
                            helper.setCurrentLastName(context, singleUser.getLast_name());
                            helper.setCurrentUserAvatar(context, singleUser.getImage());
                            helper.setCurrentUserType(context, singleUser.getSeller());
                            if (singleUser.getSeller().equalsIgnoreCase("yes")) {
                                Intent insertIntent = new Intent(context, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(insertIntent);
                                finish();
                            }
                            else if (singleUser.getSeller().equalsIgnoreCase("no")) {
                                Intent insertIntent = new Intent(context, BuyerHome.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(insertIntent);
                                finish();
                            }
                        }
                    }
                }
            });
        }
        else if (this.seller.equalsIgnoreCase("yes")) {
            Intent insertIntent = new Intent(context, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(insertIntent);
            finish();
        }
        else if (this.seller.equalsIgnoreCase("no")) {
            Intent insertIntent = new Intent(context, BuyerHome.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(insertIntent);
            finish();
        }
        else {
            helper.setCurrentUserType(this, "");
            helper.setCurrentUser(this, "");
            helper.setCompanyName(this,"");
            helper.setCurrentFirstName(this,"");
            helper.setCurrentLastName(this,"");
            helper.setCurrentUserAvatar(this,0);
            Intent insertIntent = new Intent(this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(insertIntent);
            finish();
        }
        handler.removeCallbacks(runnable);//stop loop
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helper.setCurrentUserType(this, "");
        helper.setCurrentUser(this, "");
        Intent insertIntent = new Intent(this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}