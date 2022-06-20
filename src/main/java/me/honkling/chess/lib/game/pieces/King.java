package me.honkling.chess.lib.game.pieces;

import me.honkling.chess.ChessClient;
import me.honkling.chess.lib.game.Piece;
import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public static List<int[]> offsets = new ArrayList<>();

    static {
        offsets.add(new int[] {1, -1});
        offsets.add(new int[] {1, 0});
        offsets.add(new int[] {1, 1});
        offsets.add(new int[] {0, -1});
        offsets.add(new int[] {0, 1});
        offsets.add(new int[] {-1, -1});
        offsets.add(new int[] {-1, 0});
        offsets.add(new int[] {-1, 1});
    }

    public King(int file, int rank, PieceType type, PieceColor color) {
        super(file, rank, type, color);
    }

    @Override
    public List<int[]> generateLegalMoves() {
        List<int[]> legalMoves = new ArrayList<>();

        for (int[] offset : offsets) {
            int[] turtle = new int[] {file + offset[0], rank + offset[1]};

            Piece piece = ChessClient.board.getPiece(turtle[0], turtle[1]);
            if (piece != null && piece.color == null || piece.color != color) {
                legalMoves.add(turtle);
            }
        }

        return legalMoves;
    }
}
