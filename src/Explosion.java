/*-----------------------------------------
	Explosion Class for 1942 demo

	Author: Héctor Morales Piloni, MSc.
	Date:	March 24, 2005
------------------------------------------*/

class Explosion extends Sprite
{
	private int state;
	
	public Explosion(int nFrames) {
		super(nFrames);
		state=1;
	}
	
	public void setState(int state) {
		this.state=state;
	}
	
	public int getState() {
		return state;
	}
	
	public void move() {
		state++;
		if (state > super.frames())
			super.off();
	}
	
	public void draw(javax.microedition.lcdui.Graphics g) {
		selFrame(state);
		super.draw(g);
	}
}
