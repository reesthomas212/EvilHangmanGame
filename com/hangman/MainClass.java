package com.hangman; //change this to package hangman when trying to pass off

import java.io.*;
import java.util.*;
import java.lang.*;

public class MainClass {
    public static void main(String[] args) throws FileNotFoundException{
        EvilHangmanGame EHG = new EvilHangmanGame();

        try {
            //pass in command line parameters
            File temp = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            int guesses = Integer.parseInt(args[2]);
            //call first method to "set up" game
            EHG.startGame(temp, wordLength);

            //create currentPartition and put it in map of partitions
            TreeSet<String> currentPartition = EHG.currentPartition;

            //create first key of "-----"
            String currKey = createFirstKey(currentPartition.first().length());

            //start playing the game
            while (guesses != 0)
            {
                /*
                    At beginning of each turn:
                    - used_Chars needs to be updated
                    - currentPartition needs to be updated and be best partition for evil algorithm
                    - guesses needs to have decreased if there was an unsuccessful guess
                */

                beginTurn(EHG.getUsed_Chars(), guesses, currKey);
                char c = enterGuess();
                currentPartition.addAll(EHG.makeGuess(c));
                while (EHG.GuessException == 1)
                {
                    c = enterGuess();
                    currentPartition.addAll(EHG.makeGuess(c));
                }
                int changes = checkKeys(EHG.getOldKey(), EHG.getNewKey());
                EHG.setOldKey(EHG.getNewKey());
                currKey = EHG.getNewKey();


                if (changes == 0)//true = same, false = different
                {
                    guesses--;
                    System.out.println("Sorry, there are no " + c + "'s");
                    System.out.println();
                }
                else
                {
                    System.out.println("Yes, there is " + changes + " " + c);
                    System.out.println();
                }
                if (getNumOfDashes(EHG.getNewKey()) == 0)
                {
                    System.out.println("You win!");
                    System.out.println("The word was: " + EHG.getNewKey());
                    break;
                }
            }
            if (guesses == 0 && getNumOfDashes(EHG.getNewKey()) != 0)
            {
                System.out.println("You lose!");
                System.out.println("The word was: " + currentPartition.first());
            }



        } catch (FileNotFoundException Ex) {
            System.out.println("ERROR: File not found");
        } catch (ArrayIndexOutOfBoundsException Ex){
            System.out.println("ERROR: Outside command line array");
        } catch (NumberFormatException Ex) {
            System.out.println("ERROR: This is not a number");
            System.out.println(Ex.getMessage());
        } catch (IEvilHangmanGame.GuessAlreadyMadeException e) {
            System.out.println("ERROR: Already guessed");
        }

    }
    public static String createFirstKey(int l)
    {
        StringBuilder S = new StringBuilder();
        for (int i = 0; i < l; i++)
        {
            S.append("-");
        }
        return S.toString();
    }
    public static void beginTurn(Set<String> used_Chars, int guesses, String currKey)
    {
        System.out.println("You have " + guesses + " guesses left"); //first line
        System.out.print("Used letters:"); //second line
        for (String s : used_Chars)
        {
            System.out.print(" " + s);
        }
        System.out.println();
        System.out.println("Word: " + currKey); //third line

    }
    public static char enterGuess()
    {
        char c = '#';
        boolean check = true;
        while (check)
        {
            System.out.print("Enter guess: "); //fourth line
            Scanner temp = new Scanner(System.in);
            String S = temp.nextLine();
            S = S.toLowerCase();
            if (S.length() == 1)
            {
                if (S.charAt(0) < 123 && S.charAt(0) > 96)
                {
                    c = S.charAt(0);
                    check = false;
                }
                else
                {
                    System.out.println("Invalid input");
                }
            }
            else
            {
                System.out.println("Invalid input");
            }
        }
        return c;
    }
    public static int checkKeys(String oldKey, String newKey)
    {
        //boolean check = true;
        char[] OLD = oldKey.toCharArray();
        char[] NEW = newKey.toCharArray();
        int changes = 0;
        if (OLD.length == NEW.length)
        {
            for (int i = 0; i < OLD.length; i++)
            {
                if (OLD[i] != NEW[i])
                {
                    changes++;
                }
            }
        }

        return changes;
    }
    public static int getNumOfDashes(String key)
    {
        char[] array = key.toCharArray();
        int dashes = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == '-')
            {
                dashes++;
            }
        }
        return dashes;
    }
}



