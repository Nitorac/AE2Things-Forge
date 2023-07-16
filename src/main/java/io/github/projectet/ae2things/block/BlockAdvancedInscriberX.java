package io.github.projectet.ae2things.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.block.entity.BEAdvancedInscriberX;
import io.github.projectet.ae2things.gui.advancedInscriber.AdvancedInscriberXMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class BlockAdvancedInscriberX extends AEBaseEntityBlock<BEAdvancedInscriberX> {

    public BlockAdvancedInscriberX(Properties settings) {
        super(settings);
        settings.requiresCorrectToolForDrops();
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, BEAdvancedInscriberX be) {
        return currentState.setValue(WORKING, be.isWorking());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AE2Things.ADVANCED_INSCRIBER_X_BE.get().create(pos, state);
    }

    @Override
    public InteractionResult onActivated(final Level level, final BlockPos pos, final Player p,
            final InteractionHand hand,
            final @Nullable ItemStack heldItem, final BlockHitResult hit) {
        if (!InteractionUtil.isInAlternateUseMode(p)) {
            final BEAdvancedInscriberX ai = (BEAdvancedInscriberX) level.getBlockEntity(pos);
            if (ai != null) {
                if (!level.isClientSide()) {
                    MenuOpener.open(AdvancedInscriberXMenu.ADVANCED_INSCRIBER_SHT, p,
                            MenuLocators.forBlockEntity(ai));
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

}
