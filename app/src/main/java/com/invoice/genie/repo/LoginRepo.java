package com.invoice.genie.repo;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.user.LoginInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class LoginRepo {

    //helper class
    RecursiveMethods helper = new RecursiveMethods();
    private final String TAG = "Repo_Users";

    //firebase
    private final FirebaseFirestore db;
    private final String COLLECTION = "production";
    private final String DOCUMENT = "users";
    private final String SUB_COLLECTION = "info";

    //live data
    public MutableLiveData<List<LoginInfo>> repoAllUsersInfoMutableLiveData = new MutableLiveData<>();
    public List<LoginInfo> repoAllUserInfoList = new ArrayList<>();

    //constructor
    public LoginRepo() {
        this.db = FirebaseFirestore.getInstance();
    }

    //methods

    //create
    public void createUserInformation(LoginInfo userInfo){
        try{
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("email", userInfo.getEmail());
            newUser.put("first_name", userInfo.getFirst_name());
            newUser.put("last_name", userInfo.getLast_name());
            newUser.put("seller", userInfo.getSeller());
            newUser.put("company", userInfo.getCompany());
            newUser.put("image", userInfo.getImage());

            String docID = userInfo.getEmail().toLowerCase();

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(SUB_COLLECTION)
                    .document(docID)
                    .set(newUser)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logE(TAG, "Failed to create a document : "+e.getLocalizedMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            repoAllUserInfoList.add(userInfo);
                        }
                    });
            repoAllUsersInfoMutableLiveData.postValue(repoAllUserInfoList);
        } catch (Exception e){
            helper.logE(TAG, "Found error in createUserInformation : "+e.getLocalizedMessage());
        }
    }

    //update
    public void updateUserInformation(LoginInfo userInfo){
        try{
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("email", userInfo.getEmail());
            newUser.put("first_name", userInfo.getFirst_name());
            newUser.put("last_name", userInfo.getLast_name());
            newUser.put("seller", userInfo.getSeller());
            newUser.put("company", userInfo.getCompany());
            newUser.put("image", userInfo.getImage());

            String docID = userInfo.getEmail().toLowerCase();

            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(SUB_COLLECTION)
                    .document(docID)
                    .set(newUser)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helper.logE(TAG, "Failed to create a document : "+e.getLocalizedMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            helper.logD(TAG, "Successfully created a document !");
                        }
                    });
        } catch (Exception e){
            helper.logE(TAG, "Found error in updateUserInformation : "+e.getLocalizedMessage());
        }
    }

    //get
    public void getAllUsersInformation(){
        try{
            db.collection(COLLECTION)
                    .document(DOCUMENT)
                    .collection(SUB_COLLECTION)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                helper.logE(TAG, "Failed with error in getAllUsersInformation : "+error.getLocalizedMessage());
                                return;
                            }
                            if(Objects.requireNonNull(value).isEmpty()){
                                helper.logE(TAG, "No documents found in getAllUsersInformation !");
                            } else{
                                for(DocumentChange documentChange : value.getDocumentChanges()){
                                    LoginInfo userInfo = documentChange.getDocument().toObject(LoginInfo.class);
                                    repoAllUserInfoList.add(userInfo);
                                }
                            }
                        }
                    });
            repoAllUsersInfoMutableLiveData.postValue(repoAllUserInfoList);
        } catch (Exception e){
            helper.logE(TAG, "Found error in getAllUsersInformation : "+e.getLocalizedMessage());
        }
    }
}
