package org.example;


import java.util.Scanner;

public class Main {

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

    public static void main(String[] args) {

        // Once connection to the server has been established, show menu.
        mainMenu();
    }
}