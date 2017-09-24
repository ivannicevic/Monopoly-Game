/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import java.util.Random;

class Cards {
    private boolean community16helper = false;

    Cards(){}

    void drawNewCard(Square currentSquare, Player currentPlayer, BigMoney game) {
        Random random = new Random();
        int cardNum = random.nextInt(16) + 1;

        if (currentSquare.getType() == 4)
            selectCommunityCard(cardNum, currentPlayer, game);
        else if (currentSquare.getType() == 5)
            selectChanceCard(cardNum, currentPlayer, game);
    }

    private void selectCommunityCard(int cardNum, Player currentPlayer, BigMoney game) {
        if(cardNum == 1) community1(currentPlayer, game);
        else if(cardNum == 2) community2(currentPlayer, game);
        else if(cardNum == 3) community3(currentPlayer, game);
        else if(cardNum == 4) community4(currentPlayer, game);
        else if(cardNum == 5) community5(currentPlayer, game);
        else if(cardNum == 6) community6(currentPlayer, game);
        else if(cardNum == 7) community7(currentPlayer, game);
        else if(cardNum == 8) community8(currentPlayer, game);
        else if(cardNum == 9) community9(currentPlayer, game);
        else if(cardNum == 10) community10(currentPlayer, game);
        else if(cardNum == 11) community11(currentPlayer, game);
        else if(cardNum == 12) community12(currentPlayer, game);
        else if(cardNum == 13) community13(currentPlayer, game);
        else if(cardNum == 14) community14(currentPlayer, game);
        else if(cardNum == 15) community15(currentPlayer, game);
        else if(cardNum == 16) community16(currentPlayer, game);
        else System.out.println("Error in selectCommunityCard().");
    }

    private void selectChanceCard(int cardNum, Player currentPlayer, BigMoney game)
    {
        if(cardNum == 1) chance1(currentPlayer, game);
        else if(cardNum == 2) chance2(currentPlayer, game);
        else if(cardNum == 3) chance3(currentPlayer, game);
        else if(cardNum == 4) chance4(currentPlayer, game);
        else if(cardNum == 5) chance5(currentPlayer, game);
        else if(cardNum == 6) chance6(currentPlayer, game);
        else if(cardNum == 7) chance7(currentPlayer, game);
        else if(cardNum == 8) chance8(currentPlayer, game);
        else if(cardNum == 9) chance9(currentPlayer, game);
        else if(cardNum == 10) chance10(currentPlayer, game);
        else if(cardNum == 11) chance11(currentPlayer, game);
        else if(cardNum == 12) chance12(currentPlayer, game);
        else if(cardNum == 13) chance13(currentPlayer, game);
        else if(cardNum == 14) chance14(currentPlayer, game);
        else if(cardNum == 15) chance15(currentPlayer, game);
        else if(cardNum == 16) chance16(currentPlayer, game);
        else System.out.println("Error in selectChanceCard().");
    }

    // Community methods
    private void community1(Player currentPlayer, BigMoney game) // Advance to Go
    {
        game.getGUI().printToOutput("Community Card: Advance to Go.");
        game.movePlayerManually(currentPlayer, 0);
    }
    private void community2(Player currentPlayer, BigMoney game) // Go back to Old Kent Road
    {
        game.getGUI().printToOutput("Community Card: Go back to Old Kent Road.");
        game.movePlayerManually(currentPlayer, 1);
    }
    private void community3(Player currentPlayer, BigMoney game) // Go to jail. Move directly to jail. Do not pass Go. Do not collect $200.
    {
        game.getGUI().printToOutput("Community Card: Go to jail. Move directly to jail. Do not pass Go. Do not collect $200.");
        currentPlayer.goToJail(game);
    }
    private void community4(Player currentPlayer, BigMoney game) // Pay hospital $100.
    {
        game.getGUI().printToOutput("Community Card: Pay hospital $100.");
        currentPlayer.credit(100);
        game.getGUI().printToOutput("$100 has been deducted from your account.");
    }
    private void community5(Player currentPlayer, BigMoney game) // Doctor's fee. Pay $50.
    {
        game.getGUI().printToOutput("Community Card: Doctor's fee. Pay $50.");
        currentPlayer.credit(50);
        game.getGUI().printToOutput("$50 has been deducted from your account.");
    }
    private void community6(Player currentPlayer, BigMoney game) // Pay your insurance premium $50.
    {
        game.getGUI().printToOutput("Community Card: Pay your insurance premium $50.");
        currentPlayer.credit(50);
        game.getGUI().printToOutput("$50 has been deducted from your account.");
    }
    private void community7(Player currentPlayer, BigMoney game) // Bank error in your favour. Collect £200.
    {
        game.getGUI().printToOutput("Community Card: Bank error in your favour. Collect £200.");
        currentPlayer.debit(200);
        game.getGUI().printToOutput("$200 has been added to your account.");
    }
    private void community8(Player currentPlayer, BigMoney game) // Annuity matures. Collect $100.
    {
        game.getGUI().printToOutput("Community Card: Annuity matures. Collect $100.");
        currentPlayer.debit(100);
        game.getGUI().printToOutput("$100 has been added to your account.");
    }
    private void community9(Player currentPlayer, BigMoney game) // You inherit $100.
    {
        game.getGUI().printToOutput("Community Card: You inherit $100.");
        currentPlayer.debit(100);
        game.getGUI().printToOutput("$100 has been added to your account.");
    }
    private void community10(Player currentPlayer, BigMoney game) // From sale of stock you get $50.
    {
        game.getGUI().printToOutput("Community Card: From sale of stock you get $50.");
        currentPlayer.debit(50);
        game.getGUI().printToOutput("$50 has been added to your account.");
    }
    private void community11(Player currentPlayer, BigMoney game) // Receive interest on 7% preference shares: $25.
    {
        game.getGUI().printToOutput("Community Card: Receive interest on 7% preference shares: $25.");
        currentPlayer.debit(25);
        game.getGUI().printToOutput("$25 has been added to your account.");
    }
    private void community12(Player currentPlayer, BigMoney game) // Income tax refund. Collect £20.
    {
        game.getGUI().printToOutput("Community Card: Income tax refund. Collect £20.");
        currentPlayer.debit(20);
        game.getGUI().printToOutput("$20 has been added to your account.");
    }
    private void community13(Player currentPlayer, BigMoney game) // You have won second prize in a beauty contest. Collect £10.
    {
        game.getGUI().printToOutput("Community Card: You have won second prize in a beauty contest. Collect £10.");
        currentPlayer.debit(10);
        game.getGUI().printToOutput("$10 has been added to your account.");
    }
    private void community14(Player currentPlayer, BigMoney game) // It is your birthday. Collect £10 from each player.
    {
        game.getGUI().printToOutput("Community Card: It is your birthday. Collect £10 from each player.");
        int amountCollected = game.community14helper(currentPlayer);
        game.getGUI().printToOutput("You collected a total of £" + amountCollected + ".");
    }
    private void community15(Player currentPlayer, BigMoney game) // Get out of jail free. This card may be kept until needed or sold.
    {
        game.getGUI().printToOutput("Community Card: Get out of jail free. This card may be kept until needed or sold.");
        currentPlayer.addGOOJcard();
        game.getGUI().printToOutput("You got a Get Out of Jail card. You have " + currentPlayer.getGOOJcards() + " cards in total.");
    }
    private void community16(Player currentPlayer, BigMoney game) // Pay a £10 fine or take a Chance.
    {
        game.getGUI().printToOutput("Community Card: Pay a £10 fine or take a Chance.");
        community16helper = true;
        game.getGUI().printToOutput("Type in \"pay10\" to pay a fine or \"take chance\" to take a random Chance card");
    }

    // Chance methods
    private void chance1(Player currentPlayer, BigMoney game) // Advance to Go.
    {
        game.getGUI().printToOutput("Chance Card: Advance to Go.");
        game.movePlayerManually(currentPlayer, 0);
    }
    private void chance2(Player currentPlayer, BigMoney game) // Go to jail. Move directly to jail. Do not pass Go. Do not collect £200.
    {
        game.getGUI().printToOutput("Chance Card: Go to jail. Move directly to jail. Do not pass Go. Do not collect $200.");
        currentPlayer.goToJail(game);
    }
    private void chance3(Player currentPlayer, BigMoney game) // Advance to Pall Mall. If you pass Go collect £200.
    {
        game.getGUI().printToOutput("Chance Card: Advance to Pall Mall. If you pass Go collect £200.");
        if(currentPlayer.getCurrentPosition() > 11)
        {
            currentPlayer.debit(200);
            game.getGUI().printToOutput("You have passed Go. $200 has been added to your account.");
        }
        game.movePlayerManually(currentPlayer, 11);
    }
    private void chance4(Player currentPlayer, BigMoney game) // Take a trip to Marylebone Station and if you pass Go collect £200.
    {
        game.getGUI().printToOutput("Chance Card: Take a trip to Marylebone Station and if you pass Go collect £200.");
        if(currentPlayer.getCurrentPosition() > 15)
        {
            currentPlayer.debit(200);
            game.getGUI().printToOutput("You have passed Go. $200 has been added to your account.");
        }
        game.movePlayerManually(currentPlayer, 15);
    }
    private void chance5(Player currentPlayer, BigMoney game) // Advance to Trafalgar Square. If you pass Go collect £200.
    {
        game.getGUI().printToOutput("Chance Card: Advance to Trafalgar Square. If you pass Go collect £200.");
        if(currentPlayer.getCurrentPosition() > 24)
        {
            currentPlayer.debit(200);
            game.getGUI().printToOutput("You have passed Go. $200 has been added to your account.");
        }
        game.movePlayerManually(currentPlayer, 24);
    }
    private void chance6(Player currentPlayer, BigMoney game) // Advance to Mayfair.
    {
        game.getGUI().printToOutput("Chance Card: Advance to Mayfair.");
        game.movePlayerManually(currentPlayer, 39);
    }
    private void chance7(Player currentPlayer, BigMoney game) // Go back three spaces.
    {
        game.getGUI().printToOutput("Chance Card: Go back three spaces.");
        currentPlayer.moveManually(currentPlayer.getCurrentPosition() - 3);
    }
    private void chance8(Player currentPlayer, BigMoney game) // Make general repairs on all of your houses. For each house pay £25. For each hotel pay £100.
    {
        game.getGUI().printToOutput("Chance Card: Make general repairs on all of your houses. For each house pay £25. For each hotel pay £100.");
        int numOfHouses = 0, numOfHotels = 0;
        for (Square sq : currentPlayer.getOwnedProperties())
        {
            if(sq.getHotelNum() != 0) numOfHotels = numOfHotels + sq.getHotelNum();
            if(sq.getHouseNum() != 0) numOfHouses = numOfHouses + sq.getHouseNum();
        }
        int amount = numOfHouses*25 + numOfHotels*100;
        currentPlayer.credit(amount);
        game.getGUI().printToOutput("You have " + numOfHouses + " houses and " + numOfHotels + " hotels in total. " + amount + "£ has been deducted from your account.");
    }
    private void chance9(Player currentPlayer, BigMoney game) // You are assessed for street repairs: £40 per house, £115 per hotel.
    {
        game.getGUI().printToOutput("Chance Card: You are assessed for street repairs: £40 per house, £115 per hotel.");
        int numOfHouses = 0, numOfHotels = 0;
        for (Square sq : currentPlayer.getOwnedProperties())
        {
            if(sq.getHotelNum() != 0) numOfHotels = numOfHotels + sq.getHotelNum();
            if(sq.getHouseNum() != 0) numOfHouses = numOfHouses + sq.getHouseNum();
        }
        int amount = numOfHouses*40 + numOfHotels*115;
        currentPlayer.credit(amount);
        game.getGUI().printToOutput("You have " + numOfHouses + " houses and " + numOfHotels + " hotels in total. " + amount + "£ has been deducted from your account.");

    }
    private void chance10(Player currentPlayer, BigMoney game) // Pay school fees of £150.
    {
        game.getGUI().printToOutput("Chance Card: Pay school fees of £150.");
        currentPlayer.credit(150);
        game.getGUI().printToOutput("$150 has been deducted from your account.");
    }
    private void chance11(Player currentPlayer, BigMoney game) // Drunk in charge fine £20.
    {
        game.getGUI().printToOutput("Chance Card: Drunk in charge fine £20.");
        currentPlayer.credit(20);
        game.getGUI().printToOutput("$20 has been deducted from your account.");
    }
    private void chance12(Player currentPlayer, BigMoney game) // Speeding fine £15.
    {
        game.getGUI().printToOutput("Chance Card: Speeding fine £15.");
        currentPlayer.credit(15);
        game.getGUI().printToOutput("$15 has been deducted from your account.");
    }
    private void chance13(Player currentPlayer, BigMoney game) // Your building loan matures. Receive £150.
    {
        game.getGUI().printToOutput("Chance Card: Your building loan matures. Receive £150.");
        currentPlayer.debit(150);
        game.getGUI().printToOutput("$150 has been added to your account.");
    }
    private void chance14(Player currentPlayer, BigMoney game) // You have won a crossword competition. Collect £100.
    {
        game.getGUI().printToOutput("Chance Card: You have won a crossword competition. Collect £100.");
        currentPlayer.debit(100);
        game.getGUI().printToOutput("$100 has been added to your account.");
    }
    private void chance15(Player currentPlayer, BigMoney game) // Bank pays you dividend of £50.
    {
        game.getGUI().printToOutput("Chance Card: Bank pays you dividend of £50.");
        currentPlayer.debit(50);
        game.getGUI().printToOutput("$50 has been added to your account.");
    }
    private void chance16(Player currentPlayer, BigMoney game) // Get out of jail free. This card may be kept until needed or sold.
    {
        game.getGUI().printToOutput("Chance Card: Get out of jail free. This card may be kept until needed or sold.");
        currentPlayer.addGOOJcard();
        game.getGUI().printToOutput("You got a Get Out of Jail card. You have " + currentPlayer.getGOOJcards() + " cards in total.");
    }

    public boolean getCommunity16helper() { return community16helper; }
    public void setCommunity16helper(boolean command) { community16helper = command; }

    // Drawing a random Chance card from Community 16 card
    public void drawChanceCard_Community16Helper(Player currentPlayer, BigMoney game)
    {
        Random random = new Random();
        int cardNum = random.nextInt(16) + 1;

        selectChanceCard(cardNum, currentPlayer, game);
    }
}