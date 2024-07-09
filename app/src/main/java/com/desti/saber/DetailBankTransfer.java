package com.desti.saber;

import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.desti.saber.LayoutHelper.ReportDownload.PDFCreator;
import com.desti.saber.LayoutHelper.ReportDownload.ReportDownload;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.dto.DetailTrfDto;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;

public class DetailBankTransfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_detail_bank_trasnfer);

        ReportDownload reportDownload = new ReportDownload(this);
        String detailsTrf = getIntent().getExtras().getString("detailsData");
        DetailTrfDto detailTrfDto = new Gson().fromJson(detailsTrf, DetailTrfDto.class);
        TextView detailsTrfTv = findViewById(R.id.detailsTrf);
        Button backToDashBoard = findViewById(R.id.backToDashBoardFrDtl);

        reportDownload.setPdfName("Transaction Withdraw Balance");
        reportDownload.setUserName(detailTrfDto.getUserFullName());
        reportDownload.setUserId(detailTrfDto.getUserId());
        reportDownload.startReport(new PDFCreator() {
            @Override
            public void createReportPDF(Document document) throws Exception {
                PdfPTable pdfPTable = new PdfPTable(2);

                pdfPTable.addCell(reportDownload.addCustomCell("Nama Bank Tujuan", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getBeneficiaryBankName(), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("No Rekening Tujuan", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getAccountOrVaNumberBeneficiary(), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("Nama Pemilik Rekening Tujuan", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getBeneficiaryName(), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("Tanggal Transaksi", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getTransactionDate(), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("Ref Number", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getRefnumber(), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("Jumlah Transaksi", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(IDRFormatCurr.currFormat(detailTrfDto.getTransferAmount()), Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell("Deskripsi", Element.ALIGN_LEFT, 12));
                pdfPTable.addCell(reportDownload.addCustomCell(detailTrfDto.getTransferDescription(), Element.ALIGN_LEFT, 12));

                document.add(pdfPTable);
            }
        });

        detailsTrfTv.setText(detailTrfDto.toString());
        backToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}