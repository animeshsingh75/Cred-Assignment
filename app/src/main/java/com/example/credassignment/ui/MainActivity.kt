package com.example.credassignment.ui

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.credassignment.R
import com.example.credassignment.databinding.ActivityMainBinding
import com.example.credassignment.ui.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private val logoDragMessage = "Logo Added"

    private lateinit var binding: ActivityMainBinding
    val vm by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachViewDragListener()
        binding.logoDropContainer.setOnDragListener(logoDragListener)
        vm.result.observe(this) {
            binding.loadingView.pauseAnimation()
            binding.loadingView.visibility=View.GONE
            if (it.success) {
                val context=this
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out)
                binding.logoDropContainer.startAnimation(animation)

                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                    }

                    override fun onAnimationEnd(p0: Animation?) {
                        binding.logoDropContainer.visibility=View.GONE
                        val slideInAnimation=AnimationUtils.loadAnimation(context, R.anim.slide_in)
                        binding.apiData.apply {
                            this.visibility=View.VISIBLE
                            this.text="success"
                            this.startAnimation(slideInAnimation)
                        }

                    }
                })
            }else{
                binding.arrowDown.apply {
                    this.visibility = View.VISIBLE
                    this.playAnimation()
                }
                binding.credLogoContainer.addView(binding.credLogo)
                Toast.makeText(
                    this,
                    "A failure has occurred.Please try again later",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val logoDragListener = View.OnDragListener { view, dragEvent ->

        val draggableItem = dragEvent.localState as View

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                binding.logoDropContainer.alpha = 0.3f
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                binding.logoDropContainer.alpha = 1.0f
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                binding.logoDropContainer.alpha = 1.0f

                val isIntersecting=findIntersection(binding.logoDropArea, dragEvent)
                if (isIntersecting){
                    binding.loadingView.visibility=View.VISIBLE
                    binding.loadingView.playAnimation()
                    binding.arrowDown.visibility=View.GONE
                    binding.arrowDown.pauseAnimation()
                    val parent = draggableItem.parent as ConstraintLayout
                    parent.removeView(draggableItem)
                    if(binding.toggleButton.isChecked){
                        vm.fetchSuccessResult()
                    }
                    else{
                        vm.fetchFailureResult()
                    }
                }else{
                    binding.credLogo.x=0f
                    binding.credLogo.y=0f
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            else -> {
                false
            }

        }
    }

    private fun findIntersection(containerView: ImageView, dragEvent: DragEvent): Boolean {
        val centerXOfContainerView: Int = containerView.left + containerView.width / 2
        val centerYOfContainerView: Int = containerView.top + containerView.height / 2
        val centerXOfDropView: Int = dragEvent.x.toInt()
        val centerYOfDropView: Int = dragEvent.y.toInt()
        if (centerXOfDropView in centerXOfContainerView - 10..centerXOfContainerView + 10 && centerYOfDropView in centerYOfContainerView - 10..centerYOfContainerView + 10) {
            return true
        }
        return false
    }

    private fun attachViewDragListener() {

        binding.credLogo.setOnLongClickListener { view: View ->
            val item = ClipData.Item(logoDragMessage)
            val dataToDrag = ClipData(
                logoDragMessage,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item

            )
            val logoShadow = LogoDragShadowBuilder(view)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                @Suppress("DEPRECATION")
                view.startDrag(dataToDrag, logoShadow, view, 0)
            } else {
                view.startDragAndDrop(dataToDrag, logoShadow, view, 0)
            }

            view.visibility = View.INVISIBLE
            true
        }
    }
}

private class LogoDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    private val shadow = ResourcesCompat.getDrawable(
        view.context.resources,
        R.drawable.ic_cred_logo,
        view.context.theme
    )


    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = view.width
        val height: Int = view.height
        shadow?.setBounds(0, 0, width, height)
        size.set(width, height)
        touch.set(width / 2, height / 2)
    }
    override fun onDrawShadow(canvas: Canvas) {
        shadow?.draw(canvas)
    }
}
