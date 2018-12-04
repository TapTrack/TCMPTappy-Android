package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandOption
import com.taptrack.experiments.rancheria.ui.inflateChildren
import com.taptrack.tcmptappy2.TCMPMessage
import kotlin.reflect.full.createInstance

class PlaceholderConfigurationView : ConstraintLayout {
    constructor(context: Context) : super(context) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        inflateChildren(R.layout.configure_command_view)
    }
}

class CommandConfigurationView : ConstraintLayout {
    private var configDelegate: CommandConfigurationDelegate? = null
    private var commandOption: CommandOption? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    private fun initialize(context: Context) {
    }

    fun setCommand(commandOption: CommandOption?, state: Bundle?) {

    }

    fun retrieveState(): Bundle = configDelegate?.getPersistingBundle() ?: Bundle()


    private fun getConfigDelegate(option: CommandOption) : CommandConfigurationDelegate? {
        val localCmdOption = commandOption
        if(localCmdOption == null) {
            return null
        } else {
            return NoConfigDelegate(localCmdOption, this)
        }
    }
}

abstract class CommandConfigurationDelegate(val parent: ConstraintLayout) {
    abstract fun getConfiguredMessage(): TCMPMessage?
    abstract fun getPersistingBundle(): Bundle
    abstract fun restorePersistingBundle(b: Bundle)
}

private class NoConfigDelegate(val commandOption: CommandOption, parent: ConstraintLayout) : CommandConfigurationDelegate(parent) {

    init {
        parent.inflateChildren(R.layout.noargs_command_data_view)
    }

    override fun getConfiguredMessage(): TCMPMessage? {
        val clazzes = commandOption.clazzes
        if(clazzes.isNotEmpty()) {
            clazzes
                    .map { it.kotlin }
                    .forEach {
                        try {
                            val message = it.createInstance()
                            return message
                        } catch (ignored: Exception) {
                            // cant instantiate
                        }
                    }
        }
        return null
    }

    override fun getPersistingBundle(): Bundle = Bundle()

    override fun restorePersistingBundle(b: Bundle) {
    }

}

