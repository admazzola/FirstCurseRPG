package com.mygdx.game.AssetMGMT;



public enum AbilityType {

	HUMAN("human",SpriteSheet.BASICSHEET,190),
	EYEMONSTER("eyemonster",SpriteSheet.BASICSHEET,216),
	
	
	
	;

	AbilityType(String name,SpriteSheet sheet, int index)
	{
		this.name=name;
		this.sheet=sheet;
		this.index=index;
		
	}	
	public String name = "";
	
	public  SpriteSheet sheet = SpriteSheet.BASICSHEET;
	
	public  int index = 200;
	
	
	
	public SpriteSheet getSheet() {
		
		return sheet;
	}

	public int getX() {
		
		return (index % sheet.tilesPerRow) * sheet.tilesize;
	}
	
	public int getY() {
		
		return (index / sheet.tilesPerRow) * sheet.tilesize ;
	}

}