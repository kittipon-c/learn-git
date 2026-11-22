package com.seen.reelblock

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

// หน้าเดียว: บอกวิธีเปิดใช้ + ปุ่มพาไปหน้าตั้งค่า Accessibility
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }
        layout.addView(TextView(this).apply {
            text = "ReelBlock\n\nกดปุ่มด้านล่าง → เลือก \"ReelBlock\" → เปิดใช้งาน\n\nหลังจากนั้นถ้าเปิดหน้า Reels ใน IG แอปจะพากลับหน้าหลักให้เอง"
            textSize = 18f
            gravity = Gravity.CENTER
        })
        layout.addView(Button(this).apply {
            text = "เปิดหน้าตั้งค่า Accessibility"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        setContentView(layout)
    }
}
