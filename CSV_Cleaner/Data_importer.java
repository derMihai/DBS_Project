import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Data_importer{
    public static void main(String[] args) {
        FileReader reader = null;

        try{
            reader = new FileReader(args[0]);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Error opening File!");
            return;
        }

        ArrayList<String[]> records = CSV_Parser.parse_cleaned(reader);
        ArrayList<Tweet> tweets = null;

        tweets = create_tweets(records);
    }

    public static ArrayList<Tweet> create_tweets(ArrayList<String[]> records){
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        for(int i = 1; i < records.size(); i++){
            String[] record = records.get(i);
            Tweet tweet = new Tweet();

            tweet.handle = record[0];
            tweet.text = record[1];

            if(record[2].equals("True"))    tweet.is_retweet = true;
            else                            tweet.is_retweet = false;

            tweet.time = record[3];
            tweet.retweet_count = Integer.parseInt(record[4]);
            tweet.favorite_count = Integer.parseInt(record[5]);

            tweets.add(tweet);
            parse_hashtags(tweet);
        }
        return tweets;
    }

    public static void parse_hashtags(Tweet tweet){
        String regex = "#(\\w)+(\\b|\\z)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tweet.text);
        while(matcher.find()){
            String hashtag = tweet.text.substring(matcher.start()+1, matcher.end());
            System.out.println(hashtag);
            tweet.hashtags.add(hashtag);
        }
    }
}
