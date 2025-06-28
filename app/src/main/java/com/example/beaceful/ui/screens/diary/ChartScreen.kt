package com.example.beaceful.ui.screens.diary

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.calendar.MoodBarChart
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelProperties
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

val Emotions.score: Int
    get() = when (this) {
        Emotions.INLOVE -> 5
        Emotions.HAPPY -> 4
        Emotions.CONFUSE -> 3
        Emotions.SAD -> 2
        Emotions.ANGRY -> 1
    }

val emotionByScore = mapOf(
    1 to Emotions.ANGRY,
    2 to Emotions.SAD,
    3 to Emotions.CONFUSE,
    4 to Emotions.HAPPY,
    5 to Emotions.INLOVE
)


data class DailyEmotion(
    val day: Int,      // 1‒31
    val avgScore: Float
)

fun List<Diary>.toDailyEmotion(month: LocalDateTime): List<DailyEmotion?> {
    val grouped = groupBy { it.createdAt.dayOfMonth }
        .mapValues { (_, list) ->
            list.map { it.emotion.score }.average().toFloat()
        }


    return (1..month.toLocalDate().lengthOfMonth()).map { day ->
        grouped[day]?.let { DailyEmotion(day, it) }
    }
}

@Composable
fun ChartScreen(
    currentMonth: LocalDateTime,
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel()
) {

    val diariesForMonth by viewModel.diariesForMonth.collectAsState()
    var moodCount by remember { mutableStateOf<Map<Emotions, Int>>(emptyMap()) }
    Log.d("diaries", "$diariesForMonth")
    val chartData = remember {
        convertToColumnChartData(diariesForMonth)
    }

    LaunchedEffect(currentMonth) {
        moodCount = viewModel.moodCount(currentMonth)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {


        item {
            Text(
                text = "Thống kê mức độ cảm xúc",
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Row(Modifier.height(200.dp)) {
                ColumnChart(
                    data = chartData,
                    maxValue = 1.0,
                    minValue = -1.0,
                    barProperties = BarProperties(thickness = 8.dp),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    labelProperties = LabelProperties(textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp ), enabled = true)
                )
            }
        }

        item {
            Text(
                text = "Thống kê tháng",
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            EmotionLineChart(
                diaries = diariesForMonth,
                month = currentMonth,
            )
        }

        item {
            Column {
                Text(
                    text = "Thống kê tâm trạng",
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onPrimary,
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                MoodCountChart(moodCount)
                Spacer(Modifier.height(8.dp))
                MoodIconRow(moodCount)
            }
        }
        item { Spacer(Modifier.height(56.dp)) }
    }
}

// EmotionLineChart.kt
@Composable
fun EmotionLineChart(
    diaries: List<Diary>,
    month: LocalDateTime,
    modifier: Modifier = Modifier,
    showPoints: Boolean = true
) {
    val dailyData by remember(diaries, month) {
        mutableStateOf(diaries.toDailyEmotion(month))
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1_000, easing = FastOutSlowInEasing),
        label = "chartProgress"
    )

    val maxScore = 5f
    val minScore = 1f

    val topPadding = 16.dp
    val bottomPadding = 32.dp
    val startPadding = 40.dp
    val endPadding = 16.dp

    val iconBitmaps =
        emotionByScore.mapValues { (_, emotion) ->
            loadImageBitmap(emotion.iconRes)
        }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val width = size.width
        val height = size.height

        val usableWidth = width - startPadding.toPx() - endPadding.toPx()
        val usableHeight = height - topPadding.toPx() - bottomPadding.toPx()

        val dayCount = month.toLocalDate().lengthOfMonth()
        val xStep = if (dayCount > 1) usableWidth / (dayCount - 1) else 0f
        val yStep = usableHeight / (maxScore - minScore)

//        Mesh
        val axisPaint = Paint().apply {
            color = Color(0xFFEAE0F1)
            strokeWidth = 1.dp.toPx()
        }

//        Trục dọc
        for (score in minScore.toInt()..maxScore.toInt()) {
            val y = topPadding.toPx() + (maxScore - score) * yStep

            // Vẽ đường trục
            drawLine(
                color = axisPaint.color,
                start = Offset(startPadding.toPx(), y),
                end = Offset(width - endPadding.toPx(), y),
                strokeWidth = axisPaint.strokeWidth
            )

            // Vẽ icon tương ứng
            emotionByScore[score]?.let { emotion ->
                val icon = iconBitmaps[score] ?: return@let
                val iconSize = 36.dp.toPx()
                val iconIntSize = iconSize.toInt()
                drawImage(
                    image = icon,
                    srcSize = IntSize(icon.width, icon.height),
                    dstSize = IntSize(iconIntSize, iconIntSize),
                    dstOffset = IntOffset(
                        (startPadding.toPx() - iconSize - 8.dp.toPx()).toInt(),
                        (y - iconSize / 2).toInt()
                    )
                )
            }
        }

//        Ngang (tgian)
        val labelPaint = android.graphics.Paint().apply {
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        val keyDays = listOf(1, 5, 10, 15, 20, 25, 30)
        keyDays.filter { it <= dayCount }.forEach { day ->
            val x = startPadding.toPx() + (day - 1) * xStep
            drawContext.canvas.nativeCanvas.drawText(
                day.toString(),
                x,
                height - 8.dp.toPx(),
                labelPaint
            )
        }

        val path = Path()
        var firstPoint = true
        dailyData.forEachIndexed { index, daily ->
            daily ?: return@forEachIndexed      // ngày không có diary
            val x = startPadding.toPx() + index * xStep
            val y = topPadding.toPx() + (maxScore - daily.avgScore) * yStep
            if (firstPoint) {
                path.moveTo(x, y)
                firstPoint = false
            } else {
                path.lineTo(x, y)
            }
        }

        // Cắt Path theo animationProgress
        val measure = PathMeasure()
        measure.setPath(path, false)
        val animatedPath = Path()
        measure.getSegment(
            0f,
            measure.length * animationProgress,
            animatedPath,
            true
        )

        drawPath(
            path = animatedPath,
            color = Color(0xFF230B45),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        if (showPoints) {
            dailyData.forEachIndexed { index, daily ->
                daily ?: return@forEachIndexed
                val x = startPadding.toPx() + index * xStep
                val y = topPadding.toPx() + (maxScore - daily.avgScore) * yStep
                if (animationProgress >= index.toFloat() / (dayCount - 1)) {
                    drawCircle(
                        color = Color(0xFF230B45),
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}


@Composable
fun loadImageBitmap(@DrawableRes resId: Int): ImageBitmap {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, resId)!!
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

@Composable
fun MoodCountChart(
    moodDistribution: Map<Emotions, Int>,
    modifier: Modifier = Modifier
) {
    val total = moodDistribution.values.sum()
    val proportions = remember(moodDistribution) {
        moodDistribution.entries.map { entry ->
            val percentage = if (total > 0) entry.value.toFloat() / total else 0f
            Triple(entry.key, percentage, entry.value)
        }
    }

    Box(modifier = modifier.height(180.dp), contentAlignment = Alignment.BottomCenter) {
        Canvas(
            Modifier
                .fillMaxSize()
        ) {
            val stroke = size.height * 0.4f
            val arcRadius = size.height / 2.0f
            val centerX = size.width / 2f
            val arcRect = Rect(
                left = centerX - arcRadius,
                top = size.height - arcRadius,
                right = centerX + arcRadius,
                bottom = size.height + arcRadius
            )
            var startAngle = 180f
            proportions.forEach { (emotion, percentage, _) ->
                val sweep = percentage * 180f
                drawArc(
                    color = emotion.textColor,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                    size = arcRect.size,
                    topLeft = arcRect.topLeft
                )
                startAngle += sweep
            }
        }

        Text(
            text = total.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MoodIconRow(
    moodDistribution: Map<Emotions, Int>,
    modifier: Modifier = Modifier,
    iconSize: Dp = 52.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        moodDistribution.entries.sortedByDescending { it.key.score }.forEach { (emotion, count) ->
            Box(contentAlignment = Alignment.TopEnd) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = emotion.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .clip(RoundedCornerShape(18.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = emotion.descriptionRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Badge
                Box(
                    modifier = Modifier
                        .offset(x = 8.dp, y = (-6).dp)
                        .size(20.dp)
                        .background(emotion.textColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (count > 0) count.toString() else "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}


fun convertToColumnChartData(
    diaries: List<Diary>
): List<Bars> {
    // Nhóm theo ngày trong tháng
    val groupedByDay = diaries.groupBy { it.createdAt.dayOfMonth }

    return (1..31).mapNotNull { day ->
        val entries = groupedByDay[day]?.filter { it.negativityScore != null }

        if (!entries.isNullOrEmpty()) {
            val averageNegScore = entries.map { it.negativityScore!! }.average().toFloat()

            // Chuyển đổi từ [0,1] → [-1,1]
            val score = 1 - 2 * averageNegScore

            Bars(
                label = day.toString(),
                values = listOf(
                    Bars.Data(value = score.toDouble(), color = SolidColor(getColorForScore(score)))
                )
            )
        } else {
            Bars(
                label = day.toString(),
                values = listOf(
                    Bars.Data(value = 0.0, color = SolidColor(Color.Gray))
                )
            )
        }
    }
}

fun getColorForScore(score: Float): Color {
    return when {
        score > 0.5f -> Color(0xFF81C784) // xanh tích cực
        score > -0.5f -> Color(0xFFFFF176) // vàng trung lập
        else -> Color(0xFFE57373) // đỏ tiêu cực
    }
}
