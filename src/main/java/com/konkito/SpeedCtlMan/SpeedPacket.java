package com.konkito.SpeedCtlMan;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.net.NetConnection;
import mindustry.net.Packet;

public class SpeedPacket extends Packet {
    public float speed = -1.21f;

    public SpeedPacket() {
    }

    public SpeedPacket(float speed) {
        this.speed = speed;
    }

    @Override
    public void write(Writes write) {
        if (speed == -1.21f) {
            write.f(SpeedControl.speed_multiplier);
        } else {
            write.f(speed);
        }
    }

    @Override
    public void read(Reads reads) {
        speed = reads.f();
    }

    @Override
    public int getPriority() {
        return priorityHigh;
    }

    @Override
    public void handleServer(NetConnection con) {
        if (SpeedControl.speed_multiplier == speed) return;
        if (!con.player.admin) return;

        Log.info("Player: " + con.player.name);
        Log.info("  Sets speed to: " + speed);

        SpeedControl.speed_multiplier = speed;
        Vars.net.send(new SpeedPacket(), true);
    }


    @Override
    public void handleClient() {
        SpeedControl.speed_multiplier = speed;
    }
}
