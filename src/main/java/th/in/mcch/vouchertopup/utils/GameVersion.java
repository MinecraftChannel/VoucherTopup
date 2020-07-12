package th.in.mcch.vouchertopup.utils;

public enum GameVersion {
    UNKNOWN(100),
    MC_1_8(1),
    MC_1_9(2),
    MC_1_10(3),
    MC_1_11(4),
    MC_1_12(5),
    MC_1_13(6),
    MC_1_14(7),
    MC_1_15(8),
    MC_1_16(9);

    private int id;

    GameVersion(int id) {
        this.id = id;
    }

    public int getVersionID() {
        return id;
    }
}
