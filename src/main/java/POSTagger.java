import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagger {
	
	public String tagContent(String input) throws IOException{
		
		String output="";
		String locations[]={"left","right","above","below","front","behind","top", "under","on"};
		String[] objects = { "table", "chair", "box","cone","sphere", "cylinder"};
		String[] attributes={"red", "green", "blue","brown","black", "white","small","regular","large","round","square"};

		ArrayList<VRMLObject> objectArray = new ArrayList<VRMLObject>();
		int counter =0;
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		
		//POS tagger start
		MaxentTagger tagger = new MaxentTagger(
        		"taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(input);
		
			//POS tagger end
			
			String[] taggedWords=tagged.toString().split(" ");
			String[] current;
			VRMLObject currentElement = new VRMLObject();
			for(String a:taggedWords){								
				System.out.println(a);
				current=a.split("_");
				if(current[1].equals("JJ")&&Arrays.asList(attributes).contains(current[0])){
					currentElement.attributes.add(current[0].toLowerCase());					
				}
				else if(current[1].equals("NN")&&Arrays.asList(attributes).contains(current[0])){
					currentElement.attributes.add(current[0].toLowerCase());
				}
				else if(current[1].equals("NN")&&Arrays.asList(objects).contains(current[0])){
					currentElement.name=current[0].toLowerCase();
					currentElement.location="";
					objectArray.add(counter, currentElement);
					counter++;
					currentElement = new VRMLObject();
				}
				else if(Arrays.asList(locations).contains(current[0])){
					VRMLObject temp = new VRMLObject();
					temp=objectArray.get(counter-1);
					temp.location=current[0];
					objectArray.set(counter-1, temp);					
				}
			}
			System.out.println(tagged.toString());
			objectIdentifier.defineObject(objectArray);
			
			return output;
		}		
			
		}

