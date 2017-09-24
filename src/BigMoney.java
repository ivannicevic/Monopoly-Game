

public class BigMoney implements Bot {

	// The public API of YourTeamName must not change
	// You cannot change any other classes
	// YourTeamName may not alter the state of the board or the player objects
	// It may only inspect the state of the board and the player objects
	private int stopCounter = 0, endOfTurnCounter = 0;
	private Boolean startOfTheTurn = true, wasInJail = false;
	private BoardAPI board;
	private PlayerAPI player;
	private DiceAPI dice;

	BigMoney(BoardAPI board, PlayerAPI player, DiceAPI dice) {
		this.board = board;
		this.player = player;
		this.dice = dice;
		return;
	}

	public String getName() {
		return "BigMoney";
	}

	public String getCommand() {
		//sleep(500);
		int playerPosition = player.getPosition();
		Square currentSquare = board.getSquare(playerPosition);

		// If player has negative balance he can try to mortgage properties, or he is bankrupt
		if (player.getBalance() < 0) {
			// If player owns any properties try to mortgage them first to get positive balance
			if (player.getNumProperties() != 0) {
				return mortgageProperty();
			}
			// If player has no properties, declare bankruptcy immediately
			else {
				return "bankrupt";
			}
		}

		// If player has enough money to redeem a property, he will redeem it
		String shortNameRedeem = findCheapestRedeem();
		if (shortNameRedeem != null) {
			return "redeem " + shortNameRedeem;
		}

		// If start of the turn the player rolls
		if (startOfTheTurn) {
			startOfTheTurn = false;
			if (player.isInJail()) wasInJail = true;
			return "roll";
		} else if (dice.isDouble() && !player.isInJail() && !wasInJail) {
			return "roll";
		}

		// If player stepped on a property he will buy it and build houses if possible
		if (board.isProperty(playerPosition)) {
			Property currentProperty = board.getProperty(playerPosition);
			String shortName = currentProperty.getShortName();

			// If property is not owned and player has enough money, he will buy it
			if (!currentProperty.isOwned() && player.getBalance() - currentProperty.getPrice() > 250) {
				return "buy";
			}
		}



        /*Decision making for when to use get out jail card. The player would choose to do so only when there are more than 5 properties that are not owned.
        When more than 23 properties are owned the player would stay in jail with the purpose of avoiding stepping on already owned properties*/
		if (player.isInJail() && player.hasGetOutOfJailCard() && getOwnedPropertiesOntheBoardNumber() < 24) {
			return "card";
		}

	    /*Solves the case where the person could get out of jail without staying in for the required duration
		if(!startOfTheTurn && player.isInJail()) {
			wasInJail = true;
		}
		else {
			wasInJail = false;
		}
		*/
		if (canBuild())
			return checkSiteToBuy();
		return endOfTurn();
	}

	private String endOfTurn() {
		if (endOfTurnCounter == 0) {
			endOfTurnCounter++;
			return "property";
		} else if (endOfTurnCounter == 1) {
			endOfTurnCounter++;
			return "balance";
		} else {
			endOfTurnCounter = 0;
			startOfTheTurn = true;
			wasInJail = false;
			return "done";
		}
	}

	public String getDecision() {
		// Add your code here
		return decideChanceOrPayTenMoneyUnits();
	}

	private String mortgageProperty() {
		System.out.println(player.getProperties()); // DEBUG

		// Case 1: Check if there is any utility
		for (int i = 0; i < player.getProperties().size() - 1; i++) {
			String shortName = player.getProperties().get(i).getShortName();
			Property mortgagingProperty = board.getProperty(shortName);
			if (board.isUtility(shortName)) {
				if (!mortgagingProperty.isMortgaged()) {
					return "mortgage " + shortName;
				}
			}
		}

		// Case 2: Check if there are any stations
		for (int i = 0; i < player.getProperties().size() - 1; i++) {
			String shortName = player.getProperties().get(i).getShortName();
			Property mortgagingProperty = board.getProperty(shortName);
			if (board.isStation(shortName)) {
				if (!mortgagingProperty.isMortgaged()) {
					return "mortgage " + shortName;
				}
			}
		}

		// Case 3: Check if there are any sites without houses or with houses
		for (int i = 0; i < player.getProperties().size() - 1; i++) {
			String shortName = player.getProperties().get(i).getShortName();
			Property mortgagingProperty = board.getProperty(shortName);
			if (board.isSite(shortName)) {
				Site mortgagingSite = (Site) board.getProperty(shortName);
				if (!mortgagingProperty.isMortgaged()) {
					// If site has no houses
					if (!mortgagingSite.hasBuildings()) {
						return "mortgage " + shortName;
					}
					// If site has houses
					else {
						if (mortgagingSite.canDemolish(1)) {
							return "demolish " + shortName + " 1";
						}
					}
				}
			}
		}

		// If non properties are found to be mortgaged, the player goes bankrupt.
		return "bankrupt";
	}

	// Finds cheapest property available for redemption, if any
	private String findCheapestRedeem() {
		int redeemPrice = 0;
		String redeemShortName = null;
		if (player.getNumProperties() != 0) {
			for (int i = 0; i < player.getProperties().size() - 1; i++) {
				String shortName = player.getProperties().get(i).getShortName();
				if (board.getProperty(shortName).isMortgaged()) {
					if (board.getProperty(shortName).getMortgageRemptionPrice() > redeemPrice) {
						redeemPrice = board.getProperty(shortName).getMortgageRemptionPrice();
						redeemShortName = shortName;
					}
				}
			}
			if (player.getBalance() >= redeemPrice && redeemPrice != 0) {
				System.out.println("Debug: pric" + redeemPrice + " nam " + redeemShortName + ".");
				return redeemShortName;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private int getOwnedPropertiesOntheBoardNumber() {
		int ownedPropertiesOntheBoard = 0;
		for (int i = 0; i < 40; i++) {

			if (board.isProperty(i)) {
				if (board.getProperty(i).isOwned()) {
					ownedPropertiesOntheBoard++;
				}
			}

		}
		return ownedPropertiesOntheBoard;
	}

	private String decideChanceOrPayTenMoneyUnits() {
		//Check how many properties out of 10 that matter are owned by someone else Properties that matter: 4- Railroads, 1 - Red Color Illinois Avenue, 1 - Purple Color St. Charles place, 1 - Electric Company, 1 - Water Works, Navy lue Color 1 - Broadwalk, Orange Color 1 - New York Avenue
		int ownedByCurrentPlayer = 0;
		int ownedByOtherPlayers = 0;

		//Counting ownedByCurrentPlayer, ownedByCurrentPlayer
		for (int i = 0; i < 40; i++) {

			if (board.isProperty(i)) {
				if (board.getProperty(i).isOwned() && (i == 11 || i == 15 || i == 24 || i == 39)) {
					if (board.getProperty(i).getOwner() == player) {
						ownedByCurrentPlayer++;
					} else {
						ownedByOtherPlayers++;
					}
				}
			}

		}

		//Decision making logic (CUSTOM), can be changed as required per strategy
		if (ownedByCurrentPlayer >= 2 && ownedByOtherPlayers <= 1) {
			return "chance";
		} else if (ownedByCurrentPlayer <= 2 && ownedByOtherPlayers >= 2) {
			return "pay";
		} else {
			return "chance";
		}

	}

	private String checkSiteToBuy()//checks to see which houses to buy
	{
		for (int numOfHouses = 0; numOfHouses < 6; numOfHouses++) {

			Site temp = (Site) board.getSquare(19);//checks if the colour group is owned by the bot
			if (player.isGroupOwner(temp) && player.getBalance() > 350) {//if it is and the bot has adequate funds with a small amount left over
				if (temp.getNumBuildings() == numOfHouses)//if a site does not have as much houses as other it will get one or in this case be the first to get oen
					return "build vine 1";

				temp = (Site) board.getSquare(18);
				if (temp.getNumBuildings() == numOfHouses)
					return "build marlborough 1";

				temp = (Site) board.getSquare(16);
				if (temp.getNumBuildings() == numOfHouses)
					return "build bow 1";
			}

			temp = (Site) board.getSquare(14);
			if (player.isGroupOwner(temp) && player.getBalance() > 350) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build northumberland 1";

				temp = (Site) board.getSquare(13);
				if (temp.getNumBuildings() == numOfHouses)
					return "build whitehall 1";

				temp = (Site) board.getSquare(11);
				if (temp.getNumBuildings() == numOfHouses)
					return "build mall 1";
			}

			temp = (Site) board.getSquare(24);
			if (player.isGroupOwner(temp) && player.getBalance() > 400) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build trafalgar 1";

				temp = (Site) board.getSquare(23);
				if (temp.getNumBuildings() == numOfHouses)
					return "build fleet 1";

				temp = (Site) board.getSquare(21);
				if (temp.getNumBuildings() == numOfHouses)
					return "build strand 1";
			}

			temp = (Site) board.getSquare(29);
			if (player.isGroupOwner(temp) && player.getBalance() > 400) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build piccadilly 1";

				temp = (Site) board.getSquare(27);
				if (temp.getNumBuildings() == numOfHouses)
					return "build coverntry 1";

				temp = (Site) board.getSquare(26);
				if (temp.getNumBuildings() == numOfHouses)
					return "build leicester 1";
			}

			temp = (Site) board.getSquare(34);
			if (player.isGroupOwner(temp) && player.getBalance() > 450) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build regent 1";

				temp = (Site) board.getSquare(32);
				if (temp.getNumBuildings() == numOfHouses)
					return "build oxford 1";

				temp = (Site) board.getSquare(31);
				if (temp.getNumBuildings() == numOfHouses)
					return "build bond 1";
			}

			temp = (Site) board.getSquare(39);
			if (player.isGroupOwner(temp) && player.getBalance() > 450) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build mayfair 1";

				temp = (Site) board.getSquare(37);
				if (temp.getNumBuildings() == numOfHouses)
					return "build park 1";
			}

			temp = (Site) board.getSquare(9);
			if (player.isGroupOwner(temp) && player.getBalance() > 300) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build pentonville 1";

				temp = (Site) board.getSquare(8);
				if (temp.getNumBuildings() == numOfHouses)
					return "build euston 1";

				temp = (Site) board.getSquare(6);
				if (temp.getNumBuildings() == numOfHouses)
					return "build angel 1";
			}

			temp = (Site) board.getSquare(3);
			if (player.isGroupOwner(temp) && player.getBalance() > 300) {
				if (temp.getNumBuildings() == numOfHouses)
					return "build whitechapel 1";

				temp = (Site) board.getSquare(1);
				if (temp.getNumBuildings() == numOfHouses)
					return "build kent 1";
			}
		}
		return null;
	}

	private Boolean canBuild()//checks to see if there are any houses available to buy
	{
		Site temp = (Site) board.getSquare(19);//checks if the colour group is owned by the bot
		if (player.isGroupOwner(temp) && player.getBalance() > 350) {//if it is and the bot has adequate funds with a small amount left over
			if (temp.getNumBuildings() < 5)//if a site does not have as much houses as other it will get one or in this case be the first to get oen
				return true;

			temp = (Site) board.getSquare(18);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(16);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(14);
		if (player.isGroupOwner(temp) && player.getBalance() > 350) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(13);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(11);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(24);
		if (player.isGroupOwner(temp) && player.getBalance() > 400) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(23);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(21);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(29);
		if (player.isGroupOwner(temp) && player.getBalance() > 400) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(27);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(26);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(34);
		if (player.isGroupOwner(temp) && player.getBalance() > 450) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(32);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(31);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(39);
		if (player.isGroupOwner(temp) && player.getBalance() > 450) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(37);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(9);
		if (player.isGroupOwner(temp) && player.getBalance() > 300) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(8);
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(6);
			if (temp.getNumBuildings() < 5)
				return true;
		}

		temp = (Site) board.getSquare(3);
		if (player.isGroupOwner(temp) && player.getBalance() > 300) {
			if (temp.getNumBuildings() < 5)
				return true;

			temp = (Site) board.getSquare(1);
			if (temp.getNumBuildings() < 5)
				return true;
		}
		return false;
	}

	private void sleep(int millisec) { // Sleep - pauses the program from certain amount of time
		try {
			Thread.sleep(millisec);                 //1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

}
