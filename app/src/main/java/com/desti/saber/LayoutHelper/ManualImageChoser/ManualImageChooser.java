package com.desti.saber.LayoutHelper.ManualImageChoser;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.desti.saber.R;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ManualImageChooser extends Activity{

    private final Activity activity;
    private ViewGroup parent;

    public ManualImageChooser(Activity activity) {
       this.activity = activity;
    }

    public void startChooser(SuccessSetImage successSetImage){
        int checkStorageRead = activity.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if(checkStorageRead == PackageManager.PERMISSION_GRANTED){
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
                            File nextDir = new File(fullPathByLocField);

                            if(nextDir.isDirectory()){
                                File[] nextDirList = nextDir.listFiles();

                                if(nextDirList != null){
                                    if(nextDirList.length < 1){
                                        Toast.makeText(
                                            activity,
                                            "Tidak Ada Berkas Pada Folder " + nextDir.getName(),
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

                                            inflateNextDir.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ((TextView) inflateTvFile.findViewById(singleTvLocId))
                                                    .setText(nextDirSgFile.getAbsolutePath());
                                                    inflateTvFile.callOnClick();
                                                }
                                            });
                                        }
                                    }
                                }
                            }else{
                                try{
                                    Bitmap bitmap = BitmapFactory.decodeFile(fullPathByLocField);
                                    if(bitmap != null){
                                       successSetImage.success(bitmap, fullPathByLocField, parent);
                                    }else{
                                        Toast.makeText(activity, R.string.trash_photo_format, Toast.LENGTH_LONG).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(activity, R.string.fail_set_image, Toast.LENGTH_LONG).show();
                                }
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
        }
    }

    public void setParent(ViewGroup parent){
        this.parent = parent;
    }

    public void closeImageChooser(ViewGroup parentFileChooser){
        parentFileChooser.removeView(parentFileChooser.findViewById(R.id.parentDirectoryList));
    }
}
