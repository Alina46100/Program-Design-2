import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegExp {

    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);

        // For your testing of input correctness
        //System.out.println("The input file:" + args[0]);
        //System.out.println("str1=" + str1);
        //System.out.println("str2=" + str2);
        //System.out.println("num of repeated requests of str2 = " + s2Count);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Your main code should be invoked here
                line = line.toLowerCase();
                //迴文
                System.out.print(isPalindrome(line) ? "Y," : "N,");
                //str1
                System.out.print(containsSubstring(line, str1) ? "Y," : "N,");
                //str2countin
                System.out.print(containsSubstring2(line, str2, s2Count) ? "Y," : "N,");
                System.out.println(checkForBB(line) ? "Y" : "N");
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isPalindrome(String str) {
        String reversed = reverseString(str);
        return str.equals(reversed);
    }

    private static String reverseString(String str) {
        String result = "";

        for (int i = str.length() - 1; i >= 0; i--) {
            result += str.charAt(i);
        }

        return result;
    }

    private static boolean containsSubstring(String str, String targetSubstring) {
        if (str.startsWith(targetSubstring)) {
            return true;
        }

        int targetLength = targetSubstring.length();
        int strLength = str.length();

        for (int i = 0; i <= strLength - targetLength; i++) {
            if (str.substring(i, i + targetLength).equals(targetSubstring)) {
                return true;
            }
        }

        return false;
    }

    private static boolean containsSubstring2(String str2, String targetSubstring2, int count2) {
        int targetLength = targetSubstring2.length();
        int strLength = str2.length();
        int str2count = 0;
        for (int i = 0; i <= strLength - targetLength; i++) {
            if (str2.substring(i, i + targetLength).equals(targetSubstring2)) {
                str2count++;
            }
        }
        if(str2count>=count2){
            return true;
        }
        return false;
    }
    
    public static boolean checkForBB(String Str) {

        for (int i = 0; i < Str.length(); i++) {
            if (Str.charAt(i) == 'a') {

                for (int j = i + 1; j < Str.length(); j++) {
                    if (j + 1 < Str.length() && Str.charAt(j) == 'b' && Str.charAt(j + 1) == 'b') {
                        //"在遇到 'a' 的位置之後找到 'bb'"
                        return true;
                    }
                }

                //"在遇到 'a' 的位置之後未找到 'bb'"
                return false;
            }
        }

        //"未找到 'a'"
        return false;
    }
}