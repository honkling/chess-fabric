package com.github.Vaapukkax.khc.finder.comparators;

import java.util.Comparator;

import com.github.Vaapukkax.khc.finder.ServerListEntry;

public class ServerUptimeComparator implements Comparator<ServerListEntry> {
	
	@Override
	public int compare(ServerListEntry o1, ServerListEntry o2) {
		if (o1.start < o2.start) return-1;
		if (o1.start > o2.start) return 1;
		return 0;
	}
	
}