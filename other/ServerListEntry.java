package com.github.Vaapukkax.khc.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class ServerListEntry {
	
	public static ServerListEntry create(JsonObject object) {
		JsonArray servers = object.get("staticInfo").getAsJsonObject().get("connectedServers").getAsJsonArray();
		if (servers.size() > 0)
			return new ProxyServerListEntry(object);
		else return new NormalServerListEntry(object);
	}
	
	public final String name, motd, id;
	public final Item icon;
	public final ServerPlan plan;
	public final int players, planMaxPlayers;
	public final boolean connectable;
	
	public final long start;
	
	public ServerListEntry(JsonObject object) {
		JsonElement connectableElement = object.get("connectable");
		this.connectable = connectableElement != null ? connectableElement.getAsBoolean() : false;

		this.name = object.get("name").getAsString();
		
		String motd = object.get("motd").getAsString();
		long newLineCount = motd.chars().filter(ch -> ch == '\n').count();
		if (newLineCount >= 2) {
			motd = motd.substring(0, motd.indexOf("\n")+1)+motd.substring(motd.indexOf("\n")+1).replaceAll("\n", " ");
		}
		this.motd = motd;
		
		Item icon = Items.OAK_SIGN;
		JsonElement iconElement = object.get("icon");
		if (!iconElement.isJsonNull()) {
			icon = Registry.ITEM.get(new Identifier("minecraft:"+iconElement.getAsString().toLowerCase()));
		}
		this.icon = icon;
		
		this.players = object.get("playerData").getAsJsonObject().get("playerCount").getAsInt();
		
		JsonObject staticInfo = object.get("staticInfo").getAsJsonObject();
		this.id = staticInfo.get("_id").getAsString();
		this.start = staticInfo.get("serviceStartDate").getAsLong();
		this.planMaxPlayers = staticInfo.get("planMaxPlayers").getAsInt();
		
		ServerPlan plan = null;
		String planName = staticInfo.get("rawPlan").getAsString().toUpperCase();
		try {
			plan = ServerPlan.valueOf(planName);
		} catch (Exception e) {
			System.err.println("Unknown plan '"+planName+"'");
		}
		this.plan = plan;
	}
	
	public abstract int getMaxPlayers(List<ServerListEntry> list);

	public ItemStack createIcon() {
		return new ItemStack(icon);
	}
	
	public String getAddress() {
		return name.toLowerCase()+".minehut.gg";
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	private static class ProxyServerListEntry extends ServerListEntry {

		private String[] children;
		
		public ProxyServerListEntry(JsonObject object) {
			super(object);
			JsonArray servers = object.get("staticInfo").getAsJsonObject().get("connectedServers").getAsJsonArray();
			this.children = new String[servers.size()];
			for (int i = 0; i < servers.size(); i++) {
				this.children[i] = servers.get(i).getAsString();
			}
		}

		@Override
		public int getMaxPlayers(List<ServerListEntry> list) {
			int i = 0;
			for (ServerListEntry entry : getChildren(list)) {
				i += entry.getMaxPlayers(list);
			}
			return i;
		}
		
		public List<ServerListEntry> getChildren(List<ServerListEntry> list) {
			ArrayList<ServerListEntry> children = new ArrayList<>();
			for (ServerListEntry entry : list) {
				if (Arrays.asList(this.children).contains(entry.id)) {
					children.add(entry);
				}
			}
			return Collections.unmodifiableList(children);
		}
		
	}
	
	private static class NormalServerListEntry extends ServerListEntry {

		private final int maxPlayers;
		
		public NormalServerListEntry(JsonObject object) {
			super(object);
			JsonElement maxPlayers = object.get("maxPlayers");
			this.maxPlayers = maxPlayers != null ? maxPlayers.getAsInt() : 0;
		}

		@Override
		public int getMaxPlayers(List<ServerListEntry> list) {
			return this.maxPlayers;
		}
		
	}
	
}
