package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.invoice.genie.databinding.ActivityAboutUsBinding;
import com.invoice.genie.helpers.RecursiveMethods;

public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    ActivityAboutUsBinding binding;
    String userType;
    RecursiveMethods helper = new RecursiveMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        userType = helper.getCurrentUserType(this);

        this.binding.aboutUsBack.setOnClickListener(this);
        this.binding.aboutUsWriteToUs.setOnClickListener(this);
        this.binding.aboutUsCallUs.setOnClickListener(this);
        this.binding.aboutUsPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();

            if(id == R.id.about_us_back){
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
            else if(id == R.id.about_us_write_to_us){
                Intent openEmail = new Intent(Intent.ACTION_SEND);
                openEmail.setType("plain/text");
                openEmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "invoice.genie@gmail.com" });
                openEmail.putExtra(Intent.EXTRA_SUBJECT, "Customer Feedback");
                startActivity(Intent.createChooser(openEmail, ""));
            }
            else if(id == R.id.about_us_call_us){
                Intent openDialer = new Intent(Intent.ACTION_DIAL);
                openDialer.setData(Uri.parse("tel:+12499990449"));
                startActivity(openDialer);
            }
            else if(id == R.id.about_us_privacy_policy){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/live/6614714f-d25b-47c1-9cb0-45f8f419a813"));
                startActivity(browserIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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