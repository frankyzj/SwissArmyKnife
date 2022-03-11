package com.example.swissarmyknife.disignpatterns;

import android.util.SparseArray;

public abstract class ObjectPool<T> {
    private SparseArray<T> freePool;
    private SparseArray<T> lentPool;
    private int maxCap;

    public ObjectPool(int maxCap) {
        this(maxCap / 2, maxCap);
    }

    public ObjectPool(int initCap, int maxCap) {
        initialize(initCap);
        this.maxCap = maxCap;
    }

    protected abstract T create();

    protected void initialize(int initCap) {
        freePool = new SparseArray<>();
        lentPool = new SparseArray<>();
        for (int i = 0; i < initCap; i++) {
            freePool.put(i, create());
        }
    }

    public T acquire() {
        T t = null;
        synchronized (freePool) {
            int freeSize = freePool.size();
            for (int i = 0; i < freeSize; i++) {
                int key = freePool.keyAt(i);
                t = freePool.get(key);
                if (t != null) {
                    this.lentPool.put(key, t);
                    this.freePool.remove(key);
                    return t;
                }
            }
            if (t == null && lentPool.size() + freeSize < maxCap) {
                t = create();
                lentPool.put(lentPool.size() + freeSize, t);
            }
        }
        return t;
    }

    public void release(T t) {
        if (t == null) {
            return;
        }
        int key = lentPool.indexOfValue(t);
        restore(t);
        this.freePool.put(key, t);
        this.lentPool.remove(key);
    }

    /**
     * 恢复
     *
     * @param t
     */
    protected void restore(T t) {

    }
}
