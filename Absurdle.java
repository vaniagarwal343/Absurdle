// Vani Agarwal
// 02/16/2023
// CSE 122 Section BE
// TA: Kevin Cuu Nguyen

// This program codes the game "Absurdle" - It allows a user to play the game. 

import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // TODO: Write your code here! 

    // Method: pruneDictionary
    // This method that takes the List<String> containing the contents of the dictionary file 
    // as a parameter and the user's chosen word length, and return a Set<String> that 
    // contains only the words from the dictionary that are the length specified 
    // by the user and eliminates any duplicates.  
    // Parameters: 
    // List<String> contents: List of Strings with the words in the chosen dictionary 
    // int wordLength: length of words picked by user
    // Returns: 
    // Set<String> words: updated dictionary of words with correct length
    // Exceptions: 
    // throws IllegalArgumentException if the wordLength by the user is less than 1.

    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1) {
            throw new IllegalArgumentException();
        }
        Set<String> words = new TreeSet<String>();
        for (int i = 0; i < contents.size(); i ++) {
            String word = contents.get(i);
            int length = word.length();
            if (length == wordLength) {
                words.add(word);
            }
        } 
        return words;
    }

    // Method: record 
    // This method handles the user's guess and considers the possible 
    // patterns given the user's guess and the possible answers it still 
    // has left to choose from. It chooses the pattern that corresponds to 
    // the largest set of words still remaining, using the helper. The record method updates 
    // the current set of words based on the guess to be the selected largest set
    // method (max);
    // Paramaters: 
    // String guess: User's guess
    // Set<String> words: updated dictionary of words
    // int wordLength: length of words chosen by user.
    // Returns: 
    // String guess1: from the max method, returns the pattern for the guess
    // Exceptions: 
    // Throws new IllegalArgumentException if the set of words is empty or if 
    // the guess does not have the correct length.

    public static String record (String guess, Set<String> words, int wordLength) {
        if (words.isEmpty() || guess.length() != wordLength) {
           throw new IllegalArgumentException(); 
        }

        Map<String, Set<String>> finalSet = new TreeMap<>();
        for (String str : words) {
            String pattern = patternFor(str, guess);
            if (!finalSet.containsKey(pattern)) {
                finalSet.put(pattern, new TreeSet<>());
            }
            finalSet.get(pattern).add(str);
        }
        return max(finalSet, words);
    }

    // Method: max
    // This method chooses the pattern that corresponds to 
    // the largest set of words still remaining
    // Paramaters: 
    // Map<String, Set<String>> finalSet: contains the map of guesses to their pattern 
    // Set<String> words: updated dictionary of words
    // Returns: 
    // String guess1: returns the chosen pattern for the guess

    public static String max (Map<String, Set<String>> finalSet, Set<String> words) {
        Set<String> temp = null;
        String guess1 = "";
        for (String patternGuess : finalSet.keySet()) {
            Set<String> current = finalSet.get(patternGuess);
            if (temp == null || current.size() > temp.size()) {  
                guess1 = patternGuess;
                temp = current;
            }
        }
        words.clear();
        words.addAll(temp);
        return guess1;
    }

    // Method: patternFor
    // This method generates the pattern of boxes for a given target word and guess. 
    // Paramaters: 
    // String word: the target word
    // String guess: the word guessed by the user
    // Returns: 
    // String finalStr: the final pattern of boxes in a String

    public static String patternFor(String word, String guess) {
        String[] boxPattern = new String[word.length()];
        int length = guess.length();
        Map<Character, Integer> countPattern = new TreeMap<Character, Integer>();
        for (int i = 0; i < length; i++) {
            char letter = word.charAt(i);
            if (!countPattern.containsKey(letter)) {
                countPattern.put(letter, 0);
            }
            countPattern.put(letter, countPattern.get(letter) + 1);
        }
        for (int i = 0; i < length; i++) {
            char letter = guess.charAt(i);
            if (letter == word.charAt(i)) {
                boxPattern[i] = "!";
                countPattern.put(letter, countPattern.get(letter) - 1);
            }
        }
        for (int i = 0; i < length; i++) {
            char letter = guess.charAt(i);
            if (countPattern.containsKey(letter) && boxPattern[i] != "!" 
            && countPattern.get(letter) != 0) {
                boxPattern[i] = "%";
                countPattern.put(letter, countPattern.get(letter) - 1);
            }
        }
        String finalStr = "";
        for (int i = 0; i < length; i++) {
            if (boxPattern[i] == null) {
                finalStr += "?";
            } else {
                finalStr += boxPattern[i];
            }
        }
        for (int i = 0; i < finalStr.length(); i ++) {
            char ch = finalStr.charAt(i);
            String str = "" + ch;
            if (ch == '!') {
                finalStr = finalStr.replace(str, "🟩");
            } else if (ch == '%') {
                finalStr =  finalStr.replace(str, "🟨");
            } else if (ch == '?') {
                finalStr = finalStr.replace(str, "⬜"); 
            }
        }
        return finalStr;
    }
}
