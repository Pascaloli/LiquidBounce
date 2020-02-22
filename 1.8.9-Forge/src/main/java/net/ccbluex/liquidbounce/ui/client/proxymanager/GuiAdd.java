package net.ccbluex.liquidbounce.ui.client.proxymanager;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.file.configs.ProxiesConfig;
import net.ccbluex.liquidbounce.proxy.Proxy;
import net.ccbluex.liquidbounce.proxy.ProxyCredentials;
import net.ccbluex.liquidbounce.ui.elements.GuiPasswordField;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.TabUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiAdd extends GuiScreen {

    private final GuiScreen prevGui;

    private GuiTextField proxyHostField;
    private GuiTextField proxyPortField;

    private GuiTextField proxyUsernameCredentialsField;
    private GuiPasswordField proxyPasswordCredentialsField;

    private String status;

    public GuiAdd(final GuiScreen gui) {
        this.prevGui = gui;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96, "Add"));
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Back"));

        proxyHostField = new GuiTextField(0, Fonts.font40, width / 2 - 100, 60, 200, 20);
        proxyHostField.setFocused(true);
        proxyHostField.setMaxStringLength(Integer.MAX_VALUE);

        proxyPortField = new GuiTextField(1, Fonts.font40, width / 2 - 100, 85, 200, 20);
        proxyPortField.setMaxStringLength(Integer.MAX_VALUE);

        proxyUsernameCredentialsField = new GuiTextField(2, Fonts.font40, width / 2 - 100, 110, 200, 20);
        proxyUsernameCredentialsField.setMaxStringLength(Integer.MAX_VALUE);

        proxyPasswordCredentialsField = new GuiPasswordField(3, Fonts.font40, width / 2 - 100, 135, 200, 20);
        proxyPasswordCredentialsField.setMaxStringLength(Integer.MAX_VALUE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        Gui.drawRect(30, 30, width - 30, height - 30, Integer.MIN_VALUE);

        drawCenteredString(Fonts.font40, "Add proxy", width / 2, 34, 0xffffff);
        drawCenteredString(Fonts.font35, status == null ? "" : status, width / 2, height / 4 + 86, 0xffffff);

        proxyHostField.drawTextBox();
        proxyPortField.drawTextBox();
        proxyUsernameCredentialsField.drawTextBox();
        proxyPasswordCredentialsField.drawTextBox();

        if (proxyHostField.getText().isEmpty() && !proxyHostField.isFocused())
            drawString(Fonts.font35, "§7Proxy Host", width / 2 - 95, 67, 0xffffff);

        if (proxyPortField.getText().isEmpty() && !proxyPortField.isFocused())
            drawString(Fonts.font35, "§7Proxy Port", width / 2 - 95, 92, 0xffffff);

        if (proxyUsernameCredentialsField.getText().isEmpty() && !proxyUsernameCredentialsField.isFocused())
            drawString(Fonts.font35, "§7Proxy Username", width / 2 - 95, 117, 0xffffff);

        if (proxyPasswordCredentialsField.getText().isEmpty() && !proxyPasswordCredentialsField.isFocused())
            drawString(Fonts.font35, "§7Proxy Password", width / 2 - 95, 142, 0xffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(prevGui);
                break;
            case 1:
                if (proxyHostField.getText().isEmpty()) {
                    status = "§cYou must fill the proxy host box.";
                    return;
                }

                if (proxyPortField.getText().isEmpty()) {
                    status = "§cYou must fill the proxy port box.";
                    return;
                }

                final String proxyHost = proxyHostField.getText();
                final int proxyPort = Integer.parseInt(proxyPortField.getText());

                final Proxy proxy = new Proxy(proxyHost, proxyPort);

                if (!proxyUsernameCredentialsField.getText().isEmpty())
                    proxy.setProxyCredentials(new ProxyCredentials(proxyUsernameCredentialsField.getText(), proxyPasswordCredentialsField.getText()));

                for (final Proxy otherProxy : ProxiesConfig.Companion.getProxyManagerProxies()) {
                    if (proxy.equals(otherProxy)) {
                        status = "§cProxy already in list.";
                        return;
                    }
                }

                ProxiesConfig.Companion.getProxyManagerProxies().add(proxy);
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.proxiesConfig);
                status = "§aAdded proxy.";
                mc.displayGuiScreen(prevGui);
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui);
            return;
        }

        if (Keyboard.KEY_TAB == keyCode)
            TabUtils.tab(proxyHostField, proxyPortField, proxyUsernameCredentialsField, proxyPasswordCredentialsField);

        if (proxyHostField.isFocused())
            proxyHostField.textboxKeyTyped(typedChar, keyCode);

        if (proxyPortField.isFocused() && Character.isDigit(typedChar))
            proxyPortField.textboxKeyTyped(typedChar, keyCode);

        if (proxyUsernameCredentialsField.isFocused())
            proxyUsernameCredentialsField.textboxKeyTyped(typedChar, keyCode);

        if (proxyPasswordCredentialsField.isFocused())
            proxyPasswordCredentialsField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        proxyHostField.mouseClicked(mouseX, mouseY, mouseButton);
        proxyUsernameCredentialsField.mouseClicked(mouseX, mouseY, mouseButton);
        proxyPasswordCredentialsField.mouseClicked(mouseX, mouseY, mouseButton);
        proxyPortField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
}
