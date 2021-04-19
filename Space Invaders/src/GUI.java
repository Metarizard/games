import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class GUI extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L; //A warning is showed if this line isn't included
	public boolean[] keys = new boolean[256]; //Keyboards typically have less than 256 keys. This will keep track of the keys that are being pressed (true) or that are released (false)
	//The dimensions can be changed, but some variables in the Game and Menu classes should be redefined
	int width = 900;
	int height = 700;
	Game spaceinvaders = new Game(this);
	//The main just constructs a GUI
	public static void main(String[] args) {
		new GUI();
	}
	//By constructing a GUI (which is a JFrame extension), we are running a Game over it (Game extends JPanel, where we draw)
	public GUI() {
		Toolkit screen = Toolkit.getDefaultToolkit();
		Dimension screensize = screen.getScreenSize();
		spaceinvaders = new Game(this);
		add(spaceinvaders);
		setTitle("Space Invaders");
		//Space Invaders mechanics require this GUI to not be resizable
		setResizable(false);
		setVisible(true);
		//GUI location at the center of the screen
		setBounds((screensize.width-width)/2,(screensize.height-height)/2,width,height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Fancy icon of an alien to be displayed at the window bar
		setIconImage(screen.getImage("Graphics/Icons/SpaceInvadersIcon.png"));
		//We add a keylistener to receive keyboard input from the player
		addKeyListener(this);
		spaceinvaders.run();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

}

