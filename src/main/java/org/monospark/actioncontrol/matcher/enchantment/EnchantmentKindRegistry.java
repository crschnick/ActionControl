package org.monospark.actioncontrol.matcher.enchantment;

import java.util.Optional;

import org.monospark.actioncontrol.matcher.KindRegistry;
import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.Enchantment;

public final class EnchantmentKindRegistry extends KindRegistry<EnchantmentKind> {

    @Override
    protected void initRegistry() {
        for (Enchantment enchantment : Sponge.getRegistry().getAllOf(CatalogTypes.ENCHANTMENT)) {
            for (int i = 1; i <= enchantment.getMaximumLevel(); i++) {
                allKinds.add(new EnchantmentKind(enchantment, i));
            }
        }
    }

    public Optional<EnchantmentKind> getKind(String name, int level) {
        return getAllKinds().stream()
                .filter(e -> e.getEnchantment().getId().equals(name) && e.getLevel() == level)
                .findFirst();
    }
}
