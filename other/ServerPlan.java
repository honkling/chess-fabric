package com.github.Vaapukkax.khc.finder;

public enum ServerPlan {

	FREE,
	DAILY,
	MONTHLY_2GB,
	MONTHLY_3GB,
	MONTHLY_6GB,
	MONTHLY_10GB,
	YEARLY_2GB,
	YEARLY_3GB,
	YEARLY_6GB,
	YEARLY_10GB;

	public String toString() {
		String name = name().toLowerCase().replace("_", " ");
		return Character.toUpperCase(name.charAt(0))+name.substring(1);
	}
	
}
