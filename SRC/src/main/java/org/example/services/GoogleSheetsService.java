package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.example.GoogleSheetsApplicationInterface;
import org.example.auth.SheetsServiceInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSheetsService implements GoogleSheetsApplicationInterface {
    private Sheets sheetsService;
    private String spreadsheetId;


    /**
     * This is a public constructor for the GoogleSheetsService.
     * The goal is to connect to the specific spreadsheet
     * so that the user can edit/modify/do operations.
     *
     * @param spreadsheetId what specific Google Sheet to connect to.
     * */
    public GoogleSheetsService(String spreadsheetId) throws Exception {
        SheetsServiceInitializer initializer = new SheetsServiceInitializer();
        this.sheetsService = initializer.getSheets();
        this.spreadsheetId = spreadsheetId;

        testConnection();
    }


    /**
     * This connection calls sheetsServiceInitializer to make sure
     * that the connection is still active and successful.
     * If it is, print out the title of the spreadsheet that it connected to!
     * */
    @Override
    public void testConnection() throws Exception {
        try {
            // Attempt to retrieve the spreadsheet using its ID
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();

            // If the spreadsheet is successfully retrieved, print a success message
            System.out.println("Successfully connected to the spreadsheet: " + spreadsheet.getProperties().getTitle());
        } catch (IOException e) {
            // If there's an IOException, it means the connection to the spreadsheet failed
            System.err.println("Failed to connect to the spreadsheet: " + e.getMessage());
            throw new Exception("Failed to test connection to the spreadsheet.", e);
        } catch (Exception e) {
            // Catch any other exceptions that might occur and rethrow them
            System.err.println("An error occurred while testing the connection: " + e.getMessage());
            throw e;
        }
    }


    /**
     * */
    @Override
    public List<String> getAvailableSheets() throws Exception {
        List<String> sheetTitles = new ArrayList<>();
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        spreadsheet.getSheets().forEach(sheet -> sheetTitles.add(sheet.getProperties().getTitle()));
        return sheetTitles;
    }


    /**
     * */
    @Override
    public List<Object> getItemsInSheet(String sheetName) throws Exception {
        List<Object> items = new ArrayList<>();
        // Implement the logic to retrieve rows from the specified sheet and convert them to your object model.
        // This will involve calling the Google Sheets API and parsing the response.
        return items;
    }
}