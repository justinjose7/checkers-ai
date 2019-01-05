# Checkers-AI
AI for Checkers written in Java.

## Game Features
* Option for user to verse AI
* Option to simulate AI versus AI
* Users select move from a list of legal moves
* Users can define the amount of time AI can search for its best move
* Option to load an existing game board

## Screenshots
Start new game

![Demo-01](https://github.com/justinjose7/checkers-ai/blob/master/readme_assets/begin_game_01.png)

Player turn

![Demo-02](https://github.com/justinjose7/checkers-ai/blob/master/readme_assets/next_move_02.png)


## Compilation/Usage Instructions
* The program was originally compiled in Cygwin (javac and JRE required, JDK 11 used). 
* Simply run ```make``` to begin program.
* To load existing boards into the program, place the corresponding .txt files in the directory where the other
sample boards are located.

## Game Implementation Details

+ The first major decision in the game design was determining how to uniquely identify one game state and how to
perform actions on that state. In order to represent game state, the Game.java class has been implemented with
global variables including the current player variable which indicated whose turn it was for that specific game state
and the 2D board array (8x8) which identified what spaces were occupied/unoccupied by pieces and spaces which
were invalid to move into.
* To accomplish the getLegalMoves function, first the Move class had to be written. With this implementation, each
Move consists of an initial row, initial column, start row, start column, end row, end column, list of captured rows, a
list of captured columns, a list of visited rows, a list of visited columns, a hash set which contained pairs of captured
piece positions, and the 2D board state. The majority of the complexity in the Move class arose from implementing
the getJumps function. To recursively find all possible jumps from one position on the board, the ‘start…’ variables
were required to mutate each jump’s next starting state while the ‘initial…’ variables remembered the 1st jump
position. A hash-set was used to add speed to determining whether a piece was illegally jumping over a piece it had
already captured earlier. The ‘…capture…’ lists were used to iterate over captured squares and set their values to 0,
indicating an empty square. The end position was set to the value at the initial position and the value of the initial
position was then set to 0. The ‘…visited…’ lists were used to print out the intermediate jumps. A copy constructor
for the Move class was essential for finding moves with multiple jumps.
* Two separate functions (getJumps and getSlides) were written to encompass all possible moves. The two functions
took in a list as a parameter and mutated it with additional moves if additional moves were found. An applyMove
function was created in the Game.java class to mutate the board’s state and toggle the current player. The Game
class was then instantiated in the Engine.java class and additional logic was written to allow players to play against
each other.

## AI Implementation Details

* To accomplish the AI functionality, the Computer.java class was written whose constructor is used to specify the
side the computer was playing for (maximizing player), the time limit, and the heuristic used. The alpha beta search
implementation followed Russell and Norvig’s pseudocode. A copy constructor was written for the Game class to
allow passing a copy of the game state to the minimax search algorithm to avoid wrongly mutating the original game
state.
* For the AI heuristic, a couple of factors were taken into account: the number of normal pieces/kings each player
has, the number of defending neighbors each normal piece has, the number of pieces protecting their respective
backrow, the closeness of normal pieces to becoming kings, closeness of pieces to the middle of the board, and the
number of moves each player has for the given board. Also if the maximizing player was ahead a bonus was added
for forcing trades (by keeping track of the ratio of player pieces to opponent pieces). A deduction was also added
for worsening the ratio if the player was behind.
* The heuristic has allowed the minimax algorithm to find moves which push kings out of the double corner in
endgame. However, at times, the number of moves to reach that point can be large depending on the initial distance
between the opponent’s king and the player’s kings.


## File Descriptions
* Game.java contains the game state variables and functions used to get legal moves, apply a move, print the board,
initialize a new game, and load an existing game board from a .txt file.
* Computer.java contains the alpha beta search implementation functions, the heuristic function, and helper
functions for the heuristic. The class can be used to instantiate a new Computer player with a side.
* Move.java makes the getLegalMoves function in the Game class possible.
* Pair.java is used for inserting captured positions into the hash-set for each Move
* Engine.java contains the main method which handles the game logic and interaction between players
