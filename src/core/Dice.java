/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import java.util.Random;


// Dice class represents a single dice. It has only one method that can return and integer between 1 and 6.
public class Dice
{
    private Random dice;

    Dice()
    {
        dice = new Random();
    }

    // This method returns an integer with a random number (values from 1 to 6)
    public int roll()
    {
        return dice.nextInt(6) + 1;
    }
}
