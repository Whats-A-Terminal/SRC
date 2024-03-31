package org.example;

import java.util.List;

public interface GoogleSheetsApplicationInterface {

    /**
     * This method tests the connection of the server (Google Cloud Compute)(GCC)
     * and the GCC Google Sheets API.
     * If it cannot connect, it throws an error (and from the Main the program will be terminated with an error message).
     * */
    void testConnection() throws Exception;

    /**
     * This method returns a List of Strings that indicate what the available sheets are for modification.
     * This is useful as Spreadsheets typically have multiple sheets for different purposes.
     * For example, in the Farm spreadsheet, one sheet can be dedicated to Crops,
     * while another is dedicated to Animals, etc.
     *
     * @return List<String>availableSheets</String> sheets that are available in the spreadsheet.
     * */
    List<String> getAvailableSheets() throws Exception;


    //Todo: Create method void createNewSheet(String sheetName) throws Exception; that allows the user to CREATE a new Google Sheet sheet.

    //Todo: Create method void deleteSheet(String sheetName) throws Exception; that allows the user to DELETE a specific Google Sheet sheet.

    /**
     * Retrieves items from a specified sheet and returns them as a list of objects.
     * Each object represents a row in the sheet.
     *
     */
    List<Crop> getItemsInSheet(String sheetName) throws Exception;

    /**
     * Adds a new row of data to the end of a specified sheet.
     *
     * @param sheetName The name of the sheet where the data will be added.
     * @param data The data to be added as a new row.
     * @throws Exception If an error occurs during the operation.
     */
    void addDataRow(String sheetName, List<Object> data) throws Exception;

    // Todo: addDataRow(String sheetName, List<Object> data, int cropID), to add a crop in a specific row of the spreadsheet. Push all other crops down and all 1 to cropID.

    /**
     * */
    boolean checkAndDisplayCrop(String sheetName, int cropID, List<Crop> data) throws Exception;

    //Todo: Create method void deleteData (delete row in specific spreadsheet)

    /**
     * */
    void updateCrop(String sheetName, Crop crop) throws Exception;
}