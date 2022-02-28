package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import com.invoice.genie.databinding.ActivityAddInvoiceBinding;
import com.invoice.genie.helpers.JavaMailAPI;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.viewmodels.InvoiceViewModel;

import java.util.List;
import java.util.Objects;

public class AddInvoice extends AppCompatActivity implements View.OnClickListener {

    ActivityAddInvoiceBinding binding;
    RecursiveMethods helper; Context context;

    InvoiceViewModel invoiceViewModel; Invoice placeHolderInvoice; String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAddInvoiceBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); Intent i = getIntent(); context = this;

        order_id = i.getStringExtra("order_id");
        placeHolderInvoice = (Invoice) i.getSerializableExtra("share_invoice");

        this.invoiceViewModel = InvoiceViewModel.getInstance(getApplication());

        this.binding.addInvoiceBack.setOnClickListener(this);
        this.binding.addInvoiceShareButton.setOnClickListener(this);
    }

    private void sendEmail(String userEmail){
        JavaMailAPI javaMailAPI = new JavaMailAPI(placeHolderInvoice, order_id, true, userEmail);
        javaMailAPI.execute() ;
    }

    @Override
    public void onClick(View v) {
        if(v!=null){
            int id = v.getId();
            if (id == R.id.add_invoice_back) {
                finish(); //no back trace
            } else if (id == R.id.add_invoice_share_button) {
                String email = Objects.requireNonNull(this.binding.addInvoiceTextField.getEditText()).getText().toString().toLowerCase();
                if(email.isEmpty()){
                    this.binding.addInvoiceTextField.setError("Field is empty");
                    return;
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    this.binding.addInvoiceTextField.setError("Enter valid Email ID");
                    return;
                } else{
                    this.binding.addInvoiceTextField.setError(null);
                }
                this.invoiceViewModel.shareBuyerInvoice(placeHolderInvoice, email);
                this.binding.addInvoiceTextField.getEditText().getText().clear();
                sendEmail(email);
                this.invoiceViewModel.getBuyerInvoices(email);
                this.invoiceViewModel.buyerViewModelInvoicesLiveData.observe(this, new Observer<List<Invoice>>() {
                    @Override
                    public void onChanged(List<Invoice> invoices) {
                        for(Invoice invoice:invoices){
                            String dbOrder = invoice.getCustomer_name() + " on " + invoice.getOrder_date() + " at " + invoice.getOrder_time() + " in " + invoice.getCompany_name();
                            if(order_id.equalsIgnoreCase(dbOrder)){
                                helper.toastMsg(context,"Invoice Shared");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}