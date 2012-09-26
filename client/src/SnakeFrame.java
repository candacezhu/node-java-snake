/* Snake game implementation using node.js and Java
   Copyright 2012 Candace Zhu */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class SnakeFrame extends JFrame{
    private SnakePanel panel;
    private char direction;
    private JLabel snakeTwoScoreLabel, snakeOneScoreLabel, statusLabel;

    public SnakeFrame(SnakeModel model) {
        panel = new SnakePanel(model);
        this.setSize(600, 620);
        
        JPanel scoreHolder = new JPanel(new BorderLayout());
        
        snakeOneScoreLabel = new JLabel("Blue Snake Score: 0");
        snakeOneScoreLabel.setBackground(Color.blue);
        snakeOneScoreLabel.setOpaque(true);

        snakeTwoScoreLabel = new JLabel("Orange Snake Score: 0");
        snakeTwoScoreLabel.setBackground(Color.orange);
        snakeTwoScoreLabel.setOpaque(true);
        
        statusLabel = new JLabel("");
        statusLabel.setBackground(Color.green);
        statusLabel.setOpaque(true);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        scoreHolder.add(snakeOneScoreLabel, BorderLayout.WEST);
        scoreHolder.add(snakeTwoScoreLabel, BorderLayout.EAST);
        scoreHolder.add(statusLabel, BorderLayout.CENTER);
        this.getContentPane().add(scoreHolder, BorderLayout.NORTH);

        direction = 'l';

        this.add(panel);
        addKeyListener(new KeyAdapter() {    
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        direction = 'u';
                        break;
                    case KeyEvent.VK_DOWN:
                        direction = 'd';
                        break;
                    case KeyEvent.VK_RIGHT:
                        direction = 'r';
                        break;
                    case KeyEvent.VK_LEFT:
                        direction = 'l';   
                        break;
                }
            }
        });

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setScoreLabel(String score1, String score2) {
        snakeOneScoreLabel.setText("Blue Snake Score: " + score1);
        snakeTwoScoreLabel.setText("Orange Snake Score: " + score2);
    }

    public void setStatusLabel(String status) {
        statusLabel.setText(status);
    }
    
    public char getDirection() {
        return direction;
    }
}
