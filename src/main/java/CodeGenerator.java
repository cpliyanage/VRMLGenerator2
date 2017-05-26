import java.io.IOException;


public class CodeGenerator {
	//ObjectIdentifier objectIdentifier= new ObjectIdentifier();
	
	//Basic shapes
	public String drawBox(String colour, String size, String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("box");
		
		String cordinates;
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"box");
		}else{
			cordinates="0.0 0.615 0.0";
		}
		
		//PrintWriter writer = new PrintWriter("new5.wrl", "UTF-8");
		//writer.println("#VRML V2.0 utf8");
		
		
		ObjectIdentifier.writer.println("Transform {"+
		    "translation "+ cordinates+ //0.0 0.615 0.0
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
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
	
	public String drawSphere(String colour, String size,String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("sphere");

		String cordinates="0.0 0.615 0.0";
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"sphere");
		}
		
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
			    "geometry Sphere {"+
			    attributes.sizeTable.get(size)+
			    "}"+
			"}"+
		    "]"+
		"}");
	
		 System.out.println("Sphere drawn successfully!");	
		 return cordinates;
	}
	
	public String drawCylinder(String colour, String size,String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("cylinder");

		String cordinates="0.0 0.615 0.0";
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"cylinder");
		}
		
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
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
	
	public String drawCone(String colour, String size,String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("cone");

		String cordinates="0.0 0.615 0.0";
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"cone");
		}
		
		ObjectIdentifier.writer.println("Transform {"+
			"translation "+ cordinates+ //0.0 0.615 0.0
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
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
	public String drawRoundTable(String colour, String size, String parentCordinates,String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		
		String[] cordinates={"0.0 0.615 0.0","0.0 0.3075 0.0","0.0 0.015 0.0","0.0 0.045 0.0"};
		
/*		String cordinate1= "0.0 0.615 0.0";
		String cordinate2= "0.0 0.3075 0.0";
		String cordinate3= "0.0 0.015 0.0";
		String cordinate4= "0.0 0.045 0.0";*/
		
		//parentCordinates is the center of the previously drawn object
		//String[] cordinates is the default locations of the object
		//Relative location defines the position, left, right etc.
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"roundTable",cordinates);
		}

		ObjectIdentifier.writer.println("Transform {"+
		    "translation "+ cordinates[0]+
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
			    "geometry Cylinder {"+
				"radius 0.7"+
				"height 0.03"+
			    "}"+
			"}"+
		    "]"+
		"}"+
		"Transform {"+
		    "translation "+cordinates[1]+
		    " children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			   " geometry Box {"+
				"size 0.09 0.57 0.09"+
			    "}"+
			"}"+
		    "]}"+
		"Transform {"+
		    "translation "+cordinates[2]+
		    "children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			    " geometry Box {"+
				"size 0.5 0.03 0.5"+
			    "}"+
			"}]}"+
		"Transform {"+
		    "translation "+cordinates[3]+
		    " children ["+
			"Shape {"+
			    "appearance USE " +colour +
			    " geometry Box {"+
				"size 0.35 0.03 0.35" +
			    "}}]}");
	
		System.out.println("Round Table drawn successfully!");
		return cordinates[0];
	}
	
	public String drawSquareTable(String colour, String size, String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();

		String[] cordinates={"0.0 0.615 0.0","0.4 0.3075 0.4","-0.4 0.3075 -0.4","0.4 0.3075 -0.4","-0.4 0.3075 0.4"};
		
/*		String cordinate1= "0.0 0.615 0.0";
		String cordinate2= "0.4 0.3075 0.4";
		String cordinate3= "-0.4 0.3075 -0.4";
		String cordinate4= "0.4 0.3075 -0.4";
		String cordinate5= "-0.4 0.3075 0.4";*/
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"squareTable",cordinates);
		}
		
		ObjectIdentifier.writer.println("Transform {"+
		    "translation "+cordinates[0]+
		    " children ["+
			"Shape {"+
			    "appearance DEF " +colour+ " Appearance {"+
				"material Material {"+
				    "diffuseColor "+attributes.colourTable.get(colour)+
				" }"+
			    "}"+
			    "geometry Box {"+
			    " size 1.0 0.1 1.0 "+
			    "}"+
			"}"+
		    "]"+
		"}"+
		"Transform {"+
		    "translation "+cordinates[1]+
		    " children ["+
			"Shape {"+
			    "appearance USE "+ colour+
			   " geometry Box {"+
				"size 0.05 0.57 0.05"+
			    "}"+
			"}"+
		    "]}"+
			
		"Transform {"+
	    "translation "+cordinates[2]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation "+cordinates[3]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation "+cordinates[4]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ colour+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}");
	
		System.out.println("Table drawn successfully!");
		return cordinates[0];
	}
	
	public String drawChair(String colour, String size, String parentCordinates,String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		
		String[] cordinates={"0.0 0.5 0.0","0.1575 0.2485 0.1575","-0.1575 0.2485 0.1575","-0.1575 0.2485 -0.1575","0.1575 0.2485 -0.1575","0.1875 0.5 0.0","0.0 0.54 0.0","0.0 0.2275 0.0","0.0 0.2275 -0.083","0.0 0.2275 0.083","0.0 0.2275 -0.166","0.0 0.2275 0.166"};
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"chair",cordinates);
		}
		
/*		String cordinate1= "0.0 0.5 0.0";
		String cordinate2= "0.1575 0.2485 0.1575";
		String cordinate3= "-0.1575 0.2485 0.1575";
		String cordinate4= "-0.1575 0.2485 -0.1575";
		String cordinate5= "0.1575 0.2485 -0.1575";
		String cordinate6= "0.1875 0.5 0.0";
		String cordinate7= "0.0 0.54 0.0";
		String cordinate8= "0.0 0.2275 0.0";
		String cordinate9= "0.0 0.2275 -0.083";
		String cordinate10= "0.0 0.2275 0.083";
		String cordinate11= "0.0 0.2275 -0.166";
		String cordinate12= "0.0 0.2275 0.166";*/

		ObjectIdentifier.writer.println("Transform {"+

				    "translation " +cordinates[0]+
				    " children [ "+
					"Shape { "+
					    "appearance DEF "+colour+ " Appearance { "+
						"material Material { "+
						    "diffuseColor "+attributes.colourTable.get(colour)+
						" }}"+
					    "geometry Box {"+
						"size 0.39 0.03 0.41 "+
					    "}}]}"+

				"Transform { "+
				    "translation "+cordinates[1]+
				    " children [ "+
					"DEF Leg Shape { "+
					    "appearance USE "+colour+
					    " geometry Box {"+
						"size 0.03 0.497 0.03 "+
					    "}}]}"+
				"Transform {"+
				    "translation "+cordinates[2]+
				    " children [ USE Leg ] "+
				"} "+
				"Transform { "+
				    "translation "+cordinates[3]+
				    " children [ USE Leg ] "+
				"}"+
				"Transform { "+
				    "translation "+cordinates[4]+
				    " children [ USE Leg ] "+
				"} "+

				"Transform { "+
				    "translation "+cordinates[5]+
				    " rotation 0.0 0.0 1.0 -0.17 "+
				    "children [ "+
					"Transform { "+
					    "translation "+cordinates[6]+
					    " children [ "+
						"Shape { "+
						    "appearance USE "+colour+
						    " geometry Box { "+
							"size 0.06 0.17 0.43 "+
						    "}}]}"+

					"Transform {"+
					    "translation "+cordinates[7]+
					    " children [ "+
						"DEF BackPole Shape { "+
						    "appearance USE "+colour +
						    " geometry Box { "+
							"size 0.02 0.455 0.02 "+
						    "}}]}"+
					"Transform { "+
					    "translation "+cordinates[8]+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform {"+
					    "translation "+cordinates[9]+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform { "+
					    "translation "+cordinates[10]+
					    " children [ USE BackPole ] "+
					"}"+
					"Transform {"+
					    "translation "+cordinates[11]+
					    " children [ USE BackPole ]"+
					"}]}");
	
		 System.out.println("Chair drawn successfully!");
		 return cordinates[0];
	}
	
	public String drawSofa(String colour, String size, String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();

		String[] cordinates={"0.0 0.615 0.0","0.4 0.3075 0.4","-0.4 0.3075 -0.4","0.4 0.3075 -0.4","-0.4 0.3075 0.4"};
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"sofa",cordinates);
		}
		
		ObjectIdentifier.writer.println("Group { "+
		    "children [ "+
		        "Shape { "+
		            "appearance DEF "+colour+" Appearance { "+
		                "material Material { "+
		                "diffuseColor "+attributes.colourTable.get(colour)+
		                " } } "+
		            "geometry Box { "+
		                "size 7.0  1.0  3.0"+
		            " } } "+
		        "Transform { "+
		            "translation  0.0  0.75  -1.0"+
		            "children [ "+
		                "Shape { "+
		                    "appearance USE "+colour+
		                    " geometry Box { "+
		                        "size 7.0  2.5  1.0"+
		                    " } } ] }, ] }");
	
		System.out.println("Sofa drawn successfully!");
		return cordinates[0];
	}
	
	public String drawBookshelf(String colour, String size, String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();

		String[] cordinates={"0.0 0.615 0.0","0.4 0.3075 0.4","-0.4 0.3075 -0.4","0.4 0.3075 -0.4","-0.4 0.3075 0.4"};
		
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"bookshelf",cordinates);
		}
		
		ObjectIdentifier.writer.println("#VRML V1.0 ascii \n"+
				"Separator { "+
				"Transform { "+
				"rotation	0 1 0  4.71239 "+
				"center	0.745 -1.00239 0.000395298 } "+
				"Separator { "+
				"Translation { "+
				"translation	0 -4 0 } "+
				"Scale { "+
				"scaleFactor	1 0.75 1 } "+
				"Separator { "+
				"Translation { "+
				"translation	0 8 0 } "+
	    
				"DEF BookshelfBase Separator { "+
				"Separator { "+
				"Translation { "+
				"translation	0.75 0.333 0 } "+
				"Cube { "+
				"width	1.5 "+
				"height	0.333 "+
				"depth	8 } } "+
				"Separator { "+
				"Translation { "+
				"translation	0.70835 0.125 0 } "+
				"Cube { "+
				"width	1.4167 "+
				"height	0.0833 "+
				"depth	7.833 } } "+
				"Separator { "+
				"Translation { "+
				"translation	0.667 0.04167 0 } "+
				"Cube { "+
				"width	1.333 "+
				"height	0.0833 "+
				"depth	7.75 "+
		   		"} } } } "+
		   		"Separator { "+
		   		"Rotation { "+
		   		"rotation	1 0 0  3.14 } "+
		   		"USE BookshelfBase } "+
		   		"Separator { "+
		   		"Translation { "+
		   		"translation	0 4 0 } "+
		   		"Cube { "+
		   		"width	0.02 "+
		   		"height	8 "+
		   		"depth	7.5 } } "+
		   		"Separator { "+
		   		"Translation { "+
		   		"translation	0.5625 0.25 0 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	0.04167 "+
		   		"depth	7.4167 } "+
		   		"Translation { "+
		   		"translation	0 2 0 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	0.04167 "+
		   		"depth	7.4167 } "+
		   		"Translation { "+
		   		"translation	0 2 0 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	0.04167 "+
		   		"depth	7.4167 } "+
		   		"Translation { "+
		   		"translation	0 2 0 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	0.04167 "+
		   		"depth	7.4167 } } "+
		   		"Separator { "+
		   		"Separator { "+
		   		"Translation { "+
		   		"translation	1.1458 0.125 0 } "+
		   		"Cube { "+
		   		"width	0.04167 "+
		   		"height	0.25 "+
		   		"depth	7.5833 } "+
		   		"Translation { "+
		   		"translation	0 7.75 0 } "+
		   		"Cube { "+
		   		"width	0.04167 "+
		   		"height	0.25 "+
		   		"depth	7.5833 } "+
		   		"Translation { "+
		   		"translation	0 -3.875 3.67 } "+
		   		"Cube { "+
		   		"width	0.04167 "+
		   		"height	7.5 "+
		   		"depth	0.25 } "+
		   		"Translation { "+
		   		"translation	0 0 -3.67 } "+
		   		"Cube { "+
		   		"width	0.04167 "+
		   		"height	7.5 "+
		   		"depth	0.25 } "+
		   		"Translation { "+
		   		"translation	0 0 -3.67 } "+
		   		"Cube { "+
		   		"width	0.04167 "+
		   		"height	7.5 "+
		   		"depth	0.25 } } "+
	    
		   		"Translation { "+
		   		"translation	0.5625 4 3.7 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	8 "+
		   		"depth	0.0833 } "+
		   		"Translation { "+
		   		"translation 0 0 -3.7 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	8 "+
		   		"depth	0.0833 } "+
	    
		   		"Translation { "+
		   		"translation	0 0 -3.7 } "+
		   		"Cube { "+
		   		"width	1.125 "+
		   		"height	8 "+
		   		"depth	0.0833 } "+ 
		   		"DEF SideFrame Separator { "+
		   		"Translation { "+
		   		"translation	0.4375 0 -0.0625 } "+
		
		   		"Cube { "+
		   		"width	0.25 "+
		   		"height	8 "+
		   		"depth	0.04167 } "+
		   		"Translation { "+
		   		"translation	-0.875 0 0 } "+
		   		"Cube { "+
		   		"width	0.25 "+
		   		"height	8 "+
		   		"depth	0.04167 } "+
		   		"Translation { "+
		   		"translation	0.4375 3.875 0 } "+
		   		"Cube { "+ 
		   		"width	0.625 "+
		   		"height	0.25 "+
		   		"depth	0.04167 } "+
		   		"Translation { "+
		   		"translation	0 -7.75 0 } "+
		   		
		   		"Cube { "+
		   		"width	0.625 "+
		   		"height	0.25 "+
		   		"depth	0.04167 } } "+
	    
		   		"Translation { "+
		   		"translation	0 0 7.5 } "+
		   		"USE SideFrame "+
		   		"} } } ");
	
		System.out.println("Bookshelf drawn successfully!");
		return cordinates[0];
	}
	
	public void drawCeilingLamp() throws IOException{
		ObjectIdentifier.writer.println("Inline { "+
			   "url      \"ceilingLamp.wrl\" "+
			   "bboxSize 0.5 2.5 0.5 "+
			"}");
		System.out.println("Ceiling lamp drawn successfully!");
	}
	
	public String drawTableLamp(String colour, String size,String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("tableLamp");

		String cordinates="0.0 0.615 0.0";
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(parentCordinates, relativeLocation,"tableLamp");
		}
		
		ObjectIdentifier.writer.println(
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
			                                      "} } ] } ] } ] } ] } ] } ] } ] } ] } ] } ] } ] } ");
	
		 System.out.println("Table Lamp drawn successfully!!");
		 return cordinates;
	}
	
	public String drawBed(String colour, String size, String parentCordinates, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();

		String[] cordinates={"0.0 0.615 0.0","0.0 0.815 1.0","0.4 0.3075 1.0","-0.4 0.3075 -0.8","0.4 0.3075 -0.8","-0.4 0.3075 1.0"};
		
		//check whether relative location is given
		if(!parentCordinates.equals(null)&&!parentCordinates.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(parentCordinates, relativeLocation,"bed",cordinates);
		}
		
		ObjectIdentifier.writer.println("Transform {"+
		    "translation "+cordinates[0]+
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
	    "translation "+cordinates[1]+
	    " children ["+
		"Shape {"+
	    "appearance DEF " +"brown"+ " Appearance {"+
		"material Material {"+
		    "diffuseColor "+attributes.colourTable.get("brown")+
		" }"+
	    "}"+
	    "geometry Box {"+
	    " size 1.0 0.4 0.1 "+
	    "}"+
	    "} "+
	    "]}"+
		
		"Transform {"+
		    "translation "+cordinates[2]+
		    " children ["+
			"Shape {"+
			"appearance USE "+ "brown"+ 
			   " geometry Box {"+
				"size 0.05 0.57 0.05"+
			    "}"+
			"}"+
		    "]}"+
			
		"Transform {"+
	    "translation "+cordinates[3]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation "+cordinates[4]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}"+
		"Transform {"+
	    "translation "+cordinates[5]+
	    " children ["+
		"Shape {"+
		    "appearance USE "+ "brown"+
		   " geometry Box {"+
			"size 0.05 0.57 0.05"+
		    "}"+
		"}"+
	    "]}");
	
		System.out.println("Bed drawn successfully!");
		return cordinates[0];
	}
}
