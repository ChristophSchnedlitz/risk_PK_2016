/**
 * Created by Christoph on 22.02.2016.
 */
public class Main {

    public static void main(String[] args) {

        String filepath = "C:\\Users\\Christoph\\Documents\\world.map";

        ReadFile importer = new ReadFile();
        importer.readFile(filepath);

        System.out.println(Territory.tmap.get("Japan").patches);


    }






}
