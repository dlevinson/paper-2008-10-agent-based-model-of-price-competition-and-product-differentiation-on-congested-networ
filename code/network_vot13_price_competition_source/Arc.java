/**
 * @author Lei Zhang
 * 			Nov 7, 2002
 */
import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class Arc {
	public float flow, length, capacity, fftt, ffSpeed, speed, theta1, theta2, 
				toll, tt, vCRatio, additionalCost, generalizedCost, revenue,
				cumRevenue, maintenanceCost, expansionCost, expansionCost2,
				degenerationCost, expansionCostCoef, profit, perLaneCapacity, plusCapacity, 
				plusCapacity2, minusCapacity, bCRatio, amortizedExpansionCost;
	public float initFlow, initCapacity, initFftt, initFfSpeed, initSpeed, intiCost,
				initToll, initTt, initCSFlow, initCSCost, NEWCOSTMARKUP, privateToll1, privateToll2, aveFlow;
	public int oNode, dNode, type, numLanes, optimalExpansion, optimalInvestment, numAmortizedItems, horizon, profitability;  
	public int[] amortizedCostExpiration;
	public float[] tollHistory, profitHistory, demandHistory, gCostHistory, amortizedCostList, vCHistory;
	public static int HISTORY = 500,  AVETIME;
	public int arcIteration;
	
	//New in version 1.0
	public int privateRoad, privateCompetitor;  //0: false, public; 1: true, private;
	public int consession; 
	//end

	public Arc(){}  
	
	//public Arc(int oNode, int dNode, float length, float capacity, float fftt, float theta1, float theta2, float toll, int type, int numLanes, int horizon, int privateRoad, int privateCompetitor, int consession, float NEWCOSTMARKUP, int AVETIME){
	public Arc(int oNode, int dNode, float length, float capacity, float fftt, float tt, float theta1, float theta2, float toll, int type, int numLanes, int horizon, int privateRoad, int privateCompetitor, int consession, float NEWCOSTMARKUP, int AVETIME){
	
		this.oNode = oNode;
		this.dNode = dNode;
		this.flow = 0;
		this.length = length;
		this.capacity = capacity;
		this.fftt = fftt;
		this.ffSpeed = 60*length/fftt;
		this.speed = 60*length/fftt;
		this.theta1 = theta1;
		this.theta2 = theta2;
		this.toll = toll;
		this.profit = 0;
		this.type = type;
		this.numLanes = numLanes;
		this.tt = tt;
		this.generalizedCost = 0;
		this.revenue = 0;
		this.cumRevenue = 0;
		//this.cumDeficit = 0;
		this.vCRatio = 0;
		this.maintenanceCost = 0;
		this.expansionCost = 0;
		this.expansionCost2 = 0;
		this.degenerationCost = 0;
		this.expansionCostCoef = 0;
		this.perLaneCapacity = (float)(capacity/(float)numLanes);
		this.plusCapacity = perLaneCapacity;
		this.plusCapacity2 = 2*perLaneCapacity;
		this.minusCapacity = - perLaneCapacity;
		this.bCRatio = 0;
		this.optimalExpansion = 0;
		this.numAmortizedItems = 0;
		this.tollHistory = new float[HISTORY];
		this.profitHistory = new float[HISTORY];
		this.demandHistory = new float[HISTORY];
		this.gCostHistory = new float[HISTORY];
		this.vCHistory = new float[HISTORY];
		this.amortizedCostList = new float[500];
		this.amortizedCostExpiration = new int[500];
		this.arcIteration = 0;
		//this.disposableRevenue = 0;
		this.horizon = horizon;
		this.privateRoad = privateRoad;
		this.privateCompetitor = privateCompetitor;
		this.consession = consession;
		this.profitability = 0;
		this.NEWCOSTMARKUP = NEWCOSTMARKUP;
		this.privateToll1 = 0;
		this.privateToll2 = 0;
		this.aveFlow = 0;
		this.AVETIME = AVETIME;
	}
	
	public float getOBACapacity(){
		if(capacity == 0){return(999999);}
		else{return capacity*10;}
	}
	
	public float getOBAToll(){
		if(arcIteration == 0){
			return toll;
		}
		else if(arcIteration == 1){
			return((tollHistory[arcIteration - 1] + toll)/2);
		}
		else if(arcIteration == 2){
			return((tollHistory[arcIteration - 1] + tollHistory[arcIteration - 2] + toll)/3);
		}
		else{
			return((tollHistory[arcIteration - 1] + tollHistory[arcIteration - 2] + tollHistory[arcIteration - 3] + toll)/4);
		}
	}
	
	public void updateMaintenanceCost(float mu, float alfa1, float alfa2, float alfa3){
		if(capacity == 0){
			maintenanceCost = 0;
		}
		else{
			//Speed change only- Bhanu's model
				//maintenanceCost = (float)(mu*Math.pow(length, alfa1)*Math.pow(flow, alfa2)*Math.pow(ffSpeed, alfa3));
			//Capacity and speed changes
				//maintenanceCost = (float)(mu*Math.pow(length, alfa1)*Math.pow(capacity, alfa2)*Math.pow(ffSpeed, alfa3));
			//Lei's Model in the Journal of Regional Science paper
				//maintenanceCost = (float)(mu*Math.pow(length, alfa1)*Math.pow(capacity, alfa2));
			maintenanceCost = (float)(mu*Math.pow(length, alfa1)*Math.pow(capacity, alfa2));
			/*
			if(arcIteration == 0){
				maintenanceCost = 0;
			}
			else{
				maintenanceCost = (float)(mu*Math.pow(length, alfa1)*Math.pow(capacity, alfa2));
			}
			*/
		}
	}
	
		
	public void updateExpansionCost(float sgm0, float sgm1, float sgm2, float sgm3){
		//Directly Based on Rama's regression model
			//expansionCost = (float)(Math.pow(ffSpeed/60, 0.5)*Math.exp(Math.log(length) + 10.2));
		//Continuous
			expansionCostCoef = (float)(sgm0*Math.pow(length, sgm1)*Math.pow(capacity, sgm2));  //the third term changes in capacity is not included here
		//Discrete
			expansionCost = (float)(sgm0*Math.pow(length, sgm1)*Math.pow(capacity, sgm2)*Math.pow(plusCapacity, sgm3));
			expansionCost2 = (float)(sgm0*Math.pow(length, sgm1)*Math.pow(capacity, sgm2)*Math.pow(plusCapacity2, sgm3));
			degenerationCost = (float)(sgm0*Math.pow(length, sgm1)*Math.pow(capacity, sgm2)*Math.pow(minusCapacity, sgm3));
	}
	
	
	public void updateRevenue(float phi, float vot){
		if(capacity == 0){
			revenue = 0;
		}
		else{
			generalizedCost = tt + 60*toll/vot;
			vCRatio = flow/(10*capacity);
			tt = (float)( fftt*(1 + theta1*Math.pow(vCRatio, theta2)) );
			//generalizedCost = tt + 60*toll/vot;
			speed = 60*length/tt;
			revenue = phi*toll*flow;
			
			amortizedExpansionCost = 0;
			for(int i = 0; i < numAmortizedItems; i++){
				if(amortizedCostExpiration[i] > 0){
					amortizedExpansionCost += amortizedCostList[i];
				}
			}
			
			profit = revenue - maintenanceCost - amortizedExpansionCost;
			profitHistory[arcIteration] = profit;
			tollHistory[arcIteration] = toll;
			demandHistory[arcIteration] = flow;
			gCostHistory[arcIteration] = generalizedCost;
			vCHistory[arcIteration] = vCRatio;
			aveFlow = 0;
			if(arcIteration > (AVETIME - 2)){
				for (int i = 0; i < AVETIME; i++){
					aveFlow += demandHistory[arcIteration - i];
				}
				aveFlow = aveFlow/(float)AVETIME;
			}
			//disposableRevenue = cumRevenue + revenue - maintenanceCost;
			cumRevenue += profit;
			//if(profit < 0){
			//	cumDeficit += profit;
			//}
			if(privateCompetitor == 1 & consession > 0){
				consession--;				
			}
			
			arcIteration++;
			
			//Update amortized cost expiration
			for(int i = 0; i < numAmortizedItems; i++){
				if(amortizedCostExpiration[i] > 0){
					amortizedCostExpiration[i]--;
				}
			} 
		}
	}
	
	public void updateBCRatio(float vot, float x, float y, float r, int id, float mu, float alfa1, float alfa2){
		float benefit1 = 0, benefit2 = 0, trafficRate = 1, interestRate = 1, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, BC1, BC2;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		for(int i = 0; i < y; i++){
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity2), theta2)));
			benefit1 += vot*(float)365*trafficRate*flow*ttSavings1/(60*interestRate);
			benefit2 += vot*(float)365*trafficRate*flow*ttSavings2/(60*interestRate);
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain1 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
			
		}
		//BC1 = benefit1/expansionCost;
		//BC2 = benefit2/expansionCost2;
		
		//BC1 = benefit1/(expansionCost + extraMain1);
		//BC2 = benefit2/(expansionCost2 + extraMain2);
		BC1 = benefit1/(2*(expansionCost + extraMain1));
		BC2 = benefit2/(2*(expansionCost2 + extraMain2));
		
		bCRatio = BC1>BC2? BC1:BC2;
		optimalExpansion = BC1>BC2?1:2;
	}
	
	public void updateInvestmentReturns(float vot, float x, float y, float r, float mu, float alfa1, float alfa2){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, investment1, investment2, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, p1, p2;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		
		ttSavings1 = 0;
		
		for(int i = 0; i < y; i++){
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity2), theta2)));
			
			ttSavings1 = (float)Math.min(ttSavings1, 0.32*length*6);
			ttSavings2 = (float)Math.min(ttSavings2, 0.32*length*6);
			
			return1 += vot*(float)365*trafficRate*flow*ttSavings1/(60*interestRate);
			return2 += vot*(float)365*trafficRate*flow*ttSavings2/(60*interestRate);
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain2 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
		}
		//p1 = return1 - expansionCost - extraMain1;
		//p2 = return2 - expansionCost2 - extraMain2;
		p1 = return1 - 2*expansionCost - 2*extraMain1;
		p2 = return2 - 2*expansionCost2 - 2*extraMain2;
		
		if(p1 < 0 & p2 < 0){
			optimalInvestment = 0;
		}
		else{
			optimalInvestment = p1>p2?1:2;
		}
		
		
		//if((oNode == 1722)||(oNode == 1723))System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + extraMain1 + "\t" +  + return2 + "\t" + expansionCost2 + "\t" + extraMain2 + "\t" + optimalInvestment + "\t" + ttSavings1 + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + maintenanceCost + "\t" + newMain1);
					
	}
	
	public void updateInvestmentReturnsAve(float vot, float x, float y, float r, float mu, float alfa1, float alfa2){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, investment1, investment2, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, p1, p2;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		
		ttSavings1 = 0;
		
		for(int i = 0; i < y; i++){
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*newCapacity2), theta2)));
			
			ttSavings1 = (float)Math.min(ttSavings1, 0.32*length*6);
			ttSavings2 = (float)Math.min(ttSavings2, 0.32*length*6);
						
			return1 += vot*(float)365*trafficRate*aveFlow*ttSavings1/(60*interestRate);
			return2 += vot*(float)365*trafficRate*aveFlow*ttSavings2/(60*interestRate);
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain2 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
		}
		//p1 = return1 - expansionCost - extraMain1;
		//p2 = return2 - expansionCost2 - extraMain2;
		p1 = return1 - 2*expansionCost - 2*extraMain1;
		p2 = return2 - 2*expansionCost2 - 2*extraMain2;
		
		if(p1 < 0 & p2 < 0){
			optimalInvestment = 0;
		}
		else{
			optimalInvestment = p1>p2?1:2;
		}
		
		
		//if((oNode == 1722)||(oNode == 1723))System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + extraMain1 + "\t" +  + return2 + "\t" + expansionCost2 + "\t" + extraMain2 + "\t" + optimalInvestment + "\t" + ttSavings1 + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + maintenanceCost + "\t" + newMain1);
					
	}
	
	/*
	public void updateInvestmentReturnsCeilingOld(float vot, float x, float y, float r, float mu, float alfa1, float alfa2, float maxTollPerMile){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, investment1, investment2, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, p1, p2;
		float saving1, saving2;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		for(int i = 0; i < y; i++){
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity2), theta2)));
			saving1 = vot*ttSavings1/60;
			if(saving1 > length*maxTollPerMile){
				saving1 = length*maxTollPerMile;
			}
			saving2 = vot*ttSavings2/60;
			if(saving2 > length*maxTollPerMile){
				saving2 = length*maxTollPerMile;
			}
			return1 += saving1*(float)365*trafficRate*flow/(interestRate);
			return2 += saving2*(float)365*trafficRate*flow/(interestRate);
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain2 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
			
		}
		p1 = return1 - expansionCost - extraMain1;
		p2 = return2 - expansionCost2 - extraMain2;
		if(p1 < 0 & p2 < 0){
			optimalInvestment = 0;
		}
		else{
			optimalInvestment = p1>p2?1:2;
		}
			
	}
	*/
	
	public void updateInvestmentReturnsCeilingNew(float vot, float x, float y, float r, float mu, float alfa1, float alfa2, float maxTollPerMile, float aveVC, int iteration){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, investment1, investment2, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, p1, p2;
		float saving1, saving2, return1A = 0, return2A = 0, return1B = 0, return2B = 0;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		for(int i = 0; i < y; i++){
			return1A += toll*(float)365*trafficRate*(10*plusCapacity*aveVC)/(interestRate);
			return2A += toll*(float)365*trafficRate*(10*plusCapacity2*aveVC)/(interestRate);
						
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*flow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*flow/(10*newCapacity2), theta2)));
			saving1 = vot*ttSavings1;
			if(saving1 > length*maxTollPerMile){
				saving1 = length*maxTollPerMile;
			}
			saving2 = vot*ttSavings2;
			if(saving2 > length*maxTollPerMile){
				saving2 = length*maxTollPerMile;
			}
			return1B += saving1*(float)365*trafficRate*flow/(60*interestRate);
			return2B += saving2*(float)365*trafficRate*flow/(60*interestRate);
			
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain2 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
			
		}
		
		return1 = return1A > return1B?return1A:return1B;
		return2 = return2A > return2B?return2A:return2B;
				
		p1 = return1 - expansionCost - extraMain1;
		p2 = return2 - expansionCost2 - extraMain2;
		if(p1 < 0 & p2 < 0){
			optimalInvestment = 0;
		}
		else{
			optimalInvestment = p1>p2?1:2;
		}
		
		if(iteration == 0){
			optimalInvestment = 0;
		}
			
	}
	
	public void updateInvestmentReturnsCeilingNewAve(float vot, float x, float y, float r, float mu, float alfa1, float alfa2, float maxTollPerMile, float aveVC, int iteration){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, investment1, investment2, extraMain1 = 0, extraMain2= 0, newMain1, newMain2;
		float newCapacity1, newFFTT1, newCapacity2, newFFTT2, ttSavings1, ttSavings2, p1, p2;
		float saving1, saving2, return1A = 0, return2A = 0, return1B = 0, return2B = 0;
		newCapacity1 = capacity + plusCapacity;
		newCapacity2 = capacity + plusCapacity2;
		newFFTT1 = 60*length/capacitySpeed(newCapacity1);
		newFFTT2 = 60*length/capacitySpeed(newCapacity2);
		newMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity1, alfa2));
		newMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(newCapacity2, alfa2));
		for(int i = 0; i < y; i++){
			return1A += toll*(float)365*trafficRate*(10*plusCapacity*aveVC)/(interestRate);
			return2A += toll*(float)365*trafficRate*(10*plusCapacity2*aveVC)/(interestRate);
						
			ttSavings1 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*capacity), theta2))) - (float)(newFFTT1*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*newCapacity1), theta2)));
			ttSavings2 = (float)(fftt*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*capacity), theta2))) - (float)(newFFTT2*(1 + theta1*Math.pow(trafficRate*aveFlow/(10*newCapacity2), theta2)));
			saving1 = vot*ttSavings1;
			if(saving1 > length*maxTollPerMile){
				saving1 = length*maxTollPerMile;
			}
			saving2 = vot*ttSavings2;
			if(saving2 > length*maxTollPerMile){
				saving2 = length*maxTollPerMile;
			}
			return1B += saving1*(float)365*trafficRate*aveFlow/(60*interestRate);
			return2B += saving2*(float)365*trafficRate*aveFlow/(60*interestRate);
			
			extraMain1 += (newMain1 - maintenanceCost)/interestRate;
			extraMain2 += (newMain2 - maintenanceCost)/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
			
		}
		
		return1 = return1A > return1B?return1A:return1B;
		return2 = return2A > return2B?return2A:return2B;
				
		p1 = return1 - expansionCost - extraMain1;
		p2 = return2 - expansionCost2 - extraMain2;
		if(p1 < 0 & p2 < 0){
			optimalInvestment = 0;
		}
		else{
			optimalInvestment = p1>p2?1:2;
		}
		
		if(iteration == 0){
			optimalInvestment = 0;
		}
			
	}
	
	public void updateProfitability(float vot, float x, float y, float r, float mu, float alfa1, float alfa2, float privateTollMarkUp){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, averageRate = 0, annualMain1, annualMain2, main1 = 0, main2 = 0;
		float privateFlow1, privateFlow2, p1, p2;
		annualMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(plusCapacity, alfa2));
		annualMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(plusCapacity2, alfa2));
		
		profitability = 0;
		for(int i = 0; i < y; i++){
			averageRate += trafficRate/interestRate;
			main1 += annualMain1/interestRate;
			main2 += annualMain2/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
		}
		
		
		//Profitability of constructing a one-lane parallel private road
		privateFlow1 = flow*(1/(float)(numLanes + 1));
		privateToll1 = (float)(privateTollMarkUp*vot*0.1*privateFlow1*theta1*(fftt/60)*theta2*(Math.pow(privateFlow1/(10*plusCapacity), (theta2 - 1)))/plusCapacity);
		return1 =(float)( averageRate*365*privateFlow1*privateToll1);
		p1 = return1 - NEWCOSTMARKUP*expansionCost - main1;
		
		//Profitability of constructing a two-lane parallel private road
		privateFlow2 = flow*(2/(float)(numLanes + 2));
		privateToll2 = (float)(privateTollMarkUp*vot*0.1*privateFlow2*theta1*(fftt/60)*theta2*(Math.pow(privateFlow2/(10*plusCapacity2), (theta2 - 1)))/plusCapacity2);
		return2 =(float)(averageRate*365*privateFlow2*privateToll2);
		p2 = return2 - NEWCOSTMARKUP*expansionCost2 - main2;
		
		if(p1 < 0 & p2 < 0){
			profitability = 0;
		}
		else{
			profitability = p1>p2?1:2;
		}
		
		//System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + main1 + "\t" + annualMain1 + "\t" + return2 + "\t" + expansionCost2 + "\t" + main2 + "\t" + annualMain2 + "\t" + optimalInvestment + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + averageRate + "\t" + privateFlow1 + "\t" + privateFlow2 + "\t" + profitability + "\t" + bCRatio);
	
		
		//if(profitability > 1){
		//	System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + main1 + "\t" + annualMain1 + "\t" + return2 + "\t" + expansionCost2 + "\t" + main2 + "\t" + annualMain2 + "\t" + optimalInvestment + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + averageRate + "\t" + privateFlow1 + "\t" + privateFlow2 + "\t" + profitability);
		//}
		
		/*
		//test codes, MUST be deleted/disabled in actual model runs
		if(vCRatio > 2.5){
			profitability = 2;
		}					
		else if(vCRatio > 2){
			profitability = 1;
		}
		else{
			//do nothing
		}	
		if(profitability > 0){
			privateToll = 10*toll;
		}
		*/
	}
	
	public void updateProfitabilityAve(float vot, float x, float y, float r, float mu, float alfa1, float alfa2, float privateTollMarkUp){
		float return1 = 0, return2 = 0, trafficRate = 1, interestRate = 1, averageRate = 0, annualMain1, annualMain2, main1 = 0, main2 = 0;
		float privateFlow1, privateFlow2, p1, p2;
		annualMain1 = (float)(mu*Math.pow(length, alfa1)*Math.pow(plusCapacity, alfa2));
		annualMain2 = (float)(mu*Math.pow(length, alfa1)*Math.pow(plusCapacity2, alfa2));
		
		profitability = 0;
		for(int i = 0; i < y; i++){
			averageRate += trafficRate/interestRate;
			main1 += annualMain1/interestRate;
			main2 += annualMain2/interestRate;
			trafficRate = trafficRate*(1 + x);
			interestRate = interestRate*(1 + r);
		}
		
		
		//Profitability of constructing a one-lane parallel private road
		privateFlow1 = aveFlow*(1/(float)(numLanes + 1));
		privateToll1 = (float)(privateTollMarkUp*vot*0.1*privateFlow1*theta1*(fftt/60)*theta2*(Math.pow(privateFlow1/(10*plusCapacity), (theta2 - 1)))/plusCapacity);
		return1 =(float)( averageRate*365*privateFlow1*privateToll1);
		p1 = return1 - NEWCOSTMARKUP*expansionCost - main1;
		
		//Profitability of constructing a two-lane parallel private road
		privateFlow2 = aveFlow*(2/(float)(numLanes + 2));
		privateToll2 = (float)(privateTollMarkUp*vot*0.1*privateFlow2*theta1*(fftt/60)*theta2*(Math.pow(privateFlow2/(10*plusCapacity2), (theta2 - 1)))/plusCapacity2);
		return2 =(float)(averageRate*365*privateFlow2*privateToll2);
		p2 = return2 - NEWCOSTMARKUP*expansionCost2 - main2;
		
		if(p1 < 0 & p2 < 0){
			profitability = 0;
		}
		else{
			profitability = p1>p2?1:2;
		}
		
		//System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + main1 + "\t" + annualMain1 + "\t" + return2 + "\t" + expansionCost2 + "\t" + main2 + "\t" + annualMain2 + "\t" + optimalInvestment + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + averageRate + "\t" + privateFlow1 + "\t" + privateFlow2 + "\t" + profitability + "\t" + bCRatio);
	
		
		//if(profitability > 1){
		//	System.out.println("		Link " + oNode + "\t" + dNode + "\t" + ffSpeed + "\t" + fftt + "\t" + numLanes + "\t" + speed + "\t" + toll + "\t" + tt + "\t" + flow + "\t" + length + "\t" + capacity + "\t" + generalizedCost+ "\t" + plusCapacity + "\t" + plusCapacity2 + "\t" + return1 + "\t" + expansionCost + "\t" + main1 + "\t" + annualMain1 + "\t" + return2 + "\t" + expansionCost2 + "\t" + main2 + "\t" + annualMain2 + "\t" + optimalInvestment + "\t" + interestRate + "\t" + trafficRate + "\t" + vot + "\t" + averageRate + "\t" + privateFlow1 + "\t" + privateFlow2 + "\t" + profitability);
		//}
		
		/*
		//test codes, MUST be deleted/disabled in actual model runs
		if(vCRatio > 2.5){
			profitability = 2;
		}					
		else if(vCRatio > 2){
			profitability = 1;
		}
		else{
			//do nothing
		}	
		if(profitability > 0){
			privateToll = 10*toll;
		}
		*/
	}
	
		
	public void updateInvestment(float capacityChange, int numLanesChange){
		float temp;
		
		capacity += capacityChange;
		numLanes += numLanesChange;
		ffSpeed = capacitySpeed(capacity);
		fftt = 60*length/ffSpeed;
		vCRatio = flow/(10*capacity);
		//plusCapacity = laneCapacity(numLanes + 1) - capacity;
		//plusCapacity2 = laneCapacity(numLanes + 2) - capacity;
		//minusCapacity = laneCapacity(numLanes - 1) - capacity;
		plusCapacity = perLaneCapacity;
		plusCapacity2 = 2*perLaneCapacity;
		minusCapacity = - perLaneCapacity;
		
		if(numLanesChange > 0){
			if(numAmortizedItems > 100){
				System.out.println("ERROR!!! Update Investemnt Class Arc #2");
			}
			if(numLanesChange == 1){
				amortizedCostList[numAmortizedItems] = expansionCost/(float)(horizon);
				amortizedCostExpiration[numAmortizedItems] = horizon; 
			}
			else if (numLanesChange == 2){
				amortizedCostList[numAmortizedItems] = expansionCost2/(float)(horizon);
				amortizedCostExpiration[numAmortizedItems] = horizon; 
			}
			else{
				System.out.println("ERROR!!! Update Investment Class Arc");
				// do nothing
			}
			numAmortizedItems++; 
			
		}
	}
	
	public void saveInitial(){
		this.initCapacity = this.capacity;
		this.initFfSpeed = this.ffSpeed;
		this.initFftt = this.fftt;
		this.initFlow = this.flow;
		this.initSpeed = this.speed;
		this.initToll = this.toll;
		this.initTt = this.tt;
		this.intiCost = this.generalizedCost;
	}
	
	public void saveInitialCS(float p0, float p1, float vot){
		this.initCSCost = generalizedCost - 60*toll/vot + 60*((float)(16*p0*Math.pow(length, p1)))/vot;
		this.initCSFlow = this.flow;
	}
	
	public float capacitySpeed(float capacity){
		float temp;
		temp = (float)(-30.6 + 9.79*Math.log(capacity));
		if(temp > 5){
			return temp;
		}
		else{
			return 5;
		}
	}
	
	public float laneCapacity(int numLanes){
		float temp;
		temp = (float)(341.4 + 272.6*Math.pow(numLanes,2) + 161.5*numLanes);
		return temp;
	}
}
