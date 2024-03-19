package org.example;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.example.auth.SheetsServiceInitializer;

import java.util.Scanner;

public class Main {
    private static Sheets sheetsService; // Main object used to interact with the Google Sheets database.
    private static final String SPREADSHEET_ID = "18ksHaCHNrr6uICxtjAjhN3Zs_YqJMwIywf-eCbcfklc"; // The Google Sheets database ID. PLEASE HIDE THIS BEFORE PRODUCTION!


    /**
     * This function creates and initializes the Google Sheets Service through the SheetsServiceInitializer file.
     * However, the goal is to have all the different modifications be put on a stack that'll be executed once the
     * user says so (via the mainMenu) to limit API calls and save money.
     * */
    public static void initializeSheetsService() {
        try {
            sheetsService = SheetsServiceInitializer.getSheetsService();
            System.out.println("Successfully initialized Google Sheets service.");
        } catch (Exception e) {
            System.err.println("Failed to initialize Google Sheets service.");
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * This is a helper function for the main to make sure that sheetsService was initialized successfully.
     * Once the service has been confirmed as operable, it'll display the available spreadsheets & sheets that are
     * available for the user to edit.
     * */
    public static void testConnection() {
        try {
            // Use the Sheets service to load the spreadsheet by its ID
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();

            // Print the title of the spreadsheet
            System.out.println("\nSuccessfully connected to the spreadsheet: " + spreadsheet.getProperties().getTitle());

            // Iterate through each sheet in the spreadsheet and print its title
            System.out.println("\nAvailable sheets:\n------------------------------------");
            spreadsheet.getSheets().forEach(sheet -> System.out.println("- " + sheet.getProperties().getTitle()));
        } catch (Exception e) {
            System.err.println("Failed to connect to the spreadsheet. Please check your setup:");
            e.printStackTrace();
        }
    }


    /**
     * The main menu that the farm managers will use to interact with the database.
     * This menu contains the basic operations that they can do.
     * There is no parameters or return function as this is the only thing
     * the users can see and interact with (before going to a submenu).
     * */
    public static void mainMenu(){

        Scanner input = new Scanner(System.in);
        
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
                    System.exit(0);
                    break;

                case 1:
                    System.out.println("\nShowing you all crops...");
                    break;

                case 2:
                    System.out.println("\nAdding new crop...");
                    break;

                case 3:
                    System.out.println("\nEditing crop...");
                    break;

                case 4:
                    System.out.println("\nDeleting crop...");
                    break;

                case 5:
                    System.out.println("\nPUSHING CHANGES...");
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
    public static void main(String[] args) {
        // Create connection to the server (Google Sheets Service)
        initializeSheetsService();

        // Ensure that we have access to the Google Spreadsheet
        testConnection();

        // Once connection to the server has been established, show menu.
        mainMenu();
    }
}