package com.invoice.genie.viewmodels;

import android.app.Application;

import com.invoice.genie.model.user.LoginInfo;
import com.invoice.genie.repo.LoginRepo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

    private final LoginRepo dbRepo = new LoginRepo();

    //live data
    public MutableLiveData<List<LoginInfo>> viewModelAllUsersInfoMutableLiveData = new MutableLiveData<>();
    public List<LoginInfo> viewModelAllUserInfoList = new ArrayList<>();

    //instance
    private static LoginViewModel instance;
    public static LoginViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new LoginViewModel(application);
        }
        return instance;
    }

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.dbRepo.getAllUsersInformation();
        this.viewModelAllUsersInfoMutableLiveData = this.dbRepo.repoAllUsersInfoMutableLiveData;
    }

    //methods

    //create
    public void createUserInformation(LoginInfo userInfo){
        this.dbRepo.createUserInformation(userInfo);
    }

    //update
    public void updateUserInformation(LoginInfo userInfo){
        this.dbRepo.updateUserInformation(userInfo);
    }

    //get
    public void getAllUsersInformation(){
        this.dbRepo.getAllUsersInformation();
        this.viewModelAllUsersInfoMutableLiveData = this.dbRepo.repoAllUsersInfoMutableLiveData;
        this.viewModelAllUserInfoList = this.viewModelAllUsersInfoMutableLiveData.getValue();
    }
}
