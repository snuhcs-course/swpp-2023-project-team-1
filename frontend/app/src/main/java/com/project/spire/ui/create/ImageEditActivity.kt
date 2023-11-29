package com.project.spire.ui.create

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.R
import com.example.spire.databinding.ActivityImageEditBinding
import com.project.spire.core.inference.SegmentationRepository
import com.project.spire.ui.MainActivity
import com.project.spire.utils.BitmapUtils
import com.project.spire.utils.InferenceUtils
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.iconForm

const val RECYCLER_VIEW_MARGIN = 30

class ImageEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageEditBinding
    private lateinit var inferenceViewModel: InferenceViewModel

    private val segmentationRepository = SegmentationRepository()
    private val canvasViewModelFactory = CanvasViewModelFactory(segmentationRepository)
    private val canvasViewModel = canvasViewModelFactory.create(CanvasViewModel::class.java)

    private var mImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inferenceViewModel = InferenceUtils.inferenceViewModel

        // crop image
        val uri = Uri.parse(intent.getStringExtra("imageUri"))
        val cropImageView = binding.cropImageView
        val cropDoneButton = binding.cropDoneButton
        cropImageView.setImageUriAsync(uri)
        cropImageView.setAspectRatio(1, 1)
        val mImageView = binding.editingImage

        cropDoneButton.setOnClickListener {
            val bitmap = cropImageView.getCroppedImage(mImageView.width, mImageView.height)!!
            Log.d("ImageEditActivity", "Bitmap size: ${bitmap.height}")
            canvasViewModel.setOriginImageBitmap(bitmap)
            cropImageView.visibility = ImageView.GONE
            cropDoneButton.visibility = Button.GONE
        }

        canvasViewModel.originImageBitmap.observe(this) {
            if (it != null) {
                Log.d("ImageEditActivity", "Image updated")
                mImageBitmap = Bitmap.createScaledBitmap(it, mImageView.width, mImageView.height,false)
                mImageView.setImageBitmap(mImageBitmap)
            }
        }

        val mCanvasView = binding.spireCanvasView
        mCanvasView.initViewModel(canvasViewModel)

        // enable or disable pen or erase mode
        val editBtn = binding.editButton
        editBtn.setOnClickListener { canvasViewModel.changePenMode() }
        canvasViewModel.isPenMode.observe(this) {
            if (it) {
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
            } else {
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }

        val eraseBtn = binding.eraseButton
        eraseBtn.setOnClickListener { canvasViewModel.changeEraseMode() }
        canvasViewModel.isEraseMode.observe(this) {
            if (it) {
                eraseBtn.setImageResource(R.drawable.ic_img_erase_selected)
            } else {
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
        }

        canvasViewModel.redraw.observe(this) {
            mCanvasView.invalidate()
        }

        val promptInput = binding.promptInput

        val nextBtn = binding.nextButton
        nextBtn.setOnClickListener {
            val intent = Intent(this, WriteTextActivity::class.java)
            startActivity(intent)
            val maskBitmap = mCanvasView.getBitmap()
            val maskBitmapToServer = BitmapUtils.maskTransparentToBlack(maskBitmap)
            inferenceViewModel.infer(mImageBitmap!!, maskBitmapToServer, promptInput.text.toString())
        }

        val backBtn = binding.imageEditAppBarLayout.backButton
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val exitBtn = binding.imageEditAppBarLayout.exitButton
        exitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        val fetchButton = binding.fetchButton
        val progressBar = binding.maskProgressBar

        fetchButton.setOnClickListener {
            canvasViewModel.inferMask(mImageBitmap!!, mImageView.width, mImageView.height)
            fetchButton.visibility = Button.GONE
            progressBar.visibility = ProgressBar.VISIBLE
            progressBar.isActivated = true
        }

        /*
        inferenceViewModel.maskOverallImage.observe(this) {
            // TODO show or not?
            if (it != null ) {
                Log.d("ImageEditActivity", "Mask image received")
                canvasViewModel.applyFetchedMask(it)
            }
        } */

        val recyclerView = binding.maskFetchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    //    val textColorDefault = ContextCompat.getColor(this, R.color.black)
     //   val textColorClicked = ContextCompat.getColor(this, R.color.white)

        val adapter = MaskFetchAdapter(canvasViewModel.masks.value!!, canvasViewModel.labels.value!!, canvasViewModel) //, textColorDefault, textColorClicked)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(HorizontalSpaceDecoration(RECYCLER_VIEW_MARGIN))
        recyclerView.setHasFixedSize(false)

        val resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            canvasViewModel.clearCanvas()
            editBtn.setImageResource(R.drawable.ic_img_edit)
            eraseBtn.setImageResource(R.drawable.ic_img_erase)
            recyclerView.adapter?.run {
                (this as MaskFetchAdapter).clearClickedItems()
            }
            /*
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                //child.

                recyclerView.getChildViewHolder(child).apply {
                    isClicked = false
                }


                child.setBackgroundResource(R.drawable.btn_mask_fetch_bg)
                child.setTextColor(textColorDefault)
            } */
        }



        canvasViewModel.labels.observe(this) {
            if (it.isNotEmpty() and (canvasViewModel.masks.value!!.isNotEmpty())) {
                Log.d("ImageEditActivity", "Masks received")
                recyclerView.run {
                    adapter.updateList(canvasViewModel.masks.value!!, it)
                    Log.d("ImageEditActivity", "Adapter updated label observer: ${it}")
                    visibility = RecyclerView.VISIBLE
                }
                progressBar.isActivated = false
                progressBar.visibility = ProgressBar.GONE
            }
        }

        canvasViewModel.maskError.observe(this) {
            if (it == 1) {
                //Toast.makeText(this, "Mask generate failed, retrying...", Toast.LENGTH_LONG).show()
            }
            else if (it == 2) {
                Toast.makeText(this, "Mask generate failed, please try again.", Toast.LENGTH_LONG).show()
                fetchButton.visibility = Button.VISIBLE
                recyclerView.visibility = RecyclerView.INVISIBLE
                progressBar.isActivated = false
                progressBar.visibility = ProgressBar.GONE
            }
        }

        val balloon = Balloon.Builder(context = this)
            .setLayout(R.layout.tooltip_balloon)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setArrowSize(10)
            .setCornerRadius(10f)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.blue_500))
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        val tooltipButton = binding.maskFetchTooltipButton
        tooltipButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    balloon.showAlignBottom(tooltipButton)
                }
                MotionEvent.ACTION_UP -> {
                    balloon.dismissWithDelay(500L)
                }
            }
            true
        }

    }
}
