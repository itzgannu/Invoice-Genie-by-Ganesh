package com.invoice.genie.repo;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.aisle.Inventory;
import com.invoice.genie.model.aisle.InventoryWithoutURL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class InventoryRepo {

    //helper class
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "Repository Of InventoryRepo";

    //firebase
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private final String COLLECTION = "production";
    private final String DOCUMENT = "products";

    //live data
    public MutableLiveData<List<Inventory>> inventoryMutableLiveData = new MutableLiveData<>();
    private List<Inventory> inventoryList = new ArrayList<>();

    //constructor
    public InventoryRepo() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Fetch All Products
     */
    public List<Inventory> getAllProducts(String email) {
        try {
            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                helper.logD(TAG, "Failed in getAllProducts function & error is - " + error.getLocalizedMessage());
                            }

                            if (Objects.requireNonNull(value).isEmpty()) {
                                helper.logE(TAG, "No documents found in collection -"+COLLECTION);
                            } else {
                                inventoryList.clear();
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    Inventory inventory = documentChange.getDocument().toObject(Inventory.class);
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            inventoryList.add(inventory);
                                            break;

                                        case MODIFIED:
                                            if (inventory.getName().equalsIgnoreCase(documentChange.getDocument().getId())) {
                                                for (int i = 0; i < inventoryList.size(); i++) {
                                                    if (inventory.getName().equalsIgnoreCase(inventoryList.get(i).getName())) {
                                                        inventoryList.set(i, inventory);
                                                    }
                                                }
                                            }
                                            break;

                                        case REMOVED:
                                            inventoryList.remove(inventory);
                                            break;
                                    }
                                }
                            }
                            inventoryMutableLiveData.postValue(inventoryList);
                        }
                    });
            return inventoryList;
        } catch (Exception e) {
            helper.logE(TAG, "Got an exception inside getAllProducts function & error is - " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Add New InventoryWithoutURL
     */
    public void addNewProduct(InventoryWithoutURL product, String email, byte[] uploadByte) {
        try {
            /*
            For Storage - Uploading picture to Firebase
             */
            StorageReference storageRef = storage.getReference();
            String directory = COLLECTION + "/" + email;
            String file_name = product.getName();
            StorageReference imagesRef = storageRef.child(directory).child(file_name);

            UploadTask uploadTask = imagesRef.putBytes(uploadByte);
            uploadTask
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'
                                    helper.logD(TAG, uri.toString());
                                    Map<String, Object> newProduct = new HashMap<>();
                                    newProduct.put("description", product.getDescription());
                                    newProduct.put("name", product.getName());
                                    newProduct.put("quantity", product.getQuantity());
                                    newProduct.put("price", product.getPrice());
                                    newProduct.put("pack", product.getPack());
                                    newProduct.put("url", uri.toString());

                                    String name = product.getName();

                                    Inventory inventory = new Inventory(name, product.getDescription(), product.getPack(), product.getPrice(), product.getQuantity(), uri.toString());

                                    db.collection(COLLECTION)
                                            .document(DOCUMENT)
                                            .collection(email)
                                            .document(name)
                                            .set(newProduct)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    helper.logD(TAG, "Inventory added Successfully");
                                                    inventoryList.add(inventory);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    helper.logD(TAG, "Couldn't add Inventory");
                                                }
                                            });
                                    inventoryMutableLiveData.postValue(inventoryList);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    helper.logD(TAG, "Failed to get downloadable URI");
                                }
                            });
                        }
                    });
        } catch (Exception exception) {
            helper.logE(TAG, "Got an exception inside addNewProduct function & error is - " + exception.getLocalizedMessage());
        }
    }

    /**
     * Update InventoryWithoutURL
     */
    public void updateProduct(InventoryWithoutURL product, String email, byte[] uploadByte) {
        try {
            /*
            For Storage - Update picture to Firebase
             */
            StorageReference storageRef = storage.getReference();
            String directory = COLLECTION + "/" + email;
            String file_name = product.getName();
            StorageReference imagesRef = storageRef.child(directory).child(file_name);

            UploadTask uploadTask = imagesRef.putBytes(uploadByte);
            uploadTask
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'
                                    helper.logD(TAG, uri.toString());
                                    Map<String, Object> newProduct = new HashMap<>();
                                    newProduct.put("description", product.getDescription());
                                    newProduct.put("name", product.getName());
                                    newProduct.put("quantity", product.getQuantity());
                                    newProduct.put("price", product.getPrice());
                                    newProduct.put("pack", product.getPack());
                                    newProduct.put("url", uri.toString());

                                    String name = product.getName();

                                    Inventory inventory = new Inventory(name, product.getDescription(), product.getPack(), product.getPrice(), product.getQuantity(), uri.toString());

                                    db.collection(COLLECTION)
                                            .document(DOCUMENT)
                                            .collection(email)
                                            .document(name)
                                            .update(newProduct)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    helper.logD(TAG, "Inventory Updated Successfully");
                                                    for (int i = 0; i < inventoryList.size(); i++) {
                                                        if (inventoryList.get(i).getName().equalsIgnoreCase(name)) {
                                                            inventoryList.remove(inventoryList.get(i));
                                                            inventoryList.add(inventory);
                                                            break;
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    helper.logD(TAG, "Couldn't add Inventory");
                                                }
                                            });
                                    inventoryMutableLiveData.postValue(inventoryList);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    helper.logD(TAG, "Failed to fetch downloadable URI");
                                }
                            });
                        }
                    });
        } catch (Exception exception) {
            helper.logE(TAG, "Got an exception inside updateProduct function & error is - " + exception.getLocalizedMessage());
        }
    }

    /**
     * Delete InventoryWithoutURL
     */
    public void deleteProduct(String inventory_name, String email) {
        try {
            /*
            Delete image from Storage
             */
            StorageReference storageRef = storage.getReference();
            String directory = COLLECTION + "/" + email;
            StorageReference desertRef = storageRef.child(directory).child(inventory_name);

            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    db.collection(COLLECTION)
                            .document(DOCUMENT)
                            .collection(email)
                            .document(inventory_name)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    helper.logD(TAG, "Inventory Deleted Successfully");
                                    for (Inventory inventory : inventoryList) {
                                        if (inventory.getName().equalsIgnoreCase(inventory_name)) {
                                            inventoryList.remove(inventory);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    helper.logD(TAG, "Couldn't Delete Inventory");
                                }
                            });
                    inventoryMutableLiveData.postValue(inventoryList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        } catch (Exception exception) {
            helper.logE(TAG, "Got an exception inside deleteProduct function & error is - " + exception.getLocalizedMessage());
        }
    }
}
