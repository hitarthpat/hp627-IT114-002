package M3;

/*
Challenge 2: Simple Slash Command Handler
-----------------------------------------
- Accept user input as slash commands
  - "/greet <name>" → Prints "Hello, <name>!"
  - "/roll <num>d<sides>" → Roll <num> dice with <sides> and returns a single outcome as "Rolled <num>d<sides> and got <result>!"
  - "/echo <message>" → Prints the message back
  - "/quit" → Exits the program
- Commands are case-insensitive
- Print an error for unrecognized commands
- Print errors for invalid command formats (when applicable)
- Capture 3 variations of each command except "/quit"
*/

import java.util.Scanner;
import java.util.Random;

public class SlashCommandHandler extends BaseClass {
    private static String ucid = "hp627"; // <-- change to your UCID

    public static void main(String[] args) {
        printHeader(ucid, 2, "Objective: Implement a simple slash command parser.");

        Scanner scanner = new Scanner(System.in);

        // Can define any variables needed here

        while (true) {
            System.out.print("Enter command: ");
            // get entered text
            String input = scanner.nextLine().trim();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase(); // Make command lowercase so it's not case-sensitive -- hp627 - 2/24/2025

            // check if greet
            if (command.equals("/greet")) {
                //// process greet
                if (parts.length < 2) {
                    System.out.println("Error: Missing name. Usage: /greet <name>");
                } else {
                    System.out.println("Hello, " + parts[1] + "!"); // Say hello to the user -- hp627 - 2/24/2025
                }
            } 
            // check if roll
            else if (command.equals("/roll")) {
                //// process roll
                if (parts.length < 2 || !parts[1].matches("\\d+d\\d+")) {
                    //// handle invalid formats
                    System.out.println("Error: Invalid format. Usage: /roll <num>d<sides>");
                } else {
                  // Break input into two numbers (num dice and sides) -- hp627 - 2/24/2025

                    String[] diceParts = parts[1].split("d");
                    int num = Integer.parseInt(diceParts[0]); // Number of dice -- hp627 - 2/24/2025
                    int sides = Integer.parseInt(diceParts[1]); // Number of sides per die -- hp627 - 2/24/2025
                    if (num < 1 || sides < 1) {
                        System.out.println("Error: Number of dice and sides have to be positive integers.");
                        continue;
                    }
                    Random random = new Random();
                    int result = 0;
                    for (int i = 0; i < num; i++) {
                        result += random.nextInt(sides) + 1; // Generate a random roll for each die -- hp627 - 2/24/2025
                    }
                    System.out.println("Rolled " + parts[1] + " and got " + result + "!");
                }
            } 
            // check if echo
            else if (command.equals("/echo")) {
                //// process echo
                if (parts.length < 2) {
                    System.out.println("Error: Missing message. Usage: /echo <message>");
                } else {
                    System.out.println(parts[1]); // Repeat what the user typed -- hp627 - 2/24/2025
                }
            } 
            // check if quit
            else if (command.equals("/quit")) {
                //// process quit
                System.out.println("Exiting program...");
                break; // Stop the program -- hp627 - 2/24/2025
            } 
            // handle invalid commands
            // If user types an unknown command -- hp627 - 2/24/2025

            else {
                System.out.println("Error: Unrecognized command. Please use /greet, /roll, /echo, or /quit.");
            }
        }

        printFooter(ucid, 2);
        scanner.close();
    }
}