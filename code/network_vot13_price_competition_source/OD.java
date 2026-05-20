
public class OD {
	public int o, d;
	public float flow[], flowH[], flowM[], flowL[],
				 price[], priceH[], priceM[], priceL[],
				 //gCost[], gCostH[], gCostM[], gCostL[],
				 w[], wH[], wM[], wL[],
				 giniTime[], giniToll[], giniIncome[],
				 diffTime[], diffToll[];
	public boolean priceSet, priceHSet, priceMSet, priceLSet, nonZero, GiniExist;

	
	public OD(int origin, int destination, int numIterations){
		this.o = origin;
		this.d = destination;
		this.flow = new float[numIterations];
		this.flowH = new float[numIterations];
		this.flowM = new float[numIterations];
		this.flowL = new float[numIterations];
		this.price = new float[numIterations];
		this.priceH = new float[numIterations];
		this.priceM = new float[numIterations];
		this.priceL = new float[numIterations];
		//this.gCost = new float[numIterations];
		//this.gCostH = new float[numIterations];
		//this.gCostM = new float[numIterations];
		//this.gCostL = new float[numIterations];
		this.w = new float[numIterations];
		this.wH = new float[numIterations];
		this.wM = new float[numIterations];
		this.wL = new float[numIterations];
		this.giniTime = new float[numIterations];
		this.giniToll = new float[numIterations];		
		this.giniIncome = new float[numIterations];	
		this.diffTime = new float[numIterations];	
		this.diffToll = new float[numIterations];	
		this.priceSet = false;
		this.priceHSet = false;
		this.priceMSet = false;
		this.priceLSet = false;
		this.nonZero = false;
		this.GiniExist = true;
	}
	
	public void update(int iteration, int numAutos, Auto[] auto, Evolve elove){
		int odFlow = 0, odFlowH = 0, odFlowM = 0, odFlowL = 0;
		float gCost = 0, gCostH = 0, gCostM = 0, gCostL = 0;
		float[] time, toll, income, tempTime, tempToll, tempIncome, incomeDist, timeDist, tollDist;
		tempTime = new float[numAutos];
		tempToll = new float[numAutos];
		tempIncome = new float[numAutos];
		incomeDist = new float[3];
		timeDist = new float[3];
		tollDist = new float[3];
		for(int i = 0; i < 3; i++){
			incomeDist[i] = 0;
			timeDist[i] = 0;
			tollDist[i] = 0;
		}
		for(int i = 0; i < numAutos; i++){
			if(auto[i].origin == o & auto[i].destination == d){
				tempTime[odFlow] = (float)auto[i].route_info[1];
				tempToll[odFlow] = Math.max(0, (float)(auto[i].route_info[2] - auto[i].vot*auto[i].route_info[1]));
				tempIncome[odFlow] = (float)auto[i].vot;
				gCost += (float)(auto[i].route_info[2]);
				odFlow++;
				
				if(auto[i].income == 'H'){
					gCostH += (float)(auto[i].route_info[2]);
					odFlowH++;
					incomeDist[0]++;
					timeDist[0] += auto[i].route_info[1];
					tollDist[0] += Math.max(0, (float)(auto[i].route_info[2] - auto[i].vot*auto[i].route_info[1]));
				}
				else if(auto[i].income == 'M'){
					gCostM += (float)(auto[i].route_info[2]);
					odFlowM++;
					incomeDist[1]++;
					timeDist[1] += auto[i].route_info[1];
					tollDist[1] += Math.max(0, (float)(auto[i].route_info[2] - auto[i].vot*auto[i].route_info[1]));
					
				}
				else if(auto[i].income == 'L'){
					gCostL += (float)(auto[i].route_info[2]);
					odFlowL++;
					incomeDist[2]++;
					timeDist[2] += auto[i].route_info[1];
					tollDist[2] += Math.max(0, (float)(auto[i].route_info[2] - auto[i].vot*auto[i].route_info[1]));
				}
				else{
					System.out.println("!!!ERROR: INCOME CATEGORY in OD.java");
				}
			}
		}
		gCost = gCost/odFlow;
		gCostH = gCostH/odFlowH;
		gCostM = gCostM/odFlowM;
		gCostL = gCostL/odFlowL;
		
		time = new float[odFlow];
		toll = new float[odFlow];
		income = new float[odFlow];
		for(int i = 0; i < odFlow; i++){
			time[i] = tempTime[i];
			toll[i] = tempToll[i];
			income[i] = tempIncome[i];
		}
		
		timeDist[0] = timeDist[0]/odFlowH;
		timeDist[1] = timeDist[1]/odFlowM;
		timeDist[2] = timeDist[2]/odFlowL;
		tollDist[0] = tollDist[0]/odFlowH;
		tollDist[1] = tollDist[1]/odFlowM;
		tollDist[2] = tollDist[2]/odFlowL;
		
		if(o == 1){
			//System.out.println(tollDist[0] + "\t" + tollDist[1] + "\t" +  tollDist[2]);
		}
		
		if(timeDist[2] > 0 & tollDist[2] > 0){
			this.diffTime[iteration] = timeDist[0]/timeDist[2];
			this.diffToll[iteration] = tollDist[0]/tollDist[2];	
			this.nonZero = true;
		}
				
		this.flow[iteration] = odFlow;
		this.flowH[iteration] = odFlowH;
		this.flowM[iteration] = odFlowM;
		this.flowL[iteration] = odFlowL;
		this.price[iteration] = gCost;
		this.priceH[iteration] = gCostH;
		this.priceM[iteration] = gCostM;
		this.priceL[iteration] = gCostL;
		this.giniIncome[iteration] = gini(incomeDist);
		
		this.giniTime[iteration] = giniLargeProduct(timeDist);
		this.giniToll[iteration] = giniLargeProduct(tollDist);
		//this.giniTime[iteration] = giniLarge(timeDist);
		//this.giniToll[iteration] = giniLarge(tollDist);
		//this.giniTime[iteration] = gini(timeDist);
		//this.giniToll[iteration] = gini(tollDist);
		if(o == 1){
			//System.out.println(giniToll[iteration] + "\t" + giniTime[iteration]);
		}
		
		//this.giniTime[iteration] = gini(time);
		//this.giniToll[iteration] = gini(toll);
	
		
		
		if(flow[iteration] > 5 & priceSet == false){ //>5 to ensure representativeness. This will eliminate about 1% of autos from welfare analysis, but ensures accurancy.
			price[0] = price[iteration];
			priceSet = true;
		}
		if(flowH[iteration] > 1 & priceHSet == false){
			priceH[0] = priceH[iteration];
			priceHSet = true;
		}
		if(flowM[iteration] > 1 & priceMSet == false){
			priceM[0] = priceM[iteration];
			priceMSet = true;
		}
		if(flowL[iteration] > 1 & priceLSet == false){
			priceL[0] = priceL[iteration];
			priceLSet = true;
		}
				
		//System.out.println(o + "-" + d + "\t" + flow[iteration] + "\t" + flowH[iteration] + "\t" + flowM[iteration] + "\t" + flowL[iteration] + "\t" + price[iteration] + "\t" + priceH[iteration] + "\t" + priceM[iteration] + "\t" + priceL[iteration] + "\t" + giniTime[iteration] + "\t" + giniToll[iteration] + "\t" + giniIncome[iteration]);
	}
	
	public float gini(float [] list){
		int n;
		float gini = 0, average;
		n = list.length;
		average = average(list);
		if(average == 0){
			average = (float)0.001;
		}
		
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				gini += Math.abs(list[i] - list[j]);
			}
		}
		gini = gini/(2*n*n*average);
		return gini;
	}

	public float giniLarge(float [] list){
		int n ;
		float gini = 0, average, min;
		n = list.length;
		
		min = 999999;
		for(int i = 0; i < n; i++){
			if(min > list[i]){
				min = list[i];
			}
		}
		
		for(int i = 0; i < n; i++){
			list[i] = list[i] - min;
			if(list[i] < 0){
				System.out.println("!!!ERROR: Negativity in OD.java, Method: giniLarge");
			}
		}
		
		average = average(list);
		if(average == 0){
			average = (float)0.001;
		}
			
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				gini += Math.abs(list[i] - list[j]);
			}
		}
		gini = gini/(2*n*n*average);
		return gini;
	}
	
	public float giniLargeProduct(float [] list){
		int n ;
		float gini = 0, average, min;
		n = list.length;
		
		min = 999999;
		for(int i = 0; i < n; i++){
			if(min > list[i]){
				min = list[i];
			}
		}
		
		if(min > 0){
			for(int i = 0; i < n; i++){
				list[i] = list[i]/min;
				if(list[i] < 0){
					System.out.println("!!!ERROR: Negativity in OD.java, Method: giniLarge");
				}
			}
			
			average = average(list);
			if(average == 0){
				//average = (float)0.001;
				System.out.println("!!!ERROR: Zero in OD.java, Method: giniLarge");
			}
			
			for(int i = 0; i < n; i++){
				for(int j = 0; j < n; j++){
					gini += Math.abs(list[i] - list[j]);
				}
			}
			gini = gini/(2*n*n*average);
			return gini;
		}
		else{
			this.GiniExist = false;
			return 0;
		}
		
		
	}
	
	public float average(float [] list){
		float ave = 0;
		for(int i = 0; i < list.length; i++)ave += ave + list[i];
		ave = ave/list.length;
		return ave;
	}
	
}
