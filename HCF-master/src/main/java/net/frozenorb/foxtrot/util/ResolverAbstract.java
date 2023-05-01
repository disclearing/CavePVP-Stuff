package net.frozenorb.foxtrot.util;


import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResolverAbstract<T>
{
    protected final Map<ResolverQuery, T> resolvedObjects;

    public ResolverAbstract() {
        this.resolvedObjects = new ConcurrentHashMap<ResolverQuery, T>();
    }

    protected T resolveSilent(final ResolverQuery... queries) {
        try {
            return this.resolve(queries);
        }
        catch (Exception e) {
            return null;
        }
    }

    protected T resolve(final ResolverQuery... queries) throws ReflectiveOperationException {
        if (queries == null || queries.length <= 0) {
            throw new IllegalArgumentException("Given possibilities are empty");
        }
        final int length = queries.length;
        int i = 0;
        while (i < length) {
            final ResolverQuery query = queries[i];
            if (this.resolvedObjects.containsKey(query)) {
                return this.resolvedObjects.get(query);
            }
            try {
                final T resolved = this.resolveObject(query);
                this.resolvedObjects.put(query, resolved);
                return resolved;
            } catch (ReflectiveOperationException e) {
                ++i;
                continue;
            }
        }
        throw this.notFoundException(Arrays.asList(queries).toString());
    }

    protected abstract T resolveObject(final ResolverQuery p0) throws ReflectiveOperationException;

    protected ReflectiveOperationException notFoundException(final String joinedNames) {
        return new ReflectiveOperationException("Objects could not be resolved: " + joinedNames);
    }
}
