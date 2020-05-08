package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiScreenOptionsSounds.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiScreenOptionsSounds extends MixinGuiScreen {

    @Shadow
    private GuiScreen field_146505_f;
    @Shadow
    private GameSettings game_settings_4;
    @Shadow
    protected String field_146507_a;
    @Shadow
    private String field_146508_h;
    @Shadow
    protected abstract String getSoundVolume(final SoundCategory p_146504_1_);

    @Override
    public void initGui() {
        int i = 0;
        this.field_146507_a = I18n.format("options.sounds.title", new Object[0]);
        this.field_146508_h = I18n.format("options.off", new Object[0]);
        this.buttonList.add(new Button(SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i += 2;
        SoundCategory[] values;
        for (int length = (values = SoundCategory.values()).length, j = 0; j < length; ++j) {
            final SoundCategory soundcategory = values[j];
            if (soundcategory != SoundCategory.MASTER) {
                this.buttonList.add(new Button(soundcategory.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
        }
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    class Button extends GuiButton {
        private final SoundCategory field_146153_r;
        private final String field_146152_s;
        public float field_146156_o;
        public boolean field_146155_p;

        public Button(final int p_i45024_2_, final int p_i45024_3_, final int p_i45024_4_, final SoundCategory p_i45024_5_, final boolean p_i45024_6_) {
            super(p_i45024_2_, p_i45024_3_, p_i45024_4_, p_i45024_6_ ? 310 : 150, 20, "");
            this.field_146156_o = 1.0f;
            this.field_146153_r = p_i45024_5_;
            this.field_146152_s = I18n.format("soundCategory." + p_i45024_5_.getCategoryName(), new Object[0]);
            this.displayString = String.valueOf(this.field_146152_s) + ": " + MixinGuiScreenOptionsSounds.this.getSoundVolume(p_i45024_5_);
            this.field_146156_o = MixinGuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(p_i45024_5_);
        }

        @Override
        protected int getHoverState(final boolean mouseOver) {
            return 0;
        }

        @Override
        protected void mouseDragged(final Minecraft mc, final int mouseX, final int mouseY) {
            if (this.visible) {
                if (this.field_146155_p) {
                    this.field_146156_o = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                    this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0f, 1.0f);
                    mc.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
                    mc.gameSettings.saveOptions();
                    this.displayString = String.valueOf(this.field_146152_s) + ": " + MixinGuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
                }
                Gui.drawRect(this.xPosition + (int)(this.field_146156_o * (this.width - 4)), this.yPosition,
                        this.xPosition + (int)(this.field_146156_o * (this.width - 4)) + 4,
                        this.yPosition + this.height, new Color(49, 49, 49, 180).getRGB());
            }
        }

        @Override
        public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.field_146156_o = (mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0f, 1.0f);
                mc.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
                mc.gameSettings.saveOptions();
                this.displayString = String.valueOf(this.field_146152_s) + ": " + MixinGuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
                return this.field_146155_p = true;
            }
            return false;
        }

        @Override
        public void playPressSound(final SoundHandler soundHandlerIn) {
        }

        @Override
        public void mouseReleased(final int mouseX, final int mouseY) {
            if (this.field_146155_p) {
                if (this.field_146153_r != SoundCategory.MASTER) {
                    MixinGuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(this.field_146153_r);
                }
                MixinGuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
            }
            this.field_146155_p = false;
        }
    }



}
