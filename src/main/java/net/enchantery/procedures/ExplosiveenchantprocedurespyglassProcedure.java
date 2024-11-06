package net.enchantery.procedures;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.registries.Registries;

import net.enchantery.EnchanteryMod;

import javax.annotation.Nullable;

@EventBusSubscriber(value = {Dist.CLIENT})
public class ExplosiveenchantprocedurespyglassProcedure {
	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		PacketDistributor.sendToServer(new ExplosiveenchantprocedurespyglassMessage());
		execute(event.getLevel(), event.getEntity());
	}

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public record ExplosiveenchantprocedurespyglassMessage() implements CustomPacketPayload {
		public static final Type<ExplosiveenchantprocedurespyglassMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EnchanteryMod.MODID, "procedure_explosiveenchantprocedurespyglass"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveenchantprocedurespyglassMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ExplosiveenchantprocedurespyglassMessage message) -> {
		}, (RegistryFriendlyByteBuf buffer) -> new ExplosiveenchantprocedurespyglassMessage());

		@Override
		public Type<ExplosiveenchantprocedurespyglassMessage> type() {
			return TYPE;
		}

		public static void handleData(final ExplosiveenchantprocedurespyglassMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.SERVERBOUND) {
				context.enqueueWork(() -> {
					if (!context.player().level().hasChunkAt(context.player().blockPosition()))
						return;
					execute(context.player().level(), context.player());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}

		@SubscribeEvent
		public static void registerMessage(FMLCommonSetupEvent event) {
			EnchanteryMod.addNetworkMessage(ExplosiveenchantprocedurespyglassMessage.TYPE, ExplosiveenchantprocedurespyglassMessage.STREAM_CODEC, ExplosiveenchantprocedurespyglassMessage::handleData);
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		ItemStack ITEMSTACK = ItemStack.EMPTY;
		double LEVEL = 0;
		ITEMSTACK = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
		if (ITEMSTACK.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery")))) != 0
				&& ITEMSTACK.getItem() == Items.SPYGLASS && !(entity instanceof Player _plrCldCheck3 && _plrCldCheck3.getCooldowns().isOnCooldown(ITEMSTACK.getItem()))) {
			LEVEL = ITEMSTACK.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery"))));
			if (world instanceof Level _level && !_level.isClientSide())
				_level.explode(null,
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getX()),
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getY()),
						(entity.level().clip(new ClipContext(entity.getEyePosition(1f), entity.getEyePosition(1f).add(entity.getViewVector(1f).scale(250)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getBlockPos().getZ()),
						(float) (ITEMSTACK.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery")))) + 4),
						Level.ExplosionInteraction.TNT);
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(ITEMSTACK.getItem(), (int) (30 * LEVEL));
			if (ITEMSTACK.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery")))) - 1 > 0) {
				EnchantmentHelper.updateEnchantments(ITEMSTACK, mutableEnchantments -> mutableEnchantments
						.removeIf(enchantment -> enchantment.is(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery"))))));
				ITEMSTACK.enchant(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery"))), (int) (LEVEL - 1));
			} else {
				EnchantmentHelper.updateEnchantments(ITEMSTACK, mutableEnchantments -> mutableEnchantments
						.removeIf(enchantment -> enchantment.is(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("enchantery:creepery"))))));
			}
		}
	}
}
