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
    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        ArrayList<Pair> success = new ArrayList<Pair>();
        for (int i = 0; i < words.length; i++) {
            int distance = calcDistance(typed, words[i]);
            if (distance <= threshold) {
                success.add(new Pair(words[i], distance));
            }
        }

        for (int i = 0; i < success.size(); i++) {
            for (int j = i + 1; j < success.size(); j++) {
                Pair first = success.get(i);
                Pair second = success.get(j);
                int firstLen = first.getEditDistance();
                int secondLen = second.getEditDistance();
                String firstWord = first.getWord();
                String secondWord = second.getWord();

                boolean greaterDistance = firstLen > secondLen;
                boolean wrongAlphaOrder = firstLen == secondLen && firstWord.compareTo(secondWord) > 0;
                if (greaterDistance || wrongAlphaOrder) {
                    success.set(i, second);
                    success.set(j, first);
                }
            }
        }

        String[] results = new String[success.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = success.get(i).getWord();
        }
        return results;
    }

    public int calcDistance (String typed, String comparison) {
        int[][] arr = new int[typed.length() + 1][comparison.length() + 1];
        for (int i = 0; i < arr.length; i++) {
            arr[i][0] = i;
        }
        for (int j = 0; j < arr[0].length; j++) {
            arr[0][j] = j;
        }
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
}