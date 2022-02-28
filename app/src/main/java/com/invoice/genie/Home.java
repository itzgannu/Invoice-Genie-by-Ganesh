package com.invoice.genie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.invoice.genie.adapter.RecyclerHomeView;
import com.invoice.genie.databinding.ActivityHomeBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.viewmodels.InvoiceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

    ActivityHomeBinding binding;
    RecursiveMethods helper; String email;

    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    RecyclerHomeView recyclerHomeView;

    InvoiceViewModel invoiceViewModel;
    List<Invoice> invoiceList = new ArrayList<>();

    boolean exitApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        exitApp = false;

        helper = new RecursiveMethods();
        email = helper.getCurrentUser(this);
        helper.isCacheExists(this);

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        setWelcomeMessage();

        //bottom navigation related
        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_home);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.invoiceViewModel.viewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                initializeRecyclerView(invoices);
            }
        });
        this.invoiceList = this.invoiceViewModel.getInvoices(email);

        this.binding.createOrder.setOnClickListener(this);

        this.binding.invoiceSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        exitApp = false;

        helper = new RecursiveMethods();
        email = helper.getCurrentUser(this);
        helper.isCacheExists(this);

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        setWelcomeMessage();

        //bottom navigation related
        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_home);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.invoiceViewModel.viewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                initializeRecyclerView(invoices);
            }
        });
        this.invoiceList = this.invoiceViewModel.getInvoices(email);

        this.binding.createOrder.setOnClickListener(this);

        this.binding.invoiceSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        this.binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        exitApp = false;

        helper = new RecursiveMethods();
        email = helper.getCurrentUser(this);
        helper.isCacheExists(this);

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        setWelcomeMessage();

        //bottom navigation related
        bottomNavigationView = this.binding.bottomNavigation;
        bottomNavigationView.setSelectedItemId(R.id.seller_bottom_home);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.invoiceViewModel.viewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                initializeRecyclerView(invoices);
            }
        });
        this.invoiceList = this.invoiceViewModel.getInvoices(email);

        this.binding.createOrder.setOnClickListener(this);

        this.binding.invoiceSearchText.addTextChangedListener(this);
    }

    /*
    Initialize recycler view
    */
    private void initializeRecyclerView(List<Invoice> invoice) {
        recyclerView = this.binding.invoicesRecyclerView;
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerHomeView = new RecyclerHomeView(this, invoice, helper.getCurrentUserType(this));
        recyclerView.setAdapter(recyclerHomeView);
    }

    private void setWelcomeMessage() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String title = "Hello, Genie!";

        if(timeOfDay > 0 && timeOfDay < 4){
            title = "Hello, Night Owl!";
            this.binding.sellerHomeTitle.setText(title);
        }if(timeOfDay >= 4 && timeOfDay < 12){
            title = "Good Morning, Genie!";
            this.binding.sellerHomeTitle.setText(title);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            title = "Good Afternoon, Genie!";
            this.binding.sellerHomeTitle.setText(title);
        }else if(timeOfDay >= 16 && timeOfDay < 24){
            title = "Good Evening, Genie!";
            this.binding.sellerHomeTitle.setText(title);
        }else{
            this.binding.sellerHomeTitle.setText(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.seller_bottom_home) {
            return true;
        } else if (itemId == R.id.seller_bottom_products) {
            Intent goToSellerProducts = new Intent(this, Products.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerProducts);
            finish();
            return true;
        } else if (itemId == R.id.seller_bottom_more) {
            Intent goToSellerMore = new Intent(this, SellerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToSellerMore);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v!=null) {
            int id = v.getId();
            if (id == R.id.create_order) {
                Intent insertIntent = new Intent(this, Cart_Aisle.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(insertIntent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!exitApp){
            helper.toastMsg(this, "click one more time to EXIT");
            exitApp = true;
        }else{
            super.onBackPressed();
        }
    }

    private void filterList(String searchText) {
        List<Invoice> searchList = new ArrayList<>();

        for(Invoice invoice : this.invoiceList){

            String order_id = invoice.getCustomer_name() + " on " + invoice.getOrder_date() + " at " + invoice.getOrder_time() + " in " + invoice.getCompany_name();
            if(order_id.toLowerCase().contains(searchText.toLowerCase())){
                searchList.add(invoice);
            }
        }
        if(recyclerHomeView != null){
            recyclerHomeView.filteredList(searchList);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s!=null){
            filterList(s.toString());
        }
    }
}