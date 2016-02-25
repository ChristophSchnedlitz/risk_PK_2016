import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

/**
 * Created by Nikolla, 22.02.16
 * Basic guidelines:
 *
 * 1. Main creates window/map & mouse listeners (getting values from Territory and GameState, using methods from GameState, Actions of player, EnemyAI)
 * 2. ReadFile imports txt File to Territory and Continent
 * 3. GameState knows in which stage the game is, handles reinforcment bonus and movement constraint, and if win/lose conditions are met
 * 4. Actions is used by PLAYER and EnemyAI to acquire, reinforce, attack/move
 * 5. EnemyAI is the dumbest AI ever created. It became self aware but is behaving like an obnoxious passive aggressive teenager
 *
 * Whenever owners are important, 0 is computer and 1 is player. When arrays show values for both parties, array[0] is always computer and array[1] always player
 *
 */

public class Main extends Application {

    private Territory last = null;

    public void start(Stage stage) {

        String filepath = "C:\\Users\\Christoph\\Documents\\world.map";


        ReadFile importer = new ReadFile();
        importer.readFile(filepath);


        //groups : countries, capitals , lines
        Group countries = new Group();
        Group capitals = new Group();
        Group lines = new Group();

        BorderPane layout = new BorderPane();
        Group world = new Group();


        //first loop through the territories
        for (Territory ter : Territory.tmap.values()) {

            //Lines connecting neighbors
            //second loop through the neighbors, draws the lines from one capital to all neighbouring capitals
            for (Territory neighbors : ter.getNeighbors()) {
                int x = ter.getCapital()[0] - neighbors.getCapital()[0];
                int y = ter.getCapital()[1] - neighbors.getCapital()[1];
                double distance = Math.sqrt(x * x + y * y);
                if (distance < 1250 * 0.5) {
                    Line l = new Line(ter.getCapital()[0], ter.getCapital()[1],
                            neighbors.getCapital()[0], neighbors.getCapital()[1]);

                    lines.getChildren().add(l);
                } else {
                    if (ter.getCapital()[0] < 1250 * 0.5) {
                        Line line1 = new Line(ter.getCapital()[0], ter.getCapital()[1],
                                0, neighbors.getCapital()[1]);//neighbors.getCapital()[1]/2

                        Line line2 = new Line(neighbors.getCapital()[0], neighbors.getCapital()[1],
                                1250, ter.getCapital()[1]); //ter.getCapital()[1]/2

                        lines.getChildren().addAll(line1, line2);
                    }

                }
            }

            //Armies displayed
            //get number of armies on territory, convert to string
            //set layout , coordinates for capital city (minus 5p for X, minus 10p for Y to optimize look)
            Label armyDisplay = new Label(ter.getArmy() + "");
            ter.getArmyDisplay().addListener((v, oldValue, newValue) -> { //Listener updates army number if it changes
                        armyDisplay.setText(ter.getArmy() + "");
                    }
            );
            armyDisplay.setLayoutX(ter.getCapital()[0] - 5);
            armyDisplay.setLayoutY(ter.getCapital()[1] - 10);

            //add capital to label
            capitals.getChildren().add(armyDisplay);


            //Draw Territories
            ter.setColor();
            //adds all polygons to the countries group
            for (Polygon poly : ter.patches) {
                countries.getChildren().add(poly);
            }
        }

        //adds countries, capitals and lines to the world Group
        world.getChildren().addAll(lines, countries, capitals);


        //Music
        String musicFile = "C:\\Users\\Christoph\\Documents\\sound.mp3";

        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();


        world.setOnMouseClicked(e -> {

            Territory current = isTerritory(e.getX(), e.getY());
            Territory previous = last;
            last = current;

            if (current != null) { //prevent clicks on non-territory

                boolean isRight = e.getButton().ordinal() != 1; //is it a right click?
                boolean handled = ProcessInput(current, previous, isRight, world); //has it been handled (either a method was called or the clicks were ignored)

                if (handled) {
                    last = null; // command handled, reset last, wait for new input
                }
            } else {
                last = null; // click on non-territory; reset last, wait for new input
            }

        });


        //!!!! PHASE DISPLAY - here it should say Acquisition, Reinforce, attack etc in a NICE DESIGN

        Label gameStateDisplay = new Label("" + GameState.getGameState().get());
        GameState.getGameState().addListener((v, oldValue, newValue) -> {
                    gameStateDisplay.setText(GameState.getGameState().get() + "");
                }
        );
        layout.setTop(gameStateDisplay);

        Label reinforceDisplay = new Label("" + GameState.displayBonusPlayer().get());
        GameState.getGameState().addListener((v, oldValue, newValue) -> {
                    gameStateDisplay.setText(GameState.displayBonusPlayer().get() + "");
                }
        );
        layout.setBottom(reinforceDisplay);

        //End round button for player in move/attack stage (stage 3)
        Button button = new Button("End my round");
        button.setAlignment(Pos.CENTER);
        button.setFont(new Font("Calibri", 16));


        button.setOnMouseClicked(e -> {

            GameState.setTerMovNull();
            EnemyAI.conquer();
            GameState.getGameState();
        });

        GameState.getGameState().addListener((v, oldValue, newValue) -> {
            if (GameState.getGameState().get() == 3) {
                layout.setBottom(button);
            }
        });



        //sets world group to layout center
        layout.setCenter(world);

        //sets layout background color (water, oceans)
        layout.setBackground(new Background(new BackgroundFill(Color.DEEPSKYBLUE, new CornerRadii(0), new Insets(0))));

        //scene size, title, stage
        Scene scene = new Scene(layout, 1250, 650);
        stage.setScene(scene);
        stage.setTitle("All Those Territories");

        //sets the scene to layout and shows the stage
        scene.setRoot(layout);
        stage.show();
    }

    //is the territory a territory? Returns null or Territory
    private Territory isTerritory(double x, double y) {
        for (Territory ter : Territory.tmap.values()) {
            for (Polygon poly : ter.patches) {

                if (poly.contains(x, y)) {
                    return ter;
                }
            }
        }
        return null;
    }


    public static void main(String args[]) {
        launch(args);

    }

    // returns true if input was fully handled, false if need to wait for another user input
    // current is always a territory
    // previous may be null
    // isRight: to determine if rightClick or leftClick
    // world: to show game over screen if applicable
    private static boolean ProcessInput(Territory current, Territory previous, boolean isRight, Group world) {

        if (previous == null && current.getOwner() == 0) { //first click on enemy territory is always ignored (no method invoked)
            return true; // ignore
        }

        //ACQUISITION PHASE
        if (GameState.getGameState().get() == 1 && current.getOwner() == -1 && !isRight) {
            Actions.claim(current, 1);
            EnemyAI.acquisition();
            return true; // fully handled
        }

        // CONQUER PHASE

        //reinforcement stage
        if (GameState.getGameState().get() == 2 && current.getOwner() == 1 && !isRight) {
            Actions.reinforce(current, 1);
            if (GameState.getBonus(0) > 0) {
                EnemyAI.reinforcing();
            }
            return true;
        }

        //move or attack stage
        if (GameState.getGameState().get() == 3) {
            if (previous == null) {
                return isRight; // need another territory
            } else if (previous.getOwner() != 1 || previous.getArmy() <= 1) {
                return true; // invalid move, but handled
            } else {

                if (isRight && current.getOwner() == 1 && current.isNeighbor(previous)) { //move
                    Actions.move(previous, current);
                } else if (!isRight && current.getOwner() == 0 && current.isNeighbor(previous)) { //attack
                    Actions.attack(previous, current);
                }
                checkGameOver(world);
                return true; // handles valid and other invalid cases by forcing user to start selecting countries
            }
        }

        return false; // invalid action, but lets continue
    }

    //for game over message
    static void checkGameOver(Group world) {
        // GAME OVER
        if (GameState.getGameState().get() == 4) {
            String won="";
            if (GameState.territoryCount()[0]==42){
                won = "Game Over! You lost!";
            }
            else {
                won="Game Over! You won!";
            }


            Button gameOver = new Button(won);
            gameOver.setPrefSize(300, 150);
            gameOver.setAlignment(Pos.CENTER);
            gameOver.setTranslateX(470);
            gameOver.setTranslateY(200);
            gameOver.setFont(new Font("Calibri", 15));
            gameOver.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
            //gameOver.setPadding(new Insets(20));
            world.getChildren().add(gameOver);

        }
    }


}