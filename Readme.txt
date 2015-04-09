
Java 1942 (May 2005)
--------------------

1. SUMMARY 
	This is a clone of the oldie classic 1942 shooter made with the Java Micro
	Edition for mobile platforms.

2. REQUIREMENTS TO RUN THE JAR
	-Java 2 Runtime Environment
	-J2ME emulator which comes with the Sun wireless toolkit 
	but there are also some Web based emulators out there such as:
	http://kobjects.sourceforge.net/me4se/
	http://sourceforge.net/projects/microemulator/
	however I highly recommend the Sun wireless toolkit for compatibility issues
	
3. HOW TO PLAY THE DEMO
	- Distributable files are located in the "dist" folder
	- Arrows (or 8/2, 4/6) to move the airplane
	- Action button (or 5) to fire 
	
4. HOW TO COMPILE
	-The easiest way to go is download the Netbeans IDE from: netbeans.org
	and then download the mobility pack. There's already an nbproject folder 
	for netbeans you just have to select the 1942 folder in netbeans to open it.

5. CODE STURCTURE
	-The images folder contains all the png with transparency used in the game.
	-There are 6 classes:
	* Sprite.java: 	this class manages the basic sprite stuff such as get and set
			its position, collision detection and drawing the sprite.
	* Player.java: 	this class inherits from sprite and it just adds the set/get
			state functionality (playing, dying, exploding, or whatever)
	* Bullet.java:	this class inherits from sprite and adds the owner functionality
			(to know to whom it belongs, a player or an enemy)
	* Enemy.java:	this class inherits from sprite and adds a move method (which is
			basic AI to move the enemies in a randomly way)
	* Explosion.java: just another sprite child to handle explosions.
	* Midlet.java:	this is where the magic happens, it creates a midlet, starts
			and initializes the application. It has a threaded canvas (runnable)
			which acts as the main game loop entry.



