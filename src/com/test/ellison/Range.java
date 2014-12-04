package com.test.ellison;

public class Range{
	private double from ;
	private double to ;
	private double fee ;

	public Range( double start, double end ,double fee){
		this.from = start;
		this.to = end;
		this.fee = fee;
	}

	public boolean contains( double value ){
        return (value >= this.from && value <= this.to);
	}
	
	public double getFee(String isCorp){
		int i = 0 ;
		if (isCorp.equals("Corporate")){
			return fee;
		}else if(isCorp.equals("Individual")){
			return fee;
		}
		return i;		 
	}
}