import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;


class item {
    String attribute;
    String value;


}
class generatedRule {
    float support;
    float confidence;
    Set<String> leftsideItemsets= new HashSet<String>();
    Set<String> rightsideitemsets= new HashSet<String>();


}
public class Association_rule_mining {
    private static Map<String, Integer> Countdictionary = new HashMap<>();
    private static Set<Set<item>> all_Tuples = new HashSet<>();
    private static String Filename="";
    private static long RuleGenStartTime;
    private static long RuleGenEndTime;
    private static long dbEndTime;
    private static long dbStartTime;
    private static long FrqMinStartTime;
    private static long FrqMinEndTime;


    public static void loadDataSet(String dbFileName) {
        dbStartTime = System.currentTimeMillis();
        List<String> lines = new ArrayList<String>();
        dbFileName = getfilepath(dbFileName);
        Filename=dbFileName;
        Path path = Paths.get(dbFileName);
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Set<item>> tuples = new HashSet<>();
        String[] attributes = GetItemsinoneLine(lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            String[] tuplestringitems = GetItemsinoneLine(lines.get(i));
            Set<item> tuple = new HashSet<>();
            for (int j = 0; j < tuplestringitems.length; j++) {
                String[] values = GetItemsinoneLine(lines.get(i));
                item it = new item();
                it.attribute = attributes[j];
                it.value = values[j];
                tuple.add(it);
            }
            tuples.add(tuple);
        }
        all_Tuples = tuples;
        dbEndTime= System.currentTimeMillis();
    }

    /**
     * @param line
     * @return
     */
    public static String[] GetItemsinoneLine(String line) {
        String[] tmpln = line.replaceAll("  +|   +|\t|\r|\n", " ").split(" ");
        return tmpln;
    }

    /**
     * @param filename
     * @return
     */
    public static String getfilepath(String filename) {
        String CurrDir = System.getProperty("user.dir");
        return CurrDir + "/" + filename;

    }

    /**
     * @return
     */
    public static Map<String, Integer> generateCandidate1ItemSet() {
        FrqMinStartTime= System.currentTimeMillis();
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        for (Set<item> tuple : all_Tuples) {
            for (item tupleitem : tuple) {
                String itms = tupleitem.attribute + "," + tupleitem.value;
                if (!dictionary.containsKey(itms)) {
                    dictionary.put(itms, 1);
                } else {
                    Integer val = (Integer) dictionary.get(itms);
                    dictionary.put(itms, val + 1);
                }
            }
        }
        return dictionary;
    }

    /**
     * @param allCandidate1Itemsets
     * @param minsup
     * @return
     */
    public static Set<Set<String>> generateFrequentKItemSet(
            Set<Set<String>> allCandidate1Itemsets, float minsup) {
        int i = all_Tuples.size();
        Set<Set<String>> result = new HashSet<Set<String>>();
        Iterator<Set<String>> itr = allCandidate1Itemsets.iterator();
        while (itr.hasNext()) {
            Set<String> A = itr.next();
            int numberofAinDB = countoverDB(A);
            if (((float) numberofAinDB / i) >= minsup) {
                Set<String> tmp = new HashSet<String>();
                for (String string : A) {
                    tmp.add(string);
                }
                result.add(tmp);
            }
        }
        return result;
    }

    /**
     * @param A
     * @return
     */
    public static int countitemset(Set<String> A) {
        int res = 0;
        String key = generatekeyforset(A);
        if (!Countdictionary.containsKey(key)) {
            res = countoverDB(A);
            Countdictionary.put(key, res);
        } else {
            res = (Integer) Countdictionary.get(key);
        }
        return res;
    }

    /**
     * @param A
     * @return
     */
    public static String generatekeyforset(Set<String> A) {
        String result = "";

        for (String string : A) {
            result += string;
            if (A.size() != 1)
                result += ";";
        }
        return result;

    }

    /**
     * @param A
     * @return
     */
    public static int countoverDB(Set<String> A) {
        int res = 0;
        for (Set<item> tuple : all_Tuples) {
            Set<String> itemsinatuple = new HashSet<String>();
            for (item tupleitem : tuple) {
                String itms = tupleitem.attribute + "," + tupleitem.value;
                itemsinatuple.add(itms);
            }
            if (itemsinatuple.containsAll(A)) {
                res++;
            }
        }
        return res;

    }

    /**
     * @param allCandidate1Itemsets
     * @param supportcount
     * @return
     */
    public static Set<Set<String>> generateFrequent1ItemSet(
            Map allCandidate1Itemsets, float supportcount) {
        int i = all_Tuples.size();

        List<String> results = new ArrayList<String>();
        Set<Set<String>> resultsset = new HashSet<Set<String>>();

        Map map = allCandidate1Itemsets;
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Entry thisEntry = (Entry) entries.next();
            String key = (String) thisEntry.getKey();
            Integer value = (Integer) thisEntry.getValue();

            if (((float) value / i) >= supportcount) {
                results.add(key);
            }
        }
        for (String string : results) {
            Set<String> tmp = new HashSet<String>();
            tmp.add(string);
            resultsset.add(tmp);
        }
        return resultsset;
    }

    /**
     * @param L
     * @return
     */
    public static Set<Set<String>> selfJoining(Set<Set<String>> L) {
        Set<Set<String>> res = new HashSet<Set<String>>();
        Iterator<Set<String>> itr = L.iterator();
        int ik = 1;
        itr.next();

        while (itr.hasNext()) {
            Set<String> AP = new HashSet<String>();
            AP = itr.next();
            Iterator<Set<String>> jtr = L.iterator();
            int jk = 0;
            while (jtr.hasNext()) {
                Set<String> BP = new HashSet<String>();
                BP = jtr.next();
                Set<String> mrg = new HashSet<String>();
                mrg.addAll(AP);
                mrg.addAll(BP);
                if (mrg.size() == AP.size() + 1) {
                    res.add(mrg);
                }
                jk++;
                if (jk > ik)
                    break;
            }
            ik++;
        }
        return res;
    }

    /**
     * @param frequentMItemset
     * @param minimumSupport
     * @return
     */
    public static Set<Set<String>> generateCandidateKItemSets(
            Set<Set<String>> frequentMItemset, float minimumSupport) {
        Set<Set<String>> L = new HashSet<>();
        Set<Set<String>> Lk;
        Set<Set<String>> Cm;

        Lk = frequentMItemset;
        L.addAll(Lk);
        while (Lk.size() > 0) {

            //printDictionary(Lk);

            Cm = selfJoining(Lk);
            Lk = generateFrequentKItemSet(Cm, minimumSupport);
            L.addAll(Lk);
        }
        FrqMinEndTime= System.currentTimeMillis();
        return L;
    }

    /**
     * @param data
     */
    public static void printDictionary(Set<Set<String>> data) {
        int i = 1;
        for (Set<String> rows : data) {
            System.out.print("#" + i + "-----------------------\r\n");
            for (String cols : rows) {
                System.out.print(cols + " - ");
            }
            System.out.print("\r\n");
            i++;
        }

    }

    /**
     * @param frequentItem
     * @param n
     * @return
     */
    public static Set<Set<String>> allSubsetsWithNMember(Set<String> frequentItem, Integer n) {

        Set<Set<String>> allSubsets = powerSet(frequentItem);
        Set<Set<String>> allSubsetstmp = new HashSet<Set<String>>();
        for (Set<String> set : allSubsets) {
            if (set.size() == n) {
                allSubsetstmp.add(set);
            }
        }

        return allSubsetstmp;

    }

    /**
     * @param l
     * @param minimumSupport
     * @param minimumConfidence
     * @return
     */
    public static Set<Set<String>> ruleGenerationWithPruning(Set<String> l, float minimumSupport,
                                                             float minimumConfidence) {
        
        int m = l.size();

        Set<Set<String>> bad = new HashSet<Set<String>>();
        Set<Set<String>> good = new HashSet<Set<String>>();
        for (int i = m - 1; i > 0; i--) {

            Set<Set<String>> lk = allSubsetsWithNMember(l, i);

            if (bad.size() > 0) {
                Set<Set<String>> lkn = new HashSet<Set<String>>();
                for (Set<String> set : lk) {
                    Set<String> b = new HashSet<String>();
                    b.addAll(l);
                    b.removeAll(set);
                    for (Set<String> bd : bad) {
                        boolean chk = true;
                        for (String s : bd) {
                            chk = chk & b.contains(s);
                        }
                        if (!chk) {
                            lkn.add(set);
                            break;
                        }
                    }
                }

                lk.removeAll(lk);
                lk.addAll(lkn);
            }
            for (Set<String> set : lk) {
                if (CheckConfidenceOfItemset(set, l, minimumSupport, minimumConfidence)) {
                    good.add(set);

                } else {
                    Set<String> b = new HashSet<String>();
                    b.addAll(l);
                    b.removeAll(set);
                    bad.add(b);

                }
            }
        }

        return good;
    }

    /**
     * @param set
     * @param lk
     * @param minimumSupport
     * @param minimumConfidence
     * @return
     */
    private static boolean CheckConfidenceOfItemset(Set<String> set, Set<String> lk, float minimumSupport,
                                                    float minimumConfidence) {

        int transactionNo = all_Tuples.size();
        int c_set = countitemset(set);
        int c_lk = countitemset(lk);
        float support = (float) c_lk / transactionNo;
        float confidence = (float) c_lk / c_set;

        if ((support >= minimumSupport) & (confidence >= minimumConfidence)) {

            return true;
        } else {
            return false;
        }

    }

    /**
     * @param Lk
     * @param minimumSupport
     * @param minimumConfidence
     */
    public static void ruleGeneration(Set<Set<String>> Lk, float minimumSupport, float minimumConfidence) {

        RuleGenStartTime = System.currentTimeMillis();
        int transactionNo = all_Tuples.size();
        List<generatedRule> generatedRules = new ArrayList<generatedRule>();
        for (Set<String> lk : Lk) {
            if (lk.size() >= 2) {

                Set<Set<String>> allSubsets = ruleGenerationWithPruning(lk, minimumSupport, minimumConfidence);
                for (Set<String> set : allSubsets) {

                    Set<String> B = new HashSet<String>();
                    B.addAll(lk);
                    B.removeAll(set);

                    int c_set = countitemset(set);
                    int c_lk = countitemset(lk);
                    float support = (float) c_lk / transactionNo;
                    float confidence = (float) c_lk / c_set;

                    generatedRule generatedRule = new generatedRule();
                    generatedRule.support = support;
                    generatedRule.confidence = confidence;
                    generatedRule.leftsideItemsets = set;
                    generatedRule.rightsideitemsets = B;
                    generatedRules.add(generatedRule);

                }
            }
        }
        RuleGenEndTime = System.currentTimeMillis();
        printRules(generatedRules, minimumSupport, minimumConfidence);
    }

    /**
     * @param Lk
     * @param minsup
     * @param minconf
     */
    public static void rule_gen_basic(Set<Set<String>> Lk, float minsup,
                                      float minconf) {

        int transactionNo = all_Tuples.size();
        List<generatedRule> generatedRules = new ArrayList<generatedRule>();
        for (Set<String> lk : Lk) {
            if (lk.size() >= 2) {
                Set<Set<String>> allSubsets = powerSet(lk);
                // for (int i = 0; i < array.length; i++) {

                // for (int itn=set.size()-1 -> 1
                // set=allSubsetsWithNMember(allSubsets,n)
                for (Set<String> set : allSubsets) {
                    // b=lk-S (s=>lk-s) (s=>B) // S=set
                    Set<String> B = new HashSet<String>();
                    B.addAll(lk);
                    B.removeAll(set);
                    // intersect s,lk-s or s,b
                    Set<String> IntersectlkB = new HashSet<String>();
                    IntersectlkB.addAll(set);
                    IntersectlkB.retainAll(B);
                    if ((B.size() > 0) & (IntersectlkB.size() == 0)) {//

                        int c_set = countitemset(set);
                        int c_lk = countitemset(lk);
                        float support = (float) c_lk / transactionNo;
                        float confidence = (float) c_lk / c_set;
                        if ((support >= minsup) & (confidence >= minconf)) {
                            generatedRule generatedRule = new generatedRule();
                            generatedRule.support = support;
                            generatedRule.confidence = confidence;
                            generatedRule.leftsideItemsets = set;
                            generatedRule.rightsideitemsets = B;
                            generatedRules.add(generatedRule);

                        }
                    }
                }
            }
        }

        printRules(generatedRules, minsup, minconf);
    }

    /**
     * @param generatedRules
     * @param minsup
     * @param minconf
     */
    public static void printRules(List<generatedRule> generatedRules,
                                  float minsup, float minconf) {
        int transactionNo = all_Tuples.size();
        int k = 1;
        PrintWriter writer;
        try {
            writer = new PrintWriter("Rules", "UTF-8");
            writer.println("------------------------------------------------------------");
            writer.println("Association Rules Data Mining (Apriori Algorithm)");
            writer.println("------------------------------------------------------------");

            writer.println("Summary:\r\n");
            writer.println("Input data file: " + Filename);
            writer.println("Total rows in the original set: " + transactionNo);
            writer.println("Total rules discovered:  " + generatedRules.size());
            writer.printf("The selected measures: Support=%.2f Confidence=%.2f\r\n", minsup, minconf);
            writer.println("-------------------------------------------------------------------");
            long dbprocesstime=dbEndTime- dbStartTime;
            writer.printf("reading Dataset starts at %s   Total Rule Dataset loading Time: %d milliseconds\r\n",
                    DateFormat.getDateInstance(DateFormat.FULL).format(dbStartTime),dbprocesstime);
            writer.println("-------------------------------------------------------------------");
            long frqMinprocesstime=FrqMinEndTime - FrqMinStartTime;
            writer.printf("Frequent Item Mining starts at %s   Total Frequent Item Mining Time: %d milliseconds\r\n",
                    DateFormat.getDateInstance(DateFormat.FULL).format(FrqMinStartTime),frqMinprocesstime);
            writer.println("-------------------------------------------------------------------");


            long ruleGenprocesstime=RuleGenEndTime- RuleGenStartTime;
            writer.printf("rule Generation starts at %s   Total Rule Generation Time: %d milliseconds\r\n",
                    DateFormat.getDateInstance(DateFormat.FULL).format(RuleGenStartTime),ruleGenprocesstime);
            writer.println("-------------------------------------------------------------------");

            System.out
                    .print("-------------------------------------------------------------------\r\n");

            System.out.print("\r\n");
            writer.println("\r\n");
            System.out.print("Rules:\r\n");
            writer.println("Rules:\r\n");

            for (generatedRule generatedRule : generatedRules) {
                System.out.printf(" \r\nRule#" + k
                                + ": (Support=  %.2f , Confidence= %.2f ) \r\n { ",
                        generatedRule.support, generatedRule.confidence);
                 writer.printf(" \r\nRule#" + k
                 + ": (Support=  %.2f , Confidence= %.2f ) \r\n { ",
                 generatedRule.support,generatedRule.confidence);
                int tmpi = 1;
                for (String string : generatedRule.leftsideItemsets) {
                    System.out.print(string.replace(',', '='));
                     writer.print(string.replace(',', '='));
                    if (tmpi != generatedRule.leftsideItemsets.size()) {
                        System.out.print("  ");
                         writer.print("  ");
                    }
                    tmpi++;
                }
                System.out.print(" }\r\n----> { ");
                writer.print(" }\r\n----> { ");
                tmpi = 1;
                for (String string : generatedRule.rightsideitemsets) {
                    System.out.print(string.replace(',', '='));
                     writer.print(string.replace(',', '='));
                    if (tmpi != generatedRule.rightsideitemsets.size()) {
                        System.out.print("  ");
                         writer.print("  ");
                    }
                    tmpi++;
                }
                System.out.print(" } \r\n");
                writer.print(" } \r\n");
                k++;

            }
            System.out.print("The result is in the file Rules.\r\n");

            System.out.print("*** Algorithm Finished ***\r\n");

            writer.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param A
     * @return
     */
    public static Set<Set<String>> powerSet(Set<String> A) {

        Set<Set<String>> subSets = new HashSet<Set<String>>();
        for (String addToSets : A) {
            Set<Set<String>> newSets = new HashSet<Set<String>>();
            for (Set<String> curSet : subSets) {
                Set<String> copyPlusNew = new HashSet<String>();
                copyPlusNew.addAll(curSet);
                copyPlusNew.add(addToSets);
                newSets.add(copyPlusNew);
            }
            Set<String> newValSet = new HashSet<String>();
            newValSet.add(addToSets);
            newSets.add(newValSet);
            subSets.addAll(newSets);
        }
        /*
         * for (Set<String> set : subSets) { for (String setEntry : set) {
		 * System.out.print(setEntry + " "); } System.out.println(); }
		 */
        return subSets;
    }
}
