package com.konkito.SpeedCtlMan;

import arc.Core;
import arc.Events;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.net.Net;
import mindustry.net.Packets;

public class SpeedControl extends Mod {
    public static float speed_multiplier = 1.0f;
    public static Gui gui = null;

    public SpeedControl() {
        Net.registerPacket(SpeedPacket::new);

        Events.on(EventType.ClientLoadEvent.class, e -> {
            Time.setDeltaProvider(() -> Core.graphics.getDeltaTime() * speed_multiplier * 60.0f);

            Vars.net.handleClient(Packets.Disconnect.class, p -> speed_multiplier = 1);

            if (!Vars.headless) {
                Vars.ui.planet.shown(() -> setSpeed(1));
                Vars.ui.paused.shown(() -> setSpeed(1));
                Vars.ui.research.shown(() -> setSpeed(1));

                gui = new Gui();
            }
        });

        Events.on(EventType.ServerLoadEvent.class, e -> Time.setDeltaProvider(() -> Core.graphics.getDeltaTime() * speed_multiplier * 60.0f));

        Events.on(EventType.PlayerJoin.class, e -> {
            if (Vars.net.server()) {
                e.player.con.send(new SpeedPacket(), true);
            }
        });
    }

    public static void setSpeed(float speed) {
        if (Vars.net.client()) {
            Vars.net.send(new SpeedPacket(speed), true);
        } else {
            SpeedControl.speed_multiplier = speed;
            Vars.net.send(new SpeedPacket(), true);
        }
    }
}


