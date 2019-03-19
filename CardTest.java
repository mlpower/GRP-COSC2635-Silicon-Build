import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

// GRP-COSC2635 Michael Power s3162668. An improved version of the CardTest
// class. Left-click on the deck to move a card, right-click to change the
// orientation from horizontal to vertical. When you place the card down
// (left-click again) the flip-side is shown - you can now select another
// card from the deck. Ensure that you copy this with the Card.class and
// Location.class and the images folder containing several images. If you
// are using Eclipse then the images folder should be copied inside the
// bin folder of your project.
public class CardTest extends Application
{
   private Card[] deck;
   private Group cardGroup;
   private int selectedCard;
   private ImageView selectedCardView;
   private Location selectedCardLocation;
   private boolean selectToggle = false;
   
   private Label testLabel = new Label();
   private Label dataLabel = new Label();
   private Label data2Label = new Label();
   
   private int screenWidth = 800;
   private int screenHeight = 600;
   private int screenXInset = 150;
   private int screenYInset = 100;
   private static final int TEST_WIDTH = 300;
   private static final int TEST_HEIGHT = 300;
   
   private int mainDeckXCoord = 600;
   private int mainDeckYCoord = 150;
   
   private double cardXOffset = 45.0;
   private double cardYOffset = 35.0;
   private double cardHorizontalXEdge = 0.0;
   private double cardVerticalYEdge = 13.0;
   private double stageLeftOffset = 120.0;
   private double stageDownOffset = 130.0;
   
   Scene cardScene;
   
   public void start(Stage primaryStage)
   {
	   // Set values to determine window width and height.
	   primaryStage.setMaxHeight(screenHeight);
	   primaryStage.setMaxWidth(screenWidth);
	   primaryStage.setMinHeight(screenHeight);
	   primaryStage.setMinWidth(screenWidth);
	   
	   // A separate window provides output data to help with testing
	   // the interaction between mouse and card.
	   Stage testStage = new Stage();
	   testStage.setTitle("Test Output");
	   testStage.setMaxHeight(TEST_HEIGHT);
	   testStage.setMaxWidth(TEST_WIDTH);
	   testStage.setMinHeight(TEST_HEIGHT);
	   testStage.setMinWidth(TEST_WIDTH);
	   testStage.setScene(testScene());
	   // Place the test stage next to the main window on the right
	   testStage.setX(screenXInset + screenWidth + 20);
	   testStage.setY(screenYInset);
	   
	   StackPane pane = new StackPane();
	   
	   // Attempt to load the background image from file. If successful
	   // bind its size to that of the stage and add it to the StackPane.
	   try
	   {
		   Image background = new Image("images/background.jpg");
		   ImageView backView = new ImageView(background);
	       backView.fitWidthProperty().bind(primaryStage.widthProperty());
	       backView.fitHeightProperty().bind(primaryStage.heightProperty());
		   pane.getChildren().add(backView); 
	   } catch (Exception ex)
	   {
		   System.out.println("Failed to load background image - check" +
	       " file system.");
	   }
	   
	   cardGroup = new Group();
	   // The following command will allow us to plot a card onto
	   // the Group without the Group rearranging the card's location.
	   cardGroup.setManaged(false);
	   pane.getChildren().add(cardGroup);
	   cardScene = new Scene(pane);
	   
	   // A subroutine that sets up a deck of cards - this could be
	   // useful for future testing.
	   deck = initialiseDeck();
	   
	   // Shuffle the cards and initialise the selectedCard variable to point
	   // at the card on the top of the deck
	   shuffleDeck(deck);
	   printCardNames(deck);
	   
	   ImageView[] cardViews = new ImageView[deck.length];
	   Location[] cardLocations = new Location[deck.length];
	   
	   // Display all cards stacked on top of each other
	   for(int i = deck.length - 1; i >= 0; i--)
	   {
		   cardLocations[i] = deck[i].getLocation();
		   cardLocations[i].setXCoord(mainDeckXCoord);
		   cardLocations[i].setYCoord(mainDeckYCoord);
		   cardViews[i] = new ImageView(deck[i].getImage());
		   if (!cardLocations[i].getHorizontal()) cardViews[i].setRotate(90);
		   cardViews[i].setX(cardLocations[i].getXCoord());
		   cardViews[i].setY(cardLocations[i].getYCoord());
		   // Add each card to the cardGroup
		   cardGroup.getChildren().add(cardViews[i]);
	   }
	   
	   
	   
	   // Point to the ImageView and Location on top of the deck
	   selectedCard = 0;
	   selectedCardView = cardViews[selectedCard];
	   selectedCardLocation = cardLocations[selectedCard];
	   
	   // The first card is given an EventHandler
	   selectedCardView.setOnMouseClicked(e ->
	   {
		   // Check if the left button was pressed
		   if(e.getButton().equals(MouseButton.PRIMARY))
		   {
		      handleLeftClick();
		      if(!selectToggle)
		      {
		    	  // Organise EventHandlers for the next cards
		    	  manageMouseClick(cardViews);
		      }
		   }
		   
		   // Check if the right mouse button was pressed and
		   // that the current card has already been clicked on
		   if(selectToggle && e.getButton().equals(MouseButton.SECONDARY))
		   {
			   handleRightClick();
		   }
	   });
	   
	   primaryStage.setTitle("Card Test");
	   primaryStage.setScene(cardScene);
	   primaryStage.setX(screenXInset);
	   primaryStage.setY(screenYInset);
	   
	   // Show each stage/window and ensure primaryStage is active
	   primaryStage.show();
	   testStage.show();
	   primaryStage.toFront();
	   
	   // If the main window is closed, close the test window as well.
	   primaryStage.setOnCloseRequest(e -> testStage.close());
   }
   
   // This method manages the assigning of a new Event Handler to a card view
   void manageMouseClick(ImageView[] cardViews)
   {
      cardViews[selectedCard].setOnMouseClicked(null);
      selectedCard++;
      
      // If we have not reached the end of the deck then make a
      // new Event Handler for the next card.
      if(selectedCard < deck.length)
      {
    	  selectedCardLocation = deck[selectedCard].getLocation();
    	  selectedCardView = cardViews[selectedCard];
    	  
    	  selectedCardView.setOnMouseClicked(e ->
    	  {
    		  if(e.getButton().equals(MouseButton.PRIMARY))
    		  {
    			 handleLeftClick();
    			 if(!selectToggle)
    			 {
    				 // The method is recursively called for each new card
    				 manageMouseClick(cardViews);
    			 }
    			 
    		  }
    		  
    		  // Ensure right mouse button only working if the new
    		  // card has already been clicked on
    		  if(selectToggle && e.getButton().equals(MouseButton.SECONDARY))
    		  {
    		     handleRightClick();
    		  }
    	  });
    	  
      } else
      {
    	  // The last card has been deselected.
    	  dataLabel.setText("No more cards to choose.");
    	  data2Label.setText("");
      }
      
      
   }
   
   // Method for handling the left click of a card
   void handleLeftClick()
   {
      if(selectToggle)
	  {
	  // Card was deselected
	  cardScene.setOnMouseMoved(null);
      dataLabel.setText("Left Button clicked");
      data2Label.setText("Card deselected");
      
      try
      {
    	  // Add a new ImageView representing the flip side of the card
    	  // showing the name of the card.
    	  selectedCardLocation.setXCoord((int)selectedCardView.getX());
    	  selectedCardLocation.setYCoord((int)selectedCardView.getY());
    	  
    	  selectedCardView = new ImageView(new Image("images/card_blank.png"));
    	  if(!selectedCardLocation.getHorizontal())
    	     selectedCardView.setRotate(90);
    	  selectedCardView.setX(selectedCardLocation.getXCoord());
    	  selectedCardView.setY(selectedCardLocation.getYCoord());
    	  // After determining the location and orientation, the ImageView
    	  // can be added to the cardGroup
    	  cardGroup.getChildren().add(selectedCardView);
    	  
    	  // Create a label depicting the name of the card
    	  // to be placed upon the new ImageView.
    	  Label cardLabel = new Label();
    	  cardLabel.setText(deck[selectedCard].getName());
    	  cardLabel.setTranslateX(selectedCardLocation.getXCoord() + 10.0);
    	  cardLabel.setTranslateY(selectedCardLocation.getYCoord() + 30.0);
    	  if(!selectedCardLocation.getHorizontal())
     	     cardLabel.setRotate(90);
    	  cardGroup.getChildren().add(cardLabel);
    	  
      } catch(Exception ex)
      {
    	  System.out.println("Unable to load image from file - check folder.");
      }
		 
	  selectToggle = false;
	  } else
	  {
	     // Card was selected
		 dataLabel.setText("Left Button clicked");
		 data2Label.setText(deck[selectedCard].getName() + " selected");
		 cardGroup.getChildren().remove(selectedCardView);
		 cardGroup.getChildren().add(selectedCardView);
		 // Now create another EventHandler that detects mouse
		 // movement - card coordinates are plotted to follow the
		 // mouse cursor
		 cardScene.setOnMouseMoved(event ->
		 {
		    // Setting limits for card movement to the table.
		    double xVal = event.getX() - cardXOffset;
		    double yVal = event.getY() - cardYOffset;
		    if (selectedCardLocation.getHorizontal())
		    {
			   if (xVal < cardHorizontalXEdge)
			      xVal = cardHorizontalXEdge;
			   if(xVal > screenWidth - stageLeftOffset)
				  xVal = screenWidth - stageLeftOffset;
			      selectedCardView.setX(xVal);
			      if (yVal < 0.0) yVal = 0.0;
			      if(yVal > screenHeight -
				     (stageDownOffset - 13.0))
					 yVal = screenHeight -
					    (stageDownOffset - 13.0);
					 selectedCardView.setY(yVal);
		    	  } else
		    	  {
			         if (xVal < -13.0)
			    	    xVal = -13.0;
					 if(xVal > screenWidth -
					    (stageLeftOffset - 13.0))
					    xVal = screenWidth -
					    (stageLeftOffset - 13.0);
					 selectedCardView.setX(xVal);
			    	 if (yVal < cardVerticalYEdge) yVal =
			    	    cardVerticalYEdge;
					 if(yVal > screenHeight - stageDownOffset)
					    yVal = screenHeight - stageDownOffset;
					 selectedCardView.setY(yVal);
		          }
		     });
	     selectToggle = true;
      }
   }
   
   // The following method encapsulates actions for a
   // right mouse button click
   void handleRightClick()
   {
	   if(selectedCardLocation.getHorizontal())
	   {
		   dataLabel.setText("Right Button clicked");
		   data2Label.setText("Switch to vertical");
		   selectedCardLocation.setHorizontal(false);
		   selectedCardView.setRotate(90);
	   }else
	   {
		   dataLabel.setText("Right Button clicked");
		   data2Label.setText("Switch to horizontal");
		   selectedCardLocation.setHorizontal(true);
		   selectedCardView.setRotate(0);
	   }
   }
   
   // The following is a method that creates a deck of cards that will
   // be useful for testing purposes.
   Card[] initialiseDeck()
   {
	   Card[] deck = new Card[54];
	   
	   // Set up names for each card
	   String[] suits = new String[]{" of diamonds", " of clubs",
	      " of spades", " of hearts"};
	   
	   for(int i = 0; i < 4; i++)
	   {
		  deck[0 + i * 13] = new Card("Ace" + suits[i]);
		  deck[1 + i * 13] = new Card("Two" + suits[i]);
		  deck[2 + i * 13] = new Card("Three" + suits[i]);
		  deck[3 + i * 13] = new Card("Four" + suits[i]);
		  deck[4 + i * 13] = new Card("Five" + suits[i]);
		  deck[5 + i * 13] = new Card("Six" + suits[i]);
		  deck[6 + i * 13] = new Card("Seven" + suits[i]);
		  deck[7 + i * 13] = new Card("Eight" + suits[i]);
		  deck[8 + i * 13] = new Card("Nine" + suits[i]);
		  deck[9 + i * 13] = new Card("Ten" + suits[i]);
		  deck[10 + i * 13] = new Card("Jack" + suits[i]);
		  deck[11 + i * 13] = new Card("Queen" + suits[i]);
		  deck[12 + i * 13] = new Card("King" + suits[i]);
	   }
	   
	   deck[52] = new Card("Joker1");
	   deck[53] = new Card("Joker2");
	   
	   return deck;
   }
   
   // The following method is used to shuffle the deck
   Card[] shuffleDeck(Card[] deck)
   {
	   Card tempCard;
	   for(int i = 0; i < deck.length - 1; i++)
	   {
		   tempCard = deck[i];
		   int randomCard = (int)(Math.random() * deck.length);
		   deck[i] = deck[randomCard];
		   deck[randomCard] = tempCard;
		   
	   }
	   
	   return deck;
   }
   
   // The following method prints out the card names
   void printCardNames(Card[] deck)
   {
	   for(Card card: deck)
	   {
		   System.out.println(card.getName());
	   }
   }
   
   // The following method sets up a test window scene to evaluate events
   // while running the program. Output data can be sent to the labels
   // 'dataLabel' and 'data2Label'
   Scene testScene()
   {
	   StackPane pane = new StackPane();
	   VBox testVBox = new VBox();
	   testVBox.setSpacing(40);
	   testVBox.setAlignment(Pos.CENTER);
	   
	   testLabel.setText("Test Output");
	   testLabel.setStyle("-fx-font: 20 Arial");
	   testVBox.getChildren().add(testLabel);
	   dataLabel.setText("data1");
	   dataLabel.setStyle("-fx-font: 20 Arial");
	   testVBox.getChildren().add(dataLabel);
	   data2Label.setText("data2");
	   data2Label.setStyle("-fx-font: 20 Arial");
	   testVBox.getChildren().add(data2Label);
	   
	   pane.getChildren().add(testVBox);
	   Scene scene = new Scene(pane);
	   scene.setFill(Color.AZURE);
	   
	   return scene;
   }
   
   public static void main(String[] args)
   {
	   Application.launch(args);
   }
}