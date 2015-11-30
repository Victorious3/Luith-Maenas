package moe.nightfall.luithmaenas.entity

import net.minecraftforge.common.IExtendedEntityProperties
import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraft.nbt.NBTTagCompound

/** Extended properties that hold mana for an entity */
class ManaProperties extends IExtendedEntityProperties {
    
    override def init(entity: Entity, world: World) {}
    
    override def loadNBTData(compound: NBTTagCompound) {
        
    }
    
    override def saveNBTData(compound: NBTTagCompound) {
        
    }
}