import java.util.ArrayList;

public class State {
	
	private ArrayList<Integer> playerPos;
	private ArrayList<Integer> boxPos;
	
	public State(ArrayList<Integer> playerPos, ArrayList<Integer> boxPos) {
		this.playerPos = playerPos;
		this.boxPos = boxPos;
	}
	
	public ArrayList<Integer> getPlayerPos() {
		return this.playerPos;
	}
	
	public ArrayList<Integer> getBoxPos(){
		return this.boxPos;
	}
	
}
