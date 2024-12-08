package net.switchcodeur.autochunky;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;


public class Main implements ModInitializer {
    private MinecraftServer server;


    private void execute(String command) {
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
        ParseResults<ServerCommandSource> parseResults = dispatcher.parse(command, server.getCommandSource());

        try {
            dispatcher.execute(parseResults);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void onServerStarted(MinecraftServer _server) {
        server = _server;
        execute("chunky continue");
    }

    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer _server) {
        if (server.getCurrentPlayerCount() != 0)
            return;

        execute("chunky pause");
    }

    private void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer _server) {
        if (server.getCurrentPlayerCount() != 1)
            return;

        execute("chunky continue");
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayerDisconnect);
    }
}
