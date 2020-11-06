package edu.zjff.shzj.trinity.listener

import edu.zjff.shzj.trinity.entity.Effect

interface OnEffectTouchListener {

  fun onEffectTouchEvent(event: Int, effect: Effect)
}