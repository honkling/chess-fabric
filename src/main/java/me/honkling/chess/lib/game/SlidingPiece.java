package me.honkling.chess.lib.game;

import me.honkling.chess.ChessClient;

import java.util.ArrayList;
import java.util.List;

public class SlidingPiece extends Piece {
    private List<int[]> directions = new ArrayList<>();

    public SlidingPiece(int file, int rank, PieceType type, PieceColor color) {
        super(file, rank, type, color);
    }

    public SlidingPiece(int file, int rank) {
        super(file, rank);
    }

    public List<int[]> generateLegalMoves() {
        return generateLegalMoves(directions);
    }

    public List<int[]> generateLegalMoves(List<int[]> directions) {
        List<int[]> legalMoves = new ArrayList<>();
        int turtleX = file;
        int turtleY = rank;
        for (int[] direction : directions) {
            while (turtleX >= 0 && turtleY >= 0 && turtleX < 8 && turtleY < 8) {
                turtleX += direction[0];
                turtleY += direction[1];

                if (!(turtleX >= 0 && turtleY >= 0 && turtleX < 8 && turtleY < 8)) break;

                Piece piece = ChessClient.board.getPiece(turtleX, turtleY);
                if (piece.type != PieceType.NONE) {
                    if (piece.color == color) break;
                    legalMoves.add(new int[] {turtleX, turtleY});
                    break;
                }

                legalMoves.add(new int[] {turtleX, turtleY});
            }

            turtleX = file;
            turtleY = rank;
        }

        return legalMoves;
    }
}
