package MovingObjects;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

//Extend from MovingObject class.
public class Spaceship extends MovingObject {
	public int lives = 3;
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>(); //Spaceship bullets can be considered as part of the spaceship itself
	public Spaceship(int x, int y, Image image) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.width = image.getWidth(null);
		//Due to image transparency, hitbox doesn't match image dimensions. Next lines solve this.
		this.height = (int)Math.round(0.6*image.getHeight(null)); 
		this.hitbox = new Rectangle(x,y + (int)Math.round(0.2*this.height),this.width,this.height);
	} 
	@Override
	public void moveX() {
		// TODO Auto-generated method stub
		if(x+vx-5 <= 0) {
			x = 5;
		}
		else {
			if(x+width+vx>=900-15) {
				x = 900 - width - 15;
			}
			else {
				x += vx;
			}
		}
		hitbox.x = x;
	}
	
	@Override
	public void moveY() {
		// TODO Auto-generated method stub
		//Spaceships move horizontally
	}
	
	@Override
	public void plot(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(image,x,y,null);
//		g.drawRect(hitbox.x,hitbox.y,hitbox.width,hitbox.height); //If needed, this shows where the alien hitbox really is
	}
}
