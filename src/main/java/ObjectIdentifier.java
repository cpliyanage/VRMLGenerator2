import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class ObjectIdentifier {
	
	CodeGenerator codeGenerator= new CodeGenerator();

	static PrintWriter writer;	

	String colour;
	String size;
	String parentCordinates;
	String relativeLocation;
	String currentCordinates;
	
	public void defineObject(Tree tree) throws IOException{
		writer= new PrintWriter("generated.wrl", "UTF-8");
		writer.println("#VRML V2.0 utf8");
		
	    // identifying root node
	    int childCount = tree.getChildCount();
	    if (!(childCount == 0)) {
	        for (int i = 0; i < childCount; i++) {
	            VRMLNode currentNode = tree.nodes.get(i);
	            if(currentNode.isRoot()){
	            	defineObjectRecursive(currentNode);
	            }else{
	            	continue;
	            }
	        }
	    }
		
		writer.close();
		System.out.println("Successfully created file!");
	}
	
	public void defineObjectRecursive(VRMLNode node) throws IOException{
	    
	    System.out.println("Printing Object Details");
	    System.out.println("name: "+node.name);
	    System.out.println("colour: "+node.colour);
	    System.out.println("relative location: "+node.location);
	    if((!(node.parent==null))){
	    System.out.println("parent: "+node.parent.name);
	    }
	    if((!(node.parent==null))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates==""))){
	    System.out.println("parent cordinates: "+node.parent.cordinates);
	    }
	    
		//setting colour
		if((!(node.colour==null))){
			colour=node.colour;
		}else{
			colour="black";
		}
		
		//setting size
		if((!(node.size==null))){
			size=node.size;
		}else{
			size = "regular";
		}
		
		//setting parent cordinates
		if((!(node.parent==null))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates==""))){
			parentCordinates=node.parent.cordinates;
		}else{
			parentCordinates = "";
		}
		
		//setting relative location
		if((!(node.location==null))){
			relativeLocation=node.location;
		}else{
			relativeLocation = "";
		}
		
		//Basic shapes
		if ((!(node.name==null)) && (node.name).equalsIgnoreCase("box")){
			System.out.println("Object box present");
			currentCordinates=codeGenerator.drawBox(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("sphere")){
			System.out.println("Object sphere present");
			currentCordinates=codeGenerator.drawSphere(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cone")){
			System.out.println("Object cone present");
			currentCordinates=codeGenerator.drawCone(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cylinder")){
			System.out.println("Object cylinder present");
			currentCordinates=codeGenerator.drawCylinder(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}
		
		//Custom objects
		else if(!node.name.equals(null)&& (node.name).equalsIgnoreCase("table")){
			if((!(node.type==null)) && !node.type.equals("round")){
				System.out.println("Round table present");
				currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation);
				node.setCordinates(currentCordinates);
			}else if((!(node.type==null)) && !node.type.equals("square")){
				System.out.println("Square table present");
				currentCordinates=codeGenerator.drawSquareTable(colour,size,parentCordinates,relativeLocation);
				node.setCordinates(currentCordinates);
			}else{
				System.out.println("Object table present");
				currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation);
				node.setCordinates(currentCordinates);
			}
		} 
		
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("chair")){
			System.out.println("Object chair present");
			currentCordinates=codeGenerator.drawChair(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}
		
		if(!(node.isLeaf())&& node.children.size()>0){
			int childrenCount=node.children.size();
			for(int j=0;j<childrenCount;j++){
			defineObjectRecursive(node.children.get(j));
			}
		}else{
			return;
		} 
	
	}
	
}

