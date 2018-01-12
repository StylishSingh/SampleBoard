package com.sampleboard.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.ImageView;

import com.sampleboard.R;
import com.sampleboard.enums.CurrentScreen;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.digitalSignature.DigitalSignatureFragment;
import com.sampleboard.view.digitalSignature.PrintBitmapFragment;
import com.sampleboard.view.fragment.dashboard.HomeFragment;
import com.sampleboard.view.fragment.detail.CommentFragment;
import com.sampleboard.view.fragment.profile.EditProfileFragment;
import com.sampleboard.view.fragment.detail.DetailFragment;
import com.sampleboard.view.fragment.dashboard.ProfileFragment;
import com.sampleboard.view.fragment.profile.ProfilePicFragment;
import com.sampleboard.view.fragment.SearchFragment;
import com.sampleboard.view.fragment.profile.UserProfileFragment;

public class BaseActivity extends AppCompatActivity {
    public void navigateTo(int container, Fragment fragment, boolean isBackStack) {
        try {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(container, fragment, fragment.getClass().getSimpleName());
            if (isBackStack)
                fts.addToBackStack(fragment.getClass().getSimpleName());
            fts.commit();

        } catch (Exception e) {
            Utils.e(e);
        }

    }

    public void navigateToWithBundle(int container, Fragment fragment, boolean isBackStack, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.replace(container, fragment, fragment.getClass().getSimpleName());
        if (isBackStack)
            fts.addToBackStack(fragment.getClass().getSimpleName());
        fts.commit();
    }

    public void navigateReplacingCurrentWithBundle(int container, Fragment currentFragment, Fragment fragmentToNavigate, Bundle bundle) {
        fragmentToNavigate.setArguments(bundle);
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        fts.replace(container, fragmentToNavigate);
        fts.addToBackStack(fragmentToNavigate.getClass().getSimpleName());
        fts.remove(currentFragment).commit();
    }

    public void addFragment(int container, Fragment fragment, boolean isBackStack) {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fts.add(container, fragment, fragment.getClass().getSimpleName());
        if (isBackStack)
            fts.addToBackStack(fragment.getClass().getSimpleName());
        fts.commit();
    }

    public void addFragmentWithBundle(int container, Fragment fragment, boolean isBackStack, Bundle bundle) {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fts.add(container, fragment, fragment.getClass().getSimpleName());
        if (isBackStack)
            fts.addToBackStack(fragment.getClass().getSimpleName());
        fts.commit();
    }

    public void oneStepBack() {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStackImmediate();
            fts.commit();
        } else {
            supportFinishAfterTransition();
        }
    }


    public void changeScreen(int container, CurrentScreen currentScreen, boolean isAddFragment, boolean isBackStack, Bundle bundle) {
        Fragment currentFragment = null;
        switch (currentScreen) {
            case ITEM_LIST_SCREEN:
                currentFragment = new HomeFragment();
                break;
            case SEARCH_SCREEN:
                currentFragment = new SearchFragment();
                break;
            case PROFILE_SCREEN:
                currentFragment = new ProfileFragment();
                break;
            case EDIT_PROFILE_SCREEN:
                currentFragment = new EditProfileFragment();
                break;
            case PROFILE_PIC_SCREEN:
                currentFragment = new ProfilePicFragment();
                break;
            case USER_PROFILE_SCREEN:
                currentFragment = new UserProfileFragment();
                break;
            case POST_DETAIL_SCREEN:
                currentFragment = new DetailFragment();
                currentFragment.setArguments(bundle);
                break;
            case CALANDER_SCREEN:
//                currentFragment = initializeCalanderFragment();
                break;
            case DIGITAL_SIGNATURE_SCREEN:
//                currentFragment = initializeDigitalSignatureFragment();
                currentFragment = new DigitalSignatureFragment();
                break;
            case PRINT_BITMAP_SCREEN:
//                currentFragment = initializePrintBitmapFragment();
                currentFragment = new PrintBitmapFragment();
                break;


        }
        if (currentFragment == null) {
            return;
        }
        //hide Keyboard
//        Utils.getInstance().hideSoftKeyboard(this, roo);

        if (isAddFragment) {
            //Add Fragment
            if (isBackStack)
                addFragmentWithBundle(container, currentFragment, true, bundle);
            else
                addFragmentWithBundle(container, currentFragment, false, bundle);
        } else {
            //Replace Fragment
            if (isBackStack)
                navigateToWithBundle(container, currentFragment, true, bundle);
            else
                navigateToWithBundle(container, currentFragment, false, bundle);
        }
    }

    public void slideInUpAnimation(int container, Fragment fragment, Bundle bundle) {
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fts.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
        fts.add(container, fragment, fragment.getClass().getSimpleName());
        fts.commit();
    }

    /*public void changeScreenWithAnimation(int container, CurrentScreen currentScreen, boolean isAddFragment,
                                          boolean isBackStack, Bundle bundle, int inAnimation, int outAnimation) {
        Fragment currentFragment = null;
        switch (currentScreen) {
            case COMMENT_SCREEN:
                currentFragment = new CommentFragment();
                break;


        }
        if (currentFragment == null) {
            return;
        }
        if (isAddFragment) {
            //Add Fragment
            if (isBackStack)
                addFragmentWithBundle(container, currentFragment, true, bundle, inAnimation, outAnimation);
            else
                addFragmentWithBundle(container, currentFragment, false, bundle, inAnimation, outAnimation);
        } else {
            //Replace Fragment
            if (isBackStack)
                navigateToWithBundle(container, currentFragment, true, bundle, inAnimation, outAnimation);
            else
                navigateToWithBundle(container, currentFragment, false, bundle, inAnimation, outAnimation);
        }
    }*/

    public void performTransition(int fragment_container, Fragment fragment, ImageView imageView, Bundle bundle) {
        int MOVE_DEFAULT_TIME = 300;
        int FADE_DEFAULT_TIME = 300;
        if (isDestroyed()) {
            return;
        }
        Fragment previousFragment = getSupportFragmentManager().findFragmentById(fragment_container);
        Fragment nextFragment = fragment;
        nextFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 1. Exit for Previous Fragment
        Fade exitFade = new Fade();
        exitFade.setDuration(300);
        previousFragment.setExitTransition(exitFade);

        // 2. Shared Elements Transition
        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
        nextFragment.setSharedElementEnterTransition(enterTransitionSet);

        // 3. Enter Transition for New Fragment
        Fade enterFade = new Fade();
        enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        nextFragment.setEnterTransition(enterFade);

//        View logo = ButterKnife.findById(this, R.id.fragment1_logo);
        fragmentTransaction.addSharedElement(imageView, imageView.getTransitionName());
        fragmentTransaction.replace(fragment_container, nextFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
