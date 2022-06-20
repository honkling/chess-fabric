package me.honkling.chess.lib;

import com.google.common.collect.Maps;
import me.honkling.chess.lib.game.Board;
import me.honkling.chess.lib.game.Piece;
import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;
import net.minecraft.util.Util;

import java.util.HashMap;

public class FENHelper {
    public static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static Board fromFen(String fen) {
        Board board = new Board();

        HashMap<Character, PieceType> pieceTypeFromSymbol = Util.make(Maps.newHashMap(), (map) -> {
            map.put('k', PieceType.KING);
            map.put('p', PieceType.PAWN);
            map.put('n', PieceType.KNIGHT);
            map.put('b', PieceType.BISHOP);
            map.put('r', PieceType.ROOK);
            map.put('q', PieceType.QUEEN);
        });

        String fenBoard = fen.split(" ")[0];
        int file = 0, rank = 7;

        for (char symbol : fenBoard.toCharArray()) {
            if (symbol == '/') {
                file = 0;
                rank--;
            } else {
                if (Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    PieceColor pieceColor = Character.isUpperCase(symbol) ? PieceColor.WHITE : PieceColor.BLACK;
                    PieceType pieceType = pieceTypeFromSymbol.get(Character.toLowerCase(symbol));

                    Piece piece = Board.constructPiece(file, rank, pieceType, pieceColor);
                    board.setPiece(piece);

                    file++;
                }
            }
        }

        return board;
    }
}
