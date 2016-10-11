package traffic.diy;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
import traffic.load.Tag;
import traffic.load.TrafficException;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.signal.SignalFace;
import traffic.util.State;
import traffic.util.TrafficDirection;

/**
 * Read an intersection description file and build an intersection from the data
 * it contains.
 *
 */
public class MyIntersectionLoader {
	/**
	 * Constructor for class.
	 * 
	 * @param br
	 *            where to read data from.
	 */
	
	private BufferedReader br;
	private String file;
	private Intersection myIntersection;
	private ArrayList<TrafficStream> streams = new ArrayList<TrafficStream>();
	
	public MyIntersectionLoader(BufferedReader br) {
		
		
		this.br = br;
		
	}
	
	
	


	private String getTagValues(String fileString, String openingTag, String closingTag) {
		/**Takes a file in the form of a string and two "opening and closing" tags. The method will the entire
		 * string of values that fall between the two tags. Uses pattern matching instead of the typical method of using
		 * a scanner so that the tags do not have to be in order in the file 
		 */
		Pattern tagREGEX = Pattern.compile(openingTag + "(.+?)" + closingTag);
	    String valuesBetweenTags = "";
	    final Matcher matcher = tagREGEX.matcher(fileString); //Using pattern matching to find entire string between tags
	    while (matcher.find()) {
	        valuesBetweenTags = valuesBetweenTags + matcher.group(1); //In the case of multiple strings to be added
	    }
	    return valuesBetweenTags;
	}
	
	private void intersectionInformation(){
		/** Extracts the information from the file required to initially construct the intersection, namely the
		 * intersection name and description. Calls getTagValues to find the strings between INTERSECTION tags.
		 * Returns void, instead updates myIntersection with the information.
		 */
		String intersectInfo;
		intersectInfo = getTagValues(file, Tag.INTERSECTION, Tag.END_INTERSECTION);
		String[] intersectData = intersectInfo.split("\t");	//Breaking up the string into the data separated by tabs
		for (int i = 0; i < intersectData.length; i++) {
			intersectData[i] = intersectData[i].replaceAll("\\$", ""); //Removing the "$" that was used to substitute a new line - See buildIntersection()
		}
		
		String intersectName = intersectData[0];
		String intersectDescript = intersectData[1];
		myIntersection = new Intersection(intersectName, intersectDescript); //Creating myIntersection with the name and description that was found
		
		
	}
	
	private void trafficStreamInformation(){
		/** Extracts the information from the file required to add traffic streams to the intersection, by extracting
		 *  the stream name and description. Calls getTagValues to find the strings between TRAFFIC STREAM tags.
		 * Returns void, instead updates the list streams with the information.
		 */
		String trafficInfo;
		trafficInfo = getTagValues(file, Tag.TRAFFIC_STREAMS, Tag.END_TRAFFIC_STREAMS);
		String[] trafficData = trafficInfo.split("\\$");
		
		for (int i = 0; i < trafficData.length; i++) {
			if (trafficData[i].isEmpty() == false ){ //In the case of an empty data point that would throw an index error
				String[] currentData = trafficData[i].split("\t"); //Breaking up the string into the data separated by tabs
				currentData[0] = currentData[0].replaceAll("\\s", ""); //Removing the "$" that was used to substitute a new line
				
				TrafficStream tempStream = new TrafficStream(currentData[0], currentData[1]);
				streams.add(tempStream); //Creates a list of the streams	
			}
			
		}
		
	}
	
	private void phaseAndPhasePlanInformation(){
		/** Extracts the information from the file required to add phase plans to the intersection. To do this it calls
		 * getTagValues to find the strings between the PHASEPLAN tags. It then loops over the returned string, again
		 * calling getTagValues to find strings between any PHASE tags, so that multiple phase plans will be accepted.
		 * Returns void, instead updates myIntersection with the information.
		 */
		String phasePlanInfo;
		phasePlanInfo = getTagValues(file, Tag.PHASEPLAN, Tag.END_PHASEPLAN);//Narrowing down the string we are looking at
		
		PretimedPhasePlan newPhases;
		

		while(getTagValues(phasePlanInfo, Tag.PHASES, Tag.END_PHASES) != ""){ //Looping over for the case of multiple phase plans
			
		newPhases = new PretimedPhasePlan();
		String phaseInfo;
		phaseInfo = getTagValues(phasePlanInfo, Tag.PHASES, Tag.END_PHASES);
		String[] phaseData = phaseInfo.split("\\$"); //Breaking up the string into the data separated by new lines (Which were replaced by "$")

		

		for (int i = 0; i < phaseData.length; i++) {
			if (phaseData[i].isEmpty() == false){ //In the case of an empty data point that would throw an index error
				String[] currentData = phaseData[i].split("\\t"); //Breaking up the string into the data separated by tabs		
				int minTime = Integer.valueOf(currentData[3]); //Converting the string of time to an integer
				
				Phase  newPhase = new Phase(currentData[0], currentData[1]);
				
				for (int j = 0; j < streams.size(); j++) {
					State currentState;
					switch (currentData[2].charAt(j)){ //Matching the letter with the correct state
					case 'R': currentState = State.RED;
								break;		
					case 'G': currentState = State.GREEN;
								break;
					case 'Y': currentState = State.YELLOW;
								break;
					default : currentState = State.OFF;
								break;			
					}
					
					newPhase.addStream(streams.get(j), currentState);
					newPhase.setMinGreenInterval(minTime);
				}

				newPhases.add(newPhase);
			}
		}
		myIntersection.addPlan(newPhases); //adding this iteration of the phase plan
		newPhases = new PretimedPhasePlan();
		phasePlanInfo = phasePlanInfo.replaceFirst(Tag.PHASES, "<VOIDPhases>"); //Marking the last tags as void so getTagValue skips them
		phasePlanInfo = phasePlanInfo.replaceFirst(Tag.END_PHASES, "<VOID\\Phases>");
		}
	}
	
	private void signalFaceInformation(){
		/** Extracts the information from the file required to add signal faces to the traffic streams, by extracting
		 *  the relevant information. Calls getTagValues to find the strings between SIGNAL_FACE tags. This information
		 *  is then looped over the list of traffic streams so that the corresponding signal face can be added.
		 * Returns void, instead updates myIntersection with the information.
		 */
		
		String signalInfo;
		signalInfo = getTagValues(file, Tag.SIGNAL_FACES, Tag.END_SIGNAL_FACES);
		String[] signalData = signalInfo.split("\\$"); //Removing the "$" that was used to substitute a new line


		for (int i = 0; i < signalData.length; i++) {
			if (signalData[i].isEmpty() == false ){
				String[] currentData = signalData[i].split("\\t"); //Breaking up the string into the data separated by tabs

				
				
				TrafficDirection location = TrafficDirection.NORTH; //DEFAULT
				TrafficDirection orientation = TrafficDirection.NORTH; //DEFAULT
				try {
					location = TrafficDirection.directionFor(currentData[0]);
					orientation = TrafficDirection.directionFor(currentData[1]);
				} catch (TrafficException e) {
					e.printStackTrace();
				}
				
				
				int kind;
				
				switch(currentData[2]){ //Setting the signal face to the appropriate state
				case "STANDARD" : kind = SignalFace.STANDARD;
								break;
				case "LEFT_ARROW" : kind = SignalFace.LEFT_ARROW;
								break;
				case "RIGHT_ARROW" : kind = SignalFace.RIGHT_ARROW;
								break;
				default : kind = SignalFace.RIGHT_ARROW;
								break;
				}

				
				
			SignalFace newSignalFace = new SignalFace(location, orientation, kind);
			myIntersection.addSignalFace(newSignalFace);
			currentData[3] = currentData[3].replaceAll("\\s", "");
			
			for (int j = 0; j < streams.size(); j++) { //Iterating over the traffic streams to match the appropriate signal face

				if (streams.get(j).getname().equals(currentData[3])){
					streams.get(j).addObserver(newSignalFace);

					}
				
				}
			}
		
		}
	}
	
	public Intersection buildIntersection(){
		/**Uses a scanner to convert the input file into a string, so that it can be easily passed into getTagValues.
		 * Due to the pattern matching in getTagValues, all next lines are replaces with "$" symbols to keep consistency.
		 * Then calls the appropriate methods to construct myIntersection before returning it. Returns null if a problem arises. */
		
		try{

		Scanner myScanner = new Scanner(br); //transforming the file into a continuous string, so it can be passed into getTagValues()
		while (myScanner.hasNext()){
			file = file + myScanner.nextLine() + "$"; // $ used to show where the new line is, without interfering with my regex for finding tags
		}
		myScanner.close();
		
		
		intersectionInformation();
		trafficStreamInformation();
		phaseAndPhasePlanInformation();
		signalFaceInformation();
		
		return myIntersection;
		
		}
		catch(Exception e){
			return null; //If a problem arises, will return null instead of simply crashing, so that the GUI can helpfully display that there is a problem
		}
		}
	}

	

