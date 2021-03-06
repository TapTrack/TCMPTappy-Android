package com.taptrack.experiments.rancheria.ui.views.findtappies

import android.content.Context
import android.hardware.usb.UsbDevice
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.taptrack.experiments.rancheria.R
import com.taptrack.experiments.rancheria.ui.views.getHostActivity
import com.taptrack.experiments.rancheria.ui.views.setTextAppearanceCompat
import com.taptrack.tcmptappy2.ble.TappyBleDeviceDefinition
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.chooseTappiesView() = chooseTappiesView({})

inline fun ViewManager.chooseTappiesView(init: ChooseTappiesView.() -> Unit): ChooseTappiesView {
    return ankoView({ ChooseTappiesView(it) }, theme = 0, init = init)
}

class ChooseTappiesView : NestedScrollView {
    public var vm: ChooseTappiesViewModel? = null

    private lateinit var tappyControlView: TappyControlView
    private lateinit var tappySearchView: TappySearchView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var activeHeadingView: TextView
    private lateinit var searchHeadingView: TextView
    private lateinit var bluetoothOffView: TextView

    private var state: ChooseTappiesViewState = ChooseTappiesViewState.Companion.initialState()
    private var disposable: Disposable? = null

    constructor(context: Context) :
            super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val llLayoutParams = ViewGroup.MarginLayoutParams(matchParent, matchParent)
        linearLayout {
            orientation = LinearLayout.VERTICAL

            bluetoothOffView = themedTextView {
                text = context.getString(R.string.bluetooth_not_on_message)
                backgroundColor = ContextCompat.getColor(context,R.color.colorDarkBlue)
                padding = dip(16)
                visibility = View.GONE
                layoutParams = LayoutParams(matchParent, wrapContent)
            }.lparams(matchParent, wrapContent) {
            }
            bluetoothOffView.setTextAppearanceCompat(R.style.TextAppearance_AppCompat_Medium)
            bluetoothOffView.setTextColor(ContextCompat.getColor(context,R.color.colorDarkBlueContrast))

            activeHeadingView = themedTextView {
                text = context.getString(R.string.active_devices_heading,0)
            }.lparams(matchParent, wrapContent) {
                topMargin = dip(16)
                leftMargin = dip(16)
                rightMargin = dip(16)
            }
            activeHeadingView.setTextAppearanceCompat(R.style.TextAppearance_AppCompat_Medium)

            tappyControlView = tappyControlView {

            }.lparams(matchParent, wrapContent){
                leftMargin = dip(16)
                rightMargin = dip(16)
            }

            searchHeadingView = themedTextView() {
                textResource = R.string.select_tappy_text
            }.lparams(matchParent, wrapContent) {
                leftMargin = dip(16)
                rightMargin = dip(16)
            }
//            }.lparams(matchParent, wrapContent)

            searchHeadingView.setTextAppearanceCompat(R.style.TextAppearance_AppCompat_Medium)

            tappySearchView = tappySearchView {

            }.lparams(matchParent, wrapContent) {
                leftMargin = dip(16)
                rightMargin = dip(16)
            }

            loadingIndicator = progressBar {
                isIndeterminate = true
            }.lparams(width = wrapContent, height = wrapContent) {
                topMargin = dip(16)
                leftMargin = dip(16)
                rightMargin = dip(16)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            layoutParams = llLayoutParams
        }

        tappyControlView.setTappyControlListener(object: TappyControlListener {
            override fun requestRemove(namedTappy: NamedTappy) {
                vm?.removeActiveTappy(namedTappy)
            }

        })

        tappySearchView.setTappySelectionListener(object: TappySelectionListener {
            override fun tappyUsbSelected(device: UsbDevice) {
                vm?.addActiveTappyUsb(device)
            }

            override fun tappyBleSelected(definition: TappyBleDeviceDefinition) {
                vm?.addActiveTappyBle(definition)
            }
        })

        reset()
    }

    private fun reset() {
        tappySearchView.currentTappies = state
                .foundUsbDevices.map { ChoosableTappyUsb(it.deviceId.toString(),"USB Device",it.deviceName,it) }
                .plus(state.foundBleDevices.map { ChoosableTappyBle(it.address,it.name,it.address,it) })
        tappyControlView.setViewState(TappyControlViewState(state.activeDevices))
        activeHeadingView.text = context.getString(R.string.active_devices_heading,state.activeDevices.size)

        bluetoothOffView.visibility = if(state.bluetoothOn) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @UiThread
    public fun setState(state: ChooseTappiesViewState) {
        this.state = state
        reset()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        vm = (getHostActivity() as? ChooseTappiesViewModelProvider)?.provideChooseTappiesViewModel()
        disposable = vm?.getFindTappiesState()?.subscribe {
            post { setState(it) }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
    }
}
