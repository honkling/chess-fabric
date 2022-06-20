package me.honkling.chess.lib.game.pieces;

import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;
import me.honkling.chess.lib.game.SlidingPiece;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends SlidingPiece {
    public static List<int[]> directions = new ArrayList<>();

    static {
        directions.add(new int[] {-1, 1});
        directions.add(new int[] {1, 1});
        directions.add(new int[] {-1, -1});
        directions.add(new int[] {1, -1});
    }

    public Bishop(int file, int rank, PieceType type, PieceColor color) {
        super(file, rank, type, color);
    }

    public Bishop(int file, int rank) {
        super(file, rank);
    }

    public List<int[]> generateLegalMoves() {
        return super.generateLegalMoves(directions);
    }
}
