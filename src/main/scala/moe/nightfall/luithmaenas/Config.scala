package moe.nightfall.luithmaenas

import net.minecraftforge.common.config.Configuration

object Config {
    
    /** Config value for the mana data watcher, might have to be changed in order to avoid conflicts.
     * We probably won't ever need those since its pretty unlikely that anybody will use two different mods that
     * introduce mana. Otherwise data watchers should be used scarce. */
    var DW_MANA: Int = _
    
    /** Config option to load the NLP in the background instead of keeping the loading screen busy */
    var LOAD_IN_BACKGROUND : Boolean = _
    
    def init(config: Configuration) {
        config.load()
        config.setCategoryComment("DATA_WATCHERS", """
                |Don't touch these settings unless you know what you are doing! 
                |This mod is using data watchers in order to store the mana for each entity.
                |If conflicts arise, you have to change the ids since there are only 32 slots available.
                |The mod will warn you if a conflict happens, check the logs.""".stripMargin)
        
        DW_MANA = config.getInt("dw_mana", "DATA_WATCHERS", 14, 0, 31, "Data watcher id for the current mana")
        
        LOAD_IN_BACKGROUND = config.getBoolean("load_in_background", "GENERAL", false, """
                |Enable this if you wan to load the NLP that is required to run the mod
                |on the client side in the background. This will reduce loading times for the startup screen
                |but you might have to wait before you can use any spell.""".stripMargin)
        
        config.save()
    }
}