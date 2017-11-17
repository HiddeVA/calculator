package calculator;

public class tester {

	public static void main(String[] args) {
		
		Expression tester;
		
		//standard calculator tests
		String[] inputs = {"1", "115", "1+2", "2*5", "15/3", "5-3", "-4", "7-22", "5/2", "1500*1600", "0.1+0.2", "7!", "3^3", "√20.25", "2㏒8", "6/", "/9", null};
		String[] outputs = {"1", "115", "3", "10", "5", "2", "-4", "-15", "2.5", "2400000", "0.3", "5040", "27", "4.5", "3", "6", "9", "0"};
		char[] numberSet = {46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 69};
		
		for (int i = 0; i < inputs.length; i++) {
			tester = new Expression(inputs[i], numberSet);
			tester.evaluate();
			String result = tester.getResult();
			if (result.equals(outputs[i])) {
				System.out.println("Test succeeded for " + inputs[i]);
			}
			else {
				System.out.println("Test failed for " + inputs[i] + " Expected " + outputs[i] + " got " + result);					
			}
		}
		
		//roman calculator tests
		inputs = new String[] {"I", "II+II", "CCCXVI/IV", "MDCCC/L", "M*IV", "V-X", "X-V"};
		outputs = new String[] {"I", "IV", "LXXIX", "XXXVI", "out of range", "out of range", "V"};
		numberSet = new char[] {'C', 'D', 'I', 'L', 'M', 'V', 'X'};
		
		for (int i = 0; i < inputs.length; i++) {
			tester = new Expression(inputs[i], numberSet);
			tester.evaluate();
			String result = convertToRoman((int)Double.parseDouble(tester.getResult()));
			if (result.equals(outputs[i])) {
				System.out.println("Test succeeded for " + inputs[i]);
			}
			else {
				System.out.println("Test failed for " + inputs[i] + " Expected " + outputs[i] + " got " + result);					
			}
		}
		
		//complex calculator tests
		
		inputs = new String[] {"√(-1)", "㏑e", "e^(π*i)", "√2*√i", "i*-i"};
		outputs = new String[] {"i", "1", "-1", "1+i", "1"};
		numberSet = new char[] {46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 69, 105};
		
		for (int i = 0; i < inputs.length; i++) {
			tester = new ComplexExpression(inputs[i], numberSet);
			tester.evaluate();
			String result = tester.getResult();
			if (result.equals(outputs[i])) {
				System.out.println("Test succeeded for " + inputs[i]);
			}
			else {
				System.out.println("Test failed for " + inputs[i] + " Expected " + outputs[i] + " got " + result);					
			}
		}
	}
	
	public static String convertToRoman(int x)
	{
		if (x >= 4000 || x < 0) return "out of range";
		if (x == 0) return "0";
		
		String[] romanNumbers = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
		
		String result = "";
		int reader = 0; int z = 0;
		while (x > 0) {
			while (x < romanValues[reader]) {
				reader++; z = 0; if (reader > 12) return "0";
			}
			result += romanNumbers[reader];
			x -= romanValues[reader];
			if (reader%4 > 0) {
				if (reader % 2 == 1) {
					reader = reader - reader % 4 + 5;
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
			if (reader > 12 && x > 0) return "0";
		}
		return result;
	}
	
	
}
