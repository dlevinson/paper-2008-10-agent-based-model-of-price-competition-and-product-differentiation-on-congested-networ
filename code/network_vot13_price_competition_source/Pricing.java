/**
 * @author Lei Zhang
 * 			Jan 21, 2004
 */
import Jama.Matrix;


public class Pricing {
	
	private Matrix X, XT, XX, Y, B;
	private float temp,  maxTollPerMile, currentToll;
	private final static float STEP = (float)0.5;
	
	public Pricing() {}
	
	public Pricing(Arc[] arc, int numArcs, char pricingPolicy, float p0, float p1, float p3, float vot, float maxTollPerMile){
		
		this.maxTollPerMile = maxTollPerMile;
		
		switch(pricingPolicy){
			case '1': //Regulated prices based on travel distance and level of service 
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						arc[i].toll = (float)(p0*Math.pow(arc[i].length, p1)*Math.pow(arc[i].ffSpeed, p3));
					}
				}							
			break;
			case '2': //Regulated prices based on travel distance only
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						//arc[i].toll = (float)(16*p0*Math.pow(arc[i].length, p1)); //"16" scaling coefficient
						arc[i].toll = (float)(13*p0*Math.pow(arc[i].length, p1));
					}
				}			
			break;
			case '3': //Marginal-cost prices
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						currentToll = arc[i].toll;
						temp = (float)((arc[i].flow/10)*vot*(arc[i].length/arc[i].ffSpeed)*arc[i].theta1*(Math.pow(((arc[i].flow/10)/arc[i].capacity), arc[i].theta2) - Math.pow(((arc[i].flow/10 - 1)/(arc[i].capacity)), arc[i].theta2))); 
						if(arc[i].flow < 10){
							arc[i].toll = (float)0.01;
						}
						else{
							arc[i].toll = temp;
						}
						if(arc[i].toll > 3*currentToll & currentToll > 0){
							arc[i].toll = 3*currentToll;
						}
						if(arc[i].toll > arc[i].length*maxTollPerMile){
							arc[i].toll = arc[i].length*maxTollPerMile;
						}
					}
				}			
			break;
			case '4': //Marginal-cost prices plus maintenance cost recovery
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						currentToll = arc[i].toll;
						temp = (float)((arc[i].flow/10)*vot*(arc[i].length/arc[i].ffSpeed)*arc[i].theta1*(Math.pow(((arc[i].flow/10)/arc[i].capacity), arc[i].theta2) - Math.pow(((arc[i].flow/10 - 1)/(arc[i].capacity)), arc[i].theta2))); 
						if(arc[i].flow < 1){
							arc[i].toll = (float)0.01;
						}
						else{
							arc[i].toll = temp + arc[i].maintenanceCost/(365*arc[i].flow); 
						}
						if(arc[i].toll > 3*currentToll & currentToll > 0){
							arc[i].toll = 3*currentToll;
						}
						if(arc[i].toll > arc[i].length*maxTollPerMile){
							arc[i].toll = arc[i].length*maxTollPerMile;
						}
					}
				}			
			break;
			case '5': //Short-run profit-maximizing + Quadratic Optimization
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if(arc[i].arcIteration == 0){
							System.out.println("!!!WARNING: WRONG ORDER");
						}
						else if(arc[i].arcIteration == 1){
							//arc[i].toll = (float)(p0*Math.pow(arc[i].length, p1)*Math.pow(arc[i].ffSpeed, p3));
							arc[i].toll = (float)(13*p0*Math.pow(arc[i].length, p1));
						}
						else if(arc[i].arcIteration == 2){
							arc[i].toll = arc[i].toll*(1 + STEP);
						}
						else if(arc[i].arcIteration == 3){
							temp = getNextPrice(arc[i], 3);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
							
						}
						else if(arc[i].arcIteration == 4){
							temp = getNextPrice(arc[i], 4);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
						}
						else{
							temp = getNextPrice(arc[i], 5);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
						}
						
					}
				}			
			break;
			
			case '6': //Short-run profit-maximizing + Quadratic Optimization + Price ceiling
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if(arc[i].arcIteration == 0){
							System.out.println("!!!WARNING: WRONG ORDER");
						}
						else if(arc[i].arcIteration == 1){
							//arc[i].toll = (float)(p0*Math.pow(arc[i].length, p1)*Math.pow(arc[i].ffSpeed, p3));
							arc[i].toll = (float)(13*p0*Math.pow(arc[i].length, p1));
						}
						else if(arc[i].arcIteration == 2){
							arc[i].toll = arc[i].toll*(1 + STEP);
						}
						else if(arc[i].arcIteration == 3){
							temp = getNextPrice(arc[i], 3);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
							
						}
						else if(arc[i].arcIteration == 4){
							temp = getNextPrice(arc[i], 4);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
						}
						else{
							temp = getNextPrice(arc[i], 5);
							if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
								arc[i].toll = temp;
							}
							else{
								if(temp < arc[i].toll){
									arc[i].toll = (float)(0.5*arc[i].toll);
								}
								else{
									arc[i].toll = (float)(1.5*arc[i].toll);	
								}
							}
						}
						
						if(arc[i].toll > arc[i].length*maxTollPerMile){
							arc[i].toll = arc[i].length*maxTollPerMile;
						}
						if(arc[i].arcIteration > 5){
							if(arc[i].toll == arc[i].length*maxTollPerMile & arc[i].tollHistory[arc[i].arcIteration - 3] == arc[i].length*maxTollPerMile){
								arc[i].toll = arc[i].toll*(1 - STEP);
							}
						}
					}
				}			
			break;
			
			case 'm': //Mixed Ownership.
					  // Private Roads: Short-run profit-maximizing + Quadratic Optimization
					  // Public Roads: Distance-based user charge 16/180 $/mile
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if(arc[i].privateRoad == 1){
							if(arc[i].arcIteration == 0){
								//System.out.println("Pricing 7" + "\t" + "Link " + i + "\t" + "Toll " + arc[i].toll);
							}
							else if(arc[i].arcIteration == 1){
								arc[i].toll = (float)(10*p0*Math.pow(arc[i].length, p1));
								//arc[i].toll = (float)(1.5*arc[i].tollHistory[0]); //probing market by raising price
							}
							else if(arc[i].arcIteration == 2){
								//arc[i].toll = (float)(0.5*(arc[i].tollHistory[0] + arc[i].tollHistory[1]));
								//arc[i].toll = (float)(0.5*arc[i].tollHistory[0]); //probing market by reducing price
								arc[i].toll = (float)(1.5*arc[i].toll);
							}
							else if(arc[i].arcIteration == 3){
								temp = getNextPrice(arc[i], 3);
								if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
									arc[i].toll = temp;
								}
								else{
									if(temp < arc[i].toll){
										arc[i].toll = (float)(0.5*arc[i].toll);
									}
									else{
										arc[i].toll = (float)(1.5*arc[i].toll);	
									}
								}
							}
							else if(arc[i].arcIteration == 4){
								temp = getNextPrice(arc[i], 4);
								if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
									arc[i].toll = temp;
								}
								else{
									if(temp < arc[i].toll){
										arc[i].toll = (float)(0.5*arc[i].toll);
									}
									else{
										arc[i].toll = (float)(1.5*arc[i].toll);	
									}
								}
							}
							else{ //arcIteration >= 5
								temp = getNextPrice(arc[i], 5);
								if(Math.abs(arc[i].toll - temp)/arc[i].toll <= 0.5){
									arc[i].toll = temp;
								}
								else{
									if(temp < arc[i].toll){
										arc[i].toll = (float)(0.5*arc[i].toll);
									}
									else{
										arc[i].toll = (float)(1.5*arc[i].toll);	
									}
								}
							}
							
							if(arc[i].toll > arc[i].length*maxTollPerMile){
								arc[i].toll = arc[i].length*maxTollPerMile;
							}
							if(arc[i].arcIteration > 5){
								if(arc[i].toll == arc[i].length*maxTollPerMile & arc[i].tollHistory[arc[i].arcIteration - 3] == arc[i].length*maxTollPerMile){
									arc[i].toll = arc[i].toll*(1 - STEP);
								}
							}	
						}
						else{ //public roads
							//arc[i].toll = (float)(16*p0*Math.pow(arc[i].length, p1)); //"16" scaling coefficient
							arc[i].toll = (float)(10*p0*Math.pow(arc[i].length, p1));
						}		
					}
				}			
			break;
			
			case 'f': //fixed prices
				float privateToll = (float)0.320, publicToll = (float)0.05556;
				for(int i = 0; i < numArcs; i++){
					if(arc[i].capacity > 1){
						if(arc[i].privateRoad == 1){
							arc[i].toll = privateToll*arc[i].length;
						}
						else{
							arc[i].toll = publicToll*arc[i].length;
						}
					}
				}
			break;
			
			//Future pricing policies here
						
		}
		
	}
	
	
	private float getNextPrice(Arc currentArc, int n){
		double xData[][] = new double[n][3];
		double yData[] = new double[n];
		
		float b1, b2, b3, newToll;
		float min = 9999999, max = 0;
		int minId = -9, maxId = -9;
		
		for(int i = 0; i < n; i++){
			xData[i][0] = 1;
			xData[i][1] = currentArc.tollHistory[currentArc.arcIteration - i - 1];
			xData[i][2] = currentArc.tollHistory[currentArc.arcIteration - i - 1]*currentArc.tollHistory[currentArc.arcIteration - i - 1];
			yData[i] = currentArc.profitHistory[currentArc.arcIteration - i - 1];
		}
		
		X = new Matrix(xData, n, 3);
		XT = X.transpose();
		XX = XT.times(X);
		Y = new Matrix(yData, n);
		B = new Matrix(3, 1);
		
		
		if(XX.det() == 0){
			//System.out.println("!!!WARNING: Singular Matrix");
			//X.print(14, 2);
			//System.out.println("	Link " + currentArc.oNode + "-->" + currentArc.dNode);
			newToll = currentArc.toll;
		}
		else{
			B = ((XX.inverse()).times(XT)).times(Y);
			b1 = (float)(B.get(0, 0));
			b2 = (float)(B.get(1, 0));
			b3 = (float)(B.get(2, 0));
			
			if(b3 < 0){
				newToll = - b2/(2*b3);
			}
			else{
				for(int i = 0; i < n; i++){
					if(min > xData[i][1]){
						min = (float)(xData[i][1]);
						minId = i;
					}
					if(max < xData[i][1]){
						max = (float)(xData[i][1]);
						maxId = i;
					}
				}
				if(yData[minId] < yData[maxId]){
					newToll = (1 + STEP)*(float)(xData[maxId][1]);
				}
				else{
					newToll = (1 - STEP)*(float)(xData[minId][1]);
				}
			}
		}
		
		return newToll;
	}

	

}
