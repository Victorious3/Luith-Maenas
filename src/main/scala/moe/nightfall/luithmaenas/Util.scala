package moe.nightfall

import net.minecraft.util.Vec3
import net.minecraft.entity.Entity
import moe.nightfall.luithmaenas.Config
import moe.nightfall.luithmaenas.Actor

object Util {
    
    implicit def toVec3(vec: (Double, Double, Double)): Vec3 = {
        Vec3.createVectorHelper(vec._1, vec._2, vec._3)
    }
    
    class EntityWrapper(val entity: Entity) extends AnyVal with Actor {
        def position = Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ)
        def position_= (vec: Vec3) = entity.setPosition(vec.xCoord, vec.yCoord, vec.zCoord)
        
        def velocity = Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ)
        def velocity_= (vec: Vec3) = entity.setVelocity(vec.xCoord, vec.yCoord, vec.zCoord)
        
        def mana = entity.getDataWatcher.getWatchableObjectFloat(Config.DW_MANA)
        def mana_= (mana: Float) = entity.getDataWatcher.addObject(Config.DW_MANA, mana)
        
        def destroy() = ???
    }
}