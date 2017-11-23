package calculator;

import calculator.Calculator.Style;

public class Equation extends Expression 
{
	private Variable m_result;
	
	public Equation (String expression, char[] numberSet)
	{
		m_expression = expression;
		m_numberSet = numberSet;
		m_delimiter = numberSet[0];
		m_style = Style.EQUATION;
	}
	
	public void evaluate()
	{
		m_result = eqEvaluate(fixOrderOfOperations(checkBrackets(m_expression)));
	}
	
	public Variable eqEvaluate(String expression)
	{
		if (expression.length() == 0) return new Variable();
		
		Variable memory[] = new Variable[2];
		int pointer = 0;		
		char ascii;
		
		boolean operator = false;
		char operatortype = ' ';
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
					memory[pointer] = parseVariable(expression.substring(i,j).replace(m_delimiter, '.'));
				}
				catch (NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
				i = j - 1; //skip to the first non-numeric character to continue
				
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
						memory[pointer] = new Variable();
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
					case '*': case '/': case ':': case '÷': memory[pointer-1] = doMath(memory[pointer-1], new Variable(-1), '*');
					}
				}
			}
			else if (isOpeningBracket(ascii)) {//a bracket means evaluating a smaller expression first with some basic recursion
				int k = findClosingBracket(expression, i);
				try {
					memory[pointer] = eqEvaluate(expression.substring(i + 1, k)); //opening/closing brackets should be ignored
				}
				catch (StringIndexOutOfBoundsException stre) {
					System.out.println("Error trying to read: " + expression);
					break;
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
		}
		
		return memory[0];
	}
	
	protected Variable doMath(Variable x, Variable y, char ch)
	{
		Variable z;
		switch (ch) {
		case '+':
			z = new Variable(new double[Math.max(x.size(), y.size())]);
			for (int i = 0; i < z.size(); i++) {
				z.setP(i, x.getP(i) + y.getP(i));
			}
			break;
		case '-':
			z = new Variable(new double[Math.max(x.size(), y.size())]);
			for (int i = 0; i < z.size(); i++) {
				z.setP(i, x.getP(i) - y.getP(i));
			}
			break;
		case '*':
			if (x.size() + y.size() - 1 > 5) {
				throw new ArithmeticException("Calculation too complex");
			}
			else {
				double[] temparray = new double[x.size() + y.size() - 1];
				for (int i = 0; i < x.size(); i++) {
					for (int j = 0; j < y.size(); j++) {
						temparray[i+j] += x.getP(i) * y.getP(j);
					}
				}
				z = new Variable(temparray);
			}
			break;
		default:
			z = new Variable();
		}
		return z;
	}
	
	public Variable parseVariable(String r) throws NumberFormatException
	{
		if (r.length() == 0) return new Variable();
		int pos = r.indexOf("a");
		if (pos > 0) {
			if (pos == r.length() - 1) {
				return new Variable(new double[]{0, Double.parseDouble(r.substring(0,pos))});
			}
			else {
				int exponent = (int)(r.charAt(r.length()-1));
				if (exponent > 4 || exponent < 1) throw new NumberFormatException("Exponent out of range");
				double[] array = new double[exponent];
				array[exponent - 1] = Double.parseDouble(r.substring(0,pos-1));
				return new Variable(array);
			}
		}
		else if (pos == 0) {
			return new Variable(new double[] {0,1});
		}
		else return new Variable(Double.parseDouble(r));
	}
	
	public String getResult()
	{
		return m_result.toString();
	}
}
