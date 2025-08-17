import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

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

public class BuildIndex {

    public static void buildIndex(String name, ArrayList<String> content) {
        Indexer indexer = new Indexer(content);
        
        try (FileOutputStream fos = new FileOutputStream(name + ".ser");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(indexer);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("given file name");
            return;
        }
     String name = args[0].substring(args[0].lastIndexOf('/') + 1, args[0].lastIndexOf('.'));
        String doc = args[0];
        StringBuilder doc1 = new StringBuilder(doc);
        //doc1.append(".txt");

        ArrayList<String> content = new ArrayList<>();

        try {
            

            doc = Files.readString(Paths.get(doc));
            String[] lines = doc.split("\t\n");
            for (String line : lines) {
                content.add(line);
            }
        } catch (IOException e) {
            System.err.println("error ");
            e.printStackTrace();
            return;
        }
        
        buildIndex(name, content);

        
        
    }
}
