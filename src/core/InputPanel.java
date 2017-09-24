/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import javax.swing.*;
import java.awt.*;

public class InputPanel extends JPanel
{
    private JPanel inputPanel;
    private JTextField input;

    InputPanel ()
    {
        inputPanel = new JPanel();
        input = new JTextField(30);

        add(inputPanel);
        inputPanel.add(input);
    }

    public Component getInput() { return input; }
    public String getInputString() { return input.getText(); }
    public void setEmpty() { input.setText(""); }
}
