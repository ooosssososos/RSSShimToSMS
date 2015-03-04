package main.java.oscar;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String ACCOUNT_SID = "ACb5bf74478dbd8fee5c34edf46e5877c1";
    public static final String AUTH_TOKEN = "6caefe0f08e50952c08fabfb83785c71";

    public static void main(String[] args) throws IOException {

        File f = new File("data.txt");
        File fz = new File("numbers.txt");
        if (!fz.exists()) fz.createNewFile();
        if (!f.exists()) f.createNewFile();
        BufferedReader r = new BufferedReader(new FileReader(f));
        int maxGuid = -1;
        String line = r.readLine();
        if (line != null)
            maxGuid = Integer.parseInt(line);
        r.close();

        r = new BufferedReader(new FileReader(fz));
        ArrayList<String> numbers = new ArrayList<String>();
        String tmp = r.readLine();
        while (tmp != null) {
            numbers.add(tmp);
            tmp = r.readLine();
        }

        while (true) {
            try{
            boolean changed = false;
            RSSFeedParser parser = new RSSFeedParser("http://shimchemistry.tk/?cat=8&feed=rss2");
            Feed feed = parser.readFeed();
            FeedMessage max = feed.getMessages().get(0);
            int g = getId(max.getGuid());

            if (g > maxGuid) {
                maxGuid = g;

                for(String i : numbers){
                try {
                    sendMsg(i, max.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
                BufferedWriter w = new BufferedWriter(new FileWriter(f));
                System.out.println(maxGuid);
                w.write(maxGuid + "\n");
                w.close();

            }
            }catch(Exception e){

            }

            try {
                //Sleep 15m
                Thread.sleep(60000 * 15);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void sendMsg(String num, String in) throws Exception {
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

        // Build a filter for the MessageList
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Body", in));
        params.add(new BasicNameValuePair("To", num));
        params.add(new BasicNameValuePair("From", "+16047576888"));

        MessageFactory messageFactory = client.getAccount().getMessageFactory();
        Message message = messageFactory.create(params);
    }

    public static int getId(String guid) {
        return Integer.parseInt(guid.substring(guid.lastIndexOf("?p=") + 3, guid.length()));
    }
}
