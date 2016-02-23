import javafx.scene.shape.*;
import java.util.HashMap;
import java.util.ArrayList;


public class Territory {

    public static HashMap<String, Territory> tmap = new HashMap<>(); //String: Name of Territory | Territory: Data object

    private int[] capital = new int[2]; //x and y coordinate of capital capital[0] = x coordinate;
    private int owner = -1; // -1: no owner, 0: Computer, 1: Player
    private int army; //integerproperty allows update on worldmap
    private ArrayList<Territory> neighbors = new ArrayList<>();
    public ArrayList<Polygon> patches = new ArrayList<>();


    //constructor for import
    public Territory(Polygon p){
        addPatch(p);
    }

    //for import
    public void addPatch(Polygon p){
        this.patches.add(p);
    }

    //for import
    public void setCapital(String coordinates){
        String[] xy = coordinates.split(" ");
        String x = xy[0];
        String y = xy[1];

        this.capital[0] = Integer.parseInt(x);
        this.capital[1] = Integer.parseInt(y);
    }

    //for import
    public void addNeighbor(Territory neighbor){
        this.neighbors.add(neighbor);
    }


    //owner related methods
    public void changeOwner(int owner){
        this.owner = owner;
    }

    public int getOwner(){
        return this.owner;
    }


    //army related methods

    public void changeArmy(int change){ //all other rules are dealt with in action methods
        if ( (this.army + change ) < 0){
            this.army = 0;
        } else {
            this.army += change;
        }
    }

    public int getArmy(){
        return this.army;
    }


}
