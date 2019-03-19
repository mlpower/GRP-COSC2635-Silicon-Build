
// GRP-COSC2635 Michael Power
// Location class used to represent the location of
// a card on the card table.
public class Location
{
   public Location()
   {
	   
   }
   
   private int xCoord = 500;
   private int yCoord = 380;
   private boolean horizontal = true;
   
   int getXCoord()
   {
	   return xCoord;
   }
   
   int getYCoord()
   {
	   return yCoord;
   }
   
   boolean getHorizontal()
   {
	   return horizontal;
   }
   
   void setXCoord(int xCoord)
   {
	   this.xCoord = xCoord;
   }
   
   void setYCoord(int yCoord)
   {
	   this.yCoord = yCoord;
   }
   
   void setHorizontal(boolean horizontal)
   {
	   this.horizontal = horizontal;
   }
}
