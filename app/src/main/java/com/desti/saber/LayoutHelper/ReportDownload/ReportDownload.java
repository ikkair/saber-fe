package com.desti.saber.LayoutHelper.ReportDownload;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.desti.saber.LayoutHelper.CustomFileChooser.CustomFileChooser;
import com.desti.saber.LayoutHelper.CustomFileChooser.ObjectOnClick;
import com.desti.saber.LayoutHelper.ProgressBar.ProgressBarHelper;
import com.desti.saber.R;
import com.desti.saber.utils.InputStreamToBytes;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;

public class ReportDownload {

    private final Activity activity;
    private final ViewGroup viewGroup;
    private String timePrintReport;
    private String pdfName;
    private String userId;
    private String userName;


    public ReportDownload(Activity activity) {
        this.activity = activity;
        this.viewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        timePrintReport = new Timestamp(System.currentTimeMillis()).toString();
        timePrintReport = timePrintReport.replace(":", ".");
        timePrintReport = timePrintReport.replace(" ", "_");
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        String username = userName;

        if(username != null && username.length() > 30){
            username = userName.toUpperCase().substring(0, 30).concat(" ...");
        }

        return username;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void startReport(PDFCreator pdfCreator){
        startReportOnlyClick(new ClickableReport() {
            @Override
            public void onClicked(View viewButton, ViewGroup rootView) {
                baseReport(pdfCreator, rootView);
            }
        });
    }

    public void startReportOnlyClick(ClickableReport clickableReport){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeReport();

                ViewGroup view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.download_report_layout, viewGroup);
                Button button = view.findViewById(R.id.mainBtnReportDownload);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickableReport.onClicked(v, view);
                    }
                });
            }
        });
    }

    public void baseReport(PDFCreator pdfCreator, ViewGroup rootView){
        CustomFileChooser customFileChooser = new CustomFileChooser(activity);
        ObjectOnClick objectOnClick = new ObjectOnClick() {
            @Override
            public void onFileClick(File file, ViewGroup parentFileChooser) {

            }

            @Override
            public void onDirectoryClick(File file, ViewGroup parentFileChooser) {

            }

            @Override
            public void onCancelPressed(View view) {

            }

            @Override
            public void onDirectoryHoldPress(File file, ViewGroup parentFileChooser, String absolutePathLocation) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String pdfName = absolutePathLocation + "/Saber_Report" + getPdfName() + timePrintReport + ".pdf";
                            Document document = new Document(PageSize.A4, 20, 20, 90, 100);
                            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfName));
                            Font font = FontFactory.getFont("Calibri", 11, BaseColor.BLACK);
                            PdfPageEventHelper pdfPageEventHelper = new PdfPageEventHelper(){
                                @Override
                                public void onStartPage(PdfWriter writer, Document document) {
                                    super.onStartPage(writer, document);
                                    try {
                                        InputStream inputStream = activity.getResources().getAssets().open("logIn_picture_decoration.png");
                                        Font fontSet = FontFactory.getFont("Calibri", 14);
                                        Image image = Image.getInstance(InputStreamToBytes.getBytes(inputStream));
                                        PdfContentByte direction = writer.getDirectContent();
                                        PdfContentByte canvas = writer.getDirectContent();
                                        PdfPTable tabHead = new PdfPTable(2);
                                        Rectangle pageSize = document.getPageSize();
                                        Float centerPointVal = pageSize.getWidth()/2;
                                        Float documentHeight = pageSize.getHeight();

                                        tabHead.addCell(addCustomCell("Dicetak Oleh", Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell(getUserName(), Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell("Tanggal Dicetak", Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell(timePrintReport, Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell("ID Pengguna", Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell(getUserId(), Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell("No Halaman", Element.ALIGN_LEFT, 10));
                                        tabHead.addCell(addCustomCell(String.valueOf(document.getPageNumber()), Element.ALIGN_LEFT, 10));

                                        tabHead.setTotalWidth(new float[] {120.0f, 210.0f});
                                        tabHead.writeSelectedRows(0, -1, (pageSize.getWidth() - tabHead.getTotalWidth()) - document.rightMargin(), documentHeight - (tabHead.getTotalHeight()/2) + 26, writer.getDirectContent());

                                        fontSet.setStyle(Font.BOLD);
                                        canvas.setColorStroke(BaseColor.BLACK);
                                        canvas.moveTo(10, documentHeight - 85);
                                        canvas.lineTo(pageSize.getWidth() - 10, documentHeight - 85);
                                        canvas.closePathStroke();

                                        image.setAlignment(Element.ALIGN_LEFT);
                                        image.scalePercent(20f, 20f);
                                        image.setAbsolutePosition(23,( documentHeight-image.getScaledHeight())-5);
                                        document.add(image);
                                        document.add(Chunk.NEWLINE);
                                        document.add(Chunk.NEWLINE);
                                    } catch (IOException | DocumentException e) {
                                        e.printStackTrace();
                                    }
                                }


                            };
                            Paragraph headerParagraph = new Paragraph("SABER " + getPdfName().replace("_", "").toUpperCase() + " REPORT DOCUMENT", font);

                            headerParagraph.setAlignment(Element.ALIGN_CENTER);
                            headerParagraph.setSpacingAfter(12);
                            pdfWriter.setPageEvent(pdfPageEventHelper);
                            document.open();
                            document.add(headerParagraph);
                            document.add(Chunk.NEWLINE);
                            pdfCreator.createReportPDF(document);
                            document.close();
                            customFileChooser.closeCustomChooser(parentFileChooser);
                            Toast.makeText(activity.getApplicationContext(), "Download PDF Sukses, Disimpan Pada Lokasi : " + pdfName, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(activity.getApplicationContext(), "Gagal Mengunduh Report Alasan : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        customFileChooser.setParent(rootView);
        customFileChooser.baseChooser(objectOnClick);
    }

    public void closeReport(){
        View viewById = viewGroup.findViewById(R.id.downloadReportButton);

        if(viewById != null){
            viewGroup.removeView(viewById);
        }
    }

    public String getPdfName() {
        return (pdfName == null) ? "_" : "_" + pdfName + "_";
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    private void addTableHeader(PdfPTable tbl, String[] tblHead){
        Font font = FontFactory.getFont("Calibri", 11, new BaseColor(242, 242, 242));
        PdfPCell header = new PdfPCell();
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(1);
        header.setPadding(3);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);

        for(int i =0; i < tblHead.length; i++){
            header.setPhrase(new Phrase(tblHead[i], font));
            tbl.addCell(header);
        }
    }

    public PdfPCell addCustomCell(String text, int alignment, int fontSize){
        Font font = FontFactory.getFont("Verdana", fontSize);
        Phrase phrase = new Phrase(text, font);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setUseAscender(true);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(239, 240, 244));
        cell.setBorderWidth(0.2f);
        cell.setHorizontalAlignment(alignment);

        return cell;
    }

    public void setToFront(){
        closeReport();
    }

}
