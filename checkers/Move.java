package checkers;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

class Move {
    public int initialRow;
    public int initialCol;

    public int startRow;
    public int startCol;

    public int endRow;
    public int endCol;

    public ArrayList<Integer> listCaptureRow;
    public ArrayList<Integer> listCaptureCol;

    public ArrayList<Integer> listVisitedRow;
    public ArrayList<Integer> listVisitedCol;

    public Set<Pair> capturedSquares;
    public int[][] state;

    public Move(int startRow, int startCol, int endRow, int endCol, int[][] state) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.state = state;
        this.listCaptureRow = new ArrayList<Integer>();
        this.listCaptureCol = new ArrayList<Integer>();
        this.listVisitedRow = new ArrayList<Integer>();
        this.listVisitedCol = new ArrayList<Integer>();
        this.capturedSquares = new HashSet<Pair>();

    }

    // copy constructor
    public Move(Move move) {
        this.initialRow = move.initialRow;
        this.initialCol = move.initialCol;
        this.startRow = move.startRow;
        this.startCol = move.startCol;
        this.endRow = move.endRow;
        this.endCol = move.endCol;
        this.listVisitedRow = new ArrayList<Integer>(move.listVisitedRow);
        this.listVisitedCol = new ArrayList<Integer>(move.listVisitedCol);
        this.listCaptureRow = new ArrayList<Integer>(move.listCaptureRow);
        this.listCaptureCol = new ArrayList<Integer>(move.listCaptureCol);
        this.capturedSquares = new HashSet<>(move.capturedSquares);
        this.state = move.state;
    }
}
