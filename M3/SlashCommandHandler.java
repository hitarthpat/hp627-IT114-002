package M3;

import java.util.Random;
import java.util.Scanner;

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
            String command = parts[0].toLowerCase();

            // check if greet
            if (command.equals("/greet")) {
                //// process greet
                if (parts.length < 2) {
                    System.out.println("Error: Missing name. Usage: /greet <name>");
                } else {
                    System.out.println("Hello, " + parts[1] + "!");
                }
            } 
            // check if roll
            else if (command.equals("/roll")) {
                //// process roll
                if (parts.length < 2 || !parts[1].matches("\\d+d\\d+")) {
                    //// handle invalid formats
                    System.out.println("Error: Invalid format. Usage: /roll <num>d<sides>");
                } else {
                    String[] diceParts = parts[1].split("d");
                    int num = Integer.parseInt(diceParts[0]);
                    int sides = Integer.parseInt(diceParts[1]);
                    if (num < 1 || sides < 1) {
                        System.out.println("Error: Number of dice and sides must be positive integers.");
                        continue;
                    }
                    Random random = new Random();
                    int result = 0;
                    for (int i = 0; i < num; i++) {
                        result += random.nextInt(sides) + 1;
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
                    System.out.println(parts[1]);
                }
            } 
            // check if quit
            else if (command.equals("/quit")) {
                //// process quit
                System.out.println("Exiting program...");
                break;
            } 
            // handle invalid commands
            else {
                System.out.println("Error: Unrecognized command. Please use /greet, /roll, /echo, or /quit.");
            }
        }

        printFooter(ucid, 2);
        scanner.close();
    }
}
