/**
 * Created by Christoph on 23.02.2016.
 */
public class Actions {

    //action in acquisiton phase - click on unclaimed territory, make it yours
    public static void claim(Territory toClaim, int ownerID){
        if(toClaim.getOwner()==-1){ //can be omitted because built in listener and Enemy AI
            toClaim.setOwner(ownerID);
            toClaim.changeArmy(1);
        } else {}
    }

    public static void reinforce(Territory ter, int owner){
            ter.changeArmy(1);
            GameState.decrementBonus(owner); //reinforcment counter
    }


    //Move troops into own territories | only from 1 territory to another per round (it is possible to send them back in the same round)
    public static void move(Territory first, Territory second){


    }


    public static void attack(Territory attackerTer, Territory defenderTer){

        int attackerArmy = attackerTer.getArmy();
        int defenderArmy = defenderTer.getArmy();

        //determine the army numbers according to game mechanics

        int attackerArmyMax = ( (attackerArmy - 1) >= 3) ? 3 : (attackerArmy - 1); //attacker attacks with full amount, max 3, 1 has to stay behind
        int defenderArmyMax = (defenderArmy >= 2 ) ? 2: defenderArmy; //defender defends with max, max 2 allowed

        attackerTer.changeArmy(-attackerArmyMax); //attacker army leaves its territory

        int[] losses = rollLosses(attackerArmyMax,defenderArmyMax); //losses on each side are calculated

        if (defenderArmy-losses[1] <= 0){
                defenderTer.changeArmy(-defenderArmy); //conquered country: first set armies to 0
                defenderTer.changeOwner(); //change owner of conquered country
                defenderTer.changeArmy(attackerArmyMax - losses[0]); //set surviving armies of attacker on conquered territory
        }   else {
                    defenderTer.changeArmy(-losses[1]); //defender territory loses troops
                    attackerTer.changeArmy(attackerArmyMax - losses[0]); //attackers troops return (minus the losses)
        }

    }

    //Dice methods for attack

    private static int[] rollLosses(int attacker, int defender){

        int[] rollsAttacker = fillAndSortArray(attacker);
        int[] rollsDefender = fillAndSortArray(defender);

        return compareRolls(rollsAttacker,rollsDefender);
    }

    //creates a sorted array (high to low) with random dice throws (D6 dice)
    private static int[] fillAndSortArray(int length) {

        int[] ar = new int[length];

        //fill array with dice rolls
        int i = 0;
        while (i < ar.length) {
            ar[i] = (int) ((Math.random() * 6) + 1);
            i++;
        }

        //sort dice rolls in array from highest to lowest
        boolean done;
        do {
            done = true;
            for (int j = 1; j < ar.length; j++) {
                if (ar[j - 1] < ar[j]) {
                    int h = ar[j];
                    ar[j] = ar[j - 1];
                    ar[j - 1] = h;
                    done = false;
                }
            }

        } while(! done);

        return ar;
    }

    //compares the results of dice throws of attacker and defender, returns army losses
    private static int[] compareRolls(int[] attacker, int[] defender){

        int noComparisons = Math.min(attacker.length,defender.length); //We only need to do "noComparison" comparisions

        int[] losses = new int[2]; //number of lost rounds are recorded. losses[0] = attacker, losses[1] = defender
        int i = 0;

        while (i < noComparisons){
            if (attacker[i] > defender[i]){ //attacker only wins if he is higher, loses if equal
                losses[1]+=1;
            } else{
                losses[0]+=1;}
            i++;
        }

        return losses;
    }

}
