package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.example.Crop;
import org.example.GoogleSheetsApplicationInterface;
import org.example.auth.SheetsServiceInitializer;

import java.io.IOException;
import java.util.*;

public class GoogleSheetsService implements GoogleSheetsApplicationInterface {
    private final Sheets sheetsService;
    private final String spreadsheetId;


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
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getItemsInSheet(String sheetName) throws Exception {
        List<Crop> crops = new ArrayList<>();
        String range = sheetName + "!B3:H";
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                try {
                    // Directly parse and assign values, with proper exception handling
                    String farmName = (String) row.get(0);
                    String farmLocation = (String) row.get(1);
                    int cropID = Integer.parseInt(row.get(2).toString());
                    String cropName = (String) row.get(3);
                    int quantityAvailable = Integer.parseInt(row.get(4).toString());
                    String harvestDate = (String) row.get(5);
                    boolean inSeason = "TRUE".equalsIgnoreCase(row.get(6).toString());

                    crops.add(new Crop(farmName, farmLocation, cropID, cropName, quantityAvailable, harvestDate, inSeason));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing numeric value: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error parsing row: " + e.getMessage());
                }
            }
        }
        return (List<T>) crops;
    }

    @Override
    public void addDataRow(String sheetName, List<Object> data) throws Exception {
        // Specify the range and value input option for appending data
        String range = sheetName; // The name of the sheet to append data
        String valueInputOption = "USER_ENTERED"; // Allows input as if entered by the user, including formulas

        // Prepare the new row to be added
        ValueRange body = new ValueRange().setValues(Arrays.asList(data));

        // Append the data
        sheetsService.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption(valueInputOption)
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();

        System.out.println("Data row successfully added to sheet: " + sheetName);
    }

}