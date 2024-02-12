package draylar.tiered.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import draylar.tiered.Tiered;
import draylar.tiered.api.AttributeTemplate;
import draylar.tiered.api.ModifierUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandInit {

    private static final List<String> TIER_LIST = List.of("common", "uncommon", "rare", "epic", "legendary", "mythic");

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("tiered").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.literal("tier").then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("common").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 0);
            })).then(CommandManager.literal("uncommon").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 1);
            })).then(CommandManager.literal("rare").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 2);
            })).then(CommandManager.literal("epic").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 3);
            })).then(CommandManager.literal("legendary").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 4);
            })).then(CommandManager.literal("mythic").executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 5);
            })))).then(CommandManager.literal("untier").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), -1);
            }))));
        });
    }

    // 0: common; 1: uncommon; 2: rare; 3: epic; 4: legendary; 5: mythic
    private static int executeCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int tier) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();
        // loop over players
        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = var3.next();
            ItemStack itemStack = serverPlayerEntity.getMainHandStack();

            if (itemStack.isEmpty()) {
                source.sendFeedback(() -> Text.translatable("commands.tiered.failed", serverPlayerEntity.getDisplayName()), true);
                continue;
            }

            if (tier == -1) {
                if (itemStack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
                    ModifierUtils.removeItemStackAttribute(itemStack);

                    source.sendFeedback(() -> Text.translatable("commands.tiered.untier", itemStack.getItem().getName(itemStack).getString(), serverPlayerEntity.getDisplayName()), true);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.tiered.untier_failed", itemStack.getItem().getName(itemStack).getString(), serverPlayerEntity.getDisplayName()), true);
                }
            } else {
                ArrayList<Identifier> potentialAttributes = new ArrayList<Identifier>();
                Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
                    if (attribute.isValid(Registries.ITEM.getId(itemStack.getItem()))) {
                        potentialAttributes.add(new Identifier(attribute.getID()));
                    }
                });
                if (potentialAttributes.size() <= 0) {
                    source.sendFeedback(() -> Text.translatable("commands.tiered.tiering_failed", itemStack.getItem().getName(itemStack).getString(), serverPlayerEntity.getDisplayName()), true);
                    continue;
                } else {

                    List<Identifier> potentialTier = new ArrayList<Identifier>();
                    for (int i = 0; i < potentialAttributes.size(); i++) {
                        if (potentialAttributes.get(i).getPath().contains(TIER_LIST.get(tier))) {
                            if (TIER_LIST.get(tier).equals("common") && potentialAttributes.get(i).getPath().contains("uncommon")) {
                                continue;
                            }
                            potentialTier.add(potentialAttributes.get(i));
                        }
                    }

                    if (potentialTier.size() <= 0) {
                        source.sendFeedback(() -> Text.translatable("commands.tiered.tiering_failed", itemStack.getItem().getName(itemStack).getString(), serverPlayerEntity.getDisplayName()), true);
                        continue;
                    } else {

                        ModifierUtils.removeItemStackAttribute(itemStack);

                        Identifier attribute = potentialTier.get(serverPlayerEntity.getWorld().getRandom().nextInt(potentialTier.size()));
                        if (attribute != null) {
                            itemStack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, attribute.toString());

                            HashMap<String, Object> nbtMap = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(attribute.toString())).getNbtValues();

                            // add durability nbt
                            List<AttributeTemplate> attributeList = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(attribute.toString())).getAttributes();
                            for (int i = 0; i < attributeList.size(); i++)
                                if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                                    if (nbtMap == null)
                                        nbtMap = new HashMap<String, Object>();
                                    nbtMap.put("durable", (double) Math.round(attributeList.get(i).getEntityAttributeModifier().getValue() * 100.0) / 100.0);
                                    break;
                                }
                            // add nbtMap
                            if (nbtMap != null) {
                                NbtCompound nbtCompound = itemStack.getNbt();
                                for (HashMap.Entry<String, Object> entry : nbtMap.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();

                                    // json list will get read as ArrayList class
                                    // json map will get read as linkedtreemap
                                    // json integer is read by gson -> always double
                                    if (value instanceof String)
                                        nbtCompound.putString(key, (String) value);
                                    else if (value instanceof Boolean)
                                        nbtCompound.putBoolean(key, (boolean) value);
                                    else if (value instanceof Double) {
                                        if ((double) value % 1.0 < 0.0001D)
                                            nbtCompound.putInt(key, (int) Math.round((double) value));
                                        else
                                            nbtCompound.putDouble(key, Math.round((double) value * 100.0) / 100.0);
                                    }
                                }
                                itemStack.setNbt(nbtCompound);
                            }
                            source.sendFeedback(() -> Text.translatable("commands.tiered.tier", itemStack.getItem().getName(itemStack).getString(), serverPlayerEntity.getDisplayName()), true);
                        }
                    }
                }
            }
        }
        return 1;
    }
}
