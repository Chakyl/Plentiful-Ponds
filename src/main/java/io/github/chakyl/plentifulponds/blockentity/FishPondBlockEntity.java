package io.github.chakyl.plentifulponds.blockentity;

import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.menu.SimpleDataSlots;
import io.github.chakyl.plentifulponds.ModElements;
import io.github.chakyl.plentifulponds.block.FishPondBlock;
import io.github.chakyl.plentifulponds.data.Pond;
import io.github.chakyl.plentifulponds.data.PondRegistry;
import io.github.chakyl.plentifulponds.data.codec.PondDrop;
import io.github.chakyl.plentifulponds.data.codec.PondQuest;
import io.github.chakyl.plentifulponds.item.RoeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Collection;
import java.util.function.Consumer;

public class FishPondBlockEntity extends BlockEntity implements TickingBlockEntity, SimpleDataSlots.IDataAutoRegister {
    protected final int FISH_POND_DAY_TIME_TRIGGER = 40;
    protected Item fishType;
    protected int population;
    protected int reproductionDay;
    protected int maxPopulation;
    protected int nonNativeFish;
    protected int questId;
    protected boolean questActive;
    protected boolean hasOutput;
    protected boolean valid = true;

    protected final SimpleDataSlots data = new SimpleDataSlots();

    public FishPondBlockEntity(BlockPos pos, BlockState state) {
        super(ModElements.BlockEntities.FISH_POND, pos, state);
        this.data.addData(() -> this.population, v -> this.population = v);
        this.data.addData(() -> this.maxPopulation, v -> this.maxPopulation = v);
        this.data.addData(() -> this.questId, v -> this.questId = v);
        this.data.addData(() -> this.questActive, v -> this.questActive = v);
        this.data.addData(() -> this.valid, v -> this.valid = v);

    }


    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        long dayTime = level.dayTime();
        int morningModulo = (int) (dayTime % 24000);
        if (!hasFish()) return;
        if (dayTime % 20 == 0) {
            boolean validResult = validatePond(level, pos, state);
            if (this.valid != validResult) {
                this.valid = validResult;
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                return;
            }
        }

        if (dayTime % 20 == 0 &&
                morningModulo >= FISH_POND_DAY_TIME_TRIGGER &&
                morningModulo < FISH_POND_DAY_TIME_TRIGGER + 20
        ) {
            if (this.valid) {
                Pond pond = this.getPond();
                if (this.population > 1) {
                    this.hasOutput = true;
                    this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                }
                if (this.maxPopulation != pond.maxPopulation() && !this.questActive && population == maxPopulation) {
                    chooseFishPondQuest(level, pond);
                } else if (population < maxPopulation) {
                    if (reproductionDay >= pond.reproductionRate()) {
                        this.population++;
                        this.reproductionDay = 0;
                        sendParticles(ParticleTypes.HEART, level, pos);
                        this.setChanged();
                    } else {
                        this.reproductionDay++;
                    }
                } else if (population > maxPopulation || population > pond.maxPopulation()) {
                    this.population = maxPopulation;
                }

                if (population == maxPopulation && nonNativeFish > 0) {
                    if (reproductionDay >= pond.reproductionRate()) {
                        this.nonNativeFish--;
                        this.reproductionDay = 0;
                    } else {
                        this.reproductionDay++;
                    }
                }
                this.setChanged();
            }
        }
    }

    public boolean validatePond(Level level, BlockPos pos, BlockState state) {
        BlockPos pondStart;
        BlockPos pondEnd;
        BlockPos adjacent1;
        BlockPos adjacent2;
        // Programming is my passion
        switch (state.getValue(FishPondBlock.FACING)) {
            case NORTH -> {
                pondStart = pos.offset(1, 0, 4);
                pondEnd = pos.offset(-1, 0, 1);
                adjacent1 = pos.offset(1, 0, 0);
                adjacent2 = pos.offset(-1, 0, 0);
            }
            case SOUTH -> {
                pondStart = pos.offset(-1, 0, -4);
                pondEnd = pos.offset(1, 0, -1);
                adjacent1 = pos.offset(1, 0, 0);
                adjacent2 = pos.offset(-1, 0, 0);
            }
            case EAST -> {
                pondStart = pos.offset(-1, 0, 1);
                pondEnd = pos.offset(-4, 0, -1);
                adjacent1 = pos.offset(0, 0, 1);
                adjacent2 = pos.offset(0, 0, -1);
            }
            case WEST -> {
                pondStart = pos.offset(1, 0, -1);
                pondEnd = pos.offset(4, 0, 1);
                adjacent1 = pos.offset(0, 0, 1);
                adjacent2 = pos.offset(0, 0, -1);
            }
            default -> {
                return false;
            }
        }
        if (isPondAt(level, adjacent1) || isPondAt(level, adjacent2)) return false;
        Fluid pondWater = Fluids.WATER;
        if (this.fishType != null) pondWater = this.getPond().pondFluid();
        int fluidAmount = 0;
        boolean isWater = pondWater.isSame(Fluids.WATER);

        for (BlockPos scanPos : BlockPos.betweenClosed(pondStart, pondEnd)) {
            if (!level.isLoaded(scanPos)) {
                return false;
            }
            BlockState scanState = level.getBlockState(scanPos);
            if (scanState.getFluidState().getType().isSame(pondWater)) {
                fluidAmount++;
            } else if (isWater && scanState.is(ModElements.Tags.POND_WATER)) {
                fluidAmount++;
            }
        }

        return fluidAmount == 12;
    }

    private boolean isPondAt(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        return level.getBlockState(pos).is(ModElements.Blocks.FISH_POND);
    }

    private void sendParticles(SimpleParticleType type, Level level, BlockPos pos) {
        // TODO: make better
        ((ServerLevel) level).sendParticles(
                type,
                pos.getX(), pos.getY(), pos.getZ(),
                1,
                0.5, 1, 0.5,
                0.2
        );
    }

    private int getInitialMaxPopulation() {
        Pond pond = this.getPond();
        if (pond == null) return 0;
        if (!pond.quests().isEmpty()) {
            return pond.quests().getFirst().population();
        }
        return pond.maxPopulation();
    }

    private int getQuestMaxPopulation() {
        Pond pond = this.getPond();
        if (pond == null) return 0;
        boolean chooseNext = false;
        if (!pond.quests().isEmpty()) {
            for (PondQuest quest : pond.quests()) {
                if (quest.population() == this.maxPopulation) {
                    chooseNext = true;
                } else if (chooseNext) return quest.population();
            }
        }
        return pond.maxPopulation();
    }

    private void chooseFishPondQuest(Level level, Pond pond) {
        this.questActive = true;
        this.hasOutput = false;

        for (PondQuest quest : pond.quests()) {
            if (quest.population() == this.maxPopulation) {
                this.questId = Mth.randomBetweenInclusive(level.getRandom(), 0, quest.requestedItems().size() - 1);
                return;
            }
        }
    }

    public ItemStack getRequestedItems() {
        Pond pond = this.getPond();
        for (PondQuest quest : pond.quests()) {
            if (quest.population() == this.maxPopulation) {
                if (quest.requestedItems().size() < this.questId) {
                    return quest.requestedItems().get(quest.requestedItems().size() - 1).copy();
                }
                return quest.requestedItems().get(this.questId).copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public void handleQuestSubmission(Player player, InteractionHand hand, ItemStack stack) {
        ItemStack questContent = getRequestedItems();
        if (!stack.isEmpty() && !questContent.isEmpty() && stack.getItem() == questContent.getItem()) {
//            TODO: pond_house_five int checkedCount = player.stages.has("pond_house_five")
//                    ? Math.round(questContent.count / 2)
//                    : questContent.count;
            int checkedCount = questContent.getCount();
            if (stack.getCount() >= checkedCount) {

                sendParticles(ParticleTypes.WAX_OFF, level, this.getBlockPos());
                this.questActive = false;
                this.questId = -1;
                this.maxPopulation = this.getQuestMaxPopulation();
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                player.sendSystemMessage(Component.translatable("block.plentifulponds.fish_pond.fish_quest.complete").withStyle(ChatFormatting.GREEN));
                if (!player.isCreative()) stack.shrink(checkedCount);
            } else {
                player.sendSystemMessage(Component.translatable("block.plentifulponds.fish_pond.fish_quest.partial", checkedCount - stack.getCount()).withStyle(ChatFormatting.RED));
            }
        }
    }

    public ItemStack handleFishExtraction() {
        int extractionAmount = 1; // TODO: Mitosis
        int naturalPopulation = this.population - this.nonNativeFish;
        BlockPos pondPos = this.getBlockPos();
        ItemStack result = ItemStack.EMPTY;
        if (this.population <= 0) return result;
        if (false && naturalPopulation > 0) { // TODO: Hot Hands
            this.level.playSound(null, pondPos.getX(), pondPos.getY(), pondPos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (Math.random() < 0.25) {
                result = Items.COAL.getDefaultInstance();
            } else {
                result = this.getPond().cookedVariant().getDefaultInstance().copy();
            }
        } else {
            result = this.fishType.getDefaultInstance().copy();
        }
        if (!result.isEmpty()) {
            this.level.playSound(null, pondPos.getX(), pondPos.getY(), pondPos.getZ(), SoundEvents.DOLPHIN_SPLASH, SoundSource.BLOCKS, 0.2f, 1.0f);
            this.population--;
            if (naturalPopulation <= 0 && nonNativeFish > 0) {
                this.nonNativeFish--;
            }
        }
        result.setCount(extractionAmount);
        return result.copy();
    }

    public void handleFishInsertion(Player player, InteractionHand hand, ItemStack stack) {
        if (PondRegistry.INSTANCE.isPondFish(stack.getItem())) {
            BlockPos pondPos = this.getBlockPos();
            if (this.fishType == null) {
                this.fishType = stack.copy().getItem();
                this.nonNativeFish = 1;
                this.population = 1;
                this.maxPopulation = getInitialMaxPopulation();
                if (!player.isCreative()) stack.shrink(1);
                player.swing(hand);
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                this.level.playSound(null, pondPos.getX(), pondPos.getY(), pondPos.getZ(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else if (this.population < this.maxPopulation) {
                if (stack.is(this.fishType.asItem())) {
                    this.nonNativeFish++;
                    this.population++;
                    if (!player.isCreative()) stack.shrink(1);
                    player.swing(hand);
                    this.setChanged();
                    this.level.playSound(null, pondPos.getX(), pondPos.getY(), pondPos.getZ(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    player.sendSystemMessage(Component.translatable("block.plentifulponds.fish_pond.not_same"));
                }
            }
        }
    }

    public Collection<ItemStack> handlePondHarvest() {
        NonNullList<ItemStack> drops = NonNullList.create();
        if (!this.hasOutput || this.population <= 0) return drops;
        Pond pond = this.getPond();
        int maxRoe = pond.maxRoe(); // TODO: Modify by player attributes
        int calculatedRoeCount = Math.min(this.population, Mth.randomBetweenInclusive(this.level.random, 1, maxRoe));
        ItemStack roeStack = ModElements.Items.ROE.value().getDefaultInstance();
        RoeItem.setStoredFish(roeStack, pond);
        roeStack.setCount(calculatedRoeCount);
        drops.add(roeStack);
        if (pond.pondDrops() != null && !pond.pondDrops().isEmpty()) {
            for (PondDrop drop : pond.pondDrops()) {
                if (this.population >= drop.minPopulation()) {
                    float chance = drop.chance();
                    // TODO: handle Scum Collector and Sea Biscuit
                    if (Math.random() <= chance) {
                        // Rewards scale to amount of fish population relative to when reward starts spawning
                        int rewardCount = Mth.floor(drop.drop().getCount() * ((float) (population - drop.minPopulation()) / (pond.maxPopulation() - drop.minPopulation())));
                        if (this.population == pond.maxPopulation()) rewardCount = drop.drop().getCount();
                        ItemStack reward = drop.drop().copy();
                        reward.setCount(rewardCount);
                        drops.add(reward.copy());
                    }
                }
            }
        }
        this.hasOutput = false;
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        return drops;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.saveAdditional(tag, regs);
        if (this.fishType != null) {
            tag.putString("fish_type", BuiltInRegistries.ITEM.getKey(this.fishType).toString());
        }
        tag.putInt("population", this.population);
        tag.putInt("reproduction_day", this.reproductionDay);
        tag.putInt("max_population", this.maxPopulation);
        tag.putInt("non_native_fish", this.nonNativeFish);
        tag.putInt("quest_id", this.questId);
        tag.putBoolean("quest_active", this.questActive);
        tag.putBoolean("has_output", this.hasOutput);
        tag.putBoolean("valid", this.valid);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider regs) {
        super.loadAdditional(tag, regs);
        if (tag.contains("fish_type")) {
            ResourceLocation itemId = ResourceLocation.tryParse(tag.getString("fish_type"));
            this.fishType = itemId != null ? BuiltInRegistries.ITEM.get(itemId) : null;
        } else {
            this.fishType = null;
        }

        this.population = tag.getInt("population");
        this.reproductionDay = tag.getInt("reproduction_day");
        this.maxPopulation = tag.getInt("max_population");
        this.nonNativeFish = tag.getInt("non_native_fish");
        this.questId = tag.getInt("quest_id");
        this.questActive = tag.getBoolean("quest_active");
        this.hasOutput = tag.getBoolean("has_output");
        this.valid = tag.getBoolean("valid");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean hasFish() {
        return this.fishType != null && this.getPond() != null;
    }

    public Item getFishType() {
        return this.fishType;
    }

    public void clearFishType() {
        this.fishType = null;
    }

    public int getPopulation() {
        return this.population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getMaxPopulation() {
        return this.maxPopulation;
    }

    public void setMaxPopulation(int maxPopulation) {
        this.maxPopulation = maxPopulation;
    }

    public int getNonNativeFish() {
        return this.nonNativeFish;
    }

    public void setNonNativeFish(int nonNativeFish) {
        this.nonNativeFish = nonNativeFish;
    }

    public int getQuestId() {
        return this.questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public boolean isQuestActive() {
        return this.questActive;
    }

    public void setQuestActive(boolean questActive) {
        this.questActive = questActive;
    }

    public boolean hasOutput() {
        return this.hasOutput;
    }

    public boolean isValid() {
        return this.valid;
    }

    public Pond getPond() {
        return PondRegistry.INSTANCE.getForItem(this.fishType);
    }

    @Override
    public void registerSlots(Consumer<DataSlot> consumer) {
        this.data.register(consumer);
    }

}