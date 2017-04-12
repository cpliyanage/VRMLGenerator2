import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class CodeGenerator {
	//ObjectIdentifier objectIdentifier= new ObjectIdentifier();
	
	//Basic shapes
	public String drawBox(String colour, String size, String currentLocation, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("box");
		
		String cordinates="0.0 0.615 0.0";
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(currentLocation, relativeLocation,"box");
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
	
	public String drawSphere(String colour, String size,String currentLocation, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("sphere");

		String cordinates="0.0 0.615 0.0";
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(currentLocation, relativeLocation,"sphere");
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
	
	public String drawCylinder(String colour, String size,String currentLocation, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("cylinder");

		String cordinates="0.0 0.615 0.0";
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(currentLocation, relativeLocation,"cylinder");
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
	
	public String drawCone(String colour, String size,String currentLocation, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		attributes.initializeSizes("cone");

		String cordinates="0.0 0.615 0.0";
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocation(currentLocation, relativeLocation,"cone");
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
	public String drawRoundTable(String colour, String size, String currentLocation,String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		
		String[] cordinates={"0.0 0.615 0.0","0.0 0.3075 0.0","0.0 0.015 0.0","0.0 0.045 0.0"};
		
/*		String cordinate1= "0.0 0.615 0.0";
		String cordinate2= "0.0 0.3075 0.0";
		String cordinate3= "0.0 0.015 0.0";
		String cordinate4= "0.0 0.045 0.0";*/
		
		//currentLocation is the center of the previously drawn object
		//String[] cordinates is the default locations of the object
		//Relative location defines the position, left, right etc.
		
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(currentLocation, relativeLocation,"roundTable",cordinates);
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
	
	public String drawSquareTable(String colour, String size, String currentLocation, String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();

		String[] cordinates={"0.0 0.615 0.0","0.4 0.3075 0.4","-0.4 0.3075 -0.4","0.4 0.3075 -0.4","-0.4 0.3075 0.4"};
		
/*		String cordinate1= "0.0 0.615 0.0";
		String cordinate2= "0.4 0.3075 0.4";
		String cordinate3= "-0.4 0.3075 -0.4";
		String cordinate4= "0.4 0.3075 -0.4";
		String cordinate5= "-0.4 0.3075 0.4";*/
		
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(currentLocation, relativeLocation,"squareTable",cordinates);
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
	
	public String drawChair(String colour, String size, String currentLocation,String relativeLocation) throws IOException{
		AttributeDefinitions attributes= new AttributeDefinitions();
		attributes.initializeColours();
		
		String[] cordinates={"0.0 0.5 0.0","0.1575 0.2485 0.1575","-0.1575 0.2485 0.1575","-0.1575 0.2485 -0.1575","0.1575 0.2485 -0.1575","0.1875 0.5 0.0","0.0 0.54 0.0","0.0 0.2275 0.0","0.0 0.2275 -0.083","0.0 0.2275 0.083","0.0 0.2275 -0.166","0.0 0.2275 0.166"};
		
		if(!currentLocation.equals(null)&&!currentLocation.equals("")&&!relativeLocation.equals(null)&&!relativeLocation.equals("")){
			cordinates=attributes.getLocationOfCustomObject(currentLocation, relativeLocation,"chair",cordinates);
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
}
