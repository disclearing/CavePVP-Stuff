package cc.fyre.proton.scoreboard.construct;

import cc.fyre.proton.util.TimeUtils;

public interface ScoreFunction<T> {

    ScoreFunction<Float> TIME_FANCY = (value) -> {

        if (value >= 60) {
            return (TimeUtils.formatIntoMMSS(value.intValue()));
        } else {
            return (Math.round(10.0D * value) / 10.0D + "s");
        }

    };

    ScoreFunction<Float> TIME_SIMPLE = (value) -> (TimeUtils.formatIntoMMSS(value.intValue()));

    String apply(T value);

}