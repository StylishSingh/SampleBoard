package com.sampleboard.view.otpLogin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.sampleboard.view.BaseActivity;
import com.sampleboard.R;
import com.sampleboard.utils.SharedPreferencesHandler;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.activity.DashBoardActivity;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Anuj Sharma on 4/4/2017.
 */

public class LoginActivity extends BaseActivity {
    RelativeLayout mContainer;
//    UIManager uiManager;    //for  Account kit skin

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }
    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.sampleboard", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("", "printHashKey()", e);
        }
    }

    private void initViews() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
            Utils.getInstance().showToast("User is already logged in");
            getAccount();
        } else {
            //Handle new or logged out user
        }

        //set skin
//        uiManager = new SkinManager(LoginType.PHONE,
//                SkinManager.Skin.CONTEMPORARY,
//                R.color.colorPrimary,
//                R.drawable.app_background,
//                SkinManager.Tint.WHITE,
//               0.35d);

        mContainer = (RelativeLayout)findViewById(R.id.login_container);

        Button mLoginBtn = (Button)findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneLogin(mContainer);
            }
        });
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin(final View view) {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        configurationBuilder.setDefaultCountryCode("91");
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setFacebookNotificationsEnabled(true);
        configurationBuilder.setReceiveSMS(true);
//        String whiteListCountries[] = {"IN"};
//        configurationBuilder.setSMSWhitelist(whiteListCountries);

//        configurationBuilder.setUIManager(uiManager);
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                Utils.getInstance().showToast(toastMessage);
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                Utils.getInstance().showToast(toastMessage);
            } else {
                if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                    getAccount();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                    System.out.println("AuthorizationCode-> " + loginResult.getAuthorizationCode());
                    Utils.getInstance().showToast(toastMessage);

                    //Store token to SharedPref
                    SharedPreferencesHandler.setStringValues(LoginActivity.this,getString(R.string.pref_user_id),"loggedin");
                    // Success! Start your next activity...
                    Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
//                    String phoneNumberString = AccountKit.getCurrentPhoneNumberLogInModel().getPhoneNumber().getPhoneNumber();
//                    String countryCode  = AccountKit.getCurrentPhoneNumberLogInModel().getPhoneNumber().getCountryCode();
//                    Utils.getInstance().showToast("Phone-> " + countryCode + " " + phoneNumberString);
                }
                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

            }
        }
    }

    /**
     * Gets current account from Facebook Account Kit which include user's phone number.
     */
    private void getAccount(){
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String phoneNumberString = phoneNumber.toString();
                String countryCode  = account.getPhoneNumber().getCountryCode();

                Utils.getInstance().showToast("Phone-> " + countryCode + " " + phoneNumberString);

                //Store token to SharedPref
                SharedPreferencesHandler.setStringValues(LoginActivity.this,getString(R.string.pref_user_id),"loggedin");
                // Success! Start your next activity...
                Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit",error.toString());
                // Handle Error
                Utils.getInstance().showToast(error.toString());
            }
        });
    }

}
