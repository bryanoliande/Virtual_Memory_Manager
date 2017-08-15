import java.util.ArrayList;

public class TLB {

	//size: 4 lines
	// LRU: int 0:3
	//0: least recently accessed
	//sp: int
	//f: int (starting frame address, not frame #)
	
	ArrayList<Integer[]> tlb;
	
	TLB()
	{
		this.tlb = new ArrayList<Integer[]>();
		System.out.println("!!!!!!!TLB!!!!!!!!!!!!!!!");
		for(Integer i = 0; i < 4 ; ++i)
		{
			tlb.add(new Integer[4]);
			for(Integer j = 0; j < 4 ; ++j)
			{
				tlb.get(i)[j] = -1;
			}

		}
		
		debugTLB();
	}
	
	void debugTLB()
	{
		for(Integer i = 3; i >= 0 ; --i)
		{
			for(Integer j = 0; j < 4; ++j)
			{
				System.out.println("TLB i = " + i + " j = " + j);
				System.out.println(tlb.get(i)[j]);
			}
			System.out.println("--------------------");
		}
	}
	
	Integer getFrameOnLine(Integer line)
	{
		return tlb.get(line)[3];
	}
	
	Integer findLine(Integer sToFind, Integer pToFind)
	{
		for (Integer line = 3; line >= 0; --line)
		{
			Integer sOnLine = tlb.get(line)[1];
			Integer pOnLine = tlb.get(line)[2];
			
			if ((sOnLine == sToFind) && (pOnLine == pToFind))
			{
				return line;
			}
		}
		// => not in TLB
		return -1;
	}
	
	Integer updateTLB(int line, int s, int p, int f)
	{
		//Not found
		boolean found = false;
		if (line == -1)
		{
			for (Integer i = 3; i >= 0; --i)
			{
				Integer lruCheck = tlb.get(i)[0];
				//Find free spot (LRU not used)
				if (lruCheck == -1)
				{
					line = i;
					found = true;
					//select line with LRU = 0 and set this LRU = 3
					if(line != -1)
					{
						tlb.get(line)[0] = 3;
					}
					
					//replace sp field of that line with the new sp value
					tlb.get(line)[1] = s;
					tlb.get(line)[2] = p;
					
					//replace f field of that line with PM[PM[s] + p]
					tlb.get(line)[3] = f;
					
					
					break;
				}
			}
			
			//couldnt find a free spot
			if (!found)
			{
				for (int i = 3; i >= 0; --i)
				{
					//Find LRU 
					Integer possibleLRU = tlb.get(i)[0];
					if (possibleLRU == 0)
					{
						line = i;
						//select line with LRU = 0 and set this LRU = 3
						if(line != -1)
						{
							tlb.get(line)[0] = 3;
						}
						
						//replace sp field of that line with the new sp value
						tlb.get(line)[1] = s;
						tlb.get(line)[2] = p;
						
						//replace f field of that line with PM[PM[s] + p]
						tlb.get(line)[3] = f;
						
						
						//select line with LRU = 0 and set this LRU = 3
						if(line != -1)
						{
							tlb.get(line)[0] = 3;
						}
						break;
					}
				}
			}
			
		}
		
		//decrement all other LRU values by 1
		if(line != -1)
		{
			Integer testLRU = tlb.get(line)[0];
			for (Integer i = 3; i >= 0; --i)
			{
				Integer changingLRU = tlb.get(i)[0];
				if (testLRU < changingLRU)
				{
					changingLRU = changingLRU - 1;
				}
			}
		}
		
		return line;
	}
	
	
	
}
