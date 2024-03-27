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


    // Methods to add later...

    //createNewSheet

    //deleteSheet

    /**
     * Retrieves items from a specified sheet and returns them as a list of objects.
     * Each object represents a row in the sheet.
     *
     * @param sheetName The name of the sheet to retrieve items from.
     * @return A list of objects, each representing a row in the specified sheet.
     * @throws Exception If an error occurs during the operation.
     */
    List<Object> getItemsInSheet(String sheetName) throws Exception;

    //addData (create a new row in a specific sheet as a parameter)(maybe overload method where it can add data to specific row or to the bottom row of the sheet?)

    //deleteData (delete row in specific spreadsheet)

    //modifyData (modify the contents of a specific row)
}