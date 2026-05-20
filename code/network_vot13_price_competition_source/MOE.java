/**
 * @author Lei Zhang
 * 			Dec 13, 2003
 */
public class MOE {
	
	private float [] moeList, accessListAct, accessListPop; 
	private float numTrips, vht, vkt, cs, profit, prodVKT, prodTrips, prodCS, input, output, accessAct, accessPop, giniAct, giniPop, 
				  revenue, hierarchy, amortizedExpansion, amortizedExpansionPri, amortizedExpansionPub, maintenance, maintenancePri, maintenancePub;
	private float initInput;
	private float csPri, csPub, profitPri, excessPub, vhtPri, vhtPub, vktPri, vktPub, revenuePri, revenuePub;
	private float laneKmPri, laneKmPub, kmPri, kmPub, toll, tollPri, tollPub;
	private float numPriRoads, numPubRoads, numEarningPri, numLosingPri, privateNewInvestment, privateContinuousInvestment; //should be int, I use a single float moe list
	private float welfare, welfareH, welfareM, welfareL, pdTime, pdToll, pdIncome, totalFlow, totalGiniFlow, totalDFlow, dTime, dToll;
	private int[] trips;
	
	public MOE() {
		//this.moeList = new float[16];
		this.moeList = new float[55];
		this.numTrips = 0;
		this.vht = 0;
		this.vkt = 0;
		this.cs = 0;
		this.profit = 0;
		this.input = 0;
		this.output = 0;
		this.accessAct = 0;
		this.accessPop = 0;
		this.revenue = 0;
		this.hierarchy = 0;
		this.amortizedExpansion = 0;
		this.maintenance = 0;
		//added in version 1.0
		this.csPri = 0;
		this.csPub = 0;
		this.profitPri = 0;
		this.excessPub = 0;
		this.vhtPri = 0;
		this.vhtPub = 0;
		this.vktPri = 0;
		this.vktPub = 0;
		this.revenuePri = 0;
		this.revenuePub = 0;
		this.laneKmPri = 0;
		this.laneKmPub =0;
		this.kmPri = 0;
		this.kmPub = 0;
		this.toll = 0;
		this.tollPri = 0;
		this.tollPub = 0;
		this.numPriRoads = 0;
		this.numPubRoads = 0;
		this.numLosingPri = 0;
		this.numLosingPri = 0;
		this.privateNewInvestment = 0;
		this.privateContinuousInvestment = 0;
		this.amortizedExpansionPri = 0;
		this.amortizedExpansionPub = 0;
		this.maintenancePri = 0;
		this.maintenancePub = 0;
		//for VOT10
		this.welfare = 0;
		this.welfareH = 0;
		this.welfareM = 0;
		this.welfareL = 0;
		this.pdTime = 0;
		this.pdToll = 0;
		this.pdIncome = 0;
		this.dTime = 0;
		this.dToll = 0;
		this.totalFlow = 0;
		this.totalGiniFlow = 0;
		this.totalDFlow = 0;
		this.trips = new int[3];
		for(int i = 0; i < 3; i++){
			trips[i] = 0;
		}
	}
	
	public float [] computeMOE(Arc arc[], Zone zone[], Auto auto[], OD od[], int numArcs, int numZones, int numAutos, int numOD, int initialTrips[], float distBeta, int iteration, float CONSESSION, float NEWCOSTMARKUP){
		this.accessListAct = new float[numZones];
		this.accessListPop = new float[numZones]; 
			
		for(int i = 0; i < numArcs; i++){
			vht += arc[i].flow*arc[i].tt/60;
			revenue += arc[i].revenue;
			//revenue += arc[i].flow*arc[i].toll;
			profit += arc[i].profit;
			amortizedExpansion += arc[i].amortizedExpansionCost;
			maintenance += arc[i].maintenanceCost;
			input += arc[i].flow*arc[i].generalizedCost/60;
			vkt += arc[i].flow*arc[i].length*1.6;
			cs += 0.5*(arc[i].flow + arc[i].initCSFlow)*(arc[i].initCSCost - arc[i].generalizedCost)/6;
			toll += arc[i].toll;
			if(arc[i].privateRoad == 1){
				csPri += 0.5*(arc[i].flow + arc[i].initCSFlow)*(arc[i].initCSCost - arc[i].generalizedCost)/6;
				profitPri += arc[i].profit;
				if(arc[i].profit < 0){
					numLosingPri++;
				}
				else{
					numEarningPri++;
				}
				vhtPri += arc[i].flow*arc[i].tt/60;
				vktPri += arc[i].flow*arc[i].length*1.6;
				revenuePri += arc[i].revenue;
				amortizedExpansionPri += arc[i].amortizedExpansionCost;
				maintenancePri += arc[i].maintenanceCost;
				kmPri += arc[i].length*1.6;
				laneKmPri += arc[i].length*1.6*(float)(arc[i].numLanes);
				tollPri += arc[i].toll;
				numPriRoads++;
			}
			else{ //public
				csPub += 0.5*(arc[i].flow + arc[i].initCSFlow)*(arc[i].initCSCost - arc[i].generalizedCost)/6;
				excessPub += arc[i].profit;
				vhtPub += arc[i].flow*arc[i].tt/60;
				vktPub += arc[i].flow*arc[i].length*1.6;
				revenuePub += arc[i].revenue;
				amortizedExpansionPub += arc[i].amortizedExpansionCost;
				maintenancePub += arc[i].maintenanceCost;
				kmPub += arc[i].length*1.6;
				laneKmPub += arc[i].length*1.6*(float)(arc[i].numLanes);
				tollPub += arc[i].toll;
				numPubRoads++;				
			}
			
			if(i > 0){
				if(arc[i].privateRoad == 1 & arc[i - 1].privateRoad == 0 & arc[i - 1].privateCompetitor == 1 & arc[i - 1].consession == (CONSESSION - 1)){
					privateNewInvestment +=  NEWCOSTMARKUP*arc[i - 1].expansionCost;
				}
			}
			
			if(arc[i].privateRoad == 1 & arc[i].optimalInvestment > 0){
				if(arc[i].optimalInvestment == 1){
					privateContinuousInvestment += arc[i].expansionCost;
				}				
				else if(arc[i].optimalInvestment == 2){
					privateContinuousInvestment += arc[i].expansionCost2;
				}
				else{
					System.out.println("!!!ERROR: Optimal investment > 2 in MOE.java");
				}
			}
		}
		
		toll = toll/(kmPub + kmPri);
		tollPri = tollPri/kmPri;
		tollPub = tollPub/kmPub;
		
		if(iteration == 0)initInput = input;
		
		/*
		for(int i = 0; i < numZones; i++){
			for(int j = 0; j < numZones; j++){
				numTrips += zone[i].oDFlow[j];
				zone[i].accessAct += zone[j].attraction*Math.exp(-distBeta*zone[i].oDCost[j]);
				zone[i].accessPop += zone[j].production*Math.exp(-distBeta*zone[j].oDCost[i]);
			}
			accessAct += zone[i].accessAct;
			accessPop += zone[i].accessPop;
			accessListAct[i] = zone[i].accessAct;
			accessListPop[i] = zone[i].accessPop;
			zone[i].accessAct = 0;
			zone[i].accessPop = 0;
		}
		*/
		
		//New for VOT10
		for(int i = 0; i < numOD; i++){
			if(od[i].o != od[i].d & od[i].flow[iteration] > 5 & od[i].priceSet == true){
				welfare += 0.5*(od[i].flow[iteration] + od[i].flow[0])*(od[i].price[0] - od[i].price[iteration]);
				if(od[i].flowH[iteration] > 1 & od[i].priceHSet == true){
					welfareH += 0.5*(od[i].flowH[iteration] + od[i].flowH[0])*(od[i].priceH[0] - od[i].priceH[iteration]);
				}
				if(od[i].flowM[iteration] > 1 & od[i].priceMSet == true){
					welfareM += 0.5*(od[i].flowM[iteration] + od[i].flowM[0])*(od[i].priceM[0] - od[i].priceM[iteration]);
				}
				if(od[i].flowL[iteration] > 1 & od[i].priceLSet == true){
					welfareL += 0.5*(od[i].flowL[iteration] + od[i].flowL[0])*(od[i].priceL[0] - od[i].priceL[iteration]);
				}
				
				pdIncome += od[i].giniIncome[iteration]*od[i].flow[iteration];
				totalFlow += od[i].flow[iteration];
				
							
				if(od[i].flowH[iteration] > 30 & od[i].flowM[iteration] > 30 & od[i].flowL[iteration] > 30){
					pdToll += od[i].giniToll[iteration]*od[i].flow[iteration];
					pdTime += od[i].giniTime[iteration]*od[i].flow[iteration];
					totalGiniFlow += od[i].flow[iteration];
					
					if(od[i].nonZero == true){
						dTime += od[i].diffTime[iteration]*od[i].flow[iteration];
						dToll += od[i].diffToll[iteration]*od[i].flow[iteration];
						totalDFlow += od[i].flow[iteration];
					}
				}
				
			}
			
		}
		welfare = welfare*10*365/1000000;
		welfareH = welfareH*10*365/1000000;
		welfareM = welfareM*10*365/1000000;
		welfareL = welfareL*10*365/1000000;
		//pdTime = pdTime/totalFlow;
		//pdToll = pdToll/totalFlow;
		pdTime = pdTime/totalGiniFlow;
		pdToll = pdToll/totalGiniFlow;
		pdIncome = pdIncome/totalFlow;
		dTime = dTime/totalDFlow;
		dToll = dToll/totalDFlow;
		
		//Canceled Trips
		for(int i = 0; i < numAutos; i++){
			if(auto[i].trip == true){
				if(auto[i].income == 'H'){
					trips[0]++;
				}
				else if(auto[i].income == 'M'){
					trips[1]++;
				}
				else if(auto[i].income == 'L'){
					trips[2]++;
				}
				else{
					System.out.println("ERROR: Income in MOE.java");
				}
			}
		}
		
		if(iteration == 0){
			initialTrips[0] = trips[0];
			initialTrips[1] = trips[1];
			initialTrips[2] = trips[2];
			trips[0] = 0;
			trips[1] = 0;
			trips[2] = 0;
		}
		else{
			trips[0] = trips[0] - initialTrips[0];
			trips[1] = trips[1] - initialTrips[1];
			trips[2] = trips[2] - initialTrips[2];
		}
		//System.out.println("Profit " + profit);
		output = vkt;
		prodVKT = output/input;
		output = numTrips;
		prodTrips = output/input;
		output = cs;
		prodCS = output/(input - initInput);
		
		moeList[0] = vht;
		moeList[1] = vkt;
		moeList[2] = revenue;
		moeList[3] = cs;
		moeList[4] = input;
		moeList[5] = numTrips;
		moeList[6] = accessAct;
		moeList[7] = accessPop;
		moeList[8] = gini(accessListAct);
		moeList[9] = gini(accessListPop);
		moeList[10] = prodVKT;
		moeList[11] = prodTrips;
		moeList[12] = prodCS;
		moeList[13] = profit;
		moeList[14] = amortizedExpansion;
		moeList[15] = maintenance;
		//added in version 1.0
		moeList[16] = csPri;
		moeList[17] = csPub;
		moeList[18] = profitPri;
		moeList[19] = excessPub;
		moeList[20] = vhtPri;
		moeList[21] = vhtPub;
		moeList[22] = vktPri;
		moeList[23] = vktPub;
		moeList[24] = revenuePri;
		moeList[25] = revenuePub;
		moeList[26] = laneKmPri;
		moeList[27] = laneKmPub;
		moeList[28] = kmPri;
		moeList[29] = kmPub;
		moeList[30] = toll;
		moeList[31] = tollPri;
		moeList[32] = tollPub;
		moeList[33] = numPriRoads;
		moeList[34] = numPubRoads;
		moeList[35] = numEarningPri;
		moeList[36] = numLosingPri;
		moeList[37] = privateNewInvestment;
		moeList[38] = privateContinuousInvestment;
		moeList[39] = amortizedExpansionPri;
		moeList[40] = amortizedExpansionPub;
		moeList[41] = maintenancePri;
		moeList[42] = maintenancePub;
		//moeList[43] = -9;	//residualRevenuePub;
		moeList[43] = welfare;
		moeList[44] = welfareH;
		moeList[45] = welfareM;
		moeList[46] = welfareL;
		moeList[47] = pdTime;
		moeList[48] = pdToll;
		moeList[49] = pdIncome;
		moeList[50] = trips[0];
		moeList[51] = trips[1];
		moeList[52] = trips[2];
		moeList[53] = dTime;
		moeList[54] = dToll;
		

		//System.out.println("moe13: " + moeList[13]);
		return moeList;
	}
		
	public float gini(float [] list){
		int n;
		float gini = 0, average;
		n = list.length;
		average = average(list);
		
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				gini += Math.abs(list[i] - list[j]);
			}
		}
		gini = gini/(2*n*n*average);
		return gini;
	}

	public float average(float [] list){
		float ave = 0;
		for(int i = 0; i < list.length; i++)ave += ave + list[i];
		ave = ave/list.length;
		return ave;
	}
}
