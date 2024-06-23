package com.desti.saber.LayoutHelper.CustomFileChooser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.desti.saber.BuildConfig;
import com.desti.saber.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CustomFileChooser {

    private ViewGroup dirListLocationView;
    private final Activity activity;
    private ViewGroup parent;
    private List<String> traceListPath;

    public CustomFileChooser(Activity activity) {
        traceListPath = new ArrayList<>();
       this.activity = activity;
    }

    public void startImageChooser(SuccessSetImage successSetImage){
        baseChooser(new ObjectOnClick() {
            @Override
            public void onFileClick(File file, ViewGroup parentFileChooser) {
                try{
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if(bitmap != null){
                        successSetImage.success(bitmap, file.getAbsolutePath(), parentFileChooser);
                    }else{
                        Toast.makeText(activity, R.string.trash_photo_format, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(activity, R.string.fail_set_image, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onDirectoryClick(File file, ViewGroup parentFileChooser) {

            }

            @Override
            public void onCancelPressed(View view) {

            }

            @Override
            public void onDirectoryHoldPress(File file, ViewGroup parentFileChooser, String absolutePathLocation) {

            }
        });
    }

    public void baseChooser(ObjectOnClick objectOnClick){
        int checkStorageRead = activity.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int checkStorageWrite = activity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(Build.VERSION.SDK_INT >= 30){
            int checkManageStorage = activity.checkCallingOrSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE);

            if(checkManageStorage == PackageManager.PERMISSION_GRANTED){
                readStoragePermission();
                return;
            }
        }

        if(checkStorageRead == PackageManager.PERMISSION_GRANTED && checkStorageWrite == PackageManager.PERMISSION_GRANTED){
            View checkExistsPopUp = parent.getRootView().findViewById(R.id.parentDirectoryList);

            if(checkExistsPopUp != null){
                ((ViewGroup) parent.getRootView()).removeView(checkExistsPopUp);
            }

            ViewGroup popParent = parent;
            ViewGroup inflater = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.directory_pop_up_layout, popParent, true);
            dirListLocationView = inflater.findViewById(R.id.rootDirectoryList);
            File envExternalStorage = Environment.getExternalStorageDirectory();
            Button backButtonDir = inflater.findViewById(R.id.cancelSelectImg);
            Button backTraceButton = inflater.findViewById(R.id.backTraceLocBtn);

            traceListPath.add(envExternalStorage.getAbsolutePath());
            listingDir(envExternalStorage.getAbsolutePath(), objectOnClick);

            backButtonDir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objectOnClick.onCancelPressed(v);
                    closeCustomChooser(parent);
                }
            });

            backTraceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(traceListPath.size() >= 1){
                        int calcBackTraceIndex = traceListPath.size()-1;
                        String copyIndex = String.valueOf(traceListPath.get(calcBackTraceIndex));

                        if(calcBackTraceIndex > 0){
                            traceListPath.remove(calcBackTraceIndex);
                        }

                        listingDir(copyIndex, objectOnClick);
                    }
                }
            });
        }else{
            readStoragePermission();
        }
    }

    public void setParent(ViewGroup parent){
        this.parent = parent;
    }

    public void closeCustomChooser(ViewGroup parentFileChooser){
        parentFileChooser.removeView(parentFileChooser.findViewById(R.id.parentDirectoryList));
    }

    public void  readStoragePermission(){
        List<String> permissionList = new ArrayList<>();
        boolean checkApiLevel = (Build.VERSION.SDK_INT >= 30);

        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(checkApiLevel){
            permissionList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }

        ActivityCompat.requestPermissions(
            activity, permissionList.toArray(new String[0]), PackageManager.PERMISSION_GRANTED
        );

        if(checkApiLevel){
            Uri uri= Uri.fromParts("package", activity.getPackageName(), null);
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);

            intent.setData(uri);
            activity.startActivity(intent);
        }
    }

    private void listingDir(String rootDirPath, ObjectOnClick objectOnClick){
        File rootDir = new File(rootDirPath);

        if(rootDir.exists() && rootDir.isDirectory() && rootDir.list() != null){
            if(rootDir.list().length > 0){
                dirListLocationView.removeAllViews();
            }

            for (String singlePathDir : rootDir.list()){
                String singlePathRootDir = rootDir.getAbsolutePath();
                String singleFullPathDir = singlePathRootDir + "/" + singlePathDir;
                View layoutInflater = LayoutInflater.from(dirListLocationView.getContext()).inflate(R.layout.single_list_file_choser,null);
                TextView singleTvFile = layoutInflater.findViewById(R.id.singleTvFileName);
                File checkFile = new File(singleFullPathDir);

                singleTvFile.setText(singlePathDir);
                layoutInflater.setOnTouchListener(new View.OnTouchListener() {
                    private long startClick = 0;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            startClick = System.currentTimeMillis();
                        } else if(event.getAction() == MotionEvent.ACTION_UP){
                            if((System.currentTimeMillis() - startClick) >= 500){
                                if(checkFile.isDirectory()) {
                                    objectOnClick.onDirectoryHoldPress(checkFile, parent, singleFullPathDir);
                                }
                            }else{
                                if(checkFile.isDirectory()){
                                    listingDir(singleFullPathDir, objectOnClick);
                                    if(!traceListPath.contains(singlePathRootDir)){
                                        traceListPath.add(singlePathRootDir);
                                    }
                                    objectOnClick.onDirectoryClick(checkFile, parent);
                                }else {
                                    objectOnClick.onFileClick(checkFile, parent);
                                }
                            }
                        }

                        return true;
                    }
                });

                dirListLocationView.addView(layoutInflater);
            }
        }
    }
}
