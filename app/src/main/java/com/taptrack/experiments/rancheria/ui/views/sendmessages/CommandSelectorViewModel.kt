package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.taptrack.experiments.rancheria.business.CommandDataSource
import com.taptrack.experiments.rancheria.business.CommandFamilyOption
import com.taptrack.experiments.rancheria.business.CommandOption
import io.reactivex.Observable

data class CommandSelectorViewState(
        val selectedCommandFamily: Int,
        val familyOptions: List<CommandFamilyOption>,
        val commandOptions: List<CommandOption>) {
    companion object {
        fun initialState(): CommandSelectorViewState = CommandSelectorViewState(CommandDataSource.FAM_OPTION_ID_ALL, emptyList(),emptyList())
    }
}

interface CommandSelectorViewModel {
    fun getFindTappiesState(): Observable<CommandSelectorViewState>
    fun selectCommandFamily(family: Int)
}

interface CommandSelectorViewModelProvider {
    fun provideCommandSelectorViewModel(): CommandSelectorViewModel
}

class CommandSelectorViewModelImpl(val context: Context, val dataSource: CommandDataSource ): CommandSelectorViewModel {

    private val prefs = context.getSharedPreferences(PREFS_COMMAND_SELECTOR,Context.MODE_PRIVATE)
    private val rxPrefs = RxSharedPreferences.create(prefs)

    override fun getFindTappiesState(): Observable<CommandSelectorViewState> {
        return rxPrefs.getInteger(KEY_SELECTED_FAMILY,CommandDataSource.FAM_OPTION_ID_ALL)
                .asObservable()
                .map {
                    CommandSelectorViewState(
                            it,
                            dataSource.retrieveFamilyOptions(),
                            dataSource.retrieveCommandOptions(it)
                    )
                }
    }

    override fun selectCommandFamily(family: Int) {
        prefs.edit().putInt(KEY_SELECTED_FAMILY,family).apply()
    }

    companion object {
        val PREFS_COMMAND_SELECTOR = "SELECTED_COMMAND_PREFERENCES"
        val KEY_SELECTED_FAMILY = "SELECTED_FAMILY_ID"
    }
}