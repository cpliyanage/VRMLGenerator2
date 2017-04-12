import java.util.Hashtable;


public class AttributeDefinitions {

	Hashtable<String, String> colourTable = new Hashtable<String, String>();
	Hashtable<String, String> sizeTable = new Hashtable<String, String>();
	
	   public void initializeColours() {
	      // Create a hash map for colours
	      colourTable.put("red", "1.0 0.0 0.0");
	      colourTable.put("green", "0.0 1.0 0.0");
	      colourTable.put("blue", "0.0 0.0 1.0");
	      colourTable.put("brown", "0.6 0.35 0.0");
	      colourTable.put("black", "0.0 0.0 0.0");
	      colourTable.put("white", "1.0 1.0 1.0");
	   }
	   
	   //Initialize hash map for sizes
	   public void initializeSizes(String shape) {
		   
		   if (shape.equalsIgnoreCase("box")){
			   sizeTable.put("small", "size 0.4 0.4 0.4");
			   sizeTable.put("regular", "size 2.0 2.0 2.0");
			   sizeTable.put("large", "size 4.0 4.0 4.0");
		   }
		   if (shape.equalsIgnoreCase("sphere")){
			   sizeTable.put("small", "radius 0.2");
			   sizeTable.put("regular", "radius 1.0");
			   sizeTable.put("large", "radius 2.0");
		   }
		   if (shape.equalsIgnoreCase("cone")){
			   sizeTable.put("small", " bottomRadius 0.2 "+ "height 0.5 "+ "side TRUE "+ "bottom TRUE ");
			   sizeTable.put("regular", " bottomRadius 1.0 "+ "height 2.0 "+ "side TRUE "+ "bottom TRUE ");
			   sizeTable.put("large", " bottomRadius 2.0 "+ "height 4.0 "+ "side TRUE "+ "bottom TRUE ");
		   }
		   if (shape.equalsIgnoreCase("cylinder")){
			   sizeTable.put("small", " radius 0.2 " + "height 0.5 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
			   sizeTable.put("regular", " radius 1.0 " + "height 2.0 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
			   sizeTable.put("large", " radius 2.0 " + "height 4.0 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
		   }
/*		   if (shape.equalsIgnoreCase("table")){
			   sizeTable.put("small", "1.0 0.0 0.0");
			   sizeTable.put("regular", "0.0 1.0 0.0");
			   sizeTable.put("large", "0.0 0.0 1.0");
		   }
		   if (shape.equalsIgnoreCase("chair")){
			   sizeTable.put("small", "1.0 0.0 0.0");
			   sizeTable.put("regular", "0.0 1.0 0.0");
			   sizeTable.put("large", "0.0 0.0 1.0");

		   }*/

	   }
	   
	   //Get the new location of basic shapes
	   public String getLocation(String currentLocation , String relativeLocation,String shape){
		   String newLocation;
		   String arr[]=currentLocation.split(" ");
		   double x = Double.parseDouble(arr[0]);
		   double y = Double.parseDouble(arr[1]);
		   double z = Double.parseDouble(arr[2]);
		   
		   if(relativeLocation.equals("left")){
			   x=x-3;
		   }
		   else if(relativeLocation.equals("right")){
			   x=x+3;
		   }
		   else if(relativeLocation.equals("above")||relativeLocation.equals("top")){
			   y=y+3;
		   }
		   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
			   y=y-3;
		   }
		   else if(relativeLocation.equals("on")){
			   y=y+0.2;
		   }
		   else if(relativeLocation.equals("front")){
			   z=z+3;
		   }
		   else if(relativeLocation.equals("behind")){
			   z=z-3;
		   }
		   
		   newLocation = x+" "+y+" "+z;
		   return newLocation;
		   
	   }
	 //Get location of custom shapes
	   public String[] getLocationOfCustomObject(String currentLocation , String relativeLocation,String shape,String[] oldCordinates){
		   String[] newLocations={""};
		   
		   if(shape.equals("roundTable")){
			   newLocations = new String[4];
			   String arr1[]=currentLocation.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   for(int i=0;i<oldCordinates.length;i++){
				   String arr2[]=oldCordinates[i].split(" ");
				   double x2 = Double.parseDouble(arr2[0]);
				   double y2 = Double.parseDouble(arr2[1]);
				   double z2 = Double.parseDouble(arr2[2]);
				   
				   if(relativeLocation.equals("left")){
					   x2=x1+x2-3;
				   }
				   else if(relativeLocation.equals("right")){
					   x2=x1+x2+3;
				   }
				   else if(relativeLocation.equals("above")||relativeLocation.equals("top")){
					   y2=y1+y2+3;
				   }
				   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
					   y2=y1+y2-3;
				   }
				   else if(relativeLocation.equals("front")){
					   z2=z1+z2+3;
				   }
				   else if(relativeLocation.equals("behind")){
					   z2=z1+z2-3;
				   }
				   newLocations[i]=x2+" "+y2+" "+z2;
			   }
		   }
		   else if(shape.equals("squareTable")){
			   newLocations = new String[5];
			   String arr1[]=currentLocation.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   for(int i=0;i<oldCordinates.length;i++){
				   String arr2[]=oldCordinates[i].split(" ");
				   double x2 = Double.parseDouble(arr2[0]);
				   double y2 = Double.parseDouble(arr2[1]);
				   double z2 = Double.parseDouble(arr2[2]);
				   
				   if(relativeLocation.equals("left")){
					   x2=x1+x2-3;
				   }
				   else if(relativeLocation.equals("right")){
					   x2=x1+x2+3;
				   }
				   else if(relativeLocation.equals("above")||relativeLocation.equals("top")){
					   y2=y1+y2+3;
				   }
				   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
					   y2=y1+y2-3;
				   }
				   else if(relativeLocation.equals("front")){
					   z2=z1+z2+3;
				   }
				   else if(relativeLocation.equals("behind")){
					   z2=z1+z2-3;
				   }
				   newLocations[i]=x2+" "+y2+" "+z2;
			   }
		   }
		   else if(shape.equals("chair")){
			   newLocations = new String[12];
			   String arr1[]=currentLocation.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   for(int i=0;i<oldCordinates.length;i++){
				   String arr2[]=oldCordinates[i].split(" ");
				   double x2 = Double.parseDouble(arr2[0]);
				   double y2 = Double.parseDouble(arr2[1]);
				   double z2 = Double.parseDouble(arr2[2]);
				   
				   if(relativeLocation.equals("left")){
					   x2=x1+x2-3;
				   }
				   else if(relativeLocation.equals("right")){
					   x2=x1+x2+3;
				   }
				   else if(relativeLocation.equals("above")||relativeLocation.equals("top")){
					   y2=y1+y2+3;
				   }
				   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
					   y2=y1+y2-3;
				   }
				   else if(relativeLocation.equals("front")){
					   z2=z1+z2+3;
				   }
				   else if(relativeLocation.equals("behind")){
					   z2=z1+z2-3;
				   }
				   newLocations[i]=x2+" "+y2+" "+z2;
			   }
		   }
		   
		   return newLocations;
	   }
}
