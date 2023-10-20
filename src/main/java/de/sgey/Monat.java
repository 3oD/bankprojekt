package de.sgey;

public enum Monat {
    JANUAR(31),
    FEBRUAR(29),
    MAERZ(31),
    APRIL(30),
    MAI(31),
    JUNI(30),
    JULI(31),
    AUGUST(31),
    SEPTEMBER(30),
    OKTOBER(31),
    NOVEMBER(30),
    DEZEMBER(31);

    private int maxTage;

    Monat(int maxTage) {
        this.maxTage = maxTage;
    }
}
