package cc.fyre.proton.command.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class FlagData implements Data {

    @Getter private List<String> names;
    @Getter private String description;
    @Getter private boolean defaultValue;
    @Getter private int methodIndex;
}