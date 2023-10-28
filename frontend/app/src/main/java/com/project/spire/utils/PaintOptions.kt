package com.project.spire.utils

import android.graphics.PorterDuffXfermode
import java.io.Serializable

data class PaintOptions(var strokeWidth: Float, var xfermode: PorterDuffXfermode?, var alpha: Int) :
    Serializable