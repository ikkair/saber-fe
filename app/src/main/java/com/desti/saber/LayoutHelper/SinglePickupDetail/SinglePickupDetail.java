package com.desti.saber.LayoutHelper.SinglePickupDetail;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.desti.saber.R;
import com.desti.saber.utils.dto.PickupDetailDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class SinglePickupDetail {

    private final Activity activity;

    public SinglePickupDetail(Activity activity) {
        this.activity = activity;
    }

    public ViewGroup showPickupDetail(ViewGroup rootView, PickupDetailDto pickupDetailDto){
        View inflateSingleReqTrash = activity.getLayoutInflater().inflate(R.layout.single_trash_pickup, rootView, false);
        TextView reqPickUpAddress = inflateSingleReqTrash.findViewById(R.id.trashReqPickAddress);
        TextView reqPickUpStatus = inflateSingleReqTrash.findViewById(R.id.trashReqPickStatus);
        TextView reqPhoneNumber = inflateSingleReqTrash.findViewById(R.id.trashReqPhoneNumber);
        TextView reqPickUpDate = inflateSingleReqTrash.findViewById(R.id.trashReqPickDate);
        TextView reqPickUpId = inflateSingleReqTrash.findViewById(R.id.trashReqPickId);
        TextView reqUserName = inflateSingleReqTrash.findViewById(R.id.trashReqName);
        String pickupStatus = pickupDetailDto.getStatus();
        int statusPickColor = R.color.red;

        reqPickUpAddress.setText(pickupDetailDto.getAddress());
        reqPickUpDate.setText(pickupDetailDto.getTime());
        reqPickUpId.setText(pickupDetailDto.getId());
        reqPhoneNumber.setText(pickupDetailDto.getPhone());
        reqUserName.setText(pickupDetailDto.getName());

        if(pickupStatus.equalsIgnoreCase("waiting")){
            statusPickColor = R.color.orange;
            reqPickUpStatus.setText("MENUNGGU PICKUP");
        } else if (pickupStatus.equalsIgnoreCase("success")) {
            statusPickColor = R.color.green;
            reqPickUpStatus.setText("PICKUP SELESAI");
        }

        reqPickUpStatus.setTextColor(activity.getColor(statusPickColor));
        rootView.addView(inflateSingleReqTrash);

        return (ViewGroup) inflateSingleReqTrash;
    }

    public PdfPTable pdfPTablePickupDetail(PickupDetailDto pickupDetailDto){
        PdfPTable pdfPTable = new PdfPTable(2);

        pdfPTable.addCell(cell("Pengguna"));
        pdfPTable.addCell(cell(pickupDetailDto.getName()));
        pdfPTable.addCell(cell("No Hp"));
        pdfPTable.addCell(cell(pickupDetailDto.getPhone()));
        pdfPTable.addCell(cell("Alamat Pickup"));
        pdfPTable.addCell(cell(pickupDetailDto.getAddress()));
        pdfPTable.addCell(cell("Waktu Pickup"));
        pdfPTable.addCell(cell(pickupDetailDto.getTime()));
        pdfPTable.addCell(cell("ID Pickup"));
        pdfPTable.addCell(cell(pickupDetailDto.getId()));

        return pdfPTable;
    }

    public PdfPCell cell(String value){
        Font font = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Phrase phrase = new Phrase(value, font);
        PdfPCell cell = new PdfPCell(phrase);

        cell.setUseAscender(true);
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(247, 247, 247));
        cell.setBorderWidth(0.2f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);

        return cell;
    }
}
