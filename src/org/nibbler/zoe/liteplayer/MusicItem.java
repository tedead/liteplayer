package org.nibbler.zoe.liteplayer;

public class MusicItem {
	
	public String name;
	private boolean isChecked;
	
	public MusicItem(String name, boolean isChecked) {
		this.name = name;
		this.isChecked = isChecked;
	}
	
	public void setChecked(boolean state) {
		this.isChecked = state;
	}
	
	public boolean isChecked(){
		return isChecked;
	}
}
