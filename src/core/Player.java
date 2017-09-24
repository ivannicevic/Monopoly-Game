/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import javax.swing.*;
import java.util.LinkedList;

public class Player extends JLabel {

    private int currentPosition, money, totalAssets, rollCount;
    private int dice1, dice2, totalDice; // Hold values of the most recent dice roll.
    private Dice dice;
    private String name;
    private Boolean roll, buy, payRent, done, bankrupt, reroll, mortgage, redeem, requiredToPayTax; // Boolean variables holding TRUE if player can use the command.
    private boolean[] ownsColourGroup = new boolean[9]; // TRUE if player owns all properties of that colour. FALSE by default. 9 colour groups in total.
    private LinkedList<Square> ownedProperties;
    private int GOOJcards;
    private int jail;
    private Boolean card, pay;

    public Player(String name)
    {
        this.name = name;
        dice = new Dice(); // Consider NOT making dice object for each player object
        currentPosition = 0;
        money = 1500;
        totalAssets = money;
        ownedProperties = new LinkedList<Square>();
        GOOJcards = 0;
        jail = 0;
        card = false;
        pay = false;
        rollCount = 0;
        reroll = false;
        roll = true;
        buy = false;
        payRent = false;
        done = false;
        bankrupt = false;
        mortgage = false;
        redeem = false;
        requiredToPayTax = false;
    }

    public void move()
    {
        currentPosition++;
        if(currentPosition < 0) currentPosition = currentPosition + BigMoney.NUM_SQUARES;
        else if (currentPosition >= BigMoney.NUM_SQUARES) currentPosition = currentPosition % BigMoney.NUM_SQUARES;
    }

    public void moveManually(int squareNum)
    {
        currentPosition = squareNum;
    }

    // Rolls dice and stores values
    public void roll()
    {
        roll = false;
        dice1 = dice.roll();
        dice2 = dice.roll();
        totalDice = dice1 + dice2;

        if(dice1 == dice2)  { rollCount++;      reroll = true;  }
        else                { rollCount = 0;    reroll = false; }
    }

    // Calculates total assets in a given moment and returns a string containing description of assets
    public String calculateTotalAssets()
    {
        int totalHousesValue = 0;
        for (Square sq : ownedProperties)
            totalHousesValue += (sq.getHouseNum() * sq.getHousePrice() + sq.getHotelNum() * 5 * sq.getHousePrice());

        totalAssets = getMoney() + getPropertiesValue() + totalHousesValue;
        return "Money = $" + getMoney() + "; Properties = $" + getPropertiesValue() + "; Houses and hotels = $" + totalHousesValue + ".";
    }

    // Return values of the most recent dice roll, and reroll counter
    public int      getDice1()      { return dice1; }
    public int      getDice2()      { return dice2; }
    public int      getTotalDice()  { return totalDice; }
    public int      getRollCount()  { return rollCount; }
    public void     setRollCount(int rollCount)  { this.rollCount = rollCount; }

    // Basic getter methods
    public int      getMoney()              { return money; }
    public String   getName()               { return name; }
    public int      getCurrentPosition()    { return currentPosition; }
    public int      getTotalAssets()        { return totalAssets; }
    public LinkedList<Square> getOwnedProperties() { return ownedProperties; }

    // Getter and setter methods for boolean command values
    public boolean getRollCommand()         { return roll; }
    public boolean getRerollCommand()       { return reroll; }
    public boolean getBuyCommand()          { return buy; }
    public boolean getPayRentCommand()      { return payRent; }
    public boolean getOwnsColourGroup(int i){ return ownsColourGroup[i]; } //"i" indicates which colour group is set to TRUE/FALSE (please see colour groups below)
    public boolean getDoneCommand()         { return done; }
    public boolean getBankruptCommand()     { return bankrupt; }
    public boolean getMortgageCommand()     { return mortgage; }
    public boolean getRedeemCommand()       { return redeem; }
    public boolean getRequiredToPayTaxCommand() {return requiredToPayTax; }
    public boolean getCardCommand()         { return card; }
    public boolean getPayCommand()          { return pay; }
    public void setRollCommand(Boolean roll)            { this.roll = roll; }
    public void setRerollCommand(Boolean reroll)        { this.reroll = reroll; }
    public void setBuyCommand(Boolean buy)              { this.buy = buy; }
    public void setPayRentCommand(Boolean payRent)      { this.payRent = payRent; }
    public void setOwnsColourGroup(Boolean full, int i) { ownsColourGroup[i] = full;}
    public void setDoneCommand(Boolean done)            { this.done = done; }
    public void setBankruptCommand(Boolean bankrupt)    { this.bankrupt = bankrupt; }
    public void setMortgageCommand(Boolean mortgage)    { this.mortgage = mortgage; }
    public void setRedeemCommand(Boolean redeem)        { this.redeem = redeem; }
    public void setCardCommand(Boolean card)            { this.card = card; }
    public void setPayCommand(Boolean pay)              { this.pay = pay; }
    public void resetCommands(){ roll = true; buy = false; payRent = false; done = false;}
    public void setRequiredToPayTaxCommand(Boolean requiredToPayTax){ this.requiredToPayTax = requiredToPayTax; }

    // Checks if boolean done command should become true
    public void checkDoneCommand() { if(!roll && !reroll && !payRent) done = true; }

    //Handles money moving in and out of accounts
    public void debit   (int  amount) {money += amount;} // Adds money to player
    public boolean credit  (int  amount) // Takes money from player, returns TRUE if transaction is successful, or FALSE if player does not have enough money
    {
        if(money >= amount)
        {
            money -= amount;
            return true;
        }
        else return false;
    }
    public void payTo(Player creditor, int amount) // Pays a creditor from THIS player
    {
        credit(amount);
        creditor.debit(amount);
    }

    // Properties owned by player - LinkedList methods
    public void addProperty (Square property, int squareID)
    {
        GUI.squareArray[squareID].setOwnedBy(this);
        ownedProperties.add(property);
        checkColourGroup(squareID);
    }

    public void removeProperty (Square property, int squareID)
    {
        // MIGHT NOT WORK PROPERLY! NOT TESTED! Required for sprint 4 - SELLING properties
        GUI.squareArray[squareID].setOwnedBy(null);
        for(int i = 0; i < ownedProperties.size(); i++)
            if(GUI.squareArray[squareID].getId() == ownedProperties.get(i).getId()) ownedProperties.remove(i);
        checkColourGroup(squareID);
    }

    // Checks if player owns whole colour group of a particular property
    public void checkColourGroup(int squareID)
    {
        int colourGroup = getColorGroupForSquareID(squareID);

        if (colourGroup != -1)
        {
            if (colourGroup == 0)
            {
                if(GUI.squareArray[1].getOwnedBy() == this && GUI.squareArray[3].getOwnedBy() == this) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 1)
            {
                if(GUI.squareArray[6].getOwnedBy() == this && GUI.squareArray[8].getOwnedBy() == this && GUI.squareArray[9].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 2)
            {
                if(GUI.squareArray[11].getOwnedBy() == this && GUI.squareArray[13].getOwnedBy() == this && GUI.squareArray[14].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 3)
            {
                if(GUI.squareArray[16].getOwnedBy() == this && GUI.squareArray[18].getOwnedBy() == this && GUI.squareArray[19].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 4)
            {
                if(GUI.squareArray[21].getOwnedBy() == this && GUI.squareArray[23].getOwnedBy() == this && GUI.squareArray[24].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 5)
            {
                if(GUI.squareArray[26].getOwnedBy() == this && GUI.squareArray[27].getOwnedBy() == this && GUI.squareArray[29].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 6)
            {
                if(GUI.squareArray[31].getOwnedBy() == this && GUI.squareArray[32].getOwnedBy() == this && GUI.squareArray[34].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
            else if(colourGroup == 7)
            {
                if(GUI.squareArray[37].getOwnedBy() == this && GUI.squareArray[1].getOwnedBy() == this && GUI.squareArray[39].getOwnedBy() == this ) ownsColourGroup[colourGroup] = true;
                else ownsColourGroup[colourGroup] = false;
            }
        }
    }
    // Returns the ID of a colour group for the ID of a particular square
    public int getColorGroupForSquareID(int squareID)
    {
        if      (squareID == 1  || squareID == 3)                    return 0; // Brown
        else if (squareID == 6  || squareID == 8  || squareID == 9)  return 1; // Light Blue
        else if (squareID == 11 || squareID == 13 || squareID == 14) return 2; // Pink
        else if (squareID == 16 || squareID == 18 || squareID == 19) return 3; // Orange
        else if (squareID == 21 || squareID == 23 || squareID == 24) return 4; // Red
        else if (squareID == 26 || squareID == 27 || squareID == 29) return 5; // Yellow
        else if (squareID == 31 || squareID == 32 || squareID == 34) return 6; // Green
        else if (squareID == 37 || squareID == 39)                   return 7; // Blue
        else                                                         return -1; // Not a site (no colour)
    }
    // Builds houses on a property based on the input and returns a string to be printed on GUI output
    public String build(Square property, String numOfHousesSTR) throws NumberFormatException
    {
        int numOfHouses;
        String output;
        try
        {
            // Parsing an integer and checking if the number is valid
            numOfHouses = Integer.parseInt(numOfHousesSTR, 10);
            if(numOfHouses <= 0 || numOfHouses > 5) output = "Invalid number of houses. Please use value between 1 and 5.";
            else
            {
                if(property.getHotelNum() == 1) // If there is a hotel already on the property, player can't build any more houses
                {
                    output = "You can't build more houses here. You already have a hotel on this property.";
                }
                else // If there are no hotels, player can build houses on the property
                {
                    if(property.getHouseNum() + numOfHouses < 5) // Checks if total number of new houses + existing houses does not exceed 4
                    {
                        property.setHouseNum(property.getHouseNum() + numOfHouses);
                        credit(numOfHouses * property.getHousePrice());
                        output = "You have successfully built " + numOfHouses + " houses on " + property.getFullName()
                                + ". This has cost you $" + numOfHouses * property.getHousePrice() + ". There is a total of " + property.getHouseNum() + " houses on this property now.";
                    }
                    else if(property.getHouseNum() + numOfHouses == 5) // If number of total houses is 5, remove all houses and add 1 hotel.
                    {
                        property.setHouseNum(0);
                        property.setHotelNum(1);
                        credit(numOfHouses * property.getHousePrice());
                        output = "You have successfully built a hotel on " + property.getFullName() + ". This has cost you $" + numOfHouses * property.getHousePrice() + ".";
                    }
                    else
                    {
                        output = "The total number of houses can not exceed 5.";
                    }
                }
            }
        }
        catch (NumberFormatException e) // If argument[2] is not an integer at all, string can't be parsed.
        {
            output = "Number of houses to be built is invalid. Please use format \"build <property name> <number of houses>\".";
        }

        return output;
    }

    public String demolish(Square property, String numOfHousesSTR) throws NumberFormatException
    {
        int numOfHouses, amount;
        String output;
        try
        {
            // Parsing an integer and checking if the number is valid
            numOfHouses = Integer.parseInt(numOfHousesSTR, 10);
            if(numOfHouses == 5) // If input is 5, than the property must have a hotel, otherwise - error
            {
                if(property.getHotelNum() == 1)
                {
                    property.setHotelNum(0);
                    amount = property.getHousePrice() * 5 / 2;
                    debit(amount);
                    output = "You have successfully demolished a hotel. You received $" + amount + ".";
                }
                else output = "You don't have a hotel on this property.";
            }
            else if(numOfHouses < 5 && numOfHouses > 0) // Makes sure that input is valid, player can only ever demolish between 1 and 5 houses (5 if hotel)
            {
                if((property.getHouseNum() >= numOfHouses) || property.getHotelNum() == 1)
                {
                    if(property.getHotelNum() == 1) // If player wants to downgrade from a hotel, to few houses
                    {
                        property.setHotelNum(0);
                        property.setHouseNum(5 - numOfHouses);
                    }
                    else // If player just wants to remove a number of houses
                    {
                        property.setHouseNum(property.getHouseNum() - numOfHouses);
                    }
                    amount = property.getHousePrice() * numOfHouses / 2;
                    debit(amount);
                    output = "You have successfully demolished " + numOfHouses + " houses. You received $" + amount + ". This property now has " + property.getHouseNum() + " houses and " + property.getHotelNum() + " hotels.";
                }
                else output = "You have less houses on this property, than the number you want to demolish.";
            }
            else output = "Invalid number of houses to be demolished.";
        }
        catch (NumberFormatException e) // If argument[2] is not an integer at all, string can't be parsed.
        {
            output = "Number of houses to be demolished is invalid. Please use format \"demolish <property name> <number of houses>\".";
        }

        return output;
    }

    // Returns the entire LinkedList as a single string, or returns null if empty.
    public String getPropertiesToString ()
    {
        if(ownedProperties.size() == 0) return null;
        else
        {
            int i = 0;
            String output = "";
            for (Square s : ownedProperties) {
                output = output + s.getFullName() + ", ";
            }
            return output;
        }
    }
    // Returns the total value of all properties owned by a player in a single integer.
    public int getPropertiesValue ()
    {
        if(ownedProperties.size() == 0) return 0;
        else {
            int value = 0;
            for(Square s:ownedProperties){
                value = value + s.getPrice();
            }
            return value;
        }
    }

    // Returns amount of stations owned by the player
    public int getCountOfStationsOwned()
    {
        int count = 0; // counts the amount of stations owned
        // Base case when there are no properties owned
        if(ownedProperties.size() == 0)
        {
            return 0;
        }
        else // count how many stations does the player have in his property list
        {
            for(Square s:ownedProperties)
            {
                if(s.getType() == 2)
                {
                    count++;
                }
            }
            return count;
        }

    }

    // Returns amount of utilities owned by the player
    public int getCountOfUtilitiesOwned()
    {
        int count = 0; // counts the amount of utilities owned
        // Base case when there are no properties owned
        if(ownedProperties.size() == 0)
        {
            return 0;
        }
        else // count how many stations does the player have in his property list
        {
            for(Square s:ownedProperties)
            {
                if(s.getType() == 3)
                {
                    count++;
                }
            }
            return count;
        }
    }

    // Upon Bankruptcy the properties has to be returned to the bank & market
    public void returnOwnedPropertiesToBank()
    {
        int count = 0; // counts the amount of stations owned
        // Base case when there are no properties owned

        if (ownedProperties.size() > 0) // count how many stations does the player have in his property list
        {
            for(Square s:ownedProperties) // Recent 4 key attributes of properties so that it shows them to belong to bank and being back on the market
            {
                int squareID = s.getId();

                GUI.squareArray[squareID].setOwnedBy(null);
                GUI.squareArray[squareID].setBuyable(true); //the properties that the player owns had to be buyable before the currentPlayer purchased them and so it comes back to the same stage
                GUI.squareArray[squareID].setHouseNum(0);
                GUI.squareArray[squareID].setHotelNum(0);
                GUI.squareArray[squareID].setMortgaged(false);
                this.bankrupt = true;
            }
        }
        return ;
    }


    //checking if property is available to be mortgaged
    public void checkMortgageCommand()
    {
        if(ownedProperties.size() == 0)  mortgage = false;
        else
        {
            mortgage = false;
            int i = 0;
            String output = "";
            for (Square s : ownedProperties)
            {
                if(!s.isMortgaged())
                {
                    mortgage = true;
                    break;
                }
            }
        }
    }


    //checking if property is available to be redeemed
    public void checkRedeemCommand()
    {
        if(ownedProperties.size() == 0)  redeem = false;
        else
        {
            redeem = false;
            int i = 0;
            String output = "";
            for (Square s : ownedProperties)
            {
                if(s.isMortgaged())
                {
                    redeem = true;
                    break;
                }
            }
        }
    }

    // Jail related methods
    public int getGOOJcards()    { return GOOJcards; }
    public void addGOOJcard()    { GOOJcards++; }
    public void removeGOOJcard() { GOOJcards--; }
    public void goToJail(BigMoney game)
    {
        game.movePlayerManually(this, 10);
        jail = 3;
        reroll =false;
        rollCount = 0;
        game.switchPlayersAfterTheTurnGeneral(); //DEBUG TMP
    }

    public int checkJail() { return jail; }
    public void setJail(int jail)   { this.jail = jail; }

    //Function that allows to deduct money manually by typing "admin minus balance" and also ensures bankruptcy of the player when he/she decides to do so
    public void setMinusBalance ()
    {
        money -= 99999;
    }

    //Pay taxes method choice no 1
    public boolean payTax200() // Takes money from player, returns TRUE if transaction is successful, or FALSE if player does not have enough money
    {
        if(money >= 200)
        {
            money -= 200;
            setRequiredToPayTaxCommand(false);
            return true;

        }
        else return false;

    }


    //Pay taxes method choice no 2

    public boolean payTax10Percent()
    {
        calculateTotalAssets();
        int taxableAssets = totalAssets/100 * 10; // 10% of total assets

        if(money >= taxableAssets)
        {
            money -= taxableAssets;
            this.setRequiredToPayTaxCommand(false);
            return true;
        }
        else return false;
    }

}
