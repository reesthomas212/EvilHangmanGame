package com.hangman;

import java.io.*;
import java.util.*;
import java.lang.*;



/**
 * Created by mlsfan2010 on 7/6/2017.
 */

public class EvilHangmanGame implements IEvilHangmanGame {

    public TreeSet<String> Dictionary = new TreeSet<>();
    public TreeSet<String> currentPartition = new TreeSet<>();
    public String oldKey;
    public String newKey;
    int LENGTH_OF_WORD = 0;
    TreeSet<String> used_Chars = new TreeSet<>();
    int GuessException = 0;



    public void startGame(File dictionary, int wordLength) throws FileNotFoundException //may not be right
    {
        LENGTH_OF_WORD = wordLength; //store word length in EHG object
        //create dictionary if readable and not empty
        if (!(dictionary.canRead())) {System.out.println("File not readable."); return;}
        if (dictionary.length() == 0) {System.out.println("File is empty."); return;}
        Scanner scan = new Scanner(dictionary);
        createDictionary(scan);
        if (Dictionary.size() == 0) {System.out.println("Error with dictionary input."); return;}
        //create first partition using wordLength
        Scanner scan2 = new Scanner(dictionary);
        createFirstPartition(scan2, wordLength);


        //check size of first partition
        if (currentPartition.size() == 0){System.out.println("No words of requested word length."); return;}
    }
    public void createDictionary(Scanner scan)
    {
        //need to check for special characters inside input
        while (scan.hasNext())
        {
            String input = scan.next().toLowerCase();   //grab word from file, change to lowercase
            StringBuilder S = new StringBuilder(input); //create StringBuilder that can look at each char
            if (checkInputFromDictionary(S) == false) {}
            else
            {
                Dictionary.add(input);
            }
        }
    }
    public boolean checkInputFromDictionary(StringBuilder input)
    {
        for (int i = 0; i < input.length(); i++) //for every character in input
        {
            char c = input.charAt(i); // This gives the character 'a'
            int ascii = (int) c;
            if (ascii > 96 && ascii < 123) //check if character is a lowercase letter - '97' to '122'
            {}
            else
            {
                return false;
            }
        }
        return true;
    }
    public void createFirstPartition(Scanner scan, int wordLength)
    {

        while (scan.hasNext())
        {
            String input = scan.next().toLowerCase();
            if (input.length() == wordLength)
            {
                currentPartition.add(input);
            }
        }
    }
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException
    {


        //check if guess has already been guessed

        try{
            if (newKey != null)
            {
                oldKey = newKey;
            }
            else //for first time
            {
                oldKey = createFirstKey(LENGTH_OF_WORD);
            }
            //check for already guessed letter
            StringBuilder S = new StringBuilder();
            S.append(guess);
            if (used_Chars.contains(S.toString()))
            {
                GuessException = 1;
                throw new GuessAlreadyMadeException();
            }
            else
            {
                GuessException = 0;
                used_Chars.add(S.toString());
            }
            //if not already guessed
            HashMap<String, TreeSet<String>> partitions = createPartitions(guess);
            currentPartition.addAll(determineBestPartition(partitions));
        } catch (GuessAlreadyMadeException Ex)
        {
            System.out.println("Already guessed");
        }
        return currentPartition;
    }
    public TreeSet<String> getUsed_Chars()
    {
        return used_Chars;
    }
    public String getOldKey() {return oldKey;}
    public String getNewKey() {return newKey;}
    public void setOldKey(String str) {oldKey = str;}
    public HashMap<String, TreeSet<String>> createPartitions(char guess)
    {
        HashMap<String, TreeSet<String>> partitions = new HashMap<>();

        while (!(currentPartition.isEmpty()))//create partitions
        {
            String currKey = createKey(currentPartition.first(), guess);
            if (partitions.containsKey(currKey)) //if key already exists, add word to set in map and move on
            {
                partitions.get(currKey).add(currentPartition.first());
                currentPartition.remove(currentPartition.first());
            }
            else //if key doesn't exist, create new set and add it to map, then add word to set
            {
                TreeSet<String> newSet = new TreeSet<>();
                partitions.put(currKey, newSet);
                partitions.get(currKey).add(currentPartition.first());
                currentPartition.remove(currentPartition.first());
            }
        }
        return partitions;
    }
    public String createKey(String currWord, char guess)
    {
        char[] OLD = oldKey.toCharArray();
        char[] currArray = currWord.toCharArray();

        for (int i = 0; i < OLD.length; i++)
        {
            if (currArray[i] == guess) //if guess matches that letter in currWord
            {
                OLD[i] = guess;
            }
        }
        StringBuilder S = new StringBuilder();
        for (int i = 0; i < OLD.length; i++)
        {
            S.append(OLD[i]);
        }
        return S.toString();
    }
    public class Obj
    {
        int num = -1;
        String key = new String();
        public int getNum()
        {
            return num;
        }
        public void setNum(int NUM)
        {
            num = NUM;
        }
        public String getKey()
        {
            return key;
        }
        public void setKey(String KEY)
        {
            key = KEY;
        }
    }
    public TreeSet<String> determineBestPartition(HashMap<String, TreeSet<String>> partitions)
    {
        Set<String> SK = partitions.keySet();
        TreeSet<String> temp = new TreeSet<>();
        temp.addAll(SK);
        //first net
        int biggest_size = 0;
        while (!(temp.isEmpty()))//determine highest size
        {
            if (partitions.get(temp.first()).size() > biggest_size)
            {
                biggest_size = partitions.get(temp.first()).size();
            }
            temp.remove(temp.first());
        }
        //remove smaller sets from map
        TreeSet<String> temp4 = new TreeSet<>();
        temp4.addAll(SK);
        for (String s : temp4)
        {
            if (partitions.get(s).size() < biggest_size)
            {
                partitions.remove(s);
            }
        }
        //check if there's only one left
        if (partitions.size() == 1)
        {
            for(String s : SK)
            {
                newKey = s;
                return partitions.get(s);
            }
        }

        //second net
        int lowest_num_of_guessed = 10000;
        HashSet<Obj> Object_Set = new HashSet<>();
        for (String str : SK)//for all elements in keySet
        {
            StringBuilder S = new StringBuilder();
            S.append(getGuessedLetters(str));
            Obj obj = new Obj();
            obj.setKey(str);
            obj.setNum(Integer.parseInt(S.toString()));
            Object_Set.add(obj);
        }
        //find lowest # of guessed letters
        for (Obj o : Object_Set)
        {
            if (o.getNum() < lowest_num_of_guessed)
            {
                lowest_num_of_guessed = o.getNum();
            }
        }
        // get rid of all keys and partitions that don't have lowest # of guessed
        for (Obj o : Object_Set)
        {
            if (o.getNum() != lowest_num_of_guessed)
            {
                partitions.remove(o.getKey());
            }
        }
        if (partitions.size() == 1)
        {
            for(String s : SK)
            {
                newKey = s;
                return partitions.get(s);
            }
        }
        //third net
        for (int i = LENGTH_OF_WORD - 1; i > -1; i--)
        {
            TreeSet<String> found = new TreeSet<>();
            TreeSet<String> notfound = new TreeSet<>();
            for (String s : SK)
            {
                char[] array = s.toCharArray();
                if (array[i] != '-') //found letter
                {
                    found.add(s);
                }
                else // letter not found
                {
                    notfound.add(s);
                }
            }
            if (found.size() != 0)
            {
                for (String s : notfound)
                {
                    partitions.remove(s);
                }
            }
            if (partitions.size() == 1)
            {
                for(String s : SK)
                {
                    newKey = s;
                    return partitions.get(s);
                }
            }
        }
        return partitions.get(newKey);
    }
    public int getGuessedLetters(String key)
    {
        int num = 0;
        char[] KEY = key.toCharArray();
        for (int i = 0; i < KEY.length; i++)
        {
            if (KEY[i] == '-')
            {

            }
            else
            {
                num++;
            }
        }
        return num;
    }
    public String createFirstKey(int l)
    {
        StringBuilder S = new StringBuilder();
        for (int i = 0; i < l; i++)
        {
            S.append("-");
        }
        return S.toString();
    }
}

