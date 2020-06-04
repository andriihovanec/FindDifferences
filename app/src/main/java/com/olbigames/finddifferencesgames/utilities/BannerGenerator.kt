package com.olbigames.finddifferencesgames.utilities

import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.olbigames.finddifferencesgames.R

class BannerGenerator {
    companion object {
        fun getBanner(resources: Resources): Drawable {
            val banners = ArrayList<Int>()
            banners.add(R.drawable.banner_10_en)
            banners.add(R.drawable.banner_11_en)
            banners.add(R.drawable.banner_13_en)
            banners.add(R.drawable.banner_15_en)
            banners.add(R.drawable.banner_16_en)
            banners.add(R.drawable.banner_1_en)
            banners.add(R.drawable.banner_2_en)
            banners.add(R.drawable.banner_3_en)
            banners.add(R.drawable.banner_5_en)
            banners.add(R.drawable.banner_7_en)
            banners.add(R.drawable.banner_9_en)
            val rnds = (0 until banners.size).random()
            return resources.getDrawable(banners[rnds])
        }
    }
}