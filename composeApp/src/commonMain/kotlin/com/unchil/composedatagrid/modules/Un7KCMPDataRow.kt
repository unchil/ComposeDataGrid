@file:OptIn(InternalComposeApi::class)

package com.unchil.composedatagrid.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
internal fun Un7KCMPDataRow(
    isVisibleRowNum: Boolean,
    maxWidthInDp: Dp,
    widthDividerThickness:Dp,
    widthRowNumColumn: Dp,
    pageIndex:Int,
    pageSize:Int,
    dataIndex:Int,
    pagingData: MutableMap<String, List<Any?>>,
    columnWeights:List<Float>,
){

    val paddingDataRow = remember { PaddingValues(top = 2.dp) }
    val borderStrokeLightGray = remember {BorderStroke(width = 1.dp, color = Color.LightGray)}
    val borderShapeIn = remember{RoundedCornerShape(0.dp)}
    val heightDataRow = remember{ 30.dp }


    Row(
        modifier = Modifier.padding(paddingDataRow),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(isVisibleRowNum){

            Row(
                modifier = Modifier
                    .width(widthRowNumColumn).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = getRowNumber(pageIndex, pageSize, dataIndex).toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
        if(isVisibleRowNum) {
            VerticalDivider(
                thickness = widthDividerThickness,
                color = Color.Transparent
            )
        }


        val dataColumnsWidth = if (isVisibleRowNum) {
            maxWidthInDp - widthRowNumColumn - (widthDividerThickness * (pagingData.keys.size))
        } else {
            maxWidthInDp - (widthDividerThickness * (pagingData.keys.size -1))
        }

        pagingData.keys.forEachIndexed { keyIndex, columnName ->

            Row(
                modifier = Modifier
                    .width(dataColumnsWidth * columnWeights.getOrElse(keyIndex) { 0f }).height(heightDataRow)
                    .border(borderStrokeLightGray, shape = borderShapeIn),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = (pagingData[columnName] as List<*>)[dataIndex].toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            if (keyIndex < pagingData.keys.size - 1) {
                VerticalDivider(
                    thickness = widthDividerThickness,
                    color = Color.Transparent

                )
            }
        }

    }

}
