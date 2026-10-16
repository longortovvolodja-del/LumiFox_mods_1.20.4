package net.vovawolf.lumifox.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.vovawolf.lumifox.registry.ModItems;

/**
 * Класс умной лисички Lumi, наследуется от ванильной лисы
 * Lumi - это дружелюбная и пассивная лиса, которая следует за игроком с сладкими ягодами
 */
public class LumiFox extends Fox {

    // Надет ли рюкзак (синхронизируется с клиентом, чтобы модель знала, показывать ли его)
    private static final EntityDataAccessor<Boolean> DATA_BACKPACK =
            SynchedEntityData.defineId(LumiFox.class, EntityDataSerializers.BOOLEAN);

    // Конструктор для создания сущности LumiFox
    public LumiFox(EntityType<? extends Fox> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BACKPACK, false);
    }

    public boolean hasBackpack() {
        return this.entityData.get(DATA_BACKPACK);
    }

    public void setBackpack(boolean value) {
        this.entityData.set(DATA_BACKPACK, value);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("LumiBackpack", this.hasBackpack());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setBackpack(tag.getBoolean("LumiBackpack"));
    }

    /**
     * Если Lumi погибла в рюкзаке, он выпадает
     */
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (this.hasBackpack()) {
            this.spawnAtLocation(new ItemStack(ModItems.LUMI_BACKPACK.get()));
        }
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
     * Взаимодействие с игроком:
     *  - ПКМ рюкзаком: надеть рюкзак на Lumi
     *  - Shift + ПКМ пустой рукой (пасть пуста): снять рюкзак
     *  - ПКМ пустой рукой: Lumi отдаёт предмет из пасти
     *  - Shift + ПКМ предметом: Lumi берёт один такой предмет в пасть (если пасть свободна)
     *  - ПКМ сладкими ягодами: лечит Lumi
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Обрабатываем только основную руку, чтобы не срабатывало дважды
        if (hand != InteractionHand.MAIN_HAND) {
            return super.mobInteract(player, hand);
        }

        ItemStack playerStack = player.getItemInHand(hand);
        ItemStack mouthStack = this.getItemBySlot(EquipmentSlot.MAINHAND);

        // 0) Надеть рюкзак
        if (playerStack.is(ModItems.LUMI_BACKPACK.get()) && !this.hasBackpack()) {
            if (!this.level().isClientSide) {
                this.setBackpack(true);
                if (!player.getAbilities().instabuild) {
                    playerStack.shrink(1);
                }
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 0.5) Снять рюкзак: Shift + пустая рука, пасть пуста
        if (player.isShiftKeyDown() && playerStack.isEmpty() && mouthStack.isEmpty() && this.hasBackpack()) {
            if (!this.level().isClientSide) {
                this.setBackpack(false);
                ItemStack backpack = new ItemStack(ModItems.LUMI_BACKPACK.get());
                if (!player.getInventory().add(backpack)) {
                    player.drop(backpack, false);
                }
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 1) Пустая рука, у Lumi что-то во рту: отдаём предмет
        if (playerStack.isEmpty() && !mouthStack.isEmpty()) {
            if (!this.level().isClientSide) {
                ItemStack given = mouthStack.copy();
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                if (!player.getInventory().add(given)) {
                    player.drop(given, false);
                }
                this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 2) Shift + предмет в руке, пасть свободна: берём один предмет
        if (player.isShiftKeyDown() && !playerStack.isEmpty() && mouthStack.isEmpty()) {
            if (!this.level().isClientSide) {
                ItemStack taken = playerStack.split(1);
                this.setItemSlot(EquipmentSlot.MAINHAND, taken);
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND); // при гибели предмет выпадет обязательно
                this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // 3) Обычный ПКМ ягодами: лечим лису
        if (playerStack.is(Items.SWEET_BERRIES)) {
            if (!this.level().isClientSide) {
                this.heal(2.0F);
                if (!player.isCreative()) {
                    playerStack.shrink(1);
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