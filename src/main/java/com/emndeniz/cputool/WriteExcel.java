package com.emndeniz.cputool;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.*;
import jxl.format.Colour;
import jxl.write.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by emindeniz on 12/09/16.
 * <p>
 * This class will handle the exporting excel functionality.
 */

class WriteExcel {

    private WritableCellFormat contentCellFormat, captionCellFormat, headerCellFormat;
    private WritableWorkbook workbook;

    private enum CellFontType {

        FONT_TYPE_HEADER,
        FONT_TYPE_CAPTION,
        FONT_TYPE_CONTENT
    }

    /**
     * Constructor of WriteExcel class
     *
     * @param outPutFileDirectory directory which excel file will create
     * @throws IOException
     * @throws WriteException
     */
    WriteExcel(String outPutFileDirectory) throws IOException, WriteException {
        File file = new File(outPutFileDirectory);
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        initCellFontFormats();
    }


    /**
     * Initialize the header , caption and content font formats
     *
     * @throws WriteException
     */
    private void initCellFontFormats() throws WriteException {
        // Lets create a contentCellFormat font
        WritableFont arial10pt = new WritableFont(WritableFont.ARIAL, 10);
        // Define the cell format
        contentCellFormat = new WritableCellFormat(arial10pt);
        // Lets automatically wrap the cells
        contentCellFormat.setWrap(true);

        // create create a bold font for caption
        WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        captionCellFormat = new WritableCellFormat(arial12ptBold);
        captionCellFormat.setWrap(true);

        WritableFont arial14ptBoldRed = new WritableFont(WritableFont.ARIAL, 14,
                WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.RED);
        headerCellFormat = new WritableCellFormat(arial14ptBoldRed);
        captionCellFormat.setWrap(true);


        CellView cv = new CellView();
        cv.setFormat(contentCellFormat);
        cv.setFormat(captionCellFormat);
        cv.setFormat(headerCellFormat);
    }


    /**
     * Add cell content to related column and row.
     *
     * @param sheet    WritableSheet
     * @param column   Column number
     * @param row      Row number
     * @param s        Content as a string
     * @param fontType cell font type
     * @throws WriteException Can throw jxl api exception
     */
    private void addCellContent(WritableSheet sheet, int column, int row, String s, CellFontType fontType)
            throws WriteException {
        Label label;
        if (fontType == CellFontType.FONT_TYPE_CONTENT)
            label = new Label(column, row, s, contentCellFormat);
        else if (fontType == CellFontType.FONT_TYPE_CAPTION)
            label = new Label(column, row, s, captionCellFormat);
        else
            label = new Label(column, row, s, headerCellFormat);
        sheet.addCell(label);
    }

    /**
     *
     * Creates excel file and exports all data in it.
     * @param cpuData
     * @throws IOException
     * @throws WriteException
     */
    void createAndExportToExcel(String[] cpuData) throws IOException, WriteException {

        initCellFontFormats();
        WritableSheet excelSheet = workbook.getSheet(0);
        int rowNumber = 0;
        for (String dataLine : cpuData) {
            dataLine = dataLine.trim(); // dataLine includes white space at beginning
            if (dataLine.startsWith("--------------Data")) {
                // New data received, add it as caption on first column
                String dataNumberString = dataLine.substring(dataLine.indexOf("D"), dataLine.indexOf("r"));
                addCellContent(excelSheet, 0, rowNumber, dataNumberString, CellFontType.FONT_TYPE_HEADER);

            } else if (dataLine.startsWith("User") && dataLine.contains("%")) {
                // Line includes general usage data
                String[] content = dataLine.split(",");
                for (int i = 0; i < content.length; i++) {
                    addCellContent(excelSheet, i, rowNumber, content[i], CellFontType.FONT_TYPE_CONTENT);
                }
            } else if (dataLine.startsWith("User") && !dataLine.contains("%")) {
                //TODO Shall we include it to excel ?
            } else if (dataLine.contains("PID")) {
                // Line includes data type names
                String[] content = dataLine.split("\\s+");
                for (int i = 0; i < content.length; i++) {
                    addCellContent(excelSheet, i, rowNumber, content[i], CellFontType.FONT_TYPE_CAPTION);
                }
            } else {
                // Line includes usage data for related process(content)
                String[] content = dataLine.split("\\s+");
                boolean isLineHasPCY = false; // Some lines doesn't have PCY (Policy)
                for (int i = 0; i < content.length; i++) {
                    if (content.length < 10 && i == 7) {
                        isLineHasPCY = true;
                    }
                    if (isLineHasPCY) {
                        addCellContent(excelSheet, i + 1, rowNumber, content[i], CellFontType.FONT_TYPE_CONTENT);
                    } else {
                        addCellContent(excelSheet, i, rowNumber, content[i], CellFontType.FONT_TYPE_CONTENT);
                    }
                }
            }
            rowNumber++;
        }
        workbook.write();
        workbook.close();
    }
}
