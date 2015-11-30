package moe.nightfall.luithmaenas.proxy

import cpw.mods.fml.common.FMLCommonHandler
import net.minecraftforge.common.MinecraftForge
import com.sun.glass.ui.Application.EventHandler

class CommonProxy {
    FMLCommonHandler.instance.bus.register(this)
    MinecraftForge.EVENT_BUS.register(this)
    
    CommonProxy._instance = this
}

object CommonProxy {
    var _instance : CommonProxy = _
    def instance = _instance
}