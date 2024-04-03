package org.example;

import java.util.List;

public interface GoogleSheetsApplicationInterface {

    /**
     * Tests the connection to the Google Sheets API.
     * Throws an exception if the connection cannot be established.
     * @throws Exception if unable to connect to the Google Sheets API.
     */
    void testConnection() throws Exception;

    /**
     * Retrieves a list of available sheet names in the spreadsheet.
     * @return A List of Strings representing the names of the sheets available.
     * @throws Exception if an error occurs during the operation.
     */
    List<String> getAvailableSheets() throws Exception;

    /**
     * Retrieves a list of Crop objects from a specified sheet.
     * @param sheetName The name of the sheet to retrieve items from.
     * @return A List of Crop objects representing each row in the specified sheet.
     * @throws Exception if an error occurs during the operation.
     */
    List<Crop> getItemsInSheet(String sheetName) throws Exception;

    /**
     * Adds a new Crop object as a row to the end of the specified sheet.
     * @param crop The Crop object to be added as a new row.
     * @throws Exception if an error occurs during the operation.
     */
    void addDataRow(Crop crop) throws Exception;

    /**
     * Checks the existence of a Crop by its ID and displays its information.
     * @param crop The Crop object to check and display.
     * @return true if the crop exists in the sheet, false otherwise.
     * @throws Exception if an error occurs during the operation.
     */
    boolean checkAndDisplayCrop(Crop crop) throws Exception;

    /**
     * Updates an existing Crop object's corresponding row in its sheet.
     * @param crop The Crop object to update.
     * @throws Exception if an error occurs during the operation or if the Crop ID is not found.
     */
    void updateCrop(Crop crop) throws Exception;

    /**
     * Clears the data for a given Crop object's corresponding row in its sheet.
     * Note: This operation does not delete the row to maintain the integrity of the sheet's structure.
     * @param crop The Crop object to clear data for.
     * @throws Exception if an error occurs during the operation or if the Crop ID is not found.
     */
    void deleteDataRow(Crop crop) throws Exception;

    String getSheetIdByName(String sheetName) throws Exception;

    // Optional or future implementations:
    // void createNewSheet(String sheetName) throws Exception;
    // void deleteSheet(String sheetName) throws Exception;
}
