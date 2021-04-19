import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Shield {
	ArrayList<Rectangle> rectanglestructure= new ArrayList<Rectangle>();
	Color color = Color.GREEN;
	//Shields are composed of various rectangles of fixed dimensions. Following loops try to simulate actual Space Invaders shields.
	public Shield(int x, int y, int rows, int cols, int rwidth, int rheight) {
		for(int i=0;i<rows;i++) {
			if(i==0) {
				for(int j=1;j<cols-1;j++) {
					rectanglestructure.add(new Rectangle(x+rwidth*j,y+rheight*i,rwidth,rheight));
				}
			}
			if((i==1)||(i==2)) {
				for(int j=0;j<cols;j++) {
					rectanglestructure.add(new Rectangle(x+rwidth*j,y+rheight*i,rwidth,rheight));
				}
			}
			if(i==3) {
				for(int j=0;j<2;j++) {
					rectanglestructure.add(new Rectangle(x+rwidth*j,y+rheight*i,rwidth,rheight));
				}
				for(int j=cols-2;j<cols;j++) {
					rectanglestructure.add(new Rectangle(x+rwidth*j,y+rheight*i,rwidth,rheight));
				}
			}
		}
	}
	
	public void plot(Graphics g) {
		g.setColor(color);
		for(Rectangle rect:rectanglestructure) {
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
}
