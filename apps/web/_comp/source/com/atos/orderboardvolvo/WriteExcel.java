package com.atos.orderboardvolvo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class WriteExcel {

  private WritableCellFormat timesBoldUnderline;
  private WritableCellFormat times;
  

  public void write(List<MaterialCustomDataItem> materialCustomDataItemList) throws IOException, WriteException {
	File file = new File("C:\\myFolder\\shopOrderSequence.xls");
    WorkbookSettings wbSettings = new WorkbookSettings();    
    wbSettings.setLocale(new Locale("en", "EN"));
    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
    workbook.createSheet("Report", 0);
    WritableSheet excelSheet = workbook.getSheet(0);
    createLabel(excelSheet);
    createContent(excelSheet,materialCustomDataItemList);
    workbook.write();
    workbook.close();
  }

  private void createLabel(WritableSheet sheet)
      throws WriteException {
    // Lets create a times font
    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
    // Define the cell format
    times = new WritableCellFormat(times10pt);
    // Lets automatically wrap the cells
    times.setWrap(true);

    // create create a bold font with unterlines
    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
        UnderlineStyle.SINGLE);
    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
    // Lets automatically wrap the cells
    timesBoldUnderline.setWrap(true);

    CellView cv = new CellView();
    cv.setFormat(times);
    cv.setFormat(timesBoldUnderline);
    cv.setAutosize(true);

    // Write a few headers
    addCaption(sheet, 0, 0, "Order Number");
    addCaption(sheet, 1, 0, "Material");
    addCaption(sheet, 2, 0, "Work Center");
    addCaption(sheet, 3, 0, "Priority");
    addCaption(sheet, 4, 0, "Quantity Available");
    addCaption(sheet, 5, 0, "Quantity Built");
    addCaption(sheet, 6, 0, "Labor Charge Code");
    addCaption(sheet, 7, 0, "Plan Start Date");
    addCaption(sheet, 8, 0, "Plan End Date");
    

  }

  private void createContent(WritableSheet sheet,List<MaterialCustomDataItem> materialCustomDataItemList) throws WriteException,
      RowsExceededException {
	   int row=1;
	  
	   for(MaterialCustomDataItem customDataItem:materialCustomDataItemList){
		   int column=0;
		   addCaption(sheet,column,row,customDataItem.getShopOrderRef());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getMaterial());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getWorkcenter());
		   column++;
		   addCaption(sheet,column,row,""+customDataItem.getPriority());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getQtyAvailable());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getQtytobebuild());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getLaborchargecode());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getScheduledstartdate());
		   column++;
		   addCaption(sheet,column,row,customDataItem.getScheduleenddate());
		   row++;
	   }
  }

  private void addCaption(WritableSheet sheet, int column, int row, String s)
      throws RowsExceededException, WriteException {
    Label label;
    label = new Label(column, row, s, times);
    sheet.addCell(label);
  }

 

  public static void main(String[] args) throws WriteException, IOException {
   
//    test.write();
    System.out
        .println("Please check the result file under c:/temp/lars.xls ");
	  new WriteExcel().write(new ArrayList<MaterialCustomDataItem>());
  }
} 