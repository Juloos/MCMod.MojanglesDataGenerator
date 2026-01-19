package juloos.mojanglesdatagenerator.mixin;

import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(FontManager.class)
public abstract class MojanglesFunction_FontManagerMixin {
    @Shadow @Final private Map<Identifier, FontSet> fontSets;

    @Inject(
            method = "apply",
            at = @At("TAIL")
    )
    private void getFontAndPrint(CallbackInfo ci) {
        System.out.println("Starting to write all glyphs");
        HashMap<Integer, Tuple<Float, Float>> mojangles = new HashMap<>();
        HashMap<Integer, Tuple<Float, Float>> unifont = new HashMap<>();
        this.fontSets.get(Minecraft.DEFAULT_FONT).activeProviders.forEach(provider -> {
            provider.getSupportedGlyphs().forEach(glyphCode -> {
                if (Character.toString(glyphCode).equals("\n") || Character.toString(glyphCode).equals("\r"))
                    return;
                GlyphInfo glyph = provider.getGlyph(glyphCode).info();
                if (!(provider instanceof UnihexProvider))
                    mojangles.putIfAbsent(glyphCode, new Tuple<>(glyph.getAdvance(false), glyph.getAdvance(true)));
                unifont.putIfAbsent(glyphCode, new Tuple<>(glyph.getAdvance(false), glyph.getAdvance(true)));
            });
        });
        generate_font_function("mojangles", mojangles, 1.0f);
        generate_font_function("unifont", unifont, 0.5f);
        System.out.println("Finished writing all glyphs (" + mojangles.size() + " mojangles, " + unifont.size() + " unifont)");
        Minecraft.getInstance().stop();
    }

    @Unique
    void generate_font_function(String mcfunction, HashMap<Integer, Tuple<Float, Float>> font, float expected_bold_incr) {
        try {
            FileWriter file1 = new FileWriter(mcfunction + ".mcfunction");
            file1.write("data modify storage juloos:data char merge value {widths:{");
            font.forEach((glyphCode, pair) -> {
                String k = Character.toString(glyphCode);
                k = switch (k) {
                    case "\"" -> "\\\"";
                    case "\\" -> "\\\\";
                    default -> k;
                };
                String v = pair.getA().toString().replaceAll("\\.0", "");
                try {
                    file1.write("\"" + k + "\":" + v + ",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            file1.write("}}\n");
            file1.close();

//            AtomicInteger acc = new AtomicInteger();
            FileWriter file2 = new FileWriter(mcfunction + "_bold.mcfunction");
            file2.write("data modify storage juloos:data char merge value {bold_widths:{");
            font.forEach((glyphCode, pair) -> {
                String k = Character.toString(glyphCode);
                k = switch (k) {
                    case "\"" -> "\\\"";
                    case "\\" -> "\\\\";
                    default -> k;
                };
                String v = pair.getB().toString().replaceAll("\\.0", "");
                try {
//                    if (pair.getB() - pair.getA() != expected_bold_incr) {
//                        System.out.println("\"" + k + "\" bold width mismatch: " + (pair.getB() - pair.getA()) + " vs " + expected_bold_incr);
//                        acc.getAndIncrement();
//                    }
                    file2.write("\"" + k + "\":" + v + ",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            file2.write("}}\n");
            file2.close();
//            if (acc.get() > 0)
//                System.out.println("Total bold width mismatches: " + acc.get() + " / " + font.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
