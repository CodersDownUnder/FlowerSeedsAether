package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolemFlowerLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    private final BlockRenderDispatcher blockRenderer;

    public IronGolemFlowerLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> p_234842_, BlockRenderDispatcher p_234843_) {
        super(p_234842_);
        this.blockRenderer = p_234843_;
    }

    public void render(
        PoseStack p_117172_,
        MultiBufferSource p_117173_,
        int p_117174_,
        IronGolem p_117175_,
        float p_117176_,
        float p_117177_,
        float p_117178_,
        float p_117179_,
        float p_117180_,
        float p_117181_
    ) {
        if (p_117175_.getOfferFlowerTick() != 0) {
            p_117172_.pushPose();
            ModelPart modelpart = this.getParentModel().getFlowerHoldingArm();
            modelpart.translateAndRotate(p_117172_);
            p_117172_.translate(-1.1875F, 1.0625F, -0.9375F);
            p_117172_.translate(0.5F, 0.5F, 0.5F);
            float f = 0.5F;
            p_117172_.scale(0.5F, 0.5F, 0.5F);
            p_117172_.mulPose(Axis.XP.rotationDegrees(-90.0F));
            p_117172_.translate(-0.5F, -0.5F, -0.5F);
            this.blockRenderer.renderSingleBlock(Blocks.POPPY.defaultBlockState(), p_117172_, p_117173_, p_117174_, OverlayTexture.NO_OVERLAY);
            p_117172_.popPose();
        }
    }
}
