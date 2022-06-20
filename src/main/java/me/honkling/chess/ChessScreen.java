package me.honkling.chess;

import com.mojang.blaze3d.systems.RenderSystem;
import me.honkling.chess.lib.game.Board;
import me.honkling.chess.lib.game.Piece;
import me.honkling.chess.lib.game.PieceColor;
import me.honkling.chess.lib.game.PieceType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ChessScreen extends Screen {

    private List<int[]> legalMoves = new ArrayList<>();
    private boolean spaceSelected = false;
    private int[] selectedSpace;

    public ChessScreen() {
        super(new LiteralText("Chess"));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int halfSize = height / 3;
        int spaceSize = ((height / 2 + halfSize) - (height / 2 - halfSize)) / 8;
        int spriteSize = spaceSize - 4;
        //System.out.printf("Selected? %s\n", spaceSelected);
        //if (spaceSelected) System.out.printf("Selected %s, %s\n", selectedSpace[0], selectedSpace[1]);
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                fill(
                        matrices,
                        spaceSize * file + width / 2 - (spaceSize * 4),
                        spaceSize * rank + height / 2 - (spaceSize * 4),
                        spaceSize * (file + 1) + width / 2 - (spaceSize * 4),
                        spaceSize * (rank + 1) + height / 2 - (spaceSize * 4),
                        spaceSelected && selectedSpace[0] == file && selectedSpace[1] == rank ?
                                0xfff7f769 :
                                ((file + rank) % 2 == 0 ? 0xff769656 : 0xffffffff)
                );
                Piece piece = ChessClient.board.getPiece(file, rank);
                //System.out.printf("Found %s %s at %s, %s (really %s, %s)%n", (piece.color == null ? "" : piece.color), piece.type.toString(), file, rank, piece.file, piece.rank);
                if (piece.type != PieceType.NONE) {
                    //System.out.printf("Rendering textures/pieces/%s%s.png%n", piece.color.toString().toLowerCase(), piece.type.toString().toLowerCase());
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.setShaderTexture(0, new Identifier("chess", "textures/pieces/%s%s.png".formatted(piece.color.toString().toLowerCase(), piece.type.toString().toLowerCase())));
                    drawTexture(matrices, spaceSize * file + 2 + width / 2 - (spaceSize * 4)/* - spaceSize / 2*/, spaceSize * rank + 2 + height / 2 - (spaceSize * 4)/* - spaceSize / 2*/, 1, 0, 0, spriteSize, spriteSize, spriteSize, spriteSize + 2);
                }
            }
        }

        for (int[] legalMove : legalMoves) {
            int file = legalMove[0];
            int rank = legalMove[1];
            fill(
                    matrices,
                    spaceSize * file + width / 2 - (spaceSize * 4),
                    spaceSize * rank + height / 2 - (spaceSize * 4),
                    spaceSize * (file + 1) + width / 2 - (spaceSize * 4),
                    spaceSize * (rank + 1) + height / 2 - (spaceSize * 4),
                    (file + rank) % 2 == 0 ? 0xffaa0000 : 0xffff5555
            );
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int halfSize = height / 3;
        int spaceSize = ((height / 2 + halfSize) - (height / 2 - halfSize)) / 8;
        int x = (int) (spaceSize * (mouseX / spaceSize)) - (width / 2 - (spaceSize * 4));
        int y = (int) (spaceSize * (mouseY / spaceSize)) - (height / 2 - (spaceSize * 4));

        int file = x / spaceSize;
        int rank = y / spaceSize;
        Piece piece = ChessClient.board.getPiece(file, rank);

        if (!spaceSelected && piece.type == PieceType.NONE) return true;

        System.out.println("Before condition");

        if (spaceSelected) {
            System.out.println("1");
            Piece newPieceEmpty = new Piece(selectedSpace[0], selectedSpace[1]);
            Piece oldPiece = ChessClient.board.getPiece(selectedSpace[0], selectedSpace[1]);
            Piece newPieceFilled = oldPiece.clone();
            newPieceFilled.file = file;
            newPieceFilled.rank = rank;

            if (oldPiece.file == file && oldPiece.rank == rank) {
                spaceSelected = false;
                selectedSpace = null;
                legalMoves = new ArrayList<>();
                return true;
            }

            System.out.println("2");

            boolean isLegal = false;
            for (int[] legalMove : legalMoves) {
                if (legalMove[0] == file && legalMove[1] == rank) {
                    isLegal = true;
                    break;
                }
            }

            System.out.println("3");

            if (oldPiece.color == piece.color || !isLegal) {
                spaceSelected = piece.type != PieceType.NONE;
                selectedSpace = spaceSelected ? new int[] {file, rank} : null;
                if (spaceSelected) legalMoves = Board.legalMovesFromPiece(piece);
                return true;
            }

            Piece passantPawn;
            if (Math.abs(file - selectedSpace[0]) == 1 && Math.abs(rank - selectedSpace[1]) == 1 && oldPiece.type == PieceType.PAWN && (passantPawn = ChessClient.board.getPiece(file, rank + (oldPiece.color == PieceColor.BLACK ? 1 : -1))).type == PieceType.PAWN) {
                // En passant
                ChessClient.board.setPiece(newPieceEmpty);
                newPieceEmpty = new Piece(passantPawn.file, passantPawn.rank);
            }

            newPieceFilled.moves.add(new int[] {file, rank});
            ChessClient.board.setPiece(newPieceEmpty);
            ChessClient.board.setPiece(newPieceFilled);
            spaceSelected = false;
            selectedSpace = null;
            legalMoves = new ArrayList<>();
        } else {
            System.out.println(":(");
            legalMoves = Board.legalMovesFromPiece(piece);
            spaceSelected = true;
            selectedSpace = new int[] {file, rank};
        }

        return true;
    }
}
