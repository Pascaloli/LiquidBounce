package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiOptionSlider.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiOptionSlider extends MixinGuiButton {

    @Shadow
    private float sliderValue;

    @Shadow
    public boolean dragging;

    @Shadow
    private GameSettings.Options options;

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0f, 1.0f);
                final float f = this.options.denormalizeValue(this.sliderValue);
                mc.gameSettings.setOptionFloatValue(this.options, f);
                this.sliderValue = this.options.normalizeValue(f);
                this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }
            Gui.drawRect(this.xPosition + (int)(this.sliderValue * (this.width - 4)),
                    this.yPosition, this.xPosition + (int)(this.sliderValue * (this.width - 4)) + 4,
                    this.yPosition + this.height, new Color(49, 49, 49, 180).getRGB());
        }
    }
}
