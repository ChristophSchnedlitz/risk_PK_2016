import java.util.HashMap;

/**
 * Created by Christoph on 23.02.2016.
 * contains all methods that computer AI needs to claim, reinforce, move, attack, defend
 */
public class EnemyAI {

    public static HashMap<Continent, int[]> count = new HashMap<>();


    //Analysis method for AI. Creates a hashmap with number of territories owned per side for each continent
    public static void continentCount(){

        for (Continent continent: Continent.cmap.values()){

            int computer = 0;
            int player = 0;

            //iterates through territories of this continent
            for (Territory ter: continent.territories){

                if (ter.getOwner() == 0){
                    computer++;
                }
                //if owned by player , +1
                if (ter.getOwner() == 1){
                    player++;
                }
            }
            count.put(continent,new int[]{computer,player});

        }
    }

    public static void acquisition(){

        for (Territory t : Territory.tmap.values()){
            if (t.getOwner() == -1){
                Actions.claim(t,0);
                break;
            }
        }

    }

    public static void reinforcing(){

        int i = 1;
        if (GameState.getBonus(0)>0 && GameState.getBonus(1)==0){ //if computer has reinforcments left but player hasn't, it will repeat reinforcing until it has ran out of them
            i = GameState.getBonus(0);
        }

        while (i>0){

            for (Territory t : Territory.tmap.values()){
                if (t.getOwner() == 0){
                    Actions.reinforce(t,0);
                    break;
                }
            }
            i--;
        }
    }


    public static void conquer(){

        //before conquer ends, reinforece Bonus is filled up so GameState can switch to that phase
        GameState.reinforceBonus = GameState.calculateBonus();
    }


}