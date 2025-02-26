package M3;
import java.util.Scanner;
/*
Challenge 1: Command-Line Calculator
------------------------------------
- Accept two numbers and an operator as command-line arguments
- Supports addition (+) and subtraction (-)
- Allow integer and floating-point numbers
- Ensures correct decimal places in output based on input (e.g., 0.1 + 0.2 → 1 decimal place)
- Display an error for invalid inputs or unsupported operators
- Capture 5 variations of tests
*/

public class CommandLineCalculator extends BaseClass {
    private static String ucid = "hp627"; // <-- change to your ucid

    public static void main(String[] args) {
        printHeader(ucid, 1, "Objective: Implement a calculator using command-line arguments."); // Print the header -- hp627 - 2/25/2025
        Scanner input = new Scanner(System.in);
        if (args.length != 3) {
            System.out.println("Usage: java M3.CommandLineCalculator <num1> <operator> <num2>"); /// Checks if the correct number of arguments are passed -- hp627 - 2/25/2025
            printFooter(ucid, 1); // Print the footer -- hp627 - 2/25/2025
            
        }
        
        try {
            // Tells the user to enter an equation -- hp627 - 2/25/2025
            System.out.print("Enter an equation: ");
            String equation = input.next();
            System.out.println("Calculating result...");
           
            // extract the equation (format is <num1> <operator> <num2>)
            
            String num1 = "";
            String num2 = "";
            String operator = "";
            boolean reachedoperator = false; // Check if the operator is found -- hp627 - 2/25/2025
            for (int i = 0; i < equation.length(); i++) {
                if (!reachedoperator) {
                    if (equation.indexOf('+') != -1) { /// Ensures operator is '+' or '-' -- hp627 - 2/25/2025
                        if (equation.substring(i, i + 1).equals("+") || equation.substring(i, i + 1).equals("-")) {
                            operator = equation.substring(i, i + 1); /// Store the operator -- hp627 - 2/25/2025
                            reachedoperator = true; /// Marks that the operator has been found -- hp627 - 2/25/2025
                        }
                        else if (!equation.substring(i, i + 1).equals("+") || !equation.substring(i, i + 1).equals("-")) {
                            num1 += equation.substring(i, i + 1);
                        }
                    }
                }
                else {
                    num2 += equation.substring(i, i + 1);/// Appends digits to num2 -- hp627 - 2/25/2025
                }
                
                
            }
           
            if (operator.equals("+")) {
                System.out.println(Double.valueOf(num1) + Double.valueOf(num2));
            }
            else {
                System.out.println(Double.valueOf(num1) - Double.valueOf(num2));
            }
            // check if operator is addition or subtraction

            // check the type of each number and choose appropriate parsing

            // generate the equation result (Important: ensure decimals display as the
            // longest decimal passed)
            // i.e., 0.1 + 0.2 would show as one decimal place (0.3), 0.11 + 0.2 would shows
            // as two (0.31), etc

        } catch (Exception e) {
            System.out.println("Invalid input. Please ensure correct format and valid numbers.");
        }
        
        printFooter(ucid, 1);
    }
}