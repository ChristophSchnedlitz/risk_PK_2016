/**
 * Created by Christoph on 23.02.2016.
 * Contains all methods that allow player action
 */
public class Player {

    public void claim(Territory toClaim){
        if(toClaim.getOwner()==-1){
            toClaim.changeOwner(1);
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



    }

    private void defend(){


    }


}
