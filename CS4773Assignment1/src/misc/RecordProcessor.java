package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RecordProcessor {
	private static String [] firstNames;
	private static String [] lastNames;
	private static int [] ages;
	private static String [] employeeTypes;
	private static double [] payments;
	
	public static String processFile(String filePath) {
		//make different buffers for different contexts e.g. header,body
		StringBuffer outputBuffer = new StringBuffer();
		
		Scanner employeeRecords = openFile(filePath);
		
		int nonEmptyLineCount = getLineCount(employeeRecords);
		
		firstNames = new String[nonEmptyLineCount];
		lastNames = new String[nonEmptyLineCount];
		ages = new int[nonEmptyLineCount];
		employeeTypes = new String[nonEmptyLineCount];
		payments = new double[nonEmptyLineCount];

		employeeRecords.close();
		
		employeeRecords = openFile(filePath);
		
		nonEmptyLineCount = 0;
		
		//function 
		try{
			nonEmptyLineCount = alphabatizeEmployeeDataByLastName(employeeRecords,nonEmptyLineCount);
		} catch(Exception e) {
			System.err.println(e.getMessage());
			employeeRecords.close();
			return null;
		}
		
		if(nonEmptyLineCount == 0) {
			System.err.println("No records found in data file");
			employeeRecords.close();
			return null;
		}
		
		//print the header function
		System.out.print("~~~Firstname Length: ");
		System.out.println("" + firstNames[0]);
		
		
		outputBuffer.append(String.format("# of people imported: %d\n", firstNames.length));
		
		outputBuffer.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for(int i = 0; i < 30; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" ---  "));
		for(int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" "));
		for(int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format("\n"));
		//------------------------------------------
		for(int i = 0; i < firstNames.length; i++) {
			outputBuffer.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstNames[i] + " " + lastNames[i], ages[i]
				, employeeTypes[i], payments[i]));
		}
		
		int ageSum = 0;
		float averageAge = 0f;
		int numOfComissions = 0;
		double commissionSum = 0;
		double averageComission = 0;
		int numOfHourlys = 0;
		double hourlySum = 0;
		double averageHourlyWage = 0;
		int numOfSalarys = 0;
		double salarySum = 0;
		double averageSalary = 0;
		
		//not sure how to but needs to be a func
		for(int i = 0; i < firstNames.length; i++) {
			ageSum += ages[i];
			if(employeeTypes[i].equals("Commission")) {
				commissionSum += payments[i];
				numOfComissions++;
			} else if(employeeTypes[i].equals("Hourly")) {
				hourlySum += payments[i];
				numOfHourlys++;
			} else if(employeeTypes[i].equals("Salary")) {
				salarySum += payments[i];
				numOfSalarys++;
			}
		}
		//------------------------
		averageAge = (float) ageSum / firstNames.length;
		outputBuffer.append(String.format("\nAverage age:         %12.1f\n", averageAge));
		averageComission = commissionSum / numOfComissions;
		outputBuffer.append(String.format("Average commission:  $%12.2f\n", averageComission));
		averageHourlyWage = hourlySum / numOfHourlys;
		outputBuffer.append(String.format("Average hourly wage: $%12.2f\n", averageHourlyWage));
		averageSalary = salarySum / numOfSalarys;
		outputBuffer.append(String.format("Average salary:      $%12.2f\n", averageSalary));
		
		HashMap<String, Integer> sameFirstNames = new HashMap<String, Integer>();
		//find same first name func
		//should return same first name hash
		int sameFirstNameCount = 0;
		for(int i = 0; i < firstNames.length; i++) {
			if(sameFirstNames.containsKey(firstNames[i])) {
				sameFirstNames.put(firstNames[i], sameFirstNames.get(firstNames[i]) + 1);
				sameFirstNameCount++;
			} else {
				sameFirstNames.put(firstNames[i], 1);
			}
		}

		outputBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		if(sameFirstNameCount > 0) {
			Set<String> set = sameFirstNames.keySet();
			for(String str : set) {
				if(sameFirstNames.get(str) > 1) {
					outputBuffer.append(String.format("%s, # people with this name: %d\n", str, sameFirstNames.get(str)));
				}
			}
		} else { 
			outputBuffer.append(String.format("All first names are unique"));
		}
		//end of first name func---------------------
		
		HashMap<String, Integer> sameLastNames = new HashMap<String, Integer>();
		//maybe generalize same first name func to take in last names too?
		int sameLastNameCount = 0;
		for(int i = 0; i < lastNames.length; i++) {
			if(sameLastNames.containsKey(lastNames[i])) {
				sameLastNames.put(lastNames[i], sameLastNames.get(lastNames[i]) + 1);
				sameLastNameCount++;
			} else {
				sameLastNames.put(lastNames[i], 1);
			}
		}

		outputBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		if(sameLastNameCount > 0) {
			Set<String> set = sameLastNames.keySet();
			for(String str : set) {
				if(sameLastNames.get(str) > 1) {
					outputBuffer.append(String.format("%s, # people with this name: %d\n", str, sameLastNames.get(str)));
				}
			}
		} else { 
			outputBuffer.append(String.format("All last names are unique"));
		}
		//
		
		
		//close the file
		employeeRecords.close();

		return outputBuffer.toString();
	}
	
	//-----------------------------------FUNCTIONS-----------------------------------------------------------------------------------
	
	//------------FIND SAME NAME-----------------
	
	
	
	//----------UTILS--------------------------
	private static Scanner openFile(String filePath) {
		try {
			return new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	private static int getLineCount(Scanner scanner) {
		int nonEmptyLineCount = 0;
		while(scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			if(nextLine.length() > 0)
				nonEmptyLineCount++;
		}
		return nonEmptyLineCount;
	}
	
	
	//-------------------SORTING------------------------
	private static int alphabatizeEmployeeDataByLastName(Scanner employeeRecords, int nonEmptyLineCount) throws Exception{
		while(employeeRecords.hasNextLine()) {
			String nextLine = employeeRecords.nextLine();
			if(nextLine.length() > 0) {
				setEmployeeRecords(nextLine,nonEmptyLineCount);
				nonEmptyLineCount++;
			}
		}
		return nonEmptyLineCount;
	}
	
	private static void setEmployeeRecords(String line,int nonEmptyLineCount) throws Exception{
		String [] words = line.split(",");

		int employeeIndex = 0;
		employeeIndex = getEmployeeIndex(employeeIndex, nonEmptyLineCount,words);
		firstNames[employeeIndex] = words[0];
		lastNames[employeeIndex] = words[1];
		employeeTypes[employeeIndex] = words[3];
		
		ages[employeeIndex] = Integer.parseInt(words[2]);
		payments[employeeIndex] = Double.parseDouble(words[4]);
		
	}
	
	private static int getEmployeeIndex(int employeeIndex, int nonEmptyLineCount, String words[]){
		for(;employeeIndex < lastNames.length; employeeIndex++) {
			if(lastNames[employeeIndex] == null)
				break;
			
			if(lastNames[employeeIndex].compareTo(words[1]) > 0) {
				setRecordsToPreviousRecords(nonEmptyLineCount,employeeIndex);
				break;
			}
		}
		return employeeIndex;
	}
	
	private static void setRecordsToPreviousRecords(int nonEmptyLineCount, int employeeIndex){
		for(int i = nonEmptyLineCount; i > employeeIndex; i--) {
			firstNames[i] = firstNames[i - 1];
			lastNames[i] = lastNames[i - 1];
			ages[i] = ages[i - 1];
			employeeTypes[i] = employeeTypes[i - 1];
			payments[i] = payments[i - 1];
		}
	}
}
