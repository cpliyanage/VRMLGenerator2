import java.io.IOException;


public class CodeGenerator {
	//ObjectIdentifier objectIdentifier= new ObjectIdentifier();
	
	//Basic shapes
	public String drawBox(String colour, String texture, String size, String parentCordinates, String relativeLocation, String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeTextures();
		attributes.initializeOrientations("box");
		attributes.initializeSizes("box");
		
		String cordinates;
		//Assign the rotation for the orientation specified
		String rotation=attributes.orientationTable.get(orientation);
		
		//If parent cordinates and relative location are defined
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"box",parentShape,size);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}else{
			cordinates="0.0 0.615 0.0";
		}
		
		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
			textureCode=attributes.textureTable.get(texture);
		}
		
		//PrintWriter writer = new PrintWriter("new5.wrl", "UTF-8");
		//writer.println("#VRML V2.0 utf8");
		
		ObjectIdentifier.writer.println("#Box");
		ObjectIdentifier.writer.println("Transform { "+
		    "translation "+cordinates+ //0.0 0.615 0.0
		    " rotation "+rotation+
		    " children [ "+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+
				textureCode+ 
			    " }"+
			    "geometry Box {"+
			    attributes.sizeTable.get(size)+
			    "}"+
			"}"+
		    "]"+
		"}");
		//writer.close();
		 System.out.println("Box drawn successfully!");	
		 return cordinates;
	}
	
	public String drawSphere(String colour, String texture, String size,String parentCordinates, String relativeLocation,String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("sphere");
		attributes.initializeSizes("sphere");

		String cordinates;
		//Assign the rotation for the orientation specified
		String rotation=attributes.orientationTable.get(orientation);
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"sphere",parentShape,size);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}else{
			cordinates="0.0 0.615 0.0";
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
		
		ObjectIdentifier.writer.println("#Sphere");
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " rotation "+rotation+
			" children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+
				textureCode+
			    " }"+
			    "geometry Sphere {"+
			    attributes.sizeTable.get(size)+
			    "}"+
			"}"+
		    "]"+
		"}");
	
		 System.out.println("Sphere drawn successfully!");	
		 return cordinates;
	}
	
	public String drawCylinder(String colour, String texture, String size,String parentCordinates, String relativeLocation,String parentShape, String absoluteCordinates,String orientation,int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("cylinder");
		attributes.initializeSizes("cylinder");

		String cordinates="0.0 0.615 0.0";
		//Assign the rotation for the orientation specified
		String rotation=attributes.orientationTable.get(orientation);
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"cylinder",parentShape,size);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
		
		ObjectIdentifier.writer.println("#Cylinder");
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " rotation "+rotation+
			" children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+textureCode+
			    " } "+
			    "geometry Cylinder {"+
			    attributes.sizeTable.get(size)
			    +
			    "}"+
			"}"+
		    "]"+
		"}");	
		 System.out.println("Cylinder drawn successfully!");
		 return cordinates;
	}
	
	public String drawCone(String colour, String texture, String size,String parentCordinates, String relativeLocation,String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("cone");
		attributes.initializeSizes("cone");
		
		String rotation=attributes.orientationTable.get(orientation);
		String cordinates="0.0 0.615 0.0";
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"cone",parentShape,size);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
		
		ObjectIdentifier.writer.println("#Cone");
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " rotation "+rotation+
			" children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+textureCode+
			    " } "+
			    "geometry Cone {"+
			    attributes.sizeTable.get(size)
			    +
			    "}"+
			"}"+
		    "]"+
		"}");
	
		 System.out.println("Cone drawn successfully!!");
		 return cordinates;
	}
	
	//custom shapes
	
	//parentCordinates is the center of the parent object	
	//String cordinates is the default locations of the object, i.e the origin	
	//Relative location defines the position, left, right etc.
	
	//Object Round Table
	public String drawRoundTable(String colour, String texture, String size, String parentCordinates,String relativeLocation,String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("table");
		
		String rotation=attributes.orientationTable.get(orientation);
		String cordinates= "0.0 0.0 0.0";//"0.0 0.0 0.0"
		//String[] cordinates={"0.0 0.615 0.0","0.0 0.3075 0.0","0.0 0.015 0.0","0.0 0.045 0.0"};
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"roundTable",cordinates,parentShape);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
		
		ObjectIdentifier.writer.println("#Round Table");
		ObjectIdentifier.writer.println("Transform { "+
			"translation "+cordinates+
			" rotation "+rotation+
			" children [ "+
			"Transform {"+
		    "translation 0.0 0.615 0.0 "+
		    " children ["+
			"Shape {"+ //Table Top
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+textureCode+
			    " } "+
			    "geometry Cylinder {"+
				"radius 0.7"+
				"height 0.03"+
			    "}"+
			"}"+
		    "]"+
		"}"+
		"Transform {"+
		    "translation 0.0 0.3075 0.0"+
		    " children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			   " geometry Box {"+
				"size 0.09 0.57 0.09"+
			    "}"+
			"}"+
		    "]}"+
		"Transform {"+
		    "translation 0.0 0.015 0.0 "+
		    "children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			    " geometry Box {"+
				"size 0.5 0.03 0.5"+
			    "}"+
			"}]}"+
		"Transform {"+
		    "translation 0.0 0.045 0.0"+
		    " children ["+
			"Shape {"+
			    "appearance USE " +colour +
			    " geometry Box {"+
				"size 0.35 0.03 0.35" +
			    "}}]}]}");
	
		System.out.println("Round Table drawn successfully!");
		return cordinates;
	}
	
	//Object Square Table
	public String drawSquareTable(String colour, String texture, String size, String parentCordinates, String relativeLocation,String parentShape, String absoluteCordinates,String orientation,int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("table");

		String rotation=attributes.orientationTable.get(orientation);
		String cordinates="0.0 0.0 0.0";
		//String[] cordinates={"0.0 0.615 0.0","0.4 0.3075 0.4","-0.4 0.3075 -0.4","0.4 0.3075 -0.4","-0.4 0.3075 0.4"};
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"squareTable",cordinates,parentShape);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
		
		ObjectIdentifier.writer.println("#Square Table");
		ObjectIdentifier.writer.println("Transform { "+
			"translation "+cordinates+
			" rotation "+rotation+
			" children [ "+
			"Transform {"+
		    "translation 0.0 0.615 0.0"+
		    " children ["+
			"Shape {"+ //Table Top
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" } "+textureCode+
			    " } "+
			    "geometry Box {"+
			    " size 1.0 0.1 1.0 "+
			    "}"+
			"}"+
		    "]"+
		"}"+
		"Transform {"+
		    "translation 0.4 0.3075 0.4"+
		    " children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			   " geometry Box {"+
				"size 0.05 0.57 0.05"+
			    "}"+
			"}"+
		    "]}"+
			
		"Transform {"+
	    "translation -0.4 0.3075 -0.4"+
	    " children ["+
		"Shape {"+ // Table Leg
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation 0.4 0.3075 -0.4"+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation -0.4 0.3075 0.4"+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}]}");
	
		System.out.println("Table drawn successfully!");
		return cordinates;
	}
	
	//Object Chair
	public String drawChair(String colour, String texture, String size, String parentCordinates,String relativeLocation,String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("chair");

		String rotation=attributes.orientationTable.get(orientation);
		String cordinates= "0.0 0.0 0.0";
		//String[] cordinates={"0.0 0.5 0.0","0.1575 0.2485 0.1575","-0.1575 0.2485 0.1575","-0.1575 0.2485 -0.1575","0.1575 0.2485 -0.1575","0.1875 0.5 0.0","0.0 0.54 0.0","0.0 0.2275 0.0","0.0 0.2275 -0.083","0.0 0.2275 0.083","0.0 0.2275 -0.166","0.0 0.2275 0.166"};

		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"chair",cordinates,parentShape);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
			
		ObjectIdentifier.writer.println("#Chair");
		ObjectIdentifier.writer.println("Transform { "+
				"translation "+cordinates+
				" rotation "+rotation+//0 1 0 1.57
				" children [ "+
					"Transform {"+ //chair seat
				    "translation 0.0 0.5 0.0"+
				    " children [ "+
					"Shape { "+
					    "appearance DEF "+colour+ " Appearance { "+
						"material Material { "+
						    "diffuseColor "+attributes.colourTable.get(colour)+
						" } "+textureCode
						+ " } "+
					    "geometry Box {"+
						"size 0.39 0.03 0.41 "+
					    "}}]}"+

				"Transform { "+
				    "translation 0.1575 0.2485 0.1575"+
				    " children [ "+
					"DEF Leg Shape { "+
					    "appearance USE "+colour+
					    " geometry Box {"+
						"size 0.03 0.497 0.03 "+
					    "}}]}"+
				"Transform {"+
				    "translation -0.1575 0.2485 0.1575"+
				    " children [ USE Leg ] "+
				"} "+
				"Transform { "+
				    "translation -0.1575 0.2485 -0.1575"+
				    " children [ USE Leg ] "+
				"}"+
				"Transform { "+
				    "translation 0.1575 0.2485 -0.1575"+
				    " children [ USE Leg ] "+
				"} "+

				"Transform { "+
				    "translation 0.1875 0.5 0.0"+
				    " rotation 0.0 0.0 1.0 -0.17 "+
				    "children [ "+
					"Transform { "+
					    "translation 0.0 0.54 0.0"+
					    " children [ "+
						"Shape { "+
						    "appearance USE "+colour+
						    " geometry Box { "+
							"size 0.06 0.17 0.43 "+
						    "}}]}"+

					"Transform {"+
					    "translation 0.0 0.2275 0.0"+
					    " children [ "+
						"DEF BackPole Shape { "+
						    "appearance USE "+colour +
						    " geometry Box { "+
							"size 0.02 0.455 0.02 "+
						    "}}]}"+
					"Transform { "+
					    "translation 0.0 0.2275 -0.083"+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform {"+
					    "translation 0.0 0.2275 0.083"+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform { "+
					    "translation 0.0 0.2275 -0.166"+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform {"+
					    "translation 0.0 0.2275 0.166"+
					    " children [ USE BackPole ]"+
					"}]}]}");
	
		 System.out.println("Chair drawn successfully!");
		 return cordinates;
	}
	
	public String drawSofa(String colour, String texture, String size, String parentCordinates, String relativeLocation,String parentShape,String absoluteCordinates, String orientation,int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("sofa");

		String rotation=attributes.orientationTable.get(orientation);
		String cordinates="0.0 0.0 0.0";
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"sofa",cordinates,parentShape);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
			if(!(texture==null)&&!(texture.equals(""))){
				textureCode=attributes.textureTable.get(texture);
			}
			
		ObjectIdentifier.writer.println("#Sofa");
		ObjectIdentifier.writer.println("Transform { "+
			"translation "+ cordinates+
		    " rotation "+rotation+
			" children [ "+
		        "Shape { "+ //sofa seat
		            "appearance DEF "+colour+" Appearance { "+
		                "material Material { "+
		                "diffuseColor "+attributes.colourTable.get(colour)+
		                " } "+textureCode+
		                " } "+
		            "geometry Box { "+
		                "size 1.5  0.35  0.75"+
		            " } } "+
		        "Transform { "+
		            "translation 0.0  0.25  -0.5"+
		            " children [ "+
		                "Shape { "+
		                    "appearance USE "+colour+
		                    " geometry Box { "+
		                        "size 1.5  0.85  0.35"+
		                    " } } ] }, ] }");
	
		System.out.println("Sofa drawn successfully!");
		return cordinates;
	}
	
	//Object Ceiling Lamp
	public void drawCeilingLamp() throws IOException{
		ObjectIdentifier.writer.println("#Ceiling Lamp");
		ObjectIdentifier.writer.println("Inline { "+
			   "url      \"ceilingLamp.wrl\" "+
			   "bboxSize 0.5 2.5 0.5 "+
			"}");
		System.out.println("Ceiling lamp drawn successfully!");
	}
	
	//Object Table Lamp
	public String drawTableLamp(String colour, String size,String parentCordinates, String relativeLocation,String parentShape,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("tableLamp");

		//Default location of the object
		String cordinates="0.0 0.0 0.0";
		
		//Get location of the object relative to parent
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"tableLamp",cordinates, parentShape);
		}
		
		ObjectIdentifier.writer.println("#Table Lamp");
		ObjectIdentifier.writer.println("Transform { "+
			"translation "+ cordinates+
			" children [ "+
			"Transform { "+
			  "translation 0.0 0.01 0.0 "+
			  "rotation 1.0 0.0 0.0 1.571 "+
			  "children [ "+
			    "DEF MoveLamp PlaneSensor { } "+
			    "DEF Lamp Transform { "+
			      "rotation 1.0 0.0 0.0 -1.571 "+
			      "children [ "+
			      "# Lamp base \n" +
			        "Shape { "+
			          "appearance Appearance { "+
			            "material Material { diffuseColor 0.5 0.5 0.5 } } "+
			          "geometry Cylinder { "+
			            "radius 0.1 "+
			            "height 0.01 "+
			          "} } "+
			        "Group { "+
			          "children [ "+
			            "DEF MoveFirstArm CylinderSensor { } "+
			            "DEF FirstArm Transform { "+
			              "children [ "+
			"Transform { "+
			"translation 0.0 0.15 0.0 "+
			"rotation    1.0 0.0 0.0  -0.7 "+
			"center      0.0 -0.15 0.0 "+
			"children [ "+
			                "DEF LampArm Shape { "+
			                  "appearance DEF Red Appearance { "+
			                    "material Material { diffuseColor 1.0 0.2 0.2 } "+
			                  "} "+
			                  "geometry Cylinder { "+
			                    "radius 0.01 "+
			                    "height 0.3 "+
			                  "} } "+
			                "Group { "+
			                  "children [ "+
			                    "DEF MoveSecondArm CylinderSensor { } "+
			                    "DEF SecondArm Transform { "+
			                      "children [ "+
			"Transform { "+
			"translation 0.0 0.3 0.0 "+
			"rotation  1.0 0.0 0.0  1.9 "+
			"center    0.0 -0.15 0.0 "+
			"children [ "+
			                      "# Second arm \n"+
			                        "USE LampArm, "+
			                      "# Second arm - shade joint \n"+
			                        "Group { "+
			                          "children [ "+
			                            "DEF MoveLampShade SphereSensor { "+
			                              "offset 1.0 0.0 0.0 -1.25 "+
			                            "} "+
			                            "DEF LampShade Transform { "+
			                              "translation 0.0 0.075 0.0 "+
			                              "rotation  1.0 0.0 0.0  -1.25 "+
			                              "center    0.0 0.075 0.0 "+
			                              "children [ "+
			                                "Shape { "+
			                                  "appearance USE Red "+
			                                  "geometry Cone { "+
			                                    "height 0.15 "+
			                                    "bottomRadius 0.12 "+
			                                    "bottom FALSE "+
			                                  "} } "+
						      "# Switch \n"+
						        "Transform { "+
						          "translation 0.0 0.075 0.0 "+
						          "children [ "+
						            "Shape { "+
						              "appearance Appearance { "+
						                "material Material { "+
						                  "diffuseColor 1.0 1.0 1.0 "+
						                "} } "+
						              "geometry Cylinder { "+
						                "radius 0.007 "+
						                "height 0.03 "+
						              "} } ] } "+
			                              "# Light bulb \n"+
			                                "Transform { "+
			                                  "translation 0.0 -0.05 0.0 "+
			                                  "children [ "+
							    "Shape { "+
			                                      "appearance Appearance { "+
			                                        "material Material { "+
			                                           "diffuseColor 0.0 0.0 0.0 "+
			                                           "emissiveColor 1.0 1.0 1.0 "+
			                                        "} } "+
			                                      "geometry Sphere { "+
			                                        "radius 0.05 "+
			                                      "} } ] } ] } ] } ] } ] } ] } ] } ] } ] } ] } ] } ] }");
	
		 System.out.println("Table Lamp drawn successfully!!");
		 return cordinates; //Return the location where the object is drawn
	}
	
	public String drawBed(String colour, String texture, String size, String parentCordinates, String relativeLocation,String parentShape,String absoluteCordinates,String orientation, int objectNum) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeOrientations("bed");

		String rotation=attributes.orientationTable.get(orientation);
		
		String cordinates="0.0 0.0 0.0";
		//String[] cordinates={"0.0 0.615 0.0","0.0 0.815 1.0","0.4 0.3075 1.0","-0.4 0.3075 -0.8","0.4 0.3075 -0.8","-0.4 0.3075 1.0"};
		
		//check whether relative location is given
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"bed",cordinates,parentShape);
		}else if(!(absoluteCordinates==null)&&!(absoluteCordinates.equals(""))){
			cordinates=absoluteCordinates;
		}
		
		attributes.initializeTextures();

		String textureCode="";
		if(!(texture==null)&&!(texture.equals(""))){
			textureCode=attributes.textureTable.get(texture);
		}
			
		ObjectIdentifier.writer.println("#Bed");
		ObjectIdentifier.writer.println("Transform { "+
			"translation "+ cordinates+
			" rotation "+rotation+//0 1 0 3
			" children [ "+
			"Transform {"+
		    "translation 0.0 0.615 0.0"+
		    " children ["+
			"Shape { "+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
			    "geometry Box {"+
			    " size 1.0 0.1 2.0 "+
			    "}"+
			"} "+
		    "]"+
		"}"+
		    
		"Transform {"+
	    "translation 0.0 0.815 1.0"+
	    " children ["+
		"Shape {"+
	    "appearance DEF " +"brown"+ " Appearance {"+
		"material Material {"+
		    "diffuseColor "+attributes.colourTable.get("brown")+
		" } "+textureCode+
	    " } "+
	    "geometry Box {"+
	    " size 1.0 0.4 0.1 "+
	    "}"+
	    "} "+
	    "]}"+
		
		"Transform {"+
		    "translation 0.4 0.3075 1.0"+
		    " children ["+
			"Shape {"+
			"appearance USE "+ "brown"+ 
			   " geometry Box {"+
				"size 0.05 0.57 0.05"+
			    "}"+
			"}"+
		    "]}"+
			
		"Transform {"+
	    "translation -0.4 0.3075 -0.8"+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation 0.4 0.3075 -0.8"+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation -0.4 0.3075 1.0"+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}]}");
	
		System.out.println("Bed drawn successfully!");
		return cordinates;
	}
}
