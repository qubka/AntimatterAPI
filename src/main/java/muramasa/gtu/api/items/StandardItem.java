package muramasa.gtu.api.items;

import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.TileEntityMaterial;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StandardItem extends Item implements IGregTechObject, IModelOverride {

    protected String id, tooltip = "";
    protected boolean enabled = true;
    protected Set<ItemTag> tags = new HashSet<>();

    public StandardItem(String id) {
        this.id = id;
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_ITEMS);
        GregTechAPI.register(this);
    }

    public StandardItem(String id, String tooltip) {
        this(id);
        this.tooltip = tooltip;
    }

    public StandardItem tags(ItemTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || Configs.DATA.ENABLE_ALL_MATERIAL_ITEMS;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return Utils.trans("item.standard." + getId() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(this.tooltip);
        if (Utils.hasNoConsumeTag(stack)) {
            tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
        }
        if (Data.DebugScanner.equals(this)) {
            tooltip.add("Blocks: " + GregTechAPI.BLOCKS.size());
            tooltip.add("Machines: " + Machines.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH).size());
            tooltip.add("Ores: " + MaterialType.ORE.getMats().size());
            tooltip.add("Ores Small: " + MaterialType.ORE_SMALL.getMats().size());
            tooltip.add("Storage: " + MaterialType.BLOCK.getMats().size());
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
        if (tile != null) {
            if (Data.DebugScanner.isEqual(stack)) {
                if (tile instanceof TileEntityMachine) {
                    if (tile instanceof TileEntityMultiMachine) {
                        if (!world.isRemote) {
                            if (!((TileEntityMultiMachine) tile).isStructureValid()) {
                                ((TileEntityMultiMachine) tile).checkStructure();
                            }
                        }
                        ((TileEntityMultiMachine) tile).checkRecipe();
                    } else if (tile instanceof TileEntityHatch) {
//                        MachineFluidHandler handler = ((TileEntityHatch) tile).getFluidHandler();
//                        if (handler != null) {
//                            System.out.println(handler.toString());
//                        }
                    } /*else if (tile instanceof TileEntityItemFluidMachine) {
                        MachineFluidHandler fluidHandler = ((TileEntityItemFluidMachine) tile).getFluidHandler();
                        for (FluidStack fluid : fluidHandler.getInputs()) {
                            System.out.println(fluid.getLocalizedName() + " - " + fluid.amount);
                        }
                        tile.markDirty();
                    }*/
                } else if (tile instanceof TileEntityPipe) {
                    player.sendMessage(new TextComponentString("C: " + ((TileEntityPipe) tile).getConnections() + (((TileEntityPipe) tile).getConnections() > 63 ? " (Culled)" : " (Non Culled)")));
                } else if (tile instanceof TileEntityMaterial) {
                    if (!world.isRemote) {
                        TileEntityMaterial ore = (TileEntityMaterial) tile;
                        player.sendMessage(new TextComponentString(ore.getMaterial().getId()));
                    }
                }
            }
        } else {
            if (Data.DebugScanner.isEqual(stack)) {
                if (!world.isRemote) {
                    //Data.RUBBER_SAPLING.generateTree(world, pos, Ref.RNG);
                    RecipeMap.dumpHashCollisions();
                }
            }
        }
        return EnumActionResult.FAIL; //TODO FAIL?
    }

//    public ItemType required(String... mods) {
//        for (int i = 0; i < mods.length; i++) {
//            if (!Utils.isModLoaded(mods[i])) {
//                enabled = false;
//                break;
//            }
//        }
//        return this;
//    }
//
//    public ItemType optional(String... mods) {
//        enabled = false;
//        for (int i = 0; i < mods.length; i++) {
//            if (Utils.isModLoaded(mods[i])) {
//                enabled = true;
//                break;
//            }
//        }
//        return this;
//    }

    public boolean isEqual(ItemStack stack) {
        return stack.getItem() == this;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return GregTechAPI.getCoverFromCatalyst(stack) != null;
    }

    public ItemStack get(int count) {
        //TODO replace consumeTag with flag system
        if (count == 0) return Utils.addNoConsumeTag(new ItemStack(this, 1));
        return new ItemStack(this, count);
    }

    @Override
    public ItemStack asItemStack() {
        return get(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":standard_item", "id=" + id));
    }
}
