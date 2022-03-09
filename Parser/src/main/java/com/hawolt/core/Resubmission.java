package com.hawolt.core;

import java.util.Objects;

/**
 * Created: 04/03/2022 16:20
 * Author: Twitter @hawolt
 **/

public class Resubmission {

    private final SubmissionType type;
    private final Object o;

    public Resubmission(SubmissionType type, Object o) {
        this.type = type;
        this.o = o;
    }

    public SubmissionType getType() {
        return type;
    }

    public Object getO() {
        return o;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        Resubmission that = (Resubmission) o1;
        return type == that.type && Objects.equals(o, that.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, o);
    }
}
