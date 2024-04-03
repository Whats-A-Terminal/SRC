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
     *
     * @throws Exception if there's an issue fetching data from the spreadsheet.
     */
    private static void displayCrops() throws Exception {
        getAndVerifySheets();

        if (dataRow.isEmpty()) {
            System.out.println("\nNo crops found.");
        } else {
            // Determine the maximum length of each field to ensure proper alignment
            int maxIdLength = "Crop ID".length();
            int maxNameLength = "Crop Name".length();
            int maxQuantityLength = "Quantity".length();
            int maxHarvestDateLength = "Harvest Date".length();
            int maxSeasonLength = "In Season".length();
            int maxSheetNameLength = "Sheet Name".length();

            for (Crop crop : dataRow) {
                maxIdLength = Math.max(maxIdLength, String.valueOf(crop.getCropID()).length());
                maxNameLength = Math.max(maxNameLength, crop.getCropName().length());
                maxQuantityLength = Math.max(maxQuantityLength, String.valueOf(crop.getQuantityAvailable()).length());
                maxHarvestDateLength = Math.max(maxHarvestDateLength, crop.getHarvestDate().toString().length());
                maxSeasonLength = Math.max(maxSeasonLength, String.valueOf(crop.isInSeason()).length());
                maxSheetNameLength = Math.max(maxSheetNameLength, crop.getSheetName().length());
            }

            // Print header
            System.out.printf("\n%-" + maxIdLength + "s  %-"
                            + maxNameLength + "s  %-"
                            + maxQuantityLength + "s  %-"
                            + maxHarvestDateLength + "s  %-"
                            + maxSeasonLength + "s  %-"
                            + maxSheetNameLength + "s%n",
                    "Crop ID", "Crop Name", "Quantity", "Harvest Date", "In Season", "Sheet Name");

            // Print a line under the header for better visual separation
            for (int i = 0; i < maxIdLength + maxNameLength + maxQuantityLength + maxHarvestDateLength + maxSeasonLength + maxSheetNameLength + 10; i++) {
                System.out.print("-");
            }
            System.out.println();

            // Print each row
            for (Crop crop : dataRow) {
                System.out.printf("%-" + maxIdLength + "d  %-"
                                + maxNameLength + "s  %-"
                                + maxQuantityLength + "d  %-"
                                + maxHarvestDateLength + "s  %-"
                                + maxSeasonLength + "s  %-"
                                + maxSheetNameLength + "s%n",
                        crop.getCropID(),
                        crop.getCropName(),
                        crop.getQuantityAvailable(),
                        crop.getHarvestDate(),
                        crop.isInSeason() ? "Yes" : "No",
                        crop.getSheetName());
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

        // After user is done choosing a sheet, clear everything in the dataRow
        // to prepare array for loading database.
        dataRow.clear();

        // Add all data from database into local array.
        dataRow.addAll(service.getItemsInSheet(sheetName));

        // Print message for user.
        System.out.println("Selected sheet: " + sheetName + " with " + dataRow.size() + " crops loaded.");
    }


    /** Prompts the user for a string and validates that it's not empty
     *
     * @param message
     * @return
     */
    private static String promptForString(String message) {
        String inputStr;
        do {
            System.out.print(message);
            inputStr = input.nextLine().trim();
            if (inputStr.isEmpty()) {
                System.out.println("This field cannot be empty. Please try again.");
            }
        } while (inputStr.isEmpty());
        return inputStr;
    }


    /*** Prompts the user for an integer
     *
     * @param message
     * @return
     */
    private static int promptForInt(String message) {
        System.out.print(message);
        while (!input.hasNextInt()) {
            System.out.println("That's not a number. Please enter a number.");
            System.out.print(message);
            input.next(); // Consume the invalid input
        }
        int number = input.nextInt();
        input.nextLine(); // Consume newline left-over
        return number;
    }


    /*** Prompts the user for a boolean
     *
     * @param message
     * @return
     */
    private static boolean promptForBoolean(String message) {
        System.out.print(message + " (yes/no): ");
        String response;
        boolean result;
        do {
            response = input.nextLine().trim().toLowerCase();
            result = "yes".equals(response) || "no".equals(response);
            if (!result) {
                System.out.print("\nPlease respond with 'yes' or 'no': ");
            }
        } while (!result);
        return "yes".equals(response);
    }


    /*** Checks if a cropID exists in the dataRow
     *
     * @param cropID
     * @return
     */
    private static boolean cropIDExists(int cropID) {
        for (Crop crop : dataRow) {
            if (crop.getCropID() == cropID) {
                return true;
            }
        }
        return false;
    }

    /*** Finds and returns a Crop object by its ID, or null if not found
     *
     * @param cropID
     * @return
     */
    private static Crop findCropByID(int cropID) {
        for (Crop crop : dataRow) {
            if (crop.getCropID() == cropID) {
                return crop;
            }
        }
        return null;
    }



    /**
     * */
    private static void addNewCrop() {
        System.out.println("\nAdding a new crop.");

        String farmName = promptForString("\nEnter farm name: ");
        String farmLocation = promptForString("Enter farm location: ");

        int cropID;

        do {
            cropID = promptForInt("Enter crop ID: ");
            if (cropIDExists(cropID)) {
                System.out.println("A crop with this ID already exists. Please enter a unique crop ID.\n");
            }
        } while (cropIDExists(cropID));

        String cropName = promptForString("Enter crop name: ");
        int quantityAvailable = promptForInt("Enter quantity available: ");
        String harvestDate = promptForString("Enter harvest date (YYYY-MM-DD): ");
        boolean inSeason = promptForBoolean("Is the crop in season?");

        // TODO: GET SHEETNAME AND SHEETID FROM SERVICE AS USER SHOULDN'T KNOW HOW THIS WORKS!!!
        String sheetName = "";
        String sheetID = "";

        Crop newCrop = new Crop(farmName, farmLocation, cropID, cropName, quantityAvailable, harvestDate, inSeason, sheetName, sheetID);
        changesToRow.add(newCrop);
        System.out.println("New crop added and staged for changes.");
    }


    /**
     * */
    private static void displaySingleCrop(Crop crop) {
        // Header
        System.out.printf("%-10s %-20s %-15s %-15s %-10s%n", "Crop ID", "Crop Name", "Quantity", "Harvest Date", "In Season");
        // Crop data
        System.out.printf("%-10d %-20s %-15d %-15s %-10s%n",
                crop.getCropID(), crop.getCropName(), crop.getQuantityAvailable(), crop.getHarvestDate(), crop.isInSeason() ? "Yes" : "No");
    }


    /**
     * */
    private static void modifyCrop() {
        System.out.println("\nModifying an existing crop.");
        int cropID = promptForInt("Enter the crop ID of the crop you wish to modify: ");
        Crop cropToModify = findCropByID(cropID);

        if (cropToModify == null) {
            System.out.println("No crop with the specified ID found.");
            return;
        }

        // Display the selected crop in a table-like format
        System.out.println("\nSelected Crop:");
        displaySingleCrop(cropToModify);

        boolean finished = false;
        while (!finished) {
            System.out.println("\nOptions:");
            System.out.println("1: Modify Crop Name");
            System.out.println("2: Update Crop Quantity");
            System.out.println("3: Update Harvest Date");
            System.out.println("4: Update Crop Season");
            System.out.println("5: Finish modifying crop");

            int choice = promptForInt("Select an option: ");
            switch (choice) {
                case 1:
                    cropToModify.setCropName(promptForString("Enter new crop name: "));
                    break;
                case 2:
                    cropToModify.setQuantityAvailable(promptForInt("Enter new quantity available: "));
                    break;
                case 3:
                    cropToModify.setHarvestDate(promptForString("Enter new harvest date (YYYY-MM-DD): "));
                    break;
                case 4:
                    cropToModify.setInSeason(promptForBoolean("Is the crop in season? (yes/no): "));
                    break;
                case 5:
                    finished = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            if (!finished) {
                // Display the updated crop
                System.out.println("\nUpdated Crop:");
                displaySingleCrop(cropToModify);
            }
        }

        changesToRow.add(cropToModify); // Stage changes
        System.out.println("Crop modifications staged for changes.");
    }



    /**
     * */
    private static void deleteCrop() {
        System.out.println("Deleting a crop.");

        int cropID = promptForInt("Enter the crop ID of the crop you wish to delete: ");

        Crop cropToDelete = findCropByID(cropID);

        if (cropToDelete == null) {
            System.out.println("No crop with the specified ID found.");
            return;
        }

        // Here, we're assuming deletion means removing from dataRow
        // You might need to adjust based on your application's needs
        dataRow.remove(cropToDelete);
        // If you have a different method of marking a crop as deleted for changesToRow, do that here

        System.out.println("Crop deleted and changes staged.");
    }




    /**
     * Stages changes to a crop to be added, modified, or deleted. The actual update to the Google Sheet
     * is performed in the pushChanges method.
     *
     * @throws Exception if there's an issue with sheet verification or updating the crop data.
     */
    private static void manageCropData() throws Exception {
        getAndVerifySheets(); // Select sheet to work from.

        System.out.print("\nWhat would you like to do? (add, modify, delete): ");
        String action = input.nextLine().trim().toLowerCase();

        switch (action) {
            case "add":
                addNewCrop();
                break;
            case "modify":
                modifyCrop();
                break;
            case "delete":
                deleteCrop();
                break;
            default:
                System.out.println("Invalid action. Please choose 'add', 'modify', or 'delete'.");
                break;
        }
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