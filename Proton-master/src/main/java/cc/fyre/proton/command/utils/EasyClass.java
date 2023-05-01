package cc.fyre.proton.command.utils;

public class EasyClass<T> {

    private Class<T> clazz;
    private T object;

    public EasyClass(T object) {

        if (object != null) {
            this.clazz = (Class<T>)object.getClass();
        }

        this.object = object;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public T get() {
        return this.object;
    }

    public EasyMethod getMethod(String name, Object... parameters) {
        return new EasyMethod(this, name, parameters);
    }

    public <ST> EasyField<ST> getField(String name) {
        return new EasyField(this, name);
    }
}

