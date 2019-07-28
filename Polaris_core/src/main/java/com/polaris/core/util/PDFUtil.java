package com.polaris.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PDFUtil {
    private static final LogUtil logger = LogUtil.getInstance(PDFUtil.class);

    private PDFUtil() {
    }

    /**
     * @param pageSize 页面 大小
     * @param tempPath pdf临时文件 *.pdf
     * @return Document document对象
     */
    public static Document createPDF(Rectangle pageSize, String tempPath) throws FileNotFoundException, DocumentException {
        logger.info("INFO:[tempPath:{}]", tempPath);
        Document document = new Document(pageSize);
        File file = new File(tempPath);
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        return document;
    }


    /**
     * 创建一个表格
     *
     * @param widths 每列宽度
     * @param heads  表格头
     * @param datas  数据
     */
    public static PdfPTable getTable(float[] widths, String[] heads, float fontSize, List<String> datas) throws IOException, DocumentException {
        PdfPTable table = new PdfPTable(widths);
        if (heads != null) {
            for (int i = 0; i < widths.length; i++) {
                String head = heads.length > i ? heads[i] : "";
                table.addCell(getCell(head, fontSize));
            }
        }
        for (String data : datas) {
            table.addCell(getCell(data, fontSize));
        }
        table.setHeaderRows(0);
        return table;
    }

    /**
     * 创建表格单元格
     *
     * @param text     单元格文字
     * @param fontSize 字体大小
     */
    public static PdfPCell getCell(String text, float fontSize) throws IOException, DocumentException {
        PdfPCell cell = new PdfPCell(new Paragraph(text, getFontNORMAL(fontSize)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * 设置头 （可使用 getParagraph() 创建段落对象 手动设置）
     *
     * @param headerName 头文字
     * @param document   doc 对象
     * @param fontSize   字体大小
     */
    public static void setHeaderTitle(String headerName, Document document, float fontSize) throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
        Chunk chunk = new Chunk(headerName);
        paragraph.setFont(getFontBOLD(fontSize));
        paragraph.add(chunk);
        document.add(paragraph);
    }

    /**
     * 画一条横线
     */
    public static void setLineSeparator(Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(new LineSeparator()));
        document.add(paragraph);
    }

    /**
     * 添加空行
     *
     * @param leading 空行高度 (间距)
     */
    public static void wrapHandle(Document document, float leading) throws DocumentException, IOException {
        Paragraph blankRow = new Paragraph(leading, " ");
        document.add(blankRow);
    }

    /**
     * 创建段落块
     *
     * @param content   块中内容
     * @param alignment 对齐方式 Element.
     * @param font      段落字体
     * @param spacing   间距
     */
    public static Element getParagraph(String content, int alignment, Font font, int spacing) {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(alignment);
        Chunk chunk = new Chunk(content);
        paragraph.setFont(font);
        paragraph.add(chunk);
        paragraph.setSpacingAfter(spacing);
        return paragraph;
    }

    /**
     * 创建字体 加粗
     *
     * @param fontSize
     */
    public static Font getFontBOLD(float fontSize) throws DocumentException, IOException {
        return new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), fontSize, Font.BOLD);
    }

    /**
     * 创建字体 标准
     */
    public static Font getFontNORMAL(float fontSize) throws DocumentException, IOException {
        return new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), fontSize, Font.NORMAL);
    }

    /**
     * @param document
     */
    public static void closePDF(Document document) {
        document.close();
    }

}
