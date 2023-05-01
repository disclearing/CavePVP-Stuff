package net.frozenorb.foxtrot.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResolverQuery
{
    private String name;
    private Class<?>[] types;

    public ResolverQuery(final String name, final Class<?>... types) {
        this.name = name;
        this.types = types;
    }

    public ResolverQuery(final String name) {
        this.name = name;
        this.types = (Class<?>[])new Class[0];
    }

    public ResolverQuery(final Class<?>... types) {
        this.types = types;
    }

    public String getName() {
        return this.name;
    }

    public Class<?>[] getTypes() {
        return this.types;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ResolverQuery that = (ResolverQuery)o;
        if (this.name != null) {
            if (this.name.equals(that.name)) {
                return Arrays.equals(this.types, that.types);
            }
        }
        else if (that.name == null) {
            return Arrays.equals(this.types, that.types);
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    {
        private List<ResolverQuery> queryList;

        private Builder() {
            this.queryList = new ArrayList<ResolverQuery>();
        }

        public Builder with(final String name, final Class<?>[] types) {
            this.queryList.add(new ResolverQuery(name, types));
            return this;
        }

        public Builder with(final String name) {
            this.queryList.add(new ResolverQuery(name));
            return this;
        }

        public Builder with(final Class<?>[] types) {
            this.queryList.add(new ResolverQuery(types));
            return this;
        }

        public ResolverQuery[] build() {
            return this.queryList.toArray(new ResolverQuery[this.queryList.size()]);
        }
    }
}
