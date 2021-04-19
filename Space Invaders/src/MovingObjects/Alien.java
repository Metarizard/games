package MovingObjects;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Alien extends MovingObject{
	public int row,col; //Aliens will be displayed in a "matrix"-like form
	public int lives; //Certain aliens will require more than one impact to be defeated
	public String type; //We have 5 different alien types: bot, mid, top, mystery and boss
	public boolean canfire; //Not all aliens can fire
	public Alien(int x, int y, int vx, int vy, int row, int col, Image[] imagetypes, boolean canfire, int lives) { //Big but necessary Alien constructor
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.canfire = canfire;
		this.row = row;
		this.col = col;
		this.lives = lives;
		if((this.row == 0)||(this.row == 1)) {
			this.type = "bot";
			this.image = imagetypes[0];
		}
		if((this.row == 2)||(this.row == 3)) {
			this.type = "mid";
			this.image = imagetypes[1];
		}
		if(this.row == 4) {
			this.type = "top";
			this.image = imagetypes[2];
		}
		if(this.row == -1) {
			this.type = "mystery";
			this.image = imagetypes[3];
		}
		if(this.row == 12) {
			this.type = "boss";
			this.image = imagetypes[4];
		}
		this.width = image.getWidth(null);
		this.height = image.getHeight(null);
		this.hitbox = new Rectangle(x,y,this.width,this.height);
	}
	@Override
	public void moveX() {
		// TODO Auto-generated method stub
		x += vx;
		hitbox.x = x;
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
