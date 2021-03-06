/*-----------------------------------------
	Sprite Class for 1942 demo

	Author: H�ctor Morales Piloni, MSc.
	Date:	March 20, 2005
------------------------------------------*/

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.*;
import java.io.*;

class Sprite 
{
	private int posX, posY;
	private int frame, nFrames;
	private boolean active;
	private Image sprites[];
	
	public Sprite(int nFrames)
	{
		posX = 0;
		posY = 0;
		active = false;	//default state
		frame = 1;		//initial frame
		this.nFrames = nFrames;
		sprites = new Image[nFrames+1];
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}
	
	public int getW() {
		return sprites[nFrames].getWidth ();
	}
	
	public int getH() {
		return sprites[nFrames].getHeight();
	}
	
	public int getFrame() {
		return frame;
	}
	
	public void setX(int value) {
		posX = value;
	}
	
	public void setY(int value) {
		posY = value;
	}
	
	public void on() {
		active = true;
	}
	
	public void off() {
		active = false;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public void selFrame(int frameNum) {
		frame = frameNum;
	}
	
	public int frames() {
		return nFrames;
	}
	
	/*----------------------------------------
		addFrame()
		loads a.PNG file and adds it to our 
		sprites array in the given frame
	------------------------------------------*/
	public void addFrame(int frameNum, String path)
	{
		try{
			sprites[frameNum] = Image.createImage(path);
		} catch(IOException ex){
			System.err.println("Could not load image:" + path + " " + ex.toString());
		}
	}
	
	/*----------------------------------------
		collide()
		checks for collision between actual
		sprite and a given one.
	-----------------------------------------*/
	public boolean collide(Sprite s)
	{
		int x1,x2,y1,y2,w1,w2,h1,h2;
		
		x1 = this.getX();
		y1 = this.getY();
		w1 = this.getW();
		h1 = this.getH();
		
		x2 = s.getX();
		y2 = s.getY();
		w2 = s.getW();
		h2 = s.getH();
		
		if(((x1+w1) > x2 ) && ((y1+h1) > y2) && ((x2+w2) > x1) && ((y2+h2) > y1))
			return true;
		else
			return false;
	}
	
	public void draw(Graphics g){
		g.drawImage(sprites[frame], posX, posY, Graphics.LEFT | Graphics.TOP);
	}
}
