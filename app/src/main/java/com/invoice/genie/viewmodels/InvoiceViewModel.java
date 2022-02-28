package com.invoice.genie.viewmodels;

import android.app.Application;

import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.repo.InvoiceRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class InvoiceViewModel extends AndroidViewModel {
    //Database instance
    private final InvoiceRepo db = new InvoiceRepo();

    //Live Data
    public MutableLiveData<List<Invoice>> viewModelInvoicesLiveData = new MutableLiveData<>();
    public MutableLiveData<List<Invoice>> buyerViewModelInvoicesLiveData = new MutableLiveData<>();

    //Instance Variable
    private static InvoiceViewModel instance;

    //Instance Method
    public static InvoiceViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new InvoiceViewModel(application);
        }
        return instance;
    }

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        this.viewModelInvoicesLiveData = this.db.repoInvoiceListMutableLiveData;
        this.buyerViewModelInvoicesLiveData = this.db.buyerRepoInvoiceListMutableLiveData;
    }

    public void addInvoice(Invoice invoice){
        this.db.addInvoice(invoice);
    }

    public void addBuyerInvoice(Invoice invoice){
        this.db.addBuyerInvoice(invoice);
    }

    public void shareBuyerInvoice(Invoice invoice, String email){
        this.db.shareBuyerInvoice(invoice,email);
    }

    public List<Invoice> getInvoices(String email){
        return this.db.getSellerInvoices(email);
    }

    public List<Invoice> getBuyerInvoices(String email){
       return this.db.getBuyerInvoices(email);
    }
}
