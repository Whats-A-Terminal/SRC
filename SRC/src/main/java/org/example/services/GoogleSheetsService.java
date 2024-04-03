package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.example.GoogleSheetsApplicationInterface;
import org.example.auth.SheetsServiceInitializer;
import org.example.Crop;
import java.io.IOException;
import java.util.*;

public class GoogleSheetsService implements GoogleSheetsApplicationInterface {
    private final Sheets sheetsService;
    private final String spreadsheetId;



    public GoogleSheetsService(String spreadsheetId) throws Exception {
        SheetsServiceInitializer initializer = new SheetsServiceInitializer();
        this.sheetsService = initializer.getSheets();
        this.spreadsheetId = spreadsheetId;
        testConnection();
    }


    @Override
    public void testConnection() throws Exception {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            System.out.println("Successfully connected to the spreadsheet: " + spreadsheet.getProperties().getTitle());
        } catch (IOException e) {
            System.err.println("Failed to connect to the spreadsheet: " + e.getMessage());
            throw new Exception("Failed to test connection to the spreadsheet.", e);
        }
    }


    @Override
    public List<String> getAvailableSheets() throws Exception {
        List<String> sheetTitles = new ArrayList<>();
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        spreadsheet.getSheets().forEach(sheet -> sheetTitles.add(sheet.getProperties().getTitle()));
        return sheetTitles;
    }


    public Map<String, String> getSheetNamesAndIds() throws Exception {
        Map<String, String> sheetInfo = new HashMap<>();
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).setFields("sheets(properties)").execute();
        for (Sheet sheet : spreadsheet.getSheets()) {
            String name = sheet.getProperties().getTitle();
            String id = sheet.getProperties().getSheetId().toString();
            sheetInfo.put(name, id);
        }
        return sheetInfo;
    }


    /**
     * Returns the sheet ID for the given sheet name.
     *
     * @param sheetName The name of the sheet for which the ID is desired.
     * @return The sheet ID corresponding to the given sheet name.
     * @throws Exception If an error occurs during the operation or if the sheet name does not exist.
     */
    public String getSheetIdByName(String sheetName) throws Exception {
        Map<String, String> sheetInfo = getSheetNamesAndIds();
        String sheetID = sheetInfo.get(sheetName);
        if (sheetID == null) {
            throw new Exception("Sheet name '" + sheetName + "' does not exist.");
        }
        return sheetID;
    }

    @Override
    public List<Crop> getItemsInSheet(String sheetName) throws Exception {
        Map<String, String> sheetInfo = getSheetNamesAndIds();
        String sheetID = sheetInfo.getOrDefault(sheetName, "Unknown Sheet ID");

        List<Crop> crops = new ArrayList<>();
        String range = sheetName + "!B3:H"; // Adjust range as needed
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return crops;
        }

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            try {
                // Parse row data into Crop object fields; adjust indices as needed
                String farmName = getStringValue(row, 0);
                String farmLocation = getStringValue(row, 1);
                int cropID = getIntValue(row, 2);
                String cropName = getStringValue(row, 3);
                int quantityAvailable = getIntValue(row, 4);
                String harvestDate = getStringValue(row, 5);
                boolean inSeason = getBooleanValue(row, 6);

                // Include sheetName and sheetID in the Crop constructor
                Crop crop = new Crop(farmName, farmLocation, cropID, cropName, quantityAvailable, harvestDate, inSeason, sheetName, sheetID);
                crops.add(crop);
            } catch (Exception e) {
                System.err.println("Error parsing row " + (i + 2) + ": " + e.getMessage());
                // Handle or log error
            }
        }

        return crops;
    }

    private String getStringValue(List<Object> row, int index) {
        return row.size() > index ? row.get(index).toString() : "";
    }

    private int getIntValue(List<Object> row, int index) {
        if (row.size() <= index || row.get(index) == null || row.get(index).toString().isEmpty()) {
            throw new IllegalArgumentException("Numeric value missing or invalid.");
        }
        try {
            return Integer.parseInt(row.get(index).toString());
        } catch (NumberFormatException e) {
            // Log a warning message instead of throwing an exception
            System.err.println("Warning: Invalid numeric value for Crop ID: " + row.get(index).toString());
            // Return a default value or handle it according to your application's requirements
            return -1; // or any other default value
        }
    }


    private boolean getBooleanValue(List<Object> row, int index) {
        return row.size() > index && "TRUE".equalsIgnoreCase(row.get(index).toString());
    }



    @Override
    public void addDataRow(Crop crop) throws Exception {
        String range = crop.getSheetName() + "!A1";
        List<List<Object>> values = Arrays.asList(
                Arrays.asList(
                        // Assuming a Crop object can provide its data in the correct order
                        crop.getFarmName(), crop.getFarmLocation(), String.valueOf(crop.getCropID()),
                        crop.getCropName(), String.valueOf(crop.getQuantityAvailable()),
                        crop.getHarvestDate(), String.valueOf(crop.isInSeason())
                )
        );

        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values().append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }


    @Override
    public boolean checkAndDisplayCrop(Crop crop) throws Exception {
        // This method would likely change to directly utilize Crop object's attributes
        // For simplicity, assuming that you simply log the crop's details
        System.out.println("Crop ID: " + crop.getCropID() + ", Name: " + crop.getCropName());
        return true; // or determine existence based on some criteria
    }


    @Override
    public void updateCrop(Crop crop) throws Exception {
        // Search for the crop ID in the sheet to find the row number
        String searchRange = crop.getSheetName() + "!D4:D"; // Assuming Crop ID is in column D
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, searchRange).execute();
        List<List<Object>> values = response.getValues();

        boolean found = false;
        int rowIndexToUpdate = -1;

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (!row.isEmpty() && row.get(0) != null && row.get(0).toString().equals(String.valueOf(crop.getCropID()))) {
                    rowIndexToUpdate = i + 4; // Adding 4 because data starts at row 4
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            System.out.println("Crop with ID " + crop.getCropID() + " not found.");
            return;
        }

        // Preparing the list of data to update
        List<Object> rowData = Arrays.asList(
                crop.getFarmName(),
                crop.getFarmLocation(),
                crop.getCropID(), // Assuming cropID is an Integer. No need to convert to String
                crop.getCropName(),
                crop.getQuantityAvailable(), // Assuming quantityAvailable is an Integer
                crop.getHarvestDate(),
                crop.isInSeason() ? "TRUE" : "FALSE"
        );

        // Update range to include the found row index
        String updateRange = crop.getSheetName() + "!B" + rowIndexToUpdate + ":H" + rowIndexToUpdate;
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));

        // Performing the update
        UpdateValuesResponse updateResponse = sheetsService.spreadsheets().values()
                .update(spreadsheetId, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        System.out.println("Updated crop with ID " + crop.getCropID() + ". Rows updated: " + updateResponse.getUpdatedRows());
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }







    @Override
    public void deleteDataRow(Crop crop) throws Exception {
        // Find the row of the crop to be deleted
        String range = crop.getSheetName() + "!B2:H";
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
        int rowToClear = -1;
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                int cropID = Integer.parseInt((String)row.get(2)); // Assuming cropID is at index 2
                if (cropID == crop.getCropID()) {
                    rowToClear = i + 2; // Account for array starting at 0 and header row
                    break;
                }
            }
        }

        if (rowToClear == -1) {
            System.out.println("Crop ID " + crop.getCropID() + " not found.");
            return;
        }

        // Clear the contents of the row
        String clearRange = crop.getSheetName() + "!B" + rowToClear + ":H" + rowToClear;
        ClearValuesRequest clearRequest = new ClearValuesRequest();
        sheetsService.spreadsheets().values().clear(spreadsheetId, clearRange, clearRequest).execute();

        System.out.println("Contents of Crop ID " + crop.getCropID() + " have been cleared.");
    }


}