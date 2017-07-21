import javafx.scene.shape.Circle;

public class Player {
	
	private int col;
	private int row;
	private Circle sprite;
	
	public Player() {
		this.col = -1;
		this.row = -1;
		this.sprite = new Circle(100, 50, 25); 
	}
	
	public Circle getSprite() {
		return sprite;
	}
	
	public void setPlayerRow(int row) {
		this.row = row;
	}
	
	public void setPlayerCol(int col) {
		this.col = col;
	}
	
	public int getPlayerCol() {
		return col;
	}
	public int getPlayerRow() {
		return row;
	}
	
	
	
}
