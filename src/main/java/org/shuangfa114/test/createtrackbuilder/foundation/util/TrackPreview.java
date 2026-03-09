package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//too much information!!!!!!!
public class TrackPreview {
    public static PlacementInfo cached;
    public static List<Segment> segments;
    static Segment hoveringStart;
    static Segment hoveringEnd;
    static boolean hoveringMaxed;
    static BlockPos hintPos;
    static Couple<List<BlockPos>> hints;
    static int lastLineCount = 0;
    public static TrackPreview.PlacementInfo preview;

    public static PlacementInfo tryConnect(Level level, Segment start, Segment end, boolean maximiseTurn) {
        boolean girder = false;
        if (level.isClientSide && cached != null && start.equals(hoveringStart) && end.equals(hoveringEnd) && hoveringMaxed == maximiseTurn)
            return cached;

        PlacementInfo info = new PlacementInfo();
        hoveringMaxed = maximiseTurn;
        hoveringStart = start;
        hoveringEnd = end;
        cached = info;
        Vec3 axis1 = start.axis;
        Vec3 axis2 = end.axis;
        Vec3 normal1 = getUpNormal().normalize();
        Vec3 normal2 = getUpNormal().normalize();
        Vec3 normedAxis1 = axis1.normalize();
        Vec3 normedAxis2 = axis2.normalize();
        Vec3 end1 = getCurveStart(start.pos, axis1);
        Vec3 end2 = getCurveStart(end.pos, axis2);
        //Segment segment = segments.get(seg);
        BlockPos pos1 = start.pos;//开始坐标
        info.end1 = end1;
        info.end2 = end2;
        info.normal1 = normal1;
        info.normal2 = normal2;
        info.axis1 = axis1;
        info.axis2 = end.axis;

        if (axis1.dot(end2.subtract(end1)) < 0) {
            axis1 = axis1.scale(-1);
            normedAxis1 = normedAxis1.scale(-1);
            end1 = getCurveStart(pos1, axis1);
            if (level.isClientSide) {
                info.end1 = end1;
                info.axis1 = axis1;
            }
        }

        double[] intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, Direction.Axis.Y);
        boolean parallel = intersect == null;
        boolean skipCurve = false;

        if ((parallel && normedAxis1.dot(normedAxis2) > 0) || (!parallel && (intersect[0] < 0 || intersect[1] < 0))) {
            end.axis = end.axis.scale(-1);
            normedAxis2 = normedAxis2.scale(-1);
            end2 = getCurveStart(end.pos, end.axis);
            if (level.isClientSide) {
                info.end2 = end2;
                info.axis2 = end.axis;
            }
        }

        Vec3 cross2 = normedAxis2.cross(new Vec3(0, 1, 0));

        double a1 = Mth.atan2(normedAxis2.z, normedAxis2.x);
        double a2 = Mth.atan2(normedAxis1.z, normedAxis1.x);
        double angle = a1 - a2;
        double ascend = end2.subtract(end1).y;
        double absAscend = Math.abs(ascend);
        boolean slope = !normal1.equals(normal2);

        if (level.isClientSide) {
            Vec3 offset1 = axis1.scale(info.end1Extent);
            Vec3 offset2 = end.axis.scale(info.end2Extent);
            BlockPos targetPos1 = pos1.offset(BlockPos.containing(offset1));
            BlockPos targetEnd = end.pos.offset(BlockPos.containing(offset2));
            info.curve = new BezierConnection(Couple.create(targetPos1, targetEnd),
                    Couple.create(end1.add(offset1), end2.add(offset2)), Couple.create(normedAxis1, normedAxis2),
                    Couple.create(normal1, normal2), true, girder, TrackMaterial.ANDESITE);
        }

        // S curve or Straight

        double dist = 0;

        if (parallel) {
            double[] sTest = VecHelper.intersect(end1, end2, normedAxis1, cross2, Direction.Axis.Y);
            if (sTest != null) {
                double t = Math.abs(sTest[0]);
                double u = Math.abs(sTest[1]);

                skipCurve = Mth.equal(u, 0);

                if (!skipCurve && sTest[0] < 0)
                    return info.withMessage("perpendicular")
                            .tooJumbly();

                if (skipCurve) {
                    dist = VecHelper.getCenterOf(pos1)
                            .distanceTo(VecHelper.getCenterOf(end.pos));
                    info.end1Extent = (int) Math.round((dist + 1) / axis1.length());

                } else {
                    if (!Mth.equal(ascend, 0) || normedAxis1.y != 0)
                        return info.withMessage("ascending_s_curve");

                    double targetT = u <= 1 ? 3 : u * 2;

                    if (t < targetT)
                        return info.withMessage("too_sharp");

                    // This is for standardizing s curve sizes
                    if (t > targetT) {
                        int correction = (int) ((t - targetT) / axis1.length());
                        info.end1Extent = maximiseTurn ? 0 : correction / 2 + (correction % 2);
                        info.end2Extent = maximiseTurn ? 0 : correction / 2;
                    }
                }
            }
        }

        // Slope

        if (slope) {
            if (!skipCurve)
                return info.withMessage("slope_turn");
            if (Mth.equal(normal1.dot(normal2), 0))
                return info.withMessage("opposing_slopes");
            if ((axis1.y < 0 || end.axis.y > 0) && ascend > 0)
                return info.withMessage("leave_slope_ascending");
            if ((axis1.y > 0 || end.axis.y < 0) && ascend < 0)
                return info.withMessage("leave_slope_descending");

            skipCurve = false;
            info.end1Extent = 0;
            info.end2Extent = 0;

            Direction.Axis plane = Mth.equal(axis1.x, 0) ? Direction.Axis.X : Direction.Axis.Z;
            intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, plane);
            double dist1 = Math.abs(intersect[0] / axis1.length());
            double dist2 = Math.abs(intersect[1] / end.axis.length());

            if (dist1 > dist2)
                info.end1Extent = (int) Math.round(dist1 - dist2);
            if (dist2 > dist1)
                info.end2Extent = (int) Math.round(dist2 - dist1);

            double turnSize = Math.min(dist1, dist2);
            if (intersect[0] < 0 || intersect[1] < 0)
                return info.withMessage("too_sharp")
                        .tooJumbly();
            if (turnSize < 2)
                return info.withMessage("too_sharp");

            // This is for standardizing curve sizes
            if (turnSize > 2 && !maximiseTurn) {
                info.end1Extent += maximiseTurn ? 0 : (int) (turnSize - 2);
                info.end2Extent += maximiseTurn ? 0 : (int) (turnSize - 2);
                turnSize = 2;
            }
        }

        // Straight ascend

        if (skipCurve && !Mth.equal(ascend, 0)) {
            int hDistance = info.end1Extent;
            if (axis1.y == 0 || !Mth.equal(absAscend + 1, dist / axis1.length())) {

                if (axis1.y != 0 && axis1.y == -axis2.y)
                    return info.withMessage("ascending_s_curve");

                info.end1Extent = 0;
                double minHDistance = Math.max(absAscend < 4 ? absAscend * 4 : absAscend * 3, 6) / axis1.length();
                if (hDistance < minHDistance)
                    return info.withMessage("too_steep");
                if (hDistance > minHDistance) {
                    int correction = (int) (hDistance - minHDistance);
                    info.end1Extent = correction / 2 + (correction % 2);
                    info.end2Extent = correction / 2;
                }

                skipCurve = false;
            }
        }

        // Turn

        if (!parallel) {
            float absAngle = Math.abs(AngleHelper.deg(angle));
            if (absAngle < 60 || absAngle > 300)
                return info.withMessage("turn_90")
                        .tooJumbly();

            intersect = VecHelper.intersect(end1, end2, normedAxis1, normedAxis2, Direction.Axis.Y);
            double dist1 = Math.abs(intersect[0]);
            double dist2 = Math.abs(intersect[1]);
            float ex1 = 0;
            float ex2 = 0;

            if (dist1 > dist2)
                ex1 = (float) ((dist1 - dist2) / axis1.length());
            if (dist2 > dist1)
                ex2 = (float) ((dist2 - dist1) / end.axis.length());

            double turnSize = Math.min(dist1, dist2) - .1d;
            boolean ninety = (absAngle + .25f) % 90 < 1;

            if (intersect[0] < 0 || intersect[1] < 0)
                return info.withMessage("too_sharp")
                        .tooJumbly();

            double minTurnSize = ninety ? 7 : 3.25;
            double turnSizeToFitAscend =
                    minTurnSize + (ninety ? Math.max(0, absAscend - 3) * 2f : Math.max(0, absAscend - 1.5f) * 1.5f);

            if (turnSize < minTurnSize)
                return info.withMessage("too_sharp");
            if (turnSize < turnSizeToFitAscend)
                return info.withMessage("too_steep");

            // This is for standardizing curve sizes
            if (!maximiseTurn) {
                ex1 += (float) ((turnSize - turnSizeToFitAscend) / axis1.length());
                ex2 += (float) ((turnSize - turnSizeToFitAscend) / axis2.length());
            }
            info.end1Extent = Mth.floor(ex1);
            info.end2Extent = Mth.floor(ex2);
            turnSize = turnSizeToFitAscend;
        }

        Vec3 offset1 = axis1.scale(info.end1Extent);
        Vec3 offset2 = end.axis.scale(info.end2Extent);
        BlockPos targetPos1 = pos1.offset(BlockPos.containing(offset1));
        BlockPos targetEnd = end.pos.offset(BlockPos.containing(offset2));

        info.curve = skipCurve ? null
                : new BezierConnection(Couple.create(targetPos1, targetEnd),
                Couple.create(end1.add(offset1), end2.add(offset2)), Couple.create(normedAxis1, normedAxis2),
                Couple.create(normal1, normal2), true, girder, TrackMaterial.ANDESITE);

        info.valid = true;

        info.start = pos1;
        info.end = end.pos;
        info.axis1 = axis1;
        info.axis2 = end.axis;
        info.hasRequiredTracks = true;
        return info;
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof TrackEditor trackEditor)) {
            return;
        }
        if (!trackEditor.isPosValid(stack)) {
            return;
        }
        Level level = player.level();
        BlockPos pos = trackEditor.getEndPos(stack);
        PlacementInfo info = preview;
        if (cached == null) {
            return;
        }
        if (info.valid)
            player.displayClientMessage(CreateLang.translateDirect("track.valid_connection")
                    .withStyle(ChatFormatting.GREEN), true);
        else if (info.message != null)
            player.displayClientMessage(CreateLang.translateDirect(info.message)
                            .withStyle(info.message.equals("track.second_point") ? ChatFormatting.WHITE : ChatFormatting.RED),
                    true);
        if (!info.valid) {
            if (!pos.equals(hintPos)) {
                hints = Couple.create(ArrayList::new);
                hintPos = pos;

                for (int xOffset = -5; xOffset <= 5; xOffset++) {
                    for (int zOffset = -5; zOffset <= 5; zOffset++) {
                        BlockPos offset = pos.offset(xOffset, 0, zOffset);
                        if (!level.getBlockState(offset).isAir()) {
                            continue;
                        }
                        Segment start = new Segment(trackEditor.getStartPos(stack), Util.getAxis(trackEditor.getStartShape(stack)));
                        Segment end = new Segment(offset, Util.getAxis(trackEditor.getStartShape(stack)));
                        PlacementInfo adjInfo = tryConnect(level, start, end, false);
                        hints.get(adjInfo.valid)
                                .add(offset.below());
                    }
                }
            }

            if (hints != null && !hints.either(Collection::isEmpty)) {
                Outliner.getInstance().showCluster("track_valid", hints.getFirst())
                        .withFaceTexture(AllSpecialTextures.THIN_CHECKERED)
                        .colored(0x95CD41)
                        .lineWidth(0);
                Outliner.getInstance().showCluster("track_invalid", hints.getSecond())
                        .withFaceTexture(AllSpecialTextures.THIN_CHECKERED)
                        .colored(0xEA5C2B)
                        .lineWidth(0);
            }
        }

        if (!info.valid) {
            info.end1Extent = 0;
            info.end2Extent = 0;
        }
        int color = info.valid ? 0x95CD41 : 0xEA5C2B;
        Vec3 up = new Vec3(0, 4 / 16f, 0);

        {
            Vec3 v1 = info.end1;
            Vec3 a1 = info.axis1.normalize();
            Vec3 n1 = info.normal1.cross(a1)
                    .scale(15 / 16f);
            Vec3 o1 = a1.scale(0.125f);
            Vec3 ex1 =
                    a1.scale((info.end1Extent - (info.curve == null && info.end1Extent > 0 ? 2 : 0)) * info.axis1.length());
            line(1, v1.add(n1).add(up), o1, ex1, color);
            line(2, v1.subtract(n1).add(up), o1, ex1, color);
            Vec3 v2 = info.end2;
            Vec3 a2 = info.axis2.normalize();
            Vec3 n2 = info.normal2.cross(a2)
                    .scale(15 / 16f);
            Vec3 o2 = a2.scale(0.125f);
            Vec3 ex2 = a2.scale(info.end2Extent * info.axis2.length());
            line(3, v2.add(n2).add(up), o2, ex2, color);
            line(4, v2.subtract(n2).add(up), o2, ex2, color);
        }

        BezierConnection bc = info.curve;
        if (bc == null)
            return;

        Vec3 previous1 = null;
        Vec3 previous2 = null;
        int segCount = bc.getSegmentCount();

        Vec3 end1 = bc.starts.getFirst();
        Vec3 end2 = bc.starts.getSecond();
        Vec3 finish1 = end1.add(bc.axes.getFirst()
                .scale(bc.getHandleLength()));
        Vec3 finish2 = end2.add(bc.axes.getSecond()
                .scale(bc.getHandleLength()));
        String key = "curve";

        for (int i = 0; i <= segCount; i++) {
            float t = i / (float) segCount;
            Vec3 result = VecHelper.bezier(end1, end2, finish1, finish2, t);
            Vec3 derivative = VecHelper.bezierDerivative(end1, end2, finish1, finish2, t)
                    .normalize();
            Vec3 normal = bc.getNormal(t)
                    .cross(derivative)
                    .scale(15 / 16f);
            Vec3 rail1 = result.add(normal)
                    .add(up);
            Vec3 rail2 = result.subtract(normal)
                    .add(up);

            if (previous1 != null) {
                Outliner.getInstance()
                        .showLine(Pair.of(key, i * 2), previous1, rail1)
                        .colored(color)
                        .disableLineNormals()
                        .lineWidth(1 / 16f);
                Outliner.getInstance()
                        .showLine(Pair.of(key, i * 2 + 1), previous2, rail2)
                        .colored(color)
                        .disableLineNormals()
                        .lineWidth(1 / 16f);
            }

            previous1 = rail1;
            previous2 = rail2;
        }

        for (int i = segCount + 1; i <= lastLineCount; i++) {
            Outliner.getInstance().remove(Pair.of(key, i * 2));
            Outliner.getInstance().remove(Pair.of(key, i * 2 + 1));
        }

        lastLineCount = segCount;
    }

    @OnlyIn(Dist.CLIENT)
    private static void line(int id, Vec3 v1, Vec3 o1, Vec3 ex, int color) {
        Outliner.getInstance().showLine(Pair.of("start", id), v1.subtract(o1), v1.add(ex))
                .lineWidth(1 / 8f)
                .disableLineNormals()
                .colored(color);
    }

    public static Vec3 getCurveStart(BlockPos pos, Vec3 axis) {
        boolean vertical = axis.y != 0;
        return VecHelper.getCenterOf(pos)
                .add(0, (vertical ? 0 : -.5f), 0)
                .add(axis.scale(.5));
    }

    public static Vec3 getUpNormal() {
        return new Vec3(0, 1, 0);
    }

    public static class PlacementInfo {
        public int requiredTracks = 0;
        public boolean hasRequiredTracks = false;
        public int requiredPavement = 0;
        public boolean hasRequiredPavement = false;
        // for visualization
        public Vec3 end1;
        public Vec3 end2;
        public Vec3 normal1;
        public Vec3 normal2;
        public Vec3 axis1;
        public Vec3 axis2;
        public BlockPos start;
        public BlockPos end;
        BezierConnection curve = null;
        boolean valid = false;
        int end1Extent = 0;
        int end2Extent = 0;
        String message = null;

        public PlacementInfo withMessage(String message) {
            this.message = "track." + message;
            return this;
        }

        public PlacementInfo tooJumbly() {
            curve = null;
            return this;
        }

    }
}
