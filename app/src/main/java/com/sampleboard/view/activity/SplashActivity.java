package com.sampleboard.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sampleboard.R;
import com.sampleboard.utils.SharedPreferencesHandler;

/**
 * Created by Anuj Sharma on 4/5/2017.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = SharedPreferencesHandler.getStringValues(this, getString(R.string.pref_user_id));
        Intent intent = null;
        /*if(TextUtils.isEmpty(userID)){
            //navigate to Login Screen
            intent = new Intent(this,LoginActivity.class);
        }else{
            intent = new Intent(this,DashBoardActivity.class);
        }*/
        intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
        finish();
//        manageNumber();
//        getNumberFromMatrix();
    }

    private void manageNumber() {
        int[] number = {0, 4, 0, 0, 8, 6};
        int tempPosition;
        for (int i = 0; i < number.length; i++) {
            if (number[i] == 0) {
                if (i + 1 < number.length) {
                    for (int j = i + 1; j < number.length; j++) {
                        if (number[j] != 0) {
                            number[i] = number[j];
                            number[j] = 0;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < number.length; i++) {
            System.out.println("Output-> " + number[i]);
        }
    }

    private void getNumberFromMatrix() {
        int[][] matrix = new int[][]{
                {10, 20, 30, 40},
                {20, 35, 45, 50},
                {52, 55, 60, 65},
                {67, 75, 85, 95}
        };
        int numberNeedToFind = 75;
        int i = 0, j = 3;
        while (i < 4 && j >= 0) {
            if (matrix[i][j] == numberNeedToFind) {
                System.out.println("I found number " + numberNeedToFind + " on " + i + "," + j);
                return;
            }
            if (matrix[i][j] > numberNeedToFind) {
                j--;
            } else {
                i++;
            }

        }

    }
}
