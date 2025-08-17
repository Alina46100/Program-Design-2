import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicIntegerArray;



public class TFIDFCalculator {

    public static void main(String[] args) {
        
        String doc = "";
        
        try {
            doc = Files.readString(Paths.get(args[0]));
        } catch (IOException e) {
            System.err.println("無法讀取文件 ");
            e.printStackTrace();
            return;
        }
        List<String> everyFiveString =  textToFiveString(doc);
        System.err.println(everyFiveString.get(0));
        try {
            List<String> line=Files.readAllLines(Paths.get(args[1]));
            String[] inNo = line.get(1).split(" ");
            String[] inWord = line.get(0).split(" ");
            int argsLength  = inWord.length;
            double[] tfAns = new double[argsLength];
            double[] idfAns = new double[argsLength];
            //double[] tfIdfAns = new double[argsLength];
            String filename = "output.txt";
            
            idfAns = TFIDFCalculator.idf( everyFiveString, inWord,argsLength);
            try {
                FileWriter writer = new FileWriter(filename);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                for(int i  = 0; i < argsLength;i++){
                String[] forTrie = everyFiveString.get(Integer.parseInt(inNo[i])).split(" ");
                tfAns[i] = TFIDFCalculator.tf(forTrie, inWord[i]);
                bufferedWriter.write(String.format("%.5f", idfAns[i]*tfAns[i]) + " ");
            }
                
                bufferedWriter.close();
            } catch (IOException e) {
                System.err.println("error writing：" + e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> textToFiveString(String doc){
        List<String> paragraphList = new ArrayList<>();
        StringBuilder paragraph = new StringBuilder();
        String[] sentences = doc.split("\n");
        
        for (int i = 0; i < sentences.length; i++) {
            sentences[i] = sentences[i].toLowerCase().replaceAll("[^a-z\t]", " ");
            sentences[i] = sentences[i].replaceAll("\\s+", " ").substring(1);
            paragraph.append(sentences[i]); // Adding period after each sentence
            if ((i + 1) % 5 == 0) { // Check if five sentences have been added
                paragraphList.add(paragraph.toString());
                paragraph = new StringBuilder(); // Reset StringBuilder for next paragraph
            }
        }
        return paragraphList;
    }

    public static double tf(String[] doc, String term) {
        Trie trie = new Trie();
        for(int k  = 0;k< doc.length; k++){
            trie.insert(doc[k]);
        }
        return  (double)trie.countWordsWithPrefix(term)/ doc.length;
    }

   public static double[] idf(List<String> everyFiveString, String[] terms, int length) {
        double[] num = new double[length];
        AtomicIntegerArray numberDocContainTerm = new AtomicIntegerArray(length);

        everyFiveString.parallelStream().forEach(paragraph -> {
            Trie trie = new Trie();
            Arrays.stream(paragraph.split(" ")).forEach(trie::insert);

            IntStream.range(0, length).parallel().forEach(i -> {
                if (trie.search(terms[i])) {
                    numberDocContainTerm.incrementAndGet(i);
                }
            });
        });

int totalDocs = everyFiveString.size();
        IntStream.range(0, length).forEach(i -> {
            num[i] = Math.log((double) totalDocs / (numberDocContainTerm.get(i)));  // Avoid division by zero
        });

        return num;
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
        node.count+=1;
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








