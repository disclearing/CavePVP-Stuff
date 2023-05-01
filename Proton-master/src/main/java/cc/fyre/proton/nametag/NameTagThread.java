package cc.fyre.proton.nametag;

import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagUpdate;
import lombok.Getter;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NameTagThread extends Thread {

    @Getter private static Map<NameTagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    NameTagThread() {
        super("Proton - NameTag Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {

            final Iterator<NameTagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

            while (pendingUpdatesIterator.hasNext()) {

                final NameTagUpdate pendingUpdate = pendingUpdatesIterator.next();

                try {
                    Proton.getInstance().getNameTagHandler().applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(Proton.getInstance().getNameTagHandler().getUpdateInterval() * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}