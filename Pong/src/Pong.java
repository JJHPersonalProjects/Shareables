import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Pong implements ActionListener,KeyListener {

	public static Pong pong;
	public int width=700,height=700;
	public Renderer renderer;
	public Paddle player1,player2;
	public Ball ball;
	public boolean bot = false;
	public boolean w,s,up,down;
	public int gameStatus = 0;//0 = Stopped, 1 = Paused, 2 = Play 3 = Game Over
	
	public Pong(){
		Timer timer = new Timer(20,this);
		JFrame jframe = new JFrame("Pong");
		
		renderer = new Renderer();
		
		jframe.setSize(width+16, height+35);
		 jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.add(renderer);
		jframe.addKeyListener(this);
		timer.start();
	}
	public void start(){
		gameStatus = 2;
		player1 = new Paddle(this,1);
		player2 = new Paddle(this,2);
		ball = new Ball(this);
	}
	public void update(){
		if(w){
			player1.move(true);
		}
		if(s){
			player1.move(false);
		}if(!bot){
		if(up){
			player2.move(true);
		}
		if(down){
			player2.move(false);
		}
		}else{
			int speed = 15;
			if(player2.y < ball.y){
				player2.y += speed;
		}
			if(player2.y > ball.y){
				player2.y -= speed;
			}
		}
		if(player1.score == 5 || player2.score == 5){
			gameStatus = 3;
		}
		ball.update(player1, player2);
	}
	public void actionPerformed(ActionEvent e){
		if(gameStatus == 2){
			update();
		}
		renderer.repaint();
		
	}
	public static void main(String[] arg){
		pong = new Pong();
		
	}
	
	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(gameStatus == 0){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString("PONG", width/2 - 70,50);
			g.setFont(new Font("Arial",Font.BOLD,30));
			g.drawString("Press \"Space\" To Play ", width/2 - 150,height / 2 - 25);
		}
		if(gameStatus == 2 || gameStatus == 1){
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(5f));
			g.drawLine(width/2,0,width/2,height);
			g.drawOval(width/2 - 100, height/2 - 100, 200, 200);
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString(String.valueOf(player1.score), width/2 - 75, 50);
			g.drawString(String.valueOf(player2.score), width/2 + 60, 50);
			player1.render(g);
			player2.render(g);
			ball.render(g);
		}
		if(gameStatus == 1){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString("PAUSED", width/2 - 103,height/2 - 25);
		}
		if(gameStatus == 3){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial",Font.BOLD,50));
			g.drawString("PONG GAME OVER", width/2 - 180,50);
			g.setFont(new Font("Arial",Font.BOLD,30));
			if(player1.score == 5){
				g.drawString("Player 1 Won The Game....Player 2..YOU SUCK!!", width/2 - 330,height / 2 - 25);
		}else{
			g.drawString("Player 2 Won The Game....Player 1..YOU SUCK!!", width/2 - 330,height / 2 - 25);
		}
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		int id = e.getKeyCode();
		if(id ==KeyEvent.VK_W){
			w = true;
		}
		if(id ==KeyEvent.VK_S){
			s = true;
		}
		if(id ==KeyEvent.VK_UP){
			up = true;
		}
		if(id ==KeyEvent.VK_DOWN){
			down = true;
		}
		if(id ==KeyEvent.VK_ESCAPE && gameStatus == 2){
			gameStatus = 0;
		}
		if(id ==KeyEvent.VK_SPACE){
			if(gameStatus == 0){
				start();
			}
			else if(gameStatus == 1){
				start();
			}
			else if(gameStatus == 2){
				gameStatus = 1;
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		int id = e.getKeyCode();
		if(id ==KeyEvent.VK_W){
			w = false;
		}
		if(id ==KeyEvent.VK_S){
			s = false;
		}
		if(id ==KeyEvent.VK_UP){
			up = false;
		}
		if(id ==KeyEvent.VK_DOWN){
			down = false;
		}
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
