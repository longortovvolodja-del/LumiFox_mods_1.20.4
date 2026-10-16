package net.vovawolf.lumifox.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

/**
 * Класс умной лисички Lumi, наследуется от ванильной лисы
 * Lumi - это дружелюбная и пассивная лиса, которая следует за игроком с сладкими ягодами
 */
public class LumiFox extends Fox {

    // Конструктор для создания сущности LumiFox
    public LumiFox(EntityType<? extends Fox> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Регистрация целей ИИ для LumiFox
     * ВАЖНО: super.registerGoals() обязателен, иначе ванильная лиса падает при появлении
     */
    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Следование за игроком со сладкими ягодами
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.0D, Ingredient.of(Items.SWEET_BERRIES), false));
    }

    /**
     * Lumi мирная: не нападает ни на кого (куры, кролики и т.д.)
     */
    @Override
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    /**
     * Создание атрибутов для LumiFox
     * @return поставщик атрибутов
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D) // Здоровье как у обычной лисы
                .add(Attributes.MOVEMENT_SPEED, 0.3D) // Скорость передвижения
                .add(Attributes.ATTACK_DAMAGE, 2.0D) // Урон атаки
                .add(Attributes.FOLLOW_RANGE, 32.0D); // Дальность следования
    }

    /**
     * Обработка взаимодействия с игроком
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Если игрок держит сладкие ягоды, можно покормить лису
        if (itemstack.is(Items.SWEET_BERRIES)) {
            if (!this.level().isClientSide) {
                this.heal(2.0F); // Лечим лису
                if (!player.isCreative()) {
                    itemstack.shrink(1); // Уменьшаем количество ягод
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.SWEET_BERRIES);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FOX_HURT;
    }
}