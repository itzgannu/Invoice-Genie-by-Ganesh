package com.invoice.genie.repo;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class ItemsRepo {

    //helper class
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "Repository Of ItemsRepo";

    //live data
    public MutableLiveData<List<Items>> itemRepoLiveData = new MutableLiveData<>();

    //List
    List<Items> itemsList = new ArrayList<>();

    //firebase
    private final FirebaseFirestore db;

    private final String COLLECTION = "production";
    private final String DOCUMENT = "checkout";

    public ItemsRepo() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void addItem(Items item, String email) {
        try {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("product_name", item.getProduct_name());
            newItem.put("unit_type", item.getUnit_type());
            newItem.put("url", item.getUrl());
            newItem.put("product_quantity", item.getProduct_quantity());
            newItem.put("product_price", item.getProduct_price());
            newItem.put("product_total_price", item.getProduct_total_price());

            String FINAL_DOCUMENT = item.getProduct_name();

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .document(FINAL_DOCUMENT)
                    .set(newItem)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logE(TAG, "Failed to update cart Items");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Successfully updated cart Items");
                            itemsList.add(item);
                        }
                    });
            itemRepoLiveData.postValue(itemsList);
        } catch (Exception e) {
            helper.logE(TAG, e.getLocalizedMessage());
        }
    }

    public void removeItem(Items item, String email) {
        try {
            String FINAL_DOCUMENT = item.getProduct_name();

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .document(FINAL_DOCUMENT)
                    .delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logE(TAG, "Failed to delete Item");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Successfully deleted Item");
                            for (int i = 0; i < itemsList.size(); i++) {
                                if(item.getProduct_name().equalsIgnoreCase(itemsList.get(i).getProduct_name())){
                                    itemsList.remove(itemsList.get(i));
                                    break;
                                }
                            }
                        }
                    });
            itemRepoLiveData.postValue(itemsList);
        } catch (Exception e) {
            helper.logE(TAG, e.getLocalizedMessage());
        }
    }

    public void updateItem(Items item, String email) {
        try {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("product_name", item.getProduct_name());
            newItem.put("unit_type", item.getUnit_type());
            newItem.put("url", item.getUrl());
            newItem.put("product_quantity", item.getProduct_quantity());
            newItem.put("product_price", item.getProduct_price());
            newItem.put("product_total_price", item.getProduct_total_price());

            String FINAL_DOCUMENT = item.getProduct_name();

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .document(FINAL_DOCUMENT)
                    .set(newItem)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logE(TAG, "Failed to update Item");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Successfully updated Item");
                            for (int i = 0; i < itemsList.size(); i++) {
                                if(item.getProduct_name().equalsIgnoreCase(itemsList.get(i).getProduct_name())){
                                    itemsList.remove(itemsList.get(i));
                                    itemsList.add(item);
                                    break;
                                }
                            }
                        }
                    });
            itemRepoLiveData.postValue(itemsList);
        } catch (Exception e) {
            helper.logE(TAG, e.getLocalizedMessage());
        }
    }

    public List<Items> getItems(String email) {
        try {
            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                helper.logD(TAG, "Failed in getItems function & error is - " + error.getLocalizedMessage());
                            }
                            if (Objects.requireNonNull(value).isEmpty()) {
                                helper.logE(TAG, "No documents found in collection -"+COLLECTION);
                            } else {
                                itemsList.clear();
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    Items items = documentChange.getDocument().toObject(Items.class);
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            itemsList.add(items);
                                            break;

                                        case MODIFIED:
                                            if (items.getProduct_name().equalsIgnoreCase(documentChange.getDocument().getId())) {
                                                for (int i = 0; i < itemsList.size(); i++) {
                                                    if (items.getProduct_name().equalsIgnoreCase(itemsList.get(i).getProduct_name())) {
                                                        itemsList.set(i, items);
                                                    }
                                                }
                                            }
                                            break;

                                        case REMOVED:
                                            itemsList.remove(items);
                                            break;
                                    }
                                }
                            }
                            itemRepoLiveData.postValue(itemsList);
                        }
                    });
            return itemsList;
        } catch (Exception e) {
            helper.logE(TAG, e.getLocalizedMessage());
            return null;
        }
    }

    public void deleteAllItems(String email){
        try{
            itemsList.clear();
            itemRepoLiveData.postValue(itemsList);
            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                    Items items = documentChange.getDocument().toObject(Items.class);
                                    String doc_id = items.getProduct_name();
                                    db.collection(COLLECTION)
                                            .document(DOCUMENT)
                                            .collection(email)
                                            .document(doc_id)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //itemsList.remove(items);
                                                    helper.logD(TAG, "Found record o& Deleted record. Document Id is - " + doc_id);
                                                }
                                            });
                                }
                            }

                    });

        }catch (Exception e){
            helper.logD(TAG, e.getLocalizedMessage());
        }
    }


}
