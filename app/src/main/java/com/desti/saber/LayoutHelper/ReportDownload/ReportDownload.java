package com.desti.saber.LayoutHelper.ReportDownload;

import android.app.Activity;
import android.view.*;
import android.widget.Button;
import com.desti.saber.LayoutHelper.CustomFileChooser.CustomFileChooser;
import com.desti.saber.LayoutHelper.CustomFileChooser.ObjectOnClick;
import com.desti.saber.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ReportDownload {

    public ReportDownload(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        View viewById = viewGroup.findViewById(R.id.downloadReportButton);

        if(viewById != null){
            viewGroup.removeView(viewById);
        }

        ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.download_report_layout, viewGroup);
        CustomFileChooser customFileChooser = new CustomFileChooser(activity);
        Button button = view.findViewById(R.id.mainBtnReportDownload);

        customFileChooser.setParent(view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFileChooser.startDirectoryChooser(new ObjectOnClick() {
                    @Override
                    public void onFileClick(File file, ViewGroup parentFileChooser) {

                    }

                    @Override
                    public void onDirectoryClick(File file, ViewGroup parentFileChooser) {
                        try {
                            OutputStream outputStream = new FileOutputStream(file.getAbsolutePath() + "/saber_report.pdf");
                            Document document = new Document(PageSize.A4);
                            Paragraph paragraph = new Paragraph();

                            paragraph.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

                            document.setMargins(10, 10, 10, 10);
                            PdfWriter.getInstance(document, outputStream);

                            document.close();
                            customFileChooser.closeCustomChooser(parentFileChooser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
