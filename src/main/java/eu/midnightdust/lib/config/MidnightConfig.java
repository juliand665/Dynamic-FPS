/*
 * MIT License
 *
 * Copyright (c) 2020 MidnightDust
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.midnightdust.lib.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// MidnightConfig v1.0.4
// Single class config library - feel free to copy!
// Changelog:
// - 1.0.4:
// - Number field length is now configurable
// - Fixed number fields being empty
// - 1.0.3:
// - Text field length is now configurable
// - Better separation of client and server
// - 1.0.2:
// - Update to 21w20a
// - 1.0.1:
// - Fixed buttons not working in fullscreen
// - 1.0.0:
// - The config screen no longer shows the entries of all instances of MidnightConfig
// - Compatible with servers!
// - Scrollable!
// - Comment support!
// - Fresh New Design

/** Based on https://github.com/Minenash/TinyConfig
 *  Credits to Minenash */

@SuppressWarnings("unchecked")
public class MidnightConfig {
  public static boolean useTooltipForTitle = true; // Render title as tooltip or as simple text

  private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
  private static final Pattern DECIMAL_ONLY = Pattern.compile("-?([\\d]+\\.?[\\d]*|[\\d]*\\.?[\\d]+|\\.)");

  private static final List<EntryInfo> entries = new ArrayList<>();

  protected static class EntryInfo {
    Field field;
    Object widget;
    int width;
    int max;
    Map.Entry<TextFieldWidget,Text> error;
    Object defaultValue;
    Object value;
    String tempValue;
    boolean inLimits = true;
    String id;
  }

  public static final Map<String,Class<?>> configClass = new HashMap<>();
  private static Path path;

  private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).excludeFieldsWithModifiers(Modifier.PRIVATE).addSerializationExclusionStrategy(new HiddenAnnotationExclusionStrategy()).setPrettyPrinting().create();

  public static void init(String modid, Class<?> config) {
    path = FabricLoader.getInstance().getConfigDir().resolve(modid + ".json");
    configClass.put(modid, config);

    for (Field field : config.getFields()) {
      EntryInfo info = new EntryInfo();
      if (field.isAnnotationPresent(Entry.class) || field.isAnnotationPresent(Comment.class))
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) initClient(modid, field, info);
      if (field.isAnnotationPresent(Entry.class))
        try {
          info.defaultValue = field.get(null);
        } catch (IllegalAccessException ignored) {}
    }
    try { gson.fromJson(Files.newBufferedReader(path), config); }
    catch (Exception e) { write(modid); }

    for (EntryInfo info : entries) {
      if (info.field.isAnnotationPresent(Entry.class))
        try {
          info.value = info.field.get(null);
          info.tempValue = info.value.toString();
        } catch (IllegalAccessException ignored) {
        }
    }
  }
  @Environment(EnvType.CLIENT)
  private static void initClient(String modid, Field field, EntryInfo info) {
    Class<?> type = field.getType();
    Entry e = field.getAnnotation(Entry.class);
    info.width = e != null ? e.width() : 0;
    info.field = field;
    info.id = modid;

    if (e != null) {
      if (type == int.class) textField(info, Integer::parseInt, INTEGER_ONLY, e.min(), e.max(), true);
      else if (type == double.class) textField(info, Double::parseDouble, DECIMAL_ONLY, e.min(), e.max(), false);
      else if (type == String.class) {
        info.max = e.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int) e.max();
        textField(info, String::length, null, Math.min(e.min(), 0), Math.max(e.max(), 1), true);
      } else if (type == boolean.class) {
        Function<Object, Text> func = value -> new LiteralText((Boolean) value ? "True" : "False").formatted((Boolean) value ? Formatting.GREEN : Formatting.RED);
        info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
          info.value = !(Boolean) info.value;
          button.setMessage(func.apply(info.value));
        }, func);
      } else if (type.isEnum()) {
        List<?> values = Arrays.asList(field.getType().getEnumConstants());
        Function<Object, Text> func = value -> new TranslatableText(modid + ".midnightconfig." + "enum." + type.getSimpleName() + "." + info.value.toString());
        info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
          int index = values.indexOf(info.value) + 1;
          info.value = values.get(index >= values.size() ? 0 : index);
          button.setMessage(func.apply(info.value));
        }, func);
      }
    }
    entries.add(info);
  }

  private static void textField(EntryInfo info, Function<String,Number> f, Pattern pattern, double min, double max, boolean cast) {
    boolean isNumber = pattern != null;
    info.widget = (BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) (t, b) -> s -> {
      s = s.trim();
      if (!(s.isEmpty() || !isNumber || pattern.matcher(s).matches())) return false;

      Number value = 0;
      boolean inLimits = false;
      System.out.println(((isNumber ^ s.isEmpty())));
      System.out.println(!s.equals("-") && !s.equals("."));
      info.error = null;
      if (!(isNumber && s.isEmpty()) && !s.equals("-") && !s.equals(".")) {
        value = f.apply(s);
        inLimits = value.doubleValue() >= min && value.doubleValue() <= max;
        info.error = inLimits? null : new AbstractMap.SimpleEntry<>(t, new LiteralText(value.doubleValue() < min ?
                "§cMinimum " + (isNumber? "value" : "length") + (cast? " is " + (int)min : " is " + min) :
                "§cMaximum " + (isNumber? "value" : "length") + (cast? " is " + (int)max : " is " + max)));
      }

      info.tempValue = s;
      t.setEditableColor(inLimits? 0xFFFFFFFF : 0xFFFF7777);
      info.inLimits = inLimits;
      b.active = entries.stream().allMatch(e -> e.inLimits);

      if (inLimits)
        info.value = isNumber? value : s;

      return true;
    };
  }

  public static void write(String modid) {
    path = FabricLoader.getInstance().getConfigDir().resolve(modid + ".json");
    try {
      if (!Files.exists(path)) Files.createFile(path);
      Files.write(path, gson.toJson(configClass.get(modid).getDeclaredConstructor().newInstance()).getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Environment(EnvType.CLIENT)
  public static Screen getScreen(Screen parent, String modid) {
    return new MidnightConfigScreen(parent, modid);
  }
  @Environment(EnvType.CLIENT)
  private static class MidnightConfigScreen extends Screen {

    protected MidnightConfigScreen(Screen parent, String modid) {
      super(new TranslatableText(modid + ".midnightconfig." + "title"));
      this.parent = parent;
      this.modid = modid;
      this.translationPrefix = modid + ".midnightconfig.";
    }
    private final String translationPrefix;
    private final Screen parent;
    private final String modid;
    private MidnightConfigListWidget list;

    // Real Time config update //
    @Override
    public void tick() {
      for (EntryInfo info : entries)
        try { info.field.set(null, info.value); }
        catch (IllegalAccessException ignored) {}
    }

    @Override
    protected void init() {
      super.init();

      this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> {
        try { gson.fromJson(Files.newBufferedReader(path), configClass.get(modid)); }
        catch (Exception e) { write(modid); }

        for (EntryInfo info : entries) {
          if (info.field.isAnnotationPresent(Entry.class)) {
            try {
              info.value = info.field.get(null);
              info.tempValue = info.value.toString();
            } catch (IllegalAccessException ignored) {
            }
          }
        }
        Objects.requireNonNull(client).openScreen(parent);
      }));

      ButtonWidget done = this.addDrawableChild(new ButtonWidget(this.width / 2 + 4, this.height - 28, 150, 20, ScreenTexts.DONE, (button) -> {
        for (EntryInfo info : entries)
          if (info.id.equals(modid)) {
            try {
              info.field.set(null, info.value);
            } catch (IllegalAccessException ignored) {}
          }
        write(modid);
        Objects.requireNonNull(client).openScreen(parent);
      }));

      this.list = new MidnightConfigListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
      this.addSelectableChild(this.list);
      for (EntryInfo info : entries) {
        if (info.id.equals(modid)) {
          TranslatableText name = new TranslatableText(translationPrefix + info.field.getName());
          ButtonWidget resetButton = new ButtonWidget(width - 155, 0, 40, 20, new LiteralText("Reset").formatted(Formatting.RED), (button -> {
            info.value = info.defaultValue;
            info.tempValue = info.value.toString();
            double scrollAmount = list.getScrollAmount();
            Objects.requireNonNull(client).openScreen(this);
            list.setScrollAmount(scrollAmount);
          }));

          if (info.widget instanceof Map.Entry) {
            Map.Entry<ButtonWidget.PressAction, Function<Object, Text>> widget = (Map.Entry<ButtonWidget.PressAction, Function<Object, Text>>) info.widget;
            if (info.field.getType().isEnum()) widget.setValue(value -> new TranslatableText(translationPrefix + "enum." + info.field.getType().getSimpleName() + "." + info.value.toString()));
            this.list.addButton(new ButtonWidget(width - 110, 0,100, 20, widget.getValue().apply(info.value), widget.getKey()),resetButton,name);
          } else if (info.widget != null) {
            TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 110, 0, 100, 20, null);
            widget.setMaxLength(info.width);
            widget.setText(info.tempValue);
            Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(widget, done);
            widget.setTextPredicate(processor);
            this.list.addButton(widget, resetButton, name);
          } else {
            ButtonWidget dummy = new ButtonWidget(-10, 0, 0, 0, Text.of(""), null);
            this.list.addButton(dummy,dummy,name);
          }
        }
      }

    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices);
      this.list.render(matrices, mouseX, mouseY, delta);

      int stringWidth = (int) (title.getString().length() * 2.75f);
      if (useTooltipForTitle) renderTooltip(matrices, title, width/2 - stringWidth, 27);
      else drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);

      for (EntryInfo info : entries) {
        if (info.id.equals(modid)) {
          if (list.getHoveredButton(mouseX,mouseY).isPresent()) {
            ClickableWidget buttonWidget = list.getHoveredButton(mouseX,mouseY).get();
            Text text = ButtonEntry.buttonsWithText.get(buttonWidget);
            TranslatableText name = new TranslatableText(this.translationPrefix + info.field.getName());
            String key = translationPrefix + info.field.getName() + ".tooltip";

            if (info.error != null && text.equals(name)) renderTooltip(matrices, info.error.getValue(), mouseX, mouseY);
            else if (I18n.hasTranslation(key) && text.equals(name)) {
              List<Text> list = new ArrayList<>();
              for (String str : I18n.translate(key).split("\n"))
                list.add(new LiteralText(str));
              renderTooltip(matrices, list, mouseX, mouseY);
            }
          }
        }
      }
      super.render(matrices,mouseX,mouseY,delta);
    }
  }
  @Environment(EnvType.CLIENT)
  public static class MidnightConfigListWidget extends ElementListWidget<ButtonEntry> {
    TextRenderer textRenderer;

    public MidnightConfigListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
      super(minecraftClient, i, j, k, l, m);
      this.centerListVertically = false;
      textRenderer = minecraftClient.textRenderer;
    }
    @Override
    public int getScrollbarPositionX() { return this.width -7; }

    public void addButton(ClickableWidget button, ClickableWidget resetButton, Text text) {
      this.addEntry(ButtonEntry.create(button, text, resetButton));
    }
    @Override
    public int getRowWidth() { return 10000; }
    public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
      for (ButtonEntry buttonEntry : this.children()) {
        for (ClickableWidget ClickableWidget : buttonEntry.buttons) {
          if (ClickableWidget.isMouseOver(mouseX, mouseY)) {
            return Optional.of(ClickableWidget);
          }
        }
      }
      return Optional.empty();
    }
  }
  public static class ButtonEntry extends ElementListWidget.Entry<ButtonEntry> {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final List<ClickableWidget> buttons = new ArrayList<>();
    private final List<ClickableWidget> resetButtons = new ArrayList<>();
    private final List<Text> texts = new ArrayList<>();
    private final List<ClickableWidget> buttonsWithResetButtons = new ArrayList<>();
    public static final Map<ClickableWidget, Text> buttonsWithText = new HashMap<>();

    private ButtonEntry(ClickableWidget button, Text text, ClickableWidget resetButton) {
      buttonsWithText.put(button,text);
      this.buttons.add(button);
      this.resetButtons.add(resetButton);
      this.texts.add(text);
      this.buttonsWithResetButtons.add(button);
      this.buttonsWithResetButtons.add(resetButton);
    }
    public static ButtonEntry create(ClickableWidget button, Text text, ClickableWidget resetButton) {
      return new ButtonEntry(button, text, resetButton);
    }
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      this.buttons.forEach(button -> {
        button.y = y;
        button.render(matrices, mouseX, mouseY, tickDelta);
      });
      this.texts.forEach(text -> DrawableHelper.drawTextWithShadow(matrices,textRenderer, text,12,y+5,0xFFFFFF));
      this.resetButtons.forEach((button) -> {
        button.y = y;
        button.render(matrices, mouseX, mouseY, tickDelta);
      });
    }
    public List<? extends Element> children() {
      return buttonsWithResetButtons;
    }

    public List<? extends Selectable> method_37025() {
      return buttonsWithResetButtons;
    }
  }
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Entry {
    int width() default 100;
    double min() default Double.MIN_NORMAL;
    double max() default Double.MAX_VALUE;
  }
  @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Comment {}

  public static class HiddenAnnotationExclusionStrategy implements ExclusionStrategy {
    public boolean shouldSkipClass(Class<?> clazz) { return false; }
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
      return fieldAttributes.getAnnotation(Entry.class) == null;
    }
  }
}
