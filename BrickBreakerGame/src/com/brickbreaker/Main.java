package com.brickbreaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class MapGenerator {
	
	public int map [][];
	public int brickWidth;
	public int brickHeight;
	
	// This method creates an array of bricks of size 4x7
	public MapGenerator(int row, int col) {
		map = new int [row][col];
		for (int i = 0; i < map.length; i++) { 
			for (int j=0; j< map[0].length;j++) {
				map[i][j] = 1;
			}
		}
		
		brickWidth = 540/col;
		brickHeight = 150/row;
	}
	
	//This method draws the bricks
	public void draw(Graphics2D g) {
		for (int i = 0; i < map.length; i++) {
			for (int j=0; j< map[0].length;j++) {
				if(map[i][j] > 0) {
					g.setColor(Color.BLACK); // brick color
					g.fillRect(j*brickWidth + 80, i*brickHeight + 50, brickWidth, brickHeight);
					
					g.setStroke(new BasicStroke(4));
					g.setColor(Color.WHITE);
					g.drawRect(j*brickWidth + 80, i*brickHeight + 50, brickWidth, brickHeight);
				}
			}
			
		}
	}
	
	//This method sets the value of brick to 0 if it is hit by the ball
	public void setBrickValue(int value, int row, int col) {
		map[row][col] = value;
	}

}

class GamePlay extends JPanel implements KeyListener, ActionListener  {
	private static final long serialVersionUID = 1L;
	
	private boolean play = false;
	private int score = 0;
	
	private int totalBricks = 24;
	
	private Timer timer;
	private int delay = 8;
	
	private int playerX = 310; // Initial Position of the Paddle
	private int ballposX = 290; //Initial Position of the Ball Horizontally
	private int ballposY = 380; //Initial Position of the Ball Vertically
	private int ballXdir = -2; // Movement of the Ball Horizontally
	private int ballYdir = -3; // Movement of the Ball Vertically
	
	private MapGenerator map;
	

	public GamePlay() {
		map = new MapGenerator(3, 8);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(delay, this);
		timer.start();
	}

	public void paint(Graphics g) {
		
		// Background color
		g.setColor(Color.BLUE);
		g.fillRect(1, 1, 692, 592);
		
		map.draw((Graphics2D)g);
		
		g.fillRect(0, 0, 3, 592);
		g.fillRect(0, 0, 692, 3);
		g.fillRect(691, 0, 3, 592);
		
		g.setColor(Color.BLACK);
		g.fillRect(playerX, 550, 100, 12);
		
		g.setColor(Color.RED);  // ball color
		g.fillOval(ballposX, ballposY, 20, 20);
		
		g.setColor(Color.RED);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString("Score: " + score, 520, 30);
		
		
		if (totalBricks <= 0) { // If all bricks are destroyed then you win
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.RED);
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("WELL PLAYED! YOU WON! Score: " + score, 100, 300);
			
			g.setFont(new Font("serif", Font.BOLD, 20));
			g.drawString("Press Enter to Restart.", 230, 350);
		}
		
		if(ballposY > 570) { // If the ball goes below the paddle then you lose 
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.RED);
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("GOOD TRY! GAME OVER", 150, 300);
			
			g.setFont(new Font("serif", Font.BOLD, 20));
			g.drawString("Press Enter to Restart", 230, 350);
				
		} 
		g.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		timer.start();
		if(play) {
			// Ball and Paddle interaction 
			if(new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
				ballYdir = - ballYdir;
			}
			for( int i = 0; i<map.map.length; i++) { // Ball and Brick interaction
				for(int j = 0; j<map.map[0].length; j++) {  // map.map[0].length is the number of columns
					if(map.map[i][j] > 0) {
						int brickX = j*map.brickWidth + 80;
						int brickY = i*map.brickHeight + 50;
						int brickWidth= map.brickWidth;
						int brickHeight = map.brickHeight;
						
						Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
						Rectangle ballRect = new Rectangle(ballposX, ballposY, 20,20);
						Rectangle brickRect = rect;
						
						if(ballRect.intersects(brickRect) ) {
							map.setBrickValue(0, i, j);
							totalBricks--;
							score+=10;
							
							if(ballposX + 19 <= brickRect.x || ballposX +1 >= brickRect.x + brickRect.width) 
								ballXdir = -ballXdir;
							 else {
								ballYdir = -ballYdir;
							}
						}
						
					}
					
				}
			}
			
			ballposX += ballXdir;
			ballposY += ballYdir;
			if(ballposX < 0) { // if ball hits the left wall then it bounces back
				ballXdir = -ballXdir;
			}
			if(ballposY < 0) {  // if ball hits the top wall then it bounces back
				ballYdir = -ballYdir;
			}
			if(ballposX > 670) { // if ball hits the right wall then it bounces back
				ballXdir = -ballXdir;  
			
			}
			
		}
		
		
		repaint();

	}
	
	
	@Override
	public void keyPressed(KeyEvent ke) {
		if(ke.getKeyCode() == KeyEvent.VK_RIGHT) { // if right arrow key is pressed then paddle moves right
			if(playerX >= 600) {
				playerX = 600;
			} else {
				moveRight();
					
			}
		}
		if(ke.getKeyCode() == KeyEvent.VK_LEFT) { // if left arrow key is pressed then paddle moves left
			if(playerX < 10) {
				playerX = 10;
			} else {
				moveLeft();
					
			}
		}
		
		if(ke.getKeyCode() == KeyEvent.VK_ENTER) { // if enter key is pressed then game restarts
			if(!play) {
				play = true;
				ballposX = 290;
				ballposY = 380;
				ballXdir = -1;
				ballYdir = -3;
				score = 0;
				totalBricks = 24;
				map = new MapGenerator(3,8);
				
				repaint();
			}
		}
		
	}	
	public void moveRight() { // paddle moves right by 50 pixels
		play = true;
		playerX += 60;
	}
	public void moveLeft() { // paddle moves left by 50 pixels
		play = true;
		playerX -= 60;
	}
	
	@Override
	public void keyTyped(KeyEvent ke) {
		
	}
	
	@Override
	public void keyReleased(KeyEvent ke) {
		
	}


}
class Main {

	public static void main(String[] args) {
		JFrame obj = new JFrame();
		GamePlay gp = new GamePlay();
		obj.setContentPane(gp);
		//obj.setBounds(10, 10, 700, 600);
		obj.setBounds(10, 10, 700, 600);
		obj.setTitle("Brick Breaker Game");
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}