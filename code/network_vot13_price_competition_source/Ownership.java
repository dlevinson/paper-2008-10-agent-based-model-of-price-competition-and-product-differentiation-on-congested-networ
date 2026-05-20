/**
 * @author Lei Zhang
 * 	10/21/2006
 */
import java.math.*;
import java.util.Random;

public class Ownership {

	public int newNumArcs, numNewPriRoads;
	public Arc[] newArc;
	private Arc tempArc;
	
	private int oNode, dNode, type, numLanes, privateRoad, privateCompetitor;
	private float length, capacity, fftt, toll; 	
	
	private int currentArc;
	
	private long seed = 9327;
	Random rand; 
	
	public Ownership() {}
	
	public Ownership(Arc[] arc, int numArcs, int originalNumArcs, char ownershipPolicy, int consession, float markUp, float THETA1, float THETA2, int Y, int AVETIME, float PRIVATIZATIONPROB){
		
		this.numNewPriRoads = 0;
		this.newNumArcs = numArcs;
		this.newArc = new Arc[2*originalNumArcs];
		//this.arcCopy =arc;
		this.currentArc = 0;
		this.rand = new Random(seed);
		
		switch(ownershipPolicy){
			
			
			case 'p': //Pure ownership, all public roads or all private roads
				//do nothing
			break;
			
			case 'm': //Initially all public roads. New private roads parallel to existing public roads 
					  // are introduced. However, existing public roads will not be privatized. Private roads
					  // once constructed will not be bought by the public sector in any case.
				
				for(int i = 0; i < numArcs; i++){
					newArc[currentArc] = arc[i];
					//System.out.println("arc[i].onode:  " + arc[i].oNode);
					currentArc++;
					
					if(arc[i].profitability == 1 & rand.nextFloat() < PRIVATIZATIONPROB){
						newArc[currentArc - 1].privateCompetitor = 1;
						newArc[currentArc - 1].consession = consession;
						newArc[currentArc - 1].profitability = 0;
						this.oNode = arc[i].oNode;
						this.dNode = arc[i].dNode;
						this.type = arc[i].type;
						this.numLanes = 1;
						this.length = arc[i].length;
						this.fftt = arc[i].fftt;
						this.capacity = arc[i].plusCapacity;
						this.toll = arc[i].privateToll1;
						this.privateRoad = 1;
						this.privateCompetitor = -9;
						this.tempArc = new Arc(oNode, dNode, length, capacity, fftt, fftt, THETA1, THETA2, toll, type, numLanes, Y, privateRoad, privateCompetitor, consession, markUp, AVETIME);
						newArc[currentArc] = tempArc;
						currentArc++;
						numNewPriRoads++;
					}
					else if(arc[i].profitability == 2 & rand.nextFloat() < PRIVATIZATIONPROB){
						newArc[currentArc - 1].privateCompetitor = 1;
						newArc[currentArc - 1].consession = consession;
						newArc[currentArc - 1].profitability = 0;
						this.oNode = arc[i].oNode;
						this.dNode = arc[i].dNode;
						this.type = arc[i].type;
						this.numLanes = 2;
						this.length = arc[i].length;
						this.fftt = arc[i].fftt;
						this.capacity = arc[i].plusCapacity2;
						this.toll = arc[i].privateToll2;
						this.privateRoad = 1;
						this.privateCompetitor = -9;
						this.tempArc = new Arc(oNode, dNode, length, capacity, fftt, fftt, THETA1, THETA2, toll, type, numLanes, Y, privateRoad, privateCompetitor, consession, markUp, AVETIME);
						newArc[currentArc] = tempArc;
						currentArc++;	
						numNewPriRoads++;				
					}
					else{
						//do nothing
					}
				}
				
				this.newNumArcs = currentArc;
				
				
				
				//System.out.println("		Num of New Private Toll Roads:" + "\t" + numNewPriRoads);
			
			break;
		}
	
	}
	
}
