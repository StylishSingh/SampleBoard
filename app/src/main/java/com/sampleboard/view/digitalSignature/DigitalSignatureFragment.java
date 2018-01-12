package com.sampleboard.view.digitalSignature;

import android.content.Context;
import android.content.pm.PackageManager;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sampleboard.R;
import com.sampleboard.permission.PermissionsAndroid;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DashBoardActivity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Anuj Sharma on 3/28/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DigitalSignatureFragment extends BaseFragment {
    private View rootView;
    private Toolbar mToolbar;
    private GestureOverlayView gestureView;
    //    String path;
    File signatureFile;
    Bitmap bitmap;
    public boolean gestureTouch = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_digital_signature, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(getActivity())) {
                    saveSignature();
                } else {
                    PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(DigitalSignatureFragment.this);
                }
                break;
            case R.id.action_clear:
                gestureTouch = false;
                gestureView.invalidate();
                gestureView.clear(true);
                gestureView.clearAnimation();
                gestureView.cancelClearAnimation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_digital_signature, container, false);
        // retain this fragment
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("orientation_change",true);
        Bundle bundle = new Bundle();
//        bundle.putBundle("gestureView",gestureView);
//        outState.putBundle("gestureView");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null){
            boolean orientation = savedInstanceState.getBoolean("orientation_change");
        }
        initViews();

    }

    private void initViews() {
        //initialize Toolbar
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((DashBoardActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle("E-Signature");

//        signatureFile = Utils.getInstance().saveToDirectory(getActivity(), "signature", "png");
//        if (signatureFile != null && signatureFile.isFile())
//                signatureFile.delete();

//        path= Environment.getExternalStorageDirectory()+"/signature.png";
//        file = new File(path);
//        file.delete();
        if(gestureView==null){
            gestureView = (GestureOverlayView) rootView.findViewById(R.id.signaturePad);
        }
        gestureView.setDrawingCacheEnabled(true);
        gestureView.setAlwaysDrawnWithCacheEnabled(true);
        gestureView.setHapticFeedbackEnabled(false);
        gestureView.cancelLongPress();
        gestureView.cancelClearAnimation();
        gestureView.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

            @Override
            public void onGesture(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureCancelled(GestureOverlayView arg0,
                                           MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureEnded(GestureOverlayView arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGestureStarted(GestureOverlayView arg0,
                                         MotionEvent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    gestureTouch = false;
                } else {
                    gestureTouch = true;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionsAndroid.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted,
                    saveSignature();

                } else {
                    // permission denied
                    Utils.getInstance().showToast("Storage permission required for this functionality.");
                }
                return;
            }
        }
    }

    private void saveSignature() {
        try {
            signatureFile = saveToDirectory(getActivity(), "signature", "png");
            if (signatureFile != null && signatureFile.isFile())
                signatureFile.delete();

            bitmap = Bitmap.createBitmap(gestureView.getDrawingCache());
//                    file.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(file);
//                    fos = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    fos.close();

            signatureFile.createNewFile();
            FileOutputStream fos1 = new FileOutputStream(signatureFile);
            fos1 = new FileOutputStream(signatureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos1);
            fos1.close();
            // compress to specified format (PNG), quality - which is
            // ignored for PNG, and out stream

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gestureTouch == false) {
            getActivity().setResult(0);
            Utils.getInstance().showToast("No Signature found");
        } else {
            getActivity().setResult(1);
            Utils.getInstance().showToast("Signature saved");
            //Move to Print Screen
//            DashBoardActivity.getInstance().changeScreen(CurrentScreen.PRINT_BITMAP_SCREEN, false, true, null);

//            doPrint();
        }
    }

    public File saveToDirectory(Context ctx, String name, String fileExtension) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator +
                ctx.getString(R.string.app_name));
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(dir, name + "." +  fileExtension);
        if (file.isFile()) file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void doPrint(){
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getActivity().getString(R.string.app_name) + " Document";
        printManager.print(jobName, pda, null);

    }
    PrintDocumentAdapter pda = new PrintDocumentAdapter(){

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback){
            InputStream input = null;
            OutputStream output = null;

            try {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "test.pdf");
                if(!file.isFile()){
                    Utils.getInstance().showToast("File not found");
                    return;
                }

                input = new FileInputStream(file);
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (FileNotFoundException ee){
                //Catch exception
            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    input.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException nullpointer){
                    nullpointer.printStackTrace();
                }
            }
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras){

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

            callback.onLayoutFinished(pdi, true);
        }
    };

}
