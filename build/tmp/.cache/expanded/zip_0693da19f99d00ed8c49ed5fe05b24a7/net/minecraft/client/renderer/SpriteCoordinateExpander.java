package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteCoordinateExpander implements VertexConsumer {
    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public SpriteCoordinateExpander(VertexConsumer p_110798_, TextureAtlasSprite p_110799_) {
        this.delegate = p_110798_;
        this.sprite = p_110799_;
    }

    @Override
    public VertexConsumer vertex(double p_110801_, double p_110802_, double p_110803_) {
        this.delegate.vertex(p_110801_, p_110802_, p_110803_);
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public VertexConsumer color(int p_110826_, int p_110827_, int p_110828_, int p_110829_) {
        this.delegate.color(p_110826_, p_110827_, p_110828_, p_110829_);
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public VertexConsumer uv(float p_110805_, float p_110806_) {
        this.delegate.uv(this.sprite.getU(p_110805_), this.sprite.getV(p_110806_));
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public VertexConsumer overlayCoords(int p_110823_, int p_110824_) {
        this.delegate.overlayCoords(p_110823_, p_110824_);
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public VertexConsumer uv2(int p_110835_, int p_110836_) {
        this.delegate.uv2(p_110835_, p_110836_);
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public VertexConsumer normal(float p_110831_, float p_110832_, float p_110833_) {
        this.delegate.normal(p_110831_, p_110832_, p_110833_);
        return this; //Forge: Fix MC-263524 not working with chained methods
    }

    @Override
    public void endVertex() {
        this.delegate.endVertex();
    }

    @Override
    public void defaultColor(int p_173392_, int p_173393_, int p_173394_, int p_173395_) {
        this.delegate.defaultColor(p_173392_, p_173393_, p_173394_, p_173395_);
    }

    @Override
    public void unsetDefaultColor() {
        this.delegate.unsetDefaultColor();
    }

    @Override
    public void vertex(
        float p_110808_,
        float p_110809_,
        float p_110810_,
        float p_110811_,
        float p_110812_,
        float p_110813_,
        float p_110814_,
        float p_110815_,
        float p_110816_,
        int p_110817_,
        int p_110818_,
        float p_110819_,
        float p_110820_,
        float p_110821_
    ) {
        this.delegate
            .vertex(
                p_110808_,
                p_110809_,
                p_110810_,
                p_110811_,
                p_110812_,
                p_110813_,
                p_110814_,
                this.sprite.getU(p_110815_),
                this.sprite.getV(p_110816_),
                p_110817_,
                p_110818_,
                p_110819_,
                p_110820_,
                p_110821_
            );
    }
}
