/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import java.io.File;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * PlayerEvent is fired whenever an event involving Living entities occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerEvent extends LivingEvent
{
    private final Player entityPlayer;
    public PlayerEvent(Player player)
    {
        super(player);
        entityPlayer = player;
    }

    /**
     * @return Player
     */
    public Player getPlayer() { return entityPlayer; }
    /**
     * HarvestCheck is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#hasCorrectToolForDrops(BlockState)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doPlayerHarvestCheck(Player, BlockState, boolean)}.<br>
     * <br>
     * {@link #state} contains the {@link BlockState} that is being checked for harvesting. <br>
     * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class HarvestCheck extends PlayerEvent
    {
        private final BlockState state;
        private boolean success;

        public HarvestCheck(Player player, BlockState state, boolean success)
        {
            super(player);
            this.state = state;
            this.success = success;
        }

        public BlockState getTargetBlock() { return this.state; }
        public boolean canHarvest() { return this.success; }
        public void setCanHarvest(boolean success){ this.success = success; }
    }

    /**
     * BreakSpeed is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link Player#getDigSpeed(BlockState, BlockPos)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getBreakSpeed(Player, BlockState, float, BlockPos)}.<br>
     * <br>
     * {@link #state} contains the block being broken. <br>
     * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
     * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
     * {@link #pos} contains the coordinates at which this event is occurring. Y value -1 means location is unknown.<br>
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If it is canceled, the player is unable to break the block.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        private final BlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final BlockPos pos; // Y position of -1 notes unknown location

        public BreakSpeed(Player player, BlockState state, float original, BlockPos pos)
        {
            super(player);
            this.state = state;
            this.originalSpeed = original;
            this.setNewSpeed(original);
            this.pos = pos != null ? pos : new BlockPos(0, -1, 0);
        }

        public BlockState getState() { return state; }
        public float getOriginalSpeed() { return originalSpeed; }
        public float getNewSpeed() { return newSpeed; }
        public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
        public BlockPos getPos() { return pos; }
    }

    /**
     * NameFormat is fired when a player's display name is retrieved.<br>
     * This event is fired whenever a player's name is retrieved in
     * {@link Player#getDisplayName()} or {@link Player#refreshDisplayName()}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerDisplayName(Player, Component)}.<br>
     * <br>
     * {@link #username} contains the username of the player.
     * {@link #displayname} contains the display name of the player.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class NameFormat extends PlayerEvent
    {
        private final Component username;
        private Component displayname;

        public NameFormat(Player player, Component username) 
        {
            super(player);
            this.username = username;
            this.setDisplayname(username);
        }

        public Component getUsername()
        {
            return username;
        }

        public Component getDisplayname()
        {
            return displayname;
        }

        public void setDisplayname(Component displayname)
        {
            this.displayname = displayname;
        }
    }

    /**
     * TabListNameFormat is fired when a player's display name for the tablist is retrieved.<br>
     * This event is fired whenever a player's display name for the tablist is retrieved in
     * {@link ServerPlayer#getTabListDisplayName()} or {@link ServerPlayer#refreshTabListName()}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerTabListDisplayName(Player)}.<br>
     * <br>
     * {@link #getDisplayName()} contains the display name of the player or null if the client should determine the display name itself.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class TabListNameFormat extends PlayerEvent
    {
        @Nullable
        private Component displayName;

        public TabListNameFormat(Player player)
        {
            super(player);
        }
        
        @Nullable
        public Component getDisplayName()
        {
            return displayName;
        }

        public void setDisplayName(@Nullable Component displayName)
        {
            this.displayName = displayName;
        }
    }

    /**
     * JoinMessageFormat is fired when a player's join message is retrieved.<br>
     * This event is fired whenever a player's join message is retrieved, when a player tries to join
     * <br>
     * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#getJoinMessage(Player, String)}.<br>
     * <br>
     * {@link #getJoinMessage()} contains the join message of the player or null if the default join message should be displayed
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class JoinMessageFormat extends PlayerEvent
    {
        @Nullable
        private MutableComponent joinMessage;

        public JoinMessageFormat(Player player)
        {
            super(player);
        }

        @Nullable
        public MutableComponent getJoinMessage()
        {
            return joinMessage;
        }

        public void setJoinMessage(@Nullable MutableComponent joinMessage)
        {
            this.joinMessage = joinMessage;
        }
    }

    /**
     * LeaveMessageFormat is fired when a player's leave message is retrieved.<br>
     * This event is fired whenever a player's leave message is retrieved, when a player tries to leave
     * <br>
     * This event is fired via the {@link net.minecraftforge.common.ForgeHooks#getLeaveMessage(Player, MinecraftServer)}.<br>
     * <br>
     * {@link #getLeaveMessage()} contains the leave message of the player or null if the default leave message should be displayed
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class LeaveMessageFormat extends PlayerEvent
    {
        @Nullable
        private MutableComponent leaveMessage;

        public LeaveMessageFormat(Player player)
        {
            super(player);
        }

        @Nullable
        public MutableComponent getLeaveMessage()
        {
            return leaveMessage;
        }

        public void setLeaveMessage(@Nullable MutableComponent leaveMessage)
        {
            this.leaveMessage = leaveMessage;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the impl sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     */
    public static class Clone extends PlayerEvent
    {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player _new, Player oldPlayer, boolean wasDeath)
        {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }

        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public Player getOriginal()
        {
            return original;
        }

        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public boolean isWasDeath()
        {
            return wasDeath;
        }
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     */
    public static class StartTracking extends PlayerEvent {

        private final Entity target;

        public StartTracking(Player player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity now being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     */
    public static class StopTracking extends PlayerEvent {

        private final Entity target;

        public StopTracking(Player player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity no longer being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    public static class LoadFromFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public LoadFromFile(Player player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
        }
    }
    /**
     * The player is being saved to the world store. Note that the
     * player may be in the process of logging out or otherwise departing
     * from the world. Don't assume it's association with the world.
     * This allows mods to load an additional file from the players directory
     * containing additional mod related player data.
     * <br>
     * Use this event to save the additional mod related player data to the world.
     *
     * <br>
     * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
     * corrupt the world state.
     */
    public static class SaveToFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public SaveToFile(Player player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
        }
    }

    public static class ItemPickupEvent extends PlayerEvent {
        /**
         * Original EntityItem with current remaining stack size
         */
        private final ItemEntity originalEntity;
        /**
         * Clone item stack, containing the item and amount picked up
         */
        private final ItemStack stack;
        public ItemPickupEvent(Player player, ItemEntity entPickedUp, ItemStack stack)
        {
            super(player);
            this.originalEntity = entPickedUp;
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemEntity getOriginalEntity() {
            return originalEntity;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack crafting;
        private final Container craftMatrix;
        public ItemCraftedEvent(Player player, @Nonnull ItemStack crafting, Container craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }

        @Nonnull
        public ItemStack getCrafting()
        {
            return this.crafting;
        }

        public Container getInventory()
        {
            return this.craftMatrix;
        }
    }

    public static class ItemSmeltedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack smelting;
        public ItemSmeltedEvent(Player player, @Nonnull ItemStack crafting)
        {
            super(player);
            this.smelting = crafting;
        }

        @Nonnull
        public ItemStack getSmelting()
        {
            return this.smelting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player)
        {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(Player player)
        {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        private final boolean endConquered;

        public PlayerRespawnEvent(Player player, boolean endConquered)
        {
            super(player);
            this.endConquered = endConquered;
        }

        /**
         * Did this respawn event come from the player conquering the end?
         * @return if this respawn was because the player conquered the end
         */
        public boolean isEndConquered()
        {
            return this.endConquered;
        }


    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        private final ResourceKey<Level> fromDim;
        private final ResourceKey<Level> toDim;
        public PlayerChangedDimensionEvent(Player player, ResourceKey<Level> fromDim, ResourceKey<Level> toDim)
        {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }

        public ResourceKey<Level> getFrom()
        {
            return this.fromDim;
        }

        public ResourceKey<Level> getTo()
        {
            return this.toDim;
        }
    }

    /**
     * Fired when the game type of a server player is changed to a different value than what it was previously. Eg Creative to Survival, not Survival to Survival.
     * If the event is cancelled the game mode of the player is not changed and the value of <code>newGameMode</code> is ignored.
     */
    @Cancelable
    public static class PlayerChangeGameModeEvent extends PlayerEvent
    {
        private final GameType currentGameMode;
        private GameType newGameMode;

        public PlayerChangeGameModeEvent(Player player, GameType currentGameMode, GameType newGameMode)
        {
            super(player);
            this.currentGameMode = currentGameMode;
            this.newGameMode = newGameMode;
        }

        public GameType getCurrentGameMode()
        {
            return currentGameMode;
        }

        public GameType getNewGameMode()
        {
            return newGameMode;
        }

        /**
         * Sets the game mode the player will be changed to if this event is not cancelled.
         */
        public void setNewGameMode(GameType newGameMode)
        {
            this.newGameMode = newGameMode;
        }
    }
}
