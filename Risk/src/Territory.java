import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.*;
import java.util.HashMap;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.beans.property.IntegerProperty;

/** Created by Christoph, 22.06.16
 * Each territory of importFile is 1 object, all patches/neighbors of territory are put into patches/neighbors ArrayList (Polygons/Territory obj)
 *
 */

public class Territory {

    public final static HashMap<String, Territory> tmap = new HashMap<>(); //String: Name of Territory | Territory: Data object

    private final int[] capital = new int[2]; //x and y coordinate of capital capital[0] = x coordinate;
    public IntegerProperty army = new SimpleIntegerProperty();
    private final ArrayList<Territory> neighbors = new ArrayList<>();
    public final ArrayList<Polygon> patches = new ArrayList<>();
    private int owner = -1; // -1: no owner, 0: Computer, 1: Player

    //change changeArmy to update a IntegerProety
    //create getArmyDisplay

    //A. IMPORT METHODS

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



    //B. GAME PHASE METHODS

    //B.1 Getter & checking methods

    public int getOwner(){
        return this.owner;
    }

    public int getArmy(){
        return this.army.getValue();
    }

    //returns the capital
    public int[] getCapital(){
        return this.capital;
    }

    public ArrayList<Territory> getNeighbors (){
        return neighbors;
    }


    public boolean isNeighbor (Territory t){
        for (Territory t1: this.neighbors){
            if (t1==t) {
                return true;
            }
        }
        return false;
    }

    //B.2 Action methods

    // claim method - Acquisition stage
    public void setOwner(int owner){
        this.owner = owner;
        setColor();
    }

    //for attack method
    public void changeOwner(){ //switches owner after successful attack by player or computer
        if (this.owner == 0){
            this.owner = 1;
            setColor();
        } else {
            this.owner = 0;
            setColor();
        }
    }

    //used in acquisition, reinforce, move and attack
    public void changeArmy(int change){ //all other rules are dealt with in action methods
        if ( (this.army.getValue() + change ) < 0){
            this.army.set(0);
        } else {
            this.army.set(this.army.get()+change);
        }
    }


    // C DISPLAY

    //shows updateds army number
    public IntegerProperty getArmyDisplay(){
        return this.army;
    }


    //sets the color of the Territory according to the owner
    public void setColor (){
        if(owner==-1){
            for (Polygon p: this.patches){
                p.setStroke(Color.BLACK);
                p.setFill(Color.GREY);
            }
        }

        if (owner==0){
            for (Polygon p: this.patches){
                p.setStroke(Color.BLACK);
                p.setFill(Color.RED);
            }
        }

        if (owner==1){
            for (Polygon p: this.patches){
                p.setStroke(Color.BLACK);
                p.setFill(Color.BLUE);
            }
        }

    }


}
