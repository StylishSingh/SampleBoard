package com.sampleboard.view.activity;


import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.facebook.accountkit.AccountKit;
import com.sampleboard.R;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.SharedPreferencesHandler;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseActivity;
import com.sampleboard.view.musicModule.MusicPlayerActivity;
import com.sampleboard.view.otpLogin.LoginActivity;


public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private RelativeLayout btnEditProfile, containerFingerPrint, btnTerms, btnLogout;
    private Switch mTouchSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setTitle("Settings");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });

        btnEditProfile = (RelativeLayout) findViewById(R.id.btn_editProfile);
        containerFingerPrint = (RelativeLayout) findViewById(R.id.finger_print_container);
        mTouchSwitch = (Switch) findViewById(R.id.btn_switch);
        btnTerms = (RelativeLayout) findViewById(R.id.btn_terms);
        btnLogout = (RelativeLayout) findViewById(R.id.btn_logout);

        btnEditProfile.setOnClickListener(this);
        findViewById(R.id.btn_music).setOnClickListener(this);
        btnTerms.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        //First check FInger touch hardware is present or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isFingerPrintAvailable = false;
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
            } else {
                // Everything is ready for fingerprint authentication
                isFingerPrintAvailable = true;
            }
            if (isFingerPrintAvailable) {
                containerFingerPrint.setVisibility(View.VISIBLE);
            } else {
                containerFingerPrint.setVisibility(View.GONE);
            }
        } else {
            containerFingerPrint.setVisibility(View.GONE);
        }

        boolean isFingerTouchEnable = SharedPreferencesHandler.getBooleanValues(SettingsActivity.this, getString(R.string.pref_isFingertouchEnable));
        if (isFingerTouchEnable) {
            // Enable Touch Switch
            mTouchSwitch.setChecked(true);
        } else {
            //Disable Touch Switch
            mTouchSwitch.setChecked(false);
        }

        mTouchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesHandler.setBooleanValues(SettingsActivity.this, getString(R.string.pref_isFingertouchEnable), true);
                } else {
                    SharedPreferencesHandler.setBooleanValues(SettingsActivity.this, getString(R.string.pref_isFingertouchEnable), false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_editProfile:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(Constants.DESTINATION, Constants.EDIT_PROFILE_SCREEN);
                startActivity(intent);
                break;
            case R.id.btn_music:
                Intent musicIntent = new Intent(this, MusicPlayerActivity.class);
                startActivity(musicIntent);
                break;
            case R.id.btn_terms:
                break;
            case R.id.btn_logout:
                Utils.getInstance().confirmDialog(this, "Do you want to Logout ?", "logout", new Utils.ConfirmDialogCallbackInterface() {
                    @Override
                    public void onYesClick(String tag) {
                        AccountKit.logOut();
                        SharedPreferencesHandler.clearAll(SettingsActivity.this);
                        //Move to Login Screen
                        Intent logoutintent = new Intent(SettingsActivity.this, LoginActivity.class);
                        logoutintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logoutintent);
                    }

                    @Override
                    public void onNoClick(String tag) {

                    }
                });

                break;
        }
    }

    @Override
    public void onBackPressed() {
        oneStepBack();
    }
}
