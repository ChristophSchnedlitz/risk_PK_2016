import java.util.HashMap;
import java.util.TreeMap;


/**
 * Created by Christoph on 23.02.2016.
 * contains all methods that computer AI needs to claim, reinforce, move, attack, defend
 */
public class EnemyAI {

    private static HashMap<Continent, int[]> contAnalysis = new HashMap<>(); //AI uses it to analyze for claim phase
    private static HashMap<Territory, int[]> terAnalysis = new HashMap<>(); //not used right now, needs improvement (sorting!). this will help to choose attack and reinforce smarter

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

    private static void terCount(){

        for (Territory ter: Territory.tmap.values()){
            int owner = ter.getOwner();
            int army = ter.getArmy();
            terAnalysis.put(ter,new int[]{owner,army});
        }
    }




    //couldn't sort countAnalysis Hashmap (from smallest to largest continent based on bonus.
    //so there is a lot of repetitive code here in II and IV. fix issue!
    public static void acquisition(){

        continentCount();
        boolean methodRun = true;

        //I. look if player is alone on a continent, claim an unoccupied territory from there
        for (Continent cont : contAnalysis.keySet()){
            int[] checkPossession = contAnalysis.get(cont);
            if(checkPossession[0]==0 && checkPossession[1]!=0 && getEmptyTer(cont)!=null){
                Actions.claim(getEmptyTer(cont),0);
                methodRun = false;
                //continentCount();
                break;
            }
        }

        //II. strengthen a continent where computer has more armies than player - start with smaller continents
        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if(checkPossession[0] > checkPossession[1] && getEmptyTer(cont)!=null && cont.armyBonus==2){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }
        }

        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if(checkPossession[0] > checkPossession[1] && getEmptyTer(cont)!=null && cont.armyBonus<=5){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }
        }

        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if(checkPossession[0] > checkPossession[1] && getEmptyTer(cont)!=null && cont.armyBonus<9){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }
        }

        //III. avoid player getting too strong in a continent
        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]*2) <= checkPossession[1] && getEmptyTer(cont)!=null){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }
        }

        //IV look for a continent that is empty and claim a territory,
        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]==0 && checkPossession[1]==0) && (cont.armyBonus < 3) ){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }

        }

        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]==0 && checkPossession[1]==0) && (cont.armyBonus < 4) ){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }

        }

        if (methodRun){
            for (Continent cont : contAnalysis.keySet()){
                int[] checkPossession = contAnalysis.get(cont);
                if( (checkPossession[0]==0 && checkPossession[1]==0) && (cont.armyBonus < 6) ){
                    Actions.claim(getEmptyTer(cont),0);
                    methodRun = false;
                    //continentCount();
                    break;
                }
            }

        }



        //IV. else choose random territory
        //but preferably not from Asia
        ///purpose of this method is to look first for something that is NOT in asia. But it seems to do the opposite
        /*if (methodRun) {
            for (Territory ter : Territory.tmap.values()) {
                if (ter.getOwner() == -1 && Continent.getContinentName(ter).equals("Asia")) {
                    Actions.claim(ter, 0);
                    methodRun = false;
                    continentCount();
                    break;
                }
            }
        }*/

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

            //first, reinforce territories with less than 3 armies on them
            boolean methodRun = true;

            for (Territory ter : Territory.tmap.values()){
                if (ter.getOwner() == 0 && ter.getArmy()<3){
                    Actions.reinforce(ter,0);
                    methodRun = false;
                    break;
                }
            }

            //else, reinforce random
            if (methodRun) {
                for (Territory ter : Territory.tmap.values()) {
                    if (ter.getOwner() == 0) {
                        Actions.reinforce(ter, 0);
                        break;
                    }
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