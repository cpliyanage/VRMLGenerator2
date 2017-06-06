import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
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
		
	//Hashmap to store coreference resolution results. Key=Object Id, Value=List of mentions of that object
	HashMap<String, ArrayList<ObjectMention>> objectMentionsMap;
	int objectCount;
	
	//String locations[]={"left","right","above","below","front","behind","top", "under","on"};
	String[] roomLocations={"middle"};
	static String[] objects = { "table", "chair", "box","cone","sphere", "cylinder", "sofa", "bookshelf", "lamp", "bed"};
	static String[] colours= {"red", "green", "blue","brown","black", "white", "yellow", "purple", "grey", "orange", "pink", "beige", "maroon", "magenta", "cream", "peach"};
	static String[] sizes = {"small","regular","large"};
	static String[] types= {"round","square", "ceiling"};

	
	public String tagContent(String input) throws IOException{
		
		// construct the URL to the Wordnet dictionary directory
		String wnhome = System.getenv("WNHOME");
		String path = wnhome + File.separator + "dict";
		URL url = new URL("file", null , path );
		
		// construct the dictionary object and open it
		IDictionary dict = new Dictionary ( url);
		dict.open();
		
		objectMentionsMap= new HashMap<String, ArrayList<ObjectMention>>();
		objectCount=0;
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		Tree tree=new Tree();
		ArrayList<String> objectNames = new ArrayList<String>();
		HashMap<String, VRMLNode> objectMap= new HashMap<String, VRMLNode>();	
		
		String output="";
		
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
	    			 else{
	    				 //Check synonyms to get the object name
	    				 String synonym =getSynonyms(dict,word,"object");
	    				 if(!synonym.equals("")){

		    				 boolean objectPresent=checkObjectPresent(synonym,sentenceNumber);
		    				 if(!objectPresent){
		    				 //Adding the mention of the object to the mentions list
		    		    	 ObjectMention currentMention= new ObjectMention();
		    		    	 currentMention.name=synonym;
		    		    	 currentMention.sentence=sentenceNumber;
		    		    	 mentionsList.add(currentMention);
		    		    	 break;
		    				 }
		    			 
	    				 }else{
	    					 //Check Hypernyms to get object name
	    					 String hypernym=getHypernyms(dict,word,"object");
	    					 if(!hypernym.equals("")){

			    				 boolean objectPresent=checkObjectPresent(hypernym,sentenceNumber);
			    				 if(!objectPresent){
			    				 //Adding the mention of the object to the mentions list
			    		    	 ObjectMention currentMention= new ObjectMention();
			    		    	 currentMention.name=hypernym;
			    		    	 currentMention.sentence=sentenceNumber;
			    		    	 mentionsList.add(currentMention);
			    		    	 break;
			    				 }
			    			 
		    				 }
	    				 }
	    			 }
	    		 }
	    	 }
	    	 
	    	 //End making mention list
	    	 	    	 
	    	 //Create a VRML node for the object
	    	 //Add to object ID-Mention Map
	    	//Add to object ID-VRML Node Map
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
				   
				   //Identifying colour
				   if(dependencyType.equals("amod")||dependencyType.equals("compound")||dependencyType.equals("acl:relcl")){
					   if(objectNames.contains(word1Name)){
						   
						   //Identifying the colour of the object
						   if(Arrays.asList(colours).contains(word2Name)){
							   //modifiedNode=objectMap.get(word1Name);
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.colour=word2Name;
							   objectMap.put(objectId1, modifiedNode);							   
						   }else{
				  				 //Check synonyms
			    				 String synonym =getSynonyms(dict,word2Name,"colour");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByName(word1Name,sentenceNumber);
									   modifiedNode=objectMap.get(objectId1);
									   modifiedNode.colour=synonym;
									   objectMap.put(objectId1, modifiedNode);
				    			}else{
			    					 //Check Hypernyms 
			    					 String hypernym=getHypernyms(dict,word2Name,"colour");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByName(word1Name,sentenceNumber);
										   modifiedNode=objectMap.get(objectId1);
										   modifiedNode.colour=hypernym;
										   objectMap.put(objectId1, modifiedNode);
				    				 }			    				 
				    			}
						   }
						   
						   //Identifying the size of the object
						   if(Arrays.asList(sizes).contains(word2Name)){							   
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.size=word2Name;
							   objectMap.put(objectId1, modifiedNode);
						   }else{

				  				 //Check synonyms 
			    				 String synonym =getSynonyms(dict,word2Name,"size");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByName(word1Name,sentenceNumber);
									   modifiedNode=objectMap.get(objectId1);
									   modifiedNode.size=synonym;
									   objectMap.put(objectId1, modifiedNode);
				    			}else{
			    					 //Check Hypernyms
			    					 String hypernym=getHypernyms(dict,word2Name,"size");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByName(word1Name,sentenceNumber);
										   modifiedNode=objectMap.get(objectId1);
										   modifiedNode.size=hypernym;
										   objectMap.put(objectId1, modifiedNode);
				    				 }			    				 
				    			}
						   
						   }
						   
						   //Identify the type of object
						   if(Arrays.asList(types).contains(word2Name)){					   
							   objectId1=getObjectIdByName(word1Name,sentenceNumber);
							   modifiedNode=objectMap.get(objectId1);
							   modifiedNode.type=word2Name;
							   objectMap.put(objectId1, modifiedNode);
						   }else{
				  				 //Check synonyms to get the object name
			    				 String synonym =getSynonyms(dict,word2Name,"type");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByName(word1Name,sentenceNumber);
									   modifiedNode=objectMap.get(objectId1);
									   modifiedNode.colour=synonym;
									   objectMap.put(objectId1, modifiedNode);
				    			}else{
			    					 //Check Hypernyms to get object name
			    					 String hypernym=getHypernyms(dict,word2Name,"type");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByName(word1Name,sentenceNumber);
										   modifiedNode=objectMap.get(objectId1);
										   modifiedNode.type=hypernym;
										   objectMap.put(objectId1, modifiedNode);
				    				 }			    				 
				    			}
						   
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
			dict.close();
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
    
	//Method to get synonyms of a word
	public static String getSynonyms ( IDictionary dict, String name, String wordType){
		
		String synonymName="";
		
		if(wordType.equals("object")){		
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS. NOUN );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// iterate over words associated with the synset
			for( IWord w : synset . getWords ()){
				//System .out . println (w. getLemma ());
				if(Arrays.asList(objects).contains(w.getLemma())){
					System.out.println("Synonym for object present!");
					synonymName=w.getLemma();
					break;
				}
			}
			}
		}
		
		//Get synonym of colours
		if(wordType.equals("colour")){
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// iterate over words associated with the synset
			for( IWord w : synset . getWords ()){
				//System .out . println (w. getLemma ());
				if(Arrays.asList(colours).contains(w.getLemma())){
					System.out.println("Synonym for colour present!");
					synonymName=w.getLemma();
					break;
				}
			}
			}
		}
		
		//Get synonym of sizes
		if(wordType.equals("size")){
			String sizeName="";
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// iterate over words associated with the synset
			for( IWord w : synset . getWords ()){
				//System .out . println (w. getLemma ());
				if(Arrays.asList(sizes).contains(w.getLemma())){
					System.out.println("Synonym for size present!");
					synonymName=w.getLemma();
					break;
				}
			}
			}
		}
		
		//Get synonyms of types
		if(wordType.equals("type")){
			String typeName="";
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS. NOUN );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// iterate over words associated with the synset
			for( IWord w : synset . getWords ()){
				//System .out . println (w. getLemma ());
				if(Arrays.asList(types).contains(w.getLemma())){
					System.out.println("Synonym for type present!");
					synonymName=w.getLemma();
					break;
				}
			}
			}
		}
		
		return synonymName;
	}
	
	//Method to get hypernyms of a word
	 public static String getHypernyms ( IDictionary dict, String name, String wordType){
		 
		String hypernymName="";
		
		//Get hypernym of object
		if( wordType.equals("object")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS. NOUN );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// get the hypernyms
			List<ISynsetID> hypernyms =synset . getRelatedSynsets ( Pointer . HYPERNYM );
			
			// print out each h y p e r n y m s id and synonyms
			List <IWord> words ;
				for( ISynsetID sid : hypernyms ){
				words = dict . getSynset (sid). getWords ();
					for( Iterator<IWord> i = words . iterator (); i. hasNext () ;){
						String currentWord = i. next (). getLemma ();
						if(Arrays.asList(objects).contains(currentWord)){
							System.out.println("Hypernym of object present!");
							hypernymName=currentWord;
							break;
						}
					 }
				}
			}
		}
		
		//Get hypernym of colour
		if( wordType.equals("colour")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// get the hypernyms
			List<ISynsetID> hypernyms =synset . getRelatedSynsets ( Pointer . HYPERNYM );
			
			// print out each h y p e r n y m s id and synonyms
			List <IWord> words ;
				for( ISynsetID sid : hypernyms ){
				words = dict . getSynset (sid). getWords ();
					for( Iterator<IWord> i = words . iterator (); i. hasNext () ;){
						String currentWord = i. next (). getLemma ();
						if(Arrays.asList(colours).contains(currentWord)){
							System.out.println("Hypernym of colour present!");
							hypernymName=currentWord;
							break;
						}
					 }
				}
			}
		}
		
		//Get hypernym of size
		if( wordType.equals("size")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// get the hypernyms
			List<ISynsetID> hypernyms =synset . getRelatedSynsets ( Pointer . HYPERNYM );
			
			// print out each h y p e r n y m s id and synonyms
			List <IWord> words ;
				for( ISynsetID sid : hypernyms ){
				words = dict . getSynset (sid). getWords ();
					for( Iterator<IWord> i = words . iterator (); i. hasNext () ;){
						String currentWord = i. next (). getLemma ();
						if(Arrays.asList(sizes).contains(currentWord)){
							System.out.println("Hypernym of size present!");
							hypernymName=currentWord;
							break;
						}
					 }
				}
			}
		}
		
		//Get hypernym of type
		if( wordType.equals("type")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if(idxWord . getWordIDs ().size()>0){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// get the hypernyms
			List<ISynsetID> hypernyms =synset . getRelatedSynsets ( Pointer . HYPERNYM );
			
			// print out each h y p e r n y m s id and synonyms
			List <IWord> words ;
				for( ISynsetID sid : hypernyms ){
				words = dict . getSynset (sid). getWords ();
					for( Iterator<IWord> i = words . iterator (); i. hasNext () ;){
						String currentWord = i. next (). getLemma ();
						if(Arrays.asList(types).contains(currentWord)){
							System.out.println("Hypernym of type present!");
							hypernymName=currentWord;
							break;
						}
					 }
				}
			}
		}
	return hypernymName;
	}
}

