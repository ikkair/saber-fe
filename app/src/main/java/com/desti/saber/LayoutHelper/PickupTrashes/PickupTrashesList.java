package com.desti.saber.LayoutHelper.PickupTrashes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.desti.saber.LayoutHelper.ReportDownload.PDFCreator;
import com.desti.saber.LayoutHelper.ReportDownload.ReportDownload;
import com.desti.saber.LayoutHelper.SinglePickupDetail.SinglePickupDetail;
import com.desti.saber.R;
import com.desti.saber.configs.OkHttpHandler;
import com.desti.saber.utils.IDRFormatCurr;
import com.desti.saber.utils.dto.PickupDetailDto;
import com.desti.saber.utils.dto.TrashDetailDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PickupTrashesList {

    private final Activity activity;
    private  PopupWindow popupWindow;
    private ViewGroup rootListLayout;
    private final OkHttpHandler okHttpHandler;
    private ReportDownload reportDownload;
    private Button pickupButtonTask;

    public PickupTrashesList(Activity activity) {
        this.activity = activity;
        this.okHttpHandler = new OkHttpHandler();
    }

    public void startPopUp(TrashDetailDTO[] trashDetailDTOS, PickupDetailDto pickupDetailDto, PickupOnClicked pickupOnClicked){
        close();

        View layoutPopUp = activity.getLayoutInflater().inflate(R.layout.single_pickup_trash_list, (ViewGroup) activity.getWindow().getDecorView(),false);
        popupWindow = new PopupWindow(layoutPopUp, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup pickupDetailContainer = layoutPopUp.findViewById(R.id.singlePickupDetails);
        Button printOutPdfBtn = layoutPopUp.findViewById(R.id.pdfDonwloadTrashList);
        SinglePickupDetail singlePickupDetail = new SinglePickupDetail(activity);
        Button closeBtn = layoutPopUp.findViewById(R.id.trashListCloseBtn);
        rootListLayout = layoutPopUp.findViewById(R.id.trashListContainer);
        pickupButtonTask = layoutPopUp.findViewById(R.id.pickupTrash);
        PdfPTable trashListTable = new PdfPTable(5);
        List<String> trashFinishRender = new ArrayList<>();

        trashListTable.addCell(singlePickupDetail.cell2("Foto Sampah"));
        trashListTable.addCell(singlePickupDetail.cell2("Type Sampah"));
        trashListTable.addCell(singlePickupDetail.cell2("Berat Sampah (KG)"));
        trashListTable.addCell(singlePickupDetail.cell2("Total Harga Sampah"));
        trashListTable.addCell(singlePickupDetail.cell2("Keterangan"));

        for (TrashDetailDTO trashDetailDTO : trashDetailDTOS) {
            addSingleTrashView(trashDetailDTO, new SuccessImage() {
                @Override
                public void success(InputStream inputStream) {
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        Image image = Image.getInstance(byteArrayOutputStream.toByteArray());

                        trashListTable.addCell(image);
                    } catch (Exception e){
                        trashListTable.addCell(singlePickupDetail.cell("Gagal Mengunduh Foto"));
                        e.printStackTrace();
                    }

                    long trashAmount = trashDetailDTO.getAmount();
                    float trashWeight = trashDetailDTO.getWeight_kg();
                    String trashWeightRender = String.valueOf(trashWeight);
                    String trashAmountRender = IDRFormatCurr.currFormat(trashAmount).concat(" X ")
                                             .concat(trashWeightRender).concat(" = ")
                                             .concat(IDRFormatCurr.currFormat((long) (trashAmount * trashWeight)));

                    trashFinishRender.add(trashDetailDTO.getId());
                    trashListTable.addCell(singlePickupDetail.cell(trashDetailDTO.getType()));
                    trashListTable.addCell(singlePickupDetail.cell(String.valueOf(trashDetailDTO.getWeight_kg())));
                    trashListTable.addCell(singlePickupDetail.cell(trashAmountRender));
                    trashListTable.addCell(singlePickupDetail.cell(trashDetailDTO.getDescription()));
                }
            });
        }

        singlePickupDetail.showPickupDetail(pickupDetailContainer, pickupDetailDto);
        printOutPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getReportDownload() == null){
                    Toast.makeText(activity, "Feature Report Tidak Diset", Toast.LENGTH_LONG).show();
                    return;
                }

                if(trashFinishRender.size() != trashDetailDTOS.length){
                    Toast.makeText(activity, "Tunggu Hingga List Sampah Selesai Mengunduh", Toast.LENGTH_LONG).show();
                    return;
                }

                getReportDownload().setPdfName("Trash List");
                getReportDownload().baseReport(new PDFCreator() {
                    @Override
                    public void createReportPDF(Document document) throws Exception {
                        Font labelFont  = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                        Paragraph pickupDetailsParagraph = new Paragraph("Detail Pickup Sampah", labelFont);
                        Paragraph trashListParagraph = new Paragraph("List Sampah", labelFont);

                        pickupDetailsParagraph.setAlignment(Element.ALIGN_CENTER);
                        trashListParagraph.setAlignment(Element.ALIGN_LEFT);
                        pickupDetailsParagraph.setSpacingAfter(6f);
                        trashListParagraph.setSpacingAfter(8f);

                        document.add(pickupDetailsParagraph);
                        document.add(singlePickupDetail.pdfPTablePickupDetail(pickupDetailDto));
                        document.add(Chunk.NEWLINE);
                        document.add(trashListParagraph);
                        document.add(trashListTable);
                    }
                }, (ViewGroup) layoutPopUp);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        pickupButtonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupOnClicked.pickOnClicked(v);
            }
        });

        popupWindow.showAtLocation(layoutPopUp, Gravity.CENTER, 0, 0);
    }

    public void close(){
        if(popupWindow != null){
            popupWindow.dismiss();
        }
    }

    public void addSingleTrashView(TrashDetailDTO trashDetailDTO, SuccessImage successImage){
        View view = activity.getLayoutInflater().inflate(R.layout.single_trash_detail, (ViewGroup) activity.getWindow().getDecorView(),false);
        String amount = String.valueOf((int) (trashDetailDTO.getAmount()* trashDetailDTO.getWeight_kg()));
        String uriGetPhoto = (trashDetailDTO.getPhoto() != null) ? "https://drive.usercontent.google.com/download?id=".concat(trashDetailDTO.getPhoto().replace("/view", "")) : "";
        Request trashImageReq = new Request.Builder().url(uriGetPhoto).build();
        ImageView trashImage = view.findViewById(R.id.trashDetailImage);
        final InputStream[] imageInputStreamTrash = new InputStream[1];

        ((TextView) view.findViewById(R.id.trashDetailWeight)).setText(String.valueOf(trashDetailDTO.getWeight_kg()));
        ((TextView) view.findViewById(R.id.trashDetailType)).setText(trashDetailDTO.getType());
        ((TextView) view.findViewById(R.id.trashDetailAmount)).setText(IDRFormatCurr.currFormat(Long.valueOf(amount)));
        ((TextView) view.findViewById(R.id.trashDetailsDesc)).setText(trashDetailDTO.getDescription());

        rootListLayout.addView(view);
        okHttpHandler.requestAsync(activity, trashImageReq, new OkHttpHandler.MyCallback() {

            private FrameLayout progressBar = view.findViewById(R.id.progressBarLoading);

            @Override
            public void onSuccess(Context context, Response response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if(response != null && response.body() != null && response.isSuccessful()){
                            try{
                                byte[] byteResponse = response.body().bytes();

                                successImage.success(new ByteArrayInputStream(byteResponse));
                                trashImage.setImageDrawable(Drawable.createFromStream(new ByteArrayInputStream(byteResponse), null));
                            }catch (Exception e){
                                successImage.success(null);
                                e.printStackTrace();
                            }
                        } else {
                            successImage.success(null);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        successImage.success(null);
                    }
                });
            }
        });
    }

    public ReportDownload getReportDownload() {
        return reportDownload;
    }

    public void setReportDownload(ReportDownload reportDownload) {
        this.reportDownload = reportDownload;
    }

    public void visibilityPickupButton(boolean visibility){
        if(pickupButtonTask != null){
            if(visibility){
                pickupButtonTask.setVisibility(View.VISIBLE);
            } else {
                pickupButtonTask.setVisibility(View.GONE);
            }
        }
    }
}
