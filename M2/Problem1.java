package M2;

public class Problem1 extends BaseClass {
    private static int[] array1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static int[] array2 = { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
    private static int[] array3 = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9 };
    private static int[] array4 = { 9, 9, 8, 8, 7, 7, 6, 6, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0, 0 };

    private static void printOdds(int[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printArrayInfo(arr, arrayNumber);

        // Challenge: Print odd values only in a single line separated by commas
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        System.out.print("Output Array: ");
        // Start Solution Edits

        // Challenge
        for (int n : arr) // loop through every element in the arry -- hp627 2/11/25
            if (n % 2 == 1) // check if the element has a remainder of 1 when divided by two -- hp627
                            // 2/11/25
                System.out.print(n + ","); // if the element has a remainder, then print the element followed by a comma
                                           // -- hp627 2/11/25

        // End Solution Edits
        System.out.println("");
        System.out.println("______________________________________");
    }

    public static void main(String[] args) {
        final String ucid = "hp627"; // <-- change to your UCID
        // no edits below this line
        printHeader(ucid, 1);
        printOdds(array1, 1);
        printOdds(array2, 2);
        printOdds(array3, 3);
        printOdds(array4, 4);
        printFooter(ucid, 2);

    }
}