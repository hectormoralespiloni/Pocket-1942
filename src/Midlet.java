/*-----------------------------------------
	1942 clone for J2ME
	Author: Héctor Morales Piloni, MSc.
	Date:	March 20, 2005
------------------------------------------*/

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Random;

public class Midlet extends MIDlet implements CommandListener
{
	private Command exitCommand, playCommand, endCommand;
	private Display display;
	private SSCanvas screen;
	
	public Midlet() 
	{
		display = Display.getDisplay(this);
		exitCommand = new Command("Exit",Command.CANCEL,2);
		playCommand = new Command("Play",Command.OK,2);
		
		screen=new SSCanvas();
		screen.addCommand(exitCommand);
		screen.addCommand(playCommand);
		screen.setCommandListener(this);
	}
	
	public void startApp() throws MIDletStateChangeException {
		display.setCurrent(screen);
	}
	
	public void pauseApp(){}
	
	public void destroyApp(boolean unconditional){}
	
	public void commandAction(Command c, Displayable s)
	{
		if (c.getCommandType() == Command.CANCEL) 
		{
			if(screen.isPlaying())
			{
				screen.quitGame();
				screen.repaint();	
				screen.serviceRepaints();
			}
			else {
				destroyApp(false);
				notifyDestroyed();
			}
		}
		
		if(c.getCommandType() == Command.OK && !screen.isPlaying())
			new Thread(screen).start();
	}
}

class SSCanvas extends Canvas implements Runnable 
{
	private final int MAX_TILES = 16;
	private final int MAX_ENEMIES = 7;
	private final int MAX_BULLETS = 7;
	private final int MAX_DIGITS = 4;	//from 0000 to 9999
	private final int MAX_EXPLOSIONS = 7;
	private final int MAX_LIVES = 3;
	private final int PLAYER = 1;
	private final int ENEMY = 2;
	
	private int score;				//game score
	private int sleepTime;			//how many millisecons the Thread will sleep
	private int cicle;				//number of game cycles (every sleepTime ms)
	private int lives;				//player lives
	private int shield;				//player shield
	private int deltaX,deltaY;		//used to compute player position
	private int xTiles, yTiles;		//pretty self explanatory =)
	private int index, index_in;	//used for scrolling background
	
	private boolean playing;		//to know if we're playing
	private boolean fireOn;			//to know if we're firing
	private boolean collision;		//to know if we have a collision

	private Sprite intro = new Sprite(1);
	private Sprite gameOver = new Sprite(1);
	private Sprite getReady = new Sprite(2);
	private Sprite scoreSprite = new Sprite(1);
	private Sprite livesSprite[] = new Sprite[3];
	private Sprite digits[] = new Sprite[MAX_DIGITS];
	private Sprite tiles[] = new Sprite[MAX_TILES];
	private Player player = new Player(1);
	private Enemy enemies[] = new Enemy[MAX_ENEMIES];
	private Bullet bullets[] = new Bullet[MAX_BULLETS];
	private Explosion explosions[] = new Explosion[MAX_EXPLOSIONS];
	
	//the first and last 12 rows must be the same to avoid flickering
	int map [] = {0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  //-------------
				  0,1,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,3,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,4,5,0,0,0,2,0,
				  0,6,7,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  2,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,8,9,0,
				  0,0,0,0,0,10,11,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,2,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,3,0,0,0,0,2,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,12,13,0,0,0,0,
				  0,0,14,15,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,3,0,0,0,0,0,
				  0,0,0,0,0,0,1,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,2,0,0,0,1,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,2,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,1,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,3,0,2,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,4,5,0,0,0,2,0,
				  0,6,7,0,0,0,0,0,
				  0,0,0,0,0,1,0,0,
				  2,0,0,0,1,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,1,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,1,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,3,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,4,5,0,0,0,2,0,
				  0,6,7,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  2,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  //------------
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0,
				  0,0,0,0,0,0,0,0};	
				  
	public SSCanvas()
	{
		player.addFrame(1,"/images/player.png");
		intro.addFrame(1,"/images/intro.png");
		
		player.on();
		intro.on();
	}
	
	void gameInit()
	{
		int i;
		
		collision = false;
		playing = true;
		sleepTime = 40;
		deltaX = 0;
		deltaY = 0;
		xTiles = 8;
		yTiles = 11;
		cicle = 0;
		score = 0;
		shield = 0;
		lives = MAX_LIVES;
		index_in = 0;
		index = map.length - (xTiles*yTiles);
		
		scoreSprite.addFrame(1,"/images/score.png");
		scoreSprite.on();
		scoreSprite.setX(5);
		scoreSprite.setY(5);
		
		//init player position
		player.setX((getWidth()/2)-(player.getW()/2));
		player.setY(getHeight()-player.getH());
		
		//init banners
		gameOver.addFrame(1,"/images/gameover.png");
		gameOver.setX(0);
		gameOver.setY(0);
		gameOver.off();

		getReady.addFrame(1,"/images/getready1.png");
		getReady.addFrame(2,"/images/getready2.png");
		getReady.setX(getWidth()/2-(getReady.getW()/2));
		getReady.setY(getHeight()/2+(getReady.getH()/2));
		getReady.off();
		
		//init HUD
		for(i=0; i<MAX_LIVES; i++){
			livesSprite[i] = new Sprite(1);
			livesSprite[i].addFrame(1,"/images/lives.png");
			livesSprite[i].on();
			livesSprite[i].setX(getWidth()-(livesSprite[i].getW()*(i+1)));
			livesSprite[i].setY(5);
		}
		
		for(i=0; i<MAX_DIGITS; i++){
			digits[i] = new Sprite(10);
			digits[i].addFrame(1,"/images/zero.png");
			digits[i].addFrame(2,"/images/one.png");
			digits[i].addFrame(3,"/images/two.png");
			digits[i].addFrame(4,"/images/three.png");
			digits[i].addFrame(5,"/images/four.png");
			digits[i].addFrame(6,"/images/five.png");
			digits[i].addFrame(7,"/images/six.png");
			digits[i].addFrame(8,"/images/seven.png");
			digits[i].addFrame(9,"/images/eight.png");
			digits[i].addFrame(10,"/images/nine.png");
			digits[i].off();
		}
		
		//init enemies 
		for(i=0; i<MAX_ENEMIES; i++) {
			enemies[i] = new Enemy(2);
			enemies[i].addFrame(1,"/images/enemy1.png");
			enemies[i].addFrame(2,"/images/enemy2.png");
			enemies[i].off();
		}
		
		//init bullets
		for(i=0; i<MAX_BULLETS; i++) {
			bullets[i] = new Bullet(2);
			bullets[i].addFrame(1,"/images/playerBullet.png");
			bullets[i].addFrame(2,"/images/enemyBullet.png");
			bullets[i].off();
		}
		
		//init explosions
		for(i=0; i<MAX_EXPLOSIONS; i++) {
			explosions[i] = new Explosion(7);
			explosions[i].addFrame(1,"/images/explosion1.png");
			explosions[i].addFrame(2,"/images/explosion2.png");
			explosions[i].addFrame(3,"/images/explosion3.png");
			explosions[i].addFrame(4,"/images/explosion4.png");
			explosions[i].addFrame(5,"/images/explosion5.png");
			explosions[i].addFrame(6,"/images/explosion6.png");
			explosions[i].addFrame(7,"/images/explosion7.png");
			explosions[i].off();
		}
		
		//init tiles
		for(i=0; i<MAX_TILES; i++) {
			tiles[i] = new Sprite(1);
			tiles[i].on();
		}
		
		for(i=1; i<=MAX_TILES; i++)
			tiles[i-1].addFrame(1,"/images/tile"+i+".png");
	}

	boolean isPlaying()
	{
		return playing;
	}
	
	void quitGame()
	{
		playing = false;
	}
	
	void scroll()
	{
		index_in += 2;
		if(index_in >= 32){
			index_in = 0;
			index -= xTiles;
		}
		
		if(index <= 0){
			index = map.length - (xTiles*yTiles);
			index_in = 0;
		}
	}
	
	void enemyUpdate()
	{
		int freeEnemy,i;
		Random random = new java.util.Random();
		
		//Create an enemy every 20 cicles
		if (cicle%20 == 0)
		{
			freeEnemy=-1;
			//Look for a free enemy slot
			for (i=0; i<MAX_ENEMIES; i++) {
				if (!enemies[i].isActive()) {
					freeEnemy = i;
					break;
				}
			}
			
			if (freeEnemy >= 0) {
				enemies[freeEnemy].on();
				enemies[freeEnemy].setX((Math.abs(random.nextInt()) % getWidth()));
				enemies[freeEnemy].setY(-1*enemies[freeEnemy].getH());
				enemies[freeEnemy].setState(1);
				enemies[freeEnemy].setType((Math.abs(random.nextInt()) % 2) + 1);
				enemies[freeEnemy].init(player.getX());
			}
		}
		
		//Move enemies
		for (i=0; i<MAX_ENEMIES; i++) 
		{
			if (enemies[i].isActive()) {
				enemies[i].move();
			}
			//check if enemy is out of the screen
			if (enemies[i].getY() > (getHeight()+enemies[i].getH())) {
				enemies[i].off();
			}
		}
	}

	void bulletUpdate()
	{
		int freeBullet;
		int theEnemy, i, j;
		freeBullet=-1;
		
		if (fireOn && (cicle%5 == 0))
		{
			//Look for an empty bullet slot
			for (i=0; i<MAX_BULLETS; i++) {
				if (!bullets[i].isActive()) {
					freeBullet=i;
					break;
				}
			}
			
			if (freeBullet >= 0) {
				//shot already, this is the first bullet
				bullets[freeBullet].on();
				bullets[freeBullet].setX((player.getX()+(player.getW()/2))-(bullets[freeBullet].getW()/2));
				bullets[freeBullet].setY(player.getY()-bullets[freeBullet].getH());
				bullets[freeBullet].setOwner(PLAYER);
			}//if freeBullet > 0
		}
		
		freeBullet=-1;
		theEnemy=0;
		for (i=0; i<MAX_ENEMIES; i++)
		{
			if(enemies[i].isActive() && enemies[i].getY() >
				getHeight()/3 && enemies[i].getY() < (getHeight()/3)+5)
			{
				//Look for an empty bullet slot
				for (j=1; j<MAX_BULLETS; j++) {
					if (!bullets[j].isActive()) {
						freeBullet=j;
						theEnemy=i;
						break;
					}
				}
				
				if (freeBullet >=0) {
					bullets[freeBullet].on();
					bullets[freeBullet].setX(enemies[theEnemy].getX()+(enemies[theEnemy].getW()/2)-(bullets[freeBullet].getW()/2));
					bullets[freeBullet].setY(enemies[theEnemy].getY()+enemies[theEnemy].getH());
					bullets[freeBullet].setOwner(ENEMY);
				}
			}
		}
		
		//update bullets positions
		for (i=0; i<MAX_BULLETS; i++) 
		{
			if (bullets[i].isActive()) {
				bullets[i].move();
			}
			
			if ((bullets[i].getY() > getHeight()) || (bullets[i].getY() <= 0)) {
				bullets[i].off();
			}
		}
	}

	void explosionUpdate()
	{
		int i;
		for (i=0; i<MAX_EXPLOSIONS; i++) {
			explosions[i].move();
		}
	}
	
	void HUDUpdate()
	{
		int aux, factor;
		factor = 1;
		
		for(int i=0; i<=MAX_DIGITS; i++){
			try{
				aux = score/factor;
				digits[i].selFrame((aux%10)+1);
				factor*=10;
			} catch(Exception ex){}
		}
	}

	void createExplosion(int posx, int posy)
	{
		int freeExplode,i;
		freeExplode=-1;
		
		//Look for a free explosion slo
		for (i=0; i<MAX_EXPLOSIONS; i++)
		{
			if (!explosions[i].isActive()) {
				freeExplode=i;
				break;
			}
		}
		
		if (freeExplode >= 0) {
			explosions[freeExplode].setState(1);
			explosions[freeExplode].on();
			explosions[freeExplode].setX(posx);
			explosions[freeExplode].setY(posy);
		}
	}
	
	void checkCollisions()
	{
		int i,j;
		
		if(player.isActive())
		{
			//collision Player - Enemy
			for (i=0; i<MAX_ENEMIES; i++)
			{
				if (player.collide(enemies[i]) && enemies[i].isActive() && shield == 0) {
					createExplosion(player.getX(),player.getY());
					createExplosion(enemies[i].getX(),enemies[i].getY());
					enemies[i].off();
					player.off();
					cicle=0;
					collision=true;
				}
			}
		
			//Collision Player - Bullet
			for (i=0; i<MAX_BULLETS; i++)
			{
				if (bullets[i].isActive() && player.collide(bullets[i]) && bullets[i].getOwner() != PLAYER && shield == 0)
				{
					createExplosion(player.getX(),player.getY());
					bullets[i].off();
					player.off();
					cicle=0;
					collision=true;
				}
			}
		}//END-IF player isActive
		
		//Collision Enemy - Bullet
		for (i=0; i<MAX_BULLETS; i++)
		{
			if (bullets[i].getOwner() == PLAYER && bullets[i].isActive())
			{
				for (j=0; j<MAX_ENEMIES; j++)
				{
					if (enemies[j].isActive())
					{
						if (bullets[i].collide(enemies[j]))
						{
							createExplosion(enemies[j].getX(),enemies[j].getY());
							enemies[j].off();
							bullets[i].off();
							score+=10;
						}
					}
				}
			}
		}
		
		//we must wait 10 cicles before reseting player and showing getReady banner
		if(collision && (cicle > 10))
		{
			collision = false;
			player.on();
			getReady.on();
			lives--;
			player.setX((getWidth()/2)-(player.getW()/2));
			player.setY(getHeight()-player.getH());
			
			//During 30 cicles our ship will be inmune
			shield=30;
			if (lives <= 0) {
				gameOver.on();
				playing=false;
			}
		}
		
		if (shield > 0){
			shield--;
			if(cicle%3 == 0)
				getReady.selFrame(getReady.getFrame()==1?2:1);
		}
		else
			getReady.off();
	}

	void playerUpdate()
	{
		if(!player.isActive())
			return;
		
		int x,y;
		
		x = player.getX()+deltaX;
		y = player.getY()+deltaY;
		
		if((x >= 0) && (x <= (getWidth()-player.getW())))
			player.setX(x);
		
		if((y >= 0) && (y <= (getHeight()-player.getH())))
			player.setY(y);
	}
	
	public void run()
	{
		//initialize game data
		gameInit();

		while(playing){
			//scroll background
			scroll();

			//update player´s potision
			playerUpdate();
			
			//update enemies' position
			enemyUpdate();
			
			//update bullets' poition
			bulletUpdate();
			
			//update explosions' position
			explosionUpdate();
			
			//check for collisions
			checkCollisions();
			
			//update HUD
			HUDUpdate();

			//increment cicle
			cicle++;
			
			//update screen
			repaint();	
			serviceRepaints();
			
			try{
				Thread.sleep(sleepTime);
			} catch(InterruptedException ex){
				System.out.println(ex.toString());
			}
		}
	}
	
	public void keyPressed(int key)
	{
		if(!player.isActive())
			return;
		
		int action;
		action = getGameAction(key);
		
		switch(action){
			case FIRE:
				fireOn = true;
				break;
			case LEFT:
				deltaX = -5;
				break;
			case RIGHT:
				deltaX = 5;
				break;
			case UP:
				deltaY = -5;
				break;
			case DOWN:
				deltaY = 5;
				break;
		}
	}
	
	public void keyReleased(int key)
	{
		if(!player.isActive())
			return;
		
		int action;
		action = getGameAction(key);
		
		switch(action){
			case FIRE:
				fireOn = false;
				break;
			case LEFT:
				deltaX = 0;
				break;
			case RIGHT:
				deltaX = 0;
				break;
			case UP:
				deltaY = 0;
				break;
			case DOWN:
				deltaY = 0;
				break;
		}		
	}
	
	public void paint(Graphics g)
	{
		int x,y,t,i,j;
		
		x = y = t = 0;
		
		g.setColor(255,255,255);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
		
		if(!playing){
			if(gameOver.isActive()){
				gameOver.draw(g);
				g.drawString("Your score: "+score,getWidth()/2, 110, g.TOP | g.HCENTER);
			}
			else 
				intro.draw(g);
		}
		else {
			//draw background
			for(i=0; i<yTiles; i++)
			{
				for(j=0; j<xTiles; j++)
				{
					t = map[index+(i*xTiles+j)];
				
					//get position of tile
					x = j*32;
					y = (i-1)*32+index_in;
				
					tiles[t].setX(x);
					tiles[t].setY(y);
					tiles[t].draw(g);
				}
			}
			
			
			//draw enemies
			for(i=0; i<MAX_ENEMIES; i++)
			{
				if(enemies[i].isActive()) {
					enemies[i].setX(enemies[i].getX());
					enemies[i].setY(enemies[i].getY());
					enemies[i].draw(g);
				}		
			}
			
			
			//draw player
			if(player.isActive())
			{
				player.setX(player.getX());
				player.setY(player.getY());
				player.draw(g);
			}
			
			//draw bullets
			for(i=0; i<MAX_BULLETS; i++)
			{
				if(bullets[i].isActive()) {
					bullets[i].setX(bullets[i].getX());
					bullets[i].setY(bullets[i].getY());
					bullets[i].draw(g);
				}
			}
			
			//draw explosions
			for(i=0; i<MAX_EXPLOSIONS; i++)
			{
				if(explosions[i].isActive())
					explosions[i].draw(g);
			}
			
			//draw HUD
			scoreSprite.draw(g);
			for(i=MAX_DIGITS-1, j=0; i>=0; i--, j++){
				digits[i].setX(scoreSprite.getW()+10+(digits[i].getW()*j));
				digits[i].setY(5);
				digits[i].draw(g);
			}
			
			for(i=0; i<lives; i++)
				livesSprite[i].draw(g);
			
			//draw banner
			if(getReady.isActive())
				getReady.draw(g);
		}//else
	}
}
