import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class TaggerAndParser {
	
	int objectCount;	
	//Hashmap to store coreference resolution results. Key=Object Id, Value=List of mentions of that object
	HashMap<String, ArrayList<ObjectMention>> objectMentionsMap;
	
	public String tagContent(String input) throws IOException{
		
		objectMentionsMap= new HashMap<String, ArrayList<ObjectMention>>();
		objectCount=0;
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		Tree tree=new Tree();
		ArrayList<String> objectNames = new ArrayList<String>();
		HashMap<String, VRMLNode> objectMap= new HashMap<String, VRMLNode>();	
		
		String output="";
		
		//String locations[]={"left","right","above","below","front","behind","top", "under","on"};
		String[] objects = { "table", "chair", "box","cone","sphere", "cylinder", "sofa", "bookshelf", "lamp", "bed"};
		String[] colours= {"red", "green", "blue","brown","black", "white"};
		String[] sizes = {"small","regular","large"};
		String[] types= {"round","square", "ceiling"};

/*		//POS tagger start
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(input);
		//POS tagger end
			
		String[] taggedWords=tagged.toString().split(" ");
		String[] current;
		
		for(String a:taggedWords){								
			System.out.println(a);
			current=a.split("_");
			if(current[1].equals("NN")&&Arrays.asList(objects).contains(current[0])){
				objectNames.add(current[0]);		
				currentNode = new VRMLNode(current[0]);
				objectMap.put(current[0], currentNode);
			}
		}
		
		System.out.println(tagged.toString()); */
		
		//Stanford CoreNLP start
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		 Properties props = new Properties();
	     props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	     
	     System.out.println("Annotators added");
	     // run all Annotators on this text
	     Annotation document = new Annotation(input);
	     pipeline.annotate(document);
	     
    	 //Coreference resolution start
	     Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
	     String corefResult="";
	     
	     System.out.println("Corefernce Resolution result: ");
	     
	     //For each object/ coreference chain
	     for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
	    	 
	    	 CorefChain currentCoref =entry.getValue();
	    	 corefResult = currentCoref.toString();
	    	 System.out.println(corefResult);
	    	 
	    	 ArrayList<ObjectMention> mentionsList= new ArrayList<ObjectMention>();
	    	 
	    	 //Code to get the name and sentence number
	    	 
	    	 corefResult=corefResult.substring(corefResult.indexOf("[")+1, corefResult.length()-1);
	    	 
	    	 //String[] mentions is an array to store the mentions of a single object
	    	 String[] mentions=corefResult.split(",");
	    	 //For each mention in a single coreference chain/object
	    	 for(String mention:mentions){
	    		 String[] cSplit=mention.split(" in sentence ");
	    		 //sentence number of the mention
	    		 int sentenceNumber=Integer.parseInt(cSplit[1]);
	    		 //text part of the mention
	    		 cSplit[0]=cSplit[0].substring(1, cSplit[0].length()-1);
	    		 String[] words=cSplit[0].split(" ");
	    		 //For each word of a mention
	    		 for(String word:words){
	    			 if(Arrays.asList(objects).contains(word)){
	    				 boolean objectPresent=checkObjectPresent(word,sentenceNumber);
	    				 if(!objectPresent){
	    				 //Adding the mention of the object to the mentions list
	    		    	 ObjectMention currentMention= new ObjectMention();
	    		    	 currentMention.name=word;
	    		    	 currentMention.sentence=sentenceNumber;
	    		    	 mentionsList.add(currentMention);
	    		    	 break;
	    				 }
	    			 }
	    		 }
	    	 }
	    	 	    	 
	    	 if(mentionsList.size()>0){
	    		 
	    		 objectCount++;
		    	 String currentId="object"+objectCount;
		    	 
	    		//Adding the mentions of an object to the object mentions map
		    	 objectMentionsMap.put(currentId, mentionsList);
		    	 objectNames.add(mentionsList.get(0).name);
	      	 
		    	 //Add the object to the Object Map
		    	 VRMLNode currentNode=new VRMLNode(currentId);
		    	 currentNode.name=mentionsList.get(0).name;
		    	 objectMap.put(currentId, currentNode);	    	 
	    	 }
	    	}
	     
	     System.out.println("Number of objects: " +objectCount);
	     output = "Number of objects: "+ objectCount;
	     
	     //Coreference Resolution end
	     	     
	     // These are all the sentences in this document
	     
	     // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	     List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	     int sentenceNumber=0;
	     for(CoreMap sentence: sentences) {
	    	 
	    	 sentenceNumber++;
	    	 
	    	 //Dependency parser start
	    	 
		     //Enhanced dependencies of the sentence    	 
		       System.out.println("Enhanced dependencies of the sentence");
		       
		       SemanticGraph enhancedDependencies=sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
		       String enhancedDependenciesList = enhancedDependencies.toString(SemanticGraph.OutputFormat.LIST);
			   System.out.println(enhancedDependenciesList);
			   
			   String[] dependencyArray = enhancedDependenciesList.split("\\n"); 
			   String dependencyType="";
			   
			   for (int i=0;i<dependencyArray.length;i++){
				   
				   VRMLNode modifiedNode;
				   VRMLNode parentNode;
					
				   String[] split1= dependencyArray[i].split("\\(");
				   dependencyType = split1[0];
				   
				   String[] split2 = split1[1].split(", ");
				   
				   String[] word1 = split2[0].split("-");
				   String word1Name=word1[0];
				   String word1Index=word1[1];
				   
				   String[] word2 = split2[1].split("-");
				   String word2Name=word2[0];
				   String word2Index=word2[1].substring(0,word2[1].length()-1);
				   				   
				   //start identifying attributes
				   String objectId1;
				   String objectId2;
				   
				   if(dependencyType.equals("amod")||dependencyType.equals("compound")||dependencyType.equals("acl:relcl")){
					   if(objectNames.contains(word1Name)){
						   if(Arrays.asList(colours).contains(word2Name)){
							   //modifiedNode=objectMap.get(word1Name);
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.colour=word2Name;
							   objectMap.put(objectId1, modifiedNode);							   
						   }
						   if(Arrays.asList(sizes).contains(word2Name)){							   
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.size=word2Name;
							   objectMap.put(objectId1, modifiedNode);
						   }
						   if(Arrays.asList(types).contains(word2Name)){					   
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.type=word2Name;
							   objectMap.put(objectId1, modifiedNode);
						   }
					   }
				   }
				   //end identifying attributes
				   
				   //start identifying locations
				   
				   //location on
				   if(dependencyType.equals("nmod:on")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   
						   //getting of the child node
						   
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByName(word1Name,sentenceNumber);
						   modifiedNode=objectMap.get(objectId1);
						   
						   //getting the parent node
						   
						   //parentNode=objectMap.get(word2Name);
						   objectId2=getObjectIdByName(word2Name,sentenceNumber);
						   parentNode=objectMap.get(objectId2);
						   
						   modifiedNode.location="on"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(objectId1, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(objectId2, parentNode);						   
					   }
				   }
				   
				   //location under
				   if(dependencyType.equals("nmod:under")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   objectId1=getObjectIdByName(word1Name,sentenceNumber);
						   modifiedNode=objectMap.get(objectId1);
						   //getting the parent node
						   objectId2=getObjectIdByName(word2Name,sentenceNumber);
						   parentNode=objectMap.get(objectId2);
						   
						   modifiedNode.location="under"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(objectId1, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(objectId2, parentNode);						   
					   }
				   }
				   
				   //location behind
				   if(dependencyType.equals("nmod:behind")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   objectId1=getObjectIdByName(word1Name,sentenceNumber);
						   modifiedNode=objectMap.get(objectId1);
						   //getting the parent node
						   objectId2=getObjectIdByName(word2Name,sentenceNumber);
						   parentNode=objectMap.get(objectId2);
						   
						   modifiedNode.location="behind"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(objectId1, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(objectId2, parentNode);						   
					   }
				   }
				   
				   //location front
				   if(dependencyType.equals("nmod:in")){
					   if(objectNames.contains(word1Name)&&word2Name.equals("front")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[i].split("\\(");
							   dependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = split2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("front")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByName(word1Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   
								   //parentNode=objectMap.get(innerWord2Name);
								   objectId2=getObjectIdByName(innerWord2Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="front"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
						   }
						   
					   }
				   }
				   
				   //location above
				   if(dependencyType.equals("nmod:above")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByName(word1Name,sentenceNumber);
						   modifiedNode=objectMap.get(objectId1);
						   
						   //getting the parent node
						   //parentNode=objectMap.get(word2Name);
						   objectId2=getObjectIdByName(word2Name,sentenceNumber);
						   parentNode=objectMap.get(objectId2);
						   
						   modifiedNode.location="above"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(objectId1, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(objectId2, parentNode);						   
					   }
				   }
				   
				   //location below
				   if(dependencyType.equals("nmod:below")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByName(word1Name,sentenceNumber);
						   modifiedNode=objectMap.get(objectId1);
						   
						   //getting the parent node
						   //parentNode=objectMap.get(word2Name);
						   objectId2=getObjectIdByName(word2Name,sentenceNumber);
						   parentNode=objectMap.get(objectId2);
						   
						   modifiedNode.location="below"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(objectId1, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(objectId2, parentNode);						   
					   }
				   }
				   
				   //location left
					   if(objectNames.contains(word1Name)&&word2Name.equals("left")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   dependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("left")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByName(word1Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord2Name);
								   objectId2=getObjectIdByName(innerWord2Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
						   }
						   
					   }
					   else if(word1Name.equals("left")&&objectNames.contains(word2Name)){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   dependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(objectNames.contains(innerWord1Name)&&innerWord2Name.equals("left")){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByName(word2Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord1Name);
								   objectId2=getObjectIdByName(innerWord1Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
							   else if(innerWord1Name.equals("left")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByName(word2Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord2Name);
								   objectId2=getObjectIdByName(innerWord2Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
						   }
						   
					   }
				   
				   //location right
					   if(objectNames.contains(word1Name)&&word2Name.equals("right")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   dependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("right")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByName(word1Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord2Name);
								   objectId2=getObjectIdByName(innerWord2Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="right"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
						   }
						   
					   }
					   else if(word1Name.equals("right")&&objectNames.contains(word2Name)){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   dependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(objectNames.contains(innerWord1Name)&&innerWord2Name.equals("right")){
								   
								   //getting the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByName(word2Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord1Name);
								   objectId2=getObjectIdByName(innerWord1Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="right"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
							   else if(innerWord1Name.equals("right")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByName(word2Name,sentenceNumber);
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   //parentNode=objectMap.get(innerWord2Name);
								   objectId2=getObjectIdByName(innerWord2Name,sentenceNumber);
								   parentNode=objectMap.get(objectId2);
								   
								   modifiedNode.location="right"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);
								   break;
							   }
						   }
						   
					   }//end identifying locations
					   
			   }//Dependency parser end
			   
		     }//Stanford CoreNLP end
	     
	     //Adding the objects to the tree
			for (Entry<String, VRMLNode> obj : objectMap.entrySet()) {
			    String key = obj.getKey();
			    VRMLNode value = obj.getValue();
			    System.out.println("Printing Map");
			    System.out.println(value.id);
			    System.out.println(value.name);
			    System.out.println(value.colour);
			    tree.nodes.add(value);
			}
			objectIdentifier.defineObject(tree);
			return output;
	}
	
    //Method to get object Id by defining name and sentence
    public String getObjectIdByName(String objName, int sentenceNum){   	
    	String objectId="";		
    	for (Entry<String, ArrayList<ObjectMention>> obj : objectMentionsMap.entrySet()) {
		    String key = obj.getKey();
		    ArrayList<ObjectMention> objMentions = obj.getValue();
		    for(ObjectMention objMention:objMentions){
		    	if((objMention.name.equals(objName))&&(objMention.sentence==sentenceNum)){
		    		objectId=key;
		    	}
		    }
		}   	
    	return objectId;
    }
    
    //Method to check whether the object is already added
    public boolean checkObjectPresent(String objName, int sentenceNum){    	
    	boolean objectPresent=false;		
    	for (Entry<String, ArrayList<ObjectMention>> obj : objectMentionsMap.entrySet()) {		    
		    ArrayList<ObjectMention> objMentions = obj.getValue();
		    for(ObjectMention objMention:objMentions){
		    	if((objMention.name.equals(objName))&&(objMention.sentence==sentenceNum)){
		    		objectPresent=true;
		    	}
		    }
		}   	
    	return objectPresent;
    }				
}
