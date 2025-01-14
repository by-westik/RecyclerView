package otus.gpb.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.Float.max

const val TEXT_TOP_MARGIN = 9f
const val END_MARGIN_DP = 20f

abstract class SwipeToDeleteListener(context: Context) : ItemTouchHelper.Callback() {

    private val messageBackground = ColorDrawable()
    private val colorBackground = ContextCompat.getColor(context, R.color.blue)
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.archive)
    private val deleteText = context.getString(R.string.archive)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val textPaint = Paint().apply {
        color = context.getColor(R.color.white)
        style = Paint.Style.FILL
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,13f, context.resources
            .displayMetrics)
        setTypeface(typeface)
    }

    private val textTopMargin = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        TEXT_TOP_MARGIN,
        context.resources.displayMetrics
    )

    private val textHorizontalMargin = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        END_MARGIN_DP,
        context.resources.displayMetrics
    )

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeToLeft = ItemTouchHelper.LEFT
        return makeMovementFlags(0, swipeToLeft)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView: View = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX.toInt() == 0 && !isCurrentlyActive

        if (isCancelled) {
           clearCanvas(canvas, itemView.right + dX, itemView.top.toFloat(), itemView.right
               .toFloat(), itemView.bottom.toFloat())
            return
        }

        val intrinsicWidth = deleteDrawable?.intrinsicWidth
        val intrinsicHeight = deleteDrawable?.intrinsicHeight

        messageBackground.color = colorBackground
        messageBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        messageBackground.draw(canvas)

        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight


        val textWidth = textPaint.measureText(deleteText)
        val textSize = textPaint.textSize
        val deleteIconWidth = max(textWidth, intrinsicWidth.toFloat())
        val deleteIconHeight = intrinsicHeight + textTopMargin + textSize
        val textLeft = itemView.right - textHorizontalMargin - deleteIconWidth + ((deleteIconWidth - textWidth)/ 2)
        val textTop = itemView.bottom - (itemView.height - deleteIconHeight) / 2

        canvas.drawText(deleteText, textLeft, textTop, textPaint)
        deleteDrawable?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable?.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }

    private fun clearCanvas(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        canvas.drawRect(left, top, right, bottom, clearPaint)
    }



}