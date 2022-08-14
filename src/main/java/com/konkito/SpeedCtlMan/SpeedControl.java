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

public class SpeedControl extends Mod {
    public static float speed_multiplier = 1.0f;
    public static Gui gui = null;

    public SpeedControl(){
        Net.registerPacket(SpeedSet::new);

        if (!Vars.headless) {
            Events.on(EventType.ClientLoadEvent.class, e -> {
                Time.setDeltaProvider(() -> {
                    return Core.graphics.getDeltaTime() * speed_multiplier * 60.0f;
                });

                gui = new Gui();
            });
        }
    }

    public static class SpeedSet extends Packet {
        public float speed;
        public Player player;

        @Override
        public void write(Writes write) {
            if (Vars.net.client()) {
                TypeIO.writeEntity(write, Vars.player);
            }
            write.f(speed);
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
        }


        @Override
        public void handleClient() {
            SpeedControl.speed_multiplier = speed;
        }
    }
}


