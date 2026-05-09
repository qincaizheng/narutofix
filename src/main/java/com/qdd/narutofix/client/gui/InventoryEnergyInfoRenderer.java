package com.qdd.narutofix.client.gui;

import com.qdd.narutofix.NarutoFix;
import com.qdd.narutofix.cap.awakening.IPlayerAwakeningData;
import com.qdd.narutofix.cap.awakening.PlayerAwakeningDataProvider;
import com.qdd.narutofix.util.EnergyRecoveryCalculator;
import com.qdd.narutofix.util.EnergyRecoverySnapshot;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

@SideOnly(Side.CLIENT)
public final class InventoryEnergyInfoRenderer {
    private static final int SCREEN_MARGIN = 4;
    private static final int GUI_MARGIN = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int COLUMN_GAP = 10;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR = 0x77000000;
    private static final double STATIONARY_DISTANCE_SQ = 1.0E-4D;

    private static boolean positionInitialized;
    private static int trackedEntityId;
    private static int trackedDimension;
    private static int lastPlayerTick;
    private static int stationaryTicks;
    private static double lastX;
    private static double lastY;
    private static double lastZ;

    private InventoryEnergyInfoRenderer() {
    }

    public static void render(FontRenderer fontRenderer, EntityPlayer player, int guiLeft, int guiTop,
                              int xSize, int ySize, int screenWidth, int screenHeight) {
        if (fontRenderer == null || player == null || player.isSpectator()) {
            return;
        }

        IPlayerAwakeningData awakening = PlayerAwakeningDataProvider.get(player);
        int stationary = updateStationaryTicks(player);
        EnergyRecoverySnapshot snapshot = EnergyRecoveryCalculator.calculate(player, awakening, stationary);
        TextVariants variants = buildTextVariants(snapshot, awakening);
        DrawPlan plan = choosePlan(fontRenderer, variants, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        Gui.drawRect(plan.x - 3, plan.y - 2, plan.x + plan.width + 3, plan.y + plan.height, BACKGROUND_COLOR);
        if (plan.twoColumns) {
            drawLine(fontRenderer, plan.lines[0], plan.x, plan.y);
            drawLine(fontRenderer, plan.lines[1], plan.x + plan.firstColumnWidth + COLUMN_GAP, plan.y);
            drawLine(fontRenderer, plan.lines[2], plan.x, plan.y + LINE_HEIGHT);
            drawLine(fontRenderer, plan.lines[3], plan.x + plan.firstColumnWidth + COLUMN_GAP, plan.y + LINE_HEIGHT);
        } else {
            for (int i = 0; i < plan.lines.length; i++) {
                drawLine(fontRenderer, plan.lines[i], plan.x, plan.y + i * LINE_HEIGHT);
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawLine(FontRenderer fontRenderer, String text, int x, int y) {
        fontRenderer.drawStringWithShadow(text, (float) x, (float) y, TEXT_COLOR);
    }

    private static TextVariants buildTextVariants(EnergyRecoverySnapshot snapshot, IPlayerAwakeningData awakening) {
        String bloodline = getBloodlineText(awakening);
        EnergyRecoverySnapshot.Entry body = snapshot.getBody();
        EnergyRecoverySnapshot.Entry soul = snapshot.getSoul();
        EnergyRecoverySnapshot.Entry chakra = snapshot.getChakra();

        String bodyLabel = I18n.format(NarutoFix.MODID + ".inventory.energy.body");
        String soulLabel = I18n.format(NarutoFix.MODID + ".inventory.energy.soul");
        String chakraLabel = I18n.format(NarutoFix.MODID + ".inventory.energy.chakra");
        String bodyShort = I18n.format(NarutoFix.MODID + ".inventory.energy.body.short");
        String soulShort = I18n.format(NarutoFix.MODID + ".inventory.energy.soul.short");
        String chakraShort = I18n.format(NarutoFix.MODID + ".inventory.energy.chakra.short");

        String[] full = new String[]{
                I18n.format(NarutoFix.MODID + ".inventory.bloodline.line", bloodline),
                formatFullEnergy(bodyLabel, body),
                formatFullEnergy(soulLabel, soul),
                formatFullEnergy(chakraLabel, chakra)
        };
        String[] compact = new String[]{
                I18n.format(NarutoFix.MODID + ".inventory.bloodline.compact", bloodline),
                formatCompactEnergy(bodyShort, body, 2),
                formatCompactEnergy(soulShort, soul, 2),
                formatCompactEnergy(chakraShort, chakra, 2)
        };
        String[] tiny = new String[]{
                bloodline,
                formatCompactEnergy(bodyShort, body, 1),
                formatCompactEnergy(soulShort, soul, 1),
                formatCompactEnergy(chakraShort, chakra, 1)
        };
        String[] inline = new String[]{
                tiny[0] + "  " + tiny[1] + "  " + tiny[2] + "  " + tiny[3]
        };
        return new TextVariants(full, compact, tiny, inline);
    }

    private static String formatFullEnergy(String label, EnergyRecoverySnapshot.Entry entry) {
        return I18n.format(NarutoFix.MODID + ".inventory.energy.full", label,
                formatAmount(entry.getCurrent()), formatAmount(entry.getMax()), formatRate(entry.getNetPerTick(), 2));
    }

    private static String formatCompactEnergy(String label, EnergyRecoverySnapshot.Entry entry, int rateDecimals) {
        return I18n.format(NarutoFix.MODID + ".inventory.energy.compact", label,
                formatAmount(entry.getCurrent()), formatAmount(entry.getMax()), formatRate(entry.getNetPerTick(), rateDecimals));
    }

    private static String getBloodlineText(IPlayerAwakeningData awakening) {
        if (awakening == null || !awakening.hasAnyBloodline()) {
            return I18n.format(NarutoFix.MODID + ".inventory.bloodline.none");
        }
        if (awakening.hasBothBloodlines()) {
            return I18n.format(NarutoFix.MODID + ".inventory.bloodline.both");
        }
        if (awakening.hasIndra()) {
            return I18n.format(NarutoFix.MODID + ".inventory.bloodline.indra");
        }
        if (awakening.hasAsura()) {
            return I18n.format(NarutoFix.MODID + ".inventory.bloodline.asura");
        }
        return I18n.format(NarutoFix.MODID + ".inventory.bloodline.none");
    }

    private static DrawPlan choosePlan(FontRenderer fontRenderer, TextVariants variants, int guiLeft, int guiTop,
                                       int xSize, int ySize, int screenWidth, int screenHeight) {
        DrawPlan plan = trySingleColumn(fontRenderer, variants.full, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        plan = trySingleColumn(fontRenderer, variants.compact, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        plan = tryTwoColumns(fontRenderer, variants.full, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        plan = tryTwoColumns(fontRenderer, variants.compact, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        plan = trySingleColumn(fontRenderer, variants.tiny, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        plan = tryTwoColumns(fontRenderer, variants.tiny, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
        if (plan != null) return plan;
        return trySingleColumn(fontRenderer, variants.inline, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
    }

    private static DrawPlan trySingleColumn(FontRenderer fontRenderer, String[] lines, int guiLeft, int guiTop,
                                            int xSize, int ySize, int screenWidth, int screenHeight) {
        int width = getMaxWidth(fontRenderer, lines);
        int height = lines.length * LINE_HEIGHT;
        return createPlan(lines, false, 0, width, height, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
    }

    private static DrawPlan tryTwoColumns(FontRenderer fontRenderer, String[] lines, int guiLeft, int guiTop,
                                          int xSize, int ySize, int screenWidth, int screenHeight) {
        if (lines.length != 4) {
            return null;
        }
        int firstColumnWidth = Math.max(fontRenderer.getStringWidth(lines[0]), fontRenderer.getStringWidth(lines[2]));
        int secondColumnWidth = Math.max(fontRenderer.getStringWidth(lines[1]), fontRenderer.getStringWidth(lines[3]));
        int width = firstColumnWidth + COLUMN_GAP + secondColumnWidth;
        int height = 2 * LINE_HEIGHT;
        return createPlan(lines, true, firstColumnWidth, width, height, guiLeft, guiTop, xSize, ySize, screenWidth, screenHeight);
    }

    private static DrawPlan createPlan(String[] lines, boolean twoColumns, int firstColumnWidth, int width, int height,
                                       int guiLeft, int guiTop, int xSize, int ySize, int screenWidth, int screenHeight) {
        int maxWidth = screenWidth - SCREEN_MARGIN * 2;
        if (width <= 0 || width > maxWidth) {
            return null;
        }

        Integer y = chooseY(guiTop, ySize, height, screenHeight);
        if (y == null) {
            return null;
        }

        int absoluteX = guiLeft + (xSize - width) / 2;
        int minX = SCREEN_MARGIN;
        int maxX = Math.max(SCREEN_MARGIN, screenWidth - SCREEN_MARGIN - width);
        absoluteX = Math.max(minX, Math.min(absoluteX, maxX));
        return new DrawPlan(lines, twoColumns, firstColumnWidth, absoluteX - guiLeft, y, width, height);
    }

    private static Integer chooseY(int guiTop, int ySize, int height, int screenHeight) {
        int belowSpace = screenHeight - (guiTop + ySize) - GUI_MARGIN;
        if (belowSpace >= height) {
            return ySize + GUI_MARGIN;
        }

        int aboveSpace = guiTop - GUI_MARGIN;
        if (aboveSpace >= height) {
            return -height - GUI_MARGIN;
        }
        return null;
    }

    private static int getMaxWidth(FontRenderer fontRenderer, String[] lines) {
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, fontRenderer.getStringWidth(line));
        }
        return width;
    }

    private static String formatAmount(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }
        double rounded = Math.rint(value);
        if (Math.abs(value - rounded) < 0.05D || Math.abs(value) >= 1000.0D) {
            return String.valueOf((int) Math.round(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatRate(double perTick, int decimals) {
        double perSecond = perTick * 20.0D;
        if (Math.abs(perSecond) < 0.005D) {
            perSecond = 0.0D;
        }
        return String.format(Locale.ROOT, "%+." + decimals + "f", perSecond);
    }

    private static int updateStationaryTicks(EntityPlayer player) {
        int currentTick = player.ticksExisted;
        if (!positionInitialized || player.getEntityId() != trackedEntityId || player.dimension != trackedDimension
                || currentTick < lastPlayerTick) {
            positionInitialized = true;
            trackedEntityId = player.getEntityId();
            trackedDimension = player.dimension;
            lastPlayerTick = currentTick;
            stationaryTicks = 0;
            lastX = player.posX;
            lastY = player.posY;
            lastZ = player.posZ;
            return stationaryTicks;
        }

        if (currentTick == lastPlayerTick) {
            return stationaryTicks;
        }

        int tickDelta = Math.max(1, currentTick - lastPlayerTick);
        double dx = player.posX - lastX;
        double dy = player.posY - lastY;
        double dz = player.posZ - lastZ;
        stationaryTicks = dx * dx + dy * dy + dz * dz <= STATIONARY_DISTANCE_SQ ? stationaryTicks + tickDelta : 0;
        lastPlayerTick = currentTick;
        lastX = player.posX;
        lastY = player.posY;
        lastZ = player.posZ;
        return stationaryTicks;
    }

    private static final class TextVariants {
        private final String[] full;
        private final String[] compact;
        private final String[] tiny;
        private final String[] inline;

        private TextVariants(String[] full, String[] compact, String[] tiny, String[] inline) {
            this.full = full;
            this.compact = compact;
            this.tiny = tiny;
            this.inline = inline;
        }
    }

    private static final class DrawPlan {
        private final String[] lines;
        private final boolean twoColumns;
        private final int firstColumnWidth;
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private DrawPlan(String[] lines, boolean twoColumns, int firstColumnWidth, int x, int y, int width, int height) {
            this.lines = lines;
            this.twoColumns = twoColumns;
            this.firstColumnWidth = firstColumnWidth;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
