package moe.nightfall.luithmaenas

trait Actor extends Any {
    
    def mana: Float
    protected def mana_= (mana: Float)

    /**
     * Tries to drain the specified amount of mana from this actor.
     * If the specified amount of mana couldn't be gathered, the actor is
     * destroyed and a `ManaDepletedException` is thrown.
     * 
     * @throws ManaDepletedExcetion
     */
    @throws(classOf[ManaDepletedException])
    def drain(amount: Float) {
        mana -= amount
        if (mana < 0) {
            val unsupplied = -mana
            gather(unsupplied)
            if (mana >= unsupplied) {
                mana -= unsupplied
            } else {
                destroy()
                throw new ManaDepletedException
            }
        }
    }
    
    /** Gets called before the internal mana pool gets depleted, 
     *  can be used to drain mana from elsewhere. 
     *  You have to add the equivalent of `additionalMana` to the internal mana pool. 
     *  If not enough mana can be gathered, `destroy` is called. */
    def gather(additionalMana: Float) = {}
    
    /** Gets called if gather didn't free up enough mana. 
     *  The Actor is supposed to react with total annihilation of itself! 
     *  If it doesn't you are playing in pussy-mode */
    def destroy()
    
    class ManaDepletedException extends Exception(s"Depleted mana of actor ${Actor.this}")
}
