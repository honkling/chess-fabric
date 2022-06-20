package com.github.Vaapukkax.khc.finder;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.github.Vaapukkax.khc.finder.MinehutServerListWidget.Entry;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MinehutServerListWidget extends AlwaysSelectedEntryListWidget<Entry> {
	
    private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
    
    private final MinehutServerListScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();

    public MinehutServerListWidget(MinehutServerListScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.screen = screen;
    }
    
    private void updateEntries() {
        this.clearEntries();
        this.servers.forEach(server -> this.addEntry(server));
        this.setSelected(null);
        this.setScrollAmount(Math.min(this.getMaxScroll(), this.getScrollAmount()));
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.screen.updateJoinButtonState();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Entry entry = (Entry)this.getSelectedOrNull();
        return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void updateServers() {
        this.servers.clear();
        for (int i = 0; i < screen.entries.size(); ++i) {
        	ServerListEntry serverListEntry = screen.entries.get(i);
            if (screen.isShown(serverListEntry)) {
            	this.servers.add(new ServerEntry(this.screen, serverListEntry));
            }
        }
        this.updateEntries();
    }
    
    public int getServerCount() {
    	return this.servers.size();
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Environment(value=EnvType.CLIENT) public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> { }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry extends Entry {

        private final MinehutServerListScreen screen;
        private final MinecraftClient client;
        private final ServerListEntry server;
        private final ItemStack icon;
        private long time;

        protected ServerEntry(MinehutServerListScreen screen, ServerListEntry server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
            this.icon = server.createIcon();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.client.textRenderer.draw(matrices, this.server.name, (float)(x + 32 + 3), (float)(y + 1), 0xFFFFFF);
            Text motd = Text.of(this.server.motd.replaceAll("&", "\u00a7")); // Add regex to not replace single &
            List<OrderedText> motdList = this.client.textRenderer.wrapLines(motd, entryWidth - 32 - 2);
            for (int i = 0; i < Math.min(motdList.size(), 2); ++i) {
                this.client.textRenderer.draw(matrices, motdList.get(i), (float)(x + 32 + 3), (float)(y + 12 + this.client.textRenderer.fontHeight * i), 0x808080);
            }
            
            Text playerCountText = Text.of(this.server.players+"/"+this.server.getMaxPlayers(screen.entries));
            int width = this.client.textRenderer.getWidth(playerCountText);
            this.client.textRenderer.draw(matrices, playerCountText, (float)(x + entryWidth - width - 15 - 2), (float)(y + 1), 0x808080);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            DrawableHelper.drawTexture(matrices, x + entryWidth - 15, y, 0, 176, 10, 8, 256, 256);

            if (this.icon != null) {
            	int iconX = x;
            	if (!(this.client.options.touchscreen || hovered)) iconX += 8;
                client.getItemRenderer().renderGuiItemIcon(this.icon, iconX, y+8);
            } else {
                this.draw(matrices, x, y, UNKNOWN_SERVER_TEXTURE);
            }

            if (this.client.options.touchscreen || hovered) {
                RenderSystem.setShaderTexture(0, SERVER_SELECTION_TEXTURE);
                DrawableHelper.fill(matrices, x, y, x + 32, y + 32, -1601138544);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                int mx = mouseX - x;
//                int my = mouseY - y;
                if (this.canConnect()) {
                    if (mx < 32 && mx > 16) {
                        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                
                if (mx < 32 && mx > 0) {
                	String date = DateFormat.getDateTimeInstance().format(new Date(server.start));
                	screen.renderTooltip(matrices, Text.of(date), mouseX, mouseY);
                }
            }
        }

        protected void draw(MatrixStack matrices, int x, int y, Identifier textureId) {
            RenderSystem.setShaderTexture(0, textureId);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrices, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean canConnect() {
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (Screen.hasShiftDown()) {
                MinehutServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
                int i = multiplayerServerListWidget.children().indexOf(this);
                if (i == -1) {
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double mx = mouseX - (double)MinehutServerListWidget.this.getRowLeft();
//            double my = mouseY - (double)MinehutMultiplayerServerListWidget.this.getRowTop(MinehutMultiplayerServerListWidget.this.children().indexOf(this));
            if (mx <= 32.0) {
                if (mx < 32.0 && mx > 16.0 && this.canConnect()) {
                    this.screen.select(this);
                    this.screen.connect();
                    return true;
                }
            }
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public ServerListEntry getServer() {
            return this.server;
        }

        @Override
        public Text getNarration() {
            return new TranslatableText("narrator.select", this.server.name);
        }
    }

}