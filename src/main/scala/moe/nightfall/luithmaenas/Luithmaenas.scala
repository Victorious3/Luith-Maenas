package moe.nightfall.luithmaenas

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.Mod.EventHandler
import net.minecraftforge.common.config.Configuration
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import org.apache.logging.log4j.Logger
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.ProgressManager
import moe.nightfall.luithmaenas.proxy.CommonProxy
import cpw.mods.fml.common.SidedProxy
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

@Mod(modid = Luithmaenas.modid, name = Luithmaenas.modname, modLanguage = "scala")
final object Luithmaenas {
    
    // Constants
    final val modid = "luithmaenas"
    final val modname = "Luith Maenas"
    
    private var _log : Logger = _
    def log = _log
    
    private var nlpLoadingThread : Thread = _
    private var nlpMonitorThread : Thread = _
    
    @SidedProxy(clientSide = "moe.nightfall.luithmaenas.proxy.ClientProxy", serverSide = "moe.nightfall.luithmaenas.proxy.CommonProxy")
	  var proxy: CommonProxy = _
    
    @EventHandler
    def preInit(event: FMLPreInitializationEvent) {
        // Here's our mod log
        _log = event.getModLog
        // Initialize config
        Config.init(new Configuration(event.getSuggestedConfigurationFile))
        if(event.getSide.isClient) {
            // Initialize NLP in loading thread.
            nlpLoadingThread = new Thread {
                override def run() {
                    try {
                        nlpMonitorThread.start()
                        log.info("Loading stanford parser, this may take a while. Please be pacient!")  
                        NLP.init()
                        
                        // Wait for the monitor thread to realize that we are done
                        nlpMonitorThread.join()
                        if (Minecraft.getMinecraft.thePlayer != null) {
                            // This is good enough, won't trigger if the player hasn't joined a world
                            Minecraft.getMinecraft.thePlayer.addChatComponentMessage(new ChatComponentText(s"[${Luithmaenas.modname}] Alright, we made it! Have fun!"))
                        }
                    } catch {
                        case e : Exception => {
                            log.error("Couldn't load stanford parser, aborting!")
                            throw e
                        }
                    }
                }
            }
            nlpMonitorThread = new Thread {
                override def run() {
                    val bar = ProgressManager.push(s"[$modname] Initializing stanford NLP", NLP.annotators.length + 1)
                    var lastStatus = NLP.status
                    bar.step("Initializing...")
                    
                    while (NLP.status != "initialized") {
                        if (NLP.status != lastStatus) {
                            lastStatus = NLP.status
                            bar.step(lastStatus)
                        }
                        // This is just a monitor, we don't need to be running at full speed...
                        Thread.sleep(100)
                    }
                    
                    ProgressManager.pop(bar)
                }
            }
            
            nlpLoadingThread.setDaemon(true)
            nlpMonitorThread.setDaemon(true)
            nlpLoadingThread.start()
            
        }
    }
    
    @EventHandler
    def postInit(event: FMLPostInitializationEvent) {
        // Join the NLP loading thread to wrap up nlp initialization before the game loaded.
        // You should have had way more than enough time by now!
        if (event.getSide.isClient && !Config.LOAD_IN_BACKGROUND) {
            log.warn("Waiting for stanford parser to finish loading, hold on...")
            nlpLoadingThread.join()
        }
    }
}