package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.invoice.genie.databinding.ActivityCartPlaceHolderBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.model.cart.Items;
import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.viewmodels.CartViewModel;
import com.invoice.genie.viewmodels.InvoiceViewModel;
import com.invoice.genie.viewmodels.LoginViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Cart_PlaceHolder extends AppCompatActivity {

    ActivityCartPlaceHolderBinding binding;

    RecursiveMethods helper; private final String TAG = "Cart_PlaceHolder Class";

    Context context; Activity activity;

    String company_email, company_name, customer_name, customer_email, order_date, order_time; double order_total;

    Invoice placeHolderInvoice; InvoiceViewModel invoiceViewModel; CartViewModel cartViewModel;
    LoginViewModel loginViewModel; private List<LoginInfo> userInfoList = new ArrayList<>();

    List<Items> itemsList = new ArrayList<>();

    Handler handler = new Handler(); Runnable runnable; int delay = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartPlaceHolderBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        // to hide top status bar (system), use below line
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        helper = new RecursiveMethods(); Intent i = getIntent();
        context = this; activity = this;
        this.placeHolderInvoice = new Invoice();

        customer_name = i.getStringExtra("customer_name");
        customer_email = i.getStringExtra("customer_email");
        order_total = i.getDoubleExtra("order_total",0);
        itemsList = (List<Items>) i.getSerializableExtra("items_list");

        company_name = helper.getCompanyName(this);
        company_email = helper.getCurrentUser(this);

        this.loginViewModel = LoginViewModel.getInstance(this.getApplication());
        if(helper.getCurrentFirstName(this).equalsIgnoreCase("")){
            this.loginViewModel.getAllUsersInformation();
            this.userInfoList = this.loginViewModel.viewModelAllUserInfoList;
            this.loginViewModel.viewModelAllUsersInfoMutableLiveData.observe(this, new Observer<List<LoginInfo>>() {
                @Override
                public void onChanged(List<LoginInfo> userInfoList) {
                    for(LoginInfo singleUser : userInfoList){
                        if (singleUser.getEmail().equalsIgnoreCase(company_email)) {
                                assignValuesFromFirestore(singleUser);
                        }
                    }
                }
            });
        }

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());
        this.cartViewModel = CartViewModel.getInstance(getApplication());

        order_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        order_time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());

        createInvoiceObject();

        this.invoiceViewModel.addInvoice(placeHolderInvoice);
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                executeInLoop();
            }
        }, delay);

    }

    private void executeInLoop(){
        cartViewModel.deleteAllItems(company_email);
        helper.logD(TAG, "executeInLoop");
        String order_id = customer_name + " on " + order_date + " at " + order_time + " in " + company_name;
        this.invoiceViewModel.viewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
            @Override
            public void onChanged(List<Invoice> invoices) {
                for(Invoice invoice: invoices){
                    String id = invoice.getCustomer_name() + " on " + invoice.getOrder_date() + " at " + invoice.getOrder_time() + " in " + invoice.getCompany_name();
                    if(id.equalsIgnoreCase(order_id)){
                        invoiceViewModel.addBuyerInvoice(placeHolderInvoice);
                        Intent insertIntent = new Intent(context, Cart_OrderSuccess.class);
                        insertIntent.putExtra("order_id", order_id);
                        insertIntent.putExtra("complete_order", placeHolderInvoice);
                        startActivity(insertIntent);
                        finish();
                    }

                }
            }
        });
        handler.removeCallbacks(runnable);//stop loop
    }

    private void assignValuesFromFirestore(LoginInfo loginInfo){
        String first_name = loginInfo.getFirst_name();
        String last_name = loginInfo.getLast_name();
        String company_name = loginInfo.getCompany();
        int avatar_num = loginInfo.getImage();

        helper.setCompanyName(context, company_name);
        helper.setCurrentFirstName(context, first_name);
        helper.setCurrentLastName(context, last_name);
        helper.setCurrentUserAvatar(context,avatar_num);
    }

    private void createInvoiceObject(){
        helper.logD(TAG, "createInvoiceObject");
        placeHolderInvoice.setCustomer_name(customer_name); //from intent
        placeHolderInvoice.setCustomer_email(customer_email); //from intent
        placeHolderInvoice.setCompany_name(company_name); //from shared pref
        placeHolderInvoice.setCompany_email(company_email); //from shared pref
        placeHolderInvoice.setOrder_date(order_date); //from code
        placeHolderInvoice.setOrder_time(order_time); //from code
        placeHolderInvoice.setOrder_total(order_total); //from intent
        placeHolderInvoice.setItems(itemsList); //from intent
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}