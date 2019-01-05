package checkers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Game {
    // 8x8 board composed of integers from -1 to 4.
    // -1 invalid board space, 0 empty board space
    // 1 -> player 1 normal pieces, 2 -> player 2 normal pieces, 3 -> player 1 kings, 4 -> player 2 kings
    int[][] board;
    // set default starting player and time limit
    int currentPlayer = 1;
    int timeLimit = 3;
    // board colors
    public static final String ANSI_RED = "\u001B[91m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    public static final String ANSI_CYAN = "\u001B[96m";
    public static final String ANSI_RESET = "\u001B[0m";
    // empty constructor
    public Game() {

    }
    // copy constructor, used for when AI needs to apply minimax algorithm
    public Game(Game game) {
        this.board = new int[game.board.length][game.board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = game.board[i][j];
            }
        }
        this.currentPlayer = game.currentPlayer;
        this.timeLimit = game.timeLimit;
    }
    // initialize board as new game board
    void newGame() {
        board = new int[8][8];
        // initialize the board with null spaces as -1, empty spaces as 0, player 1
        // as 1 (red checkers) and player 2 as 2 (blue checkers)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j += 2) {
                if ((i & 1) == 1) {
                    board[i][j + 1] = -1;
                    board[i][j] = 1;
                } else {
                    board[i][j] = -1;
                    board[i][j + 1] = 1;
                }
            }
        }

        for (int i = 3; i < 5; i++) {
            for (int j = 0; j < 8; j += 2) {
                if ((i & 1) == 1) {
                    board[i][j + 1] = -1;
                    board[i][j] = 0;
                } else {
                    board[i][j] = -1;
                    board[i][j + 1] = 0;
                }
            }
        }

        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j += 2) {
                if ((i & 1) == 1) {
                    board[i][j + 1] = -1;
                    board[i][j] = 2;
                } else {
                    board[i][j] = -1;
                    board[i][j + 1] = 2;
                }
            }
        }
    }
    // load games into board[][] and fill other variables using .txt files following Sable's board format
    boolean loadGameBoard(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            board = new int[8][8];
            for (int i = 0; i < 8; i++) {
                String[] boardValues = reader.readLine().trim().split("\\s+");
                if ((i & 1) == 0) {
                    for (int j = 0; j < 8; j += 2) {
                        board[i][j] = -1;
                        board[i][j + 1] = Integer.valueOf(boardValues[j / 2]);
                    }
                } else {
                    for (int j = 0; j < 8; j += 2) {
                        board[i][j] = Integer.valueOf(boardValues[j / 2]);
                        board[i][j + 1] = -1;
                    }
                }
            }
            String currPlayer = reader.readLine();
            currentPlayer = Integer.valueOf(currPlayer);
            String cpuTimeLimit = reader.readLine();
            timeLimit = Integer.valueOf(cpuTimeLimit);
            return true;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("File not found.");
            return false;
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("IO exception occurred.");
            return false;
        }
    }
    // print board corresponding to board[][]
    void printBoard() {
        System.out.println("      0      1      2      3      4      5      6      7   ");
        for (int i = 0; i < 8; i++) {
            System.out.print("   ");
            if ((i & 1) == 0) { // print even rows
                // padding with extra row of black/white space
                for (int j = 0; j < 4; j++) {
                    System.out.print(ANSI_WHITE_BACKGROUND + "       " + ANSI_RESET + "       ");
                }

                System.out.println("");
                System.out.print(" " + (i) + " ");

                for (int j = 0; j < 8; j++) {
                    printCorrectPiece(board[i][j]);
                }

                System.out.println("");
                System.out.print("   ");

                // padding with extra row of black/white space
                for (int j = 0; j < 4; j++) {
                    System.out.print(ANSI_WHITE_BACKGROUND + "       " + ANSI_RESET + "       ");
                }

            } else { //print odd rows
                // padding with extra row of black/white space
                for (int j = 0; j < 4; j++) {
                    System.out.print("       " + ANSI_WHITE_BACKGROUND + "       " + ANSI_RESET);
                }

                System.out.println("");
                System.out.print(" " + (i) + " ");

                for (int j = 0; j < 8; j++) {
                    printCorrectPiece(board[i][j]);
                }

                System.out.println("");
                System.out.print("   ");

                // padding with extra row of black/white space
                for (int j = 0; j < 4; j++) {
                    System.out.print("       " + ANSI_WHITE_BACKGROUND + "       " + ANSI_RESET);
                }
            }

            System.out.println("");
        }
    }
    // helper function for printBoard()
    void printCorrectPiece(int val) {
        switch (val) {
            case -1:
                System.out.print(ANSI_WHITE_BACKGROUND + "       " + ANSI_RESET);
                break;
            case 0:
                System.out.print("       ");
                break;
            case 1:
                System.out.print(ANSI_RED + "   -   " + ANSI_RESET);
                break;
            case 2:
                System.out.print(ANSI_CYAN + "   .   " + ANSI_RESET);
                break;
            case 3:
                System.out.print(ANSI_RED + "   *   " + ANSI_RESET);
                break;
            case 4:
                System.out.print(ANSI_CYAN + "   0   " + ANSI_RESET);
                break;

        }

    }
    // function to iterate over all pieces in the board and get legal moves for current player
    List<Move> getLegalMoves(int[][] state) {
        List<Move> slideMoves = new ArrayList<Move>();
        List<Move> jumpMoves = new ArrayList<Move>();
        // iterate over the entire board, check for legal moves for all pieces of current player
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (currentPlayer == 1) {
                    if (board[i][j] == 1 || board[i][j] == 3) {
                        getJumps(jumpMoves, null, board[i][j], i, j, state);
                        // stop looking for slide moves if we find any jump moves
                        if (jumpMoves.isEmpty()) {
                            getSlides(slideMoves, board[i][j], i, j, board);
                        }
                    }
                } else {
                    if (board[i][j] == 2 || board[i][j] == 4) {
                        getJumps(jumpMoves, null, board[i][j], i, j, state);
                        // stop looking for slide moves if we find any jump moves
                        if (jumpMoves.isEmpty()) {
                            getSlides(slideMoves, board[i][j], i, j, board);
                        }

                    }
                }
            }
        }
        return jumpMoves.isEmpty() ? slideMoves : jumpMoves;
    }
    // function which mutates the moves list it takes as a parameter
    void getSlides(List<Move> moves, int pieceType, int startRow, int startCol, int[][] state) {
        ArrayList<Integer> endRow = new ArrayList<Integer>();
        ArrayList<Integer> endCol = new ArrayList<Integer>();

        switch (pieceType) {
            case 1:
                endRow.add(startRow + 1);
                endRow.add(startRow + 1);
                endCol.add(startCol + 1);
                endCol.add(startCol - 1);
                break;
            case 2:
                endRow.add(startRow - 1);
                endRow.add(startRow - 1);
                endCol.add(startCol + 1);
                endCol.add(startCol - 1);
                break;
            case 3:
            case 4:
                endRow.add(startRow + 1);
                endRow.add(startRow + 1);
                endRow.add(startRow - 1);
                endRow.add(startRow - 1);
                endCol.add(startCol + 1);
                endCol.add(startCol - 1);
                endCol.add(startCol + 1);
                endCol.add(startCol - 1);
                break;
        }

        int numMoves = endRow.size();

        for (int i = 0; i < numMoves; i++) {
            // check if inside the board
            if (endRow.get(i) < 0 || endRow.get(i) > 7 || endCol.get(i) < 0 || endCol.get(i) > 7) continue;
            // check if end position is occupied
            if (state[endRow.get(i)][endCol.get(i)] != 0) continue;
            // move was legal so add it to list
            moves.add(new Move(startRow, startCol, endRow.get(i), endCol.get(i), state));
        }
    }
    // function which mutates the moves list it takes as a parameter
    void getJumps(List<Move> moves, Move move, int pieceType, int startRow, int startCol, int[][] state) {
        // endRow/endCol list to add all possible squares to move to from each piece
        // captureRow/captureCol list to add all possible squares that can be captured
        ArrayList<Integer> endRow = new ArrayList<Integer>();
        ArrayList<Integer> endCol = new ArrayList<Integer>();
        ArrayList<Integer> captureRow = new ArrayList<Integer>();
        ArrayList<Integer> captureCol = new ArrayList<Integer>();

        switch (pieceType) {
            case 1:
                endRow.add(startRow + 2);
                endRow.add(startRow + 2);
                endCol.add(startCol - 2);
                endCol.add(startCol + 2);
                captureRow.add(startRow + 1);
                captureRow.add(startRow + 1);
                captureCol.add(startCol - 1);
                captureCol.add(startCol + 1);
                break;
            case 2:
                endRow.add(startRow - 2);
                endRow.add(startRow - 2);
                endCol.add(startCol - 2);
                endCol.add(startCol + 2);
                captureRow.add(startRow - 1);
                captureRow.add(startRow - 1);
                captureCol.add(startCol - 1);
                captureCol.add(startCol + 1);
                break;
            case 3:
            case 4:
                endRow.add(startRow + 2);
                endRow.add(startRow + 2);
                endRow.add(startRow - 2);
                endRow.add(startRow - 2);
                endCol.add(startCol - 2);
                endCol.add(startCol + 2);
                endCol.add(startCol - 2);
                endCol.add(startCol + 2);
                captureRow.add(startRow + 1);
                captureRow.add(startRow + 1);
                captureRow.add(startRow - 1);
                captureRow.add(startRow - 1);
                captureCol.add(startCol - 1);
                captureCol.add(startCol + 1);
                captureCol.add(startCol - 1);
                captureCol.add(startCol + 1);
                break;
        }

        int numMoves = endRow.size();
        boolean anyValidMoves = false;
        boolean[] whichAreValid = new boolean[numMoves];

        for (int i = 0; i < numMoves; i++) {
            // check if inside the board
            if (endRow.get(i) < 0 || endRow.get(i) > 7 || endCol.get(i) < 0 || endCol.get(i) > 7) continue;
            // check if end position is occupied
            if (move != null) {
                // check if end position is occupied but allow piece to land on initial position
                if (state[endRow.get(i)][endCol.get(i)] != 0 && state[endRow.get(i)][endCol.get(i)] != state[move.initialRow][move.initialCol]) continue;
                // check if we're trying to capture a piece we've already captured in current move
                if (move.capturedSquares.contains(new Pair(captureRow.get(i), captureCol.get(i)))) continue;
            }
            else {
                // if move is null, make sure end position isn't occupied
                if (state[endRow.get(i)][endCol.get(i)] != 0) continue;
            }
            // check if captured positions were occupied by the opposite player
            if (currentPlayer == 1 && !(state[captureRow.get(i)][captureCol.get(i)] == 2 || state[captureRow.get(i)][captureCol.get(i)] == 4)) continue;

            if (currentPlayer == 2 && !(state[captureRow.get(i)][captureCol.get(i)] == 1 || state[captureRow.get(i)][captureCol.get(i)] == 3)) continue;

            // if we got this far, it means the move is valid
            anyValidMoves = true;
            whichAreValid[i] = true;
        }

        if (move != null && !anyValidMoves) {
            moves.add(move);
            return;
        }

        if (move == null && anyValidMoves) {
            for (int i = 0; i < numMoves; i++) {
                if (whichAreValid[i]) {
                    Move newMove = new Move(startRow, startCol, endRow.get(i), endCol.get(i), state);
                    newMove.initialRow = startRow;
                    newMove.initialCol = startCol;
                    newMove.startRow = startRow;
                    newMove.startCol = startCol;
                    newMove.endRow = endRow.get(i);
                    newMove.endCol = endCol.get(i);
                    newMove.listCaptureRow.add(captureRow.get(i));
                    newMove.listCaptureCol.add(captureCol.get(i));
                    newMove.listVisitedRow.add(endRow.get(i));
                    newMove.listVisitedCol.add(endCol.get(i));
                    newMove.capturedSquares.add(new Pair(captureRow.get(i), captureCol.get(i)));
                    getJumps(moves, newMove, pieceType, newMove.endRow, newMove.endCol, state);
                }
            }
        }
        if (move != null && anyValidMoves) {
            for (int i = 0; i < numMoves; i++) {
                if (whichAreValid[i]) {
                    Move newMove = new Move(move);
                    newMove.startRow = startRow;
                    newMove.startCol = startCol;
                    newMove.endRow = endRow.get(i);
                    newMove.endCol = endCol.get(i);
                    newMove.listCaptureRow.add(captureRow.get(i));
                    newMove.listCaptureCol.add(captureCol.get(i));
                    newMove.listVisitedRow.add(endRow.get(i));
                    newMove.listVisitedCol.add(endCol.get(i));
                    newMove.capturedSquares.add(new Pair(captureRow.get(i), captureCol.get(i)));
                    getJumps(moves, newMove, pieceType, newMove.endRow, newMove.endCol, state);
                }
            }
        }
        return;
    }
    // function to a apply a move to the board
    void applyMove(Move move, int[][] state) {
        // handle slide move
        if (move.listCaptureRow.isEmpty()) {
            // update end position to match current piece
            state[move.endRow][move.endCol] = state[move.startRow][move.startCol];
            // make the piece a king if it is in the back row of the opposite team's side
            if (state[move.startRow][move.startCol] == 1 && move.endRow == 7) {
                state[move.endRow][move.endCol] += 2;
            }
            if (state[move.startRow][move.startCol] == 2 && move.endRow == 0) {
                state[move.endRow][move.endCol] += 2;
            }
            // clear initial position
            state[move.startRow][move.startCol] = 0;
        }
        // handle jump move
        else {
            // clear capture positions
            for (int i = 0; i < move.listCaptureRow.size(); i++) {
                state[move.listCaptureRow.get(i)][move.listCaptureCol.get(i)] = 0;
            }
            // update end position to match current piece
            state[move.endRow][move.endCol] = state[move.initialRow][move.initialCol];
            // make the piece a king if it is in the back row of the opposite team's side
            if (state[move.initialRow][move.initialCol] == 1 && move.endRow == 7) {
                state[move.endRow][move.endCol] += 2;
            }
            if (state[move.initialRow][move.initialCol] == 2 && move.endRow == 0) {
                state[move.endRow][move.endCol] += 2;
            }
            //  clear initial position
            state[move.initialRow][move.initialCol] = 0;
        }
        // switch the currentPlayer
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        //System.out.println("Current Player: " + currentPlayer);
    }
    // print moves and in the case of jumps show intermediate jumps
    void printListMoves(List<Move> movesList) {
        if (movesList.get(0).listCaptureRow.isEmpty()) {
            for (int i = 0; i < movesList.size(); i++) {
                System.out.println("Move " + i + ": (" + movesList.get(i).startRow + "," + movesList.get(i).startCol + ") --> (" + movesList.get(i).endRow + "," + movesList.get(i).endCol + ")");

            }
        } else {
            for (int i = 0; i < movesList.size(); i++) {
                System.out.println("");
                System.out.print("Move " + i + ": (" + movesList.get(i).initialRow + "," + movesList.get(i).initialCol + ")");
                for (int j = 0; j < movesList.get(i).listVisitedRow.size(); j++) {
                    System.out.print(" --> (" + movesList.get(i).listVisitedRow.get(j) + "," + movesList.get(i).listVisitedCol.get(j) + ")");
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }
    // print individual move
    void printMove(Move move) {
        if (move.listCaptureRow.isEmpty()){
            System.out.println("Move " + ": (" + move.startRow + "," + move.startCol + ") --> (" + move.endRow + "," + move.endCol + ")");
        } else {
            System.out.print("Move " + ": (" + move.initialRow + "," + move.initialCol + ")");
            for (int j = 0; j < move.listVisitedRow.size(); j++) {
                System.out.print(" --> (" + move.listVisitedRow.get(j) + "," + move.listVisitedCol.get(j) + ")");
            }
            System.out.println("");
        }
        System.out.println("");
    }
    // print help note which shows what symbols represent the pieces of each player
    void printNote() {
        System.out.println("Player 1 is" + ANSI_RED + "  -  " + ANSI_RESET + "(normal piece) and" + ANSI_RED + "  *  " + ANSI_RESET + "(king)");
        System.out.println("Player 2 is" + ANSI_CYAN + "  .  " + ANSI_RESET + "(normal piece) and" + ANSI_CYAN + "  0  " + ANSI_RESET + "(king)");
        System.out.println("");
    }
}
