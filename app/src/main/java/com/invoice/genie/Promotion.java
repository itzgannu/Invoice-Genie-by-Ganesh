package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.invoice.genie.databinding.ActivityPromotionBinding;

public class Promotion extends AppCompatActivity {

    ActivityPromotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityPromotionBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        this.binding.promotionClose.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}