package com.test.ellison;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
	private final String pairs;
	private final String baseCur;
    private final String wantedCur;
    private final String who;
    private final Date when;
    private final double amount;
    private static double transactionRate [];
    private static final int NUM = 4;
	private static Range iBand[] = new Range [NUM];
    private static DecimalFormat dfAmt = new DecimalFormat("#.00");
    private static DecimalFormat dfRates = new DecimalFormat("#.0000");
    private static DecimalFormat dfPnL = new DecimalFormat("#.00");
    private static String exportCSV [];
    private static String profitSGD [];
    private static double exRateSGD [];
    private static double finalRate [];
    private static double profit [];
    private static double usdBasedAmt [];

    public Transaction(String transaction, int trades) throws ParseException {
        String[] a = transaction.split(",");
        this.pairs = a[0]+a[1];
        this.baseCur = a[0];
        this.wantedCur = a[1];
        this.who = a[3];
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm");
        this.when = simple.parse(a[4]);
        System.out.println("when"+when);
        this.amount = Double.parseDouble(a[2]);
        DecimalFormat df = new DecimalFormat("#.00"); 
        df.format(amount);

        usdBasedAmt = new double [trades];
        finalRate = new double [trades];
        exportCSV = new String [trades];
        profitSGD = new String [trades];
        profit = new double [trades];
        exRateSGD = new double [trades];
        transactionRate = new double [trades];
    }

    public String getWho() {
        return who;
    }
 
    public double amount() {
       return amount;
    }

    public void findExRateToSGD(Transaction ts[],MarketRate mr[]) {
        for (int k = 1, g = 0; k < ts.length; k++, g++) {
            for (int m = 0; m < mr.length; m++) {
                if (ts[k].wantedCur.equals(mr[m].baseCur) && mr[m].wantedCur.equals("SGD")) {
                    if (ts[k].when.compareTo(mr[m].when) < 0) { //date2 is after date1
                        exRateSGD[g] = mr[m].rates;
                    }
                }else{ // profit for SGD without conversion need
                    profitSGD[g] = exportCSV[g];
                }
            }
        }
    }
    public static void generateCsvFile(Transaction ts[], String sFileName){
    	try{
    	    FileWriter writer = new FileWriter(sFileName);
    	    writer.append("BaseCurrency");
            writer.append(",");
            writer.append("WantedCurrency");
            writer.append(",");
            writer.append("AmountInBaseCurrency");
            writer.append(",");
            writer.append("StandardRate");
            writer.append(",");
            writer.append("FinalRate");
            writer.append(",");
            writer.append("ProfitInWantedCurrency");
            writer.append(",");
            writer.append("ProfitInSGD");
            writer.append(",");
            writer.append('\n');
            for (int a=1,b=0; a< exportCSV.length; a++,b++){
                    writer.append((ts[a].baseCur));
                    writer.append(",");
                    writer.append(ts[a].wantedCur);
                    writer.append(",");
                    writer.append(String.valueOf(ts[a].amount));
                    writer.append(",");
                    writer.append(String.valueOf(Transaction.transactionRate[b]));
                    writer.append(",");
                    writer.append(String.valueOf(dfRates.format(Transaction.finalRate[b])));
                    writer.append(",");
                    writer.append(String.valueOf(exportCSV[b])); //profit
                    writer.append(",");
                    if(ts[a].wantedCur.equals("SGD")) {
                        writer.append(String.valueOf((exportCSV[b]))); //profit
                    }else {
                        profitSGD[b] = String.valueOf(dfPnL.format((profit[b] * exRateSGD[b])));
                        writer.append(String.valueOf(profitSGD[b])); //profit
                    }
                   //     profitSGD[b] = String.valueOf(dfPnL.format((profit[b] * exRateSGD[b])));
                    System.out.println("profitSGD: "+profitSGD[b]+"profit"+profit[b]);
                    writer.append('\n');
            }
    	    writer.flush();
    	    writer.close();
    	}
    	catch(IOException e)
    	{
    	     e.printStackTrace();
    	} 
    }
    public static String inputCSVfromLocal(String csvFile)throws IOException{
        //return new ReadCSV().process(new BufferedReader(new InputStreamReader(System.in)),csvFile);
        return new ReadCSV().process(csvFile);

    }

    public static Transaction[] calculationProfit(Transaction ts[], MarketRate mr[]){
        for (int k=1,g=0; k<ts.length; k++,g++){
            for (int m=0; m<mr.length; m++){
                if (ts[k].pairs.equals(mr[m].pairs)){
                    System.out.println("we made a trade!!!!!!!");
                    double bps = 0.0d;
                    //date compare
                    //check whether market rate was provided after transactions made
                    if (ts[k].when.compareTo(mr[m].when) < 0) { //date2 is after date1
                        transactionRate[g] = mr[m].rates;
                    }else{
                        continue;
                    }

                    System.out.println("Customer :"+ts[k].who+" find the fee by amount :"+dfAmt.format(usdBasedAmt[g])+" in USD");
                    double pInfiniteDouble = Double.POSITIVE_INFINITY;
                    if (ts[k].who.equals("Corporate")){
                        iBand[0] = new Range(1,1000000,0.15/100);
                        iBand[1] = new Range(1000001,3000000,0.1/100);
                        iBand[2] = new Range(3000001, pInfiniteDouble,0.05/100);
                        for (int b=0 ;b<3 ;b++){
                            if (iBand[b].contains(usdBasedAmt[g]))
                                bps = iBand[b].getFee(ts[k].getWho()) ;
                        }
                    }else if(ts[k].who.equals("Individual")){
                        iBand[0] = new Range(1,8000,0.4/100);
                        iBand[1] = new Range(8001,20000,0.35/100);
                        iBand[2] = new Range(20001,35000,0.3/100);
                        iBand[3] = new Range(35001, pInfiniteDouble,0.25/100);
                        for (int c=0 ;c<4 ;c++){
                            if (iBand[c].contains(usdBasedAmt[g]))
                                bps = iBand[c].getFee(ts[k].who) ;
                        }
                    }
                    System.out.println("bps:"+dfRates.format(bps));
                    finalRate[g] = transactionRate[g] * (1- bps);
                    double finalRateRound = new BigDecimal(finalRate[g]).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
                    System.out.println("all rates are 4 decimal places, final rate:"+dfRates.format(finalRate[g])+"transactionRate :"+dfRates.format(transactionRate[g]));
                    profit[g] = (transactionRate[g] - finalRateRound) * ts[k].amount();
                    //profit in SGD
                    System.out.println("==============Daily profit============= "+dfPnL.format(profit[g])+ " SGD");
                    exportCSV[g] = dfPnL.format(profit[g]);
                    break ;
                }else if (!ts[k].wantedCur.equals(mr[m].wantedCur)){ //simplefy
                    ts[k].findExRateToSGD(ts,mr);
                    //System.out.println("~~~~~~~~~~~~convert~~~~~~~~~~~~~~~"+profitSGD[g]+"g:"+g);
                }
            }
        }
        return ts;
    }

    public static void ConvertUSD(Transaction ts[], MarketRate mr[]){
        //convert to USD
        for (int k=1,g=0; k<ts.length; k++,g++){
            for (int m=0; m<mr.length; m++){
                if(mr[m].wantedCur.equals("USD") && ts[k].baseCur.equals(mr[m].baseCur)){
                    usdBasedAmt[g] = ts[k].amount * mr[m].rates;
                    System.out.println("transactions amount : "+dfAmt.format(usdBasedAmt[g])+" IN USD!!!");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String csvTransactions = "/Users/apple/Downloads/moneychange/transactions.csv";
        String csvRates = "/Users/apple/Downloads/moneychange/rates.csv";
        String [] trades = inputCSVfromLocal(csvTransactions).split("\r\n");
    	String [] exRates = inputCSVfromLocal(csvRates).split("\r\n");
        Transaction[] ts = new Transaction[trades.length];
        MarketRate[] mr = new MarketRate[exRates.length];
        for (int i=1; i<trades.length; i++){
            ts[i] = new Transaction(trades[i],trades.length);
        }
        for (int j=0; j<exRates.length; j++){
            mr[j] = new MarketRate(exRates[j]);
        }
        Transaction.ConvertUSD(ts,mr);
        Transaction.generateCsvFile(Transaction.calculationProfit(ts,mr), "../moneychange/PnL.csv");
    }

}


