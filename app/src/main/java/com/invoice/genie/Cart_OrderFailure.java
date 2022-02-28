package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.invoice.genie.databinding.ActivityCartOrderFailureBinding;

public class Cart_OrderFailure extends AppCompatActivity implements View.OnClickListener {

    ActivityCartOrderFailureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartOrderFailureBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        this.binding.failureClose.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityCartOrderFailureBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        this.binding.failureClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();

            if(id==R.id.failure_close){
                Intent insertIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(insertIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent insertIntent = new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(insertIntent);
        finish();
    }
}