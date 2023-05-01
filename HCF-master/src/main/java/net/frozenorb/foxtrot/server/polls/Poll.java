package net.frozenorb.foxtrot.server.polls;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Poll {

    @Getter
    private String question;
    @Getter
    private List<String> options;
    @Getter
    private Map<UUID, String> votes = new HashMap<>();
    @Getter
    @Setter
    private boolean active;

    public Poll(String question, List<String> options) {
        this.question = question;
        this.options = options;
        this.active = true;

        Foxtrot.getInstance().getPollHandler().getPolls().add(this);
    }

}
