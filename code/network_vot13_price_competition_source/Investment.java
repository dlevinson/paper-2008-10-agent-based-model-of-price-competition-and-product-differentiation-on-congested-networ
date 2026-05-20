/**
 * @author Lei Zhang
 * 			Sep 1, 2003
 */
import java.math.*;
import java.util.Random;

public class Investment {

	private int intTemp, currentArc, numExpansions, numDegenerations, numDRoads, 
	             numPriExpansions, numPubExpansions, numPriDegenerations, numPubDegenerations, numPriDRoads, numPubDRoads;
	private float floatTemp, totalRevenue, totalMaintenanceCost, totalDisposableRevenue, aveVCRatio; 
	private float capacityChange;
	public float residualRevenue;
	
	private long seed = 9327;
	Random rand; 
	
	public Investment() {}
	
	public Investment(Arc[] arc, int numArcs, char investmentPolicy, float sgm3, float speedCoeff, float residualRevenue, float INVESTMENTPROB){
		this.totalRevenue = 0;
		this.totalMaintenanceCost = 0;
		this.totalDisposableRevenue = 0;
		this.numExpansions = 0;
		this.numDegenerations = 0;
		this.numDRoads = 0;
		this.numPriExpansions = 0;
		this.numPubExpansions = 0;
		this.numPriDegenerations = 0;
		this.numPubDegenerations = 0;
		
		this.numPriDRoads = 0;
		this.numPubDRoads = 0;
		
		this.rand = new Random(seed);
		
		switch(investmentPolicy){
			case '1': //Yerra&Levinson: Decentralized, continuous, no expansion cost fn., no congestion
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){  //centroid arcs should not be considered
						if((2*Math.abs(arc[i].revenue - arc[i].maintenanceCost)/(arc[i].revenue + arc[i].maintenanceCost)) > 0.1){
							if(arc[i].revenue > arc[i].maintenanceCost){
								numExpansions++;
							}
							else{
								numDegenerations++;
							}
							arc[i].ffSpeed = (float)(arc[i].ffSpeed*Math.pow( (arc[i].revenue/arc[i].maintenanceCost), speedCoeff));
							arc[i].fftt = 60*arc[i].length/arc[i].ffSpeed;
						}
					}
				}
				residualRevenue = 0;
				
			break;
			
			/*	
			case '2': //Zhang&Levinson: Decentralized, continuous, no expansion cost fn., congestion 
			System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost");
			for(int i = 0; i < numArcs; i++){
				if(arc[i].capacity > 1){
					if((2*Math.abs(arc[i].revenue - arc[i].maintenanceCost)/(arc[i].revenue + arc[i].maintenanceCost)) > 0.1){
						if(arc[i].revenue > arc[i].maintenanceCost){
							numExpansions++;
						}
						else{
							numDegenerations++;
						}
						//Allow link degeneration.  Otherwise, the following lines of codes should be move up under "numExpansion++;"
						capacityChange = (float)(arc[i].capacity*Math.pow( (arc[i].revenue/arc[i].maintenanceCost), speedCoeff)) - arc[i].capacity;
						arc[i].updateInvestment(capacityChange, 0);
					}
				}
				residualRevenue = 0;
				if((i == 0)||(i == 100)||(i == 200))System.out.println("		Link " + i + "\t" + arc[i].revenue + "\t" + arc[i].maintenanceCost + "\t"+ arc[i].disposableRevenue + "\t" + arc[i].expansionCost + "\t" + arc[i].expansionCost2 + "\t" + arc[i].cumRevenue + "\t" + arc[i].degenerationCost + "\t" + arc[i].minusCapacity + "\t" + arc[i].plusCapacity + "\t" + arc[i].plusCapacity2 + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].numLanes + "\t" + arc[i].speed + "\t" + arc[i].toll + "\t" + arc[i].tt + "\t" + (arc[i].flow/10) + "\t" + arc[i].length + "\t" + arc[i].capacity + "\t" + arc[i].generalizedCost);
			}
			break;

			
			case '3': //decentralized, no saving, continuous, expansion cost fn., congestion
				System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost" + "\t" + "profit");
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if((i == 0)||(i == 100)||(i == 200))System.out.print("		Link " + i + "\t" + arc[i].revenue + "\t" + arc[i].maintenanceCost + "\t"+ arc[i].disposableRevenue + "\t" + arc[i].expansionCost + "\t" + arc[i].expansionCost2 + "\t" + arc[i].cumRevenue + "\t" + arc[i].degenerationCost + "\t" + arc[i].minusCapacity + "\t" + arc[i].plusCapacity + "\t" + arc[i].plusCapacity2 + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].numLanes + "\t" + arc[i].speed + "\t" + arc[i].toll + "\t" + arc[i].tt + "\t" + (arc[i].flow/10) + "\t" + arc[i].length + "\t" + arc[i].capacity + "\t" + arc[i].generalizedCost + "\t" + arc[i].profit + "\t");
						if((2*Math.abs(arc[i].disposableRevenue)/(arc[i].revenue + arc[i].maintenanceCost)) > 0.1){
							capacityChange = (float)(Math.pow((arc[i].disposableRevenue/arc[i].expansionCostCoef), 1/sgm3));
							arc[i].updateInvestment(capacityChange, 0);
							arc[i].cumRevenue = 0;
							if(capacityChange > 0)numExpansions++;
							else numDegenerations++;
						}
					}
					if((i == 0)||(i == 100)||(i == 200))System.out.println(arc[i].capacity);
				}
				residualRevenue = 0;
			break;
			
			
			case '4': //decentralized, discrete, with expansion cost fn., congestion
				System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost");
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if((i == 0)||(i == 100)||(i == 200))System.out.print("		Link " + i + "\t" + arc[i].revenue + "\t" + arc[i].maintenanceCost + "\t"+ arc[i].disposableRevenue + "\t" + arc[i].expansionCost + "\t" + arc[i].expansionCost2 + "\t" + arc[i].cumRevenue + "\t" + arc[i].degenerationCost + "\t" + arc[i].minusCapacity + "\t" + arc[i].plusCapacity + "\t" + arc[i].plusCapacity2 + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].numLanes + "\t" + arc[i].speed + "\t" + arc[i].toll + "\t" + arc[i].tt + "\t" + (arc[i].flow/10) + "\t" + arc[i].length + "\t" + arc[i].capacity + "\t" + arc[i].generalizedCost + "\t");
						if(arc[i].disposableRevenue > arc[i].expansionCost){
							capacityChange = arc[i].plusCapacity;
							arc[i].updateInvestment(capacityChange, 1);
							arc[i].cumRevenue = arc[i].disposableRevenue - arc[i].expansionCost;
							numExpansions++;
						}
						else if(arc[i].disposableRevenue > 0){
							arc[i].cumRevenue = arc[i].disposableRevenue;
						}
						else{ // disposableRevenue < 0; or revenue falls short of maintenance cost
							if(arc[i].disposableRevenue < arc[i].degenerationCost){
								//arc degenerates
								capacityChange = arc[i].minusCapacity;
								arc[i].updateInvestment(capacityChange, -1);
								arc[i].cumRevenue = arc[i].disposableRevenue - arc[i].degenerationCost;
								numDegenerations++;
							}
							else{
								arc[i].cumRevenue = arc[i].disposableRevenue;
							}				
						}
					}
					if((i == 0)||(i == 100)||(i == 200))System.out.println(arc[i].capacity);
				}
				residualRevenue = 0;
			break;
			
			case '5': //centralized, continuous, with expansion cost fn., congestion, expand the link with the highest V/C ratio first
				//Total network revenue and cost
				for(int i = 0; i < numArcs; i++){
					totalRevenue += arc[i].revenue;
					totalMaintenanceCost += arc[i].maintenanceCost;
					totalDisposableRevenue += arc[i].disposableRevenue;
					arc[i].cumRevenue = 0;
				}
				//Investment
				if( 2*(totalDisposableRevenue)/(totalRevenue + totalMaintenanceCost) > 0.1){
					do{
						//Find the average V/C ratio
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							floatTemp += arc[i].vCRatio;
						}
						aveVCRatio = floatTemp/(float)(numArcs);
						//System.out.println("Ave VC Ratio: " + aveVCRatio);
						
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							if(arc[i].vCRatio > floatTemp){
								currentArc = i;
								floatTemp = arc[i].vCRatio;
							}
						}
						
						//System.out.println("Current Arc V/C ratio: " + floatTemp); 
	
						capacityChange = arc[currentArc].flow/(10*aveVCRatio) - arc[currentArc].capacity;
						totalDisposableRevenue -= arc[currentArc].expansionCostCoef*Math.pow(capacityChange, sgm3);
						//System.out.println(totalDisposableRevenue);
						arc[currentArc].updateInvestment(capacityChange, 0);
						numExpansions++;
					}while (totalDisposableRevenue > 0);
				}
				else if(totalDisposableRevenue < 0){
					System.out.println("!!!WARNING: Negative Network Disposable Revenue!!!"); 
				}
				else{
					//System.out.println("Equilibrium Achieved");
				}
				residualRevenue = totalDisposableRevenue;
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
			break;
			*/
			
			case '6'://centralized, discrete, with expansion cost fn., congestion, expand the link with the highest V/C ratio first
				//Total network revenue and cost
				
				for(int i = 0; i < numArcs; i++){
					totalRevenue += arc[i].revenue;
					totalMaintenanceCost += arc[i].maintenanceCost;
					//totalDisposableRevenue += arc[i].disposableRevenue;
					totalDisposableRevenue += arc[i].cumRevenue;
					arc[i].cumRevenue = 0;
				}
				
				
				//Investment
				if( 2*(totalDisposableRevenue)/(totalRevenue + totalMaintenanceCost) > 0.01){
					do{
						//Find the average V/C ratio
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							floatTemp += arc[i].vCRatio;
						}
						aveVCRatio = floatTemp/(float)(numArcs);
						//System.out.println("Ave VC Ratio: " + aveVCRatio);
						
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							if(arc[i].vCRatio > floatTemp){
								currentArc = i;
								floatTemp = arc[i].vCRatio;
							}
						}
						//System.out.println("Current Arc V/C ratio: " + floatTemp); 
	
						//idea capacity change
						capacityChange = arc[currentArc].flow/(10*aveVCRatio) - arc[currentArc].capacity;
						//if expanding one lane is enough, then expand only one lane
						if(capacityChange < arc[currentArc].plusCapacity){
							capacityChange = arc[currentArc].plusCapacity;
							totalDisposableRevenue -= arc[currentArc].expansionCost;
							//System.out.println(totalDisposableRevenue + "\t" + "1 Lane");
							arc[currentArc].updateInvestment(capacityChange, 1);
							numExpansions++;
						}
						//otherwise expand two lanes
						else{
							capacityChange = arc[currentArc].plusCapacity2;
							totalDisposableRevenue -= arc[currentArc].expansionCost2;
							//System.out.println(totalDisposableRevenue + "\t" + "2 Lanes");
							arc[currentArc].updateInvestment(capacityChange, 2);
							numExpansions++;
						}
					}while (totalDisposableRevenue > 0);
				}
				else if(totalDisposableRevenue < 0){
					System.out.println("!!!WARNING: Negative Network Disposable Revenue!!!"); 
				}
				else{
					//System.out.println("Equilibrium Achieved");
				}
				residualRevenue = totalDisposableRevenue;
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
			break;
			
			
			case '7': //Centralized, discrete, with expansion cost fn., expand the link with the highest B/C ratio first
				
				//Total network revenue and cost
				for(int i = 0; i < numArcs; i++){
					totalRevenue += arc[i].revenue;
					totalMaintenanceCost += arc[i].maintenanceCost;
					//totalDisposableRevenue += arc[i].disposableRevenue;
					totalDisposableRevenue += arc[i].cumRevenue;
					arc[i].cumRevenue = 0;
				}
				//Investment according to BC ratios
				//System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost");
				if( 2*(totalDisposableRevenue)/(totalRevenue + totalMaintenanceCost) > 0.01){
					do{
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							if(arc[i].bCRatio > floatTemp){
								currentArc = i;
								floatTemp = arc[i].bCRatio;
							}
						}
						//System.out.println("Current Arc BC ratio: " + floatTemp); 
						if(floatTemp < 1){
							System.out.println("!!!WARNING: BCRatio < 1");
							break;
						}
						//System.out.print("		Link " + currentArc + "\t" + arc[currentArc].revenue + "\t" + arc[currentArc].maintenanceCost + "\t"+ arc[currentArc].disposableRevenue + "\t" + arc[currentArc].expansionCost + "\t" + arc[currentArc].expansionCost2 + "\t" + arc[currentArc].cumRevenue + "\t" + arc[currentArc].degenerationCost + "\t" + arc[currentArc].minusCapacity + "\t" + arc[currentArc].plusCapacity + "\t" + arc[currentArc].plusCapacity2 + "\t" + arc[currentArc].ffSpeed + "\t" + arc[currentArc].fftt + "\t" + arc[currentArc].numLanes + "\t" + arc[currentArc].speed + "\t" + arc[currentArc].toll + "\t" + arc[currentArc].tt + "\t" + (arc[currentArc].flow/10) + "\t" + arc[currentArc].length + "\t" + arc[currentArc].capacity + "\t" + arc[currentArc].generalizedCost + "\t");
						//Expand the link
						if(arc[currentArc].optimalExpansion == 1){
							capacityChange = arc[currentArc].plusCapacity;
							totalDisposableRevenue -= arc[currentArc].expansionCost;
							arc[currentArc].updateInvestment(capacityChange, 1);
							arc[currentArc].bCRatio = (float)0.1; //Make sure this same arc will not be expanded again in this iteration
							numExpansions++;
						}
						else{ //optimalExpansion = 2, >2 considered not practical
							capacityChange = arc[currentArc].plusCapacity2;
							totalDisposableRevenue -= arc[currentArc].expansionCost2;
							arc[currentArc].updateInvestment(capacityChange, 2);
							arc[currentArc].bCRatio = (float)0.1;
							numExpansions++;
						}
						//System.out.println(arc[currentArc].capacity);
					}while (totalDisposableRevenue > 0);
				}
				else if(totalDisposableRevenue < 0){
					System.out.println("!!!WARNING: Negative Network Disposable Revenue!!!"); 
				}
				else{
					//System.out.println("Equilibrium Achieved");
				}
				residualRevenue = totalDisposableRevenue;
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
			break;
			
			
			case '8': //decentralized, discrete, with expansion cost fn., congestion, banking
				//System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost");
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						//if((i == 0)||(i == 100)||(i == 200))System.out.print("		Link " + i + "\t" + arc[i].revenue + "\t" + arc[i].maintenanceCost + "\t"+ arc[i].disposableRevenue + "\t" + arc[i].expansionCost + "\t" + arc[i].expansionCost2 + "\t" + arc[i].cumRevenue + "\t" + arc[i].degenerationCost + "\t" + arc[i].minusCapacity + "\t" + arc[i].plusCapacity + "\t" + arc[i].plusCapacity2 + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].numLanes + "\t" + arc[i].speed + "\t" + arc[i].toll + "\t" + arc[i].tt + "\t" + (arc[i].flow/10) + "\t" + arc[i].length + "\t" + arc[i].capacity + "\t" + arc[i].generalizedCost + "\t");
						if(arc[i].optimalInvestment == 1 & rand.nextFloat() < INVESTMENTPROB){
							capacityChange = arc[i].plusCapacity;
							arc[i].updateInvestment(capacityChange, 1);
							numExpansions++;
						}
						else if(arc[i].optimalInvestment == 2 & rand.nextFloat() < INVESTMENTPROB){
							capacityChange = arc[i].plusCapacity2;
							arc[i].updateInvestment(capacityChange, 2);
							numExpansions++;
						}
						else{ // optimalInvestment == 0; 
							//if(arc[i].cumDeficit < arc[i].degenerationCost){
							if(arc[i].cumRevenue < arc[i].degenerationCost){
								if(arc[i].capacity > 400 & arc[i].numLanes > 1){
									//arc degenerates
									capacityChange = arc[i].minusCapacity;
									if(arc[i].capacity + capacityChange < 400){
										capacityChange = 400 - arc[i].capacity;
									}
									arc[i].updateInvestment(capacityChange, -1);
									//arc[i].cumDeficit = 0;
									arc[i].cumRevenue = 0;
									numDegenerations++;
								}
								else{ //capacity == 400 or numLanes ==1
									arc[i].capacity = 400;
									arc[i].numLanes = 1;
									numDRoads++;
								}
							}
						}
					}
					
					//if((i == 10000)||(i == 15000)||(i == 20000))System.out.println(arc[i].capacity);
					//if((i == 10000)||(i == 15000)||(i == 20000))System.out.println("		Link " + i + "\t" + arc[i].revenue + "\t" + arc[i].maintenanceCost + "\t"+ arc[i].disposableRevenue + "\t" + arc[i].expansionCost + "\t" + arc[i].expansionCost2 + "\t" + arc[i].cumRevenue + "\t" + arc[i].degenerationCost + "\t" + arc[i].minusCapacity + "\t" + arc[i].plusCapacity + "\t" + arc[i].plusCapacity2 + "\t" + arc[i].ffSpeed + "\t" + arc[i].fftt + "\t" + arc[i].numLanes + "\t" + arc[i].speed + "\t" + arc[i].toll + "\t" + arc[i].tt + "\t" + (arc[i].flow/10) + "\t" + arc[i].length + "\t" + arc[i].capacity + "\t" + arc[i].generalizedCost);
				}
				residualRevenue = 0;
			break;
			
			
			case '9': //Centralized, discrete, with expansion cost fn., expand all links with >1 bc ratios subject to disposable revenue
						// This case 9 is similar to case 7 except that in case 9 revenue will not be used for undesirabel expansion projects. In case 7, all revenue must be re-investment even when the investment is socially undesirable
				//Total network revenue and cost
				for(int i = 0; i < numArcs; i++){
					totalRevenue += arc[i].revenue;
					totalMaintenanceCost += arc[i].maintenanceCost;
					//totalDisposableRevenue += arc[i].disposableRevenue;
					arc[i].cumRevenue = 0;
				}
				totalDisposableRevenue = totalRevenue + residualRevenue - totalMaintenanceCost;
				//Investment according to BC ratios
				//System.out.println("		LinkId " + "\t" + "revenue" + "\t" + "maintenanceCost" + "\t"+ "disposableRevenue" + "\t" + "expansionCost" + "\t" + "expansionCost2" + "\t" + "cumRevenue" + "\t" + "degenerationCost" + "\t" + "minusCapacity" + "\t" + "plusCapacity" + "\t" + "plusCapacity2" + "\t" + "ffSpeed" + "\t" + "fftt" + "\t" + "numLanes" + "\t" + "speed" + "\t" + "toll" + "\t" + "tt" + "\t" + "flow" + "\t" + "length" + "\t" + "capacity" + "\t" + "generalizedCost");
				if( 2*(totalDisposableRevenue)/(totalRevenue + totalMaintenanceCost) > 0.01){
					do{
						floatTemp = 0;
						for(int i = 0; i < numArcs; i++){
							if(arc[i].bCRatio > floatTemp){
								currentArc = i;
								floatTemp = arc[i].bCRatio;
							}
						}
						//System.out.println("Current Arc BC ratio: " + floatTemp); 
					
						if(floatTemp < 1){
							//System.out.println("ERROR!!!: BCRatio < 1");
							break;
						}
						//System.out.print("		Link " + currentArc + "\t" + arc[currentArc].revenue + "\t" + arc[currentArc].maintenanceCost + "\t"+ arc[currentArc].disposableRevenue + "\t" + arc[currentArc].expansionCost + "\t" + arc[currentArc].expansionCost2 + "\t" + arc[currentArc].cumRevenue + "\t" + arc[currentArc].degenerationCost + "\t" + arc[currentArc].minusCapacity + "\t" + arc[currentArc].plusCapacity + "\t" + arc[currentArc].plusCapacity2 + "\t" + arc[currentArc].ffSpeed + "\t" + arc[currentArc].fftt + "\t" + arc[currentArc].numLanes + "\t" + arc[currentArc].speed + "\t" + arc[currentArc].toll + "\t" + arc[currentArc].tt + "\t" + (arc[currentArc].flow/10) + "\t" + arc[currentArc].length + "\t" + arc[currentArc].capacity + "\t" + arc[currentArc].generalizedCost + "\t");
						//Expand the link
						if(arc[currentArc].optimalExpansion == 1 & rand.nextFloat() < INVESTMENTPROB){
							capacityChange = arc[currentArc].plusCapacity;
							totalDisposableRevenue -= arc[currentArc].expansionCost;
							arc[currentArc].updateInvestment(capacityChange, 1);
							arc[currentArc].bCRatio = (float)0.1; //Make sure this same arc will not be expanded again in this iteration
							numExpansions++;
						}
						else if(arc[currentArc].optimalInvestment == 2 & rand.nextFloat() < INVESTMENTPROB){ //optimalExpansion = 2, >2 considered not practical
							capacityChange = arc[currentArc].plusCapacity2;
							totalDisposableRevenue -= arc[currentArc].expansionCost2;
							arc[currentArc].updateInvestment(capacityChange, 2);
							arc[currentArc].bCRatio = (float)0.1;
							numExpansions++;
						}
						else{
							//System.out.println("Error: > 2 Optimal Investment in Investment.java");		
							arc[currentArc].bCRatio = (float)0.1; 
						}
						//System.out.println(arc[currentArc].capacity);
					}while (totalDisposableRevenue > 0 & floatTemp > 1);
				}
				else if(totalDisposableRevenue < 0){
					//System.out.println("!!!WARNING: Negative Network Disposable Revenue!!!"); 
				}
				else{
					//System.out.println("Equilibrium Achieved");
				}
				residualRevenue = totalDisposableRevenue;
				//System.out.println();
				//System.out.println();
				//System.out.println();
				//System.out.println();
			break;
			
			case 'm': //Mixed ownership
					  // Private Roads: Long-term profit maximization, 1- or 2-lane expansion
				
				//Private Roads			
				
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if(arc[i].privateRoad == 1){
							if(arc[i].optimalInvestment == 1){
								capacityChange = arc[i].plusCapacity;
								arc[i].updateInvestment(capacityChange, 1);
								numExpansions++;
								numPriExpansions++;
							}
							else if(arc[i].optimalInvestment == 2){
								capacityChange = arc[i].plusCapacity2;
								arc[i].updateInvestment(capacityChange, 2);
								numExpansions++;
								numPriExpansions++;
							}
							else{ // optimalInvestment == 0; 
								//if(arc[i].cumDeficit < arc[i].degenerationCost){
								if(arc[i].cumRevenue < arc[i].degenerationCost){
									if(arc[i].capacity > 400 & arc[i].numLanes > 1){
										//arc degenerates
										capacityChange = arc[i].minusCapacity;
										if(arc[i].capacity + capacityChange < 400){
											capacityChange = 400 - arc[i].capacity;
										}
										arc[i].updateInvestment(capacityChange, -1);
										//arc[i].cumDeficit = 0;
										arc[i].cumRevenue = 0;
										numDegenerations++;
										numPriDegenerations++;
									}
									else{ //capacity == 400 or numLanes ==1
										arc[i].capacity = 400;
										arc[i].numLanes = 1;
										numDegenerations++;
										numPriDegenerations++;
										numDRoads++;
									}
								}
							}
						}
					}
				}
				
				//Public Roads
				
				//Total revenue and cost for all public roads
				for(int i = 0; i < numArcs; i++){
					if(arc[i].privateRoad == 0 & arc[i].capacity > 1){
						totalRevenue += arc[i].revenue;
						totalMaintenanceCost += arc[i].maintenanceCost;
						//totalDisposableRevenue += arc[i].disposableRevenue;
						arc[i].cumRevenue = 0;
					}
				}
				totalDisposableRevenue = totalRevenue + residualRevenue - totalMaintenanceCost;
				//Investment according to BC ratios
				if( 2*(totalDisposableRevenue)/(totalRevenue + totalMaintenanceCost) > 0.01){
					do{
						floatTemp = 0; //used to record the BC ratio of the link currently being expanded
						for(int i = 0; i < numArcs; i++){
							if(arc[i].privateRoad == 0){
								if(arc[i].privateCompetitor == 0){
									if(arc[i].bCRatio > floatTemp){
										currentArc = i;
										floatTemp = arc[i].bCRatio;
									}
								}
								else if(arc[i].consession == 0){
									if(arc[i].bCRatio > floatTemp){
										currentArc = i;
										floatTemp = arc[i].bCRatio;
									}
								}
								else{
									//do nothing
								}
							}
						}
						//System.out.println("Current Arc BC ratio: " + floatTemp); 
						if(floatTemp < 1){
							System.out.println("WARNING!!!: BCRatio < 1");
							break;
						}
						else{
							//Expand the link
							if(arc[currentArc].optimalExpansion == 1){
								capacityChange = arc[currentArc].plusCapacity;
								totalDisposableRevenue -= arc[currentArc].expansionCost;
								arc[currentArc].updateInvestment(capacityChange, 1);
								arc[currentArc].bCRatio = (float)0.1; //Make sure this same arc will not be expanded again in this iteration
								numExpansions++;
								numPubExpansions++;
							}
							else if(arc[currentArc].optimalExpansion == 2){ //optimalExpansion = 2, >2 considered not practical
								capacityChange = arc[currentArc].plusCapacity2;
								totalDisposableRevenue -= arc[currentArc].expansionCost2;
								arc[currentArc].updateInvestment(capacityChange, 2);
								arc[currentArc].bCRatio = (float)0.1;
								numExpansions++;
								numPubExpansions++;
							}
							else{
								System.out.println("ERROR!!!: Max BCRatio But Optimal Investment is Zero.");
							}
							//System.out.println(arc[currentArc].capacity);
						}
						
					}while (totalDisposableRevenue > 0 & floatTemp > 1); //Stop if no more money or if no public roads are worth expanding - BCRatio < 1
				}
				else if(totalDisposableRevenue < 0){
					System.out.println("!!!WARNING: Negative Network Disposable Revenue!!!"); 
				}
				else{
					System.out.println("Public Roads Investment Equilibrium Achieved");
				}
				residualRevenue = totalDisposableRevenue;
			break; //End of case 'm'
			
			
			/*	
			case 'd': //dicentralized autonomous links
				for(int i = 0; i < numArcs; i++){
					if((arc[i].capacity > 1)&(arc[i].flow > 1)){
						if(arc[i].disposableRevenue >= arc[i].expansionCost){
							//expansion
							arc[i].numLanes++;
							arc[i].capacity += 2000*arc[i].ffSpeed/60;
							if(arc[i].numLanes == 2){
								arc[i].ffSpeed = (float)(arc[i].ffSpeed*1.5);
							}
							else if(arc[i].numLanes == 3){
								arc[i].ffSpeed = (float)(arc[i].ffSpeed*1.35);
							}
							else{
							}
							arc[i].fftt = 60*arc[i].length/arc[i].ffSpeed;
							arc[i].cumRevenue = arc[i].disposableRevenue - arc[i].expansionCost;
							numExpansions++;
						}
						else if(arc[i].disposableRevenue >= 0){
							arc[i].cumRevenue = arc[i].disposableRevenue;
						}
						else{
							arc[i].ffSpeed = (float)(arc[i].ffSpeed*(1 - DEGENERATION_RATE*(-arc[i].disposableRevenue/arc[i].maintenanceCost)));
							arc[i].fftt = arc[i].length/arc[i].ffSpeed;
							arc[i].cumRevenue = 0;
							numDegenerations++;
						}
					}
				}
			break;
			
			case 'c': //centralized
				//new network revenue
				for(int i = 0; i < numArcs; i++){
					networkRevenue += arc[i].disposableRevenue;
				}
				//expansion
				for(int i = 0; i < (numArcs - 1); i++){
					//expand the arcs with the highest v/C ratio first
					for(int j = (i + 1); j < numArcs; j++){
						if(arc[i].vCRatio < arc[j].vCRatio){
							temp = arcOrder[i];
							arcOrder[i] = arcOrder[j];
							arcOrder[j] = temp;
						}
					}
					currentArc = arcOrder[i];
					if((networkRevenue > arc[currentArc].expansionCost)&(arc[i].capacity > 1)){
						arc[currentArc].numLanes++;
						arc[currentArc].capacity += 2000*arc[currentArc].ffSpeed/60;
						if(arc[currentArc].numLanes == 2){
							arc[currentArc].ffSpeed = (float)(arc[currentArc].ffSpeed*1.5);
						}
						else if(arc[currentArc].numLanes == 3){
							arc[currentArc].ffSpeed = (float)(arc[currentArc].ffSpeed*1.35);
						}
						else{
						}
						arc[currentArc].fftt = 60*arc[currentArc].length/arc[currentArc].ffSpeed;
						networkRevenue -= arc[currentArc].expansionCost;
						numExpansions++;
					}
					else{
						break;
					}
					
				}
			break;
			*/
				
		}
		System.out.print("+" + "\t" + numExpansions + "\t");
		System.out.print("-" + "\t" + numDegenerations + "\t");
		System.out.println("D" + "\t" + numDRoads);
	}
	
	public boolean getConvergence(){
		if((numExpansions + numDegenerations)== 0){
			return true;
		}
		else{
			return false;
		}
	}


}
