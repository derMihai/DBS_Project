import net.sf.javaml.core.SparseInstance;

/*
    Wrapper um SparceInstance, um den Hashtag dabei zu haben.
 */
public class Hashtag extends SparseInstance{
    private String hname;
    private int cluster;

    public Hashtag(String hname, double scalar_1, double scalar_2){
        super(new double[] {scalar_1, scalar_2});
        this.hname = hname;
    }

    public String getHname(){return hname;}
    public double getScalar_1() {return this.value(0);}
    public double getScalar_2(){return this.value(1);}
}
