package net.ccbluex.liquidbounce.ui.client.proxymanager;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.file.configs.ProxiesConfig;
import net.ccbluex.liquidbounce.proxy.Proxy;
import net.ccbluex.liquidbounce.proxy.ProxyAuth;
import net.ccbluex.liquidbounce.proxy.ProxyCredentials;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

public class GuiProxyManager extends GuiScreen {

    public static Proxy proxy;

    private final GuiScreen prevGui;

    private GuiList list;
    private String status = "§7Idle...";

    public GuiProxyManager(GuiScreen gui) {
        this.prevGui = gui;
    }

    public void initGui() {
        list = new GuiList(this);
        list.registerScrollButtons(7, 8);
        list.elementClicked(-1, false, 0, 0);

        int j = 22;
        this.buttonList.add(new GuiButton(0, width - 80, height - 65, 70, 20, "Back"));

        this.buttonList.add(new GuiButton(1, 5, j + 24, 90, 20, "Set proxy"));
        this.buttonList.add(new GuiButton(2, 5, j + 24 * 2, 90, 20, "Clear proxy"));
        this.buttonList.add(new GuiButton(3, 5, j + 24 * 3, 90, 20, "Random"));
        this.buttonList.add(new GuiButton(4, 5, j + 24 * 4, 90, 20, "Test"));

        this.buttonList.add(new GuiButton(5, width - 80, j + 24, 70, 20, "Add"));
        this.buttonList.add(new GuiButton(6, width - 80, j + 24 * 2, 70, 20, "Remove"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        list.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(Fonts.font40, "ProxyManager", width / 2, 6, 0xffffff);
        drawCenteredString(Fonts.font35, ProxiesConfig.Companion.getProxyManagerProxies().size() + " Proxies", width / 2, 18, 0xffffff);
        drawCenteredString(Fonts.font35, status == null ? "" : status, width / 2, 32, 0xffffff);

        drawString(Fonts.font35, "§7Proxy: §a" + (proxy == null ? "No proxy" : proxy.getHost() + ":" + proxy.getPort()), 6, 6, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                mc.displayGuiScreen(prevGui);
                break;
            }
            case 1: {
                if (list.getSelectedSlot() != -1 && list.getSelectedSlot() < list.getSize()) {
                    final Proxy proxy = ProxiesConfig.Companion.getProxyManagerProxies().get(list.getSelectedSlot());

                    GuiProxyManager.proxy = proxy;

                    if (proxy.hasProxyCredentials()) {
                        final ProxyCredentials proxyCredentials = proxy.getProxyCredentials();
                        Authenticator.setDefault(new ProxyAuth(proxyCredentials.getUsername(), proxyCredentials.getPassword()));
                    }

                    status = "§aSet proxy to §7" + proxy.getHost() + ":" + proxy.getPort() + "§a.";
                } else
                    status = "§cSelect a proxy.";
                break;
            }
            case 2: {
                Authenticator.setDefault(null);

                GuiProxyManager.proxy = null;

                status = "§aCleared proxy.";
                break;
            }
            case 3: {
                if (ProxiesConfig.Companion.getProxyManagerProxies().size() <= 0) {
                    status = "§cThe list is empty.";
                    return;
                }

                final int randomInteger = new Random().nextInt(ProxiesConfig.Companion.getProxyManagerProxies().size());

                if (randomInteger < list.getSize())
                    list.selectedSlot = randomInteger;

                final Proxy proxy = ProxiesConfig.Companion.getProxyManagerProxies().get(list.getSelectedSlot());

                GuiProxyManager.proxy = proxy;

                if (proxy.hasProxyCredentials()) {
                    final ProxyCredentials proxyCredentials = proxy.getProxyCredentials();
                    Authenticator.setDefault(new ProxyAuth(proxyCredentials.getUsername(), proxyCredentials.getPassword()));
                }

                status = "§aSet proxy to §7" + proxy.getHost() + ":" + proxy.getPort() + "§a.";

                break;
            }
            case 4: {
                if (list.getSelectedSlot() != -1 && list.getSelectedSlot() < list.getSize()) {
                    final Thread thread = new Thread(() -> {
                        try {
                            final Proxy proxy = ProxiesConfig.Companion.getProxyManagerProxies().get(list.getSelectedSlot());

                            status = "§aTesting...";
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(proxy.getHost(), proxy.getPort()), 1000);
                            status = "§aProxy working.";
                        } catch (IOException e) {
                            status = "§cProxy does not seem to be working!";
                        }
                    }, "Proxy-Test");
                    thread.start();
                } else
                    status = "§cSelect a proxy.";
                break;
            }
            case 5: {
                mc.displayGuiScreen(new GuiAdd(this));
                break;
            }
            case 6: {
                if (list.getSelectedSlot() != -1 && list.getSelectedSlot() < list.getSize()) {
                    ProxiesConfig.Companion.getProxyManagerProxies().remove(list.getSelectedSlot());
                    LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.proxiesConfig);
                    status = "§aRemoved proxy.";
                } else
                    status = "§cSelect a proxy.";
                break;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        list.handleMouseInput();
    }

    private class GuiList extends GuiSlot {

        private int selectedSlot;

        GuiList(GuiScreen prevGui) {
            super(Minecraft.getMinecraft(), prevGui.width, prevGui.height, 40, prevGui.height - 40, 30);
        }

        @Override
        protected boolean isSelected(int id) {
            return selectedSlot == id;
        }

        int getSelectedSlot() {
            if (selectedSlot > ProxiesConfig.Companion.getProxyManagerProxies().size())
                selectedSlot = -1;
            return selectedSlot;
        }

        @Override
        protected int getSize() {
            return ProxiesConfig.Companion.getProxyManagerProxies().size();
        }

        @Override
        protected void elementClicked(int slot, boolean doubleClick, int var3, int var4) {
            selectedSlot = slot;

            if (doubleClick) {
                if (list.getSelectedSlot() != -1 && list.getSelectedSlot() < list.getSize()) {
                    final Proxy proxy = ProxiesConfig.Companion.getProxyManagerProxies().get(list.getSelectedSlot());

                    GuiProxyManager.proxy = proxy;

                    if (proxy.hasProxyCredentials()) {
                        final ProxyCredentials proxyCredentials = proxy.getProxyCredentials();
                        Authenticator.setDefault(new ProxyAuth(proxyCredentials.getUsername(), proxyCredentials.getPassword()));
                    }

                    status = "§aSet proxy to §7" + proxy.getHost() + ":" + proxy.getPort() + "§a.";
                }
            }
        }

        @Override
        protected void drawSlot(int id, int x, int y, int var4, int var5, int var6) {
            final Proxy proxy = ProxiesConfig.Companion.getProxyManagerProxies().get(id);

            Fonts.font40.drawCenteredString(proxy.getHost() + ":" + proxy.getPort(), (width / 2), y + 2, Color.WHITE.getRGB(), true);
            Fonts.font40.drawCenteredString(proxy.hasProxyCredentials() ? "Credentials" : "No Credentials", (width / 2), y + 15, proxy.hasProxyCredentials() ? Color.GRAY.getRGB() : Color.LIGHT_GRAY.getRGB(), true);
        }

        @Override
        protected void drawBackground() {
        }
    }
}
