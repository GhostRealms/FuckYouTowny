/*
 * Copyright (C) ShadowKingdom - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * Written by Matthew Hogan <matt@matthogan.co.uk>, February 2018
 */
package org.shadowkingdom.fuckyoutowny;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

/**
 * <p>Remove Towny's EULA message</p>
 *
 * @author Matthew Hogan
 */
public class FuckYouTownyPlugin extends JavaPlugin {

    private HashSet<String> blacklist;

    {
        this.blacklist = new HashSet<>();

        this.blacklist.add("[Towny] If you have paid any real-life money for " +
                "these townblocks please understand: the " +
                "creators of Towny do not condone this transaction, " +
                "the server you play on breaks the Minecraft EULA and, " +
                "worse, is selling a part of Towny which your server " +
                "admin did not create."
        );
        this.blacklist.add("[Towny] You should consider changing servers" +
                " and requesting a refund of your money."
        );
    }

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this.onPacketSending());
    }

    private PacketAdapter onPacketSending() {
        return new PacketAdapter(this, PacketType.Play.Server.CHAT)
        {
            @Override public void onPacketSending(PacketEvent event) {
                WrapperPlayServerChat wrapperChat = new WrapperPlayServerChat(event.getPacket());
                BaseComponent[] baseComponents;

                try {
                    baseComponents = (ComponentSerializer
                            .parse(wrapperChat.getMessage().getJson())
                    );

                } catch (NullPointerException exception) {
                    return;
                }

                for (BaseComponent baseComponent : baseComponents) {
                    if (blacklist.contains(baseComponent.toPlainText())) {
                        event.setCancelled(true);
                    }
                }
            }
        };
    }
}