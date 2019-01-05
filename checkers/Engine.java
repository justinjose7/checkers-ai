package checkers;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


class Engine {
    private Game checkers;
    private Computer computerPlayer1;
    private Computer computerPlayer2;
    private boolean gameOver = false;

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.checkers = new Game();
        engine.computerPlayer1 = new Computer(1, 3);
        engine.computerPlayer2 = new Computer(2, 3);
        String player1Type = "", player2Type = "", loadGame = "";
        Boolean player1IsComputer, player2IsComputer;
        Scanner scan = new Scanner(System.in);
        // check if player 1 is AI or not
        while (!player1Type.toUpperCase().equals("Y") && !player1Type.toUpperCase().equals("N")) {
            System.out.println("Will player # 1 be a computer? (Y/N):");
            player1Type = scan.nextLine();
        }
        player1IsComputer = player1Type.toUpperCase().equals("Y") ? true : false;
        // check if player 2 is AI or not
        while (!player2Type.toUpperCase().equals("Y") && !player2Type.toUpperCase().equals("N")) {
            System.out.println("Will player # 2 be a computer? (Y/N):");
            player2Type = scan.nextLine();
        }
        player2IsComputer = player2Type.toUpperCase().equals("Y") ? true : false;
        // check if user wants to load existing game board
        while (!loadGame.toUpperCase().equals("Y") && !loadGame.toUpperCase().equals("N")) {
            System.out.println("Do you want to load a game from a file? (Y/N):");
            loadGame = scan.nextLine();
        }
        // if so, load the game board, else begin a new game
        if (loadGame.toUpperCase().equals("Y")) {
            System.out.println("Enter filename:");
            String filename = scan.nextLine();
            if (engine.checkers.loadGameBoard(filename)) {
                System.out.println("Loaded board successfully.");
            } else {
                System.out.println("\nUnable to load that board. Check filename or format.\n");
                engine.checkers.newGame();
            }
            engine.checkers.printBoard();
            engine.computerPlayer1.timeLimit = engine.checkers.timeLimit;
            engine.computerPlayer2.timeLimit = engine.checkers.timeLimit;

        }
        else {
            engine.checkers.newGame();
            int timeLimit = -1;
            if (player1IsComputer || player2IsComputer) {
                while (timeLimit < 3 || timeLimit > 60) {
                    System.out.println("Enter a time limit in seconds (3 - 60):");
                    try {
                        timeLimit = scan.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Type a valid duration only!");
                        scan.next();
                    }
                }
                engine.computerPlayer1.timeLimit = timeLimit;
                engine.computerPlayer2.timeLimit = timeLimit;
            }
            engine.checkers.printBoard();
        }

        // actual game play
        while (engine.gameOver == false) {
            List<Move> legalMoves;
            int moveNumber = -1;
            switch(engine.checkers.currentPlayer) {
                case 1:
                    legalMoves = engine.checkers.getLegalMoves(engine.checkers.board);
                    if (legalMoves.isEmpty()) {
                        engine.gameOver = true;
                        System.out.println("Player 2 Wins!");
                        continue;
                    }
                    System.out.println("Player 1's Turn: ");
                    engine.checkers.printListMoves(legalMoves);
                    if (player1IsComputer){
                        Move move = engine.computerPlayer1.alphaBetaSearch(engine.checkers);
                        engine.checkers.applyMove(move, engine.checkers.board);
                        System.out.print("Player 1 Chose: ");
                        engine.checkers.printMove(move);
                    }
                    else {
                        while (moveNumber < 0 || moveNumber >= legalMoves.size()) {
                            System.out.println("Choose a move number (e.g. type 0 and hit enter): ");
                            try {
                                moveNumber = scan.nextInt();
                                scan.nextLine();
                            }
                            catch (InputMismatchException e){
                                System.out.println("Type valid move numbers only!");
                                scan.next();
                            }
                        }
                        engine.checkers.applyMove(legalMoves.get(moveNumber), engine.checkers.board);
                        System.out.print("Player 1 Chose: ");
                        engine.checkers.printMove(legalMoves.get(moveNumber));
                    }
                    engine.checkers.printNote();
                    engine.checkers.printBoard();
                    break;
                case 2:
                    legalMoves = engine.checkers.getLegalMoves(engine.checkers.board);
                    if (legalMoves.isEmpty()) {
                        engine.gameOver = true;
                        System.out.println("Player 1 Wins!");
                        continue;
                    }
                    System.out.println("Player 2's Turn: ");
                    engine.checkers.printListMoves(legalMoves);
                    if (player2IsComputer) {
                        Move move = engine.computerPlayer2.alphaBetaSearch(engine.checkers);
                        engine.checkers.applyMove(move, engine.checkers.board);
                        System.out.print("Player 2 Chose: ");
                        engine.checkers.printMove(move);
                    }
                    else {
                        while (moveNumber < 0 || moveNumber >= legalMoves.size()) {
                            System.out.println("Choose a move number (e.g. type 0 and hit enter): ");
                            try {
                                moveNumber = scan.nextInt();
                                scan.nextLine();
                            }
                            catch (InputMismatchException e) {
                                System.out.println("Type valid move numbers only!");
                                scan.next();
                            }
                        }
                        engine.checkers.applyMove(legalMoves.get(moveNumber), engine.checkers.board);
                        System.out.print("Player 2 Chose: ");
                        engine.checkers.printMove(legalMoves.get(moveNumber));
                    }
                    engine.checkers.printNote();
                    engine.checkers.printBoard();
                    break;

            }
        }
    }
}
