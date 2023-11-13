import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

class ImAClass() {
    var property: Int = 0


    fun function(param: Int): Int {
        println("Function called, params: $param ")
        return param
    }
}

class Reflection<T : Any>(
    private val kClass: KClass<T>
) {

    fun name(): String {
        return kClass.simpleName ?: ""
    }

    fun instance(): T {
        return kClass.createInstance()
    }

    fun properties(): Collection<KProperty<*>> {
        return kClass.memberProperties
    }

    fun getProperty(propName: String): KProperty<*>? {
        return kClass
            .memberProperties
            .filter { it.name == propName }
            .firstOrNull()
    }

    fun getFunction(functionName: String): KFunction<*>? {
        return kClass
            .functions
            .filter { it.name == functionName }
            .firstOrNull()
    }

    fun functions(): Collection<KFunction<*>> {
        return kClass.functions
    }

    fun getValueByName(propName: String, instance: T): Any? {
        val prop = getProperty(propName)

        if (prop == null)
            return null

        if (prop !is KProperty1<*, *>)
            return null

        val kProperty = prop as KProperty1<T, *>

        return kProperty.get(instance)
    }

    fun <V> setValueByName(propName: String, instance: T, value: V) {
        val prop = getProperty(propName)

        if (prop == null)
            return

        if (prop !is KMutableProperty1<*, *>)
            return

        val mutableProp = prop as KMutableProperty1<T, V>
        mutableProp.set(instance, value)
    }

    fun <V> getValue(prop: KMutableProperty1<T, V>, instance: T): V {
       return prop.get(instance)
    }

    fun <V> setValue(prop: KMutableProperty1<T, V>, instance: T, value: V) {
        prop.set(instance, value)
    }

    fun callByName(functionName: String, vararg args: Any?): Any? {
        val function = getFunction(functionName)

        if (function == null)
            return null

        return function.call(*args)
    }

    fun call(function: KFunction<*>, vararg args: Any?): Any? {
        return function.call(*args)
    }

}

fun main() {

    // Get KClass
    val kclass = ImAClass::class
    val util = Reflection(kclass)

    // Create instance
    val instance = util.instance()
    println("Instance: $instance")

    // Name class
    println("Name: ${util.name()}")

    // Get properties
    val properties = util.properties()
    properties.forEach { println("Property: $it") }

    // Get property by name
    val prop = util.getProperty("property")
    println("Property by name: $prop")

    // Get functions
    val functions = util.functions()
    functions.forEach { println("Function: $it") }

    // Get function by name
    val function = util.getFunction("function")
    println("Function by name: $function")

    // Set property value with property name
    util.setValueByName("property", instance, 1)
    println("Set value by name: ${instance.property}")

    // Get Value with property name
    val value = util.getValueByName("property", instance)
    println("Get value by name: ${value}")

    // Set Value with property
    util.setValue(ImAClass::property, instance, 2)
    println("Set value with property ${instance.property}")

    // Get Value with property
    val value1 = util.getValue(ImAClass::property, instance)
    println("Get value with property $value1")

    // Call function by name
    val value2 = util.callByName("function",  instance, 2)
    println("Call function, param: $value2")

    // Call function
    val value3 = util.call(ImAClass::function, instance, 3)
    println("Call function, param: $value3")

}

