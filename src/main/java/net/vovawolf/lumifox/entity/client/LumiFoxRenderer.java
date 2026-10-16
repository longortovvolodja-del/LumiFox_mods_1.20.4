package net.vovawolf.lumifox.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.vovawolf.lumifox.entity.LumiFox;

@OnlyIn(Dist.CLIENT)
public class LumiFoxRenderer extends MobRenderer<LumiFox, LumiFoxModel<LumiFox>> {

    private static final ResourceLocation LUMI_FOX_LOCATION =
            new ResourceLocation("lumifox", "textures/entity/lumifox.png");
    private static final ResourceLocation LUMI_FOX_SLEEP_LOCATION =
            new ResourceLocation("lumifox", "textures/entity/lumifox_sleep.png");

    public LumiFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LumiFoxModel<>(context.bakeLayer(LumiFoxModel.LAYER_LOCATION)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(LumiFox entity) {
        return entity.isSleeping() ? LUMI_FOX_SLEEP_LOCATION : LUMI_FOX_LOCATION;
    }
}