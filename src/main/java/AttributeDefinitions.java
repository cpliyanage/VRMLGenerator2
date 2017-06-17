import java.util.Hashtable;


public class AttributeDefinitions {

	Hashtable<String, String> colourTable = new Hashtable<String, String>();
	Hashtable<String, String> sizeTable = new Hashtable<String, String>();
	Hashtable<String, String> orientationTable = new Hashtable<String, String>();
	Hashtable<String, String> textureTable = new Hashtable<String, String>();
	
	 // Create a hash map for colours   
		public void initializeColours() {
	      colourTable.put("red", "1.0 0.0 0.0");
	      colourTable.put("green", "0.0 1.0 0.0");
	      colourTable.put("blue", "0.0 0.0 1.0");
	      colourTable.put("brown", "0.6 0.35 0.0");
	      colourTable.put("black", "0.0 0.0 0.0");
	      colourTable.put("white", "1.0 1.0 1.0");
	      colourTable.put("yellow", "1.0 1.0 0.0");
	      colourTable.put("purple", "0.627 0.125 0.941");
	      colourTable.put("grey", "0.5 0.5 0.5");
	      colourTable.put("orange", "1.0 0.647 0.0");
	      colourTable.put("pink", "1.0 0.752 0.796");
	      colourTable.put("beige", "0.96 0.96 0.862");
	      colourTable.put("maroon", "0.5 0.0 0.0");
	      colourTable.put("magenta", "1.0 0.0 1.0");
	      colourTable.put("cream", "0.96 1.0 0.98");
	      colourTable.put("peach", "1.0 0.85 0.72");
	   }
		
		 // Create a hash map for colours   
			public void initializeTextures() {
				textureTable.put("wood", " texture ImageTexture { url \"wood.jpg\" } ");
				textureTable.put("wooden", " texture ImageTexture { url \"wood.jpg\" } ");
				textureTable.put("metal", " texture ImageTexture { url \"steel.jpg\" } ");
				textureTable.put("steel", " texture ImageTexture { url \"steel.jpg\" } ");
				textureTable.put("glass", " texture ImageTexture { url \"glass.jpg\" } ");
		   }
		
	 // Create a hash map for orientations   
		public void initializeOrientations(String shape) {
			if (shape.equalsIgnoreCase("chair")){
			orientationTable.put("front", "0 1 0 1.5708");
			orientationTable.put("backward", "0 1 0 4.7124");
			orientationTable.put("left", "0 0 0 0");
			orientationTable.put("right", "0 1 0 3.1416");
		   }
			else if (shape.equalsIgnoreCase("bed")){
			orientationTable.put("front", "0 1 0 3.1416");
			orientationTable.put("backward", "0 0 0 0");
			orientationTable.put("left", "0 1 0 1.5708");
			orientationTable.put("right", "0 1 0 4.7124");
		   }
			//if ((shape.equalsIgnoreCase("box"))||(shape.equalsIgnoreCase("sphere"))||(shape.equalsIgnoreCase("cone"))||(shape.equalsIgnoreCase("cylinder"))||(shape.equalsIgnoreCase("table"))||(shape.equalsIgnoreCase("sofa"))){
			else{
			orientationTable.put("front", "0 0 0 0");// Objects are facing front by default. No rotation needed
			orientationTable.put("backward", "0 1 0 3.1416");
			orientationTable.put("left", "0 1 0 4.7124");
			orientationTable.put("right", "0 1 0 1.5708");
		   }
		}
	   
	   //Initialize hash map for sizes
	   public void initializeSizes(String shape) {
		   
		   if (shape.equalsIgnoreCase("box")){
			   sizeTable.put("small", "size 0.2 0.2 0.2");
			   sizeTable.put("regular", "size 0.4 0.4 0.4");
			   sizeTable.put("medium", "size 0.4 0.4 0.4");
			   sizeTable.put("large", "size 0.8 0.8 0.8");
		   }
		   if (shape.equalsIgnoreCase("sphere")){
			   sizeTable.put("small", "radius 0.1");
			   sizeTable.put("regular", "radius 0.2");
			   sizeTable.put("medium", "radius 0.2");
			   sizeTable.put("large", "radius 0.4");
		   }
		   if (shape.equalsIgnoreCase("cone")){
			   sizeTable.put("small", " bottomRadius 0.1 "+ "height 0.25 "+ "side TRUE "+ "bottom TRUE ");
			   sizeTable.put("regular", " bottomRadius 0.2 "+ "height 0.5 "+ "side TRUE "+ "bottom TRUE ");
			   sizeTable.put("medium", " bottomRadius 0.2 "+ "height 0.5 "+ "side TRUE "+ "bottom TRUE ");
			   sizeTable.put("large", " bottomRadius 0.4 "+ "height 1.0 "+ "side TRUE "+ "bottom TRUE ");
		   }
		   if (shape.equalsIgnoreCase("cylinder")){
			   sizeTable.put("small", " radius 0.1 " + "height 0.25 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
			   sizeTable.put("regular", " radius 0.2 " + "height 0.5 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
			   sizeTable.put("medium", " radius 0.2 " + "height 0.5 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
			   sizeTable.put("large", " radius 0.4 " + "height 1.0 " + "side TRUE "+ "bottom TRUE " + "top TRUE ");
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
	   public String getLocation(String parentCordinates , String relativeLocation,String shape, String parentShape,String size){
		   String newLocation;
		   String arr[]=parentCordinates.split(" ");
		   double x = Double.parseDouble(arr[0]);
		   double y = Double.parseDouble(arr[1]);
		   double z = Double.parseDouble(arr[2]);
		   
		   if(relativeLocation.equals("left")){
			   x=x-1.5;
		   }
		   else if(relativeLocation.equals("right")){
			   x=x+1.5;
		   }
		   else if(relativeLocation.equals("above")){
			   if(parentShape.equals("table")){
				   y=y+0.615+1.5;
			   }
			   else if(parentShape.equals("chair")){
				   y=y+0.5+1.3;
			   }
			   else{
				   y=y+0.5+1.5;
			   }
		   }
		   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){ //Need reconsideration
			   y=y-1.5;
		   }
		   else if(relativeLocation.equals("front")){
			   z=z+1.5;
		   }
		   else if(relativeLocation.equals("behind")){
			   z=z-1.5;
		   }
		   else if(relativeLocation.equals("on")||relativeLocation.equals("top")){
			   if(shape.equals("box")){
				   String Currentsize =sizeTable.get(size);
				   String[] sSplit  = Currentsize.split(" ");
				   double radius =Double.parseDouble(sSplit[1]);
				   if(parentShape.equals("table")){
					   y=y+0.615+radius/2;
				   }
				   else if(parentShape.equals("chair")){
					   y=y+0.5+radius/2;
				   }
				   else if(parentShape.equals("sofa")){
					   y=y+0.175+radius/2;
				   }
				   else if(parentShape.equals("bed")){
					   y=y+0.57+radius/2;
				   }
				   else{
					   y=y+radius/2;
				   }
			   }
			   else if(shape.equals("sphere")){
				   String Currentsize =sizeTable.get(size);
				   String[] sSplit  = Currentsize.split(" ");
				   double radius =Double.parseDouble(sSplit[1]);
				   if(parentShape.equals("table")){
					   y=y+0.615+radius;
				   }
				   else if(parentShape.equals("chair")){
					   y=y+0.5+radius;
				   }
				   else if(parentShape.equals("bed")){
					   y=y+0.57+radius;
				   }
				   else if(parentShape.equals("sofa")){
					   y=y+0.175+radius/2;
				   }
				   else{
					   y=y+0.5+radius;
				   }
			   }
			   else if(shape.equals("cone")||shape.equals("cylinder")){
				   String Currentsize =sizeTable.get(size);
				   String[] sSplit  = Currentsize.split(" ");
				   double height =Double.parseDouble(sSplit[4]);
				   if(parentShape.equals("table")){
					   y=y+0.615+height/2;
				   }
				   else if(parentShape.equals("chair")){
					   y=y+0.5+height/2;
				   }
				   else if(parentShape.equals("chair")){
					   y=y+0.57+height/2;
				   }
				   else if(parentShape.equals("sofa")){
					   y=y+0.175+height/2;
				   }
				   else{
					   y=y+height/2;
				   }
			   }
		   }
		   
		   newLocation = x+" "+y+" "+z;
		   return newLocation;
		   
	   }
	   
	 //Get location of custom shapes
	   public String getLocationOfCustomObject(String parentCordinates , String relativeLocation,String shape,String currentCordinates,String parentShape){
		   String newCordinates="";
		   
		   //Shape round table, square table and chair
		   if(shape.equals("roundTable")||shape.equals("squareTable")||shape.equals("chair")){

			   //Parent cordinates
			   String arr1[]=parentCordinates.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   
		/*	   //Current cordinates
			   String arr2[]=currentCordinates.split(" ");
			   double x2 = Double.parseDouble(arr2[0]);
			   double y2 = Double.parseDouble(arr2[1]);
			   double z2 = Double.parseDouble(arr2[2]); */
			   			   			   
			   //old cordinates are the cordinates of the object relative to origin
			   if(relativeLocation.equals("left")){
				   x1=x1-1.5;
			   }
			   else if(relativeLocation.equals("right")){
				   x1=x1+1.5;
			   }
			   else if(relativeLocation.equals("above")){
				   y1=y1+1.5;
			   }
			   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
				   //y1=y1-1.5;
				   x1=x1+0.25;
			   }
			   else if(relativeLocation.equals("on")||relativeLocation.equals("top")){
				   y1=y1+0.2;
			   }
			   else if(relativeLocation.equals("front")){
				   z1=z1+1.5;
			   }
			   else if(relativeLocation.equals("behind")){
				   z1=z1-1.5;
			   }
			   
			   newCordinates = x1+" "+y1+" "+z1;
			   
		   }
		   
		   //Shape sofa
		   else if(shape.equals("sofa")){

			   //Parent cordinates
			   String arr1[]=parentCordinates.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   			   			   
			   //old cordinates are the cordinates of the object relative to origin
			   if(relativeLocation.equals("left")){
				   x1=x1-2;
			   }
			   else if(relativeLocation.equals("right")){
				   x1=x1+2;
			   }
			   else if(relativeLocation.equals("above")){
				   y1=y1+1.5;
			   }
			   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
				   //y1=y1-1.5;
				   x1=x1+0.25;
			   }
			   else if(relativeLocation.equals("on")||relativeLocation.equals("top")){
				   y1=y1+0.2;
			   }
			   else if(relativeLocation.equals("front")){
				   z1=z1+1.5;
			   }
			   else if(relativeLocation.equals("behind")){
				   z1=z1-1.5;
			   }			   
			   newCordinates = x1+" "+y1+" "+z1;			   
		   }
		   
		   //Shape bed
		   else if(shape.equals("bed")){

			   //Parent cordinates
			   String arr1[]=parentCordinates.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   
			   //old cordinates are the cordinates of the object relative to origin
			   if(relativeLocation.equals("left")){
				   x1=x1-1.5;
			   }
			   else if(relativeLocation.equals("right")){
				   x1=x1+1.5;
			   }
			   else if(relativeLocation.equals("above")){
				   y1=y1+1.5;
			   }
			   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
				   //y1=y1-1.5;
				   x1=x1+0.25;
			   }
			   else if(relativeLocation.equals("on")||relativeLocation.equals("top")){
				   y1=y1+0.2;
			   }
			   else if(relativeLocation.equals("front")){
				   z1=z1+2;
			   }
			   else if(relativeLocation.equals("behind")){
				   z1=z1-2;
			   }
			   
			   newCordinates = x1+" "+y1+" "+z1;
			   
		   }
		   
		   //Shape table lamp
		   else if(shape.equals("tableLamp")){

			   //Parent cordinates
			   String arr1[]=parentCordinates.split(" ");
			   double x1 = Double.parseDouble(arr1[0]);
			   double y1 = Double.parseDouble(arr1[1]);
			   double z1 = Double.parseDouble(arr1[2]);
			   
		/*	   //Current cordinates
			   String arr2[]=currentCordinates.split(" ");
			   double x2 = Double.parseDouble(arr2[0]);
			   double y2 = Double.parseDouble(arr2[1]);
			   double z2 = Double.parseDouble(arr2[2]); */
			   			   			   
			   //old cordinates are the cordinates of the object relative to origin
			   if(relativeLocation.equals("left")){
				   x1=x1-1.5;
			   }
			   else if(relativeLocation.equals("right")){
				   x1=x1+1.5;
			   }
			   else if(relativeLocation.equals("above")){
				   y1=y1+1.5;
			   }
			   else if(relativeLocation.equals("below")||relativeLocation.equals("under")){
				   //y1=y1-1.5;
				   x1=x1+0.25;
			   }
			   else if(relativeLocation.equals("on")||relativeLocation.equals("top")){
				   
				   if(parentShape.equals("table")){
					   y1=y1+0.615+0.055;
					   x1=x1-0.25;
				   }
				   else if(parentShape.equals("chair")){
					   y1=y1+0.5+0.2;
				   }
				   else{
					   y1=y1+0.2;
				   }
			   }
			   else if(relativeLocation.equals("front")){
				   z1=z1+1.5;
			   }
			   else if(relativeLocation.equals("behind")){
				   z1=z1-1.5;
			   }			   
			   newCordinates = x1+" "+y1+" "+z1;			   
		   }
		   return newCordinates;
	   }
}
