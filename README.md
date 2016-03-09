# ExViewPager
ViewPager with looping, auto scroll, handle touch and scroll orientation.

In brief, this is a convenient viewpager with additional features.

# Usage
Include this library, use

``` xml
<com.kay.exviewpager.ExViewPager
	android:id="@+id/view_pager"
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```
replace
``` xml
<android.support.v4.view.ViewPager
	android:id="@+id/view_pager"
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```

* `setCycle(true)` to infinite loop
* `startAutoScroll()` start auto scroll, `stopAutoScroll()` stop auto scroll.
* `setInterval()` to determine the delay time between auto scroll
* `setAutoScrollFactor()` and `setSwipeScrollFactor()` to specify the speed of auto scroll and swipe
* `setHandleTouch(false)` to stop handling touch event
* `setOrientation(ExViewPager.VERTICAL)` to vertical scroll

For looping feature, if you use `PagerAdapter` only to create `Views`, no more code changes are needed.

If you want to use it with `FragmentPagerAdapter` or `FragmentStatePagerAdapter`, please refer to [LoopingViewPager][1], which the looping feature based on, for detailed instructions.

## Thanks to
* [LoopingViewPager][1] for looping feature
* [Auto Scroll ViewPager][2] for auto scroll feature

## License

    Copyright 2016 Kay

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]:https://github.com/imbryk/LoopingViewPager
[2]:https://github.com/Trinea/android-auto-scroll-view-pager
