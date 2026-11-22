package com.seen.reelblock

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ReelBlockService : AccessibilityService() {

    private val IG = "com.instagram.android"

    // view id ของหน้าดู Reels (ตัวที่ปัดขึ้นลง) — IG เรียก Reels ภายในว่า "clips"
    private val reelViewerIds = listOf(
        "$IG:id/clips_viewer_view_pager",
        "$IG:id/clips_viewer_container",
        "$IG:id/clips_video_container"
    )
    private val reelTabId = "$IG:id/clips_tab"
    private val homeTabId = "$IG:id/feed_tab"
    private val homeDescs = listOf("Home", "หน้าหลัก")

    private var lastKick = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName != IG) return
        val now = SystemClock.uptimeMillis()
        if (now - lastKick < 700) return          // กันกดรัวๆ ซ้อนกัน

        val root = rootInActiveWindow ?: return
        if (isOnReels(root)) {
            lastKick = now
            kickOut(root)
        }
    }

    // เช็คว่าตอนนี้อยู่หน้า Reels ไหม: เจอตัวดู Reels หรือแท็บ Reels ถูกเลือกอยู่
    private fun isOnReels(root: AccessibilityNodeInfo): Boolean {
        for (id in reelViewerIds) {
            if (root.findAccessibilityNodeInfosByViewId(id).any { it.isVisibleToUser }) return true
        }
        return root.findAccessibilityNodeInfosByViewId(reelTabId).any { it.isSelected }
    }

    // พาออก: กดแท็บหน้าหลักก่อน ถ้าหาไม่เจอค่อยกดย้อนกลับ
    private fun kickOut(root: AccessibilityNodeInfo) {
        val home = root.findAccessibilityNodeInfosByViewId(homeTabId).firstOrNull()
            ?: findByDesc(root)
        if (home != null && clickUp(home)) return
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    private fun findByDesc(node: AccessibilityNodeInfo?, depth: Int = 0): AccessibilityNodeInfo? {
        if (node == null || depth > 40) return null
        val d = node.contentDescription?.toString()
        if (d != null && homeDescs.any { d.equals(it, ignoreCase = true) }) return node
        for (i in 0 until node.childCount) findByDesc(node.getChild(i), depth + 1)?.let { return it }
        return null
    }

    // บางที node ที่เจอกดเองไม่ได้ ให้ไล่ขึ้นไปหาตัวแม่ที่กดได้
    private fun clickUp(start: AccessibilityNodeInfo): Boolean {
        var n: AccessibilityNodeInfo? = start
        while (n != null) {
            if (n.isClickable) return n.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            n = n.parent
        }
        return false
    }

    override fun onInterrupt() {}
}
