package com.unchil.un7datagrid

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.then
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider


/**
 * 툴팁이 포함된 IconButton 헬퍼 컴포저블
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipIconButton(
    tooltipText: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,       // 앵커(아이콘 버튼)의 위치와 크기
                windowSize: IntSize,         // 전체 창 크기
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize    // 툴팁 콘텐츠의 크기
            ): IntOffset {
                // 예: 앵커의 상단 중앙에 배치하는 로직
                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                val y = anchorBounds.top - popupContentSize.height
                return IntOffset(x, y)
            }
        }
        ,
        tooltip = {
            PlainTooltip { Text(tooltipText)}
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick, enabled = enabled, content = content)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipText(
    tooltipText: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    text: String,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    TooltipBox(
        positionProvider = object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                val y = anchorBounds.top - popupContentSize.height - 24
                return IntOffset(x, y)
            }
        },
        tooltip = {
            PlainTooltip( maxWidth = TooltipDefaults.richTooltipMaxWidth) {
                Text(tooltipText)
            }
        },
        state = rememberTooltipState()
    ) {
        Text(
            color = color,
            text = text,
            modifier = then(modifier),
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            textAlign = textAlign,
            textDecoration = textDecoration,
            maxLines = maxLines
        )
    }
}