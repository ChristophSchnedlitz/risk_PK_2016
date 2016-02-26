import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by Christoph, 22.02.16
GameState controls the phases of the game (acquisition, reinforcement, attack/move) and checks the win/lose conditions
 */
public class GameState {

    private static IntegerProperty state = new SimpleIntegerProperty(); //GameState 1: Acquisition 2: Reinforce 3 Attack/Move 4 Game Over
    private static int[] reinforceBonus; //reinforceBonus[0] = computers reinforcements  reinforceBonus[1] =players reinforcements
    private static Territory ter1mov; // stores one of the two territories that can be moved to and from in 1 round (moving is only allowed from 1 ter to 1 other per round)
    private static Territory ter2mov; // stores one of the two territories that can be moved to and from in 1 round
    private static Territory conquered;
    private static Territory conquerer;


    //1. GAME STATE CHECKS

    //change: each method void and changes state when called
    // each phase gets a !checkWin!
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
                calculateBonus();
                break;
            }
        }
        return AcquisitionOngoing;
    }

    private static boolean ReinforcementPhase(){

        if ((!AcquisitionPhase() & (reinforceBonus[0]+reinforceBonus[1]) > 0)){ //reset the territorires remembered for limiting the move
            setTerMovNull();
        }

        return (!AcquisitionPhase() & (reinforceBonus[0]+reinforceBonus[1]) > 0);
    }

    private static boolean AttackPhase(){

        //end of attackPhase: check if Game over? no
        // reinforceBonus = calculateBonus();
        return !AcquisitionPhase() && !ReinforcementPhase() && !checkGameOver();
    }

    private static boolean checkGameOver(){

        int[] terOwned = territoryCount();

        return (terOwned[0]==42 || terOwned[1]==42);
    }

    //returns an array with the number of territories owned by computer [0] and player [1]
    //for win/loss condition, possibly also for display of info on GUI

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

    // HANDLE REINFORCMENT BONUS

    //calculates the reinforcment bonus and sets it to its correct values
    public static void calculateBonus(){

        int[] calcBonus = new int[2];

        //Territory bonus 1 point per 3 territories owned
        int[] terOwned = territoryCount();
        calcBonus[0] = terOwned[0]/3;
        calcBonus[1] = terOwned[1]/3;

        //Continent bonus x points per continent (stored in Continent object)
        for (String name : Continent.cmap.keySet()){
            int[] ContBonus = Continent.cmap.get(name).continentBonus();
            calcBonus[ContBonus[0]]+=ContBonus[1];
        }
        reinforceBonus = calcBonus;
    }

    //getter
    public static int getBonus(int owner){
        return reinforceBonus[owner];
    }

    //reduce Bonus after each time player/computer calls reinforce
    public static void decrementBonus(int owner){

        if (reinforceBonus[owner] -1 < 0)
        {} else {
        reinforceBonus[owner] -= 1;
        }
    }

   /* public static IntegerProperty displayBonusPlayer(){

        IntegerProperty toDisplay = new SimpleIntegerProperty();
        toDisplay.set(reinforceBonus[1]);
        return toDisplay;
    }*/


    // MOVE Restriciton

    public static void setTerMov(Territory first,Territory second){
        ter1mov=first;
        ter2mov=second;
    }

    public static void setTerMovNull(){
        ter1mov = null;
        ter2mov = null;
    }

    public static Territory getTer1Mov(){
        return ter1mov;
    }

    public static Territory getTer2mov(){
        return ter2mov;
    }



    //Conquered territory exception from move restriction
    public static void setConquerTer(Territory first,Territory second){
        conquerer=first;
        conquered=second;
    }

    public static void setConquerNull(){
        conquerer = null;
        conquered = null;
    }

    public static Territory getConquerer(){
        return conquerer;
    }

    public static Territory getConquered(){
        return conquered;
    }


















}
