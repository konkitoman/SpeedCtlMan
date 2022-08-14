package com.konkito.SpeedCtlMan;
import arc.Core;
import arc.input.KeyCode;
import arc.scene.ui.Button;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

public class Gui {
    public static void addButton(Table t, float mult){
        Button button = new Button(Styles.logict);
        button.label(()->{
            if (Core.input.keyDown(KeyCode.altLeft)){
                if (Core.input.keyDown(KeyCode.shiftLeft)){
                    return "[white]-" + mult + "[]";
                }else{
                    return "[white]+" + mult + "[]";
                }
            }else if (Core.input.keyDown(KeyCode.controlLeft)){
                if (Core.input.keyDown(KeyCode.shiftLeft)){
                    return "[white]/" + mult + "[]";
                }else{
                    return "[white]*" + mult + "[]";
                }
            }else{
                return "[white]" + mult + "[]";
            }
        });
        button.clicked(()->{
            if (Core.input.keyDown(KeyCode.altLeft)){
                if (Core.input.keyDown(KeyCode.shiftLeft)){
                    SpeedControl.speed_multiplier -= mult;
                }else{
                    SpeedControl.speed_multiplier += mult;
                }
            }else if (Core.input.keyDown(KeyCode.controlLeft)){
                if (Core.input.keyDown(KeyCode.shiftLeft)){
                     SpeedControl.speed_multiplier /= mult;
                }else{
                     SpeedControl.speed_multiplier *= mult;
                }
            }else{
                SpeedControl.speed_multiplier = mult;
            }

            SpeedControl.SpeedSet pack = new SpeedControl.SpeedSet();
            pack.speed = SpeedControl.speed_multiplier;
            Vars.net.send(pack, true);

        });

        t.add(button).size(40, 40).color(Pal.lancerLaser).pad(1).padLeft(3).padRight(3);
    }
    public Gui(){
        Table gui = new Table();
        gui.bottom().left();

        Label label = new Label("");
        label.update(()->{
            if (SpeedControl.speed_multiplier >= 128){
                label.setText("[red]" + SpeedControl.speed_multiplier);
            }
            else if (SpeedControl.speed_multiplier >= 32){
                label.setText("[orange]" + SpeedControl.speed_multiplier);
            }else{
                label.setText("[white]" + SpeedControl.speed_multiplier);
            }
        });

        gui.table(Styles.black5, t -> {
            t.background(Tex.button);
            t.add(label).padRight(20).padLeft(20);

            addButton(t, 0.1f);
            addButton(t, 1f);
            addButton(t, 2f);
            addButton(t, 4f);
            addButton(t, 8f);
        });

        Vars.ui.hudGroup.addChild(gui);
    }
}
