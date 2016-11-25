package io.henry.devlogger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mac on 2016/11/15.
 */
public class LoggerResult {

    private ErrorCode errorCode = ErrorCode.SUCCESS;

    private Session session;

    private Throwable throwable;

    private List<Object> list;

    public boolean isSuccess() {
        return errorCode == ErrorCode.SUCCESS;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void addObject(Object object) {
        if (object == null) {
            return;
        }
        if (this.list == null) {
            this.list = new LinkedList<Object>();
        }

        this.list.add(object);
    }

    public List<Object> getObjects() {
        return this.list;
    }
}
