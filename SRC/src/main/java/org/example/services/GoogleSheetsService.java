package org.example.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.example.GoogleSheetsApplicationInterface;
import org.example.auth.SheetsServiceInitializer;
import org.example.Crop;
import java.io.IOException;
import java.util.*;

/**
 * Provides services for interacting with Google Sheets, specifically tailored for managing crop data.
 * This class implements methods defined in the GoogleSheetsApplicationInterface to perform operations
 * such as reading from and writing to a specific Google Sheet, which contains crop information.
 */
public class GoogleSheetsService implements GoogleSheetsApplicationInterface {
    private final Sheets sheetsService; // The Sheets API service to perform operations with Google Sheets.
    private final String spreadsheetId; // The ID of the spreadsheet to interact with.
    // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!

    /**
     * Constructs a GoogleSheetsService object for interacting with the specified Google Sheet.
     * This constructor initializes the Sheets service and sets the spreadsheet ID for future operations.
     *
     * @param spreadsheetId The ID of the Google Sheet to interact with.
     * @throws IOException If an error occurs during the initialization of the Sheets service.
     */
    public GoogleSheetsService(String spreadsheetId) throws Exception {
        SheetsServiceInitializer initializer = new SheetsServiceInitializer();
        this.sheetsService = initializer.getSheets();
        this.spreadsheetId = spreadsheetId;
        testConnection();
    }


    /**
     * Attempts to establish a connection to a specified Google Spreadsheet.
     * Useful for testing if the Sheets service has been initialized correctly and can access spreadsheets.
     *
     * @throws Exception if there are issues creating the Sheets service instance.
     */
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


    /**
     * Retrieves a list of titles for all available sheets within the specified Google Spreadsheet.
     * This method calls the Google Sheets API to fetch the spreadsheet by its ID and iterates
     * over each sheet within the spreadsheet to collect their titles. The list of titles is
     * then returned to the caller.
     *
     * @return A List of Strings, each representing the title of a sheet within the spreadsheet.
     * @throws Exception If there's an error communicating with the Google Sheets API, such as
     *                   network issues, access permissions, or if the spreadsheet ID is invalid.
     */
    @Override
    public List<String> getAvailableSheets() throws Exception {
        List<String> sheetTitles = new ArrayList<>();
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        spreadsheet.getSheets().forEach(sheet -> sheetTitles.add(sheet.getProperties().getTitle()));
        return sheetTitles;
    }


    /**
     * Retrieves a mapping of sheet names to their corresponding IDs within the specified Google Spreadsheet.
     * This method is useful for operations that require both the name and ID of sheets, such as
     * selecting a specific sheet for data manipulation or querying. It fetches the spreadsheet by its ID,
     * iterates over each sheet to extract its title and ID, and stores this information in a map
     * where each key is a sheet's name and each value is the sheet's ID.
     *
     * @return A Map where each key is a String representing a sheet's name, and each value is a String
     *         representing the sheet's ID. This provides a convenient lookup for sheet IDs based on their names.
     * @throws Exception If there's an issue accessing the spreadsheet or its sheets' properties, such as
     *                   when the spreadsheet ID is incorrect, the fields parameter is misconfigured, or
     *                   due to network problems or API access errors.
     */
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


    /**
     * Retrieves a list of {@link Crop} objects from the specified sheet within the Google Spreadsheet.
     * This method queries a specific range in the given sheet name and parses each row of values
     * into Crop objects, encapsulating the data for each crop. It leverages helper methods to extract
     * and convert row values to the appropriate types.
     *
     * @param sheetName The name of the sheet from which to retrieve crop data.
     * @return A List of Crop objects, each representing a row from the specified sheet.
     * @throws Exception If there's an error retrieving data from the sheet or parsing the row data.
     */
    @Override
    public List<Crop> getItemsInSheet(String sheetName) throws Exception {
        Map<String, String> sheetInfo = getSheetNamesAndIds();
        String sheetID = sheetInfo.getOrDefault(sheetName, "Unknown Sheet ID");

        List<Crop> crops = new ArrayList<>();
        String range = sheetName + "!B3:H"; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
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


    /**
     * Extracts a String value from a specified index in a row of data. If the index is out of bounds,
     * or if the value at the index is null, this method returns an empty string.
     *
     * @param row The row of data from which to extract the value.
     * @param index The index within the row from which to extract the String value.
     * @return The extracted String value, or an empty string if the index is out of bounds or the value is null.
     */
    private String getStringValue(List<Object> row, int index) {
        return row.size() > index ? row.get(index).toString() : "";
    }


    /**
     * Extracts an integer value from a specified index in a row of data. This method validates the presence
     * and format of the numeric value, throwing an IllegalArgumentException if the value is missing or invalid.
     * Additionally, logs a warning message for invalid numeric values rather than throwing an exception.
     *
     * @param row The row of data from which to extract the numeric value.
     * @param index The index within the row from which to extract the integer value.
     * @return The extracted integer value. Returns -1 or any other default value as a fallback for invalid inputs.
     * @throws IllegalArgumentException If the numeric value at the specified index is missing, null, or not a valid integer.
     */
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


    /**
     * Determines the boolean value of an entry in a row based on its textual representation.
     * This method checks if the value at the specified index in the row is equivalent to "TRUE"
     * (ignoring case), indicating a boolean true value; otherwise, it returns false.
     *
     * @param row The row of data from which to extract the boolean value.
     * @param index The index within the row from which to check for a boolean value.
     * @return True if the value at the specified index is "TRUE" (ignoring case), false otherwise.
     */
    private boolean getBooleanValue(List<Object> row, int index) {
        return row.size() > index && "TRUE".equalsIgnoreCase(row.get(index).toString());
    }


    /**
     * Adds a new row to the spreadsheet for the provided {@link Crop} object. This method first
     * identifies the first empty row within a specified column to ensure that the new data is added
     * at the correct location. It then prepares and inserts a new row containing the crop's details
     * into the spreadsheet.
     *
     * @param crop The {@link Crop} object containing the data to be added to the spreadsheet.
     * @throws Exception If an error occurs during communication with the Google Sheets API or
     *                   if the data preparation or insertion process fails.
     */
    @Override
    public void addDataRow(Crop crop) throws Exception {
        // First, find the first empty row in column D starting from row 4
        String rangeToSearch = crop.getSheetName() + "!D4:D"; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, rangeToSearch).execute();
        List<List<Object>> values = response.getValues();

        // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!

        int firstEmptyRow = 4; // Start searching from row 4
        if (values != null) {
            for (List<Object> row : values) {
                if (row.isEmpty() || row.get(0) == null || row.get(0).toString().trim().isEmpty()) {
                    break; // Found the first empty row
                }
                firstEmptyRow++;
            }
        }

        // Now, prepare the data to insert for the new crop
        List<List<Object>> dataToAdd = Arrays.asList(
                Arrays.asList(
                        crop.getFarmName(), // Column B
                        crop.getFarmLocation(), // Column C
                        String.valueOf(crop.getCropID()), // Column D
                        crop.getCropName(), // Column E
                        String.valueOf(crop.getQuantityAvailable()), // Column F
                        crop.getHarvestDate(), // Column G
                        crop.isInSeason() ? "TRUE" : "FALSE" // Column H
                )
        );

        // Set the range to insert the new data starting from the first empty row found
        String updateRange = crop.getSheetName() + "!B" + firstEmptyRow + ":H" + firstEmptyRow; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
        ValueRange body = new ValueRange().setValues(dataToAdd);

        // Insert the new crop data into the sheet
        UpdateValuesResponse result = sheetsService.spreadsheets().values().update(spreadsheetId, updateRange, body)
                .setValueInputOption("USER_ENTERED").execute();

        System.out.println("Added new crop data at row " + firstEmptyRow + ". Rows updated: " + result.getUpdatedRows());
    }


    /**
     * Logs the details of a given {@link Crop} object to the console. This method is a placeholder for
     * potentially more complex operations, such as checking the existence of the crop in the sheet
     * and displaying its details. Currently, it simply prints the crop's ID and name.
     *
     * @param crop The {@link Crop} object whose details are to be logged.
     * @return Always returns true in this placeholder implementation. In practice, could return
     *         a boolean indicating whether the crop exists in the spreadsheet.
     * @throws Exception If an issue arises in accessing or processing the crop's data.
     */
    @Override
    public boolean checkAndDisplayCrop(Crop crop) throws Exception {
        // This method would likely change to directly utilize Crop object's attributes
        // For simplicity, assuming that you simply log the crop's details
        System.out.println("Crop ID: " + crop.getCropID() + ", Name: " + crop.getCropName());
        return true; // or determine existence based on some criteria
    }


    /**
     * Updates the data row in the spreadsheet that corresponds to the specified {@link Crop}.
     * This method searches for the crop's ID within a designated column to find the row that needs updating.
     * Once found, it updates the row with the crop's current data. If the crop ID is not found,
     * it logs a message indicating the crop was not found.
     *
     * @param crop The {@link Crop} object containing updated data for the row.
     * @throws Exception If there's an error during the operation, such as issues with accessing the spreadsheet or updating the data.
     */
    @Override
    public void updateDataRow(Crop crop) throws Exception {

        // Search for the crop ID in the sheet to find the row number
        // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
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
        String updateRange = crop.getSheetName() + "!B" + rowIndexToUpdate + ":H" + rowIndexToUpdate; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));

        // Performing the update
        UpdateValuesResponse updateResponse = sheetsService.spreadsheets().values()
                .update(spreadsheetId, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        System.out.println("Updated crop with ID " + crop.getCropID() + ". Rows updated: " + updateResponse.getUpdatedRows());
    }


    /**
     * Determines if the provided string represents a numeric value.
     * This helper method checks if a string can be parsed as an integer, indicating it's numeric.
     *
     * @param strNum The string to check for numeric representation.
     * @return True if the string is numeric (i.e., can be parsed as an integer), false otherwise.
     */
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


    /**
     * Deletes the data row from the spreadsheet that corresponds to the specified {@link Crop}.
     * Similar to updateDataRow, this method searches for the crop's ID within a designated column to find the specific row.
     * Once the row is identified, it clears the row's contents from the sheet. If the crop ID is not found,
     * it logs a message indicating the crop was not found.
     *
     * @param crop The {@link Crop} object whose data row is to be deleted from the spreadsheet.
     * @throws Exception If there's an error during the operation, such as issues with accessing the spreadsheet or clearing the data.
     */
    @Override
    public void deleteDataRow(Crop crop) throws Exception {
        // Assuming Crop ID is in column D, starting from row 4
        String searchRange = crop.getSheetName() + "!D4:D"; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
        ValueRange response = sheetsService.spreadsheets().values().get(spreadsheetId, searchRange).execute();
        List<List<Object>> values = response.getValues();

        int rowIndexToDelete = -1;
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                List<Object> row = values.get(i);
                if (!row.isEmpty() && row.get(0) != null && row.get(0).toString().equals(String.valueOf(crop.getCropID()))) {
                    rowIndexToDelete = i + 4; // Rows start at 1, and header is at row 3
                    break;
                }
            }
        }

        if (rowIndexToDelete == -1) {
            System.out.println("Crop with ID " + crop.getCropID() + " not found.");
            return;
        }

        // Clear the contents of the found row in the sheet
        String clearRange = crop.getSheetName() + "!B" + rowIndexToDelete + ":H" + rowIndexToDelete; // TODO: HAVE DIFFERENT METHODS AND CONSTANT VARIABLES SO THAT THE USER CAN CHANGE WHERE A ROW STARTS AND ENDS!!!
        sheetsService.spreadsheets().values().clear(spreadsheetId, clearRange, new ClearValuesRequest()).execute();
        System.out.println("Crop with ID " + crop.getCropID() + " has been cleared from the sheet.");
    }

}