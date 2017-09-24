/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class BigMoney implements KeyListener
{
    public static final int MAX_NUM_PLAYERS = 6, NUM_SQUARES = 40;
    private static final String[] tokenType = { "the Yin-Yang", "a coin", "a smiley face", "the Windows logo", "a cat", "a computer" };
    private static GUI gui;
    private Player[] player = new Player[MAX_NUM_PLAYERS];
    private int numOfPlayers, currentPlayerID;
    private Player currentPlayer; // Set in initialRoll, changes after "done" command
    private Square currentSquare; // Set in initialRoll, changes after "done" command and in movePlayer() method
    private Boolean gameOver = false;
    private Dice dice;
    private Cards cards;

    private BigMoney()
    {
        gui = new GUI();
        gui.getInputPanelComponent().addKeyListener(this);
        dice = new Dice();
        cards = new Cards();
    }

    public static void main(String args[])
    {
        BigMoney game = new BigMoney();
        game.askForNumOfPlayers();
        game.initialRoll();
    }

    // Asks for number of players and checks for invalid inputs
    private void askForNumOfPlayers()
    {
        String numOfPlayersSTR = JOptionPane.showInputDialog(null, "Enter the number of players: ");
        // Testing for invalid inputs
        if(numOfPlayersSTR == null || numOfPlayersSTR.equals("")) System.exit(0);
        try {this.numOfPlayers = Integer.parseInt(numOfPlayersSTR);}
        catch(NumberFormatException e) {JOptionPane.showMessageDialog(null, "Value must be an integer!"); System.exit(0);}
        if(this.numOfPlayers <= 1 || this.numOfPlayers > MAX_NUM_PLAYERS){ JOptionPane.showMessageDialog(null, "Invalid number of players. There must be a minimum of 2 or a maximum of 6 players."); System.exit(0); }
        // If input is valid, progress to creating players.
        createPlayers();
    }

    // Creates objects of Player class
    private void createPlayers()
    {
        int i;
        String name;
        for (i = 0; i < this.numOfPlayers; i++)
        {
            name = "";
            while(name.equals("")){name = JOptionPane.showInputDialog(null, "Enter the name of the player:").trim();}
            player[i] = new Player(name);
            player[i].setLayout(null);
            if (i==0) player[i].setIcon(new ImageIcon(getClass().getResource("/images/1.png")));
            else if (i==1) player[i].setIcon(new ImageIcon(getClass().getResource("/images/2.png")));
            else if (i==2) player[i].setIcon(new ImageIcon(getClass().getResource("/images/3.png")));
            else if (i==3) player[i].setIcon(new ImageIcon(getClass().getResource("/images/4.png")));
            else if (i==4) player[i].setIcon(new ImageIcon(getClass().getResource("/images/5.png")));
            else if (i==5) player[i].setIcon(new ImageIcon(getClass().getResource("/images/6.png")));
            GUI.squareArray[player[i].getCurrentPosition()].add(player[i]); // Places each player on "GO" square
            gui.printToOutput("Player " + (i+1) + ": called " + name + " is created. It's token is " + tokenType[i] + ".");
            gui.printToOutput(name + " has $" + player[i].getMoney() + " in its account.");
            gui.refresh();
        }
    }

    // Rolls dice for players to determine who plays first
    private int initialRoll()
    {
        int firstPlayer = 0;
        int tempValue1 = 0, tempValue2 = 0;
        for(int i = 0; i < numOfPlayers; i++)
        {
            tempValue1 = dice.roll() + dice.roll();
            while(tempValue1 == tempValue2)//does not allow for two players to have the highest roll value
            {
                tempValue1 = dice.roll() + dice.roll();//repeats roll for player if value matches highest
            }
            if (tempValue1 > tempValue2)//if player rolls value higher than previous highest its overwritten
            {
                tempValue2 = tempValue1;
                firstPlayer = i;
            }
            gui.printToOutput(player[i].getName() + " rolled a " + tempValue1 + ".");
        }
        gui.printToOutput("It is " + player[firstPlayer].getName() +"'s turn. Type \"help\" to see the list of available commands.");
        currentPlayerID = firstPlayer;
        currentPlayer = player[currentPlayerID];
        currentSquare = GUI.squareArray[player[currentPlayerID].getCurrentPosition()];
        return firstPlayer;
    }

    // Listens for command inputs from each player
    public void commandListener()
    {
        String command = gui.getInputPanelString().trim();
        if (command.equals("roll") && !gameOver)
        {
            gui.printToOutput("roll"); //echo

            if (currentPlayer.getMoney() <=0 && !currentPlayer.getBankruptCommand())
            {
                gui.printToOutput("Your balance is negative please mortgage your properties or declare a bancruptcy ");
            }
            else if(currentPlayer.getRequiredToPayTaxCommand())
            {
                gui.printToOutput("You have not paid your taxes yet!");
            }
            else if(currentPlayer.checkJail() > 0 && currentPlayer.getRollCommand())
            {
                currentPlayer.roll();
                currentPlayer.setRollCommand(false);
                currentPlayer.setPayCommand(false);
                currentPlayer.setCardCommand(false);
                currentPlayer.setRerollCommand(false);
                currentPlayer.setRollCount(0);
                if(currentPlayer.getDice1() == currentPlayer.getDice2())
                {
                    gui.printToOutput(currentPlayer.getName() + " has rolled doubles and is free from jail.");
                    currentPlayer.setJail(0);
                    movePlayer(currentPlayer, currentPlayer.getTotalDice());
                }
                else if(currentPlayer.checkJail() == 1)
                {
                    gui.printToOutput(currentPlayer.getName() + " has not rolled doubles in three turns and must pay the fine.");
                    currentPlayer.setJail(0);
                    currentPlayer.debit(50);
                    movePlayer(currentPlayer, currentPlayer.getTotalDice());
                }
                else
                {
                    currentPlayer.setJail(currentPlayer.checkJail()-1);
                    gui.printToOutput(currentPlayer.getName() + " has not rolled doubles and is in jail for " + currentPlayer.checkJail() + " turns");
                }
            }
            else if ( (currentPlayer.getRollCommand() || currentPlayer.getRerollCommand() )
                    && currentPlayer.getRollCount() < 3
                    && !currentPlayer.getPayRentCommand()
                    && !currentPlayer.getBankruptCommand()) // (Roll = TRUE or ReRoll = TRUE) and RollCount < 3 and PayRent = FALSE and currentPlayer is NOT bankrupt, player can roll a dice
            {
                currentPlayer.roll();
                movePlayer(currentPlayer, currentPlayer.getTotalDice());
                if(currentPlayer.getRerollCommand())
                {
                    gui.printToOutput(currentPlayer.getName() + " has rolled doubles and may roll again.");
                }
            }
            else // (Roll = FALSE and Reroll = FALSE) or RollCount >= 3 or PayRent = TRUE, player can't roll a dice
            {
                if (currentPlayer.getPayRentCommand()) gui.printToOutput("You have to pay rent before you may roll again.");
                else gui.printToOutput("You can not roll any more.");
            }

            if(currentPlayer.getRollCount() == 3)
            {
                currentPlayer.goToJail(this);
                gui.printToOutput("You have rolled 3 doubles in a row. The townspeople believe you're a witch and send you to jail.");
            }
            currentPlayer.checkDoneCommand();
        }

        else if (command.equals("gtj") && !gameOver)
        {
            currentPlayer.goToJail(this);
        }

        else if (command.equals("card") && !gameOver)
        {
            if(currentPlayer.getCardCommand())
            {
                currentPlayer.setJail(0);
                currentPlayer.setPayCommand(false);
                currentPlayer.setCardCommand(false);
                currentPlayer.removeGOOJcard();
                gui.printToOutput("You have used a get out of jail card and are free.");
            }
            else
                gui.printToOutput("You cant currently use this command.");
        }

        else if (command.equals("pay") && !gameOver)
        {
            if(currentPlayer.getPayCommand())
            {
                currentPlayer.setJail(0);
                currentPlayer.setPayCommand(false);
                currentPlayer.setCardCommand(false);
                gui.printToOutput("You have paid the $50 fine and are free.");
            }
            else
                gui.printToOutput("You cant currently use this command.");
        }

        else if (command.equals("mov") && !gameOver)
        {
            gui.printToOutput("mov"); //echo
            movePlayer(currentPlayer, 1);
        }

        else if (command.equals("buy") && !gameOver)
        {
            gui.printToOutput("buy"); //echo
            if ( currentPlayer.getBuyCommand() // Buy = TRUE and player has enough money and square is buyable
                    && currentPlayer.getMoney() >= GUI.squareArray[currentPlayer.getCurrentPosition()].getPrice()
                    && currentSquare.getBuyable() )
            {
                currentPlayer.credit(currentSquare.getPrice()); // Pay for the property
                currentPlayer.addProperty(currentSquare, currentPlayer.getCurrentPosition()); // Adds property to player's list
                currentSquare.setBuyable(false);                             // Property can't be purchased again
                gui.printToOutput("You just bought " + currentSquare.getFullName() + ".");

                // Makes sure player can't purchase the property again nor pay the rent and allow them to mortgage
                currentPlayer.setBuyCommand(false);
                currentPlayer.setPayRentCommand(false);
                currentPlayer.setMortgageCommand(true);
            }
            else // Buy = FALSE, player can't buy a square
            {
                gui.printToOutput("You can't buy " + currentSquare.getFullName() + ".");
            }
        }

        else if (command.equals("pay rent") && !gameOver)
        {
            gui.printToOutput("pay rent"); //echo

            if (currentPlayer.getPayRentCommand() && !currentSquare.getBuyable() && currentSquare.getOwnedBy() != null) // PayRent = TRUE and square is not buyable and the owner exists, player has to pay a rent
            {
                if(calculateRent() > currentPlayer.getMoney())
                {
                    gui.printToOutput("Player " + currentPlayer.getName() + " has insufficient funds to pay rent. Please mortgage your properties or declare a bankruptcy.");
                }
                else
                {
                    currentPlayer.payTo(currentSquare.getOwnedBy(), calculateRent());
                    gui.printToOutput(currentPlayer.getName() + " has paid rent of " + calculateRent() + " to " + currentSquare.getOwnedBy().getName() + ".");
                    currentPlayer.setPayRentCommand(false);
                    currentPlayer.checkDoneCommand();
                }
            }
            else // PayRent = FALSE, player doesn't need to pay rent here
            {
                gui.printToOutput("You should not pay any rent on " + currentSquare.getFullName() + ".");
            }
        }
        // Testing command, that speeds up development. The command reduces currentPlayer's balance (money)
        else if (command.equals("admin minus balance") && !gameOver) // amb stand for "admin minus balance"
        {
            gui.printToOutput("admin minus balance"); //echo
            currentPlayer.setMinusBalance();
            gui.printToOutput("ADMIN Command \"admin minus balance\" accepted the balance of current player will be deducted ");
        }
        else if(command.equals("tax 200") && !gameOver && currentPlayer.getRequiredToPayTaxCommand())
        {
            if(currentPlayer.payTax200())
            {
                gui.printToOutput("Your Tax Payment of 200 was successful");
                currentPlayer.setRequiredToPayTaxCommand(false);
            }
            else gui.printToOutput("Your Tax Payment was NOT successful, please check your balance if you have sufficient funds");



        }
        else if(command.equals("tax 10") && !gameOver && currentPlayer.getRequiredToPayTaxCommand())
        {
            if(currentPlayer.payTax10Percent())
            {
                gui.printToOutput("Your Tax Payment of 10% of your assets was successful");
                currentPlayer.setRequiredToPayTaxCommand(false);
            }
            else gui.printToOutput("Your Tax Payment was NOT successful, please check your balance if you have sufficient funds");
        }


        else if (command.equals("property"))
        {
            gui.printToOutput("property"); //echo
            if(currentPlayer.getPropertiesToString() != null)
            {
                gui.printToOutput("You own the following properties: ");
                gui.printToOutputNoNewLine(currentPlayer.getPropertiesToString());
            }
            else
                gui.printToOutput("You have no properties assigned to your name.");

        }

        else if (command.matches("build <(.*)> <(.*)>"))
        {
            gui.printToOutput("build"); //echo
            String[] argument = command.split("\\s<"); // White space followed by "<"; This is important as some property names may be consisted of multiple words, so we don't want to split by whitespace only
            argument[1] = argument[1].substring(0, argument[1].length()-1); // Property name (substring removes ">" at the end)
            argument[2] = argument[2].substring(0, argument[2].length()-1); // Number of houses to build (substring removes ">" at the end)

            // Test if square name is valid and store square ID.
            int squareID;
            for(squareID = 0; squareID < NUM_SQUARES; squareID++) // Loop until matching name is found; break if found, otherwise squareID will finish with a value of 40
                if(argument[1].equals(GUI.squareArray[squareID].getFullName()) || argument[1].equals(GUI.squareArray[squareID].getShortName()))
                    break;

            if(squareID < NUM_SQUARES) // If name is found squareArray (if found, squareID should be less then NUM_SQUARES (40))
            {
                if(GUI.squareArray[squareID].getType() == 1) // If square type is a site (houses can be build on sites only)
                {
                    if(GUI.squareArray[squareID].getOwnedBy() == currentPlayer) // If current player owns the site
                    {
                        if(currentPlayer.getOwnsColourGroup(currentPlayer.getColorGroupForSquareID(squareID))) // If current player owns whole colour group
                        {
                            gui.printToOutput(currentPlayer.build(GUI.squareArray[squareID], argument[2]));
                        }
                        else gui.printToOutput("You must first own whole colour group in order build houses or hotels.");
                    }
                    else gui.printToOutput("You are not the owner of this property.");
                }
                else gui.printToOutput("This square is not a site where houses can be built.");
            }
            else gui.printToOutput("Name of the property is invalid. Please use format \"build <property name> <number of houses>\".");
        }

        else if (command.matches("demolish <(.*)> <(.*)>"))
        {
            gui.printToOutput("demolish"); //echo
            String[] argument = command.split("\\s<"); // White space followed by "<"; This is important as some property names may be consisted of multiple words, so we don't want to split by whitespace only
            argument[1] = argument[1].substring(0, argument[1].length()-1); // Property name (substring removes ">" at the end)
            argument[2] = argument[2].substring(0, argument[2].length()-1); // Number of houses to build (substring removes ">" at the end)

            // Test if square name is valid and store square ID.
            int squareID;
            for(squareID = 0; squareID < NUM_SQUARES; squareID++) // Loop until matching name is found; break if found, otherwise squareID will finish with a value of 40
                if(argument[1].equals(GUI.squareArray[squareID].getFullName()) || argument[1].equals(GUI.squareArray[squareID].getShortName()))
                    break;

            if(squareID < NUM_SQUARES)
            {
                if(GUI.squareArray[squareID].getType() == 1) // If square type is a site (houses can be build on sites only)
                {
                    if(GUI.squareArray[squareID].getOwnedBy() == currentPlayer) // If current player owns the site
                    {
                        if(GUI.squareArray[squareID].getHouseNum() != 0 || GUI.squareArray[squareID].getHotelNum() != 0)
                        {
                            gui.printToOutput(currentPlayer.demolish(GUI.squareArray[squareID], argument[2]));
                        }
                        else gui.printToOutput("There are no houses, nor hotels on this property to be demolished.");
                    }
                    else gui.printToOutput("You are not the owner of this property.");
                }
                else gui.printToOutput("This square is not a site where houses can exist.");
            }
            else gui.printToOutput("Name of the property is invalid. Please use format \"demolish <property name> <number of houses>\".");
        }

        else if (command.equals("balance"))
        {
            gui.printToOutput("balance"); //echo
            gui.printToOutput("Your current balance is: $" + currentPlayer.getMoney());
        }

        else if (command.matches("mortgage <(.*)>")) {
            gui.printToOutput("mortgage"); //echo
            if (currentPlayer.getMortgageCommand())
            {
                String[] argument = command.split("\\s<");
                argument[1] = argument[1].substring(0, argument[1].length() - 1);

                int squareID;
                for (squareID = 0; squareID < NUM_SQUARES; squareID++) // Loop until matching name is found; break if found, otherwise squareID will finish with a value of 40
                    if (argument[1].equals(GUI.squareArray[squareID].getFullName()) || argument[1].equals(GUI.squareArray[squareID].getShortName()))
                        break;

                if (squareID < NUM_SQUARES) // If name is found squareArray (if found, squareID should be less then NUM_SQUARES (40))
                {
                    if (GUI.squareArray[squareID].getType() == 1 || GUI.squareArray[squareID].getType() == 2 || GUI.squareArray[squareID].getType() == 3) // If square can be mortgaged
                    {
                        if (GUI.squareArray[squareID].getOwnedBy() == currentPlayer) // If current player owns the site
                        {
                            if (!GUI.squareArray[squareID].isMortgaged())
                            {
                                GUI.squareArray[squareID].setMortgaged(true);
                                currentPlayer.debit(GUI.squareArray[squareID].getPrice() / 2);
                                if (GUI.squareArray[squareID].getHotelNum() == 1)
                                    gui.printToOutput(currentPlayer.demolish(GUI.squareArray[squareID], String.valueOf(5)));
                                else if (GUI.squareArray[squareID].getHouseNum() >= 1)
                                    gui.printToOutput(currentPlayer.demolish(GUI.squareArray[squareID], String.valueOf(GUI.squareArray[squareID].getHouseNum())));
                                else {}
                                gui.printToOutput("This property has been mortgaged and you have been debited: $" + GUI.squareArray[squareID].getPrice() / 2);
                                currentPlayer.setRedeemCommand(true);
                                currentPlayer.checkMortgageCommand();
                            }
                            else gui.printToOutput("This property is already mortgaged");
                        }
                        else gui.printToOutput("You are not the owner of this property.");
                    }
                    else gui.printToOutput("This square is not a site that can be mortgaged.");
                }
                else gui.printToOutput("Name of the property is invalid. Please use format \"mortgage <property name>\".");
            }
            else gui.printToOutput("You currently have no properties you can mortgage.");
        }

        else if (command.matches("redeem <(.*)>"))
        {
            gui.printToOutput("redeem"); //echo
            if (currentPlayer.getRedeemCommand())
            {
                String[] argument = command.split("\\s<");
                argument[1] = argument[1].substring(0, argument[1].length() - 1);

                int squareID;
                for (squareID = 0; squareID < NUM_SQUARES; squareID++) // Loop until matching name is found; break if found, otherwise squareID will finish with a value of 40
                    if (argument[1].equals(GUI.squareArray[squareID].getFullName()) || argument[1].equals(GUI.squareArray[squareID].getShortName()))
                        break;

                if (squareID < NUM_SQUARES) // If name is found squareArray (if found, squareID should be less then NUM_SQUARES (40))
                {
                    if (GUI.squareArray[squareID].getType() == 1 || GUI.squareArray[squareID].getType() == 2 || GUI.squareArray[squareID].getType() == 3) // If square can be redeemed
                    {
                        if (GUI.squareArray[squareID].getOwnedBy() == currentPlayer) // If current player owns the site
                        {
                            if (GUI.squareArray[squareID].isMortgaged())
                            {
                                if(currentPlayer.getMoney() >= GUI.squareArray[squareID].getPrice())//they need to have the funds to buy the property
                                {
                                    GUI.squareArray[squareID].setMortgaged(false);
                                    currentPlayer.credit(GUI.squareArray[squareID].getPrice() + GUI.squareArray[squareID].getPrice() / 10);
                                    gui.printToOutput("This property has been redeemed and you have been credited: $" + (GUI.squareArray[squareID].getPrice() + GUI.squareArray[squareID].getPrice() / 10));
                                    currentPlayer.setMortgageCommand(true);
                                    currentPlayer.checkRedeemCommand();
                                }
                                else gui.printToOutput("You have insufficient funds to redeem that property.");
                            }
                            else gui.printToOutput("This property is not mortgaged.");
                        }
                        else gui.printToOutput("You are not the owner of this property.");
                    }
                    else gui.printToOutput("This square is not a site that can be redeemed.");
                }
                else gui.printToOutput("Name of the property is invalid. Please use format \"redeem <property name>\".");
            }
            else gui.printToOutput("You currently have no properties you can redeem.");
        }

        else if (command.equals("help") && !gameOver)
        {
            gui.printToOutput("help"); //echo
            gui.printToOutput("The list of currently available commands to you are: ");
            if (currentPlayer.getRollCommand()) gui.printToOutputNoNewLine("roll, ");
            if (currentPlayer.getBuyCommand()) gui.printToOutputNoNewLine("buy, ");
            if (currentPlayer.getPayRentCommand()) gui.printToOutputNoNewLine("pay rent, ");
            if (currentPlayer.getMortgageCommand()) gui.printToOutputNoNewLine("mortgage, ");
            if (currentPlayer.getRedeemCommand()) gui.printToOutputNoNewLine("redeem, ");
            if (currentPlayer.getRequiredToPayTaxCommand()) gui.printToOutputNoNewLine("tax 200, tax 10, ");
            if (currentPlayer.getCardCommand()) gui.printToOutputNoNewLine("card, ");
            if (currentPlayer.getPayCommand()) gui.printToOutputNoNewLine("pay, ");
            gui.printToOutputNoNewLine("build, demolish, property, balance, help, help all, ");
            if (currentPlayer.getDoneCommand()) gui.printToOutputNoNewLine("done, ");
            gui.printToOutputNoNewLine(" bankrupt and quit.");
        }

        else if (command.equals("help all"))
        {
            gui.printToOutput("help all"); //echo
            gui.printToOutput("List of all commands: \"roll\", \"buy\", \"pay rent\", \"property\", \"card\", \"pay\", \"build\", \"demolish\", \"mortgage\", \"redeem\",  \"balance\", \"bankrupt\", \"help\", \"help all\", \"done\", \"quit\". To get the list of currently available commands type \"help\".");
        }
        else if (command.equals("done") && !gameOver)
        {
            gui.printToOutput("done"); //echo

            if (currentPlayer.getMoney() <=0 && !currentPlayer.getBankruptCommand())
            {
                gui.printToOutput("Your balance is negative please mortgage your properties or declare a bancruptcy ");
            }
            else if(currentPlayer.getRequiredToPayTaxCommand())
            {
                gui.printToOutput("You have not paid your taxes yet!");
            }
            else if (!currentPlayer.getDoneCommand()) // Done = FALSE, player can't finish the turn yet
            {
                gui.printToOutput("You can't finish the turn yet.");
                if (currentPlayer.getRollCommand())
                    gui.printToOutputNoNewLine(" You have to roll the dice first. Type \"roll\".");
                else if (currentPlayer.getPayRentCommand())
                    gui.printToOutputNoNewLine(" You have to pay rent first. Type \"pay rent\".");
            }
            else // Done = TRUE, player can finish the turn
            {
                gui.printToOutput(currentPlayer.getName() + " has finished his/her turn.");
                checkIfBankrupt(); // Before we switch to next player
                do // Do while loop switches through bankrupt players, as they can't play anymore
                {
                    this.currentPlayerID++; // Next player
                    if (this.currentPlayerID >= this.numOfPlayers) this.currentPlayerID %= this.numOfPlayers;
                    this.currentPlayer = player[currentPlayerID]; // updates currentPlayer to the next player
                } while (this.currentPlayer.getBankruptCommand());
                currentPlayer = player[this.currentPlayerID];
                currentSquare = GUI.squareArray[currentPlayer.getCurrentPosition()];
                currentPlayer.checkMortgageCommand();
                currentPlayer.checkRedeemCommand();
                if(currentPlayer.checkJail() > 1)
                {
                    if(currentPlayer.getGOOJcards() > 0)
                        currentPlayer.setCardCommand(true);
                    currentPlayer.setPayCommand(true);
                }
                if(currentPlayer.checkJail() == 1)
                {
                    if(currentPlayer.getGOOJcards() > 0)
                        currentPlayer.setCardCommand(true);
                }
                if(!gameOver)
                {
                    gui.printToOutput("It is " + currentPlayer.getName() + "'s turn. Type \"help\" to see the list of available commands.");
                    currentPlayer.resetCommands(); // Makes sure that commands reset at the beginning of new turn
                }
                else
                {
                    gui.printToOutput("The game is over. Type \"exit\" to close the game.");
                    currentPlayer.setDoneCommand(true);
                    currentPlayer.setRollCommand(false);
                }
            }
        }

        else if (command.equals("quit") && !gameOver)
        {
            gui.printToOutput("quit"); //echo

            // Calculate asset and determine a winner. Finish the game
            int[] playerAssets = new int[this.numOfPlayers];
            int max = -1, winner = -1;
            for (int i = 0; i < this.numOfPlayers; i++)
            {
                gui.printToOutput(player[i].getName() + "'s assets: " + player[i].calculateTotalAssets());
                playerAssets[i] = player[i].getTotalAssets();
                gui.printToOutput("Total assets: $" + playerAssets[i]);
                if (playerAssets[i] > max)
                {
                    max = playerAssets[i];
                    winner = i;
                }
            }
            int winnerAssets = playerAssets[winner];
            for (int i = 0; i < this.numOfPlayers; i++)
            {
                if ((playerAssets[i] == winnerAssets) && (i != winner)) winner = -1;
            }
            if (winner == -1) gui.printToOutput("Unfortunately there is no winner.");
            else gui.printToOutput("The winner is " + player[winner].getName() + ". Congratulations!");
        }

        else if (command.equals("bankrupt") && !gameOver)
        {
            gui.printToOutput("bankrupt"); //echo

            currentPlayer.returnOwnedPropertiesToBank();
            gui.printToOutput("Player " + currentPlayer.getName() + " has declared himself/herself bankrupt! Thank you for playing.");
            //This ensures that the person is bankrupt once he chooses to do so at the same time allowing the person to leave the game at any time without terminating the game for the rest of players
            currentPlayer.setMinusBalance();
            currentPlayer.setBankruptCommand(true);

            gui.printToOutput(currentPlayer.getName() + " has finished his/her turn.");
            checkIfBankrupt(); // Before we switch to next player

            do // Do while loop switches through bankrupt players, as they can't play anymore
            {
                this.currentPlayerID++; // Next player

                if (this.currentPlayerID >= this.numOfPlayers) this.currentPlayerID %= this.numOfPlayers;
                this.currentPlayer = player[currentPlayerID];
            } while (this.currentPlayer.getBankruptCommand());
            currentSquare = GUI.squareArray[currentPlayer.getCurrentPosition()];
            currentPlayer.checkMortgageCommand();
            currentPlayer.checkRedeemCommand();

            //checkForWinner();


            if(!gameOver)
            {

                gui.printToOutput("It is " + currentPlayer.getName() + "'s turn. Type \"help\" to see the list of available commands.");
                currentPlayer.resetCommands(); // Makes sure that commands reset at the beginning of new turn
            }
            else
            {
                gui.printToOutput("The game is over. Type \"exit\" to close the game.");
                currentPlayer.setDoneCommand(true);
                currentPlayer.setRollCommand(false);
            }



        }

        else if (command.equals("exit") && gameOver)
        {
            gui.printToOutput("exit"); //echo
            System.exit(0);
        }

        // Community 16 card helper (gives the option to pay 10 or to take a Chance card
        else if (cards.getCommunity16helper() && (command.equals("pay10") || command.equals("take chance")))
        {
            if(command.equals("pay10")) currentPlayer.credit(10);
            else cards.drawChanceCard_Community16Helper(currentPlayer, this);

            cards.setCommunity16helper(false); // Resets the community16helper boolean
        }

        else
        {
            if(!gameOver) gui.printToOutput("Unknown command received.");
        }
    }

    public void movePlayer(Player player, int numOfSquares)
    {
        for(int i = 0; i < numOfSquares; i++) // Loop to move player along the board
        {
            player.move();
            if (player.getCurrentPosition() == 0) // If the player lands on go after a roll they will be debited 200
            {
                player.debit(200);
                gui.printToOutput(player.getName() + " has passed go and $200 has been added to its balance.");
            }
            // Idea: add a delay to token moves?
        }

        GUI.squareArray[player.getCurrentPosition()].add(player); // Moves player to its current position square
        currentSquare = GUI.squareArray[currentPlayer.getCurrentPosition()]; // Set currentSquare to a new square where a player landed
        gui.refresh();
        gui.printToOutput(player.getName() + " has moved by " + numOfSquares + " squares to " + currentSquare.getFullName() + ".");
        squareInfo(); // Prints info about square.

        checkAfterMove(player);
    }

    public void movePlayerManually(Player player, int squareNum)
    {
        player.moveManually(squareNum);

        GUI.squareArray[player.getCurrentPosition()].add(player); // Moves player to its current position square
        currentSquare = GUI.squareArray[currentPlayer.getCurrentPosition()]; // Set currentSquare to a new square where a player landed
        gui.refresh();
        gui.printToOutput(player.getName() + " has moved to " + currentSquare.getFullName() + ".");
        squareInfo(); // Prints info about square.

        checkAfterMove(player);
    }

    private void checkAfterMove(Player player)
    {
        // Checking if someone owns a square, and therefore let's the player buy it or pay rent
        if(currentSquare.getBuyable())
            player.setBuyCommand(true);
        else if(!currentSquare.getBuyable() && currentSquare.getOwnedBy() == null)
        {
            gui.printToOutput("You stepped on " + currentSquare.getTypeString());
            if(currentSquare.getType() == 4 || currentSquare.getType() == 5)
            {
                cards.drawNewCard(currentSquare, currentPlayer, this);
            }
            else if(currentSquare.getType() == 9) // Player steps on Tax Square
            {
                gui.printToOutput("You MUST pay taxes. You have two options: 1st: pay EUR 200 by entering \"tax 200\" or pay 10% of your assets in chash by entering \"tax 10\"" );
                currentPlayer.setRequiredToPayTaxCommand(true);
                System.out.println(currentPlayer.getRequiredToPayTaxCommand());

            }
            else if(currentSquare.getType() == 8) // Player steps on GoToJail square
            {
                currentPlayer.goToJail(this);
            }
            else
            {
                // Gets in here if player landed on Go, Parking, Jail or GoToJail
            }
        }
        else if (!(currentSquare.getBuyable() && currentSquare.getOwnedBy() == currentPlayer) && currentSquare.isMortgaged())
        {
            gui.printToOutput("This property is owned by " + currentSquare.getOwnedBy().getName() + ", but is currently mortgaged so you don't have to pay rent.");
        }
        else if (!currentSquare.getBuyable() && currentSquare.getOwnedBy() != currentPlayer)
        {
            player.setPayRentCommand(true);
            player.setDoneCommand(false);
            gui.printToOutput("This property is owned by " + currentSquare.getOwnedBy().getName() + ", therefore, you have to pay rent of " + calculateRent());
        }
        else if (currentSquare.getOwnedBy() == currentPlayer)
        {
            gui.printToOutput("You already own this property.");
        }
        else
            System.out.println("Error in checkAfterMove().");
    }

    public void squareInfo()
    {
        gui.printToOutput("Square name: " + currentSquare.getFullName());
        gui.printToOutput("Square type: " + currentSquare.getTypeString());
        if (currentSquare.getBuyable() && currentSquare.getOwnedBy() == null) // Checks if the square is available to purchase
            gui.printToOutput("Square available for purchase: yes, its price is " + currentSquare.getPrice());
        else if (currentSquare.getBuyable() && currentSquare.getOwnedBy() != null)
            gui.printToOutput("Square available for purchase: no, current owner is " + currentSquare.getOwnedBy().getName());
        else{} // Case where player steps on not buyable square, such as chance, community, jail, etc.
    }

    public void checkIfBankrupt()
    {
        if(currentPlayer.getMoney() <= 0)
        {
            currentPlayer.setBankruptCommand(true);
            gui.printToOutput(currentPlayer.getName() + " is bankrupt! Sorry, you lost.");

            currentPlayer.setDoneCommand(true);

            // If Player Bankrupt == TRUE then The properties are returned to the bank and put back on the market.
            currentPlayer.returnOwnedPropertiesToBank();

            checkForWinner();
        }
    }

    public void checkForWinner()
    {
        int numberOfBankruptPlayers = 0, winner = -1;
        for(int i = 0; i < this.numOfPlayers; i++)
        {
            if(player[i].getBankruptCommand()) numberOfBankruptPlayers++;
            else winner = i; // Bankrupt = FALSE, make that player a winner
        }
        if(numberOfBankruptPlayers == this.numOfPlayers - 1) // Only enter here if there is one player with Bankrupt = FALSE.
        {
            gui.printToOutput("Congratulations " + player[winner].getName() + "! You are the winner of this game.");
            gameOver = true;
        }
    }

    // Listens for key pressed (enter), calls commandListener() method, and resets the input box
    public void keyPressed(KeyEvent e)
    {
        int keycode = e.getKeyCode();
        if(keycode == 10)
        {
            // Calls commandListener() and passes the ID of a current player. (i.e. who is sending the command)
            commandListener();
            gui.setEmpty();
        }
    }
    public void keyReleased(KeyEvent e){}   // Not used
    public void keyTyped(KeyEvent e){}      // Not used

    //Calculates Rent based on various multipliers(rules)
    private int calculateRent()
    {

        //CASE_1 If the player lands on his or her own property  nothing happens. [SECURITY FEATURE]
        if(currentSquare.getOwnedBy() == currentPlayer)
        {
            gui.printToOutput("YOU OWN THIS PROPERTY : PLEASE INFORM THE DEVELOPER THAT THIS CONDITION SHOULD MOVE UP THE CHAIN meaning that this is a security feature and this section should never be reached during execution \n");

            return 0;
        }//CASE_2 If Owner of the square has all properties of sam colour group -> currentPlayer pays double the rent. In other workds: Once a player owns all properties of a color group (a monopoly), the rent is now doubled on all unimproved lots of that color group, even if some of the properties are mortgaged to the Bank.
        else if (currentSquare.getOwnedBy()!= null  && currentSquare.getColour() > 0 && currentSquare.getHouseNum() == 0 && !currentSquare.isMortgaged() && currentSquare.getType() == 1)  //Rule_1 the square has to be owned  Rule_2 check if the owner of the square where currentPlayer has landed is owned by the player who has the whole colour group: Rule_3 there has to be no houses on the square Rule_4 the square should NOT be mortgaged Rule_5: The Type of the square has to be SITE, it filters the stations and utilities letting them to go to the next stages of the sequence of else if statements Rule_6: ensures that the pointer in Rule_2 does not go out of the scope
        {
            int rentDue = 0;
            if(currentSquare.getOwnedBy().getOwnsColourGroup(currentSquare.getColour()-1))
            {
                rentDue = currentSquare.getRent()*2;
            }
            return rentDue;
        } //CASE_3 	If the player land on the property which is owned by another player but currently mortgaged, no rent is paid
        else if(currentSquare.isMortgaged())
        {
            return 0;
        }// CASE_4 IF A PROPERTY IS A STATION
        else if(currentSquare.getType() == 2) // Check if the square is of type Station; RULE: The rent a player charges for landing on a railroad(stations) varies with the number of railroads that are also owned by the same player. The rent is as follows: Charge $25 if one is owned, $50 if two are owned, $100 if three are owned, $200 if all four are owned.
        {
            //Count how many stations does the owner of the currentSquare has.
            int count = 0;
            count = currentSquare.getOwnedBy().getCountOfStationsOwned();

            //The rent is as follows:
            if(count == 0)
            {
                return 0;
            }
            else if(count == 1) // Charge $25 if one station is owned
            {
                return 25;
            }
            else if(count == 2) // Charge $50 if two stations are owned
            {
                return 50;
            }
            else if(count == 3) //Charge $100 if three stations are owned
            {
                return 100;
            }
            else if(count == 4) //Charge $200 if four stations are owned
            {
                return 200;
            }
        }// CASE_5 IF A PROPERTY IS A UTILITY
        else if(currentSquare.getType() == 3) // Checks if the property is of type Utility;  Rule: UTILITY For utilities, after a player lands on one to owe rent, the rent is 4 times the amount rolled, if the player owns one utility. If the player possesses both utilities, the rent is 10 times the amount rolled.
        {
            int count = 0;
            count = currentSquare.getOwnedBy().getCountOfUtilitiesOwned();

            //The rent is as follows:
            if(count == 0)
            {
                return 0;
            }
            else if(count == 1) // the rent is 4 times the amount rolled
            {
                int toBePaid = 0;
                toBePaid = currentPlayer.getTotalDice() * 4;
                return  toBePaid;
            }
            else if(count == 2) // the rent is 10 times the amount rolled
            {
                int toBePaid = 0;
                toBePaid = currentPlayer.getTotalDice() * 10;

                return toBePaid;
            }
        }// CASE_5 Standard Case when the property is of type Site and there are some or none of the houses (when the double rent effect is not present)
        else if(currentSquare.getHouseNum() >= 0) // We do not required to add multiple conditions to ensure i.e. that the currentPlayer and the owner of the property are different players, because it is already taken care of in one of the conditions above :)
        {
            return currentSquare.getRent();
        }

        return -1;// ERROR CODE -1 which shows that none of the conditions were satisfied
    }

    // Cards helper method (necessary as Cards class doesn't have access to everything whats needed).
    public int community14helper(Player currentPlayer) // It is your birthday. Collect Â£10 from each player.
    {
        int amountCollected = 0;
        int tempPlayerID = this.currentPlayerID;
        Player tempPlayer;

        do // DoWhile loop which switches through all players until it gets to the current player
        {
            do // DoWhile loop which switches through bankrupt players, as you can't collect â‚¬10 from them
            {
                tempPlayerID++; // Next player
                tempPlayer = player[tempPlayerID];
                if (tempPlayerID >= this.numOfPlayers) tempPlayerID %= this.numOfPlayers;
            } while (tempPlayer.getBankruptCommand());
            tempPlayer.payTo(currentPlayer, 10); // Other players pay 10 to current player
            amountCollected += 10;
            gui.printToOutput(tempPlayer.getName() + " has paid â‚¬10 to " + currentPlayer.getName() + ".");
        } while(tempPlayer != currentPlayer);

        return amountCollected;
    }

    // GUI getter method
    public GUI getGUI() { return gui; }


    //This method helps to switch to different player after currentPlayer enters the command that leads to no other reasonable commands
    public void switchPlayersAfterTheTurnGeneral()
    {
        gui.printToOutput(currentPlayer.getName() + " has finished his/her turn.");
        checkIfBankrupt(); // Before we switch to next player
        do // Do while loop switches through bankrupt players, as they can't play anymore
        {
            this.currentPlayerID++; // Next player
            if (this.currentPlayerID >= this.numOfPlayers) this.currentPlayerID %= this.numOfPlayers;
            this.currentPlayer = player[currentPlayerID]; // updates currentPlayer to the next player
        } while (this.currentPlayer.getBankruptCommand());
        currentPlayer = player[this.currentPlayerID];
        currentSquare = GUI.squareArray[currentPlayer.getCurrentPosition()];
        currentPlayer.checkMortgageCommand();
        currentPlayer.checkRedeemCommand();
        if(!gameOver)
        {
            gui.printToOutput("It is " + currentPlayer.getName() + "'s turn. Type \"help\" to see the list of available commands.");
            currentPlayer.resetCommands(); // Makes sure that commands reset at the beginning of new turn
        }
        else
        {
            gui.printToOutput("The game is over. Type \"exit\" to close the game.");
            currentPlayer.setDoneCommand(true);
            currentPlayer.setRollCommand(false);
        }
    }


}
