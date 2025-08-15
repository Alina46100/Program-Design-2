import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeGenerator {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("請輸入文件名");
            return;
        }
        String fileName = args[0];
        System.out.println("File name: " + fileName);
        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return;
        }

        ClassProcessor classProcessor = new ClassProcessor();
        classProcessor.processMermaidCode(mermaidCode);
    }
}

class ClassProcessor {

    public void processMermaidCode(String mermaidCode) {
        ClassExtractor classExtractor = new ClassExtractor();
        String[] Array = classExtractor.extractClasses(mermaidCode);
        int class_numcount =3  ;
        if(Array[2].toString().isEmpty()){
            class_numcount = 2;
        }
        if(Array[1].toString().isEmpty()) {
            class_numcount = 1;            
        }
        else{
            class_numcount = 3;            
        }
        for (int a = 0; a < class_numcount; a++) {
            String javaCode = convertMermaidToJava(Array[a]);
            try {
                String[] newfilename = javaCode.split(" ");
                String outputFileName = newfilename[2].trim() + ".java";
                File outputFile = new File(outputFileName);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                    bw.write(javaCode);
                }
                System.out.println("已生成 Java: " + outputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String convertMermaidToJava(String mermaidDiagram) {
        StringBuilder javaCodeBuilder = new StringBuilder();
       // mermaidDiagram = "classDiagram   \n         class             Person\n   Person :    -int age   \n Person : +  integerFunction( int   a , int    b)    int   \nPerson    : +booleanFunction(      ) boolean\nPerson :   +setFunction(   String   jak   )  String\nPerson : +voidFunction()";
        String[] lines = mermaidDiagram.split("\n");
        for (String line : lines) {
            //判斷class物件
            //class BankAccount
            //BankAccount : -String owner
            //BankAccount : -int balance
            //BankAccount : +setOwner(String owner) void
            //BankAccount : +isEnough(int value, int balance) void
            //BankAccount : +getOwner() String
            if (line.contains("class ")) {
                //class BankAccount
                String className = line.substring(line.indexOf("class ") + 6).trim();
                javaCodeBuilder.append("public class ").append(className).append(" {\n");
            } else if (line.contains(":")) {
                //BankAccount : -String owner
                //BankAccount : -int balance
                //BankAccount : +setOwner(String owner) void
                //BankAccount : +isEnough(int value, int balance) void
                //BankAccount : +getOwner() String
                String[] parts = line.split(":");
                String attribute = parts[1].trim();
                // 獲取可見性和物件名
                String visibility = "+"; //public
                if (attribute.startsWith("-")) {
                    visibility = "private";
                    attribute = attribute.substring(1); 
                } else if (attribute.startsWith("+")) {
                    visibility = "public";
                    attribute = attribute.substring(1); 
                }
                String[] attributeParts = attribute.split("\\s+", 2);
                if (attribute.contains("(")) {
                    String[] attrParts = attribute.split("\\(|\\)", 3);
                    String name = attrParts[0].trim();
                    String type = attrParts[1].trim();
                    String rtype = attrParts[2].trim();
                    String[] types = type.split("\\s+");
                    
                    

                    type = "";
                    for(int i = 0; i < types.length; i++){
                        if(types[i].equals(",")){
                            type = type.concat(", ");
                        }
                        else if(types[i].contains(",")){
                            String[] typess = types[i].split("(?<=,)");
                            type = type.concat(typess[0].trim());
                            type = type.concat(" ");
                            //type = type.concat(typess[1].trim());
                            
                            //type = type.concat(" ");
                            
                        }
                        else{
                            //
                            type = type.concat(types[i]);
                            //
                            type = type.concat(" ");
                            if((i!=types.length - 1)){
                                if(types[i+1].equals(",")){
                                type=type.substring(0, type.length() - 1);
                            }
                            }
                        }
                        if (i == (types.length - 1)){
                            type=type.substring(0, type.length() - 1);
                        }
                    }
                    if (rtype.equals("")) {
                        rtype = "void";
                    }
                    if (name.startsWith("set")) {
                        String fieldName = name.substring(3);
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        String[] setrtype = type.split("\\s+");
                        javaCodeBuilder.append("    ").append(visibility).append(" ").append(rtype).append(" ").append(name).append("(").append(type).append(")").append(" {\n");
                        javaCodeBuilder.append("        this.").append(fieldName).append(" = ").append(setrtype[1]).append(";\n");
                        javaCodeBuilder.append("    }\n");
                    } else if (name.startsWith("get")) {
                        String fieldName = name.substring(3);
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        javaCodeBuilder.append("    ").append(visibility).append(" ").append(rtype).append(" ").append(name).append("(").append(type).append(")").append(" {\n");
                        javaCodeBuilder.append("        return").append(" ").append(fieldName).append(";\n");
                        javaCodeBuilder.append("    }\n");
                    } else {
                        javaCodeBuilder.append("    ").append(visibility).append(" ").append(rtype).append(" ").append(name).append("(").append(type).append(") ");
                        if (rtype.equals("void")) {
                            javaCodeBuilder.append("{;}\n");
                        } else if (rtype.equals("int")) {
                            javaCodeBuilder.append("{return 0;}\n");
                        } else if (rtype.equals("String")) {
                            javaCodeBuilder.append("{return \"\";}\n");
                        } else if (rtype.equals("boolean")) {
                            javaCodeBuilder.append("{return false;}\n");
                        }
                    }//BankAccount : -String owner
                //BankAccount : -int balance
                } else {
                    javaCodeBuilder.append("    ").append(visibility).append(" ").append(attributeParts[0]).append(" ").append(attributeParts[1]).append(";\n");
                }
            }
        }
        javaCodeBuilder.append("}\n");//}

        return javaCodeBuilder.toString();
    }
}

class ClassExtractor {
    public String[] extractClasses(String mermaidCode) {
        String[] attribute_to_each_class = mermaidCode.split("\n");
        
        StringBuilder   class_1  = new StringBuilder();;
        StringBuilder class_2 = new StringBuilder();
        StringBuilder  class_3 = new StringBuilder();
        for (int i = 0; i < attribute_to_each_class.length; i++) {
            String for_attribute_to_class = attribute_to_each_class[i];
            if(for_attribute_to_class.contains("class ")){
                //getclass name
                String class_name = for_attribute_to_class.replace("class ","").replace(" {","");
                //class Student {
                if(for_attribute_to_class.contains("{")){
                    if(class_1.toString().isEmpty()){
                        class_1.append(for_attribute_to_class.replace(" {","").trim());
                        class_1.append("\n");
                        
                        while(!(for_attribute_to_class.contains("}"))){

                            for_attribute_to_class = attribute_to_each_class[i];
                            class_1.append(" : ");
                            class_1.append(for_attribute_to_class.trim());
                            class_1.append("\n");
                            if(class_1.toString().contains("}")){
                                class_1.delete(class_1.length() - 5, class_1.length());
                            }
                        }
                        
                    }
                    else if(class_2.toString().isEmpty()){
                        class_2.append(for_attribute_to_class.replace(" {","").trim());
                        class_2.append("\n");
                        while(!(for_attribute_to_class.contains("}"))){
                            i++;
                            for_attribute_to_class = attribute_to_each_class[i];
                            class_2.append(" : ");
                            class_2.append(for_attribute_to_class.trim());
                            class_2.append("\n");
                            if(class_2.toString().contains("}")){
                                class_2.delete(class_2.length() - 5, class_2.length());
                            }
                        }
                    }
                    else if(class_3.toString().isEmpty()){
                        class_3.append(for_attribute_to_class.replace(" {","").trim());
                        class_3.append("\n");
                        while(!(for_attribute_to_class.contains("}"))){
                            i++;
                            for_attribute_to_class = attribute_to_each_class[i];
                            class_3.append(" : ");
                            class_3.append(for_attribute_to_class.trim());
                            class_3.append("\n");
                            if(class_3.toString().contains("}")){
                               class_3.delete(class_3.length() - 5, class_3.length());
                            }
                            //System.err.println(class_3.toString());
                        }
                        
                    }
                    else if(class_1.toString().contains(class_name)){
                        while(!(for_attribute_to_class.contains("}"))){
                            i++;
                            for_attribute_to_class = attribute_to_each_class[i];
                            class_1.append(" : ");
                            class_1.append(for_attribute_to_class.trim());
                            class_1.append("\n");
                            if(class_1.toString().contains("}")){
                                class_1.delete(class_1.length() - 5, class_1.length());
                            }
                        }

                    }
                    
                    else if(class_2.toString().contains(class_name)){
                        while(!(for_attribute_to_class.contains("}"))){
                            i++;
                            for_attribute_to_class = attribute_to_each_class[i];
                            class_2.append(" : ");
                            class_2.append(for_attribute_to_class.trim());
                            class_2.append("\n");
                            if(class_2.toString().contains("}")){
                                class_2.delete(class_2.length() - 5, class_2.length());
                            }
                        }

                    }
                    
                    else if(class_3.toString().contains(class_name)){
                        while(!(for_attribute_to_class.contains("}"))){
                            i++;
                            for_attribute_to_class = attribute_to_each_class[i];
                            class_3.append(" : ");
                            class_3.append(for_attribute_to_class.trim());
                            class_3.append("\n");
                            if(class_3.toString().contains("}")){
                                class_3.delete(class_3.length() - 5, class_3.length());
                            }
                        }

                    }
                }
                //class student
                else{
                    if(class_1.toString().isEmpty()){
                        class_1.append(for_attribute_to_class.trim());
                        class_1.append("\n");
                    }
                    else if(class_2 .toString().isEmpty()){
                        class_2.append(for_attribute_to_class.trim());
                        class_2.append("\n");
                    }
                    else if(class_3.toString().isEmpty()){
                        class_3.append(for_attribute_to_class.trim());
                        class_3.append("\n");
                    }   
                }
            }
            else if(for_attribute_to_class.contains(":")){

                String [] class_name = for_attribute_to_class.split(":");
                if(class_1.toString().substring(6).startsWith(class_name[0].trim())){
                    class_1.append(for_attribute_to_class.trim());
                    class_1.append("\n");
                }
                
                else if(class_2.toString().substring(6).startsWith(class_name[0].trim())){
                    class_2.append(for_attribute_to_class.trim());
                    class_2.append("\n");
                }
                else if(class_3.toString().substring(6).startsWith(class_name[0].trim())){
                    class_3.append(for_attribute_to_class.trim());
                    class_3.append("\n");
                }
     
            }
            
    }
    //System.out.println(class_1.toString());
    String[] Array = new String[3];
    Array[0] = class_1.toString();
    Array[1] = class_2.toString();
    Array[2] = class_3.toString();
        return Array;
    }
}
