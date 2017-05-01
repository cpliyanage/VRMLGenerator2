import java.util.ArrayList;

public class Tree {
    public ArrayList<VRMLNode> nodes = new ArrayList<VRMLNode>();
    
    public int getChildCount() {
    	return nodes.size();
    }
    
}