package com.gamerecorder.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Game implements Serializable {

	private static final long serialVersionUID = -493473356731931321L;
	
	private Map<String, Team> teams = new HashMap<String, Game.Team>();
	private String gameKind;
	private Date startDate,endDate;
	private String id;

	public Game(String gameKind,String team1,String team2){
		this.id = UUID.randomUUID().toString();
		this.gameKind = gameKind;
		teams.put(team1, new Team(team1));
		teams.put(team2, new Team(team2));
	}
	
	public String getId() {
		return id;
	}
	
	public void startGame(){
		startDate = new Date();
	}
	
	public void endGame(){
		endDate = new Date();
	}
	
	public boolean isGamingTeams(String[] teamNames){
		boolean isSame = true;
		for(String team:teamNames){
			if(!teams.containsKey(team)){
				isSame = false;
			}
		}
		return isSame;
	}
	
	public void addScore(String team,int score){
		teams.get(team).addScore(score);
	}
	
	public void addStatistics(String team,String teammember,Integer result,Integer type){
		String record = teammember;
		if(result != null){
			record += "$"+result;
		}
		else{
			record += "$-999999";
		}
		record += "$"+type + "$" + new Date().getTime();
		teams.get(team).addTeammemberBehavior(record);
	}
	
	private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
		oos.writeUTF(id);
		oos.writeUTF(gameKind);
		oos.writeLong(startDate.getTime());
		oos.writeLong(endDate.getTime());
		oos.writeInt(teams.size());
		for(Map.Entry<String, Team> entry:teams.entrySet()){
			oos.writeUTF(entry.getKey());
			oos.writeObject(entry.getValue());
		}
		System.out.println(" Write Object ");
	}

	private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
		id = ois.readUTF();
		gameKind = ois.readUTF();
		long startTime = ois.readLong(),endTime = ois.readLong();
		startDate = new Date(startTime);
		endDate = new Date(endTime);
		
		int size = ois.readInt();
		teams = new HashMap<String, Game.Team>();
		for(int i=0;i<size;i++){
			teams.put(ois.readUTF(), (Team)ois.readObject());
		}
		System.out.println(" Read Object ");
	}
	
	public String getHistoryRecordDesc(){
		String result = "";
		for(Map.Entry<String, Team> entry:teams.entrySet()){
			
			if(!result.contains(" VS ")){
				result += entry.getKey() + "    " +entry.getValue().getScore();
				result += " VS ";
			}
			else{
				result += entry.getValue().getScore() + "    " + entry.getKey();
			}
			
		}
		
		return result;
		
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String toString(){
		return teams.toString();
	}
	
	private class Team  implements Serializable{

		private static final long serialVersionUID = -156643609014478287L;
		
		private String name;
		private int score;

		private List<String> behaviorList;
		
		public Team(String name){
			this.name = name;
			this.score = 0;
			this.behaviorList = new ArrayList<String>();
		}
		
		public void addScore(int score){
			this.score+=score;
		}
		
		public void addTeammemberBehavior(String record){
			this.behaviorList.add(record);
		}
		
		private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
			oos.writeUTF(name);
			oos.writeInt(score);
			oos.writeInt(behaviorList.size());
			for(int i=0;i<behaviorList.size();i++){
				oos.writeUTF(behaviorList.get(i));
			}
			System.out.println(" Write Object ");
		}

		private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
			name = ois.readUTF();
			score = ois.readInt();
			int size = ois.readInt();
			
			behaviorList = new ArrayList<String>();
			for(int i=0;i<size;i++){
				behaviorList.add(ois.readUTF());
			}
			System.out.println(" Read Object ");
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}
		
		@Override
		public String toString(){
			return name + ":" + score + "(" + behaviorList.toString() + ")";
		}
	}
	
	public static class BasketballType{
		public final static int PTS = 0;
		public final static int FOULS = 1;
		public final static int ASTS = 2;
		public final static int REBS = 3;
		public final static int STLS = 4;
		public final static int BLKS = 5;
	}
	

}
