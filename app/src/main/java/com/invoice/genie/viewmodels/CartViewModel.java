package com.invoice.genie.viewmodels;

import android.app.Application;

import com.invoice.genie.model.cart.Items;
import com.invoice.genie.repo.ItemsRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class CartViewModel extends AndroidViewModel {
    //Database instance
    private final ItemsRepo db = new ItemsRepo();

    //live data
    public MutableLiveData<List<Items>> itemsLiveData;
    public List<Items> itemsList;

    //Instance Variable
    private static CartViewModel instance;

    //Instance Method
    public static CartViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new CartViewModel(application);
        }
        return instance;
    }

    //Constructor which is created automatically because of extending AndroidViewModel to the current class
    public CartViewModel(@NonNull Application application) {
        super(application);
        this.itemsLiveData = this.db.itemRepoLiveData;
    }

    public void addItem(Items item, String email){
        this.db.addItem(item, email);
    }

    public void deleteItem(Items item, String email){
        this.db.removeItem(item,email);
    }

    public void updateItem(Items item, String email){
        this.db.updateItem(item,email);
    }

    public List<Items> getItems(String email){
        this.db.getItems(email);
        this.itemsLiveData = this.db.itemRepoLiveData;
        this.itemsList = this.itemsLiveData.getValue();
        return this.db.getItems(email);
    }

    public void deleteAllItems(String email){
        this.db.deleteAllItems(email);
    }
}