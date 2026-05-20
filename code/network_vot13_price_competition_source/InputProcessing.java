/**
 * @author Lei Zhang
 * 			Aug 28, 2003
 */

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.*;   
import java.util.*;
import java.lang.*;

public class InputProcessing {
	
	public int numArcs, numZones, numNodes, totalODFlows;
	public Arc arc[];
	public Zone zone[];
	//public float arcFlow[], oDCost[][];
	
	private int oNode, dNode, type, numLanes, zoneId, production, attraction;
	private float length, fftt, tt, capacity; 
	private int privateRoad, privateCompetitor, consession;
	
	private static final float TOLL = 0;
		
	public InputProcessing() {}
	
	public InputProcessing(String dir1, String arcFileName, String zoneFileName, float markUp, float THETA1, float THETA2, int Y, int AVETIME) throws IOException {
		
		ReadAFile readArc = new ReadAFile(dir1, arcFileName);
		ReadAFile readZone = new ReadAFile(dir1, zoneFileName);
		///ReadAFile readArcFlow = new ReadAFile(dir2, arcFlowFileName);
		///ReadAFile readODCost = new ReadAFile(dir2, oDCostFileName);
		
		//arc file
		this.numArcs = readArc.readint();
		//System.out.println(numArcs);
		//this.arc = new Arc[numArcs];
		this.arc = new Arc[2*numArcs]; //Allow new private toll roads
		readArc.readLine();
		//readArc.readLine();
		for(int i = 0; i < numArcs; i++){
			this.oNode = readArc.readint();
			//System.out.print(oNode + "\t");
			this.dNode = readArc.readint();
			//System.out.print(dNode + "\t");
			this.type = readArc.readint();
			//System.out.print(type + "\t");
			this.numLanes = readArc.readint();
			//System.out.print(numLanes + "\t");
			this.length = readArc.readfloat();
			//System.out.print(length + "\t");
			this.fftt = readArc.readfloat();
			//System.out.print(fftt + "\t");
			this.capacity = readArc.readfloat();
			//System.out.print(capacity + "\t");
			this.privateRoad = readArc.readint();
			//System.out.print(privateRoad + "\t");
			this.privateCompetitor = readArc.readint();
			//System.out.print(privateCompetitor + "\t");
			this.tt = readArc.readfloat();
			//System.out.println(tt);
			this.consession = 0;
			this.arc[i] = new Arc(oNode, dNode, length, capacity, fftt, tt, THETA1, THETA2, TOLL, type, numLanes, Y, privateRoad, privateCompetitor, consession, markUp, AVETIME );
		}
		System.out.println("Arc File Initiated");
		//zone file
		this.numZones = readZone.readint();
		this.numNodes = readZone.readint();
		this.zone = new Zone[numZones];
		readZone.readLine();
		//readZone.readLine();
		this.totalODFlows = 0;
		for(int i = 0; i < numZones; i++){
			this.zoneId = readZone.readint();
			this.production = readZone.readint();
			this.attraction = readZone.readint();
			totalODFlows += production;
			//System.out.println(zoneId + "\t" + production + "\t" + attraction);
			this.zone[i] = new Zone(zoneId, production, attraction, numZones);
		}
		System.out.println("Zone File Initiated");
		/*
		//OBA link flow file
		readArcFlow.readLine();
		this.arcFlow = new float[numArcs];
		for(int i = 0; i < numArcs; i++){
			readArcFlow.readint();
			readArcFlow.readint();
			this.arcFlow[i] = readArcFlow.readfloat();
			readArcFlow.readfloat();
		}
		System.out.println("-- OBA Link File");
		//OBA OD cost table file
		readODCost.readLine();
		this.oDCost = new float[numZones][numZones];
		for(int i = 0; i < numZones; i++){
			for(int j = 0; j < numZones; j++){
				
				//readODCost.readint();
				//readODCost.readint();
				//this.oDCost[i][j] = readODCost.readfloat();
				
				//!!!!//CHAGNE TO THIS
				
				if(i == j){
					this.oDCost[i][j] = 99999;
				}
				else{
					readODCost.readint();
					readODCost.readint();
					this.oDCost[i][j] = readODCost.readfloat();
				}
				
			}
			//System.out.println(i);
		}
		System.out.println("-- OBA OD File");
		*/
	}
	
	
	public InputProcessing(Arc[] arc, int numArcs, Evolve evolve){
		for(int i = 0; i < numArcs; i++){
			arc[i].flow = (float)(10*evolve.link[arc[i].oNode][arc[i].dNode][4]);
		}	
	}
	
	/*
	public InputProcessing(String dir2, Arc[] arc, Zone[] zone, String arcFlowFileName, String oDCostFileName, int numArcs, int numZones) throws IOException {
		
		
		ReadAFile readArcFlow = new ReadAFile(dir2, arcFlowFileName);
		ReadAFile readODCost = new ReadAFile(dir2, oDCostFileName);
		
		//OBA link flow file
		readArcFlow.readLine();
		//this.arcFlow = new float[numArcs];
		for(int i = 0; i < numArcs; i++){
			readArcFlow.readint();
			readArcFlow.readint();
			arc[i].flow = readArcFlow.readfloat();
			readArcFlow.readfloat();
		}
		
		//OBA OD cost table file
		readODCost.readLine();
		//this.oDCost = new float[numZones][numZones];
		for(int i = 0; i < numZones; i++){
			for(int j = 0; j < numZones; j++){
				if(i == j){
					zone[i].oDCost[j] = 0;
				}
				else{
					readODCost.readint();
					readODCost.readint();
					//zone[i].oDCost[j] = readODCost.readfloat()
					zone[i].oDCost[j] = readODCost.readfloat() - 200; //in order to avoid unrealitic centroid traffic in OBA, centroid travel time is augmented by 100. A trip uses two centroid connectors. Therefore 200 must be substracted to get the real OD cost
				}
			}
		}
		
	}
	*/
	
		
}
