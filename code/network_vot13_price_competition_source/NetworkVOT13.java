/**
 * @author Lei Zhang
 * 			Nov. 26, 2006
 * Written for Twin Cities Network
 * Can be adapted for other networks with the following changes:
 * 	(1) Currently, fftt 100 is added to all centroied connectors. This may or may not be 
 * 		necessary for other networks or newer traffic assighment algorithms
 * 
 * Compared to original Network-Ownership, this version adds the capability of modeling the
 * process of privatization or nationalization. 
 *
 * ISSUES
 * 1. When a new priate road is invested, its expansion cost is not taken into account in calculating furture profit. The exapansion cost
 * should be amortized as regular expansion projects.
 * 2. A possible extension: Make regular investment decisions probablistic based on "investmentProb". This can prevent a large number of
 * links expand themselves under the unrealistic assumption that others won't expand capacity. 
 * 3. How do we discount expansion costs for private and public roads? For private roads, using amortized values can overestimate long-run
 * profit because at the end of the simluation a portion of loan they borrowed will not have been paid off. For public roads, using residual 
 * revenue as excess revenue take expansion cost into account the year it is spent.  
 * 
 * NEW IN VERSION 11 (COMPARED TO VERSION 10):
 * 1. Input file is now private-public mixed ownership, which should produce evoluation of price competition and product differentiation
 * 2. Class DirectGraph dg and Auto auto will not be initialized in each iteration
 * 3. All flows and capacities are hourly flows and capacities.
 */

import java.io.*;
//import java.awt.*;
//import java.applet.*;
//import java.net.*;   
//import java.util.*;
//import java.lang.*;

public class NetworkVOT13 {

	private Zone zone[];
	private Arc arc[];
	private OD od[];
	private Auto auto[];
	private InputProcessing initialInput, updatedInput;
	private OutputProcessing iterationOutput;
	//private TripDistribution tripDist;
	//private OBAInput obaInput;
	private Pricing pricing;
	private Investment investment;
	private Ownership ownership;
	private MOE moe;
	
	//New classes for integration
	private DirectedGraph dg;
	private Evolve evolve;
	
	private String dir1, dir2, dir3, dir4, arcInputFile, zoneInputFile, arcFlowInputFile, oDCostInputFile, OBAArcFile, OBAODFile, iterationOutputFile;		
	private int numArcs, originalNumArcs, numZones, numNodes, numOD, numAutos, totalODFlow, executionTime;
	private boolean converged = false, historicalConvergence[], tempBool;
	//Lei's Model: private static final float VOT = 10, DISTBETA = (float)0.01, MU = (float)365*20/180, ALFA1 = 1, ALFA2 = (float)1.25, ALFA3 = (float)1.25, PHI = 365, P0 = (float)1/180, P1 = 1, P3 = (float)0.75, S_BETA = (float)0.75;
	//Bhanu's Model: private static final float VOT = 10, DISTBETA = (float)0.01, MU = (float)365, ALFA1 = 1, ALFA2 = (float)0.75, ALFA3 = (float)0.75, PHI = 365, P0 = (float)1.5, P1 = 1, P3 = (float)0, S_BETA = (float)1;
	private static final float VOT = 10, DISTBETA = (float)0.01, 
		MU = (float)365*20/180, ALFA1 = 1, ALFA2 = (float)1.25, ALFA3 = (float)1.25, 
		PHI = 365, P0 = (float)1/180, P1 = 1, P3 = (float)0.75, 
		SGM0 = 1, SGM1 = (float)0.5, SGM2 = (float)1.25, SGM3 = 1, 
		THETA1 = (float)0.15, THETA2 = 4, 
		S_BETA = (float)0.75, R = (float)0.1, MAXTOLL = (float)5.0,
		NEWCOSTMARKUP = (float)1.0, PRIVATETOLLMARKUP = (float)1.0, PRIVATIZATIONPROB = (float)0.8, INVESTMENTPROB = (float)0.0 ; //INVESTMENTPROB = (float)0.5;
	private static final int Y = 25, AVETIME = 4;
	
	//Fixed or variable demand
	private boolean fixedDemand;
	private final float X = (float)0.00;
	
	private static final char INVESTMENTPOLICY = 'm'; // See Investment.java for a description of all seven alternative policies
	private static final char PRICINGPOLICY = 'm'; // See Pricing.java for a description of all pricing policies	
	private static final char OWNERSHIPPOLICY = 'p'; // See Ownership.java for description of all ownerhsip policies
	private static final int CONSESSION = 15, MINIMUM_CONVERGENCE_DURATION = 100, MAXIMUM_ITERATION = 100;
	private float[][] moeList;
	private float aveVC = 1, publicResidualRevenue;
	
	//New variables for integration
	final static int max_year = 3;
	final static double max_oderror = 30;
	final static double large = 999;
	
	public int[] initialTrips;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		NetworkVOT13 networkOwnership = new NetworkVOT13();
		networkOwnership.initialization();
		networkOwnership.iteration();
		networkOwnership.end();		
	}
	
	//constructors
	public NetworkVOT13() {
		this.historicalConvergence = new boolean[MINIMUM_CONVERGENCE_DURATION];
		//input file paths
		this.dir1 = "c:/work/project/unfunded/Network_VOT/data/";
		//this.dir2 = "c:/work/project/network_ownership/OBA/results/";
		//this.dir3 = "c:/work/project/network_ownership/OBA/data/";
		this.dir4 = "c:/work/project/unfunded/Network_VOT/results/";
		this.arcInputFile = "arcs.txt";
		this.zoneInputFile = "zones.txt";
		//this.arcFlowInputFile = "arc_flow.txt";
		//this.oDCostInputFile = "od_cost.txt";
		//this.OBAArcFile = "bsclnk.txt";
		//this.OBAODFile = "vehtrp.txt";  
		this.iterationOutputFile = "network";
		this.publicResidualRevenue = 0;
	}
	
	public void initialization() throws IOException{
		System.out.println("####### INITIALIZATION #######");
						
		//read input files
		initialInput = new InputProcessing(dir1, arcInputFile, zoneInputFile, NEWCOSTMARKUP, THETA1, THETA2, Y, AVETIME);
		//initialize parameters, zones, arcs, OD cost, and arc flows
		numArcs = initialInput.numArcs;
		originalNumArcs = initialInput.numArcs;
		numZones = initialInput.numZones;
		numNodes = initialInput.numNodes;
		numOD = initialInput.numZones*initialInput.numZones;
		totalODFlow = initialInput.totalODFlows;
		numAutos = initialInput.totalODFlows;
		zone = initialInput.zone;
		arc = initialInput.arc;
		od = new OD[numOD];
		int counter = 0;
		for(int i = 0; i < numZones; i++){
			for(int j = 0; j < numZones; j++){
				od[counter] = new OD( i+1, j+1, MAXIMUM_ITERATION);
				counter++;
			}
		}
		
		this.moeList = new float[MAXIMUM_ITERATION][55];
		if(this.X == 0) {
			this.fixedDemand = true; 
			System.out.println("Fixed Demand");
		}
		else{
			this.fixedDemand = false; 
			System.out.println("Variable Demand");
		}
		this.initialTrips = new int[3];
		for(int i = 0; i < 3; i++){
			initialTrips[i] = 0;
		}
		
		/*
		for(int i = 0; i < numZones; i++){
			for(int j = 0; j < numZones; j++){
				zone[i].oDCost[j] = initialInput.oDCost[i][j];
			}
		}
		for(int i = 0; i < numArcs; i++){
			arc[i].flow = initialInput.arcFlow[i];
		}
		*/
		
		//Int. Codes for integrating network growth model with agent-based travel demand model
		dg = new DirectedGraph(numArcs, numZones, arc);
		System.out.println("DirectGraph Initiated");
		evolve = new Evolve(dg, totalODFlow, zone);
		//evolve.odInitialization(zone);
		System.out.println("EvoleClass Initiated");
		System.out.println();
	}
	
	public void iteration() throws InterruptedException, IOException{
		System.out.println("####### ITERATIONS START #######");
		System.out.println();
		int iteration = 0;
		
		do{
			System.out.print("ITERATION " + iteration + "\t");
			
			//Agent-based travel demand model
			//System.out.println("Step1: Agent-Based Travel Demand Model");
			evolve.odInitialization(zone);
			//System.out.println("	0odInitilizationDone");
			evolve.odEstimator();
			//System.out.println("	0odEstimatorDone");
			evolve.resetInfo();
			//System.out.println("	0resetInfoDone");
			double error;
			error = large;
			int year = 1;
			while ((year<max_year) && (error>max_oderror)){    //???Why max_year???
				//System.out.println("	Year"+year);
				evolve.initialization();
				//System.out.println("		initializationDone");
				evolve.iteration();
				//System.out.println("		iterationDone");
				evolve.updateDestination(zone);
				//System.out.println("		updateDestinationDone");
				this.auto = evolve.auto;
				//copy for moe calculation
				error = evolve.odError();
				//System.out.println("		odErrorDone");
				if(error>max_oderror){
					evolve.resetInfo();
				}
				//System.out.println("		resetInfoDone");
				//System.out.println("	End of Year"+year+"\tError:"+error+"\tTarget"+max_oderror);
				year++;
			}
			//System.out.println("GC " + auto[1000].route_info[2]);
			//System.out.println("GC " + auto[2000].route_info[2]);
			//System.out.println("GC " + auto[3000].route_info[2]);
			//String outfilename = "Out-NetworkGrowth.txt";
			//evolve.outputfile(outfilename,dg);
			updatedInput = new InputProcessing(arc, numArcs, evolve);
			
			//Update arc attributes: Apply cost, revenue, BC ratio, investmentReturn, and profitability models
			//System.out.println("Step2: Update Arc Attributes");
				//System.out.println("		Link " + "oNode" + "\t" + "dNode" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost" + "\t" +  "plusCapacity1" + "\t" +  "plucCapcity2" + "\t" + "return1" + "\t" + "expansionCost" + "\t" + "extraMain1" + "\t" +  "return2" + "\t" + "expansionCost2" + "\t" + "extraMain2" + "\t" + "optimalInvestment" + "\t" + "ttSavings1" + "\t" + "interestRate" + "\t" + "trafficRate" + "\t" + "vot" + "\t" + "maintenanceCost" + "\t" + "newMain1");
				//System.out.println("		Link " + "oNode" + "\t" + "dNode" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost" + "\t" +  "plusCapacity1" + "\t" +  "plucCapcity2" + "\t" + "return1" + "\t" + "expansionCost" + "\t" + "main1" + "\t" + "annualMain1" + "\t" + "return2" + "\t" + "expansionCost2" + "\t" + "main2" + "\t" + "annualMain2" + "\t" + "optimalInvestment" + "\t" + "interestRate" + "\t" + "trafficRate" + "\t" + "vot" + "\t" + "ArerageRate" + "\t" + "privateFlow1" + "\t" + "privateFlow2" + "\t" + "profitability" + "\t" + "BCRatio");
			for(int i = 0; i < numArcs; i++){
				arc[i].updateMaintenanceCost(MU, ALFA1, ALFA2, ALFA3);
				arc[i].updateExpansionCost(SGM0, SGM1, SGM2, SGM3);
				arc[i].updateRevenue(PHI, VOT);
				arc[i].updateBCRatio(VOT, X, Y, R, i, MU, ALFA1, ALFA2);
				if(PRICINGPOLICY == '6'){
					if(iteration > (AVETIME - 2)){
						arc[i].updateInvestmentReturnsCeilingNewAve(VOT, X, Y, R, MU, ALFA1, ALFA2, MAXTOLL, aveVC, iteration);
					}
					else{
						arc[i].updateInvestmentReturnsCeilingNew(VOT, X, Y, R, MU, ALFA1, ALFA2, MAXTOLL, aveVC, iteration);							
					}
				}
				else{
					if(iteration > (AVETIME -2)){
						arc[i].updateInvestmentReturnsAve(VOT, X, Y, R, MU, ALFA1, ALFA2);
					}
					else{
						arc[i].updateInvestmentReturns(VOT, X, Y, R, MU, ALFA1, ALFA2);
					}
				}
				if(OWNERSHIPPOLICY == 'm'){
					//System.out.println("		Link " + "oNode" + "\t" + "dNode" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost" + "\t" +  "plusCapacity1" + "\t" +  "plucCapcity2" + "\t" + "return1" + "\t" + "expansionCost" + "\t" + "extraMain1" + "\t" +  "return2" + "\t" + "expansionCost2" + "\t" + "extraMain2" + "\t" + "optimalInvestment" + "\t" + "ttSavings1" + "\t" + "interestRate" + "\t" + "trafficRate" + "\t" + "vot" + "\t" + "maintenanceCost" + "\t" + "newMain1");
					if(arc[i].privateRoad == 0 & arc[i].privateCompetitor == 0){ //only public roads with no parallel private roads are candidates for privatization 
						if(arc[i].type == 1 || arc[i].type == 2){ //only limited access highways are considered for privatization 
						//if(iteration > 5){
						//		if(arc[i].vCHistory[arc[i].arcIteration - 1] > 1.5 & arc[i].vCHistory[arc[i].arcIteration - 2] > 1.5 & arc[i].vCHistory[arc[i].arcIteration - 3] > 1.5 & arc[i].vCHistory[arc[i].arcIteration - 4] > 1.5 & arc[i].vCHistory[arc[i].arcIteration - 5] > 1.5 ){
						if(iteration > 0){
								if(arc[i].vCHistory[arc[i].arcIteration - 1] > 1.5 & arc[i].vCHistory[arc[i].arcIteration - 2] > 1.5){
									if(iteration > (AVETIME - 2)){
										arc[i].updateProfitabilityAve(VOT, X, Y, R, MU, ALFA1, ALFA2, PRIVATETOLLMARKUP);
									}
									else{
										arc[i].updateProfitability(VOT, X, Y, R, MU, ALFA1, ALFA2, PRIVATETOLLMARKUP);
									}
								}
							}
						}
					}
				}
			}
			
			//Update OD information
			//System.out.println("OD INFO");
			//System.out.println("o" + "-" + "d" + "\t" + "flow" + "\t" + "flowH" + "\t" + "flowM" + "\t" + "flowL" + "\t" + "price" + "\t" + "priceH" + "\t" + "priceM" + "\t" + "priceL" + "\t" + "giniTime" + "\t" + "giniToll" + "\t" + "giniIncome");
			int counter = 0;
			for(int i = 0; i < numZones; i++){
				for(int j = 0; j < numZones; j++){
					od[counter].update(iteration, numAutos, auto, evolve);
					counter++;
				}
			}
			
			//Calculate MOEs
			//System.out.println("Step3: Compute Measures of Effectiveness");
			if(iteration == 0){ for(int i = 0; i < numArcs; i++)arc[i].saveInitial();}
			if(iteration == 1){ for(int i = 0; i < numArcs; i++)arc[i].saveInitialCS(P0, P1, VOT);}
			moe = new MOE();
			moeList[iteration] = moe.computeMOE(arc, zone, auto, od, numArcs, numZones, numAutos, numOD, initialTrips, DISTBETA, iteration, CONSESSION,  NEWCOSTMARKUP);
				
			System.out.print(moeList[iteration][26] + "\t");
			System.out.print(moeList[iteration][27] + "\t");
			System.out.print(moeList[iteration][31] + "\t");
			System.out.print(moeList[iteration][32] + "\t");
			System.out.print(moeList[iteration][47] + "\t");
			System.out.print(moeList[iteration][48] + "\t");
			System.out.print(moeList[iteration][53] + "\t");
			System.out.print(moeList[iteration][54] + "\t");
					
			//Apply ownership model
			//System.out.println("Step4: Apply Ownership Policy");
			if(OWNERSHIPPOLICY == 'm'){
				ownership = new Ownership(arc, numArcs, originalNumArcs, OWNERSHIPPOLICY, CONSESSION, NEWCOSTMARKUP, THETA1, THETA2, Y, AVETIME, PRIVATIZATIONPROB);
				//total number of arcs could change after new private toll roads are built
				this.numArcs = ownership.newNumArcs;
				//System.out.println("NewNumArcs: " + numArcs);
				for(int i = 0; i < numArcs; i++){
					this.arc[i] = ownership.newArc[i];
					//System.out.println(arc[i].oNode + "\t" + arc[i].dNode + "\t" + arc[i].privateRoad);
				}
			}
			
			//Apply investment model - New link capacity
			//System.out.println("Step5: Apply Investment Policy");
			investment = new Investment(arc, numArcs, INVESTMENTPOLICY, SGM3, S_BETA, publicResidualRevenue, INVESTMENTPROB);
			//moeList[iteration][43] = investment.residualRevenue;
			publicResidualRevenue = investment.residualRevenue;
						
			//Apply the pricing model
			//System.out.println("Step6: Apply Pricing Policy");
			pricing = new Pricing(arc, numArcs, PRICINGPOLICY, P0, P1, P3, VOT, MAXTOLL);
						
			//Output the current network including link flows, capacity and free-flow speeds
			//System.out.println("Step7: Writing Iteration Results to Output Files");
			iterationOutput = new OutputProcessing(arc, numArcs, moeList[iteration], dir4, iterationOutputFile, iteration);
			
			//Reset values for the next iteration
			//System.out.println("Step8: Reset Values for the Next Iteration");
			//Update traffic demand for the next iteration; uniform traffic increase in all zones by X%
			if(fixedDemand == false){
				totalODFlow = 0;
				for(int i = 0; i < numZones; i++){
					zone[i].production = Math.round((float)(zone[i].production)*(1 + X));
					totalODFlow += zone[i].production;
					zone[i].attraction = Math.round((float)(zone[i].attraction)*(1 + X));
				}
			}
			//Update arc capacity and toll in the travel demand model
			dg = new DirectedGraph(numArcs, numZones, arc);
			evolve = new Evolve(dg, totalODFlow, zone);
			/*
			for(int i = 0; i < numArcs; i++){
				evolve.link[arc[i].oNode][arc[i].dNode][2] = arc[i].capacity;
				if (evolve.link[arc[i].oNode][arc[i].dNode][2] > 0){
					evolve.link[arc[i].oNode][arc[i].dNode][5] = evolve.link[arc[i].oNode][arc[i].dNode][3]*(1+THETA1*Math.pow((evolve.link[arc[i].oNode][arc[i].dNode][4]/evolve.link[arc[i].oNode][arc[i].dNode][2]),THETA2));
				}
				evolve.link[arc[i].oNode][arc[i].dNode][6] = arc[i].toll;
			}
			*/
			
			//Update average vc ratio for investment policy case 6 only
				if(INVESTMENTPOLICY == '6'){
					aveVC = 0;
					for(int i = 0; i < numArcs; i++){
						aveVC += arc[i].vCRatio;
					}
					aveVC = aveVC/(float)numArcs;
				}
			//Delete class instances to release memory
			//Evaluate convergence
			////converged = evaluateConvergence(iteration);			
			//iteration counter adds one
			iteration ++;
			
			System.out.println();
			
		}while(converged == false);
	}
	
	public void end() throws IOException {
		System.out.println("####### END #######");
	}
	
	public boolean evaluateConvergence (int iteration){
		//minimum number of iterations 
		if(iteration < MINIMUM_CONVERGENCE_DURATION){
			historicalConvergence[iteration] = investment.getConvergence();
			return false;
		}
		//maximum number of iterations
		else if(iteration > 100){
			return true;
		}
		//if the network does not change in the past MINIMUM_CONVERGENCE_DURATION, a convergence is achived
		else{
			tempBool = true;
			for(int i = 0; i < MINIMUM_CONVERGENCE_DURATION - 1; i++){
				historicalConvergence[i] = historicalConvergence[i + 1];
				if(historicalConvergence[i] == false)tempBool = false;
			}
			historicalConvergence[MINIMUM_CONVERGENCE_DURATION - 1] = investment.getConvergence();
			if(historicalConvergence[MINIMUM_CONVERGENCE_DURATION - 1] == false)tempBool = false;
			return tempBool;		
		}
	}
	
}
