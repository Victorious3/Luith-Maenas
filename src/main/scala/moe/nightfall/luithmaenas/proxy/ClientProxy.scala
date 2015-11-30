package moe.nightfall.luithmaenas.proxy

import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraft.client.Minecraft
import moe.nightfall.luithmaenas.NLP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText
import moe.nightfall.luithmaenas.Luithmaenas

class ClientProxy extends CommonProxy {
   ClientProxy._instance = this
   
   @SubscribeEvent
   def onEntityJoinedWorld(event: EntityJoinWorldEvent) {
       if (!NLP.isInitialized) {
           val entity = event.entity
           if (entity == Minecraft.getMinecraft.thePlayer) {
               // Notify player if NLP hasn't loaded yet, can't cast any spells...
               entity.asInstanceOf[EntityPlayer].addChatMessage(
                       new ChatComponentText(s"[${Luithmaenas.modname}] You can't use any spells yet because the parser hasn't loaded, hang on!"))
           }
       }
   }
}

object ClientProxy {
    var _instance : ClientProxy = _
    def instance = _instance
}