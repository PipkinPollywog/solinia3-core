package com.solinia.solinia.Models;

import org.bukkit.entity.LivingEntity;

import com.solinia.solinia.Interfaces.ISoliniaSpell;

public class ActiveSpellEffect extends SpellEffect {
	private int calculatedValue;
	
	public ActiveSpellEffect(ISoliniaSpell spell, SpellEffect spellEffect, LivingEntity sourceEntity, LivingEntity targetEntity, int sourceLevel, int ticksleft) {
		setBase(spellEffect.getBase());
		setFormula(spellEffect.getFormula());
		setLimit(spellEffect.getLimit());
		setMax(spellEffect.getMax());
		setSpellEffectId(spellEffect.getSpellEffectId());
		setSpellEffectNo(spellEffect.getSpellEffectNo());
		setSpellEffectType(spellEffect.getSpellEffectType());
		setCalculatedValue(spell.calcSpellEffectValueFormula(spellEffect, sourceEntity, targetEntity, sourceLevel, ticksleft));
	}

	public int getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(int calculatedValue) {
		this.calculatedValue = calculatedValue;
	}
}