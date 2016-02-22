package MapClasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christoph on 21.02.2016.
 */
public class Continent {

    public static HashMap<String, Continent> cmap = new HashMap<>();

    private int armyBonus;
    private ArrayList<Territory> territories = new ArrayList<>();

    public Continent(int armyBonus,ArrayList<Territory> territories){
        this.armyBonus = armyBonus;
        this.territories = territories;
    }

}
