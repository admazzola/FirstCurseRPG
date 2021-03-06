package com.mygdx.game.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Unit;
import com.mygdx.game.AssetMGMT.AssetCenter;
import com.mygdx.game.AssetMGMT.CommonSounds;
import com.mygdx.game.AssetMGMT.MapRegion;
import com.mygdx.game.abilities.AbilityEffect;
import com.mygdx.game.abilities.AbilityExecutionInfo;
import com.mygdx.game.abilities.AbilityStats;
import com.mygdx.game.abilities.AbilityType;
import com.mygdx.game.abilities.EditUnitStatEffect;
import com.mygdx.game.abilities.EffectTargets;
import com.mygdx.game.abilities.PlaySoundEffect;
import com.mygdx.game.abilities.UnitAnimationEffect;
import com.mygdx.game.abilities.UnitManeuverEffect;
import com.mygdx.game.audio.MusicalEmotion;
import com.mygdx.game.battle.BattlingAIController;
import com.mygdx.game.controller.Player;
import com.mygdx.game.renderer.TintScreenEffect;
import com.mygdx.game.screens.GameScreen;



public class BattleController {

	/** The blocks making up the world **/
	//List<TerrainTile> tiles = new ArrayList<TerrainTile>();
	
	//List<Unit> units = new ArrayList<Unit>();
	//List<MapRegion> regions = new ArrayList<MapRegion>();
	
	BattlingAIController enemyAIController;
	
	
	
	Unit units[][] = new Unit[2][3];
	
	static final int ALLIED_TEAM_ID = 0;
	static final int ENEMY_TEAM_ID = 1;
	
	TiledMap map;

	public BattleController() {
		
		map = AssetCenter.getManager().get("maps/grassbattle.tmx");
		
		
		
		enemyAIController = new BattlingAIController(this);
	}
	
	

	public void initBattle() {
		
		CommonSounds.STARTBATTLE.play(0.5f);
		
		
		enemyAIController.initBattle();
		
		
		GameScreen.getGUIController().getBattleInterfaceController().initBattle();
		
		GameScreen.getGUIController().getScreenEffectManager().forceScreenEffect( new TintScreenEffect(Color.BLACK) );		
		GameScreen.getGUIController().getScreenEffectManager().queueScreenEffect(new TintScreenEffect(Color.CLEAR,1));
						
		GameScreen.getMusicController().setMusicalEmotion(MusicalEmotion.BATTLE);
		
		units[0] = Player.getBattlingParty();
		
		
		for(int team = 0; team < 2;team++)
		{
			for(int unit=0;unit < 3;unit++)
			{
				
				if(getUnits()[team][unit] != null && getUnits()[team][unit].getSprite()!=null){
					
					getUnits()[team][unit].getWorldModel().setBattleSpot(
							getUnits()[team][unit].getWorldModel().getPosition().cpy()    );
										
					}
			}
			
		}
		
		
	}
	
	
	public void endBattle(){
		System.out.println("ending battle");
		GameScreen.getMusicController().setMusicalEmotion(MusicalEmotion.CALM);
		
		GameScreen.getGUIController().getBattleInterfaceController().endBattle();
		
		CommonSounds.POWERUP.play(0.5f);
	}
	
	public void update(float delta)
	{
		
		for(int team = 0; team < 2;team++)
		{
			for(int unit=0;unit < 3;unit++)
			{
				if(units[team][unit]!=null)
				{
				units[team][unit].update(delta);
			
				}
			}
		}
		
		enemyAIController.update(delta);
	}
	
	public boolean battleIsOver()
	{
		
		boolean allDead = true;
		for(Unit enemy : units[ENEMY_TEAM_ID])
		{
			if(enemy !=null && enemy.isAlive())
			{
				allDead = false;
			}
			
		}
		return allDead;
		
	}
	
	
	
	
	
	public TiledMap getMap()
	{
		return map;
	}
	


	
	public Unit[][] getUnits() {
		
		return units;
	}



	public void addEnemy(Unit unit) {
		units[1][0] = unit;
		
	}


	public void executeUnitAbility(AbilityExecutionInfo info) {
		AbilityType type = AbilityType.Slash;
	
		System.out.println("executing unit ability in battle!");
		for(AbilityEffect effect : type.getEffects()){
				executeAbilityEffect(info, effect);
		}
		
		info.getCaster().resetCooldown(info.getType().getStatValue(AbilityStats.COOLDOWN));
		
	}

	public void executeAbilityEffect(AbilityExecutionInfo info, AbilityEffect effect) {
		
		if(effect instanceof EditUnitStatEffect)			
		{
			
			
			EditUnitStatEffect stateffect = (EditUnitStatEffect) effect;
			for(Unit target :  getTargets(info,effect)){
				target.queueStatValueEdit(stateffect.getStat(),stateffect.getDelta(),stateffect.getDelay());
			}
			
			
		}
		if(effect instanceof PlaySoundEffect)			
		{
			
			PlaySoundEffect soundEffect = ((PlaySoundEffect)effect);
			
			
			GameScreen.getSoundManager().queueSound(soundEffect.getSound(),soundEffect.getVolume(),effect.getDelay());
			//soundEffect.getSound().play(soundEffect.getVolume());
		}
		
		if(effect instanceof UnitAnimationEffect)
		{
			UnitAnimationEffect animEffect = (UnitAnimationEffect) effect;
			
			for(Unit target :  getTargets(info,effect)){
				target.playAnimation(animEffect.getType(), effect.getDelay());
			}
		}

		if(effect instanceof UnitManeuverEffect)			
		{
			UnitManeuverEffect maneuver = (UnitManeuverEffect) effect;
			info.getCaster().beginManeuver(maneuver.getType(), maneuver.getInterpolation(), getTargets(info,effect), effect.getDelay());
			
		}
		
	}


	private Unit[] getTargets(AbilityExecutionInfo info, AbilityEffect effect) {
		
		if(effect.getTargets() == EffectTargets.SELF)
		{
			return new Unit[]{info.getCaster()};
		}
		
		if(effect.getTargets() == EffectTargets.TARGETENEMY
				||  effect.getTargets() == EffectTargets.TARGETALLY )
		{
			return new Unit[]{info.getTarget()};
		}
		
		if(effect.getTargets() == EffectTargets.EVERYALLY )
		{
			return units[ALLIED_TEAM_ID];
		}
		
		if(effect.getTargets() == EffectTargets.EVERYENEMY )
		{
			return units[ENEMY_TEAM_ID];
		}
		
		return new Unit[]{};
	}
	
	
	
	
	

	
}