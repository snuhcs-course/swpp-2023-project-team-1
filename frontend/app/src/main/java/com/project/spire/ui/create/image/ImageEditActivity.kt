package com.project.spire.ui.create.image

import android.content.ContentValues
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.spire.R
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.spire.databinding.ActivityImageEditBinding
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding
    private val viewModel: ImageCreateViewModel by viewModels()

    private lateinit var mImageView: ImageView
    private lateinit var mCanvasView: SpireCanvasView
    private lateinit var editBtn: ImageButton
    private lateinit var eraseBtn: ImageButton
    private lateinit var resetBtn: ImageButton
    private lateinit var promptSuggestBtn: Button
    private lateinit var promptInput: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = Uri.parse(intent.getStringExtra("imageUri"))
        viewModel.setOriginImageUri(uri)
        mImageView = binding.editingImage
        viewModel.originImageUri.observe(this) {
            if (it != null) {
                mImageView.setImageURI(it)
            }
        }

        mCanvasView = binding.spireCanvasView
        mCanvasView.initViewModel(viewModel)

        editBtn = binding.editButton
        editBtn.setOnClickListener { viewModel.changePenMode() }
        val penModeObserver = Observer<Boolean> { isPenMode ->
            if (isPenMode) {
                editBtn.setImageResource(R.drawable.ic_img_edit_selected)
            }
            else {
                editBtn.setImageResource(R.drawable.ic_img_edit)
            }
        }
        viewModel.isPenMode.observe(this, penModeObserver)

        eraseBtn = binding.eraseButton
        eraseBtn.setOnClickListener { viewModel.changeEraseMode() }
        val eraseModeObserver = Observer<Boolean> { isEraseMode ->
            if (isEraseMode) {
                eraseBtn.setImageResource(R.drawable.ic_img_erase_selected)
            }
            else {
                eraseBtn.setImageResource(R.drawable.ic_img_erase)
            }
        }
        viewModel.isEraseMode.observe(this, eraseModeObserver)

        val isDrawingObserver = Observer<Boolean> { mCanvasView.invalidate() }
        viewModel.isDrawing.observe(this, isDrawingObserver)

        resetBtn = binding.resetButton
        resetBtn.setOnClickListener {
            viewModel.clearCanvas()
            editBtn.setImageResource(R.drawable.ic_img_edit)
            eraseBtn.setImageResource(R.drawable.ic_img_erase)
        }

        promptSuggestBtn = binding.promptSuggestionButton
        promptInput = binding.promptInput
        promptSuggestBtn.setOnClickListener {
            val currentText = promptInput.text.toString()
            if (currentText == "") promptInput.setText(promptSuggestBtn.text)
            else {
                promptInput.setText(currentText + ", " + promptSuggestBtn.text.toString())
            }
        }

        var saveButton = binding.saveButton
        saveButton.setOnClickListener {
             //saveImageOnAboveAndroidQ(mCanvasView.getBitmap())
            //saveImageOnAboveAndroidQ(mCanvasView.getBitmap(mImageView.width, mImageView.height))
            saveImageOnAboveAndroidQ(mCanvasView.getBitmap()) // TODO: 긴쪽 * 긴쪽의 정사각형으로 나오는듯?
        }

    }


    //Android Q (Android 10, API 29 이상에서는 이 메서드를 통해서 이미지를 저장한다.)
    //@RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        // TODO: crop image to square
        val fileName = System.currentTimeMillis().toString() + ".png" // 파일이름 현재시간.png

        /*
        * ContentValues() 객체 생성.
        * ContentValues는 ContentResolver가 처리할 수 있는 값을 저장해둘 목적으로 사용된다.
        * */
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave") // 경로 설정
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 파일이름을 put해준다.
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1) // 현재 is_pending 상태임을 만들어준다.
            // 다른 곳에서 이 데이터를 요구하면 무시하라는 의미로, 해당 저장소를 독점할 수 있다.
        }
        // val contentResolver = context.getContentResolver()

        // 이미지를 저장할 uri를 미리 설정해놓는다.
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if(uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                // write 모드로 file을 open한다.

                if(image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    //비트맵을 FileOutputStream를 통해 compress한다.
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 저장소 독점을 해제한다.
                    contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}