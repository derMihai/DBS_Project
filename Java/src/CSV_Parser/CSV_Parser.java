package CSV_Parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/*wir erstellen die Klasse*/

public class CSV_Parser{
    //originelles Header
    public static final String[] METADATA_ORIGINAL = { "handle",
            "text",
            "is_retweet",
            "original_author",
            "time",
            "in_reply_to_screen_name",
            "is_quote_status",
            "retweet_count",
            "favorite_count",
            "source_url",
            "truncated" };

    //header für die bereinigte Datei
    public static final String[] METADATA_CLEANED  = { "handle",
            "text",
            "is_retweet",
            "time",
            "retweet_count",
            "favorite_count" };


    public static ArrayList parse_original(FileReader reader){
        return parse(reader, METADATA_ORIGINAL);
    }

    public static ArrayList parse_cleaned(FileReader reader){
        return parse(reader, METADATA_CLEANED);
    }

    //Aus CSV-Datei 2-Dimensionales String-Array erzeugen
    private static ArrayList parse(FileReader reader, String[] header){
        ArrayList tweets = new ArrayList<String[]>();
        CSVParser parser = null;
        List<CSVRecord> record_list = null;

        CSVFormat format = CSVFormat.DEFAULT.withHeader(header).withDelimiter(';').withQuote('"').withRecordSeparator('\n');

        try{
            parser = new CSVParser(reader, format);
            record_list = parser.getRecords();

            for(int i = 1; i < record_list.size(); i++){
                CSVRecord record = record_list.get(i);
                String[] tweet = new String[header.length];

                tweets.add(tweet);

                for(int j = 0; j < header.length; j++){
                    tweet[j] = record.get(header[j]);
                }
            }
        } catch (Exception e){
            System.err.println("Error while Parsing CSV File!");
            e.printStackTrace();
            return null;
        }
        return tweets;
    }
    //neu-generierte, bereinigte Array in eine neue Datei

    public static void write_csv(FileWriter writer, ArrayList<String[]> records){
        CSVFormat format = CSVFormat.DEFAULT.withHeader(METADATA_CLEANED).withDelimiter(';').withQuote('"').withRecordSeparator('\n');
        CSVPrinter printer = null;

        try{
            printer = new CSVPrinter(writer, format);

            for(int i = 0; i < records.size(); i++){
                String[] record = records.get(i);
                List<String> l_record = Arrays.asList(record);
                printer.printRecord(l_record);
            }
            printer.flush();
        } catch (Exception e){
            System.err.println("Error while writing destination CSV File!");
            e.printStackTrace();
        }

        return;
    }
}
