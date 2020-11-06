package edu.zjff.shzj.trinity.editor

/**
 * Created  on 2019-07-27
 */
interface PlayerListener {
  fun getCurrentDuration(): Long

  fun getDuration(): Long

  fun updateDuration(duration: Long)
}