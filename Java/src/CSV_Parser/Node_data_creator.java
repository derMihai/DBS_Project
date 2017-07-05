package CSV_Parser;

import net.sf.javaml.core.Dataset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Erzeugt CSV-Dateien mit informationen für
 */
public class Node_data_creator {
    private static String[] colors = {"rgb(255,51,51)","rgb(255,153,51)","rgb(255,255,51)","rgb(153,255,51)","rgb(51,255,153)","rgb(51,153,255)",
            "rgb(153,51,255)","rgb(255,51,255)","rgb(255,51,153)","rgb(160,160,160)"};

    public static void main(String[] args){
        FileWriter hashtag_writer;
        FileWriter calong_writer;

        try{
            hashtag_writer = new FileWriter(args[0]);
            calong_writer = new FileWriter(args[1]);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        get_data(hashtag_writer, calong_writer);

    }

    private static boolean get_data(FileWriter hashtags_writer, FileWriter calong_writer){
        /*mit der Datenbank verbinden*/
        Connection db_conn;
        try{
            Class.forName("org.postgresql.Driver");
            db_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/election?currentSchema=dbs_schema1",
                    "testuser","testpass123");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error accessing DB!");
            return false;
        }

        Statement stmt = null;

        ArrayList<String[]> h_records = new ArrayList<>();
        ArrayList<String[]> ca_records = new ArrayList<>();
        
        String get_hashtags_query =     "SELECT hname, importance, cluster "
                                    +   "FROM hashtag;";

        String get_occurences = "SELECT COUNT (*) "
                            +   "FROM contains "
                            +   "WHERE hname=? "
                            +   "GROUP BY hname";

        String get_calong_query =   "SELECT hname1, hname2 "
                                +   "FROM comesalong;";


        try {
            stmt = db_conn.createStatement();
            PreparedStatement pstmt;

            ResultSet rset = stmt.executeQuery(get_hashtags_query);

            String[] record;
            while (rset.next()){
                record = new String[4];
                record[0] = rset.getString(1);
                record[1] = rset.getString(2);
                //record[2] = rset.getString(3);
                record[3] = colors[rset.getInt(3)];

                h_records.add(record);
            }

            for(String[] r : h_records){
                pstmt = db_conn.prepareStatement(get_occurences);
                pstmt.setString(1, r[0]);
                rset = pstmt.executeQuery();
                rset.next();
                r[2] = rset.getString(1);
            }

            rset = stmt.executeQuery(get_calong_query);

            while(rset.next()){
                record = new String[2];
                record[0] = rset.getString(1);
                record[1] = rset.getString(2);

                ca_records.add(record);
            }
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error query!");
            return false;
        }

        CSVPrinter printer;
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';').withQuote('"').withRecordSeparator('\n');

        try{
            printer = new CSVPrinter(hashtags_writer, format);

            for(String[] record : h_records){
                List<String> l_record = Arrays.asList(record);
                printer.printRecord(l_record);
            }
            printer.flush();

            printer = new CSVPrinter(calong_writer, format);

            for(String[] record : ca_records){
                List<String> l_record = Arrays.asList(record);
                printer.printRecord(l_record);
            }
            printer.flush();

        } catch (Exception e){
            System.err.println("Error while writing destination CSV File(s)!");
            e.printStackTrace();
        }
        
        
        return true;
    }

}
