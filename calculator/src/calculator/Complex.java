package calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Complex {
	public double real;
	public double imaginary;
	
	public Complex(double radius, double angle, boolean polar)
	{
		real = radius * Math.cos(angle);
		imaginary = radius * Math.sin(angle);
	}

	public Complex(double re, double im)
	{
		real = re;
		imaginary = im;
	}
	
	public Complex(String z)
	{
		int separator = z.indexOf("+");
		if (separator < 0) {
			if (z.charAt(z.length()-1) == 'i') {
				imaginary = Double.parseDouble(z.substring(0,z.length() - 1));
				real = 0;
			}
			else {
				real = Double.parseDouble(z);
				imaginary = 0;
			}
		}
		else {
			real = Double.parseDouble(z.substring(0,separator));
			imaginary = Double.parseDouble(z.substring(separator + 1));				
		}
	}
	
	public Complex(double re)
	{
		real = re;
		imaginary = 0;
	}
	
	public Complex()
	{
		real = imaginary = 0;
	}
	
	public String toString()
	{
		if (real == 0 && imaginary == 0) return "0";
		String re = "";
		String im = "";
		NumberFormat fmt = new DecimalFormat("#0.##########");
		if (real != 0) {
			if (Math.floor(real) == real)
				re = String.valueOf((int)real);
			else
				re = fmt.format(real);
		}
		else {
			re = "0";
		}
		if (imaginary != 0) {
			if (imaginary == 1) im = "i";
			else if (imaginary == -1) im = "-i";
			else if (Math.floor(imaginary) == imaginary) im = String.valueOf((int)imaginary) + 'i';
			else im = fmt.format(imaginary) + 'i';
		}
		else im = "0i";
		if (re.equals("0") || re.equals("-0")) {
			if (im.equals("0i") || im.equals("-0i"))
				return "0";
			else
				return im;
		}
		else {
			if (im.equals("0i") || im.equals("-0i"))
				return re;
			else if (imaginary < 0)
				return re + im;
			else
				return re + "+" + im;
		}
	}
	
	public double abs()
	{
		return Math.hypot(real, imaginary);
	}
	
	public double angle()
	{
		double angle;
		if (real > 0) angle = Math.atan(imaginary/real);
		else if (real < 0) angle = Math.PI + Math.atan(imaginary/real);
		else angle = Math.PI/2 * Math.signum(imaginary);
		return angle;
	}
}
