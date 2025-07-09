package com.lollipop.applist

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lollipop.applist.compose.BasicComposeActivity
import com.lollipop.applist.data.AppInfo
import com.lollipop.applist.ui.state.AppLauncher
import com.lollipop.applist.ui.state.HookStateController

class ComposeMainActivity : BasicComposeActivity() {

    private val apkChooserLauncher by lazy {
        AppLauncher.registerApkChooser(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apkChooserLauncher
    }

    @Composable
    override fun Content() {
        val navController = rememberNavController()
        val startDestination = Pages.Launcher
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
            bottomBar = {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    Pages.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(route = destination.route)
                                selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        ) { contentPadding ->
            AppNavHost(
                navController,
                startDestination,
                contentPadding = contentPadding
            )
        }
    }

    @Composable
    private fun AppNavHost(
        navController: NavHostController,
        startDestination: Pages,
        contentPadding: PaddingValues,
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination.route
        ) {
            Pages.entries.forEach { page ->
                composable(page.route) {
                    when (page) {
                        Pages.Launcher -> {
                            LauncherPage(contentPadding)
                        }

                        Pages.PackageList -> {
                            PackageListPage(contentPadding)
                        }

                        Pages.Search -> {
                            SearchPage(contentPadding)
                        }

                        Pages.Hook -> {
                            HookPage(contentPadding)
                        }

                        Pages.Settings -> {
                            SettingsPage(contentPadding)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        HookStateController.onResume(this)
    }

    override fun onStart() {
        super.onStart()
        AppLauncher.onStart()
    }

    override fun onStop() {
        super.onStop()
        AppLauncher.onStop(this)
    }

    @Composable
    private fun ContentBox(
        contentPadding: PaddingValues,
        content: @Composable BoxScope.() -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = contentPadding.calculateBottomPadding()
                ),
            content = content
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun LauncherPage(contentPadding: PaddingValues) {
        val isRefreshing by remember { AppLauncher.isRefreshingState }
        val appList = remember { AppLauncher.appList }
        val quickList = remember { AppLauncher.quickAppList }
        ContentBox(contentPadding) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 84.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                }
                if (quickList.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                text = "收藏的应用"
                            )
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                            ) {
                                items(
                                    items = quickList,
                                    key = { it.key }
                                ) { app ->
                                    Image(
                                        modifier = Modifier
                                            .padding(all = 6.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .combinedClickable(
                                                onClick = {
                                                    AppLauncher.openApp(
                                                        this@ComposeMainActivity,
                                                        app
                                                    )
                                                },
                                                onLongClick = {
                                                    AppLauncher.showOption(
                                                        this@ComposeMainActivity,
                                                        app
                                                    )
                                                }
                                            ),
                                        painter = app.painter,
                                        contentDescription = app.nameString
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                items(
                    items = appList,
                    key = { it.key },
                ) { app ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .combinedClickable(
                                onClick = {
                                    AppLauncher.openApp(this@ComposeMainActivity, app)
                                },
                                onLongClick = {
                                    AppLauncher.showOption(this@ComposeMainActivity, app)
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(0.8F)
                                .aspectRatio(1F)
                                .clip(RoundedCornerShape(12.dp)),
                            painter = app.painter,
                            contentDescription = app.nameString,
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = app.nameString,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
                // 多一格占位，避免刷新按钮遮挡图标
                item {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
            }

            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = { AppLauncher.loadAppInfo() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "刷新"
                    )
                }
            }
        }
    }

    @Composable
    private fun PackageListPage(contentPadding: PaddingValues) {
        val isRefreshing by remember { AppLauncher.isRefreshingState }
        val appList = remember { AppLauncher.appList }
        ContentBox(contentPadding) {
            PackageListColumn(list = appList, topPadding = contentPadding.calculateTopPadding())
            if (isRefreshing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = { AppLauncher.loadAppInfo() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "刷新"
                    )
                }
            }
        }
    }

    @Composable
    private fun SearchPage(contentPadding: PaddingValues) {
        val searchValue by remember { AppLauncher.searchValue }
        val searchResultList = remember { AppLauncher.searchResultList }
        val isSearching by remember { AppLauncher.isSearchingState }
        ContentBox(contentPadding) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = cardColors(),
                    elevation = cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))

                        OutlinedTextField(
                            value = searchValue,
                            onValueChange = {
                                AppLauncher.updateSearchValue(it)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            prefix = {
                                Icon(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(24.dp),
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search"
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            label = {
                                Text(
                                    text = "搜索应用",
                                    fontSize = 14.sp
                                )
                            },
                            suffix = {
                                if (searchValue.isNotEmpty()) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height(24.dp)
                                            .clickable(onClick = {
                                                AppLauncher.updateSearchValue("")
                                            }),
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        )
                    }
                }
                PackageListColumn(
                    list = searchResultList,
                    topPadding = 0.dp
                )
            }
        }
    }

    @Composable
    private fun HookPage(contentPadding: PaddingValues) {
        val isAccessibilityEnable by remember { HookStateController.isAccessibilityEnable }
        val isUsageStateEnable by remember { HookStateController.isUsageStateEnable }
        val isNotificationEnable by remember { HookStateController.isNotificationEnable }
        val isOnlyChangedLog by remember { HookStateController.isOnlyChangedLog }
        val viewFilterInterval by remember { HookStateController.viewFilterInterval }
        ContentBox(contentPadding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                SettingsSwitchItem(
                    title = "无障碍服务权限",
                    description = if (isAccessibilityEnable) {
                        "无障碍服务正常"
                    } else {
                        "当前没有权限，将无法获取屏幕上的内容"
                    },
                    checked = isAccessibilityEnable,
                    onItemClick = {
                        // 打开无障碍设置页面
                        startActivity(
                            Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                )
                SettingsSwitchItem(
                    title = "使用统计权限",
                    description = if (isUsageStateEnable) {
                        "使用统计正常"
                    } else {
                        "当前没有权限，将无法获取当前运行的应用信息"
                    },
                    checked = isUsageStateEnable,
                    onItemClick = {
                        // 打开使用统计设置页面
                        startActivity(
                            Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                )
                SettingsSwitchItem(
                    title = "仅在发生变更时记录",
                    description = if (isOnlyChangedLog) {
                        "仅在发生变更时记录信息，但是可能错过一些信息"
                    } else {
                        "每次刷新都会记录信息，但是可能会重复记录一些信息"
                    },
                    checked = isOnlyChangedLog,
                    onCheckedChange = {
                        HookStateController.setOnlyChangedLog(this@ComposeMainActivity, it)
                    }
                )
                SettingsSwitchItem(
                    title = "通知权限",
                    description = if (isNotificationEnable) {
                        "通知权限正常"
                    } else {
                        "当前没有权限，将无法获取和发送通知信息"
                    },
                    checked = isNotificationEnable,
                    onItemClick = {
                        // 打开通知设置页面
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                                putExtra(
                                    android.provider.Settings.EXTRA_CHANNEL_ID,
                                    applicationInfo.uid
                                )
                            }
                        } else {
                            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                setData(Uri.fromParts("package", packageName, null))
                            }
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                )
                SettingsInput(
                    title = "屏幕视图检查间隔",
                    description = "刷新并检索屏幕上视图的间隔，单位:秒",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    value = viewFilterInterval.toString(),
                    onValueChanged = {
                        HookStateController.updateViewFilterInterval(
                            it.toIntOrNull() ?: 1
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun SettingsPage(contentPadding: PaddingValues) {
        var isFilterSystemApp by remember { mutableStateOf(AppLauncher.isFilterSystemApp()) }
        ContentBox(contentPadding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                SettingsSwitchItem(
                    title = "过滤系统应用",
                    description = if (isFilterSystemApp) {
                        "不显示系统应用"
                    } else {
                        "显示系统应用"
                    },
                    checked = isFilterSystemApp,
                    onCheckedChange = {
                        AppLauncher.filterSystemApp(it)
                        isFilterSystemApp = it
                        AppLauncher.loadAppInfo()
                    }
                )
                SettingsItem(
                    title = "获取安装包列表的命令",
                    description = "adb shell pm list package",
                    onClick = {
                        AppLauncher.copy(this@ComposeMainActivity, "adb shell pm list package")
                    }
                )

                SettingsItem(
                    title = "获取安装包地址的命令",
                    description = "adb shell pm path [package]",
                    onClick = {
                        AppLauncher.copy(this@ComposeMainActivity, "adb shell pm path [package]")
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun PackageListColumn(list: List<AppInfo>, topPadding: Dp = 0.dp) {
        val backgroundColorA = MaterialTheme.colorScheme.surface
        val backgroundColorB = MaterialTheme.colorScheme.primary.copy(alpha = 0.1F)
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(topPadding))
            }
            itemsIndexed(
                items = list,
                key = { i, a -> a.key },
            ) { index, app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (index % 2 == 0) {
                                backgroundColorA
                            } else {
                                backgroundColorB
                            }
                        )
                        .combinedClickable(
                            onClick = {
                                AppLauncher.openApp(this@ComposeMainActivity, app)
                            },
                            onLongClick = {
                                AppLauncher.showOption(this@ComposeMainActivity, app)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                ) {
                    Image(
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        painter = app.painter,
                        contentDescription = app.nameString,
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = app.nameString,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Start,
                            fontSize = 16.sp
                        )
                        Text(
                            text = app.packageName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }

    @Composable
    private fun SettingsSwitchItem(
        title: String,
        description: String,
        checked: Boolean,
        onItemClick: () -> Unit = {},
        onCheckedChange: (Boolean) -> Unit = { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onItemClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1F)) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    private fun SettingsItem(
        title: String,
        description: String,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    @Composable
    private fun SettingsInput(
        title: String,
        description: String,
        value: String,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        onValueChanged: (String) -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChanged,
                label = {
                    Text(title)
                },
                keyboardOptions = keyboardOptions,
                supportingText = {
                    Text(description)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }


    enum class Pages(
        val route: String,
        val label: String,
        val icon: ImageVector,
        val contentDescription: String
    ) {
        Launcher(
            route = "Launcher",
            label = "启动",
            icon = Icons.Filled.Home,
            contentDescription = "Launcher"
        ),
        PackageList(
            route = "PackageList",
            label = "包信息",
            icon = Icons.AutoMirrored.Filled.List,
            contentDescription = "PackageList"
        ),
        Search(
            route = "Search",
            label = "搜索",
            icon = Icons.Filled.Search,
            contentDescription = "Search"
        ),
        Hook(
            route = "Hook",
            label = "Hook",
            icon = Icons.Filled.Place,
            contentDescription = "Hook"
        ),
        Settings(
            route = "Settings",
            label = "设置",
            icon = Icons.Filled.Settings,
            contentDescription = "Settings"
        ),
    }
}