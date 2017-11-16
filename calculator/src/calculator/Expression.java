package calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import calculator.Caclulator.Style;

public class Expression
{
	protected String m_expression;
	private double m_result;
	protected char[] m_numberSet;
	protected Style m_style;
	protected char m_delimiter;
	
	public Expression(String expression, char[] numberSet) {
		m_expression = expression;
		m_numberSet = numberSet;
		if (Arrays.binarySearch(numberSet, '1') >= 0) {
			m_style = Style.STANDARD;
			m_delimiter = numberSet[0];
		}
		else {
			m_style = Style.ROMAN;
		}
	}
	
	public Expression() {} //for testing purposes
	
	public String getResult()
	{
		if (Math.floor(m_result) == m_result) {
			if (m_result < Long.MAX_VALUE)
				return String.valueOf((long)m_result);
			else
				return String.valueOf(m_result);
			}		
		else {
			NumberFormat fmt = new DecimalFormat("#0.##########");
			return fmt.format(m_result);
		}
	}
	
	public String checkBrackets(String input)
	{
		String output = input;
		String closingBrackets = "";
		for (int i=0; i < output.length(); i++) {
			if (isOpeningBracket(output.charAt(i))) {
				if (findClosingBracket(output, i) == -1) {
					closingBrackets += ")";
				}
				if (i > 0) {
					if (isNumeric(output.charAt(i-1)) || isClosingBracket(output.charAt(i-1))) {
						output = output.substring(0, i) + "*" + output.substring(i);
						i++; //we just increased the string length
					}
				}
			}
		}
		return output + closingBrackets;
	}
	
	public void evaluate()
	{
		m_result = evaluate(checkBrackets(m_expression));
	}
	
	private double evaluate(String expression)
	{
		if (expression.length() == 0) return 0;
		
		double[] memory = new double[2];
		int pointer = 0;
		char operatortype = ' ';
		char ascii;
		
		boolean operator = false;
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
				switch (m_style) {
				case STANDARD:
					try {
						memory[pointer] = Double.parseDouble(expression.substring(i,j).replace(m_delimiter, '.'));
					}
					catch (NumberFormatException nfe) {
						System.out.println("Could not convert:" + expression.substring(i,j));
					}
					break;
				case ROMAN:
					memory[pointer] = convertToStandard(expression.substring(i,j));
					break;
				default:
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
						memory[pointer] = 0;
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
					case '-': operatortype = '+'; break;
					case '*': case '/': case ':': case '÷': memory[pointer-1] *= -1;
					}
				}
			}
			else if (isOpeningBracket(ascii)) {//a bracket means evaluating a smaller expression first with some basic recursion
				int k = findClosingBracket(expression, i);
				try {
					memory[pointer] = evaluate(expression.substring(i + 1, k)); //opening/closing brackets should be ignored
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
			else if (ascii == 33) {
				memory[pointer] = factorial((int)memory[pointer]);
			}
			else if (ascii == 101 || ascii == 960) {
				switch (ascii) {
				case 101: memory[pointer] = Math.E; break;
				case 960: memory[pointer] = Math.PI; break;
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
	
	private long factorial(int x) {
		long result = 1;
		for (int i = x; i > 1; i--) {
			result *= i;
		}
		return result;
	}
	
	private double doMath(double x, double y, char z) {
		switch (z) {
		case 43:
			x += y;
			break;
		case 45:
			x -= y;
			break;
		case 42:
			x *= y;
			break;
		case '÷':
		case 58:
		case 47:
			x = x / y;
			break;
		case 94:
			x = Math.pow(x, y);
			break;
		case 13266:
			x = Math.log(y)/Math.log(x);
			break;
		default:
		}
		return x;
	}
	
	private double doMath(double x, char z) {
		switch (z) {
		case 8730:
			x = Math.sqrt(x);
		case 13265:
			x = Math.log(x);
		default:
		}
		return x;
	}
	
	protected int findClosingBracket(String expression, int start) {
		int depth = 0;
		for (int x = start + 1; x < expression.length(); x++) {
			if (isClosingBracket(expression.charAt(x))) {
				if (depth == 0)
					return x;
				else
					depth--;
			}
			if (isOpeningBracket(expression.charAt(x)))
				depth++;
		}
		return -1;
	}
	
	protected boolean isNumeric(char ch)
	{
		return (Arrays.binarySearch(m_numberSet, ch) >= 0);
	}
	
	protected boolean isOperator(char ch)
	{
		return (Arrays.binarySearch(new char[] {'*', '+', '-' , '/', ':', '^', '×', '÷', '㏒'}, ch) >= 0);
	}
	
	protected boolean isSingularOperator(char ch)
	{
		return ch == '√' || ch == 13265;
	}
	
	protected boolean isOpeningBracket(char ch)
	{
		return ch == '(' || ch == '[' || ch == '{';
	}
	
	protected boolean isClosingBracket(char ch)
	{
		return ch == ')' || ch == ']' || ch == '}';
	}
	
	public int convertToStandard(String r) {
		int result = 0;
		int reader = 0;
		int z = 0;
		r += " ";
		String[] romanNumbers = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		
		for (int i = 0; i < r.length() - 1; i++) {
			while (!(r.substring(i, i + (reader % 2) + 1).equals(romanNumbers[reader]))) {
				reader++; z = 0; if (reader > 12) return 0;
			}
			result += romanValues[reader];
			if (reader%4 > 0) {
				if (reader % 2 == 1) {
					i++; reader = reader - reader % 4 + 5;
					if (reader > 12) reader = 12;
				}
				else {
					reader += 2;
				}
			}
			else {
				z++;
				if (z == 3) {
					z = 0; reader++;
				}
			}
			if (reader > 12 && i < r.length() - 1)
				return 0;
		}
		return result;
	}
}
