import java.math.BigInteger;
import java.util.HashMap;

public class PhysicalMemory {
	static Integer FRAME_SIZE = 512;
	static Integer BITS_IN_VA = 32;
	static Integer PHYSICAL_MEMORY_SIZE = 524288;
			
	private Integer[] pm = new Integer[PHYSICAL_MEMORY_SIZE];
	private Integer virtualAddress;
	private long[] bitMap = new long[BITS_IN_VA];
			
	//diagonal contains "1", all other entries are "0"
	private long[] MASK = new long[BITS_IN_VA]; // to set, reset, and search for bits in BM
			
	//diagonal contains "0", all other entries are "1"
	private long[] MASK2 = new long[BITS_IN_VA];
			
		
			
	PhysicalMemory()
	{	
		for(Integer i = 0; i < PHYSICAL_MEMORY_SIZE; ++i)
		{
			this.pm[i] = 0;
		}
		
		//create MASK
		MASK[31] = 1;
		for(Integer i = 30; i >= 0; --i)
		{
			MASK[i] = MASK[i+1] << 1;
			System.out.println("MASK[" + i + "] = " + MASK[i]);
		}
		
		//createMASK2
		for(Integer i = 0; i <= 31; ++i)
		{
			MASK2[i] = ~MASK[i];
		}
		
		for(Integer i = 0; i <= 31; ++i)
		{
			bitMap[i] = 0;
		}
		bitMap[0] = MASK[0]; //allocate ST
		//printBitMap();
	}
	
	//PT of segment s starts at address f
	void makeEntriesInSegmentTable(Integer s, Integer f)
	{
		//PM[s] accesses the ST
		this.pm[s] = f;
		
		//Creating a new page or PT requires searching and updating the BM
		//A page table requires 2 frames
		if(f != -1)
		{
			makeBitMapFrame(f);
			makeBitMapFrame( (f + FRAME_SIZE) );
		}
		
	}
	
	void makeEntriesInPageTable(Integer p, Integer s, Integer f)
	{
		this.pm[this.pm[s] + p] = f;
		
		if (f != -1)
		{
			makeBitMapFrame(f);
		}
	}

	private void makeBitMapFrame(Integer startingAddress)
	{
		Integer offset = 0;
		
		while(offset < FRAME_SIZE)
		{
			this.pm[startingAddress + offset] = 0;
			offset = (offset + 1);
		}
		
		Integer positionInBitMap = (startingAddress / FRAME_SIZE) / BITS_IN_VA;
		Integer positionInMaskArray = (startingAddress / FRAME_SIZE) % BITS_IN_VA;
	
		
		bitMap[positionInBitMap] = ( bitMap[positionInBitMap] | MASK[positionInMaskArray] );
		
	}
	
	private void printBitMap()
	{
		for(Integer i = 0; i < bitMap.length; ++i)
		{
			System.out.println("bitMap[" + i + "] = " + bitMap[i]);
		}
	}
	
	
	public HashMap<String,Integer>  getSPWFromVirtualAddress(Integer virtualAddress)
	{
		
		//get bits i to j: virtualAddress & (((1 << (j-i)) - 1) << i );
		System.out.println("getSPWFromVirtualAddres()");
		HashMap<String,Integer> spw = new HashMap<String,Integer>();
		Integer i = 0;
		Integer segmentBitSize = 9;
		Integer s = 0; //segmentNumber, 9 bits
		Integer p = 0; //pageNumber, 10 bits
		Integer w = 0; //offset within that page

		while(i < segmentBitSize)
		{
			//s goes from [4 to 12] , segmentNumber, 9 bits
			long sMask = MASK[i + 4];

			//p goes from [13 to 22] , pageNumber, 10 bits
			long pMask = MASK[i + 13];
			long lastPMask = MASK[i + 22];

			//w goes from [23 to 31] , offset within that page
			long wMask = MASK[i + 23];

			if( (virtualAddress & sMask) != 0)
			{
				s += (int) wMask; //ST->PT->Page->Offset within page
				System.out.println("s wMask = " + wMask);
				System.out.println("i = " + i);
			}
			if ( (virtualAddress & pMask) != 0)
			{
				p += (int) lastPMask;
				System.out.println("p lastPMask = " + lastPMask);
				System.out.println("i = " + i);
			}
			if ( (virtualAddress & wMask) != 0)
			{
				w += (int) wMask;
				System.out.println("w wMask = " + wMask);
				System.out.println("i = " + i);
			}
			i = (i + 1);
		}


		if ( (virtualAddress & MASK[22]) != 0)
		{
			p += (int) MASK[31];
			System.out.println("Last p check");
		}


		spw.put("s", s);
		spw.put("p", p);
		spw.put("w", w);

		System.out.println("s = " + spw.get("s"));
		System.out.println("p = " + spw.get("p"));
		System.out.println("w = " + spw.get("w"));


		return spw;
	}
	
	

	public Integer makeNewFrame()
	{
		
		Integer tableSize = (BITS_IN_VA * BITS_IN_VA);
		Integer i = 0;
		Integer framePosition = 0;
		while(i < tableSize)
		{
			Integer positionInBitMap = (i / BITS_IN_VA);
			Integer positionInMaskArray = (i % BITS_IN_VA);
			
			if ( (bitMap[positionInBitMap] & MASK[positionInMaskArray]) != 0)
			{
				i = (i + 1);
				continue;
			}
			else
			{
				//the bit hasnt been set
				framePosition = (i * FRAME_SIZE);
				makeBitMapFrame(framePosition);
				break;
			}
		}
		return framePosition;
	}
	

	public Integer makeNewPageTable()
	{
		Integer tableSize = ( (BITS_IN_VA * BITS_IN_VA) - 1 ); 
		Integer i = 0;
		Integer positionOfFrame = null;
		while(i < tableSize)
		{
			Integer positionInBitMapFrame1 = (i / BITS_IN_VA);
			Integer positionInMaskArrayFrame1 = (i % BITS_IN_VA);
			
			Integer positionInBitMapFrame2 = ( (i + 1) / BITS_IN_VA);
			Integer positionInMaskArrayFrame2 = ( (i + 1) % BITS_IN_VA);
			
			long frame1Check = (bitMap[positionInBitMapFrame1] & MASK[positionInMaskArrayFrame1]);
			long frame2Check = (bitMap[positionInBitMapFrame2] & MASK[positionInMaskArrayFrame2]);
			
			//if there are 2 free frames
			if( (frame1Check == 0) && (frame2Check == 0) )
			{
					//make two frames
					positionOfFrame = (i * FRAME_SIZE);
					makeBitMapFrame(positionOfFrame);
					Integer positionOfNextFrame = ((i + 1) * (FRAME_SIZE));
					makeBitMapFrame(positionOfNextFrame);
					break;
			}		
			i = (i + 1);
		}
		return positionOfFrame;
}
	
	public String read(Integer s, Integer p, Integer w)
	{

		Integer pageTableEntry = -1;
		Integer segmentTableEntry = this.pm[s];
		try{
			pageTableEntry = this.pm[this.pm[s] + p];
		}
		catch(Exception e)
		{
			return "err";
		}
		
		// -1 page fault
		if(segmentTableEntry == -1 || pageTableEntry == -1)
		{
			return "pf";
		}
		
		//0 error
		if(segmentTableEntry == 0 || pageTableEntry == 0)
		{
			return "err";
		}
		Integer readVal = this.pm[this.pm[s] + p] + w;
		return readVal.toString();
	}
	
	public String write(Integer s, Integer p, Integer w)
	{
		
		Integer segmentTableEntry = this.pm[s];
		Integer pageTableEntry = this.pm[this.pm[s] + p];
		//If ST or PT entry is -1 then output “pf” 
		if(segmentTableEntry == -1 || pageTableEntry == -1)
		{
			return "pf";
		}
		
		//If ST entry is 0 then 
		if(segmentTableEntry == 0)
		{
			//allocate new blank PT (all zeroes)
			//update the ST entry accordingly
			this.pm[s] = makeNewPageTable();
			pageTableEntry = this.pm[this.pm[s] + p];

		}
		//continue with the translation process;
	    //		if PT entry is 0 then 
		if(pageTableEntry == 0)
		{
			//	create a new blank page 			
			//	update the PT entry accordingly
			this.pm[this.pm[s] + p] = makeNewFrame();
		}
		
		//	Otherwise output the corresponding PA 
		System.out.println("WRITE: pm[pm[s] + p] = " + pm[pm[s] + p]);
		Integer physicalAddress= this.pm[this.pm[s] + p] + w;
		return physicalAddress.toString();
	}
	
	Integer getPTEntry(Integer s, Integer p)
	{
		System.out.println("getPTEntry s = " + s + " p = " + p + "pm[pm[s] + p] = " + pm[pm[s] + p]);
		Integer segmentTableEntry = this.pm[s];
		Integer pageTableEntry = this.pm[this.pm[s] + p];
		
		if(pageTableEntry == 0)
		{
			//if PT entry is 0 then 
			//create a new blank page
			//update the PT entry accordingly
			this.pm[this.pm[s] + p] = makeNewFrame();
		}
		
		return this.pm[this.pm[s] + p]; 
	}
	
	Integer getPhysicalAddressFromTLB(Integer f, Integer w)
	{
		Integer physicalAddress = this.pm[f] + w;
		return physicalAddress;
	}

}