package com.sampleboard.view.digitalSignature;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.print.PrintHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sampleboard.R;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DashBoardActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Anuj Sharma on 3/29/2017.
 */

public class PrintBitmapFragment extends BaseFragment implements View.OnClickListener {
    private Toolbar mToolbar;
    private ImageView mImageView;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_print, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_print:
                Drawable drawable = mImageView.getDrawable();
                if(drawable != null){
                    print();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
//        bundle.
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_printbitmap,container,false);
        // retain this fragment
        setRetainInstance(true);
        initView();
        return rootView;
    }

    private void initView() {
        //set toolbar
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((DashBoardActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("Preview");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashBoardActivity)getActivity()).oneStepBack();
            }
        });
        mImageView = (ImageView)rootView.findViewById(R.id.print_image);

        //fetch image from device folder and place into imageview
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                getString(R.string.app_name));
        if(dir.isDirectory()){
            //check image
            File file = new File(dir, "signature.png");
            if(file.isFile()){
                Picasso.with(getActivity()).load("file://" + file.getAbsolutePath()).memoryPolicy(MemoryPolicy.NO_CACHE ).into(mImageView);
            }else{
                Utils.getInstance().showToast("File not found");
            }
        }
    }

    private void print() {
        // Get the print manager.
        PrintHelper printHelper = new PrintHelper(getActivity());

        // Set the desired scale mode.
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        // Get the bitmap for the ImageView's drawable.
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        // Print the bitmap.
        printHelper.printBitmap("Print Bitmap", bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
}
