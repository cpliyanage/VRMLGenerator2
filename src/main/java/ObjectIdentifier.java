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
	
	public void defineObject(HashMap<String, VRMLNode> objectMap) throws IOException{
		writer= new PrintWriter("generated.wrl", "UTF-8");
		writer.println("#VRML V2.0 utf8");
		
		for (Entry<String, VRMLNode> mapEntry : objectMap.entrySet()) {
		    String key = mapEntry.getKey();
		    VRMLNode obj = mapEntry.getValue();
		    
		    System.out.println("Printing Object Details");
		    System.out.println("name: "+obj.name);
		    System.out.println("colour: "+obj.colour);
		    System.out.println("relative location: "+obj.location);
		    if((!(obj.parent==null))){
		    System.out.println("parent: "+obj.parent.name);
		    }
		    if((!(obj.parent==null))&&(!(obj.parent.cordinates==null))&&(!(obj.parent.cordinates==""))){
		    System.out.println("parent cordinates: "+obj.parent.cordinates);
		    }
		    
			//setting colour
			if((!(obj.colour==null))){
				colour=obj.colour;
			}else{
				colour="black";
			}
			
			//setting size
			if((!(obj.size==null))){
				size=obj.size;
			}else{
				size = "regular";
			}
			
			//setting parent cordinates
			if((!(obj.parent==null))&&(!(obj.parent.cordinates==null))&&(!(obj.parent.cordinates==""))){
				parentCordinates=obj.parent.cordinates;
			}else{
				parentCordinates = "";
			}
			
			//setting relative location
			if((!(obj.location==null))){
				relativeLocation=obj.location;
			}else{
				relativeLocation = "";
			}
			
			//Basic shapes
			if ((!(obj.name==null)) && (obj.name).equalsIgnoreCase("box")){
				System.out.println("Object box present");
				currentCordinates=codeGenerator.drawBox(colour,size,parentCordinates,relativeLocation);
				obj.setCordinates(currentCordinates);
			}
			else if((!(obj.name==null))&&(obj.name).equalsIgnoreCase("sphere")){
				System.out.println("Object sphere present");
				currentCordinates=codeGenerator.drawSphere(colour,size,parentCordinates,relativeLocation);
				obj.setCordinates(currentCordinates);
			}
			else if((!(obj.name==null)) && (obj.name).equalsIgnoreCase("cone")){
				System.out.println("Object cone present");
				currentCordinates=codeGenerator.drawCone(colour,size,parentCordinates,relativeLocation);
				obj.setCordinates(currentCordinates);
			}
			else if((!(obj.name==null)) && (obj.name).equalsIgnoreCase("cylinder")){
				System.out.println("Object cylinder present");
				currentCordinates=codeGenerator.drawCylinder(colour,size,parentCordinates,relativeLocation);
				obj.setCordinates(currentCordinates);
			}
			
			//Custom objects
			else if(!obj.name.equals(null)&& (obj.name).equalsIgnoreCase("table")){
				if((!(obj.type==null)) && !obj.type.equals("round")){
					System.out.println("Round table present");
					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation);
					obj.setCordinates(currentCordinates);
				}else if((!(obj.type==null)) && !obj.type.equals("square")){
					System.out.println("Square table present");
					currentCordinates=codeGenerator.drawSquareTable(colour,size,parentCordinates,relativeLocation);
					obj.setCordinates(currentCordinates);
				}else{
					System.out.println("Object table present");
					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation);
					obj.setCordinates(currentCordinates);
				}
			} 
			
			else if((!(obj.name==null)) && (obj.name).equalsIgnoreCase("chair")){
				System.out.println("Object chair present");
				currentCordinates=codeGenerator.drawChair(colour,size,parentCordinates,relativeLocation);
				obj.setCordinates(currentCordinates);
			} 
		} 
		
		writer.close();
		System.out.println("Successfully created file!");
	}
	
	public void defineObjectRecursive(){
		
	}
	
	/*public void traverseTree(Tree tree) {

	    // print, increment counter, whatever
	    System.out.println(tree.toString());

	    // traverse children
	    int childCount = tree.getChildCount();
	    if (childCount == 0) {
	        // leaf node, we're done
	    } else {
	        for (int i = 0; i < childCount; i++) {
	            Tree child = tree.getChild(i);
	            traverseTree(child);
	        }
	    }
	}*/
}

