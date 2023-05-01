package net.valorhcf.util;

import com.google.common.base.Objects;
import net.minecraft.server.ItemStack;
import net.minecraft.server.WatchableObject;

import java.util.*;

public class WrappedArrayMap implements Map<Integer, WatchableObject> {

    private WatchableObject[] dataValues = new WatchableObject[23];

    @Override
    public int size() {
        return dataValues.length;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < dataValues.length; i++) {
            if (dataValues[i] != null) return false;
        }

        return true;
    }

    public boolean containsKey(int i) {
        return dataValues[i] != null;
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof Integer && dataValues[(Integer) key] != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < dataValues.length; i++) {
            if (Objects.equal(dataValues[i], value)) return true;
        }

        return false;
    }

    public WatchableObject get(int i) {
        return dataValues[i];
    }

    @Override
    public WatchableObject get(Object key) {
        return key instanceof Integer ? dataValues[(Integer) key] : null;
    }

    public void put(int i, WatchableObject watchableObject) {
        dataValues[i] = watchableObject;
    }

    @Override
    public WatchableObject put(Integer key, WatchableObject value) {
        int index = key.intValue();
        WatchableObject old = dataValues[index];
        dataValues[index] = value;
        return old;
    }

    @Override
    public WatchableObject remove(Object key) {
        if (!(key instanceof Integer)) return null;

        int index = ((Integer) key).intValue();
        WatchableObject old = dataValues[index];
        dataValues[index] = null;
        return old;
    }

    @Override
    public void putAll(Map m) {
        for (Object object : m.entrySet()) {
            if (!(object instanceof Entry)) continue;
            Entry entry = (Entry) object;
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (!(key instanceof Integer) || (!(value instanceof WatchableObject))) continue;

            put((Integer) key, (WatchableObject) value);
        }
    }

    @Override
    public void clear() {
        this.dataValues = new WatchableObject[23];
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < dataValues.length; i++) {
            if (dataValues[i] != null) set.add(Integer.valueOf(i));
        }

        return set;
    }

    @Override
    public Collection<WatchableObject> values() {
        Set<WatchableObject> set = new HashSet<WatchableObject>();
        for (int i = 0; i < dataValues.length; i++) {
            if (dataValues[i] != null) set.add(dataValues[i]);
        }

        return set;
    }

    @Override
    public Set<Entry<Integer, WatchableObject>> entrySet() {
        Set<Entry<Integer, WatchableObject>> set = new HashSet<Entry<Integer, WatchableObject>>();
        for (int i = 0; i < dataValues.length; i++) {
            WatchableObject watchableObject = dataValues[i];
            if (watchableObject != null) {
                set.add(new AbstractMap.SimpleEntry<Integer, WatchableObject>(Integer.valueOf(i), watchableObject));
            }
        }

        return set;
    }

    // Clone the WatchableObjects and deep clone ItemStacks if there are any
    public WrappedArrayMap clone() {
        WrappedArrayMap wrappedArrayMap = new WrappedArrayMap();
        for (int i = 0; i < this.dataValues.length; i++) {
            WatchableObject watchableObject = this.dataValues[i];
            if (watchableObject != null) {
                wrappedArrayMap.dataValues[i] = watchableObject.b() instanceof ItemStack ? new WatchableObject(watchableObject.c(), watchableObject.a(), ((ItemStack) watchableObject.b()).cloneItemStack()) : watchableObject.clone();
            }
        }
        return wrappedArrayMap;
    }
}
