package net.jibini.mycelium.transfer

/**
 * An object which carries target and return route addresses
 */
interface Addressed
{
    /**
     * Adds an address to the object; can be a cache for [the router][Router] to keep track of the object's return
     * route
     *
     * @see findAddress
     * @see Router
     */
    fun address(attr : String, address : String)

    /**
     * Returns a stored address for a given attribute, or 'null' if none has been assigned
     *
     * @see address
     * @see Router
     */
    fun findAddress(attr : String) : String?
}