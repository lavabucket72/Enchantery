
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.enchantery.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import net.enchantery.enchantment.LifespreadEnchantment;
import net.enchantery.enchantment.CreeperyEnchantment;
import net.enchantery.EnchanteryMod;

public class EnchanteryModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, EnchanteryMod.MODID);
	public static final RegistryObject<Enchantment> CREEPERY = REGISTRY.register("creepery", () -> new CreeperyEnchantment());
	public static final RegistryObject<Enchantment> LIFESPREAD = REGISTRY.register("lifespread", () -> new LifespreadEnchantment());
}
