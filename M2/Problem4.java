package M2;

public class Problem4 extends BaseClass {
    private static String[] array1 = { "hello world!", "java programming", "special@#$%^&characters", "numbers 123 456",
            "mIxEd CaSe InPut!" };
    private static String[] array2 = { "hello world", "java programming", "this is a title case test",
            "capitalize every word", "mixEd CASE input" };
    private static String[] array3 = { "  hello   world  ", "java    programming  ",
            "  extra    spaces  between   words   ",
            "      leading and trailing spaces      ", "multiple      spaces" };
    private static String[] array4 = { "hello world", "java programming", "short", "a", "even" };

    private static void transformText(String[] arr, int arrayNumber) {
        // Only make edits between the designated "Start" and "End" comments
        printArrayInfoBasic(arr, arrayNumber);

        // Challenge 1: Remove non-alphanumeric characters except spaces
        // Challenge 2: Convert text to Title Case
        // Challenge 3: Trim leading/trailing spaces and remove duplicate spaces
        // Result 1-3: Assign final phrase to `placeholderForModifiedPhrase`
        // Challenge 4 (extra credit): Extract middle 3 characters (beginning starts at middle of phrase),
        // assign to 'placeholderForMiddleCharacters'
        // if not enough characters assign "Not enough characters"
 
        // Step 1: sketch out plan using comments (include ucid and date)
        // Step 2: Add/commit your outline of comments (required for full credit)
        // Step 3: Add code to solve the problem (add/commit as needed)
        String placeholderForModifiedPhrase = "";
        String placeholderForMiddleCharacters = "";
        
        for(int i = 0; i <arr.length; i++){
            // Start Solution Edits
            
            String input = arr[i];

            // remove non-alphanumeric characters except spaces -- hp627 - 2/16/25
            String cleaned = "";
            // travarse through input -- hp627 - 2/16/25
            for (int j = 0; j < input.length(); j++) {
                char c = input.charAt(j);
                // add all chars, numbers, spaces to the variable cleaned -- hp627 - 2/16/25
                if ((c >= 'a' && c <= 'z') ||
                        (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9') ||
                        c == ' ') {
                    cleaned += c;
                }
            }

            // convert to Title Case -- hp627 - 2/16/25
            String lowercaseText = "";
            // travarse through cleaned string -- hp627 - 2/16/25
            for (int j = 0; j < cleaned.length(); j++) {
                char c = cleaned.charAt(j);
                if (c >= 'A' && c <= 'Z') {
                    // convert everything to lowercase -- hp627 - 2/16/25
                    c = (char) (c + 32);
                }
                lowercaseText += c;
            }

            String titleCase = "";
            boolean capitalizeNext = true;
            // traverse through lowerCaseText string -- hp627 - 2/16/25
            for (int j = 0; j < lowercaseText.length(); j++) {
                char c = lowercaseText.charAt(j);
                if (capitalizeNext && (c >= 'a' && c <= 'z')) {
                    // convert to uppercase, set capitlizeNext to false -- hp627 - 2/16/25
                    c = (char) (c - 32);
                    capitalizeNext = false;
                }
                // if it's a space, set capitalizeNext to true -- hp627 - 2/16/25
                else if (c == ' ') {
                    capitalizeNext = true;
                }
                titleCase += c;
            }

            //trim leading/trailing spaces and remove duplicate spaces -- hp627 - 2/16/25
            String formattedText = "";
            boolean lastWasSpace = true;
            for (int j = 0; j < titleCase.length(); j++) {
                char c = titleCase.charAt(j);
                // if it's a space, and last was not a space, append to formatted string -- hp627 - 2/16/25
                if (c == ' ') {
                    if (!lastWasSpace) {
                        formattedText += c;
                    }
                    lastWasSpace = true;
                }
                else {
                    formattedText += c;
                    lastWasSpace = false;
                }
            }
            formattedText = formattedText.trim(); // finalizing trimming -- hp627 - 2/16/25

            // extract middle 3 characters (extra credit)
            int length = formattedText.length();
            if (length < 3) {
                placeholderForMiddleCharacters = "Not enough characters";
            } else {
                int mid = length / 2 - 1;
                placeholderForMiddleCharacters = formattedText.substring(mid, mid + 3);
            }



             // End Solution Edits
            System.out.println(String.format("Index[%d] \"%s\" | Middle: \"%s\"",i, placeholderForModifiedPhrase, placeholderForMiddleCharacters));
        }

       

        
        System.out.println("\n______________________________________");
    }

    public static void main(String[] args) {
        final String ucid = "hp627"; // <-- change to your UCID
        // No edits below this line
        printHeader(ucid, 4);

        transformText(array1, 1);
        transformText(array2, 2);
        transformText(array3, 3);
        transformText(array4, 4);
        printFooter(ucid, 4);
    }

}