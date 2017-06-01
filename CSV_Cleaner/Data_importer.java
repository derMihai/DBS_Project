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
        for (Tweet t : tweets){
            System.out.print(t.text + " | ");
            for (String s : t.hashtags){
                System.out.print(s + ' ');
            }
            System.out.println("");
        }
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
            System.out.println(hashtag);
            tweet.hashtags.add(hashtag);
        }
    }

    private static void send_to_db(ArrayList<Tweet> tweets){
        Connection db_conn;

        try{
            Class.forName("org.postgresql.Driver");
            db_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dbs",
                "testuser","testpass");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error accessing DB!");
            return;
        }

        Statement statement = null;

        try {
            statement = db_conn.createStatement();
//            ResultSet result = statement.executeQuery(query);

            // while (result.next()) {
            //     vorname = result.getString("vorname");
            //     nachname = result.getString("nachname");
            //     System.out.println("Student with Matrikelnummer " + matrikelnummer +
            //                             ": " + vorname + " " + nachname);
            //                         }
            for(Tweet tweet : tweets){


                String date = tweet.time.substring(0,9);
                String time = tweet.time.substring(10,17);

                String tweets_insQuery = "INSERT INTO tweet ()";
            }
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error query!");
            return;
        } finally {
            if (statement != null) statement.close();
        }
    }

    private static ArrayList<String[]> combine_hashtags(Tweet tweet){
        ArrayList<String[]> combis = new ArrayList<String[]>;

        for(int i = 0; i < tweet.hashtags.size()-1; i++){
            for(j = i+1; j < tweet.hashtags.size(); j++){
                String[] comb = {tweet.hashtags.get(i), tweet.hashtags.get(j)};
                combis.add(comb);
            }
        }
        return combis;
    }
}
