import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectIdentifier {
	
	CodeGenerator codeGenerator= new CodeGenerator();
	String[] colours = { "red", "green", "blue","brown","black", "white"};
	String[] sizes = {"small","regular","large"};
	//String locations[]={"left","right","above","below","front","behind"};
	String currentLocation="";
	static PrintWriter writer;
	
	public void defineObject(ArrayList<VRMLObject> vrmlObjects) throws IOException{
		writer= new PrintWriter("generated.wrl", "UTF-8");
		writer.println("#VRML V2.0 utf8");
		
		for(VRMLObject obj:vrmlObjects){
			String colour="black";
			String size = "regular";
			if(obj.attributes.size()>0){
				for(String a:obj.attributes){
					if(Arrays.asList(colours).contains(a)){
						colour=a;
					}
					else if(Arrays.asList(sizes).contains(a)){
						size=a;
					}
				}
			}
			
			//Basic shapes
			if (!obj.name.equals(null) && obj.name.equalsIgnoreCase("box")){
				System.out.println("Object box present");
				currentLocation=codeGenerator.drawBox(colour,size,currentLocation,obj.location);
			}
			else if(!obj.name.equals(null)&&obj.name.equalsIgnoreCase("sphere")){
				System.out.println("Object sphere present");
				currentLocation=codeGenerator.drawSphere(colour,size,currentLocation,obj.location);				
			}
			else if(!obj.name.equals(null) && obj.name.equalsIgnoreCase("cone")){
				System.out.println("Object cone present");
				currentLocation=codeGenerator.drawCone(colour,size,currentLocation,obj.location);
			}
			else if(!obj.name.equals(null) && obj.name.equalsIgnoreCase("cylinder")){
				System.out.println("Object cylinder present");
				currentLocation=codeGenerator.drawCylinder(colour,size,currentLocation,obj.location);
			}
			
			//Custom objects
			else if(!obj.name.equals(null)&& obj.name.equalsIgnoreCase("table")){
				if(obj.attributes.size()>0 && obj.attributes.contains("round")){
					System.out.println("Round table present");
					currentLocation=codeGenerator.drawRoundTable(colour,size,currentLocation,obj.location);
				}else if(obj.attributes.size()>0 && obj.attributes.contains("square")){
					System.out.println("Square table present");
					currentLocation=codeGenerator.drawSquareTable(colour,size,currentLocation,obj.location);
				}else{
					System.out.println("Object table present");
					currentLocation=codeGenerator.drawRoundTable(colour,size,currentLocation,obj.location);
				}
			} 
			
			else if(!obj.name.equals(null) && obj.name.equalsIgnoreCase("chair")){
				System.out.println("Object chair present");
				currentLocation=codeGenerator.drawChair(colour,size,currentLocation,obj.location);
			}
		}
		
		writer.close();
		System.out.println("Successfully created file!");
	}
}
