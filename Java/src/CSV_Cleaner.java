import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import CSV_Parser.CSV_Parser;


/*wir lesen die Datei ein und schreiben sie in eine neue Datei*/

public class CSV_Cleaner{
    public static void main(String[] args) {
        FileReader reader = null;
        FileWriter writer = null;

        try{
            reader = new FileReader(args[0]);
            writer = new FileWriter(args[1]);
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Error opening Files!");
            return;
        }

        ArrayList<String[]> tweets = CSV_Parser.parse_original(reader);

        if(tweets == null) return;

        trim(tweets);

        CSV_Parser.write_csv(writer, tweets);

        return;
    }

/*schaut nach dem Flag True in der tabelle, um die truncated tweets auszusortieren*/
    private static void trim(ArrayList<String[]> tweets){
        int tweets_size = tweets.size();

/*damit Zeilen nicht uebersprungen werden , muessen wir in der if-Schleife den Index herunterzaehlen*/
        for(int i = 0; i < tweets_size; i++){
            String[] record = tweets.get(i);
            if (record[10].equals("True")){
                tweets.remove(i);
                i -= 1;
                tweets_size -= 1;
                continue;
            }
/*wir selektieren nur die Spalten, die wir aus der csv tabelle brauchen*/            
            String[] record_cleaned = {record[0], record[1], record[2], record[4], record[7], record[8]};
            tweets.set(i, record_cleaned);
        }
    }
}
