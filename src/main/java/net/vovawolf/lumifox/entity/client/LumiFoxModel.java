package net.vovawolf.lumifox.entity.client;

import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.vovawolf.lumifox.LumiMod;
import net.vovawolf.lumifox.entity.LumiFox;

@OnlyIn(Dist.CLIENT)
public class LumiFoxModel<T extends LumiFox> extends FoxModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(LumiMod.MODID, "lumi_fox"), "main");

    private final ModelPart tailMain;
    private final ModelPart tailTip;
    private final ModelPart backpack;

    public LumiFoxModel(ModelPart root) {
        super(root);
        this.tailMain = root.getChild("body").getChild("tail");
        this.tailTip = this.tailMain.getChild("tail_tip");
        this.backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F),
                PartPose.offset(-1.0F, 16.5F, -3.0F));
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F),
                PartPose.ZERO);

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, (float) Math.PI / 2F, 0.0F, 0.0F));

        CubeDeformation deform = new CubeDeformation(0.001F);
        CubeListBuilder leftLeg = CubeListBuilder.create().texOffs(4, 24)
                .addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, deform);
        CubeListBuilder rightLeg = CubeListBuilder.create().texOffs(13, 24)
                .addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, deform);
        root.addOrReplaceChild("right_hind_leg", rightLeg, PartPose.offset(-5.0F, 17.5F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", leftLeg, PartPose.offset(-1.0F, 17.5F, 7.0F));
        root.addOrReplaceChild("right_front_leg", rightLeg, PartPose.offset(-5.0F, 17.5F, 0.0F));
        root.addOrReplaceChild("left_front_leg", leftLeg, PartPose.offset(-1.0F, 17.5F, 0.0F));

        // Хвост из двух сегментов: tail (UV 30,0) и tail_tip (UV 0,32)
        PartDefinition tail = body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 5.0F, 5.0F),
                PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tail_tip",
                CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 5.0F, 5.0F),
                PartPose.offset(4.0F, 5.0F, 0.0F));

        // Рюкзак на спине (координаты перенесены из LumiFox_5.bbmodel, UV из той же текстуры 48x48)
        // Часть привязана к уровню земли (y = 24), поэтому кубы заданы "вверх" отрицательными y
        PartDefinition pack = root.addOrReplaceChild("backpack", CubeListBuilder.create()
                        .texOffs(16, 38).addBox(-4.0F, -12.5F, -1.0F, 8.0F, 2.0F, 8.0F)   // верхняя плита
                        .texOffs(28, 35).addBox(-4.0F, -4.25F, 1.5F, 8.0F, 1.0F, 2.0F)    // нижняя планка
                        .texOffs(6, 42).addBox(3.1F, -8.25F, 1.5F, 1.0F, 4.0F, 2.0F)      // боковая лямка
                        .texOffs(0, 42).addBox(-4.1F, -8.25F, 1.5F, 1.0F, 4.0F, 2.0F)     // боковая лямка
                        .texOffs(16, 39).addBox(3.0F, -10.5F, 1.0F, 1.0F, 4.0F, 3.0F)     // боковой карман
                        .texOffs(40, 39).addBox(-4.0F, -10.5F, 1.0F, 1.0F, 4.0F, 3.0F),   // боковой карман
                PartPose.offset(0.0F, 24.0F, 0.0F));
        // Клапан сзади, наклон -22.5 градусов (если наклонён не в ту сторону - поменять знак)
        pack.addOrReplaceChild("flap",
                CubeListBuilder.create().texOffs(15, 33).addBox(-2.75F, -2.0F, -1.0F, 5.5F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -10.25F, 7.0F, -0.3926991F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 48, 48);
    }

    // Ванильные анимации лисы остаются как есть (super), поверх добавляем вилянье хвостом
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Рюкзак виден только когда надет
        this.backpack.visible = entity.hasBackpack();

        if (entity.isSleeping()) {
            this.tailMain.zRot = 0.0F;
            this.tailTip.zRot = 0.0F;
            return;
        }
        float wave = ageInTicks * 0.25F;
        this.tailMain.zRot = Mth.sin(wave) * 0.12F;
        this.tailTip.zRot = Mth.sin(wave - 1.0F) * 0.45F;
    }
}