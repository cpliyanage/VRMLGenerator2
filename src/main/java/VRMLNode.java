import java.util.ArrayList;
import java.util.List;

public class VRMLNode{
	String id;//object Id
    String name;
	String location; // location relative to the parent
	String colour;
	String texture;
	String size;
	String type;
	String cordinates;
	String orientation;
	long count=1;
	
	ArrayList<VRMLNode> children = new ArrayList<VRMLNode>();
    VRMLNode parent = null;
    VRMLNode parent2 = null;
    
    public VRMLNode(String id) {
        this.id = id;
    }

    public void addChild(VRMLNode child) {
        //child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(String objId) {
    	for(int i=0;i<children.size();i++){
    		if(children.get(i).id.equals(objId)){
    			children.remove(i);
    		}
    	}
    	
    }
/*    public void addChild(String name) {
        VRMLNode newChild = new VRMLNode(name);
        newChild.setParent(this);
        children.add(newChild);
    }

    public void addChildren(List<VRMLNode> children) {
        for(VRMLNode t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }
*/
    public List<VRMLNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(VRMLNode parent) {
        this.parent = parent;
    }

    public VRMLNode getParent() {
        return parent;
    }

	public String getCordinates() {
		return cordinates;
	}

	public void setCordinates(String cordinates) {
		this.cordinates = cordinates;
	}
    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeaf() {
        if(this.children.size() == 0) 
            return true;
        else 
            return false;
    }
    
}