package net.jibini.mycelium.document.impl

import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalStateException
import java.lang.reflect.*

object DocumentProxy
{
    inline fun <reified T> createFromJSON(internal : JSONObject) = createFromJSON(T::class.java, internal)

    inline fun <reified T> createFromJSON(internal : JSONArray) = createFromJSON(T::class.java, internal)

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> createFromJSON(interfaceClass : Class<T>, internal : JSONObject) = Proxy.newProxyInstance(
            interfaceClass.classLoader, arrayOf(interfaceClass), DocumentInvocationHandler(internal)) as T

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T> createFromJSON(interfaceClass : Class<T>, internal : JSONArray) : List<T>
    {
        val collection = mutableListOf<T>()

        for (item in internal)
            when (item.javaClass)
            {
                JSONObject::class.java -> collection.add(createFromJSON(interfaceClass, item as JSONObject))
                JSONArray::class.java -> collection.add(createFromJSON(interfaceClass, item as JSONArray) as T)

                else -> collection.add(item as T)
            }

        return collection
    }
}

class DocumentInvocationHandler(private val internal : JSONObject) : InvocationHandler
{
    @Suppress("UNCHECKED_CAST")
    private fun invokeGetAction(method : Method) : Any
    {
        val value = internal.get(method.name)

        return when (value.javaClass)
        {
            JSONObject::class.java -> DocumentProxy.createFromJSON(method.returnType as Class<Any>,
                    value as JSONObject)
            JSONArray::class.java -> DocumentProxy.createFromJSON(recursivelyFindParameter(method.genericReturnType),
                    value as JSONArray)

            else -> value
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any
    {
        return when (args?.size)
        {
            null, 0 -> invokeGetAction(method!!)
            1 -> internal.put(method!!.name, args[0])

            else -> throw IllegalStateException("Invalid document value getter/setter format")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun recursivelyFindParameter(type : Type) : Class<Any>
    {
        return if (type is ParameterizedType)
            recursivelyFindParameter(type.actualTypeArguments[0])
        else
            type as Class<Any>
    }
}