package org.example;


import java.util.Scanner;

public class Main {

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
        mainMenu();
    }
}