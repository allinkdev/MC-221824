package com.github.allinkdev.mc221824.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ClientWorld.class)
final class ClientWorldMixin {
    @Shadow
    @Final
    private static Set<Item> BLOCK_MARKER_ITEMS;
    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * @author Allink
     * @reason Fix MC-221824
     */
    // Yes, I know, @Overwrite is bad. But, I can't think of a reason someone would WANT to mixin to getBlockParticle() in the first place, and this is the cleanest & least wasteful way to achieve a Mojang-esque fix for MC-221824.
    @Overwrite
    @Nullable
    private Block getBlockParticle() {
        final ClientPlayerInteractionManager interactionManager = this.client.interactionManager;

        if (interactionManager == null) {
            return null;
        }

        final GameMode gameMode = interactionManager.getCurrentGameMode();

        if (gameMode != GameMode.CREATIVE) {
            return null;
        }

        final ClientPlayerEntity player = this.client.player;

        if (player == null) {
            return null;
        }

        final ItemStack mainHandItemStack = player.getMainHandStack();
        final Item mainHandItem = mainHandItemStack.getItem();

        if (mainHandItem instanceof final BlockItem blockItem && BLOCK_MARKER_ITEMS.contains(blockItem)) {
            return blockItem.getBlock();
        }

        final ItemStack offHandItemStack = player.getOffHandStack();
        final Item offHandItem = offHandItemStack.getItem();

        if (offHandItem instanceof final BlockItem blockItem && BLOCK_MARKER_ITEMS.contains(blockItem)) {
            return blockItem.getBlock();
        }

        return null;
    }
}
