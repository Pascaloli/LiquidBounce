/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.file.configs

import com.google.gson.Gson
import net.ccbluex.liquidbounce.file.FileConfig
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.proxy.Proxy
import net.ccbluex.liquidbounce.proxy.ProxyCredentials
import java.io.*
import java.util.*

class ProxiesConfig(file: File?) : FileConfig(file) {

    companion object {
        var proxyManagerProxies: MutableList<Proxy> = ArrayList()
    }

    override fun loadConfig() {
        proxyManagerProxies.clear()
        val proxiesList = Gson().fromJson(BufferedReader(FileReader(file)), Array<ProxyConfig>::class.java)
        for (proxy in proxiesList) {
            if (proxy.username != null && proxy.password != null) {
                val username = proxy.username
                val password = proxy.password
                val credentials = ProxyCredentials(username, password)
                proxyManagerProxies.add(Proxy(proxy.host, proxy.port, credentials))
            } else {
                proxyManagerProxies.add(Proxy(proxy.host, proxy.port))
            }
        }
    }

    override fun saveConfig() {
        val proxies = proxyManagerProxies.map {
            val credentials = it.proxyCredentials

            if (credentials != null) {
                ProxyConfig(it.host, it.port, credentials.username, credentials.password)
            } else {
                ProxyConfig(it.host, it.port, null, null)
            }
        }

        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(proxies))
        printWriter.close()
    }
}

private class ProxyConfig(var host: String, var port: Int, var username: String?, var password: String?)