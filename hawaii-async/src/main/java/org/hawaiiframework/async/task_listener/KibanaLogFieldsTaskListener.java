package org.hawaiiframework.async.task_listener;

import org.hawaiiframework.async.timeout.SharedTaskContext;
import org.hawaiiframework.logging.model.KibanaLogContext;
import org.hawaiiframework.logging.model.KibanaLogFields;

import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_ID;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.CALL_TYPE;
import static org.hawaiiframework.logging.model.KibanaLogFieldNames.TASK_ID;

/**
 * Task Context for Kibana log fields.
 *
 * Ordered at -100.
 */
public class KibanaLogFieldsTaskListener implements TaskListener {

    private final KibanaLogContext kibanaLogContext;
    private SharedTaskContext sharedTaskContext;

    public KibanaLogFieldsTaskListener() {
        kibanaLogContext = KibanaLogFields.getContext();
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public void setSharedTaskContext(final SharedTaskContext sharedTaskContext) {
        this.sharedTaskContext = sharedTaskContext;
    }

    @Override
    public void startExecution() {
        KibanaLogFields.populateFromContext(kibanaLogContext);
        final String taskId = sharedTaskContext.getTaskId();
        KibanaLogFields.set(TASK_ID, taskId);
        KibanaLogFields.set(CALL_ID, taskId);
        KibanaLogFields.set(CALL_TYPE, sharedTaskContext.getTaskName());
    }

    @Override
    public void finish() {
        KibanaLogFields.clear();
    }
}
