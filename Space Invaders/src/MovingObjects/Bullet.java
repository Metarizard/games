package MovingObjects;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Bullet extends MovingObject{
	public Bullet(int x, int y, int vx, int vy, Image image) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.width = (int)Math.round(0.6*image.getWidth(null));
		this.height = image.getHeight(null);
		this.hitbox = new Rectangle(x+(int)Math.round(0.2*this.width),y,this.width,this.height);
		this.vx = vx;
		this.vy = vy;
	}
	@Override
	public void moveX() {
		// TODO Auto-generated method stub
		//Bullets don't move horizontally
	}
	@Override
	public void moveY() {
		// TODO Auto-generated method stub
		y += vy;
		hitbox.y = y;
	}
	@Override
	public void plot(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(image,x,y,null);
//		g.drawRect(hitbox.x,hitbox.y,hitbox.width,hitbox.height); //If needed, this shows where the alien hitbox really is
	}
}