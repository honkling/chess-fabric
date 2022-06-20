package me.honkling.chess.lib.game.pieces;

import me.honkling.chess.ChessClient;
import me.honkling.chess.lib.game.Piece;
import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(int file, int rank, PieceType type, PieceColor color) {
        super(file, rank, type, color);
    }

    // i hate this
    public List<int[]> generateLegalMoves() {
        int direction = color == PieceColor.BLACK ? -1 : 1;
        Piece piece;
        List<int[]> legalMoves = new ArrayList<>();

        // Forward move
        if (ChessClient.board.getPiece(file, rank + direction).type == PieceType.NONE) legalMoves.add(new int[] {file, rank + direction});
        if (ChessClient.board.getPiece(file, rank + direction * 2).type == PieceType.NONE && moves.size() <= 0) legalMoves.add(new int[] {file, rank + direction * 2});

        // Diagonal capture
        if ((piece = ChessClient.board.getPiece(file - 1, rank + direction)).type != PieceType.NONE && piece.color != color) legalMoves.add(new int[] {file - 1, rank + direction});
        if ((piece = ChessClient.board.getPiece(file + 1, rank + direction)).type != PieceType.NONE && piece.color != color) legalMoves.add(new int[] {file + 1, rank + direction});

        // En passant
        if ((piece = ChessClient.board.getPiece(file - 1, rank)).type != PieceType.NONE && piece.color != color && piece.moves.size() == 1) legalMoves.add(new int[] {file - 1, rank + direction});
        if ((piece = ChessClient.board.getPiece(file + 1, rank)).type != PieceType.NONE && piece.color != color && piece.moves.size() == 1) legalMoves.add(new int[] {file + 1, rank + direction});

        return legalMoves;
    }
}