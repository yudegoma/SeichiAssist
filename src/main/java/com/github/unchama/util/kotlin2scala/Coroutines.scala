package com.github.unchama.util.kotlin2scala

import kotlin.coroutines.{Continuation, CoroutineContext, EmptyCoroutineContext}
import kotlinx.coroutines._

object Coroutines {
  def launchInGlobalScope(context: CoroutineContext = EmptyCoroutineContext.INSTANCE,
                          start: CoroutineStart = CoroutineStart.DEFAULT,
                          block: (CoroutineScope, Continuation[kotlin.Unit]) => Unit): Job = {
    BuildersKt.launch(
      GlobalScope.INSTANCE, context, start,
      new kotlin.jvm.functions.Function2[CoroutineScope, Continuation[kotlin.Unit], Unit] {
        override def invoke(p1: CoroutineScope, p2: Continuation[kotlin.Unit]): Unit = block(p1, p2)
      }.asInstanceOf[kotlin.jvm.functions.Function2[_ >: CoroutineScope, _ >: Continuation[_ >: kotlin.Unit], Unit]]
    )
  }
}
