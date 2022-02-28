package com.invoice.genie.viewmodels;

import android.app.Application;

import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.aisle.InventoryWithoutURL;
import com.invoice.genie.repo.InventoryRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class InventoryViewModel extends AndroidViewModel {
    //Database Variables
    private final InventoryRepo db = new InventoryRepo();
    //Live Data
    public MutableLiveData<List<Inventory>> inventoryViewModelProductsMutableLiveData;

    //Instance Variable
    private static InventoryViewModel instance;
    //Instance Method
    public static InventoryViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new InventoryViewModel(application);
        }
        return instance;
    }

    //Constructor which is created automatically because of extending AndroidViewModel to the current class
    public InventoryViewModel(@NonNull Application application) {
        super(application);
        this.inventoryViewModelProductsMutableLiveData = this.db.inventoryMutableLiveData;
    }

    //Repository Methods
    public List<Inventory> getAllProducts(String email) {
        return this.db.getAllProducts(email);
    }
    //InventoryWithoutURL + uploadByte will give Uri = Inventory
    public void addNewProduct(InventoryWithoutURL product, String email, byte[] uploadByte) {
        this.db.addNewProduct(product, email, uploadByte);
    }
    //InventoryWithoutURL + uploadByte will give Uri = Inventory
    public void updateProduct(InventoryWithoutURL product, String email, byte[] uploadByte) {
        this.db.updateProduct(product, email, uploadByte);
    }

    public void deleteProduct(String inventory_name, String email) {
        this.db.deleteProduct(inventory_name, email);
    }
}
