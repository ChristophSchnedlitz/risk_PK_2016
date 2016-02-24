import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Iterator;

/**
 * Created by Christoph on 23.02.2016.
 */
public class GameState {

    private static int playerReinforce;
    private static int computerReinforce;
    private static IntegerProperty state = new SimpleIntegerProperty();



    public static IntegerProperty getGameState(){

        if (AcquisitionPhase()) {state.set(1);}
        if (ReinforcementPhase()){state.set(2);}
        if (AttackPhase()) {state.set(3);}
        if (checkGameOver()) {state.set(4);}

        return state;
    }

    private static boolean AcquisitionPhase(){

        boolean AcquisitionOngoing = false;

        for (String key : Territory.tmap.keySet()){
            if (Territory.tmap.get(key).getOwner() == -1){
                AcquisitionOngoing = true;
                break;
            }
        }
        return AcquisitionOngoing;
    }

    private static boolean ReinforcementPhase(){
        int[] bonus = calculateBonus();
        int computerBonus = bonus[0];
        int playerBonus = bonus[1];
        int totalBonus = computerBonus+playerBonus;
        //decrementBonus

        return (!AcquisitionPhase() & totalBonus>0);
    }

    private static boolean AttackPhase(){

        return !AcquisitionPhase() && !ReinforcementPhase() && !checkGameOver();
    }

    public static boolean checkGameOver(){

        int[] terOwned = territoryCount();

        return (terOwned[0]==42 || terOwned[1]==42);
    }

    //returns an array with the number of territories owned by computer [0] and player [1]
    public static int[] territoryCount(){

        int[] terOwned = new int[2]; //terOwned[0] = Computer, terOwned[1] = Player

        for (String key : Territory.tmap.keySet()){
            if ( Territory.tmap.get(key).getOwner() == 0){
                terOwned[0]+=1;
            }
            if ( Territory.tmap.get(key).getOwner() == 1){
                terOwned[1]+=1;
            }
        }
        return terOwned;
    }

    //returns an array with the reinforcment bonus of the computer [0] and the player [1]
    private static int[] calculateBonus(){

        int[] reinforceBonus = new int[2];

        //Territories
        int[] terOwned = territoryCount();
        reinforceBonus[0] = terOwned[0]/3;
        reinforceBonus[1] = terOwned[1]/3;

        //Continents
        for (String name : Continent.cmap.keySet()){
            int[] ContBonus = Continent.cmap.get(name).continentBonus();
            reinforceBonus[ContBonus[0]]+=ContBonus[1];
        }

        return reinforceBonus;
    }













}
