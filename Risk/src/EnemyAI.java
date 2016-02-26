import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 23.02.2016.
 * contains all methods that computer AI needs to claim, reinforce, move, attack, defend
 */
public class EnemyAI {

    public static HashMap<Continent, int[]> contAnalysis = new HashMap<>();

    //Analysis method for AI. Fills a hashmap with number of territories owned per side for each continent

    private static void continentCount(){

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
            contAnalysis.put(continent,new int[]{computer,player});
        }
    }

    public static void acquisition(){

        continentCount();
        boolean methodRun = true;

        //I. look if player is alone on a continent, claim an unoccupied territory from there
        for (Continent cont : contAnalysis.keySet()){
            int[] checkPossession = contAnalysis.get(cont);
            if(checkPossession[0]==0 && checkPossession[1]!=0 && getEmptyTer(cont)!=null){
                Actions.claim(getEmptyTer(cont),0);
                methodRun = false;
                continentCount();
                break;
            }
        }



        // strengthen a continent where computer has more armies than player
        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if(checkPossession[0] > checkPossession[1] && getEmptyTer(cont)!=null){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    continentCount();
                    break;
                }
            }
        }

        //II. avoid player getting too strong in a continent
        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]*3) <= checkPossession[1] && getEmptyTer(cont)!=null){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    continentCount();
                    break;
                }
            }
        }


        //III. if I. & II. not the case, look for a continent that is empty and claim a territory,
        //couldn't sort contAnalysis from smallest to largest continent, so i make the same loop 3 times for small and medium continents

        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]==0 && checkPossession[1]==0) && (cont.armyBonus > 5) ){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    continentCount();
                    break;
                }
            }

        }


        //IV. else choose random territory

        if (methodRun) {
            for (Territory ter : Territory.tmap.values()) {
                if (ter.getOwner() == -1) {
                    Actions.claim(ter, 0);
                    continentCount();
                    break;
                }
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

        Territory attackFrom = null;
        Territory toAttack = null;

        for (Territory t : Territory.tmap.values()) {
            if (t.getOwner() == 0 && t.getArmy() > 1 && playerNeighbor(t)!=null) {
                toAttack = playerNeighbor(t);
                attackFrom = t;
                break;
            }
        }

        if(attackFrom != null && toAttack != null){
            Actions.attack(attackFrom,toAttack);
        }

        //before conquer ends, reinforece Bonus is filled up so GameState can switch to that phase
        GameState.calculateBonus();
    }

    //helper for conquering

    private static Territory playerNeighbor(Territory ter){

        Territory toAttack = null;

        for (Territory target : ter.getNeighbors()){

            if (target.getOwner() == 1){
                toAttack = target;
            }
        }
        return toAttack;
    }

    private static Territory getEmptyTer(Continent cont){

        for (Territory ter : cont.territories){
            if (ter.getOwner()== -1){
                return ter;
            }
        }
        return null;
    }


    //look through continent territories and return which has my strongest army and has enemy neighbor
    //loop through continent territories and return one by player that is neighbor of you
    //compare armies of two territories


}