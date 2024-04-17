package org.example.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.text.SimpleDateFormat;

import org.example.Crop;

public class ExcelExporter {

    /**
     * Converts a list of Crop objects into an XLSX file, using primitive data types for all fields.
     * If the first row of data matches header names, it is considered a duplicate header and not included in the XLSX.
     *
     * @param crops the list of Crop objects to be converted into an XLSX file.
     *              Each Crop object represents a single row of data in the resulting Excel sheet.
     * @throws Exception if an error occurs during file creation, such as file access issues or errors writing to the file.
     */
    public static void convertToXLSX(List<Crop> crops) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Detailed Crops Data");

        // Define headers
        String[] headers = {"Farm Name", "Farm Location", "Crop ID", "Crop Name", "Quantity Available", "Harvest Date (MM-DD-YYYY)", "In Season"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int h = 0; h < headers.length; h++) {
            headerRow.createCell(h).setCellValue(headers[h]);
        }

        // Iterate over each Crop object in the list, starting from the first data row
        int rowIndex = 1;
        for (Crop crop : crops) {
            // Check for duplicate headers in the first data row
            if (rowIndex == 1 && isHeaderRow(crop, headers)) {
                continue; // Skip this row and don't add it to the XLSX
            }

            XSSFRow row = sheet.createRow(rowIndex++);

            // Populate cells with properties from each Crop object
            row.createCell(0).setCellValue(crop.getFarmName());
            row.createCell(1).setCellValue(crop.getFarmLocation());
            row.createCell(2).setCellValue(Integer.toString(crop.getCropID())); // Convert int to String
            row.createCell(3).setCellValue(crop.getCropName());
            row.createCell(4).setCellValue(Integer.toString(crop.getQuantityAvailable())); // Convert int to String
            row.createCell(5).setCellValue(crop.getHarvestDate()); // Directly use the String
            row.createCell(6).setCellValue(crop.isInSeason() ? "Yes" : "No");
        }

        // Writing the workbook to a file
        FileOutputStream out = new FileOutputStream(new File("DetailedCropsData.xlsx"));
        workbook.write(out);
        out.close();

        System.out.println("XLSX file has been created successfully!");
    }

    /**
     * Checks if the given crop object has property values that match the header names.
     *
     * @param crop The crop object to check.
     * @param headers The array of header names to check against.
     * @return true if the crop object represents a header row, false otherwise.
     */
    private static boolean isHeaderRow(Crop crop, String[] headers) {
        return crop.getFarmName().equals(headers[0]) &&
                crop.getFarmLocation().equals(headers[1]) &&
                crop.getCropName().equals(headers[3]);
    }
}
