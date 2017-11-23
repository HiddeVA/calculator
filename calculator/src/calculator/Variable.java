package calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Variable
{
	private double[] parameters;
	char letter = 'a';
	
	public Variable(double[] input)
	{
		parameters = input;
	}
	
	public Variable(double x)
	{
		parameters = new double[] {x};
	}
	
	public Variable()
	{
		parameters = new double[] {0};
	}
	
	public int size()
	{
		return parameters.length;
	}
	
	public double getP(int k)
	{
		if (k >= this.size()) return 0;
		else return parameters[k];
	}
	
	public void setP(int k, double x)
	{
		if (k >= this.size()) return;
		else parameters[k] = x;
	}
	
	public String toString()
	{
		NumberFormat fmt = new DecimalFormat("#0.##########");
		String result = "";
		String number;
		String power;
		if (parameters.length == 0) return "0";
		if (parameters.length == 1 && parameters[0] == 0) return "0";
		for (int i = size() - 1; i >= 0; i--) {
			if (parameters[i] == 0)
				continue;
			
			number = fmt.format(parameters[i]);
			if (number.equals("0") || number.equals("-0"))
				continue; //double check for nasty floating point math
			
			if (i == 1)
				power = String.valueOf(letter);
			else if (i == 0)
				power = "";
			else
				power = String.valueOf(letter) + String.valueOf(i);
			
			if (!result.equals("") && parameters[i] > 0)
				result += "+";
			
			if (number.equals("1") && i != 0)
				result += power;
			else
				result += number + power;
		}
		while (result.charAt(result.length() - 1) == '+') {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
