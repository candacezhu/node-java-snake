/* Snake game implementation using node.js and Java
   Copyright 2012 Candace Zhu */
   
var net = require('net');
var cleaner = require("./dataCleaner");  

var mongo = require('mongodb/lib/mongodb'),
    Server = mongo.Server,
    Db = mongo.Db;
    
var server = net.createServer();

var dbServer = new Server('localhost', 27017, {auto_reconnect: true});
var db = new Db('mydatabase', dbServer);

server.listen(6666);

server.on('listening', function(){
    makeGame();
    console.log("Snake server is running at http://127.0.0.1:6666/"); 
});

var KEY_SNAKE = 'snake';
var KEY_SNAKE1 = 'snake1';
var KEY_SNAKE2 = 'snake2';

function makeGame() {
    var snake = [[], []];
    var direction = ['d', 'l'];
    var appleEaten = [0,0];
    var appleDots = [];
    var wallDots = [];
    var wallMap = {};
    var appleMap = {};
    var snakeMap = {};

    var wallJSON, appleJSON, snakeJSON;
    
    server.once('connection', function(conn){
        var first = conn;
        first.write("{\"status\" : \"waiting\"}" + "\n");

        server.once('connection', function(conn){
            var second = conn;
            var both = [first, second];

            for(var n = 0; n < 2; n++) {
                both[n].write("{\"status\" : \"start\"}" + "\n");
            }

            var wallCoords = [[5, 5], [6, 5], [7, 5], [5, 6],[5, 7], [5, 8], [5, 9], [39, 43],
            [40, 43],[41, 43], [42, 43], [42, 42], [42, 41], [42, 40], [42, 39], [42, 38]];

            for(var i = 0; i < wallCoords.length; i++) {
                var coord = wallCoords[i];
                wallDots.push(new Point(coord[0], coord[1])); 
            }

            for(var i = 0 ; i < 4; i++) {
                appleDots.push(createAppleDot(wallDots));
            }
            
            for(var j = 0; j < 5; j++) {
                snake[0].push(new Point(25 + j, 30));
                snake[1].push(new Point(25 + j, 10));
            }   

            wallMap["wall"] = wallDots;
            appleMap["apple"] = appleDots;

            var tmp = {};
            tmp.snake1 = snake[0];
            tmp.snake2 = snake[1];
            snakeMap[KEY_SNAKE] = tmp;

            var wallJSON = JSON.stringify(wallMap);
            var appleJSON = JSON.stringify(appleMap);
            var snakeJSON = JSON.stringify(snakeMap);

            for(var m = 0; m < 2; m++) {
                both[m].write(wallJSON + "\n");
                both[m].write(appleJSON + "\n");
                both[m].write(snakeJSON + "\n");
            }

            for(var i = 0; i < both.length; i++) {
                (function(saved_i) {
                    function doWithCleandata(cleanData) {
                        var dirFromClient = JSON.parse(cleanData)["direction"];
                        var target;

                        if(saved_i == 0) {
                            target = direction[0];
                        } else {
                            target = direction[1];
                        }

                        console.log(target + ", " + dirFromClient);
                        if(dirFromClient == 'u' && target != 'd') {
                            target = dirFromClient;
                        } else if(dirFromClient == 'd' && target != 'u') {
                            target = dirFromClient;
                        } else if(dirFromClient == 'l' && target != 'r') {
                            target = dirFromClient;
                        } else if(dirFromClient == 'r' && target != 'l') {
                            target = dirFromClient;
                        } 

                        if(saved_i == 0) {
                            direction[0] = target;
                        } else {
                            direction[1] = target;
                        }           
                    }
                    both[i].on('data', cleaner.wrap(doWithCleandata));               
                })(i);
            }

            var t = setInterval(function() {
                for(var i = 0; i < 2;i++) {
                    moveToNext(snake[i], direction[i]);
                }
                
                if(checkHitBody(snake) || checkHitWalls(wallDots, snake)) {
                    for(var m = 0; m < 2; m++) {
                        both[m].write("{\"status\" : \"gameover\"}" + "\n");            
                    }   
                    clearInterval(t);   
                    makeGame();
                }

                var boolAppleEaten = checkEatApple(appleDots, snake, appleEaten, wallDots);

                snakeMap[KEY_SNAKE][KEY_SNAKE1] = snake[0];
                snakeMap[KEY_SNAKE][KEY_SNAKE2] = snake[1];
                appleMap["apple"] = appleDots;

                var appleJSON = JSON.stringify(appleMap);
                var snakeJSON = JSON.stringify(snakeMap);

                for(var m = 0; m < 2; m++) {
                    both[m].write(snakeJSON + "\n");
                    if(boolAppleEaten) {
                        both[m].write(appleJSON + "\n");
                        var appleEatenJSON = JSON.stringify({"appleEaten":appleEaten});
                        both[m].write(appleEatenJSON + "\n");
                    }
                }
            }, 400);
        });
    });
}

function Point(x, y) {
    this.x = x;
    this.y = y;
    this.getX = function(){
        return this.x;  
    };
    this.getY = function() {
        return this.y;  
    };
    this.toString = function() {
        return this.x + "," + this.y;
    }
    this.equals = function(anotherPoint) {
        return (this.getX() == anotherPoint.getX() && this.getY() == anotherPoint.getY());
    };
}

function createAppleDot(wallDots) {
    while(true) {
        x = produceRandomNumber();
        y = produceRandomNumber();
        var point = new Point(x, y);
        console.log(point.toString());

        var pointOk = true;
        for(var i = 0; i < wallDots.length; i++) {
            if(point.equals(wallDots[i])) {
                pointOk = false;
                break;
            }
        }
        if(pointOk) {
            return point;
        }   
    }
}

function moveToNext(snake, direction) {
    for(var i = snake.length - 1; i > 0; i--) {
        snake[i] =  new Point(snake[i - 1].getX(), snake[i - 1].getY());
    }
    var snakeX = snake[0].getX();
    var snakeY = snake[0].getY();
    var newSnakeX, newSnakeY;

    switch(direction) {
        case 'u':
            newSnakeX = snakeX;
            if(snake[0].getY() == 0) {
                newSnakeY = 50;
            } else {
                newSnakeY = snakeY - 1;
            }
            break;
        case 'd':
            newSnakeX = snakeX;

            if(snake[0].getY() == 50) {
                newSnakeY = 0;
            } else {
                newSnakeY = snakeY + 1;
            }
            break;
        case 'l':
            newSnakeY = snakeY;
            if(snake[0].getX() == 0) {
                newSnakeX = 50;
            } else {
                newSnakeX = snakeX - 1;
            }
            break;
        case 'r':
            newSnakeY = snakeY;
            if(snake[0].getX() == 50) {
                newSnakeX = 0;
            } else {
                newSnakeX = snakeX + 1;
            }
            break;
    }   
    snake[0] = new Point(newSnakeX, newSnakeY);
}

function checkHitBody(snake) {
    var countMap = {};
    for(var m = 0; m < 2; m++) {
        for(var n = 0; n < snake[m].length; n++) {
            var index = snake[m][n].getX() * 50 + snake[m][n].getY();
            if(index.toString() in countMap) return true;
            countMap[index] = 1;
        }
    }
    return false;
}

function checkHitWalls(wallDots, snake) {
    for(var p = 0; p < wallDots.length; p++) {
        if(snake[0][0].equals(wallDots[p]) || snake[1][0].equals(wallDots[p])) {
            return true;
        }
    }
    return false;
}

function checkEatApple(appleDots, snake, appleEaten, wallDots) {
    for(var i = 0; i < appleDots.length; i++) {
        for(var snakeIndex = 0; snakeIndex < snake.length; snakeIndex++) {
            if(snake[snakeIndex][0].equals(appleDots[i])) {
                appleEaten[snakeIndex]++;

                db.open(function(err, db) {
                    if(!err) {
                        console.log("connected...");
                        db.collection('newOne', function(err, collection) {                      
                            console.log("error: " + err);
                            var playerName;
                            if(snakeIndex == 0) {
                                playerName = "player1";
                            } else if(snakeIndex == 1) {
                                playerName = "player2";
                            }
                            collection.update({'name':playerName}, {$inc:{'score':1}}, {safe:true}, function(err, result) {});
                        });
                    }
                    db.close();
                });

                var lastX, sndLastX, lastY, sndLastY, thisSnake, thisSnakeLength;
                thisSnake = snake[snakeIndex];
                thisSnakeLength = thisSnake.length;

                lastX = thisSnake[thisSnakeLength - 1].getX();
                lastY = thisSnake[thisSnakeLength - 1].getY();
                sndLastX = thisSnake[thisSnakeLength - 2].getX();
                sndLastY = thisSnake[thisSnakeLength - 2].getY();

                if(lastX == sndLastX) {
                    if(lastY < sndLastY) {
                        thisSnake.push(new Point(lastX, lastY - 1));
                    } else {
                        thisSnake.push(new Point(lastY, lastY + 1));
                    }
                } else if(lastY == sndLastY) {
                    if(lastX < sndLastX) {
                        thisSnake.push(new Point(lastX - 1, lastY));
                    } else {
                        thisSnake.push(new Point(lastX + 1, lastY));
                    }
                }      
                appleDots[i] = createAppleDot(wallDots);
                return true;
            }
        }
    }
}

function produceRandomNumber() {
    return Math.floor((Math.random() * 45 ) + 5);
}