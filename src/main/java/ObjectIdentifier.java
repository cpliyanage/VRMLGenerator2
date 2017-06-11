import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class ObjectIdentifier {
	
	CodeGenerator codeGenerator= new CodeGenerator();

	static PrintWriter writer;	
	static String currentCordinates;
	static String rightmostCordinates;
	
	public void defineObject(Tree tree) throws IOException{
		writer= new PrintWriter("generated.wrl");
		writer.println("#VRML V2.0 utf8");
	/*	writer.println("Transform{ "+
			"translation 0 0 -20.0 "+
			"children[ "+
			"Shape { "+
				"appearance Appearance { "+
					"material Material { } "+
				"} "+
				"geometry Box { size 50.0 20.0 0.5 } "+
			"} ] } "+		
			"Transform{ "+
			"translation -25.0 0 -10.0 "+
			"children[ "+
			"Shape { "+
				"appearance Appearance { "+
				"} "+
				"geometry Box { size 0.5 20.0 20.0 } "+
			"}]} "+			
			"Transform{ "+
			"translation 25.0 0 -10.0 "+
			"children[ "+
			"Shape { "+
				"appearance Appearance { "+
				"} "+
				"geometry Box {size 0.5 20.0 20.0 } "+
			"}]} "); */
		
	    // identifying root node
	    int childCount = tree.getChildCount();
	    if (!(childCount == 0)) {
	        for (int i = 0; i < childCount; i++) {
	            VRMLNode currentNode = tree.nodes.get(i);
	            if(currentNode.isRoot()){
	            	defineObjectRecursive(currentNode);
	            }
	        }
	    } 
		
	/*    // identifying root node (room is the root node)
	    int childCount = tree.getChildCount();
	    if (!(childCount == 0)) {
	        for (int i = 0; i < childCount; i++) {
	            VRMLNode currentNode = tree.nodes.get(i);
	            if(currentNode.name.equals("room")){
	            	defineObjectRecursive(currentNode);
	            }
	        }
	    } */
		
		writer.close();
		System.out.println("Successfully created file!");
	}
	
	public void defineObjectRecursive(VRMLNode node) throws IOException{
	    
	    System.out.println("Printing Object Details");
	    System.out.println("ID: "+node.id);
	    System.out.println("name: "+node.name);
	    System.out.println("colour: "+node.colour);
	    System.out.println("type: "+node.type);
	    System.out.println("size: "+node.size);
	    System.out.println("orientation: "+node.orientation);
	    System.out.println("count: "+node.count);
	    System.out.println("relative location: "+node.location);	    
	    
	    if((!(node.parent==null))){
	    System.out.println("parent Id: "+node.parent.id);
	    System.out.println("parent Name: "+node.parent.name);
	    }
	    if((!(node.parent==null))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates==""))){
	    System.out.println("parent cordinates: "+node.parent.cordinates);
	    }
	    
	    System.out.println("");
	    
		String colour ="black";
		String size ="regular";
		String orientation ="front";
		String parentShape="";
		String parentCordinates="";
		String relativeLocation="";
	    
		//setting colour
		if((!(node.colour==null))){
			colour=node.colour;
		}
		
		//setting size
		if((!(node.size==null))){
			size=node.size;
		}
		
		//setting orientation
		if((!(node.orientation==null))){
			orientation=node.orientation;
		}
		
		//setting parent cordinates
		if((!(node.parent==null))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates==""))){
			parentCordinates=node.parent.cordinates;
		}
		
		//setting parent shape
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))){
			parentShape=node.parent.name;
		}
		
		//setting relative location
		if((!(node.location==null))){
			relativeLocation=node.location;
		}
		
		//Basic shapes
		if ((!(node.name==null)) && (node.name).equalsIgnoreCase("box")){
			System.out.println("Object box present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawBox(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
			
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("sphere")){
			System.out.println("Object sphere present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawSphere(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
			
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cone")){
			System.out.println("Object cone present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawCone(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cylinder")){
			System.out.println("Object cylinder present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawCylinder(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
		}
		
		//Custom objects
		
		//Draw Table
		else if(!node.name.equals(null)&& (node.name).equalsIgnoreCase("table")){
			if((!(node.type==null)) && node.type.equals("round")){
				System.out.println("Round table present");
				for(int k=1;k<=node.count;k++){
					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
				}
			}else if((!(node.type==null)) && node.type.equals("square")){
				System.out.println("Square table present");
				for(int k=1;k<=node.count;k++){
					currentCordinates=codeGenerator.drawSquareTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
				}
			}else{
				System.out.println("Object table present");
				for(int k=1;k<=node.count;k++){
					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
				}
			}
		} 
		
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("chair")){
			System.out.println("Object chair present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawChair(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
		}
		
		//Draw Sofa
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("sofa")){
			System.out.println("Object sofa present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawSofa(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
		}
		
		//Draw Bookshelf
		/*else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bookshelf")){
			System.out.println("Object bookshelf present");
			currentCordinates=codeGenerator.drawBookshelf(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}*/
		
		//Draw Lamp
		else if(!node.name.equals(null)&& (node.name).equalsIgnoreCase("lamp")){
			if((!(node.type==null)) && node.type.equals("ceiling")){
				System.out.println("Ceiling lamp present");
				codeGenerator.drawCeilingLamp();
			} else if((!(node.type==null)) && node.type.equals("table")){
				System.out.println("Table lamp present");
				for(int k=1;k<=node.count;k++){
					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
				}
			}else{
				System.out.println("Table lamp present");
				for(int k=1;k<=node.count;k++){
					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
				}
			}
		}
		
		//Draw Bed
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bed")){
			System.out.println("Object bed present");
			for(int k=1;k<=node.count;k++){
				currentCordinates=codeGenerator.drawBed(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);
			}
		}
		
		if(node.children.size()>0){
			int childrenCount=node.children.size();
			for(int j=0;j<childrenCount;j++){
			defineObjectRecursive(node.children.get(j));
			}
		}
	}
}

