import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectIdentifier {
	
	CodeGenerator codeGenerator= new CodeGenerator();
	String[] colours = { "red", "green", "blue","brown","black", "white"};
	String[] sizes = {"small","regular","large"};
	//String locations[]={"left","right","above","below","front","behind"};
	String currentLocation="";
	static PrintWriter writer;
	
	public void defineObject(HashMap<String,VRMLObject> vrmlObjects) throws IOException{
		writer= new PrintWriter("generated.wrl", "UTF-8");
		writer.println("#VRML V2.0 utf8");
		
		for (HashMap.Entry<String, VRMLObject> mapEntry : vrmlObjects.entrySet()) {
		    String key = mapEntry.getKey();
		    VRMLObject obj = mapEntry.getValue();
		    
		    System.out.println("Printing Map");
		    System.out.println(obj.name);
		    System.out.println(obj.colour);
		    
			String colour="black";
			String size = "regular";
			
			if((!(obj.colour==null))){
				colour=obj.colour;
			}
			if((!(obj.size==null))){
				colour=obj.size;
			}
			
			//Basic shapes
			if ((!(obj.name==null)) && obj.name.equalsIgnoreCase("box")){
				System.out.println("Object box present");
				codeGenerator.drawBox(colour,size);
			}
			else if((!(obj.name==null))&&obj.name.equalsIgnoreCase("sphere")){
				System.out.println("Object sphere present");
				codeGenerator.drawSphere(colour,size);				
			}
			else if((!(obj.name==null)) && obj.name.equalsIgnoreCase("cone")){
				System.out.println("Object cone present");
				codeGenerator.drawCone(colour,size);
			}
			else if((!(obj.name==null)) && obj.name.equalsIgnoreCase("cylinder")){
				System.out.println("Object cylinder present");
				codeGenerator.drawCylinder(colour,size);
			}
			
			//Custom objects
			else if(!obj.name.equals(null)&& obj.name.equalsIgnoreCase("table")){
				if((!(obj.type==null)) && !obj.type.equals("round")){
					System.out.println("Round table present");
					codeGenerator.drawRoundTable(colour,size);
				}else if((!(obj.type==null)) && !obj.type.equals("square")){
					System.out.println("Square table present");
					codeGenerator.drawSquareTable(colour,size);
				}else{
					System.out.println("Object table present");
					codeGenerator.drawRoundTable(colour,size);
				}
			} 
			
			else if((!(obj.name==null)) && obj.name.equalsIgnoreCase("chair")){
				System.out.println("Object chair present");
				codeGenerator.drawChair(colour,size);
			} 
		} 
		
		writer.close();
		System.out.println("Successfully created file!");
	}
}

