package com.craftmend.treadful.world.interfaces;

public interface HotSwappable {

    void onSwap() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException;

}
