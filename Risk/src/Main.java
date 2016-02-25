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

//check if these imports are neccessary!!!

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Paint;


public class Main extends Application{

    private Territory rightClicked;
    private Territory ter1;
    private Territory ter2;

    public void start(Stage stage){

        String filepath = "C:\\Users\\Christoph\\Documents\\world.map";


        ReadFile importer = new ReadFile();
        importer.readFile(filepath);


        //groups : countries, capitals , lines
        Group countries=new Group();
        Group capitals=new Group();
        Group lines=new Group();

        BorderPane layout=new BorderPane();
        StackPane layout1=new StackPane(); //!!!! WHAT IS THE PURPUSE OF THIS??
        Group world= new Group();

        //first loop through the territories
        for (Territory ter : Territory.tmap.values()){

            //Lines connecting neighbors
            //second loop through the neighbors , draw the lines from one capital to all neighbouring capitals
            for (Territory neighbors: ter.getNeighbors()){ //solve Alaska - Kamchatka problem!

                Line l= new Line(ter.getCapital()[0],ter.getCapital()[1],neighbors.getCapital()[0],neighbors.getCapital()[1]);
                l.setStroke(Color.ANTIQUEWHITE);
                l.setStrokeWidth(2);
                lines.getChildren().add(l);
            }

            //Armies displayed
            //get number of armies on territory, convert to string
            //set layout , coordinates for capital city (minus 5p for X, minus 10p for Y to optimize look)
            Label armyDisplay=new Label(ter.getArmy()+"");
            ter.getArmyDisplay().addListener((v,oldValue, newValue) -> { //Listener updates army number if it changes
                        armyDisplay.setText(ter.getArmy() +"");
                    }
            );
            armyDisplay.setLayoutX(ter.getCapital()[0]-5);
            armyDisplay.setLayoutY(ter.getCapital()[1]-10);

            //add capital to label
            capitals.getChildren().add(armyDisplay);


            //Draw Territories
            ter.setColor();
            //adds all polygons to the countries group
            for (Polygon poly: ter.patches){
                countries.getChildren().add(poly);
            }
        }

        //adds countries, capitals and lines to the world Group
        world.getChildren().addAll(lines,countries,capitals);


        //RIGHT MOUSE BUTTON - used for move action
        world.setOnMousePressed(e -> {

            if (e.isSecondaryButtonDown()){
                rightClicked=isTerritory(e.getX(),e.getY());
                if (rightClicked.getOwner() != 1){ //right click is only to move troops to own field, so if owner is not player, drop it
                    rightClicked = null;
                }
            }
        });

      //LEFT MOUSE CLICK - 1st click (ter1), second click (ter2)

      world.setOnMouseClicked(e -> {
            if (rightClicked==null&& ter1==null){
                ter1=isTerritory(e.getX(),e.getY());
                if (GameState.getGameState().get()==1 && ter1.getOwner() !=-1){ //in Acquisition phase, only accept clicks on non occupied territories
                    ter1 = null;
                } else
                    if (ter1.getOwner() == 0) { //in all other phases, the first click of player can ONLY be on one of his own territories, otherwise ignore
                    ter1 = null;
                }
            }

            if (ter1 != null && rightClicked ==null){ //2nd left click is only used to point to territory player wishes to attack (owned by enemyAI)
                ter2= isTerritory(e.getX(),e.getY());
                if (ter2.getOwner() != 0){
                    ter2 = null;
                }
            }


          //Players mouse clicks are interpreted according to the GAME STATE

          //ACQUISITION PHASE
            if (GameState.getGameState().get()==1 && ter1 !=null && ter1.getOwner()== -1){
                Actions.claim(ter1, 1);
                EnemyAI.acquisition();
                ter1=null;
                GameState.getGameState();
            }

          // CONQUER PHASE

          //reinforcement stage
            if (GameState.getGameState().get()==2 && ter1 !=null && ter1.getOwner()==1){
                Actions.reinforce(ter1);
                ter1=null;
          }


          //move or attack stage
          if (GameState.getGameState().get()==3 && ter1 !=null && ter1.getOwner()==1 && ter1.getArmy()>1){
              if (rightClicked !=null&& rightClicked.getOwner()==1 && rightClicked.isNeighbor(ter1)){ //move
                  Actions.move(ter1, rightClicked);
                  ter1=null;
                  rightClicked=null;
                  ter2=null;
              }
              else if (ter2!=null && ter2.getOwner()==0&& ter2.isNeighbor(ter1)){ //attack
                  Actions.attack(ter1, ter2);
                  ter1=null;
                  ter2=null;
                  rightClicked=null;
              }
          }

          // GAME OVER
          if (GameState.getGameState().get()==4){

              Button gameOver=new Button("GAME OVER");
              gameOver.setPrefSize(200,200);
              gameOver.setAlignment(Pos.CENTER);
              gameOver.setFont(new Font("Calibri",30));
              gameOver.setPadding(new Insets(20));
          }

       });


        //!!!! PHASE DISPLAY - here it should say Acquisition, Reinforce, attack etc in a NICE DESIGN

        Label gameStateDisplay = new Label("" + GameState.getGameState().get());
        GameState.getGameState().addListener((v,oldValue, newValue) -> {
                    gameStateDisplay.setText(GameState.getGameState().get() + "");
                }
        );
        layout.setTop(gameStateDisplay);

        //!!! When Button appears it messes the map (map shifts up slightly)

        //End round button for player in move/attack stage (stage 3)
        Button button=new Button("End my round");
        button.setPrefSize(100,65);
        button.setAlignment(Pos.CENTER_RIGHT);
        button.setFont(new Font("Calibri",16));
        button.setPadding(new Insets(20));
        //layout.setBottom(button);
        button.setOnMouseClicked(e -> {
            EnemyAI.conquer();
        });

        GameState.getGameState().addListener((v,oldValue,newValue) ->{
            if (GameState.getGameState().get()==3){
                layout.setBottom(button);
            }
        });


        //sets world group to layout center
        layout.setCenter(world);

        //sets layout background color (water, oceans)
        layout.setBackground(new Background(new BackgroundFill(Color.DEEPSKYBLUE,new CornerRadii(0),new Insets(0))));

        //scene size, title, stage
        Scene scene= new Scene(layout, 1250,650);
        stage.setScene(scene);
        stage.setTitle("All Those Territories");

        //sets the scene to layout and shows the stage
        scene.setRoot(layout);
        stage.show();
    }

    //is the territory a territory? Returns null or Territory
    public Territory isTerritory (double x, double y){
        for (Territory ter : Territory.tmap.values()){
            for (Polygon poly: ter.patches){

                if (poly.contains(x,y)){
                    return ter;
                }
            }
        }
        return null;
    }


    public static void main(String args[]){
        launch(args);

    }

}