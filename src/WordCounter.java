import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * A WordCounter class would request news items from HackerNews via the REST-API and count the words in the
 * text property after parsing the response into an object
 *
 * @author Vishnu Kandanelly
 */
public class WordCounter {
    private static HttpsURLConnection connection;

    public static void main(String[] args) {

        final int START = 1000000;
        final int END = 1001000;
        int wordCounter = 0;
        BufferedReader reader;
        String line;
//        StringBuilder responseContent = new StringBuilder();
        String responseContent = "";
        System.out.println("WordCount | URL");
        for (int id = START; id <= END; id++) {
            try {
                URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + id + ".json");
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();

                if (status > 299) {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                while ((line = reader.readLine()) != null) {
                    responseContent = line;
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(responseContent);

                //if the json object does not have a text property, the word counter of that item is set to zero.
                if (jsonObject.has("text")) {
                    String text = jsonObject.getString("text");
                    Pattern pattern = Pattern.compile("[a-zA-Z0-9|']+");
                    text = text.replaceAll("(<(.*?)>)(</(.*?)>)|(<(.*?)>)", "");
                    Matcher match = pattern.matcher(text);
                    while (match.find()) {
                        wordCounter++;
                    }
                }
                //printing word count along with its URL
                System.out.printf("%3d | " + url + "%n", wordCounter);
                //initializing wordCounter to zero for the new News item.
                wordCounter = 0;

            } catch (MalformedURLException ex) {
                Logger.getLogger(WordCounter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WordCounter.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                connection.disconnect();
            }
        }
    }
}
