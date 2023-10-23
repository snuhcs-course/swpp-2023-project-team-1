package com.project.spire.ui.create.image

import android.graphics.PorterDuffXfermode
import java.io.Serializable

data class PaintOptions(var strokeWidth: Float, var xfermode: PorterDuffXfermode?) :
    Serializable