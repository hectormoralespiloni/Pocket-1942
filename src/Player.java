/*-----------------------------------------
	Player Class for 1942 demo

	Author: Héctor Morales Piloni, MSc.
	Date:	March 24, 2005
------------------------------------------*/

class Player extends Sprite
{
	private int state;
	
	public Player(int nFrames) {
		super(nFrames);
	}
	
	public void setState(int state) {
		this.state=state;
	}
	
	public int getState(int state) {
		return state;
	}
}
