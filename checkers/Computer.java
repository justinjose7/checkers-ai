package checkers;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Random;

public class Computer {
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    // maxDepth used to keep track of depth reached using iterative deepening search
    public int maxDepth;
    // default time limit
    public int timeLimit = 2;
    // heuristicType specifies which heuristic is used for the computer
    public int heuristicType;
    // startTime and currentTime used to ensure search doesn't exceed time limit
    public long startTime;
    public long currentTime;
    public boolean outOfTime;
    // used to keep track of number of pieces on board prior to search
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    // evaluation function for mini-max.
    public Computer(int maximizingPlayer, int heuristicType) {
        this.maximizingPlayer = maximizingPlayer;
        this.heuristicType = heuristicType;
    }
    // iterative deepening mini-max search with alpha beta pruning
    public Move alphaBetaSearch(Game game) {
        // begin by getting start time for search
        Date date = new Date();
        startTime = date.getTime();
        // initialize necessary variables
        getBoardStatus(game);
        outOfTime = false;
        Random random = new Random();
        int bestMoveVal = 0;
        int depthReached = 0;
        Move bestMove = null;
        List<Move> listBestMovesCurrentDepth;
        List<Move> legalMovesList = game.getLegalMoves(game.board);
        // just return move if only 1 move available
        if (legalMovesList.size() == 1) {
            System.out.println("Searched to depth 0 in 0 seconds.");
            return legalMovesList.get(0);
        }
        // actual search (iterative deepening mini-max w/ alpha beta pruning).
        for (maxDepth = 0; maxDepth < 15 && !outOfTime; maxDepth++) {
            listBestMovesCurrentDepth = new ArrayList<Move>();
            int bestVal = Integer.MIN_VALUE;
            for (Move move : legalMovesList) {
                Game copy = new Game(game);
                copy.applyMove(move, copy.board);
                int min = minVal(copy, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                if (outOfTime) break;
                // System.out.println("Possible move val: " + min);
                if (min == bestVal) {
                    listBestMovesCurrentDepth.add(move);
                }
                if (min > bestVal) {
                    listBestMovesCurrentDepth.clear();
                    listBestMovesCurrentDepth.add(move);
                    bestVal = min;
                }
                if (bestVal == Integer.MAX_VALUE) break;
            }
            if (!outOfTime) {
                int chosenMove = random.nextInt(listBestMovesCurrentDepth.size());
                bestMove = listBestMovesCurrentDepth.get(chosenMove);
                depthReached = maxDepth;
                bestMoveVal = bestVal;
            }
            if (bestMoveVal == Integer.MAX_VALUE) break;
        }
        // System.out.println("Best move value " + bestMoveVal);
        System.out.println("Searched to depth " + depthReached + " in " + ((currentTime-startTime)/1000) + " seconds.");
        return bestMove;
    }
    // check if we've reached leaf nodes or maximum depth
    public boolean cutoffTest(int numMoves, int depth) {
        if (numMoves == 0 || depth == maxDepth){
            return true;
        }
        return false;
    }
    // eval function decides the heuristic used to calculate value of leaf nodes
    public int evalFcn(Game game) {
        switch (heuristicType) {
            case 1:
                return easyHeuristic(game);
            case 2:
                return mediumHeuristic(game);
            case 3:
                return hardHeuristic(game);
            default:
                return hardHeuristic(game);
        }
    }
    // heuristic which takes into account: number of pieces, defending neighbors, backrow protectors, closeness to becoming king,
    // and number of moves each player has for given board. also forces trades when ahead.
    public int hardHeuristic(Game game) {
        int numRows = game.board.length;
        int numCols = game.board[0].length;
        int boardVal = 0;
        int cntAllyPieces = 0;
        int cntAllyKings = 0;
        int cntOppPieces = 0;
        int cntOppKings = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (maximizingPlayer == 1){
                    switch(game.board[i][j]) {
                        case 1:
                            cntAllyPieces++;
                            boardVal += numDefendingNeighbors(i, j, game.board) * 50 + backBonus(i) + (15 * i) + middleBonus(i, j);
                            break;
                        case 2:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, game.board) * 50 + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);
                            break;
                        case 3:
                            cntAllyKings++;
                            boardVal += middleBonus(i,j);
                            break;
                        case 4:
                            cntOppKings++;
                            boardVal -= middleBonus(i,j);
                            break;
                    }
                } else {
                    switch(game.board[i][j]) {
                        case 1:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, game.board) * 50 + backBonus(i)  + (15 * i) + middleBonus(i, j);
                            break;
                        case 2:
                            cntAllyPieces++;
                            boardVal += numDefendingNeighbors(i, j, game.board) * 50 + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);;
                            break;
                        case 3:
                            cntOppKings++;
                            boardVal -= middleBonus(i, j);
                            break;
                        case 4:
                            cntAllyKings++;
                            boardVal += middleBonus(i, j);
                            break;
                    }
                }
            }
        }

        // force trades when ahead
        if (numAllyPieces + numAllyKings > numOppPieces + numOppKings && cntOppPieces + cntOppKings != 0 && numOppPieces + numOppKings != 0 && numOppKings != 1) {
            if ((cntAllyPieces + cntAllyKings)/(cntOppPieces + cntOppKings) > (numAllyPieces + numAllyKings)/(numOppPieces + numOppKings)) {
                boardVal += 150;
            } else {
                boardVal -= 150;
            }
        }

        boardVal += 600 * cntAllyPieces + 1000 * cntAllyKings - 600 * cntOppPieces - 1000 * cntOppKings;

        // heavy computation to see how many moves each player has, dont do until players have under 6 pieces
        if (numOppPieces + numOppKings < 6 || numAllyPieces + numAllyKings < 6) {
            int originalPlayer = game.currentPlayer;
            game.currentPlayer = 1;
            List<Move> player1Moves = game.getLegalMoves(game.board);
            game.currentPlayer = 2;
            List<Move> player2Moves = game.getLegalMoves(game.board);
            game.currentPlayer = originalPlayer;

            if (player1Moves.isEmpty()) {
                return maximizingPlayer == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }

            if (player2Moves.isEmpty()) {
                return maximizingPlayer == 2 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
        }

        if (cntOppPieces + cntOppKings == 0 && cntAllyPieces + cntAllyKings > 0) {
            boardVal = Integer.MAX_VALUE;
        }

        if (cntAllyPieces + cntAllyKings == 0 && cntOppPieces + cntOppKings > 0) {
            boardVal -= Integer.MIN_VALUE;
        }

        return boardVal;
    }

    public int mediumHeuristic(Game game) {
        int numRows = game.board.length;
        int numCols = game.board[0].length;
        int boardVal = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (maximizingPlayer == 1){
                    switch(game.board[i][j]) {
                        case 1:
                            boardVal += 3 + (i * 0.5) + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal += 1;
                            }
                            if (i == 0) {
                                boardVal += 2;
                            }
                            break;
                        case 2:
                            boardVal -= 3 + ((7 - i) * 0.5) + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal -= 1;
                            }
                            if (i == 7) {
                                boardVal -= 2;
                            }
                            break;
                        case 3:
                            boardVal += 5 + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal += 1;
                            }
                            if (i == 0) {
                                boardVal += 2;
                            }
                            break;
                        case 4:
                            boardVal -= 5 + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal -= 1;
                            }
                            if (i == 7) {
                                boardVal -= 2;
                            }
                            break;
                    }
                } else {
                    switch(game.board[i][j]) {
                        case 1:
                            boardVal -= 3 + (i * 0.5) + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal -= 1;
                            }
                            if (i == 0) {
                                boardVal -= 2;
                            }
                            break;
                        case 2:
                            boardVal += 3 + ((7 - i) * 0.5) + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal += 1;
                            }
                            if (i == 7) {
                                boardVal += 2;
                            }
                            break;
                        case 3:
                            boardVal -= 5 + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal -= 1;
                            }
                            if (i == 0) {
                                boardVal -= 2;
                            }
                            break;
                        case 4:
                            boardVal += 5 + numDefendingNeighbors(i, j, game.board);
                            if (j == 0 || j == 8) {
                                boardVal += 1;
                            }
                            if (i == 7) {
                                boardVal += 2;
                            }
                            break;
                    }
                }
            }
        }
        return boardVal;
    }

    public int easyHeuristic(Game game) {
        int numRows = game.board.length;
        int numCols = game.board[0].length;
        int boardVal = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (maximizingPlayer == 1){
                    switch(game.board[i][j]) {
                        case 1:
                            boardVal += 3;
                            break;
                        case 2:
                            boardVal -= 3;
                            break;
                        case 3:
                            boardVal += 5;
                            break;
                        case 4:
                            boardVal -= 5;
                            break;
                    }
                } else {
                    switch(game.board[i][j]) {
                        case 1:
                            boardVal -= 3;
                            break;
                        case 2:
                            boardVal += 3;
                            break;
                        case 3:
                            boardVal -= 5;
                            break;
                        case 4:
                            boardVal += 5;
                            break;
                    }
                }
            }
        }
        return boardVal;
    }
    // minimax with alpha beta pruning
    public int maxVal(Game game, int alpha, int beta, int depth) {
        // check if ran out of time for search
        Date newDate = new Date();
        currentTime = newDate.getTime();
        if ((currentTime - startTime) >= timeLimit * 990) {
            outOfTime = true;
            return 0;
        }
        // actual max algorithm
        List<Move> listLegalMoves = game.getLegalMoves(game.board);
        if (cutoffTest(listLegalMoves.size(), depth)) {
            return evalFcn(game);
        }
        int v = Integer.MIN_VALUE;
        for (Move move: listLegalMoves) {
            Game copyGame = new Game(game);
            copyGame.applyMove(move, copyGame.board);
            v = Math.max(v, minVal(copyGame, alpha, beta, depth + 1));
            if (v >= beta) return v;
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    public int minVal(Game game, int alpha, int beta, int depth) {
        // check if ran out of time for search
        Date newDate = new Date();
        currentTime = newDate.getTime();
        if ((currentTime - startTime) > timeLimit * 990) {
            outOfTime = true;
            return 0;
        }
        // actual min algorithm
        List<Move> listLegalMoves = game.getLegalMoves(game.board);
        if (cutoffTest(listLegalMoves.size(), depth)) {
            return evalFcn(game);
        }
        int v = Integer.MAX_VALUE;
        for (Move move: listLegalMoves) {
            Game copyGame = new Game(game);
            copyGame.applyMove(move, copyGame.board);
            v = Math.min(v, maxVal(copyGame, alpha, beta, depth + 1));
            if (v <= alpha) return v;
            beta = Math.min(beta, v);
        }
        return v;
    }
    // gets number of neighbors for a piece on the board
    public int numDefendingNeighbors(int row, int col, int[][] state) {
        int defense = 0;
        switch (state[row][col]) {
            case 1:
                if (row + 1 < state.length && col + 1 < state[0].length) {
                    if ((state[row + 1][col + 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                if (row + 1 < state.length && col - 1 >= 0) {
                    if ((state[row + 1][col - 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                break;
            case 2:
                if (row - 1 >= 0 && col + 1 < state[0].length) {
                    if ((state[row - 1][col + 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                if (row - 1 >= 0 && col - 1 >= 0) {
                    if ((state[row - 1][col - 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                break;
            case 3:
                if (row + 1 < state.length && col + 1 < state[0].length) {
                    if ((state[row + 1][col + 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                if (row + 1 < state.length && col - 1 >= 0) {
                    if ((state[row + 1][col - 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                if (row - 1 >= 0 && col + 1 < state[0].length) {
                    if ((state[row - 1][col + 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                if (row - 1 >= 0 && col - 1 >= 0) {
                    if ((state[row - 1][col - 1] & 1) == 1) {
                        defense += 1;
                    }
                }
                break;
            case 4:
                if (row + 1 < state.length && col + 1 < state[0].length) {
                    if ((state[row + 1][col + 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                if (row + 1 < state.length && col - 1 >= 0) {
                    if ((state[row + 1][col - 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                if (row - 1 >= 0 && col + 1 < state[0].length) {
                    if ((state[row - 1][col + 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                if (row - 1 >= 0 && col - 1 >= 0) {
                    if ((state[row - 1][col - 1] & 1) == 0) {
                        defense += 1;
                    }
                }
                break;

        }
        return defense;
    }
    // returns bonus if piece is protecting its king row
    public int backBonus(int row) {
        if (maximizingPlayer == 1 && row == 0) {
            return 100;
        }
        if (maximizingPlayer == 2 && row == 7) {
            return 100;
        }
        return 0;
    }
    // returns bonus depending on how close piece is to the middle
    public int middleBonus(int row, int col) {
        return 100 - ((Math.abs(4 - col) + Math.abs(4 - row)) * 10);
    }
    // get number of pieces on original board and update global variables
    public void getBoardStatus(Game game) {
        int numRows = game.board.length;
        int numCols = game.board[0].length;
        numAllyPieces = 0;
        numAllyKings = 0;
        numOppPieces = 0;
        numOppKings = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (maximizingPlayer == 1) {
                    switch (game.board[i][j]) {
                        case 1:
                            numAllyPieces++;
                            break;
                        case 2:
                            numOppPieces++;
                            break;
                        case 3:
                            numAllyKings++;
                            break;
                        case 4:
                            numOppKings++;
                            break;
                    }
                }
                else {
                    switch (game.board[i][j]) {
                        case 1:
                            numOppPieces++;
                            break;
                        case 2:
                            numAllyPieces++;
                            break;
                        case 3:
                            numOppKings++;
                            break;
                        case 4:
                            numAllyKings++;
                            break;
                    }
                }
            }
        }
    }
}
