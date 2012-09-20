
## How To Play

This is a snake game. It allows two players to simultaneously play a game over a network. Each player's snake is able to:

   * eat apples (red dots) to gain points and grow longer
   * avoid running into itself and another snake
   * avoid hitting the walls (gray dots.)

Players use arrow keys on the keyboard to control the directions of their snakes.

## Architecture

snakegameserver.js is the server-side portion written in node.js.

All other files are client-side portion written in java. Data are directly transferred between server-side and client-side by snakegameserver.js and Client.java. They communicate in JSON format.

At the client-side,

   * Client.java talks to the server directly. It gets data from the server and then sends it to SnakeModel.java. It also gets directions of snakes from the SnakeFrame.java and then sends them to the server.
   * SnakeModel.java stores the data from the client.
   * SnakePanel.java gets data from SnakeModel.java, and contains the logic to paint the grid containing snakes, apples and walls.
   * SnakeFrame.java sets directions of snakes by receiving the keyboard events from the players, and then sends the directions to Client.java. It calls SnakePanel.java to print all elements on the panel.
