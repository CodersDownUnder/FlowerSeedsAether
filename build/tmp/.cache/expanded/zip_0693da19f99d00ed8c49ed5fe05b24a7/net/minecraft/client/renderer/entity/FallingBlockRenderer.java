package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
    private final BlockRenderDispatcher dispatcher;

    public FallingBlockRenderer(EntityRendererProvider.Context p_174112_) {
        super(p_174112_);
        this.shadowRadius = 0.5F;
        this.dispatcher = p_174112_.getBlockRenderDispatcher();
    }

    public void render(FallingBlockEntity p_114634_, float p_114635_, float p_114636_, PoseStack p_114637_, MultiBufferSource p_114638_, int p_114639_) {
        BlockState blockstate = p_114634_.getBlockState();
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = p_114634_.level();
            if (blockstate != level.getBlockState(p_114634_.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                p_114637_.pushPose();
                BlockPos blockpos = BlockPos.containing(p_114634_.getX(), p_114634_.getBoundingBox().maxY, p_114634_.getZ());
                p_114637_.translate(-0.5, 0.0, -0.5);
                var model = this.dispatcher.getBlockModel(blockstate);
                for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(p_114634_.getStartPos())), net.neoforged.neoforge.client.model.data.ModelData.EMPTY))
                this.dispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                        level,
                        this.dispatcher.getBlockModel(blockstate),
                        blockstate,
                        blockpos,
                        p_114637_,
                        p_114638_.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                        false,
                        RandomSource.create(),
                        blockstate.getSeed(p_114634_.getStartPos()),
                        OverlayTexture.NO_OVERLAY,
                        net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                        renderType
                    );
                p_114637_.popPose();
                super.render(p_114634_, p_114635_, p_114636_, p_114637_, p_114638_, p_114639_);
            }
        }
    }

    public ResourceLocation getTextureLocation(FallingBlockEntity p_114632_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
