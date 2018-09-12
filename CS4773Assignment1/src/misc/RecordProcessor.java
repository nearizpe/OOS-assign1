package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

class EmployeeStatistics{
	float averageAge = 0f;
	double averageComission = 0;
	double averageHourlyWage = 0;
	double averageSalary = 0;
	int ageSum = 0;
	int numOfComissions = 0;
	double commissionSum = 0;
	int numOfHourlys = 0;
	double hourlySum = 0;
	int numOfSalarys = 0;
	double salarySum = 0;
}

public class RecordProcessor {
	private static String[] firstNames;
	private static String[] lastNames;
	private static int[] ages;
	private static String[] employeeTypes;
	private static double[] payments;
	public static String processFile(String filePath) {
		StringBuffer outputBuffer = new StringBuffer();
		Scanner employeeRecords = openFile(filePath);
		int nonEmptyLineCount = getLineCount(employeeRecords);
		
		initializeEmployees(nonEmptyLineCount);

		employeeRecords.close();

		employeeRecords = openFile(filePath);

		nonEmptyLineCount = 0;

		if(!ifValidRecord(employeeRecords,nonEmptyLineCount)) {
			return null;
		}

		outputBuffer.append(String.format("# of people imported: %d\n", firstNames.length));

		printHeader(outputBuffer);
		appendEmployeesToOutpout(outputBuffer);

		EmployeeStatistics employeeStatistics = new EmployeeStatistics();
		calculateSums(employeeStatistics);
		printAverages(employeeStatistics,outputBuffer);

		HashMap<String, Integer> sameFirstNames = new HashMap<String, Integer>();
		outputBuffer.append(String.format("\nFirst names with more than one person sharing it:\n"));
		sameFirstNames = findSameNames(outputBuffer,firstNames, "first");

		HashMap<String, Integer> sameLastNames = new HashMap<String, Integer>();
		outputBuffer.append(String.format("\nLast names with more than one person sharing it:\n"));
		sameLastNames = findSameNames(outputBuffer,lastNames, "last");

		employeeRecords.close();
		return outputBuffer.toString();
	}
	
	private static void initializeEmployees(int nonEmptyLineCount) {
		firstNames = new String[nonEmptyLineCount];
		lastNames = new String[nonEmptyLineCount];
		ages = new int[nonEmptyLineCount];
		employeeTypes = new String[nonEmptyLineCount];
		payments = new double[nonEmptyLineCount];
	}
		

	private static void printHeader(StringBuffer outputBuffer) {
		outputBuffer.append(String.format("\n%-30s %s  %-12s %12s\n", "Person Name", "Age", "Emp. Type", "Pay"));
		for (int i = 0; i < 30; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" ---  "));
		for (int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format(" "));
		for (int i = 0; i < 12; i++)
			outputBuffer.append(String.format("-"));
		outputBuffer.append(String.format("\n"));
	}
		
	private static void appendEmployeesToOutpout(StringBuffer outputBuffer) {
		for (int i = 0; i < firstNames.length; i++) {
			outputBuffer.append(String.format("%-30s %-3d  %-12s $%12.2f\n", firstNames[i] + " " + lastNames[i],
					ages[i], employeeTypes[i], payments[i]));
		}
	}	

	private static void calculateSums(EmployeeStatistics employeeStatistics) {
		for (int i = 0; i < firstNames.length; i++) {
			employeeStatistics.ageSum += ages[i];
			if (employeeTypes[i].equals("Commission")) {
				employeeStatistics.commissionSum += payments[i];
				employeeStatistics.numOfComissions++;
			} else if (employeeTypes[i].equals("Hourly")) {
				employeeStatistics.hourlySum += payments[i];
				employeeStatistics.numOfHourlys++;
			} else if (employeeTypes[i].equals("Salary")) {
				employeeStatistics.salarySum += payments[i];
				employeeStatistics.numOfSalarys++;
			}
		}
	}
	
	private static void printAverages(EmployeeStatistics employeeStatistics,StringBuffer outputBuffer) {	
		employeeStatistics.averageAge = (float) employeeStatistics.ageSum / firstNames.length;
		outputBuffer.append(String.format("\nAverage age:         %12.1f\n", employeeStatistics.averageAge));
		employeeStatistics.averageComission = employeeStatistics.commissionSum / employeeStatistics.numOfComissions;
		outputBuffer.append(String.format("Average commission:  $%12.2f\n", employeeStatistics.averageComission));
		employeeStatistics.averageHourlyWage = employeeStatistics.hourlySum / employeeStatistics.numOfHourlys;
		outputBuffer.append(String.format("Average hourly wage: $%12.2f\n", employeeStatistics.averageHourlyWage));
		employeeStatistics.averageSalary = employeeStatistics.salarySum / employeeStatistics.numOfSalarys;
		outputBuffer.append(String.format("Average salary:      $%12.2f\n", employeeStatistics.averageSalary));
	}
	
	private static HashMap<String, Integer> findSameNames(StringBuffer outputBuffer, String[] names, String nameType) {
		HashMap<String, Integer> sameNames = new HashMap<String, Integer>();
		int sameNameCount = getCountOfNames(sameNames,names);

		if (sameNameCount > 0) {
			appendUniqueNames(outputBuffer, sameNames);
		} else {
			outputBuffer.append(String.format("All "+nameType +" names are unique"));
		}
		return sameNames;
	}

	private static int getCountOfNames(HashMap<String, Integer> sameNames,String[] names) {
		int count = 0;
		for (int i = 0; i < names.length; i++) {
			if (sameNames.containsKey(names[i])) {
				sameNames.put(names[i], sameNames.get(names[i]) + 1);
				count++;
			} else {
				sameNames.put(names[i], 1);
			}
		}
		return count;
	}

	private static void appendUniqueNames(StringBuffer outputBuffer, HashMap<String, Integer> sameNames) {
		Set<String> employeeNames = sameNames.keySet();
		for (String employeeName : employeeNames) {
			if (sameNames.get(employeeName) > 1) {
				outputBuffer.append(String.format("%s, # people with this name: %d\n", employeeName, sameNames.get(employeeName)));
			}
		}
	}

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
		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			if (nextLine.length() > 0)
				nonEmptyLineCount++;
		}
		return nonEmptyLineCount;
	}
	
	private static boolean ifValidRecord(Scanner employeeRecords,int nonEmptyLineCount) {
		try {
			nonEmptyLineCount = alphabatizeEmployeeDataByLastName(employeeRecords, nonEmptyLineCount);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			employeeRecords.close();
			return false;
		}

		if (nonEmptyLineCount == 0) {
			System.err.println("No records found in data file");
			employeeRecords.close();
			return false;
		}
		return true;
	}
	
	private static int alphabatizeEmployeeDataByLastName(Scanner employeeRecords, int nonEmptyLineCount)
			throws Exception {
		while (employeeRecords.hasNextLine()) {
			String nextLine = employeeRecords.nextLine();
			if (nextLine.length() > 0) {
				setEmployeeRecords(nextLine, nonEmptyLineCount);
				nonEmptyLineCount++;
			}
		}
		return nonEmptyLineCount;
	}

	private static void setEmployeeRecords(String line, int nonEmptyLineCount) throws Exception {
		String[] words = line.split(",");

		int employeeIndex = 0;
		employeeIndex = getEmployeeIndex(employeeIndex, nonEmptyLineCount, words);
		firstNames[employeeIndex] = words[0];
		lastNames[employeeIndex] = words[1];
		employeeTypes[employeeIndex] = words[3];

		ages[employeeIndex] = Integer.parseInt(words[2]);
		payments[employeeIndex] = Double.parseDouble(words[4]);

	}

	private static int getEmployeeIndex(int employeeIndex, int nonEmptyLineCount, String words[]) {
		for (; employeeIndex < lastNames.length; employeeIndex++) {
			if (lastNames[employeeIndex] == null)
				break;

			if (lastNames[employeeIndex].compareTo(words[1]) > 0) {
				setRecordsToPreviousRecords(nonEmptyLineCount, employeeIndex);
				break;
			}
		}
		return employeeIndex;
	}

	private static void setRecordsToPreviousRecords(int nonEmptyLineCount, int employeeIndex) {
		for (int i = nonEmptyLineCount; i > employeeIndex; i--) {
			firstNames[i] = firstNames[i - 1];
			lastNames[i] = lastNames[i - 1];
			ages[i] = ages[i - 1];
			employeeTypes[i] = employeeTypes[i - 1];
			payments[i] = payments[i - 1];
		}
	}
}
