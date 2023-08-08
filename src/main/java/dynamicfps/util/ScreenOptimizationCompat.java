package dynamicfps.util;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.CustomValue.CvObject;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;

public class ScreenOptimizationCompat {
    private static Set<String> optedInScreens = new HashSet<>();
    private static Set<String> optedOutScreens = new HashSet<>();

    static {
        FabricLoader.getInstance().getAllMods().forEach(ScreenOptimizationCompat::parseModMetadata);
    }

    public static boolean isOptedIn(String className) {
        return optedInScreens.contains(className);
    }

    public static boolean isOptedOut(String className) {
        return optedOutScreens.contains(className);
    }

    private static void parseModMetadata(ModContainer mod) {
        CvObject optimizedScreens;
        ModMetadata data = mod.getMetadata();

        try {
            var root = data.getCustomValue("dynamic_fps").getAsObject();
            optimizedScreens = root.get("optimized_screens").getAsObject();
        } catch (ClassCastException | NullPointerException e) {
            return;  // Object is either missing or is of an invalid type
        }

        var resolver = FabricLoader.getInstance().getMappingResolver();

        addToSet(resolver, optimizedScreens.get("enabled"), optedInScreens);
        addToSet(resolver, optimizedScreens.get("disabled"), optedOutScreens);
    }

    private static void addToSet(MappingResolver resolver, @Nullable CustomValue values, Set<String> set) {
        if (values == null || values.getType() != CvType.ARRAY) {
            return;
        }

        values.getAsArray().forEach(value -> {
            if (value.getType() == CvType.STRING) {
                // Translate from intermediary to runtime names for vanilla screens
                set.add(resolver.mapClassName("intermediary", value.getAsString()));
            }
        });
    }
}
