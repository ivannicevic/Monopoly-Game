/*
Team: BigMoney
Members: Ivan Nicevic 15373466, Donnacha Lynch 15526177, Laurynas Gudaitis 15761921
*/
package core;
import javax.swing.*;


public class Square extends JPanel
{
    private int id;
    private int type; // 0=Go, 1=Site, 2=Station, 3=Utility, 4=Community, 5=Chance, 6=Jail, 7=Parking, 8=GoToJail, 9=Tax;
    private String fullName, shortName;
    private int colour; // 0=NoColor, 1=Brown, 2=LightBlue, 3=Pink, 4=Orange, 5=Red, 6=Yellow, 7=Green, 8=DarkBlue;
    private int price, housePrice;
    private int[] rents;
    private int houseNum = 0, hotelNum = 0;
    private Player ownedBy = null; // Reference to a player object who owns the square.
    private boolean buyable, mortgaged;

    public Square (int id, int type, String name, String shortName, int colour, int price, int[] rents, int housePrice)
    {
        this.id = id;
        this.type = type;
        this.fullName = name;
        this.shortName = shortName;
        this.colour = colour;
        this.price = price;
        this.rents = rents;
        this.housePrice = housePrice;
        mortgaged = false;
        setBuyable();
    }

    private void setBuyable()
    {
        if(this.type == 1 || this.type == 2 || this.type == 3) this.buyable = true;
        else this.buyable = false;
    }

    public int      getId()         { return id;        }
    public int      getType()       { return type;      }
    public String   getFullName()   { return fullName;  }
    public String   getShortName()  { return shortName; }
    public int      getColour()     { return colour;    }
    public Player   getOwnedBy()    { return ownedBy;   }
    public boolean  getBuyable()    { return buyable;   }
    public int      getPrice()      { return price;     }
    public int      getRent()       { return hotelNum==0 ? rents[houseNum] : rents[5]; }
    public int      getHousePrice() { return housePrice;}
    public int      getHouseNum()   { return houseNum;  }
    public int      getHotelNum()   { return hotelNum;  }
    public boolean  isMortgaged()   { return mortgaged; }

    public void     setOwnedBy(Player player)           { this.ownedBy = player;  }
    public void     setBuyable(Boolean buyable)         { this.buyable = buyable; }
    public void     setHouseNum(int houseNum)           { this.houseNum = houseNum; }
    public void     setHotelNum(int hotelNum)           { this.hotelNum = hotelNum; }
    public void     setMortgaged(Boolean mortgaged)     { this.mortgaged = mortgaged; }

    public String   getTypeString()
    {
        if(type==0) return "Go";
        else if(type==1) return "Site";
        else if(type==2) return "Station";
        else if(type==3) return "Utility";
        else if(type==4) return "Community";
        else if(type==5) return "Chance";
        else if(type==6) return "Jail";
        else if(type==7) return "Parking";
        else if(type==8) return "Go to jail";
        else if(type==9) return "Tax";
        else return "Error in getTypeString().";
    }
}
