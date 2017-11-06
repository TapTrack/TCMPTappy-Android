package com.taptrack.experiments.rancheria.ui.views.sendmessages

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.business.CommandFamilyOption
import com.taptrack.experiments.rancheria.business.CommandOption
import com.taptrack.experiments.rancheria.ui.inflateChildren
import com.taptrack.experiments.rancheria.ui.views.getHostActivity
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.find

class CommandSelectorView : ConstraintLayout, CommandFamilySelectedListener, CommandSelectedListener {

    private var familyAdapter: CommandFamilySelectorAdaptor? = null
    private var commandAdapter: CommandSelectorAdapter? = null

    private var viewState: CommandSelectorViewState = CommandSelectorViewState.initialState()
    private var vm: CommandSelectorViewModel? = null
    private var disposable: Disposable? = null

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        inflateChildren(R.layout.command_selector_view)

        // this should probably be changed, as it assumes a nearly full-screen view
        val screenWidth = context.resources.displayMetrics.widthPixels

        val commandElementWidth = (context.resources.getDimension(R.dimen.command_selector_cmd_icon_size)+
                (2*context.resources.getDimension(R.dimen.command_selector_cmd_horiz_margin)))*1.5
        val commandColCount = Math.max((screenWidth / commandElementWidth.toInt()), 1)


        val commandLayoutManager = GridLayoutManager(context,commandColCount)
        commandAdapter = CommandSelectorAdapter(this)
        val commandSelector = find<RecyclerView>(R.id.rv_command_selector)
        commandSelector.adapter = commandAdapter
        commandSelector.layoutManager = commandLayoutManager


        val commandFamilyElementWidth = (context.resources.getDimension(R.dimen.command_selector_fam_icon_size)+
                (2*context.resources.getDimension(R.dimen.command_selector_fam_horiz_margin)))*1.5
        val familyColCount = Math.max((screenWidth / commandFamilyElementWidth.toInt()), 1)

        var familyLayoutManager: RecyclerView.LayoutManager? = null
        familyLayoutManager = GridLayoutManager(context,familyColCount)

        familyAdapter = CommandFamilySelectorAdaptor(this)
        val commandFamilySelector = find<RecyclerView>(R.id.rv_command_family_selector)
        commandFamilySelector.adapter = familyAdapter
        commandFamilySelector.layoutManager = familyLayoutManager

        reset()
    }

    override fun commandFamilySelected(family: CommandFamilyOption) {
        vm?.selectCommandFamily(family.id)
    }

    override fun commandSelected(command: CommandOption) {
        val act = getHostActivity()
        if (act is AppCompatActivity) {
            val frag = ConfigureCommandDialogFragment.createConfigureCommandFragment(command)
            val fm = act.supportFragmentManager
            frag.show(fm,"configure_tcmp")
        } else if (act != null) {
            DialogGenerator.configureCommandAlertDialog(act,command)?.show()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        vm = (getHostActivity() as? CommandSelectorViewModelProvider)?.provideCommandSelectorViewModel()
        disposable = vm?.getFindTappiesState()?.subscribe {
            post {
                viewState = it
                reset()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
    }

    private fun reset() {
        val selectableOptions = viewState.familyOptions.map { SelectableFamilyOption(it,it.id == viewState.selectedCommandFamily) }
        familyAdapter?.setItems(selectableOptions)
        commandAdapter?.setItems(viewState.commandOptions)
    }

}

