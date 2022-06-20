package com.github.Vaapukkax.khc.finder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.Vaapukkax.khc.finder.comparators.ServerActivityComparator;
import com.github.Vaapukkax.khc.finder.comparators.ServerUptimeComparator;

public enum SortType {

	ACTIVITY(new ServerActivityComparator()),
	UPTIME(new ServerUptimeComparator());
	
	private final Comparator<ServerListEntry> comparator;
	
	private SortType(Comparator<ServerListEntry> comparator) {
		this.comparator = comparator;
	}
	
	public void sort(List<ServerListEntry> list) {
		Collections.sort(list, comparator);
	}

	public SortType next() {
		List<SortType> sortTypes = Arrays.asList(values());
		int i = sortTypes.indexOf(this)+1;
		if (i >= sortTypes.size()) i = 0;
		return sortTypes.get(i);
	}
	
	@Override
	public String toString() {
		return Character.toUpperCase(name().charAt(0))+name().substring(1).toLowerCase();
	}
	
}
