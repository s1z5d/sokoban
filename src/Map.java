import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Map {
	
	private GridPane grid;
	private Player player;
	private Player player2;
	private ArrayList<Rectangle> boxList;   
	private ArrayList<Rectangle> goalList;
	private ArrayList<Rectangle> wallList;
	private ArrayList<State> stateList;
	private int moveCount;
	private boolean twoPlayer;
	boolean[] soundFlag;
	
	public Map(){
		this.grid = new GridPane();
		this.player = new Player();
		this.boxList = new ArrayList<Rectangle>();
		this.goalList = new ArrayList<Rectangle>();
		this.wallList = new ArrayList<Rectangle>();
		this.stateList = new ArrayList<State>();
		this.player2 = new Player();
		this.soundFlag = new boolean[5];
		//this.boxPos = new ArrayList<Integer>();
	}
	
	public int getMoveCount() {
		return this.moveCount;
	}
	

	public GridPane genRanMap(int difficulty) {
		
		int moves = 0; 
		if (difficulty == 1) {
			for(int i = 0; i < 2; i++) {
				Rectangle rectangle = new Rectangle(100, 100, 50, 50);
				Image image = new Image("file:goal.png");
				ImagePattern imagePattern = new ImagePattern(image);
				rectangle.setFill(imagePattern);
				goalList.add(rectangle);
			}
			moves = 7;
		} else if (difficulty == 2) {
			int k = (Math.random() > 0.5 ? 3 : 4);
			for(int i = 0; i < k; i++) {
				Rectangle rectangle = new Rectangle(100, 100, 50, 50);
				Image image = new Image("file:goal.png");
				ImagePattern imagePattern = new ImagePattern(image);
				rectangle.setFill(imagePattern);
				goalList.add(rectangle);
			}
			moves = 10;
		} else if (difficulty == 3) {
			for(int i = 0; i < 5; i++) {
				Rectangle rectangle = new Rectangle(100, 100, 50, 50);
				Image image = new Image("file:goal.png");
				ImagePattern imagePattern = new ImagePattern(image);
				rectangle.setFill(imagePattern);
				goalList.add(rectangle);
			}
			moves = 7;
		}
		
		twoPlayer = false;
		Circle circle = player.getSprite();  
		
		stateList.clear();
   	 	this.moveCount = 0;	        	
		
		Image image = new Image("file:floor.png");
   	 	ImagePattern color= new ImagePattern(image);
     
   	 	//this fills in the background
   	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
               Insets.EMPTY);
   	 	Background background = new Background(fill);
		
    	//make columns and rows for grid (10 X 10)
    	for (int i = 0; i < 10; i++) {
    		 RowConstraints row = new RowConstraints(50);
    		 ColumnConstraints col = new ColumnConstraints(50);
    		 grid.getColumnConstraints().add(col);
    		 grid.getRowConstraints().add(row);
    		 grid.setBackground(background);

    	}
    	int i = 0;
    	while (i < goalList.size()) {
    		int col = (int )(Math.random() * 9 + 1);
    		int row = (int )(Math.random() * 9 + 1);        	
    		if (!(col == player.getPlayerCol() && row == player.getPlayerRow())) {        		
    			GridPane.setConstraints(goalList.get(i), col, row);
    			i++;
    		} 
    	}
    	
    	grid.getChildren().addAll(goalList);
    	
    	
    	//generate random starting point
    	player.setPlayerCol((int )(Math.random() * 9 + 1));
		player.setPlayerRow((int )(Math.random() * 9 + 1));		
    	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
    	
    	grid.getChildren().add(circle);
        image = new Image("file:down.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        circle.setFill(imagePattern);
        
        int j = 0;
        //fill up whole map with walls
        for (i = 0; i < 10; i++) {
    		for (j = 0; j < 10; j++) {
    			if (i == player.getPlayerCol() && j == player.getPlayerRow()) {
    				continue;
    			}
    			if (goalExists(i,j)) continue;
    			Rectangle wall = new Rectangle(100, 100, 50, 50);
        		image = new Image("file:wall.png");
        		imagePattern = new ImagePattern(image);
        		wall.setFill(imagePattern);
            	wallList.add(wall);
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, i, j);
	        	grid.getChildren().add(wall);
    		}
    	}
        
		for(i = 0; i < goalList.size(); i++) {
			Rectangle rectangle = new Rectangle(100, 100, 50, 50);
			boxList.add(rectangle);
		}
		int boxCol;
		int boxRow;
		ArrayList<Integer> coordinates = new ArrayList<Integer>(2);
        ArrayList<Integer> boxPos = new ArrayList<Integer>();
        for (i = 0; i < boxList.size(); i++) {
        	coordinates = getRandomBoxCoordinates(i, moves);
        	boxCol = coordinates.get(0);
        	boxRow = coordinates.get(1);
        	for (j = 0; j < boxPos.size(); j=j+2) {
        		while(boxCol == boxPos.get(j) && boxRow == boxPos.get(j+1)) {
        			coordinates = getRandomBoxCoordinates(i, moves);
                	boxCol = coordinates.get(0);
                	boxRow = coordinates.get(1);
        		}
        	}
        	GridPane.setConstraints(boxList.get(i), boxCol, boxRow);
        	boxPos.add(boxCol);
        	boxPos.add(boxRow);
        }
        grid.getChildren().addAll(boxList);
    	image = new Image("file:crate.png");
    	imagePattern = new ImagePattern(image);
    	
    	for(Rectangle rectangle : boxList) {
    		rectangle.setFill(imagePattern);			
    	}
    	ArrayList<Integer> playerPos = new ArrayList<Integer>();
        playerPos.add(player.getPlayerCol());
        playerPos.add(player.getPlayerRow());
        State state = new State(playerPos, boxPos);
        stateList.add(state); 	
    	clearPlayerPath(difficulty);
		if (difficulty == 1 || difficulty == 2) clearBoxArea();
		return grid;
	}
	
	private void clearBoxArea() {
		
		for (int i = 0; i < boxList.size(); i++) {
			int col = GridPane.getColumnIndex(boxList.get(i));
			int row = GridPane.getRowIndex(boxList.get(i));
			int count = 0;
			if (obstacleExists(col+1, row)) {
				System.out.println("clearing wall");
				Rectangle check = getObstacle(col+1, row);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col+1, row+1)) {
				Rectangle check = getObstacle(col+1, row+1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col, row+1)) {
				Rectangle check = getObstacle(col, row+1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col-1, row+1)) {
				Rectangle check = getObstacle(col-1, row+1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col-1, row)) {
				Rectangle check = getObstacle(col-1, row);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col-1, row-1)) {
				Rectangle check = getObstacle(col-1, row-1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col, row-1)) {
				Rectangle check = getObstacle(col, row-1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
			if (obstacleExists(col+1, row-1)) {
				Rectangle check = getObstacle(col+1, row-1);
				grid.getChildren().remove(check);
				wallList.remove(check);
				count++;
				if (count > 3) continue;
			}
		}
		
	}
	
	
	private void clearPlayerPath(int difficulty) {	
		
		if (difficulty == 1) {
			BFS bfs = new BFS(player.getPlayerCol(), player.getPlayerRow());
			for (int i = 0; i < boxList.size(); i++) {
				MapNode box = bfs.getNodeFromCo(GridPane.getColumnIndex(boxList.get(i)), GridPane.getRowIndex(boxList.get(i)));
				Iterable<MapNode> path = bfs.getPath(box);
				for(MapNode next : path) {
					if (obstacleExists(next.getCol(),next.getRow())) {
						Rectangle check = getObstacle(next.getCol(), next.getRow());
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
				}
			}
		} else {
			BFS bfs = new BFS(player.getPlayerCol(), player.getPlayerRow());
			for (int i = 0; i < boxList.size(); i++) {
				if (i == 0) {
					MapNode box = bfs.getNodeFromCo(GridPane.getColumnIndex(boxList.get(i)), GridPane.getRowIndex(boxList.get(i)));
					Iterable<MapNode> path = bfs.getPath(box);
					for(MapNode next : path) {
						if (obstacleExists(next.getCol(),next.getRow())) {
							Rectangle check = getObstacle(next.getCol(), next.getRow());
							grid.getChildren().remove(check);
							wallList.remove(check);
						}
					}
				} else {
					BFS bfsBox = new BFS(GridPane.getColumnIndex(boxList.get(i-1)), GridPane.getRowIndex(boxList.get(i-1)));
					MapNode box = bfsBox.getNodeFromCo(GridPane.getColumnIndex(boxList.get(i)), GridPane.getRowIndex(boxList.get(i)));
					Iterable<MapNode> path = bfsBox.getPath(box);
					for(MapNode next : path) {
						if (obstacleExists(next.getCol(),next.getRow())) {
							Rectangle check = getObstacle(next.getCol(), next.getRow());
							grid.getChildren().remove(check);
							wallList.remove(check);
						}
					}
				}
			}
		}
		
	}
	
	
	//gets a random box col based on difficulty
	//works backwards from the goals
	private ArrayList<Integer> getRandomBoxCoordinates(int i, int moves) {
		
		int count = 0;
		int prev = -1;
		int move = 0;
		Rectangle goal = goalList.get(i);
		int col = GridPane.getColumnIndex(goal);
		int row = GridPane.getRowIndex(goal);
		boolean flag1,flag2, flag3, flag4;
		flag1 = flag2 = flag3 = flag4 = false;
		
		while (count < moves) {
			move = (int) Math.floor(Math.random()*4);
			if (flag1 && flag2 && flag3 && flag4) break;
			//move up
			if (move == 0) {
				if (player.getPlayerCol() == col && player.getPlayerRow() == row-1) {
					flag1 = true;
					continue;
				}
				if (row == 0 || row == 1) {
					flag1 = true;
					continue;
				}
				if (prev == 2) {
					flag1 = true;
					continue;
				}
				if (prev == 3) {
					Rectangle check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row-2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row - 1;
					prev = 0;
					flag1 = false;
					count++;
					continue;
				}
				if (prev == 1) {
					Rectangle check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row-2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row - 1;
					prev = 0;
					flag1 = false;
					count++;
					continue;
				}
				if (prev == -1) {
					Rectangle check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					row = row -1;
					prev = 0;
					flag1 = false;
					count++;
					continue;
				}
				if (prev == 0) {
					Rectangle check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row-2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row - 1;
					prev = 0;
					flag1 = false;
					count++;
					continue;
				}
			
			//move right
			} else if (move == 1) {
				if (player.getPlayerCol() == col+1 && player.getPlayerRow() == row) {
					flag2 = true;
					continue;
				}
				if (col == 9 || col == 8) {
					flag2 = true;
					continue;
				}
				if (prev == 3) {
					flag2 = true;
					continue;
				}
				if (prev == 0) {
					Rectangle check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col+2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col + 1;
					prev = 1;
					flag2 = false;
					count++;
					continue;
				}
				if (prev == 2) {
					Rectangle check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col+2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col + 1;
					prev = 1;
					flag2 = false;
					count++;
					continue;
				}
				if (prev == -1) {
					Rectangle check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					col = col + 1;
					prev = 1;
					flag2 = false;
					count++;
					continue;
				}
				if (prev == 1) {
					Rectangle check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col+2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col + 1;
					prev = 1;
					flag2 = false;
					count++;
					continue;
				}
			
			//move down
			} else if (move == 2) {
				if (player.getPlayerCol() == col && player.getPlayerRow() == row+1) {
					flag3 = true;
					continue;
				}
				if (row == 8 || row == 9) {
					flag3 = true;
					continue;
				}
				if (prev == 0) {
					flag3 = true;
					continue;
				}
				if (prev == 3) {
					Rectangle check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row+2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row + 1;
					prev = 2;
					flag3 = false;
					count++;
					continue;
				}
				if (prev == 1) {
					Rectangle check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col+1, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row+2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row + 1;
					prev = 2;
					flag3 = false;
					count++;
					continue;
				}
				if (prev == -1) {
					Rectangle check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					row = row + 1;
					prev = 2;
					flag3 = false;
					count++;
					continue;
				}
				if (prev == 2) {
					Rectangle check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col, row+2);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					row = row + 1;
					prev = 2;
					flag3 = false;
					count++;
					continue;
				}
			
			//move left
			} else if (move == 3) {
				if (player.getPlayerCol() == col-1 && player.getPlayerRow() == row) {
					flag4 = true;
					continue;
				}
				if (col == 0 || col == 1) {
					flag4 = true;
					continue;
				}
				if (prev == 1) {
					flag4 = true;
					continue;
				}
				if (prev == 0) {
					Rectangle check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row-1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col-2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col - 1;
					prev = 3;
					flag4 = false;
					count++;
					continue;
				}
				if (prev == 2) {
					Rectangle check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					check = getObstacle(col-1, row+1);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col-2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col - 1;
					prev = 3;
					flag4 = false;
					count++;
					continue;
				}
				if (prev == -1) {
					Rectangle check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					col = col - 1;
					prev = 3;
					flag4 = false;
					count++;
					continue;
				}
				if (prev == 3) {
					Rectangle check = getObstacle(col-1, row);
					grid.getChildren().remove(check);
					wallList.remove(check);
					if (count == moves-1) {
						check = getObstacle(col-2, row);
						grid.getChildren().remove(check);
						wallList.remove(check);
					}
					col = col - 1;
					prev = 3;
					flag4 = false;
					count++;
					continue;
				}
			}
		}
		
		ArrayList<Integer> coordinates = new ArrayList<Integer>(2);
		coordinates.add(0, col);
		coordinates.add(1, row);
		return coordinates;
	}
	
	public boolean checkForGoal() {
		
		boolean flag = true;
		boolean[] flags = new boolean[10];
		for (int i = 0; i < 10; i++) {
			flags[i] = true;
		}
		Sound goalSound = new Sound("goal.wav");
		for (int i = 0; i < boxList.size(); i++) {
			for (Rectangle goal : goalList) {
				if (GridPane.getColumnIndex(boxList.get(i)) == GridPane.getColumnIndex(goal) && GridPane.getRowIndex(boxList.get(i)) == GridPane.getRowIndex(goal)) {
					Image image = new Image("file:goalCrate.png");
			    	ImagePattern imagePattern = new ImagePattern(image);
			    	((Rectangle) boxList.get(i)).setFill(imagePattern);
					if (soundFlag[i] == false) {
						soundFlag[i] = true;
						goalSound.play();
					}
			    	flags[i] = true;
					break;
				} else {
					Image image = new Image("file:crate.png");
					ImagePattern imagePattern = new ImagePattern(image);
			    	((Rectangle) boxList.get(i)).setFill(imagePattern);
			    	flags[i] = false;
				}
			}
			//goalSound.stop();
		}
		
		for (int i = 0; i < 10; i++){
			if (flags[i] == false) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	//takes in grid, column and row which have to be checked for an obstacle
	//goes through all the children of the gridpane
	//checks for polygons and returns true/false
	public boolean obstacleExists(int col, int row) {
		for (int i = 0; i < wallList.size(); i++) {
			Node check = wallList.get(i);			
			if (GridPane.getColumnIndex(check) == col && GridPane.getRowIndex(check) == row) {
				return true;
			}
		}
		return false;
	}
	
	//gets obstacle at given coordinate
	public Rectangle getObstacle(int col, int row) {
		for (int i = 0; i < wallList.size(); i++) {
			Node check = wallList.get(i);
			if (GridPane.getColumnIndex(check) == col && GridPane.getRowIndex(check) == row) {
				return (Rectangle) check;
			}
		}
		return null;
	}
	
	//checks if a box exists at a given coordinate
	public boolean boxExists(int col, int row) {
		for (Rectangle rectangle : boxList) {
			if (GridPane.getColumnIndex(rectangle) == col && GridPane.getRowIndex(rectangle) == row) {
				return true;
			}
		}
		return false;
	}
	
	//checks if goal exists at a given coordinate
	public boolean goalExists(int col, int row) {
		for (Rectangle rectangle : goalList) {
			if (GridPane.getColumnIndex(rectangle) == col && GridPane.getRowIndex(rectangle) == row) {
				return true;
			}
		}
		return false;
	}
	
	
	//check if box is adjacent in given direction from a set of coordinates
	// up = 1, right = 2, down = 3, left = 4
	public boolean adjacentBoxExists(int direction, int col, int row) {
		
		for (Rectangle rectangle : boxList) {
			
			if (direction == 1) {
				if (GridPane.getColumnIndex(rectangle)==col && GridPane.getRowIndex(rectangle) == row-1) {
					return true;
				}
			} else if (direction == 2) {
				if (GridPane.getColumnIndex(rectangle)==col+1 && GridPane.getRowIndex(rectangle) == row) {
					return true;
				}
			} else if (direction == 3) {
				if (GridPane.getColumnIndex(rectangle)==col && GridPane.getRowIndex(rectangle) == row+1) {
					return true;
				}
			} else if (direction == 4) {
				if (GridPane.getColumnIndex(rectangle)==col-1 && GridPane.getRowIndex(rectangle) == row) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public Rectangle getAdjacentBox(int direction, int col, int row) {
		
		for (Rectangle rectangle : boxList) {
			
			if (direction == 1) {
				if (GridPane.getColumnIndex(rectangle)==col && GridPane.getRowIndex(rectangle) == row-1) {
					return rectangle;
				}
			} else if (direction == 2) {
				if (GridPane.getColumnIndex(rectangle)==col+1 && GridPane.getRowIndex(rectangle) == row) {
					return rectangle;
				}
			} else if (direction == 3) {
				if (GridPane.getColumnIndex(rectangle)==col && GridPane.getRowIndex(rectangle) == row+1) {
					return rectangle;
				}
			} else if (direction == 4) {
				if (GridPane.getColumnIndex(rectangle)==col-1 && GridPane.getRowIndex(rectangle) == row) {
					return rectangle;
				}
			}
		}
		return null;
		
	}
	
	
	
	//moves the player left
	public boolean moveLeft() {
		//get the player and the box
		Circle sprite = null;
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				break;
			}
		}
		int col = player.getPlayerCol();
		int row = player.getPlayerRow();
		Image image = new Image("file:left.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);

		//this case is if the box is adjacent to the player
		if (adjacentBoxExists(4, col, row)) {
			
		    image = new Image("file:pushLeft.png",100,100,true,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			//if box is at last col, do nothing
			//if theres an obstacle behind the box, do nothing
			//if theres a box behind the box, do nothing
			//else move box and player
			if (GridPane.getColumnIndex(getAdjacentBox(4, col, row))==0) {
				return false;
			} else if (obstacleExists(col-2, row)) {
				System.out.println("pls");
				return false;
			} else if(adjacentBoxExists(4, col-1, row)) {
				return false;
			} else if (playerExists(4,col-1,row,player2)){
				System.out.println("whudshk");
				return false;
			} else {	
				GridPane.setColumnIndex(sprite, col-1);
				player.setPlayerCol(col-1);
				GridPane.setColumnIndex(getAdjacentBox(4, col, row), col-2);
		        moveCount++;
		        ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
		        return true;
			}
		//this case is when there is no adjacent box
		} else {
			//if you're at the edge, no change
			if (col == 0) {
				return false;
			//else if there's an obstacle in front of you, do nothing
			} else if (obstacleExists(col-1, row)) {
				System.out.println("pls");
				return false;
			//else move
			} else if (playerExists(4,col,row,player2)){
				return false;
			} else {	
				GridPane.setColumnIndex(sprite, col-1);
				player.setPlayerCol(col-1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}	
	}
	
	//work same as above
	public boolean moveRight() {
		
		Circle sprite = null;
		
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				break;
			}
		}
		int col = player.getPlayerCol();
		int row = player.getPlayerRow();
		Image image = new Image("file:right.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);

		if (adjacentBoxExists(2, col, row)) {
		    image = new Image("file:pushRight.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			
			if (GridPane.getColumnIndex(getAdjacentBox(2, col, row))==9) {
				return false;
			} else if (obstacleExists(col+2, row)) {
				return false;
			} else if(adjacentBoxExists(2, col+1, row)) {
				return false;
			} else if (playerExists(2,col+1,row,player2)){
				return false;
			} else {	
				GridPane.setColumnIndex(sprite, col+1);
				player.setPlayerCol(col+1);
				GridPane.setColumnIndex(getAdjacentBox(2, col, row), col+2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (col == 9) {
				return false;
			} else if (obstacleExists(col+1, row)) {
				return false;
			} else if (playerExists(2,col,row,player2)){
				return false;
			} else {
				GridPane.setColumnIndex(sprite, col+1);
				player.setPlayerCol(col+1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}	
	}
	
	public boolean moveUp() {
		
		Circle sprite = null;
		
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				break;
			}
		}
		int col = player.getPlayerCol();
		int row = player.getPlayerRow();

		Image image = new Image("file:up.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);

		if (adjacentBoxExists(1, col, row)) {
			
		    image = new Image("file:pushUp.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			if (GridPane.getRowIndex(getAdjacentBox(1, col, row))==0) {
				return false;
			} else if (obstacleExists(col, row-2)) {
				return false;
			} else if(adjacentBoxExists(1, col, row-1)) { 
				return false;
			} else if (playerExists(1,col,row-1,player2)){
				return false;
			} else {	
				GridPane.setRowIndex(sprite, row-1);
				player.setPlayerRow(row-1);
				GridPane.setRowIndex(getAdjacentBox(1, col, row), row-2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (row == 0) {
				return false;
			} else if (obstacleExists(col, row-1)) {
				return false;
			} else if (playerExists(1,col,row,player2)){
				return false;
			} else {
				GridPane.setRowIndex(sprite, row-1);
				player.setPlayerRow(row-1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}	
	}
	
	public boolean moveDown() {
				
		Circle sprite = null;

		
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				break;
			}
		}
		int col = player.getPlayerCol();
		int row = player.getPlayerRow();
		
		Image image = new Image("file:down.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);
		
		if (adjacentBoxExists(3, col, row)) {
		    image = new Image("file:pushDown.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			if (GridPane.getRowIndex(getAdjacentBox(3, col, row))==9) {
				return false;
			} else if (obstacleExists(col, row+2)) {
				return false;
			} else if(adjacentBoxExists(3, col, row+1)) {
				return false;
			} else if (playerExists(3,col,row+1,player2)){
				return false;
			} else {	
				GridPane.setRowIndex(sprite, row+1);
				player.setPlayerRow(row+1);
				GridPane.setRowIndex(getAdjacentBox(3, col, row), row+2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (row == 9) {
				return false;
			} else if (obstacleExists(col, row+1)) {
				return false;
			} else if (playerExists(3,col,row,player2)){
				return false;
			} else {
				GridPane.setRowIndex(sprite, row+1);
				player.setPlayerRow(row+1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
		        return true;
			}
		}	
	}
	
	public boolean moveLeft2() {
		//get the player and the box
		if (!twoPlayer) return false;
		Circle sprite = null;
		int no = 0;
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				if (no == 0) {
					no = 1;
					continue;
				} else {
					break;
				}
			}
		}
		int col = player2.getPlayerCol();
		int row = player2.getPlayerRow();
		
		Image image = new Image("file:left2.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);
	
		if (adjacentBoxExists(4, col, row)) {
			
			//if box is at last col, do nothing
			//if theres an obstacle behind the box, do nothing
			//if theres a box behind the box, do nothing
			//else move box and player
			if (GridPane.getColumnIndex(getAdjacentBox(4, col, row))==0) {
				return false;
			} else if (obstacleExists(col-2, row)) {
				return false;
			} else if(adjacentBoxExists(4, col-1, row)) {
				return false;
			} else if (playerExists(4,col-1,row,player)){
				return false;
			} else {	
				GridPane.setColumnIndex(sprite, col-1);
				player2.setPlayerCol(col-1);
				GridPane.setColumnIndex(getAdjacentBox(4, col, row), col-2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		//this case is when there is no adjacent box
		} else {
			//if you're at the edge, no change
			if (col == 0) {
				return false;
			//else if there's an obstacle in front of you, do nothing
			} else if (obstacleExists(col-1, row)) {
				return false;
			//else move
			} else if (playerExists(4,col,row,player)){
				return false;
			} else {
				GridPane.setColumnIndex(sprite, col-1);
				player2.setPlayerCol(col-1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}	


	}
	
	public boolean moveRight2() {
		if (!twoPlayer) return false;
		Circle sprite = null;
		int no = 0;
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				if (no == 0) {
					no = 1;
					continue;
				} else {
					break;
				}
			}
		}
		
		int col = player2.getPlayerCol();
		int row = player2.getPlayerRow();
		
		Image image = new Image("file:right2.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);
		

        
		if (adjacentBoxExists(2, col, row)) {
			
		    image = new Image("file:pushRight2.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			if (GridPane.getColumnIndex(getAdjacentBox(2, col, row))==9) {
				return false;
			} else if (obstacleExists(col+2, row)) {
				return false;
			} else if(adjacentBoxExists(2, col+1, row)) {
				return false;
			} else if (playerExists(2,col+1,row,player)){
				return false;
			} else {	
				GridPane.setColumnIndex(sprite, col+1);
				player2.setPlayerCol(col+1);
				GridPane.setColumnIndex(getAdjacentBox(2, col, row), col+2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (col == 9) {
				return false;
			} else if (obstacleExists(col+1, row)) {
				return false;
			} else if (playerExists(2,col,row,player)){
				return false;
			} else {
				GridPane.setColumnIndex(sprite, col+1);
				player2.setPlayerCol(col+1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}		
	}
	
	public boolean moveUp2() {
		
		if (!twoPlayer) return false;
		
		Circle sprite = null;
		int no = 0;
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				if (no == 0) {
					no = 1;
					continue;
				} else {
					break;
				}
			}
		}
		int col = player2.getPlayerCol();
		int row = player2.getPlayerRow();
		
		Image image = new Image("file:up2.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);
        
		if (adjacentBoxExists(1, col, row)) {
			
		    image = new Image("file:pushUp2.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);

			if (GridPane.getRowIndex(getAdjacentBox(1, col, row))==0) {
				return false;
			} else if (obstacleExists(col, row-2)) {
				return false;
			} else if(adjacentBoxExists(1, col, row-1)) { 
				return false;
			} else if (playerExists(1,col,row-1,player)){
				return false;
			} else {	
				GridPane.setRowIndex(sprite, row-1);
				player2.setPlayerRow(row-1);
				GridPane.setRowIndex(getAdjacentBox(1, col, row), row-2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (row == 0) {
				return false;
			} else if (obstacleExists(col, row-1)) {
				return false;
			} else if (playerExists(1,col,row,player)){
				return false;
			} else {
				GridPane.setRowIndex(sprite, row-1);
				player2.setPlayerRow(row-1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		}	
	}
	
	public boolean moveDown2() {
		if (!twoPlayer) return false;		
		Circle sprite = null;
		int no = 0;
		for (Node node : grid.getChildren()) {
			if (node.getClass() == Circle.class) {
				sprite = (Circle) node;
				if (no == 0) {
					no = 1;
					continue;
				} else {
					break;
				}
			}
		}
		int col = player2.getPlayerCol();
		int row = player2.getPlayerRow();
		
		Image image = new Image("file:down2.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        sprite.setFill(imagePattern);
        
		if (adjacentBoxExists(3, col, row)) {
		    image = new Image("file:pushDown2.png",100,100,false,false);
	        imagePattern = new ImagePattern(image);
	        sprite.setFill(imagePattern);
			
			if (GridPane.getRowIndex(getAdjacentBox(3, col, row))==9) {
				return false;
			} else if (obstacleExists(col, row+2)) {
				return false;
			} else if(adjacentBoxExists(3, col, row+1)) {
				return false;
			} else if (playerExists(3,col,row+1,player)){
				return false;
			} else {	
				GridPane.setRowIndex(sprite, row+1);
				player2.setPlayerRow(row+1);
				GridPane.setRowIndex(getAdjacentBox(3, col, row), row+2);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
				return true;
			}
		} else {
			if (row == 9) {
				return false;
			} else if (obstacleExists(col, row+1)) {
				return false;
			} else if (playerExists(3,col,row,player)){
				return false;
			} else {
				GridPane.setRowIndex(sprite, row+1);
				player2.setPlayerRow(row+1);
				moveCount++;
				ArrayList<Integer> playerPos = new ArrayList<Integer>();
		        playerPos.add(player.getPlayerCol());
		        playerPos.add(player.getPlayerRow());
		        if (twoPlayer) {
		        	playerPos.add(player2.getPlayerCol());
		        	playerPos.add(player2.getPlayerRow());
		        }
		        ArrayList<Integer> boxPos = new ArrayList<Integer>();
		        int j = 0;
		        for (int i = 0; i < boxList.size(); i++) {
		        		boxPos.add(j, GridPane.getColumnIndex(boxList.get(i)));
		        		boxPos.add(j+1, GridPane.getRowIndex(boxList.get(i)));
		        		j = j + 2;
		        }
				State state = new State(playerPos, boxPos);
		        stateList.add(state);
		        return true;
			}
		}	
	}
	
	
	public boolean undoMove() {
		
		if (stateList.size() == 1) return false;
		
		stateList.remove(stateList.size() - 1);
		State state = stateList.get(stateList.size()-1);
		int j = 0;
		for (int i = 0 ; i < boxList.size(); i++) {
			GridPane.setConstraints(boxList.get(i), state.getBoxPos().get(j), state.getBoxPos().get(j+1));
			System.out.println(state.getBoxPos().get(j) + " " + state.getBoxPos().get(j+1));
			j=j+2;
		}
		GridPane.setColumnIndex(player.getSprite(), state.getPlayerPos().get(0));
		GridPane.setRowIndex(player.getSprite(), state.getPlayerPos().get(1));
		player.setPlayerCol(state.getPlayerPos().get(0));
		player.setPlayerRow(state.getPlayerPos().get(1));
		if (twoPlayer) {
			GridPane.setColumnIndex(player2.getSprite(), state.getPlayerPos().get(2));
			GridPane.setRowIndex(player2.getSprite(), state.getPlayerPos().get(3));
			player2.setPlayerCol(state.getPlayerPos().get(2));
			player2.setPlayerRow(state.getPlayerPos().get(3));
		}
		return true;
	}
	
	
	public void reset() {
		
		if (stateList.size() == 0) return;
		State state = stateList.get(0);
		stateList.clear();
		stateList.add(state);
		int j = 0;
		for (int i = 0 ; i < boxList.size(); i++) {
			GridPane.setConstraints(boxList.get(i), state.getBoxPos().get(j), state.getBoxPos().get(j+1));
			System.out.println(state.getBoxPos().get(j) + " " + state.getBoxPos().get(j+1));
			j=j+2;
		}
		GridPane.setColumnIndex(player.getSprite(), state.getPlayerPos().get(0));
		GridPane.setRowIndex(player.getSprite(), state.getPlayerPos().get(1));
		player.setPlayerCol(state.getPlayerPos().get(0));
		player.setPlayerRow(state.getPlayerPos().get(1));
		if (twoPlayer) {
			GridPane.setColumnIndex(player2.getSprite(), state.getPlayerPos().get(2));
			GridPane.setRowIndex(player2.getSprite(), state.getPlayerPos().get(3));
			player2.setPlayerCol(state.getPlayerPos().get(2));
			player2.setPlayerRow(state.getPlayerPos().get(3));
		}
		
	}
	
	public boolean playerExists(int direction, int col, int row,Player other) {
		
		
		if (direction == 1) {
			if (other.getPlayerCol()==col && other.getPlayerRow() == row-1) {
				return true;
			}
		} else if (direction == 2) {
			if (other.getPlayerCol()==col+1 && other.getPlayerRow() == row) {
				return true;
			}
		} else if (direction == 3) {
			if (other.getPlayerCol()==col && other.getPlayerRow() == row+1) {
				return true;
			}
		} else if (direction == 4) {
			if (other.getPlayerCol()==col-1 && other.getPlayerRow() == row) {
				return true;
			}
		}
	
	
	return false;
	
}

	public GridPane setUpGrid_Map1(){
		//make player and box and also the grid
		for(int i = 0; i < 4; i++) {
			Rectangle rectangle = new Rectangle(100, 100, 50, 50);
			boxList.add(rectangle);
		}
		twoPlayer = false;
		Circle circle = player.getSprite();  
		
		stateList.clear();
   	 	this.moveCount = 0;	
		
		//makes a new image pattern that can apply to shapes
   	 	Image image = new Image("file:floor.png");
   	 	ImagePattern color= new ImagePattern(image);
     
   	 	//this fills in the background
   	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
               Insets.EMPTY);
   	 	Background background = new Background(fill);
		
    	//make columns and rows for grid (6 X 8)
    	for (int i = 0; i < 10; i++) {
    		 RowConstraints row = new RowConstraints(50);
    		 ColumnConstraints col = new ColumnConstraints(50);
    		 grid.getColumnConstraints().add(col);
    		 grid.getRowConstraints().add(row);
    		 grid.setBackground(background);
    	}
    	
    	//add the box to the grid
    	int i = 0;
        ArrayList<Integer> boxPos = new ArrayList<Integer>();
    	GridPane.setConstraints(boxList.get(0), 2, 3);
    	boxPos.add(2);
    	boxPos.add(3);
    	GridPane.setConstraints(boxList.get(1), 1, 4);
    	boxPos.add(1);
    	boxPos.add(4);
    	GridPane.setConstraints(boxList.get(2), 3, 4);
    	boxPos.add(3);
    	boxPos.add(4);
    	GridPane.setConstraints(boxList.get(3), 2, 5);
    	boxPos.add(2);
    	boxPos.add(5);
    	
    	//set player col and row and add it to the grid
    	player.setPlayerCol(2);
    	player.setPlayerRow(4);
    	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
    	
    	for(i=0;i<10;i++){
    		if(i==1)continue;
    		if(i==2)continue;
    		if(i==3)continue;
    		if(i==4)continue;
    		if(i==5)continue;
    		if(i==6)continue;
    		if(i==7)continue;
    		if(i==8)continue;
    		//add goal, basically box which is colored green
    		Rectangle goal = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:goal.png");
    		ImagePattern imagePattern = new ImagePattern(image);
    		goal.setFill(imagePattern);
    		goalList.add(goal);
    		
    		//add goal to grid
    		GridPane.setConstraints(goal, i, 0);
    		grid.getChildren().add(goal);
    		
    		//set the alignment of the grid
    		grid.setAlignment(Pos.CENTER);
    	}
    	for(i=3;i<7;i++){
    		if(i==4)continue;
    		if(i==5)continue;
    		//add goal, basically box which is colored green
    		Rectangle goal = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:goal.png");
    		ImagePattern imagePattern = new ImagePattern(image);
    		goal.setFill(imagePattern);
    		goalList.add(goal);		
    		//add goal to grid
    		GridPane.setConstraints(goal, 9, i);
    		grid.getChildren().add(goal);
    		
    		//set the alignment of the grid
    		grid.setAlignment(Pos.CENTER);
    	}
    	//add the box and the player as children of the grid
    	//they won't show up otherwise
    	grid.getChildren().addAll(boxList);
    	grid.getChildren().add(circle);
    	
        image = new Image("file:down.png",100,100,false,false);
        ImagePattern imagePattern = new ImagePattern(image);
        circle.setFill(imagePattern);
        
		image = new Image("file:crate.png");
		imagePattern = new ImagePattern(image);
        
		for(Rectangle rectangle : boxList) {
			rectangle.setFill(imagePattern);			
		}
    	
    	//add polygons (walls/diamonds)
    	for (i = 0; i < 10; i++) {
    		if (i==0) continue;
    		if (i==1) continue;
    		if (i==2) continue;
    		if (i==8) continue;
    		if (i==6) continue;
    		if (i==7) continue;
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 7);
        	grid.getChildren().add(wall);
    	}
    	for (i = 0; i < 10; i++) {
    		if (i==2) continue;
    		if (i==3) continue;
    		if (i==5) continue;
    		if (i==7) continue;
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 2);
        	grid.getChildren().add(wall);
    	}
    	for (i = 0; i < 10; i++) {
    		if (i==5) continue;
    		if (i==7) continue;
    		if (i==8) continue;
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern); 
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, 5, i);
        	grid.getChildren().add(wall);
    	}
    	for (i = 8; i <10 ; i++) {
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 5);
        	grid.getChildren().add(wall);
    	}
    	for (i = 0; i < 10; i++) {
    		if(i==6)continue;
    		if(i==7)continue;
    		if(i==8)continue;
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 9);
        	grid.getChildren().add(wall);
    	}
    	for (i = 0; i < 10; i++) {
    		if(i==0) continue;
    		if(i==7) continue;
    		if(i==8) continue;
    		if(i==9) continue;
    		Rectangle wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, 0, i);
        	grid.getChildren().add(wall);
    	}
	    	//Add polygon at position (1,1)
	    	Rectangle wall = new Rectangle(100, 100, 50, 50);
			image = new Image("file:wall.png");
			imagePattern = new ImagePattern(image);
			wall.setFill(imagePattern);
	    	GridPane.setConstraints(wall, 7, 8);
	    	grid.getChildren().add(wall);
        	wallList.add(wall);
	    	//Add polygon at position (0,6)
	    	wall = new Rectangle(100, 100, 50, 50);
    		image = new Image("file:wall.png");
    		imagePattern = new ImagePattern(image);
    		wall.setFill(imagePattern);
        	wallList.add(wall);
	    	GridPane.setConstraints(wall, 6, 6);
	    	grid.getChildren().add(wall);
	        ArrayList<Integer> playerPos = new ArrayList<Integer>();
	        playerPos.add(player.getPlayerCol());
	        playerPos.add(player.getPlayerRow());
	        State state = new State(playerPos, boxPos);
	        stateList.add(state);
	    	
        //set grid lines visible, easier to debug, will be turned off later
        //grid.setGridLinesVisible(true);
        return grid;
	}
	

	public GridPane setUpGrid_Map2(){
	//make player and box and also the grid
	for(int i = 0; i < 4; i++) {
		Rectangle rectangle = new Rectangle(100, 100, 50, 50);
		boxList.add(rectangle);
	}
	twoPlayer = false;
	Circle circle = player.getSprite();      	
	
	stateList.clear();
	 	this.moveCount = 0;	
	
	//makes a new image pattern that can apply to shapes
	 	Image image = new Image("file:floor.png");
	 	ImagePattern color= new ImagePattern(image);
 
	 	//this fills in the background
	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
           Insets.EMPTY);
	 	Background background = new Background(fill);
	
	
	//make columns and rows for grid (10 X 10)
	for (int i = 0; i < 10; i++) {
		 RowConstraints row = new RowConstraints(50);
		 ColumnConstraints col = new ColumnConstraints(50);
		 grid.getColumnConstraints().add(col);
		 grid.getRowConstraints().add(row);
		 grid.setBackground(background);

	}
	
	//add the box to the grid
	int i = 0;
  ArrayList<Integer> boxPos = new ArrayList<Integer>();
	GridPane.setConstraints(boxList.get(0), 2, 1);
  boxPos.add(2);
	boxPos.add(1);
	GridPane.setConstraints(boxList.get(1), 0, 5);
  boxPos.add(0);
	boxPos.add(5);
	GridPane.setConstraints(boxList.get(2), 3, 2);
  boxPos.add(3);
	boxPos.add(2);
	GridPane.setConstraints(boxList.get(3), 4, 4);
	boxPos.add(4);
	boxPos.add(4);
	//set player col and row and add it to the grid
	player.setPlayerCol(2);
	player.setPlayerRow(4);
	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
  
  
  for(i=0;i<2;i++){
    	//add goal, basically box which is colored green
    	Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
    	
    	//add goal to grid
    	GridPane.setConstraints(goal, i, 0);
    	grid.getChildren().add(goal);

    	//set the alignment of the grid
        grid.setAlignment(Pos.CENTER);
	}
	for(i=8;i<10;i++){
    	//add goal, basically box which is colored green
    	 Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
    	//add goal to grid
    	GridPane.setConstraints(goal, i, 0);
    	grid.getChildren().add(goal);
    	//set the alignment of the grid
        grid.setAlignment(Pos.CENTER);
    }
	
	grid.getChildren().addAll(boxList);
	grid.getChildren().add(circle);
  image = new Image("file:down.png",100,100,false,false);
  ImagePattern imagePattern = new ImagePattern(image);
  circle.setFill(imagePattern);
    
	image = new Image("file:crate.png");
	imagePattern = new ImagePattern(image);
    
	for(Rectangle rectangle : boxList) {
		rectangle.setFill(imagePattern);			
	}
	
	//add polygons (walls/diamonds)
	for (i = 0; i < 8; i++) {
		if (i==2) {
        Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 0);
        	grid.getChildren().add(wall);
		}
		if (i==6) {
         Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
    		
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 0);
        	grid.getChildren().add(wall);
		}
		if (i==8) {
         Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
        	GridPane.setConstraints(wall, i, 1);
        	grid.getChildren().add(wall);
		}
	}	    	
	for (i = 0; i < 6; i++) {
		if (i==2) {
       Rectangle wall = new Rectangle(100, 100, 50, 50);
       image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
    		
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 2);
        	grid.getChildren().add(wall);
		}
	}
	for (i = 0; i < 9; i++) {
		if(i==0)continue;
		if(i==2)continue;
		if(i==4)continue;
       Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, i, 4);
       	grid.getChildren().add(wall);
	}
	for (i = 3; i < 9; i++) {
       Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 5, i);
       	grid.getChildren().add(wall);
	}
	for (i = 0; i < 9; i++) {
		if(i==1)continue;
		if(i==7)continue;
       Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
    		
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, i, 7);
        grid.getChildren().add(wall);
	}
	ArrayList<Integer> playerPos = new ArrayList<Integer>();
    playerPos.add(player.getPlayerCol());
    playerPos.add(player.getPlayerRow());
    State state = new State(playerPos, boxPos);
    stateList.add(state);
    	

    //set grid lines visible, easier to debug, will be turned off later
    ////grid.setGridLinesVisible(true);
    return grid;
}

public GridPane setUpGrid_Map3(){
	//make player and box and also the grid
	for(int i = 0; i < 4; i++) {
		Rectangle rectangle = new Rectangle(100, 100, 50, 50);
		boxList.add(rectangle);
	}

  twoPlayer = false;
	Circle circle = player.getSprite();  
	
	stateList.clear();
	 	this.moveCount = 0;	
	
	//makes a new image pattern that can apply to shapes
	 	Image image = new Image("file:floor.png");
	 	ImagePattern color= new ImagePattern(image);
 
	 	//this fills in the background
	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
           Insets.EMPTY);
	 	Background background = new Background(fill);
  
	
	//make columns and rows for grid (6 X 8)
	for (int i = 0; i < 10; i++) {
		 RowConstraints row = new RowConstraints(50);
		 ColumnConstraints col = new ColumnConstraints(50);
		 grid.getColumnConstraints().add(col);
		 grid.getRowConstraints().add(row);
		 grid.setBackground(background);

	}
	
	//add the box to the grid
	int i = 0;
  ArrayList<Integer> boxPos = new ArrayList<Integer>();
	GridPane.setConstraints(boxList.get(0), 3, 1);
  boxPos.add(3);
	boxPos.add(1);
	GridPane.setConstraints(boxList.get(1), 2, 2);
  boxPos.add(2);
	boxPos.add(2);
	GridPane.setConstraints(boxList.get(2), 3, 4);
  boxPos.add(3);
	boxPos.add(4);
	GridPane.setConstraints(boxList.get(3), 3, 5);
  boxPos.add(3);
	boxPos.add(5);
	
	//set player col and row and add it to the grid
	player.setPlayerCol(8);
	player.setPlayerRow(8);
	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
	
  
  for(i=1;i<5;i++){
    	//add goal, basically box which is colored green
     Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
    	
    	//add goal to grid
    	GridPane.setConstraints(goal, 1, i);
    	grid.getChildren().add(goal);
    	
    	//set the alignment of the grid
      grid.setAlignment(Pos.CENTER);
	}
  
	//add the box and the player as children of the grid
	//they won't show up otherwise
	grid.getChildren().addAll(boxList);
	grid.getChildren().add(circle);
	
  image = new Image("file:down.png",100,100,false,false);
  ImagePattern imagePattern = new ImagePattern(image);
  circle.setFill(imagePattern);
    
	image = new Image("file:crate.png");
	imagePattern = new ImagePattern(image);
    
	for(Rectangle rectangle : boxList) {
		rectangle.setFill(imagePattern);			
	}
	
	//add polygons (walls/diamonds)	
	for (i = 0; i < 10; i++) {
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   		
       	//add them to the grid and make them children
      GridPane.setConstraints(wall, 9, i);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 10; i++) {
    	if(i==1) continue;
        Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
   		
       	//add them to the grid and make them children
       	GridPane.setConstraints(wall, 7, i);
        grid.getChildren().add(wall);
	}
	for (i = 0; i < 7; i++) {
     Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   		
       	//add them to the grid and make them children
       	GridPane.setConstraints(wall, i, 9);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 9; i++) {
     Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   		
       	//add them to the grid and make them children
       	GridPane.setConstraints(wall, i, 0);
    	grid.getChildren().add(wall);
	}
	for (i = 1; i < 9; i++) {
     Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 0, i);
       	grid.getChildren().add(wall);
	}
	for (i = 0; i < 6; i++) {
		if(i==1) continue;
        Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, i, 7);
       	grid.getChildren().add(wall);
	}
	
	for (i = 1; i < 7; i++) {
     Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 5, i);
       	grid.getChildren().add(wall);
	}
	
	for (i = 1; i < 7; i++) {
    	if(i==1) continue;
    	if(i==4) continue;
    	if(i==5) continue;
    	if(i==6) continue;
        Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 3, i);
       	grid.getChildren().add(wall);
	}	
     Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   	//add them to the grid and make them children
     GridPane.setConstraints(wall, 2, 5);
   	grid.getChildren().add(wall);
   	
   	ArrayList<Integer> playerPos = new ArrayList<Integer>();
    playerPos.add(player.getPlayerCol());
    playerPos.add(player.getPlayerRow());
    State state = new State(playerPos, boxPos);
    stateList.add(state);

    //set grid lines visible, easier to debug, will be turned off later
    //grid.setGridLinesVisible(true);
    return grid;
}

public GridPane setUpGrid_Map4(){
	//make player and box and also the grid
	for(int i = 0; i < 4; i++) {
		Rectangle rectangle = new Rectangle(100, 100, 50, 50);
		boxList.add(rectangle);
	}
	twoPlayer = false;
	Circle circle = player.getSprite();  
	
	stateList.clear();
	 	this.moveCount = 0;	
	
	//makes a new image pattern that can apply to shapes
	 	Image image = new Image("file:floor.png");
	 	ImagePattern color= new ImagePattern(image);
 
	 	//this fills in the background
	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
           Insets.EMPTY);
	 	Background background = new Background(fill);
	
	//make columns and rows for grid (10 X 10)
	for (int i = 0; i < 10; i++) {
		 RowConstraints row = new RowConstraints(50);
		 ColumnConstraints col = new ColumnConstraints(50);
		 grid.getColumnConstraints().add(col);
		 grid.getRowConstraints().add(row);
		 grid.setBackground(background);

	}
	
	//add the box to the grid
	int i = 0;
  ArrayList<Integer> boxPos = new ArrayList<Integer>();
	GridPane.setConstraints(boxList.get(0), 4, 2);
  boxPos.add(4);
	boxPos.add(2);
	GridPane.setConstraints(boxList.get(1), 2, 1);
  boxPos.add(2);
	boxPos.add(1);
	GridPane.setConstraints(boxList.get(2), 8, 5);
  boxPos.add(8);
	boxPos.add(5);
	GridPane.setConstraints(boxList.get(3), 3, 7);
	boxPos.add(3);
	boxPos.add(7);
	//set player col and row and add it to the grid
	player.setPlayerCol(2);
	player.setPlayerRow(0);
	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
  
  for(i=0;i<4;i++){
    	//add goal, basically box which is colored green
		Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
    	
    	//add goal to grid
    	GridPane.setConstraints(goal, i, 9);
    	grid.getChildren().add(goal);
    	
    	//set the alignment of the grid
      grid.setAlignment(Pos.CENTER);
	}
	
	//add the box and the player as children of the grid
	//they won't show up otherwise
	grid.getChildren().addAll(boxList);
	grid.getChildren().add(circle);
	
  image = new Image("file:down.png",100,100,false,false);
  ImagePattern imagePattern = new ImagePattern(image);
  circle.setFill(imagePattern);
    
	image = new Image("file:crate.png");
	imagePattern = new ImagePattern(image);
    
	for(Rectangle rectangle : boxList) {
		rectangle.setFill(imagePattern);			
	}
	
	//add polygons (walls/diamonds)	    	
	for (i = 0; i < 10; i++) {
		if(i==2) continue;
		if(i==6) continue;
		if(i==7) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   		
       	//add them to the grid and make them children
       	GridPane.setConstraints(wall, i, 0);
    	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==1) continue;
		if(i==2) continue;
		if(i==9) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 0, i);
       	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==2) continue;
		if(i==4) continue;
		if(i==5) continue;
		if(i==9) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 1, i);
       	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==2) continue;
		if(i==4) continue;
		if(i==1) continue;
		if(i==6) continue;
		if(i==7) continue;
		if(i==9) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 4, i);
       	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==1) continue;
		if(i==2) continue;
		if(i==9) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 5, i);
       	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==1) continue;
		if(i==9) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall, 7, i);
       	grid.getChildren().add(wall);
	}
	for (i = 1; i < 10; i++) {
		if(i==1) continue;
		if(i==2) continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
       	//add them to the grid and make them children
        GridPane.setConstraints(wall,9,i);
       	grid.getChildren().add(wall);
	}
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
   	//add them to the grid and make them children
    GridPane.setConstraints(wall, 2, 6);
   	grid.getChildren().add(wall);
   	
   	ArrayList<Integer> playerPos = new ArrayList<Integer>();
    playerPos.add(player.getPlayerCol());
    playerPos.add(player.getPlayerRow());
    State state = new State(playerPos, boxPos);
    stateList.add(state);
    	
	
    //set grid lines visible, easier to debug, will be turned off later
    //grid.setGridLinesVisible(true);
    return grid;
}


public GridPane setUpGrid_MultiPlayer0(){
	//make player and box and also the grid
	for(int i = 0; i < 4; i++) {
		Rectangle rectangle = new Rectangle(100, 100, 50, 50);
		boxList.add(rectangle);
	}
	twoPlayer = true;
	Circle circle = player.getSprite();         	
	Circle circle2 = player2.getSprite(); 
	
	stateList.clear();
	this.moveCount = 0;
  

	//makes a new image pattern that can apply to shapes
	 	Image image = new Image("file:floor.png");
	 	ImagePattern color= new ImagePattern(image);
 
	 	//this fills in the background
	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
           Insets.EMPTY);
	 	Background background = new Background(fill);
	
	//make columns and rows for grid (10 X 10)
	for (int i = 0; i < 10; i++) {
		 RowConstraints row = new RowConstraints(50);
		 ColumnConstraints col = new ColumnConstraints(50);
		 grid.getColumnConstraints().add(col);
		 grid.getRowConstraints().add(row);
		 grid.setBackground(background);

	}
	
	//add the box to the grid
	int i = 0;	
  ArrayList<Integer> boxPos = new ArrayList<Integer>();
	GridPane.setConstraints(boxList.get(1), 3, 4);
  boxPos.add(3);
	boxPos.add(4);
	GridPane.setConstraints(boxList.get(2), 4, 3);
  boxPos.add(4);
	boxPos.add(3);
	GridPane.setConstraints(boxList.get(3), 4, 5);
  boxPos.add(4);
	boxPos.add(5);
	GridPane.setConstraints(boxList.get(0), 5, 4);
  boxPos.add(5);
	boxPos.add(4);
	
	//set player col and row and add it to the grid
	player.setPlayerCol(7);
	player.setPlayerRow(6);
	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
	player2.setPlayerCol(4);
	player2.setPlayerRow(4);
	GridPane.setConstraints(circle2, player2.getPlayerCol(), player2.getPlayerRow());
  
  		    	//add goal, basically box which is colored green
	for(i=0;i<4;i++){
  		Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
		    	
		    	//add goal to grid
     GridPane.setConstraints(goal, 9, i);
     grid.getChildren().add(goal);
           
           //set the alignment of the grid
     grid.setAlignment(Pos.CENTER);
  }
	
	//add the box and the player as children of the grid
	//they won't show up otherwise
	grid.getChildren().addAll(boxList);
	grid.getChildren().add(circle);
	grid.getChildren().add(circle2);
	
  image = new Image("file:down.png",100,100,false,false);
  ImagePattern imagePattern = new ImagePattern(image);
  circle.setFill(imagePattern);
  image = new Image("file:down2.png",100,100,false,false);
  imagePattern = new ImagePattern(image);
    
  circle2.setFill(imagePattern);
	image = new Image("file:crate.png");
	imagePattern = new ImagePattern(image);
    
	for(Rectangle rectangle : boxList) {
		rectangle.setFill(imagePattern);			
   }
	
	//add polygons (walls/diamonds)
			for (i = 0; i < 10; i++) {
           Rectangle wall = new Rectangle(100, 100, 50, 50);
           image = new Image("file:wall.png");
           imagePattern = new ImagePattern(image);
           wall.setFill(imagePattern);
           wallList.add(wall);
	        	
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, i, 9);
	        	grid.getChildren().add(wall);
	    	}
			for (i = 0; i < 9; i++) {
           Rectangle wall = new Rectangle(100, 100, 50, 50);
           image = new Image("file:wall.png");
           imagePattern = new ImagePattern(image);
           wall.setFill(imagePattern);
           wallList.add(wall);
	        	
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, i, 0);
	        	grid.getChildren().add(wall);
	    	}
	    	for (i = 0; i < 9; i++) {
           Rectangle wall = new Rectangle(100, 100, 50, 50);
           image = new Image("file:wall.png");
           imagePattern = new ImagePattern(image);
           wall.setFill(imagePattern);
           wallList.add(wall);
	        	
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, i, 8);
	        	grid.getChildren().add(wall);
	    	}
	    	for (i = 1; i < 8; i++) {
           Rectangle wall = new Rectangle(100, 100, 50, 50);
           image = new Image("file:wall.png");
           imagePattern = new ImagePattern(image);
           wall.setFill(imagePattern);
           wallList.add(wall);
	        	
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, 0, i);
	        	grid.getChildren().add(wall);
	    	}
	    	for (i = 1; i < 8; i++) {
	    		if(i==7)continue;
              Rectangle wall = new Rectangle(100, 100, 50, 50);
           image = new Image("file:wall.png");
           imagePattern = new ImagePattern(image);
           wall.setFill(imagePattern);
           wallList.add(wall);
	        	
	        	//add them to the grid and make them children
	        	GridPane.setConstraints(wall, 8, i);
	        	grid.getChildren().add(wall);
	    	}
	    	for (i = 2; i < 7; i++) {
	    		if(i == 4)continue;
              Rectangle wall = new Rectangle(100, 100, 50, 50);
              image = new Image("file:wall.png");
              imagePattern = new ImagePattern(image);
              wall.setFill(imagePattern);
              wallList.add(wall);
	        	
	        	//add them to the grid and make them children
              GridPane.setConstraints(wall, 2, i);
              grid.getChildren().add(wall);
	    	}
	    	for (i = 2; i < 7; i++) {
	    		if(i == 4)continue;
              Rectangle wall = new Rectangle(100, 100, 50, 50);
              image = new Image("file:wall.png");
              imagePattern = new ImagePattern(image);
              wall.setFill(imagePattern);
              wallList.add(wall);
	        	
	        	//add them to the grid and make them children
              GridPane.setConstraints(wall, 6, i);
              grid.getChildren().add(wall);
	    	}
	    	for (i = 3; i < 6; i++) {
	    		if(i == 4)continue;
              Rectangle wall = new Rectangle(100, 100, 50, 50);
              image = new Image("file:wall.png");
              imagePattern = new ImagePattern(image);
              wall.setFill(imagePattern);
              wallList.add(wall);
              
              //add them to the grid and make them children
              GridPane.setConstraints(wall, i, 2);
              grid.getChildren().add(wall);
	    	}
	    	for (i = 3; i < 6; i++) {
	    		if(i == 4)continue;
              Rectangle wall = new Rectangle(100, 100, 50, 50);
              image = new Image("file:wall.png");
              imagePattern = new ImagePattern(image);
              wall.setFill(imagePattern);
              wallList.add(wall);
	        	
	        	//add them to the grid and make them children
              GridPane.setConstraints(wall, i, 6);
              grid.getChildren().add(wall);
	    	}
	    	ArrayList<Integer> playerPos = new ArrayList<Integer>();
	        playerPos.add(player.getPlayerCol());
	        playerPos.add(player.getPlayerRow());
	        playerPos.add(player2.getPlayerCol());
	        playerPos.add(player2.getPlayerRow());
	        State state = new State(playerPos, boxPos);
	        stateList.add(state);
  
    //set grid lines visible, easier to debug, will be turned off later
    ////grid.setGridLinesVisible(true);
    return grid;
}

public GridPane setUpGrid_MultiPlayer1(){
	//make player and box and also the grid
	for(int i = 0; i < 4; i++) {
		Rectangle rectangle = new Rectangle(100, 100, 50, 50);
		boxList.add(rectangle);
	}
	twoPlayer = true;
	Circle circle = player.getSprite();        	
	Circle circle2 = player2.getSprite();  
	//makes a new image pattern that can apply to shapes
	 	Image image = new Image("file:floor.png");
	 	ImagePattern color= new ImagePattern(image);
 
	 	stateList.clear();
	 	this.moveCount = 0;
	 	
	 	//this fills in the background
	 	BackgroundFill fill = new BackgroundFill(color, CornerRadii.EMPTY,
           Insets.EMPTY);
	 	Background background = new Background(fill);
	
	//make columns and rows for grid (10 X 10)
	for (int i = 0; i < 10; i++) {
		 RowConstraints row = new RowConstraints(50);
		 ColumnConstraints col = new ColumnConstraints(50);
		 grid.getColumnConstraints().add(col);
		 grid.getRowConstraints().add(row);
		 grid.setBackground(background);

	}
	
	//add the box to the grid
	int i = 0;
    ArrayList<Integer> boxPos = new ArrayList<Integer>();
    GridPane.setConstraints(boxList.get(0), 2, 2);
    boxPos.add(2);
    boxPos.add(2);
    GridPane.setConstraints(boxList.get(1), 7, 2);
    boxPos.add(7);
    boxPos.add(2);
    GridPane.setConstraints(boxList.get(2), 7, 3);
    boxPos.add(7);
    boxPos.add(3);
    GridPane.setConstraints(boxList.get(3), 7, 4);
    boxPos.add(7);
    boxPos.add(4);
    
	//set player col and row and add it to the grid
	player.setPlayerCol(8);
	player.setPlayerRow(2);
	GridPane.setConstraints(circle, player.getPlayerCol(), player.getPlayerRow());
	player2.setPlayerCol(1);
	player2.setPlayerRow(1);
	GridPane.setConstraints(circle2, player2.getPlayerCol(), player2.getPlayerRow());
	
	//add the box and the player as children of the grid
	//they won't show up otherwise
	//add goal, basically box which is colored green
	for(i=1;i<9;i++){
		if(i==1||i==3)continue;
		if(i==5||i==7)continue;
		Rectangle goal = new Rectangle(100, 100, 50, 50);
		image = new Image("file:goal.png");
		ImagePattern imagePattern = new ImagePattern(image);
		goal.setFill(imagePattern);
		goalList.add(goal);
		//add goal to grid
		GridPane.setConstraints(goal, i, 9);
		grid.getChildren().add(goal);
		
	}
	grid.getChildren().add(circle);
	grid.getChildren().add(circle2);
	grid.getChildren().addAll(boxList);
	
    image = new Image("file:down.png",100,100,false,false);
    ImagePattern imagePattern = new ImagePattern(image);
    circle.setFill(imagePattern);
    image = new Image("file:down2.png",100,100,false,false);
    imagePattern = new ImagePattern(image);   
    circle2.setFill(imagePattern);
    
	image = new Image("file:crate.png");
	imagePattern = new ImagePattern(image);
    
	for(Rectangle rectangle : boxList) {
		rectangle.setFill(imagePattern);			
	}
	
	//add polygons (walls/diamonds)
	for (i = 0; i < 10; i++) {
 		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	
    	//add them to the grid and make them children
    	GridPane.setConstraints(wall, i, 0);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 10; i++) {
		if(i==2||i==4)continue;
		if(i==5)continue;
		if(i==6||i==8)continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	
    	//add them to the grid and make them children
    	GridPane.setConstraints(wall, i, 9);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 10; i++) {
		if(i==2)continue;
		if(i==3)continue;
		if(i==4)continue;
		if(i==6)continue;
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	
    	//add them to the grid and make them children
    	GridPane.setConstraints(wall, i, 5);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 5; i++) {
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	
    	//add them to the grid and make them children
    	GridPane.setConstraints(wall, 4, i);
    	grid.getChildren().add(wall);
	}
	for (i = 0; i < 10; i++) {
		if(i==3||i==6){
        Rectangle wall = new Rectangle(100, 100, 50, 50);
        image = new Image("file:wall.png");
        imagePattern = new ImagePattern(image);
        wall.setFill(imagePattern);
        wallList.add(wall);
        	
        	//add them to the grid and make them children
        	GridPane.setConstraints(wall, i, 7);
        	grid.getChildren().add(wall);
    	}
	}
	for (i = 1; i < 9; i++) {
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	GridPane.setConstraints(wall, 9, i);
    	grid.getChildren().add(wall);
	}
	for (i = 1; i < 9; i++) {
  		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	GridPane.setConstraints(wall, 0, i);
    	grid.getChildren().add(wall);
	}
		Rectangle wall = new Rectangle(100, 100, 50, 50);
		image = new Image("file:wall.png");
		imagePattern = new ImagePattern(image);
		wall.setFill(imagePattern);
    	wallList.add(wall);
    	GridPane.setConstraints(wall, 2, 4);
    	grid.getChildren().add(wall);
	//add them to the grid and make them children
	
	//set the alignment of the grid
    grid.setAlignment(Pos.CENTER);
    
    //set grid lines visible, easier to debug, will be turned off later
    ////grid.setGridLinesVisible(true);
    ArrayList<Integer> playerPos = new ArrayList<Integer>();
    playerPos.add(player.getPlayerCol());
    playerPos.add(player.getPlayerRow());
    playerPos.add(player2.getPlayerCol());
    playerPos.add(player2.getPlayerRow());
    State state = new State(playerPos, boxPos);
    stateList.add(state);
    return grid;
	}
}

