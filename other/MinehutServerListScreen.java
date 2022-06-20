package com.github.Vaapukkax.khc.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.glfw.GLFW;

import com.github.Vaapukkax.khc.Kuphack;
import com.github.Vaapukkax.khc.Server;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value = EnvType.CLIENT)
public class MinehutServerListScreen extends Screen {

	protected ArrayList<ServerListEntry> entries = new ArrayList<>();
	
    protected MinehutServerListWidget serverListWidget;
    private ButtonWidget buttonJoin;
    private TextFieldWidget textField;
	
    private final Screen parent;
    private boolean initialized;
    private String error;
    private boolean refreshing;
    
    private SortType sortType = SortType.ACTIVITY;
    private int playerCount, serverCount;
    
    public MinehutServerListScreen(Screen parent) {
        super(new LiteralText("Minehut Server List"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.client.keyboard.setRepeatEvents(true);
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.serverListWidget = new MinehutServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
            
            loadServers();
        }
        
        this.addSelectableChild(this.serverListWidget);
        
        this.addDrawableChild( // Sort
        	new ButtonWidget(
        		this.width / 2 - 154,
        		this.height - 52,
        		100, 20,
        		Text.of("Sort: "+sortType),
        		button -> {
        			sortType = sortType.next();
        			
        			sort(entries);
        			button.setMessage(Text.of("Sort: "+sortType));
        			serverListWidget.updateServers();
        			serverListWidget.setScrollAmount(0);
        		}
        	)
        );

        this.buttonJoin = this.addDrawableChild( // Join
        	new ButtonWidget(this.width / 2 - 154, this.height - 28, 100, 20, new TranslatableText("selectServer.select"), button -> this.connect())
        );
        
        this.addDrawableChild( // Refresh
        	new ButtonWidget(this.width / 2 - 50, this.height - 28, 100, 20, new TranslatableText("selectServer.refresh"), button -> this.refresh())
        );
        this.addDrawableChild( // Back
        	new ButtonWidget(this.width / 2 + 4 + 50, this.height - 28, 100, 20, ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
        );
        
        
        this.textField = new TextFieldWidget(
        	this.textRenderer,
        	this.width / 2 - 154 + 105, this.height - 52,
        	200, 20,
        	Text.of("Search")
        );
        this.textField.setChangedListener(search -> {
        	serverListWidget.updateServers();
        });
        this.addSelectableChild(this.textField);
        this.textField.setTextFieldFocused(true);
        this.setInitialFocus(this.textField);
        this.updateJoinButtonState();
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
    
    private void loadServers() {
    	error = null;
    	refreshing = true;
    	new Thread(() -> {
    		ArrayList<ServerListEntry> entries = new ArrayList<>();
	    	
	        try (CloseableHttpClient client = HttpClients.createDefault()) {
				HttpGet request = new HttpGet("https://api.minehut.com/servers");
				try (CloseableHttpResponse response = client.execute(request)) {
					
		            HttpEntity entity = response.getEntity();
		            if (entity != null) {
		            	
		                String result = EntityUtils.toString(entity);
		                
		                Gson gson = new Gson();
		                JsonObject object = gson.fromJson(result, JsonObject.class);
		                
		                this.playerCount = object.get("total_players").getAsInt();
		                this.serverCount = object.get("total_servers").getAsInt();
		                
		                JsonArray servers = object.get("servers").getAsJsonArray();
	
		                Iterator<JsonElement> it = servers.iterator();
		                while (it.hasNext()) {	                	
		                	ServerListEntry entry = ServerListEntry.create(it.next().getAsJsonObject());
		                	entries.add(entry);
		                }
	
		                sort(entries);
		            }
				}
	        } catch (Exception e) {
				e.printStackTrace();
				error = e.toString();
				entries = new ArrayList<>();
			}
	
	        synchronized (this.entries) {
	        	this.entries = entries;
	        	serverListWidget.updateServers();
	        }
	        refreshing = false;
    	}).start();
    }
    
    public void sort(List<ServerListEntry> entries) {
        sortType.sort(entries);
    }
    
    public boolean isShown(ServerListEntry entry) {
    	if (!entry.connectable) return false;
    	
    	String search = textField.getText().toLowerCase();
    	if (search.isBlank()) return true;
 
    	// Some funky stuff
    	String motd = entry.motd.toLowerCase().replaceAll("&([a-fA-F0-9]|k|l|m|n|o|r)", "").replaceAll("\n", " ");
    	return entry.name.toLowerCase().contains(search) || motd.contains(search);
    }

    @Override
    public void tick() {
        super.tick();
        this.textField.tick();
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void refresh() {
    	loadServers();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F5) {
            this.refresh();
            return true;
        }
        if (this.serverListWidget.getSelectedOrNull() != null && !this.textField.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.connect();
                return true;
            }
            return this.serverListWidget.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    	this.renderBackground(matrices);
    	this.serverListWidget.render(matrices, mouseX, mouseY, delta);
        
        if (error != null) {
        	drawCenteredText(matrices, this.textRenderer, "Wild error appeard!", this.width / 2, this.height / 2 - textRenderer.fontHeight, 0xFF0000);
        	drawCenteredText(matrices, this.textRenderer, this.error, this.width / 2, this.height / 2, 0xFFFFFF);
        } else if (refreshing) {
        	Text text = Text.of("Refreshing...");
        	drawTextWithShadow(matrices, this.textRenderer, text, client.getWindow().getScaledWidth() - this.textRenderer.getWidth(text) - 5, 5, 0xFFFFFF);
        }
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        drawCenteredText(matrices, this.textRenderer, Text.of("Servers: "+serverListWidget.getServerCount()+" ("+serverCount+") | Players: "+playerCount), this.width / 2, 20 - this.textRenderer.fontHeight, 0xFFFFFF);
        
        
        this.textField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    public void connect() {
    	
        MinehutServerListWidget.Entry entry = (MinehutServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
        if (entry instanceof MinehutServerListWidget.ServerEntry) {
        	ServerListEntry serverEntry = ((MinehutServerListWidget.ServerEntry)entry).getServer();
    	
	    	if (Kuphack.getServer() == Server.LOBBY && client.player != null) {
	    		client.player.sendChatMessage("/join "+serverEntry.name);
	    	} else {
	    		if (client.world != null) {
	    			boolean singleplayer = client.isInSingleplayer();
	    			client.world.disconnect();
	                if (singleplayer)
	                	this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
	                else this.client.disconnect();
	    		}
    		
	        	ServerInfo info = new ServerInfo(serverEntry.toString(), serverEntry.getAddress(), false);
				ConnectScreen.connect(
					this,
					this.client,
					ServerAddress.parse(info.address),
					info
				);
	        }
    	}
    }

    public void select(MinehutServerListWidget.Entry entry) {
        this.serverListWidget.setSelected(entry);
        this.updateJoinButtonState();
    }

    protected void updateJoinButtonState() {
        this.buttonJoin.active = this.serverListWidget.getSelectedOrNull() != null;
    }

}

