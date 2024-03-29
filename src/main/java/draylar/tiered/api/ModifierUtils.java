package draylar.tiered.api;

import net.levelz.access.PlayerStatsManagerAccess;
import net.levelz.stats.Skill;
import net.libz.util.SortList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import elocindev.tierify.Tierify;
import elocindev.tierify.compat.ItemBordersCompat;

public class ModifierUtils {

    /**
     * Returns the ID of a random attribute that is valid for the given {@link Item} in {@link Identifier} form.
     * <p>
     * If there is no valid attribute for the given {@link Item}, null is returned.
     *
     * @param item      {@link Item} to generate a random attribute for
     * @return          id of random attribute for item in {@link Identifier} form, or null if there are no valid options
     */
    @Nullable
    public static Identifier getRandomAttributeIDFor(@Nullable PlayerEntity playerEntity, Item item, boolean reforge) {
        List<Identifier> potentialAttributes = new ArrayList<>();
        List<Integer> attributeWeights = new ArrayList<>();
        // collect all valid attributes for the given item and their weights

        Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if (attribute.isValid(Registries.ITEM.getId(item)) && (attribute.getWeight() > 0 || reforge)) {
                potentialAttributes.add(new Identifier(attribute.getID()));
                attributeWeights.add(reforge ? attribute.getWeight() + 1 : attribute.getWeight());
            }
        });
        if (potentialAttributes.size() <= 0) {
            return null;
        }

        if (reforge && attributeWeights.size() > 2) {
            SortList.concurrentSort(attributeWeights, attributeWeights, potentialAttributes);
            int maxWeight = attributeWeights.get(attributeWeights.size() - 1);
            for (int i = 0; i < attributeWeights.size(); i++) {
                if (attributeWeights.get(i) > maxWeight / 2) {
                    attributeWeights.set(i, (int) (attributeWeights.get(i) * Tierify.CONFIG.reforgeModifier));
                }
            }
        }
        // LevelZ
        if (Tierify.isLevelZLoaded && playerEntity != null) {
            int newMaxWeight = Collections.max(attributeWeights);
            for (int i = 0; i < attributeWeights.size(); i++) {
                if (attributeWeights.get(i) > newMaxWeight / 3) {
                    attributeWeights.set(i, (int) (attributeWeights.get(i)
                            * (1.0f - Tierify.CONFIG.levelzReforgeModifier * ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.SMITHING))));
                }
            }
        }
        // Luck
        if (playerEntity != null) {
            int luckMaxWeight = Collections.max(attributeWeights);
            for (int i = 0; i < attributeWeights.size(); i++) {
                if (attributeWeights.get(i) > luckMaxWeight / 3) {
                    attributeWeights.set(i, (int) (attributeWeights.get(i) * (1.0f - Tierify.CONFIG.luckReforgeModifier * playerEntity.getLuck())));
                }
            }
        }

        if (potentialAttributes.size() > 0) {
            int totalWeight = 0;
            for (Integer weight : attributeWeights) {
                totalWeight += weight.intValue();
            }
            int randomChoice = new Random().nextInt(totalWeight);
            SortList.concurrentSort(attributeWeights, attributeWeights, potentialAttributes);

            for (int i = 0; i < attributeWeights.size(); i++) {
                if (randomChoice < attributeWeights.get(i)) {
                    return potentialAttributes.get(i);
                }
                randomChoice -= attributeWeights.get(i);
            }
            // If random choice didn't work
            return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
        } else
            return null;
    }

    /**
     * Returns a list of all attribute IDs that contain the specified quality in their identifier.
     *
     * @param quality       The quality substring to look for in the attribute identifiers (e.g., "mythic").
     * @return              List of attribute IDs that contain the specified quality substring.
     */
    public static List<Identifier> getAttributeIDsForQuality(String quality, Item item) {
        List<Identifier> matchingAttributes = new ArrayList<>();
        
        // iterate over all attributes and add matching ones to the list
        Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if (attribute.isValid(Registries.ITEM.getId(item)) && id.toString().contains(quality.toLowerCase())) {
                matchingAttributes.add(id);
            }
        });
        
        return matchingAttributes;
    }

/**
     * Returns a random attribute ID from the attributes that contain any of the specified quality substrings in their identifier,
     * considering the weights of the attributes.
     *
     * @param qualities A list of quality substrings to look for in the attribute identifiers (e.g., "mythic", "legendary").
     * @param item      The item for which the attribute is being searched.
     * 
     * @return A random attribute ID that contains one of the specified quality substrings, considering attribute weights, or null if none are found.
     */
    public static Identifier getRandomAttributeForQuality(List<String> qualities, Item item, boolean reforge) {
        List<Identifier> matchingAttributes = new ArrayList<>();
        List<Integer> matchingAttributeWeights = new ArrayList<>();

        // Collect all matching attributes for the given qualities and their weights
        Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if (attribute.isValid(Registries.ITEM.getId(item)) && qualities.stream().anyMatch(quality -> id.toString().contains(quality.toLowerCase())) && (attribute.getWeight() > 0 || reforge)) {
                matchingAttributes.add(id);
                matchingAttributeWeights.add(reforge ? attribute.getWeight() + 1 : attribute.getWeight());
            }
        });

        // Return null if no matching attributes are found
        if (matchingAttributes.isEmpty()) {
            return null;
        }

        // Calculate the total weight
        int totalWeight = matchingAttributeWeights.stream().mapToInt(Integer::intValue).sum();
        int randomIndex = new Random().nextInt(totalWeight);
        
        // Choose a random attribute based on weight
        for (int i = 0; i < matchingAttributes.size(); i++) {
            randomIndex -= matchingAttributeWeights.get(i);
            if (randomIndex < 0) {
                return matchingAttributes.get(i);
            }
        }

        // Fallback, should not be reached due to the weight calculation
        return null;
    }

    public static void setItemStackAttribute(Identifier potentialAttributeID, ItemStack stack) {
        if (potentialAttributeID != null) {

            stack.getOrCreateSubNbt(Tierify.NBT_SUBTAG_KEY).putString(Tierify.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());

            stack.getOrCreateSubNbt("itemborders_colors").putString("top", ItemBordersCompat.getColorForIdentifier(potentialAttributeID));
            stack.getOrCreateSubNbt("itemborders_colors").putString("bottom", ItemBordersCompat.getColorForIdentifier(potentialAttributeID));

            HashMap<String, Object> nbtMap = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getNbtValues();

            // add durability nbt
            List<AttributeTemplate> attributeList = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new Identifier(potentialAttributeID.toString())).getAttributes();
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                    if (nbtMap == null) {
                        nbtMap = new HashMap<String, Object>();
                    }
                    nbtMap.put("durable", (double) Math.round(attributeList.get(i).getEntityAttributeModifier().getValue() * 100.0) / 100.0);
                    break;
                }
            }

            // add nbtMap
            if (nbtMap != null) {
                NbtCompound nbtCompound = stack.getNbt();
                for (HashMap.Entry<String, Object> entry : nbtMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // json list will get read as ArrayList class
                    // json map will get read as linkedtreemap
                    // json integer is read by gson -> always double
                    if (value instanceof String) {
                        nbtCompound.putString(key, (String) value);
                    } else if (value instanceof Boolean) {
                        nbtCompound.putBoolean(key, (boolean) value);
                    } else if (value instanceof Double) {
                        if ((double) Math.abs((double) value) % 1.0 < 0.0001D) {
                            nbtCompound.putInt(key, (int) Math.round((double) value));
                        } else {
                            nbtCompound.putDouble(key, Math.round((double) value * 100.0) / 100.0);
                        }
                    }

                }

                stack.setNbt(nbtCompound);
            }
        }
    }


    public static void setItemStackAttribute(@Nullable PlayerEntity playerEntity, ItemStack stack, boolean reforge, ItemStack reforgeMaterial) {
        if (reforge && reforgeMaterial != null) {
            List<String> qualities = null;

            if (reforgeMaterial.isIn(TieredItemTags.TIER_1_ITEM)) {
                qualities = Tierify.CONFIG.tier_1_qualities;
            } else if (reforgeMaterial.isIn(TieredItemTags.TIER_2_ITEM)) {
                qualities = Tierify.CONFIG.tier_2_qualities;
            } else if (reforgeMaterial.isIn(TieredItemTags.TIER_3_ITEM)) {
                qualities = Tierify.CONFIG.tier_3_qualities;
            }

            if (qualities != null) {
                Identifier possibleAttribute = getRandomAttributeForQuality(qualities, stack.getItem(), reforge);
                if (possibleAttribute != null) {
                    setItemStackAttribute(possibleAttribute, stack);
                    return;
                }
            }
        }

        setItemStackAttribute(playerEntity, stack, reforge);
    }

    public static void setItemStackAttribute(@Nullable PlayerEntity playerEntity, ItemStack stack, boolean reforge) {
        if (stack.getSubNbt(Tierify.NBT_SUBTAG_KEY) == null) {
            setItemStackAttribute(ModifierUtils.getRandomAttributeIDFor(playerEntity, stack.getItem(), reforge), stack);   
        }
    }

    public static void removeItemStackAttribute(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {

            Identifier tier = new Identifier(itemStack.getOrCreateSubNbt(Tierify.NBT_SUBTAG_KEY).getString(Tierify.NBT_SUBTAG_DATA_KEY));
            if (Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier) != null) {
                HashMap<String, Object> nbtMap = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier).getNbtValues();
                List<String> nbtKeys = new ArrayList<String>();
                if (nbtMap != null) {
                    nbtKeys.addAll(nbtMap.keySet().stream().toList());
                }

                List<AttributeTemplate> attributeList = Tierify.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier).getAttributes();
                for (int i = 0; i < attributeList.size(); i++) {
                    if (attributeList.get(i).getAttributeTypeID().equals("tiered:generic.durable")) {
                        nbtKeys.add("durable");
                        break;
                    }
                }

                if (!nbtKeys.isEmpty()) {
                    for (int i = 0; i < nbtKeys.size(); i++) {
                        if (!nbtKeys.get(i).equals("Damage")) {
                            itemStack.getNbt().remove(nbtKeys.get(i));
                        }
                    }
                }
            }
            itemStack.removeSubNbt(Tierify.NBT_SUBTAG_KEY);
        }
    }

    @Nullable
    public static Identifier getAttributeID(ItemStack itemStack) {
        if (itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY) != null) {
            return new Identifier(itemStack.getSubNbt(Tierify.NBT_SUBTAG_KEY).getString(Tierify.NBT_SUBTAG_DATA_KEY));
        }
        return null;
    }

}