package me.honkling.chess.lib.game.pieces;

import me.honkling.chess.ChessClient;
import me.honkling.chess.lib.game.Piece;
import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;
import me.honkling.chess.lib.game.SlidingPiece;

import java.util.ArrayList;
import java.util.List;

public class Rook extends SlidingPiece {
    public static List<int[]> directions = new ArrayList<>();

    static {
        directions.add(new int[] {0, -1});
        directions.add(new int[] {-1, 0});
        directions.add(new int[] {1, 0});
        directions.add(new int[] {0, 1});
    }

    public Rook(int file, int rank, PieceType type, PieceColor color) {
        super(file, rank, type, color);
    }

    public List<int[]> generateLegalMoves() {
        return super.generateLegalMoves(directions);
    }
}
