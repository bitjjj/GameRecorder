package com.gamerecorder.events;

public class TeamVSHistoryChangeEvent {

	private String[] teams;
	
	public TeamVSHistoryChangeEvent(String[] teams){
		this.teams = teams;
	}
	
	public String[] getTeams() {
		return teams;
	}

	
}
