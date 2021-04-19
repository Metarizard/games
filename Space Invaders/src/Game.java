import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import MovingObjects.Alien;
import MovingObjects.Bullet;
import MovingObjects.Spaceship;

public class Game extends JPanel {

	private static final long serialVersionUID = 1L; //This way a warning isn't shown
	
	////Numeric variables declaration and/or initialization
	//Menu variables
	int maxoptions = 4;
	//Mechanics variables
	int round, highscore, lives;
	int deathtime = 100, countdead = 0, score = 0, initiallives = 3, alienspeedlevel = 0;
	double fireprobability = 0.998;
	Random rnd = new Random();
	//Object display variables
	int aliencollisionx, aliencollisiony, playercollisionx, playercollisiony, bulletcollisionx, bulletcollisiony, deathx, deathy;
	int alienexplosiontime = 0, bulletexplosiontime = 0, playerexplosiontime = 0, explosionanimationtime = 10,  fontsize = 20;
	int nrow = 5, ncol = 10, margin = 30, countmove = 0, acountbullet = 0, pcountbullet = 0, ROF = 10, pmaxbullets = 1, amaxbullets = 3, invasiony = 550;
	////Boolean variables declaration and/or initialization
	//Relevant keys
	boolean up = false, down = false, left = false, right = false, space = false, esc = false, enter = false;
	//Music flag
	boolean music = true;
	//Menu flags
	boolean canback = false;
	//Gameplay flags
	boolean  playerhit = false, playstate = false, endgame = false, nextround = false, mysteryright;
	//Explosion animation flags
	boolean bulletcollision = false, aliencollision = false, playercollision = false;
	////Menus
	//Options
	String[] mainoptions = {"Play","Settings","How to play","Credits"};
	String[] settingsoptions = {"Music","Gamemode"};
	String[] musicoptions = {"Music ON","Music OFF"};
	String[] gamemodeoptions = {"Classic","AOT","PKMN","Meme"};
	String[] pauseoptions = {"Resume", "Exit to main menu"};
	String gamemode;
	//Menu declaration and initialization
	Menu mainmenu = new Menu("Main menu",mainoptions);
	Menu settingsmenu = new Menu("Settings menu",settingsoptions);
	Menu musicmenu = new Menu("Music menu",musicoptions);
	Menu gamemodemenu = new Menu("Gamemode menu",gamemodeoptions);
	Menu pausemenu = new Menu("Pause",pauseoptions);
	boolean howtoplay = false, credits = false;
	////Images declaration
	public Image logoimage, gameimage, heartimage, lifeimage, playerimage, alienbullet, playerbullet, bossbullet, explosion;
	public Image[] alienimage = new Image[5]; //bot,mid,top,mystery,boss
	public Image[] explosionanimation = new Image[explosionanimationtime]; 
	////Music declaration
	Music bgmusic, explosionsound, mysterysound, lasersound, deathsound;
	////Game objects
	Spaceship player;
	ArrayList<Alien> alien = new ArrayList<Alien>();
	ArrayList<Alien> mystery = new ArrayList<Alien>();
	Alien boss;
	ArrayList<Bullet> alienbullets = new ArrayList<Bullet>();
	Shield[] shields = new Shield[4];
	////Reader for the Highscore.txt file that will be read/overwritten afterwards
	BufferedReader br;
	////GUI needed for interaction with the player
	GUI mainframe;
	//Constructor
	public Game(GUI mainframe) {
		this.mainframe = mainframe;
		try {
			gamemode = gamemodeoptions[0]; //Default gamemode is Classic
			this.logoimage = ImageIO.read(new File("Graphics/Logos/SpaceInvaders.png"));
			//Custom font load
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/RetroGaming.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
		} catch(IOException|FontFormatException e) {e.printStackTrace();} 
		logoimage = imageProportionalResize(logoimage,500,200);
	}
	
	void loadFiles() {
		try {
			//Music
			bgmusic = new Music(new File("Music/"+gamemode+"/bg.wav"));
			mysterysound = new Music(new File("Music/"+gamemode+"/mystery.wav"));
			explosionsound = new Music(new File("Music/"+gamemode+"/explosion.wav"));
			deathsound = new Music(new File("Music/"+gamemode+"/death.wav"));
			lasersound = new Music(new File("Music/"+gamemode+"/laser.wav"));
			//Images
			heartimage = ImageIO.read(new File("Graphics/Icons/heart.png"));
			gameimage = ImageIO.read(new File("Graphics/Logos/"+gamemode+".png"));
			playerimage = ImageIO.read(new File("Graphics/Images/"+gamemode+"/ship.png"));
			alienimage[0] = ImageIO.read(new File("Graphics/Images/"+gamemode+"/bot.png"));
			alienimage[1] = ImageIO.read(new File("Graphics/Images/"+gamemode+"/mid.png"));
			alienimage[2] = ImageIO.read(new File("Graphics/Images/"+gamemode+"/top.png"));
			alienimage[3] = ImageIO.read(new File("Graphics/Images/"+gamemode+"/mystery.png"));
			alienimage[4] = ImageIO.read(new File("Graphics/Images/"+gamemode+"/boss.png"));
			playerbullet = ImageIO.read(new File("Graphics/Images/"+gamemode+"/playerbullet.png"));
			alienbullet = ImageIO.read(new File("Graphics/Images/"+gamemode+"/alienbullet.png"));
			bossbullet = ImageIO.read(new File("Graphics/Images/"+gamemode+"/bossbullet.png"));
			explosion = ImageIO.read(new File("Graphics/Images/explosion.png"));
			//Highscore read and update
			br = new BufferedReader(new FileReader("Text/Highscore.txt"));
			highscore = Integer.parseInt(br.readLine());
			br.close();
		} catch(IOException e) {e.printStackTrace();}
	}
	
	void initialization() {
		round = 1;
		loadFiles();
		//Image resize so they appear similarly in different gamemodes
		playerimage = imageProportionalResize(playerimage,60,60);
		gameimage = imageProportionalResize(gameimage,200,40);
		lifeimage = imageProportionalResize(playerimage,30,15);
		heartimage = imageProportionalResize(heartimage,20,20);
		for(int i=0;i<alienimage.length-2;i++) {
			alienimage[i] = imageProportionalResize(alienimage[i],35,35);
		}
		alienimage[alienimage.length-2] = imageProportionalResize(alienimage[alienimage.length-2],50,35);
		alienimage[alienimage.length-1] = imageProportionalResize(alienimage[alienimage.length-1],200,200);
		playerbullet = imageProportionalResize(playerbullet,10,30);
		alienbullet = imageProportionalResize(alienbullet,10,30);
		bossbullet = imageProportionalResize(bossbullet,70,70);
		for(int i=0;i<explosionanimationtime;i++) {
			explosionanimation[i] = imageProportionalResize(explosion,5*(i+1),5*(i+1)); //The animations shows the explosion becoming bigger
		}
		if(music) { //If this flag is true, the user wants background music to play
			bgmusic.playBackgroundMusic();
		}		
		componentReset();
	}
	
	void componentReset() {
		pmaxbullets = 1; //Making sure the player can fire bullets after ending of games
		if(round == 1) {
			//Initial position of spaceship is centered under the invasion line
			player = new Spaceship((this.getWidth()-playerimage.getWidth(this))/2,invasiony+20,playerimage);
			mysteryright = true;
		}
		acountbullet = 0; //Making sure this variable is reset whenever the boss dies, to reset fire rate mechanic
		bulletcollision = aliencollision = playercollision = false; //Making no previous animations affect new components
		//Character dimension definition, top alien as reference
		int charwidth = alienimage[2].getWidth(this);
		int charheight = alienimage[2].getHeight(this);
		//Remove aliens if there were any
		alien.removeAll(alien);
		alienbullets.removeAll(alienbullets);
		mystery.removeAll(mystery);
		//Reset speed level
		alienspeedlevel = 0;
		//Stop mysterysound if it was running
		if(mysterysound.clip.isRunning()) {
			mysterysound.clip.stop();
			mysterysound.clip.setMicrosecondPosition(0);
		}
		//Every 5 rounds a boss alien appears. The other rounds consist of numerous aliens descending and shooting
		if(round%5 != 1) {
			for(int i=0;i<nrow;i++) {
				for(int j=0;j<ncol;j++) {
					alien.add(new Alien(margin+190+(charwidth+10)*j,300-(charheight+10)*i,1,charheight/2+3*round,i,j,alienimage,i==0,1)); //Only aliens in the bottom row can fire initially, they have only 1 life and they descend faster as rounds pass 
				}
			}
		}
		else {
			alien.add(new Alien((this.getWidth()-alienimage[alienimage.length-1].getWidth(this))/2,invasiony-300-alienimage[alienimage.length-1].getHeight(this),4,30,12,0,alienimage,true,6)); //Boss alien moves faster and has 6 lives
		}
		//Shield generation
		for(int i=0;i<shields.length;i++) {
				int rows = 4;
				int cols = 10;
				int rwidth = 10;
				int rheight = 20;
				shields[i] = new Shield(this.getWidth()/8*(2*i+1)-cols*rwidth/2,450,rows,cols,rwidth,rheight);
		}
	}

	void run() {
		//Initialization
		initialization();
		//Menu navigation
		while(true) {
			canback = false;
			mainmenu.active = true;
			mainmenu.showMenu(this);
			mainmenu.active = false;
			canback = true;
			switch(mainmenu.selection) {
				case 0:
					canback = false;
					//Playing the game
					alienspeedlevel = 0;
					play();
					break;
				case 1:
					settingsmenu.active = true;
					settingsmenu.showMenu(this);
					settingsmenu.active = false;
					if(settingsmenu.exit) {
						settingsmenu.exit = false;
						break;
					}
					else{
						switch(settingsmenu.selection) {
							case 0:
								musicmenu.active = true;
								musicmenu.showMenu(this);
								musicmenu.active = false;
								if(musicmenu.exit) {
									musicmenu.exit = false;
									break;
								}
								else{
									switch(musicmenu.selection) {
										case 0:
											music = true;
											if(!bgmusic.clip.isRunning()) {
												bgmusic.playBackgroundMusic();
											}
											break;
										case 1:
											music = false;
											if(bgmusic.clip.isRunning()) {
												bgmusic.clip.stop();
												bgmusic.clip.setMicrosecondPosition(0);
											}
											break;
									}
								}
								break;
							case 1:
								gamemodemenu.active = true;
								gamemodemenu.showMenu(this);
								gamemodemenu.active = false;
								if(gamemodemenu.exit) {
									gamemodemenu.exit = false;
									break;
								}
								else{
									if(!(gamemode == gamemodeoptions[gamemodemenu.selection])) {
										gamemode = gamemodeoptions[gamemodemenu.selection];
										System.out.println(gamemode + " selected");
										bgmusic.clip.stop();
										initialization();
									}
								}
								break;
						}
					}
					break;
				case 2:
					System.out.println("How to play selected");
					howtoplay = true;
					repaint();
					try {
						Thread.sleep(300);
					}
					catch(InterruptedException e) {e.printStackTrace();}
					while(howtoplay) {
						updateKeys();
						if(esc) {
							howtoplay = false;
						}
						repaint();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {e.printStackTrace();}
					}
					break;
				case 3:
					System.out.println("Credits selected");
					credits = true;
					repaint();
					try {
						Thread.sleep(300);
					}
					catch(InterruptedException e) {e.printStackTrace();}
					while(credits) {
						updateKeys();
						if(esc) {
							credits = false;
						}
						repaint();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {e.printStackTrace();}
					}
					break;
	 		}
		}	
	}
//	
	void play() {
		//This flag keeps track of when to show a next round screen
		nextround = true;
		repaint();
		//Wait for the player to read this next round screen
		try {
			Thread.sleep(3000);
		}
		catch(InterruptedException e) {e.printStackTrace();}
		//Play begins and playstate activates, round screen fades 
		nextround = false;
		playstate = true;
		while(true) {
			updateKeys();
			if(exit()) { //If the player decides to exit the game before leaving, just reset components, round, countmove and score
				round = 1;
				componentReset();
				alienspeedlevel = 0;
				score = 0;
				countmove = 0;
				break;
			}
			//endgame is triggered when aliens invade
			if(endgame||(player.lives == 0)) {
				//If game ends, either because aliens invade or because the player lost all their lives
				playstate = false;
				//State is endgame,plot endgame screen, appart from reseting components
				endgame = true;
				mystery.removeAll(mystery);
				componentReset();
				repaint();
				try {
					Thread.sleep(300);
				}
				catch(InterruptedException e) {e.printStackTrace();}
				while(true) {
					updateKeys();
					if(esc) {
						break;
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {e.printStackTrace();}
				}

				//Highscore update
				if(score > highscore) {
					try{
						PrintStream p = new PrintStream("Text/Highscore.txt");
						p.println(score);
						p.close();
						br = new BufferedReader(new FileReader("Text/Highscore.txt"));
						highscore = Integer.parseInt(br.readLine());
						br.close();
					}
					catch(IOException e) {e.printStackTrace();}
				}
				round = 1;
				alienspeedlevel = 0;
				countmove = 0;
				score = 0;
				endgame = false;
				//endgame phase ends and then (break) exit play()
				break;
			}
			//If game hasn't ended, update its changes
			updateGameChanges();
			makeMovements();
			countmove += 1;
			//After movements, check collisions between different objects
			collisionDetection();
			//alien size == 0  means that no aliens are left, so the round ends
			if(alien.size() == 0) {
				round += 1;
				alienbullets.removeAll(alienbullets);
				player.bullets.removeAll(player.bullets);
				//playstate ends shortly to show the nextround screen
				playstate = false;
				nextround = true;
				//New components for the next round
				componentReset();
				repaint();
				try {
					Thread.sleep(3000);
				}
				catch(InterruptedException e) {e.printStackTrace();}
				playstate = true;
				nextround = false;
			}
			//If a player was hit by an enemy bullet, playerhit triggers
			if(playerhit) {
				//Save death position
				deathx = player.x;
				deathy = player.y;
				//This is kinda ugly, but I didn't come up with anything better
				//Set the player position to the bottom right corner, unseen 
				pmaxbullets = 0;
				player.y = this.getHeight();
				player.x = this.getWidth();
				playerhit = false;
				countdead = countmove;
			}
			//To make sure that while the player is dead, no aliens fire bullets
			if(pmaxbullets == 0) {
				alienbullets.removeAll(alienbullets);
			}
			//After a delay, the player revives
			if((countmove - countdead > deathtime)&&(countdead!=0)) {
				player.y = deathy;
				player.x = deathx;
				pmaxbullets = 1;
				countdead = 0;
			}
			repaint();
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	

	public void makeMovements() {
		Alien deletemystery = null;
		//Check if mystery aliens are out of bounds and add them to delete list if they are (there will be a myster alien at a time)
		for(Alien m:mystery) {
			m.moveX();
			if((m.x < 0)||(m.x > this.getWidth() - margin)) {
				deletemystery = m;
			}
		}
		//Object movement plain and simple
		for(Bullet b:player.bullets){
			b.moveY();
		}
		for(Alien a:alien) {
			a.moveX();
		}
		//Check if aliens have arrived to the border of the screen. Change speed sign and move them down in the screen if the border is reached
		for(Alien a:alien) {
			if((a.x < margin)||(a.x + a.width > this.getWidth() - margin)) {
				//I
				for(Alien a2:alien) {
					a2.vx = - a2.vx;
					a2.moveY();
					//If when going down invasion line is reached, set engame true
					if(a2.y + a2.height>invasiony) {
						endgame = true;
						break;
					}
				}
				break;
			}
		}
		//Remove mystery alien
		mystery.remove(deletemystery);
		if(!endgame) {
			//If game hasn't ended, move bullets and player
			for(Bullet b:alienbullets){
				b.moveY();
			}
			player.moveX();
		}
	}

	void paintGame(Graphics g) {
		//Display Strings intialization
		String toplefttext = "Score: " + score;
		String toprighttext = "Highscore: " + highscore;
		String bottomrighttext = "Press ESC to access pause menu";
		String bottomlefttext = "Lives: ";
		String topcentertext = "Round " + round;
		//invasion line draw
		g.setColor(Color.GRAY.darker());
		g.drawLine(0, invasiony, this.getWidth(), invasiony);
		//Alien plot. If alien is a boss, then show remaining health as hearts
		for(Alien a:alien) { 
			a.plot(g);
			if(a.type == "boss"){
				g.setColor(Color.WHITE);
				g.setFont(new Font("Retro Gaming",Font.PLAIN,11));
				g.drawString("Boss health",(this.getWidth()-g.getFontMetrics().stringWidth("Boss health"))/2,invasiony+70);
				for(int i=0;i<a.lives;i++) {
					g.drawImage(heartimage,(this.getWidth()-(3-i)*(heartimage.getWidth(this)+30))/2,invasiony + 80,this);
				}
			}
		}
		//Bullet draw
		for(Bullet b:alienbullets){
			b.plot(g);
		}
		for(Bullet b:player.bullets) {
			b.plot(g);
		}
		//Spaceship draw
		player.plot(g);
		//Shields draw
		for(int i=0;i<shields.length;i++) {
			shields[i].plot(g);
		}
		//Mystery alien draw
		for(Alien m:mystery) {
			m.plot(g);
		}
		//Explosion animation, depending on object exploding (alien, bullet or player spaceship)
		if(aliencollision) {
			g.drawImage(explosion, aliencollisionx+(50-alienexplosiontime*5)/2, aliencollisiony+(50-alienexplosiontime*5)/2,5*(alienexplosiontime+1),5*(alienexplosiontime+1),this);
			alienexplosiontime += 1;
		}
		if(alienexplosiontime > explosionanimationtime-1) {
			alienexplosiontime = 0;
			aliencollision = false;
		}
		if(bulletcollision) {
			g.drawImage(explosion, bulletcollisionx+(50-bulletexplosiontime*5)/2, bulletcollisiony+(50-bulletexplosiontime*5)/2,5*(bulletexplosiontime+1),5*(bulletexplosiontime+1),this);
			bulletexplosiontime += 1;
		}
		if(bulletexplosiontime > explosionanimationtime-1) {
			bulletexplosiontime = 0;
			bulletcollision = false;
		}
		if(playercollision) {
			g.drawImage(explosion, playercollisionx+(50-playerexplosiontime*5)/2, playercollisiony+(50-playerexplosiontime*5)/2,5*(playerexplosiontime+1),5*(playerexplosiontime+1),this);
			playerexplosiontime += 1;
		}
		if(playerexplosiontime > explosionanimationtime-1) {
			playerexplosiontime = 0;
			playercollision = false;
		}
		//Display Strings draw
		drawText(toplefttext, this.getWidth(), this.getHeight(), g, "topleft");
		drawText(toprighttext, this.getWidth(), this.getHeight(), g, "topright");
		drawText(bottomrighttext, this.getWidth(), this.getHeight(), g, "bottomright");
		drawText(bottomlefttext, this.getWidth(), this.getHeight(), g, "bottomleft");
		drawText(topcentertext, this.getWidth(), this.getHeight(), g, "topcenter");
		//Remaining player lives draw
		for(int i=0;i<player.lives;i++) {
			g.drawImage(lifeimage,g.getFontMetrics().stringWidth(bottomlefttext)+(lifeimage.getWidth(this)+10)*i+20,this.getHeight()-6-lifeimage.getHeight(this),this);
		}
	}

	//Key update from keyboard input listened in mainframe (GUI)
	public void updateKeys() {
		up = mainframe.keys[KeyEvent.VK_UP];
		down = mainframe.keys[KeyEvent.VK_DOWN];
		left = mainframe.keys[KeyEvent.VK_LEFT];
		right = mainframe.keys[KeyEvent.VK_RIGHT];
		space = mainframe.keys[KeyEvent.VK_SPACE];
		esc = mainframe.keys[KeyEvent.VK_ESCAPE];
		enter = mainframe.keys[KeyEvent.VK_ENTER];
	}
	
	//Pause menu when esc is pressed
	public boolean exit() {
		if(esc == true) {
			playstate = false;
			//Enter pause state
			pausemenu.active = true;
			pausemenu.showMenu(this);
			pausemenu.active = false;
			//If resume is selected, continue playing (playstate true), and exit is false
			if(pausemenu.options[pausemenu.selection] == pausemenu.options[0]) {
				playstate = true;
				return false;
			}
			else {
				//Exit was selected so return exit true
				return true;
			}
		}
		//No ESC key was pressed
		else {
			return false;
		}
	}
	
	public void updateGameChanges() {
		//Changes due to keyboard input
		if(left == true) {
			player.vx = -5;
		}
		else {
			if(right == true){
				player.vx = 5;
			}
			else {
				player.vx = 0;
			}
		}
		if(space == true){
			/*
			if(countmove-pcountbullet >= ROF) {
				player.bullets.add(new Bullet(player.x+player.width/2,player.y,0,-10));
				pcountbullet = countmove;
			} // By rate of fire. This mode allows the player to fire bullets at a specified rate (ROF) if SPACE is pressed continuously. Should change pmaxbullets mechanic! (this can be combined with pmaxbullets mechanic)
			*/
			if(player.bullets.size()<pmaxbullets) {
				lasersound.playMusic();
				player.bullets.add(new Bullet(player.x+player.width/2-6,player.y,0,-10,playerbullet));
			}
		}
		//Alien speed increase as fewer aliens remain. Increase speed level as reaching certain alien size thresholds till one unique alien remains
		if((alien.size() == nrow*ncol/2)&&(alienspeedlevel == 0)) {
			for(Alien a:alien) {
				if(a.vx<0) {
					a.vx -= 1;
				}
				else {
					a.vx += 1;
				}
			}
			alienspeedlevel += 1;
		}
		if((alien.size() == 10)&&(alienspeedlevel == 1)) {
			for(Alien a:alien) {
				if(a.vx<0) {
					a.vx -= 2;
				}
				else {
					a.vx += 2;
				}
			}
			alienspeedlevel += 1;
		}
		if((alien.size() == 4)&&(alienspeedlevel == 2)) {
			for(Alien a:alien) {
				if(a.vx<0) {
					a.vx -= 2;
				}
				else {
					a.vx += 2;
				}
			}
			alienspeedlevel += 1;
		}
		if((alien.size() == 2)&&(alienspeedlevel == 3)) {
			for(Alien a:alien) {
				if(a.vx<0) {
					a.vx -= 2;
				}
				else {
					a.vx += 2;
				}
			}
			alienspeedlevel += 1;
		}
		if((alien.size() == 1)&&(alienspeedlevel == 4)) {
			for(Alien a:alien) {
				if(a.vx<0) {
					a.vx -= 1;
				}
				else {
					a.vx += 1;
				}
			}
			alienspeedlevel += 1;
		}
		//Mystery alien generation each 3000 frames (initial mystery alien at 1000 frames)
		if(countmove%3000 == 1000) {
			//Moving from left to right
			if(mysteryright) {
				mystery.add(new Alien(margin,70,4,0,-1,0,alienimage,false,1));
				mysteryright = false;
			}
			//Moving from right to left
			else {
				mystery.add(new Alien(this.getWidth()-alienimage[alienimage.length-2].getWidth(this),70,-4,0,-1,0,alienimage,false,1));
				mysteryright = true;
			}
			mysterysound.playMusic();
		}
		//Alienbullet update
		for(Alien a:alien) {
			if(a.canfire) {
				if(a.type == "boss") {
					if(countmove-acountbullet >= ROF) {
						alienbullets.add(new Bullet(a.x+a.width/2,a.y+3*a.height/4,0,5,bossbullet));
						acountbullet = countmove;
					} // By rate of fire. Fires bullets at a fixed rate (ROF)
				}
				else {
					//Aliens have a slight chance to fire a bullet. Also, there can't be more than "amaxbullets" alien bullets on screen (similar to pmaxbullets)
					if((rnd.nextFloat() > fireprobability)&&(alienbullets.size()<amaxbullets)) {
						alienbullets.add(new Bullet(a.x+a.width/2,a.y+a.height,0,5,alienbullet));
					}
				}
			}
		}
	}
	
	
	public void collisionDetection() {
		//As ArrayLists, elements can't be deleted while looping through them
		ArrayList<Alien> deletealiens = new ArrayList<Alien>();
		ArrayList<Bullet> deleteplayerbullets = new ArrayList<Bullet>();
		ArrayList<Bullet> deletealienbullets = new ArrayList<Bullet>();
		ArrayList<Rectangle> deleteshieldrectangle = new ArrayList<Rectangle>();
		Alien deletemystery = null;
		////Simple but tedious collision check for every object
		//Player bullet collisions
		for(Bullet bp:player.bullets) {
			//With mystery aliens
			for(Alien m:mystery) {
				if(bp.hitbox.intersects(m.hitbox)) {
					explosionsound.playMusic();
					deleteplayerbullets.add(bp);
					deletemystery = m;
					aliencollision = true;
					aliencollisionx = m.x;
					aliencollisiony = m.y;
					//Mystery aliens are treated separately
					score += 300;
				}
			}
			//Out of bounds
			if(bp.y - bp.height < 10) {
				deleteplayerbullets.add(bp);
			}
			//With shields
			for(int i=0;i<shields.length;i++) {
				for(Rectangle rect:shields[i].rectanglestructure) {
					if(bp.hitbox.intersects(rect)) {
						deleteplayerbullets.add(bp);
						deleteshieldrectangle.add(rect);
					}
				}
			}
			//With normal aliens (and boss)
			for(Alien a:alien) {
				if(a.hitbox.intersects(bp.hitbox)) {
					if(a.lives == 1) {
						explosionsound.playMusic();
						deleteplayerbullets.add(bp);
						deletealiens.add(a);
						aliencollision = true;
						aliencollisionx = a.x;
						aliencollisiony = a.y;
						switch(a.type) {
							case "bot":
								score += 10;
								break;
							case "mid":
								score += 20;
								break;
							case "top":
								score += 30;
								break;
							case "boss":
								score += 1000;
								break;
						}
					}
					else {
						explosionsound.playMusic();
						deleteplayerbullets.add(bp);
						a.lives -= 1;
					}
					for(Alien c:alien) {
						if((c.col == a.col)&&(c.row != a.row)) {
							c.canfire = true;
							break;
						}
					}
				}
			}
			//With alien bullets
			for(Bullet ba:alienbullets) {
				if(bp.hitbox.intersects(ba.hitbox)) {
					deleteplayerbullets.add(bp);
					deletealienbullets.add(ba);
					bulletcollision = true;
					bulletcollisionx = bp.x;
					bulletcollisiony = bp.y;
				}
			}
		}
		
		//Alien collision with shields (deletes shields)
		for(Alien a:alien) {
			for(int i=0;i<shields.length;i++) {
				for(Rectangle rect:shields[i].rectanglestructure) {
					if(a.hitbox.intersects(rect)) {
						deleteshieldrectangle.add(rect);
					}
				}
			}
		}
		//Alien bullets collision 
		for(Bullet b:alienbullets) {
			//With shields
			for(int i=0;i<shields.length;i++) {
				for(Rectangle rect:shields[i].rectanglestructure) {
					if(b.hitbox.intersects(rect)) {
						deletealienbullets.add(b);
						deleteshieldrectangle.add(rect);
					}
				}
			}
			//Out of bounds
			if(b.y + b.height > this.getHeight()) {
				deletealienbullets.add(b);
			}
			//With player
			if(player.hitbox.intersects(b.hitbox)) {
				deathsound.playMusic();
				//Triggers animation
				playercollision = true;
				playercollisionx = player.x;
				playercollisiony = player.y;
				//Triggers death
				playerhit = true;
				countdead = countmove;
				deletealienbullets.add(b);
				//If hit, a life is lost
				player.lives -= 1;
			}
		}
		//Remove objects that have collided (or are out of bounds)
		player.bullets.removeAll(deleteplayerbullets);
		alienbullets.removeAll(deletealienbullets);
		alien.removeAll(deletealiens);
		mystery.remove(deletemystery);
		for(int i=0;i<shields.length;i++) {
			shields[i].rectanglestructure.removeAll(deleteshieldrectangle);
		}
	}
	
	public void paintComponent(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();
		int arc = 3;
		super.paintComponent(g);
		//Simple update. If a menu/state is active, then draw its contents (code is written so there is no more than one menu/state active at a time)
		if(mainmenu.active) {
			mainmenu.paintMenu(g,this);
		}
		if(settingsmenu.active) {
			settingsmenu.paintMenu(g,this);
		}
		if(musicmenu.active) {
			musicmenu.paintMenu(g,this);
		}
		if(gamemodemenu.active) {
			gamemodemenu.paintMenu(g,this);
		}
		if(howtoplay) {
			int fontsize = 14;
			String[] text = new String[18];
			text[0] = "Welcome to Space Invaders. The objective of the game is to destroy as many aliens as";
			text[1] = "possible. To do so, you will control a spaceship with your keyboard's arrow keys.";
			text[2] = "By pressing the SPACE key your spaceship will fire a bullet aimed at the aliens";
			text[3] = "(there can only be one of your bullets on screen every time). These creatures will";
			text[4] = "try to invade your planet by firing bullets at you. If an enemy bullet hits you";
			text[5] = "the spaceship will disappear for a few moments and then it will respawn where it exploded.";
			text[6] = "Aliens are respectful and they will stop firing when you are dead. You have 3 lives,";
			text[7] = "so if you get hit 3 times, you lose. In addition, if any alien crosses the the gray line";
			text[8] = "over your spaceship the alien invasion will be successful and you will lose too. If you";
			text[9] = "destroy all aliens appearing on screen, another round will begin, full of new enemies to";
			text[10] = "defeat. Every 5 rounds, you will encounter an alien boss, with special attacks and harder";
			text[11] = "to kill. Each alien you destroy gives you points, depending on its type. Also, aliens";
			text[12] = "move faster when few remain. Watch out because they can move pretty fast!";
			text[13] = "To help you in your journey, the (ISC) Intenational Space Community adds 4 shields";
			text[14] = "each round so you can cover from the deadly alien bullets. Be careful because you can";
			text[15] = "destroy these shields with your bullets!";
			text[16] = "In the settings menu, you will be able to select a gamemode: 'Classic', 'Attack on Titan',";
			text[17] = "'Pokémon', and 'Meme'.";
			int textheight = g.getFontMetrics().getHeight();
			g.setColor(Color.WHITE);
			g.setFont(new Font("Retro Gaming",Font.ITALIC,fontsize));
			g.drawString("How to play",(width-g.getFontMetrics().stringWidth("How to play"))/2,100);
			g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize));
			g.setColor(Color.GRAY);
			g.fillRoundRect(margin,120, width-2*margin,height-200,arc,arc); //More fancy than regular rectangles
			for(int i=0;i<text.length;i++) {
				g.setColor(Color.BLACK);
				g.drawString(text[i],(width-g.getFontMetrics().stringWidth(text[i]))/2 , 150 + (textheight+8)*i);
				g.setColor(Color.WHITE);
				g.drawString(text[i],(width-g.getFontMetrics().stringWidth(text[i]))/2 - 1, 150 - 1 + (textheight+8)*i);
			}
			drawText("Press ESC to go back to the main menu", width, height, g, "bottomcenter");
		}
		if(credits) {
			int fontsize = 18;
			String[] text = new String[12];
			text[0] = "Music";
			text[1] = "'Classic': Tomohiro Nishikado   'Attack on Titan': Hiroyuki Sawano";
			text[2] = "'Pokémon': Junichi Masuda    'Meme': Ryuichi Katsumata";
			text[3] = "Images";
			text[4] = "'Classic': Tomohiro Nishikado   'Attack on Titan': Hajime Isayama";
			text[5] = "'Pokémon': Ken Sugimori     'Meme': Metarizard & Co. / B. Riemann";
			text[6] = "Original idea: Tomohiro Nishikado";
			text[7] = "Game developer: Metarizard (MTZD)";
			text[8] = "No rights owned by this project's creator. All rights belong to";
			text[9] = "Nintendo, Kodansha & Taito.";
			text[10] = "";
			text[11] = "2021";
			int textheight = g.getFontMetrics().getHeight();
			g.setColor(Color.WHITE);
			g.setFont(new Font("Retro Gaming",Font.ITALIC,fontsize));
			g.drawString("Credits",(width-g.getFontMetrics().stringWidth("How to play"))/2,100);
			g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize));
			g.setColor(Color.GRAY);
			g.fillRoundRect(margin,120, width-2*margin,height-200,arc,arc); //More fancy than regular rectangles
			for(int i=0;i<text.length;i++) {
				g.setColor(Color.BLACK);
				g.drawString(text[i],(width-g.getFontMetrics().stringWidth(text[i]))/2 , 150 + (textheight+20)*i);
				g.setColor(Color.WHITE);
				g.drawString(text[i],(width-g.getFontMetrics().stringWidth(text[i]))/2 - 1, 150 - 1 + (textheight+20)*i);
			}
			drawText("Press ESC to go back to the main menu", width, height, g, "bottomcenter");
		}
		if(pausemenu.active) {
			pausemenu.paintMenu(g,this);
		}
		if(playstate) {
			paintGame(g);
		}
		//Nextround screen 
		if(nextround) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Retro Gaming",Font.PLAIN,30));
			String roundtext1 = "GET READY!";
			String roundtext2 = "ROUND "+round+" IS ABOUT TO BEGIN!";
			g.drawString(roundtext1, (this.getWidth()-g.getFontMetrics().stringWidth(roundtext1))/2, (this.getHeight()-2*g.getFontMetrics().getHeight())/2);
			g.drawString(roundtext2, (this.getWidth()-g.getFontMetrics().stringWidth(roundtext2))/2, (this.getHeight()+2*g.getFontMetrics().getHeight())/2);
		}
		//Endgame screen
		if(endgame) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Retro Gaming",Font.PLAIN,20));
			String endgametext1 = "You lost!";
			g.drawString(endgametext1, (this.getWidth()-g.getFontMetrics().stringWidth(endgametext1))/2, (this.getHeight()-6*g.getFontMetrics().getHeight())/2);
			String endgametext2 = "You scored "+score+" points in " + round + " round(s).";
			g.drawString(endgametext2, (this.getWidth()-g.getFontMetrics().stringWidth(endgametext2))/2, (this.getHeight()-2*g.getFontMetrics().getHeight())/2);
			String endgametext3 ="Previous highscore was "+highscore+" points.";
			g.drawString(endgametext3, (this.getWidth()-g.getFontMetrics().stringWidth(endgametext3))/2, (this.getHeight()+2*g.getFontMetrics().getHeight())/2);
			String endgametext4;
			if(score > highscore) {
				endgametext4 = "You have set a new record! Congrats!";
			}
			else {
				endgametext4 = "You couldn't beat previous highscore! Keep trying!";
			}
			g.drawString(endgametext4, (this.getWidth()-g.getFontMetrics().stringWidth(endgametext4))/2, (this.getHeight()+6*g.getFontMetrics().getHeight())/2);

			drawText("Press ESC to go back to the main menu",this.getWidth(),this.getHeight(),g,"topcenter");
		}
		setBackground(Color.BLACK);
	}
	
	//Resize image with proportional scaling. Smooth method to preserve maximum quality
	public Image imageProportionalResize(Image image, int newwidth, int newheight) {
		int imagewidth = image.getWidth(this);
		int imageheight = image.getHeight(this);
		double widthratio = (double)newwidth/imagewidth;
		double heightratio = (double)newheight/imageheight;
		double minratio = Math.min(widthratio, heightratio);
							
		return image.getScaledInstance((int)Math.round(minratio*imagewidth), (int)Math.round(minratio*imageheight),Image.SCALE_SMOOTH);
	}
	
	//Simple drawString ampliation, just passing the position as a string (6 positions valid)
	public void drawText(String text, int panelwidth, int panelheight, Graphics g, String position) {
		int margin = 10;
		int fontsize = 11;
		g.setFont(new Font("Retro Gaming",Font.PLAIN,fontsize));
		g.setColor(Color.WHITE.darker());
		int textheight = g.getFontMetrics().getHeight();
		if(position == "bottomright") {
			g.drawString(text,panelwidth-g.getFontMetrics().stringWidth(text) - margin, panelheight - margin);
		}
		if(position == "bottomleft") {
			g.drawString(text,margin,panelheight - margin);
		}
		if(position == "bottomcenter") {
			g.drawString(text,(panelwidth-g.getFontMetrics().stringWidth(text))/2,panelheight - margin);
		}
		if(position == "topright") {
			g.drawString(text,panelwidth-g.getFontMetrics().stringWidth(text) - margin, textheight + margin);
		}
		if(position == "topleft") {
			g.drawString(text,margin,textheight + margin);
		}
		if(position == "topcenter") {
			g.drawString(text,(panelwidth-g.getFontMetrics().stringWidth(text))/2,textheight +  margin);
		}
	}
}

	

	
