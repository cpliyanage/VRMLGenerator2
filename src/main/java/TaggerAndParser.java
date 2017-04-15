import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class TaggerAndParser {
	
	public String tagContent(String input) throws IOException{
		
		ObjectIdentifier objectIdentifier = new ObjectIdentifier();
		String output="";
		
		String locations[]={"left","right","above","below","front","behind","top", "under","on"};
		String[] objects = { "table", "chair", "box","cone","sphere", "cylinder"};
		//String[] attributes={"red", "green", "blue","brown","black", "white","small","regular","large","round","square"};
		String[] colours= {"red", "green", "blue","brown","black", "white","small"};
		String[] sizes = {"small","regular","large"};
		String[] types= {"round","square"};
		
		ArrayList<String> objectNames = new ArrayList<String>();
		HashMap<String, VRMLObject> objectMap= new HashMap<String, VRMLObject>();
		
		VRMLObject currentElement;
		VRMLObject modifiedElement;
		
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
				currentElement = new VRMLObject();
				currentElement.name= current[0];
				objectMap.put(current[0], currentElement);
			}

		}
		System.out.println(tagged.toString());
		System.out.println("Number of objects: " + objectNames.size());
		output = Integer.toString(objectNames.size());
		
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
	    	 
		       System.out.println("Basic dependencies of the sentence");
		       
		       SemanticGraph basicDependencies=sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		       String basicDependenciesList = basicDependencies.toString(SemanticGraph.OutputFormat.LIST);
			   System.out.println(basicDependenciesList);
			   
			   String[] dependencyArray = basicDependenciesList.split("\\n"); 
			   String dependencyType="";
			   
			   for (String dependency:dependencyArray){
				   
				   String[] split1= dependency.split("\\(");
				   dependencyType = split1[0];
				   
				   String[] split2 = split1[1].split(", ");
				   
				   String[] word1 = split2[0].split("-");
				   String word1Name=word1[0];
				   String word1Index=word1[1];
				   
				   String[] word2 = split2[1].split("-");
				   String word2Name=word2[0];
				   String word2Index=word2[1].substring(0,word2[1].length()-1);
				   
				   if(dependencyType.equals("amod")){
					   if(objectNames.contains(word1Name)){
						   if(Arrays.asList(colours).contains(word2Name)){
							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.colour=word2Name;
							   objectMap.put(word1Name, modifiedElement);
							   System.out.println(modifiedElement.name);
							   System.out.println("modified");
							   System.out.println(modifiedElement.name);
							   System.out.println(modifiedElement.colour);

						   }
						   if(Arrays.asList(sizes).contains(word2Name)){
							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.size=word2Name;
							   objectMap.put(word1Name, modifiedElement);
						   }
						   if(Arrays.asList(types).contains(word2Name)){
							   modifiedElement=objectMap.get(word1Name);
							   modifiedElement.type=word2Name;
							   objectMap.put(word1Name, modifiedElement);
						   }
					   }
				   }

			   }
		     }
	     
		//Dependency parser end
	     
			for (HashMap.Entry<String, VRMLObject> obj : objectMap.entrySet()) {
			    String key = obj.getKey();
			    VRMLObject value = obj.getValue();
			    System.out.println("Printing Map");
			    System.out.println(value.name);
			    System.out.println(value.colour);
			}
			objectIdentifier.defineObject(objectMap);
		 return output;
	}		
			
		}

