/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawaiiframework.async;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.async.timeout.SharedTaskContextHolder;

/**
 * This class will handle all administrative calls which need to be done for every call.
 *
 * @author Richard Kohlen
 */
public abstract class HawaiiAsyncRunnable implements Runnable {

  /**
   * The abort strategy to set on the executing thread's ThreadLocal {@link
   * SharedTaskContextHolder}.
   */
  protected final SharedTaskContext sharedTaskContext;

  /**
   * Constructor.
   *
   * @param sharedTaskContext the context for the api call thread
   */
  protected HawaiiAsyncRunnable(SharedTaskContext sharedTaskContext) {
    this.sharedTaskContext = sharedTaskContext;
  }

  @Override
  public void run() {
    try {
      SharedTaskContextHolder.register(sharedTaskContext);
      sharedTaskContext.startExecution();
      doRun();
    } finally {
      sharedTaskContext.finish();
    }
  }

  /**
   * This method is executed by {@link Runnable} run. Run executes all administrative calls. Code
   * specified in this method will be executed in between those calls.
   */
  protected abstract void doRun();
}
