/*
 * Copyright 2015-2019 the original author or authors.
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

import static java.util.Objects.requireNonNull;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.springframework.lang.NonNull;

/**
 * Delegating Runnable that copies the MDC to the executing thread before running the delegate.
 *
 * @author Rutger Lubbers
 * @author Paul Klos
 * @since 2.0.0
 */
public class AbortableTaskRunnable extends HawaiiAsyncRunnable {

  /** The delegate. */
  private final Runnable delegate;

  /**
   * Construct a new instance.
   *
   * @param delegate the delegate to run.
   * @param sharedTaskContext the abort strategy to set.
   */
  public AbortableTaskRunnable(
      @NonNull Runnable delegate, @NonNull SharedTaskContext sharedTaskContext) {
    super(requireNonNull(sharedTaskContext));
    this.delegate = requireNonNull(delegate);
  }

  @Override
  protected void doRun() {
    delegate.run();
  }
}
