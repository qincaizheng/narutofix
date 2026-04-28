package com.qdd.narutofix.awakening;

public final class BloodlineAdvancementIds {
    public static final String GET_INDRA_CHAKRA = "narutofix:get_indrachakra";
    public static final String GET_ASURA_CHAKRA = "narutofix:get_asurachakra";
    public static final String INDRA_REINCARNATION = "narutofix:indra_reincarnation";
    public static final String ASURA_REINCARNATION = "narutofix:asura_reincarnation";

    private BloodlineAdvancementIds() {
    }

    public static String forBloodline(Bloodline bloodline, boolean reincarnation) {
        if (bloodline == Bloodline.INDRA) {
            return reincarnation ? INDRA_REINCARNATION : GET_INDRA_CHAKRA;
        }
        return reincarnation ? ASURA_REINCARNATION : GET_ASURA_CHAKRA;
    }
}