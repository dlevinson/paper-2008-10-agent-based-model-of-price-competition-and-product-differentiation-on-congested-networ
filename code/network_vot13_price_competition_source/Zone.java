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

public class Zone {

	public int zoneId, production, attraction;
	public float oDCost[], oDFlow[], accessAct, accessPop;
	
	public Zone() {}
	
	public Zone(int zoneId, int production, int attraction, int numZones){
		this.zoneId = zoneId;
		this.production = production;
		this.attraction = attraction;
		//this.oDCost = new float[numZones];
		//this.oDFlow = new float[numZones];
		this.accessAct = 0;
		this.accessPop = 0;
	}

}
