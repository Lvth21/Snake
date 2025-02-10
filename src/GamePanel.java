import java.awt.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener{

	
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten = 0;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	String record;
	String nome;
	
	Image img = Toolkit.getDefaultToolkit().createImage(".//res//Dark_Grass.jpg")	;
	
	
	
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(new Color(0,50,0));
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
		playSound2();
		record();
		
		
	}
	
	

	
	public void dialogbox(){
		
		String p1name = JOptionPane.showInputDialog(null,"Please enter player 1 name","Specify name",JOptionPane.PLAIN_MESSAGE);
		nome = p1name;
		
				
	}
	
	public void print() {
		try {
		      FileWriter myWriter = new FileWriter(".//res//Records.txt");
		      myWriter.write(String.valueOf(applesEaten) + "\n" + nome);
		      myWriter.close();
		      } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	public void record() {
		try {
		      File myObj = new File(".//res//Records.txt");
		      Scanner myReader = new Scanner(myObj);
		      
		        record = myReader.nextLine();
		        nome = myReader.nextLine();
		      
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
		draw(g);
		
	
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine( 0,i*UNIT_SIZE, SCREEN_WIDTH,i*UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
		
			for(int i = 0; i < bodyParts; i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(new Color(16,224,255));
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 20));
			g.drawString("Record: " + record, 30, 30);
		}
		else {
			gameOver(g);
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	
	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U':
			y[0] = y[0]-UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
			
			
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX)&&(y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			//sound
			playSound();
			newApple();
		}
	}
	
	public void playSound() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(".//res//"
	        		+ "Apple - Loud Eating Sound! (1) (mp3cut (online-audio-converter.com).wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	public void playSound2() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(".//res//"
	        		+ "CUTE 8-BIT MUSIC NO COPYRIGHT (online-audio-converter.com).wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        FloatControl gainControl = 
	        	    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        	gainControl.setValue(-25.0f); // Reduce volume by 10 decibels.
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}



	public void checkCollisions() {
		//checks if head collides with body
		for(int i = bodyParts; i>0; i--) {
			if((x[0] == x[i])&& (y[0] == y[i])) {
				running = false;
			}
		}
		//check if head touches left border
		if(x[0] < 0) {
			running = false;
		}
		//check if head touches right border
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		//check if head touches top border
		if(y[0] < 0) {
			running = false;
		}
		//check if head touches bottom border
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		//score
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
		//Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("GAME OVER, LOSER!", (SCREEN_WIDTH - metrics2.stringWidth("GAME OVER, LOSER!"))/2, SCREEN_HEIGHT/2);
		//record text
		if((Integer.parseInt(record) < applesEaten)) {
			
			new MyFrame();
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics records = getFontMetrics(g.getFont());
			g.drawString("New Record! Congrats!!", (SCREEN_WIDTH - records.stringWidth("New Record! Congrats!!"))/2, SCREEN_HEIGHT - 60);
			
			
			
		}else {
		
		g.setColor(Color.white);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics records = getFontMetrics(g.getFont());
		g.drawString("Record: " + record, (SCREEN_WIDTH - records.stringWidth("Record: " + record ))/2, SCREEN_HEIGHT - 60);
		g.drawString("Di " + nome,(SCREEN_WIDTH - records.stringWidth("Di " + nome))/2, SCREEN_HEIGHT - 20);
		}
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
			
		}
		repaint();
		
	}

	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_A:
				if(direction != 'R') {
					direction ='L';
				}
					break;
			case KeyEvent.VK_D:
				if(direction != 'L') {
					direction ='R';
				}
					break;
			case KeyEvent.VK_W:
				if(direction != 'D') {
					direction ='U';
				}
					break;
			case KeyEvent.VK_S:
				if(direction != 'U') {
					direction ='D';
				}
					break;
		
				}
			}
		}
	

public class MyFrame extends JFrame implements ActionListener{

	JButton button;
	JTextField textField;
	
	MyFrame(){
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		
		button = new JButton("Submit");
		button.addActionListener(this);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(250,40));
		textField.setFont(new Font("Consolas",Font.PLAIN,35));
		textField.setForeground(new Color(0x00FF00));
		textField.setBackground(Color.black);
		textField.setCaretColor(Color.white);
		textField.setText("username");
		
		this.setTitle("Insert your name!");
		this.add(button);
		this.add(textField);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
	}
	
	 public void actionPerformed(ActionEvent e)
		{
			button = (JButton)e.getSource();
			nome = this.textField.getText();
			System.out.println(nome);
			try {FileWriter myWriter = new FileWriter(".//res//Records.txt");
		      myWriter.write(String.valueOf(applesEaten) + "\n" + nome);
		      
				myWriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     
			
			this.dispose();		
			System.out.println("Frame Closed.");		
		}
	
	
			
			
		}
		
		
		
	

}
	
