package com.ivygames.morskoiboi;

public enum Rank {
    SEAMAN(R.drawable.rank_seaman), PETTY_OFFICER_THIRD_CLASS(R.drawable.rank_po3), PETTY_OFFICER_SECOND_CLASS(R.drawable.rank_po2), PETTY_OFFICER_FIRST_CLASS(
            R.drawable.rank_po1), CHIEF_PETTY_OFFICER(R.drawable.rank_cpo), CHIEF_SHIP_PETTY_OFFICER(R.drawable.rank_cspo), WARRANT_OFFICER(R.drawable.rank_wo), CHIEF_WARRANT_OFFICER(
            R.drawable.rank_cwo), ENSIGN(R.drawable.rank_o1), LIEUTENANT_JUNIOR(R.drawable.rank_o2), LIEUTENANT(R.drawable.rank_o3), LIEUTENANT_COMMANDER(
            R.drawable.rank_o4), COMMANDER(R.drawable.rank_o5), CAPTAIN(R.drawable.rank_o6), REAR_ADMIRAL_LOWER_HALF(R.drawable.rank_o7), REAR_ADMIRAL(
            R.drawable.rank_o8), VICE_ADMIRAL(R.drawable.rank_o9), ADMIRAL(R.drawable.rank_o10), FLEET_ADMIRAL(R.drawable.rank_o11);

    public static final int PO3 = 50000; //
    public static final int PO2 = 150000; // <!-- 100k -->
    public static final int PO1 = 250000; // <!-- 100k -->
    public static final int CPO = 350000; // <!-- 100k -->
    public static final int CSPO = 450000; // <!-- 100k -->
    public static final int WO = 600000; // <!-- 150k -->
    public static final int CWO = 750000; // <!-- 150k -->

    public static final int O1 = 1000000; // <!-- 250k -->
    public static final int O2 = 1250000; // <!-- 250k -->
    public static final int O3 = 1500000; // <!-- 250k -->
    public static final int O4 = 1750000; // <!-- 250k -->
    public static final int O5 = 2000000; // <!-- 250k -->
    public static final int O6 = 2250000; // <!-- 250k -->
    public static final int O7 = 2600000; // <!-- 350k -->
    public static final int O8 = 3000000; // <!-- 400k -->
    public static final int O9 = 3500000; // <!-- 500k -->
    public static final int O10 = 4000000; // <!-- 500k -->
    public static final int O11 = 4500000; // <!-- 500k -->

    private final int mBitmap;
    private final int mSmallBitmap;
    private int mName;
    private int mScore;

    public static Rank getBestRankForScore(int score) {
        Rank rank = SEAMAN;
        if (score >= FLEET_ADMIRAL.getScore()) {
            rank = FLEET_ADMIRAL;
        } else if (score >= ADMIRAL.getScore()) {
            rank = ADMIRAL;
        } else if (score >= VICE_ADMIRAL.getScore()) {
            rank = VICE_ADMIRAL;
        } else if (score >= REAR_ADMIRAL.getScore()) {
            rank = REAR_ADMIRAL;
        } else if (score >= REAR_ADMIRAL_LOWER_HALF.getScore()) {
            rank = REAR_ADMIRAL_LOWER_HALF;
        } else if (score >= CAPTAIN.getScore()) {
            rank = CAPTAIN;
        } else if (score >= COMMANDER.getScore()) {
            rank = COMMANDER;
        } else if (score >= LIEUTENANT_COMMANDER.getScore()) {
            rank = LIEUTENANT_COMMANDER;
        } else if (score >= LIEUTENANT.getScore()) {
            rank = LIEUTENANT;
        } else if (score >= LIEUTENANT_JUNIOR.getScore()) {
            rank = LIEUTENANT_JUNIOR;
        } else if (score >= ENSIGN.getScore()) {
            rank = ENSIGN;
        } else if (score >= CHIEF_WARRANT_OFFICER.getScore()) {
            rank = CHIEF_WARRANT_OFFICER;
        } else if (score >= WARRANT_OFFICER.getScore()) {
            rank = WARRANT_OFFICER;
        } else if (score >= CHIEF_SHIP_PETTY_OFFICER.getScore()) {
            rank = CHIEF_SHIP_PETTY_OFFICER;
        } else if (score >= CHIEF_PETTY_OFFICER.getScore()) {
            rank = CHIEF_PETTY_OFFICER;
        } else if (score >= PETTY_OFFICER_FIRST_CLASS.getScore()) {
            rank = PETTY_OFFICER_FIRST_CLASS;
        } else if (score >= PETTY_OFFICER_SECOND_CLASS.getScore()) {
            rank = PETTY_OFFICER_SECOND_CLASS;
        } else if (score >= PETTY_OFFICER_THIRD_CLASS.getScore()) {
            rank = PETTY_OFFICER_THIRD_CLASS;
        }

        return rank;
    }

    Rank(int bitmapRes) {
        mBitmap = bitmapRes;

        switch (bitmapRes) {
            case R.drawable.rank_seaman:
                mName = R.string.rank_seaman;
                mSmallBitmap = R.drawable.rank_seaman_s;
                mScore = 0;
                break;
            case R.drawable.rank_po3:
                mName = R.string.rank_po3;
                mSmallBitmap = R.drawable.rank_po3_s;
                mScore = PO3;
                break;
            case R.drawable.rank_po2:
                mName = R.string.rank_po2;
                mSmallBitmap = R.drawable.rank_po2_s;
                mScore = PO2;
                break;
            case R.drawable.rank_po1:
                mName = R.string.rank_po1;
                mSmallBitmap = R.drawable.rank_po1_s;
                mScore = PO1;
                break;
            case R.drawable.rank_cpo:
                mName = R.string.rank_cpo;
                mSmallBitmap = R.drawable.rank_cpo_s;
                mScore = CPO;
                break;
            case R.drawable.rank_cspo:
                mName = R.string.rank_cspo;
                mSmallBitmap = R.drawable.rank_cspo_s;
                mScore = CSPO;
                break;
            case R.drawable.rank_wo:
                mName = R.string.rank_wo;
                mSmallBitmap = R.drawable.rank_wo_s;
                mScore = WO;
                break;
            case R.drawable.rank_cwo:
                mName = R.string.rank_cwo;
                mSmallBitmap = R.drawable.rank_cwo_s;
                mScore = CWO;
                break;
            case R.drawable.rank_o1:
                mName = R.string.rank_o1;
                mSmallBitmap = R.drawable.rank_o1_s;
                mScore = O1;
                break;
            case R.drawable.rank_o2:
                mName = R.string.rank_o2;
                mSmallBitmap = R.drawable.rank_o2_s;
                mScore = O2;
                break;
            case R.drawable.rank_o3:
                mName = R.string.rank_o3;
                mSmallBitmap = R.drawable.rank_o3_s;
                mScore = O3;
                break;
            case R.drawable.rank_o4:
                mName = R.string.rank_o4;
                mSmallBitmap = R.drawable.rank_o4_s;
                mScore = O4;
                break;
            case R.drawable.rank_o5:
                mName = R.string.rank_o5;
                mSmallBitmap = R.drawable.rank_o5_s;
                mScore = O5;
                break;
            case R.drawable.rank_o6:
                mName = R.string.rank_o6;
                mSmallBitmap = R.drawable.rank_o6_s;
                mScore = O6;
                break;
            case R.drawable.rank_o7:
                mName = R.string.rank_o7;
                mSmallBitmap = R.drawable.rank_o7_s;
                mScore = O7;
                break;
            case R.drawable.rank_o8:
                mName = R.string.rank_o8;
                mSmallBitmap = R.drawable.rank_o8_s;
                mScore = O8;
                break;
            case R.drawable.rank_o9:
                mName = R.string.rank_o9;
                mSmallBitmap = R.drawable.rank_o9_s;
                mScore = O9;
                break;
            case R.drawable.rank_o10:
                mName = R.string.rank_o10;
                mSmallBitmap = R.drawable.rank_o10_s;
                mScore = O10;
                break;
            case R.drawable.rank_o11:
                mName = R.string.rank_o11;
                mSmallBitmap = R.drawable.rank_o11_s;
                mScore = O11;
                break;

            default:
                throw new RuntimeException("impossible drawable=" + bitmapRes);
        }
    }

    public int getBitmapRes() {
        return mBitmap;
    }

    public int getSmallBitmapRes() {
        return mSmallBitmap;
    }

    public int getNameRes() {
        return mName;
    }

    public int getScore() {
        return mScore;
    }

}
