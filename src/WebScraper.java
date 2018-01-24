import java.io.*;
import java.util.*;

public class WebScraper {

    private static HashMap<String, String> URLList;
    private static long scrapTime = 0;
    static {
        URLList = new HashMap<>();
    }
    private static ArrayList<String> WordsList;

    public static long getScrapTime() {
        return scrapTime;
    }

    public static void main(String[] args) {
        getArgs(args);
    }

    private static void getArgs(String[] args)  {
        if (args.length == 0) {
            System.out.println("Out of arguments.");
            System.exit(1);
        }
        int n = 0;
        for (; n < args.length; n++) {
            if (isFile(args[n])||isURL(args[n])) {
                Thread scraper;
                scraper = new Thread(new DataScraper(URLList));
                scraper.start();
                try {
                    scraper.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                URLList = DataScraper.getURLList();
                scrapTime += DataScraper.getSpentTime();
            }else {
                if(!isWordsToCount(args[n++])) n--;
                String[] subArgs = new String[args.length-n];
                System.arraycopy(args, n, subArgs, 0, subArgs.length);
                Thread processing = new Thread(new Processing(URLList, WordsList, subArgs));
                processing.start();
                n = args.length;
            }
        }

    }

    private static boolean isWordsToCount(String arg){
        if (arg.matches("-[wcev]"))            //if it's not a word
            return false;
        else {
            WordsList = new ArrayList<>();
            if (arg.contains(",")) {
                String[] buffer = arg.split(",");
                Collections.addAll(WordsList, buffer);
            } else WordsList.add(arg);
        }
        return true;
    }
    private static boolean isURL(String arg){
        if (arg.matches("http(.*)://(.*)\\.(.*)/(.*)")) {
            URLList.put(arg, null);
            return true;
        }
        return false;
    }
    private static boolean isFile(String address){
        try (FileInputStream file = new FileInputStream(address)){
            byte[] buf = new byte[1024];
            int length = file.read(buf);
            String urlBuffer = new String(buf, 0, length);
            String split = "http";
            String[] URLs = urlBuffer.split(String.valueOf(split));
            for (int i = 1; i < URLs.length; i++) {
                if (URLs[i].endsWith("\r\n")){
                    URLs[i] = URLs[i].substring(0, URLs[i].indexOf("\r\n"));
                }
                URLs[i] = URLs[i].trim();
                URLList.put("http" + URLs[i], null);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



}
