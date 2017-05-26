import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class TaggerAndParser {
	
	public String tagContent(String input) throws IOException{
		
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		String output="";
		
		//String locations[]={"left","right","above","below","front","behind","top", "under","on"};
		String[] objects = { "table", "chair", "box","cone","sphere", "cylinder", "sofa", "bookshelf", "lamp", "bed"};
		String[] colours= {"red", "green", "blue","brown","black", "white","small"};
		String[] sizes = {"small","regular","large"};
		String[] types= {"round","square", "ceiling"};
		
		ArrayList<String> objectNames = new ArrayList<String>();
		//ArrayList<VRMLNode> objectList = new ArrayList<VRMLNode>();
		HashMap<String, VRMLNode> objectMap= new HashMap<String, VRMLNode>();
		Tree tree=new Tree();
		
/*		VRMLObject currentElement;
		VRMLObject modifiedElement;*/
		
		VRMLNode currentNode;
		VRMLNode modifiedNode;
		VRMLNode parentNode;
		
		//POS tagger start
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
/*				currentElement = new VRMLObject();
				currentElement.name= current[0];
				objectMap.put(current[0], currentElement);*/
				
				currentNode = new VRMLNode(current[0]);
				objectMap.put(current[0], currentNode);
			}

		}
		System.out.println(tagged.toString());
		System.out.println("Number of objects: " + objectNames.size());
		output = "Number of objects: "+Integer.toString(objectNames.size());
		
		//Dependency parser start
		 Properties props = new Properties();
	     props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse");
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	     
	     Annotation document = new Annotation(input);
	     pipeline.annotate(document);
	     
	     // these are all the sentences in this document
	     // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	     List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	     
	     for(CoreMap sentence: sentences) {
  
		     //Basic dependencies of the sentence
	    	 
		       System.out.println("Enhanced dependencies of the sentence");
		       
		       SemanticGraph enhancedDependencies=sentence.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
		       String enhancedDependenciesList = enhancedDependencies.toString(SemanticGraph.OutputFormat.LIST);
			   System.out.println(enhancedDependenciesList);
			   
			   String[] dependencyArray = enhancedDependenciesList.split("\\n"); 
			   String dependencyType="";
			   
			   for (int i=0;i<dependencyArray.length;i++){
				   
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
				   
				   if(dependencyType.equals("amod")||dependencyType.equals("compound")){
					   if(objectNames.contains(word1Name)){
						   if(Arrays.asList(colours).contains(word2Name)){
/*							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.colour=word2Name;
							   objectMap.put(word1Name, modifiedElement);*/
							   
							   modifiedNode=objectMap.get(word1Name);
							   modifiedNode.colour=word2Name;
							   objectMap.put(word1Name, modifiedNode);
							   
						   }
						   if(Arrays.asList(sizes).contains(word2Name)){
/*							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.size=word2Name;
							   objectMap.put(word1Name, modifiedElement);*/
							   
							   modifiedNode=objectMap.get(word1Name);
							   modifiedNode.size=word2Name;
							   objectMap.put(word1Name, modifiedNode);
						   }
						   if(Arrays.asList(types).contains(word2Name)){
/*							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.type=word2Name;
							   objectMap.put(word1Name, modifiedElement);*/
							   
							   modifiedNode=objectMap.get(word1Name);
							   modifiedNode.type=word2Name;
							   objectMap.put(word1Name, modifiedNode);
						   }
					   }
				   }
				   //end identifying attributes
				   
				   //start identifying locations
				   
				   //location on
				   if(dependencyType.equals("nmod:on")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   modifiedNode=objectMap.get(word1Name);
						   //getting the parent node
						   parentNode=objectMap.get(word2Name);
						   
						   modifiedNode.location="on"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(word1Name, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(word2Name, parentNode);
						   
					   }
				   }
				   
				   //location under
				   if(dependencyType.equals("nmod:under")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   modifiedNode=objectMap.get(word1Name);
						   //getting the parent node
						   parentNode=objectMap.get(word2Name);
						   
						   modifiedNode.location="under"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(word1Name, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(word2Name, parentNode);						   
					   }
				   }
				   
				   //location behind
				   if(dependencyType.equals("nmod:behind")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   modifiedNode=objectMap.get(word1Name);
						   //getting the parent node
						   parentNode=objectMap.get(word2Name);
						   
						   modifiedNode.location="behind"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(word1Name, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(word2Name, parentNode);						   
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
								   modifiedNode=objectMap.get(word1Name);
								   //getting the parent node
								   parentNode=objectMap.get(innerWord2Name);
								   
								   modifiedNode.location="front"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(word1Name, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(innerWord2Name, parentNode);
								   break;
							   }
						   }
						   
					   }
				   }
				   
				   //location above
				   if(dependencyType.equals("nmod:above")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   modifiedNode=objectMap.get(word1Name);
						   //getting the parent node
						   parentNode=objectMap.get(word2Name);
						   
						   modifiedNode.location="above"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(word1Name, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(word2Name, parentNode);						   
					   }
				   }
				   
				   //location below
				   if(dependencyType.equals("nmod:below")){
					   if(objectNames.contains(word1Name)&&objectNames.contains(word2Name)){
						   //getting of the child node
						   modifiedNode=objectMap.get(word1Name);
						   //getting the parent node
						   parentNode=objectMap.get(word2Name);
						   
						   modifiedNode.location="below"; 
						   modifiedNode.setParent(parentNode);
						   objectMap.put(word1Name, modifiedNode);
						   
						   parentNode.addChild(modifiedNode);
						   objectMap.put(word2Name, parentNode);						   
					   }
				   }
				   
				   //location left
				   /*if(dependencyType.equals("acl:relcl")){*/
					   if(objectNames.contains(word1Name)&&word2Name.equals("left")){
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
							   
							   if(innerWord1Name.equals("left")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   modifiedNode=objectMap.get(word1Name);
								   //getting the parent node
								   parentNode=objectMap.get(innerWord2Name);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(word1Name, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(innerWord2Name, parentNode);
								   break;
							   }
						   }
						   
					   }else if(word1Name.equals("left")&&objectNames.contains(word2Name)){
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
							   
							   if(innerWord2Name.equals("left")&&objectNames.contains(innerWord1Name)){
								   
								   //getting of the child node
								   modifiedNode=objectMap.get(word2Name);
								   //getting the parent node
								   parentNode=objectMap.get(innerWord1Name);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(word2Name, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(innerWord1Name, parentNode);
								   break;
							   }
						   }
						   
					   }
				   /*}*/
				   
				   //location right
				   /*if(dependencyType.equals("compound")){*/
					   if(word1Name.equals("right")&&objectNames.contains(word2Name)){
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
							   
							   if(innerWord1Name.equals("right")&&objectNames.contains(innerWord2Name)){
								   
								   //getting of the child node
								   modifiedNode=objectMap.get(word2Name);
								   //getting the parent node
								   parentNode=objectMap.get(innerWord2Name);
								   
								   modifiedNode.location="left"; 
								   modifiedNode.setParent(parentNode);
								   objectMap.put(word2Name, modifiedNode);
								   
								   parentNode.addChild(modifiedNode);
								   objectMap.put(innerWord2Name, parentNode);
								   break;
							   }
						   }
						   
					   }
				   /*}*/
				   //end identifying locations
			   }
		     }
	     
		//Dependency parser end
	     
			for (Entry<String, VRMLNode> obj : objectMap.entrySet()) {
			    String key = obj.getKey();
			    VRMLNode value = obj.getValue();
			    System.out.println("Printing Map");
			    System.out.println(value.name);
			    System.out.println(value.colour);
			    tree.nodes.add(value);
			}
			objectIdentifier.defineObject(tree);
			return output;
	}
				
		}

