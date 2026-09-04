package top.iwesley.lyn.music.automotive

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import top.iwesley.lyn.music.ArtworkDecodeSize
import top.iwesley.lyn.music.LibraryNavigationTarget
import top.iwesley.lyn.music.PlayerArtworkDisplay
import top.iwesley.lyn.music.PlayerLyricsPane
import top.iwesley.lyn.music.core.model.AppDisplayScalePreset
import top.iwesley.lyn.music.core.model.PlaybackSnapshot
import top.iwesley.lyn.music.core.model.PlayerArtworkStyle
import top.iwesley.lyn.music.core.model.Track
import top.iwesley.lyn.music.core.model.trackArtworkCacheKey
import top.iwesley.lyn.music.deriveOnlinePlaybackLibraryNavigationTargets
import top.iwesley.lyn.music.derivePlaybackLibraryNavigationTargets
import top.iwesley.lyn.music.feature.player.PlayerIntent
import top.iwesley.lyn.music.feature.player.PlayerState
import top.iwesley.lyn.music.formatDuration
import top.iwesley.lyn.music.playbackModeIcon

@Composable
internal fun AutomotiveLandscapePlayerOverlayContent(
    state: PlayerState,
    track: Track,
    artworkBitmap: ImageBitmap?,
    appDisplayScalePreset: AppDisplayScalePreset,
    isPureMode: Boolean,
    playerArtworkStyle: PlayerArtworkStyle,
    isFavorite: Boolean,
    canToggleFavorite: Boolean = true,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    onlineNavigationSourceId: String? = null,
    onOpenLibraryNavigationTarget: (LibraryNavigationTarget) -> Unit,
    onPlayerIntent: (PlayerIntent) -> Unit,
    onPureModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentIsPureMode by rememberUpdatedState(isPureMode)
    val currentOnPureModeChanged by rememberUpdatedState(onPureModeChanged)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectAutomotivePureModeToggleTwoFingerTap {
                    currentOnPureModeChanged(
                        resolveAutomotivePureModeAfterTwoFingerTap(currentIsPureMode),
                    )
                }
            },
    ) {
        val frameLayout = resolveAutomotiveLandscapeFrameLayout(maxWidth)
        val referencePaneConstraints = resolveAutomotivePlaybackPaneReferenceConstraints(
            overlayMaxWidth = maxWidth,
            overlayMaxHeight = maxHeight,
            appDisplayScalePreset = appDisplayScalePreset,
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onPureModeChanged(!isPureMode) }
                .padding(
                    horizontal = frameLayout.horizontalPadding,
                    vertical = frameLayout.verticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(frameLayout.paneGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AutomotivePlaybackPane(
                state = state,
                track = track,
                artworkBitmap = artworkBitmap,
                appDisplayScalePreset = appDisplayScalePreset,
                isPureMode = isPureMode,
                playerArtworkStyle = playerArtworkStyle,
                isFavorite = isFavorite,
                canToggleFavorite = canToggleFavorite,
                onToggleFavorite = onToggleFavorite,
                onOpenQueue = onOpenQueue,
                onlineNavigationSourceId = onlineNavigationSourceId,
                onOpenLibraryNavigationTarget = onOpenLibraryNavigationTarget,
                onPlayerIntent = onPlayerIntent,
                controlsReferenceMaxWidth = referencePaneConstraints.maxWidth,
                controlsReferenceMaxHeight = referencePaneConstraints.maxHeight,
                onPureModeChanged = onPureModeChanged,
                modifier = Modifier
                    .weight(AutomotivePlaybackPaneWeight)
                    .fillMaxHeight(),
            )
            AutomotiveLyricsPane(
                state = state,
                track = track,
                onPlayerIntent = onPlayerIntent,
                isPureMode = isPureMode,
                onPureModeChanged = onPureModeChanged,
                onOpenQueue = onOpenQueue,
                modifier = Modifier
                    .weight(AutomotiveLyricsPaneWeight)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun AutomotivePlaybackPane(
    state: PlayerState,
    track: Track,
    artworkBitmap: ImageBitmap?,
    appDisplayScalePreset: AppDisplayScalePreset,
    isPureMode: Boolean,
    playerArtworkStyle: PlayerArtworkStyle,
    isFavorite: Boolean,
    canToggleFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpenQueue: () -> Unit,
    onlineNavigationSourceId: String?,
    onOpenLibraryNavigationTarget: (LibraryNavigationTarget) -> Unit,
    onPlayerIntent: (PlayerIntent) -> Unit,
    controlsReferenceMaxWidth: Dp,
    controlsReferenceMaxHeight: Dp,
    onPureModeChanged: (Boolean) -> Unit,
    carUiScale: Float = 1.5f,
    modifier: Modifier = Modifier,
) {
    val snapshot = state.snapshot
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val controlsLayout = resolveAutomotivePlaybackControlsLayout(
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            referenceMaxWidth = controlsReferenceMaxWidth,
            referenceMaxHeight = controlsReferenceMaxHeight,
            appDisplayScalePreset = appDisplayScalePreset,
        )
        val pureModePresentation = resolveAutomotivePureModePresentation(isPureMode)
        val topControlButtonSize = resolveAutomotiveTopControlButtonSize(maxWidth)
        val topControlIconScale = topControlButtonSize / AutomotiveTopControlsSlotHeight
        // v5：移除播放区整块隐式点击（原"点按进入最大化"与控制按钮冲突易误触）
        // 最大化入口改为：歌词区顶带点按 + 顶栏显式按钮 + 双指点按
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (isPureMode) AutomotivePureModeTopSlotHeight else AutomotiveTopControlsSlotHeight,
                    ),
            ) {
                if (pureModePresentation.showTopControls) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AutomotiveRoundIconButton(
                            icon = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "收起播放页",
                            onClick = { onPlayerIntent(PlayerIntent.ExpandedChanged(false)) },
                            buttonSize = topControlButtonSize,
                            iconSize = 34.dp * topControlIconScale * carUiScale,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // v11：放大搜索歌词按钮触控范围（车机点按更友好）
                            AutomotiveRoundIconButton(
                                icon = Icons.Rounded.Search,
                                contentDescription = "搜索歌词",
                                onClick = { onPlayerIntent(PlayerIntent.OpenManualLyricsSearch) },
                                buttonSize = topControlButtonSize * 1.3f,
                                iconSize = 30.dp * topControlIconScale * carUiScale * 1.3f,
                            )
                            AutomotiveRoundIconButton(
                                icon = Icons.Rounded.Fullscreen,
                                contentDescription = "纯净模式",
                                onClick = { onPureModeChanged(true) },
                                buttonSize = topControlButtonSize,
                                iconSize = 30.dp * topControlIconScale * carUiScale,
                            )
                        }
                    }
                } else {
                    // v8：最大化页顶槽 = 居中时钟 + 右侧大号列表按钮(84dp)
                    // 顶槽空白区(时钟左右、除按钮外)可点 = 退出最大化（与歌词区整块切换互补，双指手势保留）
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onPureModeChanged(!isPureMode) },
                        contentAlignment = Alignment.Center,
                    ) {
                        AutomotiveClock(scale = carUiScale)
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AutomotiveRoundIconButton(
                                icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = if (isFavorite) "取消喜欢" else "喜欢",
                                onClick = onToggleFavorite,
                                buttonSize = AutomotiveMaximizeBackButtonSize,
                                iconSize = AutomotiveMaximizeBackIconSize,
                                tint = if (isFavorite) Color(0xFFE5484D) else Color.White.copy(alpha = 0.9f),
                                enabled = canToggleFavorite,
                            )
                            AutomotiveRoundIconButton(
                                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = "播放队列",
                                onClick = onOpenQueue,
                                buttonSize = AutomotiveMaximizeBackButtonSize,
                                iconSize = AutomotiveMaximizeBackIconSize,
                            )
                        }
                    }
                }
            }
            AutomotiveTrackAndProgress(
                snapshot = snapshot,
                track = track,
                artworkBitmap = artworkBitmap,
                isPureMode = isPureMode,
                playerArtworkStyle = playerArtworkStyle,
                isFavorite = isFavorite,
                canToggleFavorite = canToggleFavorite,
                onToggleFavorite = onToggleFavorite,
                onlineNavigationSourceId = onlineNavigationSourceId,
                onOpenLibraryNavigationTarget = onOpenLibraryNavigationTarget,
                onPlayerIntent = onPlayerIntent,
                carUiScale = carUiScale,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            // 最大化模式：底部控制条不占位（高度清零），把空间让给封面和歌名栏下移
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (pureModePresentation.showPlaybackControls) controlsLayout.playButtonSize else 0.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (pureModePresentation.showPlaybackControls) {
                    AutomotivePlaybackControls(
                        snapshot = snapshot,
                        layout = controlsLayout,
                        onOpenQueue = onOpenQueue,
                        onPlayerIntent = onPlayerIntent,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AutomotiveTrackAndProgress(
    snapshot: PlaybackSnapshot,
    track: Track,
    artworkBitmap: ImageBitmap?,
    isPureMode: Boolean,
    playerArtworkStyle: PlayerArtworkStyle,
    isFavorite: Boolean,
    canToggleFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onlineNavigationSourceId: String?,
    onOpenLibraryNavigationTarget: (LibraryNavigationTarget) -> Unit,
    onPlayerIntent: (PlayerIntent) -> Unit,
    carUiScale: Float = 1.5f,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val baseLayout = resolveAutomotiveTrackAndProgressLayout(
            maxWidth = maxWidth,
            maxHeight = maxHeight,
        )
        val layout = if (isPureMode) {
            // v6：最大化页面封面再放大一档（高度占比 0.72→0.76），底部边距归零，
            // 把封面左右两侧与顶部的余量吃满（1280×720 下 394dp → 约 416dp）；
            // 歌名仍允许折两行，不会因封面变大而溢出
            baseLayout.copy(
                artworkSize = minOf(maxWidth * 0.92f, maxHeight * 0.76f).coerceAtMost(560.dp),
                bottomPadding = 0.dp,
            )
        } else {
            baseLayout
        }
        val compactVertical = layout.compactVertical
        // 播放页字号基准必须用未缩放的 Material3 默认值（AutomotiveTypography）：
        // 全局 Typography 已被 LynMusicTheme 放大 1.5×，若再 scaleFont(carUiScale)
        // 会双重放大（1.5×1.5），导致播放页文字超大、歌名截断（v3 的 bug，v4 修复）
        val titleStyle =
            (if (compactVertical) AutomotiveTypography.titleLarge else AutomotiveTypography.headlineMedium)
                .scaleFont(carUiScale)
        val inlineActionButtonSize = if (compactVertical) 44.dp else 52.dp
        val inlineActionIconSize = if (compactVertical) 24.dp else 28.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = layout.bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            AutomotiveSwipeableArtwork(
                snapshot = snapshot,
                artworkBitmap = artworkBitmap,
                artworkSize = layout.artworkSize,
                isPureMode = isPureMode,
                playerArtworkStyle = playerArtworkStyle,
                onPlayerIntent = onPlayerIntent,
            )
            Spacer(Modifier.height(layout.artworkTitleGap))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(layout.metadataGap),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = snapshot.currentDisplayTitle,
                        style = titleStyle,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.96f),
                        maxLines = if (compactVertical) 1 else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (!isPureMode) {
                        Spacer(Modifier.width(if (compactVertical) 2.dp else 4.dp))
                        AutomotiveRoundIconButton(
                            icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = if (isFavorite) "取消喜欢" else "喜欢",
                            onClick = onToggleFavorite,
                            buttonSize = inlineActionButtonSize,
                            iconSize = inlineActionIconSize,
                            tint = if (isFavorite) Color(0xFFE5484D) else Color.White.copy(alpha = 0.9f),
                            enabled = canToggleFavorite,
                        )
                    }
                }
                AutomotiveMetadataNavigationRow(
                    snapshot = snapshot,
                    track = track,
                    style = (if (compactVertical) AutomotiveTypography.bodyLarge else AutomotiveTypography.titleMedium)
                    .scaleFont(carUiScale),
                    color = Color.White.copy(alpha = 0.72f),
                    onlineNavigationSourceId = onlineNavigationSourceId,
                    onOpenLibraryNavigationTarget = onOpenLibraryNavigationTarget,
                    showAlbum = !isPureMode,
                )
            }
            if (!isPureMode) {
                Spacer(Modifier.height(layout.progressTopGap))
                AutomotivePlaybackProgress(
                    snapshot = snapshot,
                    onPlayerIntent = onPlayerIntent,
                    carUiScale = carUiScale,
                    modifier = Modifier.fillMaxWidth(layout.progressWidthFraction),
                )
            }
        }
    }
}

@Composable
private fun AutomotiveSwipeableArtwork(
    snapshot: PlaybackSnapshot,
    artworkBitmap: ImageBitmap?,
    artworkSize: Dp,
    isPureMode: Boolean,
    playerArtworkStyle: PlayerArtworkStyle,
    onPlayerIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val maxVisualOffset = resolveAutomotiveArtworkMaxVisualOffset(artworkSize)
    val swipeThreshold = resolveAutomotiveArtworkSwipeThreshold(maxVisualOffset)
    val maxVisualOffsetPx = with(density) { maxVisualOffset.toPx() }
    val swipeThresholdPx = with(density) { swipeThreshold.toPx() }
    var dragOffsetPx by remember(snapshot.currentTrack?.id) { mutableStateOf(0f) }
    val animatedDragOffsetPx by animateFloatAsState(
        targetValue = dragOffsetPx,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "automotive-artwork-swipe-offset",
    )
    // v5：封面点按 = 播放/暂停（两种模式统一），不再依赖 isPureMode
    val artworkTapModifier = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) {
        onPlayerIntent(resolveAutomotiveArtworkTapIntent())
    }
    Box(
        modifier = modifier
            .size(artworkSize)
            .offset { IntOffset(animatedDragOffsetPx.roundToInt(), 0) }
            .pointerInput(snapshot.currentTrack?.id, swipeThresholdPx, maxVisualOffsetPx) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffsetPx = resolveAutomotiveArtworkDragOffsetPx(
                            currentOffsetPx = dragOffsetPx,
                            dragAmountPx = dragAmount,
                            maxVisualOffsetPx = maxVisualOffsetPx,
                        )
                    },
                    onDragEnd = {
                        val swipeIntent = resolveAutomotiveArtworkSwipeIntent(
                            finalOffsetPx = dragOffsetPx,
                            swipeThresholdPx = swipeThresholdPx,
                        )
                        dragOffsetPx = 0f
                        if (swipeIntent != null) {
                            onPlayerIntent(swipeIntent)
                        }
                    },
                    onDragCancel = { dragOffsetPx = 0f },
                )
            }
            .then(artworkTapModifier),
        contentAlignment = Alignment.Center,
    ) {
        val artworkDisplaySpec = resolveAutomotiveArtworkDisplaySpec(
            artworkSize = artworkSize,
            playerArtworkStyle = playerArtworkStyle,
        )
        PlayerArtworkDisplay(
            style = artworkDisplaySpec.style,
            artworkSize = artworkDisplaySpec.artworkSize,
            artworkBitmap = artworkBitmap,
            artworkLocator = snapshot.currentDisplayArtworkLocator,
            artworkCacheKey = snapshot.currentTrack?.let(::trackArtworkCacheKey),
            spinning = snapshot.isPlaying,
            enableArtworkTint = false,
            vinylArtworkDiameterFraction = artworkDisplaySpec.vinylArtworkDiameterFraction,
            vinylInnerGlowDiameterFraction = artworkDisplaySpec.vinylInnerGlowDiameterFraction,
            maxArtworkDecodeSizePx = ArtworkDecodeSize.Player,
            retainPreviousArtworkWhileLoading = true,
        )
    }
}

internal data class AutomotiveArtworkDisplaySpec(
    val style: PlayerArtworkStyle,
    val artworkSize: Dp,
    val vinylArtworkDiameterFraction: Float,
    val vinylInnerGlowDiameterFraction: Float,
)

internal fun resolveAutomotiveArtworkDisplaySpec(
    artworkSize: Dp,
    playerArtworkStyle: PlayerArtworkStyle = PlayerArtworkStyle.VINYL,
): AutomotiveArtworkDisplaySpec {
    return AutomotiveArtworkDisplaySpec(
        style = playerArtworkStyle,
        artworkSize = artworkSize,
        vinylArtworkDiameterFraction = 0.76f,
        vinylInnerGlowDiameterFraction = 0.72f,
    )
}

@Composable
private fun AutomotiveMetadataNavigationRow(
    snapshot: PlaybackSnapshot,
    track: Track,
    style: TextStyle,
    color: Color,
    onlineNavigationSourceId: String?,
    onOpenLibraryNavigationTarget: (LibraryNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
    showAlbum: Boolean = true,
) {
    val navigationTargets = remember(
        snapshot.currentDisplayAlbumTitle,
        snapshot.currentDisplayArtistName,
        track.albumTitle,
        track.artistName,
        track.albumId,
        track.artistId,
        track.artworkLocator,
        onlineNavigationSourceId,
    ) {
        if (onlineNavigationSourceId != null) {
            deriveOnlinePlaybackLibraryNavigationTargets(
                snapshot = snapshot,
                track = track,
                sourceId = onlineNavigationSourceId,
            )
        } else {
            derivePlaybackLibraryNavigationTargets(snapshot, track)
        }
    }
    val artistLabel = automotiveMetadataValue(
        primary = snapshot.currentDisplayArtistName,
        fallback = track.artistName,
    ) ?: "未知艺人"
    val albumLabel = automotiveMetadataValue(
        primary = snapshot.currentDisplayAlbumTitle,
        fallback = track.albumTitle,
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AutomotiveMetadataText(
            text = artistLabel,
            target = navigationTargets.artistTarget,
            style = style,
            color = color,
            onOpenLibraryNavigationTarget = onOpenLibraryNavigationTarget,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (showAlbum && albumLabel != null) {
            Text(
                text = " · ",
                style = style,
                fontWeight = FontWeight.Medium,
                color = color,
                maxLines = 1,
            )
            AutomotiveMetadataText(
                text = albumLabel,
                target = navigationTargets.albumTarget,
                style = style,
                color = color,
                onOpenLibraryNavigationTarget = onOpenLibraryNavigationTarget,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

@Composable
private fun AutomotiveMetadataText(
    text: String,
    target: LibraryNavigationTarget?,
    style: TextStyle,
    color: Color,
    onOpenLibraryNavigationTarget: (LibraryNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.then(
            if (target != null) {
                Modifier.clickable { onOpenLibraryNavigationTarget(target) }
            } else {
                Modifier
            },
        ),
        style = style,
        fontWeight = FontWeight.Medium,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun AutomotivePlaybackProgress(
    snapshot: PlaybackSnapshot,
    onPlayerIntent: (PlayerIntent) -> Unit,
    carUiScale: Float = 1.5f,
    modifier: Modifier = Modifier,
) {
    var dragFraction by remember(snapshot.currentTrack?.id, snapshot.durationMs) {
        mutableStateOf<Float?>(null)
    }
    val progressFraction = dragFraction ?: resolveAutomotivePlayerProgressFraction(snapshot)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            AutomotiveRoundedSliderTrack(
                progressFraction = progressFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                trackHeightPx = with(LocalDensity.current) { 6.dp.toPx() },
            )
            Slider(
                value = progressFraction.coerceIn(0f, 1f),
                onValueChange = { dragFraction = it },
                onValueChangeFinished = {
                    val targetPositionMs = resolveAutomotivePlayerSeekPositionMs(dragFraction, snapshot)
                    dragFraction = null
                    if (targetPositionMs != null) {
                        onPlayerIntent(PlayerIntent.SeekTo(targetPositionMs))
                    }
                },
                enabled = snapshot.canSeek && snapshot.durationMs > 0L,
                valueRange = 0f..1f,
                colors = automotiveTransparentSliderColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .graphicsLayer(scaleY = 0.58f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatDuration(snapshot.positionMs),
                style = AutomotiveTypography.labelLarge.scaleFont(carUiScale),
                color = Color.White.copy(alpha = 0.74f),
            )
            Text(
                text = formatDuration(snapshot.durationMs),
                style = AutomotiveTypography.labelLarge.scaleFont(carUiScale),
                color = Color.White.copy(alpha = 0.74f),
            )
        }
    }
}

// v7：最大化页歌词区底部进度条——拉满歌词区宽度，且可拖动跳转（"拉歌"）
// 复用播放页的透明 Slider 方案；拖动条是独立触控目标，不会触发整屏"点击切换"
@Composable
private fun AutomotivePureModeProgressRow(
    snapshot: PlaybackSnapshot,
    onPlayerIntent: (PlayerIntent) -> Unit,
    carUiScale: Float = 1.5f,
    modifier: Modifier = Modifier,
) {
    var dragFraction by remember(snapshot.currentTrack?.id, snapshot.durationMs) {
        mutableStateOf<Float?>(null)
    }
    val progressFraction = dragFraction ?: resolveAutomotivePlayerProgressFraction(snapshot)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // v11：进度条左侧两个控制钮——a.播放模式(顺序/随机/单曲) b.搜索歌词
        // 最大化模式底部控制条被收起，故把这两个常用钮挪到进度条左侧
        AutomotiveRoundIconButton(
            icon = playbackModeIcon(snapshot.mode),
            contentDescription = "切换播放模式",
            onClick = { onPlayerIntent(PlayerIntent.CycleMode) },
            buttonSize = 48.dp,
            iconSize = 26.dp,
        )
        AutomotiveRoundIconButton(
            icon = Icons.Rounded.Search,
            contentDescription = "搜索歌词",
            onClick = { onPlayerIntent(PlayerIntent.OpenManualLyricsSearch) },
            buttonSize = 48.dp,
            iconSize = 26.dp,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                AutomotiveRoundedSliderTrack(
                    progressFraction = progressFraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    trackHeightPx = with(LocalDensity.current) { 6.dp.toPx() },
                )
                Slider(
                    value = progressFraction.coerceIn(0f, 1f),
                    onValueChange = { dragFraction = it },
                    onValueChangeFinished = {
                        val targetPositionMs = resolveAutomotivePlayerSeekPositionMs(dragFraction, snapshot)
                        dragFraction = null
                        if (targetPositionMs != null) {
                            onPlayerIntent(PlayerIntent.SeekTo(targetPositionMs))
                        }
                    },
                    enabled = snapshot.canSeek && snapshot.durationMs > 0L,
                    valueRange = 0f..1f,
                    colors = automotiveTransparentSliderColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .graphicsLayer(scaleY = 0.58f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = formatDuration(snapshot.positionMs),
                    style = AutomotiveTypography.labelLarge.scaleFont(carUiScale),
                    color = Color.White.copy(alpha = 0.74f),
                )
                Text(
                    text = formatDuration(snapshot.durationMs),
                    style = AutomotiveTypography.labelLarge.scaleFont(carUiScale),
                    color = Color.White.copy(alpha = 0.74f),
                )
            }
        }
    }
}

@Composable
private fun AutomotiveRoundedSliderTrack(
    progressFraction: Float,
    modifier: Modifier = Modifier,
    trackHeightPx: Float,
) {
    Canvas(modifier = modifier) {
        val trackWidth = size.width.coerceAtLeast(0f)
        if (trackWidth <= 0f || trackHeightPx <= 0f) return@Canvas
        val top = (size.height - trackHeightPx) / 2f
        val radius = CornerRadius(trackHeightPx / 2f, trackHeightPx / 2f)
        drawRoundRect(
            color = Color.White.copy(alpha = 0.24f),
            topLeft = Offset(0f, top),
            size = Size(trackWidth, trackHeightPx),
            cornerRadius = radius,
        )
        val activeWidth = (trackWidth * progressFraction.coerceIn(0f, 1f)).coerceIn(0f, trackWidth)
        if (activeWidth > 0f) {
            drawRoundRect(
                color = Color.White.copy(alpha = 0.96f),
                topLeft = Offset(0f, top),
                size = Size(activeWidth, trackHeightPx),
                cornerRadius = radius,
            )
        }
    }
}

@Composable
private fun automotiveTransparentSliderColors() = SliderDefaults.colors(
    thumbColor = Color.White.copy(alpha = 0.98f),
    activeTrackColor = Color.Transparent,
    inactiveTrackColor = Color.Transparent,
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
    disabledThumbColor = Color.White.copy(alpha = 0.52f),
    disabledActiveTrackColor = Color.Transparent,
    disabledInactiveTrackColor = Color.Transparent,
    disabledActiveTickColor = Color.Transparent,
    disabledInactiveTickColor = Color.Transparent,
)

@Composable
private fun AutomotivePlaybackControls(
    snapshot: PlaybackSnapshot,
    layout: AutomotivePlaybackControlsLayout,
    onOpenQueue: () -> Unit,
    onPlayerIntent: (PlayerIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (layout.showSecondaryControls) {
            AutomotiveRoundIconButton(
                icon = playbackModeIcon(snapshot.mode),
                contentDescription = "切换播放模式",
                onClick = { onPlayerIntent(PlayerIntent.CycleMode) },
                buttonSize = layout.actionButtonSize,
                iconSize = layout.actionIconSize,
            )
            Spacer(Modifier.width(layout.controlGap))
        }
        AutomotiveRoundIconButton(
            icon = Icons.Rounded.SkipPrevious,
            contentDescription = "上一首",
            onClick = { onPlayerIntent(PlayerIntent.SkipPrevious) },
            buttonSize = layout.skipButtonSize,
            iconSize = layout.skipIconSize,
        )
        Spacer(Modifier.width(layout.controlGap))
        AutomotiveRoundIconButton(
            icon = if (snapshot.isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle,
            contentDescription = if (snapshot.isPlaying) "暂停" else "播放",
            onClick = { onPlayerIntent(PlayerIntent.TogglePlayPause) },
            buttonSize = layout.playButtonSize,
            iconSize = layout.playIconSize,
        )
        Spacer(Modifier.width(layout.controlGap))
        AutomotiveRoundIconButton(
            icon = Icons.Rounded.SkipNext,
            contentDescription = "下一首",
            onClick = { onPlayerIntent(PlayerIntent.SkipNext) },
            buttonSize = layout.skipButtonSize,
            iconSize = layout.skipIconSize,
        )
        if (layout.showSecondaryControls) {
            Spacer(Modifier.width(layout.controlGap))
            AutomotiveRoundIconButton(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                contentDescription = "播放队列",
                onClick = onOpenQueue,
                buttonSize = layout.actionButtonSize,
                iconSize = layout.actionIconSize,
            )
        }
    }
}

@Composable
private fun AutomotiveLyricsPane(
    state: PlayerState,
    track: Track,
    onPlayerIntent: (PlayerIntent) -> Unit,
    isPureMode: Boolean,
    onPureModeChanged: (Boolean) -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // v7：移除上/下两条 64dp 触发带及其提示图标（歌词区 +128dp ≈ +25%）
    // 整块歌词区可点 = 播放↔最大化切换（普通页进 / 最大化页退），双指手势仍保留
    // 歌单按钮取消：播放模式左下控制条的 QueueMusic 已可查看
    Box(
        modifier = modifier
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onPureModeChanged(!isPureMode) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 中段：歌词本体（滚动浏览/自动居中行为完全不变；歌词行本身无点击跳转，整块可点安全）
            PlayerLyricsPane(
                state = state,
                track = track,
                onPlayerIntent = onPlayerIntent,
                pure = true,
                lyricsTextScale = 2f,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            // v7：最大化页专属——歌词区底部、拉满宽度的可拖动进度条 + 已播/总时长
            // 仅最大化页显示（普通页进度条仍在左侧播放区）；拖动条为独立触控目标，不触发整屏切换
            if (isPureMode) {
                Spacer(Modifier.height(AutomotiveLyricsProgressTopGap))
                AutomotivePureModeProgressRow(
                    snapshot = state.snapshot,
                    onPlayerIntent = onPlayerIntent,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun AutomotiveRoundIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 64.dp,
    iconSize: Dp = 30.dp,
    tint: Color = Color.White.copy(alpha = 0.92f),
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(buttonSize),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = 0.46f),
            modifier = Modifier.size(iconSize),
        )
    }
}

internal data class AutomotivePureModePresentation(
    val showTopControls: Boolean,
    val showPlaybackControls: Boolean,
)

internal fun resolveAutomotivePureModePresentation(
    isPureMode: Boolean,
): AutomotivePureModePresentation = AutomotivePureModePresentation(
    showTopControls = !isPureMode,
    showPlaybackControls = !isPureMode,
)

internal fun resolveAutomotiveTopControlButtonSize(maxWidth: Dp): Dp =
    minOf(AutomotiveTopControlsSlotHeight, maxWidth / 3f)
        .coerceAtLeast(AutomotiveControlMinimumTouchSize)

// v5：封面点按统一为播放/暂停（普通页+最大化页行为一致；横滑切歌手势不受影响）
internal fun resolveAutomotiveArtworkTapIntent(): PlayerIntent = PlayerIntent.TogglePlayPause

internal fun resolveAutomotivePureModeAfterTwoFingerTap(
    isPureMode: Boolean,
): Boolean = !isPureMode

internal fun isAutomotivePureModeToggleTwoFingerTap(
    firstDownTimeMillis: Long,
    secondDownTimeMillis: Long?,
    lastUpTimeMillis: Long,
    distinctPointerCount: Int,
    hadTwoPointersPressed: Boolean,
    maximumMovementPx: Float,
    touchSlopPx: Float,
): Boolean {
    val secondDown = secondDownTimeMillis ?: return false
    return distinctPointerCount == 2 &&
        hadTwoPointersPressed &&
        secondDown >= firstDownTimeMillis &&
        secondDown - firstDownTimeMillis <= AutomotivePureModeSecondPointerWindowMillis &&
        lastUpTimeMillis >= firstDownTimeMillis &&
        lastUpTimeMillis - firstDownTimeMillis <= AutomotivePureModeTapTimeoutMillis &&
        maximumMovementPx >= 0f &&
        maximumMovementPx <= touchSlopPx.coerceAtLeast(0f)
}

private suspend fun PointerInputScope.detectAutomotivePureModeToggleTwoFingerTap(
    onTogglePureMode: () -> Unit,
) {
    awaitPointerEventScope {
        var tracker: AutomotiveTwoFingerTapTracker? = null
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val eventTimeMillis = event.changes.maxOfOrNull { it.uptimeMillis } ?: continue
            val newDowns = event.changes
                .filter { it.pressed && !it.previousPressed }
                .sortedBy { it.uptimeMillis }
            if (tracker == null) {
                val firstDown = newDowns.firstOrNull() ?: continue
                tracker = AutomotiveTwoFingerTapTracker(
                    firstDownTimeMillis = firstDown.uptimeMillis,
                    pointerStartPositions = mutableMapOf(firstDown.id to firstDown.position),
                )
            }
            val activeTracker = tracker
            newDowns.forEach { change ->
                if (change.id !in activeTracker.pointerStartPositions) {
                    if (activeTracker.pointerStartPositions.size == 1) {
                        activeTracker.secondDownTimeMillis = change.uptimeMillis
                        if (
                            change.uptimeMillis - activeTracker.firstDownTimeMillis >
                            AutomotivePureModeSecondPointerWindowMillis
                        ) {
                            activeTracker.isValid = false
                        }
                    } else {
                        activeTracker.isValid = false
                    }
                    activeTracker.pointerStartPositions[change.id] = change.position
                }
            }
            event.changes.forEach { change ->
                val startPosition = activeTracker.pointerStartPositions[change.id] ?: return@forEach
                val movement = (change.position - startPosition).getDistance()
                activeTracker.maximumMovementPx = maxOf(activeTracker.maximumMovementPx, movement)
                if (movement > viewConfiguration.touchSlop) {
                    activeTracker.isValid = false
                }
            }
            val pressedTrackedPointerCount = event.changes.count { change ->
                change.pressed && change.id in activeTracker.pointerStartPositions
            }
            if (pressedTrackedPointerCount >= 2) {
                activeTracker.hadTwoPointersPressed = true
            }
            if (activeTracker.pointerStartPositions.size >= 2) {
                event.changes.forEach { it.consume() }
            }
            if (
                eventTimeMillis - activeTracker.firstDownTimeMillis >
                AutomotivePureModeTapTimeoutMillis
            ) {
                activeTracker.isValid = false
            }
            if (event.changes.none { it.pressed }) {
                if (
                    activeTracker.isValid &&
                    isAutomotivePureModeToggleTwoFingerTap(
                        firstDownTimeMillis = activeTracker.firstDownTimeMillis,
                        secondDownTimeMillis = activeTracker.secondDownTimeMillis,
                        lastUpTimeMillis = eventTimeMillis,
                        distinctPointerCount = activeTracker.pointerStartPositions.size,
                        hadTwoPointersPressed = activeTracker.hadTwoPointersPressed,
                        maximumMovementPx = activeTracker.maximumMovementPx,
                        touchSlopPx = viewConfiguration.touchSlop,
                    )
                ) {
                    onTogglePureMode()
                }
                tracker = null
            }
        }
    }
}

private data class AutomotiveTwoFingerTapTracker(
    val firstDownTimeMillis: Long,
    val pointerStartPositions: MutableMap<PointerId, Offset>,
    var secondDownTimeMillis: Long? = null,
    var hadTwoPointersPressed: Boolean = false,
    var maximumMovementPx: Float = 0f,
    var isValid: Boolean = true,
)

internal data class AutomotivePlaybackControlsLayout(
    val showSecondaryControls: Boolean,
    val actionButtonSize: Dp,
    val skipButtonSize: Dp,
    val playButtonSize: Dp,
    val actionIconSize: Dp,
    val skipIconSize: Dp,
    val playIconSize: Dp,
    val controlGap: Dp,
)

internal val AutomotivePlaybackControlsLayout.totalWidth: Dp
    get() {
        val primaryControlsWidth =
            skipButtonSize + skipButtonSize + playButtonSize + controlGap + controlGap
        return if (showSecondaryControls) {
            primaryControlsWidth +
                actionButtonSize + actionButtonSize +
                controlGap + controlGap
        } else {
            primaryControlsWidth
        }
    }

internal fun resolveAutomotivePlaybackControlsLayout(
    maxWidth: Dp,
    maxHeight: Dp,
    referenceMaxWidth: Dp = maxWidth,
    referenceMaxHeight: Dp = maxHeight,
    appDisplayScalePreset: AppDisplayScalePreset = AppDisplayScalePreset.Default,
): AutomotivePlaybackControlsLayout {
    val preferredLayout = when {
        referenceMaxWidth < 252.dp -> automotivePrimaryCompactControlsLayout()

        referenceMaxWidth < 288.dp || referenceMaxHeight < 480.dp -> AutomotivePlaybackControlsLayout(
            showSecondaryControls = true,
            actionButtonSize = 48.dp,
            skipButtonSize = 48.dp,
            playButtonSize = 60.dp,
            actionIconSize = 22.dp,
            skipIconSize = 26.dp,
            playIconSize = 46.dp,
            controlGap = 0.dp,
        )

        referenceMaxWidth < 400.dp -> AutomotivePlaybackControlsLayout(
            showSecondaryControls = true,
            actionButtonSize = 48.dp,
            skipButtonSize = 56.dp,
            playButtonSize = 72.dp,
            actionIconSize = 26.dp,
            skipIconSize = 32.dp,
            playIconSize = 56.dp,
            controlGap = 2.dp,
        )

        referenceMaxWidth < 520.dp || referenceMaxHeight < 520.dp -> AutomotivePlaybackControlsLayout(
            showSecondaryControls = true,
            actionButtonSize = 56.dp,
            skipButtonSize = 64.dp,
            playButtonSize = 84.dp,
            actionIconSize = 30.dp,
            skipIconSize = 36.dp,
            playIconSize = 64.dp,
            controlGap = 6.dp,
        )

        else -> AutomotivePlaybackControlsLayout(
            showSecondaryControls = true,
            actionButtonSize = 80.dp,
            skipButtonSize = 88.dp,
            playButtonSize = 108.dp,
            actionIconSize = 36.dp,
            skipIconSize = 44.dp,
            playIconSize = 76.dp,
            controlGap = 10.dp,
        )
    }
    val widthSafeLayout = if (
        preferredLayout.showSecondaryControls &&
        maxWidth < preferredLayout.minimumAutomotiveControlsWidth(appDisplayScalePreset)
    ) {
        preferredLayout.asPrimaryAutomotiveControlsLayout()
    } else {
        preferredLayout
    }
    return widthSafeLayout.fitAutomotiveControlsToWidth(
        maxWidth = maxWidth,
        appDisplayScalePreset = appDisplayScalePreset,
    )
}

private fun automotivePrimaryCompactControlsLayout(): AutomotivePlaybackControlsLayout =
    AutomotivePlaybackControlsLayout(
        showSecondaryControls = false,
        actionButtonSize = 48.dp,
        skipButtonSize = 48.dp,
        playButtonSize = 60.dp,
        actionIconSize = 22.dp,
        skipIconSize = 26.dp,
        playIconSize = 46.dp,
        controlGap = 4.dp,
    )

private fun AutomotivePlaybackControlsLayout.asPrimaryAutomotiveControlsLayout() = copy(
    showSecondaryControls = false,
    controlGap = maxOf(controlGap, 4.dp),
)

private fun AutomotivePlaybackControlsLayout.minimumAutomotiveControlsWidth(
    appDisplayScalePreset: AppDisplayScalePreset,
): Dp {
    val minimumActionButtonSize = minimumAutomotiveButtonSize(
        preferredSize = actionButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    val minimumSkipButtonSize = minimumAutomotiveButtonSize(
        preferredSize = skipButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    val minimumPlayButtonSize = minimumAutomotiveButtonSize(
        preferredSize = playButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    return minimumSkipButtonSize + minimumSkipButtonSize + minimumPlayButtonSize +
        if (showSecondaryControls) {
            minimumActionButtonSize + minimumActionButtonSize
        } else {
            0.dp
        }
}

private fun minimumAutomotiveButtonSize(
    preferredSize: Dp,
    appDisplayScalePreset: AppDisplayScalePreset,
): Dp {
    val displayScale = appDisplayScalePreset.validAutomotiveDisplayScale()
    val physicalSizeFloor =
        if (displayScale >= AppDisplayScalePreset.Default.scale) preferredSize / displayScale else 0.dp
    return maxOf(AutomotiveControlMinimumTouchSize, physicalSizeFloor)
}

private fun AutomotivePlaybackControlsLayout.fitAutomotiveControlsToWidth(
    maxWidth: Dp,
    appDisplayScalePreset: AppDisplayScalePreset,
): AutomotivePlaybackControlsLayout {
    if (totalWidth <= maxWidth) return this
    val physicalFloorActionButtonSize = minimumAutomotiveButtonSize(
        preferredSize = actionButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    val physicalFloorSkipButtonSize = minimumAutomotiveButtonSize(
        preferredSize = skipButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    val physicalFloorPlayButtonSize = minimumAutomotiveButtonSize(
        preferredSize = playButtonSize,
        appDisplayScalePreset = appDisplayScalePreset,
    )
    val physicalFloorTotalWidth = minimumAutomotiveControlsWidth(appDisplayScalePreset)
    val absoluteFloorTotalWidth =
        if (showSecondaryControls) AutomotiveFiveControlsAbsoluteMinimumWidth
        else AutomotivePrimaryControlsAbsoluteMinimumWidth
    val (lowerActionButtonSize, lowerSkipButtonSize, lowerPlayButtonSize, upperFraction) =
        if (maxWidth >= physicalFloorTotalWidth) {
            val availableSurplus = maxWidth - physicalFloorTotalWidth
            val preferredSurplus = totalWidth - physicalFloorTotalWidth
            val fraction = if (preferredSurplus > 0.dp) {
                (availableSurplus.value / preferredSurplus.value).coerceIn(0f, 1f)
            } else {
                1f
            }
            AutomotiveControlsFitRange(
                actionButtonSize = physicalFloorActionButtonSize,
                skipButtonSize = physicalFloorSkipButtonSize,
                playButtonSize = physicalFloorPlayButtonSize,
                fraction = fraction,
            )
        } else {
            val availableSurplus = (maxWidth - absoluteFloorTotalWidth).coerceAtLeast(0.dp)
            val physicalFloorSurplus = physicalFloorTotalWidth - absoluteFloorTotalWidth
            val fraction = if (physicalFloorSurplus > 0.dp) {
                (availableSurplus.value / physicalFloorSurplus.value).coerceIn(0f, 1f)
            } else {
                1f
            }
            AutomotiveControlsFitRange(
                actionButtonSize = AutomotiveControlMinimumTouchSize,
                skipButtonSize = AutomotiveControlMinimumTouchSize,
                playButtonSize = AutomotiveControlMinimumTouchSize,
                fraction = fraction,
            )
        }
    val upperActionButtonSize =
        if (maxWidth >= physicalFloorTotalWidth) actionButtonSize else physicalFloorActionButtonSize
    val upperSkipButtonSize =
        if (maxWidth >= physicalFloorTotalWidth) skipButtonSize else physicalFloorSkipButtonSize
    val upperPlayButtonSize =
        if (maxWidth >= physicalFloorTotalWidth) playButtonSize else physicalFloorPlayButtonSize
    val fittedActionButtonSize = lowerActionButtonSize +
        (upperActionButtonSize - lowerActionButtonSize) * upperFraction
    val fittedSkipButtonSize = lowerSkipButtonSize +
        (upperSkipButtonSize - lowerSkipButtonSize) * upperFraction
    val fittedPlayButtonSize = lowerPlayButtonSize +
        (upperPlayButtonSize - lowerPlayButtonSize) * upperFraction
    val fittedGap =
        if (maxWidth >= physicalFloorTotalWidth) controlGap * upperFraction else 0.dp
    return copy(
        actionButtonSize = fittedActionButtonSize,
        skipButtonSize = fittedSkipButtonSize,
        playButtonSize = fittedPlayButtonSize,
        actionIconSize = actionIconSize * (fittedActionButtonSize / actionButtonSize),
        skipIconSize = skipIconSize * (fittedSkipButtonSize / skipButtonSize),
        playIconSize = playIconSize * (fittedPlayButtonSize / playButtonSize),
        controlGap = fittedGap,
    )
}

private data class AutomotiveControlsFitRange(
    val actionButtonSize: Dp,
    val skipButtonSize: Dp,
    val playButtonSize: Dp,
    val fraction: Float,
)

internal data class AutomotiveLandscapeFrameLayout(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val paneGap: Dp,
)

internal fun resolveAutomotiveLandscapeFrameLayout(maxWidth: Dp): AutomotiveLandscapeFrameLayout =
    if (maxWidth < 900.dp) {
        AutomotiveLandscapeFrameLayout(
            horizontalPadding = 28.dp,
            verticalPadding = 28.dp,
            paneGap = 24.dp,
        )
    } else {
        AutomotiveLandscapeFrameLayout(
            horizontalPadding = 44.dp,
            verticalPadding = 28.dp,
            paneGap = 36.dp,
        )
    }

internal data class AutomotivePlaybackPaneReferenceConstraints(
    val maxWidth: Dp,
    val maxHeight: Dp,
)

internal fun resolveAutomotivePlaybackPaneReferenceConstraints(
    overlayMaxWidth: Dp,
    overlayMaxHeight: Dp,
    appDisplayScalePreset: AppDisplayScalePreset,
): AutomotivePlaybackPaneReferenceConstraints {
    val displayScale = appDisplayScalePreset.validAutomotiveDisplayScale()
    val referenceOverlayWidth = overlayMaxWidth * displayScale
    val referenceOverlayHeight = overlayMaxHeight * displayScale
    val referenceFrameLayout = resolveAutomotiveLandscapeFrameLayout(referenceOverlayWidth)
    val referenceContentWidth = (
        referenceOverlayWidth -
            referenceFrameLayout.horizontalPadding * 2 -
            referenceFrameLayout.paneGap
        ).coerceAtLeast(0.dp)
    val referenceContentHeight = (
        referenceOverlayHeight -
            referenceFrameLayout.verticalPadding * 2
        ).coerceAtLeast(0.dp)
    return AutomotivePlaybackPaneReferenceConstraints(
        maxWidth = referenceContentWidth * AutomotivePlaybackPaneWeight,
        maxHeight = referenceContentHeight,
    )
}

private fun AppDisplayScalePreset.validAutomotiveDisplayScale(): Float =
    scale.takeIf { it.isFinite() && it > 0f } ?: AppDisplayScalePreset.Default.scale

private val AutomotiveControlMinimumTouchSize = 48.dp
private val AutomotiveFiveControlsAbsoluteMinimumWidth = AutomotiveControlMinimumTouchSize * 5
private val AutomotivePrimaryControlsAbsoluteMinimumWidth = AutomotiveControlMinimumTouchSize * 3
private val AutomotiveTopControlsSlotHeight = 64.dp
private val AutomotivePureModeTopSlotHeight = 116.dp
private val AutomotiveMaximizeBackButtonSize = 84.dp
private val AutomotiveMaximizeBackIconSize = 48.dp
private const val AutomotivePureModeArtworkScale = 1.25f
private const val AutomotivePureModeSecondPointerWindowMillis = 150L
private const val AutomotivePureModeTapTimeoutMillis = 300L
private const val AutomotivePlaybackPaneWeight = 0.46f
private const val AutomotiveLyricsPaneWeight = 0.54f

// v7：最大化页歌词区底部进度条与歌词区顶边的间距
private val AutomotiveLyricsProgressTopGap = 10.dp

// 播放页专用未缩放字号基准：Material3 出厂默认值。
// 全局 Typography 已被 LynMusicTheme scaledTypography(1.5×) 放大（作用于设置/曲库等页面），
// 播放页文字在此基础上再 scaleFont(carUiScale) 会双重放大，故必须引用此基准（v4 修复）
private val AutomotiveTypography = Typography()

internal data class AutomotiveTrackAndProgressLayout(
    val compactVertical: Boolean,
    val artworkSize: Dp,
    val artworkMaximumSize: Dp,
    val artworkTitleGap: Dp,
    val metadataGap: Dp,
    val progressTopGap: Dp,
    val bottomPadding: Dp,
    val progressWidthFraction: Float,
)

internal fun resolveAutomotiveTrackAndProgressLayout(
    maxWidth: Dp,
    maxHeight: Dp,
): AutomotiveTrackAndProgressLayout {
    val ultraCompactVertical = maxHeight < 320.dp
    val compactVertical = maxHeight < 420.dp
    val artworkMaximumSize = when {
        ultraCompactVertical -> 220.dp
        compactVertical -> 250.dp
        else -> 360.dp
    }
    val artworkWidthFraction = when {
        ultraCompactVertical -> 0.52f
        compactVertical -> 0.56f
        else -> 0.66f
    }
    val artworkHeightFraction = when {
        ultraCompactVertical -> 0.44f
        compactVertical -> 0.48f
        else -> 0.56f
    }
    val artworkMinimumSize = when {
        ultraCompactVertical -> 140.dp
        compactVertical -> 150.dp
        else -> 190.dp
    }
    val artworkSize = minOf(
        maxWidth * artworkWidthFraction,
        maxHeight * artworkHeightFraction,
    ).coerceIn(
        minimumValue = artworkMinimumSize,
        maximumValue = artworkMaximumSize,
    )
    return AutomotiveTrackAndProgressLayout(
        compactVertical = compactVertical,
        artworkSize = artworkSize,
        artworkMaximumSize = artworkMaximumSize,
        artworkTitleGap = when {
            ultraCompactVertical -> 8.dp
            compactVertical -> 12.dp
            else -> 20.dp
        },
        metadataGap = when {
            ultraCompactVertical -> 2.dp
            compactVertical -> 4.dp
            else -> 8.dp
        },
        progressTopGap = when {
            ultraCompactVertical -> 16.dp
            compactVertical -> 26.dp
            else -> 44.dp
        },
        bottomPadding = when {
            ultraCompactVertical -> 4.dp
            compactVertical -> 8.dp
            else -> 6.dp
        },
        progressWidthFraction = when {
            ultraCompactVertical -> 0.92f
            compactVertical -> 0.9f
            else -> 0.86f
        },
    )
}

internal fun resolveAutomotivePlayerProgressFraction(snapshot: PlaybackSnapshot): Float {
    if (snapshot.durationMs <= 0L) return 0f
    return (snapshot.positionMs.toFloat() / snapshot.durationMs.toFloat()).coerceIn(0f, 1f)
}

internal fun resolveAutomotivePlayerSeekPositionMs(
    fraction: Float?,
    snapshot: PlaybackSnapshot,
): Long? {
    if (fraction == null || !snapshot.canSeek || snapshot.durationMs <= 0L) {
        return null
    }
    return (snapshot.durationMs * fraction.coerceIn(0f, 1f)).roundToLong()
}

internal fun resolveAutomotiveArtworkMaxVisualOffset(artworkSize: Dp): Dp =
    minOf(artworkSize.coerceAtLeast(0.dp) * 0.32f, 132.dp)

internal fun resolveAutomotiveArtworkSwipeThreshold(maxVisualOffset: Dp): Dp {
    val normalizedMaxVisualOffset = maxVisualOffset.coerceAtLeast(0.dp)
    return (normalizedMaxVisualOffset * 0.9f)
        .coerceIn(40.dp, 72.dp)
        .coerceAtMost(normalizedMaxVisualOffset)
}

internal fun resolveAutomotiveArtworkDragOffsetPx(
    currentOffsetPx: Float,
    dragAmountPx: Float,
    maxVisualOffsetPx: Float,
): Float {
    if (maxVisualOffsetPx <= 0f) return 0f
    return (currentOffsetPx + dragAmountPx).coerceIn(-maxVisualOffsetPx, maxVisualOffsetPx)
}

internal fun resolveAutomotiveArtworkSwipeIntent(
    finalOffsetPx: Float,
    swipeThresholdPx: Float,
): PlayerIntent? {
    if (swipeThresholdPx <= 0f) return null
    return when {
        finalOffsetPx <= -swipeThresholdPx -> PlayerIntent.SkipNext
        finalOffsetPx >= swipeThresholdPx -> PlayerIntent.SkipPrevious
        else -> null
    }
}

private fun automotiveMetadataValue(
    primary: String?,
    fallback: String?,
): String? {
    return primary?.trim()?.takeIf { it.isNotBlank() }
        ?: fallback?.trim()?.takeIf { it.isNotBlank() }
}

private fun TextStyle.scaleFont(scale: Float): TextStyle =
    this.copy(fontSize = this.fontSize * scale, lineHeight = this.lineHeight * scale)

@Composable
private fun AutomotiveClock(
    scale: Float = 1.5f,
    modifier: Modifier = Modifier,
) {
    val timeStr = remember { mutableStateOf("--:--") }
    LaunchedEffect(Unit) {
        while (true) {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            timeStr.value = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
            kotlinx.coroutines.delay(30_000)
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = timeStr.value,
            // 显式 TextStyle（不依赖 MaterialTheme.typography，避免被全局放大波及）
            // lineHeight 必须随 fontSize 放大，否则字形被垂直裁切（v3 时钟显示不全的原因）
            style = TextStyle(
                fontSize = (60f * scale).sp,
                lineHeight = (72f * scale).sp,
                fontWeight = FontWeight.ExtraBold,
            ),
            color = Color.White.copy(alpha = 0.96f),
        )
    }
}
