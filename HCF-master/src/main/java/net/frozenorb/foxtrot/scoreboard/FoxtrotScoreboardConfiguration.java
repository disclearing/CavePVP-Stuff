package net.frozenorb.foxtrot.scoreboard;

import cc.fyre.proton.scoreboard.config.ScoreboardConfiguration;
import cc.fyre.proton.scoreboard.construct.TitleGetter;
import net.frozenorb.foxtrot.Foxtrot;

public class FoxtrotScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(new TitleGetter(Foxtrot.getInstance().getMapHandler().getScoreboardTitle()));
        configuration.setScoreGetter(new FoxtrotScoreGetter());

        return (configuration);
    }

}