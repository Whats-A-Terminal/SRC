package org.example;

import org.example.services.GoogleSheetsService;

import java.util.*;
import java.util.Scanner;

public class Main {
    private static final String SPREADSHEET_ID = "18ksHaCHNrr6uICxtjAjhN3Zs_YqJMwIywf-eCbcfklc"; // The Google Sheets database ID. PLEASE HIDE THIS BEFORE PRODUCTION!

    private static GoogleSheetsApplicationInterface service;
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

    private static Stack<Object> changesToRow = new Stack<>(); // Proposed changes for a row will be kept here.
    private static List<Object> dataRow = new ArrayList<>(); // Every row and corresponding data will be here.


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
                    try {
                        // Assuming "Crops" is the name of your sheet
                        List<Crop> crops = service.<Crop>getItemsInSheet("Sheet1");
                        displayCrops(crops);
                    } catch (Exception e) {
                        System.err.println("Failed to fetch crops: " + e.getMessage());
                    }
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

        // Once connection to the server has been established, show menu.
        mainMenu();
    }
}