import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BFS {
	private boolean[] visited;    
    private ArrayList<MapNode> grid;   
    private MapNode s; 
    private ArrayList<MapNode> edge; 
	
    public BFS(int col, int row) {
        this.grid = new ArrayList<MapNode>(); 
        makeGrid();
        this.s = getNodeFromCo(col, row);
        this.edge = new ArrayList<MapNode>(100);
        setEdges();
        this.visited = new boolean[100];
        checkNode(s);
        bfs(s);
    }
    
    private void setEdges() {
    	for (int i = 0; i < 100; i++) {
    		MapNode node = new MapNode(-1,-1,-1);
    		this.edge.add(node);
    	}
    }
    
    public MapNode getNodeFromCo(int col, int row) {
    	for (MapNode node : grid) {
    		if (node.getCol() == col && node.getRow() == row) {
    			return node;
    		}
    	}
    	return null;
    }
    
    private void makeGrid() {
    	int count = 0;
    	for (int i = 0; i < 10; i++) {
    		for (int j = 0; j < 10; j++) {
    			MapNode node = new MapNode(count++,i,j);
    			grid.add(node);
    		}
    	}
    }
    
    private void bfs(MapNode s) {
    	Queue<MapNode> q = new LinkedList<MapNode>();
    	q.add(s);
    	visited[s.getID()] = true;
        while (!q.isEmpty()) {
        	MapNode v = q.poll();
	    	for (MapNode w : getAdjacentNodes(v)) {
	        	if (!checkNode(w)) continue;
	            if (!visited[w.getID()]) {
	                edge.set(w.getID(),v);
	                visited[w.getID()] = true;
	                q.add(w);
	            }
	        }
        }
    }
    
    private ArrayList<MapNode> getAdjacentNodes(MapNode s) {
    	ArrayList<MapNode> adj = new ArrayList<MapNode>();
    	for (MapNode node : grid) {
    		if (node.getCol() == s.getCol() && node.getRow() == s.getRow()-1) {
    			adj.add(node);
    		}
    		if (node.getCol() == s.getCol()+1 && node.getRow() == s.getRow()) {
    			adj.add(node);
    		}
    		if (node.getCol() == s.getCol() && node.getRow() == s.getRow()+1) {
    			adj.add(node);
    		}
    		if (node.getCol() == s.getCol()-1 && node.getRow() == s.getRow()) {
    			adj.add(node);
    		}
    	}
    	return adj;
    }
    
    public boolean pathExists(MapNode s) {
        checkNode(s);
        return visited[s.getID()];
    }
    
    public Iterable<MapNode> getPath(MapNode v) {
        checkNode(v);
        if (!pathExists(v)) return null;
        Stack<MapNode> path = new Stack<MapNode>();
        int x;
        for (x = v.getID(); x != -1; x = edge.get(x).getID())
            path.push(getNodeFromID(x));
        //path.push(s);
        return path;
    }
    
	public MapNode getNodeFromID(int id) {
		for (MapNode node : grid) {
			if (node.getID() == id) {
				return node;
			}
		}
		return null;
	}
    
    private boolean checkNode(MapNode w) {
    	if (w.getCol() < 10 && w.getCol() >=0 && w.getRow() < 10 && w.getRow() >=0) return true;
    	else return false;
    }
    
}
