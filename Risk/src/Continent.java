import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christoph on 21.02.2016.
 */
public class Continent {

    public static HashMap<String, Continent> cmap = new HashMap<>();
    public int armyBonus =0;
    public String nameOfContinent;
    public ArrayList<Territory> territories = new ArrayList<>();


    public Continent(String nameOfContinent, int armyBonus,ArrayList<Territory> territories){
        this.nameOfContinent = nameOfContinent;
        this.armyBonus = armyBonus;
        this.territories = territories;
    }

    //returns an array with array[0] being the sole owner of all continent territories and array[1] being the continent bonus
    //if no sole owner, retunrs array {0,0}!
    public int[] continentBonus(){

        int [] continentOwnerBonus = new int[2]; // [0] = Owner, [1] = armyBonus of continent

        int ownerCount = 0;
        int terCount = 0;

        for (Territory ter: this.territories){
            ownerCount += ter.getOwner();
            terCount++;
        }

        if (ownerCount == terCount || ownerCount == 0){
            continentOwnerBonus[0] = ownerCount/terCount;
            continentOwnerBonus[1] = this.armyBonus;
        }

        return continentOwnerBonus;
    }

    //return name of Continent
    public static String getContinentName(Territory ter){

        for (Continent cont : Continent.cmap.values()){
            if (cont.territories.contains(ter)){
                return cont.nameOfContinent;
            }

        }
        return null;
    }







}
