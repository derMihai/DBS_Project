package CSV_Parser;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;


/**
 * Erzeugt JSON-Dateien mit informationen für die Visualisierungen.
 * Usage: Node_data_creator <plots> <dates>
 *     where <plots> is the destination File containing the Hashtags and the connections between them, <dates> is the
 *     destination file with the days and number of hashtags in each day
 */

public class Node_data_creator {
    //colors assigned to clusters - for SigmaJS
    private static String[] colors = {"rgb(255,51,51)","rgb(255,153,51)","rgb(255,255,51)","rgb(153,255,51)","rgb(51,255,153)","rgb(51,153,255)",
            "rgb(153,51,255)","rgb(255,51,255)","rgb(255,51,153)","rgb(160,160,160)"};


    public static void main(String[] args){

        FileWriter json_writer;
        try{
            json_writer = new FileWriter(args[0]);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

               /*mit der Datenbank verbinden*/
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

        Statement stmt;
        ResultSet rset;

        ArrayList<String[]> h_records = new ArrayList<>();
        ArrayList<String[]> ca_records = new ArrayList<>();

        //gets each Hashtag from the database
        String get_hashtags_query =     "SELECT hname, importance, cluster "
                                    +   "FROM hashtag;";
        //Counts for each hashtag, how often it appears
        String get_occurences = "SELECT COUNT (*) "
                            +   "FROM contains "
                            +   "WHERE hname=? "
                            +   "GROUP BY hname";
        //get the hashtag-pairs
        String get_calong_query =   "SELECT hname1, hname2 "
                                +   "FROM comesalong;";
        //how many hashtags appeared in a day
        String get_day_occurences_query =   "SELECT datum, COUNT(datum) "
                                        +   "FROM (SELECT date_trunc('day', datum) AS datum FROM contains) AS c "
                                        +   "GROUP BY datum "
                                        +   "ORDER BY datum ASC;";
        //execute the statements, store the information blabla
        try {
            stmt = db_conn.createStatement();
            PreparedStatement pstmt;

            rset = stmt.executeQuery(get_hashtags_query);

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
            return;
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

                //We linearly space the x-Axis (importance) a little bit apart
                obj.put("x", Double.toString(20* Double.parseDouble(record[1])));
                //the spacing of the nodes on the y-Axis (hashtag total apeeareances) is horrible, so we normalize it
                obj.put("y", Double.toString(20*Math.sqrt(Math.sqrt(Integer.parseInt(record[2])))));
//                obj.put("x", record[1]);
//                obj.put("y", record[2]);
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
            return;
        }

        try{
            json_writer = new FileWriter(args[1]);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        try{
            rset = stmt.executeQuery(get_day_occurences_query);
            int i = 0;

            JSONArray days_array = new JSONArray();
            JSONObject day_object, wrapper_object;

            //int days[][] = new int[][2];

            while(rset.next()){
                day_object = new JSONObject();
                day_object.put("x", i);
                day_object.put("label", rset.getString(1).substring(0,10));
                day_object.put("y", Integer.parseInt(rset.getString(2)));

              //  days[i][0] = i;
              //  days[i][1] = rset.getInt(2);

                days_array.put(day_object);
                i++;
            }
            //wrapper_object = new JSONObject();
            //wrapper_object.put("days", days);

            days_array.write(json_writer,2,1);
            json_writer.flush();
            json_writer.close();

        } catch (Exception e){
            e.printStackTrace();
            return;
        }


        return;
    }

}
