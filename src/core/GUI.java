/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.*;



public class GUI
{
    public static final Square[] squareArray = new Square[40];
    private static final int TYPE_GO = 0, TYPE_SITE = 1, TYPE_STATION = 2, TYPE_UTILITY = 3, TYPE_COMMUNITY = 4, TYPE_CHANCE = 5, TYPE_JAIL = 6, TYPE_PARKING = 7, TYPE_GOTO_JAIL = 8, TYPE_TAX = 9;
    private static final int NO_COL = 0, COL_BROWN = 1, COL_LIGHT_BLUE = 2, COL_PINK = 3, COL_ORANGE = 4, COL_RED = 5, COL_YELLOW = 6, COL_GREEN = 7, COL_DARK_BLUE = 8;
    private static final int[]      SQUARE_COLOURS =        { NO_COL,                            COL_BROWN,                          NO_COL,                             COL_BROWN,                          NO_COL,                             NO_COL,                             COL_LIGHT_BLUE,                     NO_COL,                             COL_LIGHT_BLUE,                     COL_LIGHT_BLUE,                     NO_COL,                             COL_PINK,                           NO_COL,                             COL_PINK,                           COL_PINK,                           NO_COL,                             COL_ORANGE,                         NO_COL,                             COL_ORANGE,                         COL_ORANGE,                         NO_COL,                             COL_RED,                            NO_COL,                             COL_RED,                            COL_RED,                            NO_COL,                             COL_YELLOW,                         COL_YELLOW,                         NO_COL,                             COL_YELLOW,                         NO_COL,                             COL_GREEN,                          COL_GREEN,                          NO_COL,                             COL_GREEN,                          NO_COL,                             NO_COL,                             COL_DARK_BLUE,                      NO_COL,                             COL_DARK_BLUE                   };
    private static final int[]      SQUARE_TYPES =          { TYPE_GO,                           TYPE_SITE,                          TYPE_COMMUNITY,                     TYPE_SITE,                          TYPE_TAX,                           TYPE_STATION,                       TYPE_SITE,                          TYPE_CHANCE,                        TYPE_SITE,                          TYPE_SITE,                          TYPE_JAIL,                          TYPE_SITE,                          TYPE_UTILITY,                       TYPE_SITE,                          TYPE_SITE,                          TYPE_STATION,                       TYPE_SITE,                          TYPE_COMMUNITY,                     TYPE_SITE,                          TYPE_SITE,                          TYPE_PARKING,                       TYPE_SITE,                          TYPE_CHANCE,                        TYPE_SITE,                          TYPE_SITE,                          TYPE_STATION,                       TYPE_SITE,                          TYPE_SITE,                          TYPE_UTILITY,                       TYPE_SITE,                          TYPE_GOTO_JAIL,                     TYPE_SITE,                          TYPE_SITE,                          TYPE_COMMUNITY,                     TYPE_SITE,                          TYPE_STATION,                       TYPE_CHANCE,                        TYPE_SITE,                          TYPE_TAX,                           TYPE_SITE                       };
    private static final String[]   SQUARE_NAMES =          { "GO",                              "Old Kent Rd",                      "Community Chest",                  "Whitechapel Rd",                   "Income Tax",                       "King's Cross Station",             "The Angel Islington",              "Chance",                           "Euston Rd",                        "Pentonville Rd",                   "Jail",             "Pall Mall",                        "Electric Co",                      "Whitehall",                        "Northumberland Ave",               "Marylebone Station",               "Bow St",                           "Community Chest",                  "Marlborough St",                   "Vine St",                          "Free Parking",                     "Strand",                           "Chance",                           "Fleet St",                         "Trafalgar Sq",                     "Fenchurch St Station",             "Leicester Sq",                     "Coventry St",                      "Water Works",                      "Piccadilly",                       "Go To Jail",                       "Regent St",                        "Oxford St",                        "Community Chest",                  "Bond St",                          "Liverpool St Station",             "Chance",                           "Park Lane",                        "Super Tax",                        "Mayfair"                       };
    private static final String[]   SQUARE_SHORT_NAMES =    { "go",                              "kent",                             "community",                        "whitechapel",                      "tax",                              "kings",                            "angel",                            "chance",                           "euston",                           "pentonville",                      "jail",                             "mall",                             "electric",                         "whitehall",                        "northumberland",                   "marylebone",                       "bow",                              "community",                        "marlborough",                      "vine",                             "parking",                          "strand",                           "chance",                           "fleet",                            "trafalgar",                        "fenchurch",                        "leicester",                        "coventry",                         "water",                            "piccadilly",                       "go to jail",                       "regent",                           "oxford",                           "community",                        "bond",                             "liverpool",                        "chance",                           "park",                             "tax",                              "mayfair"                       };
    private static final int[]      SQUARE_PRICES =         { 0,                                 60,                                 0,                                  60,                                 0,                                  200,                                   100,                             0,                                  100,                                120,                                0,                                  140,                                150,                                140,                                160,                                200,                                180,                                0,                                  180,                                200,                                0,                                  220,                                0,                                  220,                                240,                                200,                                260,                                260,                                150,                                280,                                0,                                  300,                                300,                                0,                                  320,                                200,                                0,                                  350,                                0,                                  400                             };
    private static final int[][]    SQUARE_RENTS =          { {   0,   0,   0,   0,   0,   0},   {   2,  10,  30,  90, 160, 250},    {   0,   0,   0,   0,   0,   0},    {   4,  20,  60, 180, 320, 450},    {   0,   0,   0,   0,   0,   0},    {   6,  30,  90, 270, 400, 550},    {   6,  30,  90, 270, 400, 550},    {   0,   0,   0,   0,   0,   0},    {   8,  40, 100, 300, 450, 600},    {  10,  50, 150, 450, 625, 750},    {   0,   0,   0,   0,   0,   0},    {  10,  50, 150, 450, 625, 750},    {   0,   0,   0,   0,   0,   0},    {  12,  60, 180, 500, 700, 900},    {  14,  70, 200, 550, 750, 950},    {  25,  50, 100, 200, 200, 200},    {  14,  70, 200, 550, 750, 950},    {   0,   0,   0,   0,   0,   0},    {  16,  80, 220, 600, 800,1000},    {  18,  90, 250, 700, 875,1050},    {   0,   0,   0,   0,   0,   0},    {   4,  10,   0,   0,   0,   0},    {   0,   0,   0,   0,   0,   0},    {  18,  90, 250, 700, 875,1050},    {  25,  50, 100, 200, 200, 200},    {  20, 100, 300, 750, 925,1100},    {  25,  50, 100, 200, 200, 200},    {  22, 110, 330, 800, 975,1150},    {   0,   0,   0,   0,   0,   0},    {  22, 110, 330, 800, 975,1150},    {   0,   0,   0,   0,   0,   0},    {  22, 120, 360, 850,1025,1200},    {  26, 130, 390, 900,1100,1275},    {   0,   0,   0,   0,   0,   0},    {  26, 130, 390, 900,1100,1275},    {  28, 150, 450,1000,1200,1400},    {   0,   0,   0,   0,   0,   0},    {  25,  50, 100, 200, 200, 200},    {   0,   0,   0,   0,   0,   0},    {  35, 175, 500,1100,1300,1500} };
    private static final int[]      SQUARE_HOUSE_PRICE =    { 0,                                 50,                                 0,                                  50,                                 0,                                  0,                                  50,                                 0,                                  50,                                 50,                                 0,                                 100,                                 0,                                  100,                                100,                                0,                                  100,                                0,                                  100,                                100,                                0,                                  150,                                0,                                  150,                                150,                                0,                                  150,                                150,                                0,                                  150,                                0,                                  200,                                200,                                0,                                  200,                                0,                                  0,                                  200,                                0,                                  200                             };

    private JFrame mainFrame;
    private JPanel panel1, panel2;
    private OutputPanel outputPanel;
    private InputPanel inputPanel;
    private JLabel monopolyBoard;

    public GUI()
    {
        // Creating and setting JFrame
        mainFrame = new JFrame("BigMoney Monopoly Game");
        mainFrame.setLayout(new FlowLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        // Creating and setting JPanels
        panel1 = new JPanel();
        panel1.setBackground(new Color(204,227,199)); // Adding same background color (from board) to the rest of JPanel
        panel1.setPreferredSize(new Dimension(800,800));
        panel2 = new JPanel();
        panel2.setBackground(new Color(204,227,199));
        panel2.setPreferredSize(new Dimension(400,800));

        // Creating and setting control panels
        outputPanel = new OutputPanel();
        outputPanel.setPreferredSize(new Dimension(400, 400));
        inputPanel = new InputPanel();
        inputPanel.setPreferredSize(new Dimension(400, 50));

        // Importing Monopoly Board to a JLabel
        monopolyBoard = new JLabel();
        monopolyBoard.setIcon(new ImageIcon(getClass().getResource("/images/monopolyBoard.png")));
        monopolyBoard.setLayout(null);

        // Adding components to a window
        mainFrame.add(panel1);
        mainFrame.add(panel2);
        panel1.add(monopolyBoard);
        panel2.add(outputPanel);
        panel2.add(inputPanel);
        addSquares();

        mainFrame.pack(); // Sets the minimum size of the window in order to show all content.
        refresh();
    }

    // Refresh (repaints and validates the board and the frame)
    public void refresh ()
    {
        panel1.repaint();
        mainFrame.validate();
    }

    // Adds 40 squares by coordinates (40 JPanels) on the board
    public void addSquares()
    {
        for(int i=0; i<BigMoney.NUM_SQUARES; i++)
        {
            squareArray[i] = new Square(i, SQUARE_TYPES[i], SQUARE_NAMES[i], SQUARE_SHORT_NAMES[i], SQUARE_COLOURS[i], SQUARE_PRICES[i], SQUARE_RENTS[i], SQUARE_HOUSE_PRICE[i]);
            squareArray[i].setLayout(new FlowLayout());
            squareArray[i].setOpaque(false);
            monopolyBoard.add(squareArray[i]);
        }
        //South
        squareArray[0].setBounds(694,695,106,100); //Go square
        squareArray[1].setBounds(629,719,63,76);
        squareArray[2].setBounds(564,695,63,100);
        squareArray[3].setBounds(499,719,63,76);
        squareArray[4].setBounds(434,695,63,100);
        squareArray[5].setBounds(368,695,64,100);
        squareArray[6].setBounds(303,719,63,76);
        squareArray[7].setBounds(238,695,63,100);
        squareArray[8].setBounds(173,719,63,76);
        squareArray[9].setBounds(108,719,63,76);
        squareArray[10].setBounds(0,695,106,100); //Jail square
        //West
        squareArray[11].setBounds(0,630,82,63);
        squareArray[12].setBounds(0,564,106,64);
        squareArray[13].setBounds(0,499,82,63);
        squareArray[14].setBounds(0,434,106,63);
        squareArray[15].setBounds(0,368,106,63);
        squareArray[16].setBounds(0,302,82,63);
        squareArray[17].setBounds(0,237,106,63);
        squareArray[18].setBounds(0,172,82,63);
        squareArray[19].setBounds(0,106,82,63);
        //North
        squareArray[20].setBounds(0,0,106,104); // Parking square
        squareArray[21].setBounds(108,0,63,80);
        squareArray[22].setBounds(173,0,63,104);
        squareArray[23].setBounds(238,0,63,80);
        squareArray[24].setBounds(303,0,63,80);
        squareArray[25].setBounds(368,0,64,104);
        squareArray[26].setBounds(434,0,63,80);
        squareArray[27].setBounds(499,0,63,80);
        squareArray[28].setBounds(564,0,63,104);
        squareArray[29].setBounds(629,0,63,80);
        squareArray[30].setBounds(694,0,106,104); // GoToJail square
        //East
        squareArray[31].setBounds(718,106,82,63);
        squareArray[32].setBounds(718,172,82,63);
        squareArray[33].setBounds(694,237,106,63);
        squareArray[34].setBounds(718,302,82,63);
        squareArray[35].setBounds(694,368,106,63);
        squareArray[36].setBounds(694,434,106,63);
        squareArray[37].setBounds(718,499,82,63);
        squareArray[38].setBounds(694,564,106,64);
        squareArray[39].setBounds(718,630,82,63);
    }

    // Returns a single square from a square array
    public Square getSquare(int num)
    {
        return squareArray[num];
    }

    // Prints a string to the output panel (forwards the string to the addText method in OutputPanel)
    public void printToOutput (String string)
    {
        outputPanel.addText(string);
    }
    public void printToOutputNoNewLine (String string)
    {
        outputPanel.addTextNoNewLine(string);
    }
    // Resets the input box such that it becomes empty
    public void setEmpty()
    {
        inputPanel.setEmpty();
    }
    // Returns input panel JTextComponent
    public Component getInputPanelComponent()
    {
        return inputPanel.getInput();
    }
    // Returns input panel String that was typed in by player
    public String getInputPanelString() { return inputPanel.getInputString(); }
}
