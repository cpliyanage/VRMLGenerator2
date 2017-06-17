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
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ie.NumberNormalizer;

public class TaggerAndParser {
		
	//Hashmap to store coreference resolution results. Key=Object Id, Value=List of mentions of that object
	HashMap<String, ArrayList<ObjectMention>> objectMentionsMap;
	int objectCount;
	
	String[] locations={"left","right","above","below","front","behind","top", "under","on", "next"};
	String[] roomLocations={"middle", "corner"};	
	String[] roomTextures= {"brick"};
	String[] orientations= {"front","backward", "left", "right"};
	String[] pronouns={"this","these","that","those","it"};
	static String[] textures= {"wooden","wood","glass","metal","steel"};
	static String[] objects = { "table", "chair", "box","cone","sphere", "cylinder", "sofa", "lamp", "bed","ball"};
	static String[] colours= {"red", "green", "blue","brown","black", "white", "yellow", "purple", "grey", "orange", "pink", "beige", "maroon", "magenta", "cream", "peach"};
	static String[] sizes = {"small","regular","medium", "large"};
	static String[] types= {"round","square", "ceiling","coffee"};

	
	public String tagContent(String input) throws IOException{
		
		//convert the input to lower case
		input =input.toLowerCase();
		
		// construct the URL to the Wordnet dictionary directory
		String wnhome = System.getenv("WNHOME");
		String path = wnhome + File.separator + "dict";
		URL url = new URL("file", null , path );
		
		// construct the dictionary object and open it
		IDictionary dict = new Dictionary ( url);
		dict.open();
		
		//create stemmer object
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		
		//Create objects and initialize variables
		objectMentionsMap= new HashMap<String, ArrayList<ObjectMention>>();
		objectCount=0;
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		Tree tree=new Tree();
		ArrayList<String> objectNames = new ArrayList<String>();
		HashMap<String, VRMLNode> objectMap= new HashMap<String, VRMLNode>();
		ArrayList<String> nounsPresent = new ArrayList<String>();
		ArrayList<String> adjectivesPresent = new ArrayList<String>();
		
		String output="";
		
		//Stanford CoreNLP start
		
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
		 Properties props = new Properties();
	     props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	     
	     System.out.println("Annotators added");
	     // run all Annotators on this text
	     Annotation document = new Annotation(input);
	     pipeline.annotate(document);
	     
	     // These are all the sentences in this document
	     
	     // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	     List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	     
	     //POS tagger start
	     for(CoreMap taggedSentence: sentences) {
	       for (CoreLabel token: taggedSentence.get(TokensAnnotation.class)) {
		         // this is the text of the token
		         String word = token.get(TextAnnotation.class);
		         // this is the POS tag of the token
		         String pos = token.get(PartOfSpeechAnnotation.class);
				if(pos.equals("NN")||pos.equals("NNS")){
					nounsPresent.add(word);
				}
				else if(pos.equals("JJ")){
					adjectivesPresent.add(word);
				}
		       }
	     }
		
/*		//POS tagger start
		MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(input);
		//POS tagger end
			
		String[] taggedWords=tagged.toString().split(" ");
		String[] current;
		
		for(String a:taggedWords){								
			System.out.println(a);
			current=a.split("_");
			if(current[1].equals("NN")||current[1].equals("NNS")){
				nounsPresent.add(current[0]);
			}
			if(current[1].equals("JJ")){
				adjectivesPresent.add(current[0]);
			}
			//if(current[1].equals("NN")&&Arrays.asList(objects).contains(current[0])){
			//objectNames.add(current[0]);		
			//currentNode = new VRMLNode(current[0]);
			//objectMap.put(current[0], currentNode);
			//} 
		}
		
		System.out.println(tagged.toString()); */
		
	     //Create a node for the room
	     VRMLNode roomNode=new VRMLNode("object0");
	     roomNode.name="room";
	     objectMap.put("object0", roomNode);
	     
    	 //Coreference resolution start
	     Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
	     String corefResult="";
	     
	     System.out.println("Corefernce Resolution result: ");
	     
	     //For each coreference chain (A single coreference chain is a single object)
	     for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
	    	 
	    	 CorefChain currentCoref =entry.getValue();
	    	 corefResult = currentCoref.toString();
	    	 System.out.println(corefResult);
	    	 
	    	 ArrayList<ObjectMention> mentionsList= new ArrayList<ObjectMention>();
	    	 
	    	 //Code to get the name and sentence number	    	 
	    	 corefResult=corefResult.substring(corefResult.indexOf("[")+1, corefResult.length()-1);
	    	 
	    	 //Array to store the mentions in a single coreference chain (mentions of a single object)
	    	 String[] mentions=corefResult.split(", ");
	    	 if(mentions.length>0){
	    		 String[] fSplit=mentions[0].split(" in sentence ");
	    		//sentence number of the mention
	    		 int fSentenceNumber=Integer.parseInt(fSplit[1]);
	    		 //text part of the first mention
	    		 fSplit[0]=fSplit[0].substring(1, fSplit[0].length()-1);//removing quotes
	    		 String[] fMentionWords=fSplit[0].split(" ");// Array of words in the first mention
	    		 
	    		 //Eliminating coreference chains with conjunctions
	    		 if(!(Arrays.asList(fMentionWords).contains("and"))){
	    	    	 //For each mention in a single coreference chain/object
	    	    	 for(String mention:mentions){
	    	    		 String[] cSplit=mention.split(" in sentence ");
	    	    		 //sentence number of the mention
	    	    		 int sentenceNumber=Integer.parseInt(cSplit[1]);
	    	    		 //text part of the mention
	    	    		 cSplit[0]=cSplit[0].substring(1, cSplit[0].length()-1);//removing quotes
	    	    		 String[] words=cSplit[0].split(" ");
	    	    		
	    	    		 //For each word of a mention
	    		    	for(String word:words){
	    		    		if(nounsPresent.contains(word)||Arrays.asList(pronouns).contains(word)){	 
	    						 String stemWord=word;
	    						 
	    						 //Obtaining stem of the word						 
	    						 List<String> stems = stemmer.findStems(word, POS.NOUN);
	    						 if(stems.size()>0){
	    							 stemWord= stems.get(0);
	    						 }
	    						 
	    		    			 if(Arrays.asList(objects).contains(stemWord)||Arrays.asList(pronouns).contains(stemWord)){
	    		    				 boolean objectPresent=checkObjectPresent(stemWord,sentenceNumber);
	    		    				 if(!objectPresent){
	    		    				 //Adding the mention of the object to the mentions list
	    		    		    	 ObjectMention currentMention= new ObjectMention();
	    		    		    	 currentMention.name=stemWord;
	    		    		    	 currentMention.sentence=sentenceNumber;
	    		    		    	 mentionsList.add(currentMention);
	    		    		    	 break;
	    		    				 }
	    		    			 }
	    		    			 else{
	    		    				 //Check synonyms to get the object name
	    			    				 String synonym =getSynonyms(dict,stemWord,"object");
	    			    				 if(!(synonym.equals(""))){
	    				    				 boolean objectPresent=checkObjectPresent(synonym,sentenceNumber);
	    				    				 if(!objectPresent){
	    				    				 //Adding the mention of the object to the mentions list
	    				    		    	 ObjectMention currentMention= new ObjectMention();
	    				    		    	 currentMention.name=synonym;
	    				    		    	 currentMention.sentence=sentenceNumber;
	    				    		    	 mentionsList.add(currentMention);
	    				    		    	 break;
	    				    				 }
	    			    				 }
	    		    				 else{
	    		    					 //Check Hypernyms to get object name
	    				    					 String hypernym=getHypernyms(dict,stemWord,"object");
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
	    	    	 }//End making mention list
	    		 }
	    		
	    	 }
	    	 	    	 
	    	//Create a VRML node for the object, Add to object ID-Mention Map, Add to object ID-VRML Node Map
	    	 if(mentionsList.size()>0 && !(Arrays.asList(pronouns).contains(mentionsList.get(0).name))){// First mention should not be a pronoun
	    		 
	    		 objectCount++;
		    	 String currentId="object"+objectCount;
		    	 
		    	//Adding the mentions of an object to the object mentions map
		    	 objectMentionsMap.put(currentId, mentionsList);
		    	 objectNames.add(mentionsList.get(0).name);
	      	 
		    	 //Add the object to the Object Map
		    	 VRMLNode currentNode=new VRMLNode(currentId);
		    	 currentNode.name=mentionsList.get(0).name;
		    	 
		    	 //Checking for attributes		    	 
	    		 String[] mentionSplit=mentions[0].split(" in sentence ");
	    		 //Sentence number
	    		 int fMentionSentence=Integer.parseInt(mentionSplit[1]);
	    		 
	    		 //text part of the mention
	    		 mentionSplit[0]=mentionSplit[0].substring(1, mentionSplit[0].length()-1);
	    		 String[] firstMentionWords=mentionSplit[0].split(" ");
		    	 
	    		 for(int x=0;x<firstMentionWords.length;x++){
	    			 if(adjectivesPresent.contains(firstMentionWords[x])){
	    				 //colour
		    			 if((Arrays.asList(colours).contains(firstMentionWords[x]))){
		    				 currentNode.colour=firstMentionWords[x];
		    			 }else{
			  				 //Check synonyms
		    				 String wsynonym =getSynonyms(dict,firstMentionWords[x],"colour");
		    				 if(!wsynonym.equals("")){
		    					 currentNode.colour=wsynonym;
			    			}else{
		    					 //Check Hypernyms 
		    					 String whypernym=getHypernyms(dict,firstMentionWords[x],"colour");
		    					 if(!whypernym.equals("")){
		    						 currentNode.colour=whypernym;
			    				 }			    				 
			    			}					   
		    			 }
		    			 
	    				 //texture
		    			 if((Arrays.asList(textures).contains(firstMentionWords[x]))){
		    				 currentNode.texture=firstMentionWords[x];
		    			 }else{
			  				 //Check synonyms
		    				 String wsynonym =getSynonyms(dict,firstMentionWords[x],"texture");
		    				 if(!wsynonym.equals("")){
		    					 currentNode.texture=wsynonym;
			    			}else{
		    					 //Check Hypernyms 
		    					 String whypernym=getHypernyms(dict,firstMentionWords[x],"texture");
		    					 if(!whypernym.equals("")){
		    						 currentNode.texture=whypernym;
			    				 }			    				 
			    			}					   
		    			 }

		    			 //size
		    			 if(Arrays.asList(sizes).contains(firstMentionWords[x])){
		    				 currentNode.size=firstMentionWords[x];
		    			 }else{
			  				 //Check synonyms
		    				 String wsynonym =getSynonyms(dict,firstMentionWords[x],"size");
		    				 if(!wsynonym.equals("")){
		    					 currentNode.size=wsynonym;
			    			}else{
		    					 //Check Hypernyms 
		    					 String whypernym=getHypernyms(dict,firstMentionWords[x],"size");
		    					 if(!whypernym.equals("")){
		    						 currentNode.size=whypernym;
			    				 }			    				 
			    			}					   
		    			 
		    			 }

		    			 //type
		    			 if(Arrays.asList(types).contains(firstMentionWords[x])){
		    				 currentNode.type=firstMentionWords[x];
		    			 }else{
			  				 //Check synonyms
		    				 String wsynonym =getSynonyms(dict,firstMentionWords[x],"type");
		    				 if(!wsynonym.equals("")){
		    					 currentNode.type=wsynonym;
			    			}else{
		    					 //Check Hypernyms 
		    					 String whypernym=getHypernyms(dict,firstMentionWords[x],"type");
		    					 if(!whypernym.equals("")){
		    						 currentNode.type=whypernym;
			    				 }			    				 
			    			}					   
		    			 
		    			 }
	    		 	}
	    			 
	    			 //location relative to the room
	    			 if(Arrays.asList(roomLocations).contains(firstMentionWords[x])){
	    				 if(firstMentionWords[x].equals("corner")&&Arrays.asList(firstMentionWords).contains("left")){
		    				 currentNode.location="leftCorner";
		    				 currentNode.parent=roomNode;
		    				 roomNode.addChild(currentNode);
	    				 }
	    				 else if(firstMentionWords[x].equals("corner")&&Arrays.asList(firstMentionWords).contains("right")){
		    				 currentNode.location="rightCorner";
		    				 currentNode.parent=roomNode;
		    				 roomNode.addChild(currentNode);
	    				 }else{
		    				 currentNode.location=firstMentionWords[x];
		    				 currentNode.parent=roomNode;
		    				 roomNode.addChild(currentNode);
	    				 }
	    			 }
	    			 
	    			 //location relative to another object
	    			 if(Arrays.asList(locations).contains(firstMentionWords[x])&&(!(Arrays.asList(firstMentionWords).contains("facing")))){
	    				 
	    				 if(firstMentionWords[x].equals("next")){
	    					 currentNode.location="right";
	    				 }
	    				 currentNode.location=firstMentionWords[x];
	    				 
	    				 //logic to find parent name
	    				 for(int y=x+1;y<firstMentionWords.length;y++){
	    					 if(Arrays.asList(objects).contains(firstMentionWords[y])||Arrays.asList(pronouns).contains(firstMentionWords[y])){
	    	    				 String currentParentId=getObjectIdByNameAndSentence(firstMentionWords[y],fMentionSentence);
	    	    				 if(!currentParentId.equals("")){
	    	    					 VRMLNode currentParent=objectMap.get(currentParentId);
	    	    					 if(!(currentParent== null)){
	    		    				 currentNode.parent=currentParent; 
	    		    				 currentParent.addChild(currentNode);
	    	    					 }
	    	    				 }
	    					 }else{
				  				 //Check synonyms
			    				 String wsynonym =getSynonyms(dict,firstMentionWords[y],"object");
			    				 if(!wsynonym.equals("")){
		    	    				 String currentParentId=getObjectIdByNameAndSentence(wsynonym,fMentionSentence);
		    	    				 if(!currentParentId.equals("")){
		    	    					 VRMLNode currentParent=objectMap.get(currentParentId);	    	    					 
		    		    				 currentNode.parent=currentParent; 
		    		    				 currentParent.addChild(currentNode);
		    	    				 }
				    			}else{
			    					 //Check Hypernyms 
			    					 String whypernym=getHypernyms(dict,firstMentionWords[y],"object");
			    					 if(!whypernym.equals("")){
			    	    				 String currentParentId=getObjectIdByNameAndSentence(whypernym,fMentionSentence);
			    	    				 if(!currentParentId.equals("")){
			    	    					 VRMLNode currentParent=objectMap.get(currentParentId);
			    	    					 if(!(currentParent== null)){
				    		    				 currentNode.parent=currentParent; 
				    		    				 currentParent.addChild(currentNode);
			    	    					 }
			    	    				 } 
				    				 }			    				 
				    			}					   
			    			 }
	    				 }	    				 
	    			 }
	    		 }
		    	 
	    		 //Adding object to the object map
		    	 objectMap.put(currentId, currentNode);	    	 
	    	 }
	    	}
	     
	     System.out.println("Number of objects: " +objectCount);
	     output = "Number of objects: "+ objectCount;	 
	     
	     //Coreference Resolution end
	     	     
    	 //Dependency parser start
	     int sentenceNumber=0;
	     for(CoreMap sentence: sentences) {
	    	 
	    	 sentenceNumber++;
	    	 
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
				   
					 //Obtaining stem of the word					 
					 List<String> stems = stemmer.findStems(word1Name, null);
					 if(stems.size()>0){
						 word1Name= stems.get(0);
					 }
				   
				   String word1Index=word1[1];
				   
				   String[] word2 = split2[1].split("-");
				   String word2Name=word2[0];
				   
					 List<String> stems2 = stemmer.findStems(word2Name, null);
					 if(stems2.size()>0){
						 word2Name= stems2.get(0);
					 }
				   
				   String word2Index=word2[1].substring(0,word2[1].length()-1);
				   
				   //Checking for synonyms and hypernyms of object and replacing object names with synonyms and hypernyms
	    			 if(!(objectNames.contains(word1Name))&&!(Arrays.asList(pronouns).contains(word1Name))){
	    				 String synonym =getSynonyms(dict,word1Name,"object");
	    				 if(!(synonym.equals(""))){
	    					 word1Name=synonym;
	    				 }else{
	    					 String hypernym=getHypernyms(dict,word1Name,"object");
	    					 if(!hypernym.equals("")){
	    						 word1Name=hypernym;
	    					 }
	    				 }
	    			 }
	    			 
	    			 if(!(objectNames.contains(word2Name))&&!(Arrays.asList(pronouns).contains(word1Name))){
	    				 String synonym =getSynonyms(dict,word2Name,"object");
	    				 if(!(synonym.equals(""))){
	    					 word2Name=synonym;
	    				 }else{
	    					 String hypernym=getHypernyms(dict,word2Name,"object");
	    					 if(!hypernym.equals("")){
	    						 word2Name=hypernym;
	    					 }
	    				 }
	    			 }
				   				   
				   //start identifying attributes
				   String objectId1;
				   String objectId2;
				   
				   if(dependencyType.equals("amod")||dependencyType.equals("compound")||dependencyType.equals("acl:relcl")){
					   
					   //identifying room colour 
					   if((word1Name.equals("room"))||(word1Name.equals("wall"))||(word1Name.equals("walls"))){
						   //Identifying the colour of the object
						   if((Arrays.asList(colours).contains(word2Name))||(Arrays.asList(roomTextures).contains(word2Name))){
							   modifiedNode=objectMap.get("object0");
							   modifiedNode.colour=word2Name;
							   objectMap.put("object0", modifiedNode);							   
						   }else{
				  				 //Check synonyms
			    				 String synonym =getSynonyms(dict,word2Name,"colour");
			    				 if(!synonym.equals("")){
									   modifiedNode=objectMap.get("object0");
									   modifiedNode.colour=synonym;
									   objectMap.put("object0", modifiedNode);
				    			}else{
			    					 //Check Hypernyms 
			    					 String hypernym=getHypernyms(dict,word2Name,"colour");
			    					 if(!hypernym.equals("")){
										   modifiedNode=objectMap.get("object0");
										   modifiedNode.colour=hypernym;
										   objectMap.put("object0", modifiedNode);
				    				 }			    				 
				    			}
						   }
					   }					   
					   
					   //Identifying attributes of objects
					   if(objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name))){
						   
						   //Identifying the colour of the object
						   if(Arrays.asList(colours).contains(word2Name)){
							   //modifiedNode=objectMap.get(word1Name);
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   if(!(modifiedNode==null)){
									   modifiedNode.colour=word2Name;
									   objectMap.put(objectId1, modifiedNode);	
								   }
							   }
							  						   
						   }else{
				  				 //Check synonyms
			    				 String synonym =getSynonyms(dict,word2Name,"colour");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.colour=synonym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
				    			}else{
			    					 //Check Hypernyms 
			    					 String hypernym=getHypernyms(dict,word2Name,"colour");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   if(!(modifiedNode==null)){
												   modifiedNode.colour=hypernym;
												   objectMap.put(objectId1, modifiedNode);
											   }
										   }
				    				 }			    				 
				    			}
						   }
						   
						   //Identifying the texture of the object
						   if(Arrays.asList(textures).contains(word2Name)){
							   //modifiedNode=objectMap.get(word1Name);
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   if(!(modifiedNode==null)){
									   modifiedNode.texture=word2Name;
									   objectMap.put(objectId1, modifiedNode);
								   }
							   } 
						   }else{
				  				 //Check synonyms
			    				 String synonym =getSynonyms(dict,word2Name,"texture");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.texture=synonym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
				    			}else{
			    					 //Check Hypernyms 
			    					 String hypernym=getHypernyms(dict,word2Name,"texture");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   if(!(modifiedNode==null)){
												   modifiedNode.texture=hypernym;
												   objectMap.put(objectId1, modifiedNode);
											   }
										   }
				    				 }			    				 
				    			}
						   }
						   
						   //Identifying the size of the object
						   if(Arrays.asList(sizes).contains(word2Name)){							   
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   if(!(modifiedNode==null)){
									   modifiedNode.size=word2Name;
									   objectMap.put(objectId1, modifiedNode);
								   }
							   }
						   }else{
				  				 //Check synonyms 
			    				 String synonym =getSynonyms(dict,word2Name,"size");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.size=synonym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
				    			}else{
			    					 //Check Hypernyms
			    					 String hypernym=getHypernyms(dict,word2Name,"size");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   if(!(modifiedNode==null)){
												   modifiedNode.size=hypernym;
												   objectMap.put(objectId1, modifiedNode);
											   }
										   }
				    				 }			    				 
				    			}
						   
						   }
						   
						   //Identify the type of object
						   if(Arrays.asList(types).contains(word2Name)){					   
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   if(!(modifiedNode==null)){
									   modifiedNode.type=word2Name;
									   objectMap.put(objectId1, modifiedNode);
								   }
							   }
						   }else{
				  				 //Check synonyms to get the type name
			    				 String synonym =getSynonyms(dict,word2Name,"type");
			    				 if(!synonym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.type=synonym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
				    			}else{
			    					 //Check Hypernyms to get type name
			    					 String hypernym=getHypernyms(dict,word2Name,"type");
			    					 if(!hypernym.equals("")){
										   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   if(!(modifiedNode==null)){
												   modifiedNode.type=hypernym;
												   objectMap.put(objectId1, modifiedNode);
											   }
										   }
				    				 }			    				 
				    			}						   
						   }
					   }
				   }
				   
				   //Setting attributes when pronouns are used
				   if(dependencyType.equals("nsubj")&&objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name))){
					   //Identifying the colour of the object
					   if(Arrays.asList(colours).contains(word1Name)){
						   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   if(!(modifiedNode==null)){
								   modifiedNode.colour=word1Name;
								   objectMap.put(objectId1, modifiedNode);
							   }
						   }
					   }else{
			  				 //Check synonyms
		    				 String synonym =getSynonyms(dict,word1Name,"colour");
		    				 if(!synonym.equals("")){
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   if(!(modifiedNode==null)){
										   modifiedNode.colour=synonym;
										   objectMap.put(objectId1, modifiedNode);
									   }
								   }
			    			}else{
		    					 //Check Hypernyms 
		    					 String hypernym=getHypernyms(dict,word1Name,"colour");
		    					 if(!hypernym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.colour=hypernym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
			    				 }			    				 
			    			}
					   }
					   
					 //Identifying the size of the object
					   if(Arrays.asList(sizes).contains(word1Name)){							   
						   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   if(!(modifiedNode==null)){
								   modifiedNode.size=word1Name;
								   objectMap.put(objectId1, modifiedNode);
							   }
						   }
					   }else{
			  				 //Check synonyms 
		    				 String synonym =getSynonyms(dict,word1Name,"size");
		    				 if(!synonym.equals("")){
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   if(!(modifiedNode==null)){
										   modifiedNode.size=synonym;
										   objectMap.put(objectId1, modifiedNode);
									   }
								   }
			    			}else{
		    					 //Check Hypernyms
		    					 String hypernym=getHypernyms(dict,word1Name,"size");
		    					 if(!hypernym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.size=hypernym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
			    				 }			    				 
			    			}
					   
					   }
					   
					   //Identify the type of object
					   if(Arrays.asList(types).contains(word1Name)){					   
						   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   if(!(modifiedNode==null)){
								   modifiedNode.type=word1Name;
								   objectMap.put(objectId1, modifiedNode);
							   }
						   }
					   }else{
			  				 //Check synonyms to get the type name
		    				 String synonym =getSynonyms(dict,word1Name,"type");
		    				 if(!synonym.equals("")){
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   if(!(modifiedNode==null)){
										   modifiedNode.type=synonym;
										   objectMap.put(objectId1, modifiedNode);
									   }
								   }
			    			}else{
		    					 //Check Hypernyms to get type name
		    					 String hypernym=getHypernyms(dict,word1Name,"type");
		    					 if(!hypernym.equals("")){
									   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.type=hypernym;
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
				    			}			    				 
			    			}					   
					   }					   
				   }
				   
				   //end identifying attributes
				   
				   //Start obtaining count		   				  
				   if(dependencyType.equals("nummod")&& (objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))){
							   //modifiedNode=objectMap.get(word1Name);
					   		   long objNumber=1;
					   		   if(!(NumberNormalizer.wordToNumber(word2Name)==null)){
					   			   Number num= NumberNormalizer.wordToNumber(word2Name);
					   			   objNumber= num.longValue();
					   		   }
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   if(!(modifiedNode==null)){
									   modifiedNode.count=objNumber;
									   objectMap.put(objectId1, modifiedNode);
								   }
							   }
						   }
				   //End obtaining count
				   
				   //start identifying locations
				   
				   //location on
				   if(dependencyType.equals("nmod:on")){
					   if((objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))&&(objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name)))){
						   
						   //getting of the child node
						   
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   
							   //getting the parent node
							   
							   //parentNode=objectMap.get(word2Name);
							   objectId2=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
							   
							   if(!(objectId2.equals(""))){
								   parentNode=objectMap.get(objectId2);
								   if(!(modifiedNode==null) && !(parentNode==null)){
								   if(modifiedNode.parent==null){
									   modifiedNode.location="on"; 
									   modifiedNode.setParent(parentNode);
									   objectMap.put(objectId1, modifiedNode);
									   
									   parentNode.addChild(modifiedNode);
									   objectMap.put(objectId2, parentNode);
								   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
									 //Removing child	
									   String oldParentId=modifiedNode.parent.id;
									   VRMLNode oldParentNode=objectMap.get(oldParentId);
									   oldParentNode.removeChild(objectId1);
									   objectMap.put(oldParentId, oldParentNode);						   
									   
									   //Setting details 
									   modifiedNode.location="on"; 
									   modifiedNode.setParent(parentNode);
									   objectMap.put(objectId1, modifiedNode);
									   							   
									   //Adding child to new parent
									   parentNode.addChild(modifiedNode);
									   objectMap.put(objectId2, parentNode);
								   }
							   }
							   }
						   
						   }						   
					   }
				   }
				   //When draw command is used
				   if(dependencyType.equals("dobj")&&(word1Name.equals("draw")&&objectNames.contains(word2Name)||Arrays.asList(pronouns).contains(word2Name))){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,innerWord2[1].length()-1);
							   
							   //location on
							   if(innerDependencyType.equals("nmod:on")&&innerWord1Name.equals("draw")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node								 
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="on"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="on"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   //location above
							   else if(innerDependencyType.equals("nmod:above")&&innerWord1Name.equals("draw")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node								 
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="above"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="above"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   //location under
							   else if(innerDependencyType.equals("nmod:under")&&innerWord1Name.equals("draw")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node								 
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="under"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="under"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   //location behind
							   else if(innerDependencyType.equals("nmod:behind")&&innerWord1Name.equals("draw")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node								 
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="behind"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="behind"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   //location below
							   else if(innerDependencyType.equals("nmod:below")&&innerWord1Name.equals("draw")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node								 
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="below"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="below"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   //location front
							   else if(innerDependencyType.equals("nmod:in")&&innerWord1Name.equals("draw")&&innerWord2Name.equals("front")){

								   for(int k=j+1;k<dependencyArray.length;k++){
									   String[] innerSplit3= dependencyArray[k].split("\\(");
									   String innerDependencyType2 = innerSplit3[0];
									   
									   String[] innerSplit4 = innerSplit3[1].split(", ");
									   
									   String[] innerWord3 = innerSplit4[0].split("-");
									   String innerWord3Name=innerWord3[0];
									   
										 List<String> stems5 = stemmer.findStems(innerWord3Name, null);
										 if(stems5.size()>0){
											 innerWord3Name= stems5.get(0);
										 }
									   
									   String innerWord3Index=innerWord3[1];
									   
									   String[] innerWord4 = innerSplit4[1].split("-");
									   String innerWord4Name=innerWord4[0];
									   
										 List<String> stems6 = stemmer.findStems(innerWord4Name, null);
										 if(stems6.size()>0){
											 innerWord4Name= stems6.get(0);
										 }
									   
									   String innerWord4Index=innerWord4[1].substring(0,innerWord4[1].length()-1);
									   
									   if(innerDependencyType2.equals("nmod:of")&&innerWord3Name.equals("front")&&(objectNames.contains(innerWord4Name)||Arrays.asList(pronouns).contains(innerWord4Name))){
										   
										   //getting of the child node
										   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   
											   //getting the parent node
											   objectId2=getObjectIdByNameAndSentence(innerWord4Name,sentenceNumber);
											   if(!(objectId2.equals(""))){
												   parentNode=objectMap.get(objectId2);

												   if(!(modifiedNode==null)&&!(parentNode==null)){
													   if(modifiedNode.parent==null){
														   modifiedNode.location="front"; //Object added to the right of the parent node
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
														 //Removing child	
														   String oldParentId=modifiedNode.parent.id;
														   VRMLNode oldParentNode=objectMap.get(oldParentId);
														   oldParentNode.removeChild(objectId1);
														   objectMap.put(oldParentId, oldParentNode);						   
														   
														   //Setting details 
														   modifiedNode.location="front"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   							   
														   //Adding child to new parent
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }
												   }
											   }
										   }
										   break;
									   }
								   }
							   
							   }
						   }
				   }
				   //End identifying locations when draw command is used
				   
				   //location under
				   if(dependencyType.equals("nmod:under")){
					   if((objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))&&(objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name)))){
						   //getting of the child node
						   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   //getting the parent node
							   objectId2=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
							   if(!(objectId2.equals(""))){
								   parentNode=objectMap.get(objectId2);
								   
								   if(!(modifiedNode==null) && !(parentNode==null)){
									   if(modifiedNode.parent==null){
										   modifiedNode.location="under"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
										 //Removing child	
										   String oldParentId=modifiedNode.parent.id;
										   VRMLNode oldParentNode=objectMap.get(oldParentId);
										   oldParentNode.removeChild(objectId1);
										   objectMap.put(oldParentId, oldParentNode);						   
										   
										   //Setting details 
										   modifiedNode.location="under"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   							   
										   //Adding child to new parent
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }
								   }
							   }
						   }					   
					   }
				   }
				   
				   //location behind
				   if(dependencyType.equals("nmod:behind")){
					   if((objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))&&(objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name)))){
						   //getting of the child node
						   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   //getting the parent node
							   objectId2=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId2.equals(""))){
								   parentNode=objectMap.get(objectId2);
								   
		/*						   modifiedNode.location="behind"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);*/
								   if(!(modifiedNode==null) && !(parentNode==null)){
									   if(modifiedNode.parent==null){
										   modifiedNode.location="behind"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
										 //Removing child	
										   String oldParentId=modifiedNode.parent.id;
										   VRMLNode oldParentNode=objectMap.get(oldParentId);
										   oldParentNode.removeChild(objectId1);
										   objectMap.put(oldParentId, oldParentNode);						   
										   
										   //Setting details 
										   modifiedNode.location="behind"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   							   
										   //Adding child to new parent
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }
								   }
							   }
						   }
					   }
				   }
				   
				   //location front
				   if(dependencyType.equals("nmod:in")&&(objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&word2Name.equals("front")){
					   //if(objectNames.contains(word1Name)&&word2Name.equals("front")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("front")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node
								   
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
								   
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   
									   //parentNode=objectMap.get(innerWord2Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
		/*								   modifiedNode.location="front"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="front"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="front"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
						   }
						   
					   //}
				   }
				   
				   //location above
				   if(dependencyType.equals("nmod:above")){
					   if((objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))&&(objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name)))){
						   //getting of the child node
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   
							   //getting the parent node
							   //parentNode=objectMap.get(word2Name);
							   objectId2=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
							   if(!(objectId2.equals(""))){
								   parentNode=objectMap.get(objectId2);
								   
		/*						   modifiedNode.location="above"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);*/
								   if(!(modifiedNode==null) && !(parentNode==null)){
									   if(modifiedNode.parent==null){
										   modifiedNode.location="above"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
										 //Removing child	
										   String oldParentId=modifiedNode.parent.id;
										   VRMLNode oldParentNode=objectMap.get(oldParentId);
										   oldParentNode.removeChild(objectId1);
										   objectMap.put(oldParentId, oldParentNode);						   
										   
										   //Setting details 
										   modifiedNode.location="above"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   							   
										   //Adding child to new parent
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }
								   }
							   }
						   }
					   }
				   }
				   
				   //location below
				   if(dependencyType.equals("nmod:below")){
					   if((objectNames.contains(word1Name)||(Arrays.asList(pronouns).contains(word1Name)))&&(objectNames.contains(word2Name)||(Arrays.asList(pronouns).contains(word2Name)))){
						   //getting of the child node
						   //modifiedNode=objectMap.get(word1Name);
						   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
						   if(!(objectId1.equals(""))){
							   modifiedNode=objectMap.get(objectId1);
							   
							   //getting the parent node
							   //parentNode=objectMap.get(word2Name);
							   objectId2=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
							   if(!(objectId2.equals(""))){
								   parentNode=objectMap.get(objectId2);
								   
		/*						   modifiedNode.location="below"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(objectId2, parentNode);*/
								   if(!(modifiedNode==null) && !(parentNode==null)){
									   if(modifiedNode.parent==null){
										   modifiedNode.location="below"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
										 //Removing child	
										   String oldParentId=modifiedNode.parent.id;
										   VRMLNode oldParentNode=objectMap.get(oldParentId);
										   oldParentNode.removeChild(objectId1);
										   objectMap.put(oldParentId, oldParentNode);						   
										   
										   //Setting details 
										   modifiedNode.location="below"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   							   
										   //Adding child to new parent
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);
									   }
								   }
							   }
						   }
					   }
				   }
				   
				   //location left
					   if((objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&word2Name.equals("left")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("left")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord2Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
			/*							   modifiedNode.location="left"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
									   		}
							   			}
					   				}
							   
								   break;
							   }
						   }
						   
					   }
					   if(word1Name.equals("left")&&(objectNames.contains(word2Name)||Arrays.asList(pronouns).contains(word2Name))){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
								 
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if((objectNames.contains(innerWord1Name)||Arrays.asList(pronouns).contains(innerWord1Name))&&innerWord2Name.equals("left")){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord1Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord1Name,sentenceNumber);
										   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
		/*								   modifiedNode.location="left"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
							   else if(innerWord1Name.equals("left")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord2Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
		/*								   modifiedNode.location="left"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="left"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
						   }
						   
					   }
				   
				   //location right
					   if((objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&word2Name.equals("right")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("right")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word1Name);
								   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord2Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
		/*								   modifiedNode.location="right"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null) && !(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="right"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="right"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }
										   }
									   }
								   }
								   break;
							   }
						   }
						   
					   }
					   if(word1Name.equals("right")&&(objectNames.contains(word2Name)||Arrays.asList(pronouns).contains(word2Name))){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if((objectNames.contains(innerWord1Name)||Arrays.asList(pronouns).contains(innerWord1Name))&&innerWord2Name.equals("right")){
								   
								   //getting the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord1Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord1Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   if(!(modifiedNode==null)&&!(parentNode==null)){
											   modifiedNode.location="right"; 
											   modifiedNode.setParent(parentNode);
											   objectMap.put(objectId1, modifiedNode);
											   
											   parentNode.addChild(modifiedNode);
											   objectMap.put(objectId2, parentNode);
											}
									   }
								   }
								   break;
							   }
							   else if(innerWord1Name.equals("right")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
								   
								   //getting of the child node
								   //modifiedNode=objectMap.get(word2Name);
								   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
								   if(!(objectId1.equals(""))){
									   modifiedNode=objectMap.get(objectId1);
									   
									   //getting the parent node
									   //parentNode=objectMap.get(innerWord2Name);
									   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
									   if(!(objectId2.equals(""))){
										   parentNode=objectMap.get(objectId2);
										   
		/*								   modifiedNode.location="right"; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put(objectId2, parentNode);*/
										   if(!(modifiedNode==null)&&!(parentNode==null)){
											   if(modifiedNode.parent==null){
												   modifiedNode.location="right"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
											   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
												 //Removing child	
												   String oldParentId=modifiedNode.parent.id;
												   VRMLNode oldParentNode=objectMap.get(oldParentId);
												   oldParentNode.removeChild(objectId1);
												   objectMap.put(oldParentId, oldParentNode);						   
												   
												   //Setting details 
												   modifiedNode.location="right"; 
												   modifiedNode.setParent(parentNode);
												   objectMap.put(objectId1, modifiedNode);
												   							   
												   //Adding child to new parent
												   parentNode.addChild(modifiedNode);
												   objectMap.put(objectId2, parentNode);
												}
										   }
									   }
								   }
								   break;
							   }
						   }
						   
					   }
					   //When draw command is used for left and right
					   if(dependencyType.equals("dobj")&&(objectNames.contains(word2Name)||Arrays.asList(pronouns).contains(word2Name))&&word1Name.equals("draw")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerDependencyType.equals("nmod:to")&&innerWord1Name.equals("draw")&&(innerWord2Name.equals("right")||innerWord2Name.equals("left"))){
								   for(int k=j+1;k<dependencyArray.length;k++){
									   String[] innerSplit3= dependencyArray[k].split("\\(");
									   String innerDependencyType2 = innerSplit3[0];
									   
									   String[] innerSplit4 = innerSplit3[1].split(", ");
									   
									   String[] innerWord3 = innerSplit4[0].split("-");
									   String innerWord3Name=innerWord3[0];
									   
										 List<String> stems5 = stemmer.findStems(innerWord3Name, null);
										 if(stems5.size()>0){
											 innerWord3Name= stems5.get(0);
										 }
									   
									   String innerWord3Index=innerWord3[1];
									   
									   String[] innerWord4 = innerSplit4[1].split("-");
									   String innerWord4Name=innerWord4[0];
									   
										 List<String> stems6 = stemmer.findStems(innerWord4Name, null);
										 if(stems6.size()>0){
											 innerWord4Name= stems6.get(0);
										 }
									   
									   String innerWord4Index=innerWord4[1].substring(0,innerWord4[1].length()-1);
									   //Location right
									   if(innerDependencyType2.equals("nmod:of")&&innerWord3Name.equals("right")&&(objectNames.contains(innerWord4Name)||Arrays.asList(pronouns).contains(innerWord4Name))){
										   
										   //getting of the child node
										   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   
											   //getting the parent node
											   objectId2=getObjectIdByNameAndSentence(innerWord4Name,sentenceNumber);
											   if(!(objectId2.equals(""))){
												   parentNode=objectMap.get(objectId2);

												   if(!(modifiedNode==null)&&!(parentNode==null)){
													   if(modifiedNode.parent==null){
														   modifiedNode.location="right"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
														 //Removing child	
														   String oldParentId=modifiedNode.parent.id;
														   VRMLNode oldParentNode=objectMap.get(oldParentId);
														   oldParentNode.removeChild(objectId1);
														   objectMap.put(oldParentId, oldParentNode);						   
														   
														   //Setting details 
														   modifiedNode.location="right"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   							   
														   //Adding child to new parent
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }
												   }
											   }
										   }
										   break;
									   }
									   //location left
									   else if(innerDependencyType2.equals("nmod:of")&&innerWord3Name.equals("left")&&(objectNames.contains(innerWord4Name)||Arrays.asList(pronouns).contains(innerWord4Name))){
										   
										   //getting of the child node
										   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   
											   //getting the parent node
											   objectId2=getObjectIdByNameAndSentence(innerWord4Name,sentenceNumber);
											   if(!(objectId2.equals(""))){
												   parentNode=objectMap.get(objectId2);

												   if(!(modifiedNode==null)&&!(parentNode==null)){
													   if(modifiedNode.parent==null){
														   modifiedNode.location="left"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
														 //Removing child	
														   String oldParentId=modifiedNode.parent.id;
														   VRMLNode oldParentNode=objectMap.get(oldParentId);
														   oldParentNode.removeChild(objectId1);
														   objectMap.put(oldParentId, oldParentNode);						   
														   
														   //Setting details 
														   modifiedNode.location="left"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   							   
														   //Adding child to new parent
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }
												   }
											   }
										   }
										   break;
									   }
								   }
							   }
						   }
				   }
					   
					   //location next
					   if(dependencyType.equals("advmod")&&(objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&word2Name.equals("next")){
							   for(int j=i+1;j<dependencyArray.length;j++){
								   String[] innerSplit1= dependencyArray[j].split("\\(");
								   String innerDependencyType2 = innerSplit1[0];
								   
								   String[] innerSplit2 = innerSplit1[1].split(", ");
								   
								   String[] innerWord1 = innerSplit2[0].split("-");
								   String innerWord1Name=innerWord1[0];
								   
									 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
									 if(stems3.size()>0){
										 innerWord1Name= stems3.get(0);
									 }
								   
								   String innerWord1Index=innerWord1[1];
								   
								   String[] innerWord2 = innerSplit2[1].split("-");
								   String innerWord2Name=innerWord2[0];
								   
									 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
									 if(stems4.size()>0){
										 innerWord2Name= stems4.get(0);
									 }
								   
								   String innerWord2Index=innerWord2[1].substring(0,innerWord2[1].length()-1);
								   
								   if(innerWord1Name.equals("next")&&(objectNames.contains(innerWord2Name)||Arrays.asList(pronouns).contains(innerWord2Name))){
									   
									   //getting of the child node
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   
										   //getting the parent node
										   objectId2=getObjectIdByNameAndSentence(innerWord2Name,sentenceNumber);
										   if(!(objectId2.equals(""))){
											   parentNode=objectMap.get(objectId2);
											   
		/*									   modifiedNode.location="right"; //Object added to the right of the parent node by default
											   modifiedNode.setParent(parentNode);
											   objectMap.put(objectId1, modifiedNode);
											   
											   parentNode.addChild(modifiedNode);
											   objectMap.put(objectId2, parentNode);*/
											   if(!(modifiedNode==null)&&!(parentNode==null)){
												   if(modifiedNode.parent==null){
													   modifiedNode.location="right"; 
													   modifiedNode.setParent(parentNode);
													   objectMap.put(objectId1, modifiedNode);
													   
													   parentNode.addChild(modifiedNode);
													   objectMap.put(objectId2, parentNode);
												   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
													 //Removing child	
													   String oldParentId=modifiedNode.parent.id;
													   VRMLNode oldParentNode=objectMap.get(oldParentId);
													   oldParentNode.removeChild(objectId1);
													   objectMap.put(oldParentId, oldParentNode);						   
													   
													   //Setting details 
													   modifiedNode.location="right"; 
													   modifiedNode.setParent(parentNode);
													   objectMap.put(objectId1, modifiedNode);
													   							   
													   //Adding child to new parent
													   parentNode.addChild(modifiedNode);
													   objectMap.put(objectId2, parentNode);
												   }
											   }
										   }
									   }
									   break;
								   }
							   }
					   }
					  
					   if(dependencyType.equals("nsubj")&&(objectNames.contains(word2Name)||Arrays.asList(pronouns).contains(word2Name))&&word1Name.equals("is")){
						   for(int j=i+1;j<dependencyArray.length;j++){
							   String[] innerSplit1= dependencyArray[j].split("\\(");
							   String innerDependencyType = innerSplit1[0];
							   
							   String[] innerSplit2 = innerSplit1[1].split(", ");
							   
							   String[] innerWord1 = innerSplit2[0].split("-");
							   String innerWord1Name=innerWord1[0];
							   
								 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
								 if(stems3.size()>0){
									 innerWord1Name= stems3.get(0);
								 }
							   
							   String innerWord1Index=innerWord1[1];
							   
							   String[] innerWord2 = innerSplit2[1].split("-");
							   String innerWord2Name=innerWord2[0];
							   
								 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
								 if(stems4.size()>0){
									 innerWord2Name= stems4.get(0);
								 }
							   
							   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
							   
							   if(innerWord1Name.equals("is")&&innerWord2Name.equals("next")){
								   for(int k=j+1;k<dependencyArray.length;k++){
									   String[] innerSplit3= dependencyArray[k].split("\\(");
									   String innerDependencyType2 = innerSplit3[0];
									   
									   String[] innerSplit4 = innerSplit3[1].split(", ");
									   
									   String[] innerWord3 = innerSplit4[0].split("-");
									   String innerWord3Name=innerWord3[0];
									   
										 List<String> stems5 = stemmer.findStems(innerWord3Name, null);
										 if(stems5.size()>0){
											 innerWord3Name= stems5.get(0);
										 }
									   
									   String innerWord3Index=innerWord3[1];
									   
									   String[] innerWord4 = innerSplit4[1].split("-");
									   String innerWord4Name=innerWord4[0];
									   
										 List<String> stems6 = stemmer.findStems(innerWord4Name, null);
										 if(stems6.size()>0){
											 innerWord4Name= stems6.get(0);
										 }
									   
									   String innerWord4Index=innerWord4[1].substring(0,innerWord4[1].length()-1);
									   
									   if(innerDependencyType2.equals("nmod:to")&&innerWord3Name.equals("next")&&(objectNames.contains(innerWord4Name)||Arrays.asList(pronouns).contains(innerWord4Name))){
										   
										   //getting of the child node
										   objectId1=getObjectIdByNameAndSentence(word2Name,sentenceNumber);
										   if(!(objectId1.equals(""))){
											   modifiedNode=objectMap.get(objectId1);
											   
											   //getting the parent node
											   objectId2=getObjectIdByNameAndSentence(innerWord4Name,sentenceNumber);
											   if(!(objectId2.equals(""))){
												   parentNode=objectMap.get(objectId2);

												   if(!(modifiedNode==null)&&!(parentNode==null)){
													   if(modifiedNode.parent==null){
														   modifiedNode.location="right"; //Object added to the right of the parent node
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals(objectId2)))){
														 //Removing child	
														   String oldParentId=modifiedNode.parent.id;
														   VRMLNode oldParentNode=objectMap.get(oldParentId);
														   oldParentNode.removeChild(objectId1);
														   objectMap.put(oldParentId, oldParentNode);						   
														   
														   //Setting details 
														   modifiedNode.location="right"; 
														   modifiedNode.setParent(parentNode);
														   objectMap.put(objectId1, modifiedNode);
														   							   
														   //Adding child to new parent
														   parentNode.addChild(modifiedNode);
														   objectMap.put(objectId2, parentNode);
													   }
												   }
											   }
										   }
										   break;
									   }
								   }
							   }
						   }
				   }					   
					   //end identifying locations			   
					   
					   //Start identifying location relative to room					   
					   if(dependencyType.equals("nmod:in")&&(objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&Arrays.asList(roomLocations).contains(word2Name)){

							  
						   if(word2Name.equals("corner")){
							   for(int a=0;a<dependencyArray.length;a++){								   
									   String[] newSplit1= dependencyArray[a].split("\\(");
									   String newdependencyType=newSplit1[0];
									   
									   if(newdependencyType.equals("amod")){
									   String[] newSplit2 = newSplit1[1].split(", ");
									   
									   String[] newWord1 = newSplit2[0].split("-");
									   String newWord1Name=newWord1[0];
									   
										 List<String> newStems3 = stemmer.findStems(newWord1Name, null);
										 if(newStems3.size()>0){
											 newWord1Name= newStems3.get(0);
										 }
									   
									   String newWord1Index=newWord1[1];
									   
									   String[] newWord2 = newSplit2[1].split("-");
									   String newWord2Name=newWord2[0];
									   
										 List<String> newStems4 = stemmer.findStems(newWord2Name, null);
										 if(newStems4.size()>0){
											 newWord2Name= newStems4.get(0);
										 }
									   
									   String newWord2Index=newWord2[1].substring(0,newWord2[1].length()-1);
									   
									   if(newWord1Name.equals("corner")&&newWord2Name.equals("left")){
										   word2Name="leftCorner";
									   }
									   else if(newWord1Name.equals("corner")&&newWord2Name.equals("right")){
										   word2Name="rightCorner";
									   }
								   }
							   }
						   }
						   
						   		//getting of the child node
							   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
							   if(!(objectId1.equals(""))){
								   modifiedNode=objectMap.get(objectId1);
								   
								   //getting the parent node
								   parentNode=objectMap.get("object0");
								   
	/*							   modifiedNode.location=word2Name; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(objectId1, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put("object0", parentNode);*/
								   if(!(modifiedNode==null)&&!(parentNode==null)){
									   if(modifiedNode.parent==null){
										   modifiedNode.location=word2Name; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   
										   parentNode.addChild(modifiedNode);
										   objectMap.put("object0", parentNode);
									   }else if((!(modifiedNode.parent==null))&&(!((modifiedNode.parent.id).equals("object0")))){
										 //Removing child	
										   String oldParentId=modifiedNode.parent.id;
										   VRMLNode oldParentNode=objectMap.get(oldParentId);
										   oldParentNode.removeChild(objectId1);
										   objectMap.put(oldParentId, oldParentNode);						   
										   
										   //Setting details 
										   modifiedNode.location=word2Name; 
										   modifiedNode.setParent(parentNode);
										   objectMap.put(objectId1, modifiedNode);
										   							   
										   //Adding child to new parent
										   parentNode.addChild(modifiedNode);
										   objectMap.put("object0", parentNode);
									   }
								   }
							   }
					   }
					   
					   
					   //End identifying location relative to room
					   
			
					   //Start identifying orientation
					   if(dependencyType.equals("acl")&&(objectNames.contains(word1Name)||Arrays.asList(pronouns).contains(word1Name))&&word2Name.equals("facing")){
						  
							   for(int j=i+1;j<dependencyArray.length;j++){
								   String[] innerSplit1= dependencyArray[j].split("\\(");
								   String innerDependencyType = innerSplit1[0];
								   
								   String[] innerSplit2 = innerSplit1[1].split(", ");
								   
								   String[] innerWord1 = innerSplit2[0].split("-");
								   String innerWord1Name=innerWord1[0];
								   
									 List<String> stems3 = stemmer.findStems(innerWord1Name, null);
									 if(stems3.size()>0){
										 innerWord1Name= stems3.get(0);
									 }
								   
								   String innerWord1Index=innerWord1[1];
								   
								   String[] innerWord2 = innerSplit2[1].split("-");
								   String innerWord2Name=innerWord2[0];
								   
									 List<String> stems4 = stemmer.findStems(innerWord2Name, null);
									 if(stems4.size()>0){
										 innerWord2Name= stems4.get(0);
									 }
								   
								   String innerWord2Index=innerWord2[1].substring(0,word2[1].length()-1);
								   
								   if(innerWord1Name.equals("facing")&&Arrays.asList(orientations).contains(innerWord2Name)){
									   
									   //getting the node
									   
									   objectId1=getObjectIdByNameAndSentence(word1Name,sentenceNumber);
									   if(!(objectId1.equals(""))){
										   modifiedNode=objectMap.get(objectId1);
										   if(!(modifiedNode==null)){
											   modifiedNode.orientation=innerWord2Name; 
											   objectMap.put(objectId1, modifiedNode);
										   }
									   }
									   break;
								   }
							   
							   
						   }
					   }
					   //End identifying orientation
					   
			   }//Dependency parser end
			   
		     }//Stanford CoreNLP end
	     
	     //Adding the objects to the tree
			for (Entry<String, VRMLNode> obj : objectMap.entrySet()) {
			    String key = obj.getKey();
			    VRMLNode value = obj.getValue();
			    
			    if((value.parent==null)&&!(value.name.equals("room"))){
			    	value.parent=roomNode;
			    	roomNode.addChild(value);
			    } 			    
			    tree.nodes.add(value);
			    
			    System.out.println("Printing VRML node details in Tagger");
			    System.out.println("ID: "+value.id);
			    System.out.println("Name: "+value.name);
			    System.out.println("Colour: "+value.colour);
			    System.out.println("Texture: "+value.texture);
			    if(!(value.parent==null)&& !(value.parent.name.equals(""))){
			    	System.out.println("Parent: "+value.parent.name);
			    }
			    System.out.println("Count: "+value.count);
			    
			}
			
			dict.close();
			objectIdentifier.defineObject(tree);
			return output;
	}
	
    //Method to get object Id by defining name and sentence
    public String getObjectIdByNameAndSentence(String objName, int sentenceNum){   	
    	String objectId="";		
    	for (Entry<String, ArrayList<ObjectMention>> obj : objectMentionsMap.entrySet()) {
		    String key = obj.getKey();
		    ArrayList<ObjectMention> objMentions = obj.getValue();
		    for(ObjectMention objMention:objMentions){
		    	if((objMention.name.equals(objName))&&(objMention.sentence==sentenceNum)){
		    		objectId=key;
		    		break;
		    	}
		    }
		}   	
    	return objectId;
    }
    
    //Method to get object Id by defining only name
    public String getObjectIdByName(String objName){   	
    	String objectId="";		
    	for (Entry<String, ArrayList<ObjectMention>> obj : objectMentionsMap.entrySet()) {
		    String key = obj.getKey();
		    ArrayList<ObjectMention> objMentions = obj.getValue();
		    for(ObjectMention objMention:objMentions){
		    	if((objMention.name.equals(objName))){
		    		objectId=key;
		    		break;
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
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		else if(wordType.equals("colour")){
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		
		//Get synonym of textures
		else if(wordType.equals("texture")){
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
			IWordID wordID = idxWord . getWordIDs ().get (0) ; // 1st meaning
			IWord word = dict . getWord ( wordID );
			ISynset synset = word . getSynset ();
			
			// iterate over words associated with the synset
			for( IWord w : synset . getWords ()){
				//System .out . println (w. getLemma ());
				if(Arrays.asList(textures).contains(w.getLemma())){
					System.out.println("Synonym for texture present!");
					synonymName=w.getLemma();
					break;
				}
			}
			}
		}
		
		//Get synonym of sizes
		else if(wordType.equals("size")){
			
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		else if(wordType.equals("type")){
			// look up first sense of the word 
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE);
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		else if( wordType.equals("colour")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		
		//Get hypernym of texture
		else if( wordType.equals("texture")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
						if(Arrays.asList(textures).contains(currentWord)){
							System.out.println("Hypernym of texture present!");
							hypernymName=currentWord;
							break;
						}
					 }
				}
			}
		}
		
		//Get hypernym of size
		else if( wordType.equals("size")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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
		else if( wordType.equals("type")){
			// get the synset
			IIndexWord idxWord = dict . getIndexWord (name, POS.ADJECTIVE );
			if((!(idxWord==null))&&(idxWord . getWordIDs ().size()>0)){
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

