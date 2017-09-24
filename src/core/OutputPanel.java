/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import javax.swing.*;
import java.awt.*;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class OutputPanel extends JPanel
{
    private JPanel outputPanel;
    private JTextArea output;
    private JScrollPane scroll;

    public OutputPanel()
    {
        outputPanel = new JPanel();
        output = new JTextArea(20,30);
        scroll = new JScrollPane(output);

        output.setEditable(false);
        output.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        output.setWrapStyleWord(true);
        output.setLineWrap(true);
        scroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        add(outputPanel);
        outputPanel.add(scroll);
    }

    // Adds text to output
    public void addText(String text)            { output.setText(output.getText() + "\n-" + text); }
    public void addTextNoNewLine(String text)   { output.setText(output.getText() + text); }
}
