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
    private static String ucid = "mt85"; // <-- change to your ucid

    public static void main(String[] args) {
        printHeader(ucid, 1, "Objective: Implement a calculator using command-line arguments.");
        Scanner input = new Scanner(System.in);
        if (args.length != 3) {
            System.out.println("Usage: java M3.CommandLineCalculator <num1> <operator> <num2>");
            printFooter(ucid, 1);
            
        }

        try {
            System.out.print("Enter an equation: ");
            String equation = input.next();
            System.out.println("Calculating result...");
            
            // extract the equation (format is <num1> <operator> <num2>)
            System.out.println(equation.split("+"));
            String[] equation_list = {};
            for (int i = 0; i < equation.length(); i++) {
                if (equation.indexOf('+') != -1) {
                    equation_list = new String[] {equation.substring(0, equation.indexOf('+')), "+"};
                }
                else if (equation.charAt(i) == '-') {
                    equation_list = equation.split("-");
                }
            }
            System.out.println(equation_list);
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