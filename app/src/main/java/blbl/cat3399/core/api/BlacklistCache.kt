package blbl.cat3399.core.api

import blbl.cat3399.core.log.AppLog
import blbl.cat3399.core.net.BiliClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object BlacklistCache {
    @Volatile
    private var mids: Set<Long> = emptySet()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var loadJob: Job? = null

    fun isBlocked(mid: Long?): Boolean {
        if (mid == null || mid <= 0) return false
        if (!BiliClient.prefs.globalBlacklistEnabled) return false
        return mid in mids
    }

    fun addBlock(mid: Long) {
        mids = mids + mid
    }

    fun removeBlock(mid: Long) {
        mids = mids - mid
    }

    /** 后台刷新，不阻塞调用者。用于 App 启动。 */
    fun refresh() {
        if (!BiliClient.cookies.hasSessData()) return
        loadJob = scope.launch {
            try {
                load()
            } catch (t: Throwable) {
                AppLog.e("BlacklistCache", "refresh failed", t)
            }
        }
    }

    /** 挂起直到加载完成。用于设置开关等需要立即生效的场景。 */
    suspend fun refreshAndAwait() {
        if (!BiliClient.cookies.hasSessData()) return
        try {
            load()
        } catch (t: Throwable) {
            AppLog.e("BlacklistCache", "refreshAndAwait failed", t)
        }
    }

    /** 等待已在进行中的后台加载完成（如果有的话）。 */
    suspend fun awaitIfLoading() {
        loadJob?.join()
    }

    private suspend fun load() {
        val newMids = HashSet<Long>()
        var pn = 1
        while (true) {
            val page = BiliApi.blacklistPage(pn = pn, ps = 50)
            newMids.addAll(page.mids)
            if (!page.hasMore) break
            pn++
            if (pn > 100) break // safety limit
        }
        mids = newMids
        AppLog.d("BlacklistCache", "loaded ${mids.size} blocked mids")
    }
}
