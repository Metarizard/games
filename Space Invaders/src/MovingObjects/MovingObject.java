package MovingObjects;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public abstract class MovingObject {
	public int x;
	public int y;
	public int vx;
	public int vy;
	public int width;
	public int height;
	public Rectangle hitbox;
	public Image image;
	
	public abstract void moveX();
	
	public abstract void moveY();
	
	public abstract void plot(Graphics g);
}
