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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.invoice.genie.adapter.RecyclerHomeView;
import com.invoice.genie.databinding.ActivityBuyerHomeBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.viewmodels.InvoiceViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuyerHome extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, TextWatcher {

    ActivityBuyerHomeBinding binding;
    BottomNavigationView bottomNavigationView;

    RecursiveMethods helper; String current_user;
    boolean exitApp = false;

    RecyclerView recyclerView; RecyclerHomeView recyclerHomeView;
    LinearLayoutManager linearLayoutManager;

    InvoiceViewModel invoiceViewModel;
    List<Invoice> invoiceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityBuyerHomeBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        setWelcomeMessage();

        exitApp = false;

        helper = new RecursiveMethods();
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        //bottom navigation related
        bottomNavigationView = this.binding.buyerBottom;
        bottomNavigationView.setSelectedItemId(R.id.buyer_home);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.invoiceViewModel.buyerViewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                initializeRecyclerView(invoices);
            }
        });
        this.invoiceList = this.invoiceViewModel.getBuyerInvoices(current_user);

        this.binding.invoiceSearchText.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.binding = ActivityBuyerHomeBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        setWelcomeMessage();

        exitApp = false;

        helper = new RecursiveMethods();
        helper.isCacheExists(this);
        current_user = helper.getCurrentUser(this);

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        //bottom navigation related
        bottomNavigationView = this.binding.buyerBottom;
        bottomNavigationView.setSelectedItemId(R.id.buyer_home);
        bottomNavigationView.setOnItemSelectedListener(this);

        this.invoiceViewModel.buyerViewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                initializeRecyclerView(invoices);
            }
        });
        this.invoiceList = this.invoiceViewModel.getBuyerInvoices(current_user);

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
        recyclerHomeView = new RecyclerHomeView(this, invoice,helper.getCurrentUserType(this));
        recyclerView.setAdapter(recyclerHomeView);
    }

    private void setWelcomeMessage() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String title = "Hello, Genie!";

        if(timeOfDay > 0 && timeOfDay < 4){
            title = "Hello, Night Owl!";
            this.binding.buyerHomeTitle.setText(title);
        }if(timeOfDay >= 4 && timeOfDay < 12){
            title = "Good Morning, Genie!";
            this.binding.buyerHomeTitle.setText(title);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            title = "Good Afternoon, Genie!";
            this.binding.buyerHomeTitle.setText(title);
        }else if(timeOfDay >= 16 && timeOfDay < 24){
            title = "Good Evening, Genie!";
            this.binding.buyerHomeTitle.setText(title);
        }else{
            this.binding.buyerHomeTitle.setText(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.buyer_home) {
            return true;
        } else if (itemId == R.id.buyer_more) {
            Intent goToBuyerMore = new Intent(this, BuyerMore.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(goToBuyerMore);
            finish();
            return true;
        }
        return false;
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