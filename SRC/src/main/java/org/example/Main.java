package org.example;

import org.example.services.GoogleSheetsService;
import java.util.*;

public class Main {
    private static final String SPREADSHEET_ID = "18ksHaCHNrr6uICxtjAjhN3Zs_YqJMwIywf-eCbcfklc"; // Placeholder for the actual ID
    private static GoogleSheetsApplicationInterface service;
    private static Queue<Crop> changesToRow = new LinkedList<>();
    public static List<Crop> dataRow = new ArrayList<>();
    private static final Scanner input = new Scanner(System.in);

    static {
        try {
            service = new GoogleSheetsService(SPREADSHEET_ID);
            System.out.println("Connected to Google Sheets successfully. Available sheets: " + service.getAvailableSheets());
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Displays the crops currently loaded in the dataRow.
     */
    private static void displayCrops() throws Exception {
        getAndVerifySheets();

        if (dataRow.isEmpty()) {
            System.out.println("\nNo crops found.");
        } else {
            System.out.println("\nList of Crops:");
            for (Crop crop : dataRow) {
                System.out.println("Crop ID: " + crop.getCropID() +
                        ", Crop Name: " + crop.getCropName() +
                        ", Quantity: " + crop.getQuantityAvailable() +
                        ", Harvest Date: " + crop.getHarvestDate() +
                        ", In Season: " + crop.isInSeason() +
                        ", Sheet Name: " + crop.getSheetName());
            }
        }
    }

    /**
     * Allows the user to select and verify the sheet they wish to work with. Updates the dataRow with the data
     * from the selected sheet.
     *
     * @throws Exception if there's an issue fetching the available sheets or items from the sheet.
     */
    private static void getAndVerifySheets() throws Exception {
        System.out.println("\nAvailable sheets:");
        List<String> availableSheets = service.getAvailableSheets();
        availableSheets.forEach(System.out::println);

        System.out.print("Enter the name of the sheet to modify: ");
        String sheetName = input.nextLine().trim();

        if (!availableSheets.contains(sheetName)) {
            System.out.println("Invalid sheet name.");
            return;
        }

        dataRow.clear();
        dataRow.addAll(service.getItemsInSheet(sheetName));
        System.out.println("Selected sheet: " + sheetName + " with " + dataRow.size() + " crops loaded.");
    }

    /**
     * Stages changes to a crop to be added, modified, or deleted. The actual update to the Google Sheet
     * is performed in the pushChanges method.
     *
     * @throws Exception if there's an issue with sheet verification or updating the crop data.
     */
    private static void manageCropData() throws Exception {
        String action = "add"; // Placeholder for user action (add, modify, delete)
        Crop crop = new Crop(); // Assume creation or selection of a Crop object

        // Example for adding a new crop - In actual use, replace with user input for action type and crop details
        if ("add".equals(action)) {
            System.out.println("Adding new crop data...");
            changesToRow.add(crop);
        }
        // Include conditions and logic for modifying and deleting crops as necessary
    }

    /**
     * Pushes all staged changes to the Google Sheet. This method processes each crop in the changesToRow queue,
     * determining whether to add, update, or delete the crop based on its state.
     *
     * @throws Exception if there's an issue updating the Google Sheet.
     */
    private static void pushChanges() throws Exception {
        while (!changesToRow.isEmpty()) {
            Crop crop = changesToRow.poll();
            // Determine the action based on crop state (new, modified, deleted)
            // For simplicity, assuming all changes are updates
            service.updateCrop(crop);
            System.out.println("Crop ID " + crop.getCropID() + " updated.");
        }
    }

    /**
     * Displays the main menu, allowing the user to choose operations like displaying crops, managing crop data, or pushing changes.
     *
     * @throws Exception if any user operation fails.
     */
    private static void mainMenu() throws Exception {
        int choice;
        do {
            System.out.println("\nMain Menu:");
            System.out.println("1: Display Crops");
            System.out.println("2: Manage Crop Data");
            System.out.println("3: Push Changes to Google Sheet");
            System.out.println("0: Exit");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(input.nextLine()); // Using nextLine() to avoid Scanner issues

            switch (choice) {
                case 1:
                    displayCrops();
                    break;
                case 2:
                    manageCropData();
                    break;
                case 3:
                    pushChanges();
                    System.out.println("All changes pushed to the sheet.");
                    break;
                case 0:
                    System.out.println("Exiting application...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 0);
    }

    public static void main(String[] args) throws Exception {
        // Ensure the scanner is closed on application exit
        try {
            mainMenu(); // Initiates the application
        } finally {
            input.close();
        }
    }

}