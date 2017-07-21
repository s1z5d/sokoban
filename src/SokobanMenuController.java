import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class SokobanMenuController {
	@FXML
	private Button start;
	@FXML
	private Button level;
	@FXML
	private Button info;
	@FXML
	private Button music;
	@FXML
	private Button exit;
	@FXML
	private Button back;
	@FXML
	private Button multi;
	@FXML
	private Button go;
	private Button reset;
	private Button undo;
	private Button genEasy;
	private Button genMed;
	private Button genHard;
	private Button skipLevel;	
	private int moveCount = 0;
	private int gameCheck;
	private int mapNumber;
	Label myLabel;
	//BGM
	Music backgroundMusic = new Music("src/smb.mp3");
	boolean musicSwitch = false; 
	

@FXML
public void handleButtonAction(ActionEvent e) throws IOException{
	Stage primaryStage;
	Parent parent;
	if(e.getSource()==start){
		 primaryStage = new Stage();
		 primaryStage=(Stage) start.getScene().getWindow();      
		 StackPane root = new StackPane();
		 primaryStage.setScene(new Scene(root, 1080, 720));
         startGame(primaryStage, 1);
	}
	if(e.getSource()==multi){
		 primaryStage = new Stage();
		 primaryStage=(Stage) start.getScene().getWindow();      
		 StackPane root = new StackPane();
		 primaryStage.setScene(new Scene(root, 1080, 720));
         startGame(primaryStage, 5);
	}
	
	if(e.getSource()==music){
		if(musicSwitch == false) musicSwitch = true;
		else musicSwitch = false;
		System.out.println("music switch: "+musicSwitch);
		backgroundMusic.playMusic(true, musicSwitch);
	}
	
	if(e.getSource() == info){
	  System.out.println("info");
	  primaryStage= new Stage();
	  parent = FXMLLoader.load(getClass().getResource("Info.fxml"));
	  primaryStage.setScene(new Scene(parent));
	  primaryStage.initModality(Modality.APPLICATION_MODAL);
	  primaryStage.initOwner(info.getScene().getWindow());
	  primaryStage.show();
	}
	
	if(e.getSource()==back){
		System.out.println("back to menu");
		primaryStage = (Stage) back.getScene().getWindow();
		primaryStage.close();
	}
	
	if (e.getSource()==exit){
		 //Platform.exit();
		 primaryStage=(Stage) exit.getScene().getWindow();
		 primaryStage.close(); 

	 }
}

public void startGame(Stage primaryStage, int mapNumber) {
	Map map = new Map();
	moveCount = 0;
	gameCheck = 1;
	this.mapNumber = mapNumber;
	reset = new Button("Reset");
	reset.setPrefWidth(120);
    reset.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	moveCount = 0;
        	map.reset();	
        	map.checkForGoal();
        	myLabel.setText("Moves: " + moveCount);
        }
    });
    GridPane grid;
	if (mapNumber == 1) {
		grid = map.setUpGrid_Map1();
	} else if (mapNumber == 2) {
		grid = map.setUpGrid_Map2();
	} else if (mapNumber == 3) {
		grid = map.setUpGrid_Map3();
	} else if (mapNumber == 4) {
		grid = map.setUpGrid_Map4();
	} else if (mapNumber == 5) {
		grid = map.setUpGrid_MultiPlayer0();
	} else {
		grid = map.setUpGrid_MultiPlayer1();
	}
    
    
	
	
	myLabel = new Label("Moves: " + moveCount);
    undo = new Button("Undo");
    undo.setPrefWidth(120);
    genEasy = new Button("Generate easy level!");
    genEasy.setPrefWidth(240);
    genMed = new Button("Generate intemediate level!");
    genMed.setPrefWidth(240);
    genHard = new Button("Generate hard level!");
    genHard.setPrefWidth(240);
    skipLevel = new Button("Go to next level");
    skipLevel.setPrefWidth(240);
    back = new Button("Main Menu");
    back.setPrefWidth(120);
    
	VBox vbButtons = new VBox();
	vbButtons.setSpacing(20);
	vbButtons.getChildren().addAll(reset, undo, myLabel, skipLevel, genEasy, genMed, genHard);
	vbButtons.getChildren().addAll(music, info, back);
	
	if (mapNumber == 5) {
	vbButtons.getChildren().remove(genEasy);
	vbButtons.getChildren().remove(genMed);
	vbButtons.getChildren().remove(genHard);
	} else if (mapNumber == 6){
		vbButtons.getChildren().remove(genEasy);
		vbButtons.getChildren().remove(genMed);
		vbButtons.getChildren().remove(genHard);
		vbButtons.getChildren().remove(skipLevel);
		Label done = new Label("This is the last level!");
		vbButtons.getChildren().add(3, done);
	} else if (mapNumber == 4){
		vbButtons.getChildren().remove(skipLevel);
		Label done = new Label("This is the last level!");
		vbButtons.getChildren().add(3, done);
	}

	
	StackPane root = new StackPane();
    root.getChildren().addAll(grid,vbButtons);
    StackPane.setMargin(vbButtons, new Insets(100, 20, 10, 850));
    genEasy.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,1);
    	}
    });
    genMed.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,2);
    	}
    });
    genHard.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,3);
    	}
    });
    skipLevel.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		if (mapNumber == 4) return;
    		startGame(primaryStage,mapNumber+1);
    	}
    });
    undo.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		if(map.undoMove()) moveCount++;
    		if(map.checkForGoal())
				winScene(primaryStage);
    		myLabel.setText("Moves: " + moveCount);
    	}
    });    
    
    back.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		Parent root;
			try {
				root = FXMLLoader.load(getClass().getResource("SokobanMenu.fxml"));
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    });
    
    //make a new scene (read up on JFX to understand what a scene is)
    //add the stackpane to the scene
    //remember that the stackpane has the grid as its child
    //and the grid has the player/box/walls as its children
    //so we basically have access to everything via root
    //kinda like a tree
    Scene game = new Scene(root, 1080, 720);
    
    //handles any key press
    handleKeyPress(game, root, map);
    
    //changes the scene of the primary stage to our game
	primaryStage.setScene(game);
	//primaryStage.show();
}



public void ranGame(Stage primaryStage, int difficulty) {
	Map map = new Map();
	moveCount = 0;
	gameCheck = 2;
	reset = new Button("Reset");
	reset.setPrefWidth(120);
    reset.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
        	moveCount = 0;
        	map.reset();
        	map.checkForGoal();
        	myLabel.setText("Moves:" + moveCount);
        }
    });
    
    
	GridPane grid = map.genRanMap(difficulty);
	//set alignment
	grid.setAlignment(Pos.CENTER);
	
    myLabel = new Label("Moves: " + moveCount);
    undo = new Button("Undo");
    undo.setPrefWidth(120);
    genEasy = new Button("Generate easy level!");
    genEasy.setPrefWidth(240);
    genMed = new Button("Generate intemediate level!");
    genMed.setPrefWidth(240);
    genHard = new Button("Generate hard level!");
    genHard.setPrefWidth(240);
    back = new Button("Main Menu");
    back.setPrefWidth(120);
	VBox vbButtons = new VBox();
	vbButtons.setSpacing(20);
	vbButtons.getChildren().addAll(reset, undo, myLabel, genEasy, genMed, genHard);
	vbButtons.getChildren().addAll(music, info, back);
	
	//vbButtons.setAlignment(Pos.CENTER_RIGHT);
	StackPane root = new StackPane();
    root.getChildren().addAll(grid,vbButtons);
    StackPane.setMargin(vbButtons, new Insets(100, 20, 10, 850));

    genEasy.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage, 1);
    		//Scene game = new Scene(root, 1080, 720);
    	    //handleKeyPress(game, root, map);
    	}
    });
    genMed.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,2);
    	}
    });
    genHard.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,3);
    	}
    });
    undo.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		if(map.undoMove()) moveCount++;
    		if(map.checkForGoal())
				winScene(primaryStage);
    		myLabel.setText("Moves: " + moveCount);
    	}
    });    
    back.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		Parent root;
			try {
				root = FXMLLoader.load(getClass().getResource("SokobanMenu.fxml"));
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				
				primaryStage.show();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

    	}
    });
    //make a new scene (read up on JFX to understand what a scene is)
    //add the stackpane to the scene
    //remember that the stackpane has the grid as its child
    //and the grid has the player/box/walls as its children
    //so we basically have access to everything via root
    //kinda like a tree
    Scene game = new Scene(root, 1080, 720);
    
    //handles any key press
    handleKeyPress(game, root, map);
    
    //changes the scene of the primary stage to our game
	primaryStage.setScene(game);
	//primaryStage.show();
}





/*handles key presses
* idk how this code exactly works
* copied from somewhere
* basically if the key is pressed
* it calls the relevant function
* check out event handling and key listeners for more
*/
public void handleKeyPress(Scene game, StackPane root, Map map) {
	Sound move = new Sound("move.wav");
	Sound error = new Sound("error.wav");	
	game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.LEFT) {
            if (map.moveLeft()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.RIGHT) {
            if(map.moveRight()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.UP) {
            if(map.moveUp()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.DOWN) {
            if(map.moveDown()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
	game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.A) {
            if(map.moveLeft2()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.D) {
            if (map.moveRight2()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.W) {
            if(map.moveUp2()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    game.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
        if(key.getCode()==KeyCode.S) {
            if(map.moveDown2()){
            	move.play();
            	moveCount++;
            	myLabel.setText("Moves:" + moveCount);
            } else {
            	error.play();
            }
            if (map.checkForGoal())
				winScene((Stage) game.getWindow());
        }
    });
    
}

public void winScene(Stage primaryStage) {
    genEasy = new Button("Generate easy level!");
    genEasy.setPrefWidth(200);
    genMed = new Button("Generate intemediate level!");
    genMed.setPrefWidth(200);
    genHard = new Button("Generate hard level!");
    genHard.setPrefWidth(200);
    back = new Button("Main Menu");
    back.setPrefWidth(200);
	VBox vbButtons = new VBox();
	vbButtons.setSpacing(20);
	vbButtons.setAlignment(Pos.CENTER);
	
	
	
	if (gameCheck == 1) {
			skipLevel = new Button("Go to next level");
			skipLevel.setPrefWidth(240);
			vbButtons.getChildren().add(skipLevel);
	}
	vbButtons.getChildren().addAll(genEasy, genMed, genHard,back);
	vbButtons.getChildren().addAll(exit);
		
	StackPane root = new StackPane();
    root.getChildren().addAll(vbButtons);
    StackPane.setMargin(vbButtons, new Insets(10, 20, 100, 15));
    genEasy.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage, 1);
    		//Scene game = new Scene(root, 1080, 720);
    	    //handleKeyPress(game, root, map);
    	}
    });
	Image image = new Image("file:win.png");
	ImagePattern backImage= new ImagePattern(image);
 
	 	//this fills in the background
	BackgroundFill fill = new BackgroundFill(backImage, CornerRadii.EMPTY,
           Insets.EMPTY);
	Background background = new Background(fill);
	
	root.setBackground(background);
	
	if (gameCheck == 1) {
		if (mapNumber == 4){
			vbButtons.getChildren().remove(skipLevel);
			Label done = new Label("You've Completed All Levels! Click below"
					+ "to generate random ones!");
			vbButtons.getChildren().add(0,done);
			
		} else if (mapNumber == 6){
			vbButtons.getChildren().remove(skipLevel);
			vbButtons.getChildren().remove(genEasy);
			vbButtons.getChildren().remove(genMed);
			vbButtons.getChildren().remove(genHard);
			Label done = new Label("You've Completed All Our Multiplayer levels!");
			vbButtons.getChildren().add(0,done);
		}
	}
	
	

	skipLevel.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		if (gameCheck == 1) {
    			if (mapNumber == 1) {
    				startGame(primaryStage,2);
    			} else if (mapNumber == 2) {
    				startGame(primaryStage,3);
    			} else if (mapNumber == 3) {
    				startGame(primaryStage,4);
    			} else if (mapNumber == 4) {
    				startGame(primaryStage,5);
    
    			} else if (mapNumber == 5) {
    				startGame(primaryStage,6);
    			} else {
    				startGame(primaryStage,1);
    			}
    		}
    	}
    });
	
	
    genMed.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,2);
    	}
    });
    genHard.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		ranGame(primaryStage,3);
    	}
    });
    back.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
    	public void handle(ActionEvent event) {
    		Parent root;
			try {
				root = FXMLLoader.load(getClass().getResource("SokobanMenu.fxml"));
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				
				primaryStage.show();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

    	}
    });
    
    Scene game = new Scene(root, 1080, 720);
    

    //changes the scene of the primary stage to our game
	primaryStage.setScene(game);
}



}