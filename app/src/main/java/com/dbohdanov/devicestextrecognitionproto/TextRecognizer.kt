package com.dbohdanov.devicestextrecognitionproto

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.IOException

/**
 *
 */
class TextRecognizer(val context: Context) {
    val TAG = "taag"

    //    fun getTextFromBitmap(bitmap: Bitmap) {
    fun getTextFromBitmap(bitmap: Bitmap, textView: TextView?) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        getText(image, textView)
    }

    fun getTextFromImageByUri(uri: Uri, textView: TextView?) {

        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(context, uri)

            getText(image, textView)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getText(image: FirebaseVisionImage, textView: TextView?) {
        val textRecognizer = FirebaseVision.getInstance()
                .onDeviceTextRecognizer


        textRecognizer.processImage(image)
                .addOnSuccessListener { result ->
                    val resultText = result.text
                    Log.d(TAG, "result $resultText")

                    if (textView != null) {
                        textView.text = resultText
                    }
//                        for (block in result.textBlocks) {
//                            val blockText = block.text
//                            val blockConfidence = block.confidence
//                            val blockLanguages = block.recognizedLanguages
//                            val blockCornerPoints = block.cornerPoints
//                            val blockFrame = block.boundingBox
//                            Log.d(TAG, "blockText in $blockLanguages $blockText")

//                            for (line in block.lines) {
//                                val lineText = line.text
//                                val lineConfidence = line.confidence
//                                val lineLanguages = line.recognizedLanguages
//                                val lineCornerPoints = line.cornerPoints
//                                val lineFrame = line.boundingBox
//
//                                Log.d(TAG, "line $lineText")
//                                for (element in line.elements) {
//                                    val elementText = element.text
//                                    val elementConfidence = element.confidence
//                                    val elementLanguages = element.recognizedLanguages
//                                    val elementCornerPoints = element.cornerPoints
//                                    val elementFrame = element.boundingBox
//
//                                    Log.d(TAG, "element $elementText")
//                                }
//                            }

//                        }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "error ${e.message}")
                }
    }

    fun displayText(result: FirebaseVisionText) {
        val textBlocks = result.textBlocks

        for (i in textBlocks.indices) {
            val textBlock = textBlocks.get(i)
            val lines = textBlock.lines
            for (j in lines.indices) {
                val elements = lines.get(j).elements
                for (k in elements.indices) {
                    val element = elements.get(k)
                    Log.d(TAG, element.text)
                }
            }
        }
    }

    fun getByUriToBitmap(contentUri: Uri, bitmap: Bitmap, textView: TextView) {
        try {
            val exif = ExifInterface(contentUri.path)
            val exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)

            Log.v(TAG, "Orient: $exifOrientation")

            var rotate = 0

            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            }

            Log.v(TAG, "Rotation: $rotate")

            var newBitmap: Bitmap
            if (rotate != 0) {
                // Getting width & height of the given image.
                val w = bitmap.getWidth()
                val h = bitmap.getHeight()

                // Setting pre rotate
                val mtx = Matrix()
                mtx.postRotate(rotate.toFloat())

                // Rotating Bitmap
                newBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false)
            } else {
                newBitmap = bitmap
            }

            val image = FirebaseVisionImage.fromBitmap(newBitmap)


            getText(image, textView)
        } catch (e: Exception) {
        }
    }
}