import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileReader;
import java.util.ArrayList;
import CSV_Parser.CSV_Parser;
import CSV_Parser.Tweet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;


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
        send_to_db(tweets);
        return;
    }


    private static ArrayList create_tweets(ArrayList<String[]> records){
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

            parse_hashtags(tweet);
            tweets.add(tweet);
        }
        return tweets;
    }

    public static void parse_hashtags(Tweet tweet){
        String regex = "#(\\w)+(\\b|\\z)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tweet.text);
        while(matcher.find()){
            String hashtag = tweet.text.substring(matcher.start()+1, matcher.end());
            //System.out.println(hashtag);
            tweet.hashtags.add(hashtag);
        }
    }

    private static void send_to_db(ArrayList<Tweet> tweets){
        Connection db_conn;

        try{
            Class.forName("org.postgresql.Driver");
            db_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/election?currentSchema=dbs_schema1",
                    "testuser","testpass123");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error accessing DB!");
            return;
        }

        Statement statement = null;

        try {
            statement = db_conn.createStatement();
            for(Tweet tweet : tweets){


                String date = tweet.time.substring(0,10);
                String time = tweet.time.substring(11,19);
                String timestamp = date + " " + time;
                System.out.println(timestamp);

                String tweet_insQuery =    "INSERT INTO tweet (pname,datum,retweets,likes,retweet,content,importance) "+
                        "VALUES ('"  + tweet.handle      + "',"
                        + "TIMESTAMP '"+ timestamp         + "',"
                        + tweet.retweet_count    + ','
                        + tweet.favorite_count       + ','
                        + " '" +tweet.is_retweet    + "' "
                        + " '" +tweet.text        + "',"
                        + 1                 + ");";

                statement.executeUpdate(tweet_insQuery);

                for(String hashtag : tweet.hashtags){
                    String contains_insQuery = "INSERT INTO contains(pname, hname, datum) ("
                            + "'" + tweet.handle  + "',"
                            + "'" + hashtag       + "',"
                            + "TIMESTAMP '"+timestamp         + "');";
                    statement.executeUpdate(contains_insQuery);
                }

                ArrayList<String[]> hashtag_combis = combine_hashtags(tweet);
                for(String[] comb : hashtag_combis){
                    String comesAlong_upQuery =     "UPDATE comesAlong SET pairOccurences = pairOccurences + 1 "
                            + "WHERE (hname1= '" + comb[0] + "' AND hname2= '" + comb[1] + "') "
                            + "OR (hname1= '" + comb[1] + "' AND hname2= '" + comb[0] + "');";

                    String comesAlong_insQuery =    "INSERT INTO comesAlong (hname1, hname2, pairOccurences) "
                            + "SELECT '" + comb[0] + "', '" + comb[1] + "', 1 "
                            + "WHERE NOT EXISTS (SELECT 1 FROM comesAlong "
                            + "WHERE (hname1= '" + comb[0] + "' AND hname2= '" + comb[1] + "') "
                            + "OR (hname1= '" + comb[1] + "' AND hname2= '" + comb[0] + "'));";
                    statement.executeUpdate(comesAlong_upQuery + " " + comesAlong_insQuery);
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error query!");
            return;
        }
    }

    private static ArrayList<String[]> combine_hashtags(Tweet tweet){
        ArrayList<String[]> combis = new ArrayList<String[]>();

        for(int i = 0; i < tweet.hashtags.size()-1; i++){
            for(int j = i+1; j < tweet.hashtags.size(); j++){
                String[] comb = {tweet.hashtags.get(i), tweet.hashtags.get(j)};
                combis.add(comb);
            }
        }
        return combis;
    }
}