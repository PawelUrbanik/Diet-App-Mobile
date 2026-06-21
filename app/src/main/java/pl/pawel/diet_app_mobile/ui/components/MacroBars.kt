package pl.pawel.diet_app_mobile.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.pawel.diet_app_mobile.domain.model.NutritionSummary

@Composable
fun NutritionMacroBars(
    nutrition: NutritionSummary,
    modifier: Modifier = Modifier,
) {
    val maxMacro = maxOf(nutrition.protein, nutrition.fat, nutrition.carbs, 0.1)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "${nutrition.calories.formatMacro()} kcal",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        MacroBar(
            label = "Białko",
            value = nutrition.protein,
            maxValue = maxMacro,
            color = ProteinColor,
        )
        MacroBar(
            label = "Tłuszcz",
            value = nutrition.fat,
            maxValue = maxMacro,
            color = FatColor,
        )
        MacroBar(
            label = "Węgle",
            value = nutrition.carbs,
            maxValue = maxMacro,
            color = CarbsColor,
        )
    }
}

@Composable
private fun MacroBar(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(44.dp),
        )
        val targetFraction = (value / maxValue).toFloat().coerceIn(0f, 1f)
        val animatedFraction by animateFloatAsState(
            targetValue = targetFraction,
            label = "macro_fill",
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedFraction)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
        Text(
            text = "${value.formatMacro()} g",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(38.dp),
            textAlign = TextAlign.End,
        )
    }
}

val ProteinColor = Color(0xFF1E88E5)
val FatColor = Color(0xFFFFB300)
val CarbsColor = Color(0xFFEF6C00)

private fun Double.formatMacro(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)
