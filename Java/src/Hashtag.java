import net.sf.javaml.core.SparseInstance;

/*
    Wrapper um SparceInstance, um den Hashtag dabei zu haben.
 */
public class Hashtag extends SparseInstance{
    private String hname;
    private int cluster;

    public Hashtag(String hname, int scalar_1, int scalar_2){
        super(new double[] {scalar_1, scalar_2});
        this.hname = hname;
    }

    public String getHname(){return hname;}
    public int getScalar_1() {return (int) this.value(0);}
    public int getScalar_2(){return (int) this.value(1);}
}
