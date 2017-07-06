package CSV_Parser;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;


/**
 * Erzeugt CSV-Dateien mit informationen für
 */
public class Node_data_creator {
    private static String[] colors = {"rgb(255,51,51)","rgb(255,153,51)","rgb(255,255,51)","rgb(153,255,51)","rgb(51,255,153)","rgb(51,153,255)",
            "rgb(153,51,255)","rgb(255,51,255)","rgb(255,51,153)","rgb(160,160,160)"};

    private static FileWriter json_writer;
    public static void main(String[] args){


        try{
            json_writer = new FileWriter(args[0]);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        get_data();
    }

    private static boolean get_data(){
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

        Statement stmt;

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

        try{

            JSONArray NodeJsonArray = new JSONArray();
            JSONArray EdgeJsonArray = new JSONArray();
            JSONObject FinalObject = new JSONObject();

            JSONObject obj;
            String[] record;
            for (int i = 0; i < h_records.size(); i++){
                obj = new JSONObject();
                record = h_records.get(i);
                //obj.put("id", Integer.toString(i));
                obj.put("id", record[0]);
                obj.put("label", record[0]);
                obj.put("x", record[1]);
                obj.put("y", record[2]);
                obj.put("color", record[3]);
                obj.put("type", "tweetegy");
                obj.put("size", 100);

                NodeJsonArray.put(obj);
            }
            FinalObject.put("nodes", NodeJsonArray);

            for (int i = 0; i < ca_records.size(); i++){
                obj = new JSONObject();
                record = ca_records.get(i);
                obj.put("id", Integer.toString(i));
                obj.put("source", record[0]);
                obj.put("target", record[1]);

                EdgeJsonArray.put(obj);
            }

            FinalObject.put("edges", EdgeJsonArray);

            FinalObject.write(json_writer,2, 1);
            json_writer.flush();
            json_writer.close();

        } catch (Exception e){
            System.err.println("Error while writing destination JSON File!");
            e.printStackTrace();
        }
        
        
        return true;
    }

}
