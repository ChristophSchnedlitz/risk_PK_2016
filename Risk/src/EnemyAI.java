import java.util.HashMap;

/**
 * Created by Christoph on 23.02.2016.
 * contains all methods that computer AI needs to claim, reinforce, move, attack, defend
 */
public class EnemyAI {

    public static void conquer(){

    }


    public static HashMap<Continent,int[]> continentCount(){

        //the return counter
        //HashMap<String ,Arrays> count= new HashMap<>();
        HashMap<Continent, int[]> count = new HashMap<Continent, int[]>();

        //check if player alone on one continent, if yes claim on that continent
        for (Continent continent: Continent.cmap.values()){
            //ownings per continent, [0] computer, [1] player
            int[] ownedOnContinent={0,0};
            //itterates through territoeries
            for (String key: Territory.tmap.keySet()){
                //int i=0;i<continent.getTerr().size();i++
                //if owned by computer, +1
                if (Territory.tmap.get(key).getOwner()==0){
                    ownedOnContinent[0]++;
                }
                //if owned by player , +1
                if (Territory.tmap.get(key).getOwner()==1){
                    ownedOnContinent[1]++;
                }
            }
            //puts the name of the Continent and an array with ownings to the "count" hashmap
            //count.put(continent.cmap.values().toString(),ownedOnContinent);
            count.put(continent,ownedOnContinent);
        }
        return count;
    }


    public static void acquisition(){
        /*HashMap<Continent ,int[]> count= continentCount();

        for (Continent conti : count.keySet()){

            int[] values=count.get(conti);
            if (values[0]==0&& values[1]==1){
                loopContClaim();
                //   for (Continent cont: )

            }
        }*/

        for (Territory t : Territory.tmap.values()){
            if (t.getOwner() == -1){
                Actions.claim(t,0);
                break;
            }
        }

    }


    public static void loopContClaim(){

        for (Continent continent: Continent.cmap.values()){

            //itterates through territoeries
            for (String key: Territory.tmap.keySet()){

                if (Territory.tmap.get(key).getOwner()!=1){
                    Actions.claim(Territory.tmap.get(key),0);
                }
            }
        }

    }



}