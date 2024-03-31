package org.example;

import org.example.services.GoogleSheetsService;

import java.util.*;

public class Main {
    private static final String SPREADSHEET_ID = "18ksHaCHNrr6uICxtjAjhN3Zs_YqJMwIywf-eCbcfklc"; // The Google Sheets database ID. PLEASE HIDE THIS BEFORE PRODUCTION!
    private static GoogleSheetsApplicationInterface service; // The service used to interact with the Google Sheets database.

    // On startup, try to create instance of GoogleSheetsService to connect to the database.
    // Exit the program otherwise since you can't do anything in this application WITHOUT
    // database access.
    static {
        try {
            service = new GoogleSheetsService(SPREADSHEET_ID);
            System.out.println("Available sheets: " + service.getAvailableSheets());
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            e.printStackTrace();
            // Handle initialization failure, e.g., exit the program or set service to null.
            System.exit(1);
        }
    }

    // Stack to store changes to row.
    // Goal is to minimize amount of times we have to call the Google Sheets API,
    // Instead having one request to handle all the actions.
    private static Queue<Crop> changesToRow = new LinkedList<>() {
    }; // Proposed changes for a row will be kept here.

    // All data of a specific sheet will be here.
    // If the sheet we're accessing changes,
    // load all the data from said new sheet onto here.
    public static List<Crop> dataRow = new ArrayList<>(); // Every row and corresponding data will be here.

    // Create a static instance of Scanner class so that user can input things.
    // Scanner is static because we don't want main to be an instantiable class (because it's the MAIN CLASS).
    // Furthermore, global Scanner class because Scanner is used in MANY methods.
    // So to prevent a memory leak by accident, just keep it global!
    private static final Scanner input = new Scanner(System.in);


    /**
     * This method displays all the crops located in the dataRow.
     * The goal of this method is to allow the user to see the database,
     * without needing to go on sheets.google.com/sheet.
     *
     * @param crops The data from the Google Sheet (that's stored locally in the dataRow).
     * */
    private static void displayCrops(List<Crop> crops) {
        if (crops.isEmpty()) {
            System.out.println("No crops found.");
        } else {
            System.out.println("\nList of Crops:");
            for (Crop crop : crops) {
                System.out.println("Crop ID: " + crop.getCropID() +
                        ", Crop Name: " + crop.getCropName() +
                        ", Quantity: " + crop.getQuantityAvailable() +
                        ", Harvest Date: " + crop.getHarvestDate() +
                        ", In Season: " + crop.isInSeason());
            }
        }
    }


    /**
     * This is an implementation of service.getAvailableSheets,
     * where the user can select a specific sheet to modify (with verification).
     * This is a method because many methods need to figure out
     * which sheet to access.
     *
     * @return sheetName A validated string that is one of the available sheets listed in the database.
     * */
    private static String getAndVerifySheets() throws Exception {
        boolean validSheetSelected = false;
        String sheetName = "";
        List<String> availableSheets = new ArrayList<>();

        try {
            availableSheets = service.getAvailableSheets();
            System.out.println("\n*****************************");
            System.out.println("Available sheets to modify: " + availableSheets);
        } catch (Exception e) {
            System.err.println("Failed to fetch available sheets: " + e.getMessage());
            return null; // Exit the method if sheets cannot be fetched
        }

        // Consume any lingering newline character left in the buffer from previous input
        input.nextLine(); // Add this line if you've used input.nextInt() or similar before calling this method

        while (!validSheetSelected) {
            System.out.print("Choose sheet to modify: ");
            sheetName = input.nextLine().trim();

            if (availableSheets.contains(sheetName)) {
                validSheetSelected = true;
            } else {
                System.out.println("Invalid sheet name. Please try again.");
            }
        }

        System.out.println("*****************************\n");
        dataRow = service.getItemsInSheet(sheetName);
        return sheetName;
    }



    /**
     * This method allows the user to add a new row to a specific sheet in the Google Sheets database.
     * First, the user will choose a sheet to modify.
     * Then, the user will choose a specific row (or not) to add the data to.
     * Lastly, modify the data in dataRow, and stage the changes in the changesToRow queue.
     * */
    private static void addNewData() throws Exception {

        // Ask the user to choose a sheet to modify.
        String sheetToModify = getAndVerifySheets();

        // Ask the user if they want to add the data to the end of a spreadsheet,
        // or somewhere else.

        // Add new data to dataRow.

        // Append new data to changesToRow queue.

        // Todo: SAMPLE IMPLEMENTATION, FIX LATER!!!!
        List<Object> newRowData = Arrays.asList("Test", "Test", 100, "Test", "0", "1/1/2001", false);
        try {
            service.addDataRow(sheetToModify, newRowData);
            System.out.println("New crop added successfully.");
        } catch (Exception e) {
            System.err.println("Failed to add new crop: " + e.getMessage());
        }
    }


    /***/
    private static void modifySpecificCrop(String sheetName) {
        Crop cropToModify = null;
        while (cropToModify == null) {
            System.out.print("Enter Crop ID to modify (or type 'exit' to return): ");
            String inputLine = input.nextLine();

            if ("exit".equalsIgnoreCase(inputLine.trim())) {
                return; // User chooses to exit the modification process
            }

            try {
                int cropID = Integer.parseInt(inputLine);
                Optional<Crop> optionalCrop = dataRow.stream().filter(crop -> crop.getCropID() == cropID).findFirst();
                if (!optionalCrop.isPresent()) {
                    System.out.println("Crop with ID " + cropID + " not found. Please try again.");
                } else {
                    cropToModify = optionalCrop.get();
                    // Proceed with crop modification logic here
                    System.out.println("Selected Crop: " + cropToModify.getCropName());
                    // Example modification: Changing the crop name (You should implement modification options as needed)
                    System.out.print("New crop name: ");
                    String newName = input.nextLine();
                    if (newName != null && !newName.trim().isEmpty()) {
                        cropToModify.setCropName(newName.trim());
                        System.out.println("Crop name updated. Remember to push changes to save them.");
                        changesToRow.offer(cropToModify); // Queue the modified crop for later updates
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid Crop ID.");
            }
        }
    }


    /**
     * */
    private static void checkAndModifyCrop() throws Exception {
        String sheetName = getAndVerifySheets();
        if (sheetName == null) {
            return; // If sheet verification fails or user exits
        }

        boolean keepGoing = true;
        while (keepGoing) {
            System.out.println("\n*****************************");
            System.out.println("\nModifying Crop Menu for sheet: " + sheetName);
            System.out.println("1: See all crops");
            System.out.println("2: Modify a crop by Crop ID");
            System.out.println("0: Return to Main Menu");
            System.out.print("Enter your choice: ");

            int userChoice = Integer.parseInt(input.nextLine());

            switch (userChoice) {
                case 1:
                    // Display all crops
                    try {
                        // We don't need to update dataRow since getAndVerifySheets does it for us!
                        displayCrops(dataRow);
                    } catch (Exception e) {
                        System.err.println("An error occurred: " + e.getMessage());
                    }
                    break;
                case 2:
                    modifySpecificCrop(sheetName);
                    break;
                case 0:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
        System.out.println("*****************************\n");
    }


    /***/
    private static void deleteCrop(){
        System.out.println("Empty for now...");
    }


    /***/
    private static void pushChanges() throws Exception {
        if (changesToRow.isEmpty()) {
            System.out.println("You have nothing in here.");
            return;
        }

        String sheetName = getAndVerifySheets(); // Assume this method gets the relevant sheet
        if (sheetName == null) {
            System.out.println("Sheet verification failed or user exited.");
            return;
        }

        while (!changesToRow.isEmpty()) {
            Crop crop = changesToRow.poll(); // Dequeue a crop
            try {
                service.updateCrop(sheetName, crop); // Update the crop in the Google Sheet
                System.out.println("Updated Crop ID " + crop.getCropID() + " in the sheet.");
            } catch (Exception e) {
                System.err.println("Failed to update Crop ID " + crop.getCropID() + ": " + e.getMessage());
                // Optionally, requeue the crop for another attempt or log this failure
            }
        }
    }



    /**
     * The main menu that the farm managers will use to interact with the database.
     * This menu contains the basic operations that they can do.
     * There is no parameters or return function as this is the only thing
     * the users can see and interact with (before going to a submenu).
     * */
    private static void mainMenu() throws Exception {
        while (true){
            System.out.println("\nMain Menu: ");
            System.out.println("1: See all crops");
            System.out.println("2: Add new crop");
            System.out.println("3: Edit crop");
            System.out.println("4: Delete crop");
            System.out.println("5: PUSH ALL CHANGES");
            System.out.println("0: QUIT PROGRAM");

            System.out.print("\nEnter option: ");

            int userInput = input.nextInt();

            switch (userInput){
                case 0:
                    input.close();
                    System.exit(0);
                    break;

                case 1:
                    System.out.println("\nShowing you all crops in a specific sheet!");
                    getAndVerifySheets();
                    // Since getAndVerifySheets updates dataRow for us, we don't need to do anything else!
                    displayCrops(dataRow);
                    break;

                case 2:
                    System.out.println("\nAdding new crop WITH SAMPLE DATA... (CHANGE LATER)");
                    addNewData();
                    break;

                case 3:
                    System.out.println("\nEditing crop...");
                    checkAndModifyCrop();
                    break;

                case 4:
                    System.out.println("\nDeleting crop...");
                    deleteCrop();
                    break;

                case 5:
                    System.out.println("\nPUSHING CHANGES...");
                    pushChanges();
                    break;

                default:
                    System.out.println("Invalid option! Please try again.");
                    break;
            }
        }
    }


    /**
     * This is the entry point for the program. The goal is to either convert this into an interface for the user
     * so that they can do all their database operations through the use of this JAVA API.
     * Furthermore, if we get to doing a website, this will also be the entrypoint for said website where
     * JavaScript will get the data to display on the webpage.
     * Since this is the main, there is no params or returns.
     * */
    public static void main(String[] args) throws Exception {
        // Once connection to the server has been established, show menu.
        mainMenu();
    }
}