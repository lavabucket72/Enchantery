package net.enchantery.procedures;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;

import net.enchantery.init.EnchanteryModEnchantments;
import net.enchantery.EnchanteryMod;

import javax.annotation.Nullable;

import java.util.function.Supplier;
import java.util.Map;

@Mod.EventBusSubscriber(value = {Dist.CLIENT})
public class ExplosionEnchantProcedureSpyglassProcedure {
	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		EnchanteryMod.PACKET_HANDLER.sendToServer(new ExplosionEnchantProcedureSpyglassMessage());
		execute(event.getLevel(), event.getEntity());
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ExplosionEnchantProcedureSpyglassMessage {
		public ExplosionEnchantProcedureSpyglassMessage() {
		}

		public ExplosionEnchantProcedureSpyglassMessage(FriendlyByteBuf buffer) {
		}

		public static void buffer(ExplosionEnchantProcedureSpyglassMessage message, FriendlyByteBuf buffer) {
		}

		public static void handler(ExplosionEnchantProcedureSpyglassMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getSender().level().hasChunkAt(context.getSender().blockPosition()))
					return;
				execute(context.getSender().level(), context.getSender());
			});
			context.setPacketHandled(true);
		}

		@SubscribeEvent
		public static void registerMessage(FMLCommonSetupEvent event) {
			EnchanteryMod.addNetworkMessage(ExplosionEnchantProcedureSpyglassMessage.class, ExplosionEnchantProcedureSpyglassMessage::buffer, ExplosionEnchantProcedureSpyglassMessage::new, ExplosionEnchantProcedureSpyglassMessage::handler);
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		ItemStack item = ItemStack.EMPTY;
		double level = 0;
		item = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
		if (Items.SPYGLASS == item.getItem() && EnchantmentHelper.getItemEnchantmentLevel(EnchanteryModEnchantments.CREEPERY.get(), item) != 0) {
			level = item.getEnchantmentLevel(EnchanteryModEnchantments.CREEPERY.get());
			if (world instanceof Level _level && !_level.isClientSide())
				_level.explode(null,
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()),
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getY()),
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()),
						(float) (level + 4), Level.ExplosionInteraction.TNT);
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(item.getItem(), (int) (30 * level));
			if (level - 1 > 0) {
				{
					Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(item);
					if (_enchantments.containsKey(EnchanteryModEnchantments.CREEPERY.get())) {
						_enchantments.remove(EnchanteryModEnchantments.CREEPERY.get());
						EnchantmentHelper.setEnchantments(_enchantments, item);
					}
				}
				item.enchant(EnchanteryModEnchantments.CREEPERY.get(), (int) (level - 1));
			} else {
				{
					Map<Enchantment, Integer> _enchantments = EnchantmentHelper.getEnchantments(item);
					if (_enchantments.containsKey(EnchanteryModEnchantments.CREEPERY.get())) {
						_enchantments.remove(EnchanteryModEnchantments.CREEPERY.get());
						EnchantmentHelper.setEnchantments(_enchantments, item);
					}
				}
			}
		}
	}
}
