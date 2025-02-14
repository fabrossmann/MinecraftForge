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

package net.minecraftforge.debug.chat;

import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("client_join_leave_message_test")
@Mod.EventBusSubscriber
public class JoinLeaveMessageTest
{
    @SubscribeEvent
    public static void onPlayerJoinChat(PlayerEvent.JoinMessageFormat event)
    {
        event.setJoinMessage(new TextComponent("Hello " + event.getPlayer().getDisplayName()));
    }

    @SubscribeEvent
    public static void onPlayerLeaveChat(PlayerEvent.LeaveMessageFormat event)
    {
        event.setLeaveMessage(new TextComponent("Goodbye " + event.getPlayer().getDisplayName()));
    }
}
