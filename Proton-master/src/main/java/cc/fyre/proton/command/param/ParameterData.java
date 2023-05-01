package cc.fyre.proton.command.param;

import cc.fyre.proton.command.flag.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
public final class ParameterData implements Data {

    @Getter private String name;
    @Getter private String defaultValue;
    @Getter private Class<?> type;
    @Getter private boolean wildcard;
    @Getter private int methodIndex;
    @Getter private Set<String> tabCompleteFlags;
    @Getter private Class<? extends ParameterType> parameterType;

}