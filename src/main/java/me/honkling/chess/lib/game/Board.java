package me.honkling.chess.lib.game;

import me.honkling.chess.lib.game.pieces.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {
    private final HashMap<Integer, Piece> pieces;

    public Board() {
        pieces = new HashMap<>();
    }

    public static Class<? extends Piece> getClassFromPieceType(PieceType type) {
        // I hope there's a better way to do this but this'll have to do for now
        switch (type) {
            case BISHOP -> { return Bishop.class; }
            case QUEEN -> { return Queen.class; }
            case ROOK -> { return Rook.class; }
            case PAWN -> { return Pawn.class; }
            case KNIGHT -> { return Knight.class; }
            case KING -> { return King.class; }
            default -> { return Rook.class; }
        }
    }

    public static Piece constructPiece(int file, int rank, PieceType type, PieceColor color) {
        try {
            return (Piece) getClassFromPieceType(type).getConstructors()[0].newInstance(file, rank, type, color);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<int[]> legalMovesFromPiece(Piece piece) {
        return piece.generateLegalMoves().stream().filter((move) -> move[0] >= 0 && move[0] < 8 && move[1] >= 0 && move[1] < 8).toList();
    }

    public void setPiece(Piece piece) {
        pieces.put(piece.rank * 8 + piece.file, piece);
    }

    public Piece getPiece(int file, int rank) {

        if (pieces.get(rank * 8 + file) == null) {
            pieces.put(rank * 8 + file, new Piece(file, rank));
        }
        return pieces.get(rank * 8 + file);
    }

    public List<Piece> getPieces() {
        List<Piece> pieces = new ArrayList<>(64);
        this.pieces.forEach((integer, piece) -> pieces.add(piece));
        return pieces;
    }
}
