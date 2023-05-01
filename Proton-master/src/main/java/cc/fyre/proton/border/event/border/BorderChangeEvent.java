package cc.fyre.proton.border.event.border;

import lombok.Getter;
import cc.fyre.proton.border.Border;
import cc.fyre.proton.border.runnable.BorderTask;
import cc.fyre.proton.cuboid.Cuboid;

public class BorderChangeEvent extends BorderEvent {

    @Getter private int previousSize;
    @Getter private Cuboid previousBounds;
    @Getter private BorderTask.BorderAction action;

    public BorderChangeEvent(Border border,int previousSize,Cuboid previousBounds,BorderTask.BorderAction action) {
        super(border);
        this.previousSize = previousSize;
        this.previousBounds = previousBounds;
        this.action = action;
    }

}

