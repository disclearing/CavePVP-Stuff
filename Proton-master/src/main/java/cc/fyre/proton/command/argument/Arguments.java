package cc.fyre.proton.command.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class Arguments {

    @Getter private List<String> arguments;
    @Getter private Set<String> flags;

    public boolean hasFlag(String flag) {
        return this.flags.contains(flag.toLowerCase());
    }

    public String join(int from, int to, char delimiter) {

        if (to > this.arguments.size() - 1 || to < 1) {
            to = this.arguments.size() - 1;
        }

        final StringBuilder builder = new StringBuilder();

        for (int i = from; i <= to; ++i) {

            builder.append(this.arguments.get(i));
            if (i != to) {
                builder.append(delimiter);
            }

        }

        return builder.toString();
    }

    public String join(int from, char delimiter) {
        return this.join(from, -1, delimiter);
    }

    public String join(int from) {
        return this.join(from, ' ');
    }

    public String join(char delimiter) {
        return this.join(0, delimiter);
    }

    public String join() {
        return this.join(' ');
    }

}
