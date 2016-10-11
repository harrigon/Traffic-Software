package traffic.diy;

import javax.swing.JOptionPane;

import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
import traffic.misc.Detector;
import traffic.misc.RandomDetector;
import traffic.phaseplan.FullyActuatedPhasePlan;
import traffic.phaseplan.PhasePlan;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.signal.SignalFace;
import traffic.util.State;
import traffic.util.TrafficDirection;

/**
 * "Manually" create an intersection by assembling the various elements. Use the
 * classes provided in the traffic packages to construct an intersection which
 * can be displayed in the monitor.
 *
 */
public class ModelIntersection {

	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more pre-timed phase plans.
	 * @return the intersection I made.
	 */
	public static Intersection preTimedIntersection() {
		/**JOptionPane.showMessageDialog(null, "You haven't implemented this method yet.  Returning null.", "To Do",
				JOptionPane.INFORMATION_MESSAGE);
		return null;
		**/
		
		Intersection demo = new Intersection("Demo Intersection", "A simple pre-timed demo.");
		
		TrafficStream NorthBound = new TrafficStream("N->S|E|W", "North: Through, left, right");
		TrafficStream SouthBound = new TrafficStream("S->N|E|W", "South: Through, left, right");
		TrafficStream WestBound = new TrafficStream("W->E|N|S", "West: Through, left, right");
		TrafficStream EastBound = new TrafficStream("E->W|N|S", "East: Through, left, right");
		
		SignalFace SW_N = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, 3);
		SignalFace NE_N = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH, 3);
		SignalFace SE_N = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH, 3);
		NorthBound.addObserver(SW_N);
		NorthBound.addObserver(NE_N);
		NorthBound.addObserver(SE_N);
		
		SignalFace NE_S = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH, 3);
		SignalFace SW_S = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH, 3);
		SignalFace NW_S = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH, 3);
		SouthBound.addObserver(NE_S);
		SouthBound.addObserver(SW_S);
		SouthBound.addObserver(NW_S);
		
		SignalFace SW_W = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.WEST, 3);
		SignalFace NE_W = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.WEST, 3);
		SignalFace SE_W = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.WEST, 3);
		WestBound.addObserver(SW_W);
		WestBound.addObserver(NE_W);
		WestBound.addObserver(SE_W);
		
		
		SignalFace SE_E = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST, 3);
		SignalFace SW_E = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.EAST, 3);
		SignalFace NW_E = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.EAST, 3);
		EastBound.addObserver(SE_E);
		EastBound.addObserver(SW_E);
		EastBound.addObserver(NW_E);
		
		demo.addSignalFace(SW_N);
		
		demo.addSignalFace(NE_N);
		demo.addSignalFace(SE_N);
		
		demo.addSignalFace(NE_S);
		demo.addSignalFace(SW_S);
		demo.addSignalFace(NW_S);
		
		demo.addSignalFace(SW_W);
		demo.addSignalFace(NE_W);
		demo.addSignalFace(SE_W);
		
		demo.addSignalFace(SE_E);
		demo.addSignalFace(SW_E);
		demo.addSignalFace(NW_E);
		
		Phase EW = new Phase("EW", "All EW Streams: 5 Sec");
		EW.addStream(NorthBound, State.RED);
		EW.addStream(SouthBound, State.GREEN);
		EW.addStream(WestBound, State.GREEN);
		EW.addStream(EastBound, State.GREEN);
		EW.setMinGreenInterval(5);
		
		Phase EQ_Y = new Phase("EQ-Y", "EW Ending: 2 Sec");
		EQ_Y.addStream(NorthBound, State.RED);
		EQ_Y.addStream(SouthBound, State.RED);
		EQ_Y.addStream(WestBound, State.YELLOW);
		EQ_Y.addStream(EastBound, State.GREEN);
		EQ_Y.setMinGreenInterval(2);

		Phase Stop2 = new Phase("Stop", "All Red: 2 Sec");
		Stop2.addStream(NorthBound, State.RED);
		Stop2.addStream(SouthBound, State.RED);
		Stop2.addStream(WestBound, State.RED);
		Stop2.addStream(EastBound, State.RED);
		Stop2.setMinGreenInterval(2);
		
		Phase NS = new Phase("NS", "All NS Streams: 2 Sec");
		NS.addStream(NorthBound, State.GREEN);
		NS.addStream(SouthBound, State.GREEN);
		NS.addStream(EastBound, State.RED);
		NS.addStream(WestBound, State.RED);
		NS.setMinGreenInterval(2);
		
		Phase NS_Y = new Phase("NS-Y", "NS Ending: 2 Sec");
		NS_Y.addStream(NorthBound, State.YELLOW);
		NS_Y.addStream(SouthBound, State.YELLOW);
		NS_Y.addStream(WestBound, State.RED);
		NS_Y.addStream(EastBound, State.RED);
		NS_Y.setMinGreenInterval(2);
		
		Phase Stop1 = new Phase("Stop", "All Red: 1 Sec");
		Stop1.addStream(NorthBound, State.RED);
		Stop1.addStream(SouthBound, State.RED);
		Stop1.addStream(WestBound, State.RED);
		Stop1.addStream(EastBound, State.RED);
		Stop1.setMinGreenInterval(1);
		
		
		PretimedPhasePlan PretimedDemo = new PretimedPhasePlan();
		PretimedDemo.add(EW);
		PretimedDemo.add(EQ_Y);
		PretimedDemo.add(Stop2);
		PretimedDemo.add(NS);
		PretimedDemo.add(NS_Y);
		PretimedDemo.add(Stop1);
		
		demo.addPlan(PretimedDemo);
		return demo;
		
	}
	
	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more fully-actuated phase plans.
	 * @return the intersection I made.
	 */
	public static Intersection fullyActivatedIntersection() {
Intersection demo = new Intersection("Demo Intersection", "A simple fully actuated demo.");
		
		TrafficStream NorthBound = new TrafficStream("N->S|E|W", "North: Through, left, right");
		TrafficStream SouthBound = new TrafficStream("S->N|E|W", "South: Through, left, right");
		TrafficStream WestBound = new TrafficStream("W->E|N|S", "West: Through, left, right");
		TrafficStream EastBound = new TrafficStream("E->W|N|S", "East: Through, left, right");
		
		SignalFace SW_N = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, 3);
		SignalFace NE_N = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH, 3);
		SignalFace SE_N = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH, 3);
		NorthBound.addObserver(SW_N);
		NorthBound.addObserver(NE_N);
		NorthBound.addObserver(SE_N);
		
		SignalFace NE_S = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH, 3);
		SignalFace SW_S = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH, 3);
		SignalFace NW_S = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH, 3);
		SouthBound.addObserver(NE_S);
		SouthBound.addObserver(SW_S);
		SouthBound.addObserver(NW_S);
		
		SignalFace SW_W = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.WEST, 3);
		SignalFace NE_W = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.WEST, 3);
		SignalFace SE_W = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.WEST, 3);
		WestBound.addObserver(SW_W);
		WestBound.addObserver(NE_W);
		WestBound.addObserver(SE_W);
		
		
		SignalFace SE_E = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST, 3);
		SignalFace SW_E = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.EAST, 3);
		SignalFace NW_E = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.EAST, 3);
		EastBound.addObserver(SE_E);
		EastBound.addObserver(SW_E);
		EastBound.addObserver(NW_E);
		
		demo.addSignalFace(SW_N);
		
		demo.addSignalFace(NE_N);
		demo.addSignalFace(SE_N);
		
		demo.addSignalFace(NE_S);
		demo.addSignalFace(SW_S);
		demo.addSignalFace(NW_S);
		
		demo.addSignalFace(SW_W);
		demo.addSignalFace(NE_W);
		demo.addSignalFace(SE_W);
		
		demo.addSignalFace(SE_E);
		demo.addSignalFace(SW_E);
		demo.addSignalFace(NW_E);
		
		RandomDetector northDetector = new RandomDetector();
		RandomDetector southDetector = new RandomDetector();
		RandomDetector westDetector = new RandomDetector();
		RandomDetector eastDetector = new RandomDetector();
		Phase EW = new Phase("EW", "All EW Streams: 5 Sec");
		EW.addStream(NorthBound, State.RED, northDetector);
		EW.addStream(SouthBound, State.GREEN);
		EW.addStream(WestBound, State.GREEN);
		EW.addStream(EastBound, State.GREEN);
		EW.setMinGreenInterval(5);
		
		
		Phase EQ_Y = new Phase("EQ-Y", "EW Ending: 2 Sec");
		EQ_Y.addStream(NorthBound, State.RED, northDetector);
		EQ_Y.addStream(SouthBound, State.RED,southDetector);
		EQ_Y.addStream(WestBound, State.YELLOW,westDetector);
		EQ_Y.addStream(EastBound, State.GREEN,eastDetector);
		EQ_Y.setMinGreenInterval(2);

		Phase Stop2 = new Phase("Stop", "All Red: 2 Sec");
		Stop2.addStream(NorthBound, State.RED, northDetector);
		Stop2.addStream(SouthBound, State.RED,southDetector);
		Stop2.addStream(WestBound, State.RED,westDetector);
		Stop2.addStream(EastBound, State.RED,eastDetector);
		Stop2.setMinGreenInterval(2);
		
		Phase NS = new Phase("NS", "All NS Streams: 2 Sec");
		NS.addStream(NorthBound, State.GREEN, northDetector);
		NS.addStream(SouthBound, State.GREEN,southDetector);
		NS.addStream(EastBound, State.RED,westDetector);
		NS.addStream(WestBound, State.RED,eastDetector);
		NS.setMinGreenInterval(2);
		
		Phase NS_Y = new Phase("NS-Y", "NS Ending: 2 Sec");
		NS_Y.addStream(NorthBound, State.YELLOW, northDetector);
		NS_Y.addStream(SouthBound, State.YELLOW,southDetector);
		NS_Y.addStream(WestBound, State.RED,westDetector);
		NS_Y.addStream(EastBound, State.RED,eastDetector);
		NS_Y.setMinGreenInterval(2);
		
		Phase Stop1 = new Phase("Stop", "All Red: 1 Sec");
		Stop1.addStream(NorthBound, State.RED,northDetector);
		Stop1.addStream(SouthBound, State.RED,southDetector);
		Stop1.addStream(WestBound, State.RED,westDetector);
		Stop1.addStream(EastBound, State.RED,eastDetector);
		Stop1.setMinGreenInterval(1);
		
		
		FullyActuatedPhasePlan PretimedDemo = new FullyActuatedPhasePlan();
		PretimedDemo.add(EW);
		PretimedDemo.add(EQ_Y);
		PretimedDemo.add(Stop2);
		PretimedDemo.add(NS);
		PretimedDemo.add(NS_Y);
		PretimedDemo.add(Stop1);
		
		demo.addPlan(PretimedDemo);
		return demo;
	}
}
