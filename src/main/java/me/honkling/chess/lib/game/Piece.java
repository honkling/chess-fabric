package me.honkling.chess.lib.game;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Piece implements Cloneable {
    public int file;
    public int rank;
    public int[] startingPosition;
    public List<int[]> moves = new ArrayList<>();

    public PieceType type;
    public PieceColor color;

    public Piece(int file, int rank, PieceType type, PieceColor color) {
        this.file = file;
        this.rank = rank;
        this.type = type;
        this.color = color;
        startingPosition = new int[] {file, rank};
    }

    public Piece(int file, int rank) {
        this.file = file;
        this.rank = rank;
        this.type = PieceType.NONE;
    }

    public List<int[]> generateLegalMoves() {
        throw new NotImplementedException();
    }

    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
