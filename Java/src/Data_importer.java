import java.sql.PreparedStatement;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileReader;
import java.util.ArrayList;
import CSV_Parser.CSV_Parser;
import CSV_Parser.Tweet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;


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
        records = null;
        send_to_db(tweets);
        return;
    }

    //aus 2 Dimensionale Array mit Strings, array mit Tweet-Objekte erzeugen
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

/*wir schauen mit Hilfe eines regulären Ausdrucks nach hashtags im text und extrahieren*/

    public static void parse_hashtags(Tweet tweet){
        String regex = "#(\\w)+(\\b|\\z)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tweet.text);
        while(matcher.find()){
            String hashtag = tweet.text.substring(matcher.start()+1, matcher.end());
            //Avoid duplicates
            for(String h : tweet.hashtags){
                if(h.equals(hashtag)){
                    tweet.hashtags.remove(h);
                    break;
                }
            }
            tweet.hashtags.add(hashtag);
        }
    }

    private static void send_to_db(ArrayList<Tweet> tweets){
        Connection db_conn;
        /*mit der Datenbank verbinden*/
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

            PreparedStatement prepStat_tweet;
            PreparedStatement prepStat_contains;
            PreparedStatement prepStat_comesalong;
            //Für jedes Tweet...
            for(Tweet tweet : tweets){

                //in tweet Tabelle einfügen
                String date = tweet.time.substring(0,10);
                String time = tweet.time.substring(11,19);
                String timestamp = date + " " + time;
                System.out.println(timestamp);


                String tweet_ins = "INSERT INTO tweet" +
                        "(pname,datum,retweets,likes,retweet,content,importance)" +
                        "VALUES (?,?,?,?,?,?,?)";

                Timestamp ts = Timestamp.valueOf(timestamp);

                prepStat_tweet = db_conn.prepareStatement(tweet_ins);

                prepStat_tweet.setString(1, tweet.handle);
                prepStat_tweet.setTimestamp(2, ts);
                prepStat_tweet.setInt(3, tweet.retweet_count);
                prepStat_tweet.setInt(4, tweet.favorite_count);
                prepStat_tweet.setBoolean(5, tweet.is_retweet);
                prepStat_tweet.setString(6, tweet.text);
                prepStat_tweet.setDouble(7, Math.sqrt(Math.sqrt((tweet.favorite_count + tweet.retweet_count)/2)));

                prepStat_tweet.executeUpdate();

                //jeden Hashtag in einem Tweet in contains Tabelle einfügen
                for(String hashtag : tweet.hashtags){
                    String contains_ins =   "INSERT INTO contains(pname, hname, datum)" +
                            "VALUES(?,?,?)";

                    prepStat_contains = db_conn.prepareStatement(contains_ins);

                    prepStat_contains.setString(1, tweet.handle);
                    prepStat_contains.setString(2, hashtag);
                    prepStat_contains.setTimestamp(3, ts);

                    prepStat_contains.executeUpdate();
                }

                //jede Hashtag-Kombi in comesalong einfügen
                ArrayList<String[]> hashtag_combis = combine_hashtags(tweet);
                for(String[] comb : hashtag_combis){

                    String comesalong_up =  "UPDATE comesalong SET pairOccurences = pairOccurences + 1 " +
                            "WHERE (hname1 =? AND hname2 =?) " +
                            "OR (hname1 =? AND hname2 =?)";

                    prepStat_comesalong = db_conn.prepareStatement(comesalong_up);

                    prepStat_comesalong.setString(1, comb[0]);
                    prepStat_comesalong.setString(2, comb[1]);
                    prepStat_comesalong.setString(3, comb[1]);
                    prepStat_comesalong.setString(4, comb[0]);

                    prepStat_comesalong.executeUpdate();

                    String comesalong_ins = "INSERT INTO comesalong (hname1, hname2, pairoccurences) " +
                            "SELECT ?,?,? "+
                            "WHERE NOT EXISTS (SELECT 1 FROM comesalong WHERE (hname1=? AND hname2=?) OR (hname1=? AND hname2=?))";

                    prepStat_comesalong = db_conn.prepareStatement(comesalong_ins);

                    prepStat_comesalong.setString(1, comb[0]);
                    prepStat_comesalong.setString(2, comb[1]);
                    prepStat_comesalong.setInt(3, 1);
                    prepStat_comesalong.setString(4, comb[0]);
                    prepStat_comesalong.setString(5, comb[1]);
                    prepStat_comesalong.setString(6, comb[1]);
                    prepStat_comesalong.setString(7, comb[1]);

                    prepStat_comesalong.executeUpdate();
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error query!");
            return;
        }
    }

/*wir kombinieren je zwei Hashtags miteinander*/

    private static ArrayList<String[]> combine_hashtags(Tweet tweet){
        ArrayList<String[]> combis = new ArrayList<String[]>();

/*Alle Hashtag-2.er-Kombis in einem Tweet erzeugen */

        for(int i = 0; i < tweet.hashtags.size()-1; i++){
            for(int j = i+1; j < tweet.hashtags.size(); j++){
                String[] comb = {tweet.hashtags.get(i), tweet.hashtags.get(j)};
                combis.add(comb);
            }
        }
        return combis;
    }
}
