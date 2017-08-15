import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Project3
{
	public static void main(String[] args)
	{
		
		Scanner userInputScanner = new Scanner(System.in);
		System.out.print("Enter input file path for input1.txt: ");
		String inFile1Path = userInputScanner.nextLine();
		inFile1Path.trim();
		System.out.println("inFilePath: " + inFile1Path);
		File inFile1 = new File(inFile1Path);
		

		
		
		System.out.print("Enter input file path for input2.txt: ");
		
		String inFile2Path = userInputScanner.nextLine();
		inFile2Path.trim();
		System.out.println("inFilePath: " + inFile2Path);
		File inFile2 = new File(inFile2Path);
		
		Scanner inScanner1 = null;
		try {
			inScanner1 = new Scanner(inFile1);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file1");
		}
		
		PhysicalMemory physicalMemory = new PhysicalMemory();
		//initialize physical memory as follows:
		//	read si,fi pairs and make corresponding entries in ST
		//  read pj, sj, fj triples and make entries in the PT
		
		String file1Pairs = inScanner1.nextLine();    // get the entire line after the prompt 
		String[] numbersLine1 = file1Pairs.split(" "); // split by spaces
		for(int i = 0; i < numbersLine1.length; i = i + 2)
		{
			Integer s = Integer.parseInt(numbersLine1[i]);
			Integer f = Integer.parseInt(numbersLine1[i+1]);
			System.out.println("s = " + s.toString());
			System.out.println("f = " + f.toString());
			physicalMemory.makeEntriesInSegmentTable(s, f);
	    }
		
		String file1Triples = inScanner1.nextLine();    // get the entire line after the prompt 
		String[] numbersLine2 = file1Triples.split(" "); // split by spaces
		for(int i = 0; i < numbersLine2.length; i = i + 3)
		{
			Integer p = Integer.parseInt(numbersLine2[i]);
			Integer s = Integer.parseInt(numbersLine2[i+1]);
			Integer f = Integer.parseInt(numbersLine2[i+2]);
			System.out.println("p = " + p.toString());
			System.out.println("s = " + s.toString());
			System.out.println("f = " + f.toString());
			physicalMemory.makeEntriesInPageTable(p, s, f);
		}
			
		Scanner inScanner2 = null;
		try {
			inScanner2 = new Scanner(inFile2);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file2");
		}
		String outFile1Path = inFile1Path.substring(0, inFile1Path.length() - 11); //chop off "/input.txt"
		outFile1Path = outFile1Path.trim();
		outFile1Path = outFile1Path.concat("/13179240-notlb.txt");
		System.out.println("outFilePath: " + outFile1Path);
	
		final File outFile1 = new File(outFile1Path);
		
		FileWriter file1Writer = null;
		try {
			file1Writer = new FileWriter(outFile1.getAbsoluteFile(), true);
		} catch (IOException e) {
			System.out.println("Couldnt open filewriter");
		}
		
		final BufferedWriter bufferedWriter1 = new BufferedWriter(file1Writer);
		
		String file2Pairs = inScanner2.nextLine();    // get the entire line after the prompt 
		String[] numbersFile2 = file2Pairs.split(" "); // split by spaces
		for(int i = 0; i < numbersFile2.length; i = i + 2)
		{
			System.out.println("MAIN WITHOUT TLB I = " + i);
			Integer o = Integer.parseInt(numbersFile2[i]);
			Integer VA = Integer.parseInt(numbersFile2[i+1]);
			System.out.println("o = " + o.toString()); //0 read 1 write
			System.out.println("VA = " + VA.toString());
			//physicalMemory
			HashMap<String, Integer> spw = physicalMemory.getSPWFromVirtualAddress(VA);
			Integer s  = spw.get("s");
			Integer p = spw.get("p");
			Integer w = spw.get("w");
			
			if(o == 0)
			{
				System.out.println("READ! o = 0");
				String translation = physicalMemory.read(s, p, w);
				try {
					bufferedWriter1.write(translation + " ");
				} catch (IOException e) {
					System.out.println("cant write to file1");
				}
				System.out.println("Wrote " + translation + " to file");
			}
			else
			{
				String translation = physicalMemory.write(s, p, w);
				try {
					bufferedWriter1.write(translation + " ");
				} catch (IOException e) {
					System.out.println("cant write to file1");
				}
				System.out.println("Wrote " + translation + " to file");
			}
		}
		
		try {
			bufferedWriter1.close();
		} catch (IOException e1) {
			System.out.println("Couldnt close bufferedWriter1");
		}
		
		
		//Now re-initialize PM, and do process again but with TLB

		inScanner1 = null;
		try {
			inScanner1 = new Scanner(inFile1);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file1");
		}
		
		physicalMemory = new PhysicalMemory();
		TLB tlb = new TLB();
		//initialize physical memory as follows:
		//	read si,fi pairs and make corresponding entries in ST
		//  read pj, sj, fj triples and make entries in the PT
		
		file1Pairs = inScanner1.nextLine();    // get the entire line after the prompt 
		numbersLine1 = file1Pairs.split(" "); // split by spaces
		for(int i = 0; i < numbersLine1.length; i = i + 2)
		{
			Integer s = Integer.parseInt(numbersLine1[i]);
			Integer f = Integer.parseInt(numbersLine1[i+1]);
			System.out.println("s = " + s.toString());
			System.out.println("f = " + f.toString());
			physicalMemory.makeEntriesInSegmentTable(s, f);
	    }
		
		file1Triples = inScanner1.nextLine();    // get the entire line after the prompt 
		numbersLine2 = file1Triples.split(" "); // split by spaces
		for(int i = 0; i < numbersLine2.length; i = i + 3)
		{
			Integer p = Integer.parseInt(numbersLine2[i]);
			Integer s = Integer.parseInt(numbersLine2[i+1]);
			Integer f = Integer.parseInt(numbersLine2[i+2]);
			System.out.println("p = " + p.toString());
			System.out.println("s = " + s.toString());
			System.out.println("f = " + f.toString());
			physicalMemory.makeEntriesInPageTable(p, s, f);
		}

		String outFile2Path = inFile1Path.substring(0, inFile1Path.length() - 11); //chop off "/input#.txt"
		outFile2Path = outFile2Path.trim();
		outFile2Path = outFile2Path.concat("/13179240-tlb.txt");
		System.out.println("outFilePath: " + outFile2Path);
	
		final File outFile2 = new File(outFile2Path);
		
		FileWriter file2Writer = null;
		try {
			file2Writer = new FileWriter(outFile2.getAbsoluteFile(), true);
		} catch (IOException e) {
			System.out.println("Couldnt open filewriter");
		}
		
		final BufferedWriter bufferedWriter2 = new BufferedWriter(file2Writer);
		
		
		inScanner2 = null;
		try {
			inScanner2 = new Scanner(inFile2);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file2");
		}
		
		file2Pairs = inScanner2.nextLine();    // get the entire line after the prompt 
		numbersFile2 = file2Pairs.split(" "); // split by spaces
		for(int i = 0; i < numbersFile2.length; i = i + 2)
		{
			System.out.println("MAIN WITH TLB I = " + i);
			Integer o = Integer.parseInt(numbersFile2[i]);
			Integer VA = Integer.parseInt(numbersFile2[i+1]);
			System.out.println("o = " + o.toString()); //0 read 1 write
			System.out.println("VA = " + VA.toString());
			//physicalMemory
			HashMap<String, Integer> spw = physicalMemory.getSPWFromVirtualAddress(VA);
			Integer s  = spw.get("s");
			Integer p = spw.get("p");
			Integer w = spw.get("w");
			
			
			if(o == 0)
			{
				
				//String translation = physicalMemory.read(s, p, w);
				String translation = null;
				try {
					Integer line = tlb.findLine(s, p);
					//couldnt find in TLB
					if(-1 == line)
					{
						System.out.println("READ MISS level = " + line);
						bufferedWriter2.write("m ");

						translation = physicalMemory.read(s, p, w);
						if(translation.equals("pf") || translation.equals("err"))
						{
							//no change
						}
						else
						{
							Integer f = physicalMemory.getPTEntry(s, p);
						 	tlb.updateTLB(line, s, p, Integer.parseInt(translation)); 

						}
						tlb.debugTLB();
					}
					else
					{
						System.out.println("READ HIT level = " + line);
						//hit!
						System.out.println("LEVEL = " + line);
						bufferedWriter2.write("h ");
						Integer f = tlb.getFrameOnLine(line);
						System.out.println("f = " + f + " w = " + w);
						translation = Integer.toString(f + w);
						//translation = Integer.toString(physicalMemory.getPA(s, p, f));
						tlb.updateTLB(line, s, p, Integer.parseInt(translation));
						tlb.debugTLB();

					}
					
					bufferedWriter2.write(translation + " ");
				} catch (IOException e) {
					System.out.println("cant write to file2");
				}
				System.out.println("Wrote " + translation + " to file");
			}
			else
			{
				//write with TLB
				String translation = null;
				Integer line = tlb.findLine(s, p);
				try {
					if(-1 == line)
					{
						System.out.println("WRITE MISS level = " + line);
						//MISS!
						bufferedWriter2.write("m ");
						translation = physicalMemory.write(s, p, w);						
						if(translation.equals("pf") || translation.equals("err"))
						{
							//no change
						}
						else
						{
							Integer f = physicalMemory.getPTEntry(s, p);
						 	tlb.updateTLB(line, s, p, Integer.parseInt(translation)); 
						}
						tlb.debugTLB();
					}
					else
					{
						System.out.println("WRITE HIT level = " + line);
						//HIT!
						bufferedWriter2.write("h ");
						Integer f = tlb.getFrameOnLine(line);
						translation = Integer.toString(f + w);
						//translation = Integer.toString(physicalMemory.getPA(s, p, f));
						tlb.updateTLB(line, s, p, Integer.parseInt(translation));
						System.out.println("f = " + f + " w = " + w);
						tlb.debugTLB();

						
					}
					bufferedWriter2.write(translation + " ");
				} catch (IOException e) {
					System.out.println("cant write to file2");
				}
				System.out.println("Wrote " + translation + " to file");
			}
		}
		
		try {
			bufferedWriter2.close();
		} catch (IOException e) {
			System.out.println("Couldn't close bufferedWriter2...");
		}
	}


}