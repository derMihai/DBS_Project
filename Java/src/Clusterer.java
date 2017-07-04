//https://sourceforge.net/projects/java-ml/files/java-ml/0.1.5/
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

import java.sql.*;

public class Clusterer {
    private static Connection db_conn;

    public static void main(String args[]){
        DefaultDataset hashtags_dataset = new DefaultDataset();

        //holt Data und berechnet Metriken
        if(!get_data(hashtags_dataset)) return;

        //K-Means-clusterer-Objekt
        KMeans clusterer = new KMeans(10);

        //Berechnet die Clusters
        Dataset hashtag_clusters[] = clusterer.cluster(hashtags_dataset);

        //In Database speichern
        if(!store_data(hashtag_clusters)) return;

    }

    //nimmt alle Hashtags aus dem Database, berechnet die Metriken und fügt es letzendlich in
    //einen Dataset Structure
    private static boolean get_data(Dataset hashtags_dataset){
        /*mit der Datenbank verbinden*/
        try{
            Class.forName("org.postgresql.Driver");
            db_conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/election?currentSchema=dbs_schema1",
                    "testuser","testpass123");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error accessing DB!");
            return false;
        }

        Statement get_hashtags = null;
        /*gewählte Metriken:
            -importance: Durchschnitt über die wichtigkeit der Tweets, in den der Hashtag auftaucht
            -appeareances: Totale anzahl der Tweets, in denen der Hashtag auftaucht
            */
        try {
            String get_hashtags_query =     "SELECT hname, COUNT(hname), AVG(importance) "
                                        +   "FROM tweet t, contains c "
                                        +   "WHERE t.pname = c.pname AND t.datum = c.datum "
                                        +   "GROUP BY hname;";
            get_hashtags = db_conn.createStatement();
            //Dataset für die Hashtags
            ResultSet hashtags = get_hashtags.executeQuery(get_hashtags_query);

            String hname;
            double importance;
            int appearences;

            while (hashtags.next()){
                hname = hashtags.getString(1);
                appearences = hashtags.getInt(2);
                importance = hashtags.getDouble(3);
                //jeden Hashtag in Dataset hinzufügen
                hashtags_dataset.add(new Hashtag(hname, importance, appearences));
            }
        } catch (Exception e ) {
            e.printStackTrace();
            System.out.println("Error query!");
            return false;
        }
        return true;
    }

    /*
    Speichert die Hashtags in einer neuen Tabelle (hashtag), zusammen mit den Metriken und
    dem zugehörigen Cluster. Dadurch braucht man bei der Visualisierung nicht erneut zu berechnen.
     */
    private static boolean store_data(Dataset hashtag_clusters[]){
        try{
            PreparedStatement insert_hashtag_stmt;
            String insert_hashtag =     "INSERT INTO hashtag (hname, importance, appeareances, cluster) "+
                                        "VALUES (?,?,?,?)";
            for(int i = 0; i < hashtag_clusters.length; i++){
                Dataset d = hashtag_clusters[i];

                for(Instance h : d){
                    Hashtag hh = (Hashtag) h;

                    insert_hashtag_stmt = db_conn.prepareStatement(insert_hashtag);
                    insert_hashtag_stmt.setString(1, hh.getHname());
                    insert_hashtag_stmt.setDouble(2, hh.getScalar_1());
                    insert_hashtag_stmt.setInt(3,(int) hh.getScalar_2());
                    insert_hashtag_stmt.setInt(4, i);

                   // System.out.println(hh.getHname()+ " " + hh.getImportance()+ " " + hh.getAppearences() + " "+ i);

                    insert_hashtag_stmt.executeUpdate();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Error updating DB!");
            return false;
        }
        return true;
    }
}
