import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.Objects;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
public class HtmlParser {
    

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();

            Elements stockNames = doc.select("th");
            Elements stockPrices = doc.select("td");

            String[] names = stockNames.text().split("\\s+");
            String[] prices = stockPrices.text().split("\\s+");

FileWriter writer = new FileWriter("data.csv", true);

            String filePath = "data.csv";
            StringBuilder DataString = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    DataString.append(line).append("\n"); //add to StringBuilder
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            String datastring = DataString.toString();
            /*System.err.println(datastring);*/


                //System.err.println(args[0]);
                //
                if(!(args[0].equals("1"))){
                    writeData(args, writer, names, prices,datastring);
                }
                    writer.close();

            String resu = "";
            if (args[1].equals("0")) {
                    resu = datastring;
                }
            else if (args[1].equals("1")) {
                    resu = case_1(datastring,  args, names);
                }
            else if (args[1].equals("2")) {
                    resu = case_2(datastring,  args, names);
                }
            else if (args[1].equals("3")) {
                    resu = case_3(datastring, args, names);
                    //"AIG,GOOGL,C,1,30\n23.38,16.02,4.27\nAIG,GOOGL,C,9,25\n13.18,13.0,4.3\nAIG,GOOGL,C,15,29\n17.18,15.52,4.65\nAIG,GOOGL,C,7,17\n10.31,2.96,2.92\nAIG,GOOGL,C,1,21\n12.71,7.3,3.49\nAIG,GOOGL,C,14,28\n16.48,15.76,4.52\nAIG,GOOGL,C,12,30\n20.48,17.77,5.16\nAIG,GOOGL,C,17,29\n17.11,13.62,4.49\nAIG,GOOGL,C,5,24\n13.48,10.68,3.92\nAIG,GOOGL,C,22,30\n16.91,7.82,2.95";
                
                }
            else if (args[1].equals("4")) {
                    resu = case_4(datastring, args, names);
                }



            try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true))) {
                bw.append(resu);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeData(String[] args, FileWriter writer, String[] names, String[] prices,String datastring) throws IOException {

        if(datastring.length()== 0){
            for (int i = 0; i < names.length; i++) {
                if (i == names.length - 1) {
                    writer.append(names[i]);
                    writer.append("\n");
                } else {
                    writer.append(names[i]).append(",");
                }
            }
            }

        for (int i = 0; i < prices.length; i++) {
            if (i == prices.length - 1) {
                writer.append(prices[i]);
                writer.append("\n");
            } else {
                writer.append(prices[i]).append(",");
            }
        }

    }

    public static String case_1(String datastring, String[]    args, String[] names) {
        StringBuilder case_1_resu = new StringBuilder();
        case_1_resu.append(args[2]).append(",").append(args[3]).append(",").append(args[4]);
        case_1_resu.append("\n");
        
        String [] stock_prices1 = datastring.split("\n");
        String[][] stock_pricee = new String[stock_prices1.length][];
        String [] stock_prices2 = new String[stock_prices1.length];
        for(int i = 0; i < stock_prices1.length; i++){
            stock_prices2 = stock_prices1[i].split(",");
            stock_pricee[i] = stock_prices2;
        }
        int goal_stock = 0;
        for(int i = 0; i < names.length;i++){
            if(args[2].equals(stock_pricee[0][i])){
                goal_stock = i;
            }
        }
        int end_da = Integer.parseInt(args[4]) - 5;
        //
        for (int i = Integer.parseInt(args[3]) - 1; i <= end_da ; i++) {
            double mean = 1;
            mean = (Double.parseDouble(stock_pricee[i+1][goal_stock]) + Double.parseDouble(stock_pricee[i+2][goal_stock]) + Double.parseDouble(stock_pricee[i + 3][goal_stock]) + Double.parseDouble(stock_pricee[i+5][goal_stock]) + Double.parseDouble(stock_pricee[i + 4][goal_stock]))/5;
            //
            DecimalFormat df = new DecimalFormat("#.##");
            String roundedValue = df.format(mean);
            double mean_tr = Double.parseDouble(roundedValue);
            if(i ==end_da ){
            case_1_resu.append(parse(mean_tr));
            }
            else{
            case_1_resu.append(parse(mean_tr)).append(",");    
            }
        }
        case_1_resu.append("\n");
        return case_1_resu.toString();
    }

    public static String case_2(String datastring, String[]args , String[] names) {
        StringBuilder case_2_resu = new StringBuilder();
        case_2_resu.append(args[2]).append(",").append(args[3]).append(",").append(args[4]);
        case_2_resu.append("\n");
        
        String [] stock_prices1 = datastring.split("\n");
        String[][] stock_pricee = new String[stock_prices1.length][];
        String [] stock_prices2 = new String[stock_prices1.length];
        int sta_da =  Integer.parseInt(args[3]);
        int end_da = Integer.parseInt(args[4]);
        double mean = 0;
        int goal_stock = 0;
        double variance = 0;
        double sqrt_variance = 0;

        double minus = 0;
        
        //
        for(int i = 0; i < stock_prices1.length; i++){
            stock_prices2 = stock_prices1[i].split(",");
            stock_pricee[i] = stock_prices2;
        }
        
        for(int i = 0; i < names.length;i++){
            if(args[2].equals(stock_pricee[0][i])){
                goal_stock = i;
            }
        }
        //aver
        for (int i = sta_da ; i <= end_da ; i++) {
            
            mean += (Double.parseDouble(stock_pricee[i][goal_stock]));
            
            
        }
        mean = mean/(end_da -sta_da + 1);
        //standard
        for(int i =sta_da; i<=end_da ; i++){
            minus += (Double.parseDouble(stock_pricee[i][goal_stock]) - mean) * (Double.parseDouble(stock_pricee[i][goal_stock]) - mean);

        }
            variance = (minus)/(end_da - sta_da);
            sqrt_variance=newtons(variance);
            sqrt_variance = roundToTwoDecimalPlaces(sqrt_variance);
            case_2_resu.append(parse(sqrt_variance)).append("\n");
            
        return case_2_resu.toString();
    }


    
    public static String case_3(String datastring, String[]args , String[] names) {
        StringBuilder case_3_resu = new StringBuilder();

        String [] stock_prices1 = datastring.split("\n");
        String[][] stock_pricee = new String[stock_prices1.length][];
        String [] stock_prices2 = new String[stock_prices1.length];
        double [] top_3_variance = new double[names.length+1];
        double [] top_3_variance_c = new double[names.length+1];
        int sta_da =  Integer.parseInt(args[3]);
        int end_da = Integer.parseInt(args[4]);
        double mean = 0;
        double [] every_mean = new double[names.length+1];
        double variance = 0;
        double sqrt_variance = 0;
        double minus = 0;
        String [] top_3_St = new String[3];
        
        
        for(int i = 0; i < stock_prices1.length; i++){
            stock_prices2 = stock_prices1[i].split(",");
            stock_pricee[i] = stock_prices2;
        }
        //aver
        for(int j = 1; j < names.length; j++){
            for (int i = sta_da ; i <= end_da ; i++) {
                mean += (Double.parseDouble(stock_pricee[i][j]));
            }
            mean = mean/(end_da -sta_da + 1);
            every_mean[j] = mean;
            mean = 0;
        }
        
        //standard
        for(int j = 1; j < names.length; j++){
            for(int i =sta_da; i<=end_da ; i++){
                minus += (Double.parseDouble(stock_pricee[i][j]) - every_mean[j]) * (Double.parseDouble(stock_pricee[i][j]) - every_mean[j]);
            }
            variance = (minus)/(end_da - sta_da);
            sqrt_variance=newtons(variance);
            sqrt_variance = roundToTwoDecimalPlaces(sqrt_variance);
            minus = 0;
            top_3_variance[j]=sqrt_variance;
        }
        top_3_variance_c = top_3_variance.clone();
        Arrays.sort(top_3_variance);
        for(int i  = 1; i<= names.length   ;i++ ){
             if((top_3_variance_c[i] ==  top_3_variance[names.length])&(top_3_St[0] == null)){
                top_3_St[0] = stock_pricee[0][i];
             }
             else if((top_3_variance_c[i] ==  top_3_variance[names.length -1])&(top_3_St[1] == null)){
                top_3_St[1] = stock_pricee[0][i];
             }
             else if((top_3_variance_c[i] ==  top_3_variance[names.length - 2])&(top_3_St[2] == null)){
                top_3_St[2] = stock_pricee[0][i];
             }
        }
        case_3_resu.append(top_3_St[0]).append(",").append(top_3_St[1]).append(",").append(top_3_St[2]).append(",").append(args[3]). append(",").append(args[4]);
        case_3_resu.append("\n");
        case_3_resu.append(parse(top_3_variance[names.length])).append(",").append(parse(top_3_variance[names.length-1])).append(",").append(parse(top_3_variance[names.length-2]));

        case_3_resu.append("\n");
        return case_3_resu.toString();
    }
    public static String case_4(String datastring, String[]args , String[] names) {
        StringBuilder case_4_resu = new StringBuilder();
        case_4_resu.append(args[2]).append(",").append(args[3]).append(",").append(args[4]);
        case_4_resu.append("\n");
        String [] stock_prices1 = datastring.split("\n");
        String[][] stock_pricee = new String[stock_prices1.length][];
        String [] stock_prices2 = new String[stock_prices1.length];
        double Y_mean = 0;
        double Time_mean = 0;
        double b0 = 0;
        double b1=0;
        double cigma_time= 0;
        double cigma_y_time =0; 
        int sta_day=Integer.parseInt(args[3]);
        int end_day = Integer.parseInt(args[4]);
        int goal_stock = 0;

        for(int i = 0; i < stock_prices1.length; i++){
            stock_prices2 = stock_prices1[i].split(",");
            stock_pricee[i] = stock_prices2;
        }
        for(int i = 0; i < names.length;i++){
            if(args[2].equals(stock_pricee[0][i])){
                goal_stock = i;
            }
        }

        for (int i = sta_day ; i <= end_day ; i++) {
            Y_mean += (Double.parseDouble(stock_pricee[i][goal_stock]));
        }
        Y_mean = Y_mean/(end_day -sta_day + 1);
        for (int i = sta_day; i <= end_day ; i++) {
            Time_mean += i ;
        }
        Time_mean = Time_mean/(end_day -sta_day + 1);
        for(int i = sta_day; i <= end_day;i++){
            cigma_y_time += ((Double.parseDouble(stock_pricee[i][goal_stock]) - Y_mean)*((double)i - Time_mean));
            cigma_time += ((i - Time_mean)*(i - Time_mean));
            b1 = cigma_y_time/cigma_time;
            b0 = Y_mean -b1*Time_mean;
        }
        b1 = roundToTwoDecimalPlaces(b1);
        b0 = roundToTwoDecimalPlaces(b0);
        case_4_resu.append(parse(b1)).append(",").append(parse(b0)).append("\n");
        return case_4_resu.toString();
    }

    public static double newtons(double a) {
        double xk = 1.0;
        for (int i = 0; i < 100; i++) {
            xk = 0.5 * (xk + a / xk);
        }
        return xk;
    }

     public static double roundToTwoDecimalPlaces(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        String roundedValue = df.format(value);
        return Double.parseDouble(roundedValue);
    }

    public static String parse(double num) {
    if((int) num == num) return Integer.toString((int) num);
    return String.valueOf(num);
    }
    }