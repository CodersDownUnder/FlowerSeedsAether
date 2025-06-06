package net.minecraft.commands.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;

public record StringTemplate(List<String> segments, List<String> variables) {
    public static StringTemplate fromString(String p_306229_, int p_305937_) {
        Builder<String> builder = ImmutableList.builder();
        Builder<String> builder1 = ImmutableList.builder();
        int i = p_306229_.length();
        int j = 0;
        int k = p_306229_.indexOf(36);

        while(k != -1) {
            if (k != i - 1 && p_306229_.charAt(k + 1) == '(') {
                builder.add(p_306229_.substring(j, k));
                int l = p_306229_.indexOf(41, k + 1);
                if (l == -1) {
                    throw new IllegalArgumentException("Unterminated macro variable in macro '" + p_306229_ + "' on line " + p_305937_);
                }

                String s = p_306229_.substring(k + 2, l);
                if (!isValidVariableName(s)) {
                    throw new IllegalArgumentException("Invalid macro variable name '" + s + "' on line " + p_305937_);
                }

                builder1.add(s);
                j = l + 1;
                k = p_306229_.indexOf(36, j);
            } else {
                k = p_306229_.indexOf(36, k + 1);
            }
        }

        if (j == 0) {
            throw new IllegalArgumentException("Macro without variables on line " + p_305937_);
        } else {
            if (j != i) {
                builder.add(p_306229_.substring(j));
            }

            return new StringTemplate(builder.build(), builder1.build());
        }
    }

    private static boolean isValidVariableName(String p_306141_) {
        for(int i = 0; i < p_306141_.length(); ++i) {
            char c0 = p_306141_.charAt(i);
            if (!Character.isLetterOrDigit(c0) && c0 != '_') {
                return false;
            }
        }

        return true;
    }

    public String substitute(List<String> p_306329_) {
        StringBuilder stringbuilder = new StringBuilder();

        for(int i = 0; i < this.variables.size(); ++i) {
            stringbuilder.append(this.segments.get(i)).append(p_306329_.get(i));
        }

        if (this.segments.size() > this.variables.size()) {
            stringbuilder.append(this.segments.get(this.segments.size() - 1));
        }

        return stringbuilder.toString();
    }
}
