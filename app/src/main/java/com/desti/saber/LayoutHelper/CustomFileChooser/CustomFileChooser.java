package com.desti.saber.LayoutHelper.CustomFileChooser;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.desti.saber.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomFileChooser extends AppCompatActivity {

    private final Activity activity;
    private ViewGroup parent;

    public CustomFileChooser(Activity activity) {
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
        });
    }

    public void startDirectoryChooser(ObjectOnClick objectOnClick){
        baseChooser(objectOnClick);
    }

    private void baseChooser(ObjectOnClick objectOnClick){
        int checkStorageRead = activity.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int checkStorageWrite = activity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(checkStorageRead == PackageManager.PERMISSION_GRANTED && checkStorageWrite == PackageManager.PERMISSION_GRANTED){
            ViewGroup popParent = parent;
            ViewGroup inflater = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.directory_pop_up_layout, popParent, true);
            ViewGroup dirListLocationView = inflater.findViewById(R.id.rootDirectoryList);
            Button backWardLoc = inflater.findViewById(R.id.backTraceLocBtn);
            Button backButtonDir = inflater.findViewById(R.id.cancelSelectImg);
            List<String> fileTraceClicked = new ArrayList<>();
            int singleTvLocId = R.id.singleTvFileLoc;
            int singleTvNameId = R.id.singleTvFileName;
            File envExternalStorage = Environment.getExternalStorageDirectory();
            File[] lisFiles = envExternalStorage.listFiles();

            fileTraceClicked.add(envExternalStorage.getAbsolutePath());

            if(lisFiles != null){
                for(int i =0; i < lisFiles.length; i++){
                    File sgFile = lisFiles[i];
                    View inflateTvFile = activity.getLayoutInflater()
                            .inflate(
                                    R.layout.single_list_file_choser,
                                    dirListLocationView, false
                            );
                    TextView singleTvFile = inflateTvFile.findViewById(singleTvNameId);
                    TextView singleTvLoc = inflateTvFile.findViewById(singleTvLocId);

                    singleTvLoc.setText(sgFile.getAbsolutePath());
                    singleTvFile.setText(sgFile.getName());
                    dirListLocationView.addView(inflateTvFile);
                    inflateTvFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView sgFileNextDirTvLoc = v.findViewById(singleTvLocId);
                            String fullPathByLocField = sgFileNextDirTvLoc.getText().toString();
                            File file = new File(fullPathByLocField);

                            if(file.isDirectory()){
                                File[] nextDirList = file.listFiles();

                                if(nextDirList != null){
                                    if(nextDirList.length < 1){
                                        Toast.makeText(
                                            activity,
                                            "Tidak Ada Berkas Pada Folder " + file.getName(),
                                            Toast.LENGTH_LONG
                                        ).show();
                                    }else{
                                        if(!fileTraceClicked.contains(fullPathByLocField)){
                                            fileTraceClicked.add(fullPathByLocField);
                                        }

                                        dirListLocationView.removeAllViews();
                                        for(File nextDirSgFile : nextDirList){
                                            View inflateNextDir = activity.getLayoutInflater()
                                            .inflate(
                                                R.layout.single_list_file_choser,
                                                dirListLocationView, false
                                            );
                                            TextView tvNextDir = inflateNextDir.findViewById(singleTvNameId);
                                            TextView singleTvLoc = inflateNextDir.findViewById(singleTvLocId);

                                            tvNextDir.setText(nextDirSgFile.getName());
                                            singleTvLoc.setText(nextDirSgFile.getAbsolutePath());
                                            dirListLocationView.addView(inflateNextDir);

//                                            inflateNextDir.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                }
//                                            });
                                            inflateNextDir.setOnTouchListener(new View.OnTouchListener() {
                                                long startTime = 0;

                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                                                        startTime = System.currentTimeMillis();
                                                    }else{
                                                        if((System.currentTimeMillis() - startTime) >= 800){
                                                            Toast.makeText(activity, "Alamat Folder Dipilih", Toast.LENGTH_LONG).show();
                                                        }else{
                                                            ((TextView) inflateTvFile.findViewById(singleTvLocId)).setText(nextDirSgFile.getAbsolutePath());
                                                            objectOnClick.onDirectoryClick(file, parent);
                                                            inflateTvFile.callOnClick();
                                                        }
                                                    }

                                                    return true;
                                                }
                                            });
                                        }
                                    }
                                }
                            }else{
                                objectOnClick.onFileClick(file, parent);
                            }
                        }
                    });

                    if(i == 0){
                        backWardLoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int trashBackwardClick = fileTraceClicked.size();

                                if(trashBackwardClick >= 1){
                                    int getBackTracePath = (trashBackwardClick == 1) ? 0 : trashBackwardClick-2;
                                    ((TextView) inflateTvFile.findViewById(singleTvLocId)).setText(fileTraceClicked.get(getBackTracePath));
                                    inflateTvFile.callOnClick();

                                    if(trashBackwardClick > 1){
                                        fileTraceClicked.remove(trashBackwardClick-1);
                                    }
                                }
                            }
                        });
                    }
                }
            }

            backButtonDir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inflater.removeView(inflater.findViewById(R.id.parentDirectoryList));
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
        System.out.println("ABCD");
        String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(
            activity, permissionList, PackageManager.PERMISSION_GRANTED
        );
    }
}
