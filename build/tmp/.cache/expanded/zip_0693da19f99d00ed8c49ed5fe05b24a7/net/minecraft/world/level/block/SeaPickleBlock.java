package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeaPickleBlock extends BushBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    public static final MapCodec<SeaPickleBlock> CODEC = simpleCodec(SeaPickleBlock::new);
    public static final int MAX_PICKLES = 4;
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape ONE_AABB = Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
    protected static final VoxelShape TWO_AABB = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
    protected static final VoxelShape THREE_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    protected static final VoxelShape FOUR_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

    @Override
    public MapCodec<SeaPickleBlock> codec() {
        return CODEC;
    }

    public SeaPickleBlock(BlockBehaviour.Properties p_56082_) {
        super(p_56082_);
        this.registerDefaultState(this.stateDefinition.any().setValue(PICKLES, Integer.valueOf(1)).setValue(WATERLOGGED, Boolean.valueOf(true)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_56089_) {
        BlockState blockstate = p_56089_.getLevel().getBlockState(p_56089_.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(PICKLES, Integer.valueOf(Math.min(4, blockstate.getValue(PICKLES) + 1)));
        } else {
            FluidState fluidstate = p_56089_.getLevel().getFluidState(p_56089_.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;
            return super.getStateForPlacement(p_56089_).setValue(WATERLOGGED, Boolean.valueOf(flag));
        }
    }

    public static boolean isDead(BlockState p_56133_) {
        return !p_56133_.getValue(WATERLOGGED);
    }

    @Override
    protected boolean mayPlaceOn(BlockState p_56127_, BlockGetter p_56128_, BlockPos p_56129_) {
        return !p_56127_.getCollisionShape(p_56128_, p_56129_).getFaceShape(Direction.UP).isEmpty() || p_56127_.isFaceSturdy(p_56128_, p_56129_, Direction.UP);
    }

    @Override
    public boolean canSurvive(BlockState p_56109_, LevelReader p_56110_, BlockPos p_56111_) {
        BlockPos blockpos = p_56111_.below();
        return this.mayPlaceOn(p_56110_.getBlockState(blockpos), p_56110_, blockpos);
    }

    @Override
    public BlockState updateShape(BlockState p_56113_, Direction p_56114_, BlockState p_56115_, LevelAccessor p_56116_, BlockPos p_56117_, BlockPos p_56118_) {
        if (!p_56113_.canSurvive(p_56116_, p_56117_)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            if (p_56113_.getValue(WATERLOGGED)) {
                p_56116_.scheduleTick(p_56117_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56116_));
            }

            return super.updateShape(p_56113_, p_56114_, p_56115_, p_56116_, p_56117_, p_56118_);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState p_56101_, BlockPlaceContext p_56102_) {
        return !p_56102_.isSecondaryUseActive() && p_56102_.getItemInHand().is(this.asItem()) && p_56101_.getValue(PICKLES) < 4
            ? true
            : super.canBeReplaced(p_56101_, p_56102_);
    }

    @Override
    public VoxelShape getShape(BlockState p_56122_, BlockGetter p_56123_, BlockPos p_56124_, CollisionContext p_56125_) {
        switch(p_56122_.getValue(PICKLES)) {
            case 1:
            default:
                return ONE_AABB;
            case 2:
                return TWO_AABB;
            case 3:
                return THREE_AABB;
            case 4:
                return FOUR_AABB;
        }
    }

    @Override
    public FluidState getFluidState(BlockState p_56131_) {
        return p_56131_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56131_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56120_) {
        p_56120_.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader p_255984_, BlockPos p_56092_, BlockState p_56093_) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level p_222418_, RandomSource p_222419_, BlockPos p_222420_, BlockState p_222421_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel p_222413_, RandomSource p_222414_, BlockPos p_222415_, BlockState p_222416_) {
        if (!isDead(p_222416_) && p_222413_.getBlockState(p_222415_.below()).is(BlockTags.CORAL_BLOCKS)) {
            int i = 5;
            int j = 1;
            int k = 2;
            int l = 0;
            int i1 = p_222415_.getX() - 2;
            int j1 = 0;

            for(int k1 = 0; k1 < 5; ++k1) {
                for(int l1 = 0; l1 < j; ++l1) {
                    int i2 = 2 + p_222415_.getY() - 1;

                    for(int j2 = i2 - 2; j2 < i2; ++j2) {
                        BlockPos blockpos = new BlockPos(i1 + k1, j2, p_222415_.getZ() - j1 + l1);
                        if (blockpos != p_222415_ && p_222414_.nextInt(6) == 0 && p_222413_.getBlockState(blockpos).is(Blocks.WATER)) {
                            BlockState blockstate = p_222413_.getBlockState(blockpos.below());
                            if (blockstate.is(BlockTags.CORAL_BLOCKS)) {
                                p_222413_.setBlock(
                                    blockpos, Blocks.SEA_PICKLE.defaultBlockState().setValue(PICKLES, Integer.valueOf(p_222414_.nextInt(4) + 1)), 3
                                );
                            }
                        }
                    }
                }

                if (l < 2) {
                    j += 2;
                    ++j1;
                } else {
                    j -= 2;
                    --j1;
                }

                ++l;
            }

            p_222413_.setBlock(p_222415_, p_222416_.setValue(PICKLES, Integer.valueOf(4)), 2);
        }
    }

    @Override
    public boolean isPathfindable(BlockState p_56104_, BlockGetter p_56105_, BlockPos p_56106_, PathComputationType p_56107_) {
        return false;
    }
}
