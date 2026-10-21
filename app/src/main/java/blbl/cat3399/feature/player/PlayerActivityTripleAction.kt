package blbl.cat3399.feature.player

import android.view.View
import androidx.lifecycle.lifecycleScope
import blbl.cat3399.core.net.BiliClient
import blbl.cat3399.core.ui.AppToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val ACTION_BUTTON_TRIPLE_HOLD_DURATION_MS = 1_000L

/**
 * 一级 OSD 按钮栏里的点赞 / 投币 / 收藏共用一套长按手势：
 * - 短按：执行对应单项操作（沿用原有 click 逻辑）
 * - 长按 1 秒（触摸或遥控确认键）：对任意一个按钮都触发一键三连
 */
internal fun PlayerActivity.installActionButtonHoldInteractions() {
    if (actionHoldControllers.isNotEmpty()) return
    actionHoldControllers =
        listOf(binding.btnLike, binding.btnCoin, binding.btnFav).map { button ->
            HoldToTriggerController(
                view = button,
                progressHost = button,
                durationMs = ACTION_BUTTON_TRIPLE_HOLD_DURATION_MS,
                onLongTrigger = {
                    setControlsVisible(true)
                    onTripleActionLongPressed()
                },
            ).also { it.install() }
        }
}

internal fun PlayerActivity.cancelActionButtonHoldGestures(resetTriggered: Boolean = true) {
    actionHoldControllers.forEach { it.cancel(resetTriggered = resetTriggered) }
}

/** 只清理已隐藏按钮上的长按手势，避免按钮藏起来后进度环残留或误触发。 */
internal fun PlayerActivity.cancelHiddenActionButtonHoldGestures() {
    actionHoldControllers.forEach { controller ->
        if (controller.view.visibility != View.VISIBLE) {
            controller.cancel(resetTriggered = true)
        }
    }
}

private fun PlayerActivity.onTripleActionLongPressed() {
    if (tripleActionJob?.isActive == true) return
    if (
        likeActionJob?.isActive == true ||
        coinActionJob?.isActive == true ||
        favDialogJob?.isActive == true ||
        favApplyJob?.isActive == true
    ) {
        AppToast.show(this, "操作进行中，请稍后")
        return
    }
    if (!BiliClient.cookies.hasSessData()) {
        AppToast.show(this, "请先登录后再一键三连")
        return
    }

    val requestBvid = currentBvid.trim().takeIf { it.isNotBlank() } ?: return
    val requestAid = currentAid?.takeIf { it > 0L }
    val selfMid = BiliClient.cookies.getCookieValue("DedeUserID")?.trim()?.toLongOrNull()?.takeIf { it > 0L }
    val initialState =
        ArchiveTripleActionState(
            liked = actionLiked,
            coinCount = actionCoinCount,
            favored = actionFavored,
        )

    if (initialState.isSatisfied) {
        AppToast.show(this, "已完成三连")
        return
    }

    val activity = this
    setControlsVisible(true)
    tripleActionJob =
        lifecycleScope.launch {
            try {
                val result =
                    executeArchiveTripleAction(
                        bvid = requestBvid,
                        aid = requestAid,
                        selfMid = selfMid,
                        initialState = initialState,
                        isStillValid = {
                            currentBvid.trim() == requestBvid &&
                                (requestAid == null || currentAid == requestAid)
                        },
                    )
                actionLiked = result.state.liked
                actionCoinCount = result.state.coinCount
                actionFavored = result.state.favored
                updateActionButtonsUi()
                AppToast.show(activity, result.toastMessage())
            } catch (t: Throwable) {
                if (t is CancellationException) return@launch
                AppToast.show(activity, t.userMessage(defaultMessage = "操作失败"))
            } finally {
                tripleActionJob = null
                updateActionButtonsUi()
            }
        }
}
