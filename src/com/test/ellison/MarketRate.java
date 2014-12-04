package com.test.ellison;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarketRate {
	protected String pairs;
	protected String baseCur;
	protected String wantedCur;
	protected final Date    when;     
	protected final double rates;   

	public MarketRate(String transaction) throws ParseException { 	
        String[] a = transaction.split(",");
        this.pairs = a[0]+a[1];
        this.baseCur = a[0];
        this.wantedCur = a[1];
        SimpleDateFormat simple = new SimpleDateFormat("HH:mm");
        this.when = simple.parse(a[3]);
        this.rates = Double.parseDouble(a[2]);
        DecimalFormat df = new DecimalFormat("#.0000"); 
        df.format(rates);
	}
}
