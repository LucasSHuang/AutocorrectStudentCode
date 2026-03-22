import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Lucas Huang
 */
public class Autocorrect {

    private String[] words;
    private int threshold;
    private ArrayList<Integer>[] combinations;
    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
        this.combinations = new ArrayList[676];

        // Go through every word in the dictionary
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            // For every word get every two letter combination
            for (int j = 0; j < words[i].length() - 1; j++) {
                int firstLetter = word.charAt(j) - 'a';
                int secondLetter = word.charAt(j + 1) - 'a';

                // Check to make sure there isn't a dash or something else that isn't a letter
                if (!checkValid(firstLetter) || !checkValid(secondLetter)) {
                    continue;
                }

                // Get unique index of combination and then add it to arraylist using the index the of the word in the
                // dictionary
                int index = firstLetter * 26 + secondLetter;
                if (combinations[index] == null) {
                    combinations[index] = new ArrayList<Integer>();
                }
                combinations[index].add(i);
            }
        }
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {

        ArrayList<Pair> success = new ArrayList<Pair>();

        // Go through every two letter combination of the typed word
        for (int i = 0; i < typed.length() - 1; i++) {
            int firstLetter = typed.charAt(i) - 'a';
            int secondLetter = typed.charAt(i + 1) - 'a';
            if (!checkValid(firstLetter) || !checkValid(secondLetter)) {
                continue;
            }
            int index = firstLetter * 26 + secondLetter;

            // If no arraylist at that index not a possible two letter combination
            if (combinations[index] == null) {
                continue;
            }
            // Go through every dictionary word in that combination index and check edit distance
            for (int j = 0; j < combinations[index].size(); j++) {
                int wordIndex = combinations[index].get(j);
                int distance = calcDistance(typed, words[wordIndex]);
                if (distance <= threshold) {
                    success.add(new Pair(words[wordIndex], distance));
                }
            }
        }

        // Go through all the successful words and compare them to each other
        for (int i = 0; i < success.size(); i++) {
            for (int j = i + 1; j < success.size(); j++) {
                Pair first = success.get(i);
                Pair second = success.get(j);
                int firstLen = first.getEditDistance();
                int secondLen = second.getEditDistance();
                String firstWord = first.getWord();
                String secondWord = second.getWord();

                // Swap if out of order by distance or alphabetically
                boolean greaterDistance = firstLen > secondLen;
                boolean wrongAlphaOrder = firstLen == secondLen && firstWord.compareTo(secondWord) > 0;
                if (greaterDistance || wrongAlphaOrder) {
                    success.set(i, second);
                    success.set(j, first);
                }
            }
        }

        // Move sorted arraylist into array
        String[] results = new String[success.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = success.get(i).getWord();
        }
        return results;
    }

    // Calculate error distance between two words
    public int calcDistance (String typed, String comparison) {

        int[][] arr = new int[typed.length() + 1][comparison.length() + 1];

        // Fill out all base cases where distance will always be length of the word
        for (int i = 0; i < arr.length; i++) {
            arr[i][0] = i;
        }
        for (int j = 0; j < arr[0].length; j++) {
            arr[0][j] = j;
        }

        // Go through all spots on the table and if the tails aren't the same then take the smallest distance of the
        // substitution, addition, and deletion cases
        for (int i = 1; i < arr.length; i++) {
            for (int j = 1; j < arr[0].length; j++) {
                if (typed.charAt(i - 1) != comparison.charAt(j - 1)) {
                    int sub = arr[i - 1][j - 1] + 1;
                    int add = arr[i][j - 1] + 1;
                    int delete = arr[i - 1][j] + 1;
                    arr[i][j] = Math.min(sub, Math.min(add, delete));
                }
                else {
                    arr[i][j] = arr[i - 1][j - 1];
                }
            }
        }
        return arr[typed.length()][comparison.length()];
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Check to see if letter is between a-z
    private static boolean checkValid (int val) {
        if (val < 0 || val > 26) {
            return false;
        }
        return true;
    }
}