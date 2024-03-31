package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import org.example.GoogleSheetsApplicationInterface;
import org.example.auth.SheetsServiceInitializer;

import org.example.Crop;

import java.io.IOException;
import java.util.*;

public class GoogleSheetsService implements GoogleSheetsApplicationInterface {
    //
    private final Sheets sheetsService;

    //
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
     * This method gets the available sheets that are in the Google Sheet database.
     * This allows the user to select a specific sheet to modify and be flexible in
     * scaling the database.
     *
     * @return sheetTitles All the sheets available to access in the Google Sheets database.
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
    public List<Crop> getItemsInSheet(String sheetName) throws Exception {

        // Validate that sheetName is valid
        List<String> availableSheets = getAvailableSheets();
        if (!availableSheets.contains(sheetName)) {
            throw new IllegalArgumentException("Sheet name '" + sheetName + "' is not valid.");
        }

        // Assuming sheetName is valid, THEN GET all the data in the sheet.
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
        return crops;
    }


    /**
     * */
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


    /**
     * */
    @Override
    public boolean checkAndDisplayCrop(String sheetName, int cropID, List<Crop> data) throws Exception {
        boolean cropExistsInSheet = false;
        List<Crop> crops = getItemsInSheet(sheetName);
        Crop foundCrop = null;

        for (Crop crop : crops) {
            if (crop.getCropID() == cropID) {
                cropExistsInSheet = true;
                foundCrop = crop;
                break;
            }
        }

        if (!cropExistsInSheet) {
            System.out.println("Crop ID not found in Google Sheets.");
            return false;
        }

        // Assuming data list contains Crop objects, verify if the found crop is in the data list
        boolean cropExistsInData = data.stream().anyMatch(item ->
                item != null && ((Crop)item).getCropID() == cropID);

        if (!cropExistsInData) {
            throw new Exception("Crop ID exists in Google Sheets but not in provided data list.");
        }

        // Displaying information of the found crop
        if (foundCrop != null) {
            System.out.println("Crop Found: ID = " + foundCrop.getCropID() + ", Name = " + foundCrop.getCropName());
            // Further interaction for modification could follow here or be handled elsewhere
            return true;
        }
        return false;
    }

    /***/
    @Override
    public void updateCrop(String sheetName, Crop crop) throws Exception {
        String range = sheetName + "!B3:H"; // Assuming your data starts at B3 and goes to column H
        ValueRange result = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = result.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        // The row number in the sheet where the crop will be updated
        int rowNumberForUpdate = -1;

        // Loop through all rows to find the matching CropID
        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            try {
                // CropID is expected to be in the third column, adjust index accordingly if different
                int cropID = Integer.parseInt(row.get(2).toString());
                if (cropID == crop.getCropID()) {
                    // Row index found, adjust by 3 due to starting from B3 and 0-based index
                    rowNumberForUpdate = i + 3;
                    break;
                }
            } catch (NumberFormatException e) {
                // Log or handle the parsing error
                System.out.println("Error parsing number from the sheet: " + e.getMessage());
            }
        }

        // Check if a matching row was found
        if (rowNumberForUpdate == -1) {
            System.out.println("Crop ID " + crop.getCropID() + " not found in the sheet.");
            return;
        }

        // Prepare the updated data
        List<Object> updatedRowData = Arrays.asList(
                crop.getFarmName(), crop.getFarmLocation(), crop.getCropID(),
                crop.getCropName(), crop.getQuantityAvailable(),
                crop.getHarvestDate(), crop.isInSeason() ? "TRUE" : "FALSE"
        );

        // Define the range to update based on the found row number
        String updateRange = String.format("%s!B%d:H%d", sheetName, rowNumberForUpdate, rowNumberForUpdate);
        ValueRange body = new ValueRange().setValues(Collections.singletonList(updatedRowData));

        // Perform the update operation
        try {
            sheetsService.spreadsheets().values().update(spreadsheetId, updateRange, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            System.out.println("Successfully updated Crop ID " + crop.getCropID() + ".");
        } catch (Exception e) {
            System.err.println("Failed to update the sheet: " + e.getMessage());
            throw e;
        }
    }




}