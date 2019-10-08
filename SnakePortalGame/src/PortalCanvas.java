
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class PortalCanvas extends Canvas implements Runnable, KeyListener {

    /**
     *
     */
    private static final long serialVersionUID = 3065698597616690756L;
    private final int BOX_HEIGHT = 24;
    private final int BOX_WIDTH = 24;
    private final int GRID_WIDTH = 19;
    private final int GRID_HEIGHT = 19;
    private int speed = 100;

    private LinkedList<Point> snake;
    private Point fruit;
    private Point portal, portal2, portal3, portalx, portalz;
    private int direction = Direction.NO_DIRECTION;

    private Thread runThread;
    private int score = 0;
    private String highscore = "";
    private String rate;
    private boolean isInMenu = true;
    private Image menuImage = null;
    private boolean EndGame = false;
    private boolean won = false;
    private int count = 0;
    private boolean HelpScreen = false;
    private boolean SurveyScreen = false;

    public void paint(Graphics g) {
        if (runThread == null) {
            this.setPreferredSize(new Dimension(640, 515));
            this.addKeyListener(this);
            runThread = new Thread(this);
            runThread.start();
        }
        if (isInMenu) {
            DrawMenu(g);
        } else if (EndGame) {
            DrawEndGame(g);
        } else if (HelpScreen) {
            DrawHelp(g);
        } else if (SurveyScreen) {
            DrawSurvey(g);
        } else {
            if (snake == null) {
                snake = new LinkedList<Point>();
                DefaultSnake();
                PlaceFruit();
                PlacePortal();
                PlacePortal2();
                PlacePortal3();
            }
            if (highscore == "") {
                highscore = this.HighScore();
                System.out.println(highscore);
            }
            DrawPortal(g);
            DrawPortal2(g);
            DrawPortal3(g);
            DrawFruit(g);
            DrawSnake(g);
            DrawGrid(g);
            //DrawSnake(g);
            Score(g);
            Level(g);
        }
    }

    public void DrawEndGame(Graphics g) {
        BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics endGameGraphics = endGameImage.getGraphics();
        endGameGraphics.setColor(Color.white);
        if (won) {
            endGameGraphics.drawString("You Have Beat Snake Portal. Congratulations!", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
        } else if (!won && score > 500) {
            endGameGraphics.drawString("You Have A New Highscore!!", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
        } else if (count >= 3) {
            endGameGraphics.drawString("You Ran Over Too Many Portals. I'm Calling The Mercy Rule!", (this.getPreferredSize().width / 2) - 125, this.getPreferredSize().height / 2);
        } else {
            endGameGraphics.drawString("You Lost. Try Again!!", this.getPreferredSize().width / 2, this.getPreferredSize().height / 2);
        }
        endGameGraphics.drawString("Your Score: " + score, this.getPreferredSize().width / 2, (this.getPreferredSize().height / 2) + 20);
        endGameGraphics.drawString("Press \"SPACE\" to start a new game!", this.getPreferredSize().width / 2, (this.getPreferredSize().height / 2) + 40);
        g.drawImage(endGameImage, 0, 0, this);
    }

    public void DrawHelp(Graphics g) {
        BufferedImage HelpImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics HelpGraphics = HelpImage.getGraphics();
        HelpGraphics.setColor(Color.white);
        HelpGraphics.drawString("Use Arrow Keys To Move And Collect The Red Apples.", GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 10);
        HelpGraphics.drawString("Green Apples Make Your Tail Smaller, Blue Portals Are Minus 10.", GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 28);
        HelpGraphics.drawString("White Portals Make You Lose Automatically. As Score Increases, Speed Increases. Press \"SPACE\" To Go Back.", GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 46);
        g.drawImage(HelpImage, 0, 0, this);
    }

    public void DrawSurvey(Graphics g) {
        BufferedImage SurveyImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics HelpGraphics = SurveyImage.getGraphics();
        HelpGraphics.setColor(Color.white);
        HelpGraphics.drawString("Press \"SPACE\" To Go Back.", GRID_WIDTH / 2, GRID_HEIGHT / 2);
        g.drawImage(SurveyImage, 0, 0, this);
    }

    public void DrawMenu(Graphics g) {
        if (this.menuImage == null) {
            try {
                URL imagePath = PortalCanvas.class.getResource("SnakePortal2.png");
                menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        g.drawImage(menuImage, 0, 0, 640, 515, this);
    }

    public void update(Graphics g) {
        Graphics offScreenGraphics; //draws off screen
        BufferedImage offscreen = null;
        Dimension d = this.getSize();

        offscreen = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        offScreenGraphics = offscreen.getGraphics();
        offScreenGraphics.setColor(this.getBackground());
        offScreenGraphics.fillRect(0, 0, d.width, d.height);
        offScreenGraphics.setColor(this.getForeground());
        paint(offScreenGraphics);

        //flip
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Snake() {
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 1));
        snake.add(new Point(GRID_WIDTH / 2, (GRID_HEIGHT / 2) + 2));
    }

    public void DefaultSnake() {
        score = 0;
        count = 0;
        snake.clear();
        Snake();
        direction = Direction.NO_DIRECTION;
    }

    public void LevelUp() {
        if (score > 100) {
            speed = 85;
        } else if (score > 200) {
            speed = 70;
        } else if (score > 350) {
            speed = 60;
        } else if (score > 450) {
            speed = 55;
        } else if (score > 350) {
            speed = 40;
        }
    }

    public void Level(Graphics g) {
        g.setColor(Color.white);
        if (direction == Direction.NO_DIRECTION) {
            g.drawString("Press \"H\" To Learn How To Play", (GRID_WIDTH / 2) + 450, (GRID_HEIGHT / 2) + 4);
            g.drawString("Press \"R\" To Rate The Game", (GRID_WIDTH / 2) + 450, (GRID_HEIGHT / 2) + 50);

        }
        if (score >= 100) {
            g.drawString("LEVEL 2!", 0, BOX_HEIGHT * GRID_HEIGHT + 45);
        } else if (score >= 200) {
            g.drawString("       LEVEL 3!", 0, BOX_HEIGHT * GRID_HEIGHT + 45);
        } else if (score >= 250) {
            g.drawString("              LEVEL 4!", 0, BOX_HEIGHT * GRID_HEIGHT + 45);
        } else if (score >= 350) {
            g.drawString("                     LEVEL 5!", 0, BOX_HEIGHT * GRID_HEIGHT + 45);
        } else if (score >= 450) {
            g.drawString("                            LEVEL....DOESN'T MATTER, YOU CAN'T WIN!", 0, BOX_HEIGHT * GRID_HEIGHT + 45);
        }
    }

    public void Move() throws SQLException {
        if (direction == Direction.NO_DIRECTION) {
            return;
        }
        Point head = snake.peekFirst();
        Point newPoint = head;
        switch (direction) {
            case Direction.NORTH:
                newPoint = new Point(head.x, head.y - 1);
                break;
            case Direction.SOUTH:
                newPoint = new Point(head.x, head.y + 1);
                break;
            case Direction.EAST:
                newPoint = new Point(head.x + 1, head.y);
                break;
            case Direction.WEST:
                newPoint = new Point(head.x - 1, head.y);
                break;
        }
        if (this.direction != Direction.NO_DIRECTION) {
            snake.remove(snake.peekLast());
        }
        if (newPoint.equals(portal) || newPoint.equals(portalx) || newPoint.equals(portalz)) {//black
            //score = 0;
            //count = 4;
            EndGame = true;
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                case Direction.EAST:
                    newPoint = new Point(head.x + 1, head.y);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y);
                    break;
            }
            PlacePortal();
            PlacePortal2();
            PlaceFruit();
        }
        if (newPoint.equals(portal2)) {//blue
            score -= 10;
            count += 1;
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                case Direction.EAST:
                    newPoint = new Point(head.x + 1, head.y);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y);
                    break;
            }
            PlacePortal();
            PlacePortal2();
            PlacePortal3();
            PlaceFruit();
        }
        if (newPoint.equals(portal3)) {//green
            snake.remove(snake.peekLast());
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                case Direction.EAST:
                    newPoint = new Point(head.x + 1, head.y);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y);
                    break;
            }
            PlacePortal();
            PlacePortal2();
            PlacePortal3();
        }

        if (newPoint.equals(fruit)) {
            // Snake hits fruit
            score += 50;
            LevelUp();
            Point addPoint = (Point) newPoint.clone();
            PlacePortal();
            PlacePortal2();
            PlacePortal3();
            switch (direction) {
                case Direction.NORTH:
                    newPoint = new Point(head.x, head.y - 1);
                    break;
                case Direction.SOUTH:
                    newPoint = new Point(head.x, head.y + 1);
                    break;
                case Direction.EAST:
                    newPoint = new Point(head.x + 1, head.y);
                    break;
                case Direction.WEST:
                    newPoint = new Point(head.x - 1, head.y);
                    break;
            }
            snake.push(addPoint);
            PlaceFruit();
            PlacePortal();
            PlacePortal2();
            PlacePortal3();
        } else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1)) {
            // Out Of Bounds, reset game
            CheckScore();
            won = false;
            EndGame = true;
            return;
        } else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1)) {
            // Out Of Bounds, reset game
            CheckScore();
            won = false;
            EndGame = true;
            return;
        } else if (snake.contains(newPoint)) {
            // Ran into Ourselves, reset game
            if (direction != Direction.NO_DIRECTION) {
                CheckScore();
                won = false;
                EndGame = true;
                return;
            }
        } else if (snake.size() == (GRID_WIDTH * GRID_HEIGHT)) {
            CheckScore();
            won = true;
            EndGame = true;
            return;
        } else if (count == 3) {
            CheckScore();
            won = false;
            EndGame = true;
            return;
        }
        // still playing
        snake.push(newPoint);
    }

    public void Score(Graphics g) {
        g.setColor(Color.white);
        g.drawString("Score: " + score, 0, BOX_HEIGHT * GRID_HEIGHT + 15);
        g.drawString("Highscore: " + highscore.split(":")[0] + " Got " + Integer.parseInt(highscore.split(":")[1]), 0, BOX_HEIGHT * GRID_HEIGHT + 30);
    }

    public void CheckScore() throws SQLException {
        //abc dr = new abc();

        System.out.println(highscore);
        if (score > Integer.parseInt(highscore.split(":")[1])) {
            String name = JOptionPane.showInputDialog("Congratulations! What is your name?");
            String age = JOptionPane.showInputDialog("How old are you?");
            String gender = JOptionPane.showInputDialog("Gender: M or F?");
            String email = JOptionPane.showInputDialog("Enter email address");
            highscore = name + ":" + score + ":" + age + ":" + gender + ":" + email;

//            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MyDataBase", "Jabbar", "123456");
//            PreparedStatement st = con.prepareStatement("insert into UNTITLED(Score,Name)values(?,?)");
//            st.setInt(1, 101);
//            st.setString(2, name);
//            int a = st.executeUpdate();
//            if (a > 0) {
//                System.out.println("Row Update");
//            }

            File scoreFile = new File("highscore.txt"); //highscore file---------
            if (!scoreFile.exists()) {
                try {
                    scoreFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileWriter writeFile = null;
            BufferedWriter writer = null;
            try {
                writeFile = new FileWriter(scoreFile, true);
                writer = new BufferedWriter(writeFile);
                writer.newLine();
                writer.write(this.highscore);
                

            } catch (Exception e) {

            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void DrawGrid(Graphics g) {
        // Vertical Lines
        for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x += BOX_WIDTH) {
            g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
        }
        // Horizontal Lines
        for (int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y += BOX_HEIGHT) {
            g.drawLine(0, y, GRID_WIDTH * BOX_WIDTH, y);
        }
        // Outside Rectangle
        g.setColor(Color.white);
        g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);

    }

    public void DrawSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        }
        g.setColor(Color.BLACK);
    }

    public void DrawFruit(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLACK);
    }

    public void DrawPortal(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(portal.x * BOX_WIDTH, portal.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.fillOval(portalx.x * BOX_WIDTH, portalx.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.fillOval(portalz.x * BOX_WIDTH, portalz.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLUE);
    }

    public void DrawPortal2(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(portal2.x * BOX_WIDTH, portal2.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLUE);
    }

    public void DrawPortal3(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(portal3.x * BOX_WIDTH, portal3.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
        g.setColor(Color.BLUE);
    }

    public void PlacePortal() {
        Random rand = new Random();
        Random randx = new Random();
        Random randz = new Random();
        int randomX = rand.nextInt(GRID_WIDTH - 1);
        int randomY = rand.nextInt(GRID_HEIGHT - 1);
        int randomg = randx.nextInt(GRID_WIDTH - 1);
        int randomf = randx.nextInt(GRID_HEIGHT - 1);
        int randomx = randx.nextInt(GRID_WIDTH - 1);
        int randomy = randx.nextInt(GRID_HEIGHT - 1);
        Point randomPoint = new Point(randomX, randomY);
        Point randomPoint1 = new Point(randomg, randomf);
        Point randomPoint2 = new Point(randomx, randomy);
        while (snake.contains(randomPoint)) {
            randomX = rand.nextInt(GRID_WIDTH - 1);
            randomY = rand.nextInt(GRID_HEIGHT - 1);
            randomPoint = new Point(randomX, randomY);
        }
        while (snake.contains(randomPoint1)) {
            randomg = rand.nextInt(GRID_WIDTH - 1);
            randomf = rand.nextInt(GRID_HEIGHT - 1);
            randomPoint1 = new Point(randomg, randomf);
        }
        while (snake.contains(randomPoint2)) {
            randomX = rand.nextInt(GRID_WIDTH - 1);
            randomY = rand.nextInt(GRID_HEIGHT - 1);
            randomPoint = new Point(randomX, randomY);
        }
        portal = randomPoint;
        portalx = randomPoint1;
        portalz = randomPoint2;
    }

    public void PlacePortal2() {
        Random rand2 = new Random();
        int randomW = rand2.nextInt(GRID_WIDTH - 1);
        int randomZ = rand2.nextInt(GRID_HEIGHT - 1);
        Point randomPoint2 = new Point(randomW, randomZ);
        while (snake.contains(randomPoint2)) {
            randomW = rand2.nextInt(GRID_WIDTH - 1);
            randomZ = rand2.nextInt(GRID_HEIGHT - 1);
            randomPoint2 = new Point(randomW, randomZ);
        }
        portal2 = randomPoint2;
    }

    public void PlacePortal3() {
        Random rand3 = new Random();
        int randomA = rand3.nextInt(GRID_WIDTH - 1);
        int randomB = rand3.nextInt(GRID_HEIGHT - 1);
        Point randomPoint3 = new Point(randomA, randomB);
        while (snake.contains(randomPoint3)) {
            randomA = rand3.nextInt(GRID_WIDTH - 1);
            randomB = rand3.nextInt(GRID_HEIGHT - 1);
            randomPoint3 = new Point(randomA, randomB);
        }
        portal3 = randomPoint3;
    }

    public void PlaceFruit() {
        Random rand = new Random();
        int randomX = rand.nextInt(GRID_WIDTH - 1);
        int randomY = rand.nextInt(GRID_HEIGHT - 1);
        Point randomPoint = new Point(randomX, randomY);
        while (snake.contains(randomPoint)) {
            randomX = rand.nextInt(GRID_WIDTH - 1);
            randomY = rand.nextInt(GRID_HEIGHT - 1);
            randomPoint = new Point(randomX, randomY);
        }
        fruit = randomPoint;
    }

    @Override
    public void run() {
        while (true) {
            // runs forever
            repaint();
            if (!isInMenu && !EndGame) {
                try {
                    Move();
                } catch (SQLException ex) {
                    Logger.getLogger(PortalCanvas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }

            try {
                Thread.currentThread();
                Thread.sleep(speed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != Direction.SOUTH) {
                    direction = Direction.NORTH;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.NORTH) {
                    direction = Direction.SOUTH;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.WEST) {
                    direction = Direction.EAST;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != Direction.EAST) {
                    direction = Direction.WEST;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (isInMenu) {
                    isInMenu = false;
                    repaint();
                }
                break;
            case KeyEvent.VK_P:
                isInMenu = true;
                break;
            case KeyEvent.VK_SPACE:
                if (EndGame || HelpScreen || SurveyScreen) {
                    EndGame = false;
                    HelpScreen = false;
                    SurveyScreen = false;
                    won = false;
                    DefaultSnake();
                    repaint();
                }
                break;
            case KeyEvent.VK_H:
                HelpScreen = true;
                break;
            case KeyEvent.VK_R:
                SurveyScreen = true;
                String name = JOptionPane.showInputDialog("Thank you for taking time and rating the game. What is your name?");
                String rating = JOptionPane.showInputDialog("How do you rating the game from 1-10 (10 being the best)");
                String gender = JOptionPane.showInputDialog("Gender: M or F?");
                rate = name + ":" + rating + ":" + gender;
                File rateFile = new File("rating.txt"); //rating file---------
                if (!rateFile.exists()) {
                    try {
                        rateFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                FileWriter writeFile = null;
                BufferedWriter writer = null;
                try {
                    writeFile = new FileWriter(rateFile, true);
                    writer = new BufferedWriter(writeFile);
                    writer.write(this.rate);
                    writer.newLine();

                } catch (Exception el) {

                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    } catch (Exception e1) {
                    }
                }
                break;
        }
    }

    public String HighScore() {
        FileReader readFile = null;
        BufferedReader reader = null;
        try {
            readFile = new FileReader("highscore.txt");
            reader = new BufferedReader(readFile);
            return reader.readLine();

        } catch (Exception e) {
            return "N/A:0";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }
}
