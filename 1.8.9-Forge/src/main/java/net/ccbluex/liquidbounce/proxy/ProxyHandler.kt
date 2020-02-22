package net.ccbluex.liquidbounce.proxy

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.ui.client.proxymanager.GuiProxyManager
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.network.handshake.client.C00Handshake
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class ProxyHandler : Listenable, MinecraftInstance() {

    val server = ServerSocket(1337)

    private fun connectOverProxy() {
        val client = Socket(Proxy(Proxy.Type.SOCKS, InetSocketAddress(GuiProxyManager.proxy.host, GuiProxyManager.proxy.port)))
        client.connect(InetSocketAddress("178.63.18.159", 25565))
        val connection = server.accept()

        thread {
            while (true) {
                client.getOutputStream().write(connection.getInputStream().read())
            }
        }

        thread {
            while (true) {
                connection.getOutputStream().write(client.getInputStream().read())
            }
        }
    }

    @EventTarget
    fun onPacket(packetEvent: PacketEvent) {

        if (GuiProxyManager.proxy != null && packetEvent.packet is C00Handshake) {
            connectOverProxy()
        }
    }

    override fun handleEvents() = true
}