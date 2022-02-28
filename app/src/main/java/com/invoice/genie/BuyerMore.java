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
import com.google.firebase.auth.FirebaseAuth;
import com.invoice.genie.databinding.ActivityBuyerMoreBinding;
import com.invoice.genie.helpers.RecursiveMethods;

public class BuyerMore extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, View.OnClickListener {

    ActivityBuyerMoreBinding binding;
    BottomNavigationView bottomNavigationView;
    Context context; Activity activity; RecursiveMethods helper; FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityBuyerMoreBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        context = this; activity = this; helper = new RecursiveMethods(); this.mAuth = FirebaseAuth.getInstance();

        //bottom navigation related
        bottomNavigationView = this.binding.buyerBottom;
        bottomNavigationView.setSelectedItemId(R.id.buyer_more);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.binding.buyerMoreProfileBar.setOnClickListener(this);
        this.binding.buyerMoreChangePasswordBar.setOnClickListener(this);
        this.binding.buyerMoreAboutUsBar.setOnClickListener(this);
        this.binding.buyerMoreSignOutBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();

            if(id==R.id.buyer_more_profile_bar){
                //go to profile screen
                Intent goToBuyerProfile = new Intent(this, BuyerProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToBuyerProfile);
                finish();
            }
            else if(id == R.id.buyer_more_change_password_bar){
                //go to change password screen
                Intent goToChangePassword = new Intent(this, ProfileChangePassword.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToChangePassword);
                finish();
            }
            else if(id == R.id.buyer_more_about_us_bar){
                //go to about us screen
                Intent goToAboutUs = new Intent(this, AboutUs.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(goToAboutUs);
                finish();
            }
            else if(id == R.id.buyer_more_sign_out_bar){
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
        if (itemId == R.id.buyer_more) {
            //do nothing
            return true;
        } else if (itemId == R.id.buyer_home) {
            Intent goToBuyerHome = new Intent(this, BuyerHome.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToBuyerHome);
            finish();
            return true;
        }
        return false;
    }
}