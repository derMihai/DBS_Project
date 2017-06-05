package CSV_Parser;
import java.util.ArrayList;

public class Tweet{
    //Namen aus CSV-Datei
    public String   handle;
    public String   text;
    public Boolean  is_retweet;
    public String   time;
    public int      retweet_count;
    public int      favorite_count;

    public ArrayList<String> hashtags;

    public Tweet(){
        this.hashtags = new ArrayList<String>();
    }

}