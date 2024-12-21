package com.okanot.youokanot.model

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class WoundCategory {
    Abrasion,
    Bruises,
    Burns,
    Laceration,
    Normal
}

class WoundClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null

    init {
        // Initialize the interpreter when the class is created
        initializeInterpreter()
    }

    private fun initializeInterpreter() {
        val assetManager = context.assets
        // Load the model file from assets folder
        val model = loadModelFile(assetManager, "model_quantized_5.tflite")
        // Create the interpreter
        interpreter = Interpreter(model)
    }

    // Function to load the TFLite model from assets
    private fun loadModelFile(assets: android.content.res.AssetManager, modelPath: String): ByteBuffer {
        val fileDescriptor = assets.openFd(modelPath)
        val inputStream = fileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val size = fileChannel.size().toInt()

        val byteBuffer = ByteBuffer.allocateDirect(size)
        fileChannel.read(byteBuffer)
        byteBuffer.rewind()
        return byteBuffer
    }

    // Function to preprocess Bitmap image into a ByteBuffer with shape (150, 110, 3)
    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val inputSizeHeight = 224
        val inputSizeWidth = 224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSizeWidth, inputSizeHeight, true)

        // Create a ByteBuffer to hold the pixel data
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSizeHeight * inputSizeWidth * 3)  // 3 for RGB
        byteBuffer.order(ByteOrder.nativeOrder())

        // Iterate over each pixel in resizedBitmap and fill the ByteBuffer
        for (y in 0 until inputSizeHeight) {
            for (x in 0 until inputSizeWidth) {
                val pixel = resizedBitmap.getPixel(x, y)
                val r = ((pixel shr 16) and 0xFF).toFloat()
                val g = ((pixel shr 8) and 0xFF).toFloat()
                val b = (pixel and 0xFF).toFloat()

                // Add RGB values to ByteBuffer
                byteBuffer.putFloat(r)
                byteBuffer.putFloat(g)
                byteBuffer.putFloat(b)
            }
        }

        byteBuffer.rewind()  // Reset buffer position to the beginning
        return byteBuffer
    }

    // Function to perform inference with a Bitmap input
    fun classify(bitmap: Bitmap): WoundCategory {
        // Preprocess the Bitmap image into a ByteBuffer with shape (150, 110, 3)
        val input = preprocessBitmap(bitmap)

        // Assuming the model outputs 10 values (for example, a classification model with 10 categories)
        val output = Array(1) { FloatArray(5) }  // Update the output size based on your model
        interpreter?.run(input, output)
        val result = output[0]

        // Find the index of the maximum value in the result array (argmax)
        val predictedIndex = result.indices.maxByOrNull { result[it] } ?: -1

        // Map the predicted index to the corresponding category using the enum
        return if (predictedIndex != -1) {
            WoundCategory.values()[predictedIndex]
        } else {
            throw IllegalStateException("Prediction failed: Invalid index")
        }
    }

    // Don't forget to close the interpreter when done
    fun close() {
        interpreter?.close()
    }
}
