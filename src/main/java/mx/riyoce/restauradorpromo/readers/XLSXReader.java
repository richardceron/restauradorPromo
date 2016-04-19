/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.riyoce.restauradorpromo.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;
import mx.riyoce.restauradorpromo.connections.MySQLConnection;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author admin
 */
public class XLSXReader {

    public void readFile(String path, int option) {
        try {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1); // to get previous year add -1
            Date nextYear = cal.getTime();

            FileInputStream inputStream = new FileInputStream(new File(path));
            Workbook workbook = getWorkbook(inputStream, path);
            Sheet sheet = workbook.getSheetAt(0);
            MySQLConnection conn = new MySQLConnection();
                        
            String images_path = "";

            if (option == 4) {         
                Scanner keyboard = new Scanner(System.in);
                System.out.println("Teclea la rua absoluta de la carpeta principal: ");
                images_path = keyboard.nextLine();
            }

            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                try {
                    System.out.println("Se leerá la fila: " + i);
                    Row row = sheet.getRow(i);

                    String clave_producto = getStringCellValue(row.getCell(0));
                    String nombre_producto = getStringCellValue(row.getCell(1));

                    String nombre_categoria = getStringCellValue(row.getCell(2));
                    String clave_categoria = generateObjectCode(nombre_categoria);

                    String nombre_material = getStringCellValue(row.getCell(3));
                    String clave_material = generateObjectCode(nombre_material);

                    String nombre_color = getStringCellValue(row.getCell(4));
                    String clave_color = generateObjectCode(nombre_color);

                    if (option == 0) {
                        conn.insertObject(clave_categoria, nombre_categoria, option);
                    }

                    if (option == 1) {
                        conn.insertObject(clave_material, nombre_material, option);
                    }

                    if (option == 2) {
                        conn.insertObject(clave_color, nombre_color, option);
                    }

                    if (option == 3) {
                        long cat_id = conn.getObjectId(clave_categoria, 0);
                        long mat_id = conn.getObjectId(clave_material, 1);
                        long color_id = conn.getObjectId(clave_color, 2);

                        conn.insertProducto(clave_producto, nombre_producto, "", nextYear, false, cat_id, mat_id, color_id);
                    }

                    if (option == 4) {
                        long pid = conn.getObjectId(clave_producto, 3);
                        String images_folder_path = images_path + clave_producto;
                        File folder = new File(images_folder_path);
                        File[] listOfFiles = folder.listFiles();

                        for (File listOfFile : listOfFiles) {
                            try {
                                if (listOfFile.isFile()) {
                                    byte[] imagen = getBytes(listOfFile);
                                    String filename = listOfFile.getName();
                                    String mime = getMimeType(listOfFile);
                                    conn.insertImageProducto(imagen, filename, mime, pid);
                                }
                            } catch (NullPointerException e) {
                                System.out.println(e.getMessage());
                                continue;
                            }
                        }
                    }

                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al leer el archivo", e);
        }
    }

    private void insertProducto(Sheet sheet) {
        MySQLConnection conn = new MySQLConnection();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();

        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                System.out.println("Se leerá la fila: " + i);
                Row row = sheet.getRow(i);
                String clave = getStringCellValue(row.getCell(0));
                String name = row.getCell(1).getStringCellValue();

                long cat_id = conn.getObjectId(getStringCellValue(row.getCell(2)), 0);

                /*if (!getStringCellValue(row.getCell(3)).equals("") && (!getStringCellValue(row.getCell(3)).equals("0") && !getStringCellValue(row.getCell(4)).equals("0.0"))) {
                 long mat_id = conn.getObjectId(getStringCellValue(row.getCell(3)), 1);
                 conn.insertProducto(clave, name, "", nextYear, false, cat_id, mat_id);
                 } else {
                 conn.insertProducto(clave, name, "", nextYear, false, cat_id, 0);
                 }*/
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void insertImageProducto(Sheet sheet) {
        MySQLConnection conn = new MySQLConnection();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Teclea la rua absoluta de la carpeta principal: ");
        String path = keyboard.nextLine();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                System.out.println("se leerá la fila: " + i);
                String clave = getStringCellValue(sheet.getRow(i).getCell(0));

                long pid = conn.getObjectId(clave, 3);

                if (pid > 0) {
                    String images_folder_path = path + clave;
                    File folder = new File(images_folder_path);
                    File[] listOfFiles = folder.listFiles();

                    for (File listOfFile : listOfFiles) {
                        try {
                            if (listOfFile.isFile()) {
                                byte[] imagen = getBytes(listOfFile);
                                String filename = listOfFile.getName();
                                String mime = getMimeType(listOfFile);
                                conn.insertImageProducto(imagen, filename, mime, pid);
                            }
                        } catch (NullPointerException e) {
                            System.out.println(e.getMessage());
                            continue;
                        }
                    }
                }

            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void insertObject(Sheet sheet, int option, int cell) {
        MySQLConnection conn = new MySQLConnection();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            try {
                System.out.println("se leerá la fila: " + i);
                Row row = sheet.getRow(i);
                String name = getStringCellValue(row.getCell(cell));
                String clave = generateObjectCode(name);

                if (!clave.equals("")) {
                    conn.insertObject(clave, name, option);
                }

            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Workbook getWorkbook(FileInputStream inputStream, String excelFilePath)
            throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    private String getStringCellValue(Cell cell) {
        try {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();

                case Cell.CELL_TYPE_NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
            }
            return "";
        } catch (NullPointerException e) {
            System.out.println("La celda tiene null");
            return "";
        }
    }

    private String generateObjectCode(String name) {
        if (name != null || !name.equals("")) {
            if (!name.equals("0") && !name.equals("0.0")) {
                String clave = deAccent(name);
                clave = clave.toLowerCase();
                clave = clave.replace(" ", "_");
                return clave;
            }
            return "";
        } else {
            return "";
        }
    }

    public byte[] getBytes(File f) {
        try {
            FileInputStream fileInputStream = null;
            byte[] bFile = new byte[(int) f.length()];
            fileInputStream = new FileInputStream(f);
            fileInputStream.read(bFile);
            fileInputStream.close();
            return bFile;
        } catch (Exception e) {
            System.out.println("Error al traer los bytes del archivo: " + e.getMessage());
            return null;
        }
    }

    public String getMimeType(File f) {
        try {
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            return mimeTypesMap.getContentType(f);
        } catch (Exception e) {
            System.out.println("Error al leer el mime type del archivo: " + e.getMessage());
            return "";
        }
    }

    private String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
