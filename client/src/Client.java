/* Snake game implementation using node.js and Java
   Copyright 2012 Candace Zhu */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Client {
    private final static String KEY_SNAKE = "snake";
    private final static String KEY_SNAKE1 = "snake1";
    private final static String KEY_SNAKE2 = "snake2";
    private final static String KEY_WALL = "wall";
    private final static String KEY_APPLE = "apple";
    private final static String KEY_STATUS = "status";
    private final static String KEY_WAITING = "waiting";
    private final static String KEY_START = "start";
    private final static String KEY_GAMEOVER = "gameover";
    private final static String KEY_APPLEEATEN = "appleEaten";


    public static void main(String[] args) {
        Socket clientSocket = null;
        SnakeFrame frame = null;
        SnakeModel model = new SnakeModel();

        int player = 0;
        char preDirection = 'l';

        try {
            clientSocket = new Socket("localhost", 6666);
            System.out.println("connecting on 6666");
        } catch (IOException e) {
            handleException(e);
        }

        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            String strFromServer = "";
            while(true) {
                strFromServer = inFromServer.readLine();
                JSONObject jsonObj = new JSONObject(strFromServer);

                if(jsonObj.has(KEY_STATUS)) {
                    String status = (String) jsonObj.get(KEY_STATUS);
                    if(status.equals(KEY_WAITING)) {
                        frame = new SnakeFrame(model);
                        player = 1;
                        frame.setStatusLabel("Waiting for another player...");
                    } else if(status.equals(KEY_START)) {
                        if(player != 1) {
                            frame = new SnakeFrame(model);
                        } 
                        frame.setStatusLabel("playing...");
                    } else if(status.equals(KEY_GAMEOVER)) {                    
                        frame.setStatusLabel("Game Over");
                    }
                } else if(jsonObj.has(KEY_WALL)) {
                    JSONArray array = jsonObj.getJSONArray(KEY_WALL);
                    model.setWallDots(getListOfPoints(array));
                } else if(jsonObj.has(KEY_APPLE)) {
                    JSONArray array = jsonObj.getJSONArray(KEY_APPLE);
                    model.setAppleDots(getListOfPoints(array));
                } else if(jsonObj.has(KEY_SNAKE)) {
                    JSONObject twoSnakes = jsonObj.getJSONObject(KEY_SNAKE);
                    JSONArray arraySnake1 = twoSnakes.getJSONArray(KEY_SNAKE1);
                    JSONArray arraySnake2 = twoSnakes.getJSONArray(KEY_SNAKE2);
                    model.setSnakeOneDots(getListOfPoints(arraySnake1));
                    model.setSnakeTwoDots(getListOfPoints(arraySnake2));
                } else if(jsonObj.has(KEY_APPLEEATEN)) {
                    JSONArray scores = jsonObj.getJSONArray(KEY_APPLEEATEN);
                    frame.setScoreLabel(scores.get(0) + "", scores.get(1) + "");
                }

                if(frame != null) {
                    char direction = frame.getDirection();
                    if(preDirection != direction) {
                        JSONObject jsonDir = new JSONObject("{\"direction\" : " + direction + "}");
                        outToServer.writeBytes(jsonDir + "\n");    
                        preDirection = direction;
                    }
                    frame.repaint();
                }
            }
        } catch (IOException e) {
            handleException(e);
        } catch (JSONException e) {
            handleException(e);
        }
    }

    private static List<Point> getListOfPoints(JSONArray arrayDots) throws JSONException  {
        List<Point> listDots = new ArrayList<Point>();
        for(int i = 0; i < arrayDots.length(); i++) {
            JSONObject dotCoord = (JSONObject)arrayDots.get(i);
            Point point = new Point(dotCoord.getInt("x"),dotCoord.getInt("y"));
            listDots.add(point);
        }
        return listDots;
    }

    private static void handleException(Exception e) {
        JOptionPane.showMessageDialog(null, "Sorry, an unexpected error has occurred.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        throw new RuntimeException(e);
    }
}
