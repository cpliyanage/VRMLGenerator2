import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class ObjectIdentifier {
	
	CodeGenerator codeGenerator= new CodeGenerator();

	static PrintWriter writer;	
	String currentCordinates="0 0 0";//cordinates of the last object drawn
	String currentShape="";
	String rightmostCordinates="";
	String leftmostCordinates="";
	
	String roomMiddle="0.0 0.0 0.0 ";
	String roomLeftCorner="5.0 0.0 0.0";
	String roomRightCorner="-5.0 0.0 0.0";
	
	String[] relativeLocations={"left","right","above","below","front","behind","top", "under","on", "next"};
	String[] roomLocations={"middle", "leftCorner", "rightCorner"};
	
	public void defineObject(Tree tree) throws IOException{
		writer= new PrintWriter("generated.wrl");
		writer.println("#VRML V2.0 utf8");
		
		//Drawing the walls
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
				
		writer.close();
		System.out.println("Successfully created file!");
	}
	
	public void defineObjectRecursive(VRMLNode node) throws IOException{
		System.out.println("");
	    System.out.println("Printing Object Details in Object Identifier");
	    System.out.println("ID: "+node.id);
	    System.out.println("name: "+node.name);
	    System.out.println("colour: "+node.colour);
	    System.out.println("texture: "+node.texture);
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
	    
		String colour ="";
		String texture="";
		String size ="regular";
		String orientation ="front";
		String parentShape="";
		String parentCordinates="";
		String relativeLocation="";
	    
		//setting colour and texture
		if(!(node.colour==null)){
			colour=node.colour;
		}else{//Setting default colours
			if(node.name.equals("table")||node.name.equals("chair")||node.name.equals("sofa")){
				colour="brown";
			}else{
				colour="blue";
			}
			
		}
		
		if(!(node.texture==null)){
			texture=node.texture;
		}
				
		//setting size
		if((!(node.size==null))){
			size=node.size;
		}
		
		//setting orientation
		if((!(node.orientation==null))){
			orientation=node.orientation;
		}
		
		//If the parent is not defined, the parent is set as the room node by default
		
		//Setting location relative to room (for first object) 
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(node.parent.name.equals("room"))&&(node.id.equals("object1"))){
			if((!(node.location==null))&&(!(node.location.equals("")))){
				if(Arrays.asList(roomLocations).contains(node.location)){
					relativeLocation=node.location; //location can be middle, right corner, left corner
				}else if(node.location.equals("left")){
					relativeLocation="leftCorner";
				}else if(node.location.equals("left")){
					relativeLocation="rightCorner";
				}else{
					relativeLocation="middle";
				}				
			}else{
				relativeLocation="middle";
			} 
			
		}
		
		//Setting location relative to room (except for first object)
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(node.parent.name.equals("room"))&&!(node.id.equals("object1"))){
			if((!(node.location==null))&&(!(node.location.equals("")))){
				if(Arrays.asList(roomLocations).contains(node.location)){
					relativeLocation=node.location; //location can be middle, right corner, left corner
				}else if(node.location.equals("left")){
					relativeLocation="leftCorner";
				}else if(node.location.equals("left")){
					relativeLocation="rightCorner";
				}else{
					relativeLocation="middle";
				}				
			}else{
				parentCordinates=currentCordinates;
				parentShape=currentShape;
				if(node.name.equals("lamp")){
					relativeLocation="on";
				}else{
					relativeLocation="right";
				}
			} 
			
		}
		
		//setting parent cordinates (No cordinates are set for room node)
		if((!(node.parent==null))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates==""))){
			parentCordinates=node.parent.cordinates;
		}
		
		//setting parent shape (except for room node)
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(!(node.parent.name.equals("room")))){
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
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawBox(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="box";
				}else{
					currentCordinates=codeGenerator.drawBox(colour,texture,size,currentCordinates,"right","box",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="box";
				}
			}
			
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("sphere")){
			System.out.println("Object sphere present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawSphere(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawSphere(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
				}else{
					currentCordinates=codeGenerator.drawSphere(colour,texture,size,currentCordinates,"right","sphere",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
				}
			}
			
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("ball")){
			System.out.println("Object ball present");
			for(int k=1;k<=node.count;k++){
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawSphere(colour,texture,"small",parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
				}else{
					currentCordinates=codeGenerator.drawSphere(colour,texture,"small",currentCordinates,"right","sphere",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
				}
			}
			
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cone")){
			System.out.println("Object cone present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawCone(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawCone(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="cone";
				}else{
					currentCordinates=codeGenerator.drawCone(colour,texture,size,currentCordinates,"right","cone",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="cone";
				}
			}
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cylinder")){
			System.out.println("Object cylinder present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawCylinder(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawCylinder(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="cylinder";
				}else{
					currentCordinates=codeGenerator.drawCylinder(colour,texture,size,currentCordinates,"right","cylinder",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="cylinder";
				}
			}
		}
		
		//Custom objects
		
		//Draw Table
		else if(!(node.name==null)&& (node.name).equalsIgnoreCase("table")){
			if((!(node.type==null)) && (node.type.equals("round")||node.type.equals("coffee"))){
				System.out.println("Round table present");
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						currentCordinates=codeGenerator.drawRoundTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}else{
						currentCordinates=codeGenerator.drawRoundTable(colour,texture,size,currentCordinates,"right","table",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}
				}
			}else if((!(node.type==null)) && node.type.equals("square")){
				System.out.println("Square table present");
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawSquareTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}else{
						currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}
				}
			}else{
				System.out.println("Object table present");
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}else{
						currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
					}
				}
			}
		} 
		
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("chair")){
			System.out.println("Object chair present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawChair(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawChair(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="chair";
				}else{
					currentCordinates=codeGenerator.drawChair(colour,texture,size,currentCordinates,"right","chair",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="chair";
				}
			}
		}
		
		//Draw Sofa
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("sofa")){
			System.out.println("Object sofa present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawSofa(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawSofa(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sofa";
				}else{
					currentCordinates=codeGenerator.drawSofa(colour,texture,size,currentCordinates,"right","sofa",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sofa";
				}
			}
		}
		
		//Draw Bookshelf
		/*else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bookshelf")){
			System.out.println("Object bookshelf present");
			currentCordinates=codeGenerator.drawBookshelf(colour,size,parentCordinates,relativeLocation);
			node.setCordinates(currentCordinates);
		}*/
		
		//Draw Lamp
		else if(!(node.name==null)&& (node.name).equalsIgnoreCase("lamp")){
			if((!(node.type==null)) && node.type.equals("ceiling")){
				System.out.println("Ceiling lamp present");
				codeGenerator.drawCeilingLamp();
			} else if((!(node.type==null)) && node.type.equals("table")){
				System.out.println("Table lamp present");
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
					}else{
						currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"right","lamp",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
					}
				}
			}else{
				System.out.println("Table lamp present");
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
					}else{
						currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"right","lamp",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
					}
				}
			}
		}
		
		//Draw Bed
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bed")){
			System.out.println("Object bed present");
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawBed(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					currentCordinates=codeGenerator.drawBed(colour,texture,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="bed";
				}else{
					currentCordinates=codeGenerator.drawBed(colour,texture,size,currentCordinates,"right","bed",orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="bed";
				}
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

