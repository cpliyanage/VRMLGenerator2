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
	String roomLeftCorner="-5.0 0.0 0.0";
	String roomRightCorner="5.0 0.0 0.0";
	
	String[] relativeLocations={"left","right","above","below","front","behind","top", "under","on", "next"};
	String[] roomLocations={"middle", "leftCorner", "rightCorner"};
	
	boolean isFirstObject=true;
	boolean objectInMiddle=false;
	boolean objectInLeft=false;
	boolean objectInRight=false;
	
	public void defineObject(Tree tree) throws IOException{
		isFirstObject=true;
		objectInMiddle=false;
		objectInLeft=false;
		objectInRight=false;
		int childCount = tree.getChildCount();
		String roomColour = "1 0.87 0.67";
		AttributeDefinitions roomAttributes = new AttributeDefinitions();
		roomAttributes.initializeRoomColours();
		
	    if (!(childCount == 0)) {
	        for (int i = 0; i < childCount; i++) {
	            VRMLNode currentNode = tree.nodes.get(i);
	            if(currentNode.isRoot()){
	            	if(!(currentNode.colour==null)&& !(currentNode.colour.equals(""))){
	            		roomColour=roomAttributes.roomColourTable.get(currentNode.colour);
	            	}
	            }
	        }
	    } 
	    
		writer= new PrintWriter("generated.wrl");
		writer.println("#VRML V2.0 utf8");
		
		//Setting the background
		writer.println(" Viewpoint { orientation 0 0 0 0 position 0 1 4 description \"vista\" } ");
		writer.println("Transform { children [ DEF pontodeluz PointLight { color 1 0.918 0.765 on TRUE location 3.17 3.55 5.66 intensity 1 } ] } ");  
		writer.println("Background { skyColor [ "+roomColour+" ] skyAngle [] groundColor[ "+roomColour+" ] groundAngle[] }");
		writer.println("Transform { translation 0 -0.2 0 children [ Shape { appearance Appearance { texture ImageTexture{url \"floor2.jpg\" } } geometry Box { size 500 0.1 100 } } ] } ");
		
	    // identifying root node
	    
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
		String absoluteCordinates="";
	    
		//setting colour and texture
		if(!(node.colour==null)&&!(node.colour.equals(""))){
			colour=node.colour;
		}else{//Setting default colours
			if(node.name.equals("table")||node.name.equals("chair")||node.name.equals("sofa")){
				colour="brown";
			}else{
				colour="blue";
			}
			
		}
		
		if(!(node.texture==null)&&!(node.texture.equals(""))){
			texture=node.texture;
		}
				
		//setting size
		if((!(node.size==null))&&(!(node.size.equals("")))){
			size=node.size;
		}
		
		//setting orientation
		if((!(node.orientation==null))&&(!(node.orientation.equals("")))){
			orientation=node.orientation;
		}
		
		//If the parent is not defined, the parent is set as the room node by default in tagger and parser
		
		//Setting location relative to room (for first object) 		
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(node.parent.name.equals("room"))&&(isFirstObject)){	
			if((!(node.location==null))&&(!(node.location.equals("")))){//if location is defined
				if(Arrays.asList(roomLocations).contains(node.location)){
					relativeLocation=node.location; //location can be middle, right corner, left corner
				}else if(node.location.equals("left")){
					relativeLocation="leftCorner";
				}else if(node.location.equals("right")){
					relativeLocation="rightCorner";
				}else{
					relativeLocation="middle"; // If location is not mentioned above (ex: location is above, behind,front, under,below)
				}				
			}else{ //if location is not defined 
				relativeLocation="middle";
			} 
			
		}
		
		//Setting cordinates relative to room (for first object)
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(node.parent.name.equals("room"))&&(isFirstObject)){	
			if(relativeLocation.equals("middle")){
				
			}else if(relativeLocation.equals("middle")){
				absoluteCordinates=roomMiddle;
				objectInMiddle=true;
			}else if(relativeLocation.equals("leftCorner")){
				absoluteCordinates=roomLeftCorner;
				objectInLeft=true;
			}else if(relativeLocation.equals("rightCorner")){
				absoluteCordinates=roomRightCorner;
				objectInRight=true;
			}else{
				absoluteCordinates=roomMiddle;
				objectInMiddle=true;
			}
		}
		
		
		//Setting location relative to room (except for first object)
		if((!(node.parent==null))&&(!(node.parent.name==null))&&(!(node.parent.name==""))&&(node.parent.name.equals("room"))&&(!isFirstObject)){
			if((!(node.location==null))&&(!(node.location.equals("")))){ //If only the location is defined
				if(Arrays.asList(roomLocations).contains(node.location)){
					relativeLocation=node.location; //location can be middle, right corner, left corner
					if(node.location.equals("middle")&&!objectInMiddle){
						absoluteCordinates=roomMiddle;
						objectInMiddle=true;
					}
					else if(node.location.equals("leftCorner")&&!objectInLeft){
						absoluteCordinates=roomLeftCorner;
						objectInLeft=true;
					}
					else if(node.location.equals("rightCorner")&&!objectInRight){
						absoluteCordinates=roomRightCorner;
						objectInRight=true;
					}else{
						parentCordinates=currentCordinates;
						parentShape=currentShape;
						if(node.name.equals("lamp")){
							relativeLocation="on";
						}else{
							relativeLocation="right";
						}
					}
				}else{
					parentCordinates=currentCordinates;
					parentShape=currentShape;
					relativeLocation=node.location;
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
		
		//setting parent cordinates when parent is not room (No cordinates are set for room node since parent is null)
		if((!(node.parent==null))&&(!(node.parent.equals("")))&&(!(node.parent.cordinates==null))&&(!(node.parent.cordinates.equals("")))&&!(node.parent.name.equals("room"))){
			parentCordinates=node.parent.cordinates;
		}
		
		
		//setting parent shape (except for room node)
		if((!(node.parent==null))&&(!(node.parent.equals("")))&&(!(node.parent.name==null))&&(!(node.parent.name.equals("")))&&(!(node.parent.name.equals("room")))){
			parentShape=node.parent.name;
		}
		//setting relative location
		if((!(node.parent==null))&&(!(node.parent.equals("")))&&(!(node.location==null))&&(!(node.location.equals("")))&&(!(node.parent.name.equals("room")))){
			relativeLocation=node.location;
		}
			
		
		//Basic shapes
		if ((!(node.name==null)) && (node.name).equalsIgnoreCase("box")){
			System.out.println("Object box present");
			String drawnCordinates="0 0 0 ";
			for(int k=1;k<=node.count;k++){
				if(k==1){//FirstObject					
					drawnCordinates=codeGenerator.drawBox(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="box";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawBox(colour,texture,size,drawnCordinates,"right","box",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawBox(colour,texture,size,currentCordinates,"left","box",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="box";
					}else{
						currentCordinates=codeGenerator.drawBox(colour,texture,size,currentCordinates,"right","box",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="box";
					}
					isFirstObject=false;
				}
			}
			
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("sphere")){
			System.out.println("Object sphere present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawSphere(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawSphere(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);					
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="sphere";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
					/*currentCordinates=codeGenerator.drawSphere(colour,texture,size,currentCordinates,"right","sphere",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
					isFirstObject=false;*/

					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawSphere(colour,texture,size,drawnCordinates,"right","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawSphere(colour,texture,size,currentCordinates,"left","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sphere";
					}else{
						currentCordinates=codeGenerator.drawSphere(colour,texture,size,currentCordinates,"right","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sphere";
					}			
					isFirstObject=false;
				}
			}
			
		}
		else if((!(node.name==null))&&(node.name).equalsIgnoreCase("ball")){
			System.out.println("Object ball present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawSphere(colour,texture,"small",parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="sphere";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
					/*currentCordinates=codeGenerator.drawSphere(colour,texture,"small",currentCordinates,"right","sphere",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sphere";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawSphere(colour,texture,"small",drawnCordinates,"right","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawSphere(colour,texture,"small",currentCordinates,"left","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sphere";
					}else{
						currentCordinates=codeGenerator.drawSphere(colour,texture,"small",currentCordinates,"right","sphere",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sphere";
					}			
					isFirstObject=false;
				}
			}
			
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cone")){
			System.out.println("Object cone present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawCone(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawCone(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="cone";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
					/*currentCordinates=codeGenerator.drawCone(colour,texture,size,currentCordinates,"right","cone",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);					
					currentShape="cone";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawCone(colour,texture,size,drawnCordinates,"right","cone",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawCone(colour,texture,size,currentCordinates,"left","cone",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="cone";
					}else{
						currentCordinates=codeGenerator.drawCone(colour,texture,size,currentCordinates,"right","cone",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="cone";
					}			
					isFirstObject=false;
				}
			}
		}
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("cylinder")){
			System.out.println("Object cylinder present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawCylinder(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawCylinder(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="cylinder";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
					/*currentCordinates=codeGenerator.drawCylinder(colour,texture,size,currentCordinates,"right","cylinder",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="cylinder";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawCylinder(colour,texture,size,drawnCordinates,"right","cylinder",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawCylinder(colour,texture,size,currentCordinates,"left","cylinder",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="cylinder";
					}else{
						currentCordinates=codeGenerator.drawCylinder(colour,texture,size,currentCordinates,"right","cylinder",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="cylinder";
					}			
					isFirstObject=false;
				}
			}
		}
		
		//Custom objects
		
		//Draw Table
		else if(!(node.name==null)&& (node.name).equalsIgnoreCase("table")){
			if((!(node.type==null)) && (node.type.equals("round")||node.type.equals("coffee"))){
				System.out.println("Round table present");
				String drawnCordinates="0 0 0";
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						drawnCordinates=codeGenerator.drawRoundTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
						if(!(relativeLocation.equals("on"))){
							currentShape="table";
							currentCordinates=drawnCordinates;
						}
						isFirstObject=false;
					}else{
/*						currentCordinates=codeGenerator.drawRoundTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
						isFirstObject=false;*/
						if(relativeLocation.equals("on")){
							drawnCordinates=codeGenerator.drawRoundTable(colour,texture,size,drawnCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(drawnCordinates);
						}else if(relativeLocation.equals("left")){
							currentCordinates=codeGenerator.drawRoundTable(colour,texture,size,currentCordinates,"left","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}else{
							currentCordinates=codeGenerator.drawRoundTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}			
						isFirstObject=false;
					}
				}
			}else if((!(node.type==null)) && node.type.equals("square")){
				System.out.println("Square table present");
				String drawnCordinates="0 0 0";
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawSquareTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						drawnCordinates=codeGenerator.drawSquareTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
						if(!(relativeLocation.equals("on"))){
							currentShape="table";
							currentCordinates=drawnCordinates;
						}
						isFirstObject=false;
					}else{
/*						currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
						isFirstObject=false;*/
						if(relativeLocation.equals("on")){
							drawnCordinates=codeGenerator.drawSquareTable(colour,texture,size,drawnCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(drawnCordinates);
						}else if(relativeLocation.equals("left")){
							currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"left","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}else{
							currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}			
						isFirstObject=false;
					}
				}
			}else{
				System.out.println("Object table present");
				String drawnCordinates="0 0 0";
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawRoundTable(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						drawnCordinates=codeGenerator.drawSquareTable(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
						if(!(relativeLocation.equals("on"))){
							currentShape="table";
							currentCordinates=drawnCordinates;
						}
						isFirstObject=false;
					}else{
				/*		currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="table";
						isFirstObject=false;*/
						if(relativeLocation.equals("on")){
							drawnCordinates=codeGenerator.drawSquareTable(colour,texture,size,drawnCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(drawnCordinates);
						}else if(relativeLocation.equals("left")){
							currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"left","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}else{
							currentCordinates=codeGenerator.drawSquareTable(colour,texture,size,currentCordinates,"right","table",absoluteCordinates,orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="table";
						}			
						isFirstObject=false;
					}
				}
			}
		} 
		
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("chair")){
			System.out.println("Object chair present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawChair(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawChair(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="chair";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
				/*	currentCordinates=codeGenerator.drawChair(colour,texture,size,currentCordinates,"right","chair",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="chair";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawChair(colour,texture,size,drawnCordinates,"right","chair",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawChair(colour,texture,size,currentCordinates,"left","chair",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="chair";
					}else{
						currentCordinates=codeGenerator.drawChair(colour,texture,size,currentCordinates,"right","chair",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="chair";
					}			
					isFirstObject=false;
				}
			}
		}
		
		//Draw Sofa
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("sofa")){
			System.out.println("Object sofa present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawSofa(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawSofa(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="sofa";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
/*					currentCordinates=codeGenerator.drawSofa(colour,texture,size,currentCordinates,"right","sofa",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="sofa";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawSofa(colour,texture,size,drawnCordinates,"right","sofa",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawSofa(colour,texture,size,currentCordinates,"left","sofa",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sofa";
					}else{
						currentCordinates=codeGenerator.drawSofa(colour,texture,size,currentCordinates,"right","sofa",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="sofa";
					}			
					isFirstObject=false;
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
		else if(!(node.name==null)&& ((node.name).equalsIgnoreCase("lamp"))){
			if((!(node.type==null)) && node.type.equals("ceiling")){
				System.out.println("Ceiling lamp present");
				codeGenerator.drawCeilingLamp(parentCordinates,relativeLocation,parentShape);
			} else if((!(node.type==null)) && node.type.equals("table")){
				System.out.println("Table lamp present");
				String drawnCordinates="0 0 0";
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						drawnCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(drawnCordinates);
						if(!(relativeLocation.equals("on"))){
							currentShape="lamp";
							currentCordinates=drawnCordinates;
						}
						isFirstObject=false;
					}else{
/*						currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"on","lamp",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
						isFirstObject=false;*/
						if(relativeLocation.equals("on")){
							drawnCordinates=codeGenerator.drawTableLamp(colour,size,drawnCordinates,"right","lamp",orientation,k);
							node.setCordinates(drawnCordinates);
						}else if(relativeLocation.equals("left")){
							currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"left","lamp",orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="lamp";
						}else{
							currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"right","lamp",orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="lamp";
						}			
						isFirstObject=false;
					}
				}
			}else{
				System.out.println("Table lamp present");
				String drawnCordinates="0 0 0";
				for(int k=1;k<=node.count;k++){
/*					currentCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
					node.setCordinates(currentCordinates);*/
					if(k==1){//FirstObject
						drawnCordinates=codeGenerator.drawTableLamp(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
						node.setCordinates(drawnCordinates);
						if(!(relativeLocation.equals("on"))){
							currentShape="lamp";
							currentCordinates=drawnCordinates;
						}
						isFirstObject=false;
					}else{
/*						currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"on","lamp",orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="lamp";
						isFirstObject=false;*/
						if(relativeLocation.equals("on")){
							drawnCordinates=codeGenerator.drawTableLamp(colour,size,drawnCordinates,"right","lamp",orientation,k);
							node.setCordinates(drawnCordinates);
						}else if(relativeLocation.equals("left")){
							currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"left","lamp",orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="lamp";
						}else{
							currentCordinates=codeGenerator.drawTableLamp(colour,size,currentCordinates,"right","lamp",orientation,k);
							node.setCordinates(currentCordinates);
							currentShape="lamp";
						}			
						isFirstObject=false;
					}
				}
			}
		}
		
		//Draw Bulb
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bulb")){
			System.out.println("Ceiling lamp present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawCeilingLamp(parentCordinates,relativeLocation,parentShape);
					node.setCordinates(drawnCordinates);
					
				}else{
						drawnCordinates=codeGenerator.drawCeilingLamp(drawnCordinates,"right","ceilingLamp");
						node.setCordinates(drawnCordinates);
					}			
			}
		}
		
		//Draw Bed
		else if((!(node.name==null)) && (node.name).equalsIgnoreCase("bed")){
			System.out.println("Object bed present");
			String drawnCordinates="0 0 0";
			for(int k=1;k<=node.count;k++){
/*				currentCordinates=codeGenerator.drawBed(colour,size,parentCordinates,relativeLocation,parentShape,orientation,k);
				node.setCordinates(currentCordinates);*/
				if(k==1){//FirstObject
					drawnCordinates=codeGenerator.drawBed(colour,texture,size,parentCordinates,relativeLocation,parentShape,absoluteCordinates,orientation,k);
					node.setCordinates(drawnCordinates);
					if(!(relativeLocation.equals("on"))){
						currentShape="bed";
						currentCordinates=drawnCordinates;
					}
					isFirstObject=false;
				}else{
/*					currentCordinates=codeGenerator.drawBed(colour,texture,size,currentCordinates,"right","bed",absoluteCordinates,orientation,k);
					node.setCordinates(currentCordinates);
					currentShape="bed";
					isFirstObject=false;*/
					if(relativeLocation.equals("on")){
						drawnCordinates=codeGenerator.drawBed(colour,texture,size,drawnCordinates,"right","bed",absoluteCordinates,orientation,k);
						node.setCordinates(drawnCordinates);
					}else if(relativeLocation.equals("left")){
						currentCordinates=codeGenerator.drawBed(colour,texture,size,currentCordinates,"left","bed",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="bed";
					}else{
						currentCordinates=codeGenerator.drawBed(colour,texture,size,currentCordinates,"right","bed",absoluteCordinates,orientation,k);
						node.setCordinates(currentCordinates);
						currentShape="bed";
					}			
					isFirstObject=false;
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

