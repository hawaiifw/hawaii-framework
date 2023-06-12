/*
 * Copyright 2015-2020 the original author or authors.
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

package org.hawaiiframework.logging;

import org.apache.commons.lang3.StringUtils;
import org.hawaiiframework.logging.model.KibanaLogFieldNames;
import org.hawaiiframework.logging.model.KibanaLogFields;

import java.util.UUID;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TX_ID;

/**
 * Auto closeable utility to start a new transaction.
 */
public class KibanaLogTransaction implements AutoCloseable {

    /**
     * Flag to indicate that there is already a transaction going on.
     */
    private final boolean hasTx;

    /**
     * Start a new log transaction.
     *
     * @param transactionType The transaction type.
     */
    public KibanaLogTransaction(final String transactionType) {
        final String txId = KibanaLogFields.get(TX_ID);
        hasTx = StringUtils.isNotBlank(txId);
        if (!hasTx) {
            KibanaLogFields.tag(TX_ID, createTxId());
        }
        KibanaLogFields.tag(KibanaLogFieldNames.TX_TYPE, transactionType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (!hasTx) {
            KibanaLogFields.clear();
        }
    }

    private String createTxId() {
        return createTxId(UUID.randomUUID());
    }

    private String createTxId(final UUID uuid) {
        return uuid.toString();
    }
}
