package org.example;

import org.example.services.GoogleSheetsService;
import java.util.*;

public class Main {
    private static final String SPREADSHEET_ID = "18ksHaCHNrr6uICxtjAjhN3Zs_YqJMwIywf-eCbcfklc"; // The ID of the Google Sheets database.
    private static GoogleSheetsApplicationInterface service; // Used to contact the Google Sheets API and make requests via the user.
    private static Queue<Crop> changesToRow = new LinkedList<>(); // Used to stage the changes from the user to push onto the Google Sheets database.
    public static List<Crop> dataRow = new ArrayList<>(); // Used to hold the contents of a specific sheet (within the Google Sheets database). Note: each object is a row in said sheet.
    private static final Scanner input = new Scanner(System.in); // Global scanner for user input.

    // Attempt to initiate GoogleSheetsService to use in the program.
    // If it can't, exit the program as everything in this project requires
    // GoogleSheetsService to work properly.
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
     * Allows the user to select and verify the sheet they wish to work with. Updates the dataRow with the data
     * from the selected sheet.
     *
     * @return The name of the sheet selected by the user.
     * @throws Exception if there's an issue fetching the available sheets or items from the sheet.
     */
    private static String getAndVerifySheets() throws Exception {
        System.out.println("\nAvailable sheets:");

        // Find and print sheets available in the Google Sheets database.
        List<String> availableSheets = service.getAvailableSheets();
        availableSheets.forEach(System.out::println);

        // Verify user input.
        String sheetName = "";
        boolean validSheetSelected = false;
        while (!validSheetSelected) {
            System.out.print("Enter the name of the sheet to modify: ");
            sheetName = input.nextLine().trim();

            if (!availableSheets.contains(sheetName)) {
                System.out.println("Invalid sheet name. Please try again.");
            } else {
                validSheetSelected = true; // A valid sheet name has been entered; exit the loop.
            }
        }

        // Fetch the sheetID for the selected sheetName.
        String sheetID = service.getSheetIdByName(sheetName);
        if (sheetID == null) {
            System.out.println("Could not find an ID for the sheet named: " + sheetName);
            // TODO: Consider how you want to handle this unlikely case. For now, return the sheetName anyway.
        }

        // Clear the dataRow and load crops from the selected sheet.
        dataRow.clear(); // So we don't have duplicate items everytime the user selects a sheet.
        List<Crop> crops = service.getItemsInSheet(sheetName);
        String finalSheetName = sheetName;
        crops.forEach(crop -> {
            crop.setSheetName(finalSheetName);
            crop.setSheetID(sheetID);
        });
        dataRow.addAll(crops);

        System.out.println("Selected sheet: " + sheetName + " with " + dataRow.size() + " crops loaded and sheet ID: " + sheetID);

        return sheetName;
    }


    /**
     * Displays the crops currently loaded in the dataRow.
     * This method retrieves crop data from a Google Sheet and formats it for console display. It calculates
     * the maximum length of various fields such as Crop ID, Crop Name, Quantity, Harvest Date, In Season,
     * and Sheet Name to align the data neatly. If no crops are available, it informs the user.
     *
     * @throws Exception if there's an issue fetching data from the spreadsheet, indicating a problem with
     *                   the Google Sheets API connection or data retrieval process.
     */
    private static void displayCrops() throws Exception {
        // Find and select what sheet to get data from.
        getAndVerifySheets();

        if (dataRow.isEmpty()) {
            System.out.println("\nNo crops found.");
        }
        else {
            // Determine the maximum length of each field to ensure proper alignment (make the output look like a table).
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

            // Print header of 'table'.
            System.out.printf("\n%-" + maxIdLength + "s  %-"
                            + maxNameLength + "s  %-"
                            + maxQuantityLength + "s  %-"
                            + maxHarvestDateLength + "s  %-"
                            + maxSeasonLength + "s  %-"
                            + maxSheetNameLength + "s%n",
                    "Crop ID", "Crop Name", "Quantity", "Harvest Date", "In Season", "Sheet Name");

            // Print a line under the header for better visual separation.
            for (int i = 0; i < maxIdLength + maxNameLength + maxQuantityLength + maxHarvestDateLength + maxSeasonLength + maxSheetNameLength + 10; i++) {
                System.out.print("-");
            }
            System.out.println();

            // Print each row.
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
     * This is a helper function that prompts the user for a string
     * and validates that it's not empty.
     *
     * @param message the string that the user has input (for validation).
     * @return inputStr a string that is NOT empty.
     */
    private static String promptForString(String message) {
        String inputStr;
        do {
            System.out.print(message);
            inputStr = input.nextLine().trim(); // Remove all whitespace from the string.
            if (inputStr.isEmpty()) {
                System.out.println("This field cannot be empty. Please try again.");
            }
        } while (inputStr.isEmpty());
        return inputStr;
    }


    /**
     * This is a helper function that prompts the user for an integer
     * and ensures that it's not something else (such as a string or boolean).
     *
     * @param message the user input to be validated.
     * @return number a user entered, validated integer.
     */
    private static int promptForInt(String message) {
        System.out.print(message);
        while (!input.hasNextInt()) {
            System.out.println("That's not a number. Please enter a number.");
            System.out.print(message);
            input.next(); // Consume the invalid input.
        }
        int number = input.nextInt();
        input.nextLine(); // Consume newline left-over.
        return number;
    }


    /**
     * This is a helper function that prompts the user for a boolean value.
     *
     * @param message the user input to be validated.
     * @return response a validated user entered boolean value.
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


    /**
     * This is a helper function that checks if a cropID exists in the dataRow variable.
     *
     * @param cropID the ID that the user wants to search for.
     * @return a boolean value indicating to the user whether the crop exists in dataRow.
     */
    private static boolean cropIDExists(int cropID) {
        for (Crop crop : dataRow) {
            if (crop.getCropID() == cropID) {
                return true;
            }
        }
        return false;
    }


    /**
     * This is a helper function that finds and returns
     * a Crop object by its ID, or null if not found.
     *
     * @param cropID the ID that the user wants to search for.
     * @return Crop or Null depending on whether the Crop was found via the cropID.
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
     * Prompts the user to add a new crop by entering details such as farm name, farm location, and crop ID.
     * This method guides the user through the process of adding a new crop to a specified Google Sheets sheet.
     * It ensures that each new crop has a unique ID by checking the existence of the provided crop ID against
     * current entries. If the entered crop ID already exists, the user is prompted to enter a unique crop ID.
     *
     * @param sheetName The name of the sheet within the Google Sheets document where the new crop details
     *                  will be added. This parameter specifies the target sheet for the operation.
     * @throws Exception If any error occurs during the process of adding a new crop. This includes issues
     *                   with user input, problems with accessing or modifying the Google Sheets document,
     *                   or any other errors that might occur during the execution of this method.
     */
    private static void addNewCrop(String sheetName) throws Exception {
        System.out.println("\nAdding a new crop.");

        // Process for creating new Crop object.
        String farmName = promptForString("\nEnter farm name: ");
        String farmLocation = promptForString("Enter farm location: ");

        int cropID; // The ID used to identify this crop.

        do {
            cropID = promptForInt("Enter crop ID: ");
            if (cropIDExists(cropID)) {
                System.out.println("A crop with this ID already exists. Please enter a unique crop ID.\n");
            }
        } while (cropIDExists(cropID));

        String cropName = promptForString("Enter crop name: ");
        int quantityAvailable = promptForInt("Enter quantity available: ");
        String harvestDate = promptForString("Enter harvest date (YYYY-MM-DD): "); // TODO: VALIDATE HARVEST DATE TO ENSURE COMPLIANCE WITH GOOGLE SHEETS API V4.
        boolean inSeason = promptForBoolean("Is the crop in season?");

        // TODO: GET SHEETNAME AND SHEETID FROM SERVICE AS USER SHOULDN'T KNOW HOW THIS WORKS!!!
        String sheetID = service.getSheetIdByName(sheetName);

        // Create new Crop object (to stage changes for dataRow and changesToRow).
        Crop newCrop = new Crop(farmName, farmLocation, cropID, cropName, quantityAvailable, harvestDate, inSeason, sheetName, sheetID);
        newCrop.setCropChanges(1); // Set flag to let changesToRow know that this is a new addition to database.
        changesToRow.add(newCrop);

        System.out.println("New crop added and staged for changes.");
    }


    /**
     * Displays detailed information for a single crop in a tabular format. This method prints out
     * the crop's ID, name, quantity available, harvest date, and its seasonal availability status
     * in a structured manner, making it easy to read and understand at a glance.
     *
     * @param crop The crop object whose details are to be displayed. This object contains all the necessary
     *             information about the crop that will be displayed.
     */
    private static void displaySingleCrop(Crop crop) {
        // Header.
        System.out.printf("%-10s %-20s %-15s %-15s %-10s%n", "Crop ID", "Crop Name", "Quantity", "Harvest Date", "In Season");
        // Crop data.
        System.out.printf("%-10d %-20s %-15d %-15s %-10s%n",
                crop.getCropID(), crop.getCropName(), crop.getQuantityAvailable(), crop.getHarvestDate(), crop.isInSeason() ? "Yes" : "No");
    }


    /**
     * Facilitates the modification of an existing crop's details. The method prompts the user
     * to enter the ID of the crop they wish to modify. If a crop with the specified ID is found,
     * it displays the current details of the crop and allows the user to enter new values for
     * the crop's properties (name, quantity, harvest date, and seasonal status).
     * <p>
     * The changes made by this method are staged and must be explicitly pushed to the Google Sheet
     * or the application's data store to be permanently applied.
     * <p>
     * Note: The implementation details for capturing and applying the new crop details are not
     * provided in the snippet.
     */
    private static void modifyCrop() {
        System.out.println("\nModifying an existing crop.");

        // Locate crop in dataRow
        // TODO: ALLOW USER TO EXIT THE METHOD OR RETRY PUTTING IN CROP ID.
        int cropID = promptForInt("Enter the crop ID of the crop you wish to modify: ");
        Crop cropToModify = findCropByID(cropID);

        if (cropToModify == null) {
            System.out.println("No crop with the specified ID found.");
            return;
        }

        // Display the selected crop in a table-like format.
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
                    cropToModify.setCropChanges(2); // Flag Crop since it was changed from its original state.
                    break;
                case 2:
                    cropToModify.setQuantityAvailable(promptForInt("Enter new quantity available: "));
                    cropToModify.setCropChanges(2); // Flag Crop since it was changed from its original state.
                    break;
                case 3:
                    // TODO: MAKE SURE USER INPUTS CORRECT FORMAT MM-DD-YYYY
                    cropToModify.setHarvestDate(promptForString("Enter new harvest date (MM-DD-YYYY): "));
                    cropToModify.setCropChanges(2); // Flag Crop since it was changed from its original state.
                    break;
                case 4:
                    cropToModify.setInSeason(promptForBoolean("Is the crop in season? (yes/no): "));
                    cropToModify.setCropChanges(2); // Flag Crop since it was changed from its original state.
                    break;
                case 5:
                    finished = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            if (!finished) {
                // Display the updated crop.
                System.out.println("\nUpdated Crop:");
                displaySingleCrop(cropToModify);
            }
        }

        changesToRow.add(cropToModify); // Stage changes.
        System.out.println("Crop modifications staged for changes.");
    }


    /**
     * Initiates the deletion process for a specified crop. This method prompts the user for the ID
     * of the crop they wish to delete. If a crop with the entered ID exists, it sets a flag
     * (or similar mechanism) to mark the crop for deletion.
     * <p>
     * The deletion is not immediately executed; it is staged and must be finalized by a separate
     * action, such as pushing changes to the Google Sheet or updating the application's data store.
     * <p>
     * This method demonstrates handling the logic for marking a crop as deleted but does not
     * cover the execution of the deletion in the data persistence layer.
     *
     * @throws Exception If no crop matches the provided ID, or if an error occurs during the
     *                   process of marking the crop for deletion.
     */
    private static void deleteCrop() throws Exception {
        System.out.println("Deleting a crop.");

        // TODO: IMPLEMENT WAY FOR USER TO EXIT METHOD OR RETRY PUTTING IN CROPID.
        int cropID = promptForInt("Enter the crop ID of the crop you wish to delete: ");
        Crop cropToDelete = findCropByID(cropID);

        if (cropToDelete == null) {
            System.out.println("No crop with the specified ID found.");
            return;
        }

        cropToDelete.setCropChanges(3); // Let changesToRow know that this will be deleted.

        // Assuming cropToDelete is the Crop object you want to delete.
        service.deleteDataRow(cropToDelete);
        dataRow.removeIf(c -> c.getCropID() == cropToDelete.getCropID());
        System.out.println("Crop with ID " + cropToDelete.getCropID() + " has been removed from local data.");
    }


    /**
     * Manages crop data by allowing the user to add, update, or delete crops in the dataRow.
     * It presents a submenu for the user to select the desired operation. Depending on the user's choice,
     * it prompts for further details (e.g., crop ID, name, quantity) to perform the selected operation.
     * This method facilitates direct manipulation of crop data stored in memory, which can later be synchronized
     * with the Google Sheet using the pushChanges method.
     *
     * @throws Exception if there's an error in performing the selected operation, including issues with
     *                   user input or problems accessing the dataRow.
     */
    private static void manageCropData() throws Exception {
        String sheetName = getAndVerifySheets(); // Select sheet to work from.

        System.out.print("\nWhat would you like to do? (add, modify, delete): ");
        String action = input.nextLine().trim().toLowerCase();

        switch (action) {
            case "add":
                addNewCrop(sheetName);
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
     * Pushes changes made to the dataRow back to the Google Sheet. It iterates through a queue of changes,
     * applying each one to the sheet. This includes adding new crops, updating existing crop details,
     * and deleting crops from the sheet. Each operation is performed using methods provided by the
     * GoogleSheetsService class, ensuring that the in-memory data is synchronized with the persistent
     * data stored in the Google Sheet.
     *
     * @throws Exception if there's an issue applying changes to the Google Sheet, which may arise from
     *                   problems with the Google Sheets API, network issues, or authorization errors.
     */
    private static void pushChanges() throws Exception {
        while (!changesToRow.isEmpty()) {
            Crop crop = changesToRow.poll(); // Retrieve and remove the head of the queue
            switch (crop.getCropChanges()) {
                case 1: // Add new crop
                    service.addDataRow(crop);
                    System.out.println("New crop with ID " + crop.getCropID() + " added to the database.");
                    break;
                case 2: // Modify existing crop
                    service.updateDataRow(crop);
                    System.out.println("Crop with ID " + crop.getCropID() + " was modified in the database.");
                    break;
                case 3: // Delete crop
                    service.deleteDataRow(crop);
                    System.out.println("Crop with ID " + crop.getCropID() + " was deleted from the database.");
                    break;
                default:
                    System.out.println("Unrecognized crop change for ID " + crop.getCropID());
                    break;
            }
        }
    }


    /**
     * Displays the main menu and handles user interactions for navigating through the application's features.
     * Users can choose to display crops, manage crop data (add, update, delete), or push changes to the Google Sheet.
     * The method captures user input to determine the desired action and invokes the corresponding methods to
     * perform these actions. It ensures a loop until the user decides to exit the application.
     *
     * @throws Exception if any user operation fails, which could be due to issues with user input, problems
     *                   fetching or updating data in the Google Sheet, or internal application errors.
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
            choice = Integer.parseInt(input.nextLine()); // Using nextLine() to avoid Scanner issues.

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


    /**
     * The main entry point of the application. This method initializes the application and starts the user
     * interaction process by calling the mainMenu method. It ensures that the application is properly set up
     * and that resources are managed correctly, such as closing the Scanner before exiting.
     *
     * @param args The command-line arguments passed to the application (not used in this application).
     * @throws Exception if the application encounters a critical failure during initialization or execution,
     *                   such as failing to connect to Google Sheets or errors in user operations.
     */
    public static void main(String[] args) throws Exception {
        // Ensure the scanner is closed on application exit.
        try {
            mainMenu(); // Initiates the application.
        } finally {
            input.close();
        }
    }



}