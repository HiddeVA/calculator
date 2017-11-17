package calculator;

import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.*;

public class ButtonGroup
{
	ClickHandler buttonClicked = new ClickHandler();
	
	private Button[] buttons;
	private char[] text;
	private double prefWidth = 25;
	private int cols = 3;
	private TextField outputField;
	private GridPane grid;
	private int size = 0;
	private Button placeHolder = new Button();
	
	public ButtonGroup(char[] text, GridPane pane, TextField output)
	{
		this.outputField = output;
		this.grid = pane;
		this.text = text;
		buttons = new Button[text.length];
	}
	
	public void addButton(Button btn, int column, int row)
	{
		if (btn.getOnAction() == null)
			btn.setOnAction(buttonClicked);
		grid.getChildren().remove(placeHolder);
		grid.add(btn, column, row);
	}
	
	public void addButton(Button btn)
	{
		if (btn.getOnAction() == null)
			btn.setOnAction(buttonClicked);
		grid.add(btn, size % cols, size / cols);
		size++;
	}
	
	public void setRows(int cols)
	{
		this.cols = cols;
	}
	
	public void setWidth(double width)
	{
		this.prefWidth = width;
	}
	
	public void generateButtons()
	{
		for (int i = size; i < buttons.length + size; i++) {
			if (text[i] == 'x') {
				buttons[i] = placeHolder;
			}
			else {
				buttons[i] = new Button(String.valueOf(text[i]));
				buttons[i].setOnAction(buttonClicked);
				buttons[i].setPrefWidth(prefWidth);
				grid.add(buttons[i], i % cols, i / cols);
			}
		}
		size += buttons.length;
	}
	
	
	private class ClickHandler implements EventHandler <ActionEvent>
	{
		@Override public void handle(ActionEvent e)
		{
			String str = e.getSource().toString();
			outputField.appendText(String.valueOf(str.charAt(str.length()-2)));					
		}
	}
}