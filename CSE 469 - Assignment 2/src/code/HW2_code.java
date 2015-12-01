package code;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;


public class HW2_code {
	
	// My code is ending up breaking some rules. :(
	static int[][] Data;
	static int[] Support;
	static int minsup;
	
	static String[] Headers;
	static int columns;
	

	public static void main(String[] args) {
		
		// Choose between the gene data and market data
		Scanner user_in = new Scanner(System.in);
		String chosen_data = null;
		
		while(!"gene".equals(chosen_data) && !"market".equals(chosen_data)){
			System.out.println("Please choose either the gene data or the market data. \n"
					+ "Type either 'gene' or 'market.'");
			chosen_data = user_in.nextLine();
			chosen_data = chosen_data.replaceAll("[^a-zA-Z ]", "").toLowerCase();
			chosen_data = chosen_data.replaceAll("\n", "");
			if(!"gene".equals(chosen_data) && !"market".equals(chosen_data)){
				System.out.println("Sorry, that is not one of the two options.");
			}
		}
		user_in.close();
		
		//String chosen_data = "market";
				
		// We will check that the string chosen is correctly assigned, if not, we will
		// assign it to default to market:
		if(!"gene".equals(chosen_data) && !"market".equals(chosen_data)) chosen_data = "market";
		
		// create column headers for eventual displaying of sets
		column_headers(chosen_data);
		
		// import data and create a matrix for it
		// also creates support array for each item, and finds minsup
		import_data(chosen_data);
		
		/* This will find the candidate and frequent itemsets, then immediately print them to 
		 * file. The file output is as follows, where ORIGIN is either 'gene' or 'market,'
		 * and TYPE is either 'frequentitemsets' or 'candidateitemsets:
		 * ORIGIN_TYPE.txt
		*/
		try {
			run_apriori(chosen_data);
		} catch (IOException e) {
			System.out.println("Problem with apriori filewriting.");
		}
	}
	
	private static void run_apriori(String chosen) throws IOException {
		ArrayList<Integer[]> Frequent_Itemsets = new ArrayList<Integer[]>();
		ArrayList<Integer[]> Candidate_Itemsets = new ArrayList<Integer[]>();
		
		// First, find frequent items at level one.
		// These are the basis for the apriori algorithm.
		// For each level, to be neat, we will write a new file.
		
		File fout = new File(chosen + "_frequents");
		FileOutputStream ffos = new FileOutputStream(fout);
		
		File cout = new File(chosen + "_candidates");
		FileOutputStream cfos = new FileOutputStream(cout);
		
		BufferedWriter frequentw = new BufferedWriter(new OutputStreamWriter(ffos));
		BufferedWriter candidatew = new BufferedWriter(new OutputStreamWriter(cfos));

		frequentw.write("L1: "); 
		frequentw.newLine();
		
		// I could not figure out a way to increment level and run through the appropriate
		// number of candidates simply using loops, so I manually coded this for itemsets 
		// of two, and itemsets of three. Definitely a big bummer, if I had more time I 
		// could figure it out. It would probably have to be recursive.
		
		for(int j = 0; j < columns; j++){
			if(Support[j] >= minsup){
				Frequent_Itemsets.add(new Integer[]{j});
				frequentw.write("{" + Headers[j] + "}");
				frequentw.newLine();
			}
		}
		frequentw.newLine();
		
		// Candidate and frequent itemsets of level two:
		
		candidatew.write("C2: ");
		candidatew.newLine();
		frequentw.write("L2: "); 
		frequentw.newLine();
		
		for(int i = 0; i < Frequent_Itemsets.size(); i++){
			int col1 = Frequent_Itemsets.get(i)[0];
			for(int j = i + 1; j < Frequent_Itemsets.size(); j++){
				int col2 = Frequent_Itemsets.get(j)[0];
				candidatew.write("{" + Headers[col1] + ", " + Headers[col2] + "}");
				candidatew.newLine();
				int support = 0;
				for(int k = 0; k < Data.length; k++){
					if(Data[k][col1] > 0 && Data[k][col2] > 0) support++;
				}
				if(support >= minsup){
					Candidate_Itemsets.add(new Integer[]{col1,col2});
					frequentw.write("{" + Headers[col1] + ", " + Headers[col2] + "}");
					frequentw.newLine();
				}
			}
		}
		candidatew.newLine();
		frequentw.newLine();
		Frequent_Itemsets = Candidate_Itemsets;
		Candidate_Itemsets = new ArrayList<Integer[]>();
		
		// Candidate and frequent itemsets of level three:
		
		candidatew.write("C3: ");
		candidatew.newLine();
		frequentw.write("L3: "); 
		frequentw.newLine();
		
		for(int i = 0; i < Frequent_Itemsets.size(); i++){
			int col1 = Frequent_Itemsets.get(i)[0];
			int col2 = Frequent_Itemsets.get(i)[1];
			
			for(int j = i + 1; j < Frequent_Itemsets.size(); j++){
				int col3;
				if(Frequent_Itemsets.get(j)[0] == col1){
					col3 = Frequent_Itemsets.get(j)[1];
					for(int k = j + 1; k < Frequent_Itemsets.size(); k ++){
						if(Frequent_Itemsets.get(k)[0] == col2){
							if(Frequent_Itemsets.get(k)[1]==col3){
								candidatew.write("{" + Headers[col1] + ", " + Headers[col2] + ", " + Headers[col3] + "}");
								candidatew.newLine();
								int support = 0;
								for(int row = 0; row < Data.length; row++){
									if(Data[row][col1] > 0 && Data[row][col2] > 0 && Data[row][col3] > 0) support++;
								}
								if(support >= minsup){
									Candidate_Itemsets.add(new Integer[]{col1,col2,col3});
									frequentw.write("{" + Headers[col1] + ", " + Headers[col2] + ", " + Headers[col3] + "}");
									frequentw.newLine();
								}
							}
						}
					}
				}
								
			}
		}
		
		candidatew.close();
		frequentw.close();
	}

	private static void import_data(String chosen) {
		
		Scanner input = null;
		try {
			input = new Scanner (new File(chosen + "_data_binary.txt"));
		} catch (FileNotFoundException e) {System.out.print("File not found");}
		
		// dynamically create a matrix for the data
		int rows = 0;
		
		while(input.hasNextLine()){
			++rows;
			input.nextLine();
			}
		input.close();
		Data = new int[rows][columns];
				
		try {
			input = new Scanner (new File(chosen + "_data_binary.txt"));
		} catch (FileNotFoundException e) {System.out.print("File not found");}
		
		// now assign values to newly created 2d matrix 
		for(int i = 0; i < rows; ++i){
		    for(int j = 0; j < columns; ++j){
		        if(input.hasNextInt()){
		            Data[i][j] = input.nextInt();
		        }
		    }
		}
		
		// now create a 1d array showing support of each item
		Support = new int[columns];
		for(int j = 0; j < columns; j++){
			Support[j] = 0;
			for(int i = 0; i < rows; i++){
				Support[j] += Data[i][j];
			}
		}
		
		// Finally, assign minsup, which is one-half of the potential max.
		// This is integer math, so we must accommodate for odd totals.
		// Being integers, we can round 0.5 up to 1, because any integer >=
		// o.5 will also be >= 1.
		
		minsup = (rows / 2) + (rows % 2);
		
	}


	public static void column_headers(String chosen){
		
		// If using gene data, this will assign appropriate column headers:
		if(chosen.equals("gene")){
			Headers = new String[100];
			for(int i = 0; i < 100; i++){
				Headers[i] = "gene_" + (i+1);
				if(i < 9) Headers[i] = Headers[i] + " ";
				if(i < 99) Headers[i] = Headers[i] + " ";
			}
			columns = 100;
			
		}
		
		else if(chosen.equals("market")){
			Headers = new String[]{"Apples","Corn","Doll","Eggs","Ice-cream","Key-chain","Mango",
					"Nintendo","Onion","Umbrella","Yo-yo"};
			columns = 11;
		}
		
		
	}
}
