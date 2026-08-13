package com.zclient.client.module.modules.combat;

import com.zclient.client.module.Module;
import com.zclient.client.setting.settings.BooleanSetting;
import com.zclient.client.setting.settings.NumberSetting;
import com.zclient.client.setting.settings.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AimAssist extends Module {

    // Настройки
    public static BooleanSetting enabled = new BooleanSetting("Enabled", true);
    
    public static ModeSetting targetMode = new ModeSetting("Target", "Players", 
        "Players", "Mobs", "All");
    
    public static NumberSetting smoothness = new NumberSetting("Smoothness", 0.5, 0.01, 1.0, 0.01);
    public static NumberSetting range = new NumberSetting("Range", 4.5, 1.0, 8.0, 0.1);
    public static NumberSetting fov = new NumberSetting("FOV", 180, 10, 180, 1);
    public static BooleanSetting lockView = new BooleanSetting("Lock View", false);

    public AimAssist() {
        super("AimAssist", "Автоматическое наведение на цель", GLFW.GLFW_KEY_K, Category.COMBAT);
        addSettings(enabled, targetMode, smoothness, range, fov, lockView);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        if (!enabled.getValue() || mc.player == null || mc.world == null) return;

        Entity target = getTarget();
        if (target != null) {
            aimAtEntity(target);
        }
    }

    // Получение лучшей цели
    private Entity getTarget() {
        List<Entity> targets = new ArrayList<>();
        double maxRange = range.getValue();
        double maxFov = fov.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (((LivingEntity) entity).getHealth() <= 0) continue;
            if (mc.player.squaredDistanceTo(entity) > maxRange * maxRange) continue;

            // Фильтрация по типу цели
            boolean isPlayer = entity instanceof PlayerEntity;
            boolean isMob = entity instanceof HostileEntity;
            
            String mode = targetMode.getValue();
            if (mode.equals("Players") && !isPlayer) continue;
            if (mode.equals("Mobs") && !isMob) continue;
            // "All" пропускает всех

            // Проверка угла обзора (FOV)
            if (!canSeeEntity(entity, maxFov)) continue;

            targets.add(entity);
        }

        if (targets.isEmpty()) return null;

        // Возвращаем ближайшую цель
        return targets.stream()
                .min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)))
                .orElse(null);
    }

    // Проверка видимости и угла
    private boolean canSeeEntity(Entity entity, double maxFov) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        Vec3d entityPos = new Vec3d(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());
        
        Vec3d direction = entityPos.subtract(eyesPos).normalize();
        
        // Вектор взгляда игрока
        Vec3d lookVec = mc.player.getRotationVecClient();
        
        double dotProduct = direction.dotProduct(lookVec);
        double angle = Math.toDegrees(Math.acos(dotProduct));
        
        return angle < maxFov;
    }

    // Логика плавного наведения
    private void aimAtEntity(Entity target) {
        if (target == null) return;

        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getStandingEyeHeight(), mc.player.getZ());
        Vec3d targetPos = new Vec3d(target.getX(), target.getY() + target.getStandingEyeHeight(), target.getZ());
        
        Vec3d diff = targetPos.subtract(eyesPos);
        
        // Расчет необходимых углов
        double yaw = Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0;
        double pitch = -Math.toDegrees(Math.atan2(diff.y, MathHelper.sqrt((float)(diff.x * diff.x + diff.z * diff.z))));

        // Текущие углы
        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        // Нормализация углов (-180 до 180)
        yaw = normalizeAngle(yaw);
        
        // Расчет разницы
        double deltaYaw = yaw - currentYaw;
        double deltaPitch = pitch - currentPitch;
        
        // Нормализация дельты для кратчайшего пути
        while (deltaYaw > 180) deltaYaw -= 360;
        while (deltaYaw < -180) deltaYaw += 360;
        while (deltaPitch > 180) deltaPitch -= 360;
        while (deltaPitch < -180) deltaPitch += 360;

        // Применение плавности (Lerp)
        double smoothFactor = smoothness.getValue();
        
        double newYaw = currentYaw + (deltaYaw * smoothFactor);
        double newPitch = currentPitch + (deltaPitch * smoothFactor);

        // Ограничение питча (чтобы не сломать шею)
        newPitch = MathHelper.clamp(newPitch, -90.0, 90.0);

        if (lockView.getValue()) {
            // Мгновенный поворот если включен Lock View (игнорируем плавность)
            mc.player.setYaw((float)yaw);
            mc.player.setPitch((float)pitch);
        } else {
            mc.player.setYaw((float)newYaw);
            mc.player.setPitch((float)newPitch);
        }
    }

    private double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle > 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
    }
}
