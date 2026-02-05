package com.unchil.un7datagrid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Un7KCMPDataGrid의 시각적 스타일과 동작을 정의하는 설정 데이터 클래스입니다.
 *
 * @property isUsableHaptic 클릭 및 정렬 시 햅틱 피드백(진동) 사용 여부.
 * @property isUsableTooltips 인터랙티브 요소에 툴팁 표시 여부 (ExperimentalMaterial3Api 필요).
 * @property isVisibilityRowNumber 가장 왼쪽에 행 번호(Index) 컬럼 표시 여부.
 * @property rowNumberColumnName 행 번호 컬럼의 헤더 타이틀 명칭 (기본값: "Num").
 * @property pageSizeList 페이지네이션 메뉴에 표시될 페이지당 행 수 옵션 리스트.
 * @property defaultPageSizeListIndex [pageSizeList]에서 처음에 선택될 기본 인덱스.
 * @property headerRowBackgroundColor 헤더 행의 배경색.
 * @property headerRowContentColor 헤더 행의 텍스트 및 아이콘 색상.
 * @property dataRowBackgroundColor 모든 데이터 행의 기본 배경색.
 * @property dataRowContentColor 데이터 행의 텍스트 색상.
 * @property oddDataRowBackgroundColor 홀수 번째 행의 배경색 (지브라 스트라이핑 효과).
 * @property evenDataRowBackgroundColor 짝수 번째 행의 배경색.
 */
data class Un7KCMPDataGridConfig(
    val isUsableHaptic:Boolean = true,
    val isUsableTooltips:Boolean = true,
    val isVisibilityRowNumber: Boolean = true,
    val rowNumberColumnName: String = "No.",
    val pageSizeList: List<String> = listOf("10", "20", "50", "100", "1000"),
    val defaultPageSizeListIndex: Int = pageSizeList.lastIndex,
    val headerRowBackgroundColor: Color? = null ,
    val headerRowContentColor: Color? = null ,
    val dataRowBackgroundColor: Color? = null ,
    val dataRowContentColor: Color? = null ,
    val oddDataRowBackgroundColor: Color? = null,
    val evenDataRowBackgroundColor: Color? = null,
)


