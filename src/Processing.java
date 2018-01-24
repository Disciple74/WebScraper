import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Character.isLetter;

public class Processing implements Runnable {

    private static HashMap<String, String> URLList;
    private static ArrayList<String> WordsList;
    private static String[] args;
    private static long startTime = System.currentTimeMillis();

    private static void setArg(String[] args) {
        Processing.args = args;
    }

    private static void setURLList(HashMap<String, String> URLList) {
        Processing.URLList = URLList;
    }

    private static void setWordsList(ArrayList<String> wordsList) {
        WordsList = wordsList;
    }

    Processing(HashMap<String, String> URLList, ArrayList<String> WordsList, String[] args) {
        setURLList(URLList);
        setWordsList(WordsList);
        setArg(args);
    }


    @Override
    public void run() {
        if (URLList == null) {
            System.out.println("Wrong URL");
            return;
        }
        getCommands(args);
    }

    private static void getCommands(String[] args) {//Executing data commands from arguments
        for (int i = 0; i < args.length; i++){
            if (args[i].equals("-v")&& i!=args.length-1){
                String buf = args[i];
                args[i] = args[args.length-1];
                args[args.length-1] = buf;
             }
        }
        for (String arg : args) {
            switch (arg) {
                case "-w":
                    try {
                        for (String word : WordsList)
                            System.out.println("Word \"" + word + "\" occurred on this pages " + CountingWords(word) + " times");
                    } catch (NullPointerException e) {
                        System.out.println("There's no words to search for.");
                    }
                    break;
                case "-c":
                    CountingChars();
                    break;
                case "-e":
                    try {
                        for (String word : WordsList)
                            ExtractSentences(word);
                    } catch (NullPointerException e) {
                        System.out.println("There's no sentences to extract");
                    }
                    break;
                case "-v":
                    ShowTimeCounter();
                    break;
                default:
                    System.out.println("Wrong command: " + arg + "!");
            }
        }
    }

    private static void ShowTimeCounter() {//If there's a "-v" flag, program will show time spent on it's work.
        System.out.println("\n");
        SimpleDateFormat form = new SimpleDateFormat("mm:ss:SSS");
        System.out.println("WebScraper take a " + form.format(WebScraper.getScrapTime()) + " long for scraping\n" +
                "and a " + form.format(System.currentTimeMillis() - startTime) + " long for processing");
    }

    private static void ExtractSentences(String word) {//if there's a "-e" flag, program will extract needed sentences
        Set<Map.Entry<String, String>> set = URLList.entrySet();
        int count = 0;
        if (!set.isEmpty()) {
            try (FileWriter file = new FileWriter("Sentences_" + word + ".txt", false)) {
                for (Map.Entry<String, String> page : set) {
                    String text = page.getValue();
                    for (int i = -1; i < text.lastIndexOf(word); i = text.indexOf(word, i + 1)) {
                        int l = text.indexOf(word, i + 1) + 1,
                                l1 = text.lastIndexOf(". ", l) + 1,
                                l2 = text.indexOf(". ", l) + 1;
                        file.write(++count + ". " + text.substring(l1, l2).trim() + "\r\n");
                        file.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                File file1 = new File("Sentences_" + word + ".txt");
                if (file1.exists() && file1.length() > 0) {
                    System.out.println("\n");
                    System.out.println("File that contains word \"" + word + "\" is there:");
                    System.out.println(file1.getCanonicalPath());
                }
                BufferedReader sentences = new BufferedReader(new FileReader("Sentences_" + word + ".txt"));
                String buf;
                while ((buf = sentences.readLine()) != null)
                    System.out.println(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int CountingWords(String word) {

        int res = 0;
        Set<Map.Entry<String, String>> set = URLList.entrySet();
        if (!set.isEmpty())
            for (Map.Entry<String, String> page : set) {
                String text = page.getValue();
                for (int i = -1; i < text.lastIndexOf(word); i = text.indexOf(word, i + 1)) {
                    res++;
                }
            }
        else return 0;
        return res;
    }

    private static void CountingChars() {
        System.out.println("\n");
        int res = 0;
        Set<Map.Entry<String, String>> set = URLList.entrySet();
        if (!set.isEmpty()) {
            for (Map.Entry<String, String> page : set) {
                String text = page.getValue();
                for (int i = 0; i < text.length(); i++) {
                    char buf = text.charAt(i);
                    if (isLetter(buf))
                        res++;
                }
                System.out.println("There're " + res + " characters on \"" + page.getKey() + "\" page");
            }
        }
    }


}
