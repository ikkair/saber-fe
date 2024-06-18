package com.desti.saber.LayoutHelper.ReportDownload;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import com.desti.saber.R;
import com.desti.saber.utils.ImageSetterFromStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.util.zip.Inflater;

public class ReportDownload {
    public ReportDownload(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        View viewById = viewGroup.findViewById(R.id.downloadReportButton);

        if(viewById != null){
            viewGroup.removeView(viewById);
        }

        ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.download_report_layout, viewGroup);
        Button button = view.findViewById(R.id.mainBtnReportDownload);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PdfWriter pdfWriter = new PdfWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/saber_report.pdf");
                    PdfDocument pdfDocument = new PdfDocument(pdfWriter);

                    pdfDocument.close();
                    pdfDocument.addNewPage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void blink(LinearLayout layout){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRotation(layout.getRotation() + 5);
                blink(layout);
            }
        }, 10);

    }
}
