package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface

@Composable
fun CustomBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    val barColor = if (isDarkTheme) DarkSurface else Color.White
    val dividerColor = if (isDarkTheme) DarkCardBorder else Color(0xFFE5E7EB)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(barColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(
                color = dividerColor,
                thickness = 0.8.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 1. Rightmost (RTL): "مشتری"
                BottomNavItem(
                    label = "مشتری",
                    selectedIcon = Icons.Filled.PersonAdd,
                    unselectedIcon = Icons.Outlined.PersonAdd,
                    isSelected = selectedTab == "new_customer",
                    onClick = { onTabSelected("new_customer") },
                    testTag = "tab_new_customer",
                    modifier = Modifier.weight(1f)
                )

                // 2. "لیست"
                BottomNavItem(
                    label = "لیست",
                    selectedIcon = Icons.Filled.ListAlt,
                    unselectedIcon = Icons.Outlined.ListAlt,
                    isSelected = selectedTab == "customer_list",
                    onClick = { onTabSelected("customer_list") },
                    testTag = "tab_customer_list",
                    modifier = Modifier.weight(1f)
                )

                // 3. "تسویه"
                BottomNavItem(
                    label = "تسویه",
                    selectedIcon = Icons.Filled.Payments,
                    unselectedIcon = Icons.Outlined.Payments,
                    isSelected = selectedTab == "settlement",
                    onClick = { onTabSelected("settlement") },
                    testTag = "tab_settlement",
                    modifier = Modifier.weight(1f)
                )

                // 4. Center: Bank Mellat Logo (pure transparent background, zero black/white halo)
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .offset(y = (-10).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, radius = 28.dp, color = Color(0xFFC61927).copy(alpha = 0.2f)),
                                onClick = { onTabSelected("home") }
                            )
                            .testTag("tab_home_mellat_center"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mellat_logo),
                            contentDescription = "پیشخوان ملت",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                // 5. "فروشگاه‌ها"
                BottomNavItem(
                    label = "فروشگاه‌ها",
                    selectedIcon = Icons.Filled.Storefront,
                    unselectedIcon = Icons.Outlined.Storefront,
                    isSelected = selectedTab == "stores",
                    onClick = { onTabSelected("stores") },
                    testTag = "tab_stores",
                    modifier = Modifier.weight(1f)
                )

                // 6. "گزارش‌ها"
                BottomNavItem(
                    label = "گزارش‌ها",
                    selectedIcon = Icons.Filled.Assessment,
                    unselectedIcon = Icons.Outlined.Assessment,
                    isSelected = selectedTab == "reports",
                    onClick = { onTabSelected("reports") },
                    testTag = "tab_reports",
                    modifier = Modifier.weight(1f)
                )

                // 7. Leftmost: "تنظیمات"
                BottomNavItem(
                    label = "تنظیمات",
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings,
                    isSelected = selectedTab == "settings",
                    onClick = { onTabSelected("settings") },
                    testTag = "tab_settings",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFFC61927) // Mellat Red
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)

    Column(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp),
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor,
            maxLines = 1
        )
    }
}
