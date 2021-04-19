import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;


public class Menu {
	boolean active = false, exit = false; //Default flags for the menu
	String title;
	//Display text strings to be shown in each menu
	String bottomlefttext = "Press UP or DOWN arrows to change selection";
	String bottomrighttext = "Press ENTER to select the marked option";
	//Next array will contain menu options
	String[] options;
	//selection variable keeps track of the selection (keyboard input may change its value)
	int selection = 0;
	//This size is completely arbitrary
	int buttonheight = 40;
	int buttonwidth; //We set it later, as button width depends on string size 
	Menu (String title, String[] options) {
		this.title = title;
		this.options = options;
	}
	
	public void paintMenu(Graphics g, Game game) {
		int width = game.getWidth();
		int height = game.getHeight();
		int fontsize = 20;
		//Font set
		try {
			g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize)); //Retro Gaming should be loaded previously
		} catch(Exception e) {e.printStackTrace();}
		g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize));
		////Image distribution
		//Game logo
		g.drawImage(game.logoimage,(width-game.logoimage.getWidth(game))/2,30,game);
		//Gamemode image (depends on the selected gamemode)
		g.drawImage(game.gameimage,(width-game.gameimage.getWidth(game))/2,40+game.logoimage.getHeight(game),game);
		
		////Button distribution and font metrics change for each label
		int textheight = g.getFontMetrics().getHeight();
		int ystartingbuttons = game.logoimage.getHeight(game)+game.gameimage.getHeight(game)+70+2*textheight; //Once again distances are arbitrary
		int buttondistance = (height - ystartingbuttons - 30)/game.maxoptions; 
		int arc = 3;
		g.setColor(Color.WHITE.darker());
		g.setFont(new Font("Retro Gaming",Font.PLAIN,11));
		g.drawString(game.gamemode + " gamemode",(width-g.getFontMetrics().stringWidth(game.gamemode + " gamemode"))/2,game.logoimage.getHeight(game)+game.gameimage.getHeight(game)+30+10+textheight);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Retro Gaming",Font.ITALIC,fontsize));

		g.drawString(title,(width-g.getFontMetrics().stringWidth(title))/2,game.logoimage.getHeight(game)+game.gameimage.getHeight(game)+60+20+textheight);
		//Standard font reset
		g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize));
		//The selection variable changes in the showMenu method, we need modulo
		selection = modulo(selection,options.length);
		//If options is empty, it means we are in a display menu (no selection available)
		if(options.length == 0) {
			g.setColor(Color.GRAY);
			g.fillRoundRect((width-buttonwidth)/2,ystartingbuttons, buttonwidth, buttonheight, arc,arc); //More fancy than regular rectangles
		}
		else {
			buttonwidth = width/4+20;
			//Triangle selection icons x coordinates (one to the button's left and another one to its right)
			int[] xl = {(width-buttonwidth)/2-30,(width-buttonwidth)/2-10,(width-buttonwidth)/2-30};
			int[] xr = {(width+buttonwidth)/2+30,(width+buttonwidth)/2+10,(width+buttonwidth)/2+30};
			for(int i=0;i<options.length;i++) {
				//Selected option must be treated appart, showing selection icons at both sides and filled with a darker color
				if(selection == i) {
					//Triangle selection icons y coordinates
					int[] y = {ystartingbuttons + buttondistance*i + buttonheight/2 - 6,ystartingbuttons + buttondistance*i + buttonheight/2, ystartingbuttons + buttondistance*i + buttonheight/2+6};
					//Selection button draw
					g.setColor(Color.GRAY.darker());
					g.fillRoundRect((width-buttonwidth)/2, ystartingbuttons + buttondistance*i, buttonwidth, buttonheight,arc,arc);
					//Triangle selection icons draw
					g.setColor(Color.WHITE);
					g.fillPolygon(new Polygon(xl,y,3));
					g.fillPolygon(new Polygon(xr,y,3));
					g.setColor(Color.RED.darker());
					g.drawPolygon(new Polygon(xl,y,3));
					g.drawPolygon(new Polygon(xr,y,3));
				}
				else {
					//Regular button draw
					g.setColor(Color.GRAY);
					g.fillRoundRect((width-buttonwidth)/2, ystartingbuttons + buttondistance*i, buttonwidth, buttonheight,arc,arc);
				}
				//Fill buttons with options text
				int textwidth = g.getFontMetrics().stringWidth(options[i]);
				//Shade (background text)
				g.setColor(Color.BLACK);
				g.drawString(options[i],(width-textwidth)/2 + 1, 1 + ystartingbuttons + buttondistance*i + buttonheight - textheight/2);
				//Bright (foreground text). Needs offset in coordinates to show the effect
				g.setColor(Color.WHITE);
				g.drawString(options[i],(width-textwidth)/2 - 1, -1 + ystartingbuttons + buttondistance*i + buttonheight - textheight/2);
			}
		}
		//Display text strings draw
		game.drawText(bottomlefttext, width, height, g, "bottomleft");
		game.drawText(bottomrighttext, width, height, g, "bottomright");
		//If menu offers an option to go back we show it on screen
		if(game.canback) {
			game.drawText("Press ESC to go back to the main menu", width, height, g, "topcenter");
		}
	}
	
	//Menu mechanics
	public void showMenu(Game game) {
		//changemaxratio helps tracking selection slowly
		int changemaxratio = 5;
		int count = 0;
		//selected keeps trac of the moment when a key was pressed. This way if count - selected > changemaxratio, another option can be selected
		int selected = 0;
		boolean canselect = false;
		int originalselection = selection;
		game.repaint(); //We plot menu for te first time
		while(true) {
			//Update selections and menu displays
			game.updateKeys();
			//If ESC key is pressed and this menu allows to back, we exit the loop
			if((game.esc)&&(game.canback)) {
				selection = originalselection; //If we exit this menu without confirming selection, navigation doesn't affect our previous selection
				exit = true; //We change this menu flag exit to true, so outside it we can observe that we want to exit 
				break;
			}
			if(options.length>1) {
				if(!game.enter) { //Pressing ENTER continuously may trigger selection in subsequent menus. We can only select after releasing ENTER
					canselect = true;
				}
				if(count > 2) { //So not to directly change option while entering the menu
					if((game.enter)&&(canselect)){
						game.repaint();
						canselect = false;
						break;
					}
				}
				//Actual selection update
				if(count-selected > changemaxratio) {
					if(game.up) {
						selection -= 1;
						game.repaint();
						selected = count;
					}
					if(game.down) {
						selection += 1;
						game.repaint();
						selected = count;
					}
				}
			}
			count += 1;
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	//Modulo method, java just returns the remainder of positive numbers, -1 % 3 doesn't result in what we want.
	public int modulo(int x, int y) {
		 return ((x % y) + y) % y;
	}
}