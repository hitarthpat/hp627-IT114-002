package M3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.random.*;

/*
Challenge 3: Mad Libs Generator (Randomized Stories)
-----------------------------------------------------
- Load a **random** story from the "stories" folder
- Extract **each line** into a collection (i.e., ArrayList)
- Prompts user for each placeholder (i.e., <adjective>) 
    - Any word the user types is acceptable, no need to verify if it matches the placeholder type
    - Any placeholder with underscores should display with spaces instead
- Replace placeholders with user input (assign back to original slot in collection)
*/

public class MadLibsGenerator extends BaseClass {
    private static final String STORIES_FOLDER = "M3/stories";
    private static String ucid = "hp627"; // <-- change to your ucid

    public static void main(String[] args) throws FileNotFoundException, IOException {
        printHeader(ucid, 3,
                "Objective: Implement a Mad Libs generator that replaces placeholders dynamically.");

        Scanner scanner = new Scanner(System.in);
        
        File folder = new File(STORIES_FOLDER);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles().length == 0) { ///Check if the folder exists and has files -- hp627 - 2/25/2025
            System.out.println("Error: No stories found in the 'stories' folder.");
            printFooter(ucid, 3);
            scanner.close();
            return;
        }
        
        String[] storyList = {"story1.txt", "story2.txt", "story3.txt", "story4.txt", "story5.txt"}; /// list of stories -- hp627 - 2/25/2025

        Random rn = new Random();
        int random = rn.nextInt(5 - 1 + 1) + 1;

        InputStream is = new FileInputStream("M3/stories/" + storyList[random - 1]); /// Genrate a random story -- hp627 - 2/25/2025

        Scanner fileScanner = new Scanner(is); /// Scanner for the file -- hp627 - 2/25/2025
        
        
        List<String> lines = new ArrayList<>(); ///Store the lines of the story -- hp627 - 2/25/2025

        while (fileScanner.hasNextLine()) { /// Read the file -- hp627 - 2/25/2025
            String line = fileScanner.nextLine();
            String moddedLine = ""; /// Store the modified line -- hp627 - 2/25/2025
            String[] words = line.split(" "); /// Split the line into words -- hp627 - 2/25/2025
            
            List<Integer> indexes = new ArrayList<>(); /// Store the indexes of the placeholders -- hp627 - 2/25/2025
            List<String> placeholders = new ArrayList<>(); /// Store the placeholders -- hp627 - 2/25/2025

            for (int i = 0; i < words.length; ++i) {
                for (int k = 0; k < words[i].length(); ++k)
                    if (words[i].substring(0, k).equals("<")) { /// Check if the word is a placeholder -- hp627 - 2/25/2025
                        indexes.add((Integer)i);
                        placeholders.add(words[i]);
                        break;
                }
            }
            System.out.println(line);
            for (int i = 0; i < placeholders.size(); ++i) { /// Prompt the user for each placeholder -- hp627 - 2/25/2025
                System.out.print("Enter a word for " + placeholders.get(i) + ": ");
                String input = scanner.nextLine();
                System.out.println();
                words[indexes.get(i)] = input; /// Replace the placeholder with the user input -- hp627 - 2/25/2025
            }
            for (String word : words) {
                moddedLine += word + " ";
            }
            lines.add(moddedLine); /// Add the modified line to the list -- hp627 - 2/25/2025

        }
        
        // Start edits

        // load a random story file

        // parse the story lines

        // iterate through the lines

        // prompt the user for each placeholder (note: there may be more than one
        // placeholder in a line)

        // apply the update to the same collection slot

        // End edits
        System.out.println("\nYour Completed Mad Libs Story:\n");
        StringBuilder finalStory = new StringBuilder();
        for (String line : lines) {
            finalStory.append(line).append("\n");
        }
        System.out.println(finalStory.toString());

        printFooter(ucid, 3);
        scanner.close();
        is.close();
        fileScanner.close();
    }
}