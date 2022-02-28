package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivitySellerMoreBinding;
import com.invoice.genie.helpers.RecursiveMethods;

public class SellerMore extends AppCompatActivity implements View.OnClickListener, NavigationBarView.OnItemSelectedListener {

    ActivitySellerMoreBinding binding;
    BottomNavigationView bottomNavigationView;
    Context context; Activity activity; RecursiveMethods helper; FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivitySellerMoreBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        context = this; activity = this; helper = new RecursiveMethods(); this.mAuth = FirebaseAuth.getInstance();

        //bottom navigation related
        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_more);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.binding.sellerMoreProfileBar.setOnClickListener(this);
        this.binding.sellerMoreChangePasswordBar.setOnClickListener(this);
        this.binding.sellerMoreAboutUsBar.setOnClickListener(this);
        this.binding.sellerMoreSignOutBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();

            if(id==R.id.seller_more_profile_bar){
                //go to profile screen
                Intent goToBuyerProfile = new Intent(this, Profile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToBuyerProfile);
                finish();
            }
            else if(id == R.id.seller_more_change_password_bar){
                //go to change password screen
                Intent goToChangePassword = new Intent(this, ProfileChangePassword.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToChangePassword);
                finish();
            }
            else if(id == R.id.seller_more_about_us_bar){
                //go to about us screen
                Intent goToAboutUs = new Intent(this, AboutUs.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToAboutUs);
                finish();
            }
            else if(id == R.id.seller_more_sign_out_bar){
                userConfirmation();
            }
        }
    }

    private void userConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to Sign Out?")
                .setTitle("Confirmation");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mAuth.signOut();
                Intent insertIntent1 = new Intent(context, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(insertIntent1);
                helper.setCurrentUser(context, "");
                helper.setCurrentFirstName(context,"");
                helper.setCurrentLastName(context,"");
                helper.setCurrentUserAvatar(context,1);
                helper.setCompanyName(context,"");
                helper.setCurrentUserType(context, "");
                activity.finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.seller_bottom_more) {
            return true;
        } else if (itemId == R.id.seller_bottom_home) {
            Intent goToSellerHome = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerHome);
            finish();
            return true;
        } else if (itemId == R.id.seller_bottom_products) {
            Intent goToSellerProducts = new Intent(this, Products.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerProducts);
            finish();
            return true;
        }
        return false;
    }
}