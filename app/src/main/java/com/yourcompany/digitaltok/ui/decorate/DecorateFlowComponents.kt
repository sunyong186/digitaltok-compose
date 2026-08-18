package com.yourcompany.digitaltok.ui.decorate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.components.BackArrowIcon

object DecorateColors {
    val TextBlack = Color(0xFF121212)
    val TextGray1 = Color(0xFFA0A0A0)
    val TextGray2 = Color(0xFF6B6B6B)
    val PointBlue = Color(0xFF3AADFF)
    val LightGrayBg = Color(0xFFF4F4F4)
    val BorderGray = Color(0xFFE0E0E0)
    val FavoriteRed = Color(0xFFFF5252)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecorateTopAppBar(
    title: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DecorateColors.TextBlack
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = BackArrowIcon,
                        contentDescription = "뒤로가기",
                        tint = DecorateColors.TextBlack
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun DecorateTabs(
    selectedTab: DecorateTab,
    onTabSelected: (DecorateTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(44.dp)
            .background(DecorateColors.LightGrayBg, RoundedCornerShape(12.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (selectedTab == DecorateTab.RECENT) Color.White else Color.Transparent)
                .clickable { onTabSelected(DecorateTab.RECENT) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "최근 사용 항목",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == DecorateTab.RECENT) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == DecorateTab.RECENT) DecorateColors.TextBlack else DecorateColors.TextGray1
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (selectedTab == DecorateTab.TEMPLATE) Color.White else Color.Transparent)
                .clickable { onTabSelected(DecorateTab.TEMPLATE) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "템플릿 목록",
                fontSize = 14.sp,
                fontWeight = if (selectedTab == DecorateTab.TEMPLATE) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == DecorateTab.TEMPLATE) DecorateColors.TextBlack else DecorateColors.TextGray1
            )
        }
    }
}

@Composable
fun RecentDecorateContent(
    items: List<DecorateItem>,
    maxSlots: Int = 15,
    onItemClick: (DecorateItem) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    onAddImageClick: () -> Unit,
    onSendClick: () -> Unit
) {
    val selectedItem = items.find { it.isSelected && !it.isSlot && !it.isEmptySlot }
    val filledCount = items.count { !it.isSlot && !it.isEmptySlot }

    // 15개 슬롯 구성 (+ 추가 슬롯 1개 + 등록된 이미지 + 나머지 빈 슬롯)
    val displayItems = remember(items, maxSlots) {
        val nonSlotItems = items.filter { !it.isSlot && !it.isEmptySlot }
        val addSlot = items.find { it.isSlot } ?: DecorateItem(id = "slot_add", title = "추가", isSlot = true)
        val combined = listOf(addSlot) + nonSlotItems
        if (combined.size < maxSlots) {
            combined + List(maxSlots - combined.size) { index ->
                DecorateItem(
                    id = "empty_slot_$index",
                    title = "",
                    isEmptySlot = true
                )
            }
        } else {
            combined.take(maxSlots)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "최근 사용한 사진 ($filledCount/$maxSlots)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DecorateColors.TextGray2
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 15개의 네모는 독립적으로 스크롤 (LazyVerticalGrid weight(1f))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(displayItems) { item ->
                DecorateGridItemView(
                    item = item,
                    onClick = {
                        if (item.isSlot || item.isEmptySlot) {
                            onAddImageClick()
                        } else {
                            onItemClick(item)
                        }
                    },
                    onFavoriteClick = { isFav ->
                        onFavoriteClick(item.id, isFav)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 고정 하단 버튼 (하단 네비게이션 바와 가깝게 여백 최소화)
        Button(
            onClick = {
                if (selectedItem != null) {
                    onSendClick()
                } else {
                    onAddImageClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DecorateColors.PointBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (selectedItem != null) "이미지 전송하기" else "+ 내 이미지 추가",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DecorateGridItemView(
    item: DecorateItem,
    onClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(DecorateColors.LightGrayBg)
            .border(
                width = if (item.isSelected) 2.dp else 1.dp,
                color = if (item.isSelected) DecorateColors.PointBlue else DecorateColors.BorderGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (item.isSlot) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "이미지 추가",
                tint = DecorateColors.TextGray1,
                modifier = Modifier.size(24.dp)
            )
        } else if (item.isEmptySlot) {
            // 빈 슬롯: 은은한 하트 테두리 아이콘
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        } else {
            val model = item.imageUri ?: item.previewUrl
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.blank_img),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 즐겨찾기 하트 아이콘 (우상단)
            IconButton(
                onClick = { onFavoriteClick(!item.isFavorite) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "즐겨찾기",
                    tint = if (item.isFavorite) DecorateColors.FavoriteRed else Color.White
                )
            }
        }
    }
}

@Composable
fun TemplateMenuContent(
    templateCategories: List<TemplateItem>,
    onCategoryClick: (TemplateItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(templateCategories) { item ->
            TemplateItemRow(item = item, onClick = { onCategoryClick(item) })
        }
    }
}

@Composable
fun SeatListContent(
    seats: List<TemplateItem>,
    onSeatClick: (TemplateItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "교통약자 좌석 템플릿",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DecorateColors.TextGray2
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(seats) { seat ->
                TemplateItemRow(item = seat, onClick = { onSeatClick(seat) })
            }
        }
    }
}

@Composable
fun StationListContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    stations: List<TemplateItem>,
    onStationClick: (TemplateItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("더 많은 역을 검색해 보세요", fontSize = 14.sp, color = DecorateColors.TextGray1) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "검색", tint = DecorateColors.TextGray1)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DecorateColors.PointBlue,
                unfocusedBorderColor = DecorateColors.BorderGray,
                focusedContainerColor = DecorateColors.LightGrayBg,
                unfocusedContainerColor = DecorateColors.LightGrayBg
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "역명과 노선 색상이 포함된 템플릿",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DecorateColors.TextGray2
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(stations) { station ->
                TemplateItemRow(item = station, onClick = { onStationClick(station) })
            }
        }
    }
}

@Composable
fun TemplateItemRow(
    item: TemplateItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DecorateColors.LightGrayBg)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (!item.thumbUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = item.thumbUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (item.thumbRes != 0) {
                Image(
                    painter = painterResource(id = item.thumbRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.blank_img),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DecorateColors.TextBlack
            )
            if (item.desc.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.desc,
                    fontSize = 13.sp,
                    color = DecorateColors.TextGray2
                )
            }
        }
    }
}

// ==================== Android Studio Previews ====================

@Preview(showBackground = true, name = "Decorate Top App Bar")
@Composable
fun DecorateTopAppBarPreview() {
    DecorateTopAppBar(
        title = "꾸미기",
        showBackButton = true,
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Decorate Tabs")
@Composable
fun DecorateTabsPreview() {
    DecorateTabs(
        selectedTab = DecorateTab.RECENT,
        onTabSelected = {}
    )
}

@Preview(showBackground = true, name = "Recent Decorate Content (15 Slots)")
@Composable
fun RecentDecorateContentPreview() {
    val sampleItems = listOf(
        DecorateItem(id = "slot_add", title = "추가", isSlot = true),
        DecorateItem(id = "1", title = "기본 사진 1", isSelected = true),
        DecorateItem(id = "2", title = "기본 사진 2", isFavorite = true)
    )
    RecentDecorateContent(
        items = sampleItems,
        onItemClick = {},
        onFavoriteClick = { _, _ -> },
        onAddImageClick = {},
        onSendClick = {}
    )
}

@Preview(showBackground = true, name = "Template Menu Content")
@Composable
fun TemplateMenuContentPreview() {
    val sampleCategories = listOf(
        TemplateItem(id = "1", title = "교통약자 좌석", desc = "교통약자 좌석 안내 템플릿"),
        TemplateItem(id = "2", title = "지하철역", desc = "지하철 노선별로 정리된 템플릿")
    )
    TemplateMenuContent(
        templateCategories = sampleCategories,
        onCategoryClick = {}
    )
}

@Preview(showBackground = true, name = "Template Item Row")
@Composable
fun TemplateItemRowPreview() {
    TemplateItemRow(
        item = TemplateItem(id = "1", title = "임산부 배려석", desc = "분홍색 임산부 배려석 템플릿"),
        onClick = {}
    )
}
