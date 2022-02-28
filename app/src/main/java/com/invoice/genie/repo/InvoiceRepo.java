package com.invoice.genie.repo;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Invoice;
import com.invoice.genie.model.cart.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class InvoiceRepo {

    //helper class
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "Repository Of ItemsRepo";

    public MutableLiveData<List<Invoice>> repoInvoiceListMutableLiveData = new MutableLiveData<>();
    List<Invoice> invoices = new ArrayList<>();

    public MutableLiveData<List<Invoice>> buyerRepoInvoiceListMutableLiveData = new MutableLiveData<>();
    List<Invoice> buyerInvoices = new ArrayList<>();


    //firebase
    private final FirebaseFirestore db;

    private final String COLLECTION = "production";
    private final String DOCUMENT = "seller copy";
    private final String BUYER_DOCUMENT = "buyer copy";

    public InvoiceRepo() {
        this.db = FirebaseFirestore.getInstance();
    }

    public List<Invoice> getSellerInvoices(String email) {
        try {
            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(email)
                    .orderBy("order_date", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                helper.logD(TAG, "Failed in getInvoices function & error is - " + error.getLocalizedMessage());
                            }
                            if (Objects.requireNonNull(value).isEmpty()) {
                                helper.logE(TAG, "No documents found in collection -" + COLLECTION);
                            } else {
                                invoices.clear();
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    Invoice invoice = documentChange.getDocument().toObject(Invoice.class);
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            invoices.add(invoice);
                                            break;

                                        case MODIFIED:
                                            break;

                                        case REMOVED:
                                            invoices.remove(invoice);
                                            break;
                                    }
                                }
                            }
                            repoInvoiceListMutableLiveData.postValue(invoices);
                        }
                    });
            return invoices;
        } catch (Exception e) {
            helper.logD(TAG, "Caught exception in getInvoices method inside InvoiceRepo & it is - " + e.getLocalizedMessage());
            return null;
        }
    }

    public void addInvoice(Invoice invoice) {
        try {
            String customer_name, customer_email, company_name, company_email, order_date, order_time;
            double order_total;
            List<Items> items;

            customer_name = invoice.getCustomer_name();
            customer_email = invoice.getCustomer_email().toLowerCase();
            company_name = invoice.getCompany_name();
            company_email = invoice.getCompany_email().toLowerCase();
            order_date = invoice.getOrder_date();
            order_time = invoice.getOrder_time();
            order_total = invoice.getOrder_total();
            items = invoice.getItems();

            String order_id = customer_name + " on " + order_date + " at " + order_time + " in " + company_name;

            Map<String, Object> newInvoice = new HashMap<>();
            newInvoice.put("customer_name", customer_name);
            newInvoice.put("customer_email", customer_email);
            newInvoice.put("company_name", company_name);
            newInvoice.put("company_email", company_email);
            newInvoice.put("order_date", order_date);
            newInvoice.put("order_time", order_time);
            newInvoice.put("order_total", order_total);
            newInvoice.put("items", items);

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(company_email)
                    .document(order_id)
                    .set(newInvoice)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logD(TAG, "Got error while adding invoice - "+e.getLocalizedMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Got any error while adding invoice? - no error");
                            invoices.add(invoice);
                        }
                    });
            repoInvoiceListMutableLiveData.postValue(invoices);
        } catch (Exception e) {
            helper.logD(TAG, "Caught exception in repo of Invoice - " + e.getLocalizedMessage());
        }
    }

    public List<Invoice> getBuyerInvoices(String email){
        try {
            db.collection(COLLECTION)
                    .document(BUYER_DOCUMENT)
                    .collection(email)
                    .orderBy("order_date", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                helper.logD(TAG, "Failed in getInvoices function & error is - " + error.getLocalizedMessage());
                            }
                            if (Objects.requireNonNull(value).isEmpty()) {
                                helper.logE(TAG, "No documents found in collection -" + COLLECTION);
                            } else {
                                buyerInvoices.clear();
                                for (DocumentChange documentChange : value.getDocumentChanges()) {
                                    Invoice invoice = documentChange.getDocument().toObject(Invoice.class);
                                    switch (documentChange.getType()) {
                                        case ADDED:
                                            buyerInvoices.add(invoice);
                                            break;

                                        case MODIFIED:
                                            break;

                                        case REMOVED:
                                            buyerInvoices.remove(invoice);
                                            break;
                                    }
                                }
                            }
                            buyerRepoInvoiceListMutableLiveData.postValue(buyerInvoices);
                        }
                    });
            return buyerInvoices;
        } catch (Exception e) {
            helper.logD(TAG, "Caught exception in getInvoices method inside InvoiceRepo & it is - " + e.getLocalizedMessage());
            return null;
        }
    }

    public void addBuyerInvoice(Invoice invoice){
        try {
            String customer_name, customer_email, company_name, company_email, order_date, order_time;
            double order_total;
            List<Items> items;

            customer_name = invoice.getCustomer_name();
            customer_email = invoice.getCustomer_email().toLowerCase();
            company_name = invoice.getCompany_name();
            company_email = invoice.getCompany_email().toLowerCase();
            order_date = invoice.getOrder_date();
            order_time = invoice.getOrder_time();
            order_total = invoice.getOrder_total();
            items = invoice.getItems();

            String order_id = customer_name + " on " + order_date + " at " + order_time + " in " + company_name;

            Map<String, Object> newInvoice = new HashMap<>();
            newInvoice.put("customer_name", customer_name);
            newInvoice.put("customer_email", customer_email);
            newInvoice.put("company_name", company_name);
            newInvoice.put("company_email", company_email);
            newInvoice.put("order_date", order_date);
            newInvoice.put("order_time", order_time);
            newInvoice.put("order_total", order_total);
            newInvoice.put("items", items);

            db.collection(COLLECTION)
                    .document(BUYER_DOCUMENT)
                    .collection(customer_email)
                    .document(order_id)
                    .set(newInvoice)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logD(TAG, "Got error while adding invoice - "+e.getLocalizedMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Got any error while adding invoice? - no error");
                            buyerInvoices.add(invoice);
                        }
                    });
            buyerRepoInvoiceListMutableLiveData.postValue(buyerInvoices);
        } catch (Exception e) {
            helper.logD(TAG, "Caught exception in repo of Invoice - " + e.getLocalizedMessage());
        }
    }

    public void shareBuyerInvoice(Invoice invoice, String email){
        try {
            String customer_name, customer_email, company_name, company_email, order_date, order_time;
            double order_total;
            List<Items> items;

            customer_name = invoice.getCustomer_name();
            customer_email = invoice.getCustomer_email().toLowerCase();
            company_name = invoice.getCompany_name();
            company_email = invoice.getCompany_email().toLowerCase();
            order_date = invoice.getOrder_date();
            order_time = invoice.getOrder_time();
            order_total = invoice.getOrder_total();
            items = invoice.getItems();

            String order_id = customer_name + " on " + order_date + " at " + order_time + " in " + company_name;

            Map<String, Object> newInvoice = new HashMap<>();
            newInvoice.put("customer_name", customer_name);
            newInvoice.put("customer_email", customer_email);
            newInvoice.put("company_name", company_name);
            newInvoice.put("company_email", company_email);
            newInvoice.put("order_date", order_date);
            newInvoice.put("order_time", order_time);
            newInvoice.put("order_total", order_total);
            newInvoice.put("items", items);

            db.collection(COLLECTION)
                    .document(BUYER_DOCUMENT)
                    .collection(email)
                    .document(order_id)
                    .set(newInvoice)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logD(TAG, "Got error while adding invoice - "+e.getLocalizedMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Got any error while adding invoice? - no error");
                        }
                    });
        } catch (Exception e) {
            helper.logD(TAG, "Caught exception in repo of Invoice - " + e.getLocalizedMessage());
        }
    }
}
