package com.dbohdanov.devicestextrecognitionproto

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import android.content.res.AssetManager
import android.media.ExifInterface
import android.widget.TextView
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener { v -> dispatchTakePictureIntent() }
//        findViewById<Button>(R.id.button).setOnClickListener { v ->
//            TextRecognizer(this)
//                    .getTextFromBitmap(
//                            getBitmapFromAsset(this, "text.png")!!, findViewById(R.id.textview))
//                            getBitmapFromAsset(this, "dev1.jpg")!!, findViewById(R.id.textview))
//        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File
            try {
                photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(this,
                        "com.dbohdanov.devicestextrecognitionproto.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//            val extras = data!!.extras
//            val imageBitmap = extras!!.get("data") as Bitmap


            val f = File(currentPhotoPath)
            val contentUri: Uri = Uri.fromFile(f)
            val bitmap = BitmapFactory.decodeFile(contentUri.path)
//            TextRecognizer(this).getTextFromBitmap(bitmap, findViewById<TextView>(R.id.textview))
            TextRecognizer(this).getByUriToBitmap(contentUri, bitmap, findViewById(R.id.textview))
//            TextRecognizer(this).getTextFromImageByUri(contentUri, findViewById(R.id.textview))
//            Picasso.get().load(f).into(findViewById<ImageView>(R.id.image_view))
        }
    }

    fun getBitmapFromAsset(context: Context, strName: String): Bitmap? {
        val assetManager = context.getAssets()
        val istr: InputStream
        var bitmap: Bitmap
        try {
            istr = assetManager.open(strName)
            bitmap = BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            return null
        }

        return bitmap
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                "tempfile", /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath()
        return image
    }


}

