package calculator;

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Arrays;

public class Caclulator extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	TextField txtInput;
	Label lblAnswer;
	char decimalDelimiter = '.';
	public enum Style {STANDARD, ROMAN, LITERAL, COMPLEX}
	Style numberFormat = Style.STANDARD;
	char[] allowedCharSet = {33, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55,
			56, 57, 58, 67, 68, 69, 73, 76, 77, 86, 88, 91, 93, 94, 101, 105, 123, 125, 215,
			246, 960, 8730, 13265, 13266};
	//these are unicode characters. To find what they represent check out http://www.asciitable.com/ or https://unicode-table.com/
	char[] numberSet = {decimalDelimiter, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 69};
	Button btnDelim;
	
	@Override public void start(Stage stage) {
		//---------------------------------
		//Index:				Line:
		//#1 Basic Layout		36
		//#2 Basic Controls		55
		//#3 Button Inputs		73
		//#4 Settings Tab		111
		//#5 Final Statements	168
		//----------------------------------
		
		//#1 Basic Layout
		BorderPane border = new BorderPane();
		Button btnSettings = new Button("Settings");
		HBox settingsGrid = new HBox();
		btnSettings.setOnAction(e->{
			if (btnSettings.getText().equals("Settings")) {
				btnSettings.setText("Hide Settings");
				border.setBottom(settingsGrid);
			}
			else {
				btnSettings.setText("Settings");
				border.setBottom(null);
			}
		});
		
		border.setTop(new HBox(new Label("Have fun with this calculator     "), btnSettings));
		VBox mainContainer = new VBox();
		border.setCenter(mainContainer);
		
		//#2 Basic Controls
		txtInput = new TextField();
		txtInput.setPrefWidth(200);
		txtInput.setMaxWidth(250);
		HBox hboxCalculate = new HBox();
		Button btnCalculate = new Button("Calculate");
		btnCalculate.setOnAction(e->calculate());
		Button btnClear = new Button("Clear");
		btnClear.setOnAction(e->{txtInput.clear();lblAnswer.setText("");});
		Button btnUndo = new Button("Backspace");
		btnUndo.setOnAction(e->{txtInput.undo();txtInput.undo();});
		hboxCalculate.setSpacing(10);
		hboxCalculate.getChildren().addAll(btnCalculate, btnClear, btnUndo);
		hboxCalculate.setAlignment(Pos.CENTER);
		lblAnswer = new Label();
		lblAnswer.setPrefWidth(200);
		lblAnswer.setMaxWidth(300);
		
		//#3 Button Inputs
		HBox controls = new HBox();
		controls.setSpacing(50);
		controls.setAlignment(Pos.CENTER);
		GridPane numberPane = new GridPane();
		GridPane romanNumberPane = new GridPane();
		GridPane basicOperators = new GridPane();
		GridPane advancedOperators = new GridPane();
		controls.getChildren().addAll(numberPane, basicOperators);
		
		char[] numbers = {'1', '2', '3', '4', '5', '6', '7', '8', '9', decimalDelimiter, '0'};
		ButtonGroup numberButtons = new ButtonGroup(numbers, numberPane, txtInput);
		numberButtons.generateButtons();
		
		char[] romanNumbers = {'I', 'V', 'X', 'L', 'C', 'D', 'M'};
		ButtonGroup romanButtons = new ButtonGroup(romanNumbers, romanNumberPane, txtInput);
		romanButtons.setWidth(28);
		romanButtons.generateButtons();
		
		char[] operators = {'+', '-', '*', '/', '(', ')'};
		ButtonGroup operatorButtons = new ButtonGroup(operators, basicOperators, txtInput);
		operatorButtons.setRows(2);
		operatorButtons.generateButtons();
		
		char[] advOperators = {'!', '^', 'e', 'π', '√', '㏑', '㏒'};
		ButtonGroup advancedOperatorButtons = new ButtonGroup(advOperators, advancedOperators, txtInput);
		advancedOperatorButtons.setWidth(30);
		advancedOperatorButtons.setRows(2);
		advancedOperatorButtons.generateButtons();	
		Button btnExp = new Button("e^");
		btnExp.setPrefWidth(33);
		btnExp.setOnAction(e->{txtInput.appendText("e^(");});
		advancedOperatorButtons.addButton(btnExp);
		
		Button btnIi = new Button("i");
		btnIi.setPrefWidth(25);
		btnIi.setOnAction(e->{txtInput.appendText("i");});
		
		//#4 Settings Tab
		RadioButton rbtComma = new RadioButton();
		rbtComma.setOnAction(e->{refreshDelim(',');});
		RadioButton rbtDot = new RadioButton();
		rbtDot.setOnAction(e->{refreshDelim('.');});
		rbtDot.setSelected(true);
		ToggleGroup decimal = new ToggleGroup();
		decimal.getToggles().addAll(rbtComma, rbtDot);
		HBox hboxComma = new HBox(rbtComma, new Label(","));
		HBox hboxDot = new HBox(rbtDot, new Label("."));
		VBox decimalSettings = new VBox(new Label("Delimiter:"), hboxComma, hboxDot);
		
		RadioButton rbtRoman = new RadioButton();
		rbtRoman.setOnAction(e->{
			controls.getChildren().remove(numberPane);
			if (!(controls.getChildren().contains(romanNumberPane))) controls.getChildren().add(0, romanNumberPane);
			numberSet = new char[] {'C', 'D', 'I', 'L', 'M', 'V', 'X'};
			numberFormat = Style.ROMAN;
		});
		RadioButton rbtStandard = new RadioButton();
		rbtStandard.setOnAction(e->{
			controls.getChildren().remove(romanNumberPane);
			if (!(controls.getChildren().contains(numberPane))) controls.getChildren().add(0, numberPane);
			numberPane.getChildren().remove(btnIi);
			numberSet = new char[]{decimalDelimiter, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
			numberFormat = Style.STANDARD;
		});
		RadioButton rbtComplex = new RadioButton();
		rbtComplex.setOnAction(e->{
			controls.getChildren().remove(romanNumberPane);
			if (!(controls.getChildren().contains(numberPane))) controls.getChildren().add(0, numberPane);
			if (!(numberPane.getChildren().contains(btnIi))) numberPane.add(btnIi, 2, 3);
			numberSet = new char[] {decimalDelimiter, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 105};
			numberFormat = Style.COMPLEX;
		});
		rbtStandard.setSelected(true);
		ToggleGroup roman = new ToggleGroup();
		roman.getToggles().addAll(rbtRoman, rbtStandard, rbtComplex);
		HBox hboxRoman = new HBox(rbtRoman, new Label("Roman"));
		HBox hboxStandard = new HBox(rbtStandard, new Label("Standard"));
		HBox hboxComplex = new HBox(rbtComplex, new Label("Complex"));
		VBox romanSettings = new VBox(new Label("Number Format:"), hboxRoman, hboxStandard, hboxComplex);
		
		RadioButton rbtSimple = new RadioButton();
		rbtSimple.setOnAction(e->{controls.getChildren().remove(advancedOperators);});
		RadioButton rbtAdvanced = new RadioButton();
		rbtAdvanced.setOnAction(e->{controls.getChildren().add(advancedOperators);});
		rbtSimple.setSelected(true);
		ToggleGroup calculatorMode = new ToggleGroup();
		calculatorMode.getToggles().addAll(rbtSimple, rbtAdvanced);
		HBox hboxSimple = new HBox(rbtSimple, new Label("Standard"));
		HBox hboxAdvanced = new HBox(rbtAdvanced, new Label("Advanced"));
		VBox modeSettings = new VBox(new Label("Mode:"), hboxSimple, hboxAdvanced);
		
		settingsGrid.getChildren().addAll(decimalSettings, romanSettings, modeSettings);
		settingsGrid.setSpacing(30);
		
		//#5 Final Statements
		mainContainer.getChildren().addAll(txtInput, controls, hboxCalculate, lblAnswer);
		mainContainer.setAlignment(Pos.CENTER);
		mainContainer.setSpacing(10);
		
		stage.setScene(new Scene(border, 350, 350));
		stage.setTitle("Calculator");
		stage.show();
	}
	
	public void calculate()
	{
		Expression expression;
		String input = removeClutter(txtInput.getText());
		switch (numberFormat) {
		case STANDARD:
		case ROMAN:
			expression = new Expression(input, numberSet);
			break;
		case COMPLEX:
			expression = new ComplexExpression(input, numberSet);	
			break;
		default:
			expression = new Expression();
		}
		expression.evaluate();
		if (numberFormat == Style.ROMAN)
			lblAnswer.setText("Answer: "+ convertToRoman((int)Double.parseDouble(expression.getResult()))); //sorry
		else
			lblAnswer.setText("Answer: "+ expression.getResult());	
	}
	
	public String removeClutter(String str)
	{
		if (str.length() == 0) return "";
		for (int i=0; i < str.length(); i++) {
			if (isClutter(str.charAt(i)))
				return str.substring(0, i) + removeClutter(str.substring(i+1));
		}
		return str;
	}
	
	public boolean isClutter(char ch)
	{
		if (Arrays.binarySearch(allowedCharSet, ch) < 0) {
			return true;
		}
		return false;
	}
	
	public void refreshDelim(char delim)
	{
		decimalDelimiter = delim;
		btnDelim.setText(String.valueOf(delim));
		numberSet[0] = delim;
	}
	
	public String convertToRoman(int x)
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