package com.invoice.genie.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.invoice.genie.SignIn;

public class RecursiveMethods {

    //Shared Preferences
    SharedPreferences sharedPreferences;
    static final String preferenceName = "invoiceGenie";
    static final String currentUserEmail = "userEmail";
    static final String currentUserType = "userType";
    static final String currentCompanyName = "companyName";
    static final String currentFirstName = "firstName";
    static final String currentLastName = "lastName";
    static final String currentAvatar = "userAvatar";

    final String TAG = "RecursiveMethods";

    public void setCurrentUser(Context context, String email) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentUserEmail, email);
        editor.commit();
    }

    public String getCurrentUser(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentUserEmail)) {
            return sharedPreferences.getString(currentUserEmail, "");
        } else {
            return "";
        }
    }

    public void setCurrentUserType(Context context, String userType) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentUserType, userType);
        editor.commit();
    }

    public String getCurrentUserType(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentUserType)) {
            return sharedPreferences.getString(currentUserType, "");
        } else {
            return "";
        }
    }

    public void setCompanyName(Context context, String userType) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentCompanyName, userType);
        editor.commit();
    }

    public String getCompanyName(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentCompanyName)) {
            return sharedPreferences.getString(currentCompanyName, "");
        } else {
            return "";
        }
    }

    public void setCurrentFirstName(Context context, String userType) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentFirstName, userType);
        editor.commit();
    }

    public String getCurrentFirstName(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentFirstName)) {
            return sharedPreferences.getString(currentFirstName, "");
        } else {
            return "";
        }
    }

    public void setCurrentLastName(Context context, String userType) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currentLastName, userType);
        editor.commit();
    }

    public String getCurrentLastName(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentLastName)) {
            return sharedPreferences.getString(currentLastName, "");
        } else {
            return "";
        }
    }

    public void setCurrentUserAvatar(Context context, int avatar) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(currentAvatar, avatar);
        editor.commit();
    }

    public int getCurrentUserAvatar(Context context) {
        sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(currentAvatar)) {
            return sharedPreferences.getInt(currentAvatar, 1);
        } else {
            return 1;
        }
    }

    public void isCacheExists(Context context) {
        if (getCurrentUser(context).equalsIgnoreCase("") || getCurrentUser(context).isEmpty() || getCurrentUser(context) == null) {
            //No User Logged in (or) First Login (or) User Logged Out Scenarios
            logD(TAG, "No User Logged in (or) First Login (or) User Logged Out Scenarios");
            Intent goToLoginScreen = new Intent(context, SignIn.class);
            logD(TAG, "Current User & User Type is set to ''");
            setCurrentUserType(context, "");
            setCurrentUser(context, "");
            setCompanyName(context,"");
            setCurrentFirstName(context,"");
            setCurrentLastName(context,"");
            setCurrentUserAvatar(context,0);
            logD(TAG, "starting Activity - Sign In");
            context.startActivity(goToLoginScreen);
        } else if (getCurrentUserType(context).equalsIgnoreCase("") || getCurrentUserType(context).isEmpty() || getCurrentUserType(context) == null) {
            //Logged In user have no user type
            logD(TAG, "Logged in User have no User Type");
            Intent goToLoginScreen = new Intent(context, SignIn.class);
            logD(TAG, "Current User & User Type is set to ''");
            setCurrentUserType(context, "");
            setCurrentUser(context, "");
            setCompanyName(context,"");
            setCurrentFirstName(context,"");
            setCurrentLastName(context,"");
            setCurrentUserAvatar(context,0);
            logD(TAG, "starting Activity - Sign In");
            context.startActivity(goToLoginScreen);
        }
    }

    public void logD(String className, String message) {
        Log.d(className, message);
    }

    public void logE(String className, String message) {
        Log.e(className, message);
    }

    public void toastMsg(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void signOut(Context context, Activity activity) {
        Intent insertIntent1 = new Intent(context, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(insertIntent1);
        setCurrentUser(context, "");
        setCurrentFirstName(context,"");
        setCurrentLastName(context,"");
        setCurrentUserAvatar(context,1);
        setCompanyName(context,"");
        setCurrentUserType(context, "");
        activity.finish();
    }
}
