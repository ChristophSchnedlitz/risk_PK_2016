import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.shape.*;

public class ReadFile {


    // read file
    public void readFile(String filePath) {

        File file = new File(filePath);

        // abort if no file or incorrect file
        if (!file.isFile()) { //in case file name is wrong
            System.out.println("That file doesn't exist");
            System.exit(0);
        } else if (!file.canRead()) { //in case system cannot read the file
            System.out.println("Unable to read file");
            System.exit(0);
        }

        // start Reader
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filePath));

            String line; //1 line is one entity to be treated: territory patch, capital, contintent list, or neigbor list

            while ((line = in.readLine()) != null) {
                assignLine(line); //each line is treated differently according to its assignment
            }
        } catch (IOException ex) {
            ex.printStackTrace(); //prints stack trace of the exception to system.err
        } finally { //to close the reader
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("Something went wrong");
                }
        }
    }

    // assigns line to correct method based on the title of each string
    private void assignLine(String line) {
        String[] substring = line.split(" ", 2); //splits String into assignment title and the actual string that needs to be imported

        // determine type of information
        switch (substring[0]) {
            case "patch-of":
                addPatch(substring[1]);
                break;
            case "capital-of":
                addCapital(substring[1]);
                break;
            case "neighbors-of":
                addNeighbors(substring[1]);
                break;
            case "continent":
                addContinent(substring[1]);
                break;
            default:
                break;
        }
    }

    // if line is a patch
    private void addPatch(String s) {
        String patchName = getName(s);
        Polygon p = getPolygon(s.substring(patchName.length()+1)); //substring starts right after patchName (+ 1 = +space) is over

        Territory ter;

        // check if territory already exist (1 territory may have multiple patches)
        if (Territory.tmap != null && Territory.tmap.containsKey(patchName)) {
            ter = Territory.tmap.get(patchName);
            ter.addPatch(p); //add patch to existing territory
        } else {
            ter = new Territory(p); // new territory with first patch polygon
            Territory.tmap.put(patchName, ter); //put into Territory map
        }
    }

    // if line is a capital
    private void addCapital(String s) {
        String TerritoryName = getName(s);

        if (Territory.tmap != null && Territory.tmap.containsKey(TerritoryName)) {
            Territory ter = Territory.tmap.get(TerritoryName);
            ter.setCapital(s.substring(TerritoryName.length()+1)); //coordinates of capital are written in string after Territory name plus one space
        }
    }

    // if line is a neighbor list
    // the neighbor list is tricky. if a territory has been mentioned already as a neighbor of another territory,
    // that territory's neighbor list will not list the other territory as its neighbor!
    private void addNeighbors(String s) {
        String[] data = s.split(" : ", 2); //separate territory name from neigbor countries
        String territoryName = data[0];
        String[] neighbors = data[1].split(" - "); //separate neighbor countries
        Territory ter;
        Territory neighbor;

        if(Territory.tmap != null && Territory.tmap.containsKey(territoryName)) {
            ter = Territory.tmap.get(territoryName);
            for (String value : neighbors) { //most individual lines of neigbours are incomplete, so import needs to import for this territory plus add this territory as neighbor of its neigbhour
                neighbor = Territory.tmap.get(value);
                ter.addNeighbor(neighbor); //adds neighbor to Territory
                neighbor.addNeighbor(ter); //adds Territory to neighbor
            }
        }
    }

    // if line is a continent information
    private void addContinent(String s) {

        String[] allInfo = s.split(" : ", 2);
        String[] continentVar = allInfo[0].split(" ");
        String[] territories = allInfo[1].split(" - ");
        String nameOfContinent = "";
        ArrayList<Territory> territoryList = new ArrayList<>();
        int armyBonus = 0;
        Continent continent;

        for (String i : continentVar) {
            if (i.matches("^[^\\d].*")) {
                nameOfContinent = nameOfContinent +" "+i; //continent names can have multiple words
            } else {
                armyBonus = Integer.parseInt(i); //if it is a digit, then it is the army bonus of the continent
            }
        }
        nameOfContinent = nameOfContinent.substring(1); //remove first " " from Continent name

        for (String ter : territories) {
            territoryList.add(Territory.tmap.get(ter));
        }

        if (Continent.cmap == null || !Continent.cmap.containsKey(nameOfContinent)) {
            continent = new Continent(nameOfContinent,armyBonus,territoryList);
            Continent.cmap.put(nameOfContinent, continent);
        }

    }

    // extract continent/territory name from string
    private String getName(String s) {
        String[] data = s.split(" ", 2);
        String name = data[0];

        while (data[1].matches("^[^\\d].*")) {
            data = data[1].split(" ", 2);
            name += " " + data[0];
        }
        return name;
    }

    // Create a polygon of patch coordinates
    private Polygon getPolygon(String s) {
        String[] values = s.split(" ");
        double[] polygonPoints = new double[values.length]; //int array for polygon point data (length = length of string array)

        for (int i = 0; i < values.length; i++) { //pass all entries from values as Double into polygonpoints
            polygonPoints[i] = Double.parseDouble(values[i]);
        }

        Polygon p = new Polygon();

        for (double d : polygonPoints){ //pass all coordinates into Polygon
        p.getPoints().add(d);
        }

        return p; //returns Polygon
    }

}
