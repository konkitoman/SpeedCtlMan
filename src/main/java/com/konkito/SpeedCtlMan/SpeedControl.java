package com.konkito.SpeedCtlMan;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.io.TypeIO;
import mindustry.mod.Mod;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.net.Packet;
import mindustry.net.Packets;

public class SpeedControl extends Mod {
    public static float speed_multiplier = 1.0f;
    public static Gui gui = null;

    public SpeedControl(){
        Net.registerPacket(SpeedSet::new);

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Time.setDeltaProvider(() -> {
                return Core.graphics.getDeltaTime() * speed_multiplier * 60.0f;
            });

            Vars.net.handleClient(Packets.Disconnect.class, p->{
                Log.info("Disconnected!");
                speed_multiplier = 1;
            });

            if (!Vars.headless){
                gui = new Gui();
            }
        });

        Events.on(EventType.ServerLoadEvent.class, e ->{
            Time.setDeltaProvider(() -> {
                return Core.graphics.getDeltaTime() * speed_multiplier * 60.0f;
            });
        });

        Events.on(EventType.PlayerJoin.class, e->{
            if (Vars.net.server()){
                e.player.con.send(new SpeedControl.SpeedSet(), true);
            }
        });

        Events.on(EventType.SectorLaunchEvent.class, e -> {
            Log.info("SectorLaunchEvent");
            speed_multiplier = 1;
            if (Vars.net.server()){
                Vars.net.send(new SpeedControl.SpeedSet(), true);
            }
        });

    }

    public static class SpeedSet extends Packet {
        public float speed;
        public Player player;

        @Override
        public void write(Writes write) {
            if (Vars.net.client()) {
                TypeIO.writeEntity(write, Vars.player);
            }
            write.f(SpeedControl.speed_multiplier);
        }

        @Override
        public void read(Reads reads){
            if (Vars.net.server()){
                player = TypeIO.readEntity(reads);
            }
            speed = reads.f();
        }

        @Override
        public int getPriority(){
            return priorityHigh;
        }

        @Override
        public void handleServer(NetConnection con) {
            Log.info("Player Name: " + player.name);
            Log.info("Speed: " + speed);
            SpeedControl.speed_multiplier = speed;

            for (NetConnection connection : Vars.net.getConnections()){
                if (connection != con){
                    connection.send(new SpeedSet(), true);
                }
            }
        }


        @Override
        public void handleClient() {
            SpeedControl.speed_multiplier = speed;
        }
    }
}


