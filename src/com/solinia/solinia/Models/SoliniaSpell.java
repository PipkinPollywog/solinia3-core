package com.solinia.solinia.Models;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.solinia.solinia.Exceptions.CoreStateInitException;
import com.solinia.solinia.Exceptions.InvalidSpellSettingException;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Utils.SpellTargetType;
import com.solinia.solinia.Utils.Utils;

import net.md_5.bungee.api.ChatColor;

public class SoliniaSpell implements ISoliniaSpell {
	private List<SoliniaSpellClass> allowedClasses = new ArrayList<SoliniaSpellClass>();

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("player_1")
	@Expose
	private String player1;
	@SerializedName("teleport_zone")
	@Expose
	private String teleportZone;
	@SerializedName("you_cast")
	@Expose
	private String youCast;
	@SerializedName("other_casts")
	@Expose
	private String otherCasts;
	@SerializedName("cast_on_you")
	@Expose
	private String castOnYou;
	@SerializedName("cast_on_other")
	@Expose
	private String castOnOther;
	@SerializedName("spell_fades")
	@Expose
	private String spellFades;
	@SerializedName("range")
	@Expose
	private Integer range;
	@SerializedName("aoerange")
	@Expose
	private Integer aoerange;
	@SerializedName("pushback")
	@Expose
	private Integer pushback;
	@SerializedName("pushup")
	@Expose
	private Integer pushup;
	@SerializedName("cast_time")
	@Expose
	private Integer castTime;
	@SerializedName("recovery_time")
	@Expose
	private Integer recoveryTime;
	@SerializedName("recast_time")
	@Expose
	private Integer recastTime;
	@SerializedName("buffdurationformula")
	@Expose
	private Integer buffdurationformula;
	@SerializedName("buffduration")
	@Expose
	private Integer buffduration;
	@SerializedName("AEDuration")
	@Expose
	private Integer aEDuration;
	@SerializedName("mana")
	@Expose
	private Integer mana;
	@SerializedName("effect_base_value1")
	@Expose
	private Integer effectBaseValue1;
	@SerializedName("effect_base_value2")
	@Expose
	private Integer effectBaseValue2;
	@SerializedName("effect_base_value3")
	@Expose
	private Integer effectBaseValue3;
	@SerializedName("effect_base_value4")
	@Expose
	private Integer effectBaseValue4;
	@SerializedName("effect_base_value5")
	@Expose
	private Integer effectBaseValue5;
	@SerializedName("effect_base_value6")
	@Expose
	private Integer effectBaseValue6;
	@SerializedName("effect_base_value7")
	@Expose
	private Integer effectBaseValue7;
	@SerializedName("effect_base_value8")
	@Expose
	private Integer effectBaseValue8;
	@SerializedName("effect_base_value9")
	@Expose
	private Integer effectBaseValue9;
	@SerializedName("effect_base_value10")
	@Expose
	private Integer effectBaseValue10;
	@SerializedName("effect_base_value11")
	@Expose
	private Integer effectBaseValue11;
	@SerializedName("effect_base_value12")
	@Expose
	private Integer effectBaseValue12;
	@SerializedName("effect_limit_value1")
	@Expose
	private Integer effectLimitValue1;
	@SerializedName("effect_limit_value2")
	@Expose
	private Integer effectLimitValue2;
	@SerializedName("effect_limit_value3")
	@Expose
	private Integer effectLimitValue3;
	@SerializedName("effect_limit_value4")
	@Expose
	private Integer effectLimitValue4;
	@SerializedName("effect_limit_value5")
	@Expose
	private Integer effectLimitValue5;
	@SerializedName("effect_limit_value6")
	@Expose
	private Integer effectLimitValue6;
	@SerializedName("effect_limit_value7")
	@Expose
	private Integer effectLimitValue7;
	@SerializedName("effect_limit_value8")
	@Expose
	private Integer effectLimitValue8;
	@SerializedName("effect_limit_value9")
	@Expose
	private Integer effectLimitValue9;
	@SerializedName("effect_limit_value10")
	@Expose
	private Integer effectLimitValue10;
	@SerializedName("effect_limit_value11")
	@Expose
	private Integer effectLimitValue11;
	@SerializedName("effect_limit_value12")
	@Expose
	private Integer effectLimitValue12;
	@SerializedName("max1")
	@Expose
	private Integer max1;
	@SerializedName("max2")
	@Expose
	private Integer max2;
	@SerializedName("max3")
	@Expose
	private Integer max3;
	@SerializedName("max4")
	@Expose
	private Integer max4;
	@SerializedName("max5")
	@Expose
	private Integer max5;
	@SerializedName("max6")
	@Expose
	private Integer max6;
	@SerializedName("max7")
	@Expose
	private Integer max7;
	@SerializedName("max8")
	@Expose
	private Integer max8;
	@SerializedName("max9")
	@Expose
	private Integer max9;
	@SerializedName("max10")
	@Expose
	private Integer max10;
	@SerializedName("max11")
	@Expose
	private Integer max11;
	@SerializedName("max12")
	@Expose
	private Integer max12;
	@SerializedName("icon")
	@Expose
	private Integer icon;
	@SerializedName("memicon")
	@Expose
	private Integer memicon;
	@SerializedName("components1")
	@Expose
	private Integer components1;
	@SerializedName("components2")
	@Expose
	private Integer components2;
	@SerializedName("components3")
	@Expose
	private Integer components3;
	@SerializedName("components4")
	@Expose
	private Integer components4;
	@SerializedName("component_counts1")
	@Expose
	private Integer componentCounts1;
	@SerializedName("component_counts2")
	@Expose
	private Integer componentCounts2;
	@SerializedName("component_counts3")
	@Expose
	private Integer componentCounts3;
	@SerializedName("component_counts4")
	@Expose
	private Integer componentCounts4;
	@SerializedName("NoexpendReagent1")
	@Expose
	private Integer noexpendReagent1;
	@SerializedName("NoexpendReagent2")
	@Expose
	private Integer noexpendReagent2;
	@SerializedName("NoexpendReagent3")
	@Expose
	private Integer noexpendReagent3;
	@SerializedName("NoexpendReagent4")
	@Expose
	private Integer noexpendReagent4;
	@SerializedName("formula1")
	@Expose
	private Integer formula1;
	@SerializedName("formula2")
	@Expose
	private Integer formula2;
	@SerializedName("formula3")
	@Expose
	private Integer formula3;
	@SerializedName("formula4")
	@Expose
	private Integer formula4;
	@SerializedName("formula5")
	@Expose
	private Integer formula5;
	@SerializedName("formula6")
	@Expose
	private Integer formula6;
	@SerializedName("formula7")
	@Expose
	private Integer formula7;
	@SerializedName("formula8")
	@Expose
	private Integer formula8;
	@SerializedName("formula9")
	@Expose
	private Integer formula9;
	@SerializedName("formula10")
	@Expose
	private Integer formula10;
	@SerializedName("formula11")
	@Expose
	private Integer formula11;
	@SerializedName("formula12")
	@Expose
	private Integer formula12;
	@SerializedName("LightType")
	@Expose
	private Integer lightType;
	@SerializedName("goodEffect")
	@Expose
	private Integer goodEffect;
	@SerializedName("Activated")
	@Expose
	private Integer activated;
	@SerializedName("resisttype")
	@Expose
	private Integer resisttype;
	@SerializedName("effectid1")
	@Expose
	private Integer effectid1;
	@SerializedName("effectid2")
	@Expose
	private Integer effectid2;
	@SerializedName("effectid3")
	@Expose
	private Integer effectid3;
	@SerializedName("effectid4")
	@Expose
	private Integer effectid4;
	@SerializedName("effectid5")
	@Expose
	private Integer effectid5;
	@SerializedName("effectid6")
	@Expose
	private Integer effectid6;
	@SerializedName("effectid7")
	@Expose
	private Integer effectid7;
	@SerializedName("effectid8")
	@Expose
	private Integer effectid8;
	@SerializedName("effectid9")
	@Expose
	private Integer effectid9;
	@SerializedName("effectid10")
	@Expose
	private Integer effectid10;
	@SerializedName("effectid11")
	@Expose
	private Integer effectid11;
	@SerializedName("effectid12")
	@Expose
	private Integer effectid12;
	@SerializedName("targettype")
	@Expose
	private Integer targettype;
	@SerializedName("basediff")
	@Expose
	private Integer basediff;
	@SerializedName("skill")
	@Expose
	private Integer skill;
	@SerializedName("zonetype")
	@Expose
	private Integer zonetype;
	@SerializedName("EnvironmentType")
	@Expose
	private Integer environmentType;
	@SerializedName("TimeOfDay")
	@Expose
	private Integer timeOfDay;
	@SerializedName("classes1")
	@Expose
	private Integer classes1;
	@SerializedName("classes2")
	@Expose
	private Integer classes2;
	@SerializedName("classes3")
	@Expose
	private Integer classes3;
	@SerializedName("classes4")
	@Expose
	private Integer classes4;
	@SerializedName("classes5")
	@Expose
	private Integer classes5;
	@SerializedName("classes6")
	@Expose
	private Integer classes6;
	@SerializedName("classes7")
	@Expose
	private Integer classes7;
	@SerializedName("classes8")
	@Expose
	private Integer classes8;
	@SerializedName("classes9")
	@Expose
	private Integer classes9;
	@SerializedName("classes10")
	@Expose
	private Integer classes10;
	@SerializedName("classes11")
	@Expose
	private Integer classes11;
	@SerializedName("classes12")
	@Expose
	private Integer classes12;
	@SerializedName("classes13")
	@Expose
	private Integer classes13;
	@SerializedName("classes14")
	@Expose
	private Integer classes14;
	@SerializedName("classes15")
	@Expose
	private Integer classes15;
	@SerializedName("classes16")
	@Expose
	private Integer classes16;
	@SerializedName("CastingAnim")
	@Expose
	private Integer castingAnim;
	@SerializedName("TargetAnim")
	@Expose
	private Integer targetAnim;
	@SerializedName("TravelType")
	@Expose
	private Integer travelType;
	@SerializedName("SpellAffectIndex")
	@Expose
	private Integer spellAffectIndex;
	@SerializedName("disallow_sit")
	@Expose
	private Integer disallowSit;
	@SerializedName("deities0")
	@Expose
	private Integer deities0;
	@SerializedName("deities1")
	@Expose
	private Integer deities1;
	@SerializedName("deities2")
	@Expose
	private Integer deities2;
	@SerializedName("deities3")
	@Expose
	private Integer deities3;
	@SerializedName("deities4")
	@Expose
	private Integer deities4;
	@SerializedName("deities5")
	@Expose
	private Integer deities5;
	@SerializedName("deities6")
	@Expose
	private Integer deities6;
	@SerializedName("deities7")
	@Expose
	private Integer deities7;
	@SerializedName("deities8")
	@Expose
	private Integer deities8;
	@SerializedName("deities9")
	@Expose
	private Integer deities9;
	@SerializedName("deities10")
	@Expose
	private Integer deities10;
	@SerializedName("deities11")
	@Expose
	private Integer deities11;
	@SerializedName("deities12")
	@Expose
	private Integer deities12;
	@SerializedName("deities13")
	@Expose
	private Integer deities13;
	@SerializedName("deities14")
	@Expose
	private Integer deities14;
	@SerializedName("deities15")
	@Expose
	private Integer deities15;
	@SerializedName("deities16")
	@Expose
	private Integer deities16;
	@SerializedName("field142")
	@Expose
	private Integer field142;
	@SerializedName("field143")
	@Expose
	private Integer field143;
	@SerializedName("new_icon")
	@Expose
	private Integer newIcon;
	@SerializedName("spellanim")
	@Expose
	private Integer spellanim;
	@SerializedName("uninterruptable")
	@Expose
	private Integer uninterruptable;
	@SerializedName("ResistDiff")
	@Expose
	private Integer resistDiff;
	@SerializedName("dot_stacking_exempt")
	@Expose
	private Integer dotStackingExempt;
	@SerializedName("deleteable")
	@Expose
	private Integer deleteable;
	@SerializedName("RecourseLink")
	@Expose
	private Integer recourseLink;
	@SerializedName("no_partial_resist")
	@Expose
	private Integer noPartialResist;
	@SerializedName("field152")
	@Expose
	private Integer field152;
	@SerializedName("field153")
	@Expose
	private Integer field153;
	@SerializedName("short_buff_box")
	@Expose
	private Integer shortBuffBox;
	@SerializedName("descnum")
	@Expose
	private Integer descnum;
	@SerializedName("typedescnum")
	@Expose
	private Integer typedescnum;
	@SerializedName("effectdescnum")
	@Expose
	private Integer effectdescnum;
	@SerializedName("effectdescnum2")
	@Expose
	private Integer effectdescnum2;
	@SerializedName("npc_no_los")
	@Expose
	private Integer npcNoLos;
	@SerializedName("field160")
	@Expose
	private Integer field160;
	@SerializedName("reflectable")
	@Expose
	private Integer reflectable;
	@SerializedName("bonushate")
	@Expose
	private Integer bonushate;
	@SerializedName("field163")
	@Expose
	private Integer field163;
	@SerializedName("field164")
	@Expose
	private Integer field164;
	@SerializedName("ldon_trap")
	@Expose
	private Integer ldonTrap;
	@SerializedName("EndurCost")
	@Expose
	private Integer endurCost;
	@SerializedName("EndurTimerIndex")
	@Expose
	private Integer endurTimerIndex;
	@SerializedName("IsDiscipline")
	@Expose
	private Integer isDiscipline;
	@SerializedName("field169")
	@Expose
	private Integer field169;
	@SerializedName("field170")
	@Expose
	private Integer field170;
	@SerializedName("field171")
	@Expose
	private Integer field171;
	@SerializedName("field172")
	@Expose
	private Integer field172;
	@SerializedName("HateAdded")
	@Expose
	private Integer hateAdded;
	@SerializedName("EndurUpkeep")
	@Expose
	private Integer endurUpkeep;
	@SerializedName("numhitstype")
	@Expose
	private Integer numhitstype;
	@SerializedName("numhits")
	@Expose
	private Integer numhits;
	@SerializedName("pvpresistbase")
	@Expose
	private Integer pvpresistbase;
	@SerializedName("pvpresistcalc")
	@Expose
	private Integer pvpresistcalc;
	@SerializedName("pvpresistcap")
	@Expose
	private Integer pvpresistcap;
	@SerializedName("spell_category")
	@Expose
	private Integer spellCategory;
	@SerializedName("field181")
	@Expose
	private Integer field181;
	@SerializedName("field182")
	@Expose
	private Integer field182;
	@SerializedName("field183")
	@Expose
	private Integer field183;
	@SerializedName("field184")
	@Expose
	private Integer field184;
	@SerializedName("can_mgb")
	@Expose
	private Integer canMgb;
	@SerializedName("nodispell")
	@Expose
	private Integer nodispell;
	@SerializedName("npc_category")
	@Expose
	private Integer npcCategory;
	@SerializedName("npc_usefulness")
	@Expose
	private Integer npcUsefulness;
	@SerializedName("MinResist")
	@Expose
	private Integer minResist;
	@SerializedName("MaxResist")
	@Expose
	private Integer maxResist;
	@SerializedName("viral_targets")
	@Expose
	private Integer viralTargets;
	@SerializedName("viral_timer")
	@Expose
	private Integer viralTimer;
	@SerializedName("nimbuseffect")
	@Expose
	private Integer nimbuseffect;
	@SerializedName("ConeStartAngle")
	@Expose
	private Integer coneStartAngle;
	@SerializedName("ConeStopAngle")
	@Expose
	private Integer coneStopAngle;
	@SerializedName("sneaking")
	@Expose
	private Integer sneaking;
	@SerializedName("not_extendable")
	@Expose
	private Integer notExtendable;
	@SerializedName("field198")
	@Expose
	private Integer field198;
	@SerializedName("field199")
	@Expose
	private Integer field199;
	@SerializedName("suspendable")
	@Expose
	private Integer suspendable;
	@SerializedName("viral_range")
	@Expose
	private Integer viralRange;
	@SerializedName("songcap")
	@Expose
	private Integer songcap;
	@SerializedName("field203")
	@Expose
	private Integer field203;
	@SerializedName("field204")
	@Expose
	private Integer field204;
	@SerializedName("no_block")
	@Expose
	private Integer noBlock;
	@SerializedName("field206")
	@Expose
	private Integer field206;
	@SerializedName("spellgroup")
	@Expose
	private Integer spellgroup;
	@SerializedName("rank")
	@Expose
	private Integer rank;
	@SerializedName("field209")
	@Expose
	private Integer field209;
	@SerializedName("field210")
	@Expose
	private Integer field210;
	@SerializedName("CastRestriction")
	@Expose
	private Integer castRestriction;
	@SerializedName("allowrest")
	@Expose
	private Integer allowrest;
	@SerializedName("InCombat")
	@Expose
	private Integer inCombat;
	@SerializedName("OutofCombat")
	@Expose
	private Integer outofCombat;
	@SerializedName("field215")
	@Expose
	private Integer field215;
	@SerializedName("field216")
	@Expose
	private Integer field216;
	@SerializedName("field217")
	@Expose
	private Integer field217;
	@SerializedName("aemaxtargets")
	@Expose
	private Integer aemaxtargets;
	@SerializedName("maxtargets")
	@Expose
	private Integer maxtargets;
	@SerializedName("field220")
	@Expose
	private Integer field220;
	@SerializedName("field221")
	@Expose
	private Integer field221;
	@SerializedName("field222")
	@Expose
	private Integer field222;
	@SerializedName("field223")
	@Expose
	private Integer field223;
	@SerializedName("persistdeath")
	@Expose
	private Integer persistdeath;
	@SerializedName("field225")
	@Expose
	private Integer field225;
	@SerializedName("field226")
	@Expose
	private Integer field226;
	@SerializedName("min_dist")
	@Expose
	private Double minDist;
	@SerializedName("min_dist_mod")
	@Expose
	private Double minDistMod;
	@SerializedName("max_dist")
	@Expose
	private Double maxDist;
	@SerializedName("max_dist_mod")
	@Expose
	private Double maxDistMod;
	@SerializedName("min_range")
	@Expose
	private Integer minRange;
	@SerializedName("field232")
	@Expose
	private Integer field232;
	@SerializedName("field233")
	@Expose
	private Integer field233;
	@SerializedName("field234")
	@Expose
	private Integer field234;
	@SerializedName("field235")
	@Expose
	private Integer field235;
	@SerializedName("field236")
	@Expose
	private Integer field236;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlayer1() {
		return player1;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	public String getTeleportZone() {
		return teleportZone;
	}

	public void setTeleportZone(String teleportZone) {
		this.teleportZone = teleportZone;
	}

	public String getYouCast() {
		return youCast;
	}

	public void setYouCast(String youCast) {
		this.youCast = youCast;
	}

	public String getOtherCasts() {
		return otherCasts;
	}

	public void setOtherCasts(String otherCasts) {
		this.otherCasts = otherCasts;
	}

	public String getCastOnYou() {
		return castOnYou;
	}

	public void setCastOnYou(String castOnYou) {
		this.castOnYou = castOnYou;
	}

	public String getCastOnOther() {
		return castOnOther;
	}

	public void setCastOnOther(String castOnOther) {
		this.castOnOther = castOnOther;
	}

	public String getSpellFades() {
		return spellFades;
	}

	public void setSpellFades(String spellFades) {
		this.spellFades = spellFades;
	}

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	public Integer getAoerange() {
		return aoerange;
	}

	public void setAoerange(Integer aoerange) {
		this.aoerange = aoerange;
	}

	public Integer getPushback() {
		return pushback;
	}

	public void setPushback(Integer pushback) {
		this.pushback = pushback;
	}

	public Integer getPushup() {
		return pushup;
	}

	public void setPushup(Integer pushup) {
		this.pushup = pushup;
	}

	public Integer getCastTime() {
		return castTime;
	}

	public void setCastTime(Integer castTime) {
		this.castTime = castTime;
	}

	public Integer getRecoveryTime() {
		return recoveryTime;
	}

	public void setRecoveryTime(Integer recoveryTime) {
		this.recoveryTime = recoveryTime;
	}

	public Integer getRecastTime() {
		return recastTime;
	}

	public void setRecastTime(Integer recastTime) {
		this.recastTime = recastTime;
	}

	public Integer getBuffdurationformula() {
		return buffdurationformula;
	}

	public void setBuffdurationformula(Integer buffdurationformula) {
		this.buffdurationformula = buffdurationformula;
	}

	public Integer getBuffduration() {
		return buffduration;
	}

	public void setBuffduration(Integer buffduration) {
		this.buffduration = buffduration;
	}

	public Integer getAEDuration() {
		return aEDuration;
	}

	public void setAEDuration(Integer aEDuration) {
		this.aEDuration = aEDuration;
	}

	public Integer getMana() {
		return mana;
	}

	public void setMana(Integer mana) {
		this.mana = mana;
	}

	public Integer getEffectBaseValue1() {
		return effectBaseValue1;
	}

	public void setEffectBaseValue1(Integer effectBaseValue1) {
		this.effectBaseValue1 = effectBaseValue1;
	}

	public Integer getEffectBaseValue2() {
		return effectBaseValue2;
	}

	public void setEffectBaseValue2(Integer effectBaseValue2) {
		this.effectBaseValue2 = effectBaseValue2;
	}

	public Integer getEffectBaseValue3() {
		return effectBaseValue3;
	}

	public void setEffectBaseValue3(Integer effectBaseValue3) {
		this.effectBaseValue3 = effectBaseValue3;
	}

	public Integer getEffectBaseValue4() {
		return effectBaseValue4;
	}

	public void setEffectBaseValue4(Integer effectBaseValue4) {
		this.effectBaseValue4 = effectBaseValue4;
	}

	public Integer getEffectBaseValue5() {
		return effectBaseValue5;
	}

	public void setEffectBaseValue5(Integer effectBaseValue5) {
		this.effectBaseValue5 = effectBaseValue5;
	}

	public Integer getEffectBaseValue6() {
		return effectBaseValue6;
	}

	public void setEffectBaseValue6(Integer effectBaseValue6) {
		this.effectBaseValue6 = effectBaseValue6;
	}

	public Integer getEffectBaseValue7() {
		return effectBaseValue7;
	}

	public void setEffectBaseValue7(Integer effectBaseValue7) {
		this.effectBaseValue7 = effectBaseValue7;
	}

	public Integer getEffectBaseValue8() {
		return effectBaseValue8;
	}

	public void setEffectBaseValue8(Integer effectBaseValue8) {
		this.effectBaseValue8 = effectBaseValue8;
	}

	public Integer getEffectBaseValue9() {
		return effectBaseValue9;
	}

	public void setEffectBaseValue9(Integer effectBaseValue9) {
		this.effectBaseValue9 = effectBaseValue9;
	}

	public Integer getEffectBaseValue10() {
		return effectBaseValue10;
	}

	public void setEffectBaseValue10(Integer effectBaseValue10) {
		this.effectBaseValue10 = effectBaseValue10;
	}

	public Integer getEffectBaseValue11() {
		return effectBaseValue11;
	}

	public void setEffectBaseValue11(Integer effectBaseValue11) {
		this.effectBaseValue11 = effectBaseValue11;
	}

	public Integer getEffectBaseValue12() {
		return effectBaseValue12;
	}

	public void setEffectBaseValue12(Integer effectBaseValue12) {
		this.effectBaseValue12 = effectBaseValue12;
	}

	public Integer getEffectLimitValue1() {
		return effectLimitValue1;
	}

	public void setEffectLimitValue1(Integer effectLimitValue1) {
		this.effectLimitValue1 = effectLimitValue1;
	}

	public Integer getEffectLimitValue2() {
		return effectLimitValue2;
	}

	public void setEffectLimitValue2(Integer effectLimitValue2) {
		this.effectLimitValue2 = effectLimitValue2;
	}

	public Integer getEffectLimitValue3() {
		return effectLimitValue3;
	}

	public void setEffectLimitValue3(Integer effectLimitValue3) {
		this.effectLimitValue3 = effectLimitValue3;
	}

	public Integer getEffectLimitValue4() {
		return effectLimitValue4;
	}

	public void setEffectLimitValue4(Integer effectLimitValue4) {
		this.effectLimitValue4 = effectLimitValue4;
	}

	public Integer getEffectLimitValue5() {
		return effectLimitValue5;
	}

	public void setEffectLimitValue5(Integer effectLimitValue5) {
		this.effectLimitValue5 = effectLimitValue5;
	}

	public Integer getEffectLimitValue6() {
		return effectLimitValue6;
	}

	public void setEffectLimitValue6(Integer effectLimitValue6) {
		this.effectLimitValue6 = effectLimitValue6;
	}

	public Integer getEffectLimitValue7() {
		return effectLimitValue7;
	}

	public void setEffectLimitValue7(Integer effectLimitValue7) {
		this.effectLimitValue7 = effectLimitValue7;
	}

	public Integer getEffectLimitValue8() {
		return effectLimitValue8;
	}

	public void setEffectLimitValue8(Integer effectLimitValue8) {
		this.effectLimitValue8 = effectLimitValue8;
	}

	public Integer getEffectLimitValue9() {
		return effectLimitValue9;
	}

	public void setEffectLimitValue9(Integer effectLimitValue9) {
		this.effectLimitValue9 = effectLimitValue9;
	}

	public Integer getEffectLimitValue10() {
		return effectLimitValue10;
	}

	public void setEffectLimitValue10(Integer effectLimitValue10) {
		this.effectLimitValue10 = effectLimitValue10;
	}

	public Integer getEffectLimitValue11() {
		return effectLimitValue11;
	}

	public void setEffectLimitValue11(Integer effectLimitValue11) {
		this.effectLimitValue11 = effectLimitValue11;
	}

	public Integer getEffectLimitValue12() {
		return effectLimitValue12;
	}

	public void setEffectLimitValue12(Integer effectLimitValue12) {
		this.effectLimitValue12 = effectLimitValue12;
	}

	public Integer getMax1() {
		return max1;
	}

	public void setMax1(Integer max1) {
		this.max1 = max1;
	}

	public Integer getMax2() {
		return max2;
	}

	public void setMax2(Integer max2) {
		this.max2 = max2;
	}

	public Integer getMax3() {
		return max3;
	}

	public void setMax3(Integer max3) {
		this.max3 = max3;
	}

	public Integer getMax4() {
		return max4;
	}

	public void setMax4(Integer max4) {
		this.max4 = max4;
	}

	public Integer getMax5() {
		return max5;
	}

	public void setMax5(Integer max5) {
		this.max5 = max5;
	}

	public Integer getMax6() {
		return max6;
	}

	public void setMax6(Integer max6) {
		this.max6 = max6;
	}

	public Integer getMax7() {
		return max7;
	}

	public void setMax7(Integer max7) {
		this.max7 = max7;
	}

	public Integer getMax8() {
		return max8;
	}

	public void setMax8(Integer max8) {
		this.max8 = max8;
	}

	public Integer getMax9() {
		return max9;
	}

	public void setMax9(Integer max9) {
		this.max9 = max9;
	}

	public Integer getMax10() {
		return max10;
	}

	public void setMax10(Integer max10) {
		this.max10 = max10;
	}

	public Integer getMax11() {
		return max11;
	}

	public void setMax11(Integer max11) {
		this.max11 = max11;
	}

	public Integer getMax12() {
		return max12;
	}

	public void setMax12(Integer max12) {
		this.max12 = max12;
	}

	public Integer getIcon() {
		return icon;
	}

	public void setIcon(Integer icon) {
		this.icon = icon;
	}

	public Integer getMemicon() {
		return memicon;
	}

	public void setMemicon(Integer memicon) {
		this.memicon = memicon;
	}

	public Integer getComponents1() {
		return components1;
	}

	public void setComponents1(Integer components1) {
		this.components1 = components1;
	}

	public Integer getComponents2() {
		return components2;
	}

	public void setComponents2(Integer components2) {
		this.components2 = components2;
	}

	public Integer getComponents3() {
		return components3;
	}

	public void setComponents3(Integer components3) {
		this.components3 = components3;
	}

	public Integer getComponents4() {
		return components4;
	}

	public void setComponents4(Integer components4) {
		this.components4 = components4;
	}

	public Integer getComponentCounts1() {
		return componentCounts1;
	}

	public void setComponentCounts1(Integer componentCounts1) {
		this.componentCounts1 = componentCounts1;
	}

	public Integer getComponentCounts2() {
		return componentCounts2;
	}

	public void setComponentCounts2(Integer componentCounts2) {
		this.componentCounts2 = componentCounts2;
	}

	public Integer getComponentCounts3() {
		return componentCounts3;
	}

	public void setComponentCounts3(Integer componentCounts3) {
		this.componentCounts3 = componentCounts3;
	}

	public Integer getComponentCounts4() {
		return componentCounts4;
	}

	public void setComponentCounts4(Integer componentCounts4) {
		this.componentCounts4 = componentCounts4;
	}

	public Integer getNoexpendReagent1() {
		return noexpendReagent1;
	}

	public void setNoexpendReagent1(Integer noexpendReagent1) {
		this.noexpendReagent1 = noexpendReagent1;
	}

	public Integer getNoexpendReagent2() {
		return noexpendReagent2;
	}

	public void setNoexpendReagent2(Integer noexpendReagent2) {
		this.noexpendReagent2 = noexpendReagent2;
	}

	public Integer getNoexpendReagent3() {
		return noexpendReagent3;
	}

	public void setNoexpendReagent3(Integer noexpendReagent3) {
		this.noexpendReagent3 = noexpendReagent3;
	}

	public Integer getNoexpendReagent4() {
		return noexpendReagent4;
	}

	public void setNoexpendReagent4(Integer noexpendReagent4) {
		this.noexpendReagent4 = noexpendReagent4;
	}

	public Integer getFormula1() {
		return formula1;
	}

	public void setFormula1(Integer formula1) {
		this.formula1 = formula1;
	}

	public Integer getFormula2() {
		return formula2;
	}

	public void setFormula2(Integer formula2) {
		this.formula2 = formula2;
	}

	public Integer getFormula3() {
		return formula3;
	}

	public void setFormula3(Integer formula3) {
		this.formula3 = formula3;
	}

	public Integer getFormula4() {
		return formula4;
	}

	public void setFormula4(Integer formula4) {
		this.formula4 = formula4;
	}

	public Integer getFormula5() {
		return formula5;
	}

	public void setFormula5(Integer formula5) {
		this.formula5 = formula5;
	}

	public Integer getFormula6() {
		return formula6;
	}

	public void setFormula6(Integer formula6) {
		this.formula6 = formula6;
	}

	public Integer getFormula7() {
		return formula7;
	}

	public void setFormula7(Integer formula7) {
		this.formula7 = formula7;
	}

	public Integer getFormula8() {
		return formula8;
	}

	public void setFormula8(Integer formula8) {
		this.formula8 = formula8;
	}

	public Integer getFormula9() {
		return formula9;
	}

	public void setFormula9(Integer formula9) {
		this.formula9 = formula9;
	}

	public Integer getFormula10() {
		return formula10;
	}

	public void setFormula10(Integer formula10) {
		this.formula10 = formula10;
	}

	public Integer getFormula11() {
		return formula11;
	}

	public void setFormula11(Integer formula11) {
		this.formula11 = formula11;
	}

	public Integer getFormula12() {
		return formula12;
	}

	public void setFormula12(Integer formula12) {
		this.formula12 = formula12;
	}

	public Integer getLightType() {
		return lightType;
	}

	public void setLightType(Integer lightType) {
		this.lightType = lightType;
	}

	public Integer getGoodEffect() {
		return goodEffect;
	}

	public void setGoodEffect(Integer goodEffect) {
		this.goodEffect = goodEffect;
	}

	public Integer getActivated() {
		return activated;
	}

	public void setActivated(Integer activated) {
		this.activated = activated;
	}

	public Integer getResisttype() {
		return resisttype;
	}

	public void setResisttype(Integer resisttype) {
		this.resisttype = resisttype;
	}

	public Integer getEffectid1() {
		return effectid1;
	}

	public void setEffectid1(Integer effectid1) {
		this.effectid1 = effectid1;
	}

	public Integer getEffectid2() {
		return effectid2;
	}

	public void setEffectid2(Integer effectid2) {
		this.effectid2 = effectid2;
	}

	public Integer getEffectid3() {
		return effectid3;
	}

	public void setEffectid3(Integer effectid3) {
		this.effectid3 = effectid3;
	}

	public Integer getEffectid4() {
		return effectid4;
	}

	public void setEffectid4(Integer effectid4) {
		this.effectid4 = effectid4;
	}

	public Integer getEffectid5() {
		return effectid5;
	}

	public void setEffectid5(Integer effectid5) {
		this.effectid5 = effectid5;
	}

	public Integer getEffectid6() {
		return effectid6;
	}

	public void setEffectid6(Integer effectid6) {
		this.effectid6 = effectid6;
	}

	public Integer getEffectid7() {
		return effectid7;
	}

	public void setEffectid7(Integer effectid7) {
		this.effectid7 = effectid7;
	}

	public Integer getEffectid8() {
		return effectid8;
	}

	public void setEffectid8(Integer effectid8) {
		this.effectid8 = effectid8;
	}

	public Integer getEffectid9() {
		return effectid9;
	}

	public void setEffectid9(Integer effectid9) {
		this.effectid9 = effectid9;
	}

	public Integer getEffectid10() {
		return effectid10;
	}

	public void setEffectid10(Integer effectid10) {
		this.effectid10 = effectid10;
	}

	public Integer getEffectid11() {
		return effectid11;
	}

	public void setEffectid11(Integer effectid11) {
		this.effectid11 = effectid11;
	}

	public Integer getEffectid12() {
		return effectid12;
	}

	public void setEffectid12(Integer effectid12) {
		this.effectid12 = effectid12;
	}

	public Integer getTargettype() {
		return targettype;
	}

	public void setTargettype(Integer targettype) {
		this.targettype = targettype;
	}

	public Integer getBasediff() {
		return basediff;
	}

	public void setBasediff(Integer basediff) {
		this.basediff = basediff;
	}

	public Integer getSkill() {
		return skill;
	}

	public void setSkill(Integer skill) {
		this.skill = skill;
	}

	public Integer getZonetype() {
		return zonetype;
	}

	public void setZonetype(Integer zonetype) {
		this.zonetype = zonetype;
	}

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

	public Integer getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(Integer timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public Integer getClasses1() {
		return classes1;
	}

	public void setClasses1(Integer classes1) {
		this.classes1 = classes1;
	}

	public Integer getClasses2() {
		return classes2;
	}

	public void setClasses2(Integer classes2) {
		this.classes2 = classes2;
	}

	public Integer getClasses3() {
		return classes3;
	}

	public void setClasses3(Integer classes3) {
		this.classes3 = classes3;
	}

	public Integer getClasses4() {
		return classes4;
	}

	public void setClasses4(Integer classes4) {
		this.classes4 = classes4;
	}

	public Integer getClasses5() {
		return classes5;
	}

	public void setClasses5(Integer classes5) {
		this.classes5 = classes5;
	}

	public Integer getClasses6() {
		return classes6;
	}

	public void setClasses6(Integer classes6) {
		this.classes6 = classes6;
	}

	public Integer getClasses7() {
		return classes7;
	}

	public void setClasses7(Integer classes7) {
		this.classes7 = classes7;
	}

	public Integer getClasses8() {
		return classes8;
	}

	public void setClasses8(Integer classes8) {
		this.classes8 = classes8;
	}

	public Integer getClasses9() {
		return classes9;
	}

	public void setClasses9(Integer classes9) {
		this.classes9 = classes9;
	}

	public Integer getClasses10() {
		return classes10;
	}

	public void setClasses10(Integer classes10) {
		this.classes10 = classes10;
	}

	public Integer getClasses11() {
		return classes11;
	}

	public void setClasses11(Integer classes11) {
		this.classes11 = classes11;
	}

	public Integer getClasses12() {
		return classes12;
	}

	public void setClasses12(Integer classes12) {
		this.classes12 = classes12;
	}

	public Integer getClasses13() {
		return classes13;
	}

	public void setClasses13(Integer classes13) {
		this.classes13 = classes13;
	}

	public Integer getClasses14() {
		return classes14;
	}

	public void setClasses14(Integer classes14) {
		this.classes14 = classes14;
	}

	public Integer getClasses15() {
		return classes15;
	}

	public void setClasses15(Integer classes15) {
		this.classes15 = classes15;
	}

	public Integer getClasses16() {
		return classes16;
	}

	public void setClasses16(Integer classes16) {
		this.classes16 = classes16;
	}

	public Integer getCastingAnim() {
		return castingAnim;
	}

	public void setCastingAnim(Integer castingAnim) {
		this.castingAnim = castingAnim;
	}

	public Integer getTargetAnim() {
		return targetAnim;
	}

	public void setTargetAnim(Integer targetAnim) {
		this.targetAnim = targetAnim;
	}

	public Integer getTravelType() {
		return travelType;
	}

	public void setTravelType(Integer travelType) {
		this.travelType = travelType;
	}

	public Integer getSpellAffectIndex() {
		return spellAffectIndex;
	}

	public void setSpellAffectIndex(Integer spellAffectIndex) {
		this.spellAffectIndex = spellAffectIndex;
	}

	public Integer getDisallowSit() {
		return disallowSit;
	}

	public void setDisallowSit(Integer disallowSit) {
		this.disallowSit = disallowSit;
	}

	public Integer getDeities0() {
		return deities0;
	}

	public void setDeities0(Integer deities0) {
		this.deities0 = deities0;
	}

	public Integer getDeities1() {
		return deities1;
	}

	public void setDeities1(Integer deities1) {
		this.deities1 = deities1;
	}

	public Integer getDeities2() {
		return deities2;
	}

	public void setDeities2(Integer deities2) {
		this.deities2 = deities2;
	}

	public Integer getDeities3() {
		return deities3;
	}

	public void setDeities3(Integer deities3) {
		this.deities3 = deities3;
	}

	public Integer getDeities4() {
		return deities4;
	}

	public void setDeities4(Integer deities4) {
		this.deities4 = deities4;
	}

	public Integer getDeities5() {
		return deities5;
	}

	public void setDeities5(Integer deities5) {
		this.deities5 = deities5;
	}

	public Integer getDeities6() {
		return deities6;
	}

	public void setDeities6(Integer deities6) {
		this.deities6 = deities6;
	}

	public Integer getDeities7() {
		return deities7;
	}

	public void setDeities7(Integer deities7) {
		this.deities7 = deities7;
	}

	public Integer getDeities8() {
		return deities8;
	}

	public void setDeities8(Integer deities8) {
		this.deities8 = deities8;
	}

	public Integer getDeities9() {
		return deities9;
	}

	public void setDeities9(Integer deities9) {
		this.deities9 = deities9;
	}

	public Integer getDeities10() {
		return deities10;
	}

	public void setDeities10(Integer deities10) {
		this.deities10 = deities10;
	}

	public Integer getDeities11() {
		return deities11;
	}

	public void setDeities11(Integer deities11) {
		this.deities11 = deities11;
	}

	public Integer getDeities12() {
		return deities12;
	}

	public void setDeities12(Integer deities12) {
		this.deities12 = deities12;
	}

	public Integer getDeities13() {
		return deities13;
	}

	public void setDeities13(Integer deities13) {
		this.deities13 = deities13;
	}

	public Integer getDeities14() {
		return deities14;
	}

	public void setDeities14(Integer deities14) {
		this.deities14 = deities14;
	}

	public Integer getDeities15() {
		return deities15;
	}

	public void setDeities15(Integer deities15) {
		this.deities15 = deities15;
	}

	public Integer getDeities16() {
		return deities16;
	}

	public void setDeities16(Integer deities16) {
		this.deities16 = deities16;
	}

	public Integer getField142() {
		return field142;
	}

	public void setField142(Integer field142) {
		this.field142 = field142;
	}

	public Integer getField143() {
		return field143;
	}

	public void setField143(Integer field143) {
		this.field143 = field143;
	}

	public Integer getNewIcon() {
		return newIcon;
	}

	public void setNewIcon(Integer newIcon) {
		this.newIcon = newIcon;
	}

	public Integer getSpellanim() {
		return spellanim;
	}

	public void setSpellanim(Integer spellanim) {
		this.spellanim = spellanim;
	}

	public Integer getUninterruptable() {
		return uninterruptable;
	}

	public void setUninterruptable(Integer uninterruptable) {
		this.uninterruptable = uninterruptable;
	}

	public Integer getResistDiff() {
		return resistDiff;
	}

	public void setResistDiff(Integer resistDiff) {
		this.resistDiff = resistDiff;
	}

	public Integer getDotStackingExempt() {
		return dotStackingExempt;
	}

	public void setDotStackingExempt(Integer dotStackingExempt) {
		this.dotStackingExempt = dotStackingExempt;
	}

	public Integer getDeleteable() {
		return deleteable;
	}

	public void setDeleteable(Integer deleteable) {
		this.deleteable = deleteable;
	}

	public Integer getRecourseLink() {
		return recourseLink;
	}

	public void setRecourseLink(Integer recourseLink) {
		this.recourseLink = recourseLink;
	}

	public Integer getNoPartialResist() {
		return noPartialResist;
	}

	public void setNoPartialResist(Integer noPartialResist) {
		this.noPartialResist = noPartialResist;
	}

	public Integer getField152() {
		return field152;
	}

	public void setField152(Integer field152) {
		this.field152 = field152;
	}

	public Integer getField153() {
		return field153;
	}

	public void setField153(Integer field153) {
		this.field153 = field153;
	}

	public Integer getShortBuffBox() {
		return shortBuffBox;
	}

	public void setShortBuffBox(Integer shortBuffBox) {
		this.shortBuffBox = shortBuffBox;
	}

	public Integer getDescnum() {
		return descnum;
	}

	public void setDescnum(Integer descnum) {
		this.descnum = descnum;
	}

	public Integer getTypedescnum() {
		return typedescnum;
	}

	public void setTypedescnum(Integer typedescnum) {
		this.typedescnum = typedescnum;
	}

	public Integer getEffectdescnum() {
		return effectdescnum;
	}

	public void setEffectdescnum(Integer effectdescnum) {
		this.effectdescnum = effectdescnum;
	}

	public Integer getEffectdescnum2() {
		return effectdescnum2;
	}

	public void setEffectdescnum2(Integer effectdescnum2) {
		this.effectdescnum2 = effectdescnum2;
	}

	public Integer getNpcNoLos() {
		return npcNoLos;
	}

	public void setNpcNoLos(Integer npcNoLos) {
		this.npcNoLos = npcNoLos;
	}

	public Integer getField160() {
		return field160;
	}

	public void setField160(Integer field160) {
		this.field160 = field160;
	}

	public Integer getReflectable() {
		return reflectable;
	}

	public void setReflectable(Integer reflectable) {
		this.reflectable = reflectable;
	}

	public Integer getBonushate() {
		return bonushate;
	}

	public void setBonushate(Integer bonushate) {
		this.bonushate = bonushate;
	}

	public Integer getField163() {
		return field163;
	}

	public void setField163(Integer field163) {
		this.field163 = field163;
	}

	public Integer getField164() {
		return field164;
	}

	public void setField164(Integer field164) {
		this.field164 = field164;
	}

	public Integer getLdonTrap() {
		return ldonTrap;
	}

	public void setLdonTrap(Integer ldonTrap) {
		this.ldonTrap = ldonTrap;
	}

	public Integer getEndurCost() {
		return endurCost;
	}

	public void setEndurCost(Integer endurCost) {
		this.endurCost = endurCost;
	}

	public Integer getEndurTimerIndex() {
		return endurTimerIndex;
	}

	public void setEndurTimerIndex(Integer endurTimerIndex) {
		this.endurTimerIndex = endurTimerIndex;
	}

	public Integer getIsDiscipline() {
		return isDiscipline;
	}

	public void setIsDiscipline(Integer isDiscipline) {
		this.isDiscipline = isDiscipline;
	}

	public Integer getField169() {
		return field169;
	}

	public void setField169(Integer field169) {
		this.field169 = field169;
	}

	public Integer getField170() {
		return field170;
	}

	public void setField170(Integer field170) {
		this.field170 = field170;
	}

	public Integer getField171() {
		return field171;
	}

	public void setField171(Integer field171) {
		this.field171 = field171;
	}

	public Integer getField172() {
		return field172;
	}

	public void setField172(Integer field172) {
		this.field172 = field172;
	}

	public Integer getHateAdded() {
		return hateAdded;
	}

	public void setHateAdded(Integer hateAdded) {
		this.hateAdded = hateAdded;
	}

	public Integer getEndurUpkeep() {
		return endurUpkeep;
	}

	public void setEndurUpkeep(Integer endurUpkeep) {
		this.endurUpkeep = endurUpkeep;
	}

	public Integer getNumhitstype() {
		return numhitstype;
	}

	public void setNumhitstype(Integer numhitstype) {
		this.numhitstype = numhitstype;
	}

	public Integer getNumhits() {
		return numhits;
	}

	public void setNumhits(Integer numhits) {
		this.numhits = numhits;
	}

	public Integer getPvpresistbase() {
		return pvpresistbase;
	}

	public void setPvpresistbase(Integer pvpresistbase) {
		this.pvpresistbase = pvpresistbase;
	}

	public Integer getPvpresistcalc() {
		return pvpresistcalc;
	}

	public void setPvpresistcalc(Integer pvpresistcalc) {
		this.pvpresistcalc = pvpresistcalc;
	}

	public Integer getPvpresistcap() {
		return pvpresistcap;
	}

	public void setPvpresistcap(Integer pvpresistcap) {
		this.pvpresistcap = pvpresistcap;
	}

	public Integer getSpellCategory() {
		return spellCategory;
	}

	public void setSpellCategory(Integer spellCategory) {
		this.spellCategory = spellCategory;
	}

	public Integer getField181() {
		return field181;
	}

	public void setField181(Integer field181) {
		this.field181 = field181;
	}

	public Integer getField182() {
		return field182;
	}

	public void setField182(Integer field182) {
		this.field182 = field182;
	}

	public Integer getField183() {
		return field183;
	}

	public void setField183(Integer field183) {
		this.field183 = field183;
	}

	public Integer getField184() {
		return field184;
	}

	public void setField184(Integer field184) {
		this.field184 = field184;
	}

	public Integer getCanMgb() {
		return canMgb;
	}

	public void setCanMgb(Integer canMgb) {
		this.canMgb = canMgb;
	}

	public Integer getNodispell() {
		return nodispell;
	}

	public void setNodispell(Integer nodispell) {
		this.nodispell = nodispell;
	}

	public Integer getNpcCategory() {
		return npcCategory;
	}

	public void setNpcCategory(Integer npcCategory) {
		this.npcCategory = npcCategory;
	}

	public Integer getNpcUsefulness() {
		return npcUsefulness;
	}

	public void setNpcUsefulness(Integer npcUsefulness) {
		this.npcUsefulness = npcUsefulness;
	}

	public Integer getMinResist() {
		return minResist;
	}

	public void setMinResist(Integer minResist) {
		this.minResist = minResist;
	}

	public Integer getMaxResist() {
		return maxResist;
	}

	public void setMaxResist(Integer maxResist) {
		this.maxResist = maxResist;
	}

	public Integer getViralTargets() {
		return viralTargets;
	}

	public void setViralTargets(Integer viralTargets) {
		this.viralTargets = viralTargets;
	}

	public Integer getViralTimer() {
		return viralTimer;
	}

	public void setViralTimer(Integer viralTimer) {
		this.viralTimer = viralTimer;
	}

	public Integer getNimbuseffect() {
		return nimbuseffect;
	}

	public void setNimbuseffect(Integer nimbuseffect) {
		this.nimbuseffect = nimbuseffect;
	}

	public Integer getConeStartAngle() {
		return coneStartAngle;
	}

	public void setConeStartAngle(Integer coneStartAngle) {
		this.coneStartAngle = coneStartAngle;
	}

	public Integer getConeStopAngle() {
		return coneStopAngle;
	}

	public void setConeStopAngle(Integer coneStopAngle) {
		this.coneStopAngle = coneStopAngle;
	}

	public Integer getSneaking() {
		return sneaking;
	}

	public void setSneaking(Integer sneaking) {
		this.sneaking = sneaking;
	}

	public Integer getNotExtendable() {
		return notExtendable;
	}

	public void setNotExtendable(Integer notExtendable) {
		this.notExtendable = notExtendable;
	}

	public Integer getField198() {
		return field198;
	}

	public void setField198(Integer field198) {
		this.field198 = field198;
	}

	public Integer getField199() {
		return field199;
	}

	public void setField199(Integer field199) {
		this.field199 = field199;
	}

	public Integer getSuspendable() {
		return suspendable;
	}

	public void setSuspendable(Integer suspendable) {
		this.suspendable = suspendable;
	}

	public Integer getViralRange() {
		return viralRange;
	}

	public void setViralRange(Integer viralRange) {
		this.viralRange = viralRange;
	}

	public Integer getSongcap() {
		return songcap;
	}

	public void setSongcap(Integer songcap) {
		this.songcap = songcap;
	}

	public Integer getField203() {
		return field203;
	}

	public void setField203(Integer field203) {
		this.field203 = field203;
	}

	public Integer getField204() {
		return field204;
	}

	public void setField204(Integer field204) {
		this.field204 = field204;
	}

	public Integer getNoBlock() {
		return noBlock;
	}

	public void setNoBlock(Integer noBlock) {
		this.noBlock = noBlock;
	}

	public Integer getField206() {
		return field206;
	}

	public void setField206(Integer field206) {
		this.field206 = field206;
	}

	public Integer getSpellgroup() {
		return spellgroup;
	}

	public void setSpellgroup(Integer spellgroup) {
		this.spellgroup = spellgroup;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getField209() {
		return field209;
	}

	public void setField209(Integer field209) {
		this.field209 = field209;
	}

	public Integer getField210() {
		return field210;
	}

	public void setField210(Integer field210) {
		this.field210 = field210;
	}

	public Integer getCastRestriction() {
		return castRestriction;
	}

	public void setCastRestriction(Integer castRestriction) {
		this.castRestriction = castRestriction;
	}

	public Integer getAllowrest() {
		return allowrest;
	}

	public void setAllowrest(Integer allowrest) {
		this.allowrest = allowrest;
	}

	public Integer getInCombat() {
		return inCombat;
	}

	public void setInCombat(Integer inCombat) {
		this.inCombat = inCombat;
	}

	public Integer getOutofCombat() {
		return outofCombat;
	}

	public void setOutofCombat(Integer outofCombat) {
		this.outofCombat = outofCombat;
	}

	public Integer getField215() {
		return field215;
	}

	public void setField215(Integer field215) {
		this.field215 = field215;
	}

	public Integer getField216() {
		return field216;
	}

	public void setField216(Integer field216) {
		this.field216 = field216;
	}

	public Integer getField217() {
		return field217;
	}

	public void setField217(Integer field217) {
		this.field217 = field217;
	}

	public Integer getAemaxtargets() {
		return aemaxtargets;
	}

	public void setAemaxtargets(Integer aemaxtargets) {
		this.aemaxtargets = aemaxtargets;
	}

	public Integer getMaxtargets() {
		return maxtargets;
	}

	public void setMaxtargets(Integer maxtargets) {
		this.maxtargets = maxtargets;
	}

	public Integer getField220() {
		return field220;
	}

	public void setField220(Integer field220) {
		this.field220 = field220;
	}

	public Integer getField221() {
		return field221;
	}

	public void setField221(Integer field221) {
		this.field221 = field221;
	}

	public Integer getField222() {
		return field222;
	}

	public void setField222(Integer field222) {
		this.field222 = field222;
	}

	public Integer getField223() {
		return field223;
	}

	public void setField223(Integer field223) {
		this.field223 = field223;
	}

	public Integer getPersistdeath() {
		return persistdeath;
	}

	public void setPersistdeath(Integer persistdeath) {
		this.persistdeath = persistdeath;
	}

	public Integer getField225() {
		return field225;
	}

	public void setField225(Integer field225) {
		this.field225 = field225;
	}

	public Integer getField226() {
		return field226;
	}

	public void setField226(Integer field226) {
		this.field226 = field226;
	}

	public Double getMinDist() {
		return minDist;
	}

	public void setMinDist(Double minDist) {
		this.minDist = minDist;
	}

	public Double getMinDistMod() {
		return minDistMod;
	}

	public void setMinDistMod(Double minDistMod) {
		this.minDistMod = minDistMod;
	}

	public Double getMaxDist() {
		return maxDist;
	}

	public void setMaxDist(Double maxDist) {
		this.maxDist = maxDist;
	}

	public Double getMaxDistMod() {
		return maxDistMod;
	}

	public void setMaxDistMod(Double maxDistMod) {
		this.maxDistMod = maxDistMod;
	}

	public Integer getMinRange() {
		return minRange;
	}

	public void setMinRange(Integer minRange) {
		this.minRange = minRange;
	}

	public Integer getField232() {
		return field232;
	}

	public void setField232(Integer field232) {
		this.field232 = field232;
	}

	public Integer getField233() {
		return field233;
	}

	public void setField233(Integer field233) {
		this.field233 = field233;
	}

	public Integer getField234() {
		return field234;
	}

	public void setField234(Integer field234) {
		this.field234 = field234;
	}

	public Integer getField235() {
		return field235;
	}

	public void setField235(Integer field235) {
		this.field235 = field235;
	}

	public Integer getField236() {
		return field236;
	}

	public void setField236(Integer field236) {
		this.field236 = field236;
	}

	@Override
	public List<SoliniaSpellClass> getAllowedClasses() {
		return allowedClasses;
	}

	@Override
	public void setAllowedClasses(List<SoliniaSpellClass> allowedClasses) {
		this.allowedClasses = allowedClasses;
	}

	@Override
	public void sendSpellSettingsToSender(CommandSender sender) {
		// TODO Auto-generated method stub
		sender.sendMessage(ChatColor.RED + "Spell Settings for " + ChatColor.GOLD + getName() + ChatColor.RESET);
		sender.sendMessage("----------------------------");
		sender.sendMessage("- id: " + ChatColor.GOLD + getId() + ChatColor.RESET);
		sender.sendMessage("- name: " + ChatColor.GOLD + getName() + ChatColor.RESET);
	}

	@Override
	public void editSetting(String setting, String value) throws InvalidSpellSettingException, NumberFormatException, CoreStateInitException {
		String name = getName();

		switch (setting.toLowerCase()) {
		case "name":
			if (value.equals(""))
				throw new InvalidSpellSettingException("Name is empty");

			if (value.length() > 30)
				throw new InvalidSpellSettingException("Name is longer than 30 characters");
			setName(value);
			break;
		case "mana":
			if (value.equals(""))
				throw new InvalidSpellSettingException("mana is empty");
			
			int mana = Integer.parseInt(value);
			setMana(mana);
			break;
		case "teleportzone":
			try
			{
				String[] zonedata = value.split(",");
				// Dissasemble the value to ensure it is correct
				String world = zonedata[0];
				double x = Double.parseDouble(zonedata[1]);
				double y = Double.parseDouble(zonedata[2]);
				double z = Double.parseDouble(zonedata[3]);
				
				setTeleportZone(world+","+x+","+y+","+z);
				break;
			} catch (Exception e)
			{
				throw new InvalidSpellSettingException("Teleport zone value must be in format: world,x,y,z");
			}
		default:
			throw new InvalidSpellSettingException(
					"Invalid Spell setting. Valid Options are: name");
		}
	}

	@Override
	public boolean tryApplyOnBlock(Player player, Block clickedBlock) {
		return StateManager.getInstance().addActiveBlockEffect(clickedBlock,this,player);
	}

	@Override
	public boolean tryApplyOnEntity(Player player, LivingEntity targetentity) {
		// Entity was targeted for this spell but is that the final location?
		try {
			switch(Utils.getSpellTargetType(getTargettype()))
			{
				case Self:
					return StateManager.getInstance().getEntityManager().addActiveEntityEffect(player,this,player);
				case TargetOptional:
					return StateManager.getInstance().getEntityManager().addActiveEntityEffect(targetentity,this,player);
				case Target:
					return StateManager.getInstance().getEntityManager().addActiveEntityEffect(targetentity,this,player);
				case AETarget:
					// Get entities around entity and attempt to apply, if any are successful, return true
					boolean success = false;
					// TODO - should the ae range be read from a field of the spell?
					for (Entity e : targetentity.getNearbyEntities(10, 10, 10))
					{
						if (!(e instanceof LivingEntity))
							continue;
						
						boolean loopSuccess = StateManager.getInstance().getEntityManager().addActiveEntityEffect((LivingEntity)e,this,player);
						if (loopSuccess == true)
							success = true;
					}
					return success;
				case AECaster:
					// Get entities around caster and attempt to apply, if any are successful, return true
					boolean successCaster = false;
					// TODO - should the ae range be read from a field of the spell?
					for (Entity e : player.getNearbyEntities(10, 10, 10))
					{
						if (!(e instanceof LivingEntity))
							continue;
						
						boolean loopSuccess = StateManager.getInstance().getEntityManager().addActiveEntityEffect((LivingEntity)e,this,player);
						if (loopSuccess == true)
							successCaster = true;
					}
					return successCaster;
				default:
					return false;
			
			}
		} catch (CoreStateInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public boolean isBuffSpell()
	{
		if (getBuffduration() > 0 || getBuffdurationformula() > 0)
			return true;

		return false;
	}
	
	@Override
	public List<SpellEffectType> getSpellEffectTypes()
	{
		List<SpellEffectType> spellEffects = new ArrayList<SpellEffectType>();
		if (this.getEffectid1() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid1()));
		if (this.getEffectid2() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid2()));
		if (this.getEffectid3() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid3()));
		if (this.getEffectid4() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid4()));
		if (this.getEffectid5() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid5()));
		if (this.getEffectid6() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid6()));
		if (this.getEffectid7() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid7()));
		if (this.getEffectid8() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid8()));
		if (this.getEffectid9() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid9()));
		if (this.getEffectid10() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid10()));
		if (this.getEffectid11() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid11()));
		if (this.getEffectid12() >= 0)
			spellEffects.add(Utils.getSpellEffectType(getEffectid12()));
		
		return spellEffects;
	}
	
	public SpellEffect getSpellEffectByNo(int no)
	{
		int effectid;
		int base;
		int limit;
		switch(no)
		{
			case 1:
				effectid = getEffectid1();
				base = getEffectBaseValue1();
				limit = getEffectLimitValue1();
				break;
			case 2:
				effectid = getEffectid2();
				base = getEffectBaseValue2();
				limit = getEffectLimitValue2();
				break;
			case 3:
				effectid = getEffectid3();
				base = getEffectBaseValue3();
				limit = getEffectLimitValue3();
				break;
			case 4:
				effectid = getEffectid4();
				base = getEffectBaseValue4();
				limit = getEffectLimitValue4();
				break;
			case 5:
				effectid = getEffectid5();
				base = getEffectBaseValue5();
				limit = getEffectLimitValue5();
				break;
			case 6:
				effectid = getEffectid6();
				base = getEffectBaseValue6();
				limit = getEffectLimitValue6();
				break;
			case 7:
				effectid = getEffectid7();
				base = getEffectBaseValue7();
				limit = getEffectLimitValue7();
				break;
			case 8:
				effectid = getEffectid8();
				base = getEffectBaseValue8();
				limit = getEffectLimitValue8();
				break;
			case 9:
				effectid = getEffectid9();
				base = getEffectBaseValue9();
				limit = getEffectLimitValue9();
				break;
			case 10:
				effectid = getEffectid10();
				base = getEffectBaseValue10();
				limit = getEffectLimitValue10();
				break;
			case 11:
				effectid = getEffectid11();
				base = getEffectBaseValue11();
				limit = getEffectLimitValue11();
				break;
			case 12:
				effectid = getEffectid12();
				base = getEffectBaseValue12();
				limit = getEffectLimitValue12();
				break;
			default:
				return null;
			
		}
		
		SpellEffect spellEffect = new SpellEffect();
		spellEffect.setSpellEffectId(effectid);
		spellEffect.setSpellEffectType(Utils.getSpellEffectType(effectid));
		spellEffect.setBase(base);
		spellEffect.setLimit(limit);
		return spellEffect;
	}
	
	@Override
	public List<SpellEffect> getSpellEffects()
	{
		List<SpellEffect> spellEffects = new ArrayList<SpellEffect>();
		
		if (this.getEffectid1() >= 0 && this.getEffectid1() != 254)
			spellEffects.add(getSpellEffectByNo(1));
		if (this.getEffectid2() >= 0 && this.getEffectid2() != 254)
			spellEffects.add(getSpellEffectByNo(2));
		if (this.getEffectid3() >= 0 && this.getEffectid3() != 254)
			spellEffects.add(getSpellEffectByNo(3));
		if (this.getEffectid4() >= 0 && this.getEffectid4() != 254)
			spellEffects.add(getSpellEffectByNo(4));
		if (this.getEffectid5() >= 0 && this.getEffectid5() != 254)
			spellEffects.add(getSpellEffectByNo(5));
		if (this.getEffectid6() >= 0 && this.getEffectid6() != 254)
			spellEffects.add(getSpellEffectByNo(6));
		if (this.getEffectid7() >= 0 && this.getEffectid7() != 254)
			spellEffects.add(getSpellEffectByNo(7));
		if (this.getEffectid8() >= 0 && this.getEffectid8() != 254)
			spellEffects.add(getSpellEffectByNo(8));
		if (this.getEffectid9() >= 0 && this.getEffectid9() != 254)
			spellEffects.add(getSpellEffectByNo(9));
		if (this.getEffectid10() >= 0 && this.getEffectid10() != 254)
			spellEffects.add(getSpellEffectByNo(10));
		if (this.getEffectid11() >= 0 && this.getEffectid11() != 254)
			spellEffects.add(getSpellEffectByNo(11));
		if (this.getEffectid12() >= 0 && this.getEffectid12() != 254)
			spellEffects.add(getSpellEffectByNo(12));
		
		return spellEffects;
	}

	@Override
	public boolean isDamageSpell()
	{
		for(SpellEffect spellEffect : getSpellEffects())
		{
			if ((spellEffect.getSpellEffectType().equals(SpellEffectType.CurrentHPOnce) || spellEffect.getSpellEffectType().equals(SpellEffectType.CurrentHP)) &&
					Utils.getSpellTargetType(getTargettype()) != SpellTargetType.Tap && getBuffduration() < 1 
					// && .base < 0
					)
				return true;
		}
		
		return false;
	}
	
	@Override 
	public SpellEffectType getEffectType1()
	{
		return Utils.getSpellEffectType(this.getEffectid1());
	}
	
	@Override 
	public SpellEffectType getEffectType2()
	{
		return Utils.getSpellEffectType(this.getEffectid2());
	}
	
	@Override 
	public SpellEffectType getEffectType3()
	{
		return Utils.getSpellEffectType(this.getEffectid3());
	}
	
	@Override 
	public SpellEffectType getEffectType4()
	{
		return Utils.getSpellEffectType(this.getEffectid4());
	}
	
	@Override 
	public SpellEffectType getEffectType5()
	{
		return Utils.getSpellEffectType(this.getEffectid5());
	}
	
	@Override 
	public SpellEffectType getEffectType6()
	{
		return Utils.getSpellEffectType(this.getEffectid6());
	}
	
	@Override 
	public SpellEffectType getEffectType7()
	{
		return Utils.getSpellEffectType(this.getEffectid7());
	}
	
	@Override 
	public SpellEffectType getEffectType8()
	{
		return Utils.getSpellEffectType(this.getEffectid8());
	}
	
	@Override 
	public SpellEffectType getEffectType9()
	{
		return Utils.getSpellEffectType(this.getEffectid9());
	}
	
	@Override 
	public SpellEffectType getEffectType10()
	{
		return Utils.getSpellEffectType(this.getEffectid10());
	}
	
	@Override 
	public SpellEffectType getEffectType11()
	{
		return Utils.getSpellEffectType(this.getEffectid11());
	}
	
	@Override 
	public SpellEffectType getEffectType12()
	{
		return Utils.getSpellEffectType(this.getEffectid12());
	}

	@Override
	public boolean isAASpell() {
		if (this.getMana() == 0)
			return true;
		return false;
	}

	public static boolean isValidEffectForEntity(LivingEntity target, LivingEntity source, SoliniaSpell soliniaSpell) {
		for(SpellEffect effect : soliniaSpell.getSpellEffects())
		{
			// Validate spelleffecttype rules
			if (effect.getSpellEffectType().equals(SpellEffectType.CurrentHP) || effect.getSpellEffectType().equals(SpellEffectType.CurrentHP))
			{
				// If the effect is negative standard nuke and on self, cancel out
				if (effect.getBase() < 0 && target.equals(source))
					return false;
			}
			
			if (effect.getSpellEffectType().equals(SpellEffectType.Teleport) || effect.getSpellEffectType().equals(SpellEffectType.Teleport2))
			{
				// If the effect is teleport and the target is not a player then fail
				if (!(target instanceof Player))
					return false;
			}
		}
		
		return true;
	}
}