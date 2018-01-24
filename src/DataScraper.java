import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataScraper implements Runnable {

    private static HashMap<String, String> URLList;
    private static long startTime = System.currentTimeMillis();
    private static long spentTime;

    private static void setURLList(HashMap<String, String> URLList) {
        DataScraper.URLList = URLList;
    }
    public static HashMap<String, String> getURLList() {
        return URLList;
    }
    public static long getSpentTime() {
        return spentTime;
    }

    @Override
    public void run() {
        Set<Map.Entry<String, String>> set = URLList.entrySet();
        for (Map.Entry<String, String> url: set) {
            Scraper(url.getKey());
        }
        spentTime = System.currentTimeMillis() - startTime;
    }

    DataScraper(HashMap<String, String> URLList){
        setURLList(URLList);
    }

    private static void Scraper(String url) {
        String html = getURL(url);
        if (html.isEmpty()){
            URLList.remove(url);
            return;
        }
        Parser(html, url);
    }
    private static void Parser(String html, String url) {
        HTMLParser parser = new HTMLParser();
        try {
            parser.parse(html);
            URLList.put(url, parser.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getURL(String url){
        URL urlObj;
        try {
            urlObj = new URL(url);

        } catch (MalformedURLException e) {
            System.out.println("URL is malformed!");
            return "";
        }
        URLConnection URLConnection;
        BufferedReader in;
        StringBuilder output = new StringBuilder();
        try {
            URLConnection = urlObj.openConnection();
            in = new BufferedReader(new InputStreamReader(URLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("No connection to URL: " + url);
            return "";
        }
        return output.toString();
    }

}
