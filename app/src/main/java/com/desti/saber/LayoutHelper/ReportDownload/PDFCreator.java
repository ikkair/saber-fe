package com.desti.saber.LayoutHelper.ReportDownload;

import com.itextpdf.text.Document;

public interface PDFCreator {
    void createReportPDF(Document document) throws Exception;
}
