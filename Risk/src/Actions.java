/**
 * Created by Christoph on 23.02.2016.
 */
public class Actions {

    public void claim(Territory toClaim, int ownerID){
        if(toClaim.getOwner()==-1){
            toClaim.changeOwner(ownerID);
        } else {}

    }

    public void reinforce(Territory ter){
        if (ter.getOwner()==1){
            ter.changeArmy(1);
        } else {}

    }

    //Listener decides if move or attack

    //Move troops into own territories | only from 1 territory to another per round (it is possible to send them back in the same round)
    private void move(Territory first, Territory second){


    }

    //1 army always left behind | max amount of armies sent to battle (max 3)
    private void attack(Territory first, Territory second){

        int attackerArmy = first.getArmy();
        int defenderArmy = second.getArmy();

        if (attackerArmy == 1){

        } else {
            attackerArmy = (attackerArmy - 1) > 3 ? 3 : (attackerArmy - 1);
            first.changeArmy(-attackerArmy);
            defenderArmy = defenderArmy > 2 ? 2: defenderArmy;






        }




        //player Army == 1 --> not allowed
        //all others are sent, max 3

        //defender defends with


    }


    //Dice methods for attack

    public int[] diceRoll(int attacker, int defender){
        int[] ArmiesRemain = new int[2]; //here the remaining armies on each side will be stored. [0] is for attacker, [1] is for defender
        int[] rollsAttacker = new int[attacker];
        int[] rollsDefender = new int[defender];






        return new int[]{0,1};
    }

    public int[] fillAndSortArray(int length){
        int[] ar = new int[length];

        //fill array with dice rolls
        int i = 0;
        while (i<length){
            ar[i] = (int)((Math.random()*6)+1);
        }

        //sort array from highest to lowest
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





}
