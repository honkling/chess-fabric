package com.github.Vaapukkax.khc.finder.comparators;

import java.util.Comparator;

import com.github.Vaapukkax.khc.finder.ServerListEntry;

public class ServerActivityComparator implements Comparator<ServerListEntry> {
	
	@Override
	public int compare(ServerListEntry o1, ServerListEntry o2) {
		
		double o1a = o1.players/(double)o1.planMaxPlayers;
		double o2a = o2.players/(double)o2.planMaxPlayers;
		
		if (o1a > o2a) return-1;
		if (o1a < o2a) return 1;
		return 0;
	}
	
}