/**
 * @author Lei Zhang
 * 			Sep 1, 2003
 */
import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.*;   
import java. util.*;
import java.lang.*;

public class OutputProcessing {

	/**
	 * Constructor for OutputProcessing.
	 */
	public OutputProcessing(Arc[] arc, int numArcs, float [] moeList, String dir4, String fileName, int iteration) throws IOException {
		
		FileOutputStream foutArc;
		PrintWriter outArc;
		Integer count = new Integer(iteration);
		
		try{
			foutArc = new FileOutputStream(dir4 + fileName + count.toString() + ".txt");
		}catch(FileNotFoundException e) {
			System.out.println("ERROR: can not write iterationOutputFile");
			return;
		}
		outArc = new PrintWriter(foutArc);
		outArc.println("VHT" + "\t" + moeList[0]);
		outArc.println("VKT" + "\t" + moeList[1]);
		outArc.println("Revenue" + "\t" + moeList[2]);
		outArc.println("Consumers's Surplus" + "\t" + moeList[3]);
		outArc.println("Total Input: VHT + Revenue/VoT" + "\t" + moeList[4]);
		outArc.println("NumOfTrips" + "\t" + moeList[5]);
		outArc.println("Accessibility-Activity" + "\t" + moeList[6]);
		outArc.println("Accessibility-Population" + "\t" + moeList[7]);
		outArc.println("AccessAct Gini" + "\t" + moeList[8]);
		outArc.println("AccessPop Gini" + "\t" + moeList[9]);
		outArc.println("Productivity-VKT/Input" + "\t" + moeList[10]);
		outArc.println("Productivity-Trips" + "\t" + moeList[11]);
		outArc.println("Productivity-CS Change/Input Change" + "\t" + moeList[12]);
		outArc.println("Profit" + "\t" + moeList[13]);
		outArc.println("Amortized Expansion" + "\t" + moeList[14]);
		outArc.println("Maintenance" + "\t" + moeList[15]);
		//added in version 1.0
		outArc.println("csPri" + "\t" + moeList[16]);
		outArc.println("csPub" + "\t" + moeList[17]);
		outArc.println("profitPri" + "\t" + moeList[18]);
		outArc.println("excessPub" + "\t" + moeList[19]);
		outArc.println("vhtPri" + "\t" + moeList[20]);
		outArc.println("vhtPub" + "\t" + moeList[21]);
		outArc.println("vktPri" + "\t" + moeList[22]);
		outArc.println("vktPub" + "\t" + moeList[23]);
		outArc.println("revenuePri" + "\t" + moeList[24]);
		outArc.println("revenuePub" + "\t" + moeList[25]);
		outArc.println("laneKmPri" + "\t" + moeList[26]);
		outArc.println("laneKmPub" + "\t" + moeList[27]);
		outArc.println("kmPri" + "\t" + moeList[28]);
		outArc.println("kmPub" + "\t" + moeList[29]);
		outArc.println("toll" + "\t" + moeList[30]);
		outArc.println("tollPri" + "\t" + moeList[31]);
		outArc.println("tollPub" + "\t" + moeList[32]);
		outArc.println("numPriRoads" + "\t" + moeList[33]);
		outArc.println("numPubRoads" + "\t" + moeList[34]);
		outArc.println("numEarningPri" + "\t" + moeList[35]);
		outArc.println("numLosingPri" + "\t" + moeList[36]);
		outArc.println("PrivateNewInvestment" + "\t" + moeList[37]);
		outArc.println("privateContinuousInvestment" + "\t" + moeList[38]);		
		outArc.println("amortizedExpansionPri" + "\t" + moeList[39]);	
		outArc.println("amortizedExpansionPub" + "\t" + moeList[40]);	
		outArc.println("maintenancePri" + "\t" + moeList[41]);	
		outArc.println("maintenancePub" + "\t" + moeList[42]);	
		outArc.println("cs" + "\t" + moeList[43]);	
		outArc.println("csH" + "\t" + moeList[44]);
		outArc.println("csM" + "\t" + moeList[45]);
		outArc.println("csL" + "\t" + moeList[46]);
		outArc.println("pdTime" + "\t" + moeList[47]);
		outArc.println("pdToll" + "\t" + moeList[48]);
		outArc.println("pdIncome" + "\t" + moeList[49]);
		outArc.println("HighIncomeTrips" + "\t" + moeList[50]);
		outArc.println("MidIncomeTrips" + "\t" + moeList[51]);
		outArc.println("LowIncomeTrips" + "\t" + moeList[52]);
		outArc.println("HTime/Ltime" + "\t" + moeList[53]);
		outArc.println("HToll/Ltoll" + "\t" + moeList[54]);
		
		outArc.println("<NUMBER OF LINKS> " + numArcs);
		outArc.println("Init node" + "\t" + "Term node" + "\t" + "FFSpeed" + "\t" + "Free Flow Time(min)" + "\t" + "Flow(veh/day)" + "\t" + "Capacity(veh/hour)" + "\t" + "Length(mile)" + "\t" + "Toll(min)" + "\t" + "Gen.Cost(min)" + "\t" + "Profit($)" + "\t" + "Type");
		for(int i = 0; i < numArcs; i++){
			outArc.println(arc[i].oNode + "\t" + arc[i].dNode + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].flow + "\t" + arc[i].capacity + "\t" + arc[i].length + "\t" + arc[i].toll + "\t" + arc[i].generalizedCost + "\t" + arc[i].profit + "\t" + arc[i].type);
		}
		
		outArc.close();
		foutArc.close();
	}
}
