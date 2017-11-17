package calculator;

import java.util.Arrays;

import calculator.Calculator.Style;

public class ComplexExpression extends Expression
{
	private Complex m_result;
	
	public ComplexExpression(String expression, char[] numberSet)
	{
		m_expression = expression;
		m_numberSet = numberSet;
		m_delimiter = numberSet[0];
		m_style = Style.COMPLEX;
	}
	
	public String getResult()
	{
		return m_result.toString();
	}
	
	public void evaluate()
	{
		m_result = cevaluate(checkBrackets(m_expression));
	}
	
	private Complex cevaluate(String expression)
	{
		if (expression.length() == 0) return new Complex(0, 0);
		
		Complex[] memory = new Complex[2];
		int pointer = 0;
		char ascii;
		
		boolean operator = false;
		char operatortype = ' ';
		boolean singularoperator = false;
		char xoperatortype = ' ';
		boolean number = false;
		
		for (int i = 0; i < expression.length(); i++) {
			ascii = expression.charAt(i);
			if (isNumeric(ascii)) {//find a number
				int j = i;
				while (j < expression.length()) {//the number continues until we find a non-numeric character
					if (isNumeric(expression.charAt(j))) j++;
					else break;
				}
				try {
					memory[pointer] = parseComplex(expression.substring(i,j).replace(m_delimiter, '.'));
				}
				catch (NumberFormatException nfe) {
					System.out.println("Could not convert:" + expression.substring(i,j));
				}
				i = j - 1; //skip to the first non-numeric character to continue
				
				if (singularoperator) {
					memory[pointer] = doMath(memory[pointer], xoperatortype);
					singularoperator = false;
				}
				if (number && operator) {//if there is an operator ready, we can immediately use it
					memory[0] = doMath(memory[0], memory[1], operatortype);
					pointer--;
					operator = false;
				}
				number = true;
			}
			
			else if (isOperator(ascii)) {//set an operator waiting
				if (!number) {//operators at the start are ignored
					if (ascii == '-') {//except the - operator
						memory[pointer] = new Complex();
						pointer++;
						operatortype = '-';
						operator = number = true;
					}
				}
				else if (!operator) {//operators are not overwritten
					operatortype = expression.charAt(i);
					operator = true;
					pointer++;
				}
				else if (ascii == '-') {//except the - operator, which can change the existing operator
					switch (operatortype) {
					case '+': operatortype = '-'; break;
					case '*': case '/': case ':': case '÷': memory[pointer-1] = doMath(memory[pointer-1], new Complex(-1), '*');
					}
				}
			}
			else if (isOpeningBracket(ascii)) {//a bracket means evaluating a smaller expression first with some basic recursion
				int k = findClosingBracket(expression, i);
				try {
					memory[pointer] = cevaluate(expression.substring(i + 1, k)); //opening/closing brackets should be ignored
				}
				catch (StringIndexOutOfBoundsException stre) {
					System.out.println("Error trying to read: " + expression);
					break;
				}
				if (singularoperator) {
					memory[pointer] = doMath(memory[pointer], xoperatortype);
					singularoperator = false;
				}
				
				if (number && operator) {
					memory[0] = doMath(memory[0], memory[1], operatortype);
					pointer--;
					operator = false;
				}
				else
					number = true;
				i = k;
			}
			else if (ascii == 101 || ascii == 960) {
				switch (ascii) {
				case 101: memory[pointer] = new Complex(Math.E); break;
				case 960: memory[pointer] = new Complex(Math.PI); break;
				default:
				}
				if (singularoperator) {
					memory[pointer] = doMath(memory[pointer], xoperatortype);
					singularoperator = false;
				}
				
				if (number && operator) {
					memory[0] = doMath(memory[0], memory[1], operatortype);
					pointer--;
					operator = false;
				}
				else
					number = true;
			}
			else if (isSingularOperator(ascii)) {
				singularoperator = true;
				xoperatortype = ascii;
			}			
		}
		return memory[0];
	}
	
	protected Complex doMath(Complex s, Complex a, char ch) 
	{
		Complex z = new Complex();
		switch (ch) {
		case '+':
			z.real = a.real + s.real;
			z.imaginary = s.imaginary + a.imaginary;
			break;
		case '-':
			z.real = s.real - a.real;
			z.imaginary = s.imaginary - a.imaginary;
			break;
		case '*':
			z.real = a.real * s.real - a.imaginary * s.imaginary;
			z.imaginary = a.real * s.imaginary + a.imaginary * s.real;
			break;
		case '/':
			z.real = (s.real * a.real + s.imaginary * a.imaginary) / (a.real * a.real + a.imaginary * a.imaginary);
			z.imaginary = (s.imaginary * a.real - s.real * a.imaginary) / (a.real * a.real + a.imaginary * a.imaginary);
			break;
		case '^':
			//TODO: Results are correct if either imaginary part is 0 or real part is 0.
			double radius =  Math.pow(s.abs(), a.real);
			double angle = s.angle()*a.real;
			Complex za = new Complex(radius, angle, true);
			double b = Math.log(s.abs()) * a.imaginary;
			Complex zb = new Complex(1, b, true);
			z = doMath(za, zb, '*');
			break;
		default:
		}
		return z;
	}
	
	protected Complex doMath(Complex s, char ch)
	{
		Complex z = new Complex();
		switch (ch) {
		case 8730:
			double radius = s.abs();
			double angle = s.angle() / 2;
			radius = Math.sqrt(radius);
			z = new Complex(radius, angle, true);
			break;
		case 13265:
			z.real = Math.log(s.real);
		default:
		}
		return z;
	}
	
	protected boolean isNumeric(char ch) {
		if (Arrays.binarySearch(m_numberSet, ch) < 0) {
			return false;
		}
		return true;
	}
	
	private Complex parseComplex(String r) throws NumberFormatException 
	{
		Complex z = new Complex();
		if (r.charAt(r.length()-1) == 'i') {
			if (r.length()>1) {
				z.imaginary = Double.parseDouble(r.substring(0,r.length()-1));
			}
			else
				z.imaginary = 1;
		}
		else {
			z.real = Double.parseDouble(r);
		}
		return z;
	}
}
