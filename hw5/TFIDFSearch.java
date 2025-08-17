import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class TFIDFSearch {
    public static String name;

    public static Indexer loadIndex(String name) {
        Indexer indexer = null;
        try (FileInputStream fis = new FileInputStream(name + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            indexer = (Indexer) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return indexer;
    }

    public static void main(String[] args) {
        String name = args[0];
        StringBuilder doc1 = new StringBuilder();
        Indexer indexer = loadIndex(name);
        if (indexer != null) {
            for (String line : indexer.getContent()) {
                doc1.append(line);
            }
        } else {
            System.err.println("error");
            return;
        }

        String doc = doc1.toString();
        List<String> everyFiveString = textToFiveString(doc);

        try {
            String filename = "output.txt";
            List<String> testfile = Files.readAllLines(Paths.get(args[1]));
            int printIdf = Integer.parseInt(testfile.get(0));
            List<String> testWord = new ArrayList<>();
            List<String> andOr = new ArrayList<>();

            for (int i = 1; i < testfile.size(); i++) {
                String line = testfile.get(i);
                testWord.add(line.replaceAll("AND", " ").replaceAll("OR", "").replaceAll("\\s+", " "));
                if (line.contains("AND")) {
                    andOr.add("AND");
                } else if (line.contains("OR")) {
                    andOr.add("OR");
                } else {
                    andOr.add("");
                }
            }

            double[][][] tfAns =  new double[everyFiveString.size()][testWord.size()][];
            double[][] idfAns = new double[testWord.size()][];
            int[][] searchAndOr = new int[testWord.size()][everyFiveString.size()];

            idfAns = idf(idfAns, everyFiveString, testWord, andOr, searchAndOr);
            tfAns = tf(tfAns, everyFiveString, testWord, searchAndOr);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
                for (int k = 0; k < testWord.size(); k++) {
                    double[][] ansAO = new double[everyFiveString.size()][2];

                    for (int a = 0; a < everyFiveString.size(); a++) {
                        if (andOr.get(k).equals("AND") && searchAndOr[k][a] == 0) {
                            calculateTfIdf(idfAns, tfAns, ansAO, k, a);
                        } else if (andOr.get(k).equals("OR") && searchAndOr[k][a] == 2) {
                            calculateTfIdf(idfAns, tfAns, ansAO, k, a);
                        } else if (searchAndOr[k][a] == 3) {
                            calculateTfIdf(idfAns, tfAns, ansAO, k, a);
                        }
                    }
                    
                    Arrays.sort(ansAO, (y, z) -> Double.compare(y[0], z[0]));
                    for (int i = 0; i < printIdf; i++) {
                        //
                        bufferedWriter.write(String.valueOf((int) ansAO[i][1] - 1) + " ");
                    }
                    bufferedWriter.write("\n");
                }//tfAns = null;idfAns = null;
            } catch (IOException e) {
                System.err.println("writing error：" + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculateTfIdf(double[][] idfAns, double[][][] tfAns, double[][] ansAO, int k, int a) {
        for (int i = 0; i < idfAns[k].length; i++) {
            if (i == 0) {
                ansAO[a][0] = 0;
            }
            ansAO[a][0] -= idfAns[k][i] * tfAns[a][k][i];
            ansAO[a][1] = a + 1;
        }
    }

    public static List<String> textToFiveString(String doc) {
        List<String> paragraphList = new ArrayList<>();
        StringBuilder paragraph = new StringBuilder();
        String[] sentences = doc.split("\n");

        for (int i = 0; i < sentences.length; i++) {
            sentences[i] = sentences[i].toLowerCase().replaceAll("[^a-z\t]", " ");
            sentences[i] = sentences[i].replaceAll("\\s+", " ").strip();
            paragraph.append(sentences[i]).append(" ");
            if ((i + 1) % 5 == 0) {
                paragraphList.add(paragraph.toString().strip());
                paragraph.setLength(0);
            }
        }
        if (paragraph.length() > 0) {
            paragraphList.add(paragraph.toString().strip());
        }
        return paragraphList;
    }

    public static double[][][] tf(double[][][] tfAns,List<String> everyFiveString, List<String> testWord, int[][] searchAndOr) {
        //double[][][] tfResult = new double[everyFiveString.size()][testWord.size()][];
        for (int a = 0; a < everyFiveString.size(); a++) {
            Trie trie = new Trie();
            String[] forTrie = everyFiveString.get(a).split(" ");
            for (String word : forTrie) {
                trie.insert(word);
            }
            for (int k = 0; k < testWord.size(); k++) {
                String[] term = testWord.get(k).split(" ");
                tfAns[a][k] = new double[term.length];
                for (int b = 0; b < term.length; b++) {
                    tfAns[a][k][b] = (double) trie.countWordsWithPrefix(term[b]) / forTrie.length;
                }
            }
        }
        return tfAns;
    }

    public static double[][] idf(double[][] idfAns, List<String> everyFiveString, List<String> testWord, List<String> andOr, int[][] searchAndOr) {
        //double[][] printIdf = new double[testWord.size()][];
        int[][] number_doc_contain_term = new int[testWord.size()][];

        for (int k = 0; k < testWord.size(); k++) {
            String[] testWordWithSplit = testWord.get(k).split(" ");
            number_doc_contain_term[k] = new int[testWordWithSplit.length];
            searchAndOr[k] = new int[everyFiveString.size()];
        }

        for (int a = 0; a < everyFiveString.size(); a++) {
            Trie trie = new Trie();
            String[] words = everyFiveString.get(a).split(" ");
            for (String word : words) {
                trie.insert(word);
            }

            for (int k = 0; k < testWord.size(); k++) {
                String[] testWordWithSplit = testWord.get(k).split(" ");
                for (int i = 0; i < testWordWithSplit.length; i++) {
                    if (andOr.get(k).equals("AND")) {
                        if (trie.search(testWordWithSplit[i])) {
                            number_doc_contain_term[k][i]++;
                        } else {
                            searchAndOr[k][a] = -1;
                        }
                    } else if (andOr.get(k).equals("OR")) {
                        if (trie.search(testWordWithSplit[i])) {
                            number_doc_contain_term[k][i]++;
                            searchAndOr[k][a] = 2;
                        }
                    } else {
                        if (trie.search(testWordWithSplit[i])) {
                            number_doc_contain_term[k][i]++;
                            searchAndOr[k][a] = 3;
                        }
                    }
                }
            }
        }

        for (int k = 0; k < testWord.size(); k++) {
            String[] testWordWithSplitAnother = testWord.get(k).split(" ");
            idfAns[k] = new double[testWordWithSplitAnother.length];
            for (int i = 0; i < testWordWithSplitAnother.length; i++) {
                if (number_doc_contain_term[k][i] != 0) {
                    idfAns[k][i] = Math.log((double) everyFiveString.size() / number_doc_contain_term[k][i]);
                } else {
                    idfAns[k][i] = 0;
                }
            }
        }
        return idfAns;
    }
}
class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;
    int count = 0;

    public TrieNode() {
        children = new TrieNode[26];
        count = 0;
        isEndOfWord = false;
    }
}

class Trie {
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.count += 1;
        node.isEndOfWord = true;
    }

    public int countWordsWithPrefix(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        return node.count;
    }


    public boolean search(String word) {
         TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return false;
            }
        }
        return node.isEndOfWord;
    }
}








