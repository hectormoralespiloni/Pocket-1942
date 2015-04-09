/*-----------------------------------------
	Bullet Class for 1942 demo

	Author: Héctor Morales Piloni, MSc.
	Date:	March 24, 2005
------------------------------------------*/

class Bullet extends Sprite
{
	private int owner;
	
	public Bullet(int nFrames) {
		super(nFrames);
	}
	
	public void setOwner(int owner) {
		this.owner=owner;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public void move() {
		//player's bullet
		if (owner == 1){
			setY(getY()-6);
		}
		//enemy's bullet
		else{
			setY(getY()+6);
		}
	}
	
	public void draw(javax.microedition.lcdui.Graphics g) {
		selFrame(owner);
		super.draw(g);
	}
}
