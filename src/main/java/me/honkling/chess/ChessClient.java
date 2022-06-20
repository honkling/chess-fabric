package me.honkling.chess;

import me.honkling.chess.lib.FENHelper;
import me.honkling.chess.lib.game.Board;
import me.honkling.chess.lib.game.Piece;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ChessClient implements ClientModInitializer {

    private static KeyBinding chessKey;
    private static KeyBinding resetChessKey;
    public static ChessClient instance;
    public static Board board;

    @Override
    public void onInitializeClient() {
        instance = this;
        board = FENHelper.fromFen(FENHelper.startingFen);
        chessKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chess.openBoard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.chess.category"
        ));
        resetChessKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
           "key.chess.resetBoard",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.chess.category"
        ));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (chessKey.wasPressed()) {
                if (!(client.currentScreen instanceof ChessScreen)) {
                    client.setScreenAndRender(new ChessScreen());
                }
            }

            while (resetChessKey.wasPressed()) {
                board = FENHelper.fromFen(FENHelper.startingFen);
            }
        });
    }
}
