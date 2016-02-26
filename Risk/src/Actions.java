/**
 * Created by Christoph on 23.02.2016.
 * Actions methods can rely that the parameters given to them are all according to the rules
 * as much as possible, we tried to handle actions without needing to know ownerID
 *
 */
public class Actions {

    //action in acquisiton phase - click on unclaimed territory, make it yours
    //claim relies that Territory is not yet claimed
    public static void claim(Territory toClaim, int ownerID){
        toClaim.setOwner(ownerID);
        toClaim.changeArmy(1);
    }

    //reinforce relies that Territory is owned by owner
    public static void reinforce(Territory ter, int owner){
            ter.changeArmy(1);
            GameState.decrementBonus(owner); //reinforcment bonus counter
    }


    //Move troops into own territories | only from 1 territory to another per round (it is possible to send them back in the same round)
    //more relies that both territories are owned by same owner and first has >1 army
    // an exception from the move rule are territories that were just conquered. One can always send troops back and forth between these territories
    public static void move(Territory first, Territory second){

        if (GameState.getTer1Mov() == null && GameState.getTer2mov() == null && (first != GameState.getConquered() ||first != GameState.getConquerer() )) { //
            GameState.setTerMov(first,second);
            first.changeArmy(-1);
            second.changeArmy(1);
        } else {
            if ( ( (first == GameState.getTer1Mov() || first == GameState.getTer2mov() )
                    && (second == GameState.getTer2mov() || second == GameState.getTer1Mov())
                 )
                 ||
                    ( (first == GameState.getConquered() || first == GameState.getConquerer() )
                      && ( ( second == GameState.getConquered() ) || (second == GameState.getConquerer()) )
                    )
               )
            {
                first.changeArmy(-1);
                second.changeArmy(1);
            } else {

            }
        }
    }

    //attack assumes: that territory1 is owned by attacker and territory2 is owned by a different owner
    //1st Step: Max armies (attacker/defender) are calculated
    //2nd step: Create sorted (ascending) array for attacker & defender (fillandsortArray)
    //3d step: Compare arrays and return array with losses on each side
    public static void attack(Territory attackerTer, Territory defenderTer){

        GameState.setConquerNull(); //this deletes all previous exceptions of move restriction in game state

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
                GameState.setConquerTer(attackerTer,defenderTer); //free these territories of move restriction
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
