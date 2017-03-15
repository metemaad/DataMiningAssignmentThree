import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class main {

	private static Scanner scan;
	private static Scanner scan2;



    public static void main(String[] args) {



		String filename = getInputFile();
		float minimumSupport = getMinimumSupport();
		float minimumConfidence =  getMinimumConfidence();

		Association_rule_mining.loadDataSet(filename);

		Map<String, Integer> candidate1ItemSet = Association_rule_mining.generateCandidate1ItemSet();
		Set<Set<String>> FrequentOneItemSet = Association_rule_mining.generateFrequent1ItemSet(candidate1ItemSet, minimumSupport);
		Set<Set<String>> FrequentItems = Association_rule_mining.generateCandidateKItemSets(FrequentOneItemSet,minimumSupport);


		Association_rule_mining.ruleGeneration(FrequentItems, minimumSupport, minimumConfidence);

	}

	private static float getMinimumConfidence() {
		boolean Invalid;
		Invalid = false;
		float minimumConfidence = 0;
		while (!Invalid) {
			scan2 = new Scanner(System.in);
			System.out.print("Please select the minimum confidence rate(0.00-1.00):");
			minimumConfidence = scan2.nextFloat();
			if ((minimumConfidence <= 1) & (minimumConfidence >= 0)) {
				Invalid = true;
			} else {
				System.out.print("\r\nNot valid try again.\r\n");
			}
		}
		return minimumConfidence;
	}

	private static float getMinimumSupport() {
		boolean Invalid;
		float minimumSupport = 0;
		Invalid = false;
		while (!Invalid) {
			scan = new Scanner(System.in);
			System.out.print("Please select the minimum support rate(0.00-1.00):");
			minimumSupport = scan.nextFloat();
			if ((minimumSupport <= 1) & (minimumSupport >= 0)) {
				Invalid = true;
			} else {
				System.out.print("\r\nNot valid try again.\r\n");
			}
		}
		return minimumSupport;
	}

	private static String getInputFile() {
		String filename="";
		boolean Invalid = false;
		while (!Invalid) {
			scan2 = new Scanner(System.in);
			System.out
					.print("What is the name of the file containing your data?");
			filename = scan2.next();
			String DBFileName = Association_rule_mining.getfilepath(filename);
			Path path = Paths.get(DBFileName);
			if (Files.isReadable(path)) {
				Invalid = true;
			} else {
				System.out.print("\r\nCannot open file:" + DBFileName + "\r\n");
			}

		}
		return filename;
	}
}
