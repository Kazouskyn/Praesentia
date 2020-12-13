package DataBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;


public class ExcelWork {
    static XSSFWorkbook workbook;
    static FileInputStream excelFile;
    static File filename = new File("C:\\Users\\Nadim Kayali\\Desktop\\Clases\\PennState\\Current\\PresentiaGUI2.0\\src\\DataBase\\Presentia.xlsx");

    public static boolean check(){

        try {
            excelFile = new FileInputStream(filename);
            workbook = new XSSFWorkbook(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void handleAuth(String className, HashMap<String, Boolean> map){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd/MM/yyyy");
        Date date = new Date();
        String ate = formatter.format(date);
        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum()+1; rowIndex++) {
                Row row = CellUtil.getRow(rowIndex, sheet);
                String username = CellUtil.getCell(row, 0).toString();
                if (map.containsKey(username)){
                    Boolean b = map.get(username);
                    int lastRow = row.getLastCellNum()+1;
                    //set date
                    Cell cell = row.createCell(lastRow-1);
                    cell.setCellValue(ate);
                    //set present or not
                    cell = row.createCell(lastRow);
                    cell.setCellValue(b);
                }
            }
        }
        save();
    }

    public static void datePresent(String className){
        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);
            //This data needs to be written (Object[])
            Map<String, Object[]> data = new TreeMap<String, Object[]>();
            data.put("1", new Object[]{"Date", "Present"});
            //Iterate over data and write to sheet
            Set<String> keyset = data.keySet();
            for (String key : keyset) {
                Row row = sheet.getRow(0);
                Object[] objArr = data.get(key);
                short lastCell = row.getLastCellNum();
                int cellnum = lastCell;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    cell.setCellValue((String) (obj));
                }
            }
            save();
        }
    }

    
    public static void addClass(String className) {

        if(check()) {

            XSSFSheet sheet = workbook.createSheet(className);
            //This data needs to be written (Object[])
            Map<String, Object[]> data = new TreeMap<String, Object[]>();
            data.put("1", new Object[]{"Username", "FirstName", "LastName"});
            //Iterate over data and write to sheet
            Set<String> keyset = data.keySet();
            int rownum = 0;
            for (String key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    cell.setCellValue((String) (obj));
                }
            }
            save();
        }
    }

    public static ArrayList<String> getClasses(){
        ArrayList<String> Classes = new ArrayList<>();

        if(check()){
            for(int i = 0; i < workbook.getNumberOfSheets(); i++){
                Classes.add(workbook.getSheetName(i));
            }
        }
        return Classes;
    }

    public static ArrayList<String> getNames(String className) {
        ArrayList<String> names = new ArrayList<>();
        int fNameIndex = 1;
        int lNameIndex = 2;

        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum()+1; rowIndex++) {
                Row row = CellUtil.getRow(rowIndex, sheet);
                String fname = CellUtil.getCell(row, fNameIndex).toString() + "/" + CellUtil.getCell(row, lNameIndex).toString();
                names.add(fname);
            }
        }
        return names;
    }

    public static ArrayList<String> getUserNames(String className) {
        ArrayList<String> names = new ArrayList<>();


        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum()+1; rowIndex++) {
                Row row = CellUtil.getRow(rowIndex, sheet);
                String username = CellUtil.getCell(row, 0).toString();
                names.add(username);
            }
        }
        return names;
    }

    public static void deleteStudent(String className, String studentName){
        String[] nameSlice = studentName.split("/");
        String fName = nameSlice[0];
        String lName = nameSlice[1];

        int fNameIndex = 1;
        int lNameIndex = 2;

        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum()+1; rowIndex++) {
                Row row = CellUtil.getRow(rowIndex, sheet);
                Cell cell = CellUtil.getCell(row, fNameIndex);
                Cell cell1 = CellUtil.getCell(row, lNameIndex);
                if(fName.equals(cell.toString()) && lName.equals(cell1.toString())){
                    sheet.removeRow(row);
                    int lastRowNum = sheet.getLastRowNum()+1;
                    if (rowIndex >= 1 && rowIndex < lastRowNum)
                    {
                        sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
                    }
                }
            }
        }
        save();
    }


    public static void createstudent(String className, String fname, String lname, String userName){

        if (check()) {
            XSSFSheet sheet = workbook.getSheet(className);

            //This data needs to be written (Object[])
            Map<String, Object[]> data = new TreeMap<String, Object[]>();
            data.put("1", new Object[] {userName, fname, lname });

            //Iterate over data and write to sheet
            Set<String> keyset = data.keySet();
            int rownum = sheet.getLastRowNum()+1;
            for (String key : keyset)
            {
                Row row = sheet.createRow(rownum++);
                Object [] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr)
                {
                    Cell cell = row.createCell(cellnum++);
                    cell.setCellValue((String)obj);
                }
            }
        }
        save();
    }

    public static void save(){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("presentia.xlsx written successfully on disk.");
    }

    public static void deleteClass(String SheetName){
        if (check()){
            workbook.removeSheetAt(workbook.getSheetIndex(SheetName));
            save();
        }
    }
    
    public static void exportAttendence(Stage pstage, String className) {

        if(check()) {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save File");
                    File file = fileChooser.showSaveDialog(pstage);
                    FileOutputStream out = new FileOutputStream(file);
                    workbook.write(out);
                    out.close();
                    System.out.println(className + " was written successfully to: " + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
   // public static void main(String[] args) throws FileNotFoundException, IOException {
  //      ExcelWork createExcel = new ExcelWork();
  //      createExcel.addClass("testClass2");
  //  }
}


